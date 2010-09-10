#ifndef sclguiVISIBILITY_FILTER_VIEW_H
#define sclguiVISIBILITY_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiVISIBILITY_FILTER_VIEW.h,v 1.3 2005-10-18 12:52:48 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
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
 * Visibility filter view 
 */
class sclguiVISIBILITY_FILTER_VIEW : public sclguiFILTER_VIEW
{
public:
    // Class constructor
    sclguiVISIBILITY_FILTER_VIEW();

    // Class destructor
    virtual ~sclguiVISIBILITY_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();

    virtual string GetVisFilterValue();
    
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiVISIBILITY_FILTER_VIEW(const sclguiVISIBILITY_FILTER_VIEW&);
    sclguiVISIBILITY_FILTER_VIEW& operator=(const sclguiVISIBILITY_FILTER_VIEW&);
    gwtTEXTFIELD _accuracyTextfield;
};

#endif /*!sclguiVISIBILITY_FILTER_VIEW_H*/

/*___oOo___*/
