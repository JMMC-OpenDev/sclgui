/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 *  Definition of sclsvrSCENARIO_BRIGHT_K class.
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
#include "sclsvrSCENARIO_BRIGHT_K.h"
#include "sclsvrPrivate.h"

/**
 * Class constructor
 */
sclsvrSCENARIO_BRIGHT_K::sclsvrSCENARIO_BRIGHT_K(sdbENTRY* progress) : vobsSCENARIO(progress),
_starList("Main"),
_originFilter("K origin = 2mass filter"),
_magnitudeFilter("K mag filter"),
_filterList("filter List")
{
}

/**
 * Class destructor
 */
sclsvrSCENARIO_BRIGHT_K::~sclsvrSCENARIO_BRIGHT_K()
{
}

/*
 * Public methods
 */

/**
 * Return the name of this scenario
 * @return "BRIGHT_K"
 */
const char* sclsvrSCENARIO_BRIGHT_K::GetScenarioName() const
{
    return "BRIGHT_K";
}

/**
 * Initialize the BRIGHT K scenario
 *
 * @param request the user constraint the found stars should conform to
 * @param starList optional input list
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
 */
mcsCOMPL_STAT sclsvrSCENARIO_BRIGHT_K::Init(vobsSCENARIO_RUNTIME &ctx, vobsREQUEST* request, vobsSTAR_LIST* starList)
{
    // Clear the storage lists
    _starList.Clear();

    // BUILD REQUEST USED
    // Build General request
    _request.Copy(*request);

    // Build the request for I/280
    _requestI280.Copy(_request);
    _requestI280.SetSearchBand("V");

    const mcsDOUBLE kMaxi = _request.GetMaxMagRange();
    const mcsDOUBLE kMini = _request.GetMinMagRange();

    _requestI280.SetMinMagRange(0.0);
    _requestI280.SetMaxMagRange(kMaxi + 4.0); // GD: Nov 2012: K + 4 to get more giant stars

    // BUILD CRITERIA LIST
    FAIL(InitCriteriaLists());

    // BUILD FILTER USED
    // Build origin = 2MASS for Kmag filter
    _originFilter.SetCriteria(vobsSTAR_PHOT_JHN_K, vobsCATALOG_MASS_ID);
    _originFilter.Enable();

    // Build filter on magnitude
    // Get research band
    const char* band = _request.GetSearchBand();

    _magnitudeFilter.SetMagnitudeValue(band, 0.5 * (kMaxi + kMini), 0.5 * (kMaxi - kMini));
    _magnitudeFilter.Enable();

    // Build filter list
    _filterList.Add(&_originFilter, "Origin Filter");
    _filterList.Add(&_magnitudeFilter, "Magnitude Filter");

    // PRIMARY REQUEST

    // I/280
    FAIL(AddEntry(vobsCATALOG_ASCC_ID, &_requestI280, NULL, &_starList, vobsCLEAR_MERGE, &_criteriaListRaDec));

    ////////////////////////////////////////////////////////////////////////
    // SECONDARY REQUEST
    ////////////////////////////////////////////////////////////////////////

    // I/311 - Hipparcos, the New Reduction (van Leeuwen, 2007)
    // to fix Plx / pmRa/Dec (just after ASCC):
    FAIL(AddEntry(vobsCATALOG_HIP2_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));

    // HIP1 - V / B / Ic (2013-04-18)
    FAIL(AddEntry(vobsCATALOG_HIP1_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));

    // 2MASS with K mag filter:
    FAIL(AddEntry(vobsCATALOG_MASS_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec2MASS, &_filterList));

    // II/328/allwise aka WISE (LMN)
    FAIL(AddEntry(vobsCATALOG_WISE_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDecAkari));

    // DENIS_JK - J-K DENIS photometry of bright southern stars (Kimeswenger+ 2004)
    FAIL(AddEntry(vobsCATALOG_DENIS_JK_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));

    // II/7A - UBVRIJKLMNH Photoelectric Catalogue
    FAIL(AddEntry(vobsCATALOG_PHOTO_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDecMagV));

    // II/225 - Catalog of Infrared Observations, Edition 5 (Gezari+ 1999)
    FAIL(AddEntry(vobsCATALOG_CIO_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));

    // I/196
    FAIL(AddEntry(vobsCATALOG_HIC_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDecHd));

    // BSC - Bright Star Catalogue, 5th Revised Ed. (Hoffleit+, 1991)
    FAIL(AddEntry(vobsCATALOG_BSC_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDecHd));

    // SBSC - Supplement to the Bright Star Catalogue (Hoffleit+ 1983)
    FAIL(AddEntry(vobsCATALOG_SBSC_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDecHd));

    if (vobsCATALOG_DENIS_ID_ENABLE)
    {
        // B/denis - so far not able to use Denis properly
        FAIL(AddEntry(vobsCATALOG_DENIS_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));
    }

    // B/sb9 - 9th Catalogue of Spectroscopic Binary Orbits (Pourbaix+ 2004-2013)
    FAIL(AddEntry(vobsCATALOG_SB9_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));

    // B/wds/wds - Washington Visual Double Star Catalog (Mason+ 2001-2013)
    FAIL(AddEntry(vobsCATALOG_WDS_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDec));

    // II/297/irc aka AKARI
    FAIL(AddEntry(vobsCATALOG_AKARI_ID, &_request, &_starList, &_starList, vobsUPDATE_ONLY, &_criteriaListRaDecAkari));

    return mcsSUCCESS;
}


/*___oOo___*/
