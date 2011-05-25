#ifndef sclguiMULTIPLICITY_FILTER_VIEW_H
#define sclguiMULTIPLICITY_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiMULTIPLICITY_FILTER_VIEW class.
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
#include "sclguiFILTER_VIEW.h"


/*
 * Class declaration
 */
/**
 * Multiplicity filter view 
 */
class sclguiMULTIPLICITY_FILTER_VIEW : public sclguiFILTER_VIEW
{
public:
    // Class constructor
    sclguiMULTIPLICITY_FILTER_VIEW();

    // Class destructor
    virtual ~sclguiMULTIPLICITY_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual string GetChoice();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiMULTIPLICITY_FILTER_VIEW(const sclguiMULTIPLICITY_FILTER_VIEW&);
    sclguiMULTIPLICITY_FILTER_VIEW& operator=(const sclguiMULTIPLICITY_FILTER_VIEW&);

    virtual mcsLOGICAL IsMultiplicityAuthorized();
    
    gwtCHOICE _multiplicityChoice;
};

#endif /*!sclguiMULTIPLICITY_FILTER_VIEW_H*/

/*___oOo___*/
