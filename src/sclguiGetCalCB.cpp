/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiGetCalCB.cpp,v 1.1 2005-01-28 10:32:57 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Definition of GetCalCB method.
 */

static char *rcsId="@(#) $Id: sclguiGetCalCB.cpp,v 1.1 2005-01-28 10:32:57 gzins Exp $"; 
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
 * Search calibrators server
 */
#include "sclsvr.h"

/*
 * Local Headers 
 */
#include "sclguiPANEL.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

evhCB_COMPL_STAT sclguiPANEL::GetCalCB(msgMESSAGE &msg, void*)
{
    logExtDbg("sclguiPANEL::GetCalCB()");

    // Check server is IDLE (no command is currently been processed)
    if (GetSubState() != evhSUBSTATE_IDLE)
    {
        errAdd(sclguiERR_SERVER_BUSY, msg.GetCommand());
        SendReply(msg);
        return evhCB_NO_DELETE;
    }
    else
    {
        _theGui->SetStatus(true, "Looking for calibrators in star catalogs...");

        // Defines callback to handle reply
        evhCMD_CALLBACK cmdReplyCB
            (this, (evhCMD_CB_METHOD)&sclguiPANEL::GetCalReplyCB);

        // Forward command; i.e. do not wait for reply. The GetCalReplyCB()
        // method will be called when reply is received.
        if (_sclServer.Forward(msg.GetCommand(), msg.GetBody(),
                               cmdReplyCB) == mcsFAILURE )
        {
            // Report error
            _theGui->SetStatus(false, errUserGet());
            errCloseStack();

            // Clear result table
            _currentList.Clear();

            // Send reply
            msg.SetBody("Request FAILED.");
            SendReply(msg);
        }

        // Set server sub-state to BUSY
        SetSubState(evhSUBSTATE_BUSY);

        // Save received message to be able to send reply to requestor
        _msg = msg;
    }
    return evhCB_NO_DELETE;
}

evhCB_COMPL_STAT sclguiPANEL::GetCalReplyCB(msgMESSAGE &msg, void*)
{
    logExtDbg("sclguiPANEL::GetCalReplyCB()");

    // If an error reply is received
    switch (msg.GetType())
    {
        case msgTYPE_ERROR_REPLY:
        {
            // Report error
            _theGui->SetStatus(false, errUserGet());
            errCloseStack();

            // Clear result table
            _currentList.Clear();

            // Prepare message reply
            _msg.SetBody("Request FAILED.");

            break;
        }
        case msgTYPE_REPLY:
        {
            // Retreive the returned calibrator list
            miscDYN_BUF dynBuf;
            miscDynBufInit(&dynBuf);
            miscDynBufAppendString(&dynBuf, msg.GetBody());
            _currentList.UnPack(&dynBuf);

            // Display list of calibrators
            if (logGetStdoutLogLevel() >= logTEST)
            {
                _currentList.Display();
            }

            // If there is no calibrator
            if (_currentList.Size() == 0)
            {
                // Inform user
                _theGui->SetStatus(false, "No calibrator found.");
                
                // Clear result table
                _currentList.Clear();
            }
            else
            {
                // Inform user
                mcsSTRING64 usrMsg;
                sprintf(usrMsg, "%d calibrator(s) found.", _currentList.Size());
                _theGui->SetStatus(false, usrMsg);
            
                // Fill the result table
                FillResultsTable(&_currentList);
            }

            // Update main window
            _mainWindow->Hide();
            BuildMainWindow();
            _mainWindow->Show();
            
            // Prepare message reply
            _msg.SetBody("Request OK.");
            break;
        }
        default:
        {
            break;
        }
    }
    
    // Send reply to the requestor
    SendReply(_msg);

    // Reset sub-state to IDLE
    SetSubState(evhSUBSTATE_IDLE);

    return evhCB_DELETE;
}

/*___oOo___*/
