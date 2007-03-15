/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainWindow.java,v 1.17 2007-02-16 15:19:55 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.16  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.15  2006/11/27 15:01:50  lafrasse
 * Moved PlotInAladin action from MainWindow to VirtualObservatory in order to
 * ensure menu ennabling/disabling.
 *
 * Revision 1.14  2006/11/18 23:21:10  lafrasse
 * Handled SCAction change to MCSAction and StatusBar move.
 * Added AboutWindow and HelpView support, plus actions for ShowPreferencesAction,
 * PageSetupAction, PrintAction, PlotInAladinAction and AboutAction.
 *
 * Revision 1.13  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.12  2006/10/16 14:29:51  lafrasse
 * Updated to reflect MCSLogger API changes.
 *
 * Revision 1.11  2006/08/04 16:53:54  lafrasse
 * Re-added preliminary print support
 *
 * Revision 1.10  2006/07/26 13:30:32  mella
 * Use BorderLayout to educate status position
 *
 * Revision 1.9  2006/07/18 13:08:39  lafrasse
 * Jalopyzation
 *
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
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.gui.*;
import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.net.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;


/**
 * Main window. This class is at one central point and play the mediator role.
 */
public class MainWindow extends JFrame
{
    /** Main panel container, displaying the query and result views */
    Container _mainPane;

    /** Main window menu */
    MainMenuBar _menuBar;

    /** Virtual Observatory */
    public VirtualObservatory _vo;

    /** Query view */
    public QueryView _queryView;

    /** Calibrators view */
    public CalibratorsView _calibratorsView;

    /** Filters view */
    public FiltersView _filtersView;

    /** Status bar */
    StatusBar _statusBar;

    /** Preferences view */
    PreferencesView _preferencesView;

    /** Help view */
    HelpView _helpView;

    /** Preferences... action */
    public ShowPreferencesAction _showPreferencesAction;

    /** Page Setup... action */
    public PageSetupAction _pageSetupAction;

    /** Print... action */
    public PrintAction _printAction;

    /** Printer job */
    PrinterJob _printJob;

    /** PAge format */
    PageFormat _landscape;

    /** About window action */
    public AboutAction _aboutAction;

    /**
     * Constructor.
     */
    public MainWindow(VirtualObservatory vo, QueryView queryView,
        CalibratorsView calibratorsView, PreferencesView preferencesView,
        FiltersView filtersView, StatusBar statusBar)
    {
        _vo                        = vo;
        _queryView                 = queryView;
        _calibratorsView           = calibratorsView;
        _filtersView               = filtersView;
        _statusBar                 = statusBar;

        _helpView                  = new HelpView();

        // Preferences
        _preferencesView           = preferencesView;
        _showPreferencesAction     = new ShowPreferencesAction();

        // Printing
        _printJob                  = PrinterJob.getPrinterJob();
        _landscape                 = _printJob.defaultPage();
        _landscape.setOrientation(PageFormat.LANDSCAPE);
        _pageSetupAction     = new PageSetupAction();
        _printAction         = new PrintAction();

        // About... window
        _aboutAction         = new AboutAction();

        try
        {
            setTitle("SearchCal");

            _mainPane = getContentPane();
            _mainPane.setLayout(new BorderLayout());

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
            _mainPane.add(splitPane, BorderLayout.CENTER);

            // Add the Status bar
            _mainPane.add(statusBar, BorderLayout.SOUTH);

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
     * Called to show the preferences window.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class ShowPreferencesAction extends MCSAction
    {
        public ShowPreferencesAction()
        {
            super("preferences");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            // Show the Preferences window
            _preferencesView.setVisible(true);
        }
    }

    /**
     * Called to setup printed page layout.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class PageSetupAction extends MCSAction
    {
        public PageSetupAction()
        {
            super("pageSetup");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            // Show Page Setup GUI
            _landscape = _printJob.pageDialog(_landscape);
        }
    }

    /**
     * Called to print data.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class PrintAction extends MCSAction
    {
        public PrintAction()
        {
            super("print");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            Book book = new Book();
            book.append((Printable) _queryView, _landscape);
            book.append((Printable) _calibratorsView, _landscape);
            book.append((Printable) _filtersView, _landscape);

            _printJob.setPageable(book);

            // Print dialog
            if (_printJob.printDialog())
            {
                try
                {
                    _printJob.print();
                }
                catch (PrinterException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Called to show the About window.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class AboutAction extends MCSAction
    {
        public AboutAction()
        {
            super("about");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            String greetings = "";
            greetings += "Specifications :\n";
            greetings += "\tDaniel BONNEAU\n";
            greetings += "\tGilles DUVERT\n";
            greetings += "\tFabien MALBET\n";
            greetings += "\tDenis MOURARD\n";
            greetings += "\n";
            greetings += "Programming :\n";
            greetings += "\tSylvain LAFRASSE\n";
            greetings += "\tGuillaume MELLA\n";
            greetings += "\tYannick VANDERSCHUEREN\n";

            AboutWindow aboutWindow = new AboutWindow("SearchCal",
                    "4.0 beta 3", greetings, "Copyright 2006 JMMC");
        }
    }
}
/*___oOo___*/
