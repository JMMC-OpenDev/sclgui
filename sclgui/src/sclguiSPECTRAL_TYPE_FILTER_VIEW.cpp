/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiSPECTRAL_TYPE_FILTER_VIEW.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiSPTYPE_FILTER_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiSPECTRAL_TYPE_FILTER_VIEW.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"; 
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
#include "sclguiSPECTRAL_TYPE_FILTER_VIEW.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiSPTYPE_FILTER_VIEW::
sclguiSPTYPE_FILTER_VIEW() : sclguiFILTER_VIEW()
{
    // Prepare widgets 
    _spectralTypeCheckboxO.SetLabel("O");
    _spectralTypeCheckboxB.SetLabel("B");
    _spectralTypeCheckboxA.SetLabel("A");
    _spectralTypeCheckboxF.SetLabel("F");
    _spectralTypeCheckboxG.SetLabel("G");
    _spectralTypeCheckboxK.SetLabel("K");
    _spectralTypeCheckboxM.SetLabel("M");

    // Add widget in widget map 
    Add(&_spectralTypeCheckboxO);
    Add(&_spectralTypeCheckboxB);
    Add(&_spectralTypeCheckboxA);
    Add(&_spectralTypeCheckboxF);
    Add(&_spectralTypeCheckboxG);
    Add(&_spectralTypeCheckboxK);
    Add(&_spectralTypeCheckboxM);
    Add(&_applyFilterButton);

}

/**
 * Class destructor
 */
sclguiSPTYPE_FILTER_VIEW::~sclguiSPTYPE_FILTER_VIEW()
{
}

/*
 * Public methods
 */
/**
 * Update spectral type filter view
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiSPTYPE_FILTER_VIEW::Update()
{
    logTrace("sclguiSPTYPE_FILTER_VIEW::Update()");

    // get spectral type  filter
    vobsSPECTRAL_TYPE_FILTER * spTypeFilter =
        (vobsSPECTRAL_TYPE_FILTER *)
        _filterList->GetFilter(vobsSPECTRAL_TYPE_FILTER_NAME);

    // if the filter is enable
    if (spTypeFilter->IsEnabled() == mcsTRUE)
    {
        // get the list of enable spectral type
        std::list<char *> * tempClass= spTypeFilter->GetSpectralType();
       // Check if temp class O is enable at this time
       if (IsDisableTemperature(*tempClass, "O") == mcsTRUE)
       {
           _spectralTypeCheckboxO.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxO.SetValue(mcsFALSE);
       }
       // Check if temp class B is enable at this time
       if (IsDisableTemperature(*tempClass, "B") == mcsTRUE)
       {
           _spectralTypeCheckboxB.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxB.SetValue(mcsFALSE);
       }
       // Check if temp class A is enable at this time
       if (IsDisableTemperature(*tempClass, "A") == mcsTRUE)
       {
           _spectralTypeCheckboxA.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxA.SetValue(mcsFALSE);
       }
       // Check if temp class F is enable at this time
       if (IsDisableTemperature(*tempClass, "F") == mcsTRUE)
       {
           _spectralTypeCheckboxF.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxF.SetValue(mcsFALSE);
       }
       // Check if temp class G is enable at this time
       if (IsDisableTemperature(*tempClass, "G") == mcsTRUE)
       {
           _spectralTypeCheckboxG.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxG.SetValue(mcsFALSE);
       }
       // Check if temp class K is enable at this time
       if (IsDisableTemperature(*tempClass, "K") == mcsTRUE)
       {
           _spectralTypeCheckboxK.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxK.SetValue(mcsFALSE);
       }
       // Check if temp class M is enable at this time
       if (IsDisableTemperature(*tempClass, "M") == mcsTRUE)
       {
           _spectralTypeCheckboxM.SetValue(mcsTRUE);
       }
       else
       {
           _spectralTypeCheckboxM.SetValue(mcsFALSE);
       }
    }
    // if the filter is disable, that's meaning that all spectral type are
    // wanted
    else
    {
        _spectralTypeCheckboxO.SetValue(mcsTRUE);
        _spectralTypeCheckboxB.SetValue(mcsTRUE);
        _spectralTypeCheckboxA.SetValue(mcsTRUE);
        _spectralTypeCheckboxF.SetValue(mcsTRUE);
        _spectralTypeCheckboxG.SetValue(mcsTRUE);
        _spectralTypeCheckboxK.SetValue(mcsTRUE);
        _spectralTypeCheckboxM.SetValue(mcsTRUE);
    }

    return mcsSUCCESS;
}


/**
 * Get temperature class
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiSPTYPE_FILTER_VIEW::
GetTemperatureClass(std::list<char*> *tempClass)
{
    logTrace("sclguiSPTYPE_FILTER_VIEW::GetTemperatureClass()");

    // Ckeck if temperature O is activated
    if (_spectralTypeCheckboxO.GetValue() == mcsTRUE)
    {
        tempClass->push_back("O");
    }
    // Ckeck if temperature B is activated
    if (_spectralTypeCheckboxB.GetValue() == mcsTRUE)
    {
        tempClass->push_back("B");
    }
    // Ckeck if temperature A is activated
    if (_spectralTypeCheckboxA.GetValue() == mcsTRUE)
    {
        tempClass->push_back("A");
    }
    // Ckeck if temperature F is activated
    if (_spectralTypeCheckboxF.GetValue() == mcsTRUE)
    {
        tempClass->push_back("F");
    }
    // Ckeck if temperature G is activated
    if (_spectralTypeCheckboxG.GetValue() == mcsTRUE)
    {
        tempClass->push_back("G");
    }
    // Ckeck if temperature K is activated
    if (_spectralTypeCheckboxK.GetValue() == mcsTRUE)
    {
        tempClass->push_back("K");
    }
    // Ckeck if temperature M is activated
    if (_spectralTypeCheckboxM.GetValue() == mcsTRUE)
    {
        tempClass->push_back("M");
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
 * Say if spectralType is enable
 *
 * @param temperatureList temperature class list
 * @param temperatureClass temperature class
 *
 * @return mcsFALSE if the temperature class is not in the list, otherwise
 * mcsTRUE is returned
 */
mcsLOGICAL sclguiSPTYPE_FILTER_VIEW::
IsDisableTemperature(std::list<char *> temperatureList,
                     char *temperatureClass)
{
    logTrace("sclguiSPTYPE_FILTER_VIEW::IsDisableTemperature()");
    
    // Check all element of the temperature class list gave as parameters
    std::list<char *>::iterator temperatureListIterator;
    for (temperatureListIterator = temperatureList.begin();
         temperatureListIterator != temperatureList.end();
         temperatureListIterator++)
    {
        // if one of this elements not the same as the temperature class gave as
        // parameter, return true
        if (strcmp(*temperatureListIterator, temperatureClass) == 0)
        {
            return mcsTRUE;
        }
    }
    // if the temperature class has not been found, return false
    return mcsFALSE;
}


/*___oOo___*/
