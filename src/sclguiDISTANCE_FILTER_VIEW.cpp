/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiDISTANCE_FILTER_VIEW.cpp,v 1.3 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiDISTANCE_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiDISTANCE_FILTER_VIEW.cpp,v 1.3 2005-10-11 15:24:15 scetre Exp $"; 
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
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiDISTANCE_FILTER_VIEW::sclguiDISTANCE_FILTER_VIEW() :
sclguiFILTER_VIEW()
{
    // Prepare widgets 
    // Create textfield for right ascension
    _raRangeTextfield.SetLabel("Maximal RA Separation (mn)");
    _raRangeTextfield.SetText("0");

    // Create textfield for declinaison
    _decRangeTextfield.SetLabel("Maximal DEC Separation (degree)");
    _decRangeTextfield.SetText("0");

    // Add widget in widget map
    Add(&_raRangeTextfield);
    Add(&_decRangeTextfield);
    Add(&_applyFilterButton);

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
 * Update method
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclguiDISTANCE_FILTER_VIEW::Update()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::Update(");

    // Get distance filter
    vobsDISTANCE_FILTER *distFilter =
        (vobsDISTANCE_FILTER *)
        _filterList->GetFilter(vobsDISTANCE_FILTER_NAME);
        
    // If distance filter is enable
    if (distFilter->IsEnabled() == mcsTRUE)
    {
        // Get value of the filter
        mcsSTRING32 raRef;
        mcsSTRING32 decRef;
        mcsFLOAT raRange;
        mcsFLOAT decRange;
        distFilter->GetDistanceValue(&raRef, &decRef, &raRange, &decRange);

        string raValueString;
        string decValueString;
        ostringstream ostrRa;
        ostringstream ostrDec;
        
        ostrRa << raRange;
        ostrDec << decRange;
        raValueString = ostrRa.str();
        decValueString = ostrDec.str();
        
        _raRangeTextfield.SetText(raValueString);
        _decRangeTextfield.SetText(decValueString);
    }
    // if distance filter is disable set value to zero
    else
    {
        _raRangeTextfield.SetText("0");
        _decRangeTextfield.SetText("0");
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

    return _raRangeTextfield.GetText();
}

/**
 * Get declinaison range 
 *
 * @return declinaison range
 */
string sclguiDISTANCE_FILTER_VIEW::GetDecRange()
{
    logTrace("sclguiDISTANCE_FILTER_VIEW::GetDecRange()");

    return _decRangeTextfield.GetText();
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
