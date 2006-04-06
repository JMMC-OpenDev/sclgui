/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.8 2006-04-06 14:48:40 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
 * Calibrators view.
 *
 * Manage a JTable and a bunch of buttons to handle our applicaton core
 * functionalities. This file also embedds the appropriate MVC controller in the
 * form of several ActionListener.
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

    /** The cellrendered that works with every columns */
    TableCellColors _tableCellColors;

    //details state
    /** DOCUMENT ME! */
    String detresstat = "show";

    /** DOCUMENT ME! */
    boolean noteinstancied = false;

    /** DOCUMENT ME! */
    int width = 995;

    /** DOCUMENT ME! */
    int subpanelwidth = 970;

    /**
     * DOCUMENT ME!
     */
    LegendView legendView;

    /**
     * DOCUMENT ME!
     */
    JButton showLegendButton = new JButton();

    /**
     * DOCUMENT ME!
     */
    NoteFrame noteFrame = new NoteFrame();

    /**
     * DOCUMENT ME!
     */
    JButton addCommentButton = new JButton();

    /**
     * DOCUMENT ME!
     */
    JButton plotInAladinButton = new JButton();

    /**
     * Interaction with Aladin
     */
    VOInteraction _aladinInteraction = null;

    /**
     * Constructor.
     *
     * @param calibratorsModel the resutModel.
     */
    public CalibratorsView(CalibratorsModel calibratorsModel,
        Preferences preferences)
    {
        // Store the model and register against it
        _calibratorsModel = calibratorsModel;
        _calibratorsModel.addTableModelListener(this);

        // Store the application preferences and register against it
        _preferences = preferences;
        _preferences.addObserver(this);

        legendView           = new LegendView(_preferences);

        // Create tableCellColors
        _tableCellColors     = new TableCellColors(_preferences);

        // Gray border of the view.
        Border grayBorder = BorderFactory.createLineBorder(Color.gray, 1);

        // Colored border of the view.
        setBorder(new TitledBorder(grayBorder, "Found Calibrators"));

        // Size management
        setMinimumSize(new Dimension(width, 320));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Table initialization
        _jTable = new JTable();
        _jTable.setModel(new TableSorter(_calibratorsModel));
//        _jTable.setModel(_calibratorsModel);
        _jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(_jTable);
        scrollPane.setMinimumSize(new Dimension(subpanelwidth, 160));
        scrollPane.setPreferredSize(new Dimension(subpanelwidth, 260));
        add(scrollPane);

        // Button panel creation
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new TitledBorder(grayBorder, "Operations"));

        JButton selectAllButton = new JButton();
        selectAllButton.setText("Select All Data");
        buttonPanel.add(selectAllButton);

        JButton deleteSelectionButton = new JButton();
        deleteSelectionButton.setText("Delete Selection");
        buttonPanel.add(deleteSelectionButton);

        JButton resetButton = new JButton();
        resetButton.setText("Reset");
        buttonPanel.add(resetButton);

        plotInAladinButton.setText("Plot Data in Aladin");
        plotInAladinButton.addActionListener(this);
        buttonPanel.add(plotInAladinButton);

        addCommentButton.setText("Add Comment");
        addCommentButton.addActionListener(this);
        buttonPanel.add(addCommentButton);

        JButton detres = new JButton();
        detres.setText("Show Details");
        buttonPanel.add(detres);

        showLegendButton.setText("Legend");
        showLegendButton.addActionListener(this);
        buttonPanel.add(showLegendButton);

        // Tools panel creation
        JPanel toolsPanel  = new JPanel();
        JLabel resumeLabel = new JLabel();
        resumeLabel.setText("Resume : ");
        resumeLabel.setPreferredSize(new Dimension(70, 20));
        toolsPanel.add(resumeLabel);

        JTextField resumeTextField = new JTextField();
        resumeTextField.setText("10 stars with...");
        resumeTextField.setPreferredSize(new Dimension(subpanelwidth - 80, 20));
        resumeTextField.setEditable(false);
        toolsPanel.add(resumeTextField);
        toolsPanel.add(buttonPanel);
        toolsPanel.setMinimumSize(new Dimension(subpanelwidth, 140));
        toolsPanel.setSize(new Dimension(subpanelwidth, 140));
        toolsPanel.setPreferredSize(new Dimension(subpanelwidth, 140));

        add(toolsPanel);
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

        // @todo : maybe this should be done only one time
        for (int i = 0; i < _calibratorsModel.getColumnCount(); i++)
        {
            TableColumn tc = _jTable.getTableHeader().getColumnModel()
                                    .getColumn(i);
            tc.setCellRenderer(_tableCellColors);
        }
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

        // @todo : place right code to make jTable refresh itself
        _jTable.repaint();
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
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        if (e.getSource() == showLegendButton)
        {
            legendView.setVisible(true);
        }

        if (e.getSource() == addCommentButton)
        {
            noteFrame.setVisible(true);
        }

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
    }
}


/**
 */
class TableCellColors extends DefaultTableCellRenderer implements Observer
{
    // No trace log is implemented because these parts of code is often called. 

    /**
     * DOCUMENT ME!
     */
    Hashtable _hashColors;

    /**
     * DOCUMENT ME!
     */
    Hashtable _hashConfidence;

    /**
     * DOCUMENT ME!
     */
    Preferences _preferences;

    /**
     * TableCellColors  -  Constructor
     */
    public TableCellColors(Preferences preferences)
    {
        super();

        // Store the application preferences and register against it
        _preferences = preferences;
        _preferences.addObserver(this);

        // force to load Preferences at first moment
        update(_preferences, null);
    } //end TableCellColors

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
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        MCSLogger.trace();

        // Set default renderer to the component
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);

        // Get StarProperty selected
        CalibratorsModel calModel     = ((CalibratorsModel)((TableSorter)table.getModel()).getTableModel());
//        CalibratorsModel calModel     = ((CalibratorsModel) table.getModel());
        StarProperty     starProperty = calModel.getStarProperty(row, column);

        // If cell is not selecterd and not focused
        if (! (isSelected && hasFocus))
        {
            if (starProperty != null)
            {
                // Set Background Color corresponding to the Catalog Origin Color or confidence index
                if (starProperty.hasOrigin() == true)
                {
                    setBackground((Color) _hashColors.get(
                            starProperty.getOrigin()));
                }
                else if (starProperty.hasConfidence() == true)
                {
                    setBackground((Color) _hashConfidence.get(
                            starProperty.getConfidence()));
                }
                else
                {
                    // If cells are black, something bad appent !
                    setBackground(Color.BLACK);
                }
            }
        }

        // Return the component
        return this;
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

        // React to preferences changes
        if (o.equals(_preferences))
        {
            // read colors preferences for catalogs
            String      prefix = "catalog.color.";
            Enumeration e      = _preferences.getPreferences(prefix);
            _hashColors        = new Hashtable();

            while (e.hasMoreElements())
            {
                String entry       = (String) e.nextElement();
                String catalogName = entry.substring(prefix.length());

                try
                {
                    Color catalogColor = _preferences.getPreferenceAsColor(entry);
                    _hashColors.put(catalogName, catalogColor);
                }
                catch (PreferencesException ex)
                {
                    // TODO log as error instead of stderr...
                    ex.printStackTrace();
                }
            }

            // Read colors preferences for confidences
            prefix              = "confidence.color.";
            e                   = _preferences.getPreferences(prefix);
            _hashConfidence     = new Hashtable();

            while (e.hasMoreElements())
            {
                String entry          = (String) e.nextElement();
                String confidenceName = entry.substring(prefix.length());

                try
                {
                    Color confidenceColor = _preferences.getPreferenceAsColor(entry);
                    _hashConfidence.put(confidenceName, confidenceColor);
                }
                catch (PreferencesException ex)
                {
                    // TODO log as error instead of stderr...
                    ex.printStackTrace();
                }
            }
        }
    }
}
/*___oOo___*/
