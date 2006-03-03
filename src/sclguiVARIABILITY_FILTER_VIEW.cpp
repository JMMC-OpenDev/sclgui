/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiVARIABILITY_FILTER_VIEW.cpp,v 1.8 2006-03-03 15:28:17 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2005/11/30 10:35:58  scetre
 * Updated with new filter
 *
 * Revision 1.6  2005/11/29 10:39:51  gzins
 * Chnaged vobsBASE_FILTER to vobsFILTER
 *
 * Revision 1.5  2005/11/24 15:16:14  scetre
 * Used vobsBASE_FILTER instead of vobsFILTER
 *
 * Revision 1.4  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.3  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiVARIABILITY_FILTER_VIEW class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiVARIABILITY_FILTER_VIEW.cpp,v 1.8 2006-03-03 15:28:17 scetre Exp $"; 


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
#include "sclguiVARIABILITY_FILTER_VIEW.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"


/**
 * Class constructor
 */
sclguiVARIABILITY_FILTER_VIEW::
    sclguiVARIABILITY_FILTER_VIEW() : sclguiFILTER_VIEW()
{
    // Prepare widgets
    _variabilityChoice.SetLabel("Variability"); 

    // Add widget in widget map
    Add(&_variabilityChoice);
    Add(&_applyFilterButton);  
}

/**
 * Class destructor
 */
sclguiVARIABILITY_FILTER_VIEW::~sclguiVARIABILITY_FILTER_VIEW()
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
mcsCOMPL_STAT sclguiVARIABILITY_FILTER_VIEW::Update()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::Update()");
    
    _variabilityChoice.Remove("Authorised");
    _variabilityChoice.Remove("Forbidden");
    
    // Prepare widgets 
    // If variability are authorized, add first the name Authorized in the
    // widget in ordr to show to the user the state of the filter
    if (IsVariabilityAuthorized() == mcsTRUE)
    {
        _variabilityChoice.Add("Authorised");
        _variabilityChoice.Add("Forbidden");
    }
    // else add Forbidden first
    else
    {
        _variabilityChoice.Add("Forbidden");
        _variabilityChoice.Add("Authorised");
    }
   
    return mcsSUCCESS;
}

/**
 * Get the choice in the gwtCHOICE
 * 
 * @return string choice 
 */
string sclguiVARIABILITY_FILTER_VIEW::GetChoice()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::GetChoice()");
    
    return _variabilityChoice.GetSelectedItemValue();
}


/*
 * Protected methods
 */


/*
 * Private methods
 */
/**
 * Return wether the user authorized the variability or not.
 *
 * @return mcsTRUE if variability are authorized otherwise mcsFALSE is
 * returned
 */
mcsLOGICAL sclguiVARIABILITY_FILTER_VIEW::IsVariabilityAuthorized()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::IsVariabilityAuthorized()");
    
    vobsFILTER *filter = _filterListModel->GetFilter("Variability Filter");
    
    if (filter->IsEnabled() == mcsFALSE)
    {
        return mcsTRUE;
    }
    
    return mcsFALSE;
}

/*___oOo___*/
