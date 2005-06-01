/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrSPECTRAL_TYPE_FILTER.cpp,v 1.1 2005-06-01 14:18:54 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclsvrSPECTRAL_TYPE_FILTER class.
 */

static char *rcsId="@(#) $Id: sclsvrSPECTRAL_TYPE_FILTER.cpp,v 1.1 2005-06-01 14:18:54 scetre Exp $"; 
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
 * Local Headers 
 */
#include "sclsvrSPECTRAL_TYPE_FILTER.h"
#include "sclsvrPrivate.h"

/**
 * Class constructor
 */
sclsvrSPECTRAL_TYPE_FILTER::sclsvrSPECTRAL_TYPE_FILTER()
{
    strcpy(_name, sclsvrSPECTRAL_TYPE_FILTER_NAME);        
}

/**
 * Class destructor
 */
sclsvrSPECTRAL_TYPE_FILTER::~sclsvrSPECTRAL_TYPE_FILTER()
{
}

/*
 * Public methods
 */
/**
 * Set value to the filter
 *
 * \param tempClassList the temperature list
 *
 * \return always mcsSUCCESS 
 */
mcsCOMPL_STAT sclsvrSPECTRAL_TYPE_FILTER::SetSpectralType(char **tempClassList)
{
    logTrace("sclsvrSPECTRAL_TYPE_FILTER::SetSpectralType()");

    _tempClassList = tempClassList;
    
    return mcsSUCCESS;    
}

/**
 * Get value to the filter
 *
 * \param tempClassList the temperature list
 *
 * \return always mcsSUCCESS 
 */
mcsCOMPL_STAT sclsvrSPECTRAL_TYPE_FILTER::GetSpectralType(char **tempClassList)
{
    logTrace("sclsvrSPECTRAL_TYPE_FILTER::GetSpectralType()");

    tempClassList = _tempClassList;
    
    return mcsSUCCESS;    
}

/**
 * Apply spectral filter on a list
 *
 * @param list the list on which the filter is applied
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclsvrSPECTRAL_TYPE_FILTER::Apply(sclsvrCALIBRATOR_LIST *list)
{
    logTrace("sclsvrSPECTRAL_TYPE_FILTER::Apply()");

    if (IsEnable() == mcsTRUE)
    {
        // For each calibrator in the list
        sclsvrCALIBRATOR *calibrator;
        for (unsigned int el = 0; el < list->Size(); el++)
        {
            calibrator = 
                (sclsvrCALIBRATOR *)list->GetNextStar((mcsLOGICAL)(el==0));

            // If spectral type is unknown
            if (calibrator->IsPropertySet(vobsSTAR_SPECT_TYPE_MK) == mcsFALSE)
            {
                // Remove it
                if (list->Remove(*calibrator) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }
                el = el - 1;
            }
            // Else
            else
            {
                // Get the star spectral type 
                const char *spType;
                spType = calibrator-> GetPropertyValue(vobsSTAR_SPECT_TYPE_MK);
                // Look for temperature class(es). Stop when end of string 
                // reached or first character of luminosity class found 
                mcsINT32 spIdx = 0; // Index on spectral type string
                mcsLOGICAL tempClassMatch = mcsFALSE;
                while ((spType[spIdx] != '\0') && 
                       (spType[spIdx] != 'I') && (spType[spIdx] != 'V'))
                {
                    // If the list of temperature classes has been given
                    if (_tempClassList != NULL)
                    {
                        // For ecah given temperature class
                        mcsINT32 classIdx = 0;
                        while (_tempClassList[classIdx] != NULL)
                        {
                            // Check if 
                            if (spType[spIdx] == _tempClassList[classIdx][0])
                            {
                                tempClassMatch = mcsTRUE;
                            }
                            classIdx++;
                        }
                    }
                    // Else
                    else
                    {
                        // Do not filter
                        tempClassMatch = mcsTRUE;
                    }
                    spIdx++;
                }
                // If the spectral of the list is not one of the list
                if (tempClassMatch == mcsFALSE)
                {
                    // Remove it
                    if (list->Remove(*calibrator) == mcsFAILURE)
                    {
                        return mcsFAILURE;
                    }
                    el = el-1;            
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
