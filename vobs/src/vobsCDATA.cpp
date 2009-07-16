/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsCDATA.cpp,v 1.36 2009-07-16 13:47:14 lafrasse Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.35  2009/04/15 16:12:56  lafrasse
* Code, log and documentation enhancements.
*
* Revision 1.34  2009/04/15 12:57:12  lafrasse
* Added test on "Seq" parameter name for ID_MAIN ucd to retreive ID_SB9 catalog ID.
*
* Revision 1.33  2008/03/10 07:50:22  lafrasse
* Minor modifications on comments and log traces.
*
* Revision 1.32  2007/06/27 20:41:47  gzins
* Limited length of logged string causing segmentation fault
*
* Revision 1.31  2007/05/11 09:01:21  gzins
* No longer retrieve J and K magnitudes from DENIS
*
* Revision 1.30  2006/04/10 11:55:09  scetre
* Added tabulation in ucd list on extended format. Update the read of this new file
*
* Revision 1.29  2006/03/22 10:43:24  scetre
* Added test on ID_ALTERNATIVE ucd to retreive TYC2 and TYC3
*
* Revision 1.28  2006/03/03 15:03:27  scetre
* Changed rcsId to rcsId __attribute__ ((unused))
*
* Revision 1.27  2005/12/05 13:07:57  scetre
* Added error on paralax
*
* Revision 1.26  2005/11/24 13:16:43  scetre
* Added test on CODE_VARIB ucd and TYC1 for ID_ALTERNATIVE
*
* Revision 1.25  2005/11/23 08:36:12  scetre
* Added test on ucd and name to retreive properties
*
* Revision 1.24  2005/11/16 10:47:55  scetre
* Updated documentation
*
* Revision 1.23  2005/11/16 10:47:54  scetre
* Updated documentation
*
* Revision 1.22  2005/06/13 10:21:15  scetre
* Added UDDK test
*
* Revision 1.21  2005/06/01 14:16:55  scetre
* Changed logExtDbg to logTrace
*
* Revision 1.20  2005/04/14 14:39:03  scetre
* Updated documentation.
* added test on method return.
*
* Revision 1.19  2005/03/30 12:49:26  scetre
* Updated documentation
*
* Revision 1.18  2005/02/22 15:59:43  gzins
* Updated because of a miscoDYN_BUFF::GetNextLine() API change (again)
*
* Revision 1.17  2005/02/22 15:48:38  lafrasse
* Updated because of a miscoDYN_BUFF::GetNextLine() API change
*
* Revision 1.16  2005/02/17 17:57:27  gzins
* Removed trailing and leading spaces of read values when adding parameter description
*
* Revision 1.15  2005/02/16 16:18:00  scetre
* Fixed remaining bugs in Extract and Unpack methods
*
* Revision 1.14  2005/02/16 15:55:42  gzins
* Fixed bug in AppendLines; missing CR after each line insertion
*
* Revision 1.13  2005/02/16 15:22:11  gzins
* Updated call to miscoDYN_BUF::GetNextLine()
*
* Revision 1.12  2005/02/14 14:11:22  scetre
* replace \n y \0 in LoadBuffer() method
*
* Revision 1.11  2005/02/13 15:29:50  gzins
* Inherited from miscoDYN_BUF
* Added methods to load from file or buffer
* Added methods to store and extract list of stars
*
* Revision 1.10  2005/02/11 10:37:19  gzins
* Added AddParamsDesc() method
*
* Revision 1.9  2005/02/10 10:46:42  gzins
* Changed column name to parameter name
*
* Revision 1.8  2005/02/08 20:59:33  gzins
* Added name of catalog from where data is coming from
* Changed some parameter types from char* to const char*
*
* Revision 1.7  2005/02/07 09:47:08  gzins
* Renamed vobsCDATA method to be compliant with programming standards; method name starts with capital
*
* Revision 1.6  2005/01/31 13:31:38  scetre
* changed misc...Pointer in misc...
*
* Revision 1.5  2005/01/26 08:11:28  scetre
* change history
*
* scetre    15-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * @file
 * vobsCDATA class definition.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: vobsCDATA.cpp,v 1.36 2009-07-16 13:47:14 lafrasse Exp $"; 


/* 
 * System Headers 
 */
#include <iostream>
using namespace std;


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "vobsSTAR.h"
#include "vobsCDATA.h"
#include "vobsErrors.h"
#include "vobsPrivate.h"

/*
 * Class constructor.
 */
vobsCDATA::vobsCDATA()
{
    _nbLines = 0;
    _nbLinesToSkip = 0;
}

/*
 * Class destructor.
 */
vobsCDATA::~vobsCDATA()
{
    // Free all strings containing parameter names
    std::vector<char *>::iterator paramName;
    for (paramName = _paramName.begin(); 
         paramName != _paramName.end(); paramName++)
    {
        free(*paramName);
    }

    // Free all strings containing UCD names
    std::vector<char *>::iterator ucdName;
    for (ucdName = _ucdName.begin(); ucdName != _ucdName.end(); ucdName++)
    {
        free(*ucdName);
    }

    // Free all strings containing UCD names
    _paramName.clear();
    _ucdName.clear();
    _catalogName = "";
}

/*
 * Public methods
 */

/**
 * Set the catalog name from where data is coming.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::SetCatalogName(const char *name)
{
    logTrace("vobsCDATA::SetCatalogName()");

    _catalogName = name;

    return mcsSUCCESS;
}

/**
 * Get the catalog name from where data is coming.
 *
 * @return catalog name.
 */
const char *vobsCDATA::GetCatalogName(void)
{
    logTrace("vobsCDATA::GetCatalogName()");

    return _catalogName.c_str();
}
 
/** 
 * Parse and store each parameters and UCD names.
 *
 * This method parses the two lines containing the description of the parameters
 * contained in the CDATA section. The first line contains the parameter names,
 * while the second contains the corresponding UCD. The number of fields
 * (separated by tab) must be the same for each line.
 *
 * @param paramNameLine line containing names of parameters separated by tab.
 * @param ucdNameLine line containing UCD associated to parameters separated by
 * tab.
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT vobsCDATA::ParseParamsAndUCDsNamesLines(char *paramNameLine, char *ucdNameLine)
{
    logTrace("vobsCDATA::ParseParamsAndUCDsNamesLines()");

    const mcsUINT32 nbMaxParams = 512;
    mcsUINT32 nbOfUcdName;
    mcsSTRING256 ucdNameArray[nbMaxParams];
    mcsUINT32 nbOfParamName;
    mcsSTRING256 paramNameArray[nbMaxParams];

    // Split UCD name line on '\t' character, and store each token
    if (miscSplitString(ucdNameLine, '\t', ucdNameArray, 
                        nbMaxParams, &nbOfUcdName) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Remove each token trailing and leading blanks
    for (mcsUINT32 i = 0; i < nbOfUcdName; i++)
    {
        miscTrimString(ucdNameArray[i], " ");
    }


    // Split parameter name line on '\t' character, and store each token
    if (miscSplitString(paramNameLine, '\t', paramNameArray, 
                        nbMaxParams, &nbOfParamName) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Remove each token trailing and leading blanks
    for (mcsUINT32 i = 0; i < nbOfParamName; i++)
    {
        miscTrimString(paramNameArray[i], " ");
    }
    
    // Check that we found the same number of parameters and UCDs
    if (nbOfUcdName != nbOfParamName)
    {
        errAdd(vobsERR_INCONSISTENT_PARAMS_DESC, nbOfUcdName, nbOfParamName);
        return mcsFAILURE;
    }

    // For each UCD name token stored in the array
    for (mcsUINT32 i=0; i<nbOfUcdName; i++)
    {
        // If both UCD and the param names are not empty
        if ((strcmp(ucdNameArray[i], "") != 0) &&
            (strcmp(paramNameArray[i], "") != 0))
        {
            // Add parameter name and UCD to CDATA structure
            AddUcdName(ucdNameArray[i]);
            AddParamName(paramNameArray[i]);
        }
    }
    logDebug("\t-> Added '%d' parameter names and UCDs to CDATA structure ...",
             nbOfUcdName);

    return mcsSUCCESS;
}

/**
 * Add a parameter name at the end of the internal parameter name list.
 *
 * This method adds the description (parameter name) of a parameter in the
 * CDATA section. The name of the parameters have to be added in the same order
 * they appear in the CDATA section.
 *
 * @param paramName parameter name to be added to the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::AddParamName(const char *paramName)
{
    logTrace("vobsCDATA::AddParamName(%s)", paramName);

    _paramName.push_back(strdup(paramName)); 

    return mcsSUCCESS;
}

/**
 * Add a UCD name at the end of the internal UCD name list.
 *
 * This method adds the description (UCD) of a parameter in the CDATA section.
 * The name of the UCD have to be added in the same order they appear in the
 * CDATA section.
 *
 * @param ucdName corresponding UCD to be added to the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::AddUcdName(const char *ucdName)
{
    logTrace("vobsCDATA::AddUcdName(%s)", ucdName);

    _ucdName.push_back(strdup(ucdName)); 

    return mcsSUCCESS;
}

/**
 * Returns the number of stored parameters.
 *
 * @return The the number of parameters contained in CDATA
 */
mcsUINT32 vobsCDATA::GetNbParams(void) 
{
    return _paramName.size();
}
 
/**
 * Returns pointers on the next parameter and UCD names.
 *
 * This method returns the pointers to the name and the UCD of the next
 * parameter of the CDATA. If \em init is mcsTRUE, it returns the names of the
 * first parameter.
 * 
 * @param paramName pointer to the name of the next parameter.
 * @param ucdName pointer to the corresponding UCD.
 * @param init returns the description of the first parameter if mcsTRUE.
 * 
 * @warning This method call is NOT re-entrant.
 * 
 * @return mcsSUCCESS, or mcsFAILURE if the end of the parameter list is reached.
 */
mcsCOMPL_STAT vobsCDATA::GetNextParamDesc(char **paramName, 
                                          char **ucdName,
                                          mcsLOGICAL init) 
{
    logTrace("vobsCDATA::GetNextParamDesc()");

    if (init == mcsTRUE)
    {
        // Get first element
        _paramNameIterator = _paramName.begin();
        _ucdNameIterator = _ucdName.begin();
    }
    else
    {
        // Get next element
        _paramNameIterator++;
        _ucdNameIterator++;
        if ((_paramNameIterator == _paramName.end()) ||
            (_ucdNameIterator == _ucdName.end()))
        {
            return mcsFAILURE;
        }
    }

    *paramName = *_paramNameIterator;
    *ucdName = *_ucdNameIterator;

    return mcsSUCCESS;
}

/**
 * Set the number of lines to be skipped in CDATA (headers, empty lines).
 *
 * @param nbLines number of lines to be skipped.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::SetNbLinesToSkip(mcsINT32 nbLines)
{
    logTrace("vobsCDATA::SetNbLinesToSkip()");

    _nbLinesToSkip = nbLines;

    return mcsSUCCESS;
}

/**
 * Returns the number of lines to be skipped in CDATA (headers, empty lines).
 *
 * @return The number of lines to be skipped.
 */
mcsUINT32 vobsCDATA::GetNbLinesToSkip(void) 
{
    logTrace("vobsCDATA::GetNbLinesToSkip()");
    
    return _nbLinesToSkip;
}
 
/**
 * Append lines of the given buffer to the CDATA internal buffer.
 *
 * This method appends the lines which are provided in the given buffer into an
 * internal buffer. Each line of the given buffer must be seperated by '\n'.
 * The first lines, defined with SetNbLinesToSkip() method, are skipped. 
 * The GetNbLines() method gives the number of lines currently stored in
 * internal buffer. And they can be retrieved using GetNextLine().
 *
 * @param buffer pointer to the CDATA raw data, which must be null-terminated.
 * @param nbLinesToSkip number of line to skip.
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT vobsCDATA::AppendLines(char *buffer, mcsINT32 nbLinesToSkip)
{
    logTrace("vobsCDATA::AppendLines()");

    // Store buffer into a temporary buffer
    miscoDYN_BUF tmpBuffer;
    if (tmpBuffer.AppendString(buffer) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    logDebug("Buffer to process : %.80s", buffer);

    // Store usefull lines into internal buffer; i.e skip header lines and
    // empty lines
    int nbOfLine = 0;
    const char *from = NULL;
    mcsSTRING1024 line;
    do 
    {
        from = tmpBuffer.GetNextLine(from, line, sizeof(mcsSTRING1024), mcsFALSE);
        nbOfLine++;

        // If a non-empty, non-header, line was found
        if ((from != NULL) && (nbOfLine > (nbLinesToSkip)) &&
            (miscIsSpaceStr(line) == mcsFALSE))
        {
            logDebug("\t-> Add line : %s", line);
            
            if (AppendString(line) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
            if (AppendString("\n") == mcsFAILURE)
            {
                return mcsFAILURE;
            }
            _nbLines++;
        }
        else
        {
            logDebug("\t-> Skip line : %s", line);
        }
    }
    while (from != NULL);
 
    return mcsSUCCESS;
}

/**
 * Returns the number of lines currently stored in internal buffer.
 *
 * @return The number of lines stored in the internal buffer.
 */
mcsUINT32 vobsCDATA::GetNbLines(void) 
{
    return _nbLines;
}

/**
 * Load raw CDATA from file.
 *
 * @param fileName the file to load.
 * 
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT vobsCDATA::LoadFile(const char *fileName)
{
    logTrace("vobsCDATA::LoadFile()");
    
    // Use miscoDYN_BUF method to load file into the dynBuf of the class
    if (miscoDYN_BUF::LoadFile(fileName, "#") == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Parse header of the CDATA and build the list of UCD and parameter names
    if (LoadParamsAndUCDsNamesLines() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Load raw CDATA from a given buffer.
 *
 * @param buffer the buffer from which the raw data will be loaded
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT vobsCDATA::LoadBuffer(const char *buffer)
{
    logTrace("vobsCDATA::LoadBuffer()");

    // Get the content of the buffer and copy it in the internal buffer
    if (AppendString(buffer) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    SetCommentPattern("#");

    // Parse header of the CDATA and build the list of UCD and parameter names
    if (LoadParamsAndUCDsNamesLines() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Find and parse the parameter and UCD names.
 *
 * The two first lines of the buffer contain the parameter description. i.e. the
 * UCDs and the parameter names lists. This method retreives these two lines and
 * set the parameter description using the ParseParamsAndUCDsNamesLines() method. It also sets
 * the number of line to be skipped to 2.
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT vobsCDATA::LoadParamsAndUCDsNamesLines(void)
{
    logTrace("vobsCDATA::LoadParamsAndUCDsNamesLines()");

    const char* from = NULL;

    // Get a pointer to the UCD name line
    mcsSTRING2048 ucdNameLine;
    mcsUINT32     ucdNameLineMaxLength = sizeof(ucdNameLine);
    from = GetNextLine(from, ucdNameLine, ucdNameLineMaxLength);
    if (ucdNameLine == NULL)
    {
        errAdd(vobsERR_MISSING_UCDS);
        return mcsFAILURE;
    }

    // Get a pointer to the parameter name line
    mcsSTRING2048 paramNameLine;
    from = GetNextLine(from, paramNameLine, ucdNameLineMaxLength);
    if (paramNameLine == NULL)
    {
        errAdd(vobsERR_MISSING_PARAM_NAMES);
        return mcsFAILURE;
    }
    
    // Retrieve each parameter and UCD names.
    if (ParseParamsAndUCDsNamesLines(paramNameLine, ucdNameLine) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Set the number of lines to skip to 2 : the 2 lines containing parameter description.
    SetNbLinesToSkip(2);

    return mcsSUCCESS;
}

/**
 * Get the property ID corresponding to the given parameter name and UCD.
 *
 * This method returns the property ID corresponding to the parameter name and
 * UCD, or NULL if they do not correspond to an existing object property.
 *
 * @param paramName parameter name
 * @param ucdName UCD name
 *
 * @return the found property ID, or NULL id none corresponded.
 */
char *vobsCDATA::GetPropertyId(const char *paramName, const char *ucdName)
{
    // object identifiers 
    if (strcmp(ucdName, "ID_ALTERNATIVE") == 0)
    {
        if (strcmp(paramName, "HD") == 0)
        {
            return vobsSTAR_ID_HD;
        }
        else if (strcmp(paramName, "HIP") == 0)
        {
            return vobsSTAR_ID_HIP;
        }
        else if (strcmp(paramName, "DM") == 0)
        {
            return vobsSTAR_ID_DM;
        }
        else if (strcmp(paramName, "TYC1") == 0)
        {
            return vobsSTAR_ID_TYC1;
        }
        else if (strcmp(paramName, "TYC2") == 0)
        {
            return vobsSTAR_ID_TYC2;
        }
        else if (strcmp(paramName, "TYC3") == 0)
        {
            return vobsSTAR_ID_TYC3;
        }
    }

    // Object identifier
    if (strcmp(ucdName, "ID_MAIN") == 0)
    {
        if (strcmp(paramName, "DENIS") == 0)
        {
            return vobsSTAR_ID_DENIS;
        }
        else if (strcmp(paramName, "2MASS") == 0)
        {
            return vobsSTAR_ID_2MASS;
        }
        else if (strcmp(paramName, "Seq") == 0) // SBC9 catalog ID
        {
            return vobsSTAR_ID_SB9;
        }
    }

    // Flag of variability
    if (strcmp(ucdName, "CODE_VARIAB") == 0)
    {
        if (strcmp(paramName, "v1") == 0) // ASCC catalog
        {
            return vobsSTAR_CODE_VARIAB_V1;
        }
        else if (strcmp(paramName, "v2") == 0) // ASCC catalog
        {
            return vobsSTAR_CODE_VARIAB_V2;
        }
        else if (strcmp(paramName, "Var") == 0) // MIDI catalog
        {
            return vobsSTAR_CODE_VARIAB;
        }
    }
    
    // Code misc
    if (strcmp(ucdName, "CODE_MISC") == 0)
    {
        if (strcmp(paramName, "Iflg") == 0)
        {
            return vobsSTAR_CODE_MISC_I;
        }
        else if (strcmp(paramName, "Jflg") == 0)
        {
            return vobsSTAR_CODE_MISC_J;
        }
        else if (strcmp(paramName, "Kflg") == 0)
        {
            return vobsSTAR_CODE_MISC_K;
        }
    }
    
    // Diameters
    if (strcmp(ucdName, "EXTENSION_DIAM") == 0)
    {
        if (strcmp(paramName, "LD") == 0)
        {
            return vobsSTAR_LD_DIAM;
        }
        else if (strcmp(paramName, "UD") == 0)
        {
            return vobsSTAR_UD_DIAM;
        }
        else if ((strcmp(paramName, "UDDK") == 0) ||
                 (strcmp(paramName, "UDdiamKs") == 0))
        {
            return vobsSTAR_UDDK_DIAM;
        }
    }

    // Diameter errors
    if (strcmp(ucdName, "ERROR") == 0)
    {
        if (strcmp(paramName, "e_LD") == 0)
        {
            return vobsSTAR_LD_DIAM_ERROR;
        }
        else if (strcmp(paramName, "e_UD") == 0)
        {
            return vobsSTAR_UD_DIAM_ERROR;
        }
        else if ((strcmp(paramName, "e_UDDK") == 0) ||
                 (strcmp(paramName, "e_UDdiam") == 0))
        {
            return vobsSTAR_UDDK_DIAM_ERROR;
        }
        else if ((strcmp(paramName, "e_Plx") == 0))
        {
            return vobsSTAR_POS_PARLX_TRIG_ERROR;
        }
    }

    // No property corresponding to the parameter name/UCD
    return NULL;
}

/*___oOo___*/
