#ifndef sclguiPANEL_H
#define sclguiPANEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiPANEL.h,v 1.10 2005-02-17 09:25:43 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

protected:
    // Command callbacks
    virtual evhCB_COMPL_STAT GetCalCB(msgMESSAGE &msg, void*);

    // Command reply callbacks
    virtual evhCB_COMPL_STAT GetCalReplyCB(msgMESSAGE &msg, void*);
    
private:
    sclsvrREQUEST _request;
    sclsvrCALIBRATOR_LIST _currentList;
    sclsvrCALIBRATOR_LIST _coherentDiameterList;
    sclsvrCALIBRATOR_LIST _visibilityOkList;
    sclsvrCALIBRATOR_LIST _displayList;
    void FillResultsTable(sclsvrCALIBRATOR_LIST *list);

    // number of cds return, diameter coherent and visibility
    mcsINT32 _found;
    mcsINT32 _diam;
    mcsINT32 _vis;
    
    // central point for exchange with the remote GUI 
    gwtGUI *_theGui;
    string _guiHostname;
    mcsINT32 _guiPort;
    evhCB_COMPL_STAT GuiSocketCB(const int sd, void *obj);

    // _mainWindow and its widgets with associated callbacks
    mcsCOMPL_STAT BuildMainWindow();
    gwtWINDOW *_mainWindow;
    
    gwtBUTTON *_abortButton;
    mcsCOMPL_STAT AbortButtonCB(void *);
    gwtBUTTON *_showAllResultsButton;
    mcsCOMPL_STAT ShowAllResultsButtonCB(void *);
    gwtBUTTON *_resetButton;
    mcsCOMPL_STAT ResetButtonCB(void *);
    gwtBUTTON *_printButton;
    mcsCOMPL_STAT PrintButtonCB(void *);
    
    gwtTEXTAREA *_scienceStarTextarea;
    gwtTABLE *_resultsTable;
    gwtTEXTFIELD * _resumeTextfield;
    
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
