/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPanel.C,v 1.1 2004-12-01 07:25:55 mella Exp $"
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

static char *rcsId="@(#) $Id: sclguiPanel.C,v 1.1 2004-12-01 07:25:55 mella Exp $"; 
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
    sclguiPANEL scalibPanel;

    // Init server
    if (scalibPanel.Init(argc, argv) == FAILURE)
    {
        exit (EXIT_FAILURE);
    }

    // Main loop
    if (scalibPanel.MainLoop() == FAILURE)
    {
        errDisplayStack();
    }


    // Close MCS services
    mcsExit();
    
    // Exit from the application with SUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
