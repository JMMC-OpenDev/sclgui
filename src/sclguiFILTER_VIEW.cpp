/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiFILTER_VIEW.cpp,v 1.2 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiFILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiFILTER_VIEW.cpp,v 1.2 2005-10-11 15:24:15 scetre Exp $"; 
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
    // Create the button
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
 * Set apply CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT 
sclguiFILTER_VIEW::SetApplyCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiFILTER_VIEW::SetApplyCB()");

    _applyFilterButton.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach model in the request view
 *
 * @filterListModel the model to attach
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiFILTER_VIEW::AttachModel(sclguiFILTER_LIST_MODEL &filterListModel)
{
    logTrace("sclguiFILTER_VIEW::AttachModel()");

    // Attach to the filter list model
    _filterList = &filterListModel;

    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
