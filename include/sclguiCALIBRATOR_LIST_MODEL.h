#ifndef sclguiCALIBRATOR_LIST_MODEL_H
#define sclguiCALIBRATOR_LIST_MODEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
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


/*
 * SCALIB header
 */
#include "sclsvr.h"


/*
 * Local header
 */
#include "sclguiFILTER_LIST_MODEL.h"

/*
 * Enumerate
 */
typedef enum
{
    sclguiALL_CALIBRATORS,
    sclguiDELETED_CALIBRATORS,
    sclguiFILTERED_CALIBRATORS,
    sclguiCALIBRATORS_WITH_COHERENT_DIAM,
    sclguiCALIBRATORS_WITHOUT_VAR_MULT    
} sclguiTYPE_OF_CALIBRATORS;
/*
 * Class declaration
 */

/**
 * Model class of calirators.
 * 
 * This class is composed of the calibrators list received from the CDS.
 */
class sclguiCALIBRATOR_LIST_MODEL : public fndMVC_MODEL
{

public:
    // Class constructor
    sclguiCALIBRATOR_LIST_MODEL();

    // Class destructor
    virtual ~sclguiCALIBRATOR_LIST_MODEL();

    // Method related to calibrator list
    virtual mcsCOMPL_STAT SetList(msgMESSAGE &msg);
    virtual mcsCOMPL_STAT SetList(sclsvrCALIBRATOR_LIST &list);
    virtual mcsCOMPL_STAT LoadFromFile(mcsSTRING256  fileName,
                                       sclsvrREQUEST &request);
    virtual mcsCOMPL_STAT Clear(void);

    virtual sclsvrCALIBRATOR_LIST *GetList(sclguiTYPE_OF_CALIBRATORS type);
    virtual mcsINT32 GetNbCalibrators(sclguiTYPE_OF_CALIBRATORS type);

    // Filters
    virtual mcsCOMPL_STAT SetFilterList(sclguiFILTER_LIST_MODEL * filterList);
    
    // Method related to deleted calibrators
    virtual mcsCOMPL_STAT DeleteCalibrator(mcsINT32 starNum, char *starId);
    virtual mcsCOMPL_STAT ClearDeletedCalibratorList(void);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiCALIBRATOR_LIST_MODEL(const sclguiCALIBRATOR_LIST_MODEL&);
    sclguiCALIBRATOR_LIST_MODEL& operator=(const sclguiCALIBRATOR_LIST_MODEL&);

    // Calibrator list of the model
    sclsvrCALIBRATOR_LIST _calibratorList;
    sclsvrCALIBRATOR_LIST _deletedCalibratorList;
    sclsvrCALIBRATOR_LIST _filteredCalibratorList;

    sclguiFILTER_LIST_MODEL *_filterListModel;
};

#endif /*!sclguiCALIBRATOR_LIST_MODEL_H*/

/*___oOo___*/
