/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiPanel.cpp,v 1.7 2005-03-08 16:33:58 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2005/03/07 17:50:03  gzins
 * Improved star number check for delete callback: check whether is an interger or not
 * Changed messages intended to end user to be homogeneous and more explicit
 *
 * Revision 1.5  2005/03/06 10:51:00  gzins
 * Removed debug messages on cout
 *
 * Revision 1.4  2005/02/15 15:57:24  gzins
 * Added CVS log as file modification history
 *
 * mella     25-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Entry point for SearchCalib graphical application. 
 */

static char *rcsId="@(#) $Id: sclguiPanel.cpp,v 1.7 2005-03-08 16:33:58 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/**
 * Defines
 */
#define DEFAULTGUIPORT 1357


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
#include "sclgui.h"
#include "sclguiPrivate.h"


/*
 * Local Variables
 */
#include "sclguiPANEL.h"
 
/* 
 * Main
 */

int main(int argc, char *argv[])
{
    
    mcsINT32 port = DEFAULTGUIPORT ;
    char * guiPortEnvVar;
    
    guiPortEnvVar = getenv("GILDASGUIPORT");
    if ( guiPortEnvVar != NULL )
    {
        if (!sscanf( guiPortEnvVar, "%d", &port))
        {
            port = DEFAULTGUIPORT ;
        }
    }

    sclguiPANEL scalibPanel("localhost",port);

    // Init server
    if (scalibPanel.Init(argc, argv) == mcsFAILURE)
    {
        errCloseStack();
        exit (EXIT_FAILURE);
    }

    // Main loop
    while (scalibPanel.MainLoop() == mcsFAILURE)
    {
        errCloseStack();
    }

    // Close MCS services
    scalibPanel.Disconnect();
    mcsExit();

    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
