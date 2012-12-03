/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.vo;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.RecentFilesManager;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.MessagePane.ConfirmSaveChanges;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampCapabilityAction;
import fr.jmmc.jmcs.util.MimeType;
import fr.jmmc.sclgui.SearchCal;
import fr.jmmc.sclgui.calibrator.CalibratorsModel;
import fr.jmmc.sclgui.query.QueryModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle JMMC WebServices interactions and file input/output.
 */
public final class VirtualObservatory extends Observable {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(VirtualObservatory.class.getName());
    /** Query model */
    private final QueryModel _queryModel;
    /** Data model to which the result should be passed */
    private final CalibratorsModel _calibratorsModel;
    /** Path to an open or saved file */
    private File _currentFile;
    /** Store whether the Query has be launched or not */
    private final AtomicBoolean _queryIsLaunched;
    /** Open file... action */
    private final OpenFileAction _openFileAction;
    /** Save file... action */
    private final SaveFileAction _saveFileAction;
    /** Save file as... action */
    private final SaveFileAsAction _saveFileAsAction;
    /** Revet to Saved File action */
    private final RevertToSavedFileAction _revertToSavedFileAction;
    /** Export to CSV File action */
    private final ExportToCSVFileAction _exportToCSVFileAction;
    /** Export to HTML File action */
    private final ExportToHTMLFileAction _exportToHTMLFileAction;
    /** Export to SAMP action */
    private final ShareCalibratorsThroughSAMPAction _shareCalibratorsThroughSAMPAction;
    /** Get Cal action */
    public final GetCalAction _getCalAction;

    public void enableGetCalAction(boolean b) {
        _getCalAction.setEnabled(b);
    }

    public void couldEnableShareCalibratorsThroughSAMPAction(boolean shouldBeEnabled) {
        _shareCalibratorsThroughSAMPAction.couldBeEnabled(shouldBeEnabled);
    }

    public void setShareCalibratorsThroughSAMPActionText(String actionMenuText) {
        _shareCalibratorsThroughSAMPAction.setText(actionMenuText);
    }

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
    public VirtualObservatory(final QueryModel queryModel, final CalibratorsModel calibratorsModel) {

        _queryIsLaunched = new AtomicBoolean(false);

        _queryModel = queryModel;
        _calibratorsModel = calibratorsModel;
        _calibratorsModel.setVirtualObservatory(this);

        // File related members
        _currentFile = null;

        // actions:
        final String classPath = getClass().getName();
        _openFileAction = new OpenFileAction(classPath, "_openFileAction");
        _saveFileAction = new SaveFileAction(classPath, "_saveFileAction");
        _saveFileAsAction = new SaveFileAsAction(classPath, "_saveFileAsAction");
        _revertToSavedFileAction = new RevertToSavedFileAction(classPath, "_revertToSavedFileAction");
        _exportToCSVFileAction = new ExportToCSVFileAction(classPath, "_exportToCSVFileAction");
        _exportToHTMLFileAction = new ExportToHTMLFileAction(classPath, "_exportToHTMLFileAction");
        _shareCalibratorsThroughSAMPAction = new ShareCalibratorsThroughSAMPAction(classPath, "_shareCalibratorsThroughSAMPAction", SampCapability.LOAD_VO_TABLE);

        // Query related members
        _getCalAction = new GetCalAction(this, classPath, "_getCalAction");

        // Add handler to load science object coordinates
/* @TBD
         try {
         SampMessageHandler handler = new SampMessageHandler(SampCapability.POINT_COORDINATES) {
        
         public Map processCall(HubConnection c, String senderId, Message msg) {
         System.out.println("\tReceived '" + this.handledMType() + "' message from '" + senderId + "' : '" + msg + "'.");
        
         Double ra = SampUtils.decodeFloat((String) msg.getParam("ra"));
         if (ra == null)
         {
         _logger.warn("Could not read RA value from SAMP:" + this.handledMType() + ": ra = '" + ra + "'.");
         return null;
         }
         String raHMS = ALX.toHMS(ra);
         if (raHMS == null)
         {
         _logger.warn("Could not convert RA degree value '" + ra + "' to HMS.");
         return null;
         }
        
         Double dec = SampUtils.decodeFloat((String) msg.getParam("dec"));
         if (dec == null)
         {
         _logger.warn("Could not read DEC value from SAMP:" + this.handledMType() + ": dec = '" + dec + "'.");
         return null;
         }
         String decDMS = ALX.toDMS(dec);
         if (decDMS == null)
         {
         _logger.warn("Could not convert DEC degree value '" + dec + "' to DMS.");
         return null;
         }
         _queryModel.setScienceObjectRA(raHMS);
         _queryModel.setScienceObjectDEC(decDMS);
         _queryModel.update(null, null);
        
         return null;
         }
         };
         } catch (Exception ex) {
         _logger.error( "failure : ", e);
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
    public boolean isQueryLaunched() {
        return _queryIsLaunched.get();
    }

    /**
     * Set whether the query has been launched or not using Swing EDT.
     *
     * @param running true to enable all menus, false otherwise.
     */
    public void setQueryLaunchedState(final boolean running) {
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                _queryIsLaunched.set(running);

                String newActionName = "Cancel";

                if (!running) {
                    // Change button title to 'Get Calibrators'
                    newActionName = "Get Calibrators";
                    _queryModel.setQueryProgress("", 0, 0);
                }

                // Set new action name
                _getCalAction.putValue(Action.NAME, newActionName);

                setChanged();
                notifyObservers();
            }
        };

        // Use EDT to avoid concurrency and ensure that
        // data model is modified and fire events using Swing EDT :
        SwingUtils.invokeEDT(task);
    }

    /**
     * Enable or disable save/export/share menus.
     *
     * @param flag true to enable all menus, false otherwise.
     */
    public void enableDataRelatedMenus(final boolean flag) {
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
        // If there is no data change to save
        if (!_calibratorsModel.haveDataChanged()) {
            return true;
        }

        boolean canLostModifications = false;

        // If the data are NOT saved, handle it before loosing any results !!!
        // Ask the user if he wants to save modifications
        final ConfirmSaveChanges result = MessagePane.showConfirmSaveChangesBeforeClosing();

        // Handle user choice
        switch (result) {
            // If the user clicked the "Save" button
            case Save:
                // Save the current data if no cancel occured
                canLostModifications = saveCalibratorListToFile();
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

        return canLostModifications;
    }

    /**
     * Launch 'sclws' web service querying with the given query (for ASPRO instanciation).
     *
     * @param query the query parameters as an empty QuerySearchCal VOTable.
     */
    public void executeQuery(final String query) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Received query = " + query);
        }

        StatusBar.show("parsing query...");

        // Parse the query     
        try {
            // parse VOTable and start query (async):
            _calibratorsModel.parseVOTable(query, true);
        } catch (IllegalArgumentException iae) {
            StatusBar.show("calibrator search aborted (could not parse query) !");
            MessagePane.showErrorMessage("Could not parse query.", iae);
        }
    }

    /**
     * Invokes the GetCal action
     */
    public void executeGetCal() {
        _getCalAction.actionPerformed(null);
    }

    /**
     * Save data to file.
     *
     * @return true if save was done, false if save was canceled.
     */
    public boolean saveCalibratorListToFile() {

        File selectedFile = null;

        // If the current data were never saved yet
        if (_currentFile == null) {
            selectedFile = FileChooser.showSaveFileChooser("Save current calibrator list to file...", null, MimeType.SEARCHCAL_CALIBRATORLIST, null);
        }

        // If a file was NOT defined in the dialog
        if (selectedFile == null) {
            return false; // Exit immediatly
        }

        StatusBar.show("saving file...");

        // save data to file and remember its path
        _calibratorsModel.saveVOTableFile(selectedFile);
        _currentFile = selectedFile;

        // Now that a file has been saved
        RecentFilesManager.addFile(_currentFile);
        _revertToSavedFileAction.setEnabled(true);

        return true;
    }

    public void exportToFileFormat(MimeType mimeType) {
        final String mimeTypeName = mimeType.getName();
        StatusBar.show("exporting as " + mimeTypeName + "...");

        File selectedFile = FileChooser.showSaveFileChooser("Export current calibrator list to " + mimeTypeName + " file...", null, mimeType, null);

        // If a file was NOT defined in the dialog
        if (selectedFile == null) {
            StatusBar.show("exporting as " + mimeTypeName + " cancelled.");
            return; // Exit immediatly
        }

        if (mimeType == MimeType.CSV) {
            _calibratorsModel.exportCurrentVOTableToCSV(selectedFile);
        } else if (mimeType == MimeType.HTML) {
            _calibratorsModel.exportCurrentVOTableToHTML(selectedFile);
        } else {
            _logger.warn("Unknown export mime type '{}'.", mimeTypeName);
        }

        StatusBar.show("calibrator list exported to " + mimeTypeName + " file.");
    }

    /**
     * Called to open files.
     */
    protected class OpenFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        OpenFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            flagAsOpenAction();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // If we can lost current modifications
            if (canLostModifications()) {
                // If the action was automatically triggered from App launch
                if (e.getSource() == ActionRegistrar.getInstance()) {
                    _currentFile = new File(e.getActionCommand());
                } else {
                    _currentFile = FileChooser.showOpenFileChooser("Open a calibrator list from file...", null, MimeType.SEARCHCAL_CALIBRATORLIST, null);
                }

                // If a file was defined (No cancel in the dialog)
                if (_currentFile != null) {
                    StatusBar.show("loading file...");

                    // Loading the file in the calibrators model
                    String errorMsg = null;
                    Exception ex = null;
                    try {
                        StatusBar.show("loading file (parsing calibrators)...");

                        _calibratorsModel.openFile(_currentFile);

                    } catch (IOException ioe) {
                        errorMsg = "Could not load the file : " + _currentFile.getAbsolutePath();
                        ex = ioe;

                    } catch (IllegalArgumentException iae) {
                        errorMsg = "Loading aborted: calibrators parsing error in file : " + _currentFile.getAbsolutePath();
                        ex = iae;
                    }

                    // Exit if any error occurs 
                    // TODO clean query and table content ?
                    if (ex != null) {
                        _currentFile = null;
                        StatusBar.show(errorMsg);
                        MessagePane.showErrorMessage(errorMsg, ex);
                        return;
                    }

                    // Now that a file has been loaded
                    RecentFilesManager.addFile(_currentFile);
                    _revertToSavedFileAction.setEnabled(true);
                }
            }
        }
    }

    /**
     * Called to revert the current state to the last saved state.
     */
    protected class RevertToSavedFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        RevertToSavedFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (_currentFile != null) {
                // If we can lost current modifications
                if (canLostModifications()) {
                    // Loading a new file
                    try {
                        StatusBar.show("re-loading file...");

                        _calibratorsModel.openFile(_currentFile);

                        StatusBar.show("file succesfully re-loaded.");
                    } catch (IOException ioe) {
                        String errorMsg = "Could not reload the file : " + _currentFile.getAbsolutePath();
                        StatusBar.show(errorMsg);
                        MessagePane.showErrorMessage(errorMsg, ioe);
                    } catch (IllegalArgumentException iae) {
                        String errorMsg = "ReLoading aborted:  calibrators parsing error in file : " + _currentFile.getAbsolutePath();
                        StatusBar.show(errorMsg);
                        MessagePane.showErrorMessage(errorMsg, iae);
                    }
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

        SaveFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (saveCalibratorListToFile()) {
                StatusBar.show("file succesfully saved.");
            }
        }
    }

    /**
     * Called to save in a new files.
     */
    protected class SaveFileAsAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        SaveFileAsAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            _currentFile = null; // reset previously remembered file (if any)
            StatusBar.show("saving file as...");
            if (saveCalibratorListToFile()) {
                StatusBar.show("file succesfully saved.");
            }
        }
    }

    /**
     * Called to export current data to a CSV formatted file.
     */
    protected class ExportToCSVFileAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        ExportToCSVFileAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            exportToFileFormat(MimeType.CSV);
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

        @Override
        public void actionPerformed(ActionEvent e) {
            exportToFileFormat(MimeType.HTML);
        }
    }

    /**
     * Called to export current data as local VOTable to another SAMP application.
     */
    protected class ShareCalibratorsThroughSAMPAction extends SampCapabilityAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        ShareCalibratorsThroughSAMPAction(String classPath, String fieldName, SampCapability capability) {
            super(classPath, fieldName, capability);
            couldBeEnabled(false);
        }

        @Override
        public Map<?, ?> composeMessage() {
            File file = null;
            try {
                file = File.createTempFile(SearchCal.getSharedApplicationDataModel().getProgramName(), "scvot");
            } catch (IOException ex) {
                StatusBar.show("Could not share calibrators through SAMP.");

                _logger.warn("Could not save calibrator list to temp file '{}'.", file);
                return null;
            }

            file.deleteOnExit();
            URI uri = file.toURI();

            // Save the current calibrator selection (or all visible ones if none selected) to file
            _calibratorsModel.saveSelectionAsVOTableFile(file);

            final Map<String, String> parameters = new HashMap<String, String>(4);
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
        @Override
        protected boolean isQueryLaunched() {
            return _vo.isQueryLaunched();
        }

        /**
         * Return all current properties as an MCS-like sclsvr-formatted query.
         *
         * @return the query as a string.
         */
        @Override
        protected String getQueryAsMCSString() {
            return _queryModel.getQueryAsMCSString();
        }

        /**
         * Set whether the query has been launched or not using Swing EDT.
         *
         * @param running true to enable all menus, false otherwise.
         */
        @Override
        protected void setQueryLaunchedState(final boolean running) {
            _vo.setQueryLaunchedState(running);
        }

        /**
         * Define the query progression
         * @param catalogName current catalog name
         * @param catalogIndex current catalog index
         * @param nbOfCatalogs total number of catalogs
         */
        @Override
        protected void setQueryProgress(final String catalogName, final int catalogIndex, final int nbOfCatalogs) {
            // Use invokeLater to avoid concurrency and ensure that
            // data model is modified and fire events using Swing EDT :
            SwingUtils.invokeLaterEDT(new Runnable() {
                @Override
                public void run() {
                    // Update the query model accordingly
                    _queryModel.setQueryProgress(catalogName, catalogIndex, nbOfCatalogs);
                }
            });
        }

        /**
         * Parse the received VOTable
         * @param votable VOTable
         */
        @Override
        protected void parseResults(final String votable) {
            if (votable != null && votable.length() != 0) {

                // Use invokeLater to avoid concurrency and ensure that
                // data model is modified and fire events using Swing EDT :
                SwingUtils.invokeLaterEDT(new Runnable() {
                    /**
                     * Update the GUI using SearchCal results (VOTable)
                     */
                    @Override
                    public void run() {
                        // Parse the received VOTable
                        try {
                            StatusBar.show("parsing calibrators... (please wait, this may take a while)");

                            // Parse the received VOTable (async):
                            _calibratorsModel.parseVOTable(votable, false);

                            StatusBar.show("searching calibrators... done.");

                        } catch (IllegalArgumentException iae) {
                            StatusBar.show("calibrator parsing aborted !");
                            MessagePane.showErrorMessage("Calibrator search failed (invalid VOTable received).", iae);
                        }
                    }
                }); // EDT Task          

            } else {
                _logger.debug("No calibrators found.");
                StatusBar.show("no calibrators found.");
            }
        }

        /**
         * Display the given message to the user (status bar)
         * @param message message to show
         */
        @Override
        protected void showStatus(final String message) {
            StatusBar.show(message);
        }

        /**
         * Show an error with the given message plus the exception message (if any) using EDT if needed
         * and log the exception
         * @param message message to display
         * @param th exception to use
         */
        @Override
        protected void showErrorMessage(final String message, final Throwable th) {
            MessagePane.showErrorMessage(message, th);
        }
    }
}
/*___oOo___*/
