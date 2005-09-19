/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiDISTANCE_FILTER_VIEW.cpp,v 1.2 2005-09-16 13:44:55 scetre Exp $"
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
 *  Definition of sclguiDISTANCE_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiDISTANCE_FILTER_VIEW.cpp,v 1.2 2005-09-16 13:44:55 scetre Exp $"; 
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
#include "sclguiDISTANCE_FILTER_VIEW.h"
#include "sclguiMODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiDISTANCE_FILTER_VIEW::sclguiDISTANCE_FILTER_VIEW()
{
}

sclguiDISTANCE_FILTER_VIEW::sclguiDISTANCE_FILTER_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    BuildWindow();
}

/**
 * Class destructor
 */
sclguiDISTANCE_FILTER_VIEW::~sclguiDISTANCE_FILTER_VIEW()
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
mcsCOMPL_STAT sclguiDISTANCE_FILTER_VIEW::Update()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::Update()");
    
    // Get the filter list of the associated model
    vobsFILTER_LIST *filterList = _model->GetFilterList();

    // Get distance filter
    vobsDISTANCE_FILTER *distFilter =
        (vobsDISTANCE_FILTER *)
        filterList->GetFilter(vobsDISTANCE_FILTER_NAME);
        
    // If distance filter is enable
    if (distFilter->IsEnabled() == mcsTRUE)
    {
        // Get value of the filter
        mcsSTRING32 raRef;
        mcsSTRING32 decRef;
        mcsFLOAT raRange;
        mcsFLOAT decRange;
        distFilter->GetDistanceValue(&raRef, &decRef, &raRange, &decRange);
        
        // reconvert ra from degree to minute
        raRange = raRange*60/15;
        
        string raValueString;
        string decValueString;
        ostringstream ostrRa;
        ostringstream ostrDec;
        
        ostrRa << raRange;
        ostrDec << decRange;
        raValueString = ostrRa.str();
        decValueString = ostrDec.str();
        
        _raRangeTextfield->SetText(raValueString);
        _decRangeTextfield->SetText(decValueString);
    }
    // if distance filter is disable set value to zero
    else
    {
        _raRangeTextfield->SetText("0");
        _decRangeTextfield->SetText("0");
    }

    return mcsSUCCESS;    
}

/**
 * Get rigth ascension range 
 *
 * @return rigth ascension range
 */
string sclguiDISTANCE_FILTER_VIEW::GetRaRange()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::GetRaRange()");

    return _raRangeTextfield->GetText();
}

/**
 * Get declinaison range 
 *
 * @return declinaison range
 */
string sclguiDISTANCE_FILTER_VIEW::GetDecRange()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::GetDecRange()");

    return _decRangeTextfield->GetText();
}

/*
 * Protected methods
 */
/**
 * Complete Window information (title, help)
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiDISTANCE_FILTER_VIEW::CompleteWindowInformation()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::CompleteWindowInformation()");

    // Get link on his own window
    gwtWINDOW *ownWindow = GetWindowLink();
    ownWindow->SetTitle("Maximal distance in RA and DEC");
    static string windowHelp
        ("Allows to reduce the size of the field around the Science Object by "
         "the choice of the maximum distance of the Calibrators to the Science "
         "Object in R.A. and DEC.");
    ownWindow->SetHelp(windowHelp);

    return mcsSUCCESS;
}

/**
 * Build main filter view
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiDISTANCE_FILTER_VIEW::BuildMainFilterView()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::BuildMainFilterView()");

    gwtWINDOW *ownWindow = GetWindowLink();

    // Prepare widgets 
    // Create textfield for right ascension
    _raRangeTextfield = new gwtTEXTFIELD();
    _raRangeTextfield->SetLabel("Maximal RA Separation (mn)");
    _raRangeTextfield->SetText("0");

    // Create textfield for declinaison
    _decRangeTextfield = new gwtTEXTFIELD();
    _decRangeTextfield->SetLabel("Maximal DEC Separation (degree)");
    _decRangeTextfield->SetText("0");
    
    // Add widgets in the window
    ownWindow->Add(_raRangeTextfield);
    ownWindow->Add(_decRangeTextfield);
    
    return mcsSUCCESS;
}


/*
 * Private methods
 */


/*___oOo___*/
