#ifndef vobsBV_FILTER_H
#define vobsBV_FILTER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsBV_FILTER.h,v 1.5 2005-12-13 16:30:33 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/11/29 13:45:57  gzins
 * Removed filter name definition
 *
 * Revision 1.3  2005/11/29 10:26:41  gzins
 * Changed parent class to vobsFILTER
 *
 * Revision 1.2  2005/11/24 08:13:50  scetre
 * Changed mother class of filter from vobsFILTER to vobsFILTER
 *
 * Revision 1.1  2005/11/15 14:54:33  scetre
 * Added filter on origin and B-V
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of vobsBV_FILTER class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

/*
 * Local header
 */
#include "vobsFILTER.h"
#include "vobsSTAR_LIST.h"

/*
 * Class declaration
 */

/**
 * Magnitude B-V filter class.
 *
 * @sa vobsFILTER.cpp
 * @sa vobsSTAR_LIST.cpp
 * 
 */
class vobsBV_FILTER : public vobsFILTER
{

public:
    // Class constructor
    vobsBV_FILTER(const char* filterId);

    // Class destructor
    virtual ~vobsBV_FILTER();

    virtual mcsCOMPL_STAT SetMagnitudeValue(mcsDOUBLE magValue);

    virtual mcsCOMPL_STAT GetMagnitudeValue(mcsDOUBLE *magValue);

    virtual mcsCOMPL_STAT Apply(vobsSTAR_LIST *list);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    vobsBV_FILTER(const vobsBV_FILTER&);
    vobsBV_FILTER& operator=(const vobsBV_FILTER&);

    mcsDOUBLE _magValue;
};

#endif /*!vobsBV_FILTER_H*/

/*___oOo___*/
