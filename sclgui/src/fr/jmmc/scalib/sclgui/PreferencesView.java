/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.35 2010-10-10 22:45:03 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.34  2010/10/10 22:21:04  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.33  2010/09/10 13:52:01  lafrasse
 * Added TODO.
 *
 * Revision 1.32  2009/11/04 10:17:22  lafrasse
 * Revamped simple/detailed results view settings to add "full results" support.
 *
 * Revision 1.31  2008/11/28 13:12:55  lafrasse
 * Removed unused query preference pane.
 *
 * Revision 1.30  2008/09/18 21:50:53  lafrasse
 * Moved _showLegendAction and _showDetailsAction to RegisteredPreferencedBooleanAction, in order to properly handle menu items et preference view checkboxes.
 *
 * Revision 1.29  2008/09/10 22:31:06  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.28  2007/12/03 14:41:59  lafrasse
 * Removed science object inclusion preference.
 * Changed default query to match the one of the previous version.
 *
 * Revision 1.27  2007/10/09 14:41:04  lafrasse
 * Added handling of all different sets of columns order.
 *
 * Revision 1.26  2007/08/27 07:39:21  lafrasse
 * TextFields label and GUI layout enhancements.
 *
 * Revision 1.25  2007/08/17 12:06:58  lafrasse
 * Jalopization.
 *
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
 * Updated _logger APIs to more tightly reflect standard MCS 'log' module levels.
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

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;

// @TODO handle close button correctly
// @TODO add deleteObserver(this) to dispose() to dereference each subview properly
/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends JFrame implements ActionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.PreferencesView");
    /** Data model */
    Preferences _preferences = null;
    /** "Restore to Default Settings" button */
    protected JButton _restoreDefaultButton = null;
    /** "Save Modifications" button */
    protected JButton _saveModificationButton = null;

    /**
     * Constructor.
     */
    public PreferencesView() {
        super("Preferences");

        // Window size
        setSize(600, 400);
        setResizable(false);

        // Window screen position (centered)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

        // Get and listen to data model modifications
        _preferences = Preferences.getInstance();

        // Build the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(tabbedPane);

        // Add the columns preferences pane
        ColumnsPreferencesView columnsView = new ColumnsPreferencesView(
                "view.columns");
        tabbedPane.add("Columns Order", columnsView);

        // Add the catalog preferences pane
        JPanel catalogView = new LegendView(true);
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
    public void actionPerformed(ActionEvent evt) {
        _logger.entering("PreferencesView", "actionPerformed");

        // If the "Restore to default settings" button has been pressed
        if (evt.getSource().equals(_restoreDefaultButton)) {
            try {
                _preferences.resetToDefaultPreferences();
            } catch (Exception ex) {
                _logger.log(Level.WARNING,
                        "Could not reset preferences to default.", ex);
            }
        }

        // If the "Save modifications" button has been pressed
        if (evt.getSource().equals(_saveModificationButton)) {
            try {
                _preferences.saveToFile();
            } catch (Exception ex) {
                _logger.log(Level.WARNING, "Could not save preferences.", ex);
            }
        }
    }
}

/**
 * This Panel is dedicated to manage one ordered list of columns.
 */
class ColumnsPreferencesView extends JPanel implements Observer, ActionListener,
        ListSelectionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.ColumnsPreferencesView");
    /** Data model */
    private Preferences _preferences = null;
    /** The name of preference that must be managed as ordered columns. */
    private String _preferencePath = null;
    /** The widget that dispaly the list of columns */
    private JList _columnList = null;
    /** The model associated to the widget that dispaly the list of columns */
    private DefaultListModel _listModel = null;
    /** The actual shown list of columns */
    private String _shownColumns = "";
    /** All available columns set */
    JComboBox _columnsSetCombobox = null;
    /** The column sets combobox model */
    private DefaultComboBoxModel _columsSetModel = null;
    /** The column sets combobox names to path correspondance table */
    private HashMap<String, String> _columnSetNameToPathHashtable = null;
    /** Move up Button */
    private JButton _moveUpButton = null;
    /** Move down Button */
    private JButton _moveDownButton = null;

    /**
     * Constructor.
     *
     * @param preferenceName the preference name root that is the prefix to all
     * the lists of words separated by spaces composing columns sets.
     */
    public ColumnsPreferencesView(String preferencePrefix) {
        // Register against shared application preferences
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        // Set the pane vertical layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Column sets combobox creation
        JPanel headPanel = new JPanel();
        headPanel.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Column Set : ", JLabel.TRAILING);
        panel.add(label);
        _columnsSetCombobox = new JComboBox();
        label.setLabelFor(_columnsSetCombobox);
        panel.add(_columnsSetCombobox);
        _columnsSetCombobox.addActionListener(this);
        headPanel.add(panel, BorderLayout.LINE_START);
        add(headPanel);

        // Fullfil the combobox model with each desired columns set names
        _columsSetModel = new DefaultComboBoxModel();
        _columnSetNameToPathHashtable = new HashMap<String, String>();

        Enumeration e = _preferences.getPreferences(preferencePrefix);

        while (e.hasMoreElements()) {
            String preferencePath = (String) e.nextElement();

            // Compute the column set name
            String columnSetName = preferencePath.substring(preferencePrefix.length()
                    + 1);
            columnSetName = columnSetName.replace('.', ' ');

            // Fullfil the Combobox model and 'name to path' table
            _columnSetNameToPathHashtable.put(columnSetName, preferencePath);
            _columsSetModel.addElement(columnSetName);
        }

        _columnsSetCombobox.setModel(_columsSetModel);
        _preferencePath = (String) _columnSetNameToPathHashtable.get((String) _columsSetModel.getSelectedItem());

        // Instanciate the list widget and its associated model
        _listModel = new DefaultListModel();
        _columnList = new JList(_listModel);

        // Only one item can be selected at ance.
        _columnList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

        // Add a 'selection modification' listener to enable/disable 'Up' & 'Down' buttons
        _columnList.addListSelectionListener(this);

        // Add scrolling capacity to the list
        JScrollPane scrollingList = new JScrollPane(_columnList);
        scrollingList.setPreferredSize(new Dimension(400, 250));
        scrollingList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollingList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollingList.getVerticalScrollBar().setFocusable(false);
        scrollingList.setBorder(BorderFactory.createLoweredBevelBorder());
        add(scrollingList);

        // Add 'Up' & 'Down' buttons
        panel = new JPanel();
        _moveUpButton = new JButton("Up");
        _moveUpButton.addActionListener(this);
        _moveUpButton.setEnabled(false);
        panel.add(_moveUpButton);
        _moveDownButton = new JButton("Down");
        _moveDownButton.addActionListener(this);
        _moveDownButton.setEnabled(false);
        panel.add(_moveDownButton);
        add(panel);
    }

    /**
     * Present fresh content according to preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg) {
        _logger.entering("ColumnsPreferencesView", "update");

        // Fill list with ordered columns if _actualWord is not equal
        String columns = _preferences.getPreference(_preferencePath);

        if (columns == null) {
            _logger.fine(_preferencePath + " not found into preferences");

            return;
        }

        // If the preference is different than the current list
        if (columns.equals(_shownColumns) == false) {
            _shownColumns = columns;

            // Clear the list
            _listModel.clear();

            // Serialize the new list to update the GUI
            StringTokenizer st = new StringTokenizer(_shownColumns);

            while (st.hasMoreTokens()) {
                String columnName = st.nextToken();
                _listModel.addElement(columnName);
            }
        }
    }

    /**
     * This method is called each time the user changes the set of selected items
     *
     * @param evt ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent evt) {
        _logger.entering("ColumnsPreferencesView", "valueChanged");

        // When the user release the mouse button and completes the selection,
        // getValueIsAdjusting() becomes false
        if (evt.getValueIsAdjusting() == false) {
            JList list = (JList) evt.getSource();
            int index = list.getSelectedIndex();
            int size = _listModel.size();

            // If no item is selected
            if (index == -1) {
                _logger.fine("Buttons DISABLED (no selection).");
                _moveUpButton.setEnabled(false);
                _moveDownButton.setEnabled(false);

                return;
            }

            // If the first item is selected
            if (index == 0) {
                // Disable the 'Up' button.
                _logger.fine(
                        "'Up' Button DISABLED (the first item IS selected).");
                _moveUpButton.setEnabled(false);
            } else {
                // Enable the 'Up' button.
                _logger.fine(
                        "'Up' Button ENABLED (the first item is NOT selected).");
                _moveUpButton.setEnabled(true);
            }

            // If the last item is selected
            if (index == (size - 1)) {
                // Disable the 'Down' button.
                _logger.fine(
                        "'Down' Button DISABLED (the last item IS selected).");
                _moveDownButton.setEnabled(false);
            } else {
                // Enable the 'Down' button.
                _logger.fine(
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
    public void actionPerformed(ActionEvent evt) {
        _logger.entering("ColumnsPreferencesView", "actionPerformed");

        // If the ComboBox was used
        if (evt.getSource().equals(_columnsSetCombobox)) {
            // Get the the newly selected column set preference path
            _preferencePath = _columnSetNameToPathHashtable.get((String) _columsSetModel.getSelectedItem());

            // Refresh the GUI to update the list
            update(null, null);

            return;
        }

        // Get the index of the current selected item
        int currentSelection = _columnList.getSelectedIndex();

        // Hold the future index of the selected item
        int futureSelection = 0;

        // If the 'Down' button was pressed
        if (evt.getSource().equals(_moveDownButton)) {
            // Compute the future index of the selected item
            futureSelection = currentSelection + 1;

            // If current and future selection indexes are out of range
            if ((currentSelection == -1)
                    || (futureSelection >= _listModel.size())) {
                _logger.fine("Assertion failed : selection out of range.");

                return;
            }

            // Move the selected item
            Object column = _listModel.getElementAt(currentSelection);
            _listModel.removeElementAt(currentSelection); // Remove original

            if (futureSelection == _listModel.size()) {
                _listModel.addElement(column); // Add at list tail
            } else {
                _listModel.add(futureSelection, column); // Insert at position
            }
        }

        // If the 'Up' button was pressed
        if (evt.getSource().equals(_moveUpButton)) {
            // Compute the future index of the selected item
            futureSelection = currentSelection - 1;

            // If current and future selection indexes are out of range
            if ((currentSelection == -1) || (futureSelection < 0)) {
                _logger.fine("Assertion failed : selection out of range.");

                return;
            }

            // Move the selected item
            Object column = _listModel.getElementAt(currentSelection);
            _listModel.removeElementAt(currentSelection); // Remove original
            _listModel.add(futureSelection, column); // Insert at position
        }

        // Serialize the new columns name order
        StringBuilder sb = new StringBuilder();
        Enumeration en = _listModel.elements();

        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            sb.append(" ").append(o);
        }

        // Save the new order in preference
        try {
            _preferences.setPreference(_preferencePath, sb.toString());
        } catch (Exception ex) {
            _logger.warning("Could not set '" + _preferencePath
                    + "' preference : " + ex);
        }

        /*
         * Update selection to follow the moved item (at end: after preference
         * changes triggered GUI update, otherwise selection disapears)
         */
        _columnList.setSelectionInterval(futureSelection, futureSelection);
    }
}

/**
 * This Panel is dedicated to manage help behaviour configuration.
 */
class HelpPreferencesView extends JPanel implements Observer, ChangeListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.scalib.sclgui.HelpPreferencesView");
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
    public HelpPreferencesView() {
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        // Layout management
        JPanel tempPanel = new JPanel();
        add(tempPanel);
        tempPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridy = 0;
        c.gridx = 0;

        // Get instance of shared tooltip to adjust behaviour in update code
        _sharedToolTipManager = ToolTipManager.sharedInstance();

        // Handle "Result Verbosity" radio buttons
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;

        JLabel label = new JLabel("Results Verbosity : ", JLabel.TRAILING);
        radioPanel.add(label, gbc);
        gbc.gridx++;

        ButtonGroup radioGroup = new ButtonGroup();
        JRadioButton syntheticRadioButton = new JRadioButton(CalibratorsView._syntheticResultsVerbosityAction);
        CalibratorsView._syntheticResultsVerbosityAction.addBoundButton(syntheticRadioButton);
        radioGroup.add(syntheticRadioButton);
        radioPanel.add(syntheticRadioButton, gbc);
        gbc.gridy++;

        JRadioButton detailledRadioButton = new JRadioButton(CalibratorsView._detailledResultsVerbosityAction);
        CalibratorsView._detailledResultsVerbosityAction.addBoundButton(detailledRadioButton);
        radioGroup.add(detailledRadioButton);
        radioPanel.add(detailledRadioButton, gbc);
        gbc.gridy++;

        JRadioButton fullRadioButton = new JRadioButton(CalibratorsView._fullResultsVerbosityAction);
        CalibratorsView._fullResultsVerbosityAction.addBoundButton(fullRadioButton);
        radioGroup.add(fullRadioButton);
        radioPanel.add(fullRadioButton, gbc);
        tempPanel.add(radioPanel, c);
        c.gridy++;

        // Handle "Show Legend" checkbox
        JCheckBox showLegendCheckBox = new JCheckBox(CalibratorsView._showLegendAction);
        CalibratorsView._showLegendAction.addBoundButton(showLegendCheckBox);
        tempPanel.add(showLegendCheckBox, c);
        c.gridy++;

        // Handle "Show Tooltips" checkbox
        _enableToolTipCheckBox = new JCheckBox("Show Tooltips");
        _sharedToolTipManager.registerComponent(_enableToolTipCheckBox);
        _enableToolTipCheckBox.addChangeListener(this);
        tempPanel.add(_enableToolTipCheckBox, c);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg) {
        _logger.entering("HelpPreferencesView", "update");

        // Adjust view and behaviour according preferences entries
        boolean b = _preferences.getPreferenceAsBoolean("help.tooltips.show");
        _enableToolTipCheckBox.setSelected(b);
        _sharedToolTipManager.setEnabled(b);
    }

    /**
     * Update preferences according buttons change
     */
    public void stateChanged(ChangeEvent ev) {
        _logger.entering("HelpPreferencesView", "stateChanged");

        Object source = ev.getSource();

        try {
            if (source.equals(_enableToolTipCheckBox)) {
                _preferences.setPreference("help.tooltips.show",
                        _enableToolTipCheckBox.isSelected());
            }
        } catch (Exception ex) {
            _logger.warning("Could not set preference : " + ex);
        }
    }
}
