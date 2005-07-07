/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiGetCalCB.cpp,v 1.27 2005-07-07 05:09:27 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.26  2005/03/08 14:05:59  scetre
 * Added exit callback
 *
 * Revision 1.25  2005/03/07 15:56:17  gzins
 * Removed filtering on visibility
 *
 * Revision 1.24  2005/03/07 14:19:40  scetre
 * Added _withNoVarMult to know how many star of the resulting list are without variability and multiplicity
 *
 * Revision 1.23  2005/03/04 17:30:19  scetre
 * Return 'Done' when command is completed successfully
 *
 * Revision 1.22  2005/03/04 17:10:40  scetre
 * Improved error message intended to user
 *
 * Revision 1.21  2005/03/04 15:08:05  gzins
 * No longer reset error stack before sending reply when an error occured
 *
 * Revision 1.20  2005/03/04 09:53:43  gzins
 * Removed handling of -file option. Done by sclsrv.
 *
 * Revision 1.19  2005/03/03 16:50:31  scetre
 * Add user message for out of range parameter
 *
 * Revision 1.18  2005/02/28 13:50:01  scetre
 * Changed height of the result table
 *
 * Revision 1.17  2005/02/24 17:05:56  scetre
 * Checked whether calibrators have been found or not before unpacking reply
 *
 * Revision 1.16  2005/02/24 13:14:46  scetre
 * Get user error in order to show him the problem
 *
 * Revision 1.15  2005/02/23 17:06:27  scetre
 * Added list of ucd name to manage the display of the colum name
 *
 * Revision 1.14  2005/02/18 11:50:28  scetre
 * Changed behavior of gui in oder to filter by defautl on variability and visibility
 *
 * Revision 1.13  2005/02/17 09:25:43  scetre
 * Added 3 interger in the class in order to store CDS return, coherent diameter and visibility list size
 *
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

static char *rcsId="@(#) $Id: sclguiGetCalCB.cpp,v 1.27 2005-07-07 05:09:27 gzins Exp $"; 
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
#include "sclguiDISPLAY.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

evhCB_COMPL_STAT sclguiDISPLAY::GetCalCB(msgMESSAGE &msg, void*)
{
    logTrace("sclguiDISPLAY::GetCalCB()");

    // Check server is IDLE (no command is currently been processed)
    if (GetSubState() != evhSUBSTATE_IDLE)
    {
        errAdd(sclguiERR_SERVER_BUSY, msg.GetCommand());
        SendReply(msg);
        return evhCB_NO_DELETE;
    }
    else
    {
        _mainWindow->Hide();
        _theGui->SetStatus(true, "Looking for calibrators in star catalogs...");
        
        //Initialize the model
        _model.Init();
        
        // Defines callback to handle reply
        evhCMD_CALLBACK cmdReplyCB
            (this, (evhCMD_CB_METHOD)&sclguiDISPLAY::GetCalReplyCB);

        if (_model.SetRequest(msg) == mcsFAILURE)
        {
            // Report error
            _theGui->SetStatus(false, "Invalid command parameter list", 
                               errUserGet());
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        
        // Forward command; i.e. do not wait for reply. The GetCalReplyCB()
        // method will be called when reply is received.
        if (_sclServer.Forward(msg.GetCommand(), msg.GetBody(),
                               cmdReplyCB) == mcsFAILURE )
        {
            // Report error
            _theGui->SetStatus(false, errUserGet());

            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

        // Set server sub-state to BUSY
        SetSubState(evhSUBSTATE_BUSY);

        // Save received message to be able to send reply to requestor
        _msg = msg;
    }
    return evhCB_NO_DELETE;
}

evhCB_COMPL_STAT sclguiDISPLAY::GetCalReplyCB(msgMESSAGE &msg, void*)
{
    logTrace("sclguiDISPLAY::GetCalReplyCB()");
    // Clear list
    // If an error reply is received
    switch (msg.GetType())
    {
        case msgTYPE_ERROR_REPLY:
        {
            if (errIsInStack("msg", 35) == mcsTRUE)
            {
                errUserAdd(sclguiERR_SERVER_CRASH);
            }
            // Report error
            _theGui->SetStatus(false, errUserGet());

            // Clear result table

            // Prepare message reply
            _msg.SetBody("Request FAILED.");

            break;
        }
        case msgTYPE_REPLY:
        {
            // if reply success, set list in the model
            _model.SetList(msg);
            
            // Update main window
            _mainWindow->Hide();
            BuildMainWindow();
            _mainWindow->Show();
           
            // Prepare message reply
            _msg.SetBody("Done.");
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

evhCB_COMPL_STAT sclguiDISPLAY::ExitCB(msgMESSAGE &msg, void *userData)
{
    logTrace("sclguiDISPLAY::ExitCB()");
    
    _theGui->SetStatus(true, "Bye bye");

    _mainWindow->Hide();
    
    return evhSERVER::ExitCB(msg, userData);
}

/*___oOo___*/
