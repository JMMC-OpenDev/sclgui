#ifndef sclguiSPECTRAL_TYPE_FILTER_VIEW_H
#define sclguiSPECTRAL_TYPE_FILTER_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiSPECTRAL_TYPE_FILTER_VIEW.h,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiSPTYPE_FILTER_VIEW class.
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
 * Spectral type filter view 
 * 
 */
class sclguiSPTYPE_FILTER_VIEW : public sclguiFILTER_VIEW
{

public:
    // Class constructor
    sclguiSPTYPE_FILTER_VIEW();

    // Class destructor
    virtual ~sclguiSPTYPE_FILTER_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual mcsCOMPL_STAT 
        GetTemperatureClass(std::list<char *> *tempClass);
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiSPTYPE_FILTER_VIEW(const sclguiSPTYPE_FILTER_VIEW&);
    sclguiSPTYPE_FILTER_VIEW& operator=(const sclguiSPTYPE_FILTER_VIEW&);

    gwtCHECKBOX _spectralTypeCheckboxO;
    gwtCHECKBOX _spectralTypeCheckboxB;
    gwtCHECKBOX _spectralTypeCheckboxA;
    gwtCHECKBOX _spectralTypeCheckboxF;
    gwtCHECKBOX _spectralTypeCheckboxG;
    gwtCHECKBOX _spectralTypeCheckboxK;
    gwtCHECKBOX _spectralTypeCheckboxM;

    mcsLOGICAL 
        IsDisableTemperature(std::list<char *> temperatureList,
                             char *temperatureClass);
};

#endif /*!sclguiSPECTRAL_TYPE_FILTER_VIEW_H*/

/*___oOo___*/
