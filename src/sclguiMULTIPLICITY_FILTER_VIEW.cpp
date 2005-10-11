/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMULTIPLICITY_FILTER_VIEW.cpp,v 1.3 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiMULTIPLICITY_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiMULTIPLICITY_FILTER_VIEW.cpp,v 1.3 2005-10-11 15:24:15 scetre Exp $"; 
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
#include "sclguiMULTIPLICITY_FILTER_VIEW.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

/**
 * Class constructor
 */
sclguiMULTIPLICITY_FILTER_VIEW::
sclguiMULTIPLICITY_FILTER_VIEW() : sclguiFILTER_VIEW()
{
    // Prepare widgets
    _multiplicityChoice.SetLabel("Multiplicity"); 

    // Add widget in widget map
    Add(&_multiplicityChoice);
    Add(&_applyFilterButton);

}

/**
 * Class destructor
 */
sclguiMULTIPLICITY_FILTER_VIEW::~sclguiMULTIPLICITY_FILTER_VIEW()
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
mcsCOMPL_STAT sclguiMULTIPLICITY_FILTER_VIEW::Update()
{
    logTrace("sclguiMULTIPLICITY_FILTER_VIEW::Update()");
    
    _multiplicityChoice.Remove("Authorised");
    _multiplicityChoice.Remove("Forbidden");
    
    // Prepare widgets 
    // If multiplicity are authorized, add first the name Authorized in the
    // widget in ordr to show to the user the state of the filter
    if (IsMultiplicityAuthorized() == mcsTRUE)
    {
        _multiplicityChoice.Add("Authorised");
        _multiplicityChoice.Add("Forbidden");
    }
    // else add Forbidden first
    else
    {
        _multiplicityChoice.Add("Forbidden");
        _multiplicityChoice.Add("Authorised");
    }
   
    return mcsSUCCESS;
}

/**
 * Get the choice in the gwtCHOICE
 * 
 * @return string choice 
 */
string sclguiMULTIPLICITY_FILTER_VIEW::GetChoice()
{
    logTrace("sclguiMULTIPLICITY_FILTER_VIEW::GetChoice()");
    
    return _multiplicityChoice.GetSelectedItemValue();
}

/*
 * Protected methods
 */


/*
 * Private methods
 */
/**
 * Say if the user choice authorized or not the multiplicity
 *
 * @return mcsTRUE if multiplicity are authorized otherwise mcsFALSE is
 * returned
 */
mcsLOGICAL sclguiMULTIPLICITY_FILTER_VIEW::IsMultiplicityAuthorized()
{
    logTrace("sclguiMULTIPLICITY_FILTER_VIEW::IsMultiplicityAuthorized()");
    
    vobsFILTER *filter = _filterList->GetFilter(vobsMULTIPLICITY_FILTER_NAME);

    if (filter == NULL)
    {
        errAdd(sclguiERR_FILTER_NOT_FOUND, "multiplicity");
        return mcsFALSE;
    }

    if (filter->IsEnabled() == mcsFALSE)
    {
        return mcsTRUE;
    }
    
    return mcsFALSE;
}


/*___oOo___*/
