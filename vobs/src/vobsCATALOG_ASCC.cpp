/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsCATALOG_ASCC.cpp,v 1.4 2005-02-07 19:40:58 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.3  2005/01/26 08:11:28  scetre
* change history
*
* scetre    28-Jul-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * vobsCATALOG_ASCC class definition.
 */


static char *rcsId="@(#) $Id: vobsCATALOG_ASCC.cpp,v 1.4 2005-02-07 19:40:58 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <iostream>
using namespace std; 
/**
 * Export standard iostream objects (cin, cout,...).
 */

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "misc.h"

/*
 * Local Headers 
 */
#include "vobsCATALOG_ASCC.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"

/*
 * Class constructor
 */

/**
 * Build a catalog object.
 */
vobsCATALOG_ASCC::vobsCATALOG_ASCC()
{
    strcpy(_name,"I/280");
}

/*
 * Class destructor
 */

/**
 * Delete a catalog object. 
 */
vobsCATALOG_ASCC::~vobsCATALOG_ASCC()
{
    miscDynBufDestroy(&_query);
}

/*
 * Protected methods
 */

/**
 * Build the specificatic part of the asking.
 *
 * Build the specificatic part of the asking. This is the part of the asking
 * which is write specificaly for each catalog.
 *
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
 * 
 * \b Errors codes:\n
 * The possible errors are:
 *
 */
mcsCOMPL_STAT vobsCATALOG_ASCC::WriteQuerySpecificPart(void)
{
    logExtDbg("vobsCATALOG_ASCC::GetAskingSpecificParameters()");
    
    miscDynBufAppendString(&_query, "&-out=*POS_EQ_PMDEC&-out=*POS_EQ_PMRA");
    miscDynBufAppendString(&_query, "&-out=*POS_PARLX_TRIG");
    miscDynBufAppendString(&_query, "&-out=*SPECT_TYPE_MK");
    miscDynBufAppendString(&_query, "&SpType=%5bOBAFGKM%5d*");
    miscDynBufAppendString(&_query, "&-out=*PHOT_JHN_B&-out=*PHOT_JHN_V");
    miscDynBufAppendString(&_query, "&-out=v1&-out=d5&-out=HIP&-out=HD");
    miscDynBufAppendString(&_query, "&-out=DM&-sort=_r");
    
    return mcsSUCCESS;
}

/**
 * Build the specificatic part of the asking.
 *
 * Build the specificatic part of the asking. This is the part of the asking
 * which is write specificaly for each catalog. The constraints of the request
 * which help to build an asking in order to restrict the research.
 *
 * \param request vobsREQUEST which help to restrict the search
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
 * 
 * \b Errors codes:\n
 * The possible errors are:
 *
 */
mcsCOMPL_STAT vobsCATALOG_ASCC::WriteQuerySpecificPart(vobsREQUEST &request)
{
    logExtDbg("vobsCATALOG_ASCC::GetAskingSpecificParameters()");

    miscDynBufAppendString(&_query, "&");
    
    // Add band constraint
    const char *band;
    band = request.GetSearchBand();
    miscDynBufAppendString(&_query, band);
    
    // Add the magnitude range constraint
    mcsSTRING32 deltaMag;
    mcsFLOAT minDeltaMag;
    mcsFLOAT maxDeltaMag;
    minDeltaMag = request.GetMinDeltaMag();
    maxDeltaMag = request.GetMaxDeltaMag();
    sprintf(deltaMag, "%.2f..%.2f", minDeltaMag, maxDeltaMag);
    miscDynBufAppendString(&_query, "mag=");
    miscDynBufAppendString(&_query, deltaMag);

    // Add search box size
    mcsSTRING32 separation;
    mcsFLOAT deltaRa;
    mcsFLOAT deltaDec;
    deltaRa = request.GetDeltaRa();
    deltaDec = request.GetDeltaDec();
    sprintf(separation, "%.0f/%.0f", deltaRa, deltaDec);
    miscDynBufAppendString(&_query, "&-c.eq=J2000&-out.max=100&-c.bm=");
    miscDynBufAppendString(&_query, separation);
    miscDynBufAppendString(&_query, "&-c.u=arcsec");
    miscDynBufAppendString(&_query, "&-out.add=_RAJ2000,_DEJ2000&-oc=hms");
    miscDynBufAppendString(&_query, "&-out=*POS_EQ_PMDEC&-out=*POS_EQ_PMRA");
    miscDynBufAppendString(&_query, "&-out=*POS_PARLX_TRIG");
    miscDynBufAppendString(&_query, "&-out=*SPECT_TYPE_MK");
    miscDynBufAppendString(&_query, "&SpType=%5bOBAFGKM%5d*");
    miscDynBufAppendString(&_query, "&-out=*PHOT_JHN_B&-out=*PHOT_JHN_V");
    miscDynBufAppendString(&_query, "&-out=v1&-out=d5");
    miscDynBufAppendString(&_query, "&-out=HIP&-out=HD&-out=DM&-sort=_r"); 
    
    return mcsSUCCESS;
}

/*___oOo___*/
