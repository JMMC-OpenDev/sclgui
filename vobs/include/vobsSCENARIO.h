#ifndef vobsSCENARIO_H
#define vobsSCENARIO_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsSCENARIO.h,v 1.5 2005-01-27 13:44:04 scetre Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.4  2005/01/26 08:10:32  scetre
* change history
*
* scetre    07-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * Brief description of the header file, which ends at this dot.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * header files
 */
#include <list>

/*
 * local Header
 */
#include "vobsSTAR_LIST.h"
#include "vobsCATALOG.h"
#include "vobsSTAR_COMP_CRITERIA_LIST.h"
#include "vobsSCENARIO_ENTRY.h"

/*
 * Class declaration
 */

/**
 * vobsSCENARIO is a class which represent the catalog interrogation scenario 
 * of the catalogues.
 * 
 * vobsSCENARIO methods allow to
 * \li Add action into the scenario : an action is represented by the
 * catalog of destination and the specific action to apply
 * \li Execute all the action loaded
 * 
 */

class vobsSCENARIO
{
public :
    vobsSCENARIO();
    virtual ~vobsSCENARIO(); 
    
    // Add an entry in the list of actions
    virtual mcsCOMPL_STAT AddEntry(vobsCATALOG   *catalog,
                                   vobsSTAR_LIST *listInput,
                                   vobsSTAR_LIST *listOutput,
                                   vobsACTION action,
                                   vobsSTAR_COMP_CRITERIA_LIST criteriaList);
  
    // Execute the scenario
    mcsCOMPL_STAT Execute(vobsREQUEST &request, vobsSTAR_LIST &starList);

protected :

private :
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    vobsSCENARIO& operator=(const vobsSCENARIO&);
    vobsSCENARIO (const vobsSCENARIO&);

    
    // List of entries
    std::list<vobsSCENARIO_ENTRY>            _entryList;
    std::list<vobsSCENARIO_ENTRY>::iterator  _entryIterator;
};



#endif /*!vobsSCENARIO_H*/

/*___oOo___*/
