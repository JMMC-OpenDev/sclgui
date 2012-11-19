/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.PreferencesView;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.sclgui.calibrator.CalibratorsModel;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.filter.FiltersView;
import fr.jmmc.sclgui.preference.ColumnsPreferencesView;
import fr.jmmc.sclgui.preference.HelpPreferencesView;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.query.QueryView;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchCal Diff Tool
 * 
 * @author laurent bourges, sylvain lafrasse
 */
public final class SearchCalDiffTool extends App {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SearchCalDiffTool.class.getName());
    /** argument 'diff' */
    public final static String ARG_DIFF = "diff";
    /** argument 'left' */
    public final static String ARG_LEFT = "left";
    /** argument 'right' */
    public final static String ARG_RIGHT = "right";
    /* members */
    /** calibrators model of first file to diff */
    private CalibratorsModel calibratorsModelA;
    /** calibrators model of second file to diff */
    private CalibratorsModel calibratorsModelB;

    /**
     * Launch the SearchCalDiffTool
     *
     * Create all objects needed by SearchCalDiffTool and plug event responding
     * loop (Listener/Listenable, Observer/Observable) in.
     *
     * @param args command-line options.
     */
    public SearchCalDiffTool(String[] args) {
        super(args);
    }

    /**
     * Add diff custom command line argument(s)
     */
    @Override
    protected void addCustomCommandLineArguments() {
        addCustomCommandLineArgument(ARG_DIFF, false);
        addCustomCommandLineArgument(ARG_LEFT, true);
        addCustomCommandLineArgument(ARG_RIGHT, true);
    }

    /** 
     * Add diff custom command line argument help 
     */
    @Override
    protected void showCustomArgumentsHelp() {
        System.out.println("| [-diff]                      Enable comparison mode (diff)            |");
        System.out.println("| [-left]                      First file to compare (reference)        |");
        System.out.println("| [-right]                     Second file to compare                   |");
        System.out.println("|-----------------------------------------------------------------------|");
    }

    /** 
     * Initialize application objects 
     */
    @Override
    protected void init() {
        // Set default resource
        fr.jmmc.jmcs.util.ResourceUtils.setResourceName("fr/jmmc/sclgui/resource/Resources");

        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            /**
             * Initializes the swing components with their actions in EDT
             */
            @Override
            public void run() {

                // Get preferences
                final Preferences preferences = Preferences.getInstance();

                // Create a query model
                final QueryModel queryModel = new QueryModel();
                queryModel.init();

                // Create filters
                FiltersModel filtersModel = new FiltersModel(queryModel);
                FiltersView filtersView = new FiltersView(filtersModel);

                calibratorsModelA = new CalibratorsModel(filtersModel, queryModel);
                final CalibratorsView calibratorsViewA = new CalibratorsView(calibratorsModelA);
                calibratorsViewA.init();

                calibratorsModelB = new CalibratorsModel(filtersModel, queryModel);
                final CalibratorsView calibratorsViewB = new CalibratorsView(calibratorsModelB);
                calibratorsViewB.init();

                filtersModel.addObserver(calibratorsModelA);
                filtersModel.addObserver(calibratorsModelB);

                // Link everything up
                // note: _vo member is defined by App constructor !
                // note: only used to instanciate actions !
                final VirtualObservatory vo = new VirtualObservatory(queryModel, calibratorsModelA);
                // free reference to disable any action:
                calibratorsModelA.setVirtualObservatory(null);

                // Attach the query model to its query view
                final QueryView queryView = new QueryView(queryModel, null); // vo is null to disable start query action
                queryView.init();

                // Retrieve application preferences and attach them to their view
                // (This instance must be instanciated after dependencies)
                final LinkedHashMap<String, JPanel> panels = new LinkedHashMap<String, JPanel>(4);

                // Add the columns preferences pane
                final ColumnsPreferencesView columnsView = new ColumnsPreferencesView("view.columns");
                columnsView.init();
                panels.put("Columns Order", columnsView);

                // Add the catalog preferences pane
                final JPanel catalogView = new LegendView(true);
                panels.put("Legend Colors", catalogView);

                // Add the help preferences pane
                final HelpPreferencesView helpView = new HelpPreferencesView();
                helpView.init();
                panels.put("Help Settings", helpView);

                final PreferencesView preferencesView = new PreferencesView(preferences, panels);
                preferencesView.init();

                final StatusBar statusBar = new StatusBar();
                // Show the user the app is been initialized
                StatusBar.show("application initialization...");

                // Build the main window
                final DiffWindow window = new DiffWindow(vo, queryView, calibratorsViewA, calibratorsViewB, filtersView, statusBar);
                App.setFrame(window);

                // Triggers all preferences observers notification to finnish GUI setup.
                preferences.triggerObserversNotification();

                final Map<String, String> args = getCommandLineArguments();
                if (args != null) {
                    if (args.get(ARG_DIFF) != null && args.get(ARG_LEFT) != null && args.get(ARG_RIGHT) != null) {
                        _logger.warn("args: {}", args);

                        final File fileA = new File(args.get(ARG_LEFT));
                        final File fileB = new File(args.get(ARG_RIGHT));

                        calibratorsModelA.parseVOTable(fileA);
                        calibratorsModelB.parseVOTable(fileB);
                    }
                }
            }
        });
    }

    /** Execute application body */
    @Override
    protected void execute() {
        SwingUtils.invokeLaterEDT(new Runnable() {
            /**
             * Show the application frame using EDT
             */
            @Override
            public void run() {
                _logger.debug("SearchCalDiff.ready : handler called.");

                getFrame().setVisible(true);
            }
        });
    }

    /**
     * Main entry point
     *
     * @param args command line arguments (open file ...)
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(final String[] args) {
        // init swing application for science
        SwingSettings.setup();

        new SearchCalDiffTool(args);
    }

    /**
     * Diff window. This class is at one central point and play the mediator role.
     */
    private static final class DiffWindow extends JFrame {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /** Logger */
        private static final Logger _logger = LoggerFactory.getLogger(DiffWindow.class.getName());
        /** Main panel container, displaying the query and result views */
        private Container _mainPane = null;
        /** Query view */
        private final QueryView _queryView;
        /** Calibrators view for File A */
        public final CalibratorsView _calibratorsViewA;
        /** Calibrators view for File B */
        public final CalibratorsView _calibratorsViewB;
        /** Filters view */
        public final FiltersView _filtersView;
        /** Page Setup... action */
        public final PageSetupAction _pageSetupAction;
        /** Print... action */
        public final PrintAction _printAction;

        /**
         * Constructor.
         * @param vo
         * @param queryView
         * @param calibratorsViewA 
         * @param calibratorsViewB 
         * @param filtersView
         * @param statusBar  
         */
        DiffWindow(final VirtualObservatory vo, final QueryView queryView,
                   final CalibratorsView calibratorsViewA, final CalibratorsView calibratorsViewB, final FiltersView filtersView,
                   final StatusBar statusBar) {
            super("SearchCal Diff tool");

            _queryView = queryView;
            _calibratorsViewA = calibratorsViewA;
            _calibratorsViewB = calibratorsViewB;
            _filtersView = filtersView;

            final String classPath = MainWindow.class.getName();

            _pageSetupAction = new PageSetupAction(classPath, "_pageSetupAction");
            _printAction = new PrintAction(classPath, "_printAction");

            _mainPane = getContentPane();
            _mainPane.setLayout(new BorderLayout());

            // Create a first top-bottom split pane for calibrators and filters
            JSplitPane diffPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _calibratorsViewA, _calibratorsViewB);
            // Give priority to calibrators view
            diffPane.setResizeWeight(0.5);

            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            diffPane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            diffPane.setOneTouchExpandable(true);

            // Create a first top-bottom split pane for calibrators and filters
            JSplitPane resultPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, diffPane, _filtersView);
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
         * Called to setup printed page layout.
         */
        protected class PageSetupAction extends RegisteredAction {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;

            /**
             * Private empty action
             * @param classPath
             * @param fieldName 
             */
            PageSetupAction(String classPath, String fieldName) {
                super(classPath, fieldName);
            }

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // disabled
            }
        }

        /**
         * Called to print data.
         */
        protected class PrintAction extends RegisteredAction {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;

            /** 
             * Private empty action
             * @param classPath
             * @param fieldName 
             */
            PrintAction(String classPath, String fieldName) {
                super(classPath, fieldName);
            }

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // disabled
            }
        }
    }
}
/*___oOo___*/
