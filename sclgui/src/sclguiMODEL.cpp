/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMODEL.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiMODEL class.
 */

static char *rcsId="@(#) $Id: sclguiMODEL.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"; 
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

#include "sclsvr.h"
/*
 * Local Headers 
 */
#include "sclguiMODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiMODEL::sclguiMODEL()
{
    Init();
}

/**
 * Class destructor
 */
sclguiMODEL::~sclguiMODEL()
{
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
mcsCOMPL_STAT sclguiMODEL::Init()
{
    logTrace("sclguiMODEL::Init()");

    _calibratorList.Clear();
    _deletingCalibratorList.Clear();    
    // Reinitialize the filter list
    _filterList.Reset();

    // Add variability filter
    vobsVARIABILITY_FILTER *variabilityFilter = new vobsVARIABILITY_FILTER;
    _filterList.Add(variabilityFilter);
    EnableFilterVariability();
    // Add multiplicity filter
    vobsMULTIPLICITY_FILTER *multiplicityFilter =
        new vobsMULTIPLICITY_FILTER;
    _filterList.Add(multiplicityFilter);
    EnableFilterMultiplicity();
    // Add magnitude filter
    vobsMAGNITUDE_FILTER *magnitudeFilter = new vobsMAGNITUDE_FILTER;
    _filterList.Add(magnitudeFilter);
    // Add visibility filter
    sclsvrVISIBILITY_FILTER *visibilityFilter = new sclsvrVISIBILITY_FILTER;
    _filterList.Add(visibilityFilter);
    // Add distance filter
    vobsDISTANCE_FILTER *distanceFilter = new vobsDISTANCE_FILTER;
    _filterList.Add(distanceFilter);
    // Add spectral type filter
    vobsSPECTRAL_TYPE_FILTER *spectralTypeFilter =
        new vobsSPECTRAL_TYPE_FILTER;
    _filterList.Add(spectralTypeFilter);
    // Add luminosity filter
    vobsLUMINOSITY_FILTER *luminosityFilter = new vobsLUMINOSITY_FILTER;
    _filterList.Add(luminosityFilter);
    
    return mcsSUCCESS;
}

/**
 * Set list in the model
 *
 * @param msg message in which is serialized the list
 *
 * @return an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiMODEL::SetList(msgMESSAGE &msg)
{
    logTrace("sclguiMODEL::SetList()");

    // Retreive the returned calibrator list. Check whether calibrators
    // have been found or not.
    if (miscIsSpaceStr(msg.GetBody()) == mcsFALSE)
    {
        _calibratorList.UnPack(msg.GetBody());
    }
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Set list in the model
 *
 * @param list the list to copy in the model
 *
 * @return an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiMODEL::SetList(sclsvrCALIBRATOR_LIST &list)
{
    logTrace("sclguiMODEL::SetList()");

    _calibratorList.Copy(list);
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}


/**
 * Set request in the model
 * 
 *@param msg message in which is serialized the list
 * 
 * @return an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiMODEL::SetRequest(msgMESSAGE &msg)
{
    logTrace("sclguiMODEL::SetRequest()");

    // Build the request object from the parameters of the command
    if (_request.Parse(msg.GetBody()) == mcsFAILURE)
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
mcsCOMPL_STAT sclguiMODEL::EnableFilterMagnitude(mcsSTRING32 band,
                                                 mcsFLOAT magValue,
                                                 mcsFLOAT magRange)
{
    logTrace("sclguiMODEL::EnableFilterMagnitude()");
    
    // Get the magnitude filter from the list
    vobsMAGNITUDE_FILTER *magFilter = 
        (vobsMAGNITUDE_FILTER *)
        _filterList.GetFilter(vobsMAGNITUDE_FILTER_NAME);
   
    // Set enable the magnitude filter
    if (magFilter->Enabled() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set value in the magnitude filter
    if (magFilter->SetMagnitudeValue(band, magValue, magRange) == mcsFAILURE)
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
mcsCOMPL_STAT sclguiMODEL::EnableFilterDistance(mcsSTRING32 raRef,
                                                mcsSTRING32 decRef,
                                                mcsFLOAT raRange,
                                                mcsFLOAT decRange)
{
    logTrace("sclguiMODEL::EnableFilterDistance()");
    
    // Get the distance filter from the list
    vobsDISTANCE_FILTER *distanceFilter = 
        (vobsDISTANCE_FILTER *)
        _filterList.GetFilter(vobsDISTANCE_FILTER_NAME);
   
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
mcsCOMPL_STAT sclguiMODEL::EnableFilterVisibility(mcsFLOAT visMax)
{
    logTrace("sclguiMODEL::EnableFilterVisibility()");
    
    // Get the visibility filter from the list
    sclsvrVISIBILITY_FILTER *visibilityFilter = 
        (sclsvrVISIBILITY_FILTER *)
        _filterList.GetFilter(sclsvrVISIBILITY_FILTER_NAME);
   
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
mcsCOMPL_STAT sclguiMODEL::
EnableFilterSpectralType(std::list<char *> tempClassList)
{
    logTrace("sclguiMODEL::EnableFilterSpectralType()");

    // Get the spectral type filter from the list
    vobsSPECTRAL_TYPE_FILTER *spectralTypeFilter = 
        (vobsSPECTRAL_TYPE_FILTER *)
        _filterList.GetFilter(vobsSPECTRAL_TYPE_FILTER_NAME);
   
    // Set enable the spectral type filter
    if (spectralTypeFilter->Enabled() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set value in the spectral type filter
    if (spectralTypeFilter->
        SetSpectralType(tempClassList) == mcsFAILURE)
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

/**
 * Enabled the luminosity filter of the list
 *
 * @param lumClassList a list of luminosity class
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiMODEL::
EnableFilterLuminosity(std::list<char *> lumClassList)
{
    logTrace("sclguiMODEL::EnableFilterLuminosity()");

    // Get the luminosity filter from the list
    vobsLUMINOSITY_FILTER *luminosityFilter = 
        (vobsLUMINOSITY_FILTER *)
        _filterList.GetFilter(vobsLUMINOSITY_FILTER_NAME);
    
    // Set enable the luminosity filter
    if (luminosityFilter->Enabled() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set value in the luminosity filter
    if (luminosityFilter->
        SetLuminosity(lumClassList) == mcsFAILURE)
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

/**
 * Enabled the variability filter of the list
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiMODEL::EnableFilterVariability()
{
    logTrace("sclguiMODEL::EnableFilterVariability()");

    // Get the variability filter from the list
    vobsVARIABILITY_FILTER *variabilityFilter = 
        (vobsVARIABILITY_FILTER *)
        _filterList.GetFilter(vobsVARIABILITY_FILTER_NAME);
   
    // Set enable the variability filter
    if (variabilityFilter->Enabled() == mcsFAILURE)
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

/**
 * Enabled the multiplicity filter of the list
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiMODEL::EnableFilterMultiplicity()
{
    logTrace("sclguiMODEL::EnableFilterVariability()");

    // Get the multiplicity filter from the list
    vobsMULTIPLICITY_FILTER *multiplicityFilter = 
        (vobsMULTIPLICITY_FILTER *)
        _filterList.GetFilter(vobsMULTIPLICITY_FILTER_NAME);
   
    // Set enable the multiplicity filter
    if (multiplicityFilter->Enabled() == mcsFAILURE)
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


/**
 * Disable the magnitude filter of the list
 *
 * @param name the name of the filter to disable
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMODEL::DisableFilter(mcsSTRING32 name)
{
    logTrace("sclguiMODEL::DisableFilter()");

    // Get the filter from the list
    vobsFILTER *filter = 
        _filterList.GetFilter(name);
    
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

/**
 * Add a Star in the calibrator of unwanted calibrator
 *
 * @param calibrator the unwanted calibrator
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMODEL::AddDeleteStar(sclsvrCALIBRATOR &calibrator)
{
    logTrace("sclguiMODEL::AddDeleteStar()");

    // Add the calibrator in the unwanted calibrator list
    if (_deletingCalibratorList.AddAtTail(calibrator) == mcsFAILURE)
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

/**
 * Reset filter list of the model
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMODEL::ResetFilter(void)
{
    logTrace("sclguiMODEL::ResetFilter()");
    
    // Reset all filters of the list
    if (_filterList.Reset() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
   
    // Enable only the filter by variability and visibility
    EnableFilterVariability();
    EnableFilterMultiplicity();
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Reset deleting calibratorist of the model
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiMODEL::ResetDeleteCalirator(void)
{
    logTrace("sclguiMODEL::ResetDeleteCalirator()");

    _deletingCalibratorList.Clear();

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Get the list of calibrator of the model
 *
 * @return list of calibrator of the model according the filter witch are
 * enabled
 */
sclsvrCALIBRATOR_LIST* sclguiMODEL::GetCalibratorList(void)
{
    logTrace("sclguiMODEL::GetCalibratorList()");

    // Create new calibrator list from the model list
    sclsvrCALIBRATOR_LIST* list;
    list = new sclsvrCALIBRATOR_LIST;
    list->Copy(_calibratorList, mcsFALSE);
    
    // apply all enable filter on the list
    _filterList.Apply(list);
    list->Extract(_deletingCalibratorList);
    
    return list;
}

/**
 * Get the request of the model
 *
 * @return request of the model
 */
sclsvrREQUEST * sclguiMODEL::GetRequest(void)
{
    logTrace("sclguiMODEL::GetRequest()");

    return &_request;
}

/**
 * Get the filter list of the model
 *
 * @return filter list of the model
 */
vobsFILTER_LIST * sclguiMODEL::GetFilterList(void)
{
    logTrace("sclguiMODEL::GetFilterList()");
    
    return &_filterList;
}

/**
 * Get number of CDS return
 *
 * @return number of CDS return
 */
mcsFLOAT sclguiMODEL::GetNbCDSReturn()
{
    logTrace("sclguiMODEL::GetNbCDSReturn");

    return _calibratorList.Size();
}

/**
 * Get number of calibrator with coherent diameter
 *
 * @return number of calibrator with coherent diameter
 */
mcsFLOAT sclguiMODEL::GetNbCoherentDiamFound()
{
    logTrace("sclguiMODEL::GetNbCoherentDiamFound");   

    // Create new calibrator list from the model list
    sclsvrCALIBRATOR_LIST list;
    list.Copy(_calibratorList, mcsFALSE);

    return list.Size();
}

/**
 * Get number of caliobrator without variability and multiplicity flag
 *
 * @return number of caliobrator without variability and multiplicity flag
 */
mcsFLOAT sclguiMODEL::GetNbWithoutVarMult()
{
    logTrace("sclguiMODEL::GetNbWithoutVarMult");
    
    vobsFILTER_LIST filterList;
    // Add variability filter
    vobsVARIABILITY_FILTER *variabilityFilter = new vobsVARIABILITY_FILTER;
    variabilityFilter->Enabled();
    filterList.Add(variabilityFilter);

    // Add multiplicity filter
    vobsMULTIPLICITY_FILTER *multiplicityFilter =
        new vobsMULTIPLICITY_FILTER;
    multiplicityFilter->Enabled();
    filterList.Add(multiplicityFilter);
    
    // Create new calibrator list from the model list
    sclsvrCALIBRATOR_LIST list;
    list.Copy(_calibratorList, mcsFALSE);

    filterList.Apply(&list);
    
    return list.Size();
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
