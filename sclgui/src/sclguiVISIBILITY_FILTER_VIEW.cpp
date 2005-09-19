/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiVISIBILITY_FILTER_VIEW.cpp,v 1.2 2005-09-19 07:45:25 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/07/07 05:07:21  gzins
 * Added - Applied Model-View-Controller (MVC) design
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiVISIBILITY_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiVISIBILITY_FILTER_VIEW.cpp,v 1.2 2005-09-19 07:45:25 scetre Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

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
#include "sclguiVISIBILITY_FILTER_VIEW.h"
#include "sclguiMODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiVISIBILITY_FILTER_VIEW::sclguiVISIBILITY_FILTER_VIEW()
{
}

sclguiVISIBILITY_FILTER_VIEW::sclguiVISIBILITY_FILTER_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    BuildWindow();
}

/**
 * Class destructor
 */
sclguiVISIBILITY_FILTER_VIEW::~sclguiVISIBILITY_FILTER_VIEW()
{
}

/*
 * Public methods
 */
/**
 * Update window
 *
 * @return mcsTRUE if multiplicity are authorized otherwise mcsFALSE is
 * returned
 */
mcsCOMPL_STAT sclguiVISIBILITY_FILTER_VIEW::Update()
{
    logTrace("sclguiVISIBILITY_FILTER_VIEW::Update()");

    // Get the filter list of the associated model
    vobsFILTER_LIST *filterList = _model->GetFilterList();

    // Get visibility filter
    sclsvrVISIBILITY_FILTER *visFilter = 
        (sclsvrVISIBILITY_FILTER *)
        filterList->GetFilter(sclsvrVISIBILITY_FILTER_NAME);

    if (visFilter->IsEnabled() == mcsTRUE)
    {
        mcsFLOAT filterValue;
        visFilter->GetVisibilityValue(&filterValue);
       
        // convert accuracy expected to %
        filterValue = 100 * filterValue;
        
        string filterValueString;
        ostringstream ostr;
        
        ostr << filterValue;
        filterValueString = ostr.str();
        _accuracyTextfield->SetText(filterValueString);
    }
    else
    {
        _accuracyTextfield->SetText("100");
    }
    
    return mcsSUCCESS;
}

/**
 * Get the visibility filter value
 *
 * return string visibility
 */
string sclguiVISIBILITY_FILTER_VIEW::GetVisFilterValue()
{
    logTrace("sclguiVISIBILITY_FILTER_VIEW::GetVisFilterValue()");

    return _accuracyTextfield->GetText();
}

/*
 * Protected methods
 */
/**
 * Complete Window information (title, help)
 *
 * @return mcsTRUE if multiplicity are authorized otherwise mcsFALSE is
 * returned
 */
mcsCOMPL_STAT sclguiVISIBILITY_FILTER_VIEW::CompleteWindowInformation()
{
    logTrace("sclguiVISIBILITY_FILTER_VIEW::CompleteWindowInformation()");

    gwtWINDOW *ownWindow = GetWindowLink();
    ownWindow->SetTitle("Visibility");
    static string windowHelp
        ("Allows to select stars using the visibility");
    ownWindow->SetHelp(windowHelp);

    return mcsSUCCESS;
}

/**
 * Build main filter view
 *
 * @return mcsTRUE if multiplicity are authorized otherwise mcsFALSE is
 * returned
 */
mcsCOMPL_STAT sclguiVISIBILITY_FILTER_VIEW::BuildMainFilterView()
{
    logTrace("sclguiVISIBILITY_FILTER_VIEW::BuildMainFilterView()");

    gwtWINDOW *ownWindow = GetWindowLink();

    _accuracyTextfield = new gwtTEXTFIELD();
    _accuracyTextfield->SetLabel
        ("Maximal expected relative accuracy on the calibrator squared "
         "visibility (%)");
    _accuracyTextfield->SetText("100");
    
    // Add widgets in the window
    ownWindow->Add(_accuracyTextfield);
    
    return mcsSUCCESS;
}
/*
 * Private methods
 */


/*___oOo___*/
