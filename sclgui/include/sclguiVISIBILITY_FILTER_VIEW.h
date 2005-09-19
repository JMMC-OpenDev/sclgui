#ifndef sclguiVISIBILITY_FILTER_VIEW_H
#define sclguiVISIBILITY_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiVISIBILITY_FILTER_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiVISIBILITY_FILTER_VIEW class.
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
 * Visibility filter View 
 */
class sclguiVISIBILITY_FILTER_VIEW : public sclguiFILTER_VIEW
{

public:
    // Class constructor
    sclguiVISIBILITY_FILTER_VIEW();
    sclguiVISIBILITY_FILTER_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiVISIBILITY_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
   
    virtual string GetVisFilterValue();
protected:
    virtual mcsCOMPL_STAT CompleteWindowInformation();
    virtual mcsCOMPL_STAT BuildMainFilterView();
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiVISIBILITY_FILTER_VIEW(const sclguiVISIBILITY_FILTER_VIEW&);
    sclguiVISIBILITY_FILTER_VIEW& operator=(const sclguiVISIBILITY_FILTER_VIEW&);

    sclguiMODEL *_model;

    gwtTEXTFIELD *_accuracyTextfield;
    
};

#endif /*!sclguiVISIBILITY_FILTER_VIEW_H*/

/*___oOo___*/
