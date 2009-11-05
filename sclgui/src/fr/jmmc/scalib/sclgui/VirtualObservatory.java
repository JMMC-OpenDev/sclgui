/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VirtualObservatory.java,v 1.32 2009-10-23 15:55:53 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.31  2009/10/23 15:50:30  lafrasse
 * Removed deprecated GetStarAction and related stuff.
 *
 * Revision 1.30  2009/04/30 14:36:11  lafrasse
 * Added desktop application creation from JNLP.
 * Enforce use of the default Apple menu bar on Mac OS X.
 * Added automatic opening of '.scvot' files.
 * Prevent file overwritting while saving or exporting data.
 *
 * Revision 1.29  2009/04/22 14:37:06  lafrasse
 * Jalopization.
 *
 * Revision 1.28  2009/02/17 14:47:37  lafrasse
 * Added log to monitor the server address and port we connect to.
 *
 * Revision 1.27  2008/12/15 13:32:37  lafrasse
 * Corrected CSV and HTML export exception.
 *
 * Revision 1.26  2008/09/10 22:44:45  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Moved to new JMCS APIs.
 * Removed CDS Aladin binding.
 *
 * Revision 1.25  2007/12/04 11:00:50  lafrasse
 * Corrected a bug during file loading that was preventing query parsing.
 *
 * Revision 1.24  2007/11/12 10:53:11  lafrasse
 * Updated to follow web service function call changes.
 *
 * Revision 1.23  2007/08/09 07:48:52  lafrasse
 * Disabled SIMBAD SOAP package import directive.
 *
 * Revision 1.22  2007/08/01 15:29:22  lafrasse
 * Added support for SIMDAD science object querying through URL+script (plus proof
 * of disfunctionning through SOAP).
 *
 * Revision 1.21  2007/07/10 17:34:55  lafrasse
 * Added proper thread termination and communication exception handling.
 *
 * Revision 1.20  2007/07/03 17:05:00  lafrasse
 * Added support for query cancellation.
 *
 * Revision 1.19  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.18  2007/03/16 10:07:34  lafrasse
 * Added support for instanciation and execution from ASPRO.
 *
 * Revision 1.17  2007/02/16 17:19:03  lafrasse
 * Threaded the GetCal actionPerformed() method in order to let the GUI updates (progres bar) while the query is ongoing.
 * Added support for true catalog named queried.
 *
 * Revision 1.16  2007/02/13 16:17:58  lafrasse
 * Jalopyzation.
 *
 * Revision 1.15  2007/02/13 16:16:12  lafrasse
 * Enabled JMMC SearchCal SOAP webservice querying.
 *
 * Revision 1.14  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.13  2006/12/01 17:43:42  lafrasse
 * Changed "revert" menu enabling only if a file has been loaded or saved.
 *
 * Revision 1.12  2006/11/30 16:02:33  lafrasse
 * Changed exporting rules to only export currently displayed stars.
 *
 * Revision 1.11  2006/11/27 15:01:50  lafrasse
 * Moved PlotInAladin action from MainWindow to VirtualObservatory in order to
 * ensure menu ennabling/disabling.
 *
 * Revision 1.10  2006/11/23 16:22:57  lafrasse
 * Added detection and handling of unsaved modifications about to be lost (Quit).
 *
 * Revision 1.9  2006/11/18 23:09:58  lafrasse
 * Handled SCAction change to MCSAction.
 * Added Quit action.
 * Added support for unsaved changes detection on quit.
 *
 * Revision 1.8  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.7  2006/10/16 14:29:51  lafrasse
 * Updated to reflect _logger API changes.
 *
 * Revision 1.6  2006/09/15 14:20:54  lafrasse
 * Added query default values support.
 *
 * Revision 1.5  2006/08/09 13:10:15  lafrasse
 * Refined VirtualObservatory::getCal() API
 *
 * Revision 1.4  2006/08/04 16:35:43  lafrasse
 * Added queryModel & filtersModel hooks in VirtualObservatory
 *
 * Revision 1.3  2006/06/30 08:01:23  lafrasse
 * Added fake progress bar updates and science object completion API
 *
 * Revision 1.2  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;


/*
   // Not in use (do not work from here)
   import cds.simbad.uif.*;
 */
import fr.jmmc.mcs.gui.*;
import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import fr.jmmc.sclws_wsdl.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.net.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;

import javax.xml.rpc.holders.*;


/**
 * Handle JMMC WebServices interactions and file input/ouput.
 */
public class VirtualObservatory extends Observable
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.VirtualObservatory");

    /** Query model */
    private QueryModel _queryModel;

    /** Data model to which the result should be passed */
    private CalibratorsModel _calibratorsModel;

    /** Filters model */
    private FiltersModel _filtersModel;

    /** Path to an open or saved file */
    private File _file;

    /** Store wether the Query has be launched or not */
    private boolean _queryIsLaunched = false;

    /** Proxy to shared FileFilter repository */
    FileFilterRepository _fileFilterRepository = FileFilterRepository.getInstance();

    /** SearchCal-specific VOTable format */
    String _scvotMimeType = "application/x-searchcal+votable+xml";

    /**
     * DOCUMENT ME!
     */
    String _csvMimeType = "text/csv";

    /**
     * DOCUMENT ME!
     */
    String _htmlMimeType = "text/html";

    /** Open file... action */
    public OpenFileAction _openFileAction;

    /** Save file... action */
    public SaveFileAction _saveFileAction;

    /** Save file as... action */
    public SaveFileAsAction _saveFileAsAction;

    /** Revet to Saved File action */
    public RevertToSavedFileAction _revertToSavedFileAction;

    /** Export to CSV File action */
    public ExportToCSVFileAction _exportToCSVFileAction;

    /** Export to HTML File action */
    public ExportToHTMLFileAction _exportToHTMLFileAction;

    /** Get Cal action */
    public GetCalAction _getCalAction;

    /**
     * Contructor.
     */
    public VirtualObservatory(QueryModel queryModel,
        CalibratorsModel calibratorsModel, FiltersModel filtersModel)
    {
        String classPath = getClass().getName();

        _queryModel           = queryModel;
        _calibratorsModel     = calibratorsModel;
        _filtersModel         = filtersModel;

        // FileFilter initialiation
        _fileFilterRepository.put(_scvotMimeType, "scvot",
            "SearchCal VOTables (SCVOT)");
        _fileFilterRepository.put(_csvMimeType, "csv",
            "Comma Separated Values (CSV)");
        _fileFilterRepository.put(_htmlMimeType, "html",
            "HyperText Markup Language (HTML)");

        // File related members
        _file                        = null;
        _openFileAction              = new OpenFileAction(classPath,
                "_openFileAction");
        _saveFileAction              = new SaveFileAction(classPath,
                "_saveFileAction");
        _saveFileAsAction            = new SaveFileAsAction(classPath,
                "_saveFileAsAction");
        _revertToSavedFileAction     = new RevertToSavedFileAction(classPath,
                "_revertToSavedFileAction");
        _exportToCSVFileAction       = new ExportToCSVFileAction(classPath,
                "_exportToCSVFileAction");
        _exportToHTMLFileAction      = new ExportToHTMLFileAction(classPath,
                "_exportToHTMLFileAction");

        // Query related members
        _getCalAction                = new GetCalAction(classPath,
                "_getCalAction");

        // WebService related members
        setQueryLaunchedState(false);
    }

    /**
     * Return whether the query has been launched or not.
     *
     * It is 'synchronized' as only one thread should use this method at once.
     *
     * @return true if the query is already launched, false otherwise.
     */
    protected synchronized boolean isQueryLaunched()
    {
        _logger.entering("VirtualObservatory", "isQueryLaunched");

        return _queryIsLaunched;
    }

    /**
     * Set whether the query has been launched or not.
     *
     * It is 'synchronized' as only one thread should use this method at once.
     *
     * @param flag true to enable all menus, false otherwise.
     */
    protected synchronized void setQueryLaunchedState(boolean flag)
    {
        _logger.entering("VirtualObservatory", "setQueryLaunchedState");

        _queryIsLaunched = flag;

        if (_queryIsLaunched == true)
        {
            // Change button title to 'Cancel'
            _getCalAction.putValue(Action.NAME, "Cancel");
        }
        else
        {
            // Change button title to 'Get Calibrators'
            _getCalAction.putValue(Action.NAME, "Get Calibrators");
            _queryModel.setCurrentStep(0);
            _queryModel.setTotalStep(0);
            _queryModel.setCatalogName("");
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Enable/disable 'Save' related menus.
     *
     * @param flag true to enable all menus, false otherwise.
     */
    public void enableSaveMenus(boolean flag)
    {
        _logger.entering("VirtualObservatory", "enableSaveMenus");

        _saveFileAction.setEnabled(true);
        _saveFileAsAction.setEnabled(true);
        _exportToCSVFileAction.setEnabled(true);
        _exportToHTMLFileAction.setEnabled(true);
    }

    /**
     * If any modification were applied, ask the user if we discard them.
     *
     * @return true if the modifications can be ignored, false otherwise.
     */
    public boolean canLostModifications()
    {
        _logger.entering("VirtualObservatory", "canLostModifications");

        boolean canLostModifications = false;

        // If there is no data to save
        if (_calibratorsModel.dataHaveChanged() == false)
        {
            canLostModifications = true;
        }
        else // If the data are NOT saved, handle it before loosing any results !!!
        {
            // Ask the user if he wants to save modifications
            Object[] options = { "Save", "Cancel", "Don't Save" };
            int      result  = JOptionPane.showOptionDialog(null,
                    "Do you want to save changes to this document before closing ?\nIf you don't save, your changes will be definitively lost.\n\n",
                    null, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            // Handle user choice
            switch (result)
            {
            // If the user clicked the "Save" button
            case 0: // options[0] = "Save" button
                    // Save the current data if no cancel occured
                canLostModifications = _saveFileAction.save();

                break;

            // If the user clicked the "Don't Save" button
            case 2: // options[2] = "Don't Save" button
                    // Exit
                canLostModifications = true;

                break;

            // If the user clicked the "Cancel" button or pressed 'esc' key
            case 1: // options[1] = "Cancel" button
            case JOptionPane.CLOSED_OPTION: // 'esc' key
            default: // Any other case
                     // Cancel the exit
                canLostModifications = false;

                break;
            }
        }

        return canLostModifications;
    }

    /**
     * If the given file exists, ask user whether to overwrite it or not.
     *
     * @return true if the file can be overwritten, false otherwise.
     */
    public boolean canOverwriteFile(File file)
    {
        _logger.entering("VirtualObservatory", "canOverwriteFile");

        // If the given file does not already exists
        if (file.exists() == false)
        {
            return true;
        }

        boolean canOverwriteFile = false;

        // Ask the user if he wants to save modifications
        Object[] options = { "Cancel", "Replace" };
        int      result  = JOptionPane.showOptionDialog(null,
                "'" + file.getName() +
                "' alreadey exists. Do you want to replace it ?\nA file or folder with the same name already exists in the current folder.\nReplacing it will overwrite its current contents.\n\n",
                null, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);

        // Handle user choice
        switch (result)
        {
        // If the user clicked the "Don't Save" button
        case 1: // options[1] = "Replace" button
                // Exit
            canOverwriteFile = true;

            break;

        // If the user clicked the "Cancel" button or pressed 'esc' key
        case 0: // options[0] = "Cancel" button
        case JOptionPane.CLOSED_OPTION: // 'esc' key
        default: // Any other case
                 // Cancel the exit
            canOverwriteFile = false;

            break;
        }

        return canOverwriteFile;
    }

    /**
     * Launch the webservice querying with the given query (for ASPRO launch).
     *
     * @param query the query parameters as an empty SearchCal VOTable.
     */
    public void executeQuery(String query)
    {
        _logger.entering("VirtualObservatory", "executeQuery");

        _logger.fine("Received query = " + query);

        // Parse the query
        try
        {
            StatusBar.show("parsing query...");
            _calibratorsModel.parseVOTable(query);
            _queryModel.loadParamSet(_calibratorsModel.getParamSet());
        }
        catch (Exception ex)
        {
            StatusBar.show(
                "calibrator search aborted (could not parse query) !");
            _logger.log(Level.SEVERE, "Could not parse query.", ex);

            JOptionPane.showMessageDialog(null, "Could not parse query.",
                "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        // Launch the request
        StatusBar.show("Lauching search...");
        _getCalAction.actionPerformed(null);
    }

    /**
     * Called to open files.
     */
    protected class OpenFileAction extends RegisteredAction
    {
        public OpenFileAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            flagAsOpenAction();
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("OpenFileAction", "actionPerformed");

            // If we can lost current modifications
            if (canLostModifications() == true)
            {
                // If the action was automatically triggered from App launch
                if (e.getSource() == ActionRegistrar.getInstance())
                {
                    _file = new File(e.getActionCommand());
                }
                else
                {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle(
                        "Open a calibrator list from file...");

                    fileChooser.setFileFilter(_fileFilterRepository.get(
                            _scvotMimeType));

                    int returnVal = fileChooser.showOpenDialog(null);

                    if (returnVal == JFileChooser.APPROVE_OPTION)
                    {
                        _file = fileChooser.getSelectedFile();
                    }
                }

                // If a file was defined (No cancel in the dialog)
                if (_file != null)
                {
                    StatusBar.show("loading file...");

                    // Loading the file in the calibrators model
                    try
                    {
                        StatusBar.show("loading file (parsing calibrators)...");
                        _calibratorsModel.openFile(_file);
                        StatusBar.show(
                            "loading file (calibrators successfully parsed)...");
                    }
                    catch (Exception ex)
                    {
                        StatusBar.show(
                            "loading aborted (calibrators parsing error) !");
                        _logger.log(Level.SEVERE,
                            "Could not open file (calibrators parsing error) : ",
                            ex);

                        JOptionPane.showMessageDialog(null,
                            "Could not open file (calibrators parsing error).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Loading the file in the query model
                    try
                    {
                        StatusBar.show("loading file (parsing query)...");
                        _queryModel.loadParamSet(_calibratorsModel.getParamSet());
                        StatusBar.show(
                            "loading file (query successfully parsed)...");
                    }
                    catch (Exception ex)
                    {
                        StatusBar.show(
                            "loading aborted (query parsing error) !");
                        _logger.log(Level.SEVERE,
                            "Could not open file (query parsing error).", ex);

                        JOptionPane.showMessageDialog(null,
                            "Could not open file (query parsing error).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    StatusBar.show("file succesfully loaded.");

                    // Enabling the 'Save' menus
                    enableSaveMenus(true);
                }

                // Now that a file has been loaded
                _revertToSavedFileAction.setEnabled(true);
            }
        }
    }

    /**
     * Called to revert the current state to the last saved state.
     */
    protected class RevertToSavedFileAction extends RegisteredAction
    {
        public RevertToSavedFileAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("RevertToSavedFileAction", "actionPerformed");

            // If we can lost current modifications
            if (canLostModifications() == true)
            {
                // Loading a new file
                try
                {
                    StatusBar.show("re-loading file...");
                    _calibratorsModel.openFile(_file);
                    StatusBar.show("file succesfully re-loaded.");
                }
                catch (Exception ex)
                {
                    StatusBar.show("re-loading aborted (file error) !");
                    _logger.log(Level.SEVERE, "Could not re-open file.", ex);

                    JOptionPane.showMessageDialog(null,
                        "Could not re-open file.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Called to save in a file (in a new one if needed).
     */
    protected class SaveFileAction extends RegisteredAction
    {
        public SaveFileAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            setEnabled(false);
        }

        /**
         * Save data to file.
         *
         * @return true if save was done, false if save was cancelled.
         */
        public boolean save()
        {
            StatusBar.show("saving file...");

            File file = null;

            // If the current data were never saved yet
            if (_file == null)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(
                    "Save current calibrator list to file...");

                fileChooser.setFileFilter(_fileFilterRepository.get(
                        _scvotMimeType));

                int returnVal = fileChooser.showSaveDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    file = fileChooser.getSelectedFile();
                }
                else
                {
                    // Save was cancelled
                    StatusBar.show("saving cancelled.");

                    return false;
                }
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null)
            {
                if (canOverwriteFile(file) == false)
                {
                    StatusBar.show("overwritting cancelled.");

                    return false;
                }

                _calibratorsModel.saveVOTableFile(file);
                _file = file;

                // Now that a file has been saved
                _revertToSavedFileAction.setEnabled(true);

                StatusBar.show("file saved.");
            }

            return true;
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("SaveFileAction", "actionPerformed");

            save();
        }
    }

    /**
     * Called to save in a new files.
     */
    protected class SaveFileAsAction extends RegisteredAction
    {
        FileFilterRepository _fileFilterRepository = FileFilterRepository.getInstance();
        String               _scvotMimeType        = "application-x/searchcal-votable-file";

        public SaveFileAsAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("SaveFileAsAction", "actionPerformed");

            StatusBar.show("saving file as...");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(
                "Save current calibrator list to file...");

            fileChooser.setFileFilter(_fileFilterRepository.get(_scvotMimeType));

            int  returnVal = fileChooser.showSaveDialog(null);

            File file      = null;

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();

                if (canOverwriteFile(file) == false)
                {
                    StatusBar.show("overwritting file as cancelled.");

                    return;
                }

                _calibratorsModel.saveVOTableFile(file);
                _file = file;

                // Now that a file has been saved
                _revertToSavedFileAction.setEnabled(true);

                StatusBar.show("file saved as.");
            }
        }
    }

    /**
     * Called to export current data to a CSV formatted file.
     */
    protected class ExportToCSVFileAction extends RegisteredAction
    {
        public ExportToCSVFileAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("ExportToCSVFileAction", "actionPerformed");

            StatusBar.show("exporting as CSV...");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(
                "Export current calibrator list to CSV file...");

            fileChooser.setFileFilter(_fileFilterRepository.get(_csvMimeType));

            int  returnVal = fileChooser.showSaveDialog(null);

            File file      = null;

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null)
            {
                if (canOverwriteFile(file) == false)
                {
                    StatusBar.show("overwritting exported CSV cancelled...");

                    return;
                }

                _calibratorsModel.exportCurrentVOTableToCSV(file);
                StatusBar.show("calibrator list exported to CSV file.");
            }
            else
            {
                StatusBar.show("exporting as CSV cancelled.");
            }
        }
    }

    /**
     * Called to export current data to a HTML formatted file.
     */
    protected class ExportToHTMLFileAction extends RegisteredAction
    {
        public ExportToHTMLFileAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("ExportToHTMLFileAction", "actionPerformed");

            StatusBar.show("exporting as HTML...");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(
                "Export current calibrator list to HTML file...");

            fileChooser.setFileFilter(_fileFilterRepository.get(_htmlMimeType));

            int  returnVal = fileChooser.showSaveDialog(null);

            File file      = null;

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null)
            {
                if (canOverwriteFile(file) == false)
                {
                    return;
                }

                _calibratorsModel.exportCurrentVOTableToHTML(file);
                StatusBar.show("calibrator list exported to HTML file.");
            }
            else
            {
                StatusBar.show("exporting as HTML cancelled.");
            }
        }
    }

    /**
     * Get calibrator list as a raw VOTable from JMMC web service.
     */
    protected class GetCalAction extends RegisteredAction
    {
        GetCalThread _getCalThread = null;

        public GetCalAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Get Calibrators");

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("GetCalAction", "actionPerformed");

            // Launch a new thread only if no other one has been launched yet
            if (isQueryLaunched() == false)
            {
                // Query is stating
                setQueryLaunchedState(true);

                StatusBar.show(
                    "searching calibrators... (please wait, this may take a while)");

                // Launch the query in the background in order to keed GUI updated
                _getCalThread = new GetCalThread();
                _getCalThread.start();
            }
            else
            {
                StatusBar.show("cancelling current callibrator search...");

                // If the GetCal thread has already been launched
                if (_getCalThread != null)
                {
                    // Kill it
                    _logger.fine("Killing GetCal thread ... ");
                    _getCalThread.interrupt();
                    _getCalThread = null;
                    _logger.fine("GetCal thread killed.");
                }

                // Query is finished
                setQueryLaunchedState(false);

                StatusBar.show("callibrator search cancelled.");
            }
        }

        class GetCalThread extends Thread
        {
            QueryResultThread _queryResultThread = null;
            SclwsLocator      loc                = null;
            SclwsPortType     sclws              = null;
            String            id                 = null;

            GetCalThread()
            {
                _queryResultThread = null;
            }

            public void run()
            {
                _logger.entering("GetCalThread", "run");

                getCal();
            }

            public void getCal()
            {
                _logger.entering("GetCalThread", "getCal");

                try
                {
                    // Get the connection ID
                    try
                    {
                        // Start the webservice connection
                        loc       = new SclwsLocator();
                        sclws     = loc.getsclws();
                        _logger.fine("Connected to '" + loc.getsclwsAddress() +
                            "'.");

                        // Define the webservice timeout (default = 10min)
                        //org.apache.axis.client.Stub stub = (SclwsStub) s;
                        //stub.setTimeout(20000); // 20 second, in miliseconds

                        // Get the connection ID
                        id = sclws.getCalOpenSession();

                        _logger.fine("JMMC Connection ID = '" + id + "'.");
                        StatusBar.show(
                            "searching calibrators... (connection established)");
                    }
                    catch (Exception ex)
                    {
                        // Handle error when no manual cancel
                        if (_getCalThread != null)
                        {
                            StatusBar.show(
                                "calibrator search aborted (connection refused) !");
                            _logger.log(Level.SEVERE, "Connection failed.", ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not connect to JMMC server.", "Error",
                                JOptionPane.ERROR_MESSAGE);

                            interrupt();
                            setQueryLaunchedState(false);
                        }
                        else
                        {
                            _logger.log(Level.FINE,
                                "Silenced error (cancellation).", ex);
                        }

                        return;
                    }

                    // Get the query from the GUI
                    String query = _queryModel.getQueryAsMCSString();
                    _logger.fine("Query = '" + query + "'.");

                    // Launch the querying thread
                    _queryResultThread = new QueryResultThread(sclws, id, query);
                    _queryResultThread.start();

                    StatusBar.show(
                        "searching calibrators... (querying catalogs)");

                    // GetCal status polling to update ProgressBar
                    String  currentStatus       = "";
                    String  currentCatalogName  = "";
                    String  composedQueryStatus = "";
                    Integer catalogIndex        = 0;
                    Integer nbOfCatalogs        = 0;
                    Integer requestStatus       = 0;

                    do
                    {
                        try
                        {
                            // Get query progression status
                            currentStatus = sclws.getCalQueryStatus(id);

                            // Deserialize the new status to update the GUI
                            String[] splittedStatus = currentStatus.split("\t");

                            // Parse the received status
                            int i = 0;
                            requestStatus = Integer.parseInt(splittedStatus[i++]);

                            if (splittedStatus.length == 4)
                            {
                                // Get the catalog name
                                currentCatalogName      = splittedStatus[i++];

                                // Get the catalog index
                                catalogIndex            = Integer.parseInt(splittedStatus[i++]);

                                // Get the total number of catalogs
                                nbOfCatalogs            = Integer.parseInt(splittedStatus[i++]);

                                // Compose the dispalyed query status
                                composedQueryStatus     = currentCatalogName +
                                    " - (" + catalogIndex + "/" + nbOfCatalogs +
                                    ")";

                                // Update the query model accordinaly
                                _queryModel.setCatalogName(composedQueryStatus);
                                _queryModel.setCurrentStep(catalogIndex.intValue());
                                _queryModel.setTotalStep(nbOfCatalogs.intValue());
                            }

                            _logger.fine("Status = '" + currentCatalogName +
                                "' - " + catalogIndex + "/" + nbOfCatalogs +
                                " (status = '" + requestStatus + "').");
                        }
                        catch (Exception ex)
                        {
                            // Handle error when no manual cancel
                            if (_getCalThread != null)
                            {
                                StatusBar.show(
                                    "calibrator search aborted (catalog error) !");
                                _logger.log(Level.SEVERE,
                                    "JMMC Status retrieving error.", ex);

                                JOptionPane.showMessageDialog(null,
                                    "Communication with the JMMC server failed.",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                                interrupt();
                                setQueryLaunchedState(false);
                            }
                            else
                            {
                                _logger.log(Level.FINE,
                                    "Silenced error (cancellation).", ex);
                            }

                            return;
                        }
                    }
                    while (requestStatus == 1);

                    StatusBar.show(
                        "searching calibrators... (waiting for result)");

                    // Wait for the query thread
                    try
                    {
                        // Wait for the thread to end
                        _queryResultThread.join();
                    }
                    catch (Exception ex)
                    {
                        // Handle error when no manual cancel
                        if (_getCalThread != null)
                        {
                            StatusBar.show(
                                "calibrator search aborted (could not get result) !");
                            _logger.log(Level.SEVERE,
                                "Could not get JMMC result.", ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not get result from JMMC server.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                            interrupt();
                            setQueryLaunchedState(false);
                        }
                        else
                        {
                            _logger.log(Level.FINE,
                                "Silenced error (cancellation).", ex);
                        }

                        return;
                    }

                    String result = _queryResultThread.getResult();

                    if (result.length() > 0)
                    {
                        StatusBar.show(
                            "parsing calibrators... (please wait, this may take a while)");

                        try
                        {
                            // Parse the received VOTable
                            _calibratorsModel.parseVOTable(result);
                            StatusBar.show("searching calibrators... done.");
                        }
                        catch (IOException ex)
                        {
                            // Handle error when no manual cancel
                            if (_getCalThread != null)
                            {
                                StatusBar.show("calibrator parsing aborted !");
                                _logger.log(Level.SEVERE,
                                    "Could not parse received JMMC VOTable.", ex);

                                JOptionPane.showMessageDialog(null,
                                    "Calibrator search failed (invalid VOTable received).",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                                interrupt();
                                setQueryLaunchedState(false);
                            }
                            else
                            {
                                _logger.log(Level.FINE,
                                    "Silenced error (cancellation).", ex);
                            }

                            return;
                        }
                    }
                    else
                    {
                        _logger.fine("No calibrators found.");

                        StatusBar.show("no calibrators found.");

                        setQueryLaunchedState(false);

                        return;
                    }

                    // As data are now loaded
                    enableSaveMenus(true);
                }
                catch (Exception ex)
                {
                    // Handle error when no manual cancel
                    if (_getCalThread != null)
                    {
                        StatusBar.show(
                            "calibrator search aborted (communication error) !");
                        _logger.log(Level.SEVERE,
                            "Could not communicate with JMMC server.", ex);

                        JOptionPane.showMessageDialog(null,
                            "Communication failed.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        _logger.log(Level.FINE,
                            "Silenced error (cancellation).", ex);
                    }
                }

                // Query is finished
                setQueryLaunchedState(false);
            }

            public void interrupt()
            {
                _logger.entering("GetCalThread", "interrupt");

                Boolean isOk = false;

                try
                {
                    // Ask for query cancellation
                    isOk = sclws.getCalCancelSession(id);
                }
                catch (Exception ex)
                {
                    StatusBar.show(
                        "could not cancel calibrator search (communication error) !");
                    _logger.severe("Could not cancel JMMC request : " + ex);

                    JOptionPane.showMessageDialog(null,
                        "JMMC request cancellation failed.", "Error",
                        JOptionPane.ERROR_MESSAGE);

                    setQueryLaunchedState(false);

                    return;
                }

                // If the QueryResult thread has already been launched
                if (_queryResultThread != null)
                {
                    // Kill it
                    _logger.fine("Killing QueryResult thread ... ");
                    _queryResultThread.interrupt();
                    _queryResultThread = null;
                    _logger.fine("QueryResult thread killed.");
                }

                super.interrupt();
            }

            class QueryResultThread extends Thread
            {
                SclwsPortType _sclws;
                String        _id;
                String        _query;
                String        _result;

                QueryResultThread(SclwsPortType s, String id, String query)
                {
                    _sclws      = s;
                    _query      = query;
                    _id         = id;
                    _result     = null;
                }

                public void run()
                {
                    _logger.entering("QueryResultThread", "run");

                    queryResult();
                }

                public void queryResult()
                {
                    _logger.entering("QueryResultThread", "queryResult");

                    try
                    {
                        StatusBar.show(
                            "searching calibrators... (sending query)");

                        // Launch the query
                        _result = _sclws.getCalSearchCal(_id, _query);

                        StatusBar.show(
                            "searching calibrators... (result received)");
                    }
                    catch (Exception ex)
                    {
                        // Handle error when no manual cancel
                        if (_queryResultThread != null)
                        {
                            StatusBar.show(
                                "calibrator search aborted (could not send query) !");
                            _logger.log(Level.SEVERE,
                                "Could not send JMMC query.", ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not send query to JMMC server.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                            interrupt();
                            setQueryLaunchedState(false);
                        }
                        else
                        {
                            _logger.log(Level.FINE,
                                "Silenced error (cancellation).", ex);
                        }

                        return;
                    }
                }

                String getResult()
                {
                    _logger.entering("QueryResultThread", "getResult");

                    return _result;
                }
            }
        }
    }
}
/*___oOo___*/
