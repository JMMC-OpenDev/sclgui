#ifndef vobsLUMINOSITY_FILTER_H
#define vobsLUMINOSITY_FILTER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsLUMINOSITY_FILTER.h,v 1.1 2005-06-20 11:31:53 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/06/07 12:35:59  scetre
 * Updated documentation
 *
 * Revision 1.1  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of vobsLUMINOSITY_FILTER class.
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


#define vobsLUMINOSITY_FILTER_NAME "Filter by luminosity"
#define vobsNB_LUMINOSITY_CLASS 6
/*
 * Class declaration
 */

/**
 * luminosity filter class.
 * 
 * This class is a vobsFILTER object.
 *
 * @sa vobsFILTER.cpp
 * @sa vobsSTAR_LIST.cpp
 * 
 */
class vobsLUMINOSITY_FILTER : public vobsFILTER
{

public:
    // Class constructor
    vobsLUMINOSITY_FILTER();

    // Class destructor
    virtual ~vobsLUMINOSITY_FILTER();

    virtual mcsCOMPL_STAT
        SetLuminosity(std::list<char *> lumClassList);

    virtual std::list<char *> * GetLuminosity();

    virtual mcsCOMPL_STAT Apply(vobsSTAR_LIST *list);
   
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    vobsLUMINOSITY_FILTER(const vobsLUMINOSITY_FILTER&);
    vobsLUMINOSITY_FILTER& operator=(const vobsLUMINOSITY_FILTER&);

    std::list<char *> _luminosityClassList;
};

#endif /*!vobsLUMINOSITY_FILTER_H*/

/*___oOo___*/
