/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPanel.cpp,v 1.2 2005-02-07 17:37:39 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     25-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * Entry point for SearchCalib graphical application. 
 */

static char *rcsId="@(#) $Id: sclguiPanel.cpp,v 1.2 2005-02-07 17:37:39 scetre Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/**
 * Defines
 */
#define DEFAULTGUIPORT 1234


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

    cout << "Starting " << argv[0] << ":" << port << endl;
    sclguiPANEL scalibPanel("localhost",port);

    // Init server
    if (scalibPanel.Init(argc, argv) == mcsFAILURE)
    {
        exit (EXIT_FAILURE);
    }

    // Main loop
    while (scalibPanel.MainLoop() == mcsFAILURE)
    {
        errDisplayStack();
    }

    // Close MCS services
    mcsExit();
    
    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
