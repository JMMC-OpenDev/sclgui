/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsVARIABILITY_FILTER.cpp,v 1.2 2005-06-21 06:20:45 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/06/20 11:31:53  scetre
 * Added filter class
 *
 * Revision 1.2  2005/06/07 12:36:28  scetre
 * Updated documentation
 *
 * Revision 1.1  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of vobsVARIABILITY_FILTER class.
 */

static char *rcsId="@(#) $Id: vobsVARIABILITY_FILTER.cpp,v 1.2 2005-06-21 06:20:45 scetre Exp $"; 
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
#include "vobsVARIABILITY_FILTER.h"
#include "vobsPrivate.h"

/**
 * Class constructor
 */
vobsVARIABILITY_FILTER::vobsVARIABILITY_FILTER()
{
    strcpy(_name, vobsVARIABILITY_FILTER_NAME);        
}

/**
 * Class destructor
 */
vobsVARIABILITY_FILTER::~vobsVARIABILITY_FILTER()
{
}

/*
 * Public methods
 */
/**
 * Apply variability filter on a list
 *
 * @param list the list on which the filter is applied
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT vobsVARIABILITY_FILTER::Apply(vobsSTAR_LIST *list)
{
    logTrace("vobsVARIABILITY_FILTER::Apply()");

    if (IsEnabled() == mcsTRUE)
    {
        vobsSTAR *star;
        for (unsigned int el = 0; el < list->Size(); el++)
        {
            star=
                (vobsSTAR *)list->GetNextStar((mcsLOGICAL)(el==0));
            // if it is not possible to get the visibility, remove the star
            if (star->IsPropertySet(vobsSTAR_CODE_VARIAB) == mcsTRUE)
            {
                // Remove it
                logTest("star %d had variability %s\n",
                        el+1, 
                        star->GetPropertyValue(vobsSTAR_CODE_VARIAB));
                if (list->Remove(*star) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }
                el = el-1;            
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
