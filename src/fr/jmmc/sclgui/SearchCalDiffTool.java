/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.PreferencesView;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.util.RecentFilesManager;
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
    // Members
    /* note: do not set these members to null as it is initialized in App.setupGui() by super() ie before class initialization (LBO) */
    /** CalibratorsView left */
    private CalibratorsView _calibratorsViewLeft;
    /** CalibratorsView right */
    private CalibratorsView _calibratorsViewRight;
    /** CalibratorsView diff */
    private CalibratorsView _calibratorsViewDiff;
    /** query view */
    private QueryView _queryView;
    /** diff model */
    private DiffCalibratorsModel _diffModel;
    /** flag to avoid synchronizeSelection reentrance while updating JTable selections */
    private boolean _ignoreSelectionEvent = false;

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
    protected void defineCustomCommandLineArgumentsAndHelp() {
        addCustomCommandLineArgument(ARG_DIFF, false);
        addCustomCommandLineArgument(ARG_LEFT, true);
        addCustomCommandLineArgument(ARG_RIGHT, true);

        String help = "";
        help += "| [-diff]                      Enable comparison mode (diff)                   |";
        help += "| [-left]                      First file to compare (reference)               |";
        help += "| [-right]                     Second file to compare                          |";
        help += "|------------------------------------------------------------------------------|";
        addCustomArgumentsHelp(help);
    }

    @Override
    protected void initServices() {
        // Set default resource
        fr.jmmc.jmcs.util.PropertyUtils.setResourceName("fr/jmmc/sclgui/resource/Resources");
    }

    @Override
    protected void setupGui() {

        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            /**
             * Initializes the swing components with their actions in EDT
             */
            @Override
            public void run() {
                // Disable Open Recent files in diff tool:
                RecentFilesManager.setEnabled(false);

                // Get preferences
                final Preferences preferences = Preferences.getInstance();

                try {
                    // Disable non calibrator filter (show all stars):
                    preferences.setPreference(PreferenceKey.FILTER_NON_CALIBRATORS, Boolean.FALSE);

                    // Use detailed view:
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
                _queryView = new QueryView(queryModel, null); // vo is null to disable ___internalStart query action
                _queryView.init();

                // Retrieve application preferences and attach them to their view
                // (This instance must be instanciated after dependencies)
                final LinkedHashMap<String, JPanel> panels = new LinkedHashMap<String, JPanel>(4);

                // Add the columns preferences pane
                final ColumnsPreferencesView columnsView = new ColumnsPreferencesView(Preferences.PREFIX_VIEW_COLUMNS);
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
                final DiffWindow window = new DiffWindow(vo, _queryView, calibratorsViewLeft, calibratorsViewRight,
                        calibratorsViewDiff, filtersView, statusBar);
                App.setFrame(window);

                // Triggers all preferences observers notification to finnish GUI setup.
                preferences.triggerObserversNotification();

                // Overwrite the Open action:
                new OpenDiffFilesAction(VirtualObservatory.class.getName(), "_openFileAction");

                // create diff model:
                _diffModel = new DiffCalibratorsModel(calibratorsModelLeft, calibratorsModelRight, calibratorsModelDiff);

                final Map<String, String> args = getCommandLineArguments();
                if (args != null && args.get(ARG_DIFF) != null && args.get(ARG_LEFT) != null && args.get(ARG_RIGHT) != null) {
                    diff(new File(args.get(ARG_LEFT)), new File(args.get(ARG_RIGHT)));
                }

                _calibratorsViewLeft = calibratorsViewLeft;
                _calibratorsViewRight = calibratorsViewRight;
                _calibratorsViewDiff = calibratorsViewDiff;

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

                // hide filters view:
                final DiffWindow window = (DiffWindow) App.getFrame();
                final JSplitPane resultPane = window.resultPane;
                resultPane.setDividerLocation(resultPane.getHeight() - resultPane.getDividerSize());

                getFrame().setVisible(true);
            }
        });
    }

    /**
     * Synchronize selected stars among CalibratorsViews
     * @param source CalibratorsView source
     */
    private void synchronizeSelection(final CalibratorsView source) {
        if (_ignoreSelectionEvent) {
            return;
        }

        final int[] selectedIndices = source.getSelectedStarIndices();
        if (selectedIndices.length == 0) {
            return;
        }

        final String selectedColumnName = source.getSelectedPropertyName();
        if (selectedColumnName == null) {
            // column drag ie sort
            return;
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("selectedColumnName: {}", selectedColumnName);
            _logger.debug("selectedIndices: {}", Arrays.toString(selectedIndices));
        }

        // Performance timer
        final long startTime = System.nanoTime();
        try {
            _ignoreSelectionEvent = true;

            if (source == _calibratorsViewLeft) {
                // Get rowIdx values:
                final List<Integer> rowIdxValues = _diffModel.getFilteredRows(selectedIndices, -1, StarList.RowIdxColumnName); // left
                selectRows(_calibratorsViewDiff, _diffModel.findMatchingFilteredRows(rowIdxValues, 2),
                        _calibratorsViewDiff.getViewIndexFromColumnName(selectedColumnName)); // diff

                // Get otherRowIdx values:
                final List<Integer> otherIdxValues = _diffModel.getFilteredRows(selectedIndices, -1, StarList.OtherRowIdxColumnName); // left
                selectRows(_calibratorsViewRight, _diffModel.findMatchingFilteredRows(otherIdxValues, 1),
                        _calibratorsViewRight.getViewIndexFromColumnName(selectedColumnName)); // right

            } else if (source == _calibratorsViewRight) {
                // Get otherRowIdx values:
                final List<Integer> otherIdxValues = _diffModel.getFilteredRows(selectedIndices, 1, StarList.OtherRowIdxColumnName); // right
                selectRows(_calibratorsViewLeft, _diffModel.findMatchingFilteredRows(otherIdxValues, -1),
                        _calibratorsViewLeft.getViewIndexFromColumnName(selectedColumnName)); // left

                selectRows(_calibratorsViewDiff, _diffModel.findMatchingFilteredRows(otherIdxValues, 2),
                        _calibratorsViewDiff.getViewIndexFromColumnName(selectedColumnName)); // diff

            } else if (source == _calibratorsViewDiff) {
                // Get rowIdx values:
                final List<Integer> rowIdxValues = _diffModel.getFilteredRows(selectedIndices, 2, StarList.RowIdxColumnName); // diff
                selectRows(_calibratorsViewLeft, _diffModel.findMatchingFilteredRows(rowIdxValues, -1),
                        _calibratorsViewLeft.getViewIndexFromColumnName(selectedColumnName)); // left

                // Get otherRowIdx values:
                final List<Integer> otherIdxValues = _diffModel.getFilteredRows(selectedIndices, 2, StarList.OtherRowIdxColumnName); // diff
                selectRows(_calibratorsViewRight, _diffModel.findMatchingFilteredRows(otherIdxValues, 1),
                        _calibratorsViewRight.getViewIndexFromColumnName(selectedColumnName)); // right
            }

        } finally {
            _ignoreSelectionEvent = false;

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

    /**
     * Compare the two given files
     * @param fileLeft first file to compare (reference)
     * @param fileRight second file to compare
     */
    private void diff(final File fileLeft, final File fileRight) {
        // perform diff:
        _diffModel.diff(fileLeft, fileRight);

        // finally disable query view:
        _queryView.propertyChange(null);
    }

    @Override
    protected void cleanup() {
        _calibratorsViewLeft = null;
        _calibratorsViewRight = null;
        _calibratorsViewDiff = null;
        _queryView = null;
        _diffModel = null;
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
        @Override
        public void valueChanged(final ListSelectionEvent lse) {
            if (!lse.getValueIsAdjusting()) {
                synchronizeSelection(source);
            }
        }
    }

    /**
     * Custom Open action to diff files.
     */
    protected class OpenDiffFilesAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Protected constructor
         * @param classPath
         * @param fieldName 
         */
        OpenDiffFilesAction(final String classPath, final String fieldName) {
            super(classPath, fieldName);

            flagAsOpenAction();
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            File fileLeft, fileRight;

            // If the action was automatically triggered from App launch
            if (ae.getSource() == ActionRegistrar.getInstance()) {
                fileLeft = new File(ae.getActionCommand());
                fileRight = null;
            } else {
                fileLeft = _diffModel.getFileLeft();
                fileRight = _diffModel.getFileRight();
            }

            final File[] files = DiffOpenPanel.showDialog(fileLeft, fileRight);

            if (files != null) {
                fileLeft = files[0];
                fileRight = files[1];

                diff(fileLeft, fileRight);
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
         * @param _queryView
         * @param _calibratorsViewLeft 
         * @param _calibratorsViewRight 
         * @param _calibratorsViewDiff 
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

    /**
     * Main entry point
     *
     * @param args command line arguments (open file ...)
     */
    public static void main(final String[] args) {
        Bootstrapper.launchApp(new SearchCalDiffTool(args));
    }
}
/*___oOo___*/
