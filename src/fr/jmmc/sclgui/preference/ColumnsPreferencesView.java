/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

import fr.jmmc.jmcs.gui.component.AlternateRawColorCellRenderer;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This Panel is dedicated to manage one ordered list of columns.
 */
class ColumnsPreferencesView extends JPanel implements Observer, ActionListener,
        ListSelectionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(ColumnsPreferencesView.class.getName());
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
    /** The column sets combo box model */
    private DefaultComboBoxModel _columsSetModel = null;
    /** The column sets combo box names to path correspondance table */
    private HashMap<String, String> _columnSetNameToPathHashtable = null;
    /** Move up Button */
    private JButton _moveUpButton = null;
    /** Move down Button */
    private JButton _moveDownButton = null;

    /**
     * Constructor.
     * @param preferencePrefix 
     */
    ColumnsPreferencesView(String preferencePrefix) {
        // Register against shared application preferences
        _preferences = Preferences.getInstance();

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
        _preferencePath = _columnSetNameToPathHashtable.get((String) _columsSetModel.getSelectedItem());

        // Instanciate the list widget and its associated model
        _listModel = new DefaultListModel();
        _columnList = new JList(_listModel);

        // Only one item can be selected at ance.
        _columnList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

        _columnList.setCellRenderer(new AlternateRawColorCellRenderer());

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
        _moveUpButton.setEnabled(false);
        panel.add(_moveUpButton);
        _moveDownButton = new JButton("Down");
        _moveDownButton.setEnabled(false);
        panel.add(_moveDownButton);
        add(panel);
    }

    public void init() {
        _preferences.addObserver(this);
        _columnsSetCombobox.addActionListener(this);

        // Add a 'selection modification' listener to enable/disable 'Up' & 'Down' buttons
        _columnList.addListSelectionListener(this);

        _moveUpButton.addActionListener(this);
        _moveDownButton.addActionListener(this);
    }

    /**
     * Present fresh content according to preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        _logger.entering("ColumnsPreferencesView", "update");

        // Fill list with ordered columns if _actualWord is not equal
        String columns = _preferences.getPreference(_preferencePath);

        if (columns == null) {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine(_preferencePath + " not found into preferences");
            }

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
    @Override
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
    @Override
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
        _columnList.clearSelection();

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

        final int selection = futureSelection;
        
        SwingUtils.invokeLaterEDT(new Runnable() {

            @Override
            public void run() {
                /*
                 * Update selection to follow the moved item (at end: after preference
                 * changes triggered GUI update, otherwise selection disapears)
                 */
                _columnList.setSelectionInterval(selection, selection);
            }
        });
    }
}
