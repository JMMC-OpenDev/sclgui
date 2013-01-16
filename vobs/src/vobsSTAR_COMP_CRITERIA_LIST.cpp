/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * vobsSTAR_COMP_CRITERIA_LIST class definition.
 */

/* 
 * System Headers 
 */
#include <iostream>
#include <string.h>
using namespace std;
#include <map>

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "vobsSTAR_COMP_CRITERIA_LIST.h"
#include "vobsSTAR.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"

/**
 * Class constructor
 */
vobsSTAR_COMP_CRITERIA_LIST::vobsSTAR_COMP_CRITERIA_LIST()
{
    // Ensure Internal members are undefined:
    _initialized = false;
    _criteriaInfos = NULL;
}

/**
 * Copy Constructor
 */
vobsSTAR_COMP_CRITERIA_LIST::vobsSTAR_COMP_CRITERIA_LIST(const vobsSTAR_COMP_CRITERIA_LIST& criteriaList)
{
    // Ensure Internal members are undefined:
    _initialized = false;
    _criteriaInfos = NULL;

    // Uses the operator=() method to copy
    *this = criteriaList;
}

/**
 * Class destructor
 */
vobsSTAR_COMP_CRITERIA_LIST::~vobsSTAR_COMP_CRITERIA_LIST()
{
    // note: criteria list contains char* keys but these are in fact literals

    // Clear the internal map
    Clear();
}

/**
 * Assignment operator
 */
vobsSTAR_COMP_CRITERIA_LIST&vobsSTAR_COMP_CRITERIA_LIST::operator=(const vobsSTAR_COMP_CRITERIA_LIST& criteriaList)
{
    if (this != &criteriaList)
    {
        // Clear the internal map
        Clear();

        // Copy in the criteria list from the given criteriaList
        _criteriaList = criteriaList._criteriaList;
    }
    return *this;
}

/*
 * Public methods
 */

/**
 * Clear the criteria list
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_COMP_CRITERIA_LIST::Clear()
{
    _criteriaList.clear();

    // Ensure Internal members are undefined:
    resetCriterias();

    return mcsSUCCESS;
}

/**
 * Add a criteria in the list
 *
 * @param propertyId the identifier of property.
 * @param range the range value of the criteria used when comparing double 
 * floating property. It is ignored when comparing string.
 * 
 * @return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_COMP_CRITERIA_LIST::Add(const char* propertyId,
                                               mcsDOUBLE range)
{
    // check if that property is known
    // If criteria is not a property return failure
    int propertyIndex = vobsSTAR::GetPropertyIndex(propertyId);
    FAIL_COND_DO(propertyIndex == -1, errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId));

    // Put criteria in the list
    _criteriaList[propertyId] = range;

    // Ensure Internal members are undefined:
    resetCriterias();

    return mcsSUCCESS;
}

/**
 * Method to remove a criteria from the list of criteria
 *
 * @param propertyId the id of the criteria to removed
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT vobsSTAR_COMP_CRITERIA_LIST::Remove(const char* propertyId)
{
    // check if that property is known
    // If criteria is not a property return failure
    int propertyIndex = vobsSTAR::GetPropertyIndex(propertyId);
    FAIL_COND_DO(propertyIndex == -1, errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId));

    // Remove criteria from the list
    _criteriaList.erase(propertyId);

    // Ensure Internal members are undefined:
    resetCriterias();

    return mcsSUCCESS;
}

/**
 * Get the size of the list
 * 
 * Note: this function uses the map::size()
 *
 * @return the number of element in the list
 **/
int vobsSTAR_COMP_CRITERIA_LIST::Size()
{
    return _criteriaList.size();
}

/**
 * Show criteria in logs:
 * TODO: use vobsSTAR_CRITERIA_INFO instead of _criteriaList
 */
void vobsSTAR_COMP_CRITERIA_LIST::log(logLEVEL level, const char* prefix)
{
    if (doLog(level))
    {
        const char* propertyId;
        mcsDOUBLE range;

        int i = 0;
        for (vobsSTAR_CRITERIA_MAP::iterator iter = _criteriaList.begin(); iter != _criteriaList.end(); iter++)
        {
            propertyId = iter->first;
            range = iter->second;

            logPrint(MODULE_ID, level, NULL, __FILE_LINE__, "%sCriteria %d on property[%s] with range = %.9lf", prefix, (++i), propertyId, range);
        }
    }
}

/**
 * Prepare criteria traversal (lazily initialized)
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT vobsSTAR_COMP_CRITERIA_LIST::InitializeCriterias()
{
    if (_initialized)
    {
        return mcsSUCCESS;
    }

    // Reset Internal members:
    resetCriterias();

    // cache list size:
    _size = _criteriaList.size();

    const bool isLogDebug = doLog(logDEBUG);

    // Create criteria informations:
    _criteriaInfos = new vobsSTAR_CRITERIA_INFO[_size];

    // Fill criteria information:
    vobsSTAR_CRITERIA_INFO* criteria = NULL;

    const char* propertyId;
    mcsDOUBLE range;
    int propertyIndex;
    const vobsSTAR_PROPERTY_META* meta = NULL;

    /* 
     * Notes: 
     * - criterias RA and DEC are merged into one single vobsSTAR_CRITERIA_INFO to perform accurate separation computation
     * - criteria RA  is always at position 0 in _criteriaList
     * - criteria DEC is always at position 1 in _criteriaList
     */

    // TODO: check that RA and DEC criteria are always defined

    int i = 0; // correct size

    for (vobsSTAR_CRITERIA_MAP::iterator iter = _criteriaList.begin(); iter != _criteriaList.end(); iter++)
    {
        propertyId = iter->first;
        range = iter->second;

        criteria = &_criteriaInfos[i];

        criteria->propertyId = propertyId;

        // Get property index:
        propertyIndex = vobsSTAR::GetPropertyIndex(propertyId);
        if (propertyIndex == -1)
        {
            errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId);

            // Ensure Internal members are undefined:
            resetCriterias();

            return mcsFAILURE;
        }

        // Get property meta:
        meta = vobsSTAR::GetPropertyMeta(propertyIndex);
        if (meta == NULL)
        {
            errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId);

            // Ensure Internal members are undefined:
            resetCriterias();

            return mcsFAILURE;
        }

        // is RA or DEC:
        if (strcmp(propertyId, vobsSTAR_POS_EQ_RA_MAIN) == 0)
        {
            criteria->propertyIndex = -1; // undefined and useless
            criteria->propCompType = vobsPROPERTY_COMP_RA_DEC;
            criteria->propertyId = "RA/DEC"; // undefined and useless

            criteria->range = FP_NAN;
            criteria->rangeRA = range;
            criteria->rangeDEC = FP_NAN;

            criteria->lowerBoundRA = -180. + range;
            criteria->upperBoundRA = 180. - range;

            // box or circular area (MIDI) ?
            criteria->isRadius = false;

            // note: i is not incremented to define DEC next
        }
        else if (strcmp(propertyId, vobsSTAR_POS_EQ_DEC_MAIN) == 0)
        {
            criteria->propertyIndex = -1; // undefined and useless
            criteria->propCompType = vobsPROPERTY_COMP_RA_DEC;
            criteria->propertyId = "RA/DEC"; // undefined and useless
            criteria->rangeDEC = range;

            // box or circular area (MIDI) ?
            criteria->isRadius = (criteria->rangeRA == criteria->rangeDEC);

            if (!criteria->isRadius)
            {
                // fix RA bounds because separation can not be computed !
                criteria->lowerBoundRA = -180.;
                criteria->upperBoundRA = 180.;
            }

            i++;
        }
        else
        {
            criteria->propertyIndex = propertyIndex;
            criteria->range = range;

            switch (meta->GetType())
            {
                case vobsSTRING_PROPERTY:
                    criteria->propCompType = vobsPROPERTY_COMP_STRING;
                    break;
                default:
                case vobsFLOAT_PROPERTY:
                    criteria->propCompType = vobsPROPERTY_COMP_FLOAT;
            }
            i++;
        }
    }

    // fix size:
    _size = i;
    _initialized = true;

    if (isLogDebug)
    {
        for (i = 0; i < _size; i++)
        {
            criteria = &_criteriaInfos[i];

            if (criteria->propCompType == vobsPROPERTY_COMP_RA_DEC)
            {
                // ra/dec criteria
                logDebug("InitializeCriterias: criteria %d on RA/DEC using %s area", i + 1, criteria->isRadius ? "CIRCULAR" : "BOX");
                logDebug("InitializeCriterias: range RA / DEC = %.9lf / %.9lf", criteria->rangeRA, criteria->rangeDEC);
                logDebug("InitializeCriterias: RA bounds = %.9lf / %.9lf", criteria->lowerBoundRA, criteria->upperBoundRA);
            }
            else
            {
                // other criteria
                logDebug("InitializeCriterias: criteria %d on Property [%d : %s]", i + 1, criteria->propertyIndex, criteria->propertyId);

                if (criteria->propCompType == vobsPROPERTY_COMP_STRING)
                {
                    logDebug("InitializeCriterias: comparison type: STRING");
                }
                else
                {
                    logDebug("InitializeCriterias: comparison type: FLOAT");
                    logDebug("InitializeCriterias: range = %.9lf", criteria->range);
                }
            }
        }
    }

    return mcsSUCCESS;
}

/**
 * Get all criteria informations (initialized by InitializeCriterias)
 * @param criteriaInfo returned vobsSTAR_CRITERIA_INFO[] pointer or NULL
 * @param size returned number of criteria or 0
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT vobsSTAR_COMP_CRITERIA_LIST::GetCriterias(vobsSTAR_CRITERIA_INFO*& criteriaInfo, int& size)
{
    if (!_initialized)
    {
        if (InitializeCriterias() == mcsFAILURE)
        {
            criteriaInfo = NULL;
            size = 0;

            return mcsFAILURE;
        }
    }

    criteriaInfo = _criteriaInfos;
    size = _size;

    return mcsSUCCESS;
}

/**
 * Reset internal criteria informations
 */
void vobsSTAR_COMP_CRITERIA_LIST::resetCriterias()
{
    if (_initialized)
    {
        _initialized = false;
        _size = 0;

        if (_criteriaInfos != NULL)
        {
            delete[](_criteriaInfos);
        }
        _criteriaInfos = NULL;
    }
}

/*___oOo___*/
