/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPANEL.C,v 1.2 2004-12-01 15:33:54 mella Exp $"
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

static char *rcsId="@(#) $Id: sclguiPANEL.C,v 1.2 2004-12-01 15:33:54 mella Exp $"; 
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

    BuildMainWindow();
  
    _mainWindow->Show();
      
    return SUCCESS;
}

/** 
 *  Build the main window.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildMainWindow()
{
    logExtDbg("sclguiPANEL::BuildMainWindow()");
    
    static string windowHelp("The purpose of this panel is to present the result of the calibrators research and\nto allow a manual selection of the best calibrators from the list of selected stars.\nThe upper window shows the parameters of the Science Object.\nThe bottom window shows the list of selected Calibrators\nIf no results appears in this window, press the button SHOW ALL RESULTS.\nThis action displays the list of stars with measured or computed angular diameter.\n      case 1: this list is not empty.\nNo stars have an expected accuracy satisfaying the contraints of the Automatic Selection\nset by the expected accuracy of the Science Object.\nSolution: this contraint can be relax by increasing the expected absolute error on the Science Object\nsquared visibility in the previous Search Calib panel.\n      case 2: this list is empty.\nNo stars founded around the Science Object satisfaying the selection criteria of angular\ndistance and magnitude.\n  Solution 1: Increase Range in R.A. and/or Range in DEC.in the Search Calib panel.\n  Solution 2: Increase the Magnitude Range in the Search Calib panel.");
    // prepare window
    _mainWindow = new gwtWINDOW();
    _mainWindow->AttachAGui(_theGui);
    _mainWindow->SetTitle("JMMC Calibrators Group V?.?");
    _mainWindow->SetHelp(windowHelp);

    // prepare widgets
    _showAllResultsButton = new gwtBUTTON("SHOW ALL RESULTS","Display of all results without sort");
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
    // \todo place text according the command line
    // _scienceStarTextarea->SetText("...");
    
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
    _resumeTextfield->SetText("...");
    
    // prepare subpanels
    _selectPanel = new gwtSUBPANEL("SELECT CALIBRATORS");
    _selectPanel->SetLabel("Sort above list...");
    _selectPanel->SetHelp("Select a sorting criterium in the list below, click on SORT CALIBRATORS. A popup panel will open, allowing to enter adequate sorting parameters.");
    _selectChoice = new gwtCHOICE();
    _selectChoice->SetLabel("Selection parameters");
    _selectChoice->Add("Maximal distance in RA and DEC");
    _selectChoice->Add("Maximal Magnitude Separation (mag)");
    _selectChoice->Add("Spectral Type");
    _selectChoice->Add("Luminosity Class");
    _selectChoice->Add("Maximal expected accuracy on the calibrator squared visibility (%)");
    _selectChoice->Add("Variability");
    _selectChoice->Add("Multiplicity");
    _selectPanel->Add(_selectChoice);
    
    _deletePanel = new gwtSUBPANEL("DELETE");
    _deletePanel->SetLabel("Delete Star");
    _deletePanel->SetHelp("Deleting of star");
    _deleteTextfield = new gwtTEXTFIELD("1","Line number to delete");
    _deleteTextfield->SetLabel("Star number");
    _deletePanel->Add(_deleteTextfield);
    
    _loadPanel = new gwtSUBPANEL("LOAD");
    _loadPanel->SetLabel("Load File");
    _loadPanel->SetHelp("Loading of star");
    _loadTextfield = new gwtTEXTFIELD("*.txt", "File Name to load");
    _loadTextfield->SetLabel("File Name to load");
    _loadPanel->Add(_loadTextfield);

    _savePanel = new gwtSUBPANEL("SAVE");
    _savePanel->SetLabel("Save File");
    _savePanel->SetHelp("Save file in directory /Resultats/");
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

    // associate the _abortButton action to the window closing event
    _mainWindow->SetCloseCommand(_abortButton->GetWidgetId());

    
    return SUCCESS;
}

/** 
 *  Build the window for sorting by accuracy.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildAccuracyWindow()
{
    logExtDbg("sclguiPANEL::BuilAccuracydWindow()");
    
    static string windowHelp("Allows to fixed the maximum expected relative accuracy of the Calibrators squared visibility.");
    // prepare window
    _accuracyWindow = new gwtWINDOW();
    _accuracyWindow->AttachAGui(_theGui);
    _accuracyWindow->SetTitle("Maximal expected accuracy on the calibrator squared visibility");
    _accuracyWindow->SetHelp(windowHelp);

    // prepare widgets 
    _accuracyTextfield = new gwtTEXTFIELD();
    _accuracyTextfield->SetLabel("Maximal expected relative accuracy on the calibrator squared visibility (%)");
    _accuracyTextfield->SetText("100");
    _accuracyWindow->Add(_accuracyTextfield);

    return SUCCESS;
}

/** 
 *  Build the window for sorting by Luminosity.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildLumWindow()
{
    logExtDbg("sclguiPANEL::BuildLumWindow()");

    static string windowHelp("Allows to select the luminosity class of the Calibrators\nAll luminosity class allowed by default.");
    // prepare window
    _lumWindow = new gwtWINDOW();
    _lumWindow->AttachAGui(_theGui);
    _lumWindow->SetTitle("Luminosity Class");
    _lumWindow->SetHelp(windowHelp);

    // prepare widgets 
    _lumCheckboxI = new gwtCHECKBOX();
    _lumCheckboxI->SetLabel("I");
    _lumCheckboxII = new gwtCHECKBOX();
    _lumCheckboxII->SetLabel("II");
    _lumCheckboxIII = new gwtCHECKBOX();
    _lumCheckboxIII->SetLabel("III");
    _lumCheckboxIV = new gwtCHECKBOX();
    _lumCheckboxIV->SetLabel("IV");
    _lumCheckboxV = new gwtCHECKBOX();
    _lumCheckboxV->SetLabel("V");
    _lumCheckboxVI = new gwtCHECKBOX();
    _lumCheckboxVI->SetLabel("VI");

    _lumWindow->Add(_lumCheckboxI);
    _lumWindow->Add(_lumCheckboxII);
    _lumWindow->Add(_lumCheckboxIII);
    _lumWindow->Add(_lumCheckboxIV);
    _lumWindow->Add(_lumCheckboxV);
    _lumWindow->Add(_lumCheckboxVI);
    
    return SUCCESS;
}


/** 
 *  Build the window for sorting by magnitude.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildMagWindow()
{
    logExtDbg("sclguiPANEL::BuildMagWindow()");
    
    static string windowHelp("Allows to reduce the magnitude difference between the Calibrators and the Science Object.");
    // prepare window
    _magWindow = new gwtWINDOW();
    _magWindow->AttachAGui(_theGui);
    _magWindow->SetTitle("Maximal Magnitude Separation");
    _magWindow->SetHelp(windowHelp);

    // prepare widgets 
    _magTextfield = new gwtTEXTFIELD();
    _magTextfield->SetLabel("Maximal Magnitude Separation (mag)");
    _magTextfield->SetText("0");
    _magWindow->Add(_magTextfield);

     return SUCCESS;
}

/** 
 *  Build the window for sorting by multiplicity.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildMultWindow()
{
    logExtDbg("sclguiPANEL::BuildMultWindow()");
    
    static string windowHelp("Allows to select stars using the multiplicity flag from All-sky Compiled Catalogue of 2.5 million stars (Kharchenko 2001).");
    // prepare window
    _multWindow = new gwtWINDOW();
    _multWindow->AttachAGui(_theGui);
    _multWindow->SetTitle("Multiplicity");
    _multWindow->SetHelp(windowHelp);

    // prepare widgets 
    _multChoice = new gwtCHOICE();
    _multChoice->SetLabel("Multiplicity");
    _multChoice->Add("Authorised");
    _multChoice->Add("Forbidden");
    _multWindow->Add(_multChoice);

     return SUCCESS;
}

/** 
 *  Build the window for sorting by RaDec.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildRaDecWindow()
{
    logExtDbg("sclguiPANEL::BuildRaDecWindow()");
    
    static string windowHelp("Allows to reduce the size of the field around the Science Object by the choice of the maximum distance of the Calibrators to the Science Object in R.A. and DEC.");
    // prepare window
    _raDecWindow = new gwtWINDOW();
    _raDecWindow->AttachAGui(_theGui);
    _raDecWindow->SetTitle("Maximal distance in RA and DEC");
    _raDecWindow->SetHelp(windowHelp);

    // prepare widgets 
   _raDecTextfield = new gwtTEXTFIELD();
    _raDecTextfield->SetLabel("Maximal RA Separation (mn)");
    _raDecTextfield->SetText("0");
    _raDecWindow->Add(_raDecTextfield);

   _raDecTextfield = new gwtTEXTFIELD();
    _raDecTextfield->SetLabel("Maximal DEC Separation (degree)");
    _raDecTextfield->SetText("0");
    _raDecWindow->Add(_raDecTextfield);
     
    return SUCCESS;
}

/** 
 *  Build the window for sorting by Spectral Type.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildSpectralTypeWindow()
{
    logExtDbg("sclguiPANEL::BuildSpectralTypeWindow()");
    
    static string windowHelp("Allows to select the spectral type of the Calibrators.\nAll spectral type allowed by default.");
    // prepare window
    _spectralTypeWindow = new gwtWINDOW();
    _spectralTypeWindow->AttachAGui(_theGui);
    _spectralTypeWindow->SetTitle("Spectral Type");
    _spectralTypeWindow->SetHelp(windowHelp);

    // prepare widgets 
    _spectralTypeCheckboxO = new gwtCHECKBOX();
    _spectralTypeCheckboxO->SetLabel("O");
    _spectralTypeCheckboxB = new gwtCHECKBOX();
    _spectralTypeCheckboxB->SetLabel("B");
    _spectralTypeCheckboxA = new gwtCHECKBOX();
    _spectralTypeCheckboxA->SetLabel("A");
    _spectralTypeCheckboxF = new gwtCHECKBOX();
    _spectralTypeCheckboxF->SetLabel("F");
    _spectralTypeCheckboxG = new gwtCHECKBOX();
    _spectralTypeCheckboxG->SetLabel("G");
    _spectralTypeCheckboxK = new gwtCHECKBOX();
    _spectralTypeCheckboxK->SetLabel("K");
    _spectralTypeCheckboxM = new gwtCHECKBOX();
    _spectralTypeCheckboxM->SetLabel("M");
    
    _spectralTypeWindow->Add(_spectralTypeCheckboxO);
    _spectralTypeWindow->Add(_spectralTypeCheckboxB);
    _spectralTypeWindow->Add(_spectralTypeCheckboxA);
    _spectralTypeWindow->Add(_spectralTypeCheckboxF);
    _spectralTypeWindow->Add(_spectralTypeCheckboxG);
    _spectralTypeWindow->Add(_spectralTypeCheckboxK);
    _spectralTypeWindow->Add(_spectralTypeCheckboxM);

     return SUCCESS;
}

/** 
 *  Build the window for sorting by Variability.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildVariabilityWindow()
{
    logExtDbg("sclguiPANEL::BuildVariabilityWindow()");
    
    static string windowHelp("Allows to select stars using the variability flag from All-sky Compiled Catalogue of 2.5 million stars (Kharchenko 2001).");
    // prepare window
    _variabilityWindow = new gwtWINDOW();
    _variabilityWindow->AttachAGui(_theGui);
    _variabilityWindow->SetTitle("Variability");
    _variabilityWindow->SetHelp(windowHelp);

    // prepare widgets 
    _variabilityChoice = new gwtCHOICE();
    _variabilityChoice->SetLabel("Variability");
    _variabilityChoice->Add("Authorised");
    _variabilityChoice->Add("Forbidden");
    _variabilityWindow->Add(_variabilityChoice);

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
    cout << "sorting by : "<< _selectChoice->GetSelectedItemValue() <<endl;
    cout << "index is : "<< _selectChoice->GetSelectedItem() <<endl;
  
    switch(_selectChoice->GetSelectedItem())
    {
        case 0:
            BuildRaDecWindow();
            _raDecWindow->Show();
            break;
        case 1:
            BuildMagWindow();
            _magWindow->Show();
            break;
        case 2:
            BuildSpectralTypeWindow();
            _spectralTypeWindow->Show();
            break;
        case 3:
            BuildLumWindow();
            _lumWindow->Show();
            break;
        case 4:
            BuildAccuracyWindow();
            _accuracyWindow->Show();
            break;
        case 5:
            BuildVariabilityWindow();
            _variabilityWindow->Show();
            break;
        case 6:
            BuildMultWindow();
            _multWindow->Show();
            break;
    }
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
    char msg[1024];
    size = read(sd, msg,1024);
    msg[size]=0;
    string data(msg);
    _theGui->ReceiveData(data);

    return evhCB_SUCCESS;
}


/*___oOo___*/
