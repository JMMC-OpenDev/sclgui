/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCONFIRM_SUBPANEL.cpp,v 1.3 2005-12-22 13:03:22 scetre Exp $"
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
 *  Definition of sclguiCONFIRM_SUBPANEL class.
 */

static char *rcsId="@(#) $Id: sclguiCONFIRM_SUBPANEL.cpp,v 1.3 2005-12-22 13:03:22 scetre Exp $"; 
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
#include "sclguiCONFIRM_SUBPANEL.h"
#include "sclguiPrivate.h"


/**
 * Class constructor
 */
sclguiCONFIRM_SUBPANEL::sclguiCONFIRM_SUBPANEL()
{
    // Create an empty label
    _confirmLabel.SetText("");
    _confirmLabel.SetHelp("No Help");

    // Create an 'OVERWRITE' button
    _overwriteButton.SetText("Overwrite");
    _overwriteButton.PlaceAtTop(mcsTRUE);

    // Add the newly created widgets to the sub-panel
    Add(&_confirmLabel);
    Add(&_overwriteButton);
}

/**
 * Class destructor
 */
sclguiCONFIRM_SUBPANEL::~sclguiCONFIRM_SUBPANEL()
{
}


/*
 * Public methods
 */
/**
 * Attach the given callback method to the 'OVERWRITE' button-generated event.
 *
 * @param eventHandler event handler
 * @param callbackMethod the associated callback
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT 
sclguiCONFIRM_SUBPANEL::SetOverwriteCB(fndOBJECT &eventHandler,
                                       gwtCOMMAND::CB_METHOD callbackMethod)
{
    logTrace("sclguiCONFIRM_SUBPANEL::SetOverwriteCB()");

    _overwriteButton.AttachCB(&eventHandler,
                              (gwtCOMMAND::CB_METHOD) callbackMethod);
        
    return mcsSUCCESS;
}

/**
 * Set text in the pop up
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclguiCONFIRM_SUBPANEL::SetPopUpText(string text)
{
    logTrace("sclguiCONFIRM_SUBPANEL::SetPopUpText()");

    _confirmLabel.SetText(text);
    
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
