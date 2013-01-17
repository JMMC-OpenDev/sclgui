#ifndef vobsCATALOG_SSBSC_H
#define vobsCATALOG_SSBSC_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

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
#include "vobsREMOTE_CATALOG.h"

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

class vobsCATALOG_SBSC : public vobsREMOTE_CATALOG
{
public:
    // Constructor
    vobsCATALOG_SBSC();

    // Destructor
    virtual ~vobsCATALOG_SBSC();

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    vobsCATALOG_SBSC(const vobsCATALOG_SBSC&);
    vobsCATALOG_SBSC& operator=(const vobsCATALOG_SBSC&);

    // Method to build specific CDS query part
    virtual mcsCOMPL_STAT WriteQuerySpecificPart(void);

private:
};

#endif /*!vobsCATALOG_SSBSC_H*/


/*___oOo___*/
