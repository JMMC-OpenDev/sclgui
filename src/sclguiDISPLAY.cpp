/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiDISPLAY.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiDISPLAY class.
 */

static char *rcsId="@(#) $Id: sclguiDISPLAY.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"; 
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
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "sclguiDISPLAY.h"
#include "sclguiPrivate.h"

/**
 * Constructs a new sclguiDISPLAY.
 *
 * \param hostname The host name of the remote xml display.
 * \param port  The port of the remote xml display.
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
sclguiDISPLAY::sclguiDISPLAY(string hostname, mcsINT32 port): 
_sclServer("Search-calibrator server", "sclsvrServer", 120000)

{
    logTrace("sclguiDISPLAY::sclguiDISPLAY");
    _guiHostname = hostname;
    _guiPort = port;
    
    // create view
    _requestView = new sclguiREQUEST_VIEW(&_model);
    _resumeView = new sclguiRESUME_VIEW(&_model);
    _tableView = new sclguiTABLE_VIEW(&_model);
    _visibilityFilterView = new sclguiVISIBILITY_FILTER_VIEW(&_model);
    _magnitudeFilterView = new sclguiMAGNITUDE_FILTER_VIEW(&_model);
    _distanceFilterView = new sclguiDISTANCE_FILTER_VIEW(&_model);
    _luminosityFilterView = new sclguiLUMINOSITY_FILTER_VIEW(&_model);
    _spTypeFilterView = new sclguiSPTYPE_FILTER_VIEW(&_model);
    _variabilityFilterView = new sclguiVARIABILITY_FILTER_VIEW(&_model);
    _multiplicityFilterView = new sclguiMULTIPLICITY_FILTER_VIEW(&_model);
     
    // Add view in the model
    _model.AddView(_requestView);
    _model.AddView(_resumeView);
    _model.AddView(_tableView);
    _model.AddView(_visibilityFilterView);
    _model.AddView(_magnitudeFilterView);
    _model.AddView(_distanceFilterView);
    _model.AddView(_luminosityFilterView);
    _model.AddView(_spTypeFilterView);
    _model.AddView(_variabilityFilterView);
    _model.AddView(_multiplicityFilterView);
}

/**
 * Class destructor
 */
sclguiDISPLAY::~sclguiDISPLAY()
{
}

/*
 * Public methods
 */
/**
 * Initialize evh Server
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::AppInit()
{
    logTrace("sclguiDISPLAY::AppInit()");

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
        (this, (evhIOSTREAM_CB_METHOD)&sclguiDISPLAY::GuiSocketCB);
    evhIOSTREAM_KEY ioStreamKey(_theGui->GetSd());
    AddCallback(ioStreamKey, ioStreamCB);

    // Add callback for GETCAL command
    evhCMD_KEY cmdKey(sclsvrGETCAL_CMD_NAME);
    evhCMD_CALLBACK cmdCB(this, (evhCMD_CB_METHOD)&sclguiDISPLAY::GetCalCB);
    AddCallback(cmdKey, cmdCB);

    BuildMainWindow();
    _mainWindow->Show();
    
    BuildConfirmWindow();

    return mcsSUCCESS;
}

/**
 * Return the version number of the software.
 *
 * @return version number of the software
 */
const char *sclguiDISPLAY::GetSwVersion()
{
    return sclsvrVERSION;
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
evhCB_COMPL_STAT  sclguiDISPLAY::GuiSocketCB (const int sd,void* obj)
{
    logTrace(" sclguiDISPLAY::GuiSocketCB()");

    int size;
    char msg[1024];
    size = read(sd, msg,1024);
    msg[size]=0;
    string data(msg);
    _theGui->ReceiveData(data);

    return evhCB_SUCCESS;
}

/**
 * Callback associated to abort button and window's close button
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiDISPLAY::AbortButtonCB(void *)
{
    logTrace("sclguiDISPLAY::AbortButtonCB()");
    _theGui->SetStatus(true, "Search Calibrators results panel closed");

    _mainWindow->Hide();
    exit(EXIT_SUCCESS);
    return mcsSUCCESS;
}

/**
 * Callback associated to ShowAllResult button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiDISPLAY::ShowAllResultsButtonCB(void *)
{
    logTrace("sclguiDISPLAY::ShowAllResultsButtonCB()");
    
    // Reset all filter
    if (_model.ResetFilter() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    _model.ResetDeleteCalirator();
    
    // Disable filter by variability and multipicity because the default state
    // of the model enabled mult and var
    if (_model.DisableFilter(vobsVARIABILITY_FILTER_NAME) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    if (_model.DisableFilter(vobsMULTIPLICITY_FILTER_NAME) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Update main window
    _mainWindow->Update();
    
    // Send message to the XmlBasedGui
    _theGui->SetStatus(true, "Show all stars with coherent diameter");    

    return mcsSUCCESS;
}

/**
 * Callback associated to Reset button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::ResetButtonCB(void *)
{
    logTrace("sclguiDISPLAY::ResetButtonCB()");
   
    // Reset all filter
    if (_model.ResetFilter() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Reset list of unwanted calibrators
    _model.ResetDeleteCalirator();
    
    // Update main window
    _mainWindow->Update();
    
    // Send message to the XmlBasedGui    
    _theGui->SetStatus(true, "Show all stars with coherent diameter and " 
                       "without variability and multiplicity");
    
    return mcsSUCCESS;
}

/**
 * Callback associated to ShowDetails button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::ShowDetailsCB(void *)
{
    logTrace("sclguiDISPLAY::ShowDetailsCB()");
  
    // Say to the table view that details are OK
    if (_tableView->Details(mcsTRUE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // update main window
    _mainWindow->Update();
    
    // Send message to the XmlBasedGui    
    _theGui->SetStatus(true, "Show all details");
     
    return mcsSUCCESS;
}

/**
 * Callback associated to HideDetails button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::HideDetailsCB(void *)
{
    logTrace("sclguiDISPLAY::HideDetailsCB()");
  
    // Say to the table view that details are NOK
    if (_tableView->Details(mcsFALSE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // update main window
     _mainWindow->Update();
    
    // Send message to the XmlBasedGui    
     _theGui->SetStatus(true, "Show less details");
     
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::SelectPanelCB(void *)
{
    logTrace("sclguiDISPLAY::SelectPanelCB()");

    // According to the user choice, show the specific pop up window
    switch(_filterChoice->GetSelectedItem())
    {
        case 0:
            // Show distance filter pop up
            _distanceFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
        case 1:
            // Show magnitude filter pop up
            _magnitudeFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
        case 2:
            // Show temperature class filter pop up
            _spTypeFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
        case 3:
            // Show luminosity class filter pop up
            _luminosityFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
        case 4:
            // Show visibility filter pop_up
            _visibilityFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
        case 5:
            // show variability filter pop up
            _variabilityFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
        case 6:
            // Show multiplicity filter pop up
            _multiplicityFilterView->Show();
            _theGui->SetStatus(true,"");
            break;
    }
    
    return mcsSUCCESS;
}

/**
 * User callback associated to the Delete button
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::DeletePanelCB(void *)
{
    logTrace("sclguiDISPLAY::DeletePanelCB()");

    // Get the actuel looking list
    sclsvrCALIBRATOR_LIST *list = _model.GetCalibratorList();
    // Get the calibrator number to delete
    mcsUINT32 calibratorNumber;
    // Inform user
    mcsSTRING64 usrMsg;
    // Dummy buffer
    // Used with sscanf to know if a value is an integer or not
    mcsSTRING32 dummyBuffer;
    // if the number is not an integer, inform the user
    if (sscanf((_deleteTextfield->GetText()).c_str(), "%d%s",
               &calibratorNumber, dummyBuffer) != 1)
    {
        _theGui->SetStatus(false, "Invalid star number", 
                           "Value should be an integer");
        return mcsFAILURE;
    }
    // if the integer is not positive or lower than zero, inform the user
    if ((calibratorNumber>list->Size())||(calibratorNumber<=0))
    {
        sprintf(usrMsg, "Value should be a positive integer lower than %d",
                list->Size());
        _theGui->SetStatus(false, "Invalid star number", usrMsg); 
        return mcsFAILURE;
    }
    
    sclsvrCALIBRATOR *calibrator = NULL;    
    for (unsigned int el = 0; el < calibratorNumber; el++)
    {
        // Get next calibrator
        calibrator =
            (sclsvrCALIBRATOR *)list->GetNextStar((mcsLOGICAL)(el==0));
    }

    // Add the unwanted star(calibrator) in the model
    if (_model.AddDeleteStar(*calibrator) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    // Convert calibrator number to string
    mcsSTRING32 calibratorNumberBuffer;
    sprintf(calibratorNumberBuffer, "%d", calibratorNumber);
    // Build message to XmlBasedGui
    string statusMsg("Star number ");
    statusMsg.append(calibratorNumberBuffer);
    if (calibrator != NULL)
    {
        statusMsg.append(" (HD ");
        statusMsg.append(calibrator->GetPropertyValue(vobsSTAR_ID_HD));
        statusMsg.append(")");
    }
    statusMsg.append(" has been deleted");

    // Update main window
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 * User callback associated to the Load button
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::LoadPanelCB(void *)
{
    logTrace("sclguiDISPLAY::LoadPanelCB()");

    // Re Initialize the model
    _model.Init();
    
    // Get the name of the textfield
    mcsSTRING256 fileName;
    strcpy(fileName, (_loadTextfield->GetText()).c_str());
    // get the list of the model
    sclsvrCALIBRATOR_LIST calibratorList;
    // get request of the model
    sclsvrREQUEST *request = _model.GetRequest();
    
    mcsSTRING64 usrMsg;
   
    // Load list and request
    if (calibratorList.Load(fileName, *request) == mcsFAILURE)
    {
        sprintf(usrMsg, "'%s' file HAS NOT been loaded", fileName);
        _theGui->SetStatus(false, usrMsg, errUserGet());
        errCloseStack();
        return mcsFAILURE;
    }
    // Set new list in the model
    _model.SetList(calibratorList);

    // Send message to the XmlBasedGui
    sprintf(usrMsg, "'%s' file has been loaded", fileName);
    _theGui->SetStatus(true, usrMsg);

    // Update main window
    _mainWindow->Update();

    return mcsSUCCESS;
}

/**
 * User callback associated to the Save button
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::SavePanelCB(void *)
{
    logTrace("sclguiDISPLAY::SavePanelCB()");

    // Get the fileName enter by the user
    mcsSTRING32 fileName;
    strcpy(fileName, (_saveTextfield->GetText()).c_str());
    
    // Check if the file already exist
    if (miscFileExists(fileName, mcsFALSE) == mcsTRUE)
    {
        // if file already exist, write name in the label
        ostringstream out;
        out << fileName;
        out << " already exists. Do you want to overwrite it?";
        _confirmLabel->SetText(out.str());
        // Update the window
        _confirmWindow->Update();
        // Show it
        OpenPopUp(mcsTRUE);
        _theGui->SetStatus(true,"");
    }
    // if not
    else
    {
        // Call in all other case Overwrite method
        if (Overwrite(fileName, mcsTRUE) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    return mcsSUCCESS;
}

/**
 * User callback associated to the Export button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::ExportPanelCB(void *)
{
    logTrace("sclguiDISPLAY::ExportPanelCB()");

    // Get the fileName enter by the user
    mcsSTRING32 fileName;
    strcpy(fileName, (_exportTextfield->GetText()).c_str());
    
    // Check if the file already exist
    if (miscFileExists(fileName, mcsFALSE) == mcsTRUE)
    {
        // if file already exist, write name in the label
        ostringstream out;
        out << fileName;
        out << " already exists. Do you want to overwrite it?";
        _confirmLabel->SetText(out.str());
        // Update the window
        _confirmWindow->Update();
        // Show it
        OpenPopUp(mcsFALSE);
        _theGui->SetStatus(true,"");
    }
    // if not
    else
    {
        // Call in all other case Overwrite method
        if (Overwrite(fileName, mcsFALSE) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}


/**
 *  User callback associated to the Visibility button.
 *  
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::VisibilityButtonCB(void *)
{
    logTrace("sclguiDISPLAY::VisibilityButtonCB()");

    // Hide visibility filter pop_up
    _visibilityFilterView->Hide(); 
    
    // Get the user maximum accuracy
    mcsFLOAT visMax;
    sscanf((_visibilityFilterView->GetVisFilterValue()).c_str(), "%f", &visMax);
    
    // Enable visibility filter with the user entry
    _model.EnableFilterVisibility(visMax);
    
    // Update main window
     _mainWindow->Update();
    
    return mcsSUCCESS;
}

/**
 *  User callback associated to the magnitude button.
 *
 *   @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::MagnitudeButtonCB(void *)
{
    logTrace("sclguiDISPLAY::MagnitudeButtonCB()");

    // hide magnitude window
    _magnitudeFilterView->Hide(); 
    
    // Get request
    sclsvrREQUEST *request = _model.GetRequest();
    // Get band of the request
    mcsSTRING32 band;
    strcpy(band, request->GetSearchBand());
    
    // get magnitude of the request
    mcsFLOAT magnitude;
    magnitude = request->GetObjectMag();
    
    // get usr magitude range
    mcsFLOAT magRange;
    sscanf((_magnitudeFilterView->GetMagRange()).c_str(), "%f", &magRange);
    
    // Enable magnitude filter with the previous parameters
    _model.EnableFilterMagnitude(band, magnitude, magRange);
    
    // update window
     _mainWindow->Update();
    
    return mcsSUCCESS;
}

/**
 *  User callback associated to the distance button.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::DistanceButtonCB(void *)
{
    logTrace("sclguiDISPLAY::DistanceButtonCB()");

    // hide distance window    
    _distanceFilterView->Hide(); 
   
    mcsSTRING32 raRef;
    mcsSTRING32 decRef;
    // Get request
    sclsvrREQUEST *request = _model.GetRequest();
    // Get science object coordinates
    strcpy(raRef, request->GetObjectRa());
    strcpy(decRef, request->GetObjectDec());
    
    mcsFLOAT raRange;
    mcsFLOAT decRange;
    // Get user range
    sscanf((_distanceFilterView->GetRaRange()).c_str(), "%f", &raRange);
    sscanf((_distanceFilterView->GetDecRange()).c_str(), "%f", &decRange);
    
    // Enable distance filter with the previous parameters
    _model.EnableFilterDistance(raRef, decRef, raRange, decRange);     
    
    // update window
    _mainWindow->Update();
    
    return mcsSUCCESS;
}

/**
 * User callback associated to the luminosity button.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::LuminosityButtonCB(void *)
{
    logTrace("sclguiDISPLAY::LuminosityButtonCB()");

    _luminosityFilterView->Hide(); 
    
    // Get user wanted luminosity
    std::list<char *> lumClass;
    _luminosityFilterView->GetLuminosityClass(&lumClass);
    // Enable luminosity filter with the previous parameters
    _model.EnableFilterLuminosity(lumClass);
    
    // update window
     _mainWindow->Update();
    
    return mcsSUCCESS;
}

/**
 * User callback associated to the spectral type button.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::SpectralTypeButtonCB(void *)
{
    logTrace("sclguiDISPLAY::SpectralTypeButtonCB()");

    _spTypeFilterView->Hide(); 
    
    // Get user wanted luminosity
    std::list<char *> tempClass;
    _spTypeFilterView->GetTemperatureClass(&tempClass);
    // Enable luminosity filter with the previous parameters
    _model.EnableFilterSpectralType(tempClass);
    
    // update window
     _mainWindow->Update();
    
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Variability button.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::VariabilityButtonCB(void *)
{
    logTrace("sclguiDISPLAY::VariabilityButtonCB()");

    // Hide variability filter pop up
    _variabilityFilterView->Hide(); 

    // Get the user choice about variability
    if (_variabilityFilterView->GetChoice() == "Forbidden")
    {
        // if user don't want variability, enable it
        _model.EnableFilterVariability();
    }
    else
    {
        // if user wants variability, disable it
        _model.DisableFilter(vobsVARIABILITY_FILTER_NAME);
    }

    // Update main window
     _mainWindow->Update();
    
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Multiplicity button.
 *  
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::MultiplicityButtonCB(void *)
{
    logTrace("sclguiDISPLAY::MultiplicityButtonCB()");

    // Hide multiplicity filter pop up
    _multiplicityFilterView->Hide(); 
    
    // Get the user choice about multiplicity
    if (_multiplicityFilterView->GetChoice() == "Forbidden")
    {
        // if user don't want multiplicity, enable it
        _model.EnableFilterMultiplicity();
    }
    else
    {
        // if user wants multiplicity, disable it
        _model.DisableFilter(vobsMULTIPLICITY_FILTER_NAME);
    }

    // Update main window    
     _mainWindow->Update();
     
    return mcsSUCCESS;
}

/**
 *  User callback associated to the SaveOverWrite button.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::SaveOverwriteButtonCB(void *)
{
    logTrace("sclguiDISPLAY::OverwriteButtonCB()");

    // Get the fileName enter by the user
    mcsSTRING32 fileName;
    strcpy(fileName, (_saveTextfield->GetText()).c_str());

    // Call overwrite method with previous name
    if (Overwrite(fileName, mcsTRUE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Close confirm window    
    _confirmWindow->Hide();
     
    return mcsSUCCESS;
}

/**
 *  User callback associated to the ExportOverWrite button.
 *
 *   @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::ExportOverwriteButtonCB(void *)
{
    logTrace("sclguiDISPLAY::OverwriteButtonCB()");

    // Get the fileName enter by the user
    mcsSTRING32 fileName;
    strcpy(fileName, (_exportTextfield->GetText()).c_str());

    // Call overwrite method with previous name
    if (Overwrite(fileName, mcsFALSE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Close confirm window    
    _confirmWindow->Hide();
     
    return mcsSUCCESS;
}

/**
 * Overwrite method
 *
 * @param fileName the file name to overwrite
 * @param saveFlag logical
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::Overwrite(mcsSTRING32 fileName,
                                       mcsLOGICAL saveFlag)
{
    logTrace("sclguiDISPLAY::Overwrite()");

    // Save the display list in the specified file
    mcsSTRING64 usrMsg;
    // Get the list
    sclsvrCALIBRATOR_LIST * list = _model.GetCalibratorList();
    // Get the request
    sclsvrREQUEST * request = _model.GetRequest();
    vobsSTAR_PROPERTY_ID_LIST label;    
    // Get the actual label
    // if it is for a save file
    if (saveFlag == mcsTRUE)
    {
        // Get label list
        label = _tableView->GetLabel(mcsTRUE);
        // Add in the label list the "diameter ok flag"
        label.push_back(sclsvrCALIBRATOR_DIAM_FLAG);
    }
    // alse if it is for an export file
    else
    {
        // Just Get the actual label
        label = _tableView->GetLabel(_tableView->IsDetailsView());
    }
        
    // Call Save method of the list with the parameter specific for a save or an
    // export
    if (list->Save(fileName, label, *request, saveFlag) ==
        mcsFAILURE)
    {
        // if save failed, send user message and close the popup
        sprintf(usrMsg, "'%s' file HAS NOT been created", fileName);
        _theGui->SetStatus(false, usrMsg, errUserGet());
        errCloseStack();
        return mcsFAILURE;
    }
    // if failed succeed, send user message and close the popup
    sprintf(usrMsg, "'%s' file has been created", fileName);
    _theGui->SetStatus(true, usrMsg);

    return mcsSUCCESS;
}

/**
 * Open the confirm pop up
 *
 * @param saveFlag flag to know if the CB re-associated in this method should
 * be with the CB for save or export
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::OpenPopUp(mcsLOGICAL saveFlag)
{
    logTrace("sclguiDISPLAY::OpenPopUp()");

    // Attach CB according to the wanted save file (.scl or .txt)
    // if saveFlag is true, i.e for save file
    if (saveFlag == mcsTRUE)
    {
        // attach CB on SaveOverwriteButtonCB method
        _overwriteButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::SaveOverwriteButtonCB);
    }
    // else if saveFlag is false, i.e for export file
    else
    {
        // attach CB on ExportOverwriteButtonCB method        
        _overwriteButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::ExportOverwriteButtonCB);
    }

    // Show confirm window
    _confirmWindow->Show();

    return mcsSUCCESS;
}

/** 
 *  Build the main window.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::BuildMainWindow()
{
    logTrace("sclguiDISPLAY::BuildMainWindow()");

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

    // Prepare button
    BuildButtons();
   
    // Build model views
    BuildModelView();
    
    // Prepare subpanels
    BuildFilterInterface();
        
    // Build In/Out file management interface
    BuildInOutFileInterface();

    // Add buttons in the main window
    _mainWindow->Add(_showAllResultsButton);
    _mainWindow->Add(_resetButton);
    _mainWindow->Add(_showDetailsButton);
    _mainWindow->Add(_hideDetailsButton);

    // Associate the close button cb
    _mainWindow->SetCloseCommand("Close SearchCalib");
    _mainWindow->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::AbortButtonCB);
    // Associate callbacks
    _showAllResultsButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::ShowAllResultsButtonCB);
    _resetButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::ResetButtonCB);
    _showDetailsButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::ShowDetailsCB);
    _hideDetailsButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::HideDetailsCB);
    _filterPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::SelectPanelCB);

    // Get windows associated to the filters pop up window
    gwtWINDOW *visWindow = _visibilityFilterView->GetWindowLink();
    gwtWINDOW *magWindow = _magnitudeFilterView->GetWindowLink();
    gwtWINDOW *distWindow = _distanceFilterView->GetWindowLink();
    gwtWINDOW *lumWindow = _luminosityFilterView->GetWindowLink();
    gwtWINDOW *spTypeWindow = _spTypeFilterView->GetWindowLink();
    gwtWINDOW *varWindow = _variabilityFilterView->GetWindowLink();
    gwtWINDOW *multWindow = _multiplicityFilterView->GetWindowLink();

    // Attach all this window to the GUI
    visWindow->AttachAGui(_theGui);
    magWindow->AttachAGui(_theGui);
    distWindow->AttachAGui(_theGui);
    lumWindow->AttachAGui(_theGui);
    spTypeWindow->AttachAGui(_theGui);
    varWindow->AttachAGui(_theGui);
    multWindow->AttachAGui(_theGui);
    
    // Get confirm button of visibility filter
    gwtBUTTON *applyVisFilterButton =
        _visibilityFilterView->GetApplyButtonLink();
    // Attach this button to VisibilityButtonCB method
    applyVisFilterButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::VisibilityButtonCB);
    
    // Get confirm button of magnitude filter
    gwtBUTTON *applyMagFilterButton =
        _magnitudeFilterView->GetApplyButtonLink();
    // Attach this button to MagnitudeButtonCB method
    applyMagFilterButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::MagnitudeButtonCB);
    
    // Get confirm button of distance filter
    gwtBUTTON *applyDistFilterButton =
        _distanceFilterView->GetApplyButtonLink();
    // Attach this button to DistanceButtonCB method
    applyDistFilterButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::DistanceButtonCB);
    
    // Get confirm button of luminosity class filter
    gwtBUTTON *applyLumFilterButton =
        _luminosityFilterView->GetApplyButtonLink();
    // Attach this button to LuminosityButtonCB method
    applyLumFilterButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::LuminosityButtonCB);
    
    // Get confirm button of temperature class filter
    gwtBUTTON *applySpTypeFilterButton =
        _spTypeFilterView->GetApplyButtonLink();
    // Attach this button to SpectralTypeButtonCB method
    applySpTypeFilterButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::SpectralTypeButtonCB);
    
    // Get confirm button of variability filter
    gwtBUTTON *applyVarFilterButton =
        _variabilityFilterView->GetApplyButtonLink();
    // Attach this button to VariabilityButtonCB method
    applyVarFilterButton->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::VariabilityButtonCB);
    
    // Get confirm button of multiplicity filter
    gwtBUTTON *applyMultFilterWindow =
        _multiplicityFilterView->GetApplyButtonLink();
    // Attach this button to MultiplicityButtonCB method
    applyMultFilterWindow->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::MultiplicityButtonCB);
    
    // Attach delete CB
    _deletePanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::DeletePanelCB);
    // Attach in out CB (save, load, export)
    _loadPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::LoadPanelCB);
    _savePanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::SavePanelCB);
    _exportPanel->AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiDISPLAY::ExportPanelCB);
    
    return mcsSUCCESS;
}

/**
 * Build main button reset,...
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::BuildButtons()
{
    logTrace("sclguiDISPLAY::BuildButtons()");

    // Prepare button
    // Created "SHOW ALL RESULTS" button
    _showAllResultsButton = new gwtBUTTON
        ("SHOW ALL RESULTS", "Display of all results with coherent diameter.");
    // Place at top "SHOW ALL RESULTS" button
    _showAllResultsButton->PlaceAtTop(mcsTRUE);
    // Created "RESET" button
    _resetButton = new gwtBUTTON("RESET", "This button reset the star list. It will show the list with visibility OK.");
    // Place at top "RESET" button
    _resetButton->PlaceAtTop(mcsTRUE);
    // Created "SHOW DETAILS" button
    _showDetailsButton = new gwtBUTTON
        ("SHOW DETAILS", "Show all properties of a star");
    // Place at top "SHOW DETAILS" button
    _showDetailsButton->PlaceAtTop(mcsTRUE);
    // Created "HIDE DETAILS" button 
    _hideDetailsButton = new gwtBUTTON
        ("HIDE DETAILS", "Show only some specific properties");
    // Place at top "HIDE DETAILS" button
    _hideDetailsButton->PlaceAtTop(mcsTRUE);
    
    return mcsSUCCESS;
}

/**
 * Build model views
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiDISPLAY::BuildModelView()
{
    logTrace("sclguiDISPLAY::BuildModelView()");

    // Get The request view
    _scienceStarTextArea = _requestView->GetRequestTextArea();
    _mainWindow->Add(_scienceStarTextArea);    
   
    // Get the resume view
    _resumeTextArea = _resumeView->GetResumeTextArea();
    _mainWindow->Add(_resumeTextArea);

    // Get the list table view, color and conf idx
    _listTable = _tableView->GetListTable();
    _legendTable = _tableView->GetColorTable();
    _condIdxTable = _tableView->GetConfIdxTable();

    // Add views int he main window
    _mainWindow->Add(_listTable);
    _mainWindow->Add(_legendTable);
    _mainWindow->Add(_condIdxTable);

    return mcsSUCCESS;
}

/**
 * Build Filter Interface
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiDISPLAY::BuildFilterInterface()
{
    logTrace("sclguiDISPLAY::BuildFilterInterface()");
    
    // Creat subpanel
    _filterPanel = new gwtSUBPANEL("SELECT CALIBRATORS");
    _filterPanel->SetLabel("Sort above list...");
    _filterPanel->SetHelp
        ("Select a sorting criterium in the list below, click on SORT "
         "CALIBRATORS. A popup panel will open, allowing to enter adequate "
         "sorting parameters.");
    // Create filter choice
    _filterChoice = new gwtCHOICE();
    _filterChoice->SetLabel("Selection parameters");
    _filterChoice->Add("Maximal distance in RA and DEC");
    _filterChoice->Add("Maximal Magnitude Separation (mag)");
    _filterChoice->Add("Spectral Type");
    _filterChoice->Add("Luminosity Class");
    _filterChoice->Add
        ("Maximal expected accuracy on the calibrator squared visibility (%)");
    _filterChoice->Add("Variability");
    _filterChoice->Add("Multiplicity");
    // Add filter choice in the panel
    _filterPanel->Add(_filterChoice);

    // Add global subpanel in the main window
    _mainWindow->AddContainer(_filterPanel);

    return mcsSUCCESS;
}

/**
 * Build the in out file management interface
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiDISPLAY::BuildInOutFileInterface()
{
    logTrace("sclguiDISPLAY::BuildInOutFileInterface()");
    
    // Create subpanel reset
    _deletePanel = new gwtSUBPANEL("DELETE");
    _deletePanel->SetLabel("Delete Star");
    _deletePanel->SetHelp("Deleting of star");
    // Create textfield associated to reset subpanel
    _deleteTextfield = new gwtTEXTFIELD("1","Line number to delete");
    _deleteTextfield->SetLabel("Star number");
    // Add textfield in the subpanel
    _deletePanel->Add(_deleteTextfield);

    // Create subpanel load
    _loadPanel = new gwtSUBPANEL("LOAD");
    _loadPanel->SetLabel("Load File");
    _loadPanel->SetHelp("Loading of star");
    // Create textfield associated to the load subpanel
    _loadTextfield = new gwtTEXTFIELD("*.scl", "File Name to load");
    _loadTextfield->SetLabel("File Name to load");
    // Add textfield in the subpanel
    _loadPanel->Add(_loadTextfield);

    // Get request
    sclsvrREQUEST *request = _model.GetRequest();
    
    // Create subpanel save
    _savePanel = new gwtSUBPANEL("SAVE");
    _savePanel->SetLabel("Save File");
    _savePanel->SetHelp("Save file in directory /Resultats/");
    // Create default save file name
    mcsSTRING256 fileName;
    strcpy(fileName, request->GetObjectName());
    strcat(fileName, "_");
    strcat(fileName, request->GetSearchBand());
    strcat(fileName, ".scl");
    // Create textfield associated to the save subpanel
    _saveTextfield = new gwtTEXTFIELD
        (fileName, "File name to save current list of calibrators");
    _saveTextfield->SetLabel("File Name to save");
    // Add textfield in the subpanel    
    _savePanel->Add(_saveTextfield);

    // Create subpanel export
    _exportPanel = new gwtSUBPANEL("EXPORT");
    _exportPanel->SetLabel("Export File");
    _exportPanel->SetHelp("Export file in directory /Resultats/");
    // Create default export file name
    mcsSTRING256 exportFileName;
    strcpy(exportFileName, request->GetObjectName());
    strcat(exportFileName, "_");
    strcat(exportFileName, request->GetSearchBand());
    strcat(exportFileName, ".txt");
    // Create textfield associated to the export subpanel
    _exportTextfield = new gwtTEXTFIELD
        (exportFileName, "File name to export current list of calibrators");
    _exportTextfield->SetLabel("File Name to export");
    // Add textfield in the subpanel
    _exportPanel->Add(_exportTextfield);

    // Add the four subpanel in the main window
    _mainWindow->AddContainer(_deletePanel); 
    _mainWindow->AddContainer(_loadPanel); 
    _mainWindow->AddContainer(_savePanel); 
    _mainWindow->AddContainer(_exportPanel);
    
    return mcsSUCCESS;    
}

/**
 * Build confirm window
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiDISPLAY::BuildConfirmWindow()
{
    logTrace("sclguiDISPLAY::BuildConfirmWindow()");

    // Create new window
    _confirmWindow = new gwtWINDOW();
    // Attach the confirm window to the gui
    _confirmWindow->AttachAGui(_theGui);

    // Add empty label
    _confirmLabel = new gwtLABEL("", "No Help");
    _confirmWindow->Add(_confirmLabel);

    // Add confirm button
    _overwriteButton = new gwtBUTTON("Overwrite");
    _overwriteButton->PlaceAtTop(mcsTRUE);
    _confirmWindow->Add(_overwriteButton);
    
    return mcsSUCCESS;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
