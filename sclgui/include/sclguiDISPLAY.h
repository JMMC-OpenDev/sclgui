#ifndef sclguiDISPLAY_H
#define sclguiDISPLAY_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiDISPLAY.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiDISPLAY class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "evh.h"
#include "gwt.h"

#include "sclsvr.h"
#include "vobs.h"
#include "sclguiREQUEST_VIEW.h"
#include "sclguiRESUME_VIEW.h"
#include "sclguiTABLE_VIEW.h"
#include "sclguiVISIBILITY_FILTER_VIEW.h"
#include "sclguiMAGNITUDE_FILTER_VIEW.h"
#include "sclguiDISTANCE_FILTER_VIEW.h"
#include "sclguiLUMINOSITY_FILTER_VIEW.h"
#include "sclguiSPTYPE_FILTER_VIEW.h"
#include "sclguiVARIABILITY_FILTER_VIEW.h"
#include "sclguiMULTIPLICITY_FILTER_VIEW.h"
#include "sclguiMODEL.h"

/*
 * Class declaration
 */

/**
 * This class aims to build the gui for search calib application.
 * One main window is builded with associated widgets. Sub panels contain
 * parameters for specific actions. The first subpanel is used to sort one 7
 * different ways. For each choice, one window is open and the real process is
 * done in the ok associated callback.
 */
class sclguiDISPLAY : public evhSERVER
{

public:
    sclguiDISPLAY(string hostname, mcsINT32 port);   

    // Class destructor
    virtual ~sclguiDISPLAY();

    virtual mcsCOMPL_STAT AppInit();

    const char *GetSwVersion();

protected:
    // Command callbacks
    virtual evhCB_COMPL_STAT GetCalCB(msgMESSAGE &msg, void*);

    // Command reply callbacks
    virtual evhCB_COMPL_STAT GetCalReplyCB(msgMESSAGE &msg, void*);
   
    // Command exit callbacks
    virtual evhCB_COMPL_STAT ExitCB(msgMESSAGE &msg, void *);
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiDISPLAY(const sclguiDISPLAY&);
    sclguiDISPLAY& operator=(const sclguiDISPLAY&);

    // Interface with search-calibrator server
    evhINTERFACE _sclServer;

    // Received message which is currently processed
    msgMESSAGE _msg;
    
    gwtGUI *_theGui;
    string _guiHostname;
    mcsINT32 _guiPort;
    
    // CB associated to the gui 
    evhCB_COMPL_STAT GuiSocketCB(const int sd, void *obj);
    mcsCOMPL_STAT AbortButtonCB(void *);
    mcsCOMPL_STAT ShowAllResultsButtonCB(void *);
    mcsCOMPL_STAT ResetButtonCB(void *);
    mcsCOMPL_STAT ShowDetailsCB(void *);
    mcsCOMPL_STAT HideDetailsCB(void *);
    mcsCOMPL_STAT SelectPanelCB(void *);
    mcsCOMPL_STAT DeletePanelCB(void *);
    mcsCOMPL_STAT LoadPanelCB(void *);
    mcsCOMPL_STAT SavePanelCB(void *);
    mcsCOMPL_STAT ExportPanelCB(void *);
    mcsCOMPL_STAT VisibilityButtonCB(void *);
    mcsCOMPL_STAT LuminosityButtonCB(void *);
    mcsCOMPL_STAT SpectralTypeButtonCB(void *);
    mcsCOMPL_STAT MagnitudeButtonCB(void *);
    mcsCOMPL_STAT MultiplicityButtonCB(void *);
    mcsCOMPL_STAT DistanceButtonCB(void *);
    mcsCOMPL_STAT VariabilityButtonCB(void *);
    mcsCOMPL_STAT SaveOverwriteButtonCB(void *);
    mcsCOMPL_STAT ExportOverwriteButtonCB(void *);
    
    mcsCOMPL_STAT Overwrite(mcsSTRING32 fileName, mcsLOGICAL saveFlag);
    mcsCOMPL_STAT OpenPopUp(mcsLOGICAL saveFlag);

    // Building method
    mcsCOMPL_STAT BuildMainWindow();
    mcsCOMPL_STAT BuildButtons();
    mcsCOMPL_STAT BuildModelView();
    mcsCOMPL_STAT BuildFilterInterface();
    mcsCOMPL_STAT BuildInOutFileInterface();
    mcsCOMPL_STAT BuildConfirmWindow();
    
    // object of the gui
    gwtWINDOW *_mainWindow;
    gwtBUTTON *_resetButton;
    gwtBUTTON *_showAllResultsButton;
    gwtBUTTON *_hideDetailsButton;
    gwtBUTTON *_showDetailsButton;

    // object comming from view
    gwtTEXTAREA *_scienceStarTextArea;    
    gwtTEXTAREA *_resumeTextArea;
    gwtTABLE *_listTable;
    gwtTABLE *_legendTable;
    gwtTABLE *_condIdxTable;

    // object of the gui
    gwtSUBPANEL *_filterPanel;
    gwtCHOICE *_filterChoice;     
    gwtSUBPANEL *_deletePanel;
    gwtTEXTFIELD *_deleteTextfield;
    gwtSUBPANEL *_loadPanel;
    gwtTEXTFIELD *_loadTextfield;
    gwtSUBPANEL *_savePanel;
    gwtTEXTFIELD *_saveTextfield;
    gwtSUBPANEL *_exportPanel;
    gwtTEXTFIELD *_exportTextfield;

    // Confirm object
    gwtWINDOW *_confirmWindow;
    gwtBUTTON *_overwriteButton;
    gwtLABEL *_confirmLabel;

    // Views used
    sclguiREQUEST_VIEW *_requestView;
    sclguiRESUME_VIEW *_resumeView;
    sclguiTABLE_VIEW *_tableView;
    sclguiVISIBILITY_FILTER_VIEW *_visibilityFilterView;
    sclguiMAGNITUDE_FILTER_VIEW *_magnitudeFilterView;
    sclguiDISTANCE_FILTER_VIEW *_distanceFilterView;
    sclguiLUMINOSITY_FILTER_VIEW *_luminosityFilterView;
    sclguiSPTYPE_FILTER_VIEW *_spTypeFilterView;
    sclguiVARIABILITY_FILTER_VIEW *_variabilityFilterView;
    sclguiMULTIPLICITY_FILTER_VIEW *_multiplicityFilterView;

    // Model used
    sclguiMODEL _model;
};

#endif /*!sclguiDISPLAY_H*/

/*___oOo___*/
