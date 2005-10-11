/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiFILTER_LIST_MODEL.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiFILTER_LIST_MODEL class.
 */

static char *rcsId="@(#) $Id: sclguiFILTER_LIST_MODEL.cpp,v 1.1 2005-10-11 15:24:15 scetre Exp $"; 
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

#include "vobs.h"
#include "sclsvr.h"
/*
 * Local Headers 
 */
#include "sclguiFILTER_LIST_MODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiFILTER_LIST_MODEL::sclguiFILTER_LIST_MODEL()
{
    Init();
}

/**
 * Class destructor
 */
sclguiFILTER_LIST_MODEL::~sclguiFILTER_LIST_MODEL()
{
    vobsFILTER * filter;
    // Delete all filter of the list
    for (unsigned int el = 0; el < Size(); el++)
    {
        filter = GetNextFilter((mcsLOGICAL)(el==0));
        delete filter;
    }
}

/*
 * Public methods
 */
/**
 * Initialize the model
 *
 * Clear the list of the model, disable all filters exept variability and
 * multiplicity because by defautl, the soft should gave calibrators without
 * variability and multiplicity
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::Init()
{
    logTrace("sclguiFILTER_LIST_MODEL::Init()");

    // Add variability filter
    Add(&_variabilityFilter);
    SetFilterVariability(mcsTRUE);
    // Add multiplicity filter
    Add(&_multiplicityFilter);
    SetFilterMultiplicity(mcsTRUE);
    // Add magnitude filter
    Add(&_magnitudeFilter);
    // Add visibility filter
    Add(&_visibilityFilter);
    // Add distance filter
    Add(&_distanceFilter);
    // Add spectral type filter
    Add(&_spectralTypeFilter);
    // Add luminosity filter
    Add(&_luminosityFilter);
    
    return mcsSUCCESS;
}

/**
 * Reset filter list model
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
   
    // Enable only the filter by variability and visibility
    SetFilterVariability(mcsTRUE);
    SetFilterMultiplicity(mcsTRUE);
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Enabled the magnitude filter of the list
 *
 * @param band magnitude band
 * @param magValue magnitude value
 * @param magRange magnitude range
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterMagnitude(mcsSTRING32 band,
                                                    mcsFLOAT magValue,
                                                    mcsFLOAT magRange,
                                                    mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::EnableFilterMagnitude()");
    
    // Get the magnitude filter from the list
    vobsMAGNITUDE_FILTER *magFilter = 
        (vobsMAGNITUDE_FILTER *)
        GetFilter(vobsMAGNITUDE_FILTER_NAME);

    // if the set enabled the filter
    if (enable == mcsTRUE)
    {
        // Set enable the magnitude filter
        if (magFilter->Enabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set value in the magnitude filter
        if (magFilter->SetMagnitudeValue(band, magValue, magRange) ==
            mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Set disable the magnitude filter
        if (magFilter->Disabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

    }

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Enabled the distance filter of the list
 *
 * @param raRef right ascension of the science object in hms
 * @param decRef declinaison of the science object in dms
 * @param raRange right ascension range
 * @param decRange declinaison range
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterDistance(mcsSTRING32 raRef,
                                                   mcsSTRING32 decRef,
                                                   mcsFLOAT raRange,
                                                   mcsFLOAT decRange,
                                                   mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterDistance()");

    // Get the distance filter from the list
    vobsDISTANCE_FILTER *distanceFilter = 
        (vobsDISTANCE_FILTER *) GetFilter(vobsDISTANCE_FILTER_NAME);

    // if the set enabled the filter
    if (enable == mcsTRUE)
    {
        // Set enable the distance filter
        if (distanceFilter->Enabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set value in the distance filter
        if (distanceFilter->
            SetDistanceValue(raRef, decRef, raRange, decRange) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Set disable the magnitude filter
        if (distanceFilter->Disabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enabled the visibility filter of the list
 *
 * @param visMax the maximal accuracy
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterVisibility(mcsFLOAT visMax,
                                                     mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterVisibility()");
    
    // Get the visibility filter from the list
    sclsvrVISIBILITY_FILTER *visibilityFilter = 
        (sclsvrVISIBILITY_FILTER *)GetFilter(sclsvrVISIBILITY_FILTER_NAME);
   
    // if the set enabled the filter
    if (enable == mcsTRUE)
    {
// Set enable the visibility filter
    if (visibilityFilter->Enabled() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set value in the visibility filter
    if (visibilityFilter->
        SetVisibilityValue(visMax) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    }
    else
    {
        visibilityFilter->Disabled();
    }
   
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Enabled the spectral type filter of the list
 *
 * tempClassList a list of temperature class
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::
SetFilterTemperatureClass(std::list<char *> tempClassList,
                          mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterSpectralType()");

    // Get the spectral type filter from the list
    vobsSPECTRAL_TYPE_FILTER *spectralTypeFilter = 
        (vobsSPECTRAL_TYPE_FILTER *)GetFilter(vobsSPECTRAL_TYPE_FILTER_NAME);

    if (enable == mcsTRUE)
    {
        // Set enable the spectral type filter
        if (spectralTypeFilter->Enabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set value in the spectral type filter
        if (spectralTypeFilter->SetSpectralType(tempClassList) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Set disable the spectral type filter
        if (spectralTypeFilter->Disabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

    }
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }


    return mcsSUCCESS;
}

/**
 * Enabled the luminosity filter of the list
 *
 * @param lumClassList a list of luminosity class
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
        (vobsLUMINOSITY_FILTER *)GetFilter(vobsLUMINOSITY_FILTER_NAME);

    if (enable == mcsTRUE)
    {
        // Set enable the luminosity filter
        if (luminosityFilter->Enabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Set value in the luminosity filter
        if (luminosityFilter->SetLuminosity(lumClassList) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Set disable the luminosity filter
        if (luminosityFilter->Disabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enabled the variability filter of the list
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterVariability(mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterVariability()");

    // Get the variability filter from the list
    vobsVARIABILITY_FILTER *variabilityFilter = 
        (vobsVARIABILITY_FILTER *)GetFilter(vobsVARIABILITY_FILTER_NAME);

    if (enable == mcsTRUE)
    {
        // Set enable the variability filter
        if (variabilityFilter->Enabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Set disable the variability filter
        if (variabilityFilter->Disabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

    }
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Enabled the multiplicity filter of the list
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::SetFilterMultiplicity(mcsLOGICAL enable)
{
    logTrace("sclguiFILTER_LIST_MODEL::SetFilterVariability()");

    // Get the multiplicity filter from the list
    vobsMULTIPLICITY_FILTER *multiplicityFilter = 
        (vobsMULTIPLICITY_FILTER *)GetFilter(vobsMULTIPLICITY_FILTER_NAME);

    if (enable == mcsTRUE)
    {
        // Set enable the multiplicity filter
        if (multiplicityFilter->Enabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    else
    {
        // Set disable the multiplicity filter
        if (multiplicityFilter->Disabled() == mcsFAILURE)
        {
            return mcsFAILURE;
        } 
    }

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Disable the magnitude filter of the list
 *
 * @param name the name of the filter to disable
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiFILTER_LIST_MODEL::DisableFilter(mcsSTRING32 name)
{
    logTrace("sclguiFILTER_LIST_MODEL::DisableFilter()");

    // Get the filter from the list
    vobsFILTER *filter = GetFilter(name);
    
    // Set disable the filter
    if (filter->Disabled() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Notify attached views
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
