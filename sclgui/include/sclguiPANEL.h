#ifndef sclguiPANEL_H
#define sclguiPANEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiPANEL.h,v 1.19 2005-03-07 16:01:20 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.18  2005/03/07 15:56:35  gzins
 * Removed filtering on visibility
 *
 * Revision 1.17  2005/03/07 14:19:16  scetre
 * Added _withNoVarMult to know how many star of the resulting list are without variability and multiplicity
 *
 * Revision 1.16  2005/03/06 20:35:28  gzins
 * Added GetSwVersion
 *
 * Revision 1.15  2005/03/04 15:52:02  scetre
 * Added export button
 *
 * Revision 1.14  2005/03/02 16:58:19  scetre
 * Added #define for color
 *
 * Revision 1.13  2005/02/28 10:01:58  scetre
 * Add color table for origin legend and color in results table
 *
 * Revision 1.12  2005/02/23 17:06:27  scetre
 * Added list of ucd name to manage the display of the colum name
 *
 * Revision 1.11  2005/02/18 11:49:49  scetre
 * Added mcsLOGICAL state for filter on variability and multiplicity
 *
 * Revision 1.10  2005/02/17 09:25:43  scetre
 * Added 3 interger in the class in order to store CDS return, coherent diameter and visibility list size
 *
 * Revision 1.9  2005/02/08 07:25:18  gzins
 * Replaced vosREQUEST by sclsvrREQUEST
 *
 * Revision 1.8  2005/02/04 14:24:06  scetre
 * Used sort method of sclsvr
 *
 * Revision 1.7  2005/02/04 08:08:55  scetre
 * affect Call back method
 *
 * Revision 1.6  2005/01/28 10:05:52  gzins
 * Added callbacks for command reply
 *
 * mella     25-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiPANEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * MCS Headers
 */

#include "evh.h"
#include "mcs.h"
#include "gwt.h"

#include "sclsvr.h"


#include <list>

/**
 * Define color value for catalog
 */
#define sclguiWHITE_COLOR               "#ffffff"
#define sclguiCONFIDENCE_LOW_COLOR      "#6e6e6e"
#define sclguiCONFIDENCE_MEDIUM_COLOR   "#d8d8d8"
#define sclguiCONFIDENCE_HIGH_COLOR     "#ffffff"
#define sclguiI_280_COLOR               "#ffb6b6"
#define sclguiII_225_COLOR              "#f6b6ff"
#define sclguiII_7A_COLOR               "#b9b6ff"
#define sclguiII_246_COLOR              "#b6e8ff"
#define sclguiV_50_COLOR                "#b6ffe6"
#define sclguiCHARM_COLOR               "#dfffb6"
#define sclguiDENIS_COLOR               "#fff4b6"
#define sclguiI_196_COLOR               "#78fb8b"
#define sclguiCOHEN_COLOR               "#9778fb"
#define sclguiV_36B_COLOR               "#88a0a6"
#define sclgui_MIDI_COLOR               "#c994ca"

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
class sclguiPANEL : public evhSERVER
{

public:
    sclguiPANEL(string hostname, mcsINT32 port);
    virtual ~sclguiPANEL();
    virtual mcsCOMPL_STAT AppInit();

    const char *GetSwVersion();

    /** typedef of UCD order list*/
    
protected:
    // Command callbacks
    virtual evhCB_COMPL_STAT GetCalCB(msgMESSAGE &msg, void*);

    // Command reply callbacks
    virtual evhCB_COMPL_STAT GetCalReplyCB(msgMESSAGE &msg, void*);
    
private:
    vobsSTAR_PROPERTY_ID_LIST _ucdName;
    vobsSTAR_PROPERTY_ID_LIST _ucdNameforKV;
    vobsSTAR_PROPERTY_ID_LIST _ucdNameforN;
    vobsSTAR_PROPERTY_ID_LIST _ucdNameforNComplete;
    vobsSTAR_PROPERTY_ID_LIST _ucdNameDisplay;
    mcsSTRING256 _fileName;
    sclsvrREQUEST _request;
    sclsvrCALIBRATOR_LIST _currentList;
    sclsvrCALIBRATOR_LIST _coherentDiameterList;
    sclsvrCALIBRATOR_LIST _displayList;

    void BuildResultsTableLabelKV();
    void BuildResultsTableLabelN();
    void BuildResultsTableLabelNComplete();    
    
    void FillResultsTable(sclsvrCALIBRATOR_LIST *list);

    // number of cds return, diameter coherent and visibility
    mcsINT32 _found;
    mcsINT32 _diam;
    mcsINT32 _withNoVarMult;
    // falg to know if the wanted file is SAVE or EXPORT, if it is equal to
    // false -> export
    mcsLOGICAL _saveFlag;

    // flag of the gui state (with or without variablity and multiplicity)
    mcsLOGICAL _varAuthorized;
    mcsLOGICAL _multAuthorized;
    
    // central point for exchange with the remote GUI 
    gwtGUI *_theGui;
    string _guiHostname;
    mcsINT32 _guiPort;
    evhCB_COMPL_STAT GuiSocketCB(const int sd, void *obj);

    // _mainWindow and its widgets with associated callbacks
    mcsCOMPL_STAT BuildMainWindow();
    gwtWINDOW *_mainWindow;
  
    mcsCOMPL_STAT BuildConfirmWindow();
    gwtWINDOW *_confirmWindow;
    gwtBUTTON *_overwriteButton;
    mcsCOMPL_STAT OverwriteButtonCB();
    gwtLABEL *_confirmLabel;
    
    gwtBUTTON *_showAllResultsButton;
    mcsCOMPL_STAT ShowAllResultsButtonCB(void *);
    gwtBUTTON *_resetButton;
    mcsCOMPL_STAT ResetButtonCB(void *);
   
    gwtLABEL *_resultsLabel;
    gwtTEXTAREA *_scienceStarTextarea;
    gwtTABLE *_resultsTable;
    gwtTABLE *_legendTable;
    gwtTABLE *_confidenceTable;
    gwtTEXTAREA * _resumeTextArea;
    
    gwtSUBPANEL *_selectPanel;
    mcsCOMPL_STAT SelectPanelCB(void *);
    gwtCHOICE *_selectChoice;
    
    gwtSUBPANEL *_deletePanel;
    mcsCOMPL_STAT DeletePanelCB(void *);
    gwtTEXTFIELD *_deleteTextfield;
    
    gwtSUBPANEL *_loadPanel;
    mcsCOMPL_STAT LoadPanelCB(void *);
    gwtTEXTFIELD *_loadTextfield;

    gwtSUBPANEL *_savePanel;
    mcsCOMPL_STAT SavePanelCB(void *);
    gwtTEXTFIELD *_saveTextfield;

    gwtSUBPANEL *_exportPanel;
    mcsCOMPL_STAT ExportPanelCB(void *);
    gwtTEXTFIELD *_exportTextfield;


    // Sort windows 
    mcsCOMPL_STAT BuildAccuracyWindow();
    gwtWINDOW *_accuracyWindow;
    gwtTEXTFIELD *_accuracyTextfield;
    gwtBUTTON *_accuracyButton;
    mcsCOMPL_STAT AccuracyButtonCB(void *);

    mcsCOMPL_STAT BuildLumWindow();
    gwtWINDOW *_lumWindow;
    gwtCHECKBOX *_lumCheckboxI;
    gwtCHECKBOX *_lumCheckboxII;
    gwtCHECKBOX *_lumCheckboxIII;
    gwtCHECKBOX *_lumCheckboxIV;
    gwtCHECKBOX *_lumCheckboxV;
    gwtCHECKBOX *_lumCheckboxVI;
    gwtBUTTON *_lumButton;
    mcsCOMPL_STAT LumButtonCB(void *);

    mcsCOMPL_STAT BuildMagWindow();
    gwtWINDOW *_magWindow;
    gwtTEXTFIELD *_magTextfield;
    gwtBUTTON *_magButton;
    mcsCOMPL_STAT MagButtonCB(void *);

    mcsCOMPL_STAT BuildMultWindow();
    gwtWINDOW *_multWindow;
    gwtCHOICE *_multChoice;
    gwtBUTTON *_multButton;
    mcsCOMPL_STAT MultButtonCB(void *);

    mcsCOMPL_STAT BuildRaDecWindow();
    gwtWINDOW *_raDecWindow;
    gwtTEXTFIELD *_raDecTextfieldRa;
    gwtTEXTFIELD *_raDecTextfieldDec;
    gwtBUTTON *_raDecButton;
    mcsCOMPL_STAT RaDecButtonCB(void *);

    mcsCOMPL_STAT BuildSpectralTypeWindow();
    gwtWINDOW *_spectralTypeWindow;
    gwtCHECKBOX *_spectralTypeCheckboxO;
    gwtCHECKBOX *_spectralTypeCheckboxB;
    gwtCHECKBOX *_spectralTypeCheckboxA;
    gwtCHECKBOX *_spectralTypeCheckboxF;
    gwtCHECKBOX *_spectralTypeCheckboxG;
    gwtCHECKBOX *_spectralTypeCheckboxK;
    gwtCHECKBOX *_spectralTypeCheckboxM;
    gwtBUTTON *_spectralTypeButton;
    mcsCOMPL_STAT SpectralTypeButtonCB(void *);

    mcsCOMPL_STAT BuildVariabilityWindow();
    gwtWINDOW *_variabilityWindow;
    gwtCHOICE *_variabilityChoice;
    gwtBUTTON *_variabilityButton;
    mcsCOMPL_STAT VariabilityButtonCB(void *);
    
    // Interface with search-calibrator server
    evhINTERFACE _sclServer;

    // Received message which is currently processed
    msgMESSAGE _msg;
};
#endif /*!sclguiPANEL_H*/

/*___oOo___*/
