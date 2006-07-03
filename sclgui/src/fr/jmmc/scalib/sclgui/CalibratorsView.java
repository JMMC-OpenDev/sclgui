/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.18 2006-07-03 12:39:39 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
    Observer, ActionListener
{
    /** The monitored data source displayed by the embedded JTable */
    CalibratorsModel _calibratorsModel;

    /** The monitored application preferences */
    Preferences _preferences;

    /** The results table */
    JTable _jTable;

    /** Logger instance */
    Logger _logger = MCSLogger.getLogger();

    //details state
    /** DOCUMENT ME! */
    String detresstat = "show";

    /** DOCUMENT ME! */
    boolean noteinstancied = false;

    /** DOCUMENT ME! */
    int panelWidth = 895;

    /** DOCUMENT ME! */
    int subpanelwidth = 570;

    /**
     * Table and Legend container
     */
    JSplitPane tableAndLegendPane;

    /**
     * DOCUMENT ME!
     */
    LegendView legendView;

    /**
     * DOCUMENT ME!
     */
    NoteFrame noteFrame = new NoteFrame();

    /**
     * Interaction with Aladin
     */
    VOInteraction _aladinInteraction = null;

    /**
     * Save
     */
    Action _saveAction;

    /**
     * Save as
     */
    Action _saveAsAction;

    /**
     * Revert to saved
     */
    Action _revertToSavedAction;

    /**
     * Export as HTML
     */
    Action _exportAsHtmlAction;

    /**
     * Export as CSV
     */
    Action _exportAsCsvAction;

    /**
     * Delete Action
     */
    Action _deleteAction;

    /**
     * Select All
     */
    Action _selectAllAction;

    /**
     *  Show details
     */
    Action _showDetailsAction;

    /**
     *  Show legend
     */
    Action _showLegendAction;

    /**
     * plot in aladin
     */
    Action _plotInAladinAction;

    /**
     *
     */
    Action _Action;

    /**
     * Constructor.
     *
     * @param calibratorsModel the resutModel.
     */
    public CalibratorsView(CalibratorsModel calibratorsModel)
    {
        // Store the model and register against it
        _calibratorsModel = calibratorsModel;
        _calibratorsModel.addTableModelListener(this);

        // create actions
        _deleteAction         = new DeleteAction();
        _showLegendAction     = new ShowLegendAction();

        // Store the application preferences and register against it
        _preferences          = Preferences.getInstance();
        _preferences.addObserver(this);

        // Gray border of the view.
        Border grayBorder = BorderFactory.createLineBorder(Color.gray, 1);

        // Colored border of the view.
        setBorder(new TitledBorder(grayBorder, "Found Calibrators"));

        // Size management
        setMinimumSize(new Dimension(panelWidth, 320));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Table initialization
        _jTable = new JTable();

        TableSorter tableSorter = new TableSorter(_calibratorsModel);
        tableSorter.setTableHeader(_jTable.getTableHeader());
        _jTable.setModel(tableSorter);
        _jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(_jTable);
        scrollPane.setMinimumSize(new Dimension(subpanelwidth, 160));
        scrollPane.setPreferredSize(new Dimension(subpanelwidth, 260));

        //that width must be fixed
        JPanel legendPanel = new LegendView(_preferences);
        int    legendWidth = Math.min(legendPanel.getPreferredSize().width, 200);
        _logger.fine("#######" + legendWidth + " " + subpanelwidth);
        legendPanel.setMinimumSize(new Dimension(legendWidth, 0));
        // Set and place Table and Legend group
        tableAndLegendPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollPane, legendPanel);
        tableAndLegendPane.setOneTouchExpandable(true);
        tableAndLegendPane.setResizeWeight(1.0);
        tableAndLegendPane.setContinuousLayout(true);
        add(tableAndLegendPane);
        tableAndLegendPane.setAlignmentX(Float.parseFloat(".5"));

        // Tools panel creation
        JPanel resumePanel = new JPanel();
        JLabel resumeLabel = new JLabel();
        resumeLabel.setText("Resume : ");
        resumeLabel.setPreferredSize(new Dimension(70, 20));

        JTextField resumeTextField = new JTextField();
        resumeTextField.setText("10 stars with...");
        resumeTextField.setPreferredSize(new Dimension(subpanelwidth - 80, 20));
        resumeTextField.setEditable(false);

        resumePanel.add(resumeLabel);
        resumePanel.add(resumeTextField);
        add(resumePanel);

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
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        // If preference colors have changed, repaint table
        _jTable.repaint();

        // Check associated preference to be consistent
        boolean showLegendPref = _preferences.getPreferenceAsBoolean(
                "view.legend.show");
        showLegend(showLegendPref);
    }

    /**
     * setDetresstat  -  set the stat of the display status
     * @param s String
     */
    public void setDetresstat(String s)
    {
        MCSLogger.trace();

        detresstat = s;
    }

    /**
     * setnotestate  -  set the stat of the noteframe status
     * @param bool boolean
     */
    public void setnotestate(boolean bool)
    {
        MCSLogger.trace();

        noteinstancied = bool;
    }

    /**
     * Tell to the associated model to remove selected rows.
     */
    public void deleteSelectedRows()
    {
        MCSLogger.trace();
        _calibratorsModel.deleteShownStars(_jTable.getSelectedRows());
    }

    /**
     * Tell GUI to show or hide legend panel
     */
    public void showLegend(boolean flag)
    {
        MCSLogger.trace();
        int tableAndLegendWidth = ((int) tableAndLegendPane.getBounds()
                                                           .getWidth()) -
            tableAndLegendPane.getDividerSize();
        int loc                 = tableAndLegendWidth;

        if (flag)
        {
            Component legend = tableAndLegendPane.getRightComponent();
            loc = tableAndLegendWidth -
                ((int) legend.getMinimumSize().getWidth());
        }

        tableAndLegendPane.setDividerLocation(loc);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        /* TODO : move this in MainMenuBar.java
           if (e.getSource() == plotInAladinButton)
           {
               if (_calibratorsModel.getVOTable() != null)
               {
                   if (_aladinInteraction == null)
                   {
                       //
                       _aladinInteraction = new VOInteraction();
                       _aladinInteraction.startAladin(_calibratorsModel.getVOTable());
                       _aladinInteraction._aladin.execCommand("sync");
                   }
                   else
                   {
                       //
                       _aladinInteraction._aladin.setVisible(true);
                   }
               }
           }
         */
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
        public ShowLegendAction()
        {
            super("showLegend");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (e.getSource() instanceof AbstractButton)
            {
                AbstractButton b = (AbstractButton) e.getSource();
                showLegend(b.isSelected());
            }
        }
    }
}
/*___oOo___*/
