/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 *  Definition of sclsvrSCENARIO_BRIGHT_K_CATALOG class.
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
 * SCALIB Headers 
 */
#include "alx.h"

/*
 * Local Headers 
 */
#include "sclsvrSCENARIO_BRIGHT_K_CATALOG.h"
#include "sclsvrPrivate.h"

/**
 * Class constructor
 */
sclsvrSCENARIO_BRIGHT_K_CATALOG::sclsvrSCENARIO_BRIGHT_K_CATALOG(sdbENTRY* progress): vobsSCENARIO(progress),
    _originFilter("K origin = 2mass filter"),
    _magnitudeFilter("K mag filter"),
    _filterList("filter List")
{
}

/**
 * Class destructor
 */
sclsvrSCENARIO_BRIGHT_K_CATALOG::~sclsvrSCENARIO_BRIGHT_K_CATALOG()
{
}

/*
 * Public methods
 */

/**
 * Return the name of this scenario
 * @return "BRIGHT_K_CATALOG"
 */
const char* sclsvrSCENARIO_BRIGHT_K_CATALOG::GetScenarioName()
{
    return "BRIGHT_K_CATALOG";
}

/**
 * Initialize the BRIGHT K (JSDC) scenario
 *
 * @param request user request
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT sclsvrSCENARIO_BRIGHT_K_CATALOG::Init(vobsREQUEST * request)
{
    logTrace("sclsvrSCENARIO_BRIGHT_K_CATALOG::Init()");

    // Clear the scenario
    Clear();
    // Clear the list input and list output which will be used
    _starListP.Clear();
    _starListS.Clear();
    
    // BUILD REQUEST USED
    // Build Genaral request
    _request.Copy(*request);
    
    // Build the request for I/280
    _requestI280.Copy(_request);
    _requestI280.SetSearchBand("V");
    
    mcsDOUBLE kMax = _request.GetMaxMagRange();
    mcsDOUBLE vMax = kMax + 2.0;
    mcsDOUBLE vMin = 0.0;
    _requestI280.SetMinMagRange(vMin);
    _requestI280.SetMaxMagRange(vMax);

    // BUILD CRITERIA LIST
    if (InitCriteriaLists() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // BUILD FILTER USED

    // Build origin = 2MASS for Kmag filter
    _originFilter.SetOriginName(vobsCATALOG_MASS_ID ,vobsSTAR_PHOT_JHN_K);
    _originFilter.Enable();
    
    // Build filter on magnitude
    // Get research band
    mcsDOUBLE kMaxi = _request.GetMaxMagRange();
    mcsDOUBLE kMini = _request.GetMinMagRange();
    _magnitudeFilter.SetMagnitudeValue("K", (kMaxi+kMini) / 2., (kMaxi-kMini) / 2.);
    _magnitudeFilter.Enable();
    
    // Build filter list
    _filterList.Add(&_originFilter, "Origin Filter");
    _filterList.Add(&_magnitudeFilter, "Magnitude Filter");
    
    // PRIMARY REQUEST
    
    // TODO: analyse primary requests to verify cross matching constraints (radius / criteria)
    
    // I/280
    // Oct 2011: use _criteriaListRaDec to avoid duplicates:
    if (AddEntry(vobsCATALOG_ASCC_ID, &_requestI280, NULL, &_starListP, vobsCOPY, &_criteriaListRaDec, NULL, "&SpType=%5bOBAFGKM%5d*") == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // 2MASS
    // Oct 2011: use _criteriaListRaDec to avoid duplicates:
    if (AddEntry(vobsCATALOG_MASS_ID, &_request, &_starListP, &_starListP, vobsCOPY, &_criteriaListRaDec, &_filterList, "&opt=T") == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // II/225
    // Oct 2011: use _criteriaListRaDec to avoid duplicates:
    if (AddEntry(vobsCATALOG_CIO_ID, &_request, NULL, &_starListP, vobsMERGE, &_criteriaListRaDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // II/7A
    // Oct 2011: use _criteriaListRaDec to avoid duplicates:
    if (AddEntry(vobsCATALOG_PHOTO_ID, &_request, NULL, &_starListP, vobsMERGE, &_criteriaListRaDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    } 
    
    // I/280 bis
    // Oct 2011: use _criteriaListRaDec to avoid duplicates:
    if (AddEntry(vobsCATALOG_ASCC_ID, &_request, &_starListP, &_starListS, vobsCOPY, &_criteriaListRaDec, NULL, "&SpType=%5bOBAFGKM%5d*") == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    ////////////////////////////////////////////////////////////////////////
    // SECONDARY REQUEST
    ////////////////////////////////////////////////////////////////////////
    
    // Define the cone search radius to 1.1 arcsec used by Vizier queries > criteriaListRaDec ... (1 arcsec)
    _request.SetConeSearchRadius(1.1);
    
    // DENIS_JK
    if (AddEntry(vobsCATALOG_DENIS_JK_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // 2MASS
    if (AddEntry(vobsCATALOG_MASS_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDec, NULL, "&opt=T") == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // II/7A
    if (AddEntry(vobsCATALOG_PHOTO_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDecMagV) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // II/225
    if (AddEntry(vobsCATALOG_CIO_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }        
    // I/196
    if (AddEntry(vobsCATALOG_HIC_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDecHd) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // BSC
    if (AddEntry(vobsCATALOG_BSC_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDecHd) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // SBSC
    if (AddEntry(vobsCATALOG_SBSC_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDecHd) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // B/sb9
    if (AddEntry(vobsCATALOG_SB9_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // B/wsd/wsd
    if (AddEntry(vobsCATALOG_WDS_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDec) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Define the cone search radius to 2.1 arcsec used by Vizier queries > criteriaListRaDecAkari ... (2 arcsec)
    _request.SetConeSearchRadius(2.1);

    // II/297/irc aka AKARI
    if (AddEntry(vobsCATALOG_AKARI_ID, &_request, &_starListS, &_starListS, vobsUPDATE_ONLY, &_criteriaListRaDecAkari) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}


/*___oOo___*/
