/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCALIBRATOR_LIST_MODEL.cpp,v 1.3 2005-11-05 15:55:51 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.1  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiCALIBRATOR_LIST_MODEL class.
 */

static char *rcsId="@(#) $Id: sclguiCALIBRATOR_LIST_MODEL.cpp,v 1.3 2005-11-05 15:55:51 gzins Exp $"; 
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
 * SCALIB Headers 
 */
#include "vobs.h"


/*
 * Local Headers 
 */
#include "sclguiCALIBRATOR_LIST_MODEL.h"
#include "sclguiPrivate.h"


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

    // todo err
    ResetCalibrators();

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

    // todo err
    ResetCalibrators();

    _calibratorList.Copy(list);
    
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
 * Get the list of calibrators of the model
 *
 * @return list of calibrator of the model according the filter witch are
 * enabled
 */
sclsvrCALIBRATOR_LIST* sclguiCALIBRATOR_LIST_MODEL::GetCalibratorList(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetCalibratorList()");

    _filteredCalibratorList.Clear(); 
    _filteredCalibratorList.Copy(_calibratorList, mcsFALSE);

    // delete in the return list the unwanted calibrators
    _filteredCalibratorList.Extract(_deletedCalibratorList);
    _filterListModel->Apply(_filteredCalibratorList);

    return &_filteredCalibratorList;
}

/**
 * Remove a star from the calibrator list.
 *
 * Removing a calibrator of the list is done by puting this star in the list of
 * the unwanted star.
 *
 * @param calibrator the unwanted calibrator
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT 
sclguiCALIBRATOR_LIST_MODEL::DeleteCalibrator(mcsINT32       unwantedStarNumber,
                                              miscoDYN_BUF  &DeletedStarNameMsg)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::DeleteCalibrator()");

    sclsvrCALIBRATOR_LIST* list;
    list = new sclsvrCALIBRATOR_LIST;
    list->Copy(_calibratorList, mcsFALSE);
    list->Extract(_deletedCalibratorList);
    _filterListModel->Apply(*list);
    
    sclsvrCALIBRATOR* calibrator = NULL;    
    for (int el = 0; el < unwantedStarNumber; el++)
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

    // Convert calibrator number to string
    mcsSTRING32 calibratorNumberBuffer;
    sprintf(calibratorNumberBuffer, "%d", unwantedStarNumber);
    // Build message
    if (DeletedStarNameMsg.AppendString("Star number ") == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    if (DeletedStarNameMsg.AppendString(calibratorNumberBuffer) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    if (calibrator != NULL)
    {
        if (DeletedStarNameMsg.AppendString(" (HD ") == mcsFAILURE)
        {
            return mcsFAILURE;
        }
        if (DeletedStarNameMsg.
            AppendString(calibrator->GetPropertyValue(vobsSTAR_ID_HD)) ==
            mcsFAILURE)
        {
            return mcsFAILURE;
        }
        if (DeletedStarNameMsg.AppendString(")") == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    if (DeletedStarNameMsg.AppendString(" has been deleted") == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}

/**
 * Reset calibrator list of the model
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::ResetCalibrators(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::ResetCalibrators()");

    _calibratorList.Clear();

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Reset deleting calibrator list of the model
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::ResetDeletedCalibrators(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::ResetDeletedCalirators()");

    _deletedCalibratorList.Clear();

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}


/**
 * Get number of CDS return
 *
 * @return number of CDS return
 */
mcsFLOAT sclguiCALIBRATOR_LIST_MODEL::GetNbCDSReturn(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetNbCDSReturn");

    return _calibratorList.Size();
}

/**
 * Get the number of calibrators with a coherent diameter
 *
 * @return number of calibrator with coherent diameter
 */
mcsFLOAT sclguiCALIBRATOR_LIST_MODEL::GetNbCoherentDiamFound(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetNbCoherentDiamFound");   

    // Create new calibrator list from the model list
    sclsvrCALIBRATOR_LIST list;
    list.Copy(_calibratorList, mcsFALSE);

    return list.Size();
}


/**
 * Get the number of caliobrators without variability and multiplicity flag
 *
 * @return number of calibrator without variability and multiplicity flag
 */
mcsFLOAT sclguiCALIBRATOR_LIST_MODEL::GetNbWithoutVarMult(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetNbWithoutVarMult");
    
    // Temporary filter list
    vobsFILTER_LIST filterList;

    // Add variability filter
    vobsVARIABILITY_FILTER variabilityFilter;
    variabilityFilter.Enable();
    filterList.Add(&variabilityFilter);

    // Add multiplicity filter
    vobsMULTIPLICITY_FILTER multiplicityFilter;
    multiplicityFilter.Enable();
    filterList.Add(&multiplicityFilter);
    
    // Create temporary calibrator list copy of the model list
    sclsvrCALIBRATOR_LIST list;
    list.Copy(_calibratorList, mcsFALSE);

    // Apply the temporary filter list on the copied list
    filterList.Apply(&list);
    
    // Return the number of calibrator left in the list after filtering
    return list.Size();
}

/**
 * Get the number of star left in the filtered list
 *
 * @return number of star in the filtered list
 */
mcsFLOAT sclguiCALIBRATOR_LIST_MODEL::GetNbInFilteredList(void)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::GetNbInFilteredList()");

    return _filteredCalibratorList.Size();
}

/**
 * Load a calibrator list from a given file
 *
 * @param fileName the path to the file to be loaded
 * @param the request to build from the file
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_MODEL::LoadFromFile(mcsSTRING256 fileName,
                                              sclsvrREQUEST &request)
{
    logTrace("sclguiCALIBRATOR_LIST_MODEL::LoadFromFile()");

    // Reset Model
    if (ResetCalibrators() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    if (ResetDeletedCalibrators() == mcsFAILURE)
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
