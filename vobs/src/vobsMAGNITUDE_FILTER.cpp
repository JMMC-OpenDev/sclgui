/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of vobsMAGNITUDE_FILTER class.
 */


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
#include "vobsMAGNITUDE_FILTER.h"
#include "vobsPrivate.h"

/**
 * Class constructor
 */
vobsMAGNITUDE_FILTER::vobsMAGNITUDE_FILTER(const char* filterId):vobsFILTER(filterId)
{
}

/**
 * Class destructor
 */
vobsMAGNITUDE_FILTER::~vobsMAGNITUDE_FILTER()
{
}

/*
 * Public methods
 */
/**
 * Set value to the filter
 *
 * @param band magnitude band
 * @param magValue magnitude value
 * @param magRange magnitude range
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsMAGNITUDE_FILTER::SetMagnitudeValue(const char* band,
                                                      mcsDOUBLE magValue,
                                                      mcsDOUBLE magRange)
{
    logTrace("vobsMAGNITUDE_FILTER::SetMagnitudeValue()");

    strcpy(_band, band);
    _magValue = magValue;
    _magRange = magRange;
    
    return mcsSUCCESS;
}

/**
 * Get value of the filter 
 *  
 * @param band magnitude band
 * @param magValue magnitude value
 * @param magRange magnitude range
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsMAGNITUDE_FILTER::GetMagnitudeValue(char* band,
                                                      mcsDOUBLE *magValue,
                                                      mcsDOUBLE *magRange)
{
    logTrace("vobsMAGNITUDE_FILTER::GetMagnitudeValue()");

    strcpy(band, _band);
    *magValue = _magValue;
    *magRange = _magRange;
    
    return mcsSUCCESS;
}

/**
 * Apply magnitude filter on a list
 *
 * @param list the list on which the filter is applied
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT vobsMAGNITUDE_FILTER::Apply(vobsSTAR_LIST *list)
{
    logTrace("vobsMAGNITUDE_FILTER::Apply()");

    if (IsEnabled() == mcsTRUE)
    {
        // Create the UCD corresponding to the band
        mcsSTRING256 magnitudeUcd;
        strcpy(magnitudeUcd, "PHOT_JHN_");
        strcat(magnitudeUcd, _band);

        // Create a star correponding to the reference object
        vobsSTAR referenceStar;
        if (referenceStar.SetPropertyValue(magnitudeUcd, _magValue, "") == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Add criteria on magnitude
        vobsSTAR_COMP_CRITERIA_LIST criteriaList;
        if (criteriaList.Add(magnitudeUcd, _magRange) == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        int nCriteria = 0;
        vobsSTAR_CRITERIA_INFO* criterias = NULL;
        
        // log criterias:
        criteriaList.log(logTEST);

        // Get criterias:
        if (criteriaList.GetCriterias(criterias, nCriteria) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
        
        // For each star of the given star list
        vobsSTAR* star = NULL;

        // For each star of the list
        // note: Remove() and GetNextStar() ensure proper list traversal:
        for (star = list->GetNextStar(mcsTRUE); star != NULL; star = list->GetNextStar(mcsFALSE))
        {
            // Get the star ID (logs)
            mcsSTRING64 starId;
            if (star->GetId(starId, sizeof(starId)) == mcsFAILURE)
            {
                return mcsFAILURE;
            }

            // if the star is not like the reference star (according to criteria list)
            if (referenceStar.IsSame(star, criterias, nCriteria) == mcsFALSE)
            {
                // Remove it
                logTest("star '%s' has been removed by the filter '%s'", starId, GetId());
                
                if (list->Remove(*star) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }
            }
        }
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
