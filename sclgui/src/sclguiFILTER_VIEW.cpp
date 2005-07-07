/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiFILTER_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiFILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiFILTER_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"; 
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
}
sclguiFILTER_VIEW::sclguiFILTER_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    BuildWindow();
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
 * Show the window
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::Show()
{
    logTrace("sclguiFILTER_VIEW::Show()");
    
    // show filter window
    _filterWindow->Show();
    Update(); 

    return mcsSUCCESS;
}

/**
 * Hide the Window
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::Hide()
{
    logTrace("sclguiFILTER_VIEW::Hide()");
    
    _filterWindow->Hide();

    return mcsSUCCESS;
}

/**
 * Get a link on the filter window
 *
 * @return link on the filter window
 */
gwtWINDOW * sclguiFILTER_VIEW::GetWindowLink()
{
    logTrace("sclguiFILTER_VIEW::GetWindowLink()");

    return _filterWindow;
}

/**
 * Get a link on the apply button
 *
 * @return link on the apply button
 */
gwtBUTTON * sclguiFILTER_VIEW::GetApplyButtonLink()
{
    logTrace("sclguiFILTER_VIEW::GetApplyButtonLink()");

    return _applyFilterButton;
}

/*
 * Protected methods
 */
/**
 * Build window filter
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::BuildWindow()
{
    logTrace("sclguiFILTER_VIEW::BuildWindow()");

    // Create the window
    _filterWindow = new gwtWINDOW();
    CompleteWindowInformation();
    BuildMainFilterView();
    BuildApplyFilterButton();
    
    return mcsSUCCESS;
}


/**
 * Complete Window information (title, help)
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::CompleteWindowInformation()
{
    logTrace("sclguiFILTER_VIEW::CompleteWindowInformation()");

    _filterWindow->SetTitle("default filter window");
    _filterWindow->SetHelp("No help avaible");

    return mcsSUCCESS;
}

/**
 * Build main filter view
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::BuildMainFilterView()
{
    logTrace("sclguiFILTER_VIEW::BuildMainFilterView()");
    
    return mcsSUCCESS;
}

/*
 * Private methods
 */

/**
 * Build the apply filter button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_VIEW::BuildApplyFilterButton()
{
    logTrace("sclguiFILTER_VIEW::BuildApplyFilterButton()");
   
    // Create the button
    _applyFilterButton = new gwtBUTTON("Ok","Start the process");
    // Add it in the window
    _filterWindow->Add(_applyFilterButton);

    return mcsSUCCESS;
}

/*___oOo___*/
