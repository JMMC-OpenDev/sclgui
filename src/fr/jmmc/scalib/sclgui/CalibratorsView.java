/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsView.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
            JTable jTable = new JTable();
            jTable.setModel(_calibratorsModel);
            jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane scrollPane = new JScrollPane(jTable);
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
/*___oOo___*/
