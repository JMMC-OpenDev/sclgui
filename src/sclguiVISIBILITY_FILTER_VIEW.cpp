/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiVISIBILITY_FILTER_VIEW.cpp,v 1.4 2005-10-18 12:52:48 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiVISIBILITY_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiVISIBILITY_FILTER_VIEW.cpp,v 1.4 2005-10-18 12:52:48 lafrasse Exp $"; 
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
#include "sclguiPrivate.h"


/**
 * Class constructor
 */
sclguiVISIBILITY_FILTER_VIEW::sclguiVISIBILITY_FILTER_VIEW():sclguiFILTER_VIEW()    
{
    // Prepare widgets
    _accuracyTextfield.SetLabel
        ("Maximal expected relative accuracy on the calibrator squared "
         "visibility (%)");
    _accuracyTextfield.SetText("100");

    // Add widget in widget map
    Add(&_accuracyTextfield);
    Add(&_applyFilterButton); 
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
 * Update view
 *
 * @return mcsTRUE if multiplicity are authorized otherwise mcsFALSE is
 * returned
 */
mcsCOMPL_STAT sclguiVISIBILITY_FILTER_VIEW::Update()
{
    logTrace("sclguiVISIBILITY_FILTER_VIEW::Update()");

    // Get visibility filter
    sclsvrVISIBILITY_FILTER *visFilter = 
        (sclsvrVISIBILITY_FILTER *)
        _filterListModel->GetFilter(sclsvrVISIBILITY_FILTER_NAME);

    if (visFilter->IsEnabled() == mcsTRUE)
    {
        mcsFLOAT filterValue;
        visFilter->GetVisibilityValue(&filterValue);

        string filterValueString;
        ostringstream ostr;
        
        ostr << filterValue;
        filterValueString = ostr.str();
        _accuracyTextfield.SetText(filterValueString);
    }
    else
    {
        _accuracyTextfield.SetText("100");
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

    return _accuracyTextfield.GetText();
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
