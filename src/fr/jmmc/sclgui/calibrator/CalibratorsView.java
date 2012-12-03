/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.action.RegisteredPreferencedBooleanAction;
import fr.jmmc.jmcs.gui.util.AutofitTableColumns;
import fr.jmmc.sclgui.LegendView;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calibrators view.
 *
 * Manage a JTable.
 * This class also embeds the appropriate MVC controller in the form of several
 * ActionListener.
 * It extend JPanel in order to be displayable.
 * It implements TableModelListener in order to automatically update any time
 * its data model or the application preferences change.
 * It implements Observer in order to automatically update any time the
 * application preferences change.
 */
public final class CalibratorsView extends JPanel implements TableModelListener,
                                                             ListSelectionListener, Observer, Printable {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(CalibratorsView.class.getName());
    /** System property to enable multi view support (focus) */
    public static final String MODE_MULTI_VIEW = "CalibratorsView.multiViews";
    /** flag indicating to autofit table columns */
    public static final boolean _autoFitsColumns = true;
    /** The monitored application preferences */
    private final static Preferences _preferences = Preferences.getInstance();
    /** flag indicating to use multi views */
    private static boolean _multiView = false;
    /** current active view */
    private static volatile CalibratorsView _currentView = null;
    /* shared actions */
    /** Delete action */
    private static DeleteAction _deleteAction = null;
    /** Undelete action */
    private static UndeleteAction _undeleteAction = null;
    /** Show Legend action */
    public static RegisteredPreferencedBooleanAction _showLegendAction = null;
    /** Synthetic Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _syntheticResultsVerbosityAction = null;
    /** Detailed Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _detailedResultsVerbosityAction = null;
    /** Full Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _fullResultsVerbosityAction = null;
    /** Dedicated panel for calibrator data quick search */
    private static SearchPanel _searchPanel = null;
    /* members */
    /** The monitored data source displayed by the embedded JTable */
    private final CalibratorsModel _calibratorsModel;
    /** The calibrators table */
    final JTable _calibratorsTable;
    /** The calibrator table sorter */
    final TableSorter _tableSorter;
    /** Calibrators table and Legend container */
    private final JSplitPane _tableAndLegendPane;

    /**
     * Initialize static actions
     */
    private static void initializeSharedActions() {
        // avoid reentrance (Diff tool) and instanciate actions once:
        if (_showLegendAction == null) {
            final String classPath = CalibratorsView.class.getName();

            // Create actions
            _deleteAction = new DeleteAction(classPath, "_deleteAction");
            _undeleteAction = new UndeleteAction(classPath, "_undeleteAction");

            _showLegendAction = new RegisteredPreferencedBooleanAction(classPath, "_showLegendAction", "Show Legend", _preferences, PreferenceKey.SHOW_LEGEND_FLAG);
            _syntheticResultsVerbosityAction = new RegisteredPreferencedBooleanAction(classPath, "_syntheticResultsVerbosityAction", "Synthetic", _preferences, PreferenceKey.VERBOSITY_SYNTHETIC_FLAG);
            _detailedResultsVerbosityAction = new RegisteredPreferencedBooleanAction(classPath, "_detailedResultsVerbosityAction", "Detailed", _preferences, PreferenceKey.VERBOSITY_DETAILED_FLAG);
            _fullResultsVerbosityAction = new RegisteredPreferencedBooleanAction(classPath, "_fullResultsVerbosityAction", "Full", _preferences, PreferenceKey.VERBOSITY_FULL_FLAG);

            // Search Panel
            _searchPanel = new SearchPanel();

            // Check if the multi view mode is defined at startup:
            _multiView = "true".equalsIgnoreCase(System.getProperty(CalibratorsView.MODE_MULTI_VIEW, "false"));
        }
    }

    /**
     * Return the current active calibrators view
     * @return current active calibrators view
     */
    public static CalibratorsView getCurrentView() {
        return _currentView;
    }

    /**
     * Define the current active calibrators view
     * @param view current active calibrators view 
     */
    public static void setCurrentView(final CalibratorsView view) {
        final CalibratorsView previous = _currentView;

        if (view != null && view != previous) {
            _currentView = view;

            // update border:
            view.updateBorderTitle();
            // update action state according to the current view:
            view.refreshActionState();

            if (previous != null) {
                previous.updateBorderTitle();
            }
        }
    }

    /**
     * Constructor.
     *
     * @param calibratorsModel the data to be displayed.
     */
    public CalibratorsView(final CalibratorsModel calibratorsModel) {

        // Store the model and register against it
        _calibratorsModel = calibratorsModel;

        // Initialize shared actions:
        initializeSharedActions();

        // Gray border of the view.
        updateBorderTitle();

        // Size management
        setMinimumSize(new Dimension(895, 120));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Table initialization
        _calibratorsTable = new JTable();
        _calibratorsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _calibratorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Configure table sorting
        _tableSorter = new TableSorter(_calibratorsModel, _calibratorsTable.getTableHeader());

        _calibratorsTable.setModel(_tableSorter);

        // Place tables into scrollPane
        final JScrollPane scrollPane = new JScrollPane(_calibratorsTable);
        scrollPane.setMinimumSize(new Dimension(570, 60));
        scrollPane.setPreferredSize(new Dimension(570, 100));

        // Fixed Row Table Headers
        final JTable rowHeader = new JTable(_calibratorsModel._rowHeadersModel);
        rowHeader.setIntercellSpacing(new Dimension(0, 0));

        final Dimension d = rowHeader.getPreferredScrollableViewportSize();
        d.width = 50;
        rowHeader.setPreferredScrollableViewportSize(d);
        rowHeader.setRowHeight(_calibratorsTable.getRowHeight());
        rowHeader.setRowSelectionAllowed(false);
        scrollPane.setRowHeaderView(rowHeader);

        final JTableHeader corner = rowHeader.getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);

        // Set Minimum Width of legend component
        final JPanel legendPanel = new LegendView(false);
        final int legendWidth = Math.min(legendPanel.getPreferredSize().width, 200);
        legendPanel.setMinimumSize(new Dimension(legendWidth, 0));
        legendPanel.setSize(new Dimension(0, 0));

        // Set and place TableId, Table and Legend group
        _tableAndLegendPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, legendPanel);
        _tableAndLegendPane.setOneTouchExpandable(true);
        _tableAndLegendPane.setResizeWeight(1.0);
        _tableAndLegendPane.setContinuousLayout(true);
        _tableAndLegendPane.setAlignmentX(CENTER_ALIGNMENT);
        add(_tableAndLegendPane);

        // Handle window resize
        addHierarchyBoundsListener(new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {
            }

            @Override
            public void ancestorResized(HierarchyEvent e) {
                // Properly display (or not) legend view
                showLegend();
            }
        });

        if (_multiView) {
            final CalibratorsView view = this;

            final MouseAdapter mouseOver = new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent me) {
                    // _logger.info("mouseClicked: source: {}", ObjectUtils.getObjectInfo(me.getSource()));
                    setCurrentView(view);
                }

                @Override
                public void mouseReleased(final MouseEvent me) {
                    // _logger.info("mouseReleased: source: {}", ObjectUtils.getObjectInfo(me.getSource()));
                    setCurrentView(view);
                }
            };

            // Add this mouse adapter to panel and its child components (JTables)
            // to get properly mouse events:
            addMouseListener(mouseOver);
            rowHeader.addMouseListener(mouseOver);
            _calibratorsTable.addMouseListener(mouseOver);
        }

        init();
    }

    /**
     * Register this instance in JTable and preference listeners 
     */
    private void init() {
        final CalibratorsView view = this;

        // Update current view to the first CalibratorsView instance:
        if (getCurrentView() == null) {
            setCurrentView(view);
        }

        _calibratorsModel.addTableModelListener(view);

        _preferences.addObserver(view);

        // Become listselectionListener to forward selected list to model
        addRowSelectionListener(view);
    }

    /**
     * Update panel's border
     */
    private void updateBorderTitle() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append("Found Calibrators");

        final int total = _calibratorsModel.getTotalNumberOfStar();

        sb.append(" (").append(total).append(" sources");

        if (total != 0) {
            final int hidden = _calibratorsModel.getHiddenNumberOfStar();

            sb.append(", ").append(hidden).append(" filtered");
        }
        sb.append(')');

        final Color color = (_multiView && getCurrentView() == this) ? Color.BLUE : Color.GRAY;

        setBorder(new TitledBorder(BorderFactory.createLineBorder(color), sb.toString()));
    }

    /**
     * Called on the JTable row selection changes.
     *
     * @param lse 
     * @sa javax.swing.event.ListSelectionListener
     */
    @Override
    public void valueChanged(final ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
            // Update model with current selection
            _calibratorsModel.setSelectedStars(getSelectedStarIndices());

            refreshActionState();
        }
    }

    /**
     * Called on _calibratorsModel changes.
     * @param e 
     */
    @Override
    public void tableChanged(final TableModelEvent e) {
        // First: fire this event to table sorter (indirection array + custom renderer / editors):
        _tableSorter.tableModelListener.tableChanged(e);

        updateBorderTitle();

        refreshActionState();

        if (_autoFitsColumns) {
            // If DataTableChanged event:
            if (e.getLastRow() == Integer.MAX_VALUE && _calibratorsTable.getRowCount() != 0) {
                final long startTime = System.nanoTime();

                // use cell values not renderer values (html code):
                AutofitTableColumns.autoResizeTable(_calibratorsTable, true);

                _logger.info("AutofitTableColumns.autoResizeTable: done in {} ms.", 1e-6d * (System.nanoTime() - startTime));
            }
        }

        update(null, null);
    }

    /**
     * Refresh the action state (enabled / disabled)
     */
    private void refreshActionState() {
        if (_multiView) {
            // disable delete / undelete actions in SearchCal Diff tool:
            _deleteAction.setEnabled(false);
            _undeleteAction.setEnabled(false);
        } else {
            // (Dis)Enable the delete menu item
            _deleteAction.setEnabled(_calibratorsModel.getNumberOfSelectedStars() != 0);

            // Enable/disable the Undelete menu item
            _undeleteAction.setEnabled(_calibratorsModel.hasSomeDeletedStars());
        }

        // (Dis)enable Find widgets according to data availability
        _searchPanel.enableMenus(_calibratorsModel.getNumberOfDisplayedStar() != 0);
    }

    /**
     * Automatically called whenever the observed model changed
     * @param o
     * @param arg  
     */
    @Override
    public void update(final Observable o, final Object arg) {
        // If preference colors have changed, repaint tables
        _calibratorsTable.repaint();

        // Refresh legend display state
        showLegend();
    }

    /**
     * @param graphics 
     * @param pageFormat 
     * @param pageIndex 
     * @return 
     * @throws PrinterException 
     * @sa java.awt.print
     */
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex) throws PrinterException {
        final Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        final int fontHeight = g2d.getFontMetrics().getHeight();
        final int fontDescent = g2d.getFontMetrics().getDescent();

        // laisser de l'espace pour le numero de page
        final double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        final double pageWidth = pageFormat.getImageableWidth();

        g2d.drawString("Page: " + (pageIndex + 1), ((int) pageWidth / 2) - 35,
                (int) ((pageHeight + fontHeight) - fontDescent));

        g2d.scale(0.7, 0.7);
        paint(g2d);

        return (Printable.PAGE_EXISTS);
    }

    /**
     * Tell GUI to show or hide legend panel
     */
    public void showLegend() {
        final boolean preferencedLegendShouldBeVisible = _preferences.getPreferenceAsBoolean(PreferenceKey.SHOW_LEGEND_FLAG);

        final int totalWidth = (int) _tableAndLegendPane.getBounds().getWidth();
        final int dividerWidth = _tableAndLegendPane.getDividerSize();
        final int tableAndLegendWidth = totalWidth - dividerWidth;
        int legendWidth = tableAndLegendWidth;

        if (preferencedLegendShouldBeVisible) {
            final Component legend = _tableAndLegendPane.getRightComponent();
            final int legendMinWidth = (int) legend.getMinimumSize().getWidth();
            legendWidth = tableAndLegendWidth - legendMinWidth;
        }

        _tableAndLegendPane.setDividerLocation(legendWidth);
    }

    /** 
     * Returns the indices list of stars selected in the calibrator table .
     * 
     * @return the indices list
     */
    public int[] getSelectedStarIndices() {
        final int[] selectedRows = _calibratorsTable.getSelectedRows();
        final int[] convertedSelectedRows = new int[selectedRows.length];

        // Convert JTable order to data model order thanks to the TableSorter
        for (int i = 0, len = selectedRows.length; i < len; i++) {
            convertedSelectedRows[i] = _tableSorter.modelIndex(selectedRows[i]);
        }

        return convertedSelectedRows;
    }

    /** 
     * Returns the selected column name in the calibrator table.
     * 
     * @return the column name or null if the column can not be found
     */
    public String getSelectedPropertyName() {
        final int viewIndex = _calibratorsTable.convertColumnIndexToModel(_calibratorsTable.getSelectedColumn());
        if (viewIndex != -1) {
            return _tableSorter.getColumnName(viewIndex);
        }
        return null;
    }

    /**
     * Find the column index in the table (view) corresponding to the given column name
     * @param columnName column name
     * @return column index or -1 if not found
     */
    public int getViewIndexFromColumnName(final String columnName) {
        return _calibratorsTable.convertColumnIndexToView(_tableSorter.findColumnViewIndex(_calibratorsModel.getColumnIdByName(columnName)));
    }

    /**
     * Add column selection listener
     * @param listener column selection listener
     */
    public void addColumnSelectionListener(final ListSelectionListener listener) {
        _calibratorsTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * Add row selection listener
     * @param listener row selection listener
     */
    public void addRowSelectionListener(final ListSelectionListener listener) {
        _calibratorsTable.getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * Clear the table selection
     */
    public void clearTableSelection() {
        _calibratorsTable.clearSelection();
    }

    /**
     * Select the table cell at the given row and column index
     * @param row row index
     * @param column colum index
     */
    public void selectTableCell(final int row, final int column) {
        selectTableCell(row, column, false, true);
    }

    /**
     * Select the table cell at the given row and column index
     * @param rowIndex row index
     * @param column colum index
     * @param isModelIndex true if model index; false if view index
     * @param requestFocus true to request focus
     */
    public void selectTableCell(final int rowIndex, final int column, final boolean isModelIndex, final boolean requestFocus) {
        final int row = (isModelIndex) ? _tableSorter.viewIndex(rowIndex) : rowIndex;
        final int col = (column == -1) ? 0 : column; // use first column if the given column is incorrect
        // Clear previous selection and set new selection
        _calibratorsTable.changeSelection(row, col, false, false);
        _calibratorsTable.changeSelection(row, col, true, true);

        // Move view to show found cell
        _calibratorsTable.scrollRectToVisible(_calibratorsTable.getCellRect(row, col, true));

        if (requestFocus) {
            _calibratorsTable.requestFocus();
        }
    }

    /**
     * Delete the selected stars. 
     */
    private void deleteSelectedStars() {
        _calibratorsModel.deleteStars(_calibratorsTable.getSelectedRows());
    }

    /**
     * Undelete all stars previously flagged as deleted.
     */
    private void undeleteStars() {
        _calibratorsModel.undeleteStars();
    }

    /**
     * Delete stars action
     */
    protected final static class DeleteAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Protected constructor
         * @param classPath
         * @param fieldName 
         */
        DeleteAction(final String classPath, final String fieldName) {
            super(classPath, fieldName);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            // use current view to perform delete action:
            getCurrentView().deleteSelectedStars();
        }
    }

    /**
     * Undelete stars action
     */
    protected final static class UndeleteAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Protected constructor
         * @param classPath
         * @param fieldName 
         */
        UndeleteAction(final String classPath, final String fieldName) {
            super(classPath, fieldName);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            // use current view to perform undelete action:
            getCurrentView().undeleteStars();
        }
    }
}
/*___oOo___*/
