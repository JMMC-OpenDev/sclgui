#ifndef sclguiDISTANCE_FILTER_VIEW_H
#define sclguiDISTANCE_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiDISTANCE_FILTER_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
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

/*
 * Local header
 */
#include "sclguiFILTER_VIEW.h"

/*
 * Class declaration
 */

/**
 * Distance filter view
 */
class sclguiDISTANCE_FILTER_VIEW : public sclguiFILTER_VIEW
{

public:
    // Class constructor
    sclguiDISTANCE_FILTER_VIEW();
    sclguiDISTANCE_FILTER_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiDISTANCE_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();

    virtual string GetRaRange();
    virtual string GetDecRange();

protected:
    virtual mcsCOMPL_STAT CompleteWindowInformation();
    virtual mcsCOMPL_STAT BuildMainFilterView(); 
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiDISTANCE_FILTER_VIEW(const sclguiDISTANCE_FILTER_VIEW&);
    sclguiDISTANCE_FILTER_VIEW& operator=(const sclguiDISTANCE_FILTER_VIEW&);

    sclguiMODEL *_model;
    
    gwtTEXTFIELD *_raRangeTextfield;
    gwtTEXTFIELD *_decRangeTextfield;
    
};

#endif /*!sclguiDISTANCE_FILTER_VIEW_H*/

/*___oOo___*/
