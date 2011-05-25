#ifndef sclguiLUMINOSITY_FILTER_VIEW_H
#define sclguiLUMINOSITY_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiLUMINOSITY_FILTER_VIEW class.
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
 * Luminosity filter view 
 */
class sclguiLUMINOSITY_FILTER_VIEW : public sclguiFILTER_VIEW
{
public:
    // Class constructor
    sclguiLUMINOSITY_FILTER_VIEW();

    // Class destructor
    virtual ~sclguiLUMINOSITY_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual mcsCOMPL_STAT 
        GetLuminosityClass(std::list<char *> *lumClass);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiLUMINOSITY_FILTER_VIEW(const sclguiLUMINOSITY_FILTER_VIEW&);
    sclguiLUMINOSITY_FILTER_VIEW& operator=(const sclguiLUMINOSITY_FILTER_VIEW&);

    gwtCHECKBOX _lumCheckboxI;
    gwtCHECKBOX _lumCheckboxII;
    gwtCHECKBOX _lumCheckboxIII;
    gwtCHECKBOX _lumCheckboxIV;
    gwtCHECKBOX _lumCheckboxV;
    gwtCHECKBOX _lumCheckboxVI;

    mcsLOGICAL IsLuminosityDisabled(std::list<char *> luminosityList,
                                   char *luminosityClass);
};

#endif /*!sclguiLUMINOSITY_FILTER_VIEW_H*/

/*___oOo___*/
