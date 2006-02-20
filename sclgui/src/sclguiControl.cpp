/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiControl.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/


static char *rcsId="@(#) $Id: sclguiControl.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"; 
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
 * Main
 */

int main(int argc, char *argv[])
{
    {
        sclguiCONTROLLER scalibPanel;

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
    }
    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
