#ifndef sclguiPANEL_H
#define sclguiPANEL_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPANEL.h,v 1.2 2004-12-01 15:34:08 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     25-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * sclguiPANEL class declaration.
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

/*
 * Class declaration
 */

/**
 * This class aims to build the gui for search calib application.
 * 
 */
class sclguiPANEL : public evhSERVER
{

public:
    sclguiPANEL();
    virtual ~sclguiPANEL();
    virtual mcsCOMPL_STAT AppInit();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiPANEL(const sclguiPANEL&);
    sclguiPANEL& operator=(const sclguiPANEL&);

    // central point for exchange with the remote GUI 
    gwtGUI *_theGui;
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

    mcsCOMPL_STAT BuildLumWindow();
    gwtWINDOW *_lumWindow;
    gwtCHECKBOX *_lumCheckboxI;
    gwtCHECKBOX *_lumCheckboxII;
    gwtCHECKBOX *_lumCheckboxIII;
    gwtCHECKBOX *_lumCheckboxIV;
    gwtCHECKBOX *_lumCheckboxV;
    gwtCHECKBOX *_lumCheckboxVI;

    mcsCOMPL_STAT BuildMagWindow();
    gwtWINDOW *_magWindow;
    gwtTEXTFIELD *_magTextfield;

    mcsCOMPL_STAT BuildMultWindow();
    gwtWINDOW *_multWindow;
    gwtCHOICE *_multChoice;

    mcsCOMPL_STAT BuildRaDecWindow();
    gwtWINDOW *_raDecWindow;
    gwtTEXTFIELD *_raDecTextfield;

    mcsCOMPL_STAT BuildSpectralTypeWindow();
    gwtWINDOW *_spectralTypeWindow;
    gwtCHECKBOX *_spectralTypeCheckboxO;
    gwtCHECKBOX *_spectralTypeCheckboxB;
    gwtCHECKBOX *_spectralTypeCheckboxA;
    gwtCHECKBOX *_spectralTypeCheckboxF;
    gwtCHECKBOX *_spectralTypeCheckboxG;
    gwtCHECKBOX *_spectralTypeCheckboxK;
    gwtCHECKBOX *_spectralTypeCheckboxM;

    mcsCOMPL_STAT BuildVariabilityWindow();
    gwtWINDOW *_variabilityWindow;
    gwtCHOICE *_variabilityChoice;
    
};


#endif /*!sclguiPANEL_H*/

/*___oOo___*/
