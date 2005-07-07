#ifndef sclguiLUMINOSITY_FILTER_VIEW_H
#define sclguiLUMINOSITY_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiLUMINOSITY_FILTER_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
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
 * Luminosity filter View
 */
class sclguiLUMINOSITY_FILTER_VIEW : public sclguiFILTER_VIEW
{

public:
    // Class constructor
    sclguiLUMINOSITY_FILTER_VIEW();
    sclguiLUMINOSITY_FILTER_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiLUMINOSITY_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual mcsCOMPL_STAT 
        GetLuminosityClass(std::list<char *> *lumClass);
protected:
    virtual mcsCOMPL_STAT CompleteWindowInformation();
    virtual mcsCOMPL_STAT BuildMainFilterView();
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiLUMINOSITY_FILTER_VIEW(const sclguiLUMINOSITY_FILTER_VIEW&);
    sclguiLUMINOSITY_FILTER_VIEW& operator=(const sclguiLUMINOSITY_FILTER_VIEW&);
    sclguiMODEL *_model;

    gwtCHECKBOX *_lumCheckboxI;
    gwtCHECKBOX *_lumCheckboxII;
    gwtCHECKBOX *_lumCheckboxIII;
    gwtCHECKBOX *_lumCheckboxIV;
    gwtCHECKBOX *_lumCheckboxV;
    gwtCHECKBOX *_lumCheckboxVI;

    mcsLOGICAL 
        IsDisableLuminosity(std::list<char *> luminosityList,
                           char *luminosityClass);
};

#endif /*!sclguiLUMINOSITY_FILTER_VIEW_H*/

/*___oOo___*/
