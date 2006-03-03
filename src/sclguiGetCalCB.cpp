/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiGetCalCB.cpp,v 1.30 2006-03-03 15:28:17 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.29  2006/02/22 13:28:02  gzins
 * Put command callbacks in separated source files
 *
 *
 ******************************************************************************/

/**
 * @file
 * Definition of GetCalCB() method 
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiGetCalCB.cpp,v 1.30 2006-03-03 15:28:17 scetre Exp $"; 

/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
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
#include "sclguiCONTROLLER.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

/**
 * GETCAL callback method
 *
 * @param msg message
 */
evhCB_COMPL_STAT sclguiCONTROLLER::GetCalCB(msgMESSAGE &msg, void*)
{
    logTrace("sclguiCONTROLLER::GetCalCB()");

    // If the server is NOT idle (i.e. a command is currently been processed)
    if (GetSubState() != evhSUBSTATE_IDLE)
    {
        // Report error
        errAdd(sclguiERR_SERVER_BUSY, msg.GetCommand());
        SendReply(msg);
        return evhCB_NO_DELETE;
    }

    _mainWindow.Hide();
    SetStatus(true, "Looking for calibrators in star catalogs...");
    
    // Initialize the filter model
    if (_filterListModel.ResetFilters() == mcsFAILURE)
    {
        // Report error
        SetStatus(false, "failed to init filters", errUserGet());
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Enable variability filter
    if (_filterListModel.SetFilterVariability(mcsTRUE) == mcsFAILURE)
    {
        // Report error
        SetStatus(false, "failed to enable var filter", errUserGet());
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Enable multiplicity filter
    if (_filterListModel.SetFilterMultiplicity(mcsTRUE) == mcsFAILURE)
    {
        // Report error
        SetStatus(false, "failed to init mult filter", errUserGet());
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Initialize the calibrator list filter
    if ((_calibratorListModel.ResetCalibrators() == mcsFAILURE) ||
        (_calibratorListModel.ResetDeletedCalibrators() == mcsFAILURE))
    {
        // Report error
        SetStatus(false, "failed to init calibrator list", errUserGet());
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Attached the filter list to the calibrator list model
    if (_calibratorListModel.SetFilterList(&_filterListModel) == mcsFAILURE)    
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Define the callback to be used for reply handling
    evhCMD_CALLBACK cmdReplyCB
        (this, (evhCMD_CB_METHOD)&sclguiCONTROLLER::GetCalReplyCB);

    if (_requestModel.Parse(msg) == mcsFAILURE)
    {
        // Report error
        SetStatus(false, "Invalid command parameter list", errUserGet());
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Forward command; i.e. do not wait for reply. The GetCalReplyCB()
    // method will be called when reply is received.
    if (_sclServer.Forward(msg.GetCommand(), msg.GetBody(),
                           cmdReplyCB) == mcsFAILURE )
    {
        // Report error
        SetStatus(false, errUserGet());

        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Set server sub-state to 'busy'
    SetSubState(evhSUBSTATE_BUSY);

    // Save the received message in order to reply later
    _msg = msg;

    return evhCB_NO_DELETE;
}

/**
 * GETCAL callback reply method
 *
 * @param msg message
 */
evhCB_COMPL_STAT sclguiCONTROLLER::GetCalReplyCB(msgMESSAGE &msg, void*)
{
    logTrace("sclguiCONTROLLER::GetCalReplyCB()");

    // Clear list
    // Analyze received message type
    switch (msg.GetType())
    {
        // Error message
        case msgTYPE_ERROR_REPLY:
        {
            if (errIsInStack("msg", 35) == mcsTRUE)
            {
                errUserAdd(sclguiERR_SERVER_CRASH);
            }

            // Report error
            SetStatus(false, errUserGet());

            // Clear result table

            // Prepare message reply
            _msg.SetBody("Request FAILED.");

            break;
        }

        // Valid reply
        case msgTYPE_REPLY:
        {
            // If the received message is not the last
            if (msg.IsLastReply() == mcsFALSE)
            {
                // Then it is containing the progression status to be displayed
                SetStatus(true, msg.GetBody());

                // Return until the next message arrives
                return evhCB_NO_DELETE;
            }

            // if reply success, set list in the model
            _calibratorListModel.SetList(msg);
            
            // Update main window
            _mainWindow.Hide();
            _mainWindow.Show();
           
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
    
    // Set server sub-state back to 'idle'
    SetSubState(evhSUBSTATE_IDLE);

    // Return definitively
    return evhCB_DELETE;
}

/*___oOo___*/
