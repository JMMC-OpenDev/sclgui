#ifndef sclguiMULTIPLICITY_FILTER_VIEW_H
#define sclguiMULTIPLICITY_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMULTIPLICITY_FILTER_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
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
    sclguiMULTIPLICITY_FILTER_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiMULTIPLICITY_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual mcsLOGICAL IsMultiplicityAuthorized();
    
    virtual string GetChoice();    
protected:
    virtual mcsCOMPL_STAT CompleteWindowInformation();
    virtual mcsCOMPL_STAT BuildMainFilterView();
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiMULTIPLICITY_FILTER_VIEW(const sclguiMULTIPLICITY_FILTER_VIEW&);
    sclguiMULTIPLICITY_FILTER_VIEW& operator=(const sclguiMULTIPLICITY_FILTER_VIEW&);

    sclguiMODEL *_model;

    gwtCHOICE *_multiplicityChoice;
};

#endif /*!sclguiMULTIPLICITY_FILTER_VIEW_H*/

/*___oOo___*/
