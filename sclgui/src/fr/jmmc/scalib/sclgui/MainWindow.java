/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainWindow.java,v 1.9 2006-07-18 13:08:39 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2006/07/12 15:50:29  lafrasse
 * Corrected package import to resolve a comilation problem with
 * jmmc.mcs.util.StatusBar
 *
 * Revision 1.7  2006/07/12 14:29:01  lafrasse
 * Code refinment
 * Added the status bar
 *
 * Revision 1.6  2006/06/30 11:53:17  mella
 * Change GUI presentation
 *
 * Revision 1.5  2006/06/26 14:33:32  mella
 * Place menu creation at end
 *
 * Revision 1.4  2006/06/19 11:23:57  mella
 * Add export capability and remove printing one
 *
 * Revision 1.3  2006/04/06 14:33:00  lafrasse
 * Moved all the main views in split panes
 *
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.StatusBar;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.net.*;

import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


/**
 * Main window. This class is at one central point and play the mediator role.
 */
public class MainWindow extends JFrame
{
    /**
     * Shared logging object
     * @TODO : replace this by MCSLogger
     */
    Logger _logger = MCSLogger.getLogger();

    /** Main panel container, displaying the query and result views */
    Container _mainPane;

    /** Main window menu */
    MainMenuBar _menuBar;

    /** Query view */
    QueryView _queryView;

    /** Calibrators view */
    CalibratorsView _calibratorsView;

    /** Preferences view */
    PreferencesView _preferencesView;

    /** Filters view */
    FiltersView _filtersView;

    /** Status bar */
    StatusBar _statusBar;

    /**
     * Constructor.
     */
    public MainWindow(QueryView queryView, CalibratorsView calibratorsView,
        PreferencesView preferencesView, FiltersView filtersView,
        StatusBar statusBar)
    {
        _queryView           = queryView;
        _calibratorsView     = calibratorsView;
        _preferencesView     = preferencesView;
        _filtersView         = filtersView;
        _statusBar           = statusBar;

        try
        {
            setTitle("SearchCal");

            _mainPane = getContentPane();
            _mainPane.setLayout(new BoxLayout(_mainPane, BoxLayout.Y_AXIS));

            // Create a first top-bottom split pane for calibrators and filters
            JSplitPane resultPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    _calibratorsView, _filtersView);
            // Give priority to calibrators view
            resultPane.setResizeWeight(1.0);

            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            resultPane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            resultPane.setOneTouchExpandable(true);

            // Create a second top-bottom split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    _queryView, resultPane);
            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            splitPane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            splitPane.setOneTouchExpandable(true);
            _mainPane.add(splitPane);

            // Add the Status bar
            _mainPane.add(statusBar);

            // Show all the GUI
            _queryView.setVisible(true);
            _calibratorsView.setVisible(true);
            _filtersView.setVisible(true);
            _statusBar.setVisible(true);

            // Menu must be created after Actions init.
            _menuBar = new MainMenuBar(this);
            setJMenuBar(_menuBar);

            // Set the GUI up
            pack();
            setVisible(true);

            // Show the user the app is ready to be used
            StatusBar.show("application ready.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Specify that the main should quit the application when closed
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Display the registered preferences view
     */
    public void showPreferencesView()
    {
        MCSLogger.trace();

        _preferencesView.setVisible(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param inFilename DOCUMENT ME!
     * @param outFilename DOCUMENT ME!
     * @param xslFilename DOCUMENT ME!
     */
    private void doXsl(URL inFilename, String outFilename, URL xslFilename)
    {
        MCSLogger.trace();
        _logger.fine("xsl='" + xslFilename + "', xml='" + inFilename +
            "', out='" + outFilename + "'");

        try
        {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource("" +
                        xslFilename));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource("" + inFilename);
            Result result = new StreamResult(new FileOutputStream(outFilename));

            // Apply the xsl file to the source file and write the result to
            // the output file
            xformer.transform(source, result);
        }
        catch (FileNotFoundException e)
        {
            _logger.severe("File not found '" + e + "'");
        }
        catch (TransformerConfigurationException e)
        {
            // An error occurred in the XSL file
            _logger.severe("One error occured into the xsl file '" +
                xslFilename + "'");
        }
        catch (TransformerException e)
        {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator  = e.getLocator();
            int           col      = locator.getColumnNumber();
            int           line     = locator.getLineNumber();
            String        publicId = locator.getPublicId();
            String        systemId = locator.getSystemId();
            _logger.severe("One error occured applying xsl (xsl='" +
                xslFilename + "', xml='" + inFilename + "' error on line " +
                line + " column " + col + ")");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param outputFilename DOCUMENT ME!
     */
    public void exportVOTableToCSV(String outputFilename)
    {
        MCSLogger.trace();

        URL xslURL = MainWindow.class.getResource("voTableToCSV.xsl");
        URL xmlURL = MainWindow.class.getResource("eta_tau.vot");
        doXsl(xmlURL, outputFilename, xslURL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param outputFilename DOCUMENT ME!
     */
    public void exportVOTableToHTML(String outputFilename)
    {
        MCSLogger.trace();

        URL xslURL = MainWindow.class.getResource("voTableToHTML.xsl");
        URL xmlURL = MainWindow.class.getResource("eta_tau.vot");
        doXsl(xmlURL, outputFilename, xslURL);
    }
}
/*___oOo___*/
