/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiGetCalCB.cpp,v 1.13 2005-02-17 09:25:43 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2005/02/17 07:51:10  scetre
 * Replace old unused method SetObjectName by Parse method
 *
 * Revision 1.11  2005/02/16 17:35:06  gzins
 * Updated call to sclsvrCALIBRATOR_LIST::UnPack
 *
 * Revision 1.10  2005/02/16 16:58:34  gzins
 * Used new vobsREQUEST::Parse() method
 *
 * Revision 1.9  2005/02/14 14:23:19  scetre
 * used of miscoDYN_BUF instead of miscDYN_BUF
 *
 * Revision 1.8  2005/02/10 08:23:48  gzins
 * Updated message informing about the number of found stars
 *
 * Revision 1.7  2005/02/08 21:02:38  gzins
 * Used _request class member instead of local instance
 * Used new sclsvrCALIBRATOR_LIST::Copy() method
 *
 * Revision 1.6  2005/02/08 07:25:18  gzins
 * Replaced vosREQUEST by sclsvrREQUEST
 *
 * Revision 1.5  2005/02/07 17:37:39  scetre
 * *** empty log message ***
 *
 * Revision 1.4  2005/02/07 11:11:24  scetre
 * minor change
 *
 * Revision 1.3  2005/02/04 14:24:06  scetre
 * Used sort method of sclsvr
 *
 * Revision 1.2  2005/02/04 08:08:41  scetre
 * Added command read
 *
 * Revision 1.1  2005/01/28 10:32:57  gzins
 * Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of GetCalCB method.
 */

static char *rcsId="@(#) $Id: sclguiGetCalCB.cpp,v 1.13 2005-02-17 09:25:43 scetre Exp $"; 
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

        // Build the request object from the parameters of the command
        if (_request.Parse(msg.GetBody()) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

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
            _displayList.Clear();
            _visibilityOkList.Clear();
            _coherentDiameterList.Clear();

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
    // Clear list
    _currentList.Clear();
    _displayList.Clear();
    _visibilityOkList.Clear();
    _coherentDiameterList.Clear();
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
            _currentList.UnPack(msg.GetBody());
            _currentList.Display();
            _coherentDiameterList.Copy(_currentList, mcsFALSE, mcsTRUE);
            _visibilityOkList.Copy(_coherentDiameterList, mcsTRUE, mcsFALSE);
            _displayList.Copy(_visibilityOkList);
            _found = _currentList.Size();
            _diam = _coherentDiameterList.Size();
            _vis = _visibilityOkList.Size();
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
                
                // Clear list
                _currentList.Clear();
                _displayList.Clear();
                _visibilityOkList.Clear();
                _coherentDiameterList.Clear();
            }
            else
            {
                // Inform user
                mcsSTRING64 usrMsg;
                sprintf(usrMsg, "%d star(s) found in CDS catalogs",
                        _currentList.Size());
                _theGui->SetStatus(true, usrMsg);
            
                // Fill the result table
                FillResultsTable(&_displayList);
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
