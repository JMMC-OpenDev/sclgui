/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.4 2006-04-06 14:44:14 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/03/31 14:53:28  mella
 * Add TabbedPane container
 *
 * Revision 1.2  2006/03/31 14:30:42  mella
 * Support some color preferences changes
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;


// TODO handle close button correctly

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends JFrame implements Observer, ActionListener
{
    /**
     * DOCUMENT ME!
     */
    Preferences _preferences;

    /**
     * DOCUMENT ME!
     */
    protected JButton _restoreDefaultButton;
    protected JButton _saveChangesButton;

    protected JTabbedPane prefTabbedPane;
    
    /**
     * Constructor.
     * @param title String
     */
    public PreferencesView(Preferences preferences)
    {
        super("SCALIB Preferences");

        _preferences = preferences;
        _preferences.addObserver(this);

        // Build main GUI components
        prefTabbedPane = new JTabbedPane();
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(prefTabbedPane);

        // Append preference colors chooser component of catalogs
        ColorPreferencesView colorView = new ColorPreferencesView(_preferences,
                "catalog.color.");
        prefTabbedPane.add("Catalog Origin" , colorView);

        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore to default");
        _restoreDefaultButton.addActionListener(this);
        buttonsPanel.add(_restoreDefaultButton);
        _saveChangesButton = new JButton("Save changes");
        _saveChangesButton.addActionListener(this);
        buttonsPanel.add(_saveChangesButton);
        contentPane.add (buttonsPanel);
            
        // Center on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize  = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
     */
    public void update(Observable o, Object arg)
    {
        // TODO : M�J des champs de la fenetre
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        // If restore default button pressed
        if (evt.getSource().equals(_restoreDefaultButton))
        {
            try
            {
                _preferences.resetToDefaultPreferences();
            }
            catch (Exception e)
            {
                // TODO criez fort!!
                e.printStackTrace();
            }
        }
        // If save button pressed
        if (evt.getSource().equals(_saveChangesButton))
        {
            try
            {
                _preferences.saveToFile();
            }
            catch (Exception e)
            {
                // TODO criez fort!!
                e.printStackTrace();
            }
        }
    }
}


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
  */
class SpecificPreferencesView extends JPanel
{
    /**
     * DOCUMENT ME!
     */
    Preferences _preferences;

    /**
     * Constructor.
     * @param title String
     */
    public SpecificPreferencesView(Preferences preferences)
    {
        _preferences = preferences;
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
         }
}


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
  */
class ColorPreferencesView extends JPanel implements ChangeListener,
    ActionListener
{
    /**
     * DOCUMENT ME!
     */
    protected JColorChooser tcc;

    /**
     * DOCUMENT ME!
     */
    protected JLabel banner;

    /**
     * DOCUMENT ME!
     */
    protected Preferences _preferences;

    /**
     * DOCUMENT ME!
     */
    protected JComboBox _preferenceNames;

    /**
     * DOCUMENT ME!
     */
    protected String _preferencePrefix;

    /**
     * DOCUMENT ME!
     */
    protected JButton _applyChangesButton;

  
    /**
     * Creates a new ColorPreferencesView object.
     *
     * @param preferences DOCUMENT ME!
     * @param prefix DOCUMENT ME!
     */
    public ColorPreferencesView(Preferences preferences, String prefix)
    {
        super(new BorderLayout());
        _preferencePrefix     = prefix;
        _preferences          = preferences;

        //Set up color chooser for setting text color
        tcc                   = new JColorChooser();
        tcc.getSelectionModel().addChangeListener(this);

        // Set up combo test label and apply button
        _preferenceNames = new JComboBox();

        // Fill with catalog List
        Enumeration e = _preferences.getPreferences(prefix);

        while (e.hasMoreElements())
        {
            String entry       = (String) e.nextElement();
            String catalogName = entry.substring(prefix.length());
            _preferenceNames.addItem(catalogName);
        }

        _applyChangesButton = new JButton("Apply changes");
        _applyChangesButton.addActionListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(tcc);
        add(_preferenceNames);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(_applyChangesButton);
        add(buttonsPanel);
        _preferenceNames.addActionListener(this);
        updateColor();
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void stateChanged(ChangeEvent e)
    {
        Color newColor = tcc.getColor();
    }

    /**
     * DOCUMENT ME!
     */
    private void updateColor()
    {
        String preferenceName = _preferencePrefix +
            _preferenceNames.getSelectedItem();
        Color  c;

        try
        {
            c                 = _preferences.getPreferenceAsColor(preferenceName);
        }
        catch (Exception e)
        {
            c = Color.white;
        }

        tcc.setColor(c);
    }

    
    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent evt)
    {
        // If combobox was modified
        if (evt.getSource().equals(_preferenceNames))
        {
            updateColor();
        }

        // If apply button pressed
        if (evt.getSource().equals(_applyChangesButton))
        {
            String preferenceName = _preferencePrefix +
                _preferenceNames.getSelectedItem();
            Color  newColor       = tcc.getColor();
            _preferences.setPreference(preferenceName,
                jmmc.mcs.util.ColorEncoder.encode(newColor));
        }

    }
}
