/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCONFIRM_SUBPANEL.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiCONFIRM_SUBPANEL class.
 */

static char *rcsId="@(#) $Id: sclguiCONFIRM_SUBPANEL.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"; 
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
    // Add empty label
    _confirmLabel.SetText("");
    _confirmLabel.SetHelp("No Help");

    // Add confirm button
    _overwriteButton.SetText("Overwrite");
    _overwriteButton.PlaceAtTop(mcsTRUE);

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
 * Set call back associated to the overwrite button
 *
 * @param eventHandler event handler
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT 
sclguiCONFIRM_SUBPANEL::SetOverwriteCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiCONFIRM_SUBPANEL::SetOverwriteCB()");

    _overwriteButton.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);
        
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
