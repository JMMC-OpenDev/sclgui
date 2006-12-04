/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.31 2006-12-04 12:35:49 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
package jmmc.scalib.sclgui;

import jmmc.mcs.gui.StatusBar;

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.util.*;

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
    ListSelectionListener, Observer, Printable
{
    /** Show Legend action */
    static ShowLegendAction _showLegendAction;

    /** Show Details action */
    static ShowDetailsAction _showDetailsAction;

    /** The monitored data source displayed by the embedded JTable */
    CalibratorsModel _calibratorsModel;

    /** The monitored application preferences */
    Preferences _preferences;

    /** The left-most static one column table containing star ID */
    JTable _calibratorsIdTable;

    /** The calibrators table */
    JTable _calibratorsTable;

    /** Calibrators table and Legend container */
    JSplitPane _tableAndLegendPane;

    /** Delete action */
    DeleteAction _deleteAction;

    /** Undelete action */
    UndeleteAction _undeleteAction;

    /**
     * Constructor.
     *
     * @param calibratorsModel the data to be displayed.
     */
    public CalibratorsView(CalibratorsModel calibratorsModel)
    {
        // Store the model and register against it
        _calibratorsModel = calibratorsModel;
        _calibratorsModel.addTableModelListener(this);

        // Create actions
        _deleteAction          = new DeleteAction();
        _undeleteAction        = new UndeleteAction();
        _showLegendAction      = new ShowLegendAction();
        _showDetailsAction     = new ShowDetailsAction();

        // Store the application preferences and register against it
        _preferences           = Preferences.getInstance();
        _preferences.addObserver(this);

        // Gray border of the view.
        Border grayBorder = BorderFactory.createLineBorder(Color.gray, 1);

        // Colored border of the view.
        setBorder(new TitledBorder(grayBorder, "Found Calibrators"));

        // Size management
        setMinimumSize(new Dimension(895, 320));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Table initialization
        _calibratorsTable = new JTable();

        // Handle the selection listener
        _calibratorsTable.getSelectionModel().addListSelectionListener(this);

        TableSorter tableSorter = new TableSorter(_calibratorsModel);
        tableSorter.setTableHeader(_calibratorsTable.getTableHeader());
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
        JPanel legendPanel = new LegendView();
        int    legendWidth = Math.min(legendPanel.getPreferredSize().width, 200);
        legendPanel.setMinimumSize(new Dimension(legendWidth, 0));
        legendPanel.setSize(new Dimension(0, 0));

        // Set and place TableId, Table and Legend group
        _tableAndLegendPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollPane, legendPanel);
        _tableAndLegendPane.setOneTouchExpandable(true);
        _tableAndLegendPane.setResizeWeight(1.0);
        _tableAndLegendPane.setContinuousLayout(true);
        add(_tableAndLegendPane);

        // Make data refreshed according prefs
        update(null, null);
    }

    /**
     * Called on the JTable row selection changes.
     *
     * @sa javax.swing.event.ListSelectionListener
     */
    public void valueChanged(ListSelectionEvent e)
    {
        MCSLogger.trace();

        // If there is any row selected in the table
        if (_calibratorsTable.getSelectedRowCount() > 0)
        {
            // Enable the delte menu item
            _deleteAction.setEnabled(true);
        }
        else
        {
            // Disable the delte menu item
            _deleteAction.setEnabled(false);
        }
    }

    /**
     * Called on _calibratorsModel changes.
     */
    public void tableChanged(TableModelEvent e)
    {
        MCSLogger.trace();

        // Enable/disable the Undelete menu item
        _undeleteAction.setEnabled(_calibratorsModel.hasSomeDeletedStars());

        // Update identification table if bae table has a minimum of one column
        if (_calibratorsTable.getColumnModel().getColumnCount() > 0)
        {
            /*
             * If _calibratorsIdTable has not exactly one column
             * remove every other column and append correct one
             * @todo manage selection of one named column instead of first one
             */
            if (_calibratorsIdTable.getColumnModel().getColumnCount() != 1)
            {
                MCSLogger.debug(
                    "Setting first column of table to Identification table");

                // @todo try to make next line run without eating all cpu resource
                // _calibratorsIdTable.getColumnModel().addColumn(_calibratorsTable.getColumnModel().getColumn(0));
            }
        }
    }

    /**
     * Automatically called whenever the observed model changed
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        // If preference colors have changed, repaint tables
        _calibratorsTable.repaint();
        _calibratorsIdTable.repaint();

        // Check associated preference to be consistent
        boolean legendShouldBeVisible = _preferences.getPreferenceAsBoolean(
                "view.legend.show");
        showLegend(legendShouldBeVisible);
    }

    /**
     * @sa java.awt.print
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
        throws PrinterException
    {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int fontHeight  = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // laisser de l'espace pour le numero de page
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth  = pageFormat.getImageableWidth();

        g2d.drawString("Page: " + (pageIndex + 1), ((int) pageWidth / 2) - 35,
            (int) ((pageHeight + fontHeight) - fontDescent));

        g2d.scale(0.7, 0.7);
        paint(g2d);

        return (Printable.PAGE_EXISTS);
    }

    /**
     * Open the given file and display it. In fact it tells to the inner model to parse it.
     *
     * @param file the file to display.
     */
    public void openFile(File file)
    {
        try
        {
            // Get a BufferedReader from file
            String         fileName   = file.getAbsolutePath();
            FileReader     fileReader = new FileReader(fileName);
            BufferedReader in         = new BufferedReader(fileReader);

            StatusBar.show("loading file...");
            System.out.println("loading file...");

            // Build CalibratorModel and parse votable
            _calibratorsModel.parseVOTable(in);

            StatusBar.show("file succesfully loaded.");
            System.out.println("file succesfully loaded.");
        }
        catch (Exception e)
        {
            StatusBar.show("an error occured while trying to load file !");

            // TODO handle this exception
            e.printStackTrace();
        }
    }

    /**
     * Tell GUI to show or hide legend panel
     */
    public void showLegend(boolean visible)
    {
        MCSLogger.trace();

        int totalWidth          = (int) _tableAndLegendPane.getBounds()
                                                           .getWidth();
        int dividerWidth        = _tableAndLegendPane.getDividerSize();
        int tableAndLegendWidth = totalWidth - dividerWidth;
        int legendWidth         = tableAndLegendWidth;

        if (visible == true)
        {
            Component legend         = _tableAndLegendPane.getRightComponent();
            int       legendMinWidth = (int) legend.getMinimumSize().getWidth();
            legendWidth              = tableAndLegendWidth - legendMinWidth;
        }

        _tableAndLegendPane.setDividerLocation(legendWidth);
    }

    /**
     * Tell GUI to show or hide detailled column info
     */
    public void showDetails(boolean visible)
    {
        MCSLogger.trace();

        //@todo implement
    }

    protected class DeleteAction extends MCSAction
    {
        public DeleteAction()
        {
            super("deleteTableSelection");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _calibratorsModel.deleteStars(_calibratorsTable.getSelectedRows());
        }
    }

    protected class UndeleteAction extends MCSAction
    {
        public UndeleteAction()
        {
            super("undeleteAll");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _calibratorsModel.undeleteStars();
        }
    }

    protected class ShowLegendAction extends MCSAction
    {
        Preferences _preferences = Preferences.getInstance();

        public ShowLegendAction()
        {
            super("showLegend");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (e.getSource() instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) e.getSource();
                showLegend(button.isSelected());
            }
        }
    }

    protected class ShowDetailsAction extends MCSAction
    {
        Preferences _preferences = Preferences.getInstance();

        public ShowDetailsAction()
        {
            super("showDetails");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (e.getSource() instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) e.getSource();
                showDetails(button.isSelected());
            }
        }
    }
}
/*___oOo___*/
