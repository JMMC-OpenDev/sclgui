/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrServer.cpp,v 1.10 2006-03-03 15:25:23 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2005/10/26 11:27:24  lafrasse
 * Code review
 *
 * Revision 1.8  2005/03/08 16:33:51  gzins
 * Disconnected process from message service before exiting
 *
 * Revision 1.7  2005/02/08 04:52:33  gzins
 * Printed ou error stack when connection to MCS services fails
 *
 * Revision 1.6  2005/02/07 15:01:21  gzins
 * Added CVS log as modification history
 *
 * gzins     23-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * @file
 * Search Calibrators SERVER
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclsvrServer.cpp,v 1.10 2006-03-03 15:25:23 scetre Exp $"; 


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>


/**
 * @namespace std
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
#include "sclsvrSERVER.h"
#include "sclsvrPrivate.h"


/* 
 * Main
 */
int main(int argc, char *argv[])
{
    // Init MCS event server
    sclsvrSERVER scalibServer;
    if (scalibServer.Init(argc, argv) == mcsFAILURE)
    {
        errCloseStack();
        exit (EXIT_FAILURE);
    }

    // Main loop
    while (scalibServer.MainLoop() == mcsFAILURE)
    {
        errDisplayStack();
    }

    // Close MCS services
    scalibServer.Disconnect();
    mcsExit();

    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/