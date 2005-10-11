#ifndef sclguiCALIBRATOR_LIST_MODEL_H
#define sclguiCALIBRATOR_LIST_MODEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCALIBRATOR_LIST_MODEL.h,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiCALIBRATOR_LIST_MODEL class.
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
#include "misco.h"

#include "sclsvr.h"

/*
 * Local header
 */
#include "sclguiFILTER_LIST_MODEL.h"

/*
 * Class declaration
 */

/**
 * Model class of calirators.
 * 
 * This class is composed by the calibrators list coming back from the CDS.
 *
 */
class sclguiCALIBRATOR_LIST_MODEL : public fndMVC_MODEL
{

public:
    // Class constructor
    sclguiCALIBRATOR_LIST_MODEL();

    // Class destructor
    virtual ~sclguiCALIBRATOR_LIST_MODEL();

    virtual mcsCOMPL_STAT SetList(msgMESSAGE &msg);
    virtual mcsCOMPL_STAT SetList(sclsvrCALIBRATOR_LIST &list);
    virtual sclsvrCALIBRATOR_LIST *GetCalibratorList(void);
    virtual mcsCOMPL_STAT SetFilterList(sclguiFILTER_LIST_MODEL * filterList);
    
    virtual mcsCOMPL_STAT DeleteCalibrator(mcsINT32 unwantedStarNumber,
                                           miscoDYN_BUF &DeletedStarNameMsg);
    virtual mcsCOMPL_STAT ResetCalibrators(void);
    virtual mcsCOMPL_STAT ResetDeletedCalibrators(void);

    virtual mcsFLOAT GetNbCDSReturn(void);
    virtual mcsFLOAT GetNbCoherentDiamFound(void);
    virtual mcsFLOAT GetNbWithoutVarMult(void);
    virtual mcsFLOAT GetNbInFilteredList(void);

    virtual mcsCOMPL_STAT LoadFromFile(mcsSTRING256 fileName,
                                       sclsvrREQUEST &request);
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiCALIBRATOR_LIST_MODEL(const sclguiCALIBRATOR_LIST_MODEL&);
    sclguiCALIBRATOR_LIST_MODEL& operator=(const sclguiCALIBRATOR_LIST_MODEL&);

    // Calibrator list of the model
    sclsvrCALIBRATOR_LIST _calibratorList;
    sclsvrCALIBRATOR_LIST _deletedCalibratorList;
    sclsvrCALIBRATOR_LIST _listOfFilteredCalibrators;

    sclguiFILTER_LIST_MODEL *_filterList;
};

#endif /*!sclguiCALIBRATOR_LIST_MODEL_H*/

/*___oOo___*/
