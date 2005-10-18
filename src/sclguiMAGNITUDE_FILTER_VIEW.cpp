/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMAGNITUDE_FILTER_VIEW.cpp,v 1.3 2005-10-18 12:52:48 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiMAGNITUDE_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiMAGNITUDE_FILTER_VIEW.cpp,v 1.3 2005-10-18 12:52:48 lafrasse Exp $"; 
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
#include "sclguiMAGNITUDE_FILTER_VIEW.h"
#include "sclguiPrivate.h"


/**
 * Class constructor
 */
sclguiMAGNITUDE_FILTER_VIEW::sclguiMAGNITUDE_FILTER_VIEW() : sclguiFILTER_VIEW()
{
    // Create textfield
    _magRangeTextfield.SetLabel("Maximal Magnitude Separation (mag)");
    _magRangeTextfield.SetText("0");

    // Add widget in widget map
    Add(&_magRangeTextfield);
    Add(&_applyFilterButton);
}

/**
 * Class destructor
 */
sclguiMAGNITUDE_FILTER_VIEW::~sclguiMAGNITUDE_FILTER_VIEW()
{
}


/*
 * Public methods
 */
/**
 * Update view 
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMAGNITUDE_FILTER_VIEW::Update()
{
    logTrace("sclguiMAGNITUDE_FILTER_VIEW::Update()");
    
    // Get magnitude filter
    vobsMAGNITUDE_FILTER *magFilter =
        (vobsMAGNITUDE_FILTER *)
        _filterListModel->GetFilter(vobsMAGNITUDE_FILTER_NAME);

    // If the magnitude filter is enabled
    if (magFilter->IsEnabled() == mcsTRUE)
    {
        // Get the filter value
        mcsSTRING32 band;
        mcsFLOAT magnitude;
        mcsFLOAT magnitudeRange;
        magFilter->GetMagnitudeValue(&band, &magnitude, &magnitudeRange);

        string magnitudeValueString;
        ostringstream ostr;
        
        ostr << magnitudeRange;
        magnitudeValueString = ostr.str();

        // write them in the widget
        _magRangeTextfield.SetText(magnitudeValueString);
    }
    else
    {
        // else, the defautl value is set to zero
        _magRangeTextfield.SetText("0");
    }
    
    return mcsSUCCESS;    
}

/**
 * Get magnitude range 
 *
 * @return magnitude range
 */
string sclguiMAGNITUDE_FILTER_VIEW::GetMagRange()
{
    logTrace("sclguiMAGNITUDE_FILTER_VIEW::GetMagRange()");

    return _magRangeTextfield.GetText();
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
