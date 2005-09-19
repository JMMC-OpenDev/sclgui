#ifndef sclguiMODEL_H
#define sclguiMODEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiMODEL.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiMODEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "fnd.h"
#include "msg.h"
#include "sclsvr.h"
#include "sclsvrCALIBRATOR_LIST.h"
#include "sclsvrREQUEST.h"
#include "vobsFILTER_LIST.h"

/*
 * Class declaration
 */

/**
 * Model of the application.
 *
 * This class is defined by a list of calibrators, a request and a list of
 * filters
 */
class sclguiMODEL : public fndMVC_MODEL
{

public:
    // Class constructor
    sclguiMODEL();

    // Class destructor
    virtual ~sclguiMODEL();

    virtual mcsCOMPL_STAT Init();
    virtual mcsCOMPL_STAT SetList(msgMESSAGE &msg);
    virtual mcsCOMPL_STAT SetList(sclsvrCALIBRATOR_LIST &list);
    virtual mcsCOMPL_STAT SetRequest(msgMESSAGE &msg);
    
    virtual mcsCOMPL_STAT                   ResetFilter(void);
    virtual mcsCOMPL_STAT                   ResetDeleteCalirator();
    virtual sclsvrCALIBRATOR_LIST *         GetCalibratorList(void);
    virtual sclsvrREQUEST *                 GetRequest(void);
    virtual vobsFILTER_LIST *               GetFilterList(void);

    virtual mcsFLOAT GetNbCDSReturn();
    virtual mcsFLOAT GetNbCoherentDiamFound();
    virtual mcsFLOAT GetNbWithoutVarMult();
    
    virtual mcsCOMPL_STAT EnableFilterMagnitude(mcsSTRING32 band,
                                                mcsFLOAT magValue,
                                                mcsFLOAT magRange);
    virtual mcsCOMPL_STAT EnableFilterDistance(mcsSTRING32 raRef,
                                               mcsSTRING32 decRef,
                                               mcsFLOAT raRange,
                                               mcsFLOAT decRange);
    virtual mcsCOMPL_STAT EnableFilterVisibility(mcsFLOAT visMax);
    virtual mcsCOMPL_STAT 
        EnableFilterSpectralType(std::list<char *> tempClassList);
    virtual mcsCOMPL_STAT
        EnableFilterLuminosity(std::list<char *> lumClassList);
    virtual mcsCOMPL_STAT EnableFilterVariability();
    virtual mcsCOMPL_STAT EnableFilterMultiplicity();
    
    virtual mcsCOMPL_STAT DisableFilter(mcsSTRING32 name);
    
    virtual mcsCOMPL_STAT AddDeleteStar(sclsvrCALIBRATOR &calibrator);

protected:

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiMODEL(const sclguiMODEL&);
    sclguiMODEL& operator=(const sclguiMODEL&);

    // Calibrator list of the model
    sclsvrCALIBRATOR_LIST _calibratorList;
    sclsvrCALIBRATOR_LIST _deletingCalibratorList;

    // Request of the model
    sclsvrREQUEST _request;
    // filter list of the model
    vobsFILTER_LIST _filterList;
    
};

#endif /*!sclguiMODEL_H*/

/*___oOo___*/
