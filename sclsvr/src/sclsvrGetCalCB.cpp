/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclsvrGetCalCB.cpp,v 1.5 2005-02-04 15:29:54 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* gzins     23-Nov-2004  Created
*
*******************************************************************************/

/**
 * \file
 * sclsvrGetCalCB class definition.
 */

static char *rcsId="@(#) $Id: sclsvrGetCalCB.cpp,v 1.5 2005-02-04 15:29:54 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


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

#include "vobs.h"

/*
 * Local Headers 
 */
#include "sclsvrSERVER.h"
#include "sclsvrGETCAL_CMD.h"
#include "sclsvrPrivate.h"
#include "sclsvrCALIBRATOR_LIST.h"
/*
 * Public methods
 */

evhCB_COMPL_STAT sclsvrSERVER::GetCalCB(msgMESSAGE &msg, void*)
{

    logExtDbg("sclsvrSERVER::GetCalCB()");

    // GETCAL command
    sclsvrGETCAL_CMD getCalCmd(msg.GetCommand(), msg.GetBody());

    
    // Parse command
    if (getCalCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Object name
    char *objName;
    if (getCalCmd.GetObjectName(&objName) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Observed magnitude
    mcsDOUBLE magnitude;
    if (getCalCmd.GetMag(&magnitude) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // max calibrator return
    mcsINT32 maxReturn;
    if (getCalCmd.GetMaxReturn(&maxReturn) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // diff ra
    mcsINT32 diffRa;
    if (getCalCmd.GetDiffRa(&diffRa) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // diff dec
    mcsINT32 diffDec;
    if (getCalCmd.GetDiffDec(&diffDec) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // band
    char *band;
    if (getCalCmd.GetBand(&band) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // minMagRange
    char *minMagRange;
    if (getCalCmd.GetMinMagRange(&minMagRange) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // maxMagRange
    char *maxMagRange;
    if (getCalCmd.GetMaxMagRange(&maxMagRange) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // ra
    char *ra;
    if (getCalCmd.GetRa(&ra) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // dec
    char *dec;
    if (getCalCmd.GetDec(&dec) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // baseMin
    mcsDOUBLE baseMin;
    if (getCalCmd.GetBaseMin(&baseMin) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // baseMax
    mcsDOUBLE baseMax;
    if (getCalCmd.GetBaseMax(&baseMax) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // lambda
    mcsDOUBLE lambda;
    if (getCalCmd.GetLambda(&lambda) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // visibility
    mcsDOUBLE vis;
    if (getCalCmd.GetVis(&vis) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // visibility error
    mcsDOUBLE visErr;
    if (getCalCmd.GetVisErr(&visErr) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Build the request object from the parameters of the command
    vobsREQUEST request;

    // Affect the reference object name
    if (request.SetConstraint(STAR_NAME_ID, objName) 
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the right ascension position
    if (request.SetConstraint(RA_ID, ra) 
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the declinaison position
    if (request.SetConstraint(DEC_ID, dec) 
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the wavelength
    mcsSTRING256 wavelength;
    sprintf(wavelength, "%f", lambda);
    if (request.SetConstraint(STAR_WLEN_ID, wavelength)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the magnitude
    mcsSTRING256 mag;
    sprintf(mag, "%f", magnitude);
    if (request.SetConstraint(STAR_MAGNITUDE_ID, mag)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the min of the magitude range
    if (request.SetConstraint(MIN_MAGNITUDE_RANGE_ID, minMagRange)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the max of the magnitude range
    if (request.SetConstraint(MAX_MAGNITUDE_RANGE_ID, maxMagRange)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the search box ra
    mcsSTRING256 dRa;
    sprintf(dRa, "%d", diffRa);
    if (request.SetConstraint(SEARCH_BOX_RA_ID, dRa)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the serach box dec
    mcsSTRING256 dDec;
    sprintf(dDec, "%d", diffDec);
    if (request.SetConstraint(SEARCH_BOX_DEC_ID,dDec)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the expected visibility
    mcsSTRING256 visi;
    sprintf(visi, "%f", vis);
    if (request.SetConstraint(STAR_EXPECTED_VIS_ID, visi)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the error visibility
    mcsSTRING256 visError;
    sprintf(visError, "%f", visErr);
    if (request.SetConstraint(STAR_MAX_ERR_VIS_ID, visError)
        == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the observed band
    if (request.SetConstraint(OBSERVED_BAND_ID, band) ==  mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Affect the baseMin
    mcsSTRING256 baseMini;
    sprintf(baseMini, "%f", baseMin);
    if (request.SetConstraint(BASEMIN_ID, baseMini)
        ==  mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // Affect the baseMax
    mcsSTRING256 baseMaxi;
    sprintf(baseMaxi, "%f", baseMax);
    if (request.SetConstraint(BASEMAX_ID, baseMaxi)
        ==  mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Build the virtual observatory
    vobsVIRTUAL_OBSERVATORY virtualObservatory;

    // Build the list of star which will come from the virtual observatory
    vobsSTAR_LIST starList;

    // start the research in the virtual observatory
    if (virtualObservatory.Search(request, starList)==mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Build the list of calibrator
    sclsvrCALIBRATOR_LIST calibratorList;
   
    // get the resulting star list and create a calibrator list
    if (calibratorList.Copy(starList) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    // complete the calibrators list
    if (calibratorList.Complete(request) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    calibratorList.Display();
    
    // Pack the list result in a buffer in order to send it
    miscDYN_BUF dynBuff;
    miscDynBufInit(&dynBuff);
    
    calibratorList.Pack(&dynBuff);

    msg.SetBody(miscDynBufGetBuffer(&dynBuff));
   
    calibratorList.Clear();
    starList.Clear();
    // Send reply
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    return evhCB_NO_DELETE;
}






/*___oOo___*/
