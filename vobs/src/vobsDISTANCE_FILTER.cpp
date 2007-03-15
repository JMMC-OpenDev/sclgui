/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsDISTANCE_FILTER.cpp,v 1.9 2006-04-07 08:23:00 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2006/03/03 15:03:27  scetre
 * Changed rcsId to rcsId __attribute__ ((unused))
 *
 * Revision 1.7  2006/03/01 16:41:38  lafrasse
 * Added const attributes for SetDistanceValue() method parameters.
 * Replaced all the 'strcpy' calls with 'strncpy'.
 * Code review
 *
 * Revision 1.6  2005/12/14 15:07:53  scetre
 * Added log information about deleted star in filters
 *
 * Revision 1.5  2005/12/13 16:30:33  lafrasse
 * Added filter Id management through additional constructor parameter
 *
 * Revision 1.4  2005/11/29 13:49:19  gzins
 * Removed filter name setting
 *
 * Revision 1.3  2005/11/29 10:33:02  gzins
 * Minor changes
 *
 * Revision 1.2  2005/06/21 06:20:45  scetre
 * Changed method Disable() and Enable() to Disabled() and Enabled()
 * Changed '\' in doxygen documentatiuon to '@'
 *
 * Revision 1.1  2005/06/20 11:44:25  scetre
 * Added filter class
 *
 * Revision 1.2  2005/06/07 12:36:27  scetre
 * Updated documentation
 *
 * Revision 1.1  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 ******************************************************************************/

/**
 * @file
 * Definition of vobsDISTANCE_FILTER class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: vobsDISTANCE_FILTER.cpp,v 1.9 2006-04-07 08:23:00 gzins Exp $"; 

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
#include "vobsDISTANCE_FILTER.h"
#include "vobsPrivate.h"

/**
 * Class constructor
 */
vobsDISTANCE_FILTER::vobsDISTANCE_FILTER(const char* filterId):vobsFILTER(filterId)
{
}

/**
 * Class destructor
 */
vobsDISTANCE_FILTER::~vobsDISTANCE_FILTER()
{
}

/*
 * Public methods
 */
/**
 * Set value to the filter
 *
 * @param raRef right ascension of the science star
 * @param decRef declinaison of the science star
 * @param raRange right ascension range
 * @param decRange declinaision range
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsDISTANCE_FILTER::SetDistanceValue(const mcsSTRING32  raRef,
                                                    const mcsSTRING32  decRef,
                                                    const mcsFLOAT     raRange,
                                                    const mcsFLOAT     decRange)
{
    logTrace("vobsDISTANCE_FILTER::SetDistanceValue()");

    // Copy right ascension and declinaison get in parameter
    strncpy(_raRef,  raRef,  sizeof(_raRef));
    strncpy(_decRef, decRef, sizeof(_decRef));

    // Copy right ascension and declinaison range get as parameter
    _raRange  = raRange;
    _decRange = decRange;
    
    return mcsSUCCESS;
}

/**
 * Get value of the filter 
 *
 * @param raRef right ascension of the science star
 * @param decRef declinaison of the science star
 * @param raRange right ascension range
 * @param decRange declinaision range
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsDISTANCE_FILTER::GetDistanceValue(mcsSTRING32 *raRef,
                                                    mcsSTRING32 *decRef,
                                                    mcsFLOAT *raRange,
                                                    mcsFLOAT *decRange)
{
    logTrace("vobsDISTANCE_FILTER::SetDistanceValue()");
    
    // Copy right ascension and declinaison get in parameter
    strncpy(*raRef,  _raRef,  sizeof(*raRef));
    strncpy(*decRef, _decRef, sizeof(*decRef));

    // Copy right ascension and declinaison range get as parameter
    *raRange  = _raRange;
    *decRange = _decRange;

    return mcsSUCCESS;
}

/**
 * Apply the filter on a list
 *
 * @param list star list on wich the filter is applied
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT vobsDISTANCE_FILTER::Apply(vobsSTAR_LIST *list)
{
    logTrace("vobsDISTANCE_FILTER::Apply()");

    // Create a star correponding to the science object
    vobsSTAR scienceStar;

    // Set right ascension property (ref) to this star
    if (scienceStar.SetPropertyValue(vobsSTAR_POS_EQ_RA_MAIN, _raRef, "") ==
        mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set declinaison property (ref) to this star
    if (scienceStar.SetPropertyValue(vobsSTAR_POS_EQ_DEC_MAIN, _decRef, "") ==
        mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Create a criterialist
    vobsSTAR_COMP_CRITERIA_LIST criteriaList;

    // Add criteria on right ascension
    if (criteriaList.Add(vobsSTAR_POS_EQ_RA_MAIN, _raRange) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Add criteria on declinaison
    if (criteriaList.Add(vobsSTAR_POS_EQ_DEC_MAIN, _decRange) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // For each star of the list
    vobsSTAR* star;
    for (unsigned int el = 0; el < list->Size(); el++)
    {
        star = list->GetNextStar((mcsLOGICAL)(el == 0));

        // Get Star ID
        mcsSTRING32 starId;
        if (star->GetId(starId, sizeof(starId)) == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // If the star is different of the science star according to the
        // criteria list
        if (star->IsSame(scienceStar, &criteriaList) == mcsFALSE)
        {
            // Remove it
            logTest("star %s not in the box", starId);
            list->Remove(*star);
            el = el-1;
        }
        else
        {
            logTest("star %s in the box", starId);
        }
    }
    
    return mcsSUCCESS;    
}


/*___oOo___*/
