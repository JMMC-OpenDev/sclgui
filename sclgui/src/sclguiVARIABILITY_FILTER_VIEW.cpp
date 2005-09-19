/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiVARIABILITY_FILTER_VIEW.cpp,v 1.2 2005-09-16 13:44:01 scetre Exp $"
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
 *  Definition of sclguiVARIABILITY_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiVARIABILITY_FILTER_VIEW.cpp,v 1.2 2005-09-16 13:44:01 scetre Exp $"; 
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
#include "sclguiVARIABILITY_FILTER_VIEW.h"
#include "sclguiMODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiVARIABILITY_FILTER_VIEW::sclguiVARIABILITY_FILTER_VIEW()
{
}
sclguiVARIABILITY_FILTER_VIEW::sclguiVARIABILITY_FILTER_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    BuildWindow();
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
    
    _variabilityChoice->Remove("Authorised");
    _variabilityChoice->Remove("Forbidden");
    
    // Prepare widgets 
    // If variability are authorized, add first the name Authorized in the
    // widget in ordr to show to the user the state of the filter
    if (IsVariabilityAuthorized() == mcsTRUE)
    {
        _variabilityChoice->Add("Authorised");
        _variabilityChoice->Add("Forbidden");
    }
    // else add Forbidden first
    else
    {
        _variabilityChoice->Add("Forbidden");
        _variabilityChoice->Add("Authorised");
    }
   
    return mcsSUCCESS;
}

/**
 * Say if the user choice authorized or not the variability
 *
 * @return mcsTRUE if variability are authorized otherwise mcsFALSE is
 * returned

 */
mcsLOGICAL sclguiVARIABILITY_FILTER_VIEW::IsVariabilityAuthorized()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::IsVariabilityAuthorized()");
    
    vobsFILTER_LIST *list = _model->GetFilterList(); 

    vobsFILTER *filter = list->GetFilter(vobsVARIABILITY_FILTER_NAME);

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
string sclguiVARIABILITY_FILTER_VIEW::GetChoice()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::GetChoice()");
    
    return _variabilityChoice->GetSelectedItemValue();
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
mcsCOMPL_STAT sclguiVARIABILITY_FILTER_VIEW::CompleteWindowInformation()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::CompleteWindowInformation()");

    gwtWINDOW *ownWindow = GetWindowLink();
    ownWindow->SetTitle("Variability");
    static string windowHelp
        ("Allows to select stars using the variability flag from All-sky "
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
mcsCOMPL_STAT sclguiVARIABILITY_FILTER_VIEW::BuildMainFilterView()
{
    logTrace("sclguiVARIABILITY_FILTER_VIEW::BuildMainFilterView()");
    
    gwtWINDOW *ownWindow = GetWindowLink();
    
    // Prepare widgets 
    _variabilityChoice = new gwtCHOICE();
    _variabilityChoice->SetLabel("Variability");    
    _variabilityChoice->SetHelp("Choose if you want to authorize or to forbid the variability");    
    // Prepare widgets 
    if (IsVariabilityAuthorized() == mcsTRUE)
    {
        _variabilityChoice->Add("Authorised");
        _variabilityChoice->Add("Forbidden");
    }
    else
    {
        _variabilityChoice->Add("Forbidden");
        _variabilityChoice->Add("Authorised");
    }

    // Add widgets in the window
    ownWindow->Add(_variabilityChoice);
    
    return mcsSUCCESS;
}

/*
 * Private methods
 */


/*___oOo___*/
