/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiLUMINOSITY_FILTER_VIEW class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiLUMINOSITY_FILTER_VIEW.cpp,v 1.6 2006-03-03 15:28:17 scetre Exp $"; 


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
#include "sclguiLUMINOSITY_FILTER_VIEW.h"
#include "sclguiPrivate.h"


/**
 * Class constructor
 */
sclguiLUMINOSITY_FILTER_VIEW::sclguiLUMINOSITY_FILTER_VIEW():sclguiFILTER_VIEW()
{
    // Prepare widgets 
    _lumCheckboxI.SetLabel("I");
    _lumCheckboxII.SetLabel("II");
    _lumCheckboxIII.SetLabel("III");
    _lumCheckboxIV.SetLabel("IV");
    _lumCheckboxV.SetLabel("V");
    _lumCheckboxVI.SetLabel("VI");
    
    // Add widget in widget map 
    Add(&_lumCheckboxI);
    Add(&_lumCheckboxII);
    Add(&_lumCheckboxIII);
    Add(&_lumCheckboxIV);
    Add(&_lumCheckboxV);
    Add(&_lumCheckboxVI);
    Add(&_applyFilterButton);
}

/**
 * Class destructor
 */
sclguiLUMINOSITY_FILTER_VIEW::~sclguiLUMINOSITY_FILTER_VIEW()
{
}


/*
 * Public methods
 */
/**
 * Update method.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiLUMINOSITY_FILTER_VIEW::Update()
{
    logTrace("sclguiLUMINOSITY_FILTER_VIEW::Update()");

    // Get luminosity filter
    vobsLUMINOSITY_FILTER * lumfilter =
        (vobsLUMINOSITY_FILTER *)
        _filterListModel->GetFilter("Luminosity Filter");

    // If the filter is enable
    if (lumfilter->IsEnabled() == mcsTRUE)
    {
        // Get the list of enable luminosity
        std::list<char *> * lumClass = lumfilter->GetLuminosity();

        // Check if lum class I are enable at this time
        if (IsLuminosityDisabled(*lumClass, "I") == mcsTRUE)
        {
            _lumCheckboxI.SetValue(mcsTRUE);           
        }
        else
        {
            _lumCheckboxI.SetValue(mcsFALSE);
        }

        // Check if lum class II are enable at this time
        if (IsLuminosityDisabled(*lumClass, "II") == mcsTRUE)
        {
            _lumCheckboxII.SetValue(mcsTRUE);
        }
        else
        {
            _lumCheckboxII.SetValue(mcsFALSE);            
        }

        // Check if lum class III are enable at this time
        if (IsLuminosityDisabled(*lumClass, "III") == mcsTRUE)
        {
            _lumCheckboxIII.SetValue(mcsTRUE);
        }
        else
        {
            _lumCheckboxIII.SetValue(mcsFALSE);            
        }

        // Check if lum class IV are enable at this time
        if (IsLuminosityDisabled(*lumClass, "IV") == mcsTRUE)
        {
            _lumCheckboxIV.SetValue(mcsTRUE);
        }
        else
        {
            _lumCheckboxIV.SetValue(mcsFALSE);            
        }

        // Check if lum class V are enable at this time        
        if (IsLuminosityDisabled(*lumClass, "V") == mcsTRUE)
        {
            _lumCheckboxV.SetValue(mcsTRUE);
        }
        else
        {
            _lumCheckboxV.SetValue(mcsFALSE);
        }

        // Check if lum class VI are enable at this time
        if (IsLuminosityDisabled(*lumClass, "VI") == mcsTRUE)
        {
            _lumCheckboxVI.SetValue(mcsTRUE);
        }
        else
        {
            _lumCheckboxVI.SetValue(mcsFALSE);
        }
    }
    // If the filter is disable, fix all value to disable
    else
    {
        _lumCheckboxI.SetValue(mcsTRUE);
        _lumCheckboxII.SetValue(mcsTRUE);
        _lumCheckboxIII.SetValue(mcsTRUE);
        _lumCheckboxIV.SetValue(mcsTRUE);
        _lumCheckboxV.SetValue(mcsTRUE);
        _lumCheckboxVI.SetValue(mcsTRUE);
    }
    
    return mcsSUCCESS;
}

/**
 * Get luminosity class.
 *
 * @param lumClass luminosity class list to return
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT
sclguiLUMINOSITY_FILTER_VIEW::GetLuminosityClass(std::list<char*> *lumClass)
{
    logTrace("sclguiLUMINOSITY_FILTER_VIEW::GetLuminosityClass()");

    // Check if lum class I is activated
    if (_lumCheckboxI.GetValue() == mcsTRUE)
    {
        lumClass->push_back("I");
    }
    // Check if lum class II is activated
    if (_lumCheckboxII.GetValue() == mcsTRUE)
    {
        lumClass->push_back("II");
    }
    // Check if lum class III is activated
    if (_lumCheckboxIII.GetValue() == mcsTRUE)
    {
        lumClass->push_back("III");
    }
    // Check if lum class IV is activated
    if (_lumCheckboxIV.GetValue() == mcsTRUE)
    {
        lumClass->push_back("IV");
    }
    // Check if lum class V is activated
    if (_lumCheckboxV.GetValue() == mcsTRUE)
    {
        lumClass->push_back("V");
    }
    // Check if lum class VI is activated
    if (_lumCheckboxVI.GetValue() == mcsTRUE)
    {
        lumClass->push_back("VI");
    }

    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */
/**
 * Return wether the given luminosity class is enabled or not.
 *
 * @param luminosityList luminosity class list
 * @param luminosityClass luminosity class
 * 
 * @return mcsTRUE if the luminosity class is disabled, mcsFALSE otherwise.
 */
mcsLOGICAL sclguiLUMINOSITY_FILTER_VIEW::IsLuminosityDisabled(
                                            std::list<char *> luminosityList,
                                            char *luminosityClass)
{
    logTrace("sclguiLUMINOSITY_FILTER_VIEW::IsLuminosityDisabled()");

    // Check all element of the luminosity class list given as parameter
    std::list<char *>::iterator luminosityListIterator;
    for (luminosityListIterator = luminosityList.begin();
         luminosityListIterator != luminosityList.end();
         luminosityListIterator++)
    {
        // if one of this elements not the same as the luminosity class gave as
        // parameter, return true
        if (strcmp(*luminosityListIterator, luminosityClass) == 0)
        {
            return mcsTRUE;
        }
    }    
    // if the luminosity class has not been found, return false
    return mcsFALSE;
}


/*___oOo___*/
