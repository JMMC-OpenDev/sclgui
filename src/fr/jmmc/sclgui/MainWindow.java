/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.filter.FiltersView;
import fr.jmmc.sclgui.query.QueryView;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main window. This class is at one central point and play the mediator role.
 */
public final class MainWindow extends JFrame {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(MainWindow.class.getName());
    /** Main panel container, displaying the query and result views */
    private final Container _mainPane;
    /** Query view */
    private final QueryView _queryView;
    /** Calibrators view */
    public final CalibratorsView _calibratorsView;
    /** Filters view */
    public final FiltersView _filtersView;
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
     * @param vo
     * @param queryView
     * @param calibratorsView 
     * @param filtersView
     * @param statusBar  
     */
    public MainWindow(final VirtualObservatory vo, final QueryView queryView,
                      final CalibratorsView calibratorsView, final FiltersView filtersView,
                      final StatusBar statusBar) {
        super("SearchCal");

        final String classPath = getClass().getName();

        _queryView = queryView;
        _calibratorsView = calibratorsView;
        _filtersView = filtersView;

        _pageSetupAction = new PageSetupAction(classPath, "_pageSetupAction");
        _printAction = new PrintAction(classPath, "_printAction");

        _mainPane = getContentPane();
        _mainPane.setLayout(new BorderLayout());

        // Create a first top-bottom split pane for calibrators and filters
        JSplitPane resultPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _calibratorsView, _filtersView);
        // Give priority to calibrators view
        resultPane.setResizeWeight(1.0);

        // Set the split pane to continuously resize the child components
        // which the divider is dragged
        resultPane.setContinuousLayout(true);
        // Allows the user to conveniently move the divider to either end with a single click
        resultPane.setOneTouchExpandable(true);

        // Create a second top-bottom split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _queryView, resultPane);
        // Set the split pane to continuously resize the child components
        // which the divider is dragged
        splitPane.setContinuousLayout(true);
        // Allows the user to conveniently move the divider to either end with a single click
        splitPane.setOneTouchExpandable(true);
        _mainPane.add(splitPane, BorderLayout.CENTER);

        // Add the Status bar
        _mainPane.add(statusBar, BorderLayout.SOUTH);

        // Show the user the app is ready to be used
        StatusBar.show("application ready.");


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
     * Called to setup printed page layout.
     */
    protected class PageSetupAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Page setup action constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        PageSetupAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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

        /**
         * Print action constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        PrintAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
                    _logger.error("print failure : ", pe);
                }
            }
        }
    }
}
/*___oOo___*/
