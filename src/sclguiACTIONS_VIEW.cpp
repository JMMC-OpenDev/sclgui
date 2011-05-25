/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiACTIONS_VIEW class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiACTIONS_VIEW.cpp,v 1.4 2006-03-03 15:28:17 scetre Exp $"; 

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
#include "sclguiACTIONS_VIEW.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiACTIONS_VIEW::sclguiACTIONS_VIEW()
{
    Add(&_filterPanel);
    Add(&_deletePanel);
    Add(&_loadPanel);
    Add(&_savePanel);
    Add(&_CSVExportPanel);
    Add(&_VOTExportPanel);
}

sclguiACTIONS_VIEW::sclguiACTIONS_VIEW(sclguiREQUEST_MODEL &requestModel)
{
    // Attach to the request model
    AttachModel(requestModel);
    
    Add(&_filterPanel);
    Add(&_deletePanel);
    Add(&_loadPanel);
    Add(&_savePanel);
    Add(&_CSVExportPanel);
    Add(&_VOTExportPanel);
}

/**
 * Class destructor
 */
sclguiACTIONS_VIEW::~sclguiACTIONS_VIEW()
{
}

/*
 * Public methods
 */
/**
 * Update window
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::Update()
{
    logTrace("sclguiACTIONS_VIEW::Update()");
    
    SetDefaultIOFileName();
    //BuildInOutFileInterface();    
       
    return mcsSUCCESS;
}


/**
 * Attach widget select filter to the approriate CB
 *
 * @param eventHandler event handler 
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetFilterButtonCB(fndOBJECT &eventHandler,
                                             gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiACTIONS_VIEW::SetFilterButtonCB");

    _filterPanel.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach widget delete panel to the approriate CB
 *
 * @param eventHandler event handler 
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetDeleteButtonCB(fndOBJECT &eventHandler,
                                             gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiACTIONS_VIEW::SetDeleteButtonCB()");

    _deletePanel.
        AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach widget load panel to the approriate CB
 *
 * @param eventHandler event handler 
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetLoadButtonCB(fndOBJECT &eventHandler,
                                             gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiACTIONS_VIEW::SetLoadButtonCB()");

    _loadPanel.AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach widget save panel to the approriate CB
 *
 * @param eventHandler event handler 
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetSaveButtonCB(fndOBJECT &eventHandler,
                                             gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiACTIONS_VIEW::SetSaveButtonCB()");

    _savePanel.AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach widget export SCL panel to the approriate CB
 *
 * @param eventHandler event handler 
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetCSVExportCB(fndOBJECT &eventHandler,
                                                 gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiACTIONS_VIEW::SetCSVExportCB()");

    _CSVExportPanel.AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Attach widget export VOT panel to the approriate CB
 *
 * @param eventHandler event handler 
 * @param cbMethod the associated CB
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetVOTExportCB(fndOBJECT &eventHandler,
                                                 gwtCOMMAND::CB_METHOD cbMethod)
{
    logTrace("sclguiACTIONS_VIEW::SetVOTExportCB()");

    _VOTExportPanel.AttachCB(&eventHandler, (gwtCOMMAND::CB_METHOD) cbMethod);

    return mcsSUCCESS;
}

/**
 * Get filter number choice
 */
mcsINT32 sclguiACTIONS_VIEW::GetFilterChoice(void)
{
    logTrace("sclguiACTIONS_VIEW::GetFilterChoice()");

    // return filter choice
    return _filterChoice.GetSelectedItem();
}

/**
 * Get deleted star number choice
 */
string sclguiACTIONS_VIEW::GetDeletedStarNumber(void)
{
    logTrace("sclguiACTIONS_VIEW::GetDeletedStarNumber()");

    // return deleted star number choice
    return _deleteTextfield.GetText();
}

/**
 * Get load file
 */
string sclguiACTIONS_VIEW::GetLoadFileName(void)
{
    logTrace("sclguiACTIONS_VIEW::GetLoadFileName()");

    // return deleted star number choice
    return _loadTextfield.GetText();
}

/**
 * Get save file
 */
string sclguiACTIONS_VIEW::GetSaveFileName(void)
{
    logTrace("sclguiACTIONS_VIEW::GetSaveFileName()");

    // return deleted star number choice
    return _saveTextfield.GetText();
}

/**
 * Get SCL export file name
 */
string sclguiACTIONS_VIEW::GetCSVExportFileName(void)
{
    logTrace("sclguiACTIONS_VIEW::GetSclExportFileName()");

    // return deleted star number choice
    return _CSVExportTextfield.GetText();
}

/**
 * Get VOT export file name
 */
string sclguiACTIONS_VIEW::GetVOTExportFileName(void)
{
    logTrace("sclguiACTIONS_VIEW::GetVotExportFileName()");

    // return deleted star number choice
    return _VOTExportTextfield.GetText();
}

/**
 * Attach model in the request view
 *
 * @ request the model to attach
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::AttachModel(sclguiREQUEST_MODEL &requestModel)
{
    logTrace("sclguiACTIONS_VIEW::AddRequestModel()");

     // Attach to the request model
    _requestModelModel = &requestModel;

    BuildFilterInterface();
    BuildInOutFileInterface();

    return mcsSUCCESS;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */
/**
 * Build Filter Interface
 *
 * @warning look order at the top of the file to know the order in which the
 * filter have to be inserted in the filter choice
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::BuildFilterInterface()
{
    logTrace("sclguiACTIONS_VIEW::BuildFilterInterface()");
    
    // Create subpanel
    _filterPanel.SetText("SELECT CALIBRATORS");
    _filterPanel.SetLabel("Sort above list...");
    _filterPanel.SetHelp
        ("Select a sorting criterium in the list below, click on SORT "
         "CALIBRATORS. A popup panel will open, allowing to enter adequate "
         "sorting parameters.");

    // Create filter choice
    _filterChoice.SetLabel("Selection parameters");
    _filterChoice.Add("Maximal distance in RA and DEC");
    _filterChoice.Add("Maximal Magnitude Separation (mag)");
    _filterChoice.Add("Spectral Type");
    _filterChoice.Add("Luminosity Class");
    _filterChoice.Add(
          "Maximal expected accuracy on the calibrator squared visibility (%)");
    _filterChoice.Add("Variability");
    _filterChoice.Add("Multiplicity");

    // Add filter choice in the panel
    _filterPanel.Add(&_filterChoice);

    return mcsSUCCESS;
}

/**
 * Build the in out file management interface
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::BuildInOutFileInterface()
{
    logTrace("sclguiACTIONS_VIEW::BuildInOutFileInterface()");
    
    // Create subpanel reset
    _deletePanel.SetText("DELETE");
    _deletePanel.SetLabel("Delete Star");
    _deletePanel.SetHelp("Deleting of star");
    // Create textfield associated to reset subpanel
    _deleteTextfield.SetText("1");
    _deleteTextfield.SetHelp("Line number to delete");
    _deleteTextfield.SetLabel("Star number");
    // Add textfield in the subpanel
    _deletePanel.Add(&_deleteTextfield);

    // Create subpanel load
    _loadPanel.SetText("LOAD");
    _loadPanel.SetLabel("Load File");
    _loadPanel.SetHelp("Loading of star");
    // Create textfield associated to the load subpanel
    _loadTextfield.SetText("*.scl");
    _loadTextfield.SetHelp("File Name to load");
    _loadTextfield.SetLabel("File Name to load");
    // Add textfield in the subpanel
    _loadPanel.Add(&_loadTextfield);

    // Create subpanel save
    _savePanel.SetText("SAVE");
    _savePanel.SetLabel("Save File");
    _savePanel.SetHelp("Save file in directory /Resultats/");
    _saveTextfield.SetText("*.scl");
    _saveTextfield.SetHelp("File name to save current list of calibrators");
    _saveTextfield.SetLabel("File Name to save");
    // Add textfield in the subpanel    
    _savePanel.Add(&_saveTextfield);

    // Create subpanel CSV export
    _CSVExportPanel.SetText("EXPORT CSV");
    _CSVExportPanel.SetLabel("Export CSV File");
    _CSVExportPanel.SetHelp("Export CSV file in directory /Resultats/");
    
    // Create textfield associated to the export subpanel
    _CSVExportTextfield.SetText("*.csv");
    _CSVExportTextfield.SetHelp("File name to export current list of calibrators as CSV file");
    _CSVExportTextfield.SetLabel("File Name to export");
    // Add textfield in the subpanel
    _CSVExportPanel.Add(&_CSVExportTextfield);

    // Create subpanel VOT export
    _VOTExportPanel.SetText("EXPORT VOT");
    _VOTExportPanel.SetLabel("Export VOT File");
    _VOTExportPanel.SetHelp("Export VOT file in directory /Resultats/");
    
    // Create textfield associated to the export subpanel
    _VOTExportTextfield.SetText("*.vot");
    _VOTExportTextfield.SetHelp("File name to export current list of calibrators as VOTable");
    _VOTExportTextfield.SetLabel("File Name to export");
    // Add textfield in the subpanel
    _VOTExportPanel.Add(&_VOTExportTextfield);

    return mcsSUCCESS;    
}

/**
 * Set default file name in the textfield
 *
 * @return always mcsSUCCESS 
 */
mcsCOMPL_STAT sclguiACTIONS_VIEW::SetDefaultIOFileName()
{
    logTrace("sclguiACTIONS_VIEW::SetDefaultIOFileName()");

    // Create default file name
    mcsSTRING256 fileName, tempFileName;
    snprintf(fileName, sizeof(fileName), "%s-%s",
             _requestModelModel->GetObjectName(),
             _requestModelModel->GetSearchBand());

    // Create textfield associated to the save subpanel
    snprintf(tempFileName, sizeof(tempFileName), "%s.scl", fileName);
    _saveTextfield.SetText(tempFileName);

    // Create default CSV export file name
    snprintf(tempFileName, sizeof(tempFileName), "%s.csv", fileName);
    _CSVExportTextfield.SetText(tempFileName);

    // Create textfield associated to the save subpanel
    snprintf(tempFileName, sizeof(tempFileName), "%s.vot", fileName);
    _VOTExportTextfield.SetText(tempFileName);

    return mcsSUCCESS;
};
/*___oOo___*/
