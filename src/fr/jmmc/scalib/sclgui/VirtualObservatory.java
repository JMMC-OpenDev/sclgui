/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VirtualObservatory.java,v 1.54 2011-04-01 14:53:10 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.53  2011/03/18 16:21:22  bourgesl
 * fixed Swing EDT violations in thread handling
 *
 * Revision 1.52  2011/02/28 13:11:36  mella
 * Handle proxy error without duplicating error messages (cancel), close ticket #38
 *
 * Revision 1.51  2011/01/24 16:18:29  lafrasse
 * Set default behavior as disabled.
 *
 * Revision 1.50  2011/01/24 11:22:41  lafrasse
 * Refactoring.
 *
 * Revision 1.49  2011/01/20 16:55:35  lafrasse
 * Fixed typos and minor issues.
 *
 * Revision 1.48  2011/01/20 15:03:19  mella
 * Change sampExport action to return the selected list in the table or the whole list if none is selected.
 *
 * Revision 1.47  2011/01/20 10:05:37  mella
 * Fix javadoc opy/paste
 *
 * Revision 1.46  2010/11/04 16:48:06  lafrasse
 * Changed communication channel (now through HTTP standard port 80) to better support tightly filtered public networks (WiFi, etc).
 *
 * Revision 1.45  2010/10/22 10:15:55  bourgesl
 * SAMP Message handler (Start query) modified : use EDT and give focus to searchCal frame
 *
 * Revision 1.44  2010/10/15 09:03:35  lafrasse
 * Back-ported the "File Overwriting ?" dialog box from canOverwriteFile() to JMCS::showConfirmFileOverwrite().
 * Refined class members definition and constructor javadoc.
 *
 * Revision 1.43  2010/10/11 14:15:29  bourgesl
 * SampMessageHandler refactoring
 *
 * Revision 1.42  2010/10/11 14:04:21  lafrasse
 * Replaced direct call to SWING for error messages with JMCS MessagePane (EDT safe).
 * Cleaned imports.
 *
 * Revision 1.41  2010/10/10 22:45:04  lafrasse
 * Code reformating.
 *
 * Revision 1.40  2010/10/10 22:21:04  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.39  2010/10/07 14:59:29  bourgesl
 * replaced System.out votable by logger.fine
 * removed unused catch exception when declaring a SampMessageHandler
 *
 * Revision 1.38  2010/10/05 14:58:37  bourgesl
 * fixed composeMessage signature
 *
 * Revision 1.37  2010/10/05 08:45:11  mella
 * Use SampCapability.SEARCHCAL_START_QUERY
 *
 * Revision 1.36  2010/10/04 23:44:39  lafrasse
 * Code refactoring and logging.
 *
 * Revision 1.35  2010/09/24 12:08:33  lafrasse
 * Added preliminary support for SAMP message sending and receiving.
 *
 * Revision 1.34  2010/01/29 13:17:19  lafrasse
 * Jalopization.
 *
 * Revision 1.33  2010/01/29 13:09:34  lafrasse
 * Updated to show the humaan-readable catalog name (when available) in the propgress bar while
 * querying.
 *
 * Revision 1.32  2009/10/23 15:55:53  lafrasse
 * Jalopization.
 *
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
 * Enabled JMMC QuerySearchCal SOAP webservice querying.
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

import fr.jmmc.mcs.astro.Catalog;
import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.MessagePane;

import fr.jmmc.mcs.gui.StatusBar;

import fr.jmmc.mcs.interop.SampCapability;
import fr.jmmc.mcs.interop.SampCapabilityAction;
import fr.jmmc.mcs.interop.SampMessageHandler;
import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mcs.util.FileFilterRepository;
import fr.jmmc.mcs.util.MCSExceptionHandler;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.scalib.sclgui.ws.SearchCalServerClient;
import fr.jmmc.sclws_wsdl.SclwsPortType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.axis.transport.http.AbortableCommonsHTTPSender;
import org.astrogrid.samp.Message;

/**
 * Handle JMMC WebServices interactions and file input/output.
 */
public final class VirtualObservatory extends Observable {

  /** Logger */
  private static final Logger _logger = Logger.getLogger(
          "fr.jmmc.scalib.sclgui.VirtualObservatory");
  /** Query model */
  private QueryModel _queryModel = null;
  /** Data model to which the result should be passed */
  private CalibratorsModel _calibratorsModel = null;
  /** Path to an open or saved file */
  private File _file = null;
  /** Store whether the Query has be launched or not */
  private boolean _queryIsLaunched = false;
  /** Proxy to shared FileFilter repository */
  private FileFilterRepository _fileFilterRepository = FileFilterRepository.getInstance();
  /** QuerySearchCal-specific VOTable format */
  private String _scvotMimeType = "application/x-searchcal+votable+xml";
  /** MIME type for CSV exports */
  private String _csvMimeType = "text/csv";
  /** MIME type for HTML exports */
  private String _htmlMimeType = "text/html";
  /** Open file... action */
  public OpenFileAction _openFileAction = null;
  /** Save file... action */
  public SaveFileAction _saveFileAction = null;
  /** Save file as... action */
  public SaveFileAsAction _saveFileAsAction = null;
  /** Revet to Saved File action */
  public RevertToSavedFileAction _revertToSavedFileAction = null;
  /** Export to CSV File action */
  public ExportToCSVFileAction _exportToCSVFileAction = null;
  /** Export to HTML File action */
  public ExportToHTMLFileAction _exportToHTMLFileAction = null;
  /** Export to SAMP action */
  public ShareCalibratorsThroughSAMPAction _shareCalibratorsThroughSAMPAction = null;
  /** Get Cal action */
  public GetCalAction _getCalAction = null;

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
    _getCalAction = new GetCalAction(classPath, "_getCalAction");

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
              executeQuery(query);

              // change focus :
              App.getFrame().toFront();
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
    ex.printStackTrace();
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

    return _queryIsLaunched;
  }

  /**
   * Set whether the query has been launched or not using Swing EDT.
   *
   * @param flag true to enable all menus, false otherwise.
   */
  protected void setQueryLaunchedState(final boolean flag) {
    _logger.entering("VirtualObservatory", "setQueryLaunchedState");

    final Runnable task = new Runnable() {

      public void run() {

        _queryIsLaunched = flag;

        final String actionName;
        if (flag) {
          // Change button title to 'Cancel'
          actionName = "Cancel";

        } else {
          // Change button title to 'Get Calibrators'
          actionName = "Get Calibrators";
          _queryModel.setCurrentStep(0);
          _queryModel.setTotalStep(0);
          _queryModel.setCatalogName("");
        }
        _getCalAction.putValue(Action.NAME, actionName);

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

    final Runnable task = new Runnable() {

      public void run() {
        _saveFileAction.setEnabled(flag);
        _saveFileAsAction.setEnabled(flag);
        _exportToCSVFileAction.setEnabled(flag);
        _exportToHTMLFileAction.setEnabled(flag);
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
      Object[] options = {"Save", "Cancel", "Don't Save"};
      int result = JOptionPane.showOptionDialog(null,
              "Do you want to save changes to this document before closing ?\nIf you don't save, your changes will be definitively lost.\n\n",
              null, JOptionPane.DEFAULT_OPTION,
              JOptionPane.WARNING_MESSAGE, null, options, options[0]);

      // Handle user choice
      switch (result) {
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
  public void executeQuery(String query) {
    _logger.entering("VirtualObservatory", "executeQuery");

    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("Received query = " + query);
    }

    // Parse the query
    try {
      StatusBar.show("parsing query...");

      _calibratorsModel.parseVOTable(query);
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
  protected final class GetCalAction extends RegisteredAction {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** http query thread */
    private GetCalThread _getCalThread = null;
    /** query count */
    private final AtomicInteger _queryCounter = new AtomicInteger(0);

    /**
     * Constructor
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     */
    public GetCalAction(final String classPath, final String fieldName) {
      super(classPath, fieldName, "Get Calibrators");

      setEnabled(false);
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      _logger.entering("GetCalAction", "actionPerformed");

      // Launch a new thread only if no other one has been launched yet
      if (!isQueryLaunched()) {
        // Query is starting
        setQueryLaunchedState(true);

        StatusBar.show("searching calibrators... (please wait, this may take a while)");

        // Get the query from the GUI
        final String query = _queryModel.getQueryAsMCSString();

        if (_logger.isLoggable(Level.FINE)) {
          _logger.fine("Query = '" + query + "'.");
        }

        // Launch the query in the background in order to keed GUI updated
        _getCalThread = new GetCalThread(_queryCounter.incrementAndGet(), query);

        // define UncaughtExceptionHandler :
        MCSExceptionHandler.installThreadHandler(_getCalThread);

        _getCalThread.start();

      } else {
        StatusBar.show("cancelling current calibrator search...");

        // If the GetCal thread has already been launched
        if (_getCalThread != null) {
          // Kill it
          _logger.fine("Killing GetCal thread ... ");

          // indicate that the user cancelled the query:
          _getCalThread.cancel(true);
          _getCalThread = null;

          _logger.fine("GetCal thread killed.");
        }

        // Query is finished
        setQueryLaunchedState(false);

        StatusBar.show("calibrator search cancelled.");
      }
    }

    /**
     * Return a thread name
     * @param name name prefix
     * @param number number to identify uniquely the thread
     * @return thread name : prefix + '-' + thread number
     */
    private final String getThreadName(final String name, final int number) {
      final StringBuilder sb = new StringBuilder(name).append('-');
      sb.append(number);
      return sb.toString();
    }

    /**
     * Wait for each CDS interrogation progress while another thread waits for the final response.
     */
    private final class GetCalThread extends Thread {

      /** query number */
      private final int _queryNumber;
      /** QuerySearchCal WebService client */
      private final SclwsPortType _sclws;
      /** QuerySearchCal query */
      private final String _query;
      /** QuerySearchCal Session ID */
      private String _id;
      /** flag to indicate that cancel is done */
      /* atomic stop flag used to indicate that cancel is done and coordinate state between threads */
      private final AtomicBoolean _cancel = new AtomicBoolean();
      /** flag to indicate an user cancellation */
      private boolean _userCancel = false;
      /** QuerySearchCal query thread */
      private QueryResultThread _queryResultThread = null;

      /**
       * Create a new QuerySearchCal Session thread (get Id, get statuses and waits for the final response)
       * @param queryNumber query number
       * @param query QuerySearchCal query
       */
      private GetCalThread(final int queryNumber, final String query) {
        super(getThreadName("GetCalThread", queryNumber));
        _queryNumber = queryNumber;
        _query = query;

        // Get one WS client:
        _sclws = SearchCalServerClient.getInstance().getSclWsClient();
      }

      @Override
      public void run() {
        _logger.entering("GetCalThread", "run");

        // current state used to report proper message if an exception is caught:
        QueryState currentState = QueryState.OpenSession;
        try {

          // Check if the query was cancelled:
          if (isCancelled()) {
            return;
          }

          // 1 - Get the connection ID :
          // this WS call can block if connection attempt fails :
          _id = _sclws.getCalOpenSession();

          if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("JMMC Connection ID = '" + _id + "'.");
          }

          StatusBar.show("searching calibrators... (connection established)");

          // 2 - Launch the querying thread
          _queryResultThread = new QueryResultThread(_queryNumber, _sclws, _id, _query);

          // define UncaughtExceptionHandler :
          MCSExceptionHandler.installThreadHandler(_queryResultThread);

          // Check if the query was cancelled:
          if (isCancelled()) {
            return;
          }

          _queryResultThread.start();

          StatusBar.show("searching calibrators... (querying catalogs)");

          // change the current state to QueryStatus:
          currentState = QueryState.QueryStatus;

          // 3 - GetCal status polling to update ProgressBar
          int requestStatus;
          do {

            // Check if the query was cancelled:
            if (isCancelled()) {
              return;
            }

            // Get query progression status
            // this WS call can block if connection attempt fails :
            final String currentStatus = _sclws.getCalQueryStatus(_id);

            // Deserialize the new status to update the GUI
            final String[] splittedStatus = currentStatus.split("\t");

            // Parse the received status
            int i = 0;
            requestStatus = Integer.parseInt(splittedStatus[i++]);

            final String currentCatalogName;
            final Integer catalogIndex;
            final Integer nbOfCatalogs;

            if (splittedStatus.length == 4) {
              // Get the catalog name
              currentCatalogName = Catalog.titleFromReference(splittedStatus[i++]);

              // Get the catalog index
              catalogIndex = Integer.valueOf(splittedStatus[i++]);

              // Get the total number of catalogs
              nbOfCatalogs = Integer.valueOf(splittedStatus[i++]);

              // Compose the dispalyed query status
              final String composedQueryStatus = currentCatalogName
                      + " - (" + catalogIndex + "/" + nbOfCatalogs
                      + ")";

              // Use invokeLater to avoid concurrency and ensure that
              // data model is modified and fire events using Swing EDT :
              SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                  // Update the query model accordingly
                  _queryModel.setCatalogName(composedQueryStatus);
                  _queryModel.setCurrentStep(catalogIndex.intValue());
                  _queryModel.setTotalStep(nbOfCatalogs.intValue());
                }
              });
            } else {
              currentCatalogName = "";
              catalogIndex = Integer.valueOf(0);
              nbOfCatalogs = Integer.valueOf(0);
            }

            if (_logger.isLoggable(Level.FINE)) {
              _logger.fine("Status = '" + currentCatalogName
                      + "' - " + catalogIndex + "/" + nbOfCatalogs
                      + " (status = '" + requestStatus + "').");
            }

          } while (requestStatus == 1);

          StatusBar.show("searching calibrators... (waiting for result)");

          // change the current state to QueryResult:
          currentState = QueryState.QueryResult;

          // Check if the query was cancelled:
          if (isCancelled()) {
            return;
          }

          // 4 - Wait for the query thread to finish:
          _queryResultThread.join();

          // Check if the query was cancelled:
          if (isCancelled()) {
            return;
          }

          // 5 - get results and parse them :
          final String result = _queryResultThread.getResult();

          // Use invokeLater to avoid concurrency and ensure that
          // data model is modified and fire events using Swing EDT :
          SwingUtilities.invokeLater(new Runnable() {

            /**
             * Update the GUI using SearchCal results (VOTable)
             */
            public void run() {
              if (result != null && result.length() > 0) {
                try {
                  StatusBar.show("parsing calibrators... (please wait, this may take a while)");

                  // Parse the received VOTable
                  // TODO : refine the possible exceptions in parseVOTable(string):
                  _calibratorsModel.parseVOTable(result);

                  StatusBar.show("searching calibrators... done.");

                  // As data are now loaded
                  enableDataRelatedMenus(true);

                } catch (Exception e) {
                  StatusBar.show("calibrator parsing aborted !");
                  MessagePane.showErrorMessage("Calibrator search failed (invalid VOTable received).", e);
                }
              } else {
                _logger.fine("No calibrators found.");
                StatusBar.show("no calibrators found.");
              }
            }
          }); // EDT Task

        } catch (NumberFormatException nfe) {
          // can occur when parsing status query (2)
          handleException(nfe, currentState);
        } catch (InterruptedException ie) {
          // can occur when thie thread wait for the query thread to finish (4)
          handleException(ie, currentState);
        } catch (RemoteException re) {
          // server or http failure
          handleException(re, currentState);
        } finally {
          if (_logger.isLoggable(Level.INFO)) {
            _logger.log(Level.INFO, getName() + " thread.run done.");
          }
        }

        // Query is finished
        setQueryLaunchedState(false);
      }

      /**
       * Handle exceptions coming from threads communicating with the SearchCal Server
       * @param e exception
       * @param state current state used to find user messages
       */
      private void handleException(final Exception e, final QueryState state) {
        // Handle error when no manual cancel
        if (isUserCancel() || isCancelled()) {
          // already handled:
          _logger.log(Level.FINE, "Silenced error (cancellation) : ", e);

          return;
        }

        // cancel queries now to change cancel barrier:
        cancel(false);

        final String status;
        final String message;
        switch (state) {
          default:
          case OpenSession:
            status = "calibrator search aborted (connection refused) !";
            message = "Could not connect to JMMC server.";
            break;

          case QueryStatus:
            status = "calibrator search aborted (catalog error) !";
            message = "Communication with the JMMC server failed.";
            break;

          case QuerySearchCal:
            status = "calibrator search aborted (could not send query) !";
            message = "Could not send query to JMMC server.";
            break;

          case QueryResult:
            status = "calibrator search aborted (could not get result) !";
            message = "Could not get result from JMMC server.";
        }

        // Use messages corresponding to state:
        StatusBar.show(status);

        // Exception is logged in MessagePane:
        MessagePane.showErrorMessage(message, e);

        // change the GUI state:
        setQueryLaunchedState(false);
      }

      /**
       * Cancel current requests
       * @param userCancel flag to indicate if the user cancelled requests
       */
      private void cancel(final boolean userCancel) {
        _logger.entering("GetCalThread", "cancel");

        /* avoid multiple cancel calls and indicate to threads to halt at the next
         * opportunity. */
        if (_cancel.compareAndSet(false, true)) {
          _userCancel = userCancel;

          if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("cancel: " + userCancel);
          }

          // First Cancel the request:
// Disabled cancel query because it crashes the SearchCal Server !
/*

          if (_id != null) {
          // Launch the cancel query thread to free resources on the server side:
          final CancelQueryThread cancelThread = new CancelQueryThread(_queryNumber, _sclws, _id);

          // define UncaughtExceptionHandler :
          MCSExceptionHandler.installThreadHandler(cancelThread);

          cancelThread.start();
          }
           */

          // If the QueryResult thread has already been launched
          if (_queryResultThread != null) {
            // interrupt it:

            _logger.fine("Killing QueryResult thread ... ");

            // interrupt the QueryResult thread:

            /* Interrupt the blocking thread.  This won't break out of a blocking
             * I/O request, but will break out of a wait or sleep call.  While in
             * this case we know that no such condition is possible, it is always a
             * good idea to include an interrupt to avoid assumptions about the
             * thread in question. */
            _queryResultThread.interrupt();

            // Close HTTP connection:
            AbortableCommonsHTTPSender.abort(_queryResultThread);

            _logger.fine("QueryResult thread killed.");
          }

          // interrupt this thread:

          /* Interrupt the blocking thread.  This won't break out of a blocking
           * I/O request, but will break out of a wait or sleep call.  While in
           * this case we know that no such condition is possible, it is always a
           * good idea to include an interrupt to avoid assumptions about the
           * thread in question. */
          this.interrupt();

          // Close HTTP connection:
          AbortableCommonsHTTPSender.abort(this);

          if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("cancel done.");
          }
        }
      }

      /**
       * Return true if the user cancelled the query
       * @return true if the user cancelled the query
       */
      private boolean isUserCancel() {
        return _userCancel;
      }

      /**
       * Return true if the query is cancelled
       * @return true if the query is cancelled
       */
      private boolean isCancelled() {
        return _cancel.get();
      }

      /**
       * Wait for the final reply from the server while another thread monitor CDS interrogation progress.
       */
      private final class QueryResultThread extends Thread {

        /** QuerySearchCal WebService client */
        private final SclwsPortType _sclws;
        /** QuerySearchCal Session ID */
        private final String _id;
        /** QuerySearchCal query */
        private final String _query;
        /** QuerySearchCal server response (VOTable) */
        private String _result = null;

        /**
         * Creates a new Thread to send query to the server
         * @param queryNumber query number
         * @param sclws QuerySearchCal WebService client
         * @param id QuerySearchCal Session ID
         * @param query QuerySearchCal query
         */
        private QueryResultThread(final int queryNumber, final SclwsPortType sclws, final String id, final String query) {
          super(getThreadName("QueryResultThread", queryNumber));
          _sclws = sclws;
          _query = query;
          _id = id;
        }

        /**
         * Runs the query i.e. calls getCalSearchCal(_id, _query)
         */
        @Override
        public void run() {
          _logger.entering("QueryResultThread", "run");

          try {
            StatusBar.show("searching calibrators... (sending query)");

            // Check if the query was cancelled:
            if (isCancelled()) {
              return;
            }

            // Launch the query
            final long start = System.nanoTime();

            // this WS call can block if connection attempt fails :
            _result = _sclws.getCalSearchCal(_id, _query);

            if (_logger.isLoggable(Level.INFO)) {
              _logger.info(getName() + " duration = " + 1e-9d * (System.nanoTime() - start) + " s.");
            }

            StatusBar.show("searching calibrators... (result received)");

          } catch (RemoteException re) {
            // server or http failure
            handleException(re, QueryState.QuerySearchCal);
          } finally {
            if (_logger.isLoggable(Level.INFO)) {
              _logger.log(Level.INFO, getName() + " thread.run done.");
            }
          }
        }

        /**
         * Return the query result
         * @return result or null if interrupted or cancelled
         */
        private String getResult() {
          _logger.entering("QueryResultThread", "getResult");

          return _result;
        }
      }

      /**
       * Send cancel message to the server asynchronously
       * 
       * UNUSED until server handles properly getCalCancelSession() i.e. stable (no more crash)
       */
      private final class CancelQueryThread extends Thread {

        /** QuerySearchCal WebService client */
        private final SclwsPortType _sclws;
        /** QuerySearchCal Session ID */
        private final String _id;

        /**
         * Creates a new Thread to send cancel message to the server asynchronously
         * @param queryNumber query number
         * @param sclws QuerySearchCal WebService client
         * @param id QuerySearchCal Session ID
         */
        private CancelQueryThread(final int queryNumber, final SclwsPortType sclws, final String id) {
          super(getThreadName("CancelQueryThread", queryNumber));
          _sclws = sclws;
          _id = id;
        }

        /**
         * Send the cancel query i.e. calls getCalCancelSession(id)
         */
        @Override
        public void run() {
          _logger.entering("CancelQueryThread", "run");

          try {

            // Ask for query cancellation:
            // 1- free resources on the server side
            // 2- remove id from active identifiers
            // note: next calls using this id will fail.

            if (_logger.isLoggable(Level.INFO)) {
              _logger.log(Level.INFO, getName() + " cancel session with id = " + _id);
            }

            // this WS call can block if connection attempt fails :
            _sclws.getCalCancelSession(_id);

          } catch (RemoteException re) {
            // do not report this failure to the user, only log the exception:
            if (_logger.isLoggable(Level.WARNING)) {
              _logger.log(Level.WARNING, "cancel request failed : ", re);
            }

          } finally {
            if (_logger.isLoggable(Level.INFO)) {
              _logger.log(Level.INFO, getName() + " thread.run done.");
            }
          }
        }
      }
    }
  }
}
/*___oOo___*/
