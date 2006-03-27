/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


// TODO handle close button correctly

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends JFrame implements Observer
{
    /**
     * DOCUMENT ME!
     */
    FlowLayout flowlay = new FlowLayout();

    /**
     * DOCUMENT ME!
     */
    Border borderlight = BorderFactory.createLineBorder(Color.gray, 1);

    /**
     * DOCUMENT ME!
     */
    Border borderultralight = BorderFactory.createLineBorder(Color.lightGray, 1);

    //Panel
    /**
     * DOCUMENT ME!
     */
    JPanel panelconfigureresume = new JPanel();

    /**
     * DOCUMENT ME!
     */
    JPanel panelconfigureresumebutton = new JPanel();

    /**
     * DOCUMENT ME!
     */
    JPanel paneldata = new JPanel();

    //Radio buttons
    /**
     * DOCUMENT ME!
     */
    JRadioButton details = new JRadioButton("Details");

    /**
     * DOCUMENT ME!
     */
    JRadioButton resume = new JRadioButton("Resume");

    /**
     * DOCUMENT ME!
     */
    ButtonGroup groupRadio = new ButtonGroup();

    //buttons
    /**
     * DOCUMENT ME!
     */
    JButton buttonconfresume = new JButton("Configure Resume");

    /**
     * DOCUMENT ME!
     */
    JButton buttonapplyconfresume = new JButton("Apply");

    /**
     * DOCUMENT ME!
     */
    JButton buttondefaultconfresume = new JButton("Default");

    //list and scrollpane
    /**
     * DOCUMENT ME!
     */
    JScrollPane scrollcolumn;

    /**
     * DOCUMENT ME!
     */
    JList listcolumn;

    //evts
    /**
     * DOCUMENT ME!
     */
    PreferencesListener preferencesListener = new PreferencesListener(this);

    /**
     * DOCUMENT ME!
     */
    Preferences _preferences;

    /**
     * Constructor.
     * @param title String
     */
    public PreferencesView(Preferences preferences)
    {
        super("SCALIB Preferences");

        _preferences = preferences;
        _preferences.addObserver(this);

        try
        {
            jbInit();
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    /**
     * jbInit  -  Components initialisation
     * @throws Exception
     */
    private void jbInit() throws Exception
    {
        setSize(300, 200);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize  = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);

        flowlay.setAlignment(FlowLayout.LEFT);

        //Panels configuration
        paneldata.setLayout(flowlay);
        paneldata.setBorder(new TitledBorder(borderlight, "Type of display"));

        panelconfigureresume.setLayout(flowlay);
        panelconfigureresume.setBorder(new TitledBorder(borderultralight,
                "Resume configuration"));
        panelconfigureresumebutton.setPreferredSize(new Dimension(80, 80));
        panelconfigureresume.setVisible(false);

        //type of data
        Vector data = new Vector();
        data.add("Mag I");
        data.add("Mag J");
        data.add("Mag H");
        data.add("Mag K");

        listcolumn       = new JList(data);
        scrollcolumn     = new JScrollPane(listcolumn);
        scrollcolumn.setPreferredSize(new Dimension(160, 120));
        listcolumn.setToolTipText("Press on Ctrl to select several data");

        buttonconfresume.addActionListener(preferencesListener);
        groupRadio.add(resume);
        groupRadio.add(details);
        resume.setSelected(true);
        resume.addActionListener(preferencesListener);
        details.addActionListener(preferencesListener);
        details.setPreferredSize(new Dimension(250, 21));

        buttonapplyconfresume.addActionListener(preferencesListener);
        buttonapplyconfresume.setPreferredSize(new Dimension(80, 27));
        buttondefaultconfresume.addActionListener(preferencesListener);
        buttondefaultconfresume.setPreferredSize(new Dimension(80, 27));

        panelconfigureresume.add(scrollcolumn);
        panelconfigureresume.add(panelconfigureresumebutton);
        panelconfigureresumebutton.add(buttonapplyconfresume);
        panelconfigureresumebutton.add(buttondefaultconfresume);

        paneldata.add(details);
        paneldata.add(resume);
        paneldata.add(buttonconfresume);
        paneldata.add(panelconfigureresume);

        getContentPane().add(paneldata, java.awt.BorderLayout.CENTER);
    }

    /**
     * Set Frame configuration
     * @param display boolean
     */
    public void configureResumeDisplay(boolean display)
    {
        if (display)
        {
            setSize(300, 310);
            setVisible(false);
            setVisible(true);
        }
        else
        {
            setSize(300, 200);
            setVisible(true);
            setVisible(false);
        }

        panelconfigureresume.setVisible(display);
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
     */
    public void update(Observable o, Object arg)
    {
        // TODO : MˆJ des champs de la fenetre
    }
}


/**
 * DOCUMENT ME!
 */
class PreferencesListener implements ActionListener
{
    /**
     * DOCUMENT ME!
     */
    PreferencesView _view;

    /**
     * PreferencesListener  -  Constructor
     * @param view PreferencesView
     */
    public PreferencesListener(PreferencesView view)
    {
        _view = view;
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        //Check resume or details
        if (evt.getSource() == _view.resume)
        {
            System.out.println("radio resume");
        }
        else if (evt.getSource() == _view.details)
        {
            System.out.println("radio details");
        }

        //Configure resume display
        if (evt.getSource() == _view.buttonconfresume)
        {
            _view.configureResumeDisplay(true);
        }

        //Apply configuration
        if (evt.getSource() == _view.buttonapplyconfresume)
        {
            if (_view.listcolumn.isSelectionEmpty() == false)
            {
                int[] column = null;
                column = _view.listcolumn.getSelectedIndices();

                int k = 0;

                for (k = 0; k < column.length; k++)
                {
                    System.out.println("Elts selectionne : " +
                        _view.listcolumn.getModel().getElementAt(column[k]));
                }
            }
            else
            {
                System.out.println("Aucun elts selectionne");
            }
        }

        //Default configuration
        if (evt.getSource() == _view.buttondefaultconfresume)
        {
            System.out.println("Default configuration");
        }
    } //end actionPerformed
} //end class
