#ifndef sclguiFILTER_VIEW_H
#define sclguiFILTER_VIEW_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiFILTER_VIEW class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "fnd.h"
#include "gwt.h"


/*
 * Local header
 */
#include "sclguiFILTER_LIST_MODEL.h"


/*
 * Class declaration
 */
/**
 * Filter view.
 *
 * This class is the mother class of each filter view classes. It is used to
 * show the state of any filter. It provides generic methods to register it at a main gui. 
 */
class sclguiFILTER_VIEW : public fndMVC_VIEW, public gwtCONTAINER
{

public:
    // Class constructor
    sclguiFILTER_VIEW();

    // Class destructor
    virtual ~sclguiFILTER_VIEW();

    // Class callback
    virtual mcsCOMPL_STAT SetApplyCB  (fndOBJECT &eventHandler,
                                       gwtCOMMAND::CB_METHOD callbackMethod);
   
    virtual mcsCOMPL_STAT AttachModel (sclguiFILTER_LIST_MODEL &filterList);

protected:
    sclguiFILTER_LIST_MODEL *_filterListModel;
    gwtBUTTON _applyFilterButton;

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiFILTER_VIEW(const sclguiFILTER_VIEW&);
    sclguiFILTER_VIEW& operator=(const sclguiFILTER_VIEW&);
};

#endif /*!sclguiFILTER_VIEW_H*/

/*___oOo___*/
