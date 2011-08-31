/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.gui.action.RegisteredPreferencedBooleanAction;
import fr.jmmc.jmcs.gui.action.RegisteredAction;

import fr.jmmc.sclgui.LegendView;
import fr.jmmc.sclgui.Preferences;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;

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
public class CalibratorsView extends JPanel implements TableModelListener,
        ListSelectionListener, Observer, Printable {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(CalibratorsView.class.getName());
    /** Show Legend action */
    //public static ShowLegendAction _showLegendAction;
    public static RegisteredPreferencedBooleanAction _showLegendAction = null;
    /** Synthetic Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _syntheticResultsVerbosityAction = null;
    /** Detailed Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _detailledResultsVerbosityAction = null;
    /** Full Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _fullResultsVerbosityAction = null;
    /** Delete action */
    public DeleteAction _deleteAction = null;
    /** Undelete action */
    public UndeleteAction _undeleteAction = null;
    /** The monitored data source displayed by the embedded JTable */
    private CalibratorsModel _calibratorsModel = null;
    /** The monitored application preferences */
    private Preferences _preferences = null;
    /** The left-most static one column table containing star ID */
    private JTable _calibratorsIdTable = null;
    /** The calibrators table */
    private JTable _calibratorsTable = null;
    /** The calibrator table sorter */
    private TableSorter _tableSorter = null;
    /** Calibrators table and Legend container */
    private JSplitPane _tableAndLegendPane = null;

    /**
     * Constructor.
     *
     * @param calibratorsModel the data to be displayed.
     */
    public CalibratorsView(CalibratorsModel calibratorsModel) {
        String classPath = getClass().getName();

        // Store the model and register against it
        _calibratorsModel = calibratorsModel;
        _calibratorsModel.addTableModelListener(this);

        // Store the application preferences and register against it
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        // Create actions
        _deleteAction = new DeleteAction(classPath,
                "_deleteAction");
        _undeleteAction = new UndeleteAction(classPath,
                "_undeleteAction");
        _showLegendAction = new RegisteredPreferencedBooleanAction(classPath,
                "_showLegendAction", "Show Legend", _preferences,
                "view.legend.show");
        _syntheticResultsVerbosityAction = new RegisteredPreferencedBooleanAction(classPath,
                "_syntheticResultsVerbosityAction", "Synthetic", _preferences,
                "view.result.verbosity.synthetic");
        _detailledResultsVerbosityAction = new RegisteredPreferencedBooleanAction(classPath,
                "_detailledResultsVerbosityAction", "Detailled", _preferences,
                "view.result.verbosity.detailled");
        _fullResultsVerbosityAction = new RegisteredPreferencedBooleanAction(classPath,
                "_fullResultsVerbosityAction", "Full", _preferences,
                "view.result.verbosity.full");

        // Gray border of the view.
        updateBorderTitle();

        // Size management
        setMinimumSize(new Dimension(895, 320));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Table initialization
        _calibratorsTable = new JTable();

        // Handle the selection listener
        _calibratorsTable.getSelectionModel().addListSelectionListener(this);

        // Configure table sorting
        _tableSorter = new TableSorter(_calibratorsModel,
                _calibratorsTable.getTableHeader());
        _calibratorsTable.setModel(_tableSorter);
        _calibratorsIdTable = new JTable(_tableSorter,
                new DefaultTableColumnModel(), null);
        _calibratorsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _calibratorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Become listselectionListener to forward selected list to model
        _calibratorsTable.getSelectionModel().addListSelectionListener(this);

        // Place tables into scrollPane
        JScrollPane scrollPane = new JScrollPane(_calibratorsTable);
        scrollPane.setMinimumSize(new Dimension(570, 160));
        scrollPane.setPreferredSize(new Dimension(570, 260));

        // Fixed Row Table Headers
        JTable rowHeader = new JTable(_calibratorsModel._rowHeadersModel);
        rowHeader.setIntercellSpacing(new Dimension(0, 0));

        Dimension d = rowHeader.getPreferredScrollableViewportSize();
        d.width = 50;
        rowHeader.setPreferredScrollableViewportSize(d);
        rowHeader.setRowHeight(_calibratorsTable.getRowHeight());
        // @TODO : allow selection that select main table correspondins rows -> must derive JTable :((
        rowHeader.setRowSelectionAllowed(false);
        scrollPane.setRowHeaderView(rowHeader);

        JTableHeader corner = rowHeader.getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);

        // Set Minimum Width of legend component
        JPanel legendPanel = new LegendView(false);
        int legendWidth = Math.min(legendPanel.getPreferredSize().width, 200);
        legendPanel.setMinimumSize(new Dimension(legendWidth, 0));
        legendPanel.setSize(new Dimension(0, 0));

        // Set and place TableId, Table and Legend group
        _tableAndLegendPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollPane, legendPanel);
        _tableAndLegendPane.setOneTouchExpandable(true);
        _tableAndLegendPane.setResizeWeight(1.0);
        _tableAndLegendPane.setContinuousLayout(true);
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
    }

    private void updateBorderTitle() {
        // Colored border of the view.
        Border grayBorder = BorderFactory.createLineBorder(Color.gray, 1);
        String title = "Found Calibrators";
        int total = _calibratorsModel.getTotalNumberOfStar();
        title += " (" + total + " sources";
        if (total > 0) {
            int hidden = _calibratorsModel.getHiddenNumberOfStar();
            title += ", " + hidden + " filtered";
        }
        title += ")";
        setBorder(new TitledBorder(grayBorder, title));
    }

    /**
     * Called on the JTable row selection changes.
     *
     * @param e 
     * @sa javax.swing.event.ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        _logger.entering("CalibratorsView", "valueChanged");

        // If there is any row selected in the table
        if (_calibratorsTable.getSelectedRowCount() > 0) {
            // Enable the delte menu item
            _deleteAction.setEnabled(true);
        } else {
            // Disable the delte menu item
            _deleteAction.setEnabled(false);
        }

        // Update model with current selection
        _calibratorsModel.setSelectedStars(getSelectedStarIndices());
    }

    /**
     * Called on _calibratorsModel changes.
     * @param e 
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        _logger.entering("CalibratorsView", "tableChanged");

        updateBorderTitle();

        // Enable/disable the Undelete menu item
        _undeleteAction.setEnabled(_calibratorsModel.hasSomeDeletedStars());

        // Update identification table if the table has a minimum of one column
        if (_calibratorsTable.getColumnModel().getColumnCount() > 0) {
            /*
             * If _calibratorsIdTable has not exactly one column
             * remove every other column and append correct one
             * @todo manage selection of one named column instead of first one
             */
            if (_calibratorsIdTable.getColumnModel().getColumnCount() != 1) {
                _logger.fine(
                        "Setting first column of table to Identification table");

                // @todo try to make next line run without eating all cpu resource
                // _calibratorsIdTable.getColumnModel().addColumn(_calibratorsTable.getColumnModel().getColumn(0));
            }
        }

        update(null, null);
    }

    /**
     * Automatically called whenever the observed model changed
     * @param o
     * @param arg  
     */
    @Override
    public void update(Observable o, Object arg) {
        _logger.entering("CalibratorsView", "update");

        // If preference colors have changed, repaint tables
        _calibratorsTable.repaint();
        _calibratorsIdTable.repaint();

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
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        _logger.entering("CalibratorsView", "print");

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int fontHeight = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // laisser de l'espace pour le numero de page
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth = pageFormat.getImageableWidth();

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
        _logger.entering("CalibratorsView", "showLegend");

        boolean preferencedLegendShouldBeVisible = _preferences.getPreferenceAsBoolean(
                "view.legend.show");

        int totalWidth = (int) _tableAndLegendPane.getBounds().getWidth();
        int dividerWidth = _tableAndLegendPane.getDividerSize();
        int tableAndLegendWidth = totalWidth - dividerWidth;
        int legendWidth = tableAndLegendWidth;

        if (preferencedLegendShouldBeVisible == true) {
            Component legend = _tableAndLegendPane.getRightComponent();
            int legendMinWidth = (int) legend.getMinimumSize().getWidth();
            legendWidth = tableAndLegendWidth - legendMinWidth;
        }

        _tableAndLegendPane.setDividerLocation(legendWidth);
    }

    protected class DeleteAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        DeleteAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("DeleteAction", "actionPerformed");

            _calibratorsModel.deleteStars(_calibratorsTable.getSelectedRows());
        }
    }

    protected class UndeleteAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        UndeleteAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("UndeleteAction", "actionPerformed");

            _calibratorsModel.undeleteStars();
        }
    }

    /** Returns the indices list of stars selected in the calibrator table .
     * 
     * @return the indices list
     */
    public int[] getSelectedStarIndices() {
        int[] selectedRows = _calibratorsTable.getSelectedRows();
        int[] convertedSelectedRows = new int[selectedRows.length];

        // Convert JTable order to data model order thanks to the TableSorter
        for (int i = 0; i < selectedRows.length; i++) {
            convertedSelectedRows[i] = _tableSorter.modelIndex(selectedRows[i]);
        }

        return convertedSelectedRows;
    }
}
/*___oOo___*/
