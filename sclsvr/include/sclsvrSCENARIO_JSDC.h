#ifndef sclsvrSCENARIO_JSDC_H
#define sclsvrSCENARIO_JSDC_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of sclsvrSCENARIO_JSDC class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "vobs.h"


/*
 * Class declaration
 */

/**
 * Specific scenario for N band research
 */
class sclsvrSCENARIO_JSDC : public vobsSCENARIO
{

public:
    // Class constructor
    sclsvrSCENARIO_JSDC(sdbENTRY* progress);

    // Class destructor
    virtual ~sclsvrSCENARIO_JSDC();

    virtual mcsCOMPL_STAT Init(vobsREQUEST* request);

    virtual const char* GetScenarioName();
    
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclsvrSCENARIO_JSDC(const sclsvrSCENARIO_JSDC&);
    sclsvrSCENARIO_JSDC& operator=(const sclsvrSCENARIO_JSDC&);

    // Star lists used in scenarion
    vobsSTAR_LIST     _starListS;

    vobsREQUEST _request;
};

#endif /*!sclsvrSCENARIO_JSDC_H*/

/*___oOo___*/
