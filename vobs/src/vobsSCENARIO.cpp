/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * vobsSCENARIO class definition.
 */

/* 
 * System Headers 
 */
#include <iostream>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
using namespace std;
#include<list>


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "misc.h"
#include "timlog.h"
#include "sdb.h"

/*
 * Local Headers 
 */
#include "vobsDISTANCE_FILTER.h"
#include "vobsSCENARIO.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"


/*
 * Class constructor
 */
vobsSCENARIO::vobsSCENARIO(sdbENTRY* progress)
{
    // affect to NULL the catalog list
    _catalogList = NULL;
    _nbOfCatalogs = 0;
    _catalogIndex = 0;

    _progress = progress;

    // disable debugging flags by default:
    _saveSearchXml    = false;
    _saveSearchList   = false;
    _saveMergedList   = false;

    // TODO: decide ASAP if _filterDuplicates and _enableStarIndex are enabled definitely:
    // enable duplicates detection before the merge operation:
    _filterDuplicates = true;

    // enable star index use to perform faster merge operations:
    _enableStarIndex  = true;

    // enable flag to determine automatically the cone search radius for secondary requests using criteria radius
    _autoConeSearchRadius = true;
}

/*
 * Class destructor
 */
vobsSCENARIO::~vobsSCENARIO()
{
    Clear();
}

/*
 * Public methods
 */

/**
 * Return the name of this scenario
 * @return "JSDC"
 */
const char* vobsSCENARIO::GetScenarioName()
{
    return "UNDEF";
}

/**
 * Initialize the scenario.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned 
 */
mcsCOMPL_STAT vobsSCENARIO::Init(vobsREQUEST* request)
{
    logTrace("vobsSCENARIO::Init()");
    return mcsFAILURE;
}

/**
 * Adds the element at the end of the list.
 *
 * The method create a entry from the parameters and put it in the list.
 * @param catalogName the catalog name to ask corresponding to the entry
 * @param request request associated with the scenario
 * @param listInput list of star in enter of the research
 * @param listOutput list of star resulting of the asking of the catalog
 * @param action enumerate representing the action to do
 * @param criteriaList list of comparaison criteria
 * @param filter filter
 * @param queryOption query option
 * 
 * @return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSCENARIO::AddEntry(const char*                   catalogName,
                                     vobsREQUEST*                  request,
                                     vobsSTAR_LIST*                listInput,
                                     vobsSTAR_LIST*                listOutput,
                                     vobsACTION                    action,
                                     vobsSTAR_COMP_CRITERIA_LIST*  criteriaList,
                                     vobsFILTER*                   filter,
                                     const char*                   queryOption)
{
    logTrace("vobsSCENARIO::AddEntry()");

    // Create a new entry
    // Affect in this entry the catalogName, the list input, the list output,
    // the action to do, and the criteria list
    vobsSCENARIO_ENTRY* entry = new vobsSCENARIO_ENTRY(catalogName,
                             request,
                             listInput,
                             listOutput,
                             action,
                             criteriaList,
                             filter);
    // Set query option
    if (queryOption != NULL)
    {
        entry->SetQueryOption(queryOption);
    }

    // Put element in the list    
    _entryList.push_back(entry);

    // Increment true catalog counter (if not a filter)
    if (strcmp(catalogName, vobsNO_CATALOG_ID) != 0)
    {
        _nbOfCatalogs++;
    }

    return mcsSUCCESS;
}

/**
 * Execute the scenario
 *
 * The methods execute the scenario which had been loaded before. It will
 * read each entry and query the specific catalog.
 * The scenario execution progress is reported using sdbENTRY instance given to
 * constructor. It writes a message having the following format:
 *      &lt;status&gt; &lt;catalog name&gt; &lt;catalog number&gt; 
 *      &lt;number of catalogs&gt;
 * where
 *  <li> &lt;status&gt; is 1 meaning 'In progress'
 *  <li> &lt;catalog name&gt; is the name of catalog currently consulted
 *  <li> &lt;catalog number&gt; is the catalog number in the list
 *  <li> &;number of catalogs&gt; is the number of catalogs in the list
 *
 * @param starList vobsSTAR_LIST which is the result of the interrogation,
 * this is the last list return of the last interrogation.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned 
 */
mcsCOMPL_STAT vobsSCENARIO::Execute(vobsSTAR_LIST &starList)
{
    logInfo("Execute: start");

    mcsUINT32 nStep        = 0; // step count
    mcsINT64 elapsedTime   = 0; // current search time
    mcsINT64 sumSearchTime = 0; // cumulative search time 

    // define action for timlog trace
    mcsSTRING256 timLogActionName;

    // Create a temporary list of star in which will be store the list input
    vobsSTAR_LIST tempList;

    // Create a temporary list of star used to detect / filter duplicates
    vobsSTAR_LIST dupList;

    int inputSize;

    vobsSCENARIO_ENTRY* entry;

    // Create an iterator on the scenario entries
    for (std::list<vobsSCENARIO_ENTRY*>::iterator _entryIterator = _entryList.begin(); 
            _entryIterator != _entryList.end(); _entryIterator++)    
    {
        entry = *_entryIterator;

        // Increment step count:
        nStep++;

        // Begin to clean the temporary list
        if (tempList.Clear() == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Get entry information:
        const char* catalogName   = entry->_catalogName;
        vobsSTAR_LIST* inputList  = entry->_listInput;
        vobsSTAR_LIST* outputList = entry->_listOutput;
        vobsFILTER* filter        = entry->_filter;
        vobsSTAR_COMP_CRITERIA_LIST* criteriaList = entry->_criteriaList;
        vobsACTION action         = entry->_action;

        inputSize = (inputList != NULL) ? inputList->Size() : 0;

        // Copy the list input into the temporary list
        if (inputSize > 0)
        {
            // DEEP copy because inputList => tempList and outputList ( = inputList) .clear() will
            // delete also stars present in tempList (vobsCOPY only case)
            if ((action == vobsCOPY) && (inputList == outputList)
                && (strcmp(catalogName, vobsNO_CATALOG_ID) == 0))
            {
                tempList.Copy(*inputList);
            }
            else
            {
                // only copy star pointers (still managed by input list to free them):
                tempList.CopyRefs(*inputList, mcsFALSE);
            }
        }

        // **** CATALOG QUERYING ****

        // If there is a catalog to query
        if ((strcmp(catalogName, vobsNO_CATALOG_ID) != 0) &&
            ((inputList == NULL) || (inputSize > 0)))
        {
            // Start research in entry's catalog
            logTest("Execute: Step %d - Consulting %s ...", nStep, catalogName);

            // Get catalog name, and replace '/' by '_'
            mcsSTRING32 catalog;
            strcpy(catalog, catalogName);

            if (miscReplaceChrByChr(catalog, '/', '_') == mcsFAILURE)
            {
                return mcsFAILURE;
            }

            strcpy(timLogActionName, catalog);

            // Add request type (primary or not)
            if (inputSize == 0)
            {
                strcat(timLogActionName, "_PRIMARY");
            }
            else
            {
                strcat(timLogActionName, "_SECONDARY");
            }

            // Write the current action in the shared database
            mcsSTRING256 message;
            snprintf(message, sizeof(message), "1\t%s\t%d\t%d", catalogName, (_catalogIndex + 1), _nbOfCatalogs);

            if (_progress->Write(message) == mcsFAILURE)
            {
                goto errCond;
            }

            // Check if the list is not NULL, i.e the SetCatalogList has
            // ever been executed one time
            if (_catalogList == NULL)
            {
                errAdd(vobsERR_CATALOG_LIST_EMPTY);

                goto errCond;
            }

            // Get catalog from list
            vobsCATALOG* tempCatalog = _catalogList->Get(catalogName);
            if (tempCatalog == NULL)
            {
                errAdd(vobsERR_UNKNOWN_CATALOG);

                goto errCond;
            }

            // Get query option from scenario entry
            tempCatalog->SetOption(entry->GetQueryOption());

            vobsREQUEST* request = entry->_request;

            // Optimize query radius for secondary requests (vobsUPDATE_ONLY)
            // reset cone search radius:
            request->SetConeSearchRadius(-1.0); // means undefined

            if (_autoConeSearchRadius && (action == vobsUPDATE_ONLY) && (inputSize > 0) && (criteriaList != NULL))
            {
                // Get criteria informations:
                int nCriteria = 0;
                vobsSTAR_CRITERIA_INFO* criterias = NULL;

                if (criteriaList->GetCriterias(criterias, nCriteria) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }

                if (nCriteria > 0)
                {
                    // note: RA_DEC criteria is always the first one
                    vobsSTAR_CRITERIA_INFO* criteria = &criterias[0];

                    if ((criteria->propCompType == vobsPROPERTY_COMP_RA_DEC) && (criteria->isRadius))
                    {
                        // convert degrees to arcsec:
                        mcsDOUBLE radius = criteria->rangeRA * alxDEG_IN_ARCSEC;

                        logTest("Execute: Step %d - Optimized ConeSearchRadius = %0.1lf arcsec", nStep, radius);

                        request->SetConeSearchRadius(radius);
                    }
                }
            }

            // Start time counter
            timlogInfoStart(timLogActionName);

            // if research failed, return mcsFAILURE and tempList is empty
            if (tempCatalog->Search(*request, tempList, (mcsLOGICAL)_saveSearchXml) == mcsFAILURE )
            {
                goto errCond;
            }

            // Stop time counter
            timlogStopTime(timLogActionName, &elapsedTime);

            if (elapsedTime != 0)
            {
                sumSearchTime += elapsedTime;
            }

            logTest("Execute: Step %d - number of returned stars = %d", nStep, tempList.Size());

            _catalogIndex++;

            // Clean the catalog option
            tempCatalog->SetOption(NULL);

            // If the saveSearchList flag is enabled
            // or the verbose level is higher or equal to debug level, search
            // results will be stored in file
            if (_saveSearchList || doLog(logDEBUG))
            {
                // This file will be stored in the $MCSDATA/tmp repository
                mcsSTRING256 logFileName;
                strcpy(logFileName, "$MCSDATA/tmp/");

                // Get scenario name, and replace ' ' by '_'
                mcsSTRING32 scenarioName;
                strcpy(scenarioName, GetScenarioName());
                if (miscReplaceChrByChr(scenarioName, ' ', '_') == mcsFAILURE)
                {
                    return mcsFAILURE;
                }
                strcat(logFileName, scenarioName);

                // Add step
                mcsSTRING4 step;
                sprintf(step, "%d", nStep);
                strcat(logFileName, "_");
                strcat(logFileName, step);

                // Get band used for search
                strcat(logFileName, "_");
                strcat(logFileName, request->GetSearchBand());

                // Get catalog name, and replace '/' by '_'
                mcsSTRING32 catName;
                strcpy(catName, catalogName);
                if (miscReplaceChrByChr(catName, '/', '_') == mcsFAILURE)
                {
                    return mcsFAILURE;
                }
                strcat(logFileName, "_");
                strcat(logFileName, catName);

                // Add request type (primary or not)
                if (inputList == NULL)
                {
                    strcat(logFileName, "_1.log");
                }
                else
                {
                    strcat(logFileName, "_2.log");
                }

                // Resolve path
                char* resolvedPath = miscResolvePath(logFileName);
                if (resolvedPath != NULL)
                {
                    logTest("Save star list to: %s", resolvedPath);

                    // Save resulting list
                    if (tempList.Save(resolvedPath) == mcsFAILURE)
                    {
                        free(resolvedPath);
                        return mcsFAILURE;
                    }
                    free(resolvedPath);
                }
            }
        }

        // **** LIST COPYING/MERGING ****
        // There are 3 different action to do when the scenario is executed

        mcsLOGICAL isCatalogCIO = (mcsLOGICAL)(strcmp(catalogName, vobsCATALOG_CIO_ID) == 0);
        
        // DETECT duplicates (except CIO because multiple lines i.e. fluxes per star) 
        // on PRIMARY requests ONLY:
        if (_filterDuplicates && (isCatalogCIO == mcsFALSE) && !tempList.IsHasTargetIds())
        {
            // note: dupList is only used temporarly:
            if (dupList.FilterDuplicates(tempList, criteriaList, (mcsLOGICAL)_enableStarIndex) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }

        switch (action)
        {
            // First action is vobsCOPY. The list output will be cleared and
            // it will be merge from the temporary list which contain the list input or query results 
            case vobsCOPY:
            {
                logTest("Execute: Step %d - Performing COPY action with %d stars", nStep, tempList.Size());

                if (outputList->Clear() == mcsFAILURE)
                {
                    return mcsFAILURE;
                }

                if (outputList->Merge(tempList, criteriaList, mcsFALSE, (mcsLOGICAL)_enableStarIndex, isCatalogCIO) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }

                logTest("Execute: Step %d - after COPY, %d stars", nStep, outputList->Size());

                break;
            }

            // Second action is vobsMERGE. The list output will be 
            // merge from the temporary list whitout being cleared.
            // The information which is stored in the the list 
            // output is preserved and can be modified
            case vobsMERGE:
            {
                logTest("Execute: Step %d - Performing MERGE action with %d stars", nStep, tempList.Size());

                if (outputList->Merge(tempList, criteriaList, mcsFALSE, (mcsLOGICAL)_enableStarIndex, isCatalogCIO) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }

                logTest("Execute: Step %d - after MERGE, %d stars", nStep, outputList->Size());

                break;
            }

            // Third action is vobsUPDATE_ONLY. The list output will
            // be merge from the temporary list, but this merge will
            // not modified the existant information of the list output
            case vobsUPDATE_ONLY:
            {
                logTest("Execute: Step %d - Performing UPDATE_ONLY action with %d stars", nStep, tempList.Size());

                if (outputList->Merge(tempList, criteriaList, mcsTRUE, (mcsLOGICAL)_enableStarIndex, isCatalogCIO) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }

                logTest("Execute: Step %d - after UPDATE_ONLY, %d stars", nStep, outputList->Size());

                break;
            }

            default:
                break;
        }

        // If the saveMergedList flag is enabled
        // or the verbose level is higher or equal to debug level, search
        // results will be stored in file
        if (_saveMergedList || doLog(logDEBUG))
        {
            // This file will be stored in the $MCSDATA/tmp repository
            mcsSTRING256 logFileName;
            strcpy(logFileName, "$MCSDATA/tmp/");

            // Get scenario name, and replace ' ' by '_'
            mcsSTRING32 scenarioName;
            strcpy(scenarioName, GetScenarioName());
            if (miscReplaceChrByChr(scenarioName, ' ', '_') == mcsFAILURE)
            {
                return mcsFAILURE;
            }
            strcat(logFileName, scenarioName);

            // Add step
            mcsSTRING4 step;
            sprintf(step, "%d", nStep);
            strcat(logFileName, "_");
            strcat(logFileName, step);

            strcat(logFileName, "_MERGE.log");

            // Resolve path
            char* resolvedPath = miscResolvePath(logFileName);
            if (resolvedPath != NULL)
            {
                logTest("Save star list to: %s", resolvedPath);

                // Save resulting list
                if (outputList->Save(resolvedPath) == mcsFAILURE)
                {
                    free(resolvedPath);
                    return mcsFAILURE;
                }
                free(resolvedPath);
            }
        }

        // **** LIST FILTERING ****

        // Apply filter if defined
        if (filter != NULL)
        {
            logTest("Execute: Step %d - apply FILTER '%s'", nStep, filter->GetId());

            filter->Apply(outputList);

            logTest("Execute: Step %d - after FILTER '%s', star list size = %d", nStep, filter->GetId(), outputList->Size());
        }
    }

    // Copy resulting list (only references):
    starList.CopyRefs(*_entryList.back()->_listOutput);
 
    // clear scenario lists...
    for (std::list<vobsSCENARIO_ENTRY*>::iterator _entryIterator = _entryList.begin(); 
            _entryIterator != _entryList.end(); _entryIterator++)    
    {
        entry = *_entryIterator;

        vobsSTAR_LIST* inputList = entry->_listInput;
        if (inputList != NULL)
        {
            // clear input list
            inputList->Clear();
        }
        vobsSTAR_LIST* outputList = entry->_listOutput;
        if (outputList != NULL)
        {
            // clear output list
            outputList->Clear();
        }
    } 

    logInfo("Execute: done = %d star(s) found.", starList.Size()); 

    if (sumSearchTime != 0)
    {
        mcsSTRING16 time;
        timlogFormatTime(sumSearchTime, time);

        logInfo("Total time in catalog queries %s", time);
    }

    _catalogIndex = 0;

    return mcsSUCCESS;
    
errCond:
    // Discard time counter
    timlogCancel(timLogActionName);

    return mcsFAILURE;
}

/**
 * Clear the scenario
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSCENARIO::Clear(void)
{
    logTrace("vobsSCENARIO::Clear()");

    _catalogList = NULL;

    // Deallocate all entries
    for (std::list<vobsSCENARIO_ENTRY*>::iterator iter = _entryList.begin(); iter != _entryList.end(); iter++)
    {
        delete (*iter);
    }

    _entryList.clear();
    _nbOfCatalogs = 0;
    _catalogIndex = 0;

    return mcsSUCCESS;
}


/*___oOo___*/
