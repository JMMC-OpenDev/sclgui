#ifndef sclguiPANEL_H
#define sclguiPANEL_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPANEL.h,v 1.1 2004-12-01 07:25:53 mella Exp $"
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

    // widgets
    gwtGUI *_theGui;
    gwtWINDOW *_mainWindow;
    gwtBUTTON *_showAllResultsButton;
    gwtBUTTON *_resetButton;
    gwtBUTTON *_printButton;
    gwtBUTTON *_abortButton;
    gwtTEXTAREA *_scienceStarTextarea;
    gwtTABLE *_resultsTable;
    gwtTEXTFIELD * _resumeTextfield;
    
    gwtSUBPANEL *_selectPanel;
    
    gwtSUBPANEL *_deletePanel;
    gwtTEXTFIELD *_deleteTextfield;
    
    gwtSUBPANEL *_loadPanel;
    gwtTEXTFIELD *_loadTextfield;

    gwtSUBPANEL *_savePanel;
    gwtTEXTFIELD *_saveTextfield;

    // gui callback
    evhCB_COMPL_STAT GuiSocketCB(const int sd, void *obj);
    
    // widget callbacks
    mcsCOMPL_STAT ShowAllResultsButtonCB(void *);
    mcsCOMPL_STAT ResetButtonCB(void *);
    mcsCOMPL_STAT PrintButtonCB(void *);
    mcsCOMPL_STAT AbortButtonCB(void *);
    mcsCOMPL_STAT SelectPanelCB(void *);
    mcsCOMPL_STAT DeletePanelCB(void *);
    mcsCOMPL_STAT LoadPanelCB(void *);
    mcsCOMPL_STAT SavePanelCB(void *);
    
};


#endif /*!sclguiPANEL_H*/

/*___oOo___*/
