/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiGetCalCB.cpp,v 1.9 2005-02-14 14:23:19 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

static char *rcsId="@(#) $Id: sclguiGetCalCB.cpp,v 1.9 2005-02-14 14:23:19 scetre Exp $"; 
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

        // Get the command
        sclsvrGETCAL_CMD getCalCmd(msg.GetCommand(), msg.GetBody());

        // Parse command
        if (getCalCmd.Parse() == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

        // Object name
        char *objectName;
        if (getCalCmd.GetObjectName(&objectName) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Observed magnitude
        mcsDOUBLE magnitude;
        if (getCalCmd.GetMag(&magnitude) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // band
        char *band;
        if (getCalCmd.GetBand(&band) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // ra
        char *ra;
        if (getCalCmd.GetRa(&ra) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // dec
        char *dec;
        if (getCalCmd.GetDec(&dec) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // baseMin
        mcsDOUBLE baseMin;
        if (getCalCmd.GetBaseMin(&baseMin) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // baseMax
        mcsDOUBLE baseMax;
        if (getCalCmd.GetBaseMax(&baseMax) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // wlen
        mcsDOUBLE wlen;
        if (getCalCmd.GetWlen(&wlen) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // visibility
        mcsDOUBLE vis;
        if (getCalCmd.GetVis(&vis) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // visibility error
        mcsDOUBLE visErr;
        if (getCalCmd.GetVisErr(&visErr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

        // Build the request object from the parameters of the command
        // Affect the reference object name
        if (_request.SetObjectName(objectName) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the right ascension position
        if (_request.SetObjectRa(ra) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the declinaison position
        if (_request.SetObjectDec(dec) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the wavelength
        if (_request.SetObservingWlen(wlen) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the magnitude
        if (_request.SetObjectMag(magnitude) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the expected visibility
        if (_request.SetExpectedVisibility(vis, visErr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the observed band
        if (_request.SetSearchBand(band) ==  mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Affect the baseline length
        if (_request.SetBaseline(baseMin, baseMax) ==  mcsFAILURE)
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
            miscoDYN_BUF dynBuf;
            dynBuf.AppendString(msg.GetBody());
            _currentList.UnPack(&dynBuf);
            _currentList.Display();
            _coherentDiameterList.Copy(_currentList, mcsFALSE, mcsTRUE);
            _visibilityOkList.Copy(_currentList, mcsTRUE, mcsFALSE);
            _displayList.Copy(_visibilityOkList);
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
