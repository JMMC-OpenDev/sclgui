/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.24 2006-10-10 09:09:37 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

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
    Observer, Printable
{
    /** Show Details action */
    static Action _showDetailsAction;

    /** Show Legend action */
    static Action _showLegendAction;

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

    /** Save action */
    Action _saveAction;

    /** Save As... action */
    Action _saveAsAction;

    /** Revert to Saved action */
    Action _revertToSavedAction;

    /** Export as HTML action */
    Action _exportAsHtmlAction;

    /** Export as CSV action */
    Action _exportAsCsvAction;

    /** Delete action */
    Action _deleteAction;

    /** Select All action */
    Action _selectAllAction;

    /** Plot in Aladin action */
    Action _plotInAladinAction;

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

        // create actions
        _deleteAction          = new DeleteAction();
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

        JScrollPane leftScrollPane = new JScrollPane(_calibratorsIdTable);
        leftScrollPane.setMinimumSize(new Dimension(100, 160));
        leftScrollPane.setPreferredSize(new Dimension(100, 260));

        //set Minimum Width of legend component
        JPanel legendPanel = new LegendView();
        int    legendWidth = Math.min(legendPanel.getPreferredSize().width, 200);
        legendPanel.setMinimumSize(new Dimension(legendWidth, 0));

        // Set and place TableId, Table and Legend group
        _tableAndLegendPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollPane, legendPanel);
        _tableAndLegendPane.setOneTouchExpandable(true);
        _tableAndLegendPane.setResizeWeight(1.0);
        _tableAndLegendPane.setContinuousLayout(true);

        JSplitPane tablesPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftScrollPane, _tableAndLegendPane);
        //        tablesPane.setResizeWeight(1.0);
        tablesPane.setAlignmentX(Float.parseFloat(".5"));
        add(tablesPane);

        // Make data refreshed according prefs
        update(null, null);
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

            // Build CalibratorModel and parse votable
            _calibratorsModel.parseVOTable(in);

            // Finally affect this model to the main view
        }
        catch (Exception e)
        {
            // TODO handle this exception
            e.printStackTrace();
        }
    }

    /**
     * Called on _calibratorsModel changes.
     */
    public void tableChanged(TableModelEvent e)
    {
        MCSLogger.trace();
        _deleteAction.setEnabled(true);

        // update identification table if bae table has a minimum of one column
        if (_calibratorsTable.getColumnModel().getColumnCount() > 0) //        if (_calibratorsTable.getColumnModel().getColumnCount()>1110 )
        {
            // if _calibratorsIdTable has not exactly one column
            // remove every other column
            // and append coorect one
            // @todo manage selection of one named column instead of first one
            if (_calibratorsIdTable.getColumnModel().getColumnCount() != 1)
            {
                MCSLogger.debug(
                    "Setting first column of table to Identification table");

                // @todo try to make next line run without eating all cpu
                // resource
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
        boolean showLegendPref = _preferences.getPreferenceAsBoolean(
                "view.legend.show");
        showLegend(showLegendPref);
    }

    /**
     * Tell to the associated model to remove selected rows.
     */
    public void deleteSelectedRows()
    {
        MCSLogger.trace();
        _calibratorsModel.deleteShownStars(_calibratorsTable.getSelectedRows());
    }

    /**
     * Tell GUI to show or hide legend panel
     */
    public void showLegend(boolean flag)
    {
        MCSLogger.trace();

        int tableAndLegendWidth = ((int) _tableAndLegendPane.getBounds()
                                                            .getWidth()) -
            _tableAndLegendPane.getDividerSize();
        int loc                 = tableAndLegendWidth;

        if (flag)
        {
            Component legend = _tableAndLegendPane.getRightComponent();
            loc = tableAndLegendWidth -
                ((int) legend.getMinimumSize().getWidth());
        }

        _tableAndLegendPane.setDividerLocation(loc);
    }

    /**
     * Tell GUI to show or hide detailled column info
     */
    public void showDetails(boolean flag)
    {
        MCSLogger.trace();

        //@todo implement
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

    protected class DeleteAction extends SCAction
    {
        public DeleteAction()
        {
            super("deleteTableSelection");
            setEnabled(false);
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();
            deleteSelectedRows();
        }
    }

    protected class ShowLegendAction extends SCAction
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

    protected class ShowDetailsAction extends SCAction
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
                MCSLogger.debug("Button.selected = " + button.isSelected());
            }
        }
    }
}
/*___oOo___*/
