#ifndef vobsCATALOG_SSBSC_H
#define vobsCATALOG_SSBSC_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsCATALOG_SBSC.h,v 1.5 2005-01-26 08:10:32 scetre Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* scetre    27-Jul-2004  Created
*
*
*******************************************************************************/
/**
 * \file
 * vobsCATALOG_SBSC class declaration.
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
 * vobsCATALOG_SBSC is a class which caracterise the Suplement to the BSC.
 * 
 * vobsCATALOG_SBSC methods allow to
 * \li Prepare a request
 * \li Send this request to the CDS
 * \li Build a star list from the CDS answer
 *
 * 
 */

class vobsCATALOG_SBSC : public vobsCATALOG
{
public:
    // Constructor
   vobsCATALOG_SBSC();

   // Destructor
   virtual ~vobsCATALOG_SBSC();
protected:
    // Method to build parts of the request
    virtual mcsCOMPL_STAT WriteQuerySpecificPart(void);

private:
};

#endif /*!vobsCATALOG_SSBSC_H*/


/*___oOo___*/
