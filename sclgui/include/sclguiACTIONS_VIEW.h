#ifndef sclguiACTIONS_VIEW_H
#define sclguiACTIONS_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiACTIONS_VIEW.h,v 1.1 2005-10-11 15:24:15 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiACTIONS_VIEW class.
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

#include "sclguiREQUEST_MODEL.h"

/**
 * Define the order of the filter in the filter choice
 * the filter should be enter in this order
 */
#define sclguiDISTANCE_FILTER_NUMBER 0
#define sclguiMAGNITUDE_FILTER_NUMBER 1
#define sclguiSP_TYPE_FILTER_NUMBER 2
#define sclguiLUMINOSITY_FILTER_NUMBER 3
#define sclguiVISIBILITY_FILTER_NUMBER 4
#define sclguiVARIABILITY_FILTER_NUMBER 5
#define sclguiMULTIPLICITY_FILTER_NUMBER 6

/*
 * Class declaration
 */

/**
 * Specific Mega Widget dedicated to the file IO management and the open of the
 * filter
 *
 */
class sclguiACTIONS_VIEW : public gwtCONTAINER, public fndMVC_VIEW 
{

public:
    // Class constructor
    sclguiACTIONS_VIEW();
    sclguiACTIONS_VIEW(sclguiREQUEST_MODEL &requestModel);

    // Class destructor
    virtual ~sclguiACTIONS_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual mcsCOMPL_STAT SetFilterButtonCB(fndOBJECT &eventHandler,
                                           gwtCOMMAND::CB_METHOD cbMethod);
    virtual mcsCOMPL_STAT SetDeleteButtonCB(fndOBJECT &eventHandler,
                                           gwtCOMMAND::CB_METHOD cbMethod);
    virtual mcsCOMPL_STAT SetLoadButtonCB(fndOBJECT &eventHandler,
                                         gwtCOMMAND::CB_METHOD cbMethod);
    virtual mcsCOMPL_STAT SetSaveButtonCB(fndOBJECT &eventHandler,
                                         gwtCOMMAND::CB_METHOD cbMethod);
    virtual mcsCOMPL_STAT SetExportCB(fndOBJECT &eventHandler,
                                      gwtCOMMAND::CB_METHOD cbMethod);
    
    virtual mcsINT32 GetFilterChoice(void);
    virtual string GetDeletedStarNumber(void);
    virtual string GetLoadFileName(void);
    virtual string GetSaveFileName(void);
    virtual string GetExportFileName(void);

    virtual mcsCOMPL_STAT AttachModel(sclguiREQUEST_MODEL &requestModel);
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiACTIONS_VIEW(const sclguiACTIONS_VIEW&);
    sclguiACTIONS_VIEW& operator=(const sclguiACTIONS_VIEW&);

    sclguiREQUEST_MODEL *_requestModel;
    
    gwtSUBPANEL _filterPanel;
    gwtCHOICE _filterChoice;     
    gwtSUBPANEL _deletePanel;
    gwtTEXTFIELD _deleteTextfield;
    gwtSUBPANEL _loadPanel;
    gwtTEXTFIELD _loadTextfield;
    gwtSUBPANEL _savePanel;
    gwtTEXTFIELD _saveTextfield;
    gwtSUBPANEL _exportPanel;
    gwtTEXTFIELD _exportTextfield;

    mcsCOMPL_STAT BuildFilterInterface();
    mcsCOMPL_STAT BuildInOutFileInterface();
    mcsCOMPL_STAT SetDefaultIOFileName();
};

#endif /*!sclguiACTIONS_VIEW_H*/

/*___oOo___*/
