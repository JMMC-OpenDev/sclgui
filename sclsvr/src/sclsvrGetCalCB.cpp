/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrGetCalCB.cpp,v 1.20 2005-11-15 15:01:19 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.19  2005/10/26 11:27:24  lafrasse
 * Code review
 *
 * Revision 1.18  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 * Revision 1.17  2005/03/06 20:31:13  gzins
 * Updated sclsvrCALIBRATOR_LIST::Save() API
 *
 * Revision 1.16  2005/03/04 15:38:10  gzins
 * Added timer log
 *
 * Revision 1.15  2005/03/04 09:47:17  gzins
 * Implemented -file option; i.e. save calibrator list in file if requested
 *
 * Revision 1.14  2005/02/16 16:56:30  gzins
 * Fixed wrong parameter name in documentation
 *
 * Revision 1.13  2005/02/14 15:10:40  scetre
 * changed m..RangeMag to m..MagRange
 *
 * Revision 1.12  2005/02/14 14:13:41  scetre
 * minor changed
 *
 * Revision 1.11  2005/02/13 16:08:09  gzins
 * Changed Min/MaxDeltaMag to Min/MaxRangeMag
 *
 * Revision 1.10  2005/02/08 20:55:50  gzins
 * Removed display of resulting star list
 *
 * Revision 1.9  2005/02/08 04:39:32  gzins
 * Updated for new vobsREQUEST API and used new sclsvrREQUEST class
 *
 * Revision 1.8  2005/02/07 15:01:11  gzins
 * Reformated file header
 *
 * Revision 1.7  2005/02/07 14:44:03  gzins
 * Renamed lambda to wlen.
 * Renamed minMagRange to minRangeMag, and maxMagRange to maxRangeMag.
 *
 * Revision 1.6  2005/02/07 09:24:42  gzins
 * Replaced vobsVIRTUAL_OBSERVATORY local instance by the class member
 *
 * Revision 1.5  2005/02/04 15:29:54  gzins
 * Removed unused printf
 *
 * gzins     23-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * @file
 * sclsvrGetCalCB class definition.
 */

static char *rcsId="@(#) $Id: sclsvrGetCalCB.cpp,v 1.20 2005-11-15 15:01:19 scetre Exp $"; 
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
#include "timlog.h"


/*
 * SCALIB Headers 
 */
#include "vobs.h"


/*
 * Local Headers 
 */
#include "sclsvrSERVER.h"
#include "sclsvrPrivate.h"
#include "sclsvrCALIBRATOR_LIST.h"
#include "sclsvrSCENARIO_BRIGHT_K.h"
#include "sclsvrSCENARIO_BRIGHT_V.h"
#include "sclsvrSCENARIO_BRIGHT_N.h"

/*
 * Public methods
 */
evhCB_COMPL_STAT sclsvrSERVER::GetCalCB(msgMESSAGE &msg, void*)
{
    logTrace("sclsvrSERVER::GetCalCB()");

    // Build the request object from the parameters of the command
    sclsvrREQUEST request;
    if (request.Parse(msg.GetBody()) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
 
    // Start timer log
    timlogInfoStart(msg.GetCommand());

    // Build the list of star which will come from the virtual observatory
    vobsSTAR_LIST starList;

    // Get the observed band
    const char *band;
    band = request.GetSearchBand();
    if ((strcmp(band, "I")==0)||
        (strcmp(band, "J")==0)||
        (strcmp(band, "H")==0)||
        (strcmp(band, "K")==0) )
    {
        // Load Scenario K
        if (scenarioBrightK.Init(&request) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Start the research in the virtual observatory
        if (_virtualObservatory.Search(&scenarioBrightK, request,
                                       starList)==mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

    }
    else if (strcmp(band, "V")==0)
    {
        // Load Scenario V
        if (scenarioBrightV.Init(&request) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Start the research in the virtual observatory
        if (_virtualObservatory.Search(&scenarioBrightV, request,
                                       starList)==mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

    }
    else if ((strcmp(band, "N") == 0))
    {
        // Load Scenario N
        if (scenarioBrightN.Init(&request) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Start the research in the virtual observatory
        if (_virtualObservatory.Search(&scenarioBrightN, request,
                                       starList)==mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

    }
    else 
    {
        errAdd(vobsERR_UNKNOWN_BAND, band);
        return evhCB_NO_DELETE | evhCB_FAILURE; 
    }

    // Build the list of calibrator
    sclsvrCALIBRATOR_LIST calibratorList;
   
    // Get the returned star list and create a calibrator list from it
    if (calibratorList.Copy(starList) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Complete the calibrators list
    if (calibratorList.Complete(request) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Pack the list result in a buffer in order to send it
    if (calibratorList.Size() != 0)
    { 
        miscoDYN_BUF dynBuff;
        calibratorList.Pack(&dynBuff);
        
        msg.SetBody(dynBuff.GetBuffer());

        // If a file has been given, store result in this file
        if (strcmp(request.GetFileName(), "") != 0)
        {
            if (calibratorList.Save(request.GetFileName(), 
                                    request) == mcsFAILURE)
            {
                return evhCB_NO_DELETE | evhCB_FAILURE;
            };
        }
    }
    else
    {
        msg.ClearBody();
    }

    // Send reply
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Stop timer log
    timlogStop(msg.GetCommand());
    
    return evhCB_NO_DELETE;
}


/*___oOo___*/
