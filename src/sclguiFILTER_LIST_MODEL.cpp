/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiFILTER_LIST_MODEL class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiFILTER_LIST_MODEL.cpp,v 1.8 2006-03-03 15:28:17 scetre Exp $"; 


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
 * SCALIB Headers 
 */
#include "vobs.h"
#include "sclsvr.h"


/*
 * Local Headers 
 */
#include "sclguiFILTER_LIST_MODEL.h"
#include "sclguiPrivate.h"


/**
 * Class constructor.
 */
sclguiFILTER_LIST_MODEL::sclguiFILTER_LIST_MODEL(const char* filterId):
vobsFILTER_LIST(filterId),
_variabilityFilter("Variability Filter"),
    _multiplicityFilter("Multiplicity Filter"),
    _magnitudeFilter("Magnitude Filter"),
    _visibilityFilter("Visibility Filter"),
    _distanceFilter("Distance Filter"),
    _spectralTypeFilter("Spectral type Filter"),
    _luminosityFilter("Luminosity Filter")
{
    Init();
}

/**
 * Class destructor.
 */
sclguiFILTER_LIST_MODEL::~sclguiFILTER_LIST_MODEL()
{
    /*vobsFILTER *filter;
    // Delete all filter of the list
    for (unsigned int el = 0; el < Size(); el++)
    {
        filter = GetNextFilter((mcsLOGICAL)(el==0));
        delete filter;
    }*/
}


/*
 * Public methods
 */
/**
 * Initialize the model.
 *
 * Clear the list of the model, disable all filters exept variability and
 * multiplicity (enabled by default because the soft should gave calibrators
 * without variability and multiplicity).
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::Init()
{
    logTrace("sclguiFILTER_LIST_MODEL::Init()");

    // Add variability filter
    Add(&_variabilityFilter, "Variability Filter");
    SetFilterVariability(mcsTRUE);

    // Add multiplicity filter
    Add(&_multiplicityFilter, "Multiplicity Filter");
    SetFilterMultiplicity(mcsTRUE);

    // Add magnitude filter
    Add(&_magnitudeFilter, "Magnitude Filter");

    // Add visibility filter
    Add(&_visibilityFilter, "Visibility Filter");

    // Add distance filter
    Add(&_distanceFilter, "Distance Filter");

    // Add spectral type filter
    Add(&_spectralTypeFilter, "Spectral Type Filter");

    // Add luminosity filter
    Add(&_luminosityFilter, "Luminosity Filter");
    
    return mcsSUCCESS;
}

/**
 * Reset the filter list.
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::ResetFilters(void)
{
    logTrace("sclguiFILTER_LIST_MODEL::ResetFilters()");
    
    // Reset all filters of the list
    if (Reset() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
   
    // Enable the ddefault filters (variability and visibility)
    SetFilterVariability(mcsTRUE);
    SetFilterMultiplicity(mcsTRUE);
    
    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Enable or disable the magnitude filter.
 *
 * @param band magnitude band to be filtered
 * @param value magnitude value to be filtered
 * @param range magnitude range to be filtered
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterMagnitude(mcsSTRING32  band,
                                                          mcsFLOAT     value,
                                                          mcsFLOAT     range,
                                                          mcsLOGICAL   enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::EnableFilterMagnitude()");
    
    // Get the magnitude filter from the list
    vobsMAGNITUDE_FILTER *magFilter =
        (vobsMAGNITUDE_FILTER *)GetFilter("Magnitude Filter");

    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (magFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set the filter parameters
        if (magFilter->SetMagnitudeValue(band, value, range) ==
            mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (magFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Enable or disable the distance filter.
 *
 * @param raValue right ascension of the science object in hms
 * @param decValue declinaison of the science object in dms
 * @param raRange right ascension range
 * @param decRange declinaison range
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterDistance(mcsSTRING32  raValue,
                                                         mcsSTRING32  decValue,
                                                         mcsFLOAT     raRange,
                                                         mcsFLOAT     decRange,
                                                         mcsLOGICAL   enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterDistance()");

    // Get the distance filter from the list
    vobsDISTANCE_FILTER *distanceFilter = 
        (vobsDISTANCE_FILTER *)GetFilter("Distance Filter");

    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (distanceFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set the filter parameters
        if (distanceFilter->SetDistanceValue(raValue, decValue, raRange,
                                             decRange) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (distanceFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enable or disable the visibility filter.
 *
 * @param visMax the maximal accuracy
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterVisibility(mcsFLOAT    visMax,
                                                           mcsLOGICAL  enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterVisibility()");
    
    // Get the visibility filter from the list
    sclsvrVISIBILITY_FILTER *visibilityFilter = 
        (sclsvrVISIBILITY_FILTER *)GetFilter("Visibility Filter");
   
    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (visibilityFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    
        // Set the filter parameter
        if (visibilityFilter->SetVisibilityValue(visMax) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (visibilityFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
   
    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Enable or disable the spectral type filter.
 *
 * @param tempClassList a list of temperature class
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::
SetFilterTemperatureClass(std::list<char *> tempClassList,mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterSpectralType()");

    // Get the spectral type filter from the list
    vobsSPECTRAL_TYPE_FILTER *spectralTypeFilter = 
        (vobsSPECTRAL_TYPE_FILTER *)GetFilter("Spectral Type Filter");

    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (spectralTypeFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set the filter parameter
        if (spectralTypeFilter->SetSpectralType(tempClassList) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (spectralTypeFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enable or disable the luminosity filter.
 *
 * @param lumClassList a list of luminosity class
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::
SetFilterLuminosityClass(std::list<char *> lumClassList,
                         mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterLuminosity()");

    // Get the luminosity filter from the list
    vobsLUMINOSITY_FILTER *luminosityFilter = 
        (vobsLUMINOSITY_FILTER *)GetFilter("Luminosity Filter");

    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (luminosityFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set the filter parameter
        if (luminosityFilter->SetLuminosity(lumClassList) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (luminosityFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enable or disable the variability filter.
 *
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterVariability(mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterVariability()");

    // Get the variability filter from the list
    vobsVARIABILITY_FILTER *variabilityFilter = 
        (vobsVARIABILITY_FILTER *)GetFilter("Variability Filter");

    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (variabilityFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (variabilityFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enable or disable the multiplicity filter.
 *
 * @param enable logical flag specifying wether the filter should be enabled or
 * disabled
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterMultiplicity(mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterVariability()");

    // Get the multiplicity filter from the list
    vobsMULTIPLICITY_FILTER *multiplicityFilter = 
        (vobsMULTIPLICITY_FILTER *)GetFilter("Multiplicity Filter");

    // if the filter must be enabled
    if (enable == mcsTRUE)
    {
        // Enable it
        if (multiplicityFilter->Enable() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Disable the filter
        if (multiplicityFilter->Disable() == mcsFAILURE)
        {
            return mcsFAILURE;
        } 
    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Disable the filter of the given name
 *
 * @param name the name of the filter to disable
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::DisableFilter(mcsSTRING32 name)
{
    logTrace("sclguiFILTER_LIST_MODEL::DisableFilter()");

    // Get the filter from the list by its name
    vobsFILTER *filter = GetFilter(name);
    
    // Disable the filter
    if (filter->Disable() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Notify any attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
