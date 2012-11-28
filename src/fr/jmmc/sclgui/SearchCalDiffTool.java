/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.PreferencesView;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.sclgui.calibrator.CalibratorsModel;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.calibrator.DiffCalibratorsModel;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.TableSorter;
import fr.jmmc.sclgui.filter.FilterView;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.filter.FiltersView;
import fr.jmmc.sclgui.preference.ColumnsPreferencesView;
import fr.jmmc.sclgui.preference.HelpPreferencesView;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.query.QueryView;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    /** CalibratorsView left */
    CalibratorsView calibratorsViewLeft;
    /** CalibratorsView right */
    CalibratorsView calibratorsViewRight;
    /** CalibratorsView diff */
    CalibratorsView calibratorsViewDiff;
    /** diff model */
    DiffCalibratorsModel diffModel;

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

        final SearchCalDiffTool diffTool = this;

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

                // Disable non calibrator filter (show all stars):
                try {
                    preferences.setPreference(PreferenceKey.FILTER_NON_CALIBRATORS, Boolean.FALSE);

                    preferences.setPreference(PreferenceKey.VERBOSITY_SYNTHETIC_FLAG, Boolean.FALSE);
                    preferences.setPreference(PreferenceKey.VERBOSITY_DETAILED_FLAG, Boolean.TRUE);
                    preferences.setPreference(PreferenceKey.VERBOSITY_FULL_FLAG, Boolean.FALSE);

                } catch (PreferencesException pe) {
                    _logger.warn("setPreference failed: ", pe);
                }

                // Enable multi views:
                System.setProperty(CalibratorsView.MODE_MULTI_VIEW, "true");

                // Disable distance check in cell renderer:
                System.setProperty(TableSorter.DISABLE_DISTANCE_RENDERER, "true");

                // Create a query model
                final QueryModel queryModel = new QueryModel();
                queryModel.init();

                // Create filters
                final FiltersModel filtersModel = new FiltersModel(queryModel);

                // Disable all filters at startup:
                for (FilterView filterView : filtersModel.getFilterViews()) {
                    if (filterView.getEnabledCheckbox().isSelected()) {
                        // trick to fire actionPerformed():
                        filterView.getEnabledCheckbox().doClick();
                    }
                }

                final FiltersView filtersView = new FiltersView(filtersModel);

                final CalibratorsModel calibratorsModelLeft = new CalibratorsModel(filtersModel, queryModel); // query model represents the reference file
                final CalibratorsView calibratorsViewLeft = new CalibratorsView(calibratorsModelLeft);

                final CalibratorsModel calibratorsModelRight = new CalibratorsModel(filtersModel, null); // query model is null 
                final CalibratorsView calibratorsViewRight = new CalibratorsView(calibratorsModelRight);

                final CalibratorsModel calibratorsModelDiff = new CalibratorsModel(filtersModel, null); // query model is null 
                final CalibratorsView calibratorsViewDiff = new CalibratorsView(calibratorsModelDiff);

                filtersModel.addObserver(calibratorsModelLeft);
                filtersModel.addObserver(calibratorsModelRight);
                filtersModel.addObserver(calibratorsModelDiff);

                // Link everything up
                // note: _vo member is defined by App constructor !
                // note: only used to instanciate actions !
                final VirtualObservatory vo = new VirtualObservatory(queryModel, calibratorsModelLeft);
                // free reference to disable any action:
                calibratorsModelLeft.setVirtualObservatory(null);

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
                final DiffWindow window = new DiffWindow(vo, queryView, calibratorsViewLeft, calibratorsViewRight,
                        calibratorsViewDiff, filtersView, statusBar);
                App.setFrame(window);

                // Triggers all preferences observers notification to finnish GUI setup.
                preferences.triggerObserversNotification();

                final Map<String, String> args = getCommandLineArguments();

                if (args != null && args.get(ARG_DIFF) != null && args.get(ARG_LEFT) != null && args.get(ARG_RIGHT) != null) {

                    final File fileLeft = new File(args.get(ARG_LEFT));
                    final File fileRight = new File(args.get(ARG_RIGHT));

                    diffTool.diffModel = new DiffCalibratorsModel(calibratorsModelLeft, calibratorsModelRight, calibratorsModelDiff);

                    diffTool.diffModel.diff(fileLeft, fileRight);

                    diffTool.calibratorsViewLeft = calibratorsViewLeft;
                    diffTool.calibratorsViewRight = calibratorsViewRight;
                    diffTool.calibratorsViewDiff = calibratorsViewDiff;

                    // add table selection listeners:
                    final SyncSelectionHandler sshLeft = new SyncSelectionHandler(calibratorsViewLeft);
                    calibratorsViewLeft.addColumnSelectionListener(sshLeft);
                    calibratorsViewLeft.addRowSelectionListener(sshLeft);

                    final SyncSelectionHandler sshRight = new SyncSelectionHandler(calibratorsViewRight);
                    calibratorsViewRight.addColumnSelectionListener(sshRight);
                    calibratorsViewRight.addRowSelectionListener(sshRight);

                    final SyncSelectionHandler sshDiff = new SyncSelectionHandler(calibratorsViewDiff);
                    calibratorsViewDiff.addColumnSelectionListener(sshDiff);
                    calibratorsViewDiff.addRowSelectionListener(sshDiff);
                }

                // finally disable query view:
                queryView.propertyChange(null);
            }
        });
    }
    /** flag to avoid synchronizeSelection reentrance while updating JTable selections */
    private boolean ignoreSelectionEvent = false;

    /**
     * Synchronize selected stars amond CalibratorsViews
     * @param source CalibratorsView source
     */
    private void synchronizeSelection(final CalibratorsView source) {
        if (ignoreSelectionEvent) {
            return;
        }

        final int[] selectedIndices = source.getSelectedStarIndices();
        if (selectedIndices.length == 0) {
            return;
        }
        // TODO: fix column mapping (ordering changed):
        final int selectedColumn = source.getSelectedProperty();

        if (_logger.isDebugEnabled()) {
            _logger.debug("selectedColumn: {}", selectedColumn);
            _logger.debug("selectedIndices: {}", Arrays.toString(selectedIndices));
        }

        // Performance timer
        final long startTime = System.nanoTime();
        try {
            ignoreSelectionEvent = true;

            if (source == calibratorsViewLeft) {
                // Get rowIdx values:
                final List<Integer> rowIdxValues = diffModel.getFilteredRows(selectedIndices, -1, StarList.RowIdxColumnName); // left
                selectRows(calibratorsViewDiff, diffModel.findMatchingFilteredRows(rowIdxValues, 2), selectedColumn); // diff

                // Get otherRowIdx values:
                final List<Integer> otherIdxValues = diffModel.getFilteredRows(selectedIndices, -1, StarList.OtherRowIdxColumnName); // left
                selectRows(calibratorsViewRight, diffModel.findMatchingFilteredRows(otherIdxValues, 1), selectedColumn); // right

            } else if (source == calibratorsViewRight) {
                // Get otherRowIdx values:
                final List<Integer> otherIdxValues = diffModel.getFilteredRows(selectedIndices, 1, StarList.OtherRowIdxColumnName); // right
                selectRows(calibratorsViewLeft, diffModel.findMatchingFilteredRows(otherIdxValues, -1), selectedColumn); // left
                selectRows(calibratorsViewDiff, diffModel.findMatchingFilteredRows(otherIdxValues, 2), selectedColumn); // diff

            } else if (source == calibratorsViewDiff) {
                // Get rowIdx values:
                final List<Integer> rowIdxValues = diffModel.getFilteredRows(selectedIndices, 2, StarList.RowIdxColumnName); // diff
                selectRows(calibratorsViewLeft, diffModel.findMatchingFilteredRows(rowIdxValues, -1), selectedColumn); // left

                // Get otherRowIdx values:
                final List<Integer> otherIdxValues = diffModel.getFilteredRows(selectedIndices, 2, StarList.OtherRowIdxColumnName); // diff
                selectRows(calibratorsViewRight, diffModel.findMatchingFilteredRows(otherIdxValues, 1), selectedColumn); // right
            }

        } finally {
            ignoreSelectionEvent = false;

            if (_logger.isDebugEnabled()) {
                _logger.debug("synchronizeSelection() done in {} ms.", 1e-6d * (System.nanoTime() - startTime));
            }
        }
    }

    /**
     * Select the given row indexes and column in the given calibrators view 
     * @param calibratorsView calibrators view
     * @param rowIndexes row indexes
     * @param selectedColumn column index
     */
    private void selectRows(final CalibratorsView calibratorsView, final List<Integer> rowIndexes, final int selectedColumn) {
        if (rowIndexes == null || rowIndexes.isEmpty()) {
            calibratorsView.clearTableSelection();
        } else {
            for (Integer idx : rowIndexes) {
                calibratorsView.selectTableCell(idx.intValue(), selectedColumn, true, false);
            }
        }
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

                // hide filters view:
                final DiffWindow window = (DiffWindow) App.getFrame();
                final JSplitPane resultPane = window.resultPane;
                resultPane.setDividerLocation(resultPane.getHeight() - resultPane.getDividerSize());

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
     * ListSelectionListener implementation to synchronize CalibratorsView selections
     */
    private class SyncSelectionHandler implements ListSelectionListener {

        /** CalibratorsView source */
        private final CalibratorsView source;

        /**
         * Protected constructor
         * @param source CalibratorsView source
         */
        SyncSelectionHandler(final CalibratorsView source) {
            this.source = source;
        }

        /**
         * Process list selection event
         * @param lse list selection event 
         */
        public void valueChanged(final ListSelectionEvent lse) {
            if (!lse.getValueIsAdjusting()) {
                synchronizeSelection(source);
            }
        }
    }

    /**
     * Diff window. This class is at one central point and play the mediator role.
     */
    private static final class DiffWindow extends JFrame {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1L;
        /** result pane */
        final JSplitPane resultPane;

        /**
         * Constructor.
         * @param vo
         * @param queryView
         * @param calibratorsViewLeft 
         * @param calibratorsViewRight 
         * @param calibratorsViewDiff 
         * @param filtersView
         * @param statusBar  
         */
        DiffWindow(final VirtualObservatory vo, final QueryView queryView,
                   final CalibratorsView calibratorsViewLeft, final CalibratorsView calibratorsViewRight,
                   final CalibratorsView calibratorsViewDiff,
                   final FiltersView filtersView, final StatusBar statusBar) {
            super("SearchCal Diff tool");

            // Use MainWindow class path for registered actions:
            final String classPath = MainWindow.class.getName();

            final PageSetupAction pageSetupAction = new PageSetupAction(classPath, "_pageSetupAction");
            final PrintAction printAction = new PrintAction(classPath, "_printAction");

            final Container mainPane = getContentPane();
            mainPane.setLayout(new BorderLayout());

            // Create a first top-bottom split pane for calibrators and filters
            final JSplitPane filePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, calibratorsViewLeft, calibratorsViewRight);
            // Give priority to both calibrators view
            filePane.setResizeWeight(0.5);

            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            filePane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            filePane.setOneTouchExpandable(true);

            // Create a first top-bottom split pane for calibrators and filters
            final JSplitPane diffPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filePane, calibratorsViewDiff);
            // Give priority to calibrators view
            diffPane.setResizeWeight(0.66);
            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            diffPane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            diffPane.setOneTouchExpandable(true);

            // Create a first top-bottom split pane for calibrators and filters
            resultPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, diffPane, filtersView);
            // Give priority to calibrators view
//            resultPane.setResizeWeight(1.0);

            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            resultPane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            resultPane.setOneTouchExpandable(true);

            // Create a second top-bottom split pane
            final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryView, resultPane);
            // Set the split pane to continuously resize the child components
            // which the divider is dragged
            splitPane.setContinuousLayout(true);
            // Allows the user to conveniently move the divider to either end with a single click
            splitPane.setOneTouchExpandable(true);

            mainPane.add(splitPane, BorderLayout.CENTER);

            // Add the Status bar
            mainPane.add(statusBar, BorderLayout.SOUTH);

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
             * Fake Page setup action constructor
             * @param classPath the path of the class containing the field pointing to
             * the action, in the form returned by 'getClass().getName();'.
             * @param fieldName the name of the field pointing to the action.
             */
            PageSetupAction(String classPath, String fieldName) {
                super(classPath, fieldName);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
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
             * Fake Print action constructor
             * @param classPath the path of the class containing the field pointing to
             * the action, in the form returned by 'getClass().getName();'.
             * @param fieldName the name of the field pointing to the action.
             */
            PrintAction(String classPath, String fieldName) {
                super(classPath, fieldName);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // disabled
            }
        }
    }
}
/*___oOo___*/
