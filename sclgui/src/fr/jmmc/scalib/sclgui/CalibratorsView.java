/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.40 2011-01-05 15:14:45 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.39  2010/10/10 22:45:03  lafrasse
 * Code reformating.
 *
 * Revision 1.38  2010/10/10 22:21:04  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.37  2009/11/04 10:17:21  lafrasse
 * Revamped simple/detailed results view settings to add "full results" support.
 *
 * Revision 1.36  2008/09/18 21:50:53  lafrasse
 * Moved _showLegendAction and _showDetailsAction to RegisteredPreferencedBooleanAction, in order to properly handle menu items et preference view checkboxes.
 *
 * Revision 1.35  2008/09/10 22:19:40  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Added clickable cell to open web page on star information from HIP and HD
 * catalog columns identifier.
 * Moved to new JMCS APIs.
 * Code, documentation and log enhancement.
 *
 * Revision 1.34  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.33  2007/06/14 11:59:40  lafrasse
 * Added support for simple/detailled view.
 *
 * Revision 1.32  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.31  2006/12/04 12:35:49  lafrasse
 * Enhanced delete menu item enabling, now based on JTable row selection detection.
 *
 * Revision 1.30  2006/11/30 23:03:53  lafrasse
 * Added a fixed column on the left with generated star IDs.
 *
 * Revision 1.29  2006/11/30 14:54:27  lafrasse
 * Added support for hidden legend view on startup.
 *
 * Revision 1.28  2006/11/29 17:34:19  lafrasse
 * Added new menu item to undelete stars flagged as deleted.
 *
 * Revision 1.27  2006/11/27 16:38:19  lafrasse
 * Added delete menu item disabling on emptied JTable.
 *
 * Revision 1.26  2006/11/18 23:01:46  lafrasse
 * Handled SCAction change to MCSAction.
 *
 * Revision 1.25  2006/11/13 13:28:23  lafrasse
 * Documentation refinment.
 *
 * Revision 1.24  2006/10/10 09:09:37  lafrasse
 * Updated MCSLogger APIs to more tightly reflect standard MCS 'log' module levels.
 *
 * Revision 1.23  2006/10/03 15:27:56  lafrasse
 * Refined logger usage.
 * Updated to match new LegendView constructor API.
 *
 * Revision 1.22  2006/08/04 16:53:54  lafrasse
 * Re-added preliminary print support
 *
 * Revision 1.21  2006/07/18 13:08:11  lafrasse
 * Removed resume text field and label
 * Code refinment
 *
 * Revision 1.20  2006/07/11 11:18:55  mella
 * Add show details action and place for first id column
 *
 * Revision 1.19  2006/07/05 14:53:29  mella
 * Change panel order, panels are numbered and enabled according inputs
 *
 * Revision 1.18  2006/07/03 12:39:39  mella
 * Implement right showLegend method
 *
 * Revision 1.17  2006/06/30 11:53:17  mella
 * Change GUI presentation
 *
 * Revision 1.16  2006/06/26 14:36:43  mella
 * Add delete action
 *
 * Revision 1.15  2006/06/23 09:19:41  mella
 * Jalopization
 *
 * Revision 1.14  2006/06/22 12:29:18  lafrasse
 * Removed Button Bar (replaced by menus)
 *
 * Revision 1.13  2006/06/19 11:27:21  mella
 * Add some base of code
 *
 * Revision 1.12  2006/04/07 13:07:25  mella
 * Improve presentation
 *
 * Revision 1.11  2006/04/07 11:05:28  mella
 * Housekeeping
 *
 * Revision 1.10  2006/04/07 08:41:03  mella
 * Preferences singleton is accessed using Preferences.getInstance()
 *
 * Revision 1.9  2006/04/07 07:51:37  mella
 * Make table sorter work
 *
 * Revision 1.8  2006/04/06 14:48:40  lafrasse
 * Added TableSorter usage
 *
 * Revision 1.7  2006/04/06 13:08:47  yvander
 * Aladin interaction
 *
 * Revision 1.6  2006/03/31 14:33:13  mella
 * Now the jTable refresh itself on preferences changes
 *
 * Revision 1.5  2006/03/31 14:30:42  mella
 * Support some color preferences changes
 *
 * Revision 1.4  2006/03/31 11:49:29  mella
 * Make file-open menu work
 *
 * Revision 1.3  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.2  2006/03/30 13:39:19  yvander
 * Mise en place des couleurs
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;


import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Calibrators view.
 *
 * Manage a JTable.
 * This file also embedds the appropriate MVC controller in the form of several
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
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.CalibratorsView");
    /** Show Legend action */
    //public static ShowLegendAction _showLegendAction;
    public static RegisteredPreferencedBooleanAction _showLegendAction = null;
    /** Synthetic Results Verbosity action */
    public static RegisteredPreferencedBooleanAction _syntheticResultsVerbosityAction = null;
    /** Detailled Results Verbosity action */
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
        TableSorter tableSorter = new TableSorter(_calibratorsModel,
                _calibratorsTable.getTableHeader());
        _calibratorsTable.setModel(tableSorter);
        _calibratorsIdTable = new JTable(tableSorter,
                new DefaultTableColumnModel(), null);
        _calibratorsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _calibratorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

            public void ancestorMoved(HierarchyEvent e) {
            }

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
     * @sa javax.swing.event.ListSelectionListener
     */
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
    }

    /**
     * Called on _calibratorsModel changes.
     */
    public void tableChanged(TableModelEvent e) {
        _logger.entering("CalibratorsView", "tableChanged");

        updateBorderTitle();

        // Enable/disable the Undelete menu item
        _undeleteAction.setEnabled(_calibratorsModel.hasSomeDeletedStars());

        // Update identification table if bae table has a minimum of one column
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
     */
    public void update(Observable o, Object arg) {
        _logger.entering("CalibratorsView", "update");

        // If preference colors have changed, repaint tables
        _calibratorsTable.repaint();
        _calibratorsIdTable.repaint();

        // Refresh legend display state
        showLegend();
    }

    /**
     * @sa java.awt.print
     */
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

        public DeleteAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("DeleteAction", "actionPerformed");

            _calibratorsModel.deleteStars(_calibratorsTable.getSelectedRows());
        }
    }

    protected class UndeleteAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        public UndeleteAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("UndeleteAction", "actionPerformed");

            _calibratorsModel.undeleteStars();
        }
    }
}
/*___oOo___*/
