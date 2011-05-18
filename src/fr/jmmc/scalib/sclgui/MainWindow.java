/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainWindow.java,v 1.35 2011-04-04 14:00:08 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.34  2011/04/01 14:16:09  mella
 * Fix ticket #106 related to delay sometimes occuring on os with broken printing system
 *
 * Revision 1.33  2010/10/10 22:45:04  lafrasse
 * Code reformating.
 *
 * Revision 1.32  2010/10/10 22:21:05  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.31  2010/10/06 16:08:53  lafrasse
 * Properly quit application when clicking on the close button of the main window.
 *
 * Revision 1.30  2010/10/04 15:58:14  lafrasse
 * Removed redundant menu bar.
 *
 * Revision 1.29  2008/09/10 22:29:05  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Moved to new JMCS APIs.
 *
 * Revision 1.28  2008/05/19 15:39:29  lafrasse
 * Updated to add preliminary support for the new JMCS application framework.
 *
 * Revision 1.27  2008/03/10 08:09:59  lafrasse
 * Updated version number to 4.0b12.
 *
 * Revision 1.26  2007/11/12 10:55:08  lafrasse
 * Updated version number to 4.0b11.
 *
 * Revision 1.25  2007/10/22 12:39:14  lafrasse
 * Updated version number to 4.0b10.
 *
 * Revision 1.24  2007/09/03 05:59:45  lafrasse
 * Updated version number to 4.0b9.
 *
 * Revision 1.23  2007/08/16 13:44:32  lafrasse
 * Updated version number to 4.0b8.
 *
 * Revision 1.22  2007/08/09 12:27:41  lafrasse
 * Updated version number to 4.0b7.
 *
 * Revision 1.21  2007/08/01 15:30:31  lafrasse
 * Updated version number to 4.0b6.
 * Added greeting to CDS as required.
 *
 * Revision 1.20  2007/06/26 16:04:09  lafrasse
 * Updated version number to 4.0b5.
 *
 * Revision 1.19  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.18  2007/04/13 14:36:07  lafrasse
 * Updated version number and greeting.
 *
 * Revision 1.17  2007/02/16 15:19:55  lafrasse
 * Updated software version number in the About window.
 *
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
 * Updated to reflect _logger API changes.
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

import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.HelpView;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.RegisteredAction;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

/**
 * Main window. This class is at one central point and play the mediator role.
 */
public class MainWindow extends JFrame {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.MainWindow");
    /** Main panel container, displaying the query and result views */
    private Container _mainPane = null;
    /** Virtual Observatory */
    private final VirtualObservatory _vo;
    /** Query view */
    private final QueryView _queryView;
    /** Calibrators view */
    public final CalibratorsView _calibratorsView;
    /** Filters view */
    public final FiltersView _filtersView;
    /** Status bar */
    private final StatusBar _statusBar;
    /** Preferences view */
    private final PreferencesView _preferencesView;
    /** Preferences... action */
    public final ShowPreferencesAction _showPreferencesAction;
    /** Page Setup... action */
    public final PageSetupAction _pageSetupAction;
    /** Print... action */
    public final PrintAction _printAction;
    /** Printer job */
    private PrinterJob _printJob = null;
    /** PAge format */
    private PageFormat _landscape = null;

    /**
     * Constructor.
     */
    public MainWindow(final VirtualObservatory vo, final QueryView queryView,
            final CalibratorsView calibratorsView, final PreferencesView preferencesView,
            final FiltersView filtersView, final StatusBar statusBar) {
        super("SearchCal");

        final String classPath = getClass().getName();

        _vo = vo;
        _queryView = queryView;
        _calibratorsView = calibratorsView;
        _filtersView = filtersView;
        _statusBar = statusBar;

        new HelpView();

        // Preferences
        _preferencesView = preferencesView;
        _showPreferencesAction = new ShowPreferencesAction(classPath,
                "_showPreferencesAction");

        _pageSetupAction = new PageSetupAction(classPath, "_pageSetupAction");
        _printAction = new PrintAction(classPath, "_printAction");

        try {
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

            // Set the GUI up
            pack();
            setVisible(true);

            // Show the user the app is ready to be used
            StatusBar.show("application ready.");
        } catch (Exception e) {
            _logger.log(Level.SEVERE, "Main window failure : ", e);
        }

        // previous adapter manages the windowClosing(event) :
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Properly quit the application when main window close button is clicked
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                // callback on exit :
                App.quitAction().actionPerformed(null);
            }
        });
    }

    /**
     * Called during action execution to check that global variables are properly inited.
     * Do no perform this printing init during programm init because
     * one broken printing system can throw long timeout and freeze the application.
     */
    private void initPrinting() {
        if (_printJob == null) {
            _printJob = PrinterJob.getPrinterJob();
        }

        if (_landscape == null) {
            _landscape = _printJob.defaultPage();
            _landscape.setOrientation(PageFormat.LANDSCAPE);
        }
    }

    /**
     * Called to show the preferences window.
     */
    protected class ShowPreferencesAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public ShowPreferencesAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            flagAsPreferenceAction();
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("ShowPreferencesAction", "actionPerformed");

            // Show the Preferences window
            _preferencesView.setVisible(true);
        }
    }

    /**
     * Called to setup printed page layout.
     */
    protected class PageSetupAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public PageSetupAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("PageSetupAction", "actionPerformed");

            // Check Printing if not yet done
            initPrinting();
            
            // Show Page Setup GUI
            _landscape = _printJob.pageDialog(_landscape);
        }
    }

    /**
     * Called to print data.
     */
    protected class PrintAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public PrintAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("PrintAction", "actionPerformed");

            // Check Printing and landscape initialisation
            initPrinting();

            Book book = new Book();
            book.append((Printable) _queryView, _landscape);
            book.append((Printable) _calibratorsView, _landscape);
            book.append((Printable) _filtersView, _landscape);

            _printJob.setPageable(book);

            // Print dialog
            if (_printJob.printDialog()) {
                try {
                    _printJob.print();
                } catch (PrinterException pe) {
                    _logger.log(Level.SEVERE, "print failure : ", pe);
                }
            }
        }
    }
}
/*___oOo___*/
