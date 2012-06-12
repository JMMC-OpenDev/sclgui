#ifndef sclsvrSCENARIO_BRIGHT_V_H
#define sclsvrSCENARIO_BRIGHT_V_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of sclsvrSCENARIO_BRIGHT_V class.
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
 * Specific scenario for V band research
 */
class sclsvrSCENARIO_BRIGHT_V : public vobsSCENARIO
{

public:
    // Class constructor
    sclsvrSCENARIO_BRIGHT_V(sdbENTRY* progress);

    // Class destructor
    virtual ~sclsvrSCENARIO_BRIGHT_V();

    virtual mcsCOMPL_STAT Init(vobsREQUEST* request);

    virtual const char* GetScenarioName();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclsvrSCENARIO_BRIGHT_V(const sclsvrSCENARIO_BRIGHT_V&);
    sclsvrSCENARIO_BRIGHT_V& operator=(const sclsvrSCENARIO_BRIGHT_V&);

    // Star lists used in scenarion
    vobsSTAR_LIST _starListS;

    vobsREQUEST   _request;
};

#endif /*!sclsvrSCENARIO_BRIGHT_V_H*/

/*___oOo___*/
