/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPANEL.C,v 1.1 2004-12-01 07:25:55 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     25-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * sclguiPANEL class definition.
 */

static char *rcsId="@(#) $Id: sclguiPANEL.C,v 1.1 2004-12-01 07:25:55 mella Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
using namespace std;


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "sclguiPANEL.h"
#include "sclguiPrivate.h"

/*
 * Local Variables
 */
#define sclguiNB_CALIBRATOR_PROPERTIES 14
static char *calibratorProperties[] =
{
    "diamAngul",
    "diamAngulError",
    "Mo",
    "Lo",
    "Ko",
    "Ho",
    "Jo",
    "Io",
    "Ro",
    "Vo",
    "Bo",
    "mult",
    "vis",
    "visError",
    NULL
};
#define sclguiNB_STAR_PROPERTIES 31
static char *starProperties[] =
{
   "hd",
   "hip",
   "ra",
   "dec",
   "pmdec",
   "pmra",
   "plx",
   "tsp",
   "varflag",
   "multflag",
   "glat",
   "glon",
   "radvel",
   "diam",
   "meth",
   "wlen",
   "photflux",
   "units",
   "U",
   "B",
   "V",
   "R",
   "I",
   "J",
   "H",
   "K",
   "L",
   "M",
   "N",
   "velocrotat",
   "color",
   NULL
};
static string _generalHelp("The purpose of this panel is to present the result of the calibrators research and\nto allow a manual selection of the best calibrators from the list of selected stars.\nThe upper window shows the parameters of the Science Object.\nThe bottom window shows the list of selected Calibrators\nIf no results appears in this window, press the button SHOW ALL RESULTS.\nThis action displays the list of stars with measured or computed angular diameter.\n      case 1: this list is not empty.\nNo stars have an expected accuracy satisfaying the contraints of the Automatic Selection\nset by the expected accuracy of the Science Object.\nSolution: this contraint can be relax by increasing the expected absolute error on the Science Object\nsquared visibility in the previous Search Calib panel.\n      case 2: this list is empty.\nNo stars founded around the Science Object satisfaying the selection criteria of angular\ndistance and magnitude.\n  Solution 1: Increase Range in R.A. and/or Range in DEC.in the Search Calib panel.\n  Solution 2: Increase the Magnitude Range in the Search Calib panel.");

/*
 * Class constructor
 */

/**
 * Constructs a new sclguiPANEL.
 */
sclguiPANEL::sclguiPANEL()
{
    logExtDbg("sclguiPANEL::sclguiPANEL");
}


/*
 * Class destructor
 */

/*
 * Destructs the sclguiPANEL.
 */
sclguiPANEL::~sclguiPANEL()
{
    logExtDbg("sclguiPANEL::~sclguiPANEL");
    
}




/*
 * Public methods
 */
mcsCOMPL_STAT sclguiPANEL::AppInit()
{
    logExtDbg("sclguiPANEL::AppInit()");

/*  evhCMD_KEY key("SEARCH");
    evhCMD_CALLBACK cb(this, (evhCMD_CB_METHOD)&sclsvrSERVER::SearchCB);
    AddCallback(key, cb);
*/

    mcsCOMPL_STAT status;
    // init by default on localhost:1234
    // \todo make it adjustable giving parameters to the constructor
    _theGui = new gwtGUI();
    status = _theGui->ConnectToRemoteGui("localhost",1234, mcsGetProcName());
    
    if (status == FAILURE)
    {
        /* \todo errAdd */
        cout << "connection on localhost:1234 failed" << endl;
        return FAILURE;
    }
    
    // prepare Event Handling for reception of messages from the gui
    evhIOSTREAM_CALLBACK cb(this, (evhIOSTREAM_CB_METHOD)&sclguiPANEL::GuiSocketCB);
    evhIOSTREAM_KEY key(_theGui->GetSd());
    AddCallback(key, cb);

    // prepare window
    _mainWindow = new gwtWINDOW();
    _mainWindow->AttachAGui(_theGui);
    _mainWindow->SetTitle("JMMC Calibrators Group V?.?");
    _mainWindow->SetHelp(_generalHelp);

    // prepare widgets
    _showAllResultsButton = new gwtBUTTON("SHOW ALL RESULTS","No Help");
    _showAllResultsButton->PlaceAtTop(mcsTRUE);
    _resetButton = new gwtBUTTON("RESET","No Help");
    _resetButton->PlaceAtTop(mcsTRUE);
    _printButton = new gwtBUTTON("PRINT","No Help");
    _printButton->PlaceAtTop(mcsTRUE);
    _abortButton = new gwtBUTTON("ABORT","No Help");
    _abortButton->PlaceAtTop(mcsTRUE);

    // place science star information
    _scienceStarTextarea = new gwtTEXTAREA("--",4,50,"No Help");
    _scienceStarTextarea->SetLabel("Science star");
    
    // the results table presents the entry number, the calibrator properties followed by star ones. 
    // the table is empty at the beginning
    int nbOfProperties = sclguiNB_CALIBRATOR_PROPERTIES+sclguiNB_STAR_PROPERTIES + 1 ;
    _resultsTable = new gwtTABLE(0,nbOfProperties);
    _resultsTable->SetLabel("Results");
    
    // insert first comulmn Header
    _resultsTable->SetColumnHeader(0, "Number");
    
    // insert headers for calibrator properties
    int columnsIdx=1;
    while(columnsIdx < sclguiNB_CALIBRATOR_PROPERTIES)
    {
        _resultsTable->SetColumnHeader(columnsIdx, calibratorProperties[columnsIdx]);
        columnsIdx++;
    }

    // insert headers for calibrator properties
    columnsIdx=0;
    while(columnsIdx < sclguiNB_STAR_PROPERTIES)
    {
        _resultsTable->SetColumnHeader(columnsIdx + sclguiNB_CALIBRATOR_PROPERTIES,
                                       starProperties[columnsIdx]);
        columnsIdx++;
    }

    // insert resume textfield
    _resumeTextfield = new gwtTEXTFIELD();
    _resumeTextfield->SetLabel("Resume");
    
    // prepare subpanels
    _selectPanel = new gwtSUBPANEL("SELECT CALIBRATORS");
    _selectPanel->SetLabel("Sort above list...");
    
    _deletePanel = new gwtSUBPANEL("DELETE");
    _deletePanel->SetLabel("Delete Star");
    _deleteTextfield = new gwtTEXTFIELD("1","Line number to delete");
    _deleteTextfield->SetLabel("Star number");
    _deletePanel->Add(_deleteTextfield);
    
    _loadPanel = new gwtSUBPANEL("LOAD");
    _loadPanel->SetLabel("Load File");
    _loadTextfield = new gwtTEXTFIELD("*.txt", "File Name to load");
    _loadTextfield->SetLabel("File Name to load");
    _loadPanel->Add(_loadTextfield);

    _savePanel = new gwtSUBPANEL("SAVE");
    _savePanel->SetLabel("Save File");
    _saveTextfield = new gwtTEXTFIELD("*.txt", "File name to save current list of calibrators");
    _saveTextfield->SetLabel("File Name to save");
    _savePanel->Add(_saveTextfield);
   
    
    // add widgets to the main window 
    _mainWindow->Add(_scienceStarTextarea);
    _mainWindow->Add(_resultsTable);
    _mainWindow->Add(_resumeTextfield);
    _mainWindow->AddContainer(_selectPanel); 
    _mainWindow->AddContainer(_deletePanel); 
    _mainWindow->AddContainer(_loadPanel); 
    _mainWindow->AddContainer(_savePanel); 
    
    // following elements are placed here because 11th element is placed
    // before the 2nd one
    // \todo find a solution to get element from map unsorted
    _mainWindow->Add(_showAllResultsButton);
    _mainWindow->Add(_resetButton);
    _mainWindow->Add(_printButton);
    _mainWindow->Add(_abortButton);
   

    // associate callbacks
    _showAllResultsButton->AttachCB(this,
                                    (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ShowAllResultsButtonCB);
    _resetButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ResetButtonCB);
    _printButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::PrintButtonCB);
    _abortButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::AbortButtonCB);
    _selectPanel->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SelectPanelCB);
    _deletePanel->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::DeletePanelCB);
    _loadPanel->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::LoadPanelCB);
    _savePanel->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SavePanelCB);


    _mainWindow->Show();
    
    
    return SUCCESS;
}



/*
 * Protected methods
 */


/*
 * Private methods
 */

/**
 *  User callback associated to the  ShowAllResults button.
 */
mcsCOMPL_STAT sclguiPANEL::ShowAllResultsButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ShowAllResultsButtonCB()");
    string s;
    s.append(_deleteTextfield->GetText());
    s.append("-");
    _deleteTextfield->SetText(s);
    cout << "Show all results :" << _deleteTextfield->GetText() << endl;
    _mainWindow->Hide();
    _mainWindow->Show();
    return SUCCESS;
}

/**
 *  User callback associated to the Reset button.
 */
mcsCOMPL_STAT sclguiPANEL::ResetButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ResetButtonCB()");
    return SUCCESS;
}

/**
 *  User callback associated to the Print button.
 */
mcsCOMPL_STAT sclguiPANEL::PrintButtonCB(void *)
{
    logExtDbg("sclguiPANEL::PrintButtonCB()");
    return SUCCESS;
}

/**
 *  User callback associated to the Abort button.
 */
mcsCOMPL_STAT sclguiPANEL::AbortButtonCB(void *)
{
    logExtDbg("sclguiPANEL::AbortButtonCB()");
   
    _mainWindow->Hide();
    exit (EXIT_SUCCESS);
    return SUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 */
mcsCOMPL_STAT sclguiPANEL::SelectPanelCB(void *)
{
    logExtDbg("sclguiPANEL::SelectPanelCB()");
    return SUCCESS;
}

/**
 *  User callback associated to the Delete Panel.
 */
mcsCOMPL_STAT sclguiPANEL::DeletePanelCB(void *)
{
    logExtDbg("sclguiPANEL::DeletePanelCB()");
    cout << "Delete star nb :" << _deleteTextfield->GetText() << endl;
    return SUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 */
mcsCOMPL_STAT sclguiPANEL::LoadPanelCB(void *)
{
    logExtDbg("sclguiPANEL::LoadPanelCB()");
    cout << "Load :" << _loadTextfield->GetText() << endl;
    return SUCCESS;
}

/**
 *  User callback associated to the Save Panel.
 */
mcsCOMPL_STAT sclguiPANEL::SavePanelCB(void *)
{
    logExtDbg("sclguiPANEL::SavePanelCB()");
    cout << "Save :" << _saveTextfield->GetText() << endl;
    return SUCCESS;
}


/**
 *  User callback associated to the  button.
mcsCOMPL_STAT sclguiPANEL::ButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ButtonCB()");
    return SUCCESS;
}
 */

/**
 *  Main callback used to get back events from the GUI. Actually there must be
 *  only one evh callbak to inform the gwt.
 *
 * \param sd  the socket descriptor
 * \param obj nc
 *
 * \return  SUCCESS or FAILURE
 */
evhCB_COMPL_STAT sclguiPANEL::GuiSocketCB (const int sd,void* obj)
{
    logExtDbg("sclguiPANEL::GuiSocketCB()");

    int size;
    mcsSTRING80 msg;
    size = read(sd, msg,80);
    msg[size]=0;
    string data(msg);
    _theGui->ReceiveData(data);

    return evhCB_SUCCESS;
}





/*___oOo___*/
