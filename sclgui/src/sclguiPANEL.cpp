/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPANEL.cpp,v 1.1 2005-01-28 10:34:11 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     25-Nov-2004  Created
* gzins     09-Dec-2004  Fixed cast problem with new mcsLOGICAL enumerate
*
*******************************************************************************/

/**
 * \file
 * sclguiPANEL class definition.
 */

static char *rcsId="@(#) $Id: sclguiPANEL.cpp,v 1.1 2005-01-28 10:34:11 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
using namespace std;


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "misc.h"
#include "msg.h"
#include "log.h"
#include "err.h"



/*
 * Search calibrators server
 */
#include "sclsvr.h"
/*
 * Local Headers 
 */
#include "sclguiPANEL.h"
#include "sclguiPrivate.h"

/*
 * Local Variables
 */

/*
 * Class constructor
 */

/**
 * Constructs a new sclguiPANEL.
 *
 * \param hostname The host name of the remote xml display.
 * \param port  The port of the remote xml display.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
sclguiPANEL::sclguiPANEL(string hostname, mcsINT32 port): 
    _sclServer("Search-calibrator server", "sclsvrServer", 120000)

{
    logExtDbg("sclguiPANEL::sclguiPANEL");
    _guiHostname = hostname;
    _guiPort = port;
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

    // Init by default on localhost:1234
    // \todo make it adjustable giving parameters to the constructor
    _theGui = new gwtGUI();
    if (_theGui->ConnectToRemoteGui(_guiHostname , _guiPort,
                                         mcsGetProcName()) == mcsFAILURE)
    {
        /* \todo errAdd */
        cout << "connection on " << _guiHostname << ":" << _guiPort << " failed" << endl;
        return mcsFAILURE;
    }
    
    // Prepare Event Handling for reception of messages from the gui
    evhIOSTREAM_CALLBACK ioStreamCB
        (this, (evhIOSTREAM_CB_METHOD)&sclguiPANEL::GuiSocketCB);
    evhIOSTREAM_KEY ioStreamKey(_theGui->GetSd());
    AddCallback(ioStreamKey, ioStreamCB);

    // Add callback for GETCAL command
    evhCMD_KEY cmdKey(sclsvrGETCAL_CMD_NAME);
    evhCMD_CALLBACK cmdCB(this, (evhCMD_CB_METHOD)&sclguiPANEL::GetCalCB);
    AddCallback(cmdKey, cmdCB);

    BuildMainWindow();
  
    _mainWindow->Show();
      
    return mcsSUCCESS;
}

/** 
 *  Build the main window.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildMainWindow()
{
    logExtDbg("sclguiPANEL::BuildMainWindow()");
    
    static string windowHelp
        ("The purpose of this panel is to present the result of the "
         "calibrators research and\nto allow a manual selection of the best "
         "calibrators from the list of selected stars.\nThe upper window shows "
         "the parameters of the Science Object.\nThe bottom window shows the "
         "list of selected Calibrators\nIf no results appears in this window, "
         "press the button SHOW ALL RESULTS.\nThis action displays the list of "
         "stars with measured or computed angular diameter.\n      case 1: "
         "this list is not empty.\nNo stars have an expected accuracy "
         "satisfaying the contraints of the Automatic Selection\nset by the "
         "expected accuracy of the Science Object.\nSolution: this contraint "
         "can be relax by increasing the expected absolute error on the "
         "Science Object\nsquared visibility in the previous Search Calib "
         "panel.\n      case 2: this list is empty.\nNo stars founded around "
         "the Science Object satisfaying the selection criteria of "
         "angular\ndistance and magnitude.\n  Solution 1: Increase Range in "
         "R.A. and/or Range in DEC.in the Search Calib panel.\n  Solution 2: "
         "Increase the Magnitude Range in the Search Calib panel.");
    
    // Prepare window
    _mainWindow = new gwtWINDOW();
    _mainWindow->AttachAGui(_theGui);
    _mainWindow->SetTitle("JMMC Calibrators Group V?.?");
    _mainWindow->SetHelp(windowHelp);

    // Prepare widgets
    _showAllResultsButton = new gwtBUTTON
        ("SHOW ALL RESULTS", "Display of all results without sort");
    _showAllResultsButton->PlaceAtTop(mcsTRUE);
    _resetButton = new gwtBUTTON("RESET", "No Help");
    _resetButton->PlaceAtTop(mcsTRUE);
    _printButton = new gwtBUTTON("PRINT", "No Help");
    _printButton->PlaceAtTop(mcsTRUE);
    _abortButton = new gwtBUTTON("ABORT", "No Help");
    _abortButton->PlaceAtTop(mcsTRUE);

    // Place science star information
    _scienceStarTextarea = new gwtTEXTAREA("--", 4, 50, "No Help");
    _scienceStarTextarea->SetLabel("Science star");
    // \todo place text according the command line
    // _scienceStarTextarea->SetText("...");
    
    // The results table presents the entry number, the calibrator properties
    // followed by star ones. 
    // The table is empty but should be filled with first result of SEARCH 
    FillResultsTable(&_currentList);
 
    // Insert resume textfield
    _resumeTextfield = new gwtTEXTFIELD();
    _resumeTextfield->SetLabel("Resume");
    _resumeTextfield->SetText("...");
    
    // Prepare subpanels
    _selectPanel = new gwtSUBPANEL("SELECT CALIBRATORS");
    _selectPanel->SetLabel("Sort above list...");
    _selectPanel->SetHelp
        ("Select a sorting criterium in the list below, click on SORT "
         "CALIBRATORS. A popup panel will open, allowing to enter adequate "
         "sorting parameters.");
    _selectChoice = new gwtCHOICE();
    _selectChoice->SetLabel("Selection parameters");
    _selectChoice->Add("Maximal distance in RA and DEC");
    _selectChoice->Add("Maximal Magnitude Separation (mag)");
    _selectChoice->Add("Spectral Type");
    _selectChoice->Add("Luminosity Class");
    _selectChoice->Add
        ("Maximal expected accuracy on the calibrator squared visibility (%)");
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
    _saveTextfield = new gwtTEXTFIELD
        ("*.txt", "File name to save current list of calibrators");
    _saveTextfield->SetLabel("File Name to save");
    _savePanel->Add(_saveTextfield);
   
    // Add widgets to the main window 
    _mainWindow->Add(_scienceStarTextarea);
    _mainWindow->Add(_resultsTable);
    _mainWindow->Add(_resumeTextfield);
    _mainWindow->AddContainer(_selectPanel); 
    _mainWindow->AddContainer(_deletePanel); 
    _mainWindow->AddContainer(_loadPanel); 
    _mainWindow->AddContainer(_savePanel); 
    
    // Following elements are placed here because 11th element is placed
    // before the 2nd one
    // \todo find a solution to get element from map unsorted
    _mainWindow->Add(_showAllResultsButton);
    _mainWindow->Add(_resetButton);
    _mainWindow->Add(_printButton);
    _mainWindow->Add(_abortButton);

    // Associate callbacks
    _showAllResultsButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ShowAllResultsButtonCB);
    _resetButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ResetButtonCB);
    _printButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::PrintButtonCB);
    _abortButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::AbortButtonCB);
    _selectPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SelectPanelCB);
    _deletePanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::DeletePanelCB);
    _loadPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::LoadPanelCB);
    _savePanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SavePanelCB);

    // Associate the _abortButton action to the window closing event
    _mainWindow->SetCloseCommand(_abortButton->GetWidgetId());
    
    return mcsSUCCESS;
}

/** 
 *  Build the window for sorting by accuracy.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildAccuracyWindow()
{
    logExtDbg("sclguiPANEL::BuilAccuracydWindow()");
    
    static string windowHelp
        ("Allows to fixed the maximum expected relative accuracy of the "
         "Calibrators squared visibility.");
    
    // Prepare window
    _accuracyWindow = new gwtWINDOW();
    _accuracyWindow->AttachAGui(_theGui);
    _accuracyWindow->SetTitle
        ("Maximal expected accuracy on the calibrator squared visibility");
    _accuracyWindow->SetHelp(windowHelp);

    // Prepare widgets 
    _accuracyTextfield = new gwtTEXTFIELD();
    _accuracyTextfield->SetLabel
        ("Maximal expected relative accuracy on the calibrator squared "
         "visibility (%)");
    _accuracyTextfield->SetText("100");
    _accuracyWindow->Add(_accuracyTextfield);

    _accuracyButton = new gwtBUTTON("Ok","Start the process");
    _accuracyButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::AccuracyButtonCB);
    _accuracyWindow->Add(_accuracyButton);
    
    return mcsSUCCESS;
}

/** 
 *  Build the window for sorting by Luminosity.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildLumWindow()
{
    logExtDbg("sclguiPANEL::BuildLumWindow()");

    static string windowHelp
        ("Allows to select the luminosity class of the Calibrators\nAll "
         "luminosity class allowed by default.");
   
    // Prepare window
    _lumWindow = new gwtWINDOW();
    _lumWindow->AttachAGui(_theGui);
    _lumWindow->SetTitle("Luminosity Class");
    _lumWindow->SetHelp(windowHelp);

    // Prepare widgets 
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
 
    _lumButton = new gwtBUTTON("Ok","Start the process");
    _lumButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::LumButtonCB);
    _lumWindow->Add(_lumButton);
   
    return mcsSUCCESS;
}


/** 
 *  Build the window for sorting by magnitude.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildMagWindow()
{
    logExtDbg("sclguiPANEL::BuildMagWindow()");
    
    static string windowHelp 
        ("Allows to reduce the magnitude difference between the Calibrators "
         "and the Science Object.");
   
    // Prepare window
    _magWindow = new gwtWINDOW();
    _magWindow->AttachAGui(_theGui);
    _magWindow->SetTitle("Maximal Magnitude Separation");
    _magWindow->SetHelp(windowHelp);

    // Prepare widgets 
    _magTextfield = new gwtTEXTFIELD();
    _magTextfield->SetLabel("Maximal Magnitude Separation (mag)");
    _magTextfield->SetText("0");
    _magWindow->Add(_magTextfield);
 
    _magButton = new gwtBUTTON("Ok","Start the process");
    _magButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::MagButtonCB);
    _magWindow->Add(_magButton);
   
     return mcsSUCCESS;
}

/** 
 *  Build the window for sorting by multiplicity.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildMultWindow()
{
    logExtDbg("sclguiPANEL::BuildMultWindow()");
    
    static string windowHelp
        ("Allows to select stars using the multiplicity flag from All-sky "
         "Compiled Catalogue of 2.5 million stars (Kharchenko 2001).");

    // Prepare window
    _multWindow = new gwtWINDOW();
    _multWindow->AttachAGui(_theGui);
    _multWindow->SetTitle("Multiplicity");
    _multWindow->SetHelp(windowHelp);

    // Prepare widgets 
    _multChoice = new gwtCHOICE();
    _multChoice->SetLabel("Multiplicity");
    _multChoice->Add("Authorised");
    _multChoice->Add("Forbidden");
    _multWindow->Add(_multChoice);
 
    _multButton = new gwtBUTTON("Ok","Start the process");
    _multButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::MultButtonCB);
    _multWindow->Add(_multButton);
   
     return mcsSUCCESS;
}

/** 
 *  Build the window for sorting by RaDec.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildRaDecWindow()
{
    logExtDbg("sclguiPANEL::BuildRaDecWindow()");
    
    static string windowHelp
        ("Allows to reduce the size of the field around the Science Object by "
         "the choice of the maximum distance of the Calibrators to the Science "
         "Object in R.A. and DEC.");
   
    // Prepare window
    _raDecWindow = new gwtWINDOW();
    _raDecWindow->AttachAGui(_theGui);
    _raDecWindow->SetTitle("Maximal distance in RA and DEC");
    _raDecWindow->SetHelp(windowHelp);

    // Prepare widgets 
    _raDecTextfieldRa = new gwtTEXTFIELD();
    _raDecTextfieldRa->SetLabel("Maximal RA Separation (mn)");
    _raDecTextfieldRa->SetText("0");
    _raDecWindow->Add(_raDecTextfieldRa);

    _raDecTextfieldDec = new gwtTEXTFIELD();
    _raDecTextfieldDec->SetLabel("Maximal DEC Separation (degree)");
    _raDecTextfieldDec->SetText("0");
    _raDecWindow->Add(_raDecTextfieldDec);
 
    _raDecButton = new gwtBUTTON("Ok","Start the process");
    _raDecButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::RaDecButtonCB);
    _raDecWindow->Add(_raDecButton);
      
    return mcsSUCCESS;
}

/** 
 *  Build the window for sorting by Spectral Type.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildSpectralTypeWindow()
{
    logExtDbg("sclguiPANEL::BuildSpectralTypeWindow()");
    
    static string windowHelp
        ("Allows to select the spectral type of the Calibrators.\nAll "
         "spectral type allowed by default.");
   
    // Prepare window
    _spectralTypeWindow = new gwtWINDOW();
    _spectralTypeWindow->AttachAGui(_theGui);
    _spectralTypeWindow->SetTitle("Spectral Type");
    _spectralTypeWindow->SetHelp(windowHelp);

    // Prepare widgets 
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
 
    _spectralTypeButton = new gwtBUTTON("Ok","Start the process");
    _spectralTypeButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SpectralTypeButtonCB);
    _spectralTypeWindow->Add(_spectralTypeButton);
 
     return mcsSUCCESS;
}

/** 
 *  Build the window for sorting by Variability.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildVariabilityWindow()
{
    logExtDbg("sclguiPANEL::BuildVariabilityWindow()");
    
    static string windowHelp
        ("Allows to select stars using the variability flag from All-sky "
         "Compiled Catalogue of 2.5 million stars (Kharchenko 2001).");

    // Prepare window
    _variabilityWindow = new gwtWINDOW();
    _variabilityWindow->AttachAGui(_theGui);
    _variabilityWindow->SetTitle("Variability");
    _variabilityWindow->SetHelp(windowHelp);

    // Prepare widgets 
    _variabilityChoice = new gwtCHOICE();
    _variabilityChoice->SetLabel("Variability");
    _variabilityChoice->Add("Authorised");
    _variabilityChoice->Add("Forbidden");
    _variabilityWindow->Add(_variabilityChoice);
 
    _variabilityButton = new gwtBUTTON("Ok","Start the process");
    _variabilityButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::VariabilityButtonCB);
    _variabilityWindow->Add(_variabilityButton);
   
     return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */

/** 
 *  Fill the _resultsTable taking information from the given calibrator list.
 *
 * \param list the list of calibrator to place into the table. 
 * If the list is NULL, the table is filled only with the headers.
 *
 */
void sclguiPANEL::FillResultsTable(sclsvrCALIBRATOR_LIST *list)
{
    logExtDbg("sclguiPANEL::FillResultsTable()");

    sclsvrCALIBRATOR tmpCalibrator;

    // Add 1 for the number column
    int nbOfProperties = tmpCalibrator.NbProperties() + 1 ;
    int nbOfRows;

    nbOfRows = list->Size();
    _resultsTable = new gwtTABLE(nbOfRows, nbOfProperties);
    _resultsTable->SetLabel("Results");
    
    // Insert first column Header
    _resultsTable->SetColumnHeader(0, "Number");
    
    // Insert headers for calibrator properties
    int propIdx;
    for (propIdx = 0; propIdx < tmpCalibrator.NbProperties(); propIdx++)
    {
        _resultsTable->SetColumnHeader
            (propIdx+1,
             tmpCalibrator.GetNextProperty((mcsLOGICAL)(propIdx==0))->GetName());
    }

    // End to fill the table
    for (unsigned int el = 0; el < list->Size(); el++)
    {
        sclsvrCALIBRATOR *calibrator;
        ostringstream elStr;
        elStr << el;
        _resultsTable->SetCell(el,0,elStr.str());

        calibrator = (sclsvrCALIBRATOR*)list->GetNextStar((mcsLOGICAL)(el==0));
        int i;

        // Add calibrator properties raws
        for (i=0; i < calibrator->NbProperties() ; i++)
        {
            string propvalue;
            propvalue.append
                ((calibrator->GetNextProperty((mcsLOGICAL)(i==0)))->GetValue());
            _resultsTable->SetCell(el, i+1, propvalue);
        } // End for each properties

    } // End for each calibrators

}

/**
 *  User callback associated to the  ShowAllResults button.
 */
mcsCOMPL_STAT sclguiPANEL::ShowAllResultsButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ShowAllResultsButtonCB()");

    // This stupid callback update one widget and show the window again
    string s;
    s.append(_deleteTextfield->GetText());
    s.append("-");
    _deleteTextfield->SetText(s);
    cout << "Show all results :" << _deleteTextfield->GetText() << endl;
    _mainWindow->Hide();
    _mainWindow->Show();
    
    _theGui->SetStatus(true, "Show all result button pressed");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Reset button.
 */
mcsCOMPL_STAT sclguiPANEL::ResetButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ResetButtonCB()");
    _theGui->SetStatus(true, "Reset button pressed");

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Print button.
 */
mcsCOMPL_STAT sclguiPANEL::PrintButtonCB(void *)
{
    logExtDbg("sclguiPANEL::PrintButtonCB()");
    _theGui->SetStatus(true, "Print button pressed");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Abort button.
 */
mcsCOMPL_STAT sclguiPANEL::AbortButtonCB(void *)
{
    logExtDbg("sclguiPANEL::AbortButtonCB()");
    _theGui->SetStatus(true, "Bye bye");
   
    _mainWindow->Hide();
    exit (EXIT_SUCCESS);
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 */
mcsCOMPL_STAT sclguiPANEL::SelectPanelCB(void *)
{
    logExtDbg("sclguiPANEL::SelectPanelCB()");
    cout << "sorting by : "<< _selectChoice->GetSelectedItemValue() <<endl;
    //cout << "index is : "<< _selectChoice->GetSelectedItem() <<endl;
  
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
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Delete Panel.
 */
mcsCOMPL_STAT sclguiPANEL::DeletePanelCB(void *)
{
    logExtDbg("sclguiPANEL::DeletePanelCB()");
    cout << "Delete star nb :" << _deleteTextfield->GetText() << endl;
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 */
mcsCOMPL_STAT sclguiPANEL::LoadPanelCB(void *)
{
    logExtDbg("sclguiPANEL::LoadPanelCB()");
    cout << "Load :" << _loadTextfield->GetText() << endl;
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Save Panel.
 */
mcsCOMPL_STAT sclguiPANEL::SavePanelCB(void *)
{
    logExtDbg("sclguiPANEL::SavePanelCB()");
    cout << "Save :" << _saveTextfield->GetText() << endl;
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Accuracy button.
 */
mcsCOMPL_STAT sclguiPANEL::AccuracyButtonCB(void *)
{
    logExtDbg("sclguiPANEL::AccuracyButtonCB()");
    cout << "data: " <<_accuracyTextfield->GetText() <<endl;
    _accuracyWindow->Hide();
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Lum button.
 */
mcsCOMPL_STAT sclguiPANEL::LumButtonCB(void *)
{
    logExtDbg("sclguiPANEL::LumButtonCB()");
    cout << "data: I " <<(int) _lumCheckboxI->GetValue() <<endl;
    cout << "data: II " <<(int) _lumCheckboxII->GetValue() <<endl;
    cout << "data: III " <<(int) _lumCheckboxIII->GetValue() <<endl;
    cout << "data: IV " <<(int) _lumCheckboxIV->GetValue() <<endl;
    cout << "data: V " <<(int) _lumCheckboxV->GetValue() <<endl;
    cout << "data: VI " <<(int) _lumCheckboxVI->GetValue() <<endl;
    _lumWindow->Hide();
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Mag button.
 */
mcsCOMPL_STAT sclguiPANEL::MagButtonCB(void *)
{
    logExtDbg("sclguiPANEL::MagButtonCB()");
    cout << "data: " <<_magTextfield->GetText() <<endl;
    _magWindow->Hide();
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Mult button.
 */
mcsCOMPL_STAT sclguiPANEL::MultButtonCB(void *)
{
    logExtDbg("sclguiPANEL::MultButtonCB()");
    cout << "data: " <<_multChoice->GetSelectedItemValue() <<endl;
    _multWindow->Hide();
    return mcsSUCCESS;
}


/**
 *  User callback associated to the RaDec button.
 */
mcsCOMPL_STAT sclguiPANEL::RaDecButtonCB(void *)
{
    logExtDbg("sclguiPANEL::RaDecButtonCB()");
    cout << "data: Ra " <<_raDecTextfieldRa->GetText() <<endl;
    cout << "data: Dec " <<_raDecTextfieldDec->GetText() <<endl;
    _raDecWindow->Hide();
    return mcsSUCCESS;
}


/**
 *  User callback associated to the SpectralType button.
 */
mcsCOMPL_STAT sclguiPANEL::SpectralTypeButtonCB(void *)
{
    logExtDbg("sclguiPANEL::SpectralTypeButtonCB()");
    cout << "data: O " <<(int) _spectralTypeCheckboxO->GetValue() <<endl;
    cout << "data: B " <<(int) _spectralTypeCheckboxB->GetValue() <<endl;
    cout << "data: A " <<(int) _spectralTypeCheckboxA->GetValue() <<endl;
    cout << "data: F " <<(int) _spectralTypeCheckboxF->GetValue() <<endl;
    cout << "data: G " <<(int) _spectralTypeCheckboxG->GetValue() <<endl;
    cout << "data: K " <<(int) _spectralTypeCheckboxK->GetValue() <<endl;
    cout << "data: M " <<(int) _spectralTypeCheckboxM->GetValue() <<endl;
    _spectralTypeWindow->Hide();
    return mcsSUCCESS;
}


/**
 *  User callback associated to the Variability button.
 */
mcsCOMPL_STAT sclguiPANEL::VariabilityButtonCB(void *)
{
    logExtDbg("sclguiPANEL::VariabilityButtonCB()");

    cout << "data: " <<_variabilityChoice->GetSelectedItemValue() <<endl;
    
    _variabilityWindow->Hide();
    return mcsSUCCESS;
}


/**
 *  Main callback used to get back events from the GUI. Actually there must be
 *  only one evh callbak to inform the gwt.
 *
 * \param sd  the socket descriptor
 * \param obj nc
 *
 * \return  mcsSUCCESS or mcsFAILURE
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
