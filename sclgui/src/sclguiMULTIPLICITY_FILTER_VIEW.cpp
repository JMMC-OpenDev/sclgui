/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMULTIPLICITY_FILTER_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiMULTIPLICITY_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiMULTIPLICITY_FILTER_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"; 
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
#include "sclguiMULTIPLICITY_FILTER_VIEW.h"
#include "sclguiMODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiMULTIPLICITY_FILTER_VIEW::sclguiMULTIPLICITY_FILTER_VIEW()
{
}
sclguiMULTIPLICITY_FILTER_VIEW::
sclguiMULTIPLICITY_FILTER_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    BuildWindow();
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
    
    _multiplicityChoice->Remove("Authorised");
    _multiplicityChoice->Remove("Forbidden");
    
    // Prepare widgets 
    // If multiplicity are authorized, add first the name Authorized in the
    // widget in ordr to show to the user the state of the filter
    if (IsMultiplicityAuthorized() == mcsTRUE)
    {
        _multiplicityChoice->Add("Authorised");
        _multiplicityChoice->Add("Forbidden");
    }
    // else add Forbidden first
    else
    {
        _multiplicityChoice->Add("Forbidden");
        _multiplicityChoice->Add("Authorised");
    }
   
    return mcsSUCCESS;
}

/**
 * Say if the user choice authorized or not the multiplicity
 *
 * @return mcsTRUE if multiplicity are authorized otherwise mcsFALSE is
 * returned
 */
mcsLOGICAL sclguiMULTIPLICITY_FILTER_VIEW::IsMultiplicityAuthorized()
{
    logTrace("sclguisclguiMULTIPLICITY_FILTER_VIEW::IsMultiplicityAuthorized()");
    
    vobsFILTER_LIST *list = _model->GetFilterList(); 

    vobsFILTER *filter =
        list->GetFilter(vobsMULTIPLICITY_FILTER_NAME);

    if (filter->IsEnabled() == mcsFALSE)
    {
        return mcsTRUE;
    }
    
    return mcsFALSE;
}

/**
 * Get the choice in the gwtCHOICE
 *
 * @return string choice 
 */
string sclguiMULTIPLICITY_FILTER_VIEW::GetChoice()
{
    logTrace("sclguiMULTIPLICITY_FILTER_VIEW::GetChoice()");
    
    return _multiplicityChoice->GetSelectedItemValue();
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
mcsCOMPL_STAT sclguiMULTIPLICITY_FILTER_VIEW::CompleteWindowInformation()
{
    logTrace("sclguiMULTIPLICITY_FILTER_VIEW::CompleteWindowInformation()");

    gwtWINDOW *ownWindow = GetWindowLink();
    ownWindow->SetTitle("Multiplicity");
    static string windowHelp
        ("Allows to select stars using the multiplicity flag from All-sky "
         "Compiled Catalogue of 2.5 million stars (Kharchenko 2001).");
    ownWindow->SetHelp(windowHelp);

    return mcsSUCCESS;
}

/**
 * Build main filter view
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMULTIPLICITY_FILTER_VIEW::BuildMainFilterView()
{
    logTrace("sclguiMULTIPLICITY_FILTER_VIEW::BuildMainFilterView()");
    
    gwtWINDOW *ownWindow = GetWindowLink();
    
    // Prepare widgets 
    _multiplicityChoice = new gwtCHOICE();
    _multiplicityChoice->SetLabel("Multiplicity");    
    // Prepare widgets 
    if (IsMultiplicityAuthorized() == mcsTRUE)
    {
        _multiplicityChoice->Add("Authorised");
        _multiplicityChoice->Add("Forbidden");
    }
    else
    {
        _multiplicityChoice->Add("Forbidden");
        _multiplicityChoice->Add("Authorised");
    }

    // Add widgets in the window
    ownWindow->Add(_multiplicityChoice);
    
    return mcsSUCCESS;
}

/*
 * Private methods
 */


/*___oOo___*/
