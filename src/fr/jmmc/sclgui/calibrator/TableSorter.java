package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TableSorter is a decorator for TableModels; adding sorting
 * functionality to a supplied TableModel. TableSorter does
 * not store or copy the data in its TableModel; instead it maintains
 * a map from the row indexes of the view to the row indexes of the
 * model. As requests are made of the sorter (like getValueAt(row, col))
 * they are passed to the underlying model after the row numbers
 * have been translated via the internal mapping array. This way,
 * the TableSorter appears to hold another copy of the table
 * with the rows in a different order.
 * <p/>
 * TableSorter registers itself as a listener to the underlying model,
 * just as the JTable itself would. Events recieved from the model
 * are examined, sometimes manipulated (typically widened), and then
 * passed on to the TableSorter's listeners (typically the JTable).
 * If a change to the model has invalidated the order of TableSorter's
 * rows, a note of this is made and the sorter will resort the
 * rows the next time a value is requested.
 * <p/>
 * When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the
 * table header may be used as a complete UI for TableSorter.
 * The default renderer of the tableHeader is decorated with a renderer
 * that indicates the sorting status of each column. In addition,
 * a mouse listener is installed with the following behavior:
 * <ul>
 * <li>
 * Mouse-click: Clears the sorting status of all other columns
 * and advances the sorting status of that column through three
 * values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to
 * NOT_SORTED again).
 * <li>
 * SHIFT-mouse-click: Clears the sorting status of all other columns
 * and cycles the sorting status of the column through the same
 * three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li>
 * CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except
 * that the changes to the column do not cancel the statuses of columns
 * that are already sorting - giving a way to initiate a compound
 * sort.
 * </ul>
 * <p/>
 * This is a long overdue rewrite of a class of the same name that
 * first appeared in the swing table demos in 1997.
 *
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 */
public final class TableSorter extends AbstractTableModel implements Observer {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(TableSorter.class.getName());
    /**
     * DOCUMENT ME!
     */
    public static final int DESCENDING = -1;
    /**
     * DOCUMENT ME!
     */
    public static final int NOT_SORTED = 0;
    /**
     * DOCUMENT ME!
     */
    public static final int ASCENDING = 1;
    /**
     * DOCUMENT ME!
     */
    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);
    /**
     * DOCUMENT ME!
     */
    public static final Comparator COMPARABLE_COMPARATOR = new Comparator() {
        public int compare(final Object o1, final Object o2) {
            return ((Comparable) o1).compareTo(o2);
        }
    };
    /**
     * DOCUMENT ME!
     */
    public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
        public int compare(final Object o1, final Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    };
    /**
     * DOCUMENT ME!
     */
    protected TableModel tableModel;
    /**
     * DOCUMENT ME!
     */
    private Row[] viewToModel;
    /**
     * DOCUMENT ME!
     */
    private int[] modelToView;
    /**
     * DOCUMENT ME!
     */
    private JTableHeader tableHeader;
    /**
     * DOCUMENT ME!
     */
    private MouseListener mouseListener;
    /**
     * DOCUMENT ME!
     */
    private TableModelListener tableModelListener;
    /**
     * DOCUMENT ME!
     */
    private final Map<Class<?>, Comparator<?>> columnComparators = new HashMap<Class<?>, Comparator<?>>();
    /**
     * DOCUMENT ME!
     */
    private final List<Directive> sortingColumns = new ArrayList<Directive>();
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Indirection array.
     *
     * Contains the model column for any given displayed column.
     * modelColumn = _viewIndex[viewColumn];
     */
    private int[] _viewIndex;
    /** Proxy to the preference values */
    private Preferences _preferences;
    /** Proxy to the model data */
    private CalibratorsModel _calibratorsModel;
    /** The cellrendered that works with every columns */
    private TableCellColors _tableCellColors;
    /**
     * DOCUMENT ME!
     */
    private TableCellColorsEditor _tableCellColorsEditor;

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new TableSorter object.
     */
    public TableSorter() {
        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
    }

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel DOCUMENT ME!
     */
    public TableSorter(TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel DOCUMENT ME!
     * @param tableHeader DOCUMENT ME!
     */
    public TableSorter(TableModel tableModel, JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new TableSorter object, specialized for SearchCal needs.
     * (color cell renderers, simple/detailed views).
     *
     * @param tableModel DOCUMENT ME!
     * @param tableHeader DOCUMENT ME!
     */
    public TableSorter(CalibratorsModel tableModel, JTableHeader tableHeader) {
        this((TableModel) tableModel, tableHeader);

        _calibratorsModel = tableModel;

        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        _tableCellColors = new TableCellColors();
        _tableCellColorsEditor = new TableCellColorsEditor();

        computeColumnsIndirectionArray();
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * DOCUMENT ME!
     */
    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public TableModel getTableModel() {
        return tableModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param tableModel DOCUMENT ME!
     */
    public void setTableModel(TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;

        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * DOCUMENT ME!
     *
     * @param tableHeader DOCUMENT ME!
     */
    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener);

            TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();

            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
            }
        }

        this.tableHeader = tableHeader;

        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSorting() {
        return !sortingColumns.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Directive getDirective(final int column) {
        for (Directive directive : sortingColumns) {
            if (directive.column == column) {
                return directive;
            }
        }

        return EMPTY_DIRECTIVE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSortingStatus(int column) {
        return getDirective(_viewIndex[column]).direction;
    }

    /**
     * DOCUMENT ME!
     */
    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();

        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     * @param status DOCUMENT ME!
     */
    public void setSortingStatus(int column, int status) {
        ////////////////////////////////////////////////////////////////////////
        final int realColumn = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        Directive directive = getDirective(realColumn);

        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }

        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(realColumn, status));
        }

        sortingStatusChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Icon getHeaderRendererIcon(int column, int size) {
        Directive directive = getDirective(_viewIndex[column]);

        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }

        return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    /**
     * DOCUMENT ME!
     */
    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param comparator DOCUMENT ME!
     */
    public void setColumnComparator(Class<?> type, Comparator<?> comparator) {
        if (comparator == null) {
            columnComparators.remove(type);
        } else {
            columnComparators.put(type, comparator);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Comparator getComparator(final int column) {
        final Class<?> columnType = tableModel.getColumnClass(column);
        final Comparator<?> comparator = columnComparators.get(columnType);

        if (comparator != null) {
            return comparator;
        }

        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMPARATOR;
        }

        return LEXICAL_COMPARATOR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Row[] getViewToModel() {
        if (viewToModel == null) {
            final int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];

            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }

        return viewToModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param viewIndex DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int modelIndex(final int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int[] getModelToView() {
        if (modelToView == null) {
            final Row[] viewModel = getViewToModel();
            final int n = viewModel.length;

            modelToView = new int[n];

            for (int i = 0; i < n; i++) {
                modelToView[viewModel[i].modelIndex] = i;
            }
        }

        return modelToView;
    }

    // TableModel interface methods
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnCount() {
        int nbOfColumns = 0;

        if (tableModel != null) {
            // If the table is empty, should show NO columns at all.
            nbOfColumns = Math.min(tableModel.getColumnCount(), _viewIndex.length);
        }

        return nbOfColumns;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Override
    public String getColumnName(int column) {
        return tableModel.getColumnName(_viewIndex[column]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Override
    public Class<?> getColumnClass(int column) {
        return tableModel.getColumnClass(_viewIndex[column]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param row DOCUMENT ME!
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    /**
     * DOCUMENT ME!
     *
     * @param row DOCUMENT ME!
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getValueAt(final int row, final int column) {
        return tableModel.getValueAt(modelIndex(row), _viewIndex[column]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param aValue DOCUMENT ME!
     * @param row DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
        tableModel.setValueAt(aValue, modelIndex(row), _viewIndex[column]);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Automatically called whenever the observed model changed
     */
    public void computeColumnsIndirectionArray() {
        final String scenario;

        // Get the scenario
        if (_calibratorsModel.getBrightScenarioFlag()) {
            scenario = "bright";
        } else {
            scenario = "faint";
        }

        // Get the magnitude band
        final String magnitude = _calibratorsModel.getMagnitudeBand();

        final String selectedView;

        // Get the detailed/simple view flag state
        if ((magnitude != null) && (_preferences.getPreferenceAsBoolean(PreferenceKey.VERBOSITY_SYNTHETIC_FLAG))) {
            selectedView = "view.columns.simple." + scenario + "." + magnitude;
        } else if ((magnitude != null) && (_preferences.getPreferenceAsBoolean(PreferenceKey.VERBOSITY_DETAILED_FLAG))) {
            selectedView = "view.columns.detailed." + scenario + "." + magnitude;
        } else {
            selectedView = null;
        }

        // Compute the corresponding preference path
        if (_logger.isDebugEnabled()) {
            _logger.debug("Selected view = '" + ((selectedView != null) ? selectedView : "RAW") + "'.");
        }

        final String prefColumns;
        if (selectedView == null) {
            prefColumns = null;
        } else {
            prefColumns = _preferences.getPreference(selectedView);

            if (prefColumns == null) {
                _logger.error("No preference found for [{}]", selectedView);
            }
        }

        // Either simple or detailed views
        if (prefColumns != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Columns (preferences) = " + prefColumns);
            }
            // Get the selected ordered column name table
            final String[] columnStrings = prefColumns.trim().split(" ");
            final int nbOfColumns = columnStrings.length;

            _viewIndex = new int[nbOfColumns];

            String columnName;
            int columnId;

            for (int i = 0, len = Math.min(nbOfColumns, _calibratorsModel.getColumnCount()); i < len; i++) {
                columnName = columnStrings[i];

                if (columnName != null) {
                    // Get each column name column index
                    columnId = _calibratorsModel.getColumnIdByName(columnName);
                    _viewIndex[i] = columnId;

                    // If no column Id was found for the given column name
                    if (columnId == -1) {
                        _logger.warn("No column called '{}'.", columnName);
                    } else {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("_viewIndex[" + i + "] = '" + columnId + "' -> '" + columnName + "'.");
                        }
                    }
                }
            }

            // Remove any -1 (eg column name not found) !!!
            int shift = 0;

            for (int i = 0; i < (nbOfColumns - shift); i++) {
                if (_viewIndex[i] == -1) {
                    shift++;
                }

                _viewIndex[i] = _viewIndex[i + shift];
            }

            // Create a new array of this with the right size
            final int[] result = new int[nbOfColumns - shift];

            // Copy back all the meaningfull result in the rightly sized array
            for (int i = 0, len = result.length; i < len; i++) {
                result[i] = _viewIndex[i];
            }

            _viewIndex = result;
        } else { // Full view, with all columns

            // Get column count and allocate correspondaing memory for the inderection array
            final int nbOfColumns = _calibratorsModel.getColumnCount();
            _viewIndex = new int[nbOfColumns];

            // Generate a 'one to one' inderection array to show every single column
            for (int i = 0; i < nbOfColumns; i++) {
                _viewIndex[i] = i;
            }
        }
    }

    /**
     * Automatically called whenever the observed model changed
     */
    public void update(Observable o, Object arg) {
        computeColumnsIndirectionArray();

        _calibratorsModel.update(null, this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper classes
    private final class Row implements Comparable {

        final int modelIndex;

        public Row(final int index) {
            this.modelIndex = index;
        }

        @SuppressWarnings("unchecked")
        public int compareTo(final Object o) {
            final int row1 = modelIndex;
            final int row2 = ((Row) o).modelIndex;

            int column, comparison;
            Object o1, o2;

            for (Directive directive : sortingColumns) {
                column = directive.column;
                o1 = tableModel.getValueAt(row1, column);
                o2 = tableModel.getValueAt(row2, column);

                // Define null less than everything, except null.
                if ((o1 == null) && (o2 == null)) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                    comparison = getComparator(column).compare(o1, o2);
                }

                if (comparison != 0) {
                    return (directive.direction == DESCENDING) ? -comparison : comparison;
                }
            }

            return 0;
        }
    }

    private final class TableModelHandler implements TableModelListener {

        public void tableChanged(TableModelEvent e) {
            ////////////////////////////////////////////////////////////////////////
            computeColumnsIndirectionArray();

            // Use the internal cell renderer with origin and confidence
            final TableColumnModel columnModel = tableHeader.getColumnModel();
            for (int i = 0, len = columnModel.getColumnCount(); i < len; i++) {
                TableColumn tc = columnModel.getColumn(i);
                tc.setCellRenderer(_tableCellColors);
                tc.setCellEditor(_tableCellColorsEditor);
            }

            ////////////////////////////////////////////////////////////////////////

            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);

                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);

                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();

            if ((e.getFirstRow() == e.getLastRow())
                    && (column != TableModelEvent.ALL_COLUMNS)
                    && (getSortingStatus(column) == NOT_SORTED)
                    && (modelToView != null)) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(TableSorter.this,
                        viewIndex, viewIndex, column, e.getType()));

                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();
        }
    }

    private final class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();

            if (column != -1) {
                int status = getSortingStatus(column);

                if (!e.isControlDown()) {
                    cancelSorting();
                }

                // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                status += (e.isShiftDown() ? (-1) : 1);
                status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}
                setSortingStatus(column, status);
            }
        }
    }

    private final static class Arrow implements Icon {

        private final boolean descending;
        private final int size;
        private final int priority;

        public Arrow(boolean descending, int size, int priority) {
            this.descending = descending;
            this.size = size;
            this.priority = priority;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            ////////////////////////////////////////////////////////////////////////
            Color color = (c == null) ? Color.red : c.getBackground();

            ////////////////////////////////////////////////////////////////////////
            //     Color color = (c == null) ? Color.GRAY : c.getBackground();

            // In a compound sort, make each succesive triangle 20%
            // smaller than the previous one.
            int dx = (int) (size / 2 * Math.pow(0.8, priority));
            int dy = descending ? dx : (-dx);
            // Align icon (roughly) with font baseline.
            y = y + ((5 * size) / 6) + (descending ? (-dy) : 0);

            int shift = descending ? 1 : (-1);
            g.translate(x, y);

            // Right diagonal.
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);

            // Left diagonal.
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);

            // Horizontal line.
            if (descending) {
                g.setColor(color.darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }

            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }
    }

    private final class SortableHeaderRenderer implements TableCellRenderer {

        final TableCellRenderer tableCellRenderer;

        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            final Component c = tableCellRenderer.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setHorizontalTextPosition(JLabel.LEFT);

                ////////////////////////////////////////////////////////////////////////////
                final int modelColumn = table.convertColumnIndexToModel(column);

                ////////////////////////////////////////////////////////////////////////////
                l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));

                ////////////////////////////////////////////////////////////////////////////

                // Set the column header tooltip (with unit if any)
                final int viewColumn = _viewIndex[modelColumn];

                String tooltip = _calibratorsModel.getHeaderTooltipForColumn(viewColumn);
                final String unit = _calibratorsModel.getHeaderUnitForColumn(viewColumn);

                // If a unit was found
                if (unit.length() != 0) {
                    // If a description was found
                    if (tooltip.length() != 0) {
                        // Add a space separator between description and unit
                        tooltip += " ";
                    }

                    // Append the unit
                    tooltip += ("(" + unit + ")");
                }

                l.setToolTipText(tooltip);

                ////////////////////////////////////////////////////////////////////////////
            }

            return c;
        }
    }

    private final static class Directive {

        final int column;
        final int direction;

        public Directive(int column, int direction) {
            this.column = column;
            this.direction = direction;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Used to display colorized cells.
     *
     * @warning: No trace log implemented as this is often called (performance).
     */
    private final class TableCellColors extends DefaultTableCellRenderer
            implements Observer {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private HashMap<String, Color> _colorForOrigin;
        private HashMap<String, Color> _colorForConfidence;
        private Preferences _preferences;
        // Get the prefered distance to detect the science object
        private Double _prefDistance = 0.0;

        /**
         * TableCellColors  -  Constructor
         */
        public TableCellColors() {
            super();

            // Store the application preferences and register against it
            _preferences = Preferences.getInstance();
            _preferences.addObserver(this);
        }

        /**
         * getTableCellRendererComponent  -  return the component with renderer (Table)
         * @param table JTable
         * @param value Object
         * @param isSelected boolean
         * @param hasFocus boolean
         * @param row int
         * @param column int
         * @return Component
         */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            // Set default renderer to the component
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            final int modelColumn;
            try {
                modelColumn = _viewIndex[table.convertColumnIndexToModel(column)];
            } catch (ArrayIndexOutOfBoundsException ex) {
                // This code is reached when model / _viewIndex array / or table size mismatch
                // exact reason is not yet defined
                // @todo track source of mismatch
                _logger.warn("Error searching in the table model while trying to render cell at column "
                        + column + " table.getColumnCount()=" + table.getColumnCount()
                        + " _viewIndex.length=" + _viewIndex.length, ex);
                return this;
            }

            final CalibratorsModel calModel = ((CalibratorsModel) ((TableSorter) table.getModel()).getTableModel());

            // Get StarProperty selected using modelIndex Method
            final int modelRow = modelIndex(row);

            String tooltip = null;
            Color foregroundColor = Color.BLACK;
            Color backgroundColor = Color.WHITE;
            final Font cellFont = getFont();

            final StarProperty starProperty = calModel.getStarProperty(modelRow, modelColumn);
            if (starProperty != null) {

                // Set Background Color corresponding to the Catalog Origin Color or confidence index
                if (starProperty.hasOrigin()) {
                    // Get origin and set it as tooltip
                    final String origin = starProperty.getOrigin();

                    final Catalog catalog = Catalog.catalogFromReference(origin);

                    final String originDescription = "Catalog origin: " + ((catalog != null) ? catalog : origin); // Diff tool

                    // If tooltip already contains an explanation about clickable cells
                    // TODO: clarify this as tooltip var is null !!!
                    if (tooltip != null) {
                        // Add catalog origin to it
                        tooltip += originDescription;
                    } else {
                        // Only use catalog origin as tooltip
                        tooltip = originDescription;
                    }

                    // Get origin color and set it as cell backgroung color
                    backgroundColor = _colorForOrigin.get(origin);

                } else if (starProperty.hasConfidence()) {
                    // Get confidence and set it as tooltip
                    final String confidence = starProperty.getConfidence();
                    tooltip = "Computed value (confidence index: " + confidence + ")";

                    // Get confidence color and set it as cell backgroung color
                    backgroundColor = _colorForConfidence.get(confidence);

                } else if (starProperty.hasValue()) {
                    // If something bad happened, write text in red !
                    foregroundColor = Color.RED;
                    tooltip = "!!! BUG (no origin nor confidence) !!!";

                } else {
                    // If the property has no origin nor confidence: it is empty
                    tooltip = null;
                }
            }

            if (backgroundColor == null) {
                // Diff tool case:
                backgroundColor = Color.RED;
            }

            // Set tooltip (if any)
            if (tooltip != null) {
                setToolTipText(tooltip);
            } else {
                setToolTipText(null);
            }

            // Do not change color if cell is located on a selected row
            if (table.isRowSelected(row)) {
                // Except if it is the selected cell itself (to highlight found tokens)
                if (table.isColumnSelected(column)) {
                    final Border border = BorderFactory.createLineBorder(Color.ORANGE, 2);
                    setBorder(border);
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);
                    setFont(cellFont.deriveFont(cellFont.getStyle() | Font.ITALIC));
                }

                return this;
            }

            final int distId = calModel.getColumnIdByName("dist");

            // If the current row distance is close enough to be detected as a science object
            // @note SCIENCE_DISTANCE_CHECK : The same is used in ASPRO for science object star detection and removal.
            final StarProperty distanceProperty = calModel.getStarProperty(modelRow, distId);

            if (distanceProperty != null) {
                final double rowDistance = distanceProperty.getDoubleValue();
                if (rowDistance < _prefDistance) {
                    // Put the corresponding row font in bold
                    setFont(cellFont.deriveFont(cellFont.getStyle() | Font.BOLD));

                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Put row['" + row
                                + "'] in BOLD : (rowDistance = '" + rowDistance
                                + "') < (prefDistance = '" + _prefDistance + "').");
                    }
                }
            }

            // Compose catalog URL
            if (starProperty != null && calModel.hasURL(modelColumn)) {
                final String cellValue = starProperty.getString();

                if (cellValue != null) {
                    setText("<html><a href='#empty'>" + cellValue + "</a></html>");
                }
            }

            // If cell is not selected and not focused 
            if (!(isSelected && hasFocus)) {
                // Apply colors
                setForeground(foregroundColor);
                setBackground(backgroundColor);
                setBorder(noFocusBorder);
            }
            // Return the component
            return this;
        }

        /**
         * Automatically called whenever color preferences change.
         */
        public void update(Observable o, Object arg) {
            // React to preferences changes
            if (o.equals(_preferences)) {
                // Get science object detection distance
                _prefDistance = _preferences.getPreferenceAsDouble(PreferenceKey.QUERY_SCIENCE_DETECTION);

                // Read colors preferences for catalogs
                String prefix = Preferences.PREFIX_CATALOG_COLOR;
                Enumeration e = _preferences.getPreferences(prefix);
                _colorForOrigin = new HashMap<String, Color>();

                while (e.hasMoreElements()) {
                    String entry = (String) e.nextElement();
                    String catalogName = entry.substring(prefix.length());

                    try {
                        Color catalogColor = _preferences.getPreferenceAsColor(entry);
                        _colorForOrigin.put(catalogName, catalogColor);
                    } catch (PreferencesException pe) {
                        _logger.warn("Could not get catalog color from preference : ", pe);
                    }
                }

                // Read colors preferences for confidences
                prefix = Preferences.PREFIX_CONFIDENCE_COLOR;
                e = _preferences.getPreferences(prefix);
                _colorForConfidence = new HashMap<String, Color>();

                while (e.hasMoreElements()) {
                    String entry = (String) e.nextElement();
                    String confidenceName = entry.substring(prefix.length());

                    try {
                        Color confidenceColor = _preferences.getPreferenceAsColor(entry);
                        _colorForConfidence.put(confidenceName, confidenceColor);
                    } catch (PreferencesException pe) {
                        _logger.warn("Could not get confidence color from preference : ", pe);
                    }
                }
            }
        }
    }

    final class TableCellColorsEditor extends AbstractCellEditor implements TableCellEditor {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private StarProperty _starProperty = null;

        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {

            // Retrieve clicked cell informations
            final int modelRow = modelIndex(row);
            final int modelColumn = _viewIndex[table.convertColumnIndexToModel(column)];
            final CalibratorsModel calModel = ((CalibratorsModel) ((TableSorter) table.getModel()).getTableModel());
            _starProperty = calModel.getStarProperty(modelRow, modelColumn);

            final String cellValue = (_starProperty != null) ? _starProperty.getString() : null;

            // If the cell is empty
            if (cellValue == null) {
                return null; // Exit
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("getTableCellEditorComponent(" + row + "," + column
                        + ") = '" + value + "' <==> Model[" + modelRow + ","
                        + modelColumn + "] = '" + cellValue + "'.");
            }

            if (calModel.hasURL(modelColumn)) {
                final String url = calModel.getURL(modelColumn, _starProperty.getStringValue());

                if (_logger.isDebugEnabled()) {
                    _logger.debug("User clicked on column '"
                            + calModel.getColumnNameById(modelColumn)
                            + "' in the CalibratorView, will open '" + url
                            + "' in default browser.");
                }

                // Open web browser with the computed URL
                BrowserLauncher.openURL(url);
            }

            // Return null to "cancel" editing
            return null;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        public Object getCellEditorValue() {
            // Should not be called
            _logger.error("TableCellColorsEditor.getCellEditorValue() should have not been called.");

            return _starProperty;
        }
    }
////////////////////////////////////////////////////////////////////////////
}
