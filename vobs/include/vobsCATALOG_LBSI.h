#ifndef vobsCATALOG_LBSI_H
#define vobsCATALOG_LBSI_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsCATALOG_LBSI.h,v 1.4 2004-11-30 10:32:31 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    27-Jul-2004  Created
*
*
*******************************************************************************/
/**
 * \file
 * vobsCATALOG_LBSI class declaration.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * Local header
 */
#include "vobsCATALOG.h"

/*
 * Class declaration
 */

/**
 * vobsCATALOG_LBSI is a class which caracterise the Catalog of calibrators
 * star for LBSI.
 * 
 * vobsCATALOG_LBSI methods allow to
 * \li Prepare a request
 * \li Send this request to the CDS
 * \li Build a star list from the CDS answer
 *
 * 
 */

class vobsCATALOG_LBSI : public vobsCATALOG
{
public:
    // Constructor
   vobsCATALOG_LBSI();

   // Destructor
   virtual ~vobsCATALOG_LBSI();

protected:
    // Method to build parts of the request
    virtual mcsCOMPL_STAT WriteQuerySpecificPart(void);

private:
};


#endif /*!vobsCATALOG_LBSI_H*/


/*___oOo___*/
