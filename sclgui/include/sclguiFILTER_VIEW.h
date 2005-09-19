#ifndef sclguiFILTER_VIEW_H
#define sclguiFILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiFILTER_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
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

#include "sclguiMODEL.h"
#include "sclsvr.h"

/*
 * Class declaration
 */

/**
 * Mother class of all filter view 
 */
class sclguiFILTER_VIEW : public fndMVC_VIEW
{

public:
    // Class constructor
    sclguiFILTER_VIEW();
    sclguiFILTER_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiFILTER_VIEW();

    virtual mcsCOMPL_STAT Show();
    virtual mcsCOMPL_STAT Hide();
    
    virtual gwtWINDOW * GetWindowLink();
    virtual gwtBUTTON * GetApplyButtonLink();
protected:
    virtual mcsCOMPL_STAT BuildWindow();
    virtual mcsCOMPL_STAT CompleteWindowInformation();
    virtual mcsCOMPL_STAT BuildMainFilterView();
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiFILTER_VIEW(const sclguiFILTER_VIEW&);
    sclguiFILTER_VIEW& operator=(const sclguiFILTER_VIEW&);

    sclguiMODEL *_model;

    gwtWINDOW *_filterWindow;
    gwtBUTTON *_applyFilterButton;

    virtual mcsCOMPL_STAT BuildApplyFilterButton();    
};

#endif /*!sclguiFILTER_VIEW_H*/

/*___oOo___*/
