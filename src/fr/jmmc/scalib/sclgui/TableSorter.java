////////////////////////////////////////////////////////////////////////////////
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.astro.Catalog;
import fr.jmmc.mcs.gui.*;
import fr.jmmc.mcs.util.*;

////////////////////////////////////////////////////////////////////////////////
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

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
////////////////////////////////////////////////////////////////////////////////
public class TableSorter extends AbstractTableModel implements Observer ////////////////////////////////////////////////////////////////////////////////
// public class TableSorter extends AbstractTableModel
{

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.TableSorter");
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
    public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {

        public int compare(Object o1, Object o2) {
            return ((Comparable) o1).compareTo(o2);
        }
    };
    /**
     * DOCUMENT ME!
     */
    public static final Comparator LEXICAL_COMPARATOR = new Comparator() {

        public int compare(Object o1, Object o2) {
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
    private Map columnComparators = new HashMap();
    /**
     * DOCUMENT ME!
     */
    private List sortingColumns = new ArrayList();
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
     * (color cell renderers, simple/detailled views).
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

        computeColumnsInderectionArray();
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
            this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(
                    this.tableHeader.getDefaultRenderer()));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Directive getDirective(int column) {
        for (int i = 0; i < sortingColumns.size(); i++) {
            Directive directive = (Directive) sortingColumns.get(i);

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
        ////////////////////////////////////////////////////////////////////////
        column = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        return getDirective(column).direction;
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
        column = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        Directive directive = getDirective(column);

        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }

        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
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
        ////////////////////////////////////////////////////////////////////////
        column = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        Directive directive = getDirective(column);

        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }

        return new Arrow(directive.direction == DESCENDING, size,
                sortingColumns.indexOf(directive));
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
    public void setColumnComparator(Class type, Comparator comparator) {
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
    protected Comparator getComparator(int column) {
        Class columnType = tableModel.getColumnClass(column);
        Comparator comparator = (Comparator) columnComparators.get(columnType);

        if (comparator != null) {
            return comparator;
        }

        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMAPRATOR;
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
            int tableModelRowCount = tableModel.getRowCount();
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
    public int modelIndex(int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];

            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
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
        ////////////////////////////////////////////////////////////////////////
        int nbOfColumns = 0;

        if (tableModel != null) {
            // If the table is empty, should show NO columns at all.
            nbOfColumns = Math.min(tableModel.getColumnCount(),
                    _viewIndex.length);
        }

        return nbOfColumns;

        ////////////////////////////////////////////////////////////////////////
        // return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnName(int column) {
        ////////////////////////////////////////////////////////////////////////
        column = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        return tableModel.getColumnName(column);
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getColumnClass(int column) {
        ////////////////////////////////////////////////////////////////////////
        column = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        return tableModel.getColumnClass(column);
    }

    /**
     * DOCUMENT ME!
     *
     * @param row DOCUMENT ME!
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
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
    public Object getValueAt(int row, int column) {
        ////////////////////////////////////////////////////////////////////////
        column = _viewIndex[column];

        ////////////////////////////////////////////////////////////////////////
        return tableModel.getValueAt(modelIndex(row), column);
    }

    /**
     * DOCUMENT ME!
     *
     * @param aValue DOCUMENT ME!
     * @param row DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setValueAt(Object aValue, int row, int column) {
        ////////////////////////////////////////////////////////////////////////
        column = _viewIndex[column];
        ////////////////////////////////////////////////////////////////////////
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Automatically called whenever the observed model changed
     */
    public void computeColumnsInderectionArray() {
        _logger.entering("TableSorter", "computeColumnsInderectionArray");

        String scenario = "bright";

        // Get the scenario
        if (_calibratorsModel.getBrightScenarioFlag() == true) {
            scenario = "bright";
        } else {
            scenario = "faint";
        }

        // Get the magnitude band
        String magnitude = _calibratorsModel.getMagnitudeBand();

        String selectedView = null;

        // Get the detailled/simple view flag state
        if (_preferences.getPreferenceAsBoolean(
                "view.result.verbosity.synthetic") == true) {
            selectedView = "view.columns.simple." + scenario + "." + magnitude;
        } else if (_preferences.getPreferenceAsBoolean(
                "view.result.verbosity.detailled") == true) {
            selectedView = "view.columns.detailled." + scenario + "."
                    + magnitude;
        } else if (_preferences.getPreferenceAsBoolean(
                "view.result.verbosity.full") == true) {
            selectedView = null;
        }

        // Compute the corresponding preference path
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Selected view = '"
                + ((selectedView != null) ? selectedView : "RAW") + "'.");
        }

        if (selectedView != null) {
            // Get the selected ordered column name table
            String[] columnStrings = _preferences.getPreference(selectedView).trim().split(" ");
            int nbOfColumns = columnStrings.length;

            _viewIndex = new int[nbOfColumns];

            for (int i = 0;
                    i < Math.min(nbOfColumns, _calibratorsModel.getColumnCount());
                    i++) {
                String columnName = columnStrings[i];

                if (columnName != null) {
                    // Get each column name column index
                    int columnId = _calibratorsModel.getColumnIdByName(columnName);
                    _viewIndex[i] = columnId;

                    // If no column Id was found for the given column name
                    if (columnId < 0) {
                        _logger.warning("No column called '" + columnName
                                + "'.");
                    } else {
                        if (_logger.isLoggable(Level.FINE)) {
                            _logger.fine("_viewIndex[" + i + "] = '" + columnId
                                + "' -> '" + columnName + "'.");
                        }
                    }
                }
            }

            // Remove any -1 (eg column name not found) !!!
            int shift = 0;

            for (int i = 0; i < (nbOfColumns - shift); i++) {
                if (_viewIndex[i] < 0) {
                    shift++;
                }

                _viewIndex[i] = _viewIndex[i + shift];
            }

            // Create a new array of this with the right size
            int[] result = new int[nbOfColumns - shift];

            // Copy back all the meaningfull result in the rightly sized array
            for (int i = 0; i < result.length; i++) {
                result[i] = _viewIndex[i];
            }

            _viewIndex = result;
        } else // Full view, with all columns
        {
            // Get column count and allocate correspondaing memory for the inderection array
            int nbOfColumns = _calibratorsModel.getColumnCount();
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
        _logger.entering("TableSorter", "update");

        computeColumnsInderectionArray();

        _calibratorsModel.update(null, this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper classes
    private class Row implements Comparable {

        private int modelIndex;

        public Row(int index) {
            this.modelIndex = index;
        }

        public int compareTo(Object o) {
            int row1 = modelIndex;
            int row2 = ((Row) o).modelIndex;

            for (Iterator it = sortingColumns.iterator(); it.hasNext();) {
                Directive directive = (Directive) it.next();
                int column = directive.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                int comparison = 0;

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
                    return (directive.direction == DESCENDING) ? (-comparison)
                            : comparison;
                }
            }

            return 0;
        }
    }

    private class TableModelHandler implements TableModelListener {

        public void tableChanged(TableModelEvent e) {
            ////////////////////////////////////////////////////////////////////////
            computeColumnsInderectionArray();

            // Use the internal cell renderer with origin and confidence
            for (int i = 0; i < tableHeader.getColumnModel().getColumnCount();
                    i++) {
                TableColumn tc = tableHeader.getColumnModel().getColumn(i);
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

            return;
        }
    }

    private class MouseHandler extends MouseAdapter {

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
                status = status + (e.isShiftDown() ? (-1) : 1);
                status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}
                setSortingStatus(column, status);
            }
        }
    }

    private static class Arrow implements Icon {

        private boolean descending;
        private int size;
        private int priority;

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

    private class SortableHeaderRenderer implements TableCellRenderer {

        private TableCellRenderer tableCellRenderer;

        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Component c = tableCellRenderer.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setHorizontalTextPosition(JLabel.LEFT);

                ////////////////////////////////////////////////////////////////////////////
                int modelColumn = table.convertColumnIndexToModel(column);

                ////////////////////////////////////////////////////////////////////////////
                l.setIcon(getHeaderRendererIcon(modelColumn,
                        l.getFont().getSize()));

                ////////////////////////////////////////////////////////////////////////////

                // Set the column header tooltip (with unit if any)
                int viewColumn = _viewIndex[modelColumn];

                String tooltip = _calibratorsModel.getHeaderTooltipForColumn(viewColumn);
                String unit = _calibratorsModel.getHeaderUnitForColumn(viewColumn);

                // If a unit was found
                if (unit.length() > 0) {
                    // If a description was found
                    if (tooltip.length() > 0) {
                        // Add a spce separator betwenn description and unit
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

    private static class Directive {

        private int column;
        private int direction;

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
    private class TableCellColors extends DefaultTableCellRenderer
            implements Observer {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private HashMap<String, Color> _colorForOrigin;
        private HashMap<String, Color> _colorForConfidence;
        private Preferences _preferences;
        private CalibratorsModel _calModel;
        private int _distId;
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
            // _logger.entering("TableCellColors", "getTableCellRendererComponent");
            // _logger.fine("getTableCellRendererComponent(" + row +
            //  "," + column + ")");

            // Set default renderer to the component
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);

            // Generated tooltip
            String tooltip = null;

            _calModel = ((CalibratorsModel) ((TableSorter) table.getModel()).getTableModel());
            _distId = _calModel.getColumnIdByName("dist");

            // Get StarProperty selected using modelIndex Method
            int modelRow = modelIndex(row);
            int modelColumn;

            try {
                modelColumn = _viewIndex[table.convertColumnIndexToModel(column)];
            } catch (ArrayIndexOutOfBoundsException ex) {
                // This code is reached when model / _viewIndex array / or table size mismatch
                // exact reason is not yet defined
                // @todo track source of mismatch
                _logger.log(Level.WARNING, "Error searching in the table model while trying to render cell at column "
                        + column + " table.getColumnCount()=" + table.getColumnCount()
                        + " _viewIndex.length=" + _viewIndex.length, ex);
                return this;
            }

            String cellValue = "";
            StarProperty starProperty = _calModel.getStarProperty(modelRow,
                    modelColumn);

            if (starProperty != null) {
                cellValue = starProperty.getStringValue();
            }

            // Do not change color if cell is located onto a selected row
            int[] selectedRows = table.getSelectedRows();

            for (int i = 0; i < selectedRows.length; i++) {
                if (selectedRows[i] == row) {
                    // Should provide tooltips before !!!
                    return this;
                }
            }

            // Get the row's distance star property
            StarProperty distanceProperty = _calModel.getStarProperty(modelRow,
                    _distId);
            Double rowDistance = distanceProperty.getDoubleValue();

            // If the current row distance is close enough to be detected as a science object
            // @note SCIENCE_DISTANCE_CHECK : The same is used in ASPRO for science object star detection and removal.
            if (rowDistance < _prefDistance) {
                // Put the corresponding row font in bold
                Font f = getFont();
                setFont(f.deriveFont(f.getStyle() | Font.BOLD));
                
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("Put row['" + row
                        + "'] in BOLD : (rowDistance = '" + rowDistance
                        + "') < (prefDistance = '" + _prefDistance + "').");
                }
            }

            // Compose catalog URL
            if (starProperty.hasURL() == true) {
                if (cellValue.length() != 0) {
                    setText("<html><a href='#empty'>" + cellValue
                            + "</a></html>");
                    tooltip = "Click to see star '"
                            + _calModel.getColumnNameById(modelColumn)
                            + "' entry at CDS - ";
                }
            }

            // If cell is not selected and not focused 
            if (!(isSelected && hasFocus)) {
                // Set default text color to black
                Color foregroundColor = Color.BLACK;

                // Set default cell color to white
                Color backgroundColor = Color.WHITE;

                if (starProperty != null) {
                    // Set Background Color corresponding to the Catalog Origin Color or confidence index
                    if (starProperty.hasOrigin() == true) {
                        // Get origin and set it as tooltip
                        String origin = starProperty.getOrigin();

                        // If the cell as an origin ("-" is the used blanking value for empty cell origins)
                        if (!origin.equals("-")) {
                            String originDescription = "Catalog origin: "
                                    + Catalog.catalogFromReference(origin);

                            // If tooltip already contains an explanation about clickable cells
                            if (tooltip != null) {
                                // Add catalog origin to it
                                tooltip += originDescription;
                            } else {
                                // Only use catalog origin as tooltip
                                tooltip = originDescription;
                            }
                        }

                        // Get origin color and set it as cell backgroung color
                        backgroundColor = _colorForOrigin.get(origin);
                    } else if (starProperty.hasConfidence() == true) {
                        // Get confidence and set it as tooltip
                        String confidence = starProperty.getConfidence();
                        tooltip = "Confidence index: "
                                + confidence;

                        // Get confidence color and set it as cell backgroung color
                        backgroundColor = _colorForConfidence.get(confidence);
                    } else {
                        // If something bad appent, write text in red !
                        foregroundColor = Color.RED;
                        tooltip = "!!! BUG !!!";
                    }

                    // Apply colors
                    setForeground(foregroundColor);
                    setBackground(backgroundColor);
                }
            }

            // Set tooltip (if any)
            if (tooltip != null) {
                setToolTipText(tooltip);
            } else {
                setToolTipText("");
            }

            // Return the component
            return this;
        }

        /**
         * Automatically called whenever color preferences change.
         */
        public void update(Observable o, Object arg) {
            _logger.entering("TableCellColors", "update");

            // React to preferences changes
            if (o.equals(_preferences)) {
                // Get science object detection distance
                _prefDistance = _preferences.getPreferenceAsDouble(
                        "query.scienceObjectDetectionDistance");

                // Read colors preferences for catalogs
                String prefix = "catalog.color.";
                Enumeration e = _preferences.getPreferences(prefix);
                _colorForOrigin = new HashMap<String, Color>();

                while (e.hasMoreElements()) {
                    String entry = (String) e.nextElement();
                    String catalogName = entry.substring(prefix.length());

                    try {
                        Color catalogColor = _preferences.getPreferenceAsColor(entry);
                        _colorForOrigin.put(catalogName, catalogColor);
                    } catch (PreferencesException ex) {
                        _logger.log(Level.WARNING,
                                "Could not get catalog color from preference : ", ex);
                    }
                }

                // Read colors preferences for confidences
                prefix = "confidence.color.";
                e = _preferences.getPreferences(prefix);
                _colorForConfidence = new HashMap<String, Color>();

                while (e.hasMoreElements()) {
                    String entry = (String) e.nextElement();
                    String confidenceName = entry.substring(prefix.length());

                    try {
                        Color confidenceColor = _preferences.getPreferenceAsColor(entry);
                        _colorForConfidence.put(confidenceName, confidenceColor);
                    } catch (PreferencesException ex) {
                        _logger.log(Level.WARNING,
                                "Could not get confidence color from preference : ",
                                ex);
                    }
                }
            }
        }
    }

    class TableCellColorsEditor extends AbstractCellEditor
            implements TableCellEditor {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private StarProperty _starProperty = null;

        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            _logger.entering("TableCellColorsEditor",
                    "getTableCellEditorComponent()");

            // Retrieve clicked cell informations
            int modelRow = modelIndex(row);
            int modelColumn = _viewIndex[table.convertColumnIndexToModel(column)];
            CalibratorsModel calModel = ((CalibratorsModel) ((TableSorter) table.getModel()).getTableModel());
            _starProperty = calModel.getStarProperty(modelRow,
                    modelColumn);

            String cellValue = _starProperty.getStringValue();

            // If the cell is empty
            if (cellValue.length() == 0) {
                return null; // Exit
            }

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("getTableCellEditorComponent(" + row + "," + column
                    + ") = '" + value + "' <==> Model[" + modelRow + ","
                    + modelColumn + "] = '" + cellValue + "'.");
            }

            if (_starProperty.hasURL() == true) {
                String url = _starProperty.getURL();

                if (_logger.isLoggable(Level.FINER)) {
                    _logger.finer("User clicked on column '"
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
            _logger.severe(
                    "TableCellColorsEditor.getCellEditorValue() should have not been called.");

            return _starProperty;
        }
    }
    ////////////////////////////////////////////////////////////////////////////
}
