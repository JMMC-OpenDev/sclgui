/*******************************************************************************
 * JMMC project
 * 
 * "@(#) $Id: alxAngularDiameter.c,v 1.9 2005-02-22 16:51:58 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2005/02/22 14:05:13  scetre
 * Patch diam error : use 0.1*d_v_k insted of mean 0.1*diam
 *
 * Revision 1.7  2005/02/16 15:10:57  gzins
 * Updated call to miscDynBufGetNextLine()
 *
 * Revision 1.6  2005/02/12 15:13:11  gzins
 * Removed call to miscResolvePath; done by miscLocateFile
 *
 * Revision 1.5  2005/02/10 07:55:31  gzins
 * Updated alxComputeAngularDiameter to return diameter from B-V, V-R and V-K calibration
 *
 * Revision 1.4  2005/02/04 13:50:39  gzins
 * Changed alxCONFIDENCE_VERY_LOW to alxCONFIDENCE_LOW
 *
 * Revision 1.3  2005/01/31 13:32:37  scetre
 * changed misc...Pointer in misc...
 *
 * Revision 1.2  2005/01/26 15:49:09  scetre
 * solve memory problem with miscDYN_BUF
 *
 * Revision 1.1  2005/01/21 08:14:25  gluck
 * Creation
 *
 * 
 * scetre    11-Oct-2004  Created
 * gzins     12-Jan-2005  - Updated to be compliant with programming standards
 *                        - Improved reliability and error handling
 *                        - Add new confidence index computation
 *
 ******************************************************************************/

/**
 * \file
 * Function definition for angular diameter computation.
 *
 * \sa JMMC-MEM-2600-0009 document.
 */

static char *rcsId="@(#) $Id: alxAngularDiameter.c,v 1.9 2005-02-22 16:51:58 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
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
static alxPOLYNOMIAL_ANGULAR_DIAMETER *alxGetPolynamialForAngularDiameter(void);


/* 
 * Local functions definition
 */

/**
 * Return the polynomial coefficients for angular diameter computation 
 *
 * \return pointer to structure containing polynomial coefficients or NULL if
 * an error occured.
 *
 * \usedfiles alxAngDiamPolynomialForBrightStar.cfg : file containing the
 * polynomial coefficients to compute the angular diameter for bright star. The
 * polynomial coefficients are given for B-V, V-R and V-K.
 */
static alxPOLYNOMIAL_ANGULAR_DIAMETER *alxGetPolynamialForAngularDiameter(void)
{
    logExtDbg("alxGetPolynamialForAngularDiameter()");

    /*
     * Check if the structure polynomial, where will be stored polynomial
     * coefficients to compute angular diameter, is loaded into memory. If not
     * loaded it.
     */
    static alxPOLYNOMIAL_ANGULAR_DIAMETER polynomial = 
        {mcsFALSE, "alxAngDiamPolynomialForBrightStar.cfg"};
    if (polynomial.loaded == mcsTRUE)
    {
        return (&polynomial);
    }

    /* 
     * Build the dynamic buffer which will contain the file of coefficient 
     * of angular diameter
     */
    /* Find the location of the file */
    char *fileName;
    fileName = miscLocateFile(polynomial.fileName);
    if (fileName == NULL)
    {
        return NULL;
    }

    /* Load file where comment lines started with '#' */
    miscDYN_BUF dynBuf;
    miscDynBufInit(&dynBuf);
    if (miscDynBufLoadFile(&dynBuf, fileName, "#") == mcsFAILURE)
    {
        miscDynBufDestroy(&dynBuf);
        return NULL;
    }

    /* For each line */
    int  lineNum = 0;
    const char *pos = NULL;
    mcsSTRING1024 line;
    while ((pos = miscDynBufGetNextLine
            (&dynBuf, pos, line, sizeof(mcsSTRING1024), mcsTRUE)) != NULL)
    {
        logTest("miscDynBufGetNextLine() = '%s'", line);

        /* If line is not empty */
        if (miscIsSpaceStr(line) == mcsFALSE)
        {
            /* Check if there is to many lines in file */
            if (lineNum >= alxNB_COLOR_INDEXES)
            {
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_TOO_MANY_LINES, fileName);
                return NULL;
            }

            /* Get polynomial coefficients */
            if (sscanf(line, "%*s %f %f %f %f %f %f",   
                       &polynomial.coeff[lineNum][0],
                       &polynomial.coeff[lineNum][1],
                       &polynomial.coeff[lineNum][2],
                       &polynomial.coeff[lineNum][3],
                       &polynomial.coeff[lineNum][4],
                       &polynomial.coeff[lineNum][5]) !=
                alxNB_POLYNOMIAL_COEFF_DIAMETER)
            {
                miscDynBufDestroy(&dynBuf);
                errAdd(alxERR_WRONG_FILE_FORMAT, line, fileName);
                return NULL;
            }

            /* Next line */
            lineNum++;
        }
    }
    /* Destroy the dynamic buffr where is stored the file information */
    miscDynBufDestroy(&dynBuf);

    /* Check if there is missing line */
    if (lineNum != alxNB_COLOR_INDEXES)
    {
        errAdd(alxERR_MISSING_LINE, lineNum, alxNB_COLOR_INDEXES, fileName);
        miscDynBufDestroy(&dynBuf);
        return NULL;
    }

    polynomial.loaded = mcsTRUE;

    return (&polynomial);
}


/*
 * Public functions definition
 */

/**
 * Compute the angular diameter.
 *
 * Compute the star angular diameter from its the photometric properties.
 *
 * \param mgB magnitude in band B
 * \param mgV magnitude in band V
 * \param mgR magnitude in band R
 * \param mgK magnitude in band K
 * \param diamBv diameter from (V, (B-V)) calibration 
 * \param diamVr diameter from (V, (V-R)) calibration 
 * \param diamVk diameter from (V, (V-K)) calibration 
 * \param diamError computed diameter error
 * \param confidenceIdx confidence index 
 *  
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 *
 * \todo affect correct value to confidence index.
 */
mcsCOMPL_STAT alxComputeAngularDiameter(mcsFLOAT mgB,
                                        mcsFLOAT mgV,
                                        mcsFLOAT mgR,
                                        mcsFLOAT mgK,
                                        mcsFLOAT *diamBv,
                                        mcsFLOAT *diamVr,
                                        mcsFLOAT *diamVk,
                                        mcsFLOAT *diamBvErr,
                                        mcsFLOAT *diamVrErr,
                                        mcsFLOAT *diamVkErr,
                                        alxCONFIDENCE_INDEX *confidenceIdx)
{
    logExtDbg("alxComputeAngularDiameter()");

    /* Get polynamial for diameter computation */
    alxPOLYNOMIAL_ANGULAR_DIAMETER *polynomial;
    polynomial = alxGetPolynamialForAngularDiameter();
    if (polynomial == NULL)
    {
        return mcsFAILURE;        
    }

    /* Compute B-V, V-R, V-K */
    mcsFLOAT b_v = mgB - mgV;
    mcsFLOAT v_r = mgV - mgR;
    mcsFLOAT v_k = mgV - mgK;

    /* Declare polynomials P(B-V), P(V-R), P(V-K) */
    mcsFLOAT p_b_v, p_v_r, p_v_k;

    /* 
     * Declare the 3 diameters D(B-V), D(V-R), D(V-K),
     * the mean diameter
     * and the mean delta
     */
    mcsFLOAT meanDiam;
    mcsFLOAT meanDiamErr;

    /* Compute the polynomials P(B-V), P(V-R), P(V-K) */
    p_b_v = polynomial->coeff[0][0]
        + polynomial->coeff[0][1] * b_v
        + polynomial->coeff[0][2] * pow(b_v, 2)
        + polynomial->coeff[0][3] * pow(b_v, 3)
        + polynomial->coeff[0][4] * pow(b_v, 4)
        + polynomial->coeff[0][5] * pow(b_v, 5);

    p_v_r = polynomial->coeff[1][0]
        + polynomial->coeff[1][1] * v_r
        + polynomial->coeff[1][2] * pow(v_r, 2)
        + polynomial->coeff[1][3] * pow(v_r, 3)
        + polynomial->coeff[1][4] * pow(v_r, 4)
        + polynomial->coeff[1][5] * pow(v_r, 5);

    p_v_k = polynomial->coeff[2][0]
        + polynomial->coeff[2][1] * v_k
        + polynomial->coeff[2][2] * pow(v_k, 2)
        + polynomial->coeff[2][3] * pow(v_k, 3)
        + polynomial->coeff[2][4] * pow(v_k, 4)
        + polynomial->coeff[2][5] * pow(v_k, 5);

    /* Compute the diameters D(B-V), D(V-R), D(V-K) */
    *diamBv = 9.306 * pow(10, -0.2 * mgV) * p_b_v;
    *diamVr = 9.306 * pow(10, -0.2 * mgV) * p_v_r;
    *diamVk = 9.306 * pow(10, -0.2 * mgV) * p_v_k;
    *diamBvErr = *diamBv * 8.0/100.0;
    *diamVrErr = *diamVr * 9.7/100.0;;
    *diamVkErr = *diamVk * 6.9/100.0;;

    /* Compute mean diameter and its associated error */
    meanDiam = (*diamVk + *diamVr + *diamBv) / 3;
    meanDiamErr = 0.1 * meanDiam;

    /* Check whether the diameter is coherent or not */
    if ((fabs(*diamBv - meanDiam) > 2.0 * meanDiamErr) ||
        (fabs(*diamVr - meanDiam) > 2.0 * meanDiamErr) ||
        (fabs(*diamVk - meanDiam) > 2.0 * meanDiamErr) )
    {
        *confidenceIdx = alxCONFIDENCE_LOW;
    }
    else
    {
        *confidenceIdx =  alxCONFIDENCE_HIGH;
    }

    logTest("Diameter BV = %.3f(%.4f), VR = %.3f(%.4f), VK = %.3f(%.4f)", 
            *diamBv, *diamBvErr, *diamVr, *diamVrErr, *diamVk, *diamVkErr);
    logTest("Confidence index       = %s", 
            (*confidenceIdx == alxCONFIDENCE_LOW) ? "LOW" : "HIGH");

    return mcsSUCCESS;
}
/*___oOo___*/
