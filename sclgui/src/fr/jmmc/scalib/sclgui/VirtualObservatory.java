/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VirtualObservatory.java,v 1.20 2007-07-03 17:05:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
 * Updated to reflect MCSLogger API changes.
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

import fr.jmmc.mcs.gui.*;
import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import fr.jmmc.sclws_wsdl.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.net.URL;

import java.util.*;

import javax.swing.*;

import javax.xml.rpc.holders.*;


/**
 * Handle JMMC WebServices interactions and file input/ouput.
 */
public class VirtualObservatory extends Observable
{
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

    /** Quit action */
    public QuitAction _quitAction;

    /** Get Star action */
    public GetStarAction _getStarAction;

    /** Get Cal action */
    public GetCalAction _getCalAction;

    /** Plot in Aladin action */
    public PlotInAladinAction _plotInAladinAction;

    /** Interaction with Aladin */
    VOInteraction _aladinInteraction;

    /**
     * Contructor.
     */
    public VirtualObservatory(QueryModel queryModel,
        CalibratorsModel calibratorsModel, FiltersModel filtersModel)
    {
        _queryModel                  = queryModel;
        _calibratorsModel            = calibratorsModel;
        _filtersModel                = filtersModel;

        // File related members
        _file                        = null;
        _openFileAction              = new OpenFileAction();
        _saveFileAction              = new SaveFileAction();
        _saveFileAsAction            = new SaveFileAsAction();
        _revertToSavedFileAction     = new RevertToSavedFileAction();
        _exportToCSVFileAction       = new ExportToCSVFileAction();
        _exportToHTMLFileAction      = new ExportToHTMLFileAction();
        _quitAction                  = new QuitAction();

        // Query related members
        _getStarAction               = new GetStarAction();
        _getCalAction                = new GetCalAction();

        // Calibrators related members
        _aladinInteraction           = null;
        _plotInAladinAction          = new PlotInAladinAction();

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
        MCSLogger.trace();

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
        MCSLogger.trace();

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
        MCSLogger.trace();

        _saveFileAction.setEnabled(true);
        _saveFileAsAction.setEnabled(true);
        _exportToCSVFileAction.setEnabled(true);
        _exportToHTMLFileAction.setEnabled(true);
        _plotInAladinAction.setEnabled(true);
    }

    /**
     * If any modification were applied, ask the user if we ditch them.
     *
     * @return true if the modifications can be ignored, false otherwise.
     */
    public boolean canLostModifications()
    {
        MCSLogger.trace();

        boolean canLostModifications = true;

        // Check if the data are NOT saved before loosing any results !!!
        if (_calibratorsModel.dataHaveChanged() == true)
        {
            // Ask the user if he wants to save modifications
            Object[] options = { "Save", "Cancel", "Don't Save" };
            int      result  = JOptionPane.showOptionDialog(null,
                    "Do you want to save changes to this document before closing ?\nIf you don't save, your changes will be lost.\n\n",
                    null, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            // If the User clicked the "Save" button
            if (result == 0)
            {
                // Save the current data
                _saveFileAction.actionPerformed(null);
            }

            // If the ucer clicked the "Cancel" button
            if (result == 1)
            {
                // Cancel the exit
                canLostModifications = false;
            }

            // If the user clicked the "Don't Save" button, do nothing !
        }

        return canLostModifications;
    }

    /**
     * Launch the webservice querying with the given query (for ASPRO launch).
     *
     * @param query the query parameters as an empty SearchCal VOTable.
     */
    public void executeQuery(String query)
    {
        MCSLogger.debug("Received query = " + query);

        // Parse the query
        try
        {
            StatusBar.show("parsing query...");
            _calibratorsModel.parseVOTable(query);
            _queryModel.loadParamSet(_calibratorsModel.getParamSet());
        }
        catch (Exception ex)
        {
            StatusBar.show("search aborted (could not parse query) !");
            MCSLogger.error("Could not parse query : " + ex);

            JOptionPane.showMessageDialog(null, "Could not parse query.",
                "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        // Launch the resquest
        StatusBar.show("Lauching search...");
        _getCalAction.actionPerformed(null);
    }

    /**
     * Called to open files.
     */
    protected class OpenFileAction extends MCSAction
    {
        public OpenFileAction()
        {
            super("openFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            // If we can lost current modifications
            if (canLostModifications() == true)
            {
                JFileChooser fileChooser = new JFileChooser();
                int          returnVal   = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    _file = fileChooser.getSelectedFile();

                    // If a file was defined (No cancel in the dialog)
                    if (_file != null)
                    {
                        // Loading the file in the calibrators model and in the query
                        try
                        {
                            StatusBar.show("loading file...");
                            _calibratorsModel.openFile(_file);
                            _queryModel.loadParamSet(_calibratorsModel.getParamSet());
                            StatusBar.show("file succesfully loaded.");
                        }
                        catch (Exception ex)
                        {
                            StatusBar.show("loading aborted (file error) !");
                            MCSLogger.error("Could not open file : " + ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not open file.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }

                        // Enabling the 'Save' menus
                        enableSaveMenus(true);
                    }
                }

                // Now that a file has been loaded
                _revertToSavedFileAction.setEnabled(true);
            }
        }
    }

    /**
     * Called to revert the current state to the last saved state.
     */
    protected class RevertToSavedFileAction extends MCSAction
    {
        public RevertToSavedFileAction()
        {
            super("revertToSavedFile");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

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
                    MCSLogger.error("Could not re-open file : " + ex);

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
    protected class SaveFileAction extends MCSAction
    {
        public SaveFileAction()
        {
            super("saveFile");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            // If the current data were never saved yet
            if (_file == null)
            {
                JFileChooser fileChooser = new JFileChooser();
                int          returnVal   = fileChooser.showSaveDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    _file = fileChooser.getSelectedFile();
                }
            }

            // If a file was defined (No cancel in the dialog)
            if (_file != null)
            {
                _calibratorsModel.saveVOTableFile(_file);
            }

            // Now that a file has been saved
            _revertToSavedFileAction.setEnabled(true);
        }
    }

    /**
     * Called to save in a new files.
     */
    protected class SaveFileAsAction extends MCSAction
    {
        public SaveFileAsAction()
        {
            super("saveFileAs");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            JFileChooser fileChooser = new JFileChooser();
            int          returnVal   = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                _file = fileChooser.getSelectedFile();
                _calibratorsModel.saveVOTableFile(_file);
            }

            // Now that a file has been saved
            _revertToSavedFileAction.setEnabled(true);
        }
    }

    /**
     * Called to export current data to a CSV formatted file.
     */
    protected class ExportToCSVFileAction extends MCSAction
    {
        public ExportToCSVFileAction()
        {
            super("exportToCSVFile");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            JFileChooser fileChooser = new JFileChooser();
            int          returnVal   = fileChooser.showSaveDialog(null);

            File         file        = null;

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null)
            {
                _calibratorsModel.exportCurrentVOTableToCSV(file);
            }
        }
    }

    /**
     * Called to export current data to a HTML formatted file.
     */
    protected class ExportToHTMLFileAction extends MCSAction
    {
        public ExportToHTMLFileAction()
        {
            super("exportToHTMLFile");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            JFileChooser fileChooser = new JFileChooser();
            int          returnVal   = fileChooser.showSaveDialog(null);

            File         file        = null;

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null)
            {
                _calibratorsModel.exportCurrentVOTableToHTML(file);
            }
        }
    }

    /**
     * Called to quit SearchCal.
     */
    protected class QuitAction extends MCSAction
    {
        public QuitAction()
        {
            super("quit");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (canLostModifications() == true)
            {
                // Quitting
                System.exit(0);
            }
        }
    }

    /**
     * Get science object properties from is name through Simbad web service.
     */
    protected class GetStarAction extends MCSAction
    {
        public GetStarAction()
        {
            // @TODO : set button image
            super("getStar");

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            // @TODO : Querying Simbad and fill the query model accordinally
            _queryModel.reset();

            try
            {
                _queryModel.loadDefaultValues();
            }
            catch (Exception ex)
            {
                MCSLogger.error("GetStar error : " + ex);
            }
        }
    }

    /**
     * Get calibrator list as a raw VOTable from JMMC web service.
     */
    protected class GetCalAction extends MCSAction
    {
        SclwsLocator  loc = null;
        SclwsPortType s   = null;
        String        id  = null;

        public GetCalAction()
        {
            super("getCal");

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            // Launch a new thread only if no other one has been launched yet
            if (isQueryLaunched() == false)
            {
                // Query is stating
                setQueryLaunchedState(true);

                StatusBar.show(
                    "searching calibrators... (please wait, this may take a while)");

                // Launch the query in the background in order to keed GUI updated
                class GetCalThread extends Thread
                {
                    GetCalThread()
                    {
                    }

                    public void run()
                    {
                        try
                        {
                            // Get the connection ID
                            try
                            {
                                loc     = new SclwsLocator();
                                s       = loc.getsclws();

                                // Get the connection ID
                                id      = s.getCalAsyncID();

                                MCSLogger.test("Connection ID = '" + id + "'.");
                                StatusBar.show(
                                    "searching calibrators... (connection established)");
                            }
                            catch (Exception ex)
                            {
                                StatusBar.show(
                                    "search aborted (connection refused) !");
                                MCSLogger.error("Connection failed : " + ex);

                                JOptionPane.showMessageDialog(null,
                                    "Could not connect to JMMC server.",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                                setQueryLaunchedState(false);

                                return;
                            }

                            // Get the query from the GUI
                            String query = _queryModel.getQueryAsMCSString();
                            MCSLogger.test("Query = '" + query + "'.");

                            // Launch the query in the background
                            class QueryThread extends Thread
                            {
                                SclwsPortType _s;
                                String        _id;
                                String        _query;
                                String        _result;

                                QueryThread(SclwsPortType s, String id,
                                    String query)
                                {
                                    _s          = s;
                                    _query      = query;
                                    _id         = id;
                                    _result     = null;
                                }

                                String getResult()
                                {
                                    return _result;
                                }

                                public void run()
                                {
                                    try
                                    {
                                        // Launch the query
                                        StatusBar.show(
                                            "searching calibrators... (sending query)");
                                        _result = _s.getCalAsyncQuery(_id,
                                                _query);
                                        StatusBar.show(
                                            "searching calibrators... (query sent)");
                                    }
                                    catch (Exception ex)
                                    {
                                        StatusBar.show(
                                            "search aborted (could not send query) !");
                                        MCSLogger.error(
                                            "Could not send query : " + ex);

                                        JOptionPane.showMessageDialog(null,
                                            "Could not send query to JMMC server.",
                                            "Error", JOptionPane.ERROR_MESSAGE);

                                        setQueryLaunchedState(false);

                                        return;
                                    }
                                }
                            }

                            // Launch the querying thread
                            QueryThread queryThread = new QueryThread(s, id,
                                    query);
                            queryThread.start();

                            // @TODO : See for thread destruction on Cancel click
                            // queryThread.interrupt();
                            StatusBar.show(
                                "searching calibrators... (querying catalogs)");

                            // GetCal status polling to update ProgressBar
                            String  currentCatalogName = "";
                            Integer catalogIndex       = 0;
                            Integer nbOfCatalogs       = 0;
                            Boolean lastCatalog        = false;

                            do
                            {
                                // Get query progression status
                                try
                                {
                                    currentCatalogName = s.getCalWaitForCurrentCatalogName(id);
                                    _queryModel.setCatalogName(currentCatalogName);

                                    nbOfCatalogs = s.getCalNbOfCatalogs(id);
                                    _queryModel.setTotalStep(nbOfCatalogs);

                                    catalogIndex = s.getCalCurrentCatalogIndex(id);
                                    _queryModel.setCurrentStep(catalogIndex);

                                    lastCatalog = s.getCalIsLastCatalog(id);
                                    /*
                                       pm.setMinimum(catalogIndex);
                                       pm.setMaximum(nbOfCatalogs);
                                       pm.setNote(currentCatalogName);
                                     */
                                    MCSLogger.test("Status = '" +
                                        currentCatalogName + "' - " +
                                        catalogIndex + "/" + nbOfCatalogs +
                                        " (" + lastCatalog + ").");
                                }
                                catch (Exception ex)
                                {
                                    StatusBar.show(
                                        "search aborted (catalog error) !");
                                    MCSLogger.error(
                                        "Status retrieving error : " + ex);

                                    JOptionPane.showMessageDialog(null,
                                        "Communication with the JMMC server failed.",
                                        "Error", JOptionPane.ERROR_MESSAGE);

                                    setQueryLaunchedState(false);

                                    return;
                                }
                            }
                            while (lastCatalog == false);

                            StatusBar.show(
                                "searching calibrators... (waiting for result)");

                            // Wait for the query thread
                            try
                            {
                                // Wait for the thread to end
                                queryThread.join();
                            }
                            catch (Exception ex)
                            {
                                StatusBar.show(
                                    "search aborted (could not get result) !");
                                MCSLogger.error("Could not get result : " + ex);

                                JOptionPane.showMessageDialog(null,
                                    "Could not get result from JMMC server.",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                                setQueryLaunchedState(false);

                                return;
                            }

                            String result = queryThread.getResult();

                            if (result.length() > 0)
                            {
                                StatusBar.show(
                                    "parsing calibrators... (please wait, this may take a while)");

                                try
                                {
                                    // Parse the received VOTable
                                    _calibratorsModel.parseVOTable(result);
                                    StatusBar.show(
                                        "searching calibrators... done.");
                                }
                                catch (IOException ex)
                                {
                                    StatusBar.show("parsing aborted !");
                                    MCSLogger.error(
                                        "Could not parse received VOTable : " +
                                        ex);

                                    JOptionPane.showMessageDialog(null,
                                        "Search failed (invalid VOTable received).",
                                        "Error", JOptionPane.ERROR_MESSAGE);

                                    setQueryLaunchedState(false);

                                    return;
                                }
                            }
                            else
                            {
                                MCSLogger.test("No calibrators found.");

                                StatusBar.show("no calibrators found.");

                                setQueryLaunchedState(false);

                                return;
                            }

                            // As data are now loaded
                            enableSaveMenus(true);
                        }
                        catch (Exception ex)
                        {
                            StatusBar.show(
                                "search aborted (communication error) !");
                            MCSLogger.error(
                                "Could not communicate with server : " + ex);

                            JOptionPane.showMessageDialog(null,
                                "Communication failed.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }

                        // Query is finished
                        setQueryLaunchedState(false);
                    }
                }

                // Launch the GetCal thread
                GetCalThread getCalThread = new GetCalThread();
                getCalThread.start();
            }
            else
            {
                StatusBar.show("cancelling current search...");

                // @TODO : dispose thread and cancel query
                StatusBar.show("search cancelled.");

                Boolean isOk = false;

                // Get query progression status
                try
                {
                    isOk = s.getCalCancelID(id);
                }
                catch (Exception ex)
                {
                    StatusBar.show(
                        "Could not cancel request (communication error) !");
                    MCSLogger.error("Could not cancel request : " + ex);

                    JOptionPane.showMessageDialog(null,
                        "Request cancellation failed.", "Error",
                        JOptionPane.ERROR_MESSAGE);

                    setQueryLaunchedState(false);

                    return;
                }

                // Query is finished
                setQueryLaunchedState(false);
            }
        }
    }

    /**
     * Called to plot data in Aladin.
     */
    protected class PlotInAladinAction extends MCSAction
    {
        public PlotInAladinAction()
        {
            super("plotCalibratorsInAladin");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (_calibratorsModel.getVOTable() != null)
            {
                if (_aladinInteraction == null)
                {
                    StatusBar.show("launchin Aladin...");
                    _aladinInteraction = new VOInteraction();
                    _aladinInteraction.startAladin(_calibratorsModel.getVOTable());
                    _aladinInteraction._aladin.execCommand("sync");
                }
                else
                {
                    InputStream in  = new StringBufferInputStream(_calibratorsModel.getVOTable());
                    String      oid = _aladinInteraction.putVOTable(_aladinInteraction,
                            in, "SearchCal");
                    MCSLogger.info("Aladin return new oid for votable : " +
                        oid);

                    _aladinInteraction._aladin.setVisible(true);
                }
            }
            else
            {
                StatusBar.show("could not launch Aladin (no calibrators).");
            }
        }
    }
}
/*___oOo___*/
