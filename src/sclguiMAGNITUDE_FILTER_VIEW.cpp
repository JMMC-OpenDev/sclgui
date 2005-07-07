/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMAGNITUDE_FILTER_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiMAGNITUDE_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiMAGNITUDE_FILTER_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"; 
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
#include "sclguiMODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiMAGNITUDE_FILTER_VIEW::sclguiMAGNITUDE_FILTER_VIEW()
{
}
sclguiMAGNITUDE_FILTER_VIEW::sclguiMAGNITUDE_FILTER_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    BuildWindow();
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
 * Update window
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMAGNITUDE_FILTER_VIEW::Update()
{
    logTrace("sclguiMAGNITUDE_FILTER_VIEW::Update()");
    
    // Get the filter list of the associated model
    vobsFILTER_LIST *filterList = _model->GetFilterList();

    // Get magnitude filter
    vobsMAGNITUDE_FILTER *magFilter =
        (vobsMAGNITUDE_FILTER *)
        filterList->GetFilter(vobsMAGNITUDE_FILTER_NAME);

    // if the magnitude filter is enabled
    if (magFilter->IsEnabled() == mcsTRUE)
    {
        // Get filter value
        mcsSTRING32 band;
        mcsFLOAT magnitude;
        mcsFLOAT magnitudeRange;
        magFilter->GetMagnitudeValue(&band, &magnitude, &magnitudeRange);

        string magnitudeValueString;
        ostringstream ostr;
        
        ostr << magnitudeRange;
        magnitudeValueString = ostr.str();
        // write them in the widget
        _magRangeTextfield->SetText(magnitudeValueString);
    }
    else
    {
        // else, the defautl value is set to zero
        _magRangeTextfield->SetText("0");
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

    return _magRangeTextfield->GetText();
}



/*
 * Protected methods
 */
/**
 * Complete Window information (title, help)
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMAGNITUDE_FILTER_VIEW::CompleteWindowInformation()
{
    logTrace("sclguiMAGNITUDE_FILTER_VIEW::CompleteWindowInformation()");

    gwtWINDOW *ownWindow = GetWindowLink();
    ownWindow->SetTitle("Maximal Magnitude Separation (mag)");
    static string windowHelp
        ("Allows to reduce the magnitude difference between the Calibrators "
         "and the Science Object.");
    ownWindow->SetHelp(windowHelp);

    return mcsSUCCESS;
}

/**
 * Build main filter view
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMAGNITUDE_FILTER_VIEW::BuildMainFilterView()
{
    logTrace("sclguiMAGNITUDE_FILTER_VIEW::BuildMainFilterView()");

    gwtWINDOW *ownWindow = GetWindowLink();

    // Create textfield
    _magRangeTextfield = new gwtTEXTFIELD();
    _magRangeTextfield->SetLabel("Maximal Magnitude Separation (mag)");
    _magRangeTextfield->SetText("0");
    
    // Add widgets in the window
    ownWindow->Add(_magRangeTextfield);
    
    return mcsSUCCESS;
}

/*
 * Private methods
 */


/*___oOo___*/
