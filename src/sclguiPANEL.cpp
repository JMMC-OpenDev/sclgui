/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiPANEL.cpp,v 1.40 2005-03-08 07:14:41 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.39  2005/03/07 17:51:44  gzins
 * Added CVS log as file modification history
 *
 * mella     25-Nov-2004  Created
 * gzins     09-Dec-2004  Fixed cast problem with new mcsLOGICAL enumerate
 *
 ******************************************************************************/

/**
 * \file
 * sclguiPANEL class definition.
 */

static char *rcsId="@(#) $Id: sclguiPANEL.cpp,v 1.40 2005-03-08 07:14:41 scetre Exp $"; 
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
    _found = _currentList.Size();
    _diam = _coherentDiameterList.Size();

    int nbOfProperties = _ucdNameDisplay.size() + 1 ;
    _resultsTable = new gwtTABLE(0, nbOfProperties);
    // build legend table
    _legendTable = new gwtTABLE(1, 11);
    _legendTable->SetHeight(14);
    //_legendTable->SetVerticalOrientation(mcsTRUE);
    _legendTable->SetLabel("Catalog Origin");
    _legendTable->SetCell(0, 0, "I/280");
    _legendTable->SetCellBackground(0, 0, sclguiI_280_COLOR);
    _legendTable->SetCell(0, 1, "II/225");
    _legendTable->SetCellBackground(0, 1, sclguiII_225_COLOR);
    _legendTable->SetCell(0, 2, "II/7A");
    _legendTable->SetCellBackground(0, 2, sclguiII_7A_COLOR);
    _legendTable->SetCell(0, 3, "II/246");
    _legendTable->SetCellBackground(0, 3, sclguiII_246_COLOR);
    _legendTable->SetCell(0, 4, "V/50");
    _legendTable->SetCellBackground(0, 4, sclguiV_50_COLOR);
    _legendTable->SetCell(0, 5, "charm");
    _legendTable->SetCellBackground(0, 5, sclguiCHARM_COLOR);
    
    _legendTable->SetCell(0, 6, "B/denis");
    _legendTable->SetCellBackground(0, 6, sclguiDENIS_COLOR);
    _legendTable->SetCell(0, 7, "I/196");
    _legendTable->SetCellBackground(0, 7, sclguiI_196_COLOR);
    _legendTable->SetCell(0, 8, "Cohen");
    _legendTable->SetCellBackground(0, 8, sclguiCOHEN_COLOR);
    _legendTable->SetCell(0, 9, "V/36B");
    _legendTable->SetCellBackground(0, 9, sclguiV_36B_COLOR);
    _legendTable->SetCell(0, 10, "MIDI");
    _legendTable->SetCellBackground(0, 10, sclgui_MIDI_COLOR);



    _confidenceTable = new gwtTABLE(1,3);
    _confidenceTable->SetHeight(14);
    _confidenceTable->SetLabel("Confidence Index");
    _confidenceTable->SetCell(0, 0, "Low");
    _confidenceTable->SetCellBackground(0, 0, sclguiCONFIDENCE_LOW_COLOR);
    _confidenceTable->SetCell(0, 1, "Medium");           
    _confidenceTable->SetCellBackground(0, 1, sclguiCONFIDENCE_MEDIUM_COLOR);
    _confidenceTable->SetCell(0, 2, "High");             
    _confidenceTable->SetCellBackground(0, 2, sclguiCONFIDENCE_HIGH_COLOR);
    
    _varAuthorized = mcsFALSE;
    _multAuthorized = mcsFALSE;
    BuildResultsTableLabelKV();
    BuildResultsTableLabelN();
    BuildResultsTableLabelNComplete();
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

    strcpy(_fileName, "");
    _confirmWindow = NULL;
    _overwriteButton = NULL;
    _confirmLabel = NULL;
    BuildConfirmWindow();
    //_mainWindow->Show();

    return mcsSUCCESS;
}

/**
 * Return the version number of the software.
 */
const char *sclguiPANEL::GetSwVersion()
{
    return sclsvrVERSION;
}

/**
 * Build the confirm window
 *
 * \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT sclguiPANEL::BuildConfirmWindow()
{
    logExtDbg("sclguiPANEL::BuildConfirmWindow()");
    // Build the windows
    if (_confirmWindow == NULL)
    {
        _confirmWindow = new gwtWINDOW();
        _confirmWindow->AttachAGui(_theGui);
    }

    if (_confirmLabel != NULL)
    {
        delete _confirmLabel;
    }
    ostringstream out;
    out << _fileName;
    out << " already exists. Do you want to overwrite it?";
    _confirmLabel = new gwtLABEL(out.str(), "No Help");

    // Create overwrite button and cancel button
    if (_overwriteButton == NULL)
    {
        _overwriteButton = new gwtBUTTON("Overwrite");
        _overwriteButton->PlaceAtTop(mcsTRUE);
        _confirmWindow->Add(_confirmLabel);
        _confirmWindow->Add(_overwriteButton);

        // Associate callbacks
        _overwriteButton->AttachCB
            (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::OverwriteButtonCB);
    }
    
    return mcsSUCCESS;
}

/**
 * Overwrite button callback
 */
mcsCOMPL_STAT sclguiPANEL::OverwriteButtonCB()
{
    logExtDbg("sclguiPANEL::OverwriteButtonCB()");
   
    // Save the display list in the specified file
    mcsSTRING64 usrMsg;
    if (_displayList.Save(_fileName, _ucdName, _request, _saveFlag) ==
        mcsFAILURE)
    {
        // if save failed, send user message and close the popup
        sprintf(usrMsg, "'%s' file HAS NOT been created", _fileName);
        _theGui->SetStatus(false, usrMsg, errUserGet());
        errCloseStack();
        _confirmWindow->Hide();
        return mcsFAILURE;
    }
    // if failed succeed, send user message and close the popup
    sprintf(usrMsg, "'%s' file has been created", _fileName);
    _theGui->SetStatus(true, usrMsg);

    _confirmWindow->Hide();
    
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
    _mainWindow->SetTitle("JMMC Calibrator Group " sclsvrVERSION);
    _mainWindow->SetHelp(windowHelp);

    // Prepare widgets
    _showAllResultsButton = new gwtBUTTON
        ("SHOW ALL RESULTS", "Display of all results with coherent diameter.");
    _showAllResultsButton->PlaceAtTop(mcsTRUE);
    _resetButton = new gwtBUTTON("RESET", "This button reset the star list. It will show the list with visibility OK.");
    _resetButton->PlaceAtTop(mcsTRUE);

    // Place science star information
    _scienceStarTextarea = new gwtTEXTAREA("--", 4, 50, "No Help");
    _scienceStarTextarea->SetVerticalOrientation(mcsTRUE);
    _scienceStarTextarea->SetLabel("Science star");
   
    // Insert resume textfield
    _resumeTextArea = new gwtTEXTAREA("--", 1, 50, "No Help");
    _resumeTextArea->SetLabel("Results");
    _resumeTextArea->SetVerticalOrientation(mcsTRUE);

    // The results table presents the entry number, the calibrator properties
    // followed by star ones. 
    // The table is empty but should be filled with first result of SEARCH 
    FillResultsTable(&_displayList);

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
    _loadTextfield = new gwtTEXTFIELD("*.scl", "File Name to load");
    _loadTextfield->SetLabel("File Name to load");
    _loadPanel->Add(_loadTextfield);

    _savePanel = new gwtSUBPANEL("SAVE");
    _savePanel->SetLabel("Save File");
    _savePanel->SetHelp("Save file in directory /Resultats/");
    mcsSTRING256 fileName;
    strcpy(fileName, _request.GetObjectName());
    strcat(fileName, "_");
    strcat(fileName, _request.GetSearchBand());
    strcat(fileName, ".scl");
    _saveTextfield = new gwtTEXTFIELD
        (fileName, "File name to save current list of calibrators");
    _saveTextfield->SetLabel("File Name to save");
    _savePanel->Add(_saveTextfield);

    _exportPanel = new gwtSUBPANEL("EXPORT");
    _exportPanel->SetLabel("Export File");
    _exportPanel->SetHelp("Export file in directory /Resultats/");
    mcsSTRING256 exportFileName;
    strcpy(exportFileName, _request.GetObjectName());
    strcat(exportFileName, "_");
    strcat(exportFileName, _request.GetSearchBand());
    strcat(exportFileName, ".txt");
    _exportTextfield = new gwtTEXTFIELD
        (exportFileName, "File name to export current list of calibrators");
    _exportTextfield->SetLabel("File Name to export");
    _exportPanel->Add(_exportTextfield);


    // Add widgets to the main window 
    _mainWindow->Add(_scienceStarTextarea);
    _mainWindow->Add(_resumeTextArea);
    _mainWindow->Add(_resultsTable);
    _mainWindow->Add(_legendTable);    
    _mainWindow->Add(_confidenceTable);    
    _mainWindow->AddContainer(_selectPanel); 
    _mainWindow->AddContainer(_deletePanel); 
    _mainWindow->AddContainer(_loadPanel); 
    _mainWindow->AddContainer(_savePanel); 
    _mainWindow->AddContainer(_exportPanel); 

    // Following elements are placed here because 11th element is placed
    // before the 2nd one
    // \todo find a solution to get element from map unsorted
    _mainWindow->Add(_showAllResultsButton);
    _mainWindow->Add(_resetButton);

    // Associate callbacks
    _showAllResultsButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ShowAllResultsButtonCB);
    _resetButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ResetButtonCB);
    _selectPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SelectPanelCB);
    _deletePanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::DeletePanelCB);
    _loadPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::LoadPanelCB);
    _savePanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SavePanelCB);
    _exportPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::ExportPanelCB);

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
    if (_multAuthorized == mcsFALSE)
    {
        _multChoice->Add("Forbidden");
        _multChoice->Add("Authorised");
    }
    else
    {
        _multChoice->Add("Authorised");        
        _multChoice->Add("Forbidden");
    }
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
    if (_varAuthorized == mcsFALSE)
    {
        _variabilityChoice->Add("Forbidden");
        _variabilityChoice->Add("Authorised");
    }
    else
    {
        _variabilityChoice->Add("Authorised");
        _variabilityChoice->Add("Forbidden");
    }
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
    
    // Get the value in the request in order to view constraints on the panel
    ostringstream out;
    out << "NAME\tRAJ2000\tDEJ2000\tMag";
    out << _request.GetSearchBand();
    out << "\tBase-max\tLambda\tDiamVK\n";
    out << "---------\t-----------\t-----------\t------\t--------\t---------\t--------\t--------\n";
    out << _request.GetObjectName() << "\t" << _request.GetObjectRa() << "\t" 
        << _request.GetObjectDec() << "\t" << _request.GetObjectMag() << "\t";
    out << _request.GetMaxBaselineLength() << "\t" 
        << _request.GetObservingWlen() << "\t";
   
    // Build science object with his coordinates
    sclsvrCALIBRATOR scienceObject;
    // Get ra coordinates
    scienceObject.SetPropertyValue(vobsSTAR_POS_EQ_RA_MAIN,
                                   _request.GetObjectRa(), "none");
    // Get dec coordinates
    scienceObject.SetPropertyValue(vobsSTAR_POS_EQ_DEC_MAIN,
                                   _request.GetObjectDec(), "none");
    // Get the science object if it is in the list
    if (_currentList.GetScienceObject(scienceObject) == mcsFAILURE)
    {
        // if not, write ---- in the Science object panel information under diam
        // v_k
        out << scienceObject.GetPropertyValue(sclsvrCALIBRATOR_DIAM_VK);
    }
    else
    {
        // if dian v-k is not affected  write ---- in the Science object 
        // panel information under diam v_k
        if (scienceObject.IsPropertySet(sclsvrCALIBRATOR_DIAM_VK) == mcsTRUE)
        {
            out << scienceObject.GetPropertyValue(sclsvrCALIBRATOR_DIAM_VK);
        }
        else
        {
            out << scienceObject.GetPropertyValue(sclsvrCALIBRATOR_DIAM_VK);
        }
    }
    _scienceStarTextarea->SetText(out.str());

    // Add 1 for the number column
    int nbOfProperties = _ucdNameDisplay.size() + 1 ;
    int nbOfRows;

    nbOfRows = list->Size();
    _resultsTable->SetDimension(nbOfRows, nbOfProperties);
    _resultsTable->SetHeight(160);

    _resultsTable->SetVerticalOrientation(mcsTRUE);

    // Insert first column Header
    _resultsTable->SetColumnHeader(0, "Number");

    // Insert headers for calibrator properties
    int propIdx=0;
    vobsSTAR_PROPERTY_ID_LIST::iterator ucdNameOrderIterator;
    ucdNameOrderIterator = _ucdNameDisplay.begin();
    while(ucdNameOrderIterator != _ucdNameDisplay.end())
    {
        _resultsTable->SetColumnHeader
            (propIdx+1, 
             tmpCalibrator.GetProperty((char *)(*ucdNameOrderIterator).data())->GetName());
        propIdx++;
        ucdNameOrderIterator++;
    }
    
    // End to fill the table
    for (unsigned int el = 0; el < list->Size(); el++)
    {
        sclsvrCALIBRATOR *calibrator;
        ostringstream elStr;
        elStr << el+1;
        _resultsTable->SetCell(el,0,elStr.str());

        calibrator = (sclsvrCALIBRATOR*)list->GetNextStar((mcsLOGICAL)(el==0));
        int i=0;
        
        // Add calibrator properties raws
        ucdNameOrderIterator = _ucdNameDisplay.begin();
        while(ucdNameOrderIterator != _ucdNameDisplay.end())
        {
            string propvalue;
            vobsSTAR_PROPERTY *property;
            property = calibrator->GetProperty((char *)(*ucdNameOrderIterator).data());
            
            propvalue.append
                (calibrator->GetPropertyValue((char *)(*ucdNameOrderIterator).data()));
            _resultsTable->SetCell(el, i+1, propvalue);
            _resultsTable->SetCellBackground(el, i+1, "#ffffff");
            if (strcmp(property->GetOrigin(), vobsSTAR_COMPUTED_PROP) == 0)
            {
                if (property->GetConfidenceIndex() == vobsCONFIDENCE_LOW)
                {
                    _resultsTable->
                        SetCellBackground(el, i+1,
                                          sclguiCONFIDENCE_LOW_COLOR);
                }
                else if (property->GetConfidenceIndex() ==
                         vobsCONFIDENCE_MEDIUM)
                {
                    _resultsTable->
                        SetCellBackground(el, i+1,
                                          sclguiCONFIDENCE_MEDIUM_COLOR);
                }
                else if (property->GetConfidenceIndex() == vobsCONFIDENCE_HIGH)
                {
                    _resultsTable->
                        SetCellBackground(el, i+1,
                                          sclguiCONFIDENCE_HIGH_COLOR);
                }
            }
            if (strcmp(property->GetOrigin(), "I/280") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiI_280_COLOR);
            }
            if (strcmp(property->GetOrigin(), "II/225/catalog") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiII_225_COLOR);
            }
            if (strcmp(property->GetOrigin(), "II/7A/catalog") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiII_7A_COLOR);
            }
            if (strcmp(property->GetOrigin(), "II/246/out") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiII_246_COLOR);
            }
            if (strcmp(property->GetOrigin(), "V/50/catalog") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiV_50_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/386/492/charm") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiCHARM_COLOR);
            }
            /**/
            if (strcmp(property->GetOrigin(), "B/denis") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiDENIS_COLOR);
            }
            if (strcmp(property->GetOrigin(), "I/196/main") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiI_196_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/393/183/catalog") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiCOHEN_COLOR);
            }
            if (strcmp(property->GetOrigin(), "MIDI") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclgui_MIDI_COLOR);
            }
            if (strcmp(property->GetOrigin(), "V/36B/bsc4s") == 0)
            {
                _resultsTable->SetCellBackground(el, i+1, sclguiV_36B_COLOR);
            }

            i++;
            ucdNameOrderIterator++;

        } // End for each properties

    } // End for each calibrators


    // Update resume textfield
    ostringstream output;
    output << "Number of stars: " << _found << " found, "  
        << _diam << " with coherent diameter and "
        << _withNoVarMult << " without variability and multiplicity";
    _resumeTextArea->SetText(output.str());
    
}

/**
 *  User callback associated to the  ShowAllResults button.
 */
mcsCOMPL_STAT sclguiPANEL::ShowAllResultsButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ShowAllResultsButtonCB()");

    _varAuthorized = mcsTRUE;
    _multAuthorized = mcsTRUE;

    _coherentDiameterList.Clear();
    _coherentDiameterList.Copy(_currentList, mcsFALSE);

    _displayList.Clear();
    _displayList.Copy(_coherentDiameterList);
    
    // if the observed band is N, Show All Result button show all property
    if (strcmp(_request.GetSearchBand(), "N") == 0)
    {
        _ucdNameDisplay = _ucdNameforNComplete;
    }
    
    FillResultsTable(&_displayList);
    _mainWindow->Update();

    _theGui->SetStatus(true, "'Show all result' button has been pressed");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Reset button.
 */
mcsCOMPL_STAT sclguiPANEL::ResetButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ResetButtonCB()");
    _theGui->SetStatus(true, "'Reset' button has been pressed");
    
    _varAuthorized = mcsFALSE;
    _multAuthorized = mcsFALSE;
    
    _coherentDiameterList.Clear();
    // Extract from the CDS return the list of coherent diameter
    _coherentDiameterList.Copy(_currentList, mcsFALSE);
    // Filter the coherent diameter list
    _coherentDiameterList.FilterByVariability(_varAuthorized);
    _coherentDiameterList.FilterByMultiplicity(_multAuthorized);
    
    _displayList.Clear();
    _displayList.Copy(_coherentDiameterList);
    
    // if the observed band is N, reset button show principal property
    if (strcmp(_request.GetSearchBand(), "N") == 0)
    {
        _ucdNameDisplay = _ucdNameforN;
    }

    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 */
mcsCOMPL_STAT sclguiPANEL::SelectPanelCB(void *)
{
    logExtDbg("sclguiPANEL::SelectPanelCB()");

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
    // Get the line number as an integer
    mcsUINT32 starNumber;
    // Inform user
    mcsSTRING64 usrMsg;
    // Dummy buffer
    // Used with sscanf to know if a value is an integer or not
    mcsSTRING32 dummyBuffer;
    
    if (sscanf((_deleteTextfield->GetText()).c_str(), "%d%s", 
               &starNumber, dummyBuffer) != 1)
    {
        _theGui->SetStatus(false, "Invalid star number", 
                           "Value should be an integer");
        return mcsFAILURE;
    }
    if ((starNumber > _displayList.Size()) || (starNumber<=0))
    {
        sprintf(usrMsg, "Value should be a positive integer lower than %d",
                _displayList.Size());
        _theGui->SetStatus(false, "Invalid star number", usrMsg); 
        return mcsFAILURE;
    }
    else
    {
        // if te value is ok, delete star
        _displayList.Delete(starNumber);

        // Update main window
        FillResultsTable(&_displayList);
        _mainWindow->Update();
    }
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 */
mcsCOMPL_STAT sclguiPANEL::LoadPanelCB(void *)
{
    logExtDbg("sclguiPANEL::LoadPanelCB()");

    // Get the name of the textfield
    mcsSTRING256 fileName;
    strcpy(fileName, (_loadTextfield->GetText()).c_str());

    _currentList.Clear();
    
    mcsSTRING64 usrMsg;
    
    if (_currentList.Load(fileName, _request) == mcsFAILURE)
    {
        sprintf(usrMsg, "'%s' file HAS NOT been loaded", fileName);
        _theGui->SetStatus(false, usrMsg, errUserGet());
        errCloseStack();
        return mcsFAILURE;
    }
    sprintf(usrMsg, "'%s' file has been loaded", fileName);
        _theGui->SetStatus(true, usrMsg);

    _coherentDiameterList.Clear();
    _displayList.Clear();
    
    _coherentDiameterList.Copy(_currentList, mcsFALSE);
    _diam = _coherentDiameterList.Size();

    // Filter the coherent diameter list
    _coherentDiameterList.FilterByVariability(_varAuthorized);
    _coherentDiameterList.FilterByMultiplicity(_multAuthorized);

    _displayList.Copy(_coherentDiameterList);

    _withNoVarMult=_displayList.Size();
    _found = _currentList.Size();

    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Save Panel.
 */
mcsCOMPL_STAT sclguiPANEL::SavePanelCB(void *)
{
    logExtDbg("sclguiPANEL::SavePanelCB()");
    
    _saveFlag=mcsTRUE;    
    // Get the name of the textfield
    strcpy(_fileName, (_saveTextfield->GetText()).c_str());

    // Check if the file already exist
    if (miscFileExists(_fileName, mcsFALSE) == mcsTRUE)
    {
        // if file already exist, create a confirm button
        BuildConfirmWindow();
        _confirmWindow->Show();
    }
    // if it is not an existing file, write in it
    else
    {
        if (OverwriteButtonCB() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Export Panel.
 */
mcsCOMPL_STAT sclguiPANEL::ExportPanelCB(void *)
{
    logExtDbg("sclguiPANEL::ExportPanelCB()");

    _saveFlag=mcsFALSE;
    // Get the name of the textfield
    strcpy(_fileName, (_exportTextfield->GetText()).c_str());
  
    // Check if the file already exist
    if (miscFileExists(_fileName, mcsFALSE) == mcsTRUE)
    {
        // if file already exist, create a confirm button
        BuildConfirmWindow();
        _confirmWindow->Show();
    }
    // if it is not an existing file, write in it
    else
    {
        if (OverwriteButtonCB() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Accuracy button.
 */
mcsCOMPL_STAT sclguiPANEL::AccuracyButtonCB(void *)
{
    logExtDbg("sclguiPANEL::AccuracyButtonCB()");
    _accuracyWindow->Hide();
    mcsFLOAT visMax;
    // Get the value of the magnitude range
    sscanf((_accuracyTextfield->GetText()).c_str(), "%f", &visMax);
    
    // changed vis from % to value between 0 and 1
    visMax = visMax/100;
    
    _displayList.FilterByVisibility(visMax);
    
    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Lum button.
 */
mcsCOMPL_STAT sclguiPANEL::LumButtonCB(void *)
{
    logExtDbg("sclguiPANEL::LumButtonCB()");
    _lumWindow->Hide();
    
    // for each spectral type, check if it is wanted by the user
    // if wanted, put it in the list of spectral type
    char     *lumClassList[7];
    mcsINT32 idx = 0;
    if ((int) _lumCheckboxI->GetValue() == 1)
    {
        lumClassList[idx++] = "I";
    }
    if ((int) _lumCheckboxII->GetValue() == 1)
    {
        lumClassList[idx++] = "II";
    }
    if ((int) _lumCheckboxIII->GetValue() == 1)
    {
        lumClassList[idx++] = "III";
    }
    if ((int) _lumCheckboxIV->GetValue() == 1)
    {
        lumClassList[idx++] = "IV";
    }
    if ((int) _lumCheckboxV->GetValue() == 1)
    {
        lumClassList[idx++] = "V";
    }
    if ((int) _lumCheckboxVI->GetValue() == 1)
    {
        lumClassList[idx++] = "VI";
    }
    lumClassList[idx++] = NULL;
    
    _displayList.FilterBySpectralType(NULL, lumClassList);
    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Mag button.
 */
mcsCOMPL_STAT sclguiPANEL::MagButtonCB(void *)
{
    logExtDbg("sclguiPANEL::MagButtonCB()");
    _magWindow->Hide();
   
    mcsFLOAT magRange;
    // Get the value of the magnitude range
    sscanf((_magTextfield->GetText()).c_str(), "%f", &magRange);    
    
    // Get the observed band and the magnitude
    const char *band;
    band = _request.GetSearchBand();
    mcsFLOAT magnitude;
    magnitude = _request.GetObjectMag();
    
    _displayList.FilterByMagnitude(band, magnitude, magRange);
    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 *  User callback associated to the Variability button.
 */
mcsCOMPL_STAT sclguiPANEL::VariabilityButtonCB(void *)
{
    logExtDbg("sclguiPANEL::VariabilityButtonCB()");

    // if variability are authorized variable, recreate list with variability
    // flag because by default, variability flag are forbidden.
    if (strcmp((_variabilityChoice->GetSelectedItemValue()).c_str(), 
               "Authorised") == 0)
    {
        if (_varAuthorized == mcsFALSE)
        {
            _varAuthorized = mcsTRUE;
        }
        _variabilityWindow->Hide();
        return mcsSUCCESS;
    }
    // if the wanted state is forbidden and if the variability flag is
    // authorized, it' like a reset. Else, do nothing, the state is ever
    // forbidden
    if (strcmp((_variabilityChoice->GetSelectedItemValue()).c_str(), 
               "Forbidden") == 0)
    {
        if (_varAuthorized == mcsTRUE)
        {
            _varAuthorized = mcsFALSE;
            // Filter the visibility ok list
            _displayList.FilterByVariability(_varAuthorized); 

        }
        else 
        {
            _variabilityWindow->Hide();
            return mcsSUCCESS;
        }
    }

    _variabilityWindow->Hide();

    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Mult button.
 */
mcsCOMPL_STAT sclguiPANEL::MultButtonCB(void *)
{
    logExtDbg("sclguiPANEL::MultButtonCB()");

    // if variability are authorized variable, recreate list with variability
    // flag because by default, variability flag are forbidden.
    if (strcmp((_multChoice->GetSelectedItemValue()).c_str(), 
               "Authorised") == 0)
    {
        if (_multAuthorized == mcsFALSE)
        {
            _multAuthorized = mcsTRUE;
        }
        _multWindow->Hide();
        return mcsSUCCESS;
    }
    // if the wanted state is forbidden and if the variability flag is
    // authorized, it' like a reset. Else, do nothing, the state is ever
    // forbidden
    if (strcmp((_multChoice->GetSelectedItemValue()).c_str(), 
               "Forbidden") == 0)
    {
        if (_multAuthorized == mcsTRUE)
        {
            _multAuthorized = mcsFALSE;
            // Filter the visibility ok list
            _displayList.FilterByMultiplicity(_multAuthorized); 
        }
        else
        {
            _multWindow->Hide();
            return mcsSUCCESS;
        }
    }
    
    _multWindow->Hide();

    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();
    return mcsSUCCESS;
}


/**
 *  User callback associated to the RaDec button.
 */
mcsCOMPL_STAT sclguiPANEL::RaDecButtonCB(void *)
{
    logExtDbg("sclguiPANEL::RaDecButtonCB()");

    _raDecWindow->Hide();
    mcsFLOAT raRange, decRange;
    // Get Value of raRange and decRange
    if (sscanf((_raDecTextfieldRa->GetText()).c_str(), "%f", &raRange) != 1)
    {
        // todo err
        return mcsFAILURE;
    }
    // convert raRange from min to degree
    raRange=raRange*15/60;
    if (sscanf((_raDecTextfieldDec->GetText()).c_str(), "%f", &decRange) != 1)
    {
        // todo err
        return mcsFAILURE;
    }
    
    // Get the value of ra dec in user request
    const char *ra, *dec;
    ra  = _request.GetObjectRa();
    dec = _request.GetObjectDec();
    
    // Remove stars which are not in the box
    _displayList.FilterByDistanceSeparation(ra, dec,
                                            raRange,
                                            decRange);
    // todo put the value from GetText instead of 0.1
    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();
    return mcsSUCCESS;
}


/**
 *  User callback associated to the SpectralType button.
 */
mcsCOMPL_STAT sclguiPANEL::SpectralTypeButtonCB(void *)
{
    logExtDbg("sclguiPANEL::SpectralTypeButtonCB()");
    _spectralTypeWindow->Hide();

    char     *tempClassList[8];
    mcsINT32 idx = 0;
    // for each spectral type, check if it is wanted by the user
    // if wanted, put it in the list of spectral type
    if ((int) _spectralTypeCheckboxO->GetValue() == 1)
    {
        tempClassList[idx++] = "O";
    }
    if ((int) _spectralTypeCheckboxB->GetValue() == 1)
    {
        tempClassList[idx++] = "B";
    }
    if ((int) _spectralTypeCheckboxA->GetValue() == 1)
    {
        tempClassList[idx++] = "A";
    }
    if ((int) _spectralTypeCheckboxF->GetValue() == 1)
    {
        tempClassList[idx++] = "F";
    }
    if ((int) _spectralTypeCheckboxG->GetValue() == 1)
    {
        tempClassList[idx++] = "G";
    }
    if ((int) _spectralTypeCheckboxK->GetValue() == 1)
    {
        tempClassList[idx++] = "K";
    }
    if ((int) _spectralTypeCheckboxM->GetValue() == 1)
    {
        tempClassList[idx++] = "M";
    }
    tempClassList[idx++] = NULL;
    
    _displayList.FilterBySpectralType(tempClassList, NULL);
    // Update main window
    FillResultsTable(&_displayList);
    _mainWindow->Update();
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

void sclguiPANEL::BuildResultsTableLabelKV()
{
    // build ucdNameforKV list
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_VIS2            );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_VIS2_ERROR      );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_DIAM_BV         );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_DIAM_VR         );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_DIAM_VK         );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_DIAM_VK_ERROR   );
    _ucdNameforKV.push_back(vobsSTAR_ID_HIP                  );
    _ucdNameforKV.push_back(vobsSTAR_ID_HD                   );
    _ucdNameforKV.push_back(vobsSTAR_ID_DM                   );
    _ucdNameforKV.push_back(vobsSTAR_POS_EQ_RA_MAIN          );
    _ucdNameforKV.push_back(vobsSTAR_POS_EQ_DEC_MAIN         );
    _ucdNameforKV.push_back(vobsSTAR_POS_EQ_PMDEC            );
    _ucdNameforKV.push_back(vobsSTAR_POS_EQ_PMRA             );
    _ucdNameforKV.push_back(vobsSTAR_POS_PARLX_TRIG          );
    _ucdNameforKV.push_back(vobsSTAR_SPECT_TYPE_MK           );
    _ucdNameforKV.push_back(vobsSTAR_CODE_VARIAB             );
    _ucdNameforKV.push_back(vobsSTAR_CODE_MULT_FLAG          );
    _ucdNameforKV.push_back(vobsSTAR_POS_GAL_LAT             );
    _ucdNameforKV.push_back(vobsSTAR_POS_GAL_LON             );
    _ucdNameforKV.push_back(vobsSTAR_VELOC_HC                );
    _ucdNameforKV.push_back(vobsSTAR_LD_DIAM                 );
    _ucdNameforKV.push_back(vobsSTAR_LD_DIAM_ERROR           );
    _ucdNameforKV.push_back(vobsSTAR_UD_DIAM                 );
    _ucdNameforKV.push_back(vobsSTAR_UD_DIAM_ERROR           );
    _ucdNameforKV.push_back(vobsSTAR_UDDK_DIAM               );
    _ucdNameforKV.push_back(vobsSTAR_UDDK_DIAM_ERROR         );
    _ucdNameforKV.push_back(vobsSTAR_OBS_METHOD              );
    _ucdNameforKV.push_back(vobsSTAR_INST_WAVELENGTH_VALUE   );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_U              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_B              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_V              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_R              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_I              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_J              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_H              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_K              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_L              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_M              );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_JHN_N              );
    _ucdNameforKV.push_back(vobsSTAR_VELOC_ROTAT             );
    _ucdNameforKV.push_back(vobsSTAR_PHOT_COLOR_EXCESS       );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_MO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_LO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_KO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_HO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_JO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_IO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_RO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_VO              );
    _ucdNameforKV.push_back(sclsvrCALIBRATOR_BO              );

}

void sclguiPANEL::BuildResultsTableLabelN()
{
    _ucdNameforN.push_back(vobsSTAR_ID_HD);
    _ucdNameforN.push_back(vobsSTAR_POS_EQ_RA_MAIN );
    _ucdNameforN.push_back(vobsSTAR_POS_EQ_DEC_MAIN);
    _ucdNameforN.push_back(sclsvrCALIBRATOR_VIS2      );
    _ucdNameforN.push_back(sclsvrCALIBRATOR_VIS2_ERROR);
    _ucdNameforN.push_back(vobsSTAR_DIAM12      );
    _ucdNameforN.push_back(vobsSTAR_DIAM12_ERROR);
    _ucdNameforN.push_back(vobsSTAR_PHOT_FLUX_IR_12);
    _ucdNameforN.push_back(vobsSTAR_SPECT_TYPE_MK);
    _ucdNameforN.push_back(vobsSTAR_PHOT_JHN_N);
    _ucdNameforN.push_back(sclsvrCALIBRATOR_VIS2_8       );
    _ucdNameforN.push_back(sclsvrCALIBRATOR_VIS2_8_ERROR );
    _ucdNameforN.push_back(sclsvrCALIBRATOR_VIS2_13      );
    _ucdNameforN.push_back(sclsvrCALIBRATOR_VIS2_13_ERROR);

}

void sclguiPANEL::BuildResultsTableLabelNComplete()
{
    _ucdNameforNComplete.push_back(vobsSTAR_ID_HD);
    _ucdNameforNComplete.push_back(vobsSTAR_POS_EQ_RA_MAIN );
    _ucdNameforNComplete.push_back(vobsSTAR_POS_EQ_DEC_MAIN);
    _ucdNameforNComplete.push_back(sclsvrCALIBRATOR_VIS2      );
    _ucdNameforNComplete.push_back(sclsvrCALIBRATOR_VIS2_ERROR);
    _ucdNameforNComplete.push_back(vobsSTAR_DIAM12      );
    _ucdNameforNComplete.push_back(vobsSTAR_DIAM12_ERROR);
    _ucdNameforNComplete.push_back(vobsSTAR_IR_FLUX_ORIGIN);
    _ucdNameforNComplete.push_back(vobsSTAR_PHOT_FLUX_IR_12);
    _ucdNameforNComplete.push_back(vobsSTAR_PHOT_FLUX_IR_12_ERROR);
    _ucdNameforNComplete.push_back(vobsSTAR_SPECT_TYPE_MK);
    _ucdNameforNComplete.push_back(vobsSTAR_PHOT_JHN_N);
    _ucdNameforNComplete.push_back(sclsvrCALIBRATOR_VIS2_8       );
    _ucdNameforNComplete.push_back(sclsvrCALIBRATOR_VIS2_8_ERROR );
    _ucdNameforNComplete.push_back(sclsvrCALIBRATOR_VIS2_13      );
    _ucdNameforNComplete.push_back(sclsvrCALIBRATOR_VIS2_13_ERROR);
    
    _ucdNameforNComplete.push_back(vobsSTAR_REF_STAR);
    _ucdNameforNComplete.push_back(vobsSTAR_CODE_MULT_FLAG);
    _ucdNameforNComplete.push_back(vobsSTAR_CODE_VARIAB);
    _ucdNameforNComplete.push_back(vobsSTAR_PHOT_JHN_V);
    _ucdNameforNComplete.push_back(vobsSTAR_PHOT_JHN_H);
    _ucdNameforNComplete.push_back(vobsSTAR_POS_PARLX_TRIG_ERROR);
    _ucdNameforNComplete.push_back(vobsSTAR_POS_PARLX_TRIG);
    _ucdNameforNComplete.push_back(vobsSTAR_POS_EQ_PMRA);
    _ucdNameforNComplete.push_back(vobsSTAR_POS_EQ_PMDEC);
    _ucdNameforNComplete.push_back(vobsSTAR_PHOT_EXTINCTION_TOTAL);
    _ucdNameforNComplete.push_back(vobsSTAR_CHI2_QUALITY);   
    _ucdNameforNComplete.push_back(vobsSTAR_SP_TYP_PHYS_TEMP_EFFEC);   
}

/*___oOo___*/
