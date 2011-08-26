/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.jmal.Catalog;
import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.MessagePane;
import fr.jmmc.mcs.gui.MessagePane.ConfirmSaveChanges;

import fr.jmmc.mcs.gui.StatusBar;

import fr.jmmc.mcs.interop.SampCapability;
import fr.jmmc.mcs.interop.SampCapabilityAction;
import fr.jmmc.mcs.interop.SampMessageHandler;
import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mcs.util.FileFilterRepository;
import fr.jmmc.mcs.util.RegisteredAction;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.astrogrid.samp.Message;

/**
 * Handle JMMC WebServices interactions and file input/output.
 */
public final class VirtualObservatory extends Observable {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.VirtualObservatory");
    /** Query model */
    private final QueryModel _queryModel;
    /** Data model to which the result should be passed */
    private final CalibratorsModel _calibratorsModel;
    /** Path to an open or saved file */
    private File _file;
    /** Store whether the Query has be launched or not */
    private final AtomicBoolean _queryIsLaunched;
    /** Proxy to shared FileFilter repository */
    private FileFilterRepository _fileFilterRepository = FileFilterRepository.getInstance();
    /** QuerySearchCal-specific VOTable format */
    private String _scvotMimeType = "application/x-searchcal+votable+xml";
    /** MIME type for CSV exports */
    private String _csvMimeType = "text/csv";
    /** MIME type for HTML exports */
    private String _htmlMimeType = "text/html";
    /** Open file... action */
    public final OpenFileAction _openFileAction;
    /** Save file... action */
    public final SaveFileAction _saveFileAction;
    /** Save file as... action */
    public final SaveFileAsAction _saveFileAsAction;
    /** Revet to Saved File action */
    public final RevertToSavedFileAction _revertToSavedFileAction;
    /** Export to CSV File action */
    public final ExportToCSVFileAction _exportToCSVFileAction;
    /** Export to HTML File action */
    public final ExportToHTMLFileAction _exportToHTMLFileAction;
    /** Export to SAMP action */
    public final ShareCalibratorsThroughSAMPAction _shareCalibratorsThroughSAMPAction;
    /** Get Cal action */
    public final GetCalAction _getCalAction;

    /**
     * Define QuerySearchCal Web Service steps
     */
    static enum QueryState {

        /** Open Session */
        OpenSession,
        /** QuerySearchCal Query */
        QuerySearchCal,
        /** Status Query */
        QueryStatus,
        /** Get Query result */
        QueryResult;
    }

    /**
     * Constructor
     * @param queryModel
     * @param calibratorsModel
     */
    public VirtualObservatory(QueryModel queryModel, CalibratorsModel calibratorsModel) {
        String classPath = getClass().getName();

        _queryIsLaunched = new AtomicBoolean(false);

        _queryModel = queryModel;
        _calibratorsModel = calibratorsModel;

        // FileFilter initialization
        _fileFilterRepository.put(_scvotMimeType, "scvot", "SearchCal VOTables (SCVOT)");
        _fileFilterRepository.put(_csvMimeType, "csv", "Comma Separated Values (CSV)");
        _fileFilterRepository.put(_htmlMimeType, "html", "HyperText Markup Language (HTML)");

        // File related members
        _file = null;
        _openFileAction = new OpenFileAction(classPath, "_openFileAction");
        _saveFileAction = new SaveFileAction(classPath, "_saveFileAction");
        _saveFileAsAction = new SaveFileAsAction(classPath, "_saveFileAsAction");
        _revertToSavedFileAction = new RevertToSavedFileAction(classPath, "_revertToSavedFileAction");
        _exportToCSVFileAction = new ExportToCSVFileAction(classPath, "_exportToCSVFileAction");
        _exportToHTMLFileAction = new ExportToHTMLFileAction(classPath, "_exportToHTMLFileAction");
        _shareCalibratorsThroughSAMPAction = new ShareCalibratorsThroughSAMPAction(classPath,
                "_shareCalibratorsThroughSAMPAction", SampCapability.LOAD_VO_TABLE);

        // Query related members
        _getCalAction = new GetCalAction(this, classPath, "_getCalAction");

        // Add handler to load query params and launch calibrator search
        new SampMessageHandler(SampCapability.SEARCHCAL_START_QUERY) {

            /**
             * Implements message processing
             *
             * @param senderId public ID of sender client
             * @param message message with MType this handler is subscribed to
             * @throws SampException if any error occurred while message processing
             */
            protected void processMessage(final String senderId, final Message message) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("\tReceived '" + this.handledMType() + "' message from '" + senderId + "' : '" + message + "'.");
                }

                final String query = (String) message.getParam("query");
                if (query != null) {

                    SwingUtilities.invokeLater(new Runnable() {

                        /**
                         * Synchronized by EDT
                         */
                        public void run() {
                            // bring this application to front :
                            App.showFrameToFront();

                            executeQuery(query);
                        }
                    });

                } else {
                    StatusBar.show("Could not start query from SAMP.");
                }
            }
        };

        // Add handler to load science object coordinates
/* @TBD
        try {
        SampMessageHandler handler = new SampMessageHandler(SampCapability.POINT_COORDINATES) {

        public Map processCall(HubConnection c, String senderId, Message msg) {
        System.out.println("\tReceived '" + this.handledMType() + "' message from '" + senderId + "' : '" + msg + "'.");

        Double ra = SampUtils.decodeFloat((String) msg.getParam("ra"));
        if (ra == null)
        {
        _logger.warning("Could not read RA value from SAMP:" + this.handledMType() + ": ra = '" + ra + "'.");
        return null;
        }
        String raHMS = ALX.toHMS(ra);
        if (raHMS == null)
        {
        _logger.warning("Could not convert RA degree value '" + ra + "' to HMS.");
        return null;
        }

        Double dec = SampUtils.decodeFloat((String) msg.getParam("dec"));
        if (dec == null)
        {
        _logger.warning("Could not read DEC value from SAMP:" + this.handledMType() + ": dec = '" + dec + "'.");
        return null;
        }
        String decDMS = ALX.toDMS(dec);
        if (decDMS == null)
        {
        _logger.warning("Could not convert DEC degree value '" + dec + "' to DMS.");
        return null;
        }
        _queryModel.setScienceObjectRA(raHMS);
        _queryModel.setScienceObjectDEC(decDMS);
        _queryModel.update(null, null);

        return null;
        }
        };
        } catch (Exception ex) {
        _logger.log(Level.SEVERE, "failure : ", e);
        }
         */
        // WebService related members
        setQueryLaunchedState(false);
    }

    /**
     * Return whether the query has been launched or not.
     *
     * @return true if the query is already launched, false otherwise.
     */
    protected boolean isQueryLaunched() {
        _logger.entering("VirtualObservatory", "isQueryLaunched");

        return _queryIsLaunched.get();
    }

    /**
     * Set whether the query has been launched or not using Swing EDT.
     *
     * @param running true to enable all menus, false otherwise.
     */
    protected void setQueryLaunchedState(final boolean running) {
        _logger.entering("VirtualObservatory", "setQueryLaunchedState");

        final Runnable task = new Runnable() {

            public void run() {
                _queryIsLaunched.set(running);

                String newActionName = "Cancel";

                if (!running) {
                    // Change button title to 'Get Calibrators'
                    newActionName = "Get Calibrators";
                    _queryModel.setCurrentStep(0);
                    _queryModel.setTotalStep(0);
                    _queryModel.setCatalogName("");
                }

                // Set new action name
                _getCalAction.putValue(Action.NAME, newActionName);

                setChanged();
                notifyObservers();
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            // Use invokeLater to avoid concurrency and ensure that
            // data model is modified and fire events using Swing EDT :
            SwingUtilities.invokeLater(task);
        }
    }

    /**
     * Enable or disable save/export/share menus.
     *
     * @param flag true to enable all menus, false otherwise.
     */
    public void enableDataRelatedMenus(final boolean flag) {
        _logger.entering("VirtualObservatory", "enableSaveMenus");

        _saveFileAction.setEnabled(flag);
        _saveFileAsAction.setEnabled(flag);
        _exportToCSVFileAction.setEnabled(flag);
        _exportToHTMLFileAction.setEnabled(flag);
    }

    /**
     * If any modification were applied, ask the user if we discard them.
     *
     * @return true if the modifications can be ignored, false otherwise.
     */
    public boolean canLostModifications() {
        _logger.entering("VirtualObservatory", "canLostModifications");

        boolean canLostModifications = false;

        // If there is no data to save
        if (!_calibratorsModel.dataHaveChanged()) {
            canLostModifications = true;
        } else {
          
          // If the data are NOT saved, handle it before loosing any results !!!
          // Ask the user if he wants to save modifications
          final ConfirmSaveChanges result = MessagePane.showConfirmSaveChangesBeforeClosing();

            // Handle user choice
            switch (result) {
                // If the user clicked the "Save" button
                case Save:
                    // Save the current data if no cancel occured
                    canLostModifications = _saveFileAction.save();
                    break;

                // If the user clicked the "Don't Save" button
                case Ignore:
                    // Exit
                    canLostModifications = true;
                    break;

                // If the user clicked the "Cancel" button or pressed 'esc' key
                case Cancel:
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
    public boolean canOverwriteFile(File file) {
        _logger.entering("VirtualObservatory", "canOverwriteFile");

        // If the given file does not already exists
        if (!file.exists()) {
            return true;
        }

        // Ask the user if he wants to save modifications
        return MessagePane.showConfirmFileOverwrite(file.getName());
    }

    /**
     * Launch 'sclws' web service querying with the given query (for ASPRO instanciation).
     *
     * @param query the query parameters as an empty QuerySearchCal VOTable.
     */
    public void executeQuery(final String query) {
        _logger.entering("VirtualObservatory", "executeQuery");

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Received query = " + query);
        }

        // Parse the query
        try {
            StatusBar.show("parsing query...");

            _calibratorsModel.parseVOTable(query);
            
            // load default values to reset completely the query model:
            _queryModel.loadDefaultValues();
            // load given parameters (some may be missing)
            _queryModel.loadParamSet(_calibratorsModel.getParamSet());

        } catch (Exception e) {
            StatusBar.show("calibrator search aborted (could not parse query) !");
            MessagePane.showErrorMessage("Could not parse query.", e);
            return;
        }

        // Launch the request
        StatusBar.show("Lauching search...");
        _getCalAction.actionPerformed(null);
    }

    /**
     * Called to open files.
     */
    protected class OpenFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public OpenFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            flagAsOpenAction();
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("OpenFileAction", "actionPerformed");

            // If we can lost current modifications
            if (canLostModifications()) {
                // If the action was automatically triggered from App launch
                if (e.getSource() == ActionRegistrar.getInstance()) {
                    _file = new File(e.getActionCommand());
                } else {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Open a calibrator list from file...");

                    fileChooser.setFileFilter(_fileFilterRepository.get(_scvotMimeType));

                    int returnVal = fileChooser.showOpenDialog(null);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        _file = fileChooser.getSelectedFile();
                    }
                }

                // If a file was defined (No cancel in the dialog)
                if (_file != null) {
                    StatusBar.show("loading file...");

                    // Loading the file in the calibrators model
                    try {
                        StatusBar.show("loading file (parsing calibrators)...");

                        _calibratorsModel.openFile(_file);

                        StatusBar.show("loading file (calibrators successfully parsed)...");
                    } catch (Exception ex) {
                        StatusBar.show("loading aborted (calibrators parsing error) !");
                        MessagePane.showErrorMessage("Could not open file (calibrators parsing error).", ex);
                    }

                    // Loading the file in the query model
                    try {
                        StatusBar.show("loading file (parsing query)...");

                        _queryModel.loadParamSet(_calibratorsModel.getParamSet());

                        StatusBar.show("loading file (query successfully parsed)...");

                    } catch (Exception ex) {
                        StatusBar.show("loading aborted (query parsing error) !");
                        MessagePane.showErrorMessage("Could not open file (query parsing error).", ex);
                    }

                    StatusBar.show("file succesfully loaded.");

                    // Enabling the 'Save' menus
                    enableDataRelatedMenus(true);
                }

                // Now that a file has been loaded
                _revertToSavedFileAction.setEnabled(true);
            }
        }
    }

    /**
     * Called to revert the current state to the last saved state.
     */
    protected class RevertToSavedFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public RevertToSavedFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("RevertToSavedFileAction", "actionPerformed");

            // If we can lost current modifications
            if (canLostModifications()) {
                // Loading a new file
                try {
                    StatusBar.show("re-loading file...");

                    _calibratorsModel.openFile(_file);

                    StatusBar.show("file succesfully re-loaded.");
                } catch (Exception ex) {
                    StatusBar.show("re-loading aborted (file error) !");
                    MessagePane.showErrorMessage("Could not re-open file.", ex);
                }
            }
        }
    }

    /**
     * Called to save in a file (in a new one if needed).
     */
    protected class SaveFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public SaveFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        /**
         * Save data to file.
         *
         * @return true if save was done, false if save was cancelled.
         */
        public boolean save() {
            StatusBar.show("saving file...");

            File file = null;

            // If the current data were never saved yet
            if (_file == null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save current calibrator list to file...");

                fileChooser.setFileFilter(_fileFilterRepository.get(_scvotMimeType));

                int returnVal = fileChooser.showSaveDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                } else {
                    // Save was cancelled
                    StatusBar.show("saving cancelled.");

                    return false;
                }
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null) {
                if (!canOverwriteFile(file)) {
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

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("SaveFileAction", "actionPerformed");

            save();
        }
    }

    /**
     * Called to save in a new files.
     */
    protected class SaveFileAsAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private FileFilterRepository _fileFilterRepository = FileFilterRepository.getInstance();
        private String _scvotMimeType = "application-x/searchcal-votable-file";

        public SaveFileAsAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("SaveFileAsAction", "actionPerformed");

            StatusBar.show("saving file as...");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save current calibrator list to file...");

            fileChooser.setFileFilter(_fileFilterRepository.get(_scvotMimeType));

            int returnVal = fileChooser.showSaveDialog(null);

            File file = null;

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();

                if (!canOverwriteFile(file)) {
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
    protected class ExportToCSVFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public ExportToCSVFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("ExportToCSVFileAction", "actionPerformed");

            StatusBar.show("exporting as CSV...");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export current calibrator list to CSV file...");

            fileChooser.setFileFilter(_fileFilterRepository.get(_csvMimeType));

            int returnVal = fileChooser.showSaveDialog(null);

            File file = null;

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null) {
                if (!canOverwriteFile(file)) {
                    StatusBar.show("overwritting exported CSV cancelled...");

                    return;
                }
                _calibratorsModel.exportCurrentVOTableToCSV(file);

                StatusBar.show("calibrator list exported to CSV file.");
            } else {
                StatusBar.show("exporting as CSV cancelled.");
            }
        }
    }

    /**
     * Called to export current data to a HTML formatted file.
     */
    protected class ExportToHTMLFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public ExportToHTMLFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("ExportToHTMLFileAction", "actionPerformed");

            StatusBar.show("exporting as HTML...");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export current calibrator list to HTML file...");

            fileChooser.setFileFilter(_fileFilterRepository.get(_htmlMimeType));

            int returnVal = fileChooser.showSaveDialog(null);

            File file = null;

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }

            // If a file was defined (No cancel in the dialog)
            if (file != null) {
                if (!canOverwriteFile(file)) {
                    return;
                }

                _calibratorsModel.exportCurrentVOTableToHTML(file);
                StatusBar.show("calibrator list exported to HTML file.");
            } else {
                StatusBar.show("exporting as HTML cancelled.");
            }
        }
    }

    /**
     * Called to export current data as local VOTable to another SAMP application.
     */
    protected class ShareCalibratorsThroughSAMPAction extends SampCapabilityAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public ShareCalibratorsThroughSAMPAction(String classPath, String fieldName, SampCapability capability) {
            super(classPath, fieldName, capability);
            couldBeEnabled(false);
        }

        public Map<?, ?> composeMessage() {
            File file = null;
            try {
                file = File.createTempFile(SearchCalibrators.getSharedApplicationDataModel().getProgramName(), "scvot");
            } catch (IOException ex) {
                StatusBar.show("Could not share calibrators through SAMP.");

                if (_logger.isLoggable(Level.WARNING)) {
                    _logger.warning("Could not save calibrator list to temp file '" + file + "'.");
                }
                return null;
            }

            file.deleteOnExit();
            URI uri = file.toURI();

            // Save the current calibrator selection (or all visible ones if none selected) to file
            _calibratorsModel.saveSelectionAsVOTableFile(file);

            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("url", uri.toString());
            return parameters;
        }
    }

    /**
     * Get calibrator list as a raw VOTable from JMMC web service.
     */
    protected final class GetCalAction extends AbstractGetCalAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /* members */
        /** VirtualObservatory instance */
        private final VirtualObservatory _vo;

        /**
         * Constructor
         * @param vo VirtualObservatory instance
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        protected GetCalAction(final VirtualObservatory vo, final String classPath, final String fieldName) {
            super(classPath, fieldName, "Get Calibrators");
            
            this._vo = vo;
        }

        /**
         * Return whether the query has been launched or not.
         *
         * @return true if the query is already launched, false otherwise.
         */
        protected boolean isQueryLaunched() {
            return _vo.isQueryLaunched();
        }

         /**
         * Return all current properties as an MCS-like sclsvr-formatted query.
         *
         * @return the query as a string.
         */
        protected String getQueryAsMCSString() {
            return _queryModel.getQueryAsMCSString();
        }

         /**
         * Set whether the query has been launched or not using Swing EDT.
         *
         * @param running true to enable all menus, false otherwise.
         */
        protected void setQueryLaunchedState(final boolean running) {
            _vo.setQueryLaunchedState(running);
        }

        /**
         * Define the query progression
         * @param catalogName current catalog name
         * @param catalogIndex current catalog index
         * @param nbOfCatalogs total number of catalogs
         */
        protected void setQueryProgress(final String catalogName, final int catalogIndex, final int nbOfCatalogs) {
            // Use invokeLater to avoid concurrency and ensure that
            // data model is modified and fire events using Swing EDT :
            SwingUtilities.invokeLater(new Runnable() {

              public void run() {
                  // Update the query model accordingly
                  _queryModel.setCatalogName(catalogName);
                  _queryModel.setCurrentStep(catalogIndex);
                  _queryModel.setTotalStep(nbOfCatalogs);
              }
            });          
        }

        /**
         * Parse the received VOTable
         * @param votable VOTable
         */
        protected void parseResults(final String votable) {
            if (votable != null && votable.length() > 0) {

                // Use invokeLater to avoid concurrency and ensure that
                // data model is modified and fire events using Swing EDT :
                SwingUtilities.invokeLater(new Runnable() {

                    /**
                     * Update the GUI using SearchCal results (VOTable)
                     */
                    public void run() {
                      // Parse the received VOTable
                      try {
                        StatusBar.show("parsing calibrators... (please wait, this may take a while)");

                        // Parse the received VOTable
                        // TODO : refine the possible exceptions in parseVOTable(string):
                        _calibratorsModel.parseVOTable(votable);

                        StatusBar.show("searching calibrators... done.");

                        // As data are now loaded
                        _vo.enableDataRelatedMenus(true);

                      } catch (Exception e) {
                        StatusBar.show("calibrator parsing aborted !");
                        MessagePane.showErrorMessage("Calibrator search failed (invalid VOTable received).", e);
                      }
                    }
                }); // EDT Task          
          
            } else {
              _logger.fine("No calibrators found.");
              StatusBar.show("no calibrators found.");
            }
        }

        /**
         * Display the given message to the user (status bar)
         * @param message message to show
         */
        protected void showStatus(final String message) {
          StatusBar.show(message);
        }

        /**
         * Show an error with the given message plus the exception message (if any) using EDT if needed
         * and log the exception
         * @param message message to display
         * @param th exception to use
         */
        protected void showErrorMessage(final String message, final Throwable th) {
          MessagePane.showErrorMessage(message, th);
        }

    }        
}
/*___oOo___*/
