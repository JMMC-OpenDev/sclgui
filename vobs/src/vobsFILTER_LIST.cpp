/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsFILTER_LIST.cpp,v 1.1 2005-06-20 11:31:53 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of vobsFILTER_LIST class.
 */

static char *rcsId="@(#) $Id: vobsFILTER_LIST.cpp,v 1.1 2005-06-20 11:31:53 scetre Exp $"; 
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
#include "vobsFILTER_LIST.h"
#include "vobsPrivate.h"

/**
 * Class constructor
 */
vobsFILTER_LIST::vobsFILTER_LIST()
{
    _filterlistIterator = _filterList.begin();
}

/**
 * Class destructor
 */
vobsFILTER_LIST::~vobsFILTER_LIST()
{
}

/*
 * Public methods
 */

/**
 * Add a filter in the list
 *
 * @param filter the filter to add in the list
 *
 * \return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsFILTER_LIST::Add(vobsFILTER *filter)
{
    logTrace("vobsFILTER_LIST::Add()");

    // add the filter at the end of the list
    _filterList.push_back(filter);

    return mcsSUCCESS;
}

/**
 * Reset the list of filter
 *
 * this method remove all the filter of the list
 * 
 * \return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsFILTER_LIST::Reset(void)
{
    logTrace(" vobsFILTER_LIST::Reset()");

    // Remove all element of the list between the begining and the end of the
    // list, i.e remove ALL element of the list
    for (unsigned int el = 0; el < Size(); el++)
    {
        GetNextFilter((mcsLOGICAL)(el==0))->Disable();
    }
    
    return mcsSUCCESS;
}

/**
 * Returns the number of elements (filters) currently stored in the list.
 * \return 
 * The numbers of filters in the list.
 */
mcsUINT32 vobsFILTER_LIST::Size(void) 
{
    return _filterList.size();
}


/**
 * Returns the next element (filter) in the list.
 *
 * This method returns the pointer to the next element of the list. If \em
 * init is mcsTRUE, it returns the first element of the list.
 * 
 * This method can be used to move forward in the list, as shown below:
 * \code
 *     for (unsigned int el = 0; el < filterList.Size(); el++)
 *     {
 *         filterList.GetNextFilter((mcsLOGICAL)(el==0))-><method>;
 *     }
 * \endcode
 * \return pointer to the next element of the list or NULL if the end of the
 * list is reached.
 */
vobsFILTER *vobsFILTER_LIST::GetNextFilter(mcsLOGICAL init)
{
    logTrace("vobsFILTER_LIST::GetNextFilter()");
    
    if (init == mcsTRUE)
    {
        _filterlistIterator = _filterList.begin();
    }
    else
    {
        _filterlistIterator++;
        if (_filterlistIterator == _filterList.end())
        {
            return NULL;
        }
    }
    return (*_filterlistIterator);
}

/**
 * Get the filter which have corresponding name
 *
 * @param name the name of the wanted filter
 *
 * \return pointer to the element of the list or NULL if the element has not
 * been found.
 */
vobsFILTER *vobsFILTER_LIST::GetFilter(mcsSTRING32 name)
{
    logTrace("vobsFILTER_LIST::GetFilter()");
    vobsFILTER * filter = NULL;
    // Look in the list if the filter is in the list
    for (unsigned int el = 0; el < Size(); el++)
    {
        filter = GetNextFilter((mcsLOGICAL)(el==0));
        // if name have been found, break
        if (strcmp(filter->GetName(), name) == 0)
        {
            break;
        }
    }

    // return filter
    // If it has not been found, NULL will be return
    return filter;
}

/**
 * Apply all filter on a list
 *
 * \param list the list on which the filter will be apply
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned
 */
mcsCOMPL_STAT vobsFILTER_LIST::Apply(vobsSTAR_LIST *list)
{
    logTrace("vobsFILTER_LIST::Apply()");
    vobsFILTER * filter;
    for (unsigned int el = 0; el < Size(); el++)
    {
        filter = GetNextFilter((mcsLOGICAL)(el==0));
        if (filter->IsEnable() == mcsTRUE)
        {
            if (filter->Apply(list) == mcsFAILURE)
            {
                return mcsFAILURE;
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
