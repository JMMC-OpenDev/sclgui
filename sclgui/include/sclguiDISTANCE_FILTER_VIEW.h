#ifndef sclguiDISTANCE_FILTER_VIEW_H
#define sclguiDISTANCE_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiDISTANCE_FILTER_VIEW.h,v 1.2 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiDISTANCE_FILTER_VIEW class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

#include "sclguiFILTER_VIEW.h"
/*
 * Class declaration
 */

/**
 * Distance filter view 
 * 
 */
class sclguiDISTANCE_FILTER_VIEW : public sclguiFILTER_VIEW
{

public:
    // Class constructor
    sclguiDISTANCE_FILTER_VIEW();

    // Class destructor
    virtual ~sclguiDISTANCE_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual string GetRaRange();
    virtual string GetDecRange();
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiDISTANCE_FILTER_VIEW(const sclguiDISTANCE_FILTER_VIEW&);
    sclguiDISTANCE_FILTER_VIEW& operator=(const sclguiDISTANCE_FILTER_VIEW&);

    gwtTEXTFIELD _raRangeTextfield;
    gwtTEXTFIELD _decRangeTextfield;
};

#endif /*!sclguiDISTANCE_FILTER_VIEW_H*/

/*___oOo___*/
