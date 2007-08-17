/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.25 2007-08-17 12:06:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.24  2007/08/17 10:23:35  lafrasse
 * Enhanced "Simple Columns Order" view.
 *
 * Revision 1.23  2007/08/16 12:19:58  lafrasse
 * Enhanced precision of formatted textfields for double values.
 *
 * Revision 1.22  2007/06/29 09:56:12  lafrasse
 * Removed unimplemented widget (science object inclusion checkbox, Network pane).
 *
 * Revision 1.21  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.20  2007/06/14 13:33:42  lafrasse
 * Corrected column sorting preference entry name.
 *
 * Revision 1.19  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.18  2006/10/10 09:09:37  lafrasse
 * Updated MCSLogger APIs to more tightly reflect standard MCS 'log' module levels.
 *
 * Revision 1.17  2006/10/04 11:34:31  lafrasse
 * Added support for preferenced science object detection distance.
 *
 * Revision 1.16  2006/10/03 15:31:19  lafrasse
 * Added support for preferenced min and max magnitude auto-update deltas.
 *
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
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;

import java.text.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;


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
        setSize(600, 400);
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
        tabbedPane.add("Query Settings", queryView);

        // Add the columns preferences pane
        ColumnsPreferencesView columnsView = new ColumnsPreferencesView(
                "view.simple.columns");
        tabbedPane.add("Simple Columns Order", columnsView);

        // Add the catalog preferences pane
        JPanel catalogView = new LegendView();
        tabbedPane.add("Legend Colors", catalogView);

        // Add the help preferences pane
        HelpPreferencesView helpView = new HelpPreferencesView();
        tabbedPane.add("Help Settings", helpView);

        // Add the restore and sace buttons
        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore Default Settings");
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
     *
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
            catch (Exception ex)
            {
                MCSLogger.warning("Could not reset preferences to default : " +
                    ex);
            }
        }

        // If the "Save modifications" button has been pressed
        if (evt.getSource().equals(_saveModificationButton))
        {
            try
            {
                _preferences.saveToFile();
            }
            catch (Exception ex)
            {
                MCSLogger.warning("Could not save preferences : " + ex);
            }
        }
    }
}


/**
 * This panel is dedicated to query default values configuration.
 */
class QueryPreferencesView extends JPanel implements Observer, ActionListener,
    FocusListener
{
    /** Data model */
    private Preferences _preferences;

    /** Science object distance tolerance */
    private JFormattedTextField _scienceObjectDetectionDistanceTextfield = new JFormattedTextField(new Double(
                0.0));

    /** Min magnitude delta textfield */
    private JFormattedTextField _minMagnitudeDeltaTextfield = new JFormattedTextField(new Double(
                0.0));

    /** Max magnitude delta textfield */
    private JFormattedTextField _maxMagnitudeDeltaTextfield = new JFormattedTextField(new Double(
                0.0));

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

        // @TODO : removed (not implemented)
        // add(cb);

        // JFormattedTextField formatter creation
        DefaultFormatter doubleFormater = new NumberFormatter(new DecimalFormat(
                    "0.0####"));
        doubleFormater.setValueClass(java.lang.Double.class);

        DefaultFormatterFactory doubleFormaterFactory = new DefaultFormatterFactory(doubleFormater,
                doubleFormater, doubleFormater);

        JPanel                  panel;
        JLabel                  label;
        Dimension               textfieldDimension    = new Dimension(100, 20);

        panel                                         = new JPanel();
        label                                         = new JLabel(
                "Science Object Detection Distance :");
        panel.add(label);
        _scienceObjectDetectionDistanceTextfield.setFormatterFactory(doubleFormaterFactory);
        _scienceObjectDetectionDistanceTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectDetectionDistanceTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectDetectionDistanceTextfield.addActionListener(this);
        panel.add(_scienceObjectDetectionDistanceTextfield);
        add(panel);

        panel     = new JPanel();
        label     = new JLabel("Minimum Magnitude Delta :");
        panel.add(label);
        _minMagnitudeDeltaTextfield.setFormatterFactory(doubleFormaterFactory);
        _minMagnitudeDeltaTextfield.setMinimumSize(textfieldDimension);
        _minMagnitudeDeltaTextfield.setPreferredSize(textfieldDimension);
        _minMagnitudeDeltaTextfield.addActionListener(this);
        panel.add(_minMagnitudeDeltaTextfield);
        add(panel);

        panel     = new JPanel();
        label     = new JLabel("Maximum Magnitude Delta :");
        panel.add(label);
        _maxMagnitudeDeltaTextfield.setFormatterFactory(doubleFormaterFactory);
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

        d = _preferences.getPreferenceAsDouble(
                "query.scienceObjectDetectionDistance");
        _scienceObjectDetectionDistanceTextfield.setValue(d);

        d = _preferences.getPreferenceAsDouble("query.queryMinMagnitudeDelta");
        _minMagnitudeDeltaTextfield.setValue(d);

        d = _preferences.getPreferenceAsDouble("query.queryMaxMagnitudeDelta");
        _maxMagnitudeDeltaTextfield.setValue(d);
    }

    /**
     * Called when the focus enters a widget.
     */
    public void focusGained(FocusEvent e)
    {
        // Does nothing (not needed)
    }

    /**
     * Called when the focus leaves a widget.
     *
     * Used to validate and store TextFields data when tabbing between them.
     */
    public void focusLost(FocusEvent e)
    {
        MCSLogger.trace();

        // Store new data
        storeValues(e);
    }

    /**
     * Called when a widget triggered an action.
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        // Store new data
        storeValues(e);
    }

    /**
     * Store form values in the model.
     */
    public void storeValues(AWTEvent e)
    {
        MCSLogger.trace();

        Object source = e.getSource();

        if (source.equals(_scienceObjectDetectionDistanceTextfield))
        {
            try
            {
                _preferences.setPreference("query.scienceObjectDetectionDistance",
                    ((Double) _scienceObjectDetectionDistanceTextfield.getValue()));
            }
            catch (Exception ex)
            {
                MCSLogger.warning("Could not set preference : " + ex);
            }
        }

        if (source.equals(_minMagnitudeDeltaTextfield))
        {
            try
            {
                _preferences.setPreference("query.queryMinMagnitudeDelta",
                    ((Double) _minMagnitudeDeltaTextfield.getValue()));
            }
            catch (Exception ex)
            {
                MCSLogger.warning("Could not set preference : " + ex);
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
                MCSLogger.warning("Could not set preference : " + ex);
            }
        }
    }
}


/**
 * This Panel is dedicated to manage one ordered list of words.
 */
class ColumnsPreferencesView extends JPanel implements Observer, ActionListener,
    ListSelectionListener
{
    /** Data model */
    private Preferences _preferences;

    /** The name of preference that must be managed as ordered words. */
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

        // Set vertical layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Append list with associated model into a scrollPane
        _listModel     = new DefaultListModel();
        _wordsList     = new JList(_listModel);

        // Only one item can be selected at a time.
        _wordsList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

        // Add a 'selection modification' listener to enable 'Up' & 'Down buttons
        _wordsList.addListSelectionListener(this);

        // Add scrolling capacity to the list
        JScrollPane scrollingList = new JScrollPane(_wordsList);
        scrollingList.setPreferredSize(new Dimension(400, 250));
        scrollingList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollingList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollingList.getVerticalScrollBar().setFocusable(false);
        scrollingList.setBorder(BorderFactory.createLoweredBevelBorder());
        add(scrollingList);

        // Append 'Up' & 'Down' buttons
        JPanel panel = new JPanel();
        _moveUpButton = new JButton("Up");
        _moveUpButton.addActionListener(this);
        _moveUpButton.setEnabled(false);
        panel.add(_moveUpButton);
        _moveDownButton = new JButton("Down");
        _moveDownButton.addActionListener(this);
        _moveDownButton.setEnabled(false);
        panel.add(_moveDownButton);
        add(panel);

        // Populate GUI
        update(null, null);
    }

    /**
     * Present fresh content according to preference content.
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
            MCSLogger.debug(_preferenceName + " not found into preferences");

            return;
        }

        // If the preference is different than the current list
        if (words.equals(_actualWords) == false)
        {
            _actualWords = words;

            // Serialize the new list to update the GUI
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
     * This method is called each time the user changes the set of selected items
     *
     * @param evt ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent evt)
    {
        // When the user release the mouse button and completes the selection,
        // getValueIsAdjusting() becomes false
        if (evt.getValueIsAdjusting() == false)
        {
            JList list  = (JList) evt.getSource();
            int   index = list.getSelectedIndex();
            int   size  = _listModel.size();

            // If no item is selected
            if (index == -1)
            {
                MCSLogger.debug("Buttons DISABLED (no selection).");
                _moveUpButton.setEnabled(false);
                _moveDownButton.setEnabled(false);

                return;
            }

            // If the first item is selected
            if (index == 0)
            {
                // Disable the 'Up' button.
                MCSLogger.debug(
                    "'Up' Button DISABLED (the first item IS selected).");
                _moveUpButton.setEnabled(false);
            }
            else
            {
                // Enable the 'Up' button.
                MCSLogger.debug(
                    "'Up' Button ENABLED (the first item is NOT selected).");
                _moveUpButton.setEnabled(true);
            }

            // If the last item is selected
            if (index == (size - 1))
            {
                // Disable the 'Down' button.
                MCSLogger.debug(
                    "'Down' Button DISABLED (the last item IS selected).");
                _moveDownButton.setEnabled(false);
            }
            else
            {
                // Enable the 'Down' button.
                MCSLogger.debug(
                    "'Down' Button ENABLED (the last item is NOT selected).");
                _moveDownButton.setEnabled(true);
            }
        }
    }

    /**
     * actionPerformed  -  Listener
     *
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        boolean listModified = false;

        // Get the index of the current selected item
        int currentSelection = _wordsList.getSelectedIndex();
        int futureSelection  = currentSelection;

        // If the 'Down' button was pressed
        if (evt.getSource().equals(_moveDownButton))
        {
            // Compute the future index
            futureSelection++;

            if ((currentSelection != -1) &&
                    (futureSelection < _listModel.size()))
            {
                Object el = _listModel.getElementAt(currentSelection);

                _listModel.removeElementAt(currentSelection);

                if (futureSelection == _listModel.size())
                {
                    _listModel.addElement(el);
                }
                else
                {
                    _listModel.add(futureSelection, el);
                }

                listModified = true;
            }
        }

        // If the 'Up' button was pressed
        if (evt.getSource().equals(_moveUpButton))
        {
            // Compute the future index
            futureSelection--;

            if ((currentSelection != -1) && (currentSelection > 0))
            {
                Object el = _listModel.getElementAt(currentSelection);

                _listModel.removeElementAt(currentSelection);
                _listModel.add(futureSelection, el);

                listModified = true;
            }
        }

        // If the list has been modified
        if (listModified == true)
        {
            // Serialize columns name order
            StringBuffer sb = new StringBuffer();
            Enumeration  en = _listModel.elements();

            while (en.hasMoreElements())
            {
                Object o = en.nextElement();
                sb.append(" " + o);
            }

            // Save this order in preference
            try
            {
                _preferences.setPreference(_preferenceName, sb.toString());
            }
            catch (Exception ex)
            {
                MCSLogger.warning("Could not set '" + _preferenceName +
                    "' preference : " + ex);
            }

            _wordsList.setSelectionInterval(futureSelection, futureSelection);
        }
    }
}


/**
 * This Panel is dedicated to manage help behaviour configuration.
 */
class HelpPreferencesView extends JPanel implements Observer, ChangeListener
{
    /** Data model */
    private Preferences _preferences;

    /** Tooltip enabling checkbox */
    private JCheckBox _enableToolTipCheckBox;

    /** Tooltip manager */
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
            catch (Exception ex)
            {
                MCSLogger.warning("Could not set preference : " + ex);
            }
        }
    }
}
