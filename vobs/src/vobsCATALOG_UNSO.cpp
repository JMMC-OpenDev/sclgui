/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsCATALOG_UNSO.cpp,v 1.4 2006-03-03 15:03:27 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/11/24 14:54:25  scetre
 * UNSO catalog added
 *
 * Revision 1.2  2005/11/23 08:34:15  scetre
 * Added property for faint K scenario
 *
 * Revision 1.1  2005/11/21 13:49:26  scetre
 * UNSO catalog added
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of vobsCATALOG_UNSO class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: vobsCATALOG_UNSO.cpp,v 1.4 2006-03-03 15:03:27 scetre Exp $"; 

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
#include "vobsCATALOG_UNSO.h"
#include "vobsPrivate.h"

/**
 * Class constructor
 */
vobsCATALOG_UNSO::vobsCATALOG_UNSO()  : vobsREMOTE_CATALOG("I/284")
{
}

/**
 * Class destructor
 */
vobsCATALOG_UNSO::~vobsCATALOG_UNSO()
{
    miscDynBufDestroy(&_query);    
}

/*
 * Public methods
 */


/*
 * Protected methods
 */
/**
 * Build the specificatic part of the asking.
 *
 * Build the specificatic part of the asking. This is the part of the asking
 * which is write specificaly for each catalog.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 * 
 */
mcsCOMPL_STAT vobsCATALOG_UNSO::WriteQuerySpecificPart(void)
{
    logTrace("vobsCATALOG_UNSO::GetAskingSpecificParameters()");
    
    miscDynBufAppendString(&_query, "&-out=*POS_EQ_PMDEC");
    miscDynBufAppendString(&_query, "&-out=*POS_EQ_PMRA");
    miscDynBufAppendString(&_query, "&-out=*PHOT_PHG_I");
    miscDynBufAppendString(&_query, "&-out=*PHOT_PHG_R");
    miscDynBufAppendString(&_query, "&-out=*PHOT_PHG_B");
    miscDynBufAppendString(&_query, "&-sort=_r");
    
    return mcsSUCCESS;
}


/*
 * Private methods
 */


/*___oOo___*/
