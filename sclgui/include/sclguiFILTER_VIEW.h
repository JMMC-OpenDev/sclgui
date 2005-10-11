#ifndef sclguiFILTER_VIEW_H
#define sclguiFILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiFILTER_VIEW.h,v 1.2 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
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
 * This clas is the mother class of the filter view class need to see the state
 * of the filters. It provides generic methods to register it at a main gui. 
 * 
 */
class sclguiFILTER_VIEW : public fndMVC_VIEW, public gwtCONTAINER
{

public:
    // Class constructor
    sclguiFILTER_VIEW();

    // Class destructor
    virtual ~sclguiFILTER_VIEW();

    virtual mcsCOMPL_STAT SetApplyCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD cbMethod);
   
    virtual mcsCOMPL_STAT AttachModel(sclguiFILTER_LIST_MODEL &filterList);
protected:
    sclguiFILTER_LIST_MODEL *_filterList;

    gwtBUTTON _applyFilterButton;
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiFILTER_VIEW(const sclguiFILTER_VIEW&);
    sclguiFILTER_VIEW& operator=(const sclguiFILTER_VIEW&);
};

#endif /*!sclguiFILTER_VIEW_H*/

/*___oOo___*/
