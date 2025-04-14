package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.Band;
import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.component.ArrowIcon;
import fr.jmmc.jmcs.gui.component.Directive;
import fr.jmmc.jmcs.gui.component.Row;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.service.BrowserLauncher;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
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
 * TableSorter registers itself as a listener to the underlying model,
 * just as the JTable itself would. Events recieved from the model
 * are examined, sometimes manipulated (typically widened), and then
 * passed on to the TableSorter's listeners (typically the JTable).
 * If a change to the model has invalidated the order of TableSorter's
 * rows, a note of this is made and the sorter will resort the
 * rows the next time a value is requested.
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
    /** System property to disable distance check in renderer */
    public static final String DISABLE_DISTANCE_RENDERER = "TableSorter.renderer.disable.distance";
    /** flag indicating to enable renderer distance test */
    private final static boolean CELL_RENDERER_USE_DISTANCE_TEST = !("true".equalsIgnoreCase(System.getProperty(TableSorter.DISABLE_DISTANCE_RENDERER, "false")));
    /** Ascending Sort */
    public static final int ASCENDING = 1;
    /** Not Sorted */
    public static final int NOT_SORTED = 0;
    /** Descending Sort */
    public static final int DESCENDING = -1;
    /** empty sort directive*/
    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED, null, null);
    /**
     * comparison based on Comparable interface
     */
    public static final Comparator<Object> COMPARABLE_COMPARATOR = new Comparator<Object>() {
        @Override
        @SuppressWarnings("unchecked")
        public int compare(final Object o1, final Object o2) {
            return ((Comparable) o1).compareTo(o2);
        }
    };
    /**
     * Lexical comparison ie compare String values ignoring case.
     */
    public static final Comparator<Object> LEXICAL_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(final Object o1, final Object o2) {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }
    };

    /* members */
    /** source data model */
    private TableModel tableModel;
    /** sorted rows mapping the view to the source model */
    private Row[] viewToModel;
    /** mapping from the source model index to the view index */
    private int[] modelToView;
    /** table header */
    private JTableHeader tableHeader;
    private MouseListener mouseListener;
    TableModelListener tableModelListener;
    private final List<Directive> sortingColumns = new ArrayList<Directive>(4);
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
    private TableCellColorsEditor _tableCellColorsEditor;

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
     * @param tableModel
     */
    public TableSorter(TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel
     * @param tableHeader
     */
    public TableSorter(TableModel tableModel, JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    /**
     * Creates a new TableSorter object, specialized for SearchCal needs.
     * (color cell renderers, simple/detailed views).
     *
     * @param tableModel
     * @param tableHeader
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

    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    /**
     * @return the source table model
     */
    public TableModel getTableModel() {
        return tableModel;
    }

    /**
     * Defines the source data model
     * @param tableModel source data model
     */
    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;

        // Multiple table model listeners => ordering side effects !!!
        clearSortingState();
        fireTableStructureChanged();
    }

    /**
     * @return table header
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * Define the table header (to customize)
     * @param tableHeader table header
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

    public boolean isSorting() {
        return !sortingColumns.isEmpty();
    }

    private Directive getDirective(final int column) {
        for (Directive directive : sortingColumns) {
            if (directive.column == column) {
                return directive;
            }
        }

        return EMPTY_DIRECTIVE;
    }

    /**
     * @param column column index
     * @return integer value in (ASCENDING = 1, NOT_SORTED = 0, DESCENDING = -1)
     */
    public int getSortingStatus(int column) {
        return getDirective(columnModelIndex(column)).direction;
    }

    /**
     * Defines the column sorting
    @param column column index
    @param status integer value in (ASCENDING = 1, NOT_SORTED = 0, DESCENDING = -1)
     */
    public void setSortingStatus(final int column, final int status) {
        final int modelColumn = columnModelIndex(column);

        final Directive directive = getDirective(modelColumn);

        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }

        if (status != NOT_SORTED) {
            sortingColumns.add(createDirective(modelColumn, status));
        }

        _logger.debug("setSortingStatus: {}", sortingColumns);

        sortingStatusChanged();
    }

    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();

        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    Icon getHeaderRendererIcon(final int column, final int size) {
        final Directive directive = getDirective(columnModelIndex(column));

        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new ArrowIcon(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    private void checkSortingState() {
        for (ListIterator<Directive> it = sortingColumns.listIterator(); it.hasNext();) {
            Directive directive = it.next();

            // Get the current column index given its name:
            final int columnId = _calibratorsModel.getColumnIdByName(directive.colName);

            // check column indexes:
            if (columnId != directive.column) {
                if (columnId != -1) {
                    // update directive:
                    directive = createDirective(columnId, directive.direction);
                    _logger.debug("update sorting directive: {}", directive);
                    it.set(directive);
                } else {
                    // missing column:
                    _logger.debug("remove sorting directive: {}", directive);
                    it.remove();
                }
            }
        }
        clearSortingState();
    }

    private Directive createDirective(final int realColumn, final int direction) {
        // note: may return a virtual column name (AA, AB, etc.) if realColumn > column count
        // see AbstractTableModel.getColumnName(int)
        final String colName = tableModel.getColumnName(realColumn);

        return new Directive(realColumn, direction, getComparator(realColumn), colName);
    }

    private Comparator<Object> getComparator(final int realColumn) {
        final Class<?> columnType = tableModel.getColumnClass(realColumn);

        if (_logger.isDebugEnabled()) {
            _logger.debug("getComparator({}): {}", tableModel.getColumnName(realColumn), columnType);
        }

        if (String.class == columnType) {
            return LEXICAL_COMPARATOR;
        }

        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMPARATOR;
        }

        return LEXICAL_COMPARATOR;
    }

    private Row[] getViewToModel() {
        if (viewToModel == null) {

            final int tableModelRowCount = tableModel.getRowCount();
            final Row[] newModel = new Row[tableModelRowCount];

            final Row.RowState state = new Row.RowState(tableModel, sortingColumns);

            for (int row = 0; row < tableModelRowCount; row++) {
                newModel[row] = new Row(state, row);
            }

            if (isSorting()) {
                final long start = System.nanoTime();

                Arrays.sort(newModel);

                if (_logger.isDebugEnabled()) {
                    _logger.debug("sort ({} rows) processed in {} ms.", tableModelRowCount, 1e-6d * (System.nanoTime() - start));
                }
            }

            // update model once:
            viewToModel = newModel;
        }

        return viewToModel;
    }

    /**
     * @param viewIndex row index in the view
     * @return row index in the source model
     */
    public int modelIndex(final int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    private int[] getModelToView() {
        if (modelToView == null) {
            final Row[] viewModel = getViewToModel();
            final int len = viewModel.length;

            modelToView = new int[len];

            for (int i = 0; i < len; i++) {
                modelToView[viewModel[i].modelIndex] = i;
            }
        }

        return modelToView;
    }

    /**
     * @param modelIndex row index in the source model
     * @return row index in the view
     */
    public int viewIndex(final int modelIndex) {
        return getModelToView()[modelIndex];
    }

    /**
     * @param column column index in the view
     * @return model column index in the source model
     */
    public int columnModelIndex(final int column) {
        return _viewIndex[column];
    }

    /**
     * Find the column index in the view
     * @param modelColumn model column index
     * @return view index of this column or -1 if not found
     */
    public int findColumnViewIndex(final int modelColumn) {
        if (modelColumn != -1) {
            for (int i = 0, len = _viewIndex.length; i < len; i++) {
                if (_viewIndex[i] == modelColumn) {
                    return i;
                }
            }
        }
        return -1;
    }

    // TableModel interface methods
    @Override
    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    @Override
    public int getColumnCount() {
        int nbOfColumns = 0;

        if (tableModel != null) {
            // If the table is empty, should show NO columns at all.
            nbOfColumns = Math.min(tableModel.getColumnCount(), _viewIndex.length);
        }

        return nbOfColumns;
    }

    @Override
    public String getColumnName(final int column) {
        return tableModel.getColumnName(columnModelIndex(column));
    }

    @Override
    public Class<?> getColumnClass(final int column) {
        return tableModel.getColumnClass(columnModelIndex(column));
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return tableModel.isCellEditable(modelIndex(row), columnModelIndex(column));
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        return tableModel.getValueAt(modelIndex(row), columnModelIndex(column));
    }

    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
        tableModel.setValueAt(aValue, modelIndex(row), columnModelIndex(column));
    }

    /**
     * Automatically called whenever the observed model changed
     */
    public void computeColumnsIndirectionArray() {

        // Get the scenario
        final boolean bright = _calibratorsModel.getBrightScenarioFlag();
        final String scenario = (bright) ? "bright" : "faint";

        // Get the magnitude band:
        String band = _calibratorsModel.getMagnitudeBand();
        if (bright) {
            // "V", "J", "H", "K", "L", "M", "N"
            if (!"V".equals(band)) {
                if ("J".equals(band) || "H".equals(band) || "K".equals(band)) {
                    // JHK
                    band = "K";
                } else {
                    // LMN:
                    band = "N";
                }
            }
        } else {
            // faint = JHK
            band = "K";
        }

        // Get the detailed/simple view flag state
        final String selectedView;
        if ((band != null) && (_preferences.getPreferenceAsBoolean(PreferenceKey.VERBOSITY_SYNTHETIC_FLAG))) {
            selectedView = Preferences.PREFIX_VIEW_COLUMNS_SIMPLE + scenario + '.' + band;
        } else if ((band != null) && (_preferences.getPreferenceAsBoolean(PreferenceKey.VERBOSITY_DETAILED_FLAG))) {
            selectedView = Preferences.PREFIX_VIEW_COLUMNS_DETAILED + scenario + '.' + band;
        } else {
            // Full view:
            selectedView = null;
        }

        // Compute the corresponding preference path
        if (_logger.isDebugEnabled()) {
            _logger.debug("Selected view = '{}'.", ((selectedView != null) ? selectedView : "RAW"));
        }

        final String prefColumns;

        if (selectedView == null) {
            prefColumns = null;
        } else {
            // ignore missing:
            prefColumns = _preferences.getPreference(selectedView, true);

            if (prefColumns == null) {
                _logger.error("No preference found for [{}]", selectedView);
            }
        }

        // Get column count in the model:
        final int nbOfModelColumns = _calibratorsModel.getColumnCount();

        // Either simple or detailed views
        if ((prefColumns != null) && (nbOfModelColumns != 0)) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Columns (preferences) = {}", prefColumns);
            }

            final String altColumns;
            if (_calibratorsModel.isSearchCalServerVersion5()) {
                altColumns = prefColumns;
            } else {
                // add supplementary columns (6.0+):
                altColumns = prefColumns
                        + (("V".equals(band)) ? " G" : "")
                        + " BADCAL CalFlag"
                        + (("N".equals(band)) ? " IRFlag Lflux_med Mflux_med Nflux_med" : "");
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("Used Columns = {}", altColumns);
            }

            // Get the selected ordered column name table
            final String[] columnStrings = altColumns.trim().split(" ");
            final int nbOfColumns = columnStrings.length;

            final Set<String> missingCols = _calibratorsModel.getIgnoreMissingColumns();

            final boolean isJy = _preferences.getPreferenceAsBoolean(PreferenceKey.FLUX_EDITOR_JY);

            // Use list to keep only valid columns:
            final List<Integer> viewIndex = new ArrayList<Integer>(nbOfColumns);

            for (int i = 0; i < nbOfColumns; i++) {
                String columnName = columnStrings[i];

                // use the converted flux in Jy for LMN bands instead of magnitude:
                if (isJy && (columnName.length() == 1)) {
                    if (Band.L.name().equals(columnName)) {
                        columnName = StarList.fluxLColumnName;
                    } else if (Band.M.name().equals(columnName)) {
                        columnName = StarList.fluxMColumnName;
                    } else if (Band.N.name().equals(columnName)) {
                        columnName = StarList.fluxNColumnName;
                    }
                }

                if (!StringUtils.isEmpty(columnName)) {
                    // Get the current column index given its name:
                    final int columnId = _calibratorsModel.getColumnIdByName(columnName);

                    // If no column Id was found for the given column name
                    if (columnId == -1) {
                        if (missingCols.add(columnName)) {
                            _logger.warn("No column called '{}'.", columnName);
                        }
                    } else {
                        viewIndex.add(NumberUtils.valueOf(columnId));
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("viewIndex[{}] = '{}' -> '{}'.", (viewIndex.size() - 1), columnId, columnName);
                        }
                    }
                }
            }

            // Create a new array of this with the right size
            final int rightSize = viewIndex.size();
            _viewIndex = new int[rightSize];

            // Copy back all the meaningfull result in the rightly sized array
            for (int i = 0; i < rightSize; i++) {
                _viewIndex[i] = viewIndex.get(i).intValue();
            }

        } else { // Full view, with all columns

            // allocate corresponding memory for the indirection array
            _viewIndex = new int[nbOfModelColumns];

            // Generate a 'one to one' indirection array to show every single column
            for (int i = 0; i < nbOfModelColumns; i++) {
                _viewIndex[i] = i;
            }
        }
    }

    /**
     * Automatically called whenever the observed model changed
     */
    @Override
    public void update(Observable o, Object arg) {
        computeColumnsIndirectionArray();

        _calibratorsModel.update(null, this);
    }

    // Helper classes
    private final class TableModelHandler implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {

            computeColumnsIndirectionArray();

            // Use the internal cell renderer with origin and confidence
            final TableColumnModel columnModel = tableHeader.getColumnModel();
            for (int i = 0, len = columnModel.getColumnCount(); i < len; i++) {
                TableColumn tc = columnModel.getColumn(i);
                tc.setCellRenderer(_tableCellColors);
                tc.setCellEditor(_tableCellColorsEditor);
            }

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
                // 2017: maintain sorting enabled when the user enable/disable filters or dynamic columns are updated (vis2 / dist)
                checkSortingState();
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
            final JTableHeader h = (JTableHeader) e.getSource();
            final TableColumnModel columnModel = h.getColumnModel();
            final int viewColumn = columnModel.getColumnIndexAtX(e.getX());

            if (viewColumn != -1) {
                final int column = columnModel.getColumn(viewColumn).getModelIndex();

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
    }

    private final class SortableHeaderRenderer implements TableCellRenderer {

        /* members */
        /** parent table cell header renderer */
        final TableCellRenderer tableCellRenderer;
        /** internal string buffer */
        final StringBuilder _buffer = new StringBuilder(128);
        /** text icon gap scaled */
        private final int textSpacing = SwingUtils.adjustUISizeCeil(4);

        /**
         * Protected constructor
         * @param tableCellRenderer parent  table cell header renderer
         */
        SortableHeaderRenderer(final TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                       final boolean isSelected, final boolean hasFocus,
                                                       final int row, final int column) {

            final Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel) {
                final JLabel jLabel = (JLabel) c;
                jLabel.setHorizontalTextPosition(JLabel.LEFT);
                jLabel.setHorizontalAlignment(JLabel.CENTER);
                jLabel.setIconTextGap(textSpacing);

                final int colIndex = table.convertColumnIndexToModel(column);

                if (colIndex != -1) {
                    jLabel.setIcon(getHeaderRendererIcon(colIndex, table.getRowHeight()));

                    // Set the column header tooltip (with unit if any)
                    final int modelColumn = columnModelIndex(colIndex);

                    final String tooltip = _calibratorsModel.getHeaderTooltipForColumn(modelColumn, _buffer);
                    jLabel.setToolTipText(tooltip);
                }
            }
            // Return the component
            return c;
        }
    }

    /**
     * Used to display colorized cells.
     *
     * @warning: No trace log implemented as this is very often called (performance).
     */
    private final class TableCellColors extends DefaultTableCellRenderer {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /* members */
        /** derived italic font */
        private Font _derivedItalicFont = null;
        /** derived bold font */
        private Font _derivedBoldFont = null;
        /** orange border for selected cell */
        private final Border _orangeBorder = BorderFactory.createLineBorder(Color.ORANGE, 2);
        /** internal string buffer */
        final StringBuilder _buffer = new StringBuilder(128);

        /**
         * TableCellColors  -  Constructor
         */
        TableCellColors() {
            super();
        }

        /**
         * Sets the <code>String</code> object for the cell being rendered to
         * <code>value</code>.
         *
         * @param value  the string value for this cell; if value is
         *          <code>null</code> it sets the text value to an empty string
         * @see JLabel#setText
         *
         */
        @Override
        public void setValue(final Object value) {
            String text = "";
            if (value != null) {
                if (value instanceof Double) {
                    text = NumberUtils.format(((Double) value).doubleValue());
                } else {
                    text = value.toString();
                }
            }
            setText(text);
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
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                       final boolean isSelected, final boolean hasFocus,
                                                       final int row, final int column) {

            // Set default renderer to the component
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // always use right alignment:
            setHorizontalAlignment(JLabel.RIGHT);

            final int colIndex = table.convertColumnIndexToModel(column);

            if (colIndex == -1) {
                // This code is reached when model / _viewIndex array / or table size mismatch
                // exact reason is not yet defined
                // @todo track source of mismatch
                _logger.warn("Error searching in the table model while trying to render cell at column {} table.getColumnCount() = {} _viewIndex.length = {}", column, table.getColumnCount(), _viewIndex.length);
                return this;
            }

            final int modelColumn = columnModelIndex(colIndex);

            final CalibratorsModel calModel = ((CalibratorsModel) ((TableSorter) table.getModel()).getTableModel());

            // Get StarProperty selected using modelIndex Method
            final int modelRow = modelIndex(row);

            final StringBuilder sb = _buffer;

            String tooltip = null;
            Color foregroundColor = Color.BLACK;
            Color backgroundColor = Color.WHITE;

            Origin origin = null;
            String confidence = null;

            final StarProperty starProperty = calModel.getStarProperty(modelRow, modelColumn);

            if (starProperty != StarProperty.EMPTY_STAR_PROPERTY && starProperty != null) {
                // Set Background Color corresponding to the Catalog Origin Color or confidence index
                if (starProperty.hasOrigin()) {
                    // Get origin and set it as tooltip
                    origin = starProperty.getOrigin();

                    sb.append("Catalog origin: ");

                    // use optional catalog reference stored in origin enum: 
                    final Catalog catalog = origin.getCatalog();

                    if (catalog != null) {
                        // use catalog description as tooltip
                        catalog.toString(sb);

                        // Get origin color (alias handling) and set it as cell background color
                        backgroundColor = TableCellColorsPreferenceListener.INSTANCE._colorForOrigin.get(catalog.reference());
                    } else {
                        // Only use catalog origin as tooltip (Diff tool) (note: no string creation)
                        sb.append(origin.toString());
                    }
                    tooltip = sb.toString();
                    sb.setLength(0); // recycle buffer

                } else if (starProperty.hasConfidence()) {
                    // Get confidence and set it as tooltip (note: no string creation)
                    confidence = starProperty.getConfidence().toString();

                    tooltip = sb.append("Computed value (confidence index: ").append(confidence).append(')').toString();
                    sb.setLength(0); // recycle buffer

                    // Get confidence color and set it as cell background color
                    backgroundColor = TableCellColorsPreferenceListener.INSTANCE._colorForConfidence.get(confidence);

                } else if (starProperty.hasValue()) {
                    // If something bad happened, write text in red !
                    foregroundColor = Color.RED;
                    tooltip = "(no origin nor confidence)";

                } else {
                    // If the property has no origin nor confidence: it is empty
                    tooltip = null;
                }

                if (backgroundColor == null) {
                    // Diff tool case: origin or confidence not found in color maps:
                    confidence = (origin != null) ? origin.toString() : (confidence != null) ? confidence : null;
                    if (confidence != null) {
                        if (confidence.startsWith("DIFF")) {
                            backgroundColor = Color.ORANGE;
                        } else if (confidence.startsWith("LEFT")) {
                            backgroundColor = Color.GREEN;
                        } else if (confidence.startsWith("RIGHT")) {
                            backgroundColor = Color.PINK;
                        } else if (confidence.startsWith("SKIP")) {
                            backgroundColor = Color.GRAY;
                        } else if (confidence.startsWith("LESS")) {
                            backgroundColor = Color.LIGHT_GRAY;
                        }
                    } else {
                        backgroundColor = Color.RED;
                    }
                }
            }

            // Set tooltip (if any)
            setToolTipText(tooltip);

            // Do not change color if cell is located on a selected row
            if (table.isRowSelected(row)) {
                // Except if it is the selected cell itself (to highlight found tokens)
                if (table.isColumnSelected(column)) {
                    setBorder(_orangeBorder);
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);

                    // Put the corresponding row font in italic:
                    if (_derivedItalicFont == null) {
                        // cache derived Font:
                        final Font cellFont = getFont();
                        _derivedItalicFont = cellFont.deriveFont(cellFont.getStyle() | Font.ITALIC);
                    }
                    setFont(_derivedItalicFont);
                }

                return this;
            }

            if (CELL_RENDERER_USE_DISTANCE_TEST) {
                final int distId = calModel.getColumnIdByName(StarList.DistColumnName);
                if (distId != -1) {
                    // If the current row distance is close enough to be detected as a science object
                    // @note SCIENCE_DISTANCE_CHECK : The same is used in ASPRO for science object star detection and removal.
                    final StarProperty distanceProperty = calModel.getStarProperty(modelRow, distId);

                    if (distanceProperty != null) {
                        final double prefDistance = TableCellColorsPreferenceListener.INSTANCE._prefDistance;
                        final double rowDistance = distanceProperty.getDoubleValue();
                        if (rowDistance < prefDistance) {
                            // Put the corresponding row font in bold:
                            if (_derivedBoldFont == null) {
                                // cache derived Font:
                                final Font cellFont = getFont();
                                _derivedBoldFont = cellFont.deriveFont(cellFont.getStyle() | Font.BOLD);
                            }
                            setFont(_derivedBoldFont);

                            if (_logger.isDebugEnabled()) {
                                _logger.debug("Put row['{}'] in BOLD : (rowDistance = '{}') < (prefDistance = '{}').", row, rowDistance, prefDistance);
                            }
                        }
                    }
                }
            }

            // Compose catalog URL
            if (value != null && starProperty != null && calModel.hasURL(modelColumn)) {
                setText(sb.append("<html><a href='#empty'>").append(value).append("</a></html>").toString());
                sb.setLength(0); // recycle buffer
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
    }

    final class TableCellColorsEditor extends AbstractCellEditor implements TableCellEditor {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        // This method is called when a cell value is edited by the user.
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
                                                     final int row, final int column) {

            // Retrieve clicked cell informations
            final int modelRow = modelIndex(row);

            final int colIndex = table.convertColumnIndexToModel(column);

            if (colIndex == -1) {
                return null;
            }

            final int modelColumn = columnModelIndex(colIndex);
            final CalibratorsModel calModel = ((CalibratorsModel) ((TableSorter) table.getModel()).getTableModel());

            final StarProperty starProperty = calModel.getStarProperty(modelRow, modelColumn);

            final String cellValue = (starProperty != null) ? starProperty.getString() : null;

            // If the cell is empty
            if (cellValue == null) {
                return null; // Exit
            }

            if (calModel.hasURL(modelColumn)) {
                final String url = calModel.getURL(modelColumn, cellValue);

                if (_logger.isDebugEnabled()) {
                    _logger.debug("User clicked on column '{}' in the CalibratorView, will open '{}' in default browser.", calModel.getColumnNameById(modelColumn), url);
                }

                // Open web browser with the computed URL
                BrowserLauncher.openURL(url);
            }

            // Return null to "cancel" editing
            return null;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        @Override
        public Object getCellEditorValue() {
            // Should not be called
            _logger.error("TableCellColorsEditor.getCellEditorValue() should have not been called.");

            return null;
        }
    }

    /**
     * Preference listener for the TableCellColors renderer instances
     */
    final static class TableCellColorsPreferenceListener implements Observer {

        /** singleton */
        static final TableCellColorsPreferenceListener INSTANCE = new TableCellColorsPreferenceListener();
        /* members */
        /** preferences */
        private final Preferences _preferences;
        /** color map for origins */
        HashMap<String, Color> _colorForOrigin;
        /** color map for confidence indexes */
        HashMap<String, Color> _colorForConfidence;
        /** Get the prefered distance to detect the science object */
        double _prefDistance = 0.0d;

        /**
         * Private constructor (registers to Preference changes)
         */
        private TableCellColorsPreferenceListener() {
            // Store the application preferences and register against it
            _preferences = Preferences.getInstance();
            _preferences.addObserver(this);

            // force initializing values:
            update(_preferences, null);
        }

        /**
         * Automatically called whenever color preferences change.
         */
        @Override
        public void update(final Observable o, final Object arg) {
            // React to preferences changes
            if (o.equals(_preferences)) {
                // Get science object detection distance
                _prefDistance = _preferences.getPreferenceAsDouble(PreferenceKey.QUERY_SCIENCE_DETECTION);

                // Read colors preferences for catalogs
                String prefix = Preferences.PREFIX_CATALOG_COLOR;
                Enumeration<String> e = _preferences.getPreferences(prefix);
                _colorForOrigin = new HashMap<String, Color>(32);

                while (e.hasMoreElements()) {
                    String entry = e.nextElement();
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
                _colorForConfidence = new HashMap<String, Color>(8);

                while (e.hasMoreElements()) {
                    String entry = e.nextElement();
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

}
