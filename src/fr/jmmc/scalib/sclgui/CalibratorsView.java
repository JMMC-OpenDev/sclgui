/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.2 2006-03-30 13:39:19 yvander Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.awt.*;
import java.awt.event.*;

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

    //** The results table */
    JTable _jTable;

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
    LegendView legendView = new LegendView();

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

        // Gray border of the view.
        Border grayBorder = BorderFactory.createLineBorder(Color.gray, 1);

        // Colored border of the view.
//        Border coloredBorder = BorderFactory.createLineBorder(Color.red, 2);
//        setBorder(new TitledBorder(coloredBorder, "RESULTS"));
        setBorder(new TitledBorder(grayBorder, "Found Calibrators"));

        // View layout.
        FlowLayout viewLayout = new FlowLayout();

        try
        {
            // Size management
            setMinimumSize(new Dimension(width, 320));
            setSize(new Dimension(width, 320));
            setLayout(viewLayout);
            setPreferredSize(new Dimension(width, 320));

            // Table panel creation
            JPanel tablePanel = new JPanel();
            tablePanel.setMinimumSize(new Dimension(subpanelwidth, 270));
            tablePanel.setSize(new Dimension(subpanelwidth, 270));
            tablePanel.setPreferredSize(new Dimension(subpanelwidth, 270));
            tablePanel.setLayout(viewLayout);
            add(tablePanel);

            // Table initialization
            _jTable = new JTable();
            _jTable.setModel(_calibratorsModel);
            _jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane scrollPane = new JScrollPane(_jTable);
            scrollPane.setMinimumSize(new Dimension(subpanelwidth, 160));
            scrollPane.setPreferredSize(new Dimension(subpanelwidth, 160));
            tablePanel.add(scrollPane);

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

            JButton plotInAladinButton = new JButton();
            plotInAladinButton.setText("Plot Data in Aladin");
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
            resumeTextField.setPreferredSize(new Dimension(subpanelwidth - 80,
                    20));
            resumeTextField.setEditable(false);
            toolsPanel.add(resumeTextField);
            toolsPanel.add(buttonPanel);
            toolsPanel.setMinimumSize(new Dimension(subpanelwidth, 140));
            toolsPanel.setSize(new Dimension(subpanelwidth, 140));
            toolsPanel.setPreferredSize(new Dimension(subpanelwidth, 140));

            tablePanel.add(toolsPanel);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Called on _calibratorsModel changes.
     */
    public void tableChanged(TableModelEvent e)
    {


	for (int i = 0; i < _calibratorsModel.getColumnCount(); i++) {
              TableColumn tc = _jTable.getTableHeader().getColumnModel().getColumn(i);
              tc.setCellRenderer(new TableCellColors());
            }


        MCSLogger.trace();

        // TODO implement
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
     */
    public void update(Observable o, Object arg)
    {
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
        if (e.getSource() == showLegendButton)
        {
            legendView.setVisible(true);
        }

        if (e.getSource() == addCommentButton)
        {
            noteFrame.setVisible(true);
        }
    }
}

/**
 * <p>Titre : </p>
 *
 * <p>Description : </p>
 *
 * <p>Copyright : Copyright (c) 2006</p>
 *
 * <p>Société : </p>
 *
 * @author non attribuable
 * @version 1.0
 */
class TableCellColors extends DefaultTableCellRenderer
{
    /**
     * DOCUMENT ME!
     */
    Hashtable _hashColors;

    /**
     * DOCUMENT ME!
     */
    Hashtable _hashConfidence;

    /**
     * TableCellColors  -  Constructor
     */
    public TableCellColors()
    {
        super();

        _hashColors = new Hashtable();
        _hashColors.put("I/280",Color.blue);
        _hashColors.put("II/246/out",Color.orange);
        _hashColors.put("II/225/catalog",Color.green);
        _hashColors.put("II/7A/catalog",Color.red);

        _hashConfidence = new Hashtable();
        _hashConfidence.put("HIGH",Color.white);
        _hashConfidence.put("MEDIUM",Color.gray);
        _hashConfidence.put("LOW",Color.darkGray);
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
        // Get the name of column with index
        TableColumnModel colModel = table.getTableHeader().getColumnModel();
        String columnName = (String)colModel.getColumn(column).getHeaderValue();

        // Set default renderer to the component
        super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column);

	// Get StarProperty selected
        CalibratorsModel calModel = (CalibratorsModel)table.getModel();
        StarProperty starProperty = calModel.getStarProperty( row, columnName);

        // If cell is not selecterd and not focused
      if ( ! ( isSelected && hasFocus ) ){


	if(starProperty != null){

          // Set Background Color corresponding to the Catalog Origin Color
          if(starProperty.getOrigin().equals("")==false)
            setBackground((Color)_hashColors.get(starProperty.getOrigin()));

          else
	  //Set the Confidence Value
            if(starProperty.getConfidence().equals("")==false)
	      setBackground((Color)_hashConfidence.get(starProperty.getConfidence()));
	}
      }
      // Return the component
      return this;
    }
}
/*___oOo___*/
