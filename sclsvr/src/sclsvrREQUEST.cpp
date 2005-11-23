/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrREQUEST.cpp,v 1.13 2005-11-23 17:31:59 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2005/11/23 14:35:33  lafrasse
 * Added fileName proper management (strncpy() calls instead of strcpy())
 * Removed unused 'MaxReturn' command parmater
 * Added 'bright' command parameter
 *
 * Revision 1.11  2005/11/15 15:01:19  scetre
 * Updated with new scenario structure
 *
 * Revision 1.10  2005/10/26 11:27:24  lafrasse
 * Code review
 *
 * Revision 1.9  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 * Revision 1.8  2005/03/07 16:06:06  gzins
 * Removed automatic sort on visibility
 *
 * Revision 1.7  2005/03/07 13:40:49  gzins
 * Remove min baseline length
 * Defined magnitude range option as optional (not used in N band)
 *
 * Revision 1.6  2005/03/04 09:46:04  gzins
 * Rename Set/GetSaveFileName to  Set/GetFileName
 *
 * Revision 1.5  2005/02/28 13:48:56  scetre
 * Added save file option in the request
 *
 * Revision 1.4  2005/02/17 15:33:18  gzins
 * Added GetCmdParamLine method
 * Used _getCalCmd member
 *
 * Revision 1.3  2005/02/16 16:56:30  gzins
 * Fixed wrong parameter name in documentation
 *
 * Revision 1.2  2005/02/15 15:54:00  gzins
 * Changed remaining SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.1  2005/02/07 14:43:41  gzins
 * Created
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclsvrREQUEST class.
 */

static char *rcsId="@(#) $Id: sclsvrREQUEST.cpp,v 1.13 2005-11-23 17:31:59 lafrasse Exp $"; 
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


/*
 * Local Headers 
 */
#include "sclsvrREQUEST.h"
#include "sclsvrPrivate.h"


/**
 * Class constructor
 */
sclsvrREQUEST::sclsvrREQUEST()
{
    _maxBaselineLength = 0.0;
    _observingWlen     = 0.0;
    _getCalCmd         = NULL;
    _brightFlag        = mcsTRUE;
    memset(_fileName, '\0', sizeof(_fileName));
}

/**
 * Class destructor
 */
sclsvrREQUEST::~sclsvrREQUEST()
{
    if (_getCalCmd != NULL)
    {
        delete(_getCalCmd);
    }
}

/**
 * Copy from another request
 *
 * @param request request to copy
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclsvrREQUEST::Copy(sclsvrREQUEST& request)
{
    logTrace("sclsvrREQUEST::Copy()");
   
    vobsREQUEST::Copy(request);
    
    _maxBaselineLength = request._maxBaselineLength;
    _observingWlen     = request._observingWlen;
    _getCalCmd         = request._getCalCmd;
    _brightFlag        = request._brightFlag;
    strncpy(_fileName, request._fileName, sizeof(_fileName));

    return mcsSUCCESS;
}

/*
 * Public methods
 */
/**
 * Parses the GETCAL command parameters.
 *
 * It retrieves the GETCAL command parameter values and set its corresponding
 * members according.
 *
 * @param cmdParamLine GETCAL command parameters
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclsvrREQUEST::Parse(const char *cmdParamLine)
{
    logTrace("sclsvrREQUEST::Parse()");

    // GETCAL command
    if (_getCalCmd != NULL)
    {
        delete (_getCalCmd);
    }
    _getCalCmd = new sclsvrGETCAL_CMD(sclsvrGETCAL_CMD_NAME, cmdParamLine);
    
    // Parse command
    if (_getCalCmd->Parse() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Object name
    char *objectName;
    if (_getCalCmd->GetObjectName(&objectName) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Observed magnitude
    mcsDOUBLE magnitude;
    if (_getCalCmd->GetMag(&magnitude) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Diff ra
    mcsINT32 diffRa;
    if (_getCalCmd->GetDiffRa(&diffRa) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Diff dec
    mcsINT32 diffDec;
    if (_getCalCmd->GetDiffDec(&diffDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Band
    char *band;
    if (_getCalCmd->GetBand(&band) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Magnitude is not used for N band
    mcsDOUBLE minRangeMag;
    mcsDOUBLE maxRangeMag;
    if (strcmp(band, "N") != 0)
    {
        // MinRangeMag
        if (_getCalCmd->GetMinMagRange(&minRangeMag) == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // MaxRangeMag
        if (_getCalCmd->GetMaxMagRange(&maxRangeMag) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Ra
    char *ra;
    if (_getCalCmd->GetRa(&ra) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Dec
    char *dec;
    if (_getCalCmd->GetDec(&dec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // BaseMax
    mcsDOUBLE baseMax;
    if (_getCalCmd->GetBaseMax(&baseMax) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Wlen
    mcsDOUBLE wlen;
    if (_getCalCmd->GetWlen(&wlen) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Brightness
    mcsLOGICAL brightFlag = mcsTRUE;
    if (_getCalCmd->IsDefinedBright() == mcsTRUE)
    {
        if (_getCalCmd->GetBright(&brightFlag) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // File name
    char *fileName = "";
    if (_getCalCmd->IsDefinedFile() == mcsTRUE)
    {
        if (_getCalCmd->GetFile(&fileName) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Build the request object from the parameters of the command
    // Affect the reference object name
    if (SetObjectName(objectName) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the right ascension position
    if (SetObjectRa(ra) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the declinaison position
    if (SetObjectDec(dec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the wavelength
    if (SetObservingWlen(wlen) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the magnitude
    if (SetObjectMag(magnitude) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Magnitude is not used for N band
    if (strcmp(band, "N") != 0)
    {
        // Affect the min of the magitude range
        if (SetMinMagRange(minRangeMag) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
        // Affect the max of the magnitude range
        if (SetMaxMagRange(maxRangeMag) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    // Get the search area size
    if (SetSearchArea(diffRa, diffDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the observed band
    if (SetSearchBand(band) ==  mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the baseline length
    if (SetMaxBaselineLength(baseMax) ==  mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the brightness flag
    if (SetBrightFlag(brightFlag) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Affect the file name
    if (SetFileName(fileName) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Returns the GETCAL command parameter line.
 *
 * @return command parameter line or NULL if it is not initialized 
 */
mcsCOMPL_STAT sclsvrREQUEST::GetCmdParamLine(mcsSTRING256 cmdParamLine)
{
    logTrace("sclsvrREQUEST::GetCmdParamLine()");

    memset(cmdParamLine, '\0', sizeof(mcsSTRING256)); 

    // If no parameter has been given, return NULL 
    if (_getCalCmd == NULL)
    {
        return mcsSUCCESS;
    }

    // Else build the command parameter line and return it
    string str;
    if (_getCalCmd->GetCmdParamLine(str) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    strncpy(cmdParamLine, str.c_str(), sizeof(mcsSTRING256) -1);

    return mcsSUCCESS;
}

/**
 * Set the minimum and maximum lengths (in meter) of baseline.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrREQUEST::SetMaxBaselineLength(mcsFLOAT length)
{
    logTrace("sclsvrREQUEST::SetMaxBaselineLength()");

    _maxBaselineLength = length;
    
    return mcsSUCCESS;
}

/**
 * Get the maximum baseline length.
 *
 * @return maximum baseline length.
 */
mcsFLOAT sclsvrREQUEST::GetMaxBaselineLength(void)
{
    logTrace("sclsvrREQUEST::GetMaxBaselineLength()");

    return _maxBaselineLength;
}

/**
 * Set the observing wavelength (um).
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrREQUEST::SetObservingWlen(mcsFLOAT wlen)
{
    logTrace("sclsvrREQUEST::SetObservingWlen()");

    _observingWlen = wlen;
    
    return mcsSUCCESS;
}

/**
 * Get the observing wavelength.
 *
 * @return observing wavelength.
 */
mcsFLOAT sclsvrREQUEST::GetObservingWlen(void)
{
    logTrace("sclsvrREQUEST::GetObservingWlen()");

    return _observingWlen;
}

/**
 * Specify wether the query should return bright (by default) or faint stars.
 *
 * @param brightFlag mcsTRUE if the query should return bright starts, otherwise
 * mcsFALSE to get faint stars.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrREQUEST::SetBrightFlag(mcsLOGICAL brightFlag)
{
    logTrace("sclsvrREQUEST::SetBrightFlag()");
    
    _brightFlag = brightFlag;

    return mcsSUCCESS;
}

/**
 * Return wether the query should return bright or faint stars.
 *
 * @return mcsTRUE if the query should return bright stars, otherwise mcsFALSE
 * for faint stars.
 */
mcsLOGICAL sclsvrREQUEST::IsBright(void)
{
    logTrace("sclsvrREQUEST::IsBright()");

    return _brightFlag;
}

/**
 * Set the file name in which the value will be saved.
 *
 * @param fileName the name of the save file
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclsvrREQUEST::SetFileName(mcsSTRING256 fileName)
{
    logTrace("sclsvrREQUEST::SetFileName()");
    
    if (strncpy(_fileName, fileName, sizeof(_fileName)) == NULL)
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Get the name of the save file
 *
 * @return name of the save file
 */
const char *sclsvrREQUEST::GetFileName(void)
{
    logTrace("sclsvrREQUEST::GetFileName()");

    return _fileName;
}

/*___oOo___*/
