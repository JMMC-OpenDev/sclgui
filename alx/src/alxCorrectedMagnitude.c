/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Function definition for corrected magnitude computation (i.e taking into
 * account interstellar absorption).
 *
 * @sa JMMC-MEM-2600-0008 document.
 */


/* Needed to preclude warnings on snprintf() */
#define  _BSD_SOURCE 1

/* 
 * System Headers
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

/* Needed for FP_NAN support */
#define  __USE_ISOC99 1
#include <math.h>


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "misc.h"


/* 
 * Local Headers
 */
#include "alx.h"
#include "alxPrivate.h"
#include "alxErrors.h"


/*
 * Local Functions declaration
 */
mcsCOMPL_STAT alxString2SpectralType(mcsSTRING32        spType,
                                     alxSPECTRAL_TYPE*  spectralType);

static alxCOLOR_TABLE* alxGetColorTableForStar(alxSPECTRAL_TYPE* spectralType,
                                               mcsLOGICAL        isBright);
    
static mcsLOGICAL alxIsBlankingValue(mcsDOUBLE cellValue);
    
static mcsINT32 alxGetLineForBrightStar(alxCOLOR_TABLE    *colorTable,
                                        alxSPECTRAL_TYPE  *spectralType,
                                        mcsSTRING32        spType);

static mcsINT32 alxGetLineForFaintStar(alxCOLOR_TABLE    *colorTable,
                                       alxSPECTRAL_TYPE  *spectralType,
                                       mcsSTRING32        spType,
                                       mcsDOUBLE          diffMagJK);

static mcsCOMPL_STAT 
alxComputeDiffMagnitudeForBrightStar(mcsSTRING32                 spType,
                                     mcsDOUBLE                   mgB,
                                     mcsDOUBLE                   mgV,
                                     alxDIFFERENTIAL_MAGNITUDES  diffMagnitudes);

static mcsCOMPL_STAT 
alxComputeDiffMagnitudeForFaintStar(mcsSTRING32                 spType,
                                    mcsDOUBLE                   mgJ,
                                    mcsDOUBLE                   mgK,
                                    alxDIFFERENTIAL_MAGNITUDES  diffMagnitudes);

static mcsCOMPL_STAT
alxComputeAllMagnitudesForBrightStar(alxDIFFERENTIAL_MAGNITUDES  diffMagnitudes,
                                     alxMAGNITUDES               magnitudes,
                                     mcsDOUBLE                   mgV);

static mcsCOMPL_STAT
alxComputeAllMagnitudesForFaintStar(alxDIFFERENTIAL_MAGNITUDES  diffMagnitudes,
                                    alxMAGNITUDES               magnitudes,
                                    mcsDOUBLE                   mgJ);

static mcsCOMPL_STAT alxComputeMagnitude(mcsDOUBLE             firstMag,
                                         alxDATA               diffMag,
                                         alxDATA              *magnitude,
                                         alxCONFIDENCE_INDEX   confIndex);

static alxEXTINCTION_RATIO_TABLE *alxGetExtinctionRatioTable(void);

/*
 * Local functions definition
 */
/**
 * Return the extinction ratio for interstellar absorption computation .
 *
 * @return pointer onto structure containing extinction ratio table, or NULL if
 * an error occured.
 *
 * @usedfiles : alxExtinctionRatioTable.cfg : configuration file containing the
 * extinction ratio according to the color (i.e. magnitude band)
 */
static alxEXTINCTION_RATIO_TABLE* alxGetExtinctionRatioTable(void)
{ 
    logTrace("alxGetExtinctionRatioTable()");

    /*
     * Check if the structure extinctionRatioTable, where will be stored
     * extinction ratio to compute interstellar extinction, is loaded into
     * memory. If not load it.
     */
    static alxEXTINCTION_RATIO_TABLE extinctionRatioTable = 
        {mcsFALSE, "alxExtinctionRatioTable.cfg"};
    if (extinctionRatioTable.loaded == mcsTRUE)
    {
        return (&extinctionRatioTable);
    }
    
    /*
     * Reset all extinction ratio
     */
    int i;
    for (i = 0; i < alxNB_BANDS; i++)
    {
        extinctionRatioTable.rc[i] = 0.0;
    }

    /* 
     * Build the dynamic buffer which will contain the file of extinction ratio
     */
    /* Find the location of the file */
    char* fileName;
    fileName = miscLocateFile(extinctionRatioTable.fileName);
    if (fileName == NULL)
    {
        return NULL;
    }

    /* Load file. Comment lines start with '#' */
    miscDYN_BUF dynBuf;
    miscDynBufInit(&dynBuf);
    if (miscDynBufLoadFile(&dynBuf, fileName, "#") == mcsFAILURE)
    {
        miscDynBufDestroy(&dynBuf);
        free(fileName);
        return NULL;
    }

    /* For each line of the loaded file */
    mcsINT32  lineNum = 0;
    const char *pos = NULL;
    mcsSTRING1024 line;
    while ((pos = miscDynBufGetNextLine
            (&dynBuf, pos, line, sizeof(mcsSTRING1024), mcsTRUE)) != NULL)
    {
        logTrace("miscDynBufGetNextLine() = '%s'", line);

        /* Trim line for leading and trailing characters */        
        miscTrimString (line, " ");

        /* If line is not empty */
        if (strlen(line) != 0)
        {
            /* Check if there is to many lines in file */
            if (lineNum >= alxNB_BANDS)
            {
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_TOO_MANY_LINES, fileName);
                free(fileName);
                return NULL;
            }

            /* Get extinction ratio */
            char band;
            float rc;
            if (sscanf(line, "%c %f", &band, &rc) != 2)
            {
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_WRONG_FILE_FORMAT, line, fileName);
                free(fileName);
                return NULL;
            }
            else
            {
                /* fitzpatrick identifier */
                alxBAND fitzId;
                switch (toupper(band))
                {
                    case 'M':
                        fitzId = alxM_BAND;
                        break;

                    case 'L':
                        fitzId = alxL_BAND;
                        break;

                    case 'K':
                        fitzId = alxK_BAND;
                        break;

                    case 'H':
                        fitzId = alxH_BAND;
                        break;

                    case 'J':
                        fitzId = alxJ_BAND;
                        break;

                    case 'I':
                        fitzId = alxI_BAND;
                        break;

                    case 'R':
                        fitzId = alxR_BAND;
                        break;

                    case 'V':
                        fitzId = alxV_BAND;
                        break;

                    case 'B':
                        fitzId = alxB_BAND;
                        break;

                    default:
                        errAdd(alxERR_INVALID_BAND, band, fileName);
                        miscDynBufDestroy(&dynBuf);
                        free(fileName);
                        return NULL;
                }

                /* Store read extinction ratio */
                if (extinctionRatioTable.rc[fitzId] == 0)
                {
                    extinctionRatioTable.rc[fitzId] = rc;
                }
                else
                {
                    errAdd(alxERR_DUPLICATED_LINE, line, fileName);
                    miscDynBufDestroy(&dynBuf);
                    free(fileName);
                    return NULL;
                }
            }

            /* Next line */
            lineNum++;
        }
    }

    /* Destroy the dynamic buffer where is stored the file information */
    miscDynBufDestroy(&dynBuf);

    /* Check if there is missing line */
    if (lineNum != alxNB_BANDS)
    {
        errAdd(alxERR_MISSING_LINE, lineNum, alxNB_BANDS, fileName);
        free(fileName);
        return NULL;
    }

    free(fileName);
    
    extinctionRatioTable.loaded = mcsTRUE;

    return (&extinctionRatioTable);
}

static alxSTAR_TYPE alxGetLuminosityClass(alxSPECTRAL_TYPE *spectralType)
{
    logTrace("alxGetLuminosityClass()");

    /* Default value of DWARF TO BE CONFIRMED! */
    alxSTAR_TYPE starType = alxDWARF; 

    /* If no spectral type given */
    if (spectralType == NULL)
    {
        logTest("Type of star = DWARF (by default as no Spectral Type provided)");
        return starType;
    }

    /* If no luminosity class given */
    if (strlen(spectralType->luminosityClass) == 0)
    {
        logTest("Type of star = DWARF (by default as no Luminosity Class provided)");
        return starType;
    }

    /* Determination of star type according to the spectral type */
    static char* spectralTypes[] = {"VIII", "VII", "VI", "III-IV", "III/IV", "IV-III", "IV/III",
                             "II-III", "II/III", "I-II", "I/II", "III", "IB-II", "IB/II", 
                             "IBV", "II", "IV", "V", "(I)", "IA-O/IA", "IA-O", "IA/AB",
                             "IAB-B", "IAB", "IA", "IB", "I",
                              NULL};
    static alxSTAR_TYPE luminosityClasses[] = {alxDWARF, alxDWARF, alxDWARF, alxGIANT, alxGIANT, 
                                        alxGIANT, alxGIANT, alxGIANT, alxGIANT, alxSUPER_GIANT, 
                                        alxSUPER_GIANT, alxGIANT, alxSUPER_GIANT, alxSUPER_GIANT, 
                                        alxSUPER_GIANT, alxGIANT, alxDWARF, alxDWARF,
                                        alxSUPER_GIANT, alxSUPER_GIANT, alxSUPER_GIANT, 
                                        alxSUPER_GIANT, alxSUPER_GIANT, alxSUPER_GIANT, 
                                        alxSUPER_GIANT, alxSUPER_GIANT, alxSUPER_GIANT};
    
    char* luminosityClass = spectralType->luminosityClass;
    mcsUINT32 index = 0;
    while (spectralTypes[index] != NULL)
    {
        /* If the current spectral type is found */
        if (strstr(luminosityClass, spectralTypes[index]) != NULL)
        {
            /* Get the corresponding luminoisity class */
            starType = luminosityClasses[index];
            break;
        }
        index++;
    }

    /* Print out type of star */
    switch (starType)
    {
        case alxDWARF:
            logTest("Type of star = DWARF"); 
            break;
            
        case alxGIANT:
            logTest("Type of star = GIANT");
            break;
            
        case alxSUPER_GIANT:
            logTest("Type of star = SUPER GIANT");
            break;
    }

    return starType;
}

/**
 * Say if a cell value is defined or not.
 *
 * If the cell value = '99.99'(alxBLANKING_VALUE), return false.
 *
 * @param cellValue the value of the cell
 *
 * @return mcsTRUE if cell value != alxBLANKING_VALUE, otherwise mcsFALSE is 
 * returned.
 */
static mcsLOGICAL alxIsBlankingValue(mcsDOUBLE cellValue)
{
    if (cellValue == (mcsDOUBLE)alxBLANKING_VALUE)
    {
        return mcsFALSE;
    }

    return mcsTRUE;
}

/**
 * Return the color table corresponding to a given spectral type.
 *
 * This method determines the color table associated to the given spectral
 * type of a bright star, and reads (if not yet done) this table from
 * the configuration file.
 *
 * @param starType the star type to consider.
 * @param isBright mcsTRUE for bright stars, mcsFALSE for faint ones.
 *
 * @return pointer to structure containing color table, or NULL if an error
 * occured.
 *
 * @usedfiles Files containing the color indexes, the absolute magnitude in V
 * and the stellar mass according to the temperature class for different star
 * types. These tables are used to compute missing magnitudes.
 *  - alxColorTableForFaintDwarfStar.cfg : faint dwarf star 
 *  - see code for other tables! 
 */
static alxCOLOR_TABLE*
alxGetColorTableForStar(alxSPECTRAL_TYPE* spectralType, mcsLOGICAL isBright)
{
    logTrace("alxGetColorTableForStar()");

    /* Existing ColorTables */
    static alxCOLOR_TABLE colorTablesFaint[alxNB_STAR_TYPES] = {
        {mcsFALSE, "alxColorTableForFaintDwarfStar.cfg"},
        {mcsFALSE, "alxColorTableForFaintGiantStar.cfg"},
        {mcsFALSE, "alxColorTableForFaintSuperGiantStar.cfg"},
    };
    static alxCOLOR_TABLE colorTablesBright[alxNB_STAR_TYPES] = {
        {mcsFALSE, "alxColorTableForBrightDwarfStar.cfg"},
        {mcsFALSE, "alxColorTableForBrightGiantStar.cfg"},
        {mcsFALSE, "alxColorTableForBrightSuperGiantStar.cfg"},
    };

    /* Choose the right Color table for the different type of stars */
    alxCOLOR_TABLE* colorTables = NULL;
    if (isBright)
    {
        colorTables = colorTablesBright;
    }
    else
    {
        colorTables = colorTablesFaint;
    }

    /* Determination of star type according to the given star type */
    alxSTAR_TYPE starType = alxGetLuminosityClass(spectralType);
    alxCOLOR_TABLE* colorTable = &colorTables[starType];

    /*
     * Check if the structure in which polynomial coefficients will be stored is
     * loaded into memory. If not load it.
     */
    if (colorTable->loaded == mcsTRUE)
    {
        return colorTable;
    }

    /* Find the location of the file */
    char* fileName = miscLocateFile(colorTable->fileName);
    if (fileName == NULL)
    {
        return NULL;
    }
    
    /* Load file (skipping comment lines starting with '#') */
    miscDYN_BUF dynBuf;
    miscDynBufInit(&dynBuf);
    
    logDebug("Loading %s ...", fileName);
    
    if (miscDynBufLoadFile(&dynBuf, fileName, "#") == mcsFAILURE)
    {
        miscDynBufDestroy(&dynBuf);
        free(fileName);
        return NULL;
    }

    /* For each line of the loaded file */
    mcsINT32 lineNum = 0;
    const char *pos = NULL;
    mcsSTRING1024 line;
    while ((pos = miscDynBufGetNextLine(&dynBuf, pos, line, sizeof(line), mcsTRUE)) != NULL)
    {
        logTrace("miscDynBufGetNextLine() = '%s'", line);

        /* Trim line for any leading and trailing blank characters */
        miscTrimString (line, " ");

        /* If line is not empty */
        if (strlen(line) != 0)
        {
            /* Check if there are to many lines in file */
            if (lineNum >= alxNB_SPECTRAL_TYPES)
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_TOO_MANY_LINES, fileName);
                free(fileName);
                return NULL;
            }

            /* Try to read each polynomial coefficients */
            mcsDOUBLE values[alxNB_COLOR_INDEXES];
            mcsINT32 nbOfReadTokens = sscanf(line, "%c%lf %lf %lf %lf %lf %lf %lf %lf %lf",   
                       &colorTable->spectralType[lineNum].code,
                       &colorTable->spectralType[lineNum].quantity,
                       &values[0],
                       &values[1],
                       &values[2],
                       &values[3],
                       &values[4],
                       &values[5],
                       &values[6],
                       &values[7]);

            /* If parsing went wrong */
            if (nbOfReadTokens != (alxNB_DIFF_MAG + 1))
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_WRONG_FILE_FORMAT, line, fileName);
                free(fileName);
                return NULL;
            }
            
            /* Set whether each colorTable cell has been written or not */
            int i;
            for (i = 0; i < alxNB_COLOR_INDEXES; i++)
            {
                alxDATA* colorTableCell = &(colorTable->index[lineNum][i]);

                mcsDOUBLE value = values[i];

                colorTableCell->value = value;
                colorTableCell->isSet = alxIsBlankingValue(value);
            }

            /* Next line */
            lineNum++;
        }
    }

    /* Set the total number of lines in the color table */
    colorTable->nbLines = lineNum;

    /* Mark the color table as "loaded" */
    colorTable->loaded = mcsTRUE;

    /* Destroy the temporary dynamic buffer used to parse the color table file */
    miscDynBufDestroy(&dynBuf);
    free(fileName);

    /* Return a pointer on the freshly loaded color table */
    return colorTable;
}

/**
 * Create a spectral type structure from a string.
 *
 * Should get each part of the spectral type XN.NLLL where X is a letter, N.N a
 * number between 0 and 9 and LLL is the light class. Modified as to ingest the 
 * more complicated spectral types found
 *
 * @param spectralType the spectral type string to decode
 * @param decodedSpectralType the spectral type structure to return
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT alxString2SpectralType(mcsSTRING32       spectralType,
                                     alxSPECTRAL_TYPE* decodedSpectralType)
{
    logTrace("alxString2SpectralType()");

    /* Function parameter check */
    if (spectralType == NULL)
    {
        errAdd(alxERR_NULL_PARAMETER, "spectralType");
        return mcsFAILURE;
    }
    if (decodedSpectralType == NULL)
    {
        errAdd(alxERR_NULL_PARAMETER, "decodedSpectralType");
        return mcsFAILURE;
    }
    else /* Initialize Spectral Type structure */
    {
        decodedSpectralType->code              = '\0';
        decodedSpectralType->quantity          = FP_NAN;
        strcpy(decodedSpectralType->luminosityClass, "");
        decodedSpectralType->isDouble          = mcsFALSE;
        decodedSpectralType->isSpectralBinary  = mcsFALSE;
        decodedSpectralType->isVariable        = mcsFALSE;
    }

    char* tempSP = miscDuplicateString(spectralType);
    if (tempSP == NULL)
    {
        errAdd(alxERR_NULL_PARAMETER, "tempSP");
        return mcsFAILURE;
    }

    /* backup char pointer to free later as tempSP is modified later */
    char* const tempSPPtr = tempSP;

    mcsUINT32 bufferLength = strlen(tempSP) + 1;

    logDebug("Original spectral type = '%s'.", spectralType);

    /* Remove ':', '(',')', ' ' from string, and move all the rest to UPPERCASE. */
    miscDeleteChr(tempSP, ':', mcsTRUE);
    miscDeleteChr(tempSP, '(', mcsTRUE);
    miscDeleteChr(tempSP, ')', mcsTRUE);
    miscDeleteChr(tempSP, ' ', mcsTRUE);
    miscStrToUpper(tempSP);

    logDebug("Cleaned spectral type = '%s'.", tempSP);

    /* If the spectral type contains a "+" sign, it is a sure sign that the star
     * is a close binary system.
     * Example: HD 47205 (J2000=06:36:41.0-19:15:21) which is K1III(+M) */
    mcsSTRING256 subStrings[2];
    mcsUINT32   nbSubString = 0;
    
    if (miscSplitString(tempSP, '+', subStrings, 2, &nbSubString) == mcsFAILURE)
    {
        errAdd(alxERR_WRONG_SPECTRAL_TYPE_FORMAT, spectralType);
        free(tempSPPtr);
        return mcsFAILURE;
    }
    if (nbSubString > 1)
    {
        strncpy(tempSP, subStrings[0], bufferLength);
        
        logDebug("Un-doubled spectral type = '%s'.", tempSP);

        decodedSpectralType->isDouble = mcsTRUE;
    }

    /* If the spectral type contains "SB", remove the trailing part and
     * tag it as spectral binary. */
    char* tokenPosition = strstr(tempSP, "SB");
    if (tokenPosition != NULL)
    {
        *tokenPosition = '\0'; /* Cut here */
        
        logDebug("Un-SB spectral type = '%s'.", tempSP);

        decodedSpectralType->isSpectralBinary = mcsTRUE;
    }

    /* Notice variability and remove it */
    tokenPosition = strstr(tempSP, "VAR");
    if (tokenPosition != NULL)
    {
        *tokenPosition = '\0'; /* Cut here */
        
        logDebug("Un-VAR spectral type = '%s'.", tempSP);

        decodedSpectralType->isVariable = mcsTRUE;
    }

    /* Index for the following */
    mcsUINT32 index = 0;

    /* If the spectral type contains "CN" or "BA" etc... (Cyanogen, Barium, etc) 
     * remove the annoying trailing part and tag it as hasSpectralLines */
    static char* hasSpectralIndicators[] = {"LAM", "FE", "MN", "HG", "CN", "BA", "SI", "SR", "CR", "EU", "MG", "EM", "CA", NULL};
    while (hasSpectralIndicators[index] != NULL)
    {
        /* If the current spectral type is found */
        if ((tokenPosition = strstr(tempSP, hasSpectralIndicators[index])) != NULL)
        {
            *tokenPosition = '\0'; /* Cut here */
            /* NO Break since the number and order of indicators is variable */
        }
        index++;
    }

    /*If O was wrongly written instead of 0 in normal classes, correct*/
    static char* hasWrongO[] = {"OO", "BO", "AO", "FO", "GO", "KO", "MO", NULL};
    index = 0;
    while (hasWrongO[index] != NULL)
    {
        tokenPosition = strstr(tempSP, hasWrongO[index]);
        if (tokenPosition != NULL)
        {
            *++tokenPosition = '0'; /* replace O by 0 */
            break;
        }
        index++;
    }

    /*Hesitates between consecutive classes: get inbetween*/
    static char* hesitateBetweenClasses[] = {"O/B", "O-B", "B/A", "B-A", "A/F", "A-F", "F/G", "F-G", "G/K", "G-K", "K/M", "K-M", NULL};
    index = 0;
    while (hesitateBetweenClasses[index] != NULL)
    {
        tokenPosition = strstr(tempSP, hesitateBetweenClasses[index]); /* Say "B/A" is a "B9." */
        if (tokenPosition != NULL)
        {
            *++tokenPosition = '9'; 
            *++tokenPosition = '.'; 
            break;
        }
        index++;
    }

    /* If the spectral type hesitates between two subclasses (A0/3, A0-3), 
     * or has a wrong comma, replace by a numerical value.
     */
    char        type, separator, type2;
    mcsSTRING32 tempBuffer;
    mcsINT32    firstSubType, secondSubType;
    mcsUINT32   nbOfTokens = sscanf(tempSP, "%c%1d%c%1d", &type, &firstSubType, &separator, &secondSubType);
    if (nbOfTokens == 4)
    {
        char* luminosityClassPointer = tempSP + 1; /* Skipping first char */

        if ((separator == '/') || (separator == '-'))
        {
            mcsDOUBLE meanSubType = (firstSubType + secondSubType) / 2.0;
            sprintf(tempBuffer, "%3.1lf", meanSubType);
            strncpy(luminosityClassPointer, tempBuffer, 3);
            
            logDebug("Un-hesitated spectral type = '%s'.", tempSP);
        }
        else if (separator == ',')
        {
            sprintf(tempBuffer, "%1d%c%1d", firstSubType, 46, secondSubType); /* "." */
            strncpy(luminosityClassPointer, tempBuffer, 3);
            
            logDebug("Un-comma-ed spectral type = '%s'.", tempSP);
        }
    } 

    /* If the spectral type is Xx/Xy..., it is another hesitation */
    nbOfTokens = sscanf(tempSP, "%c%1d/%c%1d", &type, &firstSubType, &type2,&secondSubType );
    if (nbOfTokens == 4)
    { 
        if (type == type2)
        {
            /* type A8/A9 , gives A8.50 for further interpretation*/
            char* luminosityClassPointer = tempSP + 1; /* Skipping first char */
            mcsDOUBLE meanSubType = (firstSubType + secondSubType) / 2.0;
            sprintf(tempBuffer, "%4.2lf", meanSubType);
            strncpy(luminosityClassPointer, tempBuffer, 4);
            
            logDebug("Un-hesitate(2) spectral type = '%s'.", tempSP);
        }
        else
        {
            /* in the case of, say, G8/K0, we want G8.50 */
            static char *hesitateBetweenClassesBis[] = {"O9/B0", "B9/A0", "A8/F0", "F8/G0", "G8/K0", "K7/M0", NULL};
            index = 0;
            while (hesitateBetweenClassesBis[index] != NULL)
            {
                tokenPosition = strstr(tempSP,hesitateBetweenClassesBis[index]); /* Say "B9/A0" is a "B9.50" */
                if (tokenPosition != NULL)
                {
                    tokenPosition++;
                    *++tokenPosition = '.'; 
                    *++tokenPosition = '5'; 
                    *++tokenPosition = '0'; 
                    break;
                }
                index++;
            }
        }
    }

    /* If the spectral type is AxM..., it is a peculiar A star which is normally a dwarf */
    nbOfTokens = sscanf(tempSP, "%c%1d%c", &type, &firstSubType, &separator);
    if (nbOfTokens == 3)
    { 
        if (separator == 'M')
        {
            /* V for Dwarf, to be further interpreted */
            snprintf(tempSP, bufferLength, "%c%1dV (%c%1d%c)", type, firstSubType, type, firstSubType, separator);
            
            logDebug("Un-M spectral type = '%s'.", tempSP);
        }
    } 
    
    /* If the spectral type is sd[OBAFG..]xx, it is a subdwarf of type VI */
    tokenPosition = strstr(tempSP, "SD");
    if (tokenPosition == tempSP)
    {
        nbOfTokens = sscanf(tempSP, "SD%c%c", &type, &separator);
        if (nbOfTokens == 2)
        { 
            snprintf(tempSP, sizeof(tempSP), "%c%cVI", type, separator); /* VI for SubDwarf, to be further interpreted */
        } 
        else
        {
            tempSP += 2; /* Skip leading 'SD' */
        }
        logDebug("Un-SD spectral type = '%s'.", tempSP);
    } 

    /* Properly parse cleaned-up spectral type string */
    nbOfTokens = sscanf(tempSP, "%c%lf%s", &(decodedSpectralType->code), &decodedSpectralType->quantity, decodedSpectralType->luminosityClass);

    /*nbitems 3 is OK*/
    /* If there is no luminosity class in given spectral type, reset it */
    if (nbOfTokens == 2)
    {
        strcpy(decodedSpectralType->luminosityClass, ""); 
    }
    else if (nbOfTokens == 1) /*meaning there is no numerical value for the spectral type */
    {
        /* try a simple [O-M] spectral type + luminosity class*/
        mcsINT32 nbOfTokens2 = sscanf(tempSP, "%c%s", &(decodedSpectralType->code), decodedSpectralType->luminosityClass);
        if (nbOfTokens2 > 0) 
        {
            /* Spectral Type covers one whole class, artificially put subclass at 5.
             * This is what the CDS java decoder does in fact! */
            decodedSpectralType->quantity = 5.0;  
        }
        else
        {
            /* Null spectral code, go no further */
            errAdd(alxERR_WRONG_SPECTRAL_TYPE_FORMAT, spectralType);
            free(tempSPPtr);
            return mcsFAILURE;
        }
    }
    else if (nbOfTokens == 0) 
    {
        /* Null spectral code, go no further */
        errAdd(alxERR_WRONG_SPECTRAL_TYPE_FORMAT, spectralType);
        free(tempSPPtr);
        return mcsFAILURE;
    }

    /*Insure the decodedSpectralType is something we handle well:*/
    switch (decodedSpectralType->code)
    {
        case 'O':
        case 'B':
        case 'A':
        case 'F':
        case 'G':
        case 'K':
        case 'M':
        break;
        default:
            errAdd(alxERR_WRONG_SPECTRAL_TYPE_FORMAT, spectralType);
            free(tempSPPtr);
            return mcsFAILURE;
    }
 
    logTest("Parsed spectral type = '%s' : Code = '%c', Sub-type Quantity = '%.2lf', Luminosity Class = '%s', Is Double  = '%s', Is Spectral Binary = '%s', Is Variable = '%s'", 
                tempSP, decodedSpectralType->code, decodedSpectralType->quantity, decodedSpectralType->luminosityClass,
                (decodedSpectralType->isDouble == mcsTRUE ? "YES" : "NO"),
                (decodedSpectralType->isSpectralBinary == mcsTRUE ? "YES" : "NO"),
                (decodedSpectralType->isVariable == mcsTRUE ? "YES" : "NO")
           );

    /* Return the pointer on the created spectral type structure */
    free(tempSPPtr);
    
    return mcsSUCCESS;
}

/**
 * Get the line in the color table which matches the star spectral type or the
 * line just after if interpolation is needed
 *
 * @param colorTable color table 
 * @param spectralType spectral type 
 * @param spType string of the spectral
 * 
 * @return a line number that matches the star spectral type or the line just
 * after or -1 if no match is found or interpolation is impossible
 */
static mcsINT32 alxGetLineForBrightStar(alxCOLOR_TABLE    *colorTable,
                                        alxSPECTRAL_TYPE  *spectralType,
                                        mcsSTRING32        spType)
{
    logTrace("alxGetLineForBrightStar()");

    mcsLOGICAL codeFound = mcsFALSE; 
    mcsLOGICAL found = mcsFALSE; 
    mcsINT32 line = 0;
    while ((found == mcsFALSE) && (line < colorTable->nbLines))
    {
        /* If the spectral type code match */
        if (colorTable->spectralType[line].code ==  spectralType->code)
        {
            /*
             * And quantities match or star quantity is lower than the one of
             * the current line
             */
            if (colorTable->spectralType[line].quantity>=spectralType->quantity)
            {
                /* Stop search */
                found = mcsTRUE;
            }
            else /* Else go to the next line */
            {
                line++;         
            }   

            /* the code of the spectral type had been found */
            codeFound = mcsTRUE;
        }
        else /* Spectral type code doesn't match */
        {
            /*
             * If the lines corresponding to the star spectral type code have
             * been scanned
             */
            if (codeFound == mcsTRUE)
            {
                /* Stop search */
                found = mcsTRUE;
            }
            else /* Else go to the next line */
            {
                line++;
            }
        }
    }

    /*
     * Check if spectral type is out of the table; i.e. before the first entry
     * in the color table. The quantity is strictly lower than the first entry
     * of the table 
     */
    if ((line == 0) && 
        (colorTable->spectralType[line].quantity != spectralType->quantity))
    {
        found = mcsFALSE;
    }

    /* If spectral type not found in color table, return error */
    if (found != mcsTRUE)
    {
        errAdd(alxERR_SPECTRAL_TYPE_NOT_FOUND, spType);
        return -1;
    }

    /* return the line found */
    return line;
}

/**
 * Get the line in the color table which matches the star magnitude difference
 * between J an K or the line just after if interpolation is need. In this case
 * the founs tag will be false
 *
 * @param colorTable color table 
 * @param found logical to know if the line is found 
 * @param spType string of the spectral
 * 
 * @return a line number that matches the star magnitude difference
 * between J an K or the line just after or -1 if no match is found or
 * interpolation is impossible
 */
static mcsINT32 alxGetLineForFaintStar(alxCOLOR_TABLE    *colorTable,
                                       alxSPECTRAL_TYPE  *spectralType,
                                       mcsSTRING32       spType,
                                       mcsDOUBLE          diffMagJK)
{
    logTrace("alxGetLineForFaintStar()");

    mcsINT32 line = 0;

    if (spectralType == NULL) 
    {
        mcsLOGICAL found = mcsFALSE; 
        while ((found == mcsFALSE) && (line < colorTable->nbLines))
        {
            /* If J-K == diff mag */
            if (colorTable->index[line][alxJ_K].value == diffMagJK)
            {
                found = mcsTRUE;
            }
            /* J-K > diff mag*/
            else if (colorTable->index[line][alxJ_K].value > diffMagJK)
            {
                if (line == 0)
                {
                    errAdd(alxERR_NO_LINE_FOUND);
                    return -1;
                }
                else
                {
                    found = mcsTRUE;
                }
            }
            else 
            {
                line++;
            }
        }

        if (found == mcsFALSE)
        {
            errAdd(alxERR_DIFFJK_NOT_IN_TABLE);
            return -1;
        }
    }
    else
    {
        mcsLOGICAL codeFound = mcsFALSE; 
        mcsLOGICAL found = mcsFALSE; 
        while ((found == mcsFALSE) && (line < colorTable->nbLines))
        {
            /* If the spectral type code match */
            if (colorTable->spectralType[line].code ==  spectralType->code)
            {
                /*
                 * And quantities match or star quantity is lower than the one 
                 * of the current line
                 */
                if (colorTable->spectralType[line].quantity >=
                    spectralType->quantity)
                {
                    /* Stop search */
                    found = mcsTRUE;
                }
                else /* Else go to the next line */
                {
                    line++;         
                }   

                /* the code of the spectral type had been found */
                codeFound = mcsTRUE;
            }
            else /* Spectral type code doesn't match */
            {
                /*
                 * If the lines corresponding to the star spectral type code 
                 * have been scanned
                 */
                if (codeFound == mcsTRUE)
                {
                    /* Stop search */
                    found = mcsTRUE;
                }
                else /* Else go to the next line */
                {
                    line++;
                }
            }
        }

        /*
         * Check if spectral type is out of the table; i.e. before the first 
         * entry in the color table. The quantity is strictly lower than the
         * first entry of the table 
         */
        if ((line == 0) && 
            (colorTable->spectralType[line].quantity != spectralType->quantity))
        {
            found = mcsFALSE;
        }

        /* If spectral type not found in color table, return error */
        if (found != mcsTRUE)
        {
            errAdd(alxERR_SPECTRAL_TYPE_NOT_FOUND, spType);
            return -1;
        }
    }

    /* return the line found */
    return line;
}

/**
 * Compute differential magnitudes.
 *
 * @param spType spectral type structure
 * @param mgB magnitude in B band
 * @param mgV magnitude in V band
 * @param diffMag differential magnitudes
 *
 * @return mcsSUCCESS on successfull completion otherwise, mcsFAILURE is
 * returned
 */
static mcsCOMPL_STAT 
alxComputeDiffMagnitudeForBrightStar(mcsSTRING32                 spType,
                                     mcsDOUBLE                   mgB,
                                     mcsDOUBLE                   mgV,
                                     alxDIFFERENTIAL_MAGNITUDES  diffMagnitudes)
{
    logTrace("alxComputeDiffMagnitudeForBrightStar()");
    
    alxSPECTRAL_TYPE *spectralType = malloc(sizeof(alxSPECTRAL_TYPE));
    if (spType != NULL)
    {
        /* 
         * Get each part of the spectral type XN.NLLL where X is a letter, N.N a
         * number between 0 and 9 and LLL is the light class
         */
        if (alxString2SpectralType(spType, spectralType) == mcsFAILURE)
        {
            free(spectralType);
            return mcsFAILURE;
        }
    }
    else
    {
        free(spectralType);
        spectralType = NULL;
    }
    
    /* Get the color table according to the spectral type of the star */
    alxCOLOR_TABLE* colorTable = alxGetColorTableForStar(spectralType, mcsTRUE);
    if (colorTable == NULL)
    {
        free(spectralType);
        return mcsFAILURE;
    }

    /* Line corresponding to the spectral type */
    mcsINT32 line = alxGetLineForBrightStar(colorTable, spectralType, spType);
    /* if line not found, i.e = -1, return mcsFAILURE */
    if (line == -1)
    {
        free(spectralType);
        return mcsFAILURE;
    }

    /* If the spectral type matches the line of the color table */
    diffMagnitudes[alxB_V].isSet = mcsFALSE;
    diffMagnitudes[alxV_I].isSet = mcsFALSE;
    diffMagnitudes[alxV_R].isSet = mcsFALSE;
    diffMagnitudes[alxI_J].isSet = mcsFALSE;
    diffMagnitudes[alxJ_H].isSet = mcsFALSE;
    diffMagnitudes[alxJ_K].isSet = mcsFALSE;
    diffMagnitudes[alxK_L].isSet = mcsFALSE;
    diffMagnitudes[alxK_M].isSet = mcsFALSE;

    if (colorTable->spectralType[line].quantity == spectralType->quantity)
    {
        /* 
         * Compare B-V star differential magnitude to the one of the color table
         * line; delta should be less than +/- 0.1 
         */
        if ((fabs((mgB - mgV) - colorTable->index[line][alxB_V].value)) <= 0.11)
        {
            /* Get differential magnitudes */
            diffMagnitudes[alxB_V].value = colorTable->index[line][alxB_V].value;
            diffMagnitudes[alxB_V].isSet = mcsTRUE;
            diffMagnitudes[alxV_I].value = colorTable->index[line][alxV_I].value;
            diffMagnitudes[alxV_I].isSet = mcsTRUE;
            diffMagnitudes[alxV_R].value = colorTable->index[line][alxV_R].value;
            diffMagnitudes[alxV_R].isSet = mcsTRUE;
            diffMagnitudes[alxI_J].value = colorTable->index[line][alxI_J].value;
            diffMagnitudes[alxI_J].isSet = mcsTRUE;
            diffMagnitudes[alxJ_H].value = colorTable->index[line][alxJ_H].value;
            diffMagnitudes[alxJ_H].isSet = mcsTRUE;
            diffMagnitudes[alxJ_K].value = colorTable->index[line][alxJ_K].value;
            diffMagnitudes[alxJ_K].isSet = mcsTRUE;
            diffMagnitudes[alxK_L].value = colorTable->index[line][alxK_L].value;
            diffMagnitudes[alxK_L].isSet = mcsTRUE;
            if ((colorTable->index[line][alxK_L].isSet != mcsFALSE) &&
                (colorTable->index[line][alxL_M].isSet != mcsFALSE))
            {
                diffMagnitudes[alxK_M].value = 
                      colorTable->index[line][alxK_L].value 
                    + colorTable->index[line][alxL_M].value;
            }
        }
        else
        {
            logTest("Could not compute differential magnitudes; "
                    "mgB-mgV = %.3e / B-V %.3e; delta > 0.1",
                    (mgB-mgV), colorTable->index[line][alxB_V].value);
        }
    }
    else /* Else, interpolate */
    {
        mcsDOUBLE ratio; /* need to compute ratio */
        mcsINT32 lineInf, lineSup; /* integer to have the lines sup and inf */
        lineSup = line;
        lineInf = line - 1;
        /*
        logDebug("Inferior line = %d", lineInf);
        logDebug("Superior line = %d", lineSup);
        */
        
        /*
         * Compare B-V star differential magnitude to the ones of the color
         * table inferior/superior lines; delta should be less than +/- 0.1 
         */
        if ((fabs((mgB-mgV) - colorTable->index[lineSup][alxB_V].value) <= 0.1) ||
            (fabs((mgB-mgV) - colorTable->index[lineInf][alxB_V].value) <= 0.1))
        {
            /* Compute ratio for interpolation */
            ratio = fabs(((mgB - mgV) - colorTable->index[lineInf][alxB_V].value) / (colorTable->index[lineSup][alxB_V].value - colorTable->index[lineInf][alxB_V].value));
            /* logTest("Ratio = %f", ratio); */

            alxDATA* dataSup = NULL;
            alxDATA* dataInf = NULL;
            
            /* Compute differential magnitudes */

            /* V - R */
            dataSup = &colorTable->index[lineSup][alxV_R];
            dataInf = &colorTable->index[lineInf][alxV_R];
            if ((dataSup->isSet != mcsFALSE) &&
                (dataInf->isSet != mcsFALSE))
            {
                diffMagnitudes[alxV_R].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
                diffMagnitudes[alxV_R].isSet = mcsTRUE;
            }

            /* V - I */
            dataSup = &colorTable->index[lineSup][alxV_I];
            dataInf = &colorTable->index[lineInf][alxV_I];
            if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
            {
                diffMagnitudes[alxV_I].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
                diffMagnitudes[alxV_I].isSet = mcsTRUE;
            }

            /* I - J */
            dataSup = &colorTable->index[lineSup][alxI_J];
            dataInf = &colorTable->index[lineInf][alxI_J];
            if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
            {
                diffMagnitudes[alxI_J].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
                diffMagnitudes[alxI_J].isSet = mcsTRUE;
            }

            /* J - H */
            dataSup = &colorTable->index[lineSup][alxJ_H];
            dataInf = &colorTable->index[lineInf][alxJ_H];
            if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
            {
                diffMagnitudes[alxJ_H].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
                diffMagnitudes[alxJ_H].isSet = mcsTRUE;
            }

            /* J - K */
            dataSup = &colorTable->index[lineSup][alxJ_K];
            dataInf = &colorTable->index[lineInf][alxJ_K];
            if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
            {
                diffMagnitudes[alxJ_K].value = dataInf->value + ratio * (dataSup->value - dataSup->value);
                diffMagnitudes[alxJ_K].isSet = mcsTRUE;
            }

            /* K - L */
            dataSup = &colorTable->index[lineSup][alxK_L];
            dataInf = &colorTable->index[lineInf][alxK_L];
            if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
            {
                diffMagnitudes[alxK_L].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
                diffMagnitudes[alxK_L].isSet = mcsTRUE;
            }

            /* K - L & L - M */
            if ((colorTable->index[lineSup][alxK_L].isSet != mcsFALSE) &&
                (colorTable->index[lineInf][alxK_L].isSet != mcsFALSE) &&
                (colorTable->index[lineSup][alxL_M].isSet != mcsFALSE) &&
                (colorTable->index[lineInf][alxL_M].isSet != mcsFALSE))
            {
                diffMagnitudes[alxK_M].value = colorTable->index[lineInf][alxK_L].value + colorTable->index[lineInf][alxL_M].value + ratio *(colorTable->index[lineSup][alxK_L].value + colorTable->index[lineSup][alxL_M].value - colorTable->index[lineInf][alxK_L].value - colorTable->index[lineInf][alxL_M].value);
                diffMagnitudes[alxK_M].isSet = mcsTRUE;
            }
        }
        else
        {
            logTest("mgB-mgV = %.3lf / B-V [%.3lf..%.3lf]; delta > 0.1",
                    (mgB-mgV), colorTable->index[lineInf][alxB_V].value, 
                    colorTable->index[lineSup][alxB_V].value);

        }
    }
    
    free(spectralType);

    return mcsSUCCESS;
}

/**
 * Compute differential magnitudes in faint.
 *
 * @param mgB magnitude in B band
 * @param mgV magnitude in V band
 * @param diffMag differential magnitudes
 *
 * @return mcsSUCCESS on successfull completion otherwise, mcsFAILURE is
 * returned
 */
static mcsCOMPL_STAT alxComputeDiffMagnitudeForFaintStar(mcsSTRING32                 spType,
                                                         mcsDOUBLE                   mgJ,
                                                         mcsDOUBLE                   mgK,
                                                         alxDIFFERENTIAL_MAGNITUDES  diffMagnitudes)
{
    logTrace("alxComputeDiffMagnitudeForFaintStar()");

    alxSPECTRAL_TYPE *spectralType = malloc(sizeof(alxSPECTRAL_TYPE));
    if (spType != NULL)
    {
        /* 
         * Get each part of the spectral type XN.NLLL where X is a letter, N.N a
         * number between 0 and 9 and LLL is the light class
         */
        if (alxString2SpectralType(spType, spectralType) == mcsFAILURE)
        {
            free(spectralType);
            return mcsFAILURE;
        }
    }
    else
    {
        free(spectralType);
        spectralType = NULL;
        /* printf("FAINT--Spectral Type Pointer NULL!\n"); */
    }
    
    /* Get the color table according to the spectral type of the star */
    alxCOLOR_TABLE* colorTable = alxGetColorTableForStar(spectralType, mcsFALSE);
    if (colorTable == NULL)
    {
        free(spectralType);
        return mcsFAILURE;
    }

    /* Line corresponding to the spectral type */
    mcsINT32 line = alxGetLineForFaintStar(colorTable, spectralType, spType, mgJ-mgK);
    /* if line not found, i.e = -1, return mcsFAILURE */
    if (line == -1)
    {
        free(spectralType);
        return mcsFAILURE;
    }

    /* If the spectral type matches the line of the color table */
    diffMagnitudes[alxB_V].isSet = mcsFALSE;
    diffMagnitudes[alxV_I].isSet = mcsFALSE;
    diffMagnitudes[alxV_R].isSet = mcsFALSE;
    diffMagnitudes[alxI_J].isSet = mcsFALSE;
    diffMagnitudes[alxJ_H].isSet = mcsFALSE;

    if (colorTable->index[line][alxJ_K].value == (mgJ - mgK))
    {
        /* Get differential magnitudes */
        diffMagnitudes[alxB_V].value = colorTable->index[line][alxB_V].value;
        diffMagnitudes[alxB_V].isSet = mcsTRUE;
        diffMagnitudes[alxV_I].value = colorTable->index[line][alxV_I].value;
        diffMagnitudes[alxV_I].isSet = mcsTRUE;
        diffMagnitudes[alxV_R].value = colorTable->index[line][alxV_R].value;
        diffMagnitudes[alxV_R].isSet = mcsTRUE;
        diffMagnitudes[alxI_J].value = colorTable->index[line][alxI_J].value;
        diffMagnitudes[alxI_J].isSet = mcsTRUE;
        diffMagnitudes[alxJ_H].value = colorTable->index[line][alxJ_H].value;
        diffMagnitudes[alxJ_H].isSet = mcsTRUE;

    }
    else /* Else, interpolate */
    {
        mcsDOUBLE ratio; /* Need to compute ratio */
        mcsINT32 lineInf, lineSup; /* integer to have the lines sup and inf */
        lineSup = line;
        lineInf = line - 1;

        /* logDebug("Inferior line = %d", lineInf); */
        /* logDebug("Superior line = %d", lineSup); */
        /* logTest("%f < J-K (%f) < %f", 
                colorTable->index[lineInf][alxJ_K].value,
                mgJ-mgK,
                colorTable->index[lineSup][alxJ_K].value); */

        /* Compute ratio for interpolation */
        if (colorTable->index[lineSup][alxJ_K].value != 
            colorTable->index[lineInf][alxJ_K].value)
        {
            ratio = fabs(((mgJ-mgK) - colorTable->index[lineInf][alxJ_K].value) 
                         / (colorTable->index[lineSup][alxJ_K].value 
                            - colorTable->index[lineInf][alxJ_K].value));
        }
        /* If ratio can be computed, return failure */
        else
        {
            ratio = 0.5;
        }
        /* logTest("Ratio = %f", ratio); */
        
        alxDATA* dataSup = NULL;
        alxDATA* dataInf = NULL;
            
        /* Compute differential magnitudes */

        /* V - R */
        dataSup = &colorTable->index[lineSup][alxV_R];
        dataInf = &colorTable->index[lineInf][alxV_R];
        if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
        {
            diffMagnitudes[alxV_R].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
            diffMagnitudes[alxV_R].isSet = mcsTRUE;
        }

        /* V - I */
        dataSup = &colorTable->index[lineSup][alxV_I];
        dataInf = &colorTable->index[lineInf][alxV_I];
        if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
        {
            diffMagnitudes[alxV_I].value = (-1) * (dataInf->value + ratio * (dataSup->value - dataInf->value));
            diffMagnitudes[alxV_I].isSet = mcsTRUE;
        }

        /* I - J */
        dataSup = &colorTable->index[lineSup][alxI_J];
        dataInf = &colorTable->index[lineInf][alxI_J];
        if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
        {
            diffMagnitudes[alxI_J].value = (-1) * (dataInf->value + ratio * (dataSup->value - dataInf->value));
            diffMagnitudes[alxI_J].isSet = mcsTRUE;
        }

        /* J - H */
        dataSup = &colorTable->index[lineSup][alxJ_H];
        dataInf = &colorTable->index[lineInf][alxJ_H];
        if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
        {
            diffMagnitudes[alxJ_H].value = dataInf->value + ratio * (dataSup->value - dataInf->value);
            diffMagnitudes[alxJ_H].isSet = mcsTRUE;
        }

        /* B - V */
        dataSup = &colorTable->index[lineSup][alxB_V];
        dataInf = &colorTable->index[lineInf][alxB_V];
        if ((dataSup->isSet != mcsFALSE) && (dataInf->isSet != mcsFALSE))
        {
            diffMagnitudes[alxB_V].value = (-1) * (dataInf->value + ratio * (dataSup->value - dataSup->value));
            diffMagnitudes[alxB_V].isSet = mcsTRUE;
        }
    }

    free(spectralType);
    
    return mcsSUCCESS;
}

/**
 * Compute magnitude and set confidence index of a specific magnitude
 * 
 * @param magnitude magnitude to modify
 * @param firstMag first magnitude of the calcul (X-Y)
 * @param diffMag difference magnitude (ex : V-K)
 * @param confIndex the confidence index to set to the modify magnitude if
 * necessary
 * 
 * @return always SUCCESS
 */
static mcsCOMPL_STAT alxComputeMagnitude(mcsDOUBLE firstMag,
                                         alxDATA diffMag,
                                         alxDATA *magnitude,
                                         alxCONFIDENCE_INDEX confIndex)
{
    logTrace("alxComputeMagnitude()");
    
    /* If magnitude is not set */
    if (magnitude->isSet == mcsFALSE)
    {
        /* If magnitude difference needed is equal to blanking value */
        if (diffMag.isSet != mcsFALSE)
        {
            /* Else compute*/
            magnitude->value = firstMag - diffMag.value;
            /* Set correct confidence index */
            magnitude->confIndex = confIndex;
            magnitude->isSet = mcsTRUE;
        }
        else
        {
            magnitude->confIndex = alxNO_CONFIDENCE;
        }
    }
    
    return mcsSUCCESS;
}

/**
 * Compute missing magnitude according to the differential magnitudes
 *
 * @param diffMag diffrential magnitudes
 * @param magnitudes all magnitudes bands
 * @param mgV magnitude in V band
 *
 * @return always mcsSUCCESS
 */
static mcsCOMPL_STAT 
alxComputeAllMagnitudesForBrightStar(alxDIFFERENTIAL_MAGNITUDES diffMagnitudes,
                                     alxMAGNITUDES magnitudes,
                                     mcsDOUBLE mgV)
{
    logTrace("alxComputeAllMagnitudesForBrightStar()");

    /* Set confidence index for computed values */
    /* If magnitude in K band is unknown, set confidence index to Low */
    alxCONFIDENCE_INDEX confIndex;
    if (magnitudes[alxK_BAND].isSet == mcsFALSE)
    {
        confIndex = alxCONFIDENCE_LOW;
    }
    /* Else B, V and K is known */
    else
    {
        confIndex = alxCONFIDENCE_HIGH;
    }
    /* Compute *missing* magnitudes in R, I, J, H, K, L and M bands */
    alxComputeMagnitude(mgV,
                        diffMagnitudes[alxV_R],
                        &(magnitudes[alxR_BAND]),
                        confIndex);
    alxComputeMagnitude(mgV,
                        diffMagnitudes[alxV_I],
                        &(magnitudes[alxI_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxI_BAND].value,
                        diffMagnitudes[alxI_J],
                        &(magnitudes[alxJ_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxJ_BAND].value,
                        diffMagnitudes[alxJ_H],
                        &(magnitudes[alxH_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxJ_BAND].value,
                        diffMagnitudes[alxJ_K],
                        &(magnitudes[alxK_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxK_BAND].value,
                        diffMagnitudes[alxK_L],
                        &(magnitudes[alxL_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxK_BAND].value,
                        diffMagnitudes[alxK_M],
                        &(magnitudes[alxM_BAND]),
                        confIndex);

    return mcsSUCCESS;
}

/**
 * Compute missing magnitude according to the differential magnitudes
 *
 * @param diffMag diffrential magnitudes
 * @param magnitudes all magnitudes bands
 * @param mgJ magnitude in J band
 *
 * @return always mcsSUCCESS
 */
static mcsCOMPL_STAT 
alxComputeAllMagnitudesForFaintStar(alxDIFFERENTIAL_MAGNITUDES diffMagnitudes,
                                    alxMAGNITUDES magnitudes,
                                    mcsDOUBLE mgJ)
{
    logTrace("alxComputeAllMagnitudesForFaintStar()");
    
    /* Set confidence index for computed values */
    /* If magnitude in K band is unknown, set confidence index to Low */
    alxCONFIDENCE_INDEX confIndex;
    if (magnitudes[alxK_BAND].isSet == mcsFALSE)
    {
        confIndex = alxCONFIDENCE_LOW;
    }
    /* Else B, V and K is known */
    else
    {
        confIndex = alxCONFIDENCE_HIGH;
    }
    /* Compute *missing* magnitudes in R, I, J, H, K, bands */
    alxComputeMagnitude(mgJ,
                        diffMagnitudes[alxJ_H],
                        &(magnitudes[alxH_BAND]),
                        confIndex);
    alxComputeMagnitude(mgJ,
                        diffMagnitudes[alxI_J],
                        &(magnitudes[alxI_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxI_BAND].value,
                        diffMagnitudes[alxV_I],
                        &(magnitudes[alxV_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxV_BAND].value,
                        diffMagnitudes[alxV_R],
                        &(magnitudes[alxR_BAND]),
                        confIndex);
    alxComputeMagnitude(magnitudes[alxV_BAND].value,
                        diffMagnitudes[alxB_V],
                        &(magnitudes[alxB_BAND]),
                        confIndex);

    return mcsSUCCESS;
}

/*
 * Public functions definition
 */

/**
 * Compute magnitudes in R, I, J, H, K, L and M bands for bright star.
 *
 * It computes magnitudes in R, I, J, H, K, L and M bands according to the
 * spectral type and the magnitudes in B and V bands for a bright star.
 * If magnitude in B or V are unknown, no magnitude will be computed.
 * otherwise, if K band is unkwown, the confidence index of
 * computed values is set to LOW, otherwise (K known) it is set to 
 * HIGH.
 * If magnitude can not be computed, its associated confidence index is set to
 * NO CONFIDENCE.
 *
 * @param spType spectral type
 * @param magnitudes contains magnitudes in B and V bands, and the computed
 * magnitudes in R, I, J, H, K, L and M bands
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT 
alxComputeMagnitudesForBrightStar(mcsSTRING32 spType,
                                  alxMAGNITUDES magnitudes)
{ 
    logTrace("alxComputeMagnitudesForBrightStar()");

    /* 
     * If magnitude B or V are not set, return SUCCESS : the alxMAGNITUDE
     * structure will not be changed -> the magnitude won't be computed 
     */
    if ((magnitudes[alxB_BAND].isSet == mcsFALSE) ||
        (magnitudes[alxV_BAND].isSet == mcsFALSE))
    {
        /* Set to low the confidence index of each unknown magnitude */
        int i;
        for (i = 0; i < alxNB_BANDS; i++)
        {
            /* 
             * If the band is not affected , write alxCONFIDENCE_LOW into the
             * confidence index table 
             */
            if (magnitudes[i].isSet == mcsFALSE)
            {
                magnitudes[i].confIndex = alxCONFIDENCE_LOW;
            }
        }
        return mcsSUCCESS;
    }

    /* If B and V are affected, get magnitudes in B and V bands */
    mcsDOUBLE mgB, mgV;
    mgB = magnitudes[alxB_BAND].value;
    mgV = magnitudes[alxV_BAND].value;
    
    /* Create a differential magnitudes structure */
    alxDIFFERENTIAL_MAGNITUDES diffMag;
    /* Compute differential magnitude */
    if (alxComputeDiffMagnitudeForBrightStar(spType, mgB, mgV, diffMag) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    /* Compute all new magnitude */
    alxComputeAllMagnitudesForBrightStar(diffMag, magnitudes, mgV);
    
    /* Print out results */
    logTest("Computed magnitudes (bright): B = %0.3lf (%s), V = %0.3lf (%s), "
            "R = %0.3lf (%s), I = %0.3lf (%s), J = %0.3lf (%s), H = %0.3lf (%s), "
            "K = %0.3lf (%s), L = %0.3lf (%s), M = %0.3lf (%s)", 
            magnitudes[alxB_BAND].value, alxGetConfidenceIndex(magnitudes[alxB_BAND].confIndex), 
            magnitudes[alxV_BAND].value, alxGetConfidenceIndex(magnitudes[alxV_BAND].confIndex), 
            magnitudes[alxR_BAND].value, alxGetConfidenceIndex(magnitudes[alxR_BAND].confIndex), 
            magnitudes[alxI_BAND].value, alxGetConfidenceIndex(magnitudes[alxI_BAND].confIndex), 
            magnitudes[alxJ_BAND].value, alxGetConfidenceIndex(magnitudes[alxJ_BAND].confIndex), 
            magnitudes[alxH_BAND].value, alxGetConfidenceIndex(magnitudes[alxH_BAND].confIndex), 
            magnitudes[alxK_BAND].value, alxGetConfidenceIndex(magnitudes[alxK_BAND].confIndex), 
            magnitudes[alxL_BAND].value, alxGetConfidenceIndex(magnitudes[alxL_BAND].confIndex), 
            magnitudes[alxM_BAND].value, alxGetConfidenceIndex(magnitudes[alxM_BAND].confIndex));

    return mcsSUCCESS;
}

/**
 * Compute magnitudes in B, V, R, I, K, L and M bands for faint star.
 *
 * It computes magnitudes in B, V, R, I, K, L and M bands according to the
 * magnitudes in J and K bands for a faint star.
 * If magnitude in J and K band is unkwown, the confidence index of computed
 * values is set to LOW, otherwise (J and K known) it is set to HIGH.
 * If magnitude can not be computed, its associated confidence index is set to
 * NO CONFIDENCE.
 *
 * @param spType spectral type
 * @param magnitudes contains magnitudes in J and K bands, and the computed
 * magnitudes in B, V, R, I, K, L and M bands
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT 
alxComputeMagnitudesForFaintStar(mcsSTRING32 spType,
                                 alxMAGNITUDES magnitudes)
{ 
    logTrace("alxComputeMagnitudesForFaintStar()");

    /* 
     * If magnitude J or K are not set, return SUCCESS : the alxMAGNITUDE
     * structure will not be changed -> the magnitude won't be computed 
     */
    if ((magnitudes[alxJ_BAND].isSet == mcsFALSE) ||
        (magnitudes[alxK_BAND].isSet == mcsFALSE))
    {
        /* Set to low the confidence index of each unknown magnitude */
        int i;
        for (i = 0; i < alxNB_BANDS; i++)
        {
            /* 
             * If the band is not affected , write alxCONFIDENCE_LOW into the
             * confidence index table 
             */
            if (magnitudes[i].isSet == mcsFALSE)
            {
                magnitudes[i].confIndex = alxCONFIDENCE_LOW;
            }
        }
        return mcsSUCCESS;
    }
    /* If J and K are affected, get magnitudes in J and K bands */
    mcsDOUBLE mgJ, mgK;
    mgJ = magnitudes[alxJ_BAND].value;
    mgK = magnitudes[alxK_BAND].value;
    
    /* Create a differential magnitudes structure */
    alxDIFFERENTIAL_MAGNITUDES diffMag;
    /* Compute differential magnitude */
    if (alxComputeDiffMagnitudeForFaintStar(spType, mgJ, mgK, diffMag) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    /* Compute all new magnitude */
    alxComputeAllMagnitudesForFaintStar(diffMag, magnitudes, mgJ);
    
    /* Print out results */
    logTest("Computed magnitudes (faint): B = %0.3lf (%s), V = %0.3lf (%s), "
            "R = %0.3lf (%s), I = %0.3lf (%s), J = %0.3lf (%s), H = %0.3lf (%s), "
            "K = %0.3lf (%s), L = %0.3lf (%s), M = %0.3lf (%s)", 
            magnitudes[alxB_BAND].value, alxGetConfidenceIndex(magnitudes[alxB_BAND].confIndex), 
            magnitudes[alxV_BAND].value, alxGetConfidenceIndex(magnitudes[alxV_BAND].confIndex), 
            magnitudes[alxR_BAND].value, alxGetConfidenceIndex(magnitudes[alxR_BAND].confIndex), 
            magnitudes[alxI_BAND].value, alxGetConfidenceIndex(magnitudes[alxI_BAND].confIndex), 
            magnitudes[alxJ_BAND].value, alxGetConfidenceIndex(magnitudes[alxJ_BAND].confIndex), 
            magnitudes[alxH_BAND].value, alxGetConfidenceIndex(magnitudes[alxH_BAND].confIndex), 
            magnitudes[alxK_BAND].value, alxGetConfidenceIndex(magnitudes[alxK_BAND].confIndex), 
            magnitudes[alxL_BAND].value, alxGetConfidenceIndex(magnitudes[alxL_BAND].confIndex), 
            magnitudes[alxM_BAND].value, alxGetConfidenceIndex(magnitudes[alxM_BAND].confIndex));

    return mcsSUCCESS;
}

/**
 * Compute corrected magnitudes according to the interstellar absorption and the
 * corrected ones.
 *
 * The corrected magnitudes are computed using the galactic interstellar
 * extinction model based on galatic coordinates and distance of the star.
 *
 * @param av the extinction ratio
 * @param magnitudes computed magnitudes in B, V, R, I, J, H, K, L and M bands
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
/* WARNING: the computation of corrected magnitudes based on interstellar
 * absorption, described in JMMC-MEM-2600-0008 document (section 2.1.1), has not
 * been implemented because there is no catalog which provides this information
 * for a representative number of stars.
 */
mcsCOMPL_STAT alxComputeCorrectedMagnitudes(mcsDOUBLE      av,
                                            alxMAGNITUDES magnitudes)
{
    logTrace("alxComputeCorrectedMagnitudes()");

    /* Get extinction ratio table */
    alxEXTINCTION_RATIO_TABLE *extinctionRatioTable;
    extinctionRatioTable = alxGetExtinctionRatioTable();
    if (extinctionRatioTable == NULL)
    {
        return mcsFAILURE;        
    }

    /* 
     * Computed corrected magnitudes.
     * Co = C - Ac
     * where Ac = Av*Rc/Rv, with Rv=3.10
     * If the pointer of a magnitude is NULL its means that there is nothing
     * to compute. In this case, do nothing
     */
    int band;
    for (band = alxB_BAND; band <= alxM_BAND; band++)
    {
        if (magnitudes[band].isSet == mcsTRUE)
        {
            magnitudes[band].value = magnitudes[band].value 
                - (av * extinctionRatioTable->rc[band] / 3.10);
        }
    }

    /* Print out results */
    logTest("Corrected magnitudes: B = %0.3lf (%s), V = %0.3lf (%s), "
            "R = %0.3lf (%s), I = %0.3lf (%s), J = %0.3lf (%s), H = %0.3lf (%s), "
            "K = %0.3lf (%s), L = %0.3lf (%s), M = %0.3lf (%s)", 
            magnitudes[alxB_BAND].value, alxGetConfidenceIndex(magnitudes[alxB_BAND].confIndex), 
            magnitudes[alxV_BAND].value, alxGetConfidenceIndex(magnitudes[alxV_BAND].confIndex), 
            magnitudes[alxR_BAND].value, alxGetConfidenceIndex(magnitudes[alxR_BAND].confIndex), 
            magnitudes[alxI_BAND].value, alxGetConfidenceIndex(magnitudes[alxI_BAND].confIndex), 
            magnitudes[alxJ_BAND].value, alxGetConfidenceIndex(magnitudes[alxJ_BAND].confIndex), 
            magnitudes[alxH_BAND].value, alxGetConfidenceIndex(magnitudes[alxH_BAND].confIndex), 
            magnitudes[alxK_BAND].value, alxGetConfidenceIndex(magnitudes[alxK_BAND].confIndex), 
            magnitudes[alxL_BAND].value, alxGetConfidenceIndex(magnitudes[alxL_BAND].confIndex), 
            magnitudes[alxM_BAND].value, alxGetConfidenceIndex(magnitudes[alxM_BAND].confIndex));

    return mcsSUCCESS;
}

/**
 * Returns the ratio of the plack function B(lambda1,T1)/B(lambda2,T2)
 * using constants and reduced planck function as in T. Michels, 
 * Nasa Technical Note D-4446.
 *
 * @param Teff1 double the first BlackBody temperature 
 * @param lambda1 double the first BlackBody wavelength (in microns)
 * @param Teff2 double the second BlackBody temperature 
 * @param lambda2 double the second BlackBody wavelength (in microns)
 *
 * @return double the flux ratio
 */
static mcsDOUBLE alxBlackBodyFluxRatio(mcsDOUBLE Teff1,
				                       mcsDOUBLE lambda1,
				                       mcsDOUBLE Teff2,
				                       mcsDOUBLE lambda2)
{
    logTrace("alxBlackBodyFluxRatio()");

	/* Constants are for lambda^-1 in cm^-1, but exponent 3 is correct for flux
	 * densities in wavelength */
    mcsDOUBLE nu1 = 10000.0 / lambda1;  /*wavenumber cm^-1*/
    mcsDOUBLE nu2 = 10000.0 / lambda2;  /*wavenumber cm^-1*/

    mcsDOUBLE x = nu1 / Teff1;
    mcsDOUBLE y = nu2 / Teff2;

    mcsDOUBLE ratio = pow((x / y), 3.0) * (exp(1.43879 * y) -1) / (exp(1.43879 * x) -1);
    return ratio;
}

static alxAKARI_TABLE* alxLoadAkariTable()
{
    logTrace("alxLoadAkariTable()");

    /* To know if it was loaded already */
    static alxAKARI_TABLE akariTable = {mcsFALSE, "alxAkariBlackBodyCorrectionTable.cfg"};

    /*
     * Check if the structure in which polynomial coefficients will be stored is
     * loaded into memory. If not load it.
     */
    if (akariTable.loaded == mcsTRUE)
    {
        return &akariTable;
    }

    /* Find the location of the file */
    char* fileName = miscLocateFile(akariTable.fileName);
    if (fileName == NULL)
    {
        return NULL;
    }
    
    /* Load file (skipping comment lines starting with '#') */
    miscDYN_BUF dynBuf;
    miscDynBufInit(&dynBuf);
    
    logDebug("Loading %s ...", fileName);
    
    if (miscDynBufLoadFile(&dynBuf, fileName, "#") == mcsFAILURE)
    {
        miscDynBufDestroy(&dynBuf);
        free(fileName);
        return NULL;
    }

    /* For each line of the loaded file */
    mcsINT32 lineNum = 0;
    const char *pos = NULL;
    mcsSTRING1024 line;
    while ((pos = miscDynBufGetNextLine(&dynBuf, pos, line, sizeof(line), mcsTRUE)) != NULL)
    {
        logTrace("miscDynBufGetNextLine() = '%s'", line);

        /* Trim line for any leading and trailing blank characters */
        miscTrimString (line, " ");

        /* If line is not empty */
        if (strlen(line) != 0)
        {
            /* Check if there are to many lines in file */
            if (lineNum >= alxNB_AKARI_TEFF)
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_TOO_MANY_LINES, fileName);
                free(fileName);
                return NULL;
            }

            /* Try to read each BB correction coefficients */
            mcsINT32 nbOfReadTokens = sscanf(line, "%lf %lf %lf %lf %lf %lf %lf",   
                       &akariTable.teff[lineNum],
                       &akariTable.coeff[lineNum][0],
                       &akariTable.coeff[lineNum][1],
                       &akariTable.coeff[lineNum][2],
                       &akariTable.coeff[lineNum][3],
                       &akariTable.coeff[lineNum][4],
                       &akariTable.coeff[lineNum][5]);

            /* If parsing went wrong */
            if (nbOfReadTokens != (alxNB_AKARI_BANDS + 1))
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_WRONG_FILE_FORMAT, line, fileName);
                free(fileName);
                return NULL;
            }

            /* Next line */
            lineNum++;
        }
    }

    /* Set the total number of lines in the akari table */
    akariTable.nbLines = lineNum;

    /* Mark the akari table as "loaded" */
    akariTable.loaded = mcsTRUE;

    /* Destroy the temporary dynamic buffer used to parse the akari table file */
    miscDynBufDestroy(&dynBuf);
    free(fileName);

    /* Return a pointer on the freshly loaded akari table */
    return &akariTable;
}


static mcsINT32 alxGetLineForAkari(alxAKARI_TABLE *akariTable,
		                           mcsDOUBLE       Teff)
{
    logTrace("alxGetLineForAkari()");

    mcsLOGICAL found = mcsFALSE; 
    mcsINT32 line = 0;
    while ((found == mcsFALSE) && (line < akariTable->nbLines))
    {
        /* get line immediately above Teff */
        if (akariTable->teff[line] > Teff)
        {
            found = mcsTRUE;
        }
        else /* Else go to the next line */
        {
            line++;
        }
    }

    /* If spectral type not found in akari table, return error */
    if (line == 0)
    {
        return -1;
    }

    /* return the line found */
    return line;
}


mcsCOMPL_STAT alxComputeFluxesFromAkari18(mcsDOUBLE  Teff,
                                          mcsDOUBLE *fnu_18,
                                          mcsDOUBLE *fnu_12,
                                          mcsDOUBLE *fnu_9)
{
    mcsDOUBLE correctionFactor;
    mcsDOUBLE bandFluxRatio;
    mcsDOUBLE mono18,mono9;

    logTrace("alxComputeFluxesFromAkari18()");

    /* Load Akari table  */
    alxAKARI_TABLE* akariTable = alxLoadAkariTable();
    if (akariTable == NULL)
    {
        return mcsFAILURE;
    }

    /* Line corresponding to the spectral type */
    mcsINT32 line = alxGetLineForAkari(akariTable, Teff);
    /* if line not found, i.e = -1, return mcsFAILURE */
    if (line == -1)
    {
        return mcsFAILURE;
    }

    /* interpolate */
    mcsDOUBLE ratio; /* Needed to compute ratio */
    mcsINT32 lineSup = line;
    mcsINT32 lineInf = line - 1;

    
    /* logTest("Inferior line = %d", lineInf); */
    /* logTest("Superior line = %d", lineSup); */
    /* logTest("%f < Teff (%f) < %f", akariTable->teff[lineInf], Teff, akariTable->teff[lineSup]); */

    /* Compute ratio for interpolation */
    if (akariTable->teff[lineSup] !=  akariTable->teff[lineInf])
    {
        ratio = fabs(((Teff) - akariTable->teff[lineInf]) 
                / (akariTable->teff[lineSup]
                - akariTable->teff[lineInf]));
    }
    else
    {
        ratio = 0.5;
    }
    
    /* logTest("Ratio = %f", ratio); */
	
    /* Compute correction Factor */
    mcsDOUBLE dataSup = akariTable->coeff[lineSup][alx18mu];
    mcsDOUBLE dataInf = akariTable->coeff[lineInf][alx18mu];
    correctionFactor = dataInf + ratio * (dataSup - dataInf);
    /* logTest("correctionFactor = %f", correctionFactor); */

    mono18 = (*fnu_18) / correctionFactor;

    /* compute new flux at 12 mu by black_body approximation */
    bandFluxRatio = alxBlackBodyFluxRatio(Teff, (mcsDOUBLE) 12.0, Teff, (mcsDOUBLE)AKARI_18MU);
    *fnu_12 = mono18 * bandFluxRatio;

    /* compute (complementary) fnu_9 in the same manner:*/
    bandFluxRatio = alxBlackBodyFluxRatio(Teff, (mcsDOUBLE)AKARI_9MU, Teff, (mcsDOUBLE)AKARI_18MU);
    mono9 = mono18 * bandFluxRatio;

    /* for coherence of data, use the correction factor to give an estimate of the akari band flux,
     * not the monochromatic flux: */
    dataSup = akariTable->coeff[lineSup][alx9mu];
    dataInf = akariTable->coeff[lineInf][alx9mu];
    correctionFactor = dataInf + ratio * (dataSup - dataInf);
    *fnu_9 = mono9 * correctionFactor;

    return mcsSUCCESS;
}

mcsCOMPL_STAT alxComputeFluxesFromAkari09(mcsDOUBLE  Teff,
                                          mcsDOUBLE *fnu_9,
                                          mcsDOUBLE *fnu_12,
                                          mcsDOUBLE *fnu_18)
{
    mcsDOUBLE correctionFactor;
    mcsDOUBLE bandFluxRatio;
    mcsDOUBLE mono18,mono9;

    logTrace("alxComputeFluxesFromAkari09()");

    /* Load Akari table  */
    alxAKARI_TABLE* akariTable = alxLoadAkariTable();
    if (akariTable == NULL)
    {
        return mcsFAILURE;
    }

    /* Line corresponding to the spectral type */
    mcsINT32 line = alxGetLineForAkari(akariTable, Teff);
    /* if line not found, i.e = -1, return mcsFAILURE */
    if (line == -1)
    {
        return mcsFAILURE;
    }

    /* interpolate */
    mcsDOUBLE ratio; /* Needed to compute ratio */
    mcsINT32 lineSup = line;
    mcsINT32 lineInf = line - 1;

    /* logTest("Inferior line = %d", lineInf); */
    /* logTest("Superior line = %d", lineSup); */ 
    /* logTest("%f < Teff (%f) < %f", akariTable->teff[lineInf], Teff, akariTable->teff[lineSup]); */

    /* Compute ratio for interpolation */
    if (akariTable->teff[lineSup] !=  akariTable->teff[lineInf])
    {
        ratio = fabs(((Teff) - akariTable->teff[lineInf]) 
                / (akariTable->teff[lineSup]
                - akariTable->teff[lineInf]));
    }
    else
    {
        ratio = 0.5;
    }
    
    /* logTest("Ratio = %f", ratio); */
	
    /* Compute correction Factor */
    mcsDOUBLE dataSup = akariTable->coeff[lineSup][alx9mu];
    mcsDOUBLE dataInf = akariTable->coeff[lineInf][alx9mu];
    correctionFactor = dataInf + ratio * (dataSup - dataInf);
    /* logTest("correctionFactor = %f", correctionFactor); */

    mono9 = (*fnu_9) / correctionFactor;

    /* compute new flux at 12 mu by black_body approximation */
    bandFluxRatio = alxBlackBodyFluxRatio(Teff, (mcsDOUBLE) 12.0, Teff, (mcsDOUBLE)AKARI_9MU);
    *fnu_12 = mono9 * bandFluxRatio;

    /* compute (complementary) fnu_18 in the same manner:*/
    bandFluxRatio = alxBlackBodyFluxRatio(Teff, (mcsDOUBLE)AKARI_18MU, Teff, (mcsDOUBLE)AKARI_9MU);
    mono18 = mono9 * bandFluxRatio;

    /* for coherence of data, use the correction factor to give an estimate of the akari band flux,
     * not the monochromatic flux: */
    dataSup = akariTable->coeff[lineSup][alx18mu];
    dataInf = akariTable->coeff[lineInf][alx18mu];
    correctionFactor = dataInf + ratio * (dataSup - dataInf);
    *fnu_18 = mono18 * correctionFactor;

    return mcsSUCCESS;
}

static alxTEFFLOGG_TABLE* alxGetTeffLoggTable()
{
    logTrace("alxGetTeffLoggTable()");

    /* Existing ColorTables */
    static alxTEFFLOGG_TABLE teffloggTable = {mcsFALSE, "alxTableTeffLogg.cfg"};

    /*
     * Check if the structure in which polynomial coefficients will be stored is
     * loaded into memory. If not load it.
     */
    if (teffloggTable.loaded == mcsTRUE)
    {
        return &teffloggTable;
    }

    /* Find the location of the file */
    char* fileName = miscLocateFile(teffloggTable.fileName);
    if (fileName == NULL)
    {
        return NULL;
    }
    
    /* Load file (skipping comment lines starting with '#') */
    miscDYN_BUF dynBuf;
    miscDynBufInit(&dynBuf);
    
    logDebug("Loading %s ...", fileName);
    
    if (miscDynBufLoadFile(&dynBuf, fileName, "#") == mcsFAILURE)
    {
        miscDynBufDestroy(&dynBuf);
        free(fileName);
        return NULL;
    }

    /* For each line of the loaded file */
    mcsINT32 lineNum = 0;
    const char *pos = NULL;
    mcsSTRING1024 line;
    while ((pos = miscDynBufGetNextLine(&dynBuf, pos, line, sizeof(line), mcsTRUE)) != NULL)
    {
        logTrace("miscDynBufGetNextLine() = '%s'", line);

        /* Trim line for any leading and trailing blank characters */
        miscTrimString (line, " ");

        /* If line is not empty */
        if (strlen(line) != 0)
        {
            /* Check if there are to many lines in file */
            if (lineNum >= alxNB_SPECTRAL_TYPES_FOR_TEFF)
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_TOO_MANY_LINES, fileName);
                free(fileName);
                return NULL;
            }

            /* Try to read each polynomial coefficients */
            mcsDOUBLE unused;
            mcsINT32 nbOfReadTokens = sscanf(line, "%c%lf %lf %lf %lf %lf %lf %lf %lf",   
					     &teffloggTable.spectralType[lineNum].code,
					     &teffloggTable.spectralType[lineNum].quantity,
					     &unused,
					     &teffloggTable.teff[lineNum][alxDWARF],
					     &teffloggTable.logg[lineNum][alxDWARF],
					     &teffloggTable.teff[lineNum][alxGIANT],
					     &teffloggTable.logg[lineNum][alxGIANT],
					     &teffloggTable.teff[lineNum][alxSUPER_GIANT],
					     &teffloggTable.logg[lineNum][alxSUPER_GIANT]);

            /* If parsing went wrong */
            if (nbOfReadTokens != (2*alxNB_LUMINOSITY_CLASSES+ 3))
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_WRONG_FILE_FORMAT, line, fileName);
                free(fileName);
                return NULL;
            }
            
            /* Next line */
            lineNum++;
        }
    }

    /* Set the total number of lines in the tefflogg table */
    teffloggTable.nbLines = lineNum;

    /* Mark the tefflogg table as "loaded" */
    teffloggTable.loaded = mcsTRUE;

    /* Destroy the temporary dynamic buffer used to parse the tefflogg table file */
    miscDynBufDestroy(&dynBuf);
    free(fileName);

    /* Return a pointer on the freshly loaded tefflogg table */
    return &teffloggTable;
}

static mcsINT32 alxGetLineForTeffLogg(alxTEFFLOGG_TABLE *teffloggTable,
                                      alxSPECTRAL_TYPE  *spectralType)
{
    logTrace("alxGetLineForTeffLogg()");

    mcsLOGICAL codeFound = mcsFALSE; 
    mcsLOGICAL found = mcsFALSE; 
    mcsINT32 line = 0;
    while ((found == mcsFALSE) && (line < teffloggTable->nbLines))
    {
        /* If the spectral type code match */
        if (teffloggTable->spectralType[line].code ==  spectralType->code)
        {
            /*
             * And quantities match or star quantity is lower than the one of
             * the current line
             */
            if (teffloggTable->spectralType[line].quantity>=spectralType->quantity)
            {
                /* Stop search */
                found = mcsTRUE;
            }
            else /* Else go to the next line */
            {
                line++;         
            }   
            /* the code of the spectral type had been found */
            codeFound = mcsTRUE;
        }
        else /* Spectral type code doesn't match */
        {
            /*
             * If the lines corresponding to the star spectral type code have
             * been scanned
             */
            if (codeFound == mcsTRUE)
            {
                /* Stop search */
                found = mcsTRUE;
            }
            else /* Else go to the next line */
            {
                line++;
            }
        }
    }

    /*
     * Check if spectral type is out of the table; i.e. before the first entry
     * in the tefflogg table. The quantity is strictly lower than the first entry
     * of the table 
     */
    if (line >= teffloggTable->nbLines)
    {
        found = mcsFALSE;
    }

    if ((line == 0) && 
        (teffloggTable->spectralType[line].quantity != spectralType->quantity))
    {
        found = mcsFALSE;
    }

    /* If spectral type not found in tefflogg table, return error */
    if (found != mcsTRUE)
    {
        /* errAdd(alxERR_SPECTRAL_TYPE_NOT_FOUND, spectralType->code); */
        return -1;
    }

    /* else return the line found */
    return line;
}

mcsCOMPL_STAT alxRetrieveTeffAndLoggFromSptype(mcsSTRING32  spType,
                                               mcsDOUBLE   *Teff,
                                               mcsDOUBLE   *LogG)
{
    logTrace("alxRetrieveTeffAndLoggFromSptype()");

    alxSPECTRAL_TYPE *spectralType = malloc(sizeof(alxSPECTRAL_TYPE));
    if (spType != NULL)
    {
        /* 
         * Get each part of the spectral type XN.NLLL where X is a letter, N.N a
         * number between 0 and 9 and LLL is the light class
         */
        if (alxString2SpectralType(spType, spectralType) == mcsFAILURE)
        {
            free(spectralType);
            return mcsFAILURE;
        }
    }
    else
    {
        free(spectralType);
        errAdd(alxERR_NULL_PARAMETER, "spType");
        return mcsFAILURE;
    }

    /* Get the teff,logg table */
    alxTEFFLOGG_TABLE* teffloggTable = alxGetTeffLoggTable(spectralType);
    if (teffloggTable == NULL)
    {
        free(spectralType);
        return mcsFAILURE;
    }
    /* Line corresponding to the spectral type */
    mcsINT32 line = alxGetLineForTeffLogg(teffloggTable, spectralType);
    /* if line not found, i.e = -1, return mcsFAILURE */
    if (line == -1)
    {
        free(spectralType);
        return mcsFAILURE;
    }
    /* interpolate */
    mcsDOUBLE ratio; /* Need to compute ratio */
    mcsINT32 lineSup = line;
    mcsINT32 lineInf = line - 1;

    /* Compute ratio for interpolation */
    mcsDOUBLE sup = teffloggTable->spectralType[lineSup].quantity;
    mcsDOUBLE inf = teffloggTable->spectralType[lineInf].quantity;
    alxSTAR_TYPE lumClass = alxGetLuminosityClass(spectralType);
    mcsDOUBLE subClass = spectralType->quantity;
    if (sup != inf)
    {
        ratio = fabs((subClass - inf) / (sup-inf));
    }
    else
    {
        ratio = 0.5;
    }
    /* logTest("Ratio = %f", ratio); */

    /* Compute Teff */
    mcsDOUBLE dataSup = teffloggTable->teff[lineSup][lumClass];
    mcsDOUBLE dataInf = teffloggTable->teff[lineInf][lumClass];

    *Teff = dataInf + ratio * (dataSup - dataInf);

    dataSup = teffloggTable->logg[lineSup][lumClass];
    dataInf = teffloggTable->logg[lineInf][lumClass];

    /* We add the LogG of the Sun = 4.378 to get LogG in cm s^-2 */
    *LogG = dataInf + ratio * (dataSup - dataInf) + 4.378;

    free(spectralType);
    return mcsSUCCESS;
}

static alxUD_CORRECTION_TABLE* alxGetUDTable()
{
    logTrace("alxGetUDTable()");

    static alxUD_CORRECTION_TABLE udTable = {mcsFALSE, "alxTableUDCoefficientCorrection.cfg"};

    if (udTable.loaded == mcsTRUE)
    {
        return &udTable;
    }

    /* Find the location of the file */
    char* fileName = miscLocateFile(udTable.fileName);
    if (fileName == NULL)
    {
        return NULL;
    }
    
    /* Load file (skipping comment lines starting with '#') */
    miscDYN_BUF dynBuf;
    miscDynBufInit(&dynBuf);
    
    logDebug("Loading %s ...", fileName);
    
    if (miscDynBufLoadFile(&dynBuf, fileName, "#") == mcsFAILURE)
    {
        miscDynBufDestroy(&dynBuf);
        free(fileName);
        return NULL;
    }

    /* For each line of the loaded file */
    mcsINT32 lineNum = 0;
    const char *pos = NULL;
    mcsSTRING1024 line;
    while ((pos = miscDynBufGetNextLine(&dynBuf, pos, line, sizeof(line), mcsTRUE)) != NULL)
    {
        logTrace("miscDynBufGetNextLine() = '%s'", line);

        /* Trim line for any leading and trailing blank characters */
        miscTrimString (line, " ");

        /* If line is not empty */
        if (strlen(line) != 0)
        {
            /* Check if there are to many lines in file */
            if (lineNum >= alxNB_UD_ENTRIES)
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_TOO_MANY_LINES, fileName);
                free(fileName);
                return NULL;
            }

            /* Try to read each polynomial coefficients */
            mcsINT32 nbOfReadTokens = sscanf(line, "%lf %lf %lf %lf %lf %lf %lf %lf %lf %lf %lf %lf",   
                                             &udTable.logg[lineNum],
                                             &udTable.teff[lineNum],
                                             &udTable.coeff[lineNum][alxU],
                                             &udTable.coeff[lineNum][alxB],
                                             &udTable.coeff[lineNum][alxV],
                                             &udTable.coeff[lineNum][alxR],
                                             &udTable.coeff[lineNum][alxI],
                                             &udTable.coeff[lineNum][alxJ],
                                             &udTable.coeff[lineNum][alxH],
                                             &udTable.coeff[lineNum][alxK],
                                             &udTable.coeff[lineNum][alxL],
                                             &udTable.coeff[lineNum][alxN]);

            /* If parsing went wrong */
            if (nbOfReadTokens != (alxNBUD_BANDS+ 2))
            {
                /* Destroy the temporary dynamic buffer, raise an error and return */
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_WRONG_FILE_FORMAT, line, fileName);
                free(fileName);
                return NULL;
            }
            
            /* Next line */
            lineNum++;
        }
    }

    /* Set the total number of lines in the ud table */
    udTable.nbLines = lineNum;

    /* Mark the ud table as "loaded" */
    udTable.loaded = mcsTRUE;

    /* Destroy the temporary dynamic buffer used to parse the ud table file */
    miscDynBufDestroy(&dynBuf);
    free(fileName);
    
    /* Return a pointer on the freshly loaded  ud table */
    return &udTable;
}

static mcsINT32 alxGetLineForUd(alxUD_CORRECTION_TABLE *udTable,
                                mcsDOUBLE               teff, 
                                mcsDOUBLE               logg)
{
    logTrace("alxGetLineForUd()");

    mcsINT32 line = 0;
    mcsDOUBLE *distToUd = malloc(alxNB_UD_ENTRIES * sizeof(mcsDOUBLE));
    
    distToUd[0] = sqrt(pow(teff-udTable->teff[0], 2.0) + pow(logg-udTable->logg[0], 2.0));
    
    int i;
    for (i = 1; i< udTable->nbLines; i++)
    {
        distToUd[i]=sqrt(pow(teff-udTable->teff[i],2.0)+pow(logg-udTable->logg[i],2.0));
        if (distToUd[i] < distToUd[line])
        {
            line = i;
        }
    }

    free(distToUd);
    /* return the line found */
    return line;
}

/**
 * Compute uniform diameters from limb-darkened diameter and spectral type.
 *
 * @param ld limb-darkened diameter (milli arcseconds)
 * @param sp spectral type
 * @param ud output uniform diameters (milli arcseconds)
 *
 * @warning ud will be flushed on each call.
 * @sa alxFlushUNIFORM_DIAMETERS()
 *
 * @todo Handle executon failures.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT alxGetUDFromLDAndSP(const mcsDOUBLE       ld,
                                  const mcsDOUBLE       teff,
                                  const mcsDOUBLE       logg,
                                  alxUNIFORM_DIAMETERS *ud)
{
    logTrace("alxGetUDFromLDAndSP()");

    if (ud == NULL)
    {
        errAdd(alxERR_NULL_PARAMETER, "ud");
        return mcsFAILURE;
    }

    /* Flush output structure before use */
    if (alxFlushUNIFORM_DIAMETERS(ud) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    alxUD_CORRECTION_TABLE* udTable = alxGetUDTable();
    if (udTable == NULL)
    {
        return mcsFAILURE;
    }

    ud->Teff = teff;
    ud->LogG = logg;

    mcsDOUBLE rho, value; 
    mcsINT32 line = alxGetLineForUd(udTable, teff, logg);

    value = udTable->coeff[line][alxU];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->u = ld / rho;

    value = udTable->coeff[line][alxB];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->b = ld / rho;

    value = udTable->coeff[line][alxV];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->v = ld / rho;

    value = udTable->coeff[line][alxR];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->r = ld / rho;

    value = udTable->coeff[line][alxI];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->i = ld / rho;

    value = udTable->coeff[line][alxJ];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->j = ld / rho;

    value = udTable->coeff[line][alxH];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->h = ld / rho;

    value = udTable->coeff[line][alxK];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->k = ld / rho;

    value = udTable->coeff[line][alxL];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->l = ld / rho;

    value = udTable->coeff[line][alxN];
    rho = sqrt((1.0 - value / 3.0) / (1.0 - 7 * value / 15.0));
    ud->n = ld / rho;

    /* Print results */
    logTest("Computed UD: U = %lf, B = %lf, V = %lf, "
            "R = %lf, I = %lf, J = %lf, H = %lf, "
            "K = %lf, L = %lf, N = %lf", 
            ud->u, ud->b, ud->v,
            ud->r, ud->i, ud->j,
            ud->h, ud->k, ud->l, ud->n);

    return mcsSUCCESS;
}

/**
 * Initialize this code
 * @return void
 */
mcsCOMPL_STAT alxCorrectedMagnitudeInit(void)
{
    alxGetExtinctionRatioTable();

    alxSPECTRAL_TYPE *spectralType = malloc(sizeof(alxSPECTRAL_TYPE));

    strcpy(spectralType->luminosityClass, "VIII"); /* alxDWARF */
    alxGetColorTableForStar(spectralType, mcsTRUE);
    alxGetColorTableForStar(spectralType, mcsFALSE);

    strcpy(spectralType->luminosityClass, "IV/III"); /* alxGIANT */
    alxGetColorTableForStar(spectralType, mcsTRUE);
    alxGetColorTableForStar(spectralType, mcsFALSE);
    
    strcpy(spectralType->luminosityClass, "I"); /* alxSUPER_GIANT */
    alxGetColorTableForStar(spectralType, mcsTRUE);
    alxGetColorTableForStar(spectralType, mcsFALSE);
    
    free(spectralType);
    
    alxLoadAkariTable();
    
    alxGetTeffLoggTable();
    
    alxGetUDTable();
    
    return mcsSUCCESS;
}


/*___oOo___*/
