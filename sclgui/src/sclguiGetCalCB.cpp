/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiGetCalCB.cpp,v 1.2 2005-02-04 08:08:41 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/01/28 10:32:57  gzins
 * Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of GetCalCB method.
 */

static char *rcsId="@(#) $Id: sclguiGetCalCB.cpp,v 1.2 2005-02-04 08:08:41 scetre Exp $"; 
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
        char *objName;
        if (getCalCmd.GetObjectName(&objName) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        // Observed magnitude
        mcsDOUBLE magnitude;
        if (getCalCmd.GetMag(&magnitude) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        mcsSTRING256 magnitudeStr;
        sprintf(magnitudeStr, "%lf", magnitude);
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
        mcsSTRING256 baseMinStr;
        sprintf(baseMinStr, "%lf", baseMin);
        // baseMax
        mcsDOUBLE baseMax;
        if (getCalCmd.GetBaseMax(&baseMax) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        mcsSTRING256 baseMaxStr;    
        sprintf(baseMaxStr, "%lf", baseMax);
        // lambda
        mcsDOUBLE lambda;
        if (getCalCmd.GetLambda(&lambda) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        mcsSTRING256 lambdaStr;
        sprintf(lambdaStr, "%lf", lambda);
        // visibility
        mcsDOUBLE vis;
        if (getCalCmd.GetVis(&vis) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        mcsSTRING256 visStr;
        sprintf(visStr, "%lf", vis);
        // visibility error
        mcsDOUBLE visErr;
        if (getCalCmd.GetVisErr(&visErr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        mcsSTRING256 visErrStr;
        sprintf(visErrStr, "%lf", visErr);

        // affect in the request object the value get by the user
        if (_request.SetConstraint(STAR_NAME_ID, objName) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(RA_ID, ra) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(DEC_ID, dec) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(OBSERVED_BAND_ID, band) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(STAR_MAGNITUDE_ID, magnitudeStr) 
            == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(BASEMIN_ID, baseMinStr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(BASEMAX_ID, baseMaxStr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(STAR_WLEN_ID, lambdaStr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(STAR_EXPECTED_VIS_ID, visStr) == mcsFAILURE)
        {
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        if (_request.SetConstraint(STAR_MAX_ERR_VIS_ID, visErrStr) 
            == mcsFAILURE)
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
            _displayList.UnPack(&dynBuf);
            
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
                _displayList.Clear();
            }
            else
            {
                // Inform user
                mcsSTRING64 usrMsg;
                sprintf(usrMsg, "CDS Return %d ", _displayList.Size());
                _theGui->SetStatus(false, usrMsg);
            
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
