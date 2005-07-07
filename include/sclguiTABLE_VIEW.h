#ifndef sclguiTABLE_VIEW_H
#define sclguiTABLE_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiTABLE_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiTABLE_VIEW class.
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
 * View of the model calibrators list 
 */
class sclguiTABLE_VIEW : public fndMVC_VIEW
{

public:
    // Class constructor
    sclguiTABLE_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiTABLE_VIEW();

    virtual mcsCOMPL_STAT Update();
   
    virtual mcsCOMPL_STAT Details(mcsLOGICAL state);
    
    gwtTABLE * GetListTable();
    gwtTABLE * GetColorTable();
    gwtTABLE * GetConfIdxTable();
    vobsSTAR_PROPERTY_ID_LIST GetLabel(mcsLOGICAL details = mcsTRUE);
    virtual mcsLOGICAL IsDetailsView();
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiTABLE_VIEW(const sclguiTABLE_VIEW&);
    sclguiTABLE_VIEW& operator=(const sclguiTABLE_VIEW&);

    sclguiMODEL *_model;
    
    gwtTABLE *_listTable;
    gwtTABLE *_legendTable;
    gwtTABLE *_confidenceTable;

    mcsLOGICAL _details;

    vobsSTAR_PROPERTY_ID_LIST _label;

    mcsCOMPL_STAT ResetLabel();
    mcsCOMPL_STAT BuildLabel(vobsSTAR_PROPERTY_ID_LIST *label,
                             mcsLOGICAL details);
    mcsCOMPL_STAT BuildLabelKV(vobsSTAR_PROPERTY_ID_LIST *label);
    mcsCOMPL_STAT BuildLabelKVComplete(vobsSTAR_PROPERTY_ID_LIST *label);
    mcsCOMPL_STAT BuildLabelN(vobsSTAR_PROPERTY_ID_LIST *label);
    mcsCOMPL_STAT BuildLabelNComplete(vobsSTAR_PROPERTY_ID_LIST *label);
    
};

#endif /*!sclguiTABLE_VIEW_H*/

/*___oOo___*/
