/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiBUTTONS_SUBPANEL.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiBUTTONS_SUBPANEL class.
 */

static char *rcsId="@(#) $Id: sclguiBUTTONS_SUBPANEL.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"; 
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
 * Local Headers 
 */
#include "sclguiBUTTONS_SUBPANEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiBUTTONS_SUBPANEL::sclguiBUTTONS_SUBPANEL()
{
    // Prepare button
    // Created "SHOW ALL RESULTS" button
    _showAllResultsButton.SetText("SHOW ALL RESULTS");
    _showAllResultsButton.SetHelp("Display of all results with coherent diameter.");
    // Place at top "SHOW ALL RESULTS" button
    _showAllResultsButton.PlaceAtTop(mcsTRUE);
    // Created "RESET" button
    _resetButton.SetText("RESET");
    _resetButton.SetHelp("This button reset the star list. It will show the list with visibility OK.");
    // Place at top "RESET" button
    _resetButton.PlaceAtTop(mcsTRUE);
    // Created "SHOW DETAILS" button
    _showDetailsButton.SetText("SHOW DETAILS");
    _showDetailsButton.SetHelp("Show all properties of a star");
    // Place at top "SHOW DETAILS" button
    _showDetailsButton.PlaceAtTop(mcsTRUE);
    // Created "HIDE DETAILS" button 
    _hideDetailsButton.SetText("HIDE DETAILS");
    _hideDetailsButton.SetHelp("Show only some specific properties");
    // Place at top "HIDE DETAILS" button
    _hideDetailsButton.PlaceAtTop(mcsTRUE);
    
    // Add this button in the megaWidget map
    Add(&_resetButton);
    Add(&_showAllResultsButton);
    Add(&_showDetailsButton);
    Add(&_hideDetailsButton);
}

/**
 * Class destructor
 */
sclguiBUTTONS_SUBPANEL::~sclguiBUTTONS_SUBPANEL()
{
}

/*
 * Public methods
 */

/**
 * Attach widget Reset to the approriate CB
 *
 * @param eventHandler event handler
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiBUTTONS_SUBPANEL::SetResetCB(fndOBJECT &eventHandler,
                                             gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetResetCB()");

    _resetButton.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach Show All Result widget to the approriate CB
 *
 * @param eventHandler event handler
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiBUTTONS_SUBPANEL::SetShowAllResultsCB(fndOBJECT &eventHandler,
                                        gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetShowAllResultsCB()");

    _showAllResultsButton.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach Show Details widget to the approriate CB
 *
 * @param eventHandler event handler
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiBUTTONS_SUBPANEL::SetShowDetailsCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetShowDetailsCB()");

    _showDetailsButton.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach Hide Details widget to the approriate CB
 *
 * @param eventHandler event handler
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiBUTTONS_SUBPANEL::SetHideDetailsCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetHideDetailsCB()");

    _hideDetailsButton.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
