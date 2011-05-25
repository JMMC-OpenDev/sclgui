/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiCONTROLLER class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiCONTROLLER.cpp,v 1.20 2006-10-10 15:50:41 lafrasse Exp $"; 

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
#include "sclguiCONTROLLER.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

/*
 * Class constructor
 *
 * @param hostname The host name of the remote MCS XMLBasedGUI server.
 * @param port The port of the remote MCS XMLBasedGUI server.
 */
sclguiCONTROLLER::sclguiCONTROLLER(): 
_filterListModel("Filter List of the GUI"),
    _sclServer("Search-calibrator server", "sclsvrServer", 600000)
{
    logTrace("sclguiCONTROLLER::sclguiCONTROLLER");
}

/**
 * Class destructor
 */
sclguiCONTROLLER::~sclguiCONTROLLER()
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
mcsCOMPL_STAT sclguiCONTROLLER::AppInit()
{
    logTrace("sclguiCONTROLLER::AppInit()");

    // Add callback for GETCAL command
    evhCMD_KEY cmdKey(sclsvrGETCAL_CMD_NAME);
    evhCMD_CALLBACK cmdCB(this, (evhCMD_CB_METHOD)&sclguiCONTROLLER::GetCalCB);
    AddCallback(cmdKey, cmdCB);

    // Build general GUI
    if (BuildGUI() == mcsFAILURE)
    {
        return mcsSUCCESS;
    }

    // Show the main window in order to be able to load without research a save
    // file
    //_mainWindow.Show();
    
    return mcsSUCCESS;
}

/**
 * Return the version number of the software.
 *
 * @return version number of the software as a null-terminated char array
 */
const char* sclguiCONTROLLER::GetSwVersion()
{
    return sclsvrVERSION;
}

/*
 * Protected methods
 */

/*
 * Private methods
 */
/**
 * Build general GUI
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::BuildGUI()
{
    logTrace("sclguiCONTROLLER::BuildGUI()");

    // Build all the necessary windows of the Graphical User Interface
    if (BuildMainWindow() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    if (BuildFilterWindows() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    if (BuildConfirmPopUp() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Add view(s) to each model
    _requestModel.AddView(&_requestSubPanel);
    _requestModel.AddView(&_actionsSubPanel);
    _requestModel.AddView(&_calibratorListModelSubPanel);

    _calibratorListModel.AddView(&_calibratorListModelSubPanel);    

    _filterListModel.AddView(&_calibratorListModelSubPanel);
    _filterListModel.AddView(&_visibilityFilterSubPanel);
    _filterListModel.AddView(&_magnitudeFilterSubPanel);
    _filterListModel.AddView(&_distanceFilterSubPanel);
    _filterListModel.AddView(&_luminosityFilterSubPanel);
    _filterListModel.AddView(&_spectralTypeFilterSubPanel);
    _filterListModel.AddView(&_variabilityFilterSubPanel);
    _filterListModel.AddView(&_multiplicityFilterSubPanel);

    // Attach filter list model to the calibrator list model
    _calibratorListModel.SetFilterList(&_filterListModel);
    return mcsSUCCESS;
}

/**
 * Build main window
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::BuildMainWindow()
{
    logTrace("sclguiCONTROLLER::BuildMainWindow()");

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
    _mainWindow.AttachAGui(this);
    _mainWindow.SetTitle("JMMC Calibrator Group " sclsvrVERSION);
    _mainWindow.SetHelp(windowHelp);

    // Associate the close button event to the corresponding callback
    _mainWindow.SetCloseCommand("Close SearchCalib");
    _mainWindow.AttachCB
        (this, (gwtCOMMAND::CB_METHOD) &sclguiCONTROLLER::AbortButtonCB);

    // Create container of button Reset details, ...
    // Set callback of this container
    _buttonsSubPanel.SetResetCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::ResetButtonCB);
    _buttonsSubPanel.SetShowAllResultsCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::ShowAllResultsButtonCB);
    _buttonsSubPanel.SetShowDetailsCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::ShowDetailsButtonCB);
    _buttonsSubPanel.SetHideDetailsCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::HideDetailsButtonCB);

    // Add this container in the windows
    _mainWindow.Add(&_buttonsSubPanel);

    // Create container of models view
    _requestSubPanel.AttachModel(_requestModel);
    _calibratorListModelSubPanel.AttachModel(_calibratorListModel,
                                             _requestModel);
    // Add this container in the windows
    _mainWindow.Add(&_requestSubPanel);
    _mainWindow.Add(&_calibratorListModelSubPanel);
    
    // create filter choice subpanel container
    _actionsSubPanel.AttachModel(_requestModel);
    _actionsSubPanel.SetFilterButtonCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::SelectButtonCB);
    _actionsSubPanel.SetDeleteButtonCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::DeleteButtonCB);
    _actionsSubPanel.SetLoadButtonCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::LoadButtonCB);
    _actionsSubPanel.SetSaveButtonCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::SaveButtonCB);
    _actionsSubPanel.SetCSVExportCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::CSVExportButtonCB);
    _actionsSubPanel.SetVOTExportCB(*this, (gwtCOMMAND::CB_METHOD)
                        &sclguiCONTROLLER::VOTExportButtonCB);

    // Add this container in the windows
    _mainWindow.Add(&_actionsSubPanel);    

    return mcsSUCCESS;
}

/**
 * Build filters windows
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::BuildFilterWindows()
{
    logTrace("sclguiCONTROLLER::BuildFilterWindows()");

    // Build all the view of each filter, and attach associated callback
    _visibilityFilterSubPanel.AttachModel(_filterListModel);
    _visibilityFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                                 &sclguiCONTROLLER::VisibilityButtonCB);

    _magnitudeFilterSubPanel.AttachModel(_filterListModel);
    _magnitudeFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                                &sclguiCONTROLLER::MagnitudeButtonCB);

    _distanceFilterSubPanel.AttachModel(_filterListModel);
    _distanceFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                               &sclguiCONTROLLER::DistanceButtonCB);

    _luminosityFilterSubPanel.AttachModel(_filterListModel);
    _luminosityFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                                 &sclguiCONTROLLER::LuminosityButtonCB);

    _spectralTypeFilterSubPanel.AttachModel(_filterListModel);
    _spectralTypeFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                                   &sclguiCONTROLLER::SpectralTypeButtonCB);

    _variabilityFilterSubPanel.AttachModel(_filterListModel);
    _variabilityFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                                  &sclguiCONTROLLER::VariabilityButtonCB);

    _multiplicityFilterSubPanel.AttachModel(_filterListModel);
    _multiplicityFilterSubPanel.SetApplyCB(*this, (gwtCOMMAND::CB_METHOD)
                                   &sclguiCONTROLLER::MultiplicityButtonCB);
    
    // Create all filter window and attached them to the GUI
    _visibilityFilterWindow.AttachAGui(this);
    _magnitudeFilterWindow.AttachAGui(this);
    _distanceFilterWindow.AttachAGui(this);
    _luminosityFilterWindow.AttachAGui(this);
    _spectralTypeFilterWindow.AttachAGui(this);
    _variabilityFilterWindow.AttachAGui(this);
    _multiplicityFilterWindow.AttachAGui(this);
    
    // Attach view in their window
    _visibilityFilterWindow.Add(&_visibilityFilterSubPanel); 
    _magnitudeFilterWindow.Add(&_magnitudeFilterSubPanel); 
    _distanceFilterWindow.Add(&_distanceFilterSubPanel); 
    _luminosityFilterWindow.Add(&_luminosityFilterSubPanel); 
    _spectralTypeFilterWindow.Add(&_spectralTypeFilterSubPanel); 
    _variabilityFilterWindow.Add(&_variabilityFilterSubPanel); 
    _multiplicityFilterWindow.Add(&_multiplicityFilterSubPanel); 
    
    return mcsSUCCESS;
}

/**
 * Build Main Window
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::BuildConfirmPopUp()
{
    logTrace("sclguiCONTROLLER::BuildConfirmPopUp()");

    // Create window
    // Add the confirm container in the confirm windows
    _confirmWindow.Add(&_confirmSupPanel);
    // Attach the confirm window to the gui
    _confirmWindow.AttachAGui(this);
    
    return mcsSUCCESS;
}

/**
 * Callback associated to abort button and window's close button
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiCONTROLLER::AbortButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::AbortButtonCB()");
    SetStatus(true, "Search Calibrators results panel closed");

    _mainWindow.Hide();
    exit(EXIT_SUCCESS);
    return mcsSUCCESS;
}

/**
 * Callback associated to ShowAllResult button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned. 
 */
mcsCOMPL_STAT sclguiCONTROLLER::ShowAllResultsButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::ShowAllResultsButtonCB()");
    
    // Reset all filter
    if (_filterListModel.ResetFilters() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    _calibratorListModel.ClearDeletedCalibratorList();
    
    // Disable filter by variability and multipicity because the default state
    // of the model enabled mult and var
    if (_filterListModel.DisableFilter("Variability Filter") == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    if (_filterListModel.DisableFilter("Multiplicity Filter") == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Update main window
    _mainWindow.Update();
    
    // Send message to the XMLBasedGUI
    SetStatus(true, "Show all stars with coherent diameter");    

    return mcsSUCCESS;
}

/**
 * Callback associated to Reset button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::ResetButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::ResetButtonCB()");
   
    // Reset all filter
    if (_filterListModel.ResetFilters() == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Reset list of unwanted calibrators
    _calibratorListModel.ClearDeletedCalibratorList();
    
    // Update main window
    _mainWindow.Update();
    
    // Send message to the XMLBasedGUI server    
    SetStatus(true, "Show all stars with coherent diameter and " 
                       "without variability and multiplicity");
    
    return mcsSUCCESS;
}

/**
 * Callback associated to ShowDetails button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::ShowDetailsButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::ShowDetailsButtonCB()");
  
    // Say to the table view that details are OK
    if (_calibratorListModelSubPanel.Detail(mcsTRUE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Update main window
    _mainWindow.Update();
    
    // Send message to the XMLBasedGUI    
    SetStatus(true, "Show all details");
     
    return mcsSUCCESS;
}

/**
 * Callback associated to HideDetails button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::HideDetailsButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::HideDetailsButtonCB()");
  
    // Say to the table view that details are NOK
    if (_calibratorListModelSubPanel.Detail(mcsFALSE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Update main window
     _mainWindow.Update();
    
    // Send message to the XMLBasedGUI    
     SetStatus(true, "Show less details");
     
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Select Panel.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::SelectButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::SelectButtonCB()");

    // According to the user choice, show the specific pop up window
    switch(_actionsSubPanel.GetFilterChoice())
    {
        case sclguiDISTANCE_FILTER_NUMBER:
        {
            // Show distance filter pop up
            _distanceFilterWindow.Show();
            SetStatus(true,"Distance filter panel open");
            break;
        }

        case sclguiMAGNITUDE_FILTER_NUMBER:
        {
            // Show magnitude filter pop up
            _magnitudeFilterWindow.Show();
            SetStatus(true,"Magnitude filter panel open");
            break;
        }

        case sclguiSP_TYPE_FILTER_NUMBER:
        {
            // Show temperature class filter pop up
            _spectralTypeFilterWindow.Show();
            SetStatus(true,"Temperature class filter panel open");
            break;
        }

        case sclguiLUMINOSITY_FILTER_NUMBER:
        {
            // Show luminosity class filter pop up
            _luminosityFilterWindow.Show();
            SetStatus(true,"Luminosity class filter panel open");
            break;
        }

        case sclguiVISIBILITY_FILTER_NUMBER:
        {
            // Show visibility filter pop_up
            _visibilityFilterWindow.Show();
            SetStatus(true,"Visibility filter panel open");
            break;
        }

        case sclguiVARIABILITY_FILTER_NUMBER:
        {
            // show variability filter pop up
            _variabilityFilterWindow.Show();
            SetStatus(true,"Variability filter panel open");
            break;
        }

        case sclguiMULTIPLICITY_FILTER_NUMBER:
        {
            // Show multiplicity filter pop up
            _multiplicityFilterWindow.Show();
            SetStatus(true,"Multiplicity filter panel open");
            break;
        }
    }
    
    return mcsSUCCESS;
}

/**
 * User callback associated to the Delete button
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::DeleteButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::DeleteButtonCB()");

    // Get the calibrator number to delete
    mcsUINT32 calibratorNumber;
    // Status message buffer
    mcsSTRING64 statusMessage;
    // Buffer used with sscanf to test if a value is an integer or not
    mcsSTRING32 buffer;

    // If the number is not an integer, inform the user
    if (sscanf((_actionsSubPanel.GetDeletedStarNumber()).c_str(), "%d%s",
               &calibratorNumber, buffer) != 1)
    {
        SetStatus(false, "Invalid star number", "Value should be an integer");
        return mcsFAILURE;
    }

    // If the number of star in the filtered list is not positive or lower
    // than zero, inform the user
    mcsINT32 nbCalibrators;
    nbCalibrators = 
        _calibratorListModel.GetNbCalibrators(sclguiFILTERED_CALIBRATORS);
    if (nbCalibrators == 0)
    {
        SetStatus(false, "Invalid star number",
                  "Can't delete, no star in the list");
        return mcsFAILURE;
    }
    else if ((calibratorNumber > (mcsUINT32)nbCalibrators) ||
             (calibratorNumber <= 0))
    {
        sprintf(statusMessage,
                "Value should be a positive integer lower than %d",
                nbCalibrators);
        SetStatus(false, "Invalid star number", statusMessage); 
        return mcsFAILURE;
    }
    
    // Delete the unwanted calibrator star in the model
    mcsSTRING32 starId;
    if (_calibratorListModel.DeleteCalibrator(calibratorNumber,
                                              starId) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Display a status message containing the deleted star name
    ostringstream statusMsg;
    statusMsg << "Star number " << calibratorNumber << " (" 
        << starId << ") has been deleted";
    SetStatus(true, statusMsg.str());
    
    // Update main window
    _mainWindow.Update();

    return mcsSUCCESS;
}

/**
 * User callback associated to the Load button
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::LoadButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::LoadButtonCB()");

    // Status message buffer
    mcsSTRING64 statusMessage;
   
    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strcpy(fileName, (_actionsSubPanel.GetLoadFileName()).c_str());
    
    // Load list and request
    if (_calibratorListModel.LoadFromFile(fileName, _requestModel) ==mcsFAILURE)
    {
        sprintf(statusMessage, "'%s' file HAS NOT been loaded", fileName);
        SetStatus(false, statusMessage, errUserGet());
        errCloseStack();
        return mcsFAILURE;
    }
    
    // Display a status message containing the load file name
    sprintf(statusMessage, "'%s' file has been loaded", fileName);
    SetStatus(true, statusMessage);

    // Update main window
    _mainWindow.Update();

    return mcsSUCCESS;
}

/**
 * User callback associated to the Save button
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::SaveButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::SaveButtonCB()");

    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strcpy(fileName, (_actionsSubPanel.GetSaveFileName()).c_str());
    
    // Check if the file already exist without error reporting
    if (miscFileExists(fileName, mcsFALSE) == mcsTRUE)
    {
        // If file already exist, write name in the label
        ostringstream out;
        out << fileName << " already exists. Do you want to overwrite it?";
        _confirmSupPanel.SetPopUpText(out.str());

        // Update the window
        _confirmWindow.Update();

        // Show it
        OpenPopUp(sclguiSAVE_OVERWRITE);
        SetStatus(true,"");
    }
    else
    {
        // Call in all other case Overwrite method
        if (Overwrite(fileName, sclguiSAVE_OVERWRITE) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}

/**
 * User callback associated to the CSV Export button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::CSVExportButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::CSVExportButtonCB()");

    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strncpy(fileName, (_actionsSubPanel.GetCSVExportFileName()).c_str(),
            sizeof(fileName));
    
    // Check if the file already exist
    if (miscFileExists(fileName, mcsFALSE) == mcsTRUE)
    {
        // If file already exist, write name in the label
        ostringstream out;
        out << fileName;
        out << " already exists. Do you want to overwrite it?";
        _confirmSupPanel.SetPopUpText(out.str());
        // Update the window
        _confirmWindow.Update();
        // Show it
        OpenPopUp(sclguiCSV_EXPORT_OVERWRITE);
        SetStatus(true,"");
    }
    else
    {
        // Call in all other case Overwrite method
        if (Overwrite(fileName, sclguiCSV_EXPORT_OVERWRITE) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}


/**
 * User callback associated to the VOT Export button
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::VOTExportButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::VOTExportButtonCB()");

    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strncpy(fileName, (_actionsSubPanel.GetVOTExportFileName()).c_str(),
            sizeof(fileName));
    
    // Check if the file already exist
    if (miscFileExists(fileName, mcsFALSE) == mcsTRUE)
    {
        // If file already exist, write name in the label
        ostringstream out;
        out << fileName;
        out << " already exists. Do you want to overwrite it?";
        _confirmSupPanel.SetPopUpText(out.str());
        // Update the window
        _confirmWindow.Update();
        // Show it
        OpenPopUp(sclguiVOT_EXPORT_OVERWRITE);
        SetStatus(true,"");
    }
    else
    {
        // Call in all other case Overwrite method
        if (Overwrite(fileName, sclguiVOT_EXPORT_OVERWRITE) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}


/**
 *  User callback associated to the SaveOverWrite button.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::SaveOverwriteButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::SaveOverwriteButtonCB()");

    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strcpy(fileName, (_actionsSubPanel.GetSaveFileName()).c_str());

    // Call overwrite method with previous name
    if (Overwrite(fileName, sclguiSAVE_OVERWRITE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Close confirm window    
    _confirmWindow.Hide();
     
    SetStatus(true, "Save succeed");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the CSVExportOverWrite button.
 *
 *   @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::CSVExportOverwriteButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::CSVExportOverwriteButtonCB()");

    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strncpy(fileName, _actionsSubPanel.GetCSVExportFileName().c_str(),
            sizeof(fileName));

    // Call overwrite method with previous name
    if (Overwrite(fileName, sclguiCSV_EXPORT_OVERWRITE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Close confirm window    
    _confirmWindow.Hide();
     
    SetStatus(true, "Export succeed");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the VOTExportOverWrite button.
 *
 *   @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::VOTExportOverwriteButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::VOTExportOverwriteButtonCB()");

    // Get the file name from the corresponding textfield
    mcsSTRING256 fileName;
    strncpy(fileName, _actionsSubPanel.GetVOTExportFileName().c_str(),
            sizeof(fileName));

    // Call overwrite method with previous name
    if (Overwrite(fileName, sclguiVOT_EXPORT_OVERWRITE) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Close confirm window    
    _confirmWindow.Hide();
     
    SetStatus(true, "Export succeed");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Visibility button.
 *  
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::VisibilityButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::VisibilityButtonCB()");

    // Hide visibility filter pop_up
    _visibilityFilterWindow.Hide(); 
    
    // Get the user maximum accuracy
    mcsFLOAT visMax;
    sscanf((_visibilityFilterSubPanel.GetVisFilterValue()).c_str(),
           "%f", &visMax);
    
    // Enable visibility filter with the user entry
    _filterListModel.SetFilterVisibility(visMax, mcsTRUE);
    
    // Update main window
     _mainWindow.Update();
    
    SetStatus(true, "Visibility filter apply");
    return mcsSUCCESS;
}

/**
 * User callback associated to the luminosity button.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::LuminosityButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::LuminosityButtonCB()");

    _luminosityFilterWindow.Hide(); 
    
    // Get user wanted luminosity
    std::list<char*> lumClass;
    _luminosityFilterSubPanel.GetLuminosityClass(&lumClass);
    // Enable luminosity filter with the previous parameters
    _filterListModel.SetFilterLuminosityClass(lumClass, mcsTRUE);
    
    // update window
     _mainWindow.Update();
    
    SetStatus(true, "Luminosity class filter apply");
    return mcsSUCCESS;
}

/**
 * User callback associated to the spectral type button.
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::SpectralTypeButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::SpectralTypeButtonCB()");

    _spectralTypeFilterWindow.Hide(); 
    
    // Get user wanted luminosity
    std::list<char*> tempClass;
    _spectralTypeFilterSubPanel.GetTemperatureClass(&tempClass);
    // Enable luminosity filter with the previous parameters
    _filterListModel.SetFilterTemperatureClass(tempClass, mcsTRUE);
    
    // update window
     _mainWindow.Update();
    
    SetStatus(true, "Temperature class filter apply");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the magnitude button.
 *
 *   @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::MagnitudeButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::MagnitudeButtonCB()");

    // Hide magnitude window
    _magnitudeFilterWindow.Hide(); 
    
    // Get band of the request
    mcsSTRING32 band;
    strcpy(band, _requestModel.GetSearchBand());
    
    // get magnitude of the request
    mcsFLOAT magnitude;
    magnitude = _requestModel.GetObjectMag();
    
    // get usr magitude range
    mcsFLOAT magRange;
    sscanf((_magnitudeFilterSubPanel.GetMagRange()).c_str(), "%f", &magRange);
    
    // Enable magnitude filter with the previous parameters
    _filterListModel.SetFilterMagnitude(band, magnitude, magRange, mcsTRUE);
    
    // update window
     _mainWindow.Update();
    
    SetStatus(true, "Magnitude filter apply");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Multiplicity button.
 *  
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::MultiplicityButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::MultiplicityButtonCB()");

    // Hide multiplicity filter pop up
    _multiplicityFilterWindow.Hide(); 
    
    // Get the user choice about multiplicity
    if (_multiplicityFilterSubPanel.GetChoice() == "Forbidden")
    {
        // if user don't want multiplicity, enable it
        _filterListModel.SetFilterMultiplicity(mcsTRUE);
    }
    else
    {
        // if user wants multiplicity, disable it
        _filterListModel.DisableFilter("Multiplicity Filter");
    }

    // Update main window    
     _mainWindow.Update();

    SetStatus(true, "Multiplicity filter apply");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the distance button.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::DistanceButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::DistanceButtonCB()");

    // Hide distance window    
    _distanceFilterWindow.Hide(); 
   
    mcsSTRING32 raRef;
    mcsSTRING32 decRef;
    // Get science object coordinates
    strcpy(raRef, _requestModel.GetObjectRa());
    strcpy(decRef, _requestModel.GetObjectDec());
    
    // Get user range
    mcsFLOAT raRange;
    mcsFLOAT decRange;
    sscanf((_distanceFilterSubPanel.GetRaRange()).c_str(), "%f", &raRange);
    sscanf((_distanceFilterSubPanel.GetDecRange()).c_str(), "%f", &decRange);
    
    // Enable distance filter with the previous parameters
    _filterListModel.SetFilterDistance(raRef, decRef, raRange, decRange, mcsTRUE);     
    
    // Update window
    _mainWindow.Update();
    
    SetStatus(true, "Distance filter apply");
    return mcsSUCCESS;
}

/**
 *  User callback associated to the Variability button.
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::VariabilityButtonCB(void*)
{
    logTrace("sclguiCONTROLLER::VariabilityButtonCB()");

    // Hide variability filter pop up
    _variabilityFilterWindow.Hide(); 

    // Get the user choice about variability
    if (_variabilityFilterSubPanel.GetChoice() == "Forbidden")
    {
        // if user don't want variability, enable it
        _filterListModel.SetFilterVariability(mcsTRUE);
    }
    else
    {
        // if user wants variability, disable it
        _filterListModel.DisableFilter("Variability Filter");
    }

    // Update main window
     _mainWindow.Update();
    
    SetStatus(true, "Variability filter apply");
    return mcsSUCCESS;
}

/**
 * Open the confirm pop up
 *
 * @param saveFlag flag to know if the callback re-associated in this method should
 * be with the callback for save or export
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::OpenPopUp(sclguiOVERWRITE_ACTION  action)
{
    logTrace("sclguiCONTROLLER::OpenPopUp()");

    // Attach callback according to the wanted save file (.scl or .txt)
    switch (action)
    {
        case sclguiSAVE_OVERWRITE :

            _confirmSupPanel.SetOverwriteCB(*this,
              (gwtCOMMAND::CB_METHOD) &sclguiCONTROLLER::SaveOverwriteButtonCB);

            break;


        case sclguiCSV_EXPORT_OVERWRITE :

            // Attach callback on ExportOverwriteButtonCB method        
            _confirmSupPanel.SetOverwriteCB(*this,
         (gwtCOMMAND::CB_METHOD) &sclguiCONTROLLER::CSVExportOverwriteButtonCB);

            break;


        case sclguiVOT_EXPORT_OVERWRITE :

            // Attach callback on ExportOverwriteButtonCB method        
            _confirmSupPanel.SetOverwriteCB(*this,
         (gwtCOMMAND::CB_METHOD) &sclguiCONTROLLER::VOTExportOverwriteButtonCB);

            break;


        default :
            return mcsFAILURE;
            break;
    }

    // Show confirm window
    _confirmWindow.Show();

    return mcsSUCCESS;
}

/**
 * Overwrite method to save the display list in the specified file
 *
 * @param fileName the file name to overwrite
 * @param action the action to be performed, amongst 'save', 'export to CSV',
 * and 'export to VOTable'
 *
 *  @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiCONTROLLER::Overwrite(mcsSTRING32             fileName,
                                          sclguiOVERWRITE_ACTION  action)
{
    logTrace("sclguiCONTROLLER::Overwrite()");

    // Save status
    mcsCOMPL_STAT status;

    // Actual label
    vobsSTAR_PROPERTY_ID_LIST label;

    // Get the calibrator list
    sclsvrCALIBRATOR_LIST* list =
        _calibratorListModel.GetList(sclguiFILTERED_CALIBRATORS);

    // Get the user request parameters
    mcsSTRING256 request;
    _requestModel.GetCmdParamLine(request);
        
    // According to the desired action (save in SCL, export to CSV, or VOTable)
    switch (action)
    {
        case sclguiSAVE_OVERWRITE :
        {
            list = _calibratorListModel.GetList(sclguiALL_CALIBRATORS);

            // Get list label
            label = _calibratorListModelSubPanel.GetLabel(mcsTRUE);
    
            // Add in the label list the "diameter ok flag"
            label.push_back(sclsvrCALIBRATOR_DIAM_FLAG);

            // Save the calibrator list in exended SCL format
            status = list->Save(fileName, label, _requestModel, mcsTRUE);

            break;
        }

        case sclguiCSV_EXPORT_OVERWRITE :
        {
            // Get actual label
            label = _calibratorListModelSubPanel.GetLabel(
                                     _calibratorListModelSubPanel.IsDetailed());

            // Add in the label list the "diameter ok flag"
            label.push_back(sclsvrCALIBRATOR_DIAM_FLAG);

            // Save the calibrator list in standard CSV format
            status = list->Save(fileName, label, _requestModel, mcsFALSE);

            break;
        }


        case sclguiVOT_EXPORT_OVERWRITE :
        {
            // Just Get the actual label
            label = _calibratorListModelSubPanel.GetLabel(
                                     _calibratorListModelSubPanel.IsDetailed());

            char * header = "SearchCal software: http://www.mariotti.fr/aspro_page.htm (In case of problem, please report to jmmc-user-support@ujf-grenoble.fr)";

            // Get the software name and version
            mcsSTRING32 software;
            snprintf(software, sizeof(software), "%s v%s", "SearchCal",
                     GetSwVersion());

            // Get theXML serialization of th request
            string xmlOutput;
            if (_requestModel.AppendParamsToVOTable(xmlOutput) == mcsFAILURE)
            {
                return mcsFAILURE;
            }

            // Save the list as a VOTable v1.1
            status = list->SaveToVOTable(fileName, header, software, request, xmlOutput.c_str());

            break;
        }

        default :
        {
            return mcsFAILURE;
        }
    }

    // Status message buffer
    mcsSTRING64 statusMessage;
    if (status == mcsSUCCESS)
    {
        // if everything succeed, send user message and close the popup
        snprintf(statusMessage, sizeof(statusMessage),
                 "'%s' file has been created", fileName);
        SetStatus(true, statusMessage, request);
        return mcsSUCCESS;
    }
    else
    {
        // if save failed, send user message and close the popup
        snprintf(statusMessage, sizeof(statusMessage),
                 "'%s' file HAS NOT been created", fileName);
        SetStatus(false, statusMessage, errUserGet());
        errCloseStack();
        return mcsFAILURE;
    }
}


/*___oOo___*/
