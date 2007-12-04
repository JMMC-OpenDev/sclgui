/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VirtualObservatory.java,v 1.25 2007-12-04 11:00:50 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

    /** Store wether the CDS is queried or not */
    private boolean _CDSIsQueried = false;

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
        setCDSQueringState(false);
    }

    /**
     * Return whether the virtual observatory is busy or not.
     *
     * @return true if the VO is busy querying either CDS or JMMC, false otherwise.
     */
    protected synchronized boolean isBusy()
    {
        MCSLogger.trace();

        return (isQueryLaunched() || isCDSQueried());
    }

    /**
     * Return whether the CDS is accessed or not or not.
     *
     * @return true if the CDS is accessed, false otherwise.
     */
    protected synchronized boolean isCDSQueried()
    {
        MCSLogger.trace();

        return _CDSIsQueried;
    }

    /**
     * Set whether the CDS is accessed or not.
     *
     * @param flag true to enable all menus, false otherwise.
     */
    protected synchronized void setCDSQueringState(boolean flag)
    {
        MCSLogger.trace();

        _CDSIsQueried = flag;

        if (_CDSIsQueried == true)
        {
            // Change button title to 'Cancel'
            _getStarAction.putValue(Action.NAME, "Cancel");
        }
        else
        {
            // Change button title to 'Get Calibrators'
            _getStarAction.putValue(Action.NAME, "Get Star");
        }

        setChanged();
        notifyObservers();
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
            StatusBar.show(
                "calibrator search aborted (could not parse query) !");
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
                        StatusBar.show("loading file...");

                        // Loading the file in the calibrators model
                        try
                        {
                            StatusBar.show(
                                "loading file (parsing calibrators)...");
                            _calibratorsModel.openFile(_file);
                            StatusBar.show(
                                "loading file (calibrators successfully parsed)...");
                        }
                        catch (Exception ex)
                        {
                            StatusBar.show(
                                "loading aborted (calibrators parsing error) !");
                            MCSLogger.error(
                                "Could not open file (calibrators parsing error) : " +
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
                            MCSLogger.error(
                                "Could not open file (query parsing error) : " +
                                ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not open file (query parsing error).",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        StatusBar.show("file succesfully loaded.");

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
        GetStarThread _getStarThread = null;

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
            /*
               _queryModel.reset();
               try
               {
                   _queryModel.loadDefaultValues();
               }
               catch (Exception ex)
               {
                   MCSLogger.error("GetStar error : " + ex);
               }
             */

            // Launch a new thread only if no other one has been launched yet
            if (isCDSQueried() == false)
            {
                // Query is stating
                setCDSQueringState(true);

                StatusBar.show(
                    "searching science object... (please wait, this may take a while)");

                // Launch the query in the background in order to keed GUI updated
                _getStarThread = new GetStarThread();
                _getStarThread.start();
            }
            else
            {
                StatusBar.show("cancelling science object search...");

                // If the thread has already been launched
                if (_getStarThread != null)
                {
                    // Kill it
                    MCSLogger.debug("Killing GetStar thread ... ");
                    _getStarThread.interrupt();
                    _getStarThread = null;
                    MCSLogger.debug("GetStar thread killed.");
                }

                // Query is finished
                setCDSQueringState(false);

                StatusBar.show("science object search cancelled.");
            }
        }

        class GetStarThread extends Thread
        {
            String _result;

            GetStarThread()
            {
            }

            public void run()
            {
                simbadResult();
            }

            public void simbadResult()
            {
                // Re-initializing the result
                _result = "";

                // Ask CDS Simbad for our science object (if any) properties
                String id = _queryModel.getScienceObjectName();

                if (id.length() != 0)
                {
                    // Simbad URL
                    String simbadBaseURL = "http://simbad.u-strasbg.fr/simbad/sim-script?script=";

                    // The script to execute
                    String simbadScript = "output console=off script=off\n"; // Just data
                    simbadScript += "votable {"; // Desired VOTable format definition start
                    simbadScript += "ra,"; // RA value
                    simbadScript += "dec,"; // DEC Value
                    simbadScript += "flux(V),"; // V magnitude value
                    simbadScript += "flux(I),"; // I magnitude value
                    simbadScript += "flux(J),"; // J magnitude value
                    simbadScript += "flux(H),"; // H magnitude value
                    simbadScript += "flux(K)"; // K magnitude value
                    simbadScript += "}\n"; // Desired VOTable format definition end
                    simbadScript += "votable open\n"; // Set our VOTable as the desired output format
                    simbadScript += "set frame FK5\n"; // Set the FK5 frame as coord system
                    simbadScript += ("query id " + id); // Add the object name we are looking for

                    // Getting the result
                    try
                    {
                        // Forging the URL int UTF8 unicode charset
                        String simbadURL = simbadBaseURL +
                            URLEncoder.encode(simbadScript, "UTF-8");
                        URL    url       = new URL(simbadURL);

                        MCSLogger.debug("simbadURL : " + simbadURL);

                        // Launching the query
                        BufferedReader rdr = new BufferedReader(new InputStreamReader(
                                    url.openStream()));

                        // Reading the result line by line
                        String currentLine;

                        while ((currentLine = rdr.readLine()) != null)
                        {
                            if (_result.length() > 0)
                            {
                                _result += "\n";
                            }

                            _result += currentLine;
                        }
                    }
                    catch (Exception ex)
                    {
                        // Handle error when no manual cancel
                        if (_getStarThread != null)
                        {
                            StatusBar.show(
                                "science object search aborted (connection refused) !");
                            MCSLogger.error("CDS Connection failed : " + ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not connect to CDS Simbad server.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                            setCDSQueringState(false);
                        }
                        else
                        {
                            MCSLogger.debug("Silenced error (cancellation) : " +
                                ex);
                        }

                        return;
                    }

                    MCSLogger.debug("VOTable :\n" + _result);

                    // Parsing the result
                    try
                    {
                        // If the result srting is empty
                        if (_result.length() < 1)
                        {
                            throw new Exception("SIMBAD Empty result");
                        }

                        // If there was an error during query
                        if (_result.startsWith("::error"))
                        {
                            throw new Exception("SIMBAD Error result");
                        }

                        // Giving the result to _queryModel for parsing
                        _queryModel.loadFromSimbadVOTable(_result);
                    }
                    catch (Exception ex)
                    {
                        // Handle error when no manual cancel
                        if (_getStarThread != null)
                        {
                            StatusBar.show(
                                "science object search aborted (parsing error) !");
                            MCSLogger.error("CDS Result parsing failed : " +
                                ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not parse to CDS Simbad result.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                            setCDSQueringState(false);
                        }
                        else
                        {
                            MCSLogger.debug("Silenced error (cancellation) : " +
                                ex);
                        }

                        return;
                    }
                }
                else
                {
                    //@TODO : Assertion - should never receive an empty scence object name
                    MCSLogger.error(
                        "Received an empty scence object name for CDS Sibad search");
                }

                setCDSQueringState(false);
            }

            /*
               // Not in use (do not work from here)
               public void SOAPsimbadResult()
               {
                   WSQueryInterface myv = null;
                   // CDS Simbad connection
                   try
                   {
                       // Locator creation
                       System.out.print("Creating locator ... ");
                       WSQueryInterfaceService locator = new WSQueryInterfaceServiceLocator();
                       System.out.println("OK.");
                       //@TODO : try catch this -> opening
                       // Sesame object
                       System.out.print("Getting WSQuery ... ");
                       myv = locator.getWSQuery();
                       System.out.println("OK.");
                       // Define the webservice timeout (default = ???min)
                       org.apache.axis.client.Stub stub = (WSQuerySoapBindingStub) myv;
                       stub.setTimeout(15000); // 15 second, in miliseconds
                   }
                   catch (Exception ex)
                   {
                       // Handle error when no manual cancel
                       if (_getStarThread != null)
                       {
                           StatusBar.show(
                               "science object search aborted (connection refused) !");
                           MCSLogger.error("CDS Connection failed : " + ex);
                           JOptionPane.showMessageDialog(null,
                               "Could not connect to CDS Simbad server.", "Error",
                               JOptionPane.ERROR_MESSAGE);
                           setCDSQueringState(false);
                       }
                       else
                       {
                           MCSLogger.debug("Silenced error (cancellation) : " +
                               ex);
                       }
                       return;
                   }
                   // Ask CDS Simbad for our science object (if any) properties
                   String id = _queryModel.getScienceObjectName();
                   if (id.length() != 0)
                   {
                       try
                       {
                           String fields = "ra dec flux(V) flux(I) flux(J) flux(H) flux(k)"; // Fields needed.
                           String format = "vo"; // XML result.
                           System.out.print("Getting queryObjectById('" + id +
                               "', '" + fields + "', '" + format + "') ... ");
                           String result = myv.queryObjectById(id, fields, format);
                           System.out.println("OK.");
                           System.out.println("\n\n\n\n\nResult = '" + result +
                               "'.\n\n\n\n\n\n\n");
                           // @TODO : _queryModel.setCDSSimbadResult(_getStarThread.getResult());
                       }
                       catch (Exception ex)
                       {
                           // Handle error when no manual cancel
                           if (_getStarThread != null)
                           {
                               System.out.println("KO.");
                               StatusBar.show(
                                   "science object search aborted (communication failed) !");
                               MCSLogger.error("CDS communication failed : " + ex);
                               JOptionPane.showMessageDialog(null,
                                   "Could not connect to CDS server.", "Error",
                                   JOptionPane.ERROR_MESSAGE);
                               System.out.println("GetStar error : " + ex);
                           }
                           else
                           {
                               MCSLogger.debug("Silenced error (cancellation) : " +
                                   ex);
                           }
                       }
                   }
                   else
                   {
                       //@TODO : Assertion - should never receive an empty scence object name
                       MCSLogger.error(
                           "Received an empty scence object name for CDS Sibad search");
                   }
                   setCDSQueringState(false);
               }
             */
            String getResult()
            {
                return _result;
            }
        }
    }

    /**
     * Get calibrator list as a raw VOTable from JMMC web service.
     */
    protected class GetCalAction extends MCSAction
    {
        GetCalThread _getCalThread         = null;

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
                    MCSLogger.debug("Killing GetCal thread ... ");
                    _getCalThread.interrupt();
                    _getCalThread = null;
                    MCSLogger.debug("GetCal thread killed.");
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
                getCal();
            }

            public void getCal()
            {
                try
                {
                    // Get the connection ID
                    try
                    {
                        // Start the webservice connection
                        loc       = new SclwsLocator();
                        sclws     = loc.getsclws();

                        // Define the webservice timeout (default = 10min)
                        //org.apache.axis.client.Stub stub = (SclwsStub) s;
                        //stub.setTimeout(20000); // 20 second, in miliseconds

                        // Get the connection ID
                        id        = sclws.getCalOpenSession();

                        MCSLogger.test("JMMC Connection ID = '" + id + "'.");
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
                            MCSLogger.error("Connection failed : " + ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not connect to JMMC server.", "Error",
                                JOptionPane.ERROR_MESSAGE);

                            interrupt();
                            setQueryLaunchedState(false);
                        }
                        else
                        {
                            MCSLogger.debug("Silenced error (cancellation) : " +
                                ex);
                        }

                        return;
                    }

                    // Get the query from the GUI
                    String query = _queryModel.getQueryAsMCSString();
                    MCSLogger.test("Query = '" + query + "'.");

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

                            MCSLogger.test("Status = '" + currentCatalogName +
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
                                MCSLogger.error(
                                    "JMMC Status retrieving error : " + ex);

                                JOptionPane.showMessageDialog(null,
                                    "Communication with the JMMC server failed.",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                                interrupt();
                                setQueryLaunchedState(false);
                            }
                            else
                            {
                                MCSLogger.debug(
                                    "Silenced error (cancellation) : " + ex);
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
                            MCSLogger.error("Could not get JMMC result : " +
                                ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not get result from JMMC server.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                            interrupt();
                            setQueryLaunchedState(false);
                        }
                        else
                        {
                            MCSLogger.debug("Silenced error (cancellation) : " +
                                ex);
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
                                MCSLogger.error(
                                    "Could not parse received JMMC VOTable : " +
                                    ex);

                                JOptionPane.showMessageDialog(null,
                                    "Calibrator search failed (invalid VOTable received).",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                                interrupt();
                                setQueryLaunchedState(false);
                            }
                            else
                            {
                                MCSLogger.debug(
                                    "Silenced error (cancellation) : " + ex);
                            }

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
                    // Handle error when no manual cancel
                    if (_getCalThread != null)
                    {
                        StatusBar.show(
                            "calibrator search aborted (communication error) !");
                        MCSLogger.error(
                            "Could not communicate with JMMC server : " + ex);

                        JOptionPane.showMessageDialog(null,
                            "Communication failed.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        MCSLogger.debug("Silenced error (cancellation) : " +
                            ex);
                    }
                }

                // Query is finished
                setQueryLaunchedState(false);
            }

            public void interrupt()
            {
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
                    MCSLogger.error("Could not cancel JMMC request : " + ex);

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
                    MCSLogger.debug("Killing QueryResult thread ... ");
                    _queryResultThread.interrupt();
                    _queryResultThread = null;
                    MCSLogger.debug("QueryResult thread killed.");
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
                    queryResult();
                }

                public void queryResult()
                {
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
                            MCSLogger.error("Could not send JMMC query : " +
                                ex);

                            JOptionPane.showMessageDialog(null,
                                "Could not send query to JMMC server.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                            interrupt();
                            setQueryLaunchedState(false);
                        }
                        else
                        {
                            MCSLogger.debug("Silenced error (cancellation) : " +
                                ex);
                        }

                        return;
                    }
                }

                String getResult()
                {
                    return _result;
                }
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
