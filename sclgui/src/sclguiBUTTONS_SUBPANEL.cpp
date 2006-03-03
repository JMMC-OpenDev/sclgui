/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiBUTTONS_SUBPANEL.cpp,v 1.3 2006-03-03 15:28:17 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.1  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of sclguiBUTTONS_SUBPANEL class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiBUTTONS_SUBPANEL.cpp,v 1.3 2006-03-03 15:28:17 scetre Exp $"; 


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
 * Class constructor.
 */
sclguiBUTTONS_SUBPANEL::sclguiBUTTONS_SUBPANEL()
{
    // Create the "SHOW ALL RESULTS" button
    _showAllResultsButton.SetText("SHOW ALL RESULTS");
    _showAllResultsButton.SetHelp("Display of all results with coherent diameter.");
    // Place it at the of the container
    _showAllResultsButton.PlaceAtTop(mcsTRUE);

    // Create the "RESET" button
    _resetButton.SetText("RESET");
    _resetButton.SetHelp("This button reset the star list. It will show the list with visibility OK.");
    // Place it at the of the container
    _resetButton.PlaceAtTop(mcsTRUE);

    // Create the "SHOW DETAILS" button
    _showDetailsButton.SetText("SHOW DETAILS");
    _showDetailsButton.SetHelp("Show all properties of a star");
    // Place it at the of the container
    _showDetailsButton.PlaceAtTop(mcsTRUE);

    // Create the "HIDE DETAILS" button 
    _hideDetailsButton.SetText("HIDE DETAILS");
    _hideDetailsButton.SetHelp("Show only some specific properties");
    // Place it at the of the container
    _hideDetailsButton.PlaceAtTop(mcsTRUE);
    
    // Add all these new buttons in the container
    Add(&_resetButton);
    Add(&_showAllResultsButton);
    Add(&_showDetailsButton);
    Add(&_hideDetailsButton);
}

/**
 * Class destructor.
 */
sclguiBUTTONS_SUBPANEL::~sclguiBUTTONS_SUBPANEL()
{
}


/*
 * Public methods
 */
/**
 * Attach the given callback method to the 'RESET' button-generated event.
 *
 * @param eventHandler event handler
 * @param callbackMethod the associated callback
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiBUTTONS_SUBPANEL::SetResetCB(fndOBJECT &eventHandler,
                                         gwtCOMMAND::CB_METHOD callbackMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetResetCB()");

    _resetButton.AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD)callbackMethod);

    return mcsSUCCESS;
}

/**
 * Attach the given callback method to the 'SHOW ALL RESULT' button-generated
 * event.
 *
 * @param eventHandler event handler
 * @param callbackMethod the associated callback
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiBUTTONS_SUBPANEL::SetShowAllResultsCB(fndOBJECT &eventHandler,
                                  gwtCOMMAND::CB_METHOD callbackMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetShowAllResultsCB()");

    _showAllResultsButton.AttachCB(&eventHandler,
                                   (gwtCOMMAND::CB_METHOD) callbackMethod);

    return mcsSUCCESS;
}

/**
 * Attach the given callback method to the 'SHOW DETAILS' button-generated
 * event.
 *
 * @param eventHandler event handler
 * @param callbackMethod the associated callback
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiBUTTONS_SUBPANEL::SetShowDetailsCB(fndOBJECT &eventHandler,
                               gwtCOMMAND::CB_METHOD callbackMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetShowDetailsCB()");

    _showDetailsButton.AttachCB(&eventHandler,
                                (gwtCOMMAND::CB_METHOD) callbackMethod);

    return mcsSUCCESS;
}

/**
 * Attach the given callback method to the 'HIDE DETAILS' button-generated
 * event.
 *
 * @param eventHandler event handler
 * @param callbackMethod the associated callback
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiBUTTONS_SUBPANEL::SetHideDetailsCB(fndOBJECT &eventHandler,
                               gwtCOMMAND::CB_METHOD callbackMethod)
{
    logTrace("sclguiBUTTONS_SUBPANEL::SetHideDetailsCB()");

    _hideDetailsButton.AttachCB(&eventHandler,
                                (gwtCOMMAND::CB_METHOD) callbackMethod);

    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
