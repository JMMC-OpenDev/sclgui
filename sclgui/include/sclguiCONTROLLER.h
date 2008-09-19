#ifndef sclguiCONTROLLER_H
#define sclguiCONTROLLER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCONTROLLER.h,v 1.4 2006-02-22 15:48:42 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/02/22 13:28:52  gzins
 * Renamed command callbacks as <Command>CB
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
 * Declaration of sclguiCONTROLLER class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS Headers
 */
#include "mcs.h"
#include "evh.h"
#include "gwt.h"

/*
 * SCALIB Headers
 */
#include "sclsvr.h"
#include "vobs.h"

/*
 * Local Headers
 */
#include "sclguiACTIONS_VIEW.h"
#include "sclguiCALIBRATOR_LIST_MODEL.h"
#include "sclguiCALIBRATOR_LIST_VIEW.h"
#include "sclguiDISTANCE_FILTER_VIEW.h"
#include "sclguiFILTER_LIST_MODEL.h"
#include "sclguiFILTER_VIEW.h"
#include "sclguiLUMINOSITY_FILTER_VIEW.h"
#include "sclguiMAGNITUDE_FILTER_VIEW.h"
#include "sclguiBUTTONS_SUBPANEL.h"
#include "sclguiMULTIPLICITY_FILTER_VIEW.h"
#include "sclguiREQUEST_MODEL.h"
#include "sclguiREQUEST_VIEW.h"
#include "sclguiSPECTRAL_TYPE_FILTER_VIEW.h"
#include "sclguiVARIABILITY_FILTER_VIEW.h"
#include "sclguiVISIBILITY_FILTER_VIEW.h"
#include "sclguiCONFIRM_SUBPANEL.h"

/*
 * Class declaration
 */

/**
 * This class is the mediator between SCALIB model and view objects.
 *
 * It is responsible of:
 * @li GUI building;
 * @li event handling;
 * @li model updates;
 */
class sclguiCONTROLLER : public gwtGUI
{

public:
    // Class constructor
    sclguiCONTROLLER();

    // Class destructor
    virtual ~sclguiCONTROLLER();

    virtual mcsCOMPL_STAT   AppInit();
   
    const   char           *GetSwVersion();
    
protected:
    // Command callbacks
    virtual evhCB_COMPL_STAT GetCalCB(msgMESSAGE &msg, void*);
    virtual evhCB_COMPL_STAT ExitCB(msgMESSAGE &msg, void *); 

    // Command reply callbacks
    virtual evhCB_COMPL_STAT GetCalReplyCB(msgMESSAGE &msg, void*);
   
    // Building method of the general GUI
    mcsCOMPL_STAT BuildGUI();
    
    // Building method of the main window
    mcsCOMPL_STAT BuildMainWindow();

    // Build filters windows
    mcsCOMPL_STAT BuildFilterWindows();
    
    // Build Confirm Pop up window
    mcsCOMPL_STAT BuildConfirmPopUp();
    
    // CB associated to the gui 
    mcsCOMPL_STAT AbortButtonCB(void *);
    mcsCOMPL_STAT ShowAllResultsButtonCB(void *);
    mcsCOMPL_STAT ResetButtonCB(void *);
    mcsCOMPL_STAT ShowDetailsButtonCB(void *);
    mcsCOMPL_STAT HideDetailsButtonCB(void *);
    mcsCOMPL_STAT SelectButtonCB(void *);
    mcsCOMPL_STAT DeleteButtonCB(void *);
    mcsCOMPL_STAT LoadButtonCB(void *);
    mcsCOMPL_STAT SaveButtonCB(void *);
    mcsCOMPL_STAT CSVExportButtonCB(void *);
    mcsCOMPL_STAT VOTExportButtonCB(void *);
    mcsCOMPL_STAT VisibilityButtonCB(void *);
    mcsCOMPL_STAT LuminosityButtonCB(void *);
    mcsCOMPL_STAT SpectralTypeButtonCB(void *);
    mcsCOMPL_STAT MagnitudeButtonCB(void *);
    mcsCOMPL_STAT MultiplicityButtonCB(void *);
    mcsCOMPL_STAT DistanceButtonCB(void *);
    mcsCOMPL_STAT VariabilityButtonCB(void *);
    mcsCOMPL_STAT SaveOverwriteButtonCB(void *);
    mcsCOMPL_STAT CSVExportOverwriteButtonCB(void *);
    mcsCOMPL_STAT VOTExportOverwriteButtonCB(void *);

    mcsCOMPL_STAT OpenPopUp(sclguiOVERWRITE_ACTION  action);
    mcsCOMPL_STAT Overwrite(mcsSTRING32             fileName,
                            sclguiOVERWRITE_ACTION  action);

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiCONTROLLER(const sclguiCONTROLLER&);
    sclguiCONTROLLER& operator=(const sclguiCONTROLLER&);

    // Models
    sclguiCALIBRATOR_LIST_MODEL _calibratorListModel;
    sclguiREQUEST_MODEL         _requestModel;
    sclguiFILTER_LIST_MODEL     _filterListModel;

    // Main window
    gwtWINDOW _mainWindow; 

    // Sub-panels of the main window
    sclguiBUTTONS_SUBPANEL      _buttonsSubPanel;
    sclguiREQUEST_VIEW          _requestSubPanel;
    sclguiCALIBRATOR_LIST_VIEW  _calibratorListModelSubPanel;
    sclguiACTIONS_VIEW          _actionsSubPanel;

    // Windows of the filters and his attached sub-panels
    gwtWINDOW                      _visibilityFilterWindow;
    sclguiVISIBILITY_FILTER_VIEW   _visibilityFilterSubPanel;

    gwtWINDOW                      _magnitudeFilterWindow;
    sclguiMAGNITUDE_FILTER_VIEW    _magnitudeFilterSubPanel;

    gwtWINDOW                      _distanceFilterWindow;
    sclguiDISTANCE_FILTER_VIEW     _distanceFilterSubPanel;

    gwtWINDOW                      _luminosityFilterWindow;
    sclguiLUMINOSITY_FILTER_VIEW   _luminosityFilterSubPanel;

    gwtWINDOW                      _spectralTypeFilterWindow;
    sclguiSPTYPE_FILTER_VIEW       _spectralTypeFilterSubPanel;

    gwtWINDOW                      _variabilityFilterWindow;
    sclguiVARIABILITY_FILTER_VIEW  _variabilityFilterSubPanel;

    gwtWINDOW                      _multiplicityFilterWindow;
    sclguiMULTIPLICITY_FILTER_VIEW _multiplicityFilterSubPanel;

    // Confirm window
    gwtWINDOW              _confirmWindow;
    sclguiCONFIRM_SUBPANEL _confirmSupPanel;

    // Interface with search-calibrator server
    evhINTERFACE _sclServer;

    // Received message which is currently processed
    msgMESSAGE _msg;
};

#endif /*!sclguiCONTROLLER_H*/

/*___oOo___*/
