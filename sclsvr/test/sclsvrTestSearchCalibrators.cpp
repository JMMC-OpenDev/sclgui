/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclsvrTestSearchCalibrators.cpp,v 1.2 2005-02-15 15:46:00 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    04-Oct-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * Test programm of sclsvr.
 */

static char *rcsId="@(#) $Id: sclsvrTestSearchCalibrators.cpp,v 1.2 2005-02-15 15:46:00 gzins Exp $"; 
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
#include "cmd.h"
#include "msg.h"
#include "gui.h"
/*
 * Local Headers 
 */
#include "sclsvr.h"
#include "sclsvrPrivate.h"
#include "msgErrors.h"

/* 
 * Main
 */

int main(int argc, char *argv[])
{
    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Error handling if necessary
        
        // Exit from the application with mcsFAILURE
        exit (EXIT_FAILURE);
    }
    logSetStdoutLogLevel(logINFO);
    /*******************************************************/
    // Try to connect the msgManager communication server 
    if (msgConnect("sclsvr", NULL) == mcsFAILURE)
    {
        logWarning("msgConnect() failed");
        errDisplayStack();
        errCloseStack();
        exit(EXIT_FAILURE);
    }
    logInfo("sclsvr connected to msgManager."); 
    for(;;)
    {
        // Try to receive a command 
        msgMESSAGE msg;
        if (msgReceive(&msg, msgWAIT_FOREVER) == mcsFAILURE)
        {
            // If the connection with msgManager has been lost... 
            if (errIsInStack("msg", msgERR_BROKEN_PIPE) == mcsTRUE)
            {
                msgDisconnect();
                logWarning("Connection with msgManager lost... ",
                           "Trying to reconnect.");
                for(;;)
                {
                    if (msgConnect("sclsvr", NULL) == mcsSUCCESS)
                    {
                        logInfo("Connected to msgManager.");
                        break;
                    }
                    errResetStack();
                    sleep(1);
                }
            }
            else
            {
                errCloseStack();
                sleep(1);
            }
        } // If an error occured during command reception... 
        else
        {
            logInfo("Received %s command.", msgGetCommand(&msg));
            logSetStdoutLogLevel(logQUIET);
            // If the received command is DEBUG 
            if (strcmp (msgGetCommand(&msg), "QUERY") == 0)
            {
                printf("Received parameter :\n %s", msgGetBodyPtr(&msg));
                printf("Starting Query ...\n");
                cmdPARAM_LIST *paramList;
                paramList = cmdParseParam("QUERY", msgGetBodyPtr(&msg));
                mcsUINT32 newArgc = (2 * cmdGetNbOfParams(paramList)) + 1;
                char *newArgv[newArgc];
                newArgv[0] = argv[0];
                char* tmpPtr = msgGetBodyPtr(&msg);
                for (mcsUINT32 index = 1; index < newArgc; index++)
                {
                    newArgv[index] = tmpPtr;
                    tmpPtr = strchr(tmpPtr, ' ');
                    if (tmpPtr != NULL)
                    {
                        *tmpPtr = '\0';
                        tmpPtr++;
                    }
                }
                
                
                sclsvrSEARCH_CALIBRATORS searchCalibrators;
                if (searchCalibrators.DecodeCommand(newArgc,
                                                    newArgv) == mcsFAILURE)
                {
                    exit (EXIT_FAILURE);
                }

                if (searchCalibrators.Run() == mcsFAILURE)
                {
                    exit (EXIT_FAILURE);
                }
                              
                printf("Query Finished ...\n");

                msgSetBody(&msg, "OK", 0);
                if (msgSendReply(&msg, mcsTRUE) == mcsFAILURE)
                {
                    errCloseStack();
                }
            }            
            // If the received command is EXIT 
            else if (strcmp(msgGetCommand(&msg), "EXIT") == 0)
            {
                // Try to send a confirmation reply 
                msgSetBody(&msg, "OK", 0);
                if (msgSendReply(&msg, mcsTRUE) == mcsFAILURE)
                {
                    errCloseStack();
                }

                // Exiting... 
                break;
            }
            else
            {
                // Try to send a confirmation reply 
                msgSetBody(&msg, "OK", 0);
                if (msgSendReply(&msg, mcsTRUE) == mcsFAILURE)
                {
                    errCloseStack();
                }
            }
        }
    }
    logSetStdoutLogLevel(logINFO);

    msgDisconnect();

    logInfo("sclsvr exited."); 

    // Close MCS services
    mcsExit();
    
    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
