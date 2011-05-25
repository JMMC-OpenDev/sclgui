/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiCALIBRATOR_LIST_MODEL class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiCALIBRATOR_LIST_MODEL.cpp,v 1.8 2006-04-05 15:08:28 gzins Exp $"; 

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

/*
 * Local Headers 
 */
#include "sclguiCALIBRATOR_LIST_MODEL.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

/**
 * Class constructor
 */
sclguiCALIBRATOR_LIST_MODEL::sclguiCALIBRATOR_LIST_MODEL()
{
}

/**
 * Class destructor
 */
sclguiCALIBRATOR_LIST_MODEL::~sclguiCALIBRATOR_LIST_MODEL()
{
}


/*
 * Public methods
 */
/**
 * Set list in the model
 *
 * @param msg message in which is serialized the list
 *
 * @return an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::SetList(msgMESSAGE &msg)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::SetList()");

    // Clear list 
    Clear();

    // If calibrators have been found or not.
    if (miscIsSpaceStr(msg.GetBody()) == mcsFALSE)
    {
        // Extract the received calibrator list.
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
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::SetList(sclsvrCALIBRATOR_LIST &list)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::SetList()");

    // Clear list 
    Clear();

    _calibratorList.Copy(list);
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Clear calibrator list of the model
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::Clear(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::Clear()");

    // Clear the list of calibrators
    _calibratorList.Clear();

     // Clear list of deleted calibrators 
    if (ClearDeletedCalibratorList() == mcsFAILURE)
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
 * Set filter list pointer
 *
 * @param filterList the list to attached to the calibrator model
 *
 * @return an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::SetFilterList(sclguiFILTER_LIST_MODEL * filterList)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::SetFilterList()");

    _filterListModel = filterList;
    
    return mcsSUCCESS;
}

/**
 * Get the list of the specified calibrators
 *
 * @param type specify  
 *
 * @return list of calibrator of the model according the filter witch are
 * enabled
 */
sclsvrCALIBRATOR_LIST* sclguiCALIBRATOR_LIST_MODEL::GetList
    (sclguiTYPE_OF_CALIBRATORS type)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetList()");

    switch (type)
    {
        case sclguiALL_CALIBRATORS:
        {
            return (&_calibratorList);
            break;
        }
        case sclguiDELETED_CALIBRATORS:
        {
            return (&_deletedCalibratorList);
            break;
        }        
        case sclguiFILTERED_CALIBRATORS:
        {
            // Apply filter and remove deleted calibrators
            _filteredCalibratorList.Clear(); 
            _filteredCalibratorList.Copy(_calibratorList, mcsFALSE);
            _filteredCalibratorList.Delete(_deletedCalibratorList);
            _filterListModel->Apply(_filteredCalibratorList);
            return (&_filteredCalibratorList);
            break;
        }        
        case sclguiCALIBRATORS_WITH_COHERENT_DIAM:
        case sclguiCALIBRATORS_WITHOUT_VAR_MULT:
        {
            errAdd(sclguiERR_NOT_SUPPORTED, 
                   "Get list of calibrators with coherent diameters or without "
                   "multiply/variability");
            return NULL;
            break;
        }
        default:
        {
            errAdd(sclguiERR_INVALID_CALIB_TYPE, type);
            return NULL;
            break;
        }
    }
}

/**
 * Get number of CDS return
 *
 * @return number of CDS return
 */
mcsINT32 sclguiCALIBRATOR_LIST_MODEL::GetNbCalibrators
    (sclguiTYPE_OF_CALIBRATORS type)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetNbCalibrators");

    switch (type)
    {
        case sclguiALL_CALIBRATORS:
        {
            return (_calibratorList.Size());
            break;
        }
        case sclguiDELETED_CALIBRATORS:
        {
            return (_deletedCalibratorList.Size());
            break;
        }        
        case sclguiFILTERED_CALIBRATORS:
        {
            // Apply filter and remove deleted calibrators
            _filteredCalibratorList.Clear(); 
            _filteredCalibratorList.Copy(_calibratorList, mcsFALSE);
            _filteredCalibratorList.Delete(_deletedCalibratorList);
            _filterListModel->Apply(_filteredCalibratorList);
            return (_filteredCalibratorList.Size());
            break;
        }        
        case sclguiCALIBRATORS_WITH_COHERENT_DIAM:
        {
            // Get list with only calibrators having coherent diameter
            sclsvrCALIBRATOR_LIST list;
            list.Copy(_calibratorList, mcsFALSE);
            return (list.Size());
            break;
        }
        case sclguiCALIBRATORS_WITHOUT_VAR_MULT:
        {
            // Filter list
            vobsFILTER_LIST filterList("Filter List");

            // Add variability filter
            vobsVARIABILITY_FILTER variabilityFilter("Var filter");
            variabilityFilter.Enable();
            filterList.Add(&variabilityFilter, "Variability Filter");

            // Add multiplicity filter
            vobsMULTIPLICITY_FILTER multiplicityFilter("Mult filter");
            multiplicityFilter.Enable();
            filterList.Add(&multiplicityFilter, "Multiplicity Filter");

            // Get list with only calibrators having coherent diameter
            sclsvrCALIBRATOR_LIST list;
            list.Copy(_calibratorList, mcsFALSE);
    
            // Apply the temporary filter list on the copied list
            filterList.Apply(&list);
            return (list.Size());

            break;
        }
        default:
        {
            errAdd(sclguiERR_INVALID_CALIB_TYPE, type);
            return -1;
            break;
        }
    }
}

/**
 * Remove a star from the calibrator list.
 *
 * Removing a calibrator of the list is done by puting this star in the list of
 * the unwanted star.
 *
 * @param starNum the unwanted calibrator number
 * @param starId Id of the deleted star
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiCALIBRATOR_LIST_MODEL::DeleteCalibrator(mcsINT32 starNum,
                                              mcsSTRING32 starId)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::DeleteCalibrator()");

    sclsvrCALIBRATOR_LIST* list;
    list = new sclsvrCALIBRATOR_LIST;
    list->Copy(_calibratorList, mcsFALSE);
    list->Delete(_deletedCalibratorList);
    _filterListModel->Apply(*list);
    
    sclsvrCALIBRATOR* calibrator = NULL;    
    for (int el = 0; el < starNum; el++)
    {
        // Get next calibrator
        calibrator = (sclsvrCALIBRATOR*)list->GetNextStar((mcsLOGICAL)(el==0));
    }
    
    // Add the calibrator in the unwanted calibrator list
    if (_deletedCalibratorList.AddAtTail(*calibrator) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    // Get star Id 
    calibrator->GetId(starId, sizeof(mcsSTRING32) - 1);
    
    return mcsSUCCESS;
}

/**
 * Clear list of deleted calibrators 
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::ClearDeletedCalibratorList(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::ClearDeletedCalibratorList()");

    _deletedCalibratorList.Clear();

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Load a calibrator list from a given file
 *
 * @param fileName the path to the file to be loaded
 * @param request the request to build from the file
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::LoadFromFile(mcsSTRING256 fileName,
                                                        sclsvrREQUEST &request)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::LoadFromFile()");

    // Clear list of calibrators 
    if (Clear() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Load list and request
    if (_calibratorList.Load(fileName, request) == mcsFAILURE)
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
