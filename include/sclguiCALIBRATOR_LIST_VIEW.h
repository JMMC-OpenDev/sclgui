#ifndef sclguiCALIBRATOR_LIST_VIEW_H
#define sclguiCALIBRATOR_LIST_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCALIBRATOR_LIST_VIEW.h,v 1.4 2006-08-23 12:12:25 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/11/23 08:42:27  scetre
 * Added possibility to build label for faint K
 *
 * Revision 1.2  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.1  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiCALIBRATOR_LIST_VIEW class.
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


#include "sclguiCALIBRATOR_LIST_MODEL.h"
#include "sclguiREQUEST_MODEL.h"
#include "sclguiFILTER_LIST_MODEL.h"

/*
 * Class declaration
 */

/**
 * View associated to the calibrators model.
 *
 * It derives from the mega widget class.
 */
class sclguiCALIBRATOR_LIST_VIEW : public gwtCONTAINER, public fndMVC_VIEW
{

public:
    // Class constructor
    sclguiCALIBRATOR_LIST_VIEW();
    sclguiCALIBRATOR_LIST_VIEW(sclguiCALIBRATOR_LIST_MODEL &calibratorsModel,
                           sclguiREQUEST_MODEL &requestModel);

    // Class destructor
    virtual ~sclguiCALIBRATOR_LIST_VIEW();

    virtual mcsCOMPL_STAT Update();

    virtual mcsCOMPL_STAT Detail(mcsLOGICAL state);
    
    vobsSTAR_PROPERTY_ID_LIST GetLabel(mcsLOGICAL details = mcsTRUE);
    
    virtual mcsLOGICAL IsDetailed();

    virtual mcsCOMPL_STAT AttachModel(sclguiCALIBRATOR_LIST_MODEL &calibratorsModel,
                                      sclguiREQUEST_MODEL &requestModel);
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiCALIBRATOR_LIST_VIEW(const sclguiCALIBRATOR_LIST_VIEW&);
    sclguiCALIBRATOR_LIST_VIEW& operator=(const sclguiCALIBRATOR_LIST_VIEW&);

    sclguiCALIBRATOR_LIST_MODEL* _calibratorListModel;
    sclguiREQUEST_MODEL* _requestModel;
    
    gwtTEXTAREA _resumeTextArea;    
    gwtTABLE* _listTable;
    gwtTABLE* _legendTable;
    gwtTABLE* _confidenceTable;

    mcsLOGICAL _details;

    mcsCOMPL_STAT GetLabelForBrightKV(vobsSTAR_PROPERTY_ID_LIST *label,
                                      mcsLOGICAL details);
    mcsCOMPL_STAT GetLabelForBrighN(vobsSTAR_PROPERTY_ID_LIST *label,
                                    mcsLOGICAL details);
    mcsCOMPL_STAT GetLabelForFaintK(vobsSTAR_PROPERTY_ID_LIST *label,
                                    mcsLOGICAL details);
    
};

#endif /*!sclguiCALIBRATOR_LIST_VIEW_H*/

/*___oOo___*/