/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsTestVOInVBand.cpp,v 1.3 2005-02-15 15:26:06 gzins Exp $"
 *
 * History
 * -------
 * scetre    25-Aug-2004  Created
 *
 ******************************************************************************/

static char *rcsId="@(#) $Id: vobsTestVOInVBand.cpp,v 1.3 2005-02-15 15:26:06 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>

/**
 * \namespace std
 * Export standard iostream objects (cin, cout,...).
 */
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
#include "vobsSTAR_LIST.h"
#include "vobsREQUEST.h"
#include "vobsVIRTUAL_OBSERVATORY.h"
#include "vobsPrivate.h"

/* 
 * Main
 */
int main(int argc, char *argv[])
{
    // Set logging service
    logSetStdoutLogLevel(logTEST);

    logInfo("Starting ...");

    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        errCloseStack(); 
        exit (EXIT_FAILURE);
    }

    // Set request in V band
    vobsREQUEST request;
    request.SetObjectName("ETA TAU");
    request.SetObjectRa("03+47+29.08");
    request.SetObjectDec("24+06+18.5");
    request.SetObjectMag(2.87);
    request.SetSearchBand("V");
    request.SetDeltaRa(1800.0);
    request.SetDeltaDec(300.0);
    request.SetMinRangeMag(1);
    request.SetMaxRangeMag(5);
    request.SetMaxNbOfSelectedObjects(50);
    request.Display();

    // Search in VO
    vobsSTAR_LIST starList;
    vobsVIRTUAL_OBSERVATORY vobs;
    if (vobs.Search(request, starList)==mcsFAILURE)
    {
        errCloseStack();
        exit(EXIT_FAILURE);
    }
    printf(">>>>>>>>> V BAND REQUEST <<<<<<<<<<\n");
    starList.Display();

    logInfo("Exiting ...");
    exit(EXIT_SUCCESS);
}


/*___oOo___*/
