/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclguiPANEL.C,v 1.5 2004-12-03 14:06:39 mella Exp $"
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

static char *rcsId="@(#) $Id: sclguiPANEL.C,v 1.5 2004-12-03 14:06:39 mella Exp $"; 
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
 *
 * \param hostname The host name of the remote xml display.
 * \param port  The port of the remote xml display.
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
sclguiPANEL::sclguiPANEL(string hostname, mcsINT32 port)
{
    logExtDbg("sclguiPANEL::sclguiPANEL");
    _guiHostname = hostname;
    _guiPort = port;
    _currentList = NULL;
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
    status = _theGui->ConnectToRemoteGui(_guiHostname , _guiPort, mcsGetProcName());
    
    if (status == FAILURE)
    {
        /* \todo errAdd */
        cout << "connection on " << _guiHostname << ":" << _guiPort << " failed" << endl;
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
    // the table is empty but should be filled with first result of SEARCH 
    FillResultsTable(_currentList);
 
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

    _accuracyButton = new gwtBUTTON("Ok","Start the process");
    _accuracyButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::AccuracyButtonCB);
    _accuracyWindow->Add(_accuracyButton);
    
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
 
    _lumButton = new gwtBUTTON("Ok","Start the process");
    _lumButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::LumButtonCB);
    _lumWindow->Add(_lumButton);
   
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
 
    _magButton = new gwtBUTTON("Ok","Start the process");
    _magButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::MagButtonCB);
    _magWindow->Add(_magButton);
   
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
 
    _multButton = new gwtBUTTON("Ok","Start the process");
    _multButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::MultButtonCB);
    _multWindow->Add(_multButton);
   
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
    _raDecTextfieldRa = new gwtTEXTFIELD();
    _raDecTextfieldRa->SetLabel("Maximal RA Separation (mn)");
    _raDecTextfieldRa->SetText("0");
    _raDecWindow->Add(_raDecTextfieldRa);

    _raDecTextfieldDec = new gwtTEXTFIELD();
    _raDecTextfieldDec->SetLabel("Maximal DEC Separation (degree)");
    _raDecTextfieldDec->SetText("0");
    _raDecWindow->Add(_raDecTextfieldDec);
 
    _raDecButton = new gwtBUTTON("Ok","Start the process");
    _raDecButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::RaDecButtonCB);
    _raDecWindow->Add(_raDecButton);
      
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
 
    _spectralTypeButton = new gwtBUTTON("Ok","Start the process");
    _spectralTypeButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::SpectralTypeButtonCB);
    _spectralTypeWindow->Add(_spectralTypeButton);
 
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
 
    _variabilityButton = new gwtBUTTON("Ok","Start the process");
    _variabilityButton->AttachCB(this, (gwtCOMMAND::CB_METHOD) &sclguiPANEL::VariabilityButtonCB);
    _variabilityWindow->Add(_variabilityButton);
   
     return SUCCESS;
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

    int nbOfProperties = sclguiNB_CALIBRATOR_PROPERTIES+sclguiNB_STAR_PROPERTIES + 1 ;
    int nbOfRows;

    if(list == NULL)
    {
        nbOfRows=0;
    }
    else
    {
        nbOfRows = list->Size();
    }
    _resultsTable = new gwtTABLE(nbOfRows, nbOfProperties);
    _resultsTable->SetLabel("Results");
    
    // insert first comulmn Header
    _resultsTable->SetColumnHeader(0, "Number");
    
    // insert headers for calibrator properties
    int columnsIdx=1;
    while(columnsIdx <= sclguiNB_CALIBRATOR_PROPERTIES)
    {
        _resultsTable->SetColumnHeader(columnsIdx, calibratorProperties[columnsIdx-1]);
        columnsIdx++;
    }

    // insert headers for calibrator properties
    columnsIdx=0;
    while(columnsIdx < sclguiNB_STAR_PROPERTIES)
    {
        _resultsTable->SetColumnHeader(columnsIdx + sclguiNB_CALIBRATOR_PROPERTIES + 1,
                                       starProperties[columnsIdx]);
        columnsIdx++;
    }

    // end to fill the table
    if(list != NULL)
    {
        for (unsigned int el = 0; el < list->Size(); el++)
        {
            sclsvrCALIBRATOR *calibrator;
            ostringstream elStr;
            elStr << el;
            _resultsTable->SetCell(el,0,elStr.str());

            calibrator = (sclsvrCALIBRATOR*)list->GetNextStar((el==0));
            int i;
            
            // add calibrator properties raws
            for (i=ANGULAR_DIAMETER_ID; i <= VISIBILITY_ERROR_ID ; i++)
            {
                mcsSTRING64 value;
                if( calibrator->GetProperty((sclsvrPROPERTY_ID)(i),value) == FAILURE )
                {
                    // \todo errAdd
                    _resultsTable->SetCell(el,i+1,"error");
                }
                else
                {
                    _resultsTable->SetCell(el,i+1,value);
                }
            }

            // add calibrator properties raws
            for ( int j = DATA_LINK_ID ; j <= PHOT_COLOR_EXCESS_ID ; j++)
            {
                mcsSTRING64 value;
                if( calibrator->GetProperty((vobsUCD_ID)j,value) == FAILURE )
                {
                    // \todo errAdd
                    _resultsTable->SetCell(el,j+1+i,"error");
                }
                else
                {
                    _resultsTable->SetCell(el,j+1+i,value);
                }
            }
        }
    }

    
}

/**
 *  User callback associated to the  ShowAllResults button.
 */
mcsCOMPL_STAT sclguiPANEL::ShowAllResultsButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ShowAllResultsButtonCB()");
    // this stupid callback update one widget and show the window again
    string s;
    s.append(_deleteTextfield->GetText());
    s.append("-");
    _deleteTextfield->SetText(s);
    cout << "Show all results :" << _deleteTextfield->GetText() << endl;
    _mainWindow->Hide();
    _mainWindow->Show();
    
    _theGui->SetStatus(true, "Show all result button pressed");
    return SUCCESS;
}

/**
 *  User callback associated to the Reset button.
 */
mcsCOMPL_STAT sclguiPANEL::ResetButtonCB(void *)
{
    logExtDbg("sclguiPANEL::ResetButtonCB()");
    //_theGui->SetStatus(true, "Reset button pressed");

    msgMANAGER_IF manager;
    
    if ( SendCommand("SEARCH", "sclsvrServer", "-objetName ETA_TAU -mag 2.96 -maxReturn 50 -diffRa 3600 -diffDec 300 -band K -minMagRange 1 -maxMagRange 5 -ra 03:47:29.08 -dec 24:06:18.50 -baseMin 46.64762 -baseMax 102.45 -lambda 0.65 -vis 0.922 -visErr 0.09", 0) == FAILURE )
    {
        errDisplayStack();
        errCloseStack();
        _currentList=NULL;
        _theGui->SetStatus(false, "Problem while sending data");
    }
    else
    {
        msgMESSAGE    msg;
        // wait 2 min max
        if ( manager.Receive(msg ,120000) == FAILURE )
        {
            errDisplayStack();
            errCloseStack();
            _currentList=NULL;
            _theGui->SetStatus(false, "Problem while waiting data");
        }
        else
        {
            cout <<" the result was received"<<endl;
            _theGui->SetStatus(true, "Reset button pressed");

            _currentList = new sclsvrCALIBRATOR_LIST();

            miscDYN_BUF dynBuf;
            miscDynBufInit(&dynBuf);

            miscDynBufAppendString(&dynBuf, msg.GetBodyPtr());
            _currentList->UnPack(&dynBuf);
            _currentList->Display();
            _theGui->SetStatus(true, "Calibrator list was successfully built");

            FillResultsTable(_currentList);
            _mainWindow->Hide();
            BuildMainWindow();
            _mainWindow->Show();
        }
    }


    return SUCCESS;
}

/**
 *  User callback associated to the Print button.
 */
mcsCOMPL_STAT sclguiPANEL::PrintButtonCB(void *)
{
    logExtDbg("sclguiPANEL::PrintButtonCB()");
    _theGui->SetStatus(true, "Print button pressed");
    return SUCCESS;
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
    return SUCCESS;
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
 *  User callback associated to the Accuracy button.
 */
mcsCOMPL_STAT sclguiPANEL::AccuracyButtonCB(void *)
{
    logExtDbg("sclguiPANEL::AccuracyButtonCB()");
    cout << "data: " <<_accuracyTextfield->GetText() <<endl;
    _accuracyWindow->Hide();
    return SUCCESS;
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
    return SUCCESS;
}

/**
 *  User callback associated to the Mag button.
 */
mcsCOMPL_STAT sclguiPANEL::MagButtonCB(void *)
{
    logExtDbg("sclguiPANEL::MagButtonCB()");
    cout << "data: " <<_magTextfield->GetText() <<endl;
    _magWindow->Hide();
    return SUCCESS;
}

/**
 *  User callback associated to the Mult button.
 */
mcsCOMPL_STAT sclguiPANEL::MultButtonCB(void *)
{
    logExtDbg("sclguiPANEL::MultButtonCB()");
    cout << "data: " <<_multChoice->GetSelectedItemValue() <<endl;
    _multWindow->Hide();
    return SUCCESS;
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
    return SUCCESS;
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
    return SUCCESS;
}


/**
 *  User callback associated to the Variability button.
 */
mcsCOMPL_STAT sclguiPANEL::VariabilityButtonCB(void *)
{
    logExtDbg("sclguiPANEL::VariabilityButtonCB()");

    cout << "data: " <<_variabilityChoice->GetSelectedItemValue() <<endl;
    
    _variabilityWindow->Hide();
    return SUCCESS;
}


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
