/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiFILTER_VIEW.cpp,v 1.3 2005-10-18 12:52:48 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiFILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiFILTER_VIEW.cpp,v 1.3 2005-10-18 12:52:48 lafrasse Exp $"; 
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
#include "sclguiFILTER_VIEW.h"
#include "sclguiPrivate.h"


/**
 * Class constructor
 */
sclguiFILTER_VIEW::sclguiFILTER_VIEW()
{
    // Create an "Ok"  button
    _applyFilterButton.SetText("Ok");
    _applyFilterButton.SetHelp("Start the process");
}

/**
 * Class destructor
 */
sclguiFILTER_VIEW::~sclguiFILTER_VIEW()
{
}


/*
 * Public methods
 */
/**
 * Attach the given callback method to the 'Ok' button-generated event.
 *
 * @param eventHandler event handler
 * @param callback the associated callback
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::SetApplyCB(fndOBJECT &eventHandler,
                                         gwtCOMMAND::CB_METHOD callbackMethod)
{
    logTrace("sclguiFILTER_VIEW::SetApplyCB()");

    _applyFilterButton.AttachCB(&eventHandler,
                                (gwtCOMMAND::CB_METHOD)callbackMethod);

    return mcsSUCCESS;
}

/**
 * Attach model in the request view
 *
 * @param filterListModel the model to attach
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiFILTER_VIEW::AttachModel(sclguiFILTER_LIST_MODEL &filterListModel)
{
    logTrace("sclguiFILTER_VIEW::AttachModel()");

    // Attach to the filter list model
    _filterListModel = &filterListModel;

    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
