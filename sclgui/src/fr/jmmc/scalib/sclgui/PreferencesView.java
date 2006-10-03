/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.16 2006-10-03 15:31:19 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.15  2006/09/28 15:23:29  lafrasse
 * Updated to handle jmmc.util.Preferences API modifications.
 *
 * Revision 1.14  2006/09/27 11:54:10  lafrasse
 * Removed unused widgets in Query view
 *
 * Revision 1.13  2006/09/15 14:19:59  lafrasse
 * Code refinments.
 *
 * Revision 1.12  2006/07/28 08:37:56  mella
 * Use shared PreferenceButtonModels
 *
 * Revision 1.11  2006/07/11 11:16:23  mella
 * Add show Detail and show legend preference checkboxes
 *
 * Revision 1.10  2006/06/30 11:53:17  mella
 * Change GUI presentation
 *
 * Revision 1.9  2006/06/19 11:22:45  mella
 * Add GUI to manage ordered keywords
 *
 * Revision 1.8  2006/06/09 13:36:51  mella
 * add some config panels , the help one manage tooltips behaviour
 *
 * Revision 1.7  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.6  2006/04/07 11:06:33  mella
 * Add better widgets
 *
 * Revision 1.5  2006/04/07 08:41:03  mella
 * Preferences singleton is accessed using Preferences.getInstance()
 *
 * Revision 1.4  2006/04/06 14:44:14  mella
 * Add feature to restore to default preferences
 *
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

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.table.*;


// @TODO handle close button correctly

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends JFrame implements ActionListener
{
    /** Data model */
    Preferences _preferences;

    /** "Restore to Default Settings" button */
    protected JButton _restoreDefaultButton;

    /** "Save Modifications" button */
    protected JButton _saveModificationButton;

    /**
     * Constructor.
     */
    public PreferencesView()
    {
        super("Preferences");

        // Window size
        setSize(480, 360);
        setResizable(false);

        // Window screen position (centered)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize  = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);

        // Get and listen to data model modifications
        _preferences = Preferences.getInstance();

        // Build the tabbed pane
        JTabbedPane tabbedPane  = new JTabbedPane();
        Container   contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(tabbedPane);

        // Add the query preferences pane
        QueryPreferencesView queryView = new QueryPreferencesView();
        tabbedPane.add("Query", queryView);

        // Add the columns preferences pane
        ColumnsPreferencesView columnsView = new ColumnsPreferencesView(
                "star.properties.order");
        tabbedPane.add("Columns", columnsView);

        // Add the catalog preferences pane
        JPanel catalogView = new LegendView();
        tabbedPane.add("Catalogs", catalogView);

        // Add the network preferences pane
        NetworkPreferencesView networkView = new NetworkPreferencesView();
        tabbedPane.add("Network", networkView);

        // Add the help preferences pane
        HelpPreferencesView helpView = new HelpPreferencesView();
        tabbedPane.add("Help", helpView);

        // Add the restore and sace buttons
        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore to Default Settings");
        _restoreDefaultButton.addActionListener(this);
        buttonsPanel.add(_restoreDefaultButton);
        _saveModificationButton = new JButton("Save Modifications");
        _saveModificationButton.addActionListener(this);
        buttonsPanel.add(_saveModificationButton);
        contentPane.add(buttonsPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        // If the "Restore to default settings" button has been pressed
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

        // If the "Save modifications" button has been pressed
        if (evt.getSource().equals(_saveModificationButton))
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
 * This panel is dedicated to query default values configuration.
 */
class QueryPreferencesView extends JPanel implements Observer, ActionListener
{
    /** Data model */
    private Preferences _preferences;

    /**  Min magnitude delta textfield */
    private JFormattedTextField _minMagnitudeDeltaTextfield;

    /**  Max magnitude delta textfield */
    private JFormattedTextField _maxMagnitudeDeltaTextfield;

    /**
     * Constructor.
     * @param preferences the application preferences
     */
    public QueryPreferencesView()
    {
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Include Science Object Checkbox
        JCheckBox cb = new JCheckBox(QueryView._includeScienceObjectAction);
        cb.setModel(PreferencedButtonModel.getInstance(_preferences,
                "science.includeObject"));
        add(cb);

        JPanel    panel;
        JLabel    label;
        Dimension textfieldDimension = new Dimension(100, 20);

        panel     = new JPanel();
        label     = new JLabel("Minimum Magnitude Delta :");
        panel.add(label);
        _minMagnitudeDeltaTextfield = new JFormattedTextField(new Double(0));
        _minMagnitudeDeltaTextfield.setMinimumSize(textfieldDimension);
        _minMagnitudeDeltaTextfield.setPreferredSize(textfieldDimension);
        _minMagnitudeDeltaTextfield.addActionListener(this);
        panel.add(_minMagnitudeDeltaTextfield);
        add(panel);

        panel     = new JPanel();
        label     = new JLabel("Maximum Magnitude Delta :");
        panel.add(label);
        _maxMagnitudeDeltaTextfield = new JFormattedTextField(new Double(0));
        _maxMagnitudeDeltaTextfield.setMinimumSize(textfieldDimension);
        _maxMagnitudeDeltaTextfield.setPreferredSize(textfieldDimension);
        _maxMagnitudeDeltaTextfield.addActionListener(this);
        panel.add(_maxMagnitudeDeltaTextfield);
        add(panel);

        // Make data filled
        update(null, null);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg)
    {
        Double d;

        d = _preferences.getPreferenceAsDouble("query.queryMinMagnitudeDelta");
        _minMagnitudeDeltaTextfield.setValue(d);

        d = _preferences.getPreferenceAsDouble("query.queryMaxMagnitudeDelta");
        _maxMagnitudeDeltaTextfield.setValue(d);
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        MCSLogger.trace();

        Object source = evt.getSource();

        if (source.equals(_minMagnitudeDeltaTextfield))
        {
            try
            {
                _preferences.setPreference("query.queryMinMagnitudeDelta",
                    ((Double) _minMagnitudeDeltaTextfield.getValue()));
            }
            catch (Exception ex)
            {
                //@TODO
            }
        }

        if (source.equals(_maxMagnitudeDeltaTextfield))
        {
            try
            {
                _preferences.setPreference("query.queryMaxMagnitudeDelta",
                    ((Double) _maxMagnitudeDeltaTextfield.getValue()));
            }
            catch (Exception ex)
            {
                //@TODO
            }
        }
    }
}


/**
 * This Panel is dedicated to manage one ordered list of words.
 */
class ColumnsPreferencesView extends JPanel implements Observer, ActionListener
{
    /** Data model */
    private Preferences _preferences;

    /** The name of preference that must be managed as a ordered words. */
    private String _preferenceName;

    /** The widget that dispaly the list of words */
    private JList _wordsList;

    /** The model associated to the widget that dispaly the list of words */
    private DefaultListModel _listModel;

    /** The actual shown list of words */
    private String _actualWords = "";

    /** Move up Button */
    private JButton _moveUpButton;

    /** Move down Button */
    private JButton _moveDownButton;

    /** Add Button */
    private JButton _addWordButton;

    /** Remove  Button */
    private JButton _removeWordButton;

    /**
     * Constructor.
     * @param preferences the application preferences
     * @param preferenceName the preference name that list the words separated
     * by spaces
     */
    public ColumnsPreferencesView(String preferenceName)
    {
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        _preferenceName = preferenceName;

        //Set vertical layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Append list with associated model into a scrollPane
        _listModel     = new DefaultListModel();
        _wordsList     = new JList(_listModel);

        JScrollPane scrollingList = new JScrollPane(_wordsList);
        add(scrollingList);

        // Append move buttons
        JPanel panel = new JPanel();
        _moveUpButton = new JButton("Up");
        _moveUpButton.addActionListener(this);
        panel.add(_moveUpButton);
        _moveDownButton = new JButton("Down");
        _moveDownButton.addActionListener(this);
        panel.add(_moveDownButton);
        add(panel);

        // Make data filled
        update(null, null);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg)
    {
        // Fill list with ordered words if _actualWord is not equal
        String words = _preferences.getPreference(_preferenceName);

        if (words == null)
        {
            MCSLogger.finest(_preferenceName + " not found into preferences");

            return;
        }

        if (! words.equals(_actualWords))
        {
            _actualWords = words;

            StringTokenizer st = new StringTokenizer(_actualWords);

            while (st.hasMoreTokens())
            {
                String word = st.nextToken();
                _listModel.removeElement(word);
                _listModel.addElement(word);
            }
        }
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        boolean listModified = false;

        // test pressed button origin
        if (evt.getSource().equals(_moveDownButton))
        {
            // Get the index of the first selected item
            int firstSelIx = _wordsList.getSelectedIndex();
            int nextIx     = firstSelIx + 1;

            if ((nextIx < _listModel.size()) && (firstSelIx != -1))
            {
                Object el = _listModel.getElementAt(firstSelIx);
                _listModel.removeElementAt(firstSelIx);

                if (nextIx == _listModel.size())
                {
                    _listModel.addElement(el);
                }
                else
                {
                    _listModel.add(nextIx, el);
                }

                _wordsList.setSelectionInterval(nextIx, nextIx);
                listModified = true;
            }
        }

        if (evt.getSource().equals(_moveUpButton))
        {
            // Get the index of the first selected item
            int firstSelIx = _wordsList.getSelectedIndex();
            int nextIx     = firstSelIx - 1;

            if ((firstSelIx > 0) && (firstSelIx != -1))
            {
                Object el = _listModel.getElementAt(firstSelIx);
                _listModel.removeElementAt(firstSelIx);
                _listModel.add(nextIx, el);
                _wordsList.setSelectionInterval(nextIx, nextIx);
                listModified = true;
            }
        }

        if (listModified)
        {
            StringBuffer sb = new StringBuffer();
            Enumeration  en = _listModel.elements();

            while (en.hasMoreElements())
            {
                Object o = en.nextElement();
                sb.append(" " + o);
            }

            try
            {
                _preferences.setPreference(_preferenceName, sb.toString());
            }
            catch (Exception e)
            {
                // @TODO
            }
        }
    }
}


/**
 * This Panel is dedicated to manage network preferences.
 */
class NetworkPreferencesView extends JPanel implements Observer
{
    /** Data model */
    private Preferences _preferences;

    /**
     * Constructor.
     * @param preferences the application preferences
     */
    public NetworkPreferencesView()
    {
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        // @TODO : implement
        add(new JLabel("TODO"));

        // Make data filled
        update(null, null);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg)
    {
        // Fill with preferences entries
    }
}


/**
 * This Panel is dedicated to manage help behaviour configuration.
 */
class HelpPreferencesView extends JPanel implements Observer, ChangeListener
{
    /** Data model */
    private Preferences _preferences;

    /**
     * DOCUMENT ME!
     */
    private JCheckBox _enableToolTipCheckBox;

    /**
     * DOCUMENT ME!
     */
    private ToolTipManager _sharedToolTipManager;

    /**
     * Constructor.
     * @param preferences the application preferences
     */
    public HelpPreferencesView()
    {
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // get instance of shared tooltip to adjust behaviour in update code
        _sharedToolTipManager      = ToolTipManager.sharedInstance();

        // Handle tooltips
        _enableToolTipCheckBox     = new JCheckBox("Show tooltips");

        String ttt                 = Resources.getToolTipText(
                "_enableToolTipCheckBox");
        _enableToolTipCheckBox.setToolTipText(ttt);
        _sharedToolTipManager.registerComponent(_enableToolTipCheckBox);
        _enableToolTipCheckBox.addChangeListener(this);
        add(_enableToolTipCheckBox);

        // Handle include science object name
        Hashtable booleanPrefs = new Hashtable(); // Table of:  pref->Action
        booleanPrefs.put("view.details.show", CalibratorsView._showDetailsAction);
        booleanPrefs.put("view.legend.show", CalibratorsView._showLegendAction);

        // Create a checkbox for every pref and action entries
        for (Enumeration e = booleanPrefs.keys(); e.hasMoreElements();)
        {
            String    pref   = (String) e.nextElement();
            Action    action = (Action) booleanPrefs.get(pref);
            JCheckBox cb     = new JCheckBox(action);
            cb.setModel(PreferencedButtonModel.getInstance(_preferences, pref));
            add(cb);
        }

        // Make data filled
        update(null, null);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg)
    {
        // Adjust view and behaviour according preferences entries
        boolean b;

        // Tooltips
        b = _preferences.getPreferenceAsBoolean("help.tooltips.show");
        _enableToolTipCheckBox.setSelected(b);
        _sharedToolTipManager.setEnabled(b);
    }

    /**
     * Update preferences according buttons change
     */
    public void stateChanged(ChangeEvent ev)
    {
        MCSLogger.trace();

        Object source = ev.getSource();

        if (source.equals(_enableToolTipCheckBox))
        {
            try
            {
                _preferences.setPreference("help.tooltips.show",
                    _enableToolTipCheckBox.isSelected());
            }
            catch (Exception e)
            {
                // @TODO
            }
        }
    }
}
