#ifndef vobsREMOTE_CATALOG_H
#define vobsREMOTE_CATALOG_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsREMOTE_CATALOG.h,v 1.2 2006-01-18 08:45:38 scetre Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.1  2005/02/11 14:14:31  gluck
* Added vobsLOCAL_CATALOG  and vobsREMOTE_CATALOG classes to have a more coherent and homogenous inheritance tree
*
* scetre    27-Jul-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * Declaration of vobsREMOTE_CATALOG class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * MCS header
 */
#include "misc.h"

/*
 * Local header
 */
#include "vobsCATALOG.h"


/*
 * Class declaration
 */

/**
 * vobsCATALOG is a class which caracterise a remote catalog.
 * 
 * vobsCATALOG methods allow to
 * \li Prepare a request
 * \li Send this request to the CDS
 * \li Build a star list from the CDS answer
 *
 * 
 */
class vobsREMOTE_CATALOG : public vobsCATALOG
{
public:
   // Constructor
   vobsREMOTE_CATALOG(const char *name);

   // Destructor
   virtual ~vobsREMOTE_CATALOG();

   // Method to get a  star list from the catalog
   virtual mcsCOMPL_STAT Search(vobsREQUEST &request,
                                vobsSTAR_LIST &list);
   
   virtual mcsCOMPL_STAT SetOption(string option);
protected:
    // Method to prepare the request in a string format
   virtual mcsCOMPL_STAT PrepareQuery(vobsREQUEST &request);
   virtual mcsCOMPL_STAT PrepareQuery(vobsREQUEST &request,
                                      vobsSTAR_LIST &tmpList);

   // Method to build all parts of the asking
   virtual mcsCOMPL_STAT WriteQueryURIPart(void);
   virtual mcsCOMPL_STAT WriteQueryConstantPart(void);
   virtual mcsCOMPL_STAT WriteQuerySpecificPart(void);
   virtual mcsCOMPL_STAT WriteQuerySpecificPart(vobsREQUEST &request);
   virtual mcsCOMPL_STAT WriteReferenceStarPosition(vobsREQUEST &request);
   virtual mcsCOMPL_STAT WriteQueryStarListPart(vobsSTAR_LIST &list);
  
   // Write option
   virtual mcsCOMPL_STAT WriteOption(void); 
   
   // Method to get a star list in a string format from a normal star list
   // format
   virtual mcsCOMPL_STAT StarList2Sring(miscDYN_BUF &strList,
                                        vobsSTAR_LIST &list);

   // Request to write and to send to the CDS
   miscDYN_BUF _query;

private:
   // Declaration of assignment operator as private
   // method, in order to hide them from the users.
   vobsREMOTE_CATALOG& operator=(const vobsCATALOG&);
   vobsREMOTE_CATALOG (const vobsCATALOG&);

};

#endif /*!vobsREMOTE_CATALOG_H*/


/*___oOo___*/