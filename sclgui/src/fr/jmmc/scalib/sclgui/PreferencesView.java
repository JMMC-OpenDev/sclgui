/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.9 2006-06-19 11:22:45 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.table.*;


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

    /**
     * DOCUMENT ME!
     */
    protected JButton _saveChangesButton;

    /**
     * DOCUMENT ME!
     */
    protected JTabbedPane prefTabbedPane;

    /**
     * Constructor.
     */
    public PreferencesView()
    {
        super("SCALIB Preferences");

        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        // Build main GUI components
        prefTabbedPane = new JTabbedPane();

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(prefTabbedPane);

        // Append properties component
        OrderedWordsPreferencesView propertiesView = new OrderedWordsPreferencesView(_preferences,
                "star.properties.order");
        prefTabbedPane.add("Star Properties", propertiesView);

        // Append preference colors chooser component of catalogs
        ColorPreferencesView colorView = new ColorPreferencesView(_preferences,
                "catalog.color.");
        prefTabbedPane.add("Catalog Origin", colorView);

        // Append preference colors chooser component of catalogs
        ColorPreferencesView confidenceView = new ColorPreferencesView(_preferences,
                "confidence.color.");
        prefTabbedPane.add("Confidence index", confidenceView);

        // Append proxy component
        ProxyPreferencesView proxyView = new ProxyPreferencesView(_preferences);
        prefTabbedPane.add("Proxy", proxyView);

        // Append help component
        HelpSetupPreferencesView helpView = new HelpSetupPreferencesView(_preferences);
        prefTabbedPane.add("Help", helpView);

        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore to default");
        _restoreDefaultButton.addActionListener(this);
        buttonsPanel.add(_restoreDefaultButton);
        _saveChangesButton = new JButton("Save changes");
        _saveChangesButton.addActionListener(this);
        buttonsPanel.add(_saveChangesButton);
        contentPane.add(buttonsPanel);

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
        // TODO : MAJ des champs de la fenetre
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
 * This Panel is dedicated to manage proxy configuration.
 *
 */
class ProxyPreferencesView extends JPanel implements Observer
{
    /**
     * DOCUMENT ME!
     */
    private Preferences _preferences;

    /**
     * Constructor.
     * @param preferences the application preferences
     */
    public ProxyPreferencesView(Preferences preferences)
    {
        _preferences = preferences;
        _preferences.addObserver(this);

        this.add(new JLabel("TODO"));

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
 * This Panel is dedicated to manage one ordered list of words.
 *
 */
class OrderedWordsPreferencesView extends JPanel implements Observer,
    ActionListener
{
    /**
     * DOCUMENT ME!
     */
    Logger _logger = MCSLogger.getLogger();

    /**
     * The main preferences object.
     */
    private Preferences _preferences;

    /**
     * The name of preference that must be managed as a ordered words.
     */
    private String _preferenceName;

    /**
     * The widget that dispaly the list of words
     */
    private JList _wordsList;

    /**
     * The model associated to the widget that dispaly the list of words
     */
    private DefaultListModel _listModel;

    /**
     * The actual shown list of words
     */
    private String _actualWords = "";

    /**
     * Move up Button
     */
    private JButton _moveUpButton;

    /**
     * Move down Button
     */
    private JButton _moveDownButton;

    /**
     * Add Button
     */
    private JButton _addWordButton;

    /**
     * Remove  Button
     */
    private JButton _removeWordButton;

    /**
     * newWord textfield
     */
    private JTextField _newWordTextfield;

    /**
     * Constructor.
     * @param preferences the application preferences
     * @param preferenceName the preference name that list the words separated
     * by spaces
     */
    public OrderedWordsPreferencesView(Preferences preferences,
        String preferenceName)
    {
        _preferences = preferences;
        _preferences.addObserver(this);

        _preferenceName = preferenceName;

        //Set vertical layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Append list with associated model into a scrollPane
        _listModel     = new DefaultListModel();
        _wordsList     = new JList(_listModel);

        JScrollPane scrollingList = new JScrollPane(_wordsList);
        this.add(scrollingList);

        // Append move buttons
        JPanel panel = new JPanel();
        _moveUpButton = new JButton("Up");
        _moveUpButton.addActionListener(this);
        panel.add(_moveUpButton);
        _moveDownButton = new JButton("Down");
        _moveDownButton.addActionListener(this);
        panel.add(_moveDownButton);
        this.add(panel);

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
            _logger.warning(_preferenceName + " not found into preferences");

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
            Enumeration  e  = _listModel.elements();

            while (e.hasMoreElements())
            {
                Object o = e.nextElement();
                sb.append(" " + o);
            }

            _preferences.setPreference(_preferenceName, sb.toString());
        }
    }
}


/**
 * This Panel is dedicated to manage help behaviour configuration.
 *
 */
class HelpSetupPreferencesView extends JPanel implements Observer,
    ChangeListener
{
    /**
     * DOCUMENT ME!
     */
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
    public HelpSetupPreferencesView(Preferences preferences)
    {
        _preferences = preferences;
        _preferences.addObserver(this);

        // get instance of shared tooltip to adjust behaviour in update code
        _sharedToolTipManager      = ToolTipManager.sharedInstance();

        _enableToolTipCheckBox     = new JCheckBox("Show tooltips");

        String ttt                 = Resources.getToolTipText(
                "_enableToolTipCheckBox");
        _enableToolTipCheckBox.setToolTipText(ttt);
        _sharedToolTipManager.registerComponent(_enableToolTipCheckBox);
        _enableToolTipCheckBox.addChangeListener(this);

        this.add(_enableToolTipCheckBox);

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
        boolean enableTT = _preferences.getPreferenceAsBoolean(
                "help.tooltips.show");
        _enableToolTipCheckBox.setSelected(enableTT);
        _sharedToolTipManager.setEnabled(enableTT);
    }

    /**
     * Update preferences according buttons change
     */
    public void stateChanged(ChangeEvent e)
    {
        if (e.getSource().equals(_enableToolTipCheckBox))
        {
            _preferences.setPreference("help.tooltips.show",
                _enableToolTipCheckBox.isSelected());
        }
    }
}


/**
 *  This panel is dedicated to manage color preferences.
 */
class ColorPreferencesView extends JPanel implements Observer
{
    /**
     * DOCUMENT ME!
     */
    protected Preferences _preferences;

    /**
     * DOCUMENT ME!
     */
    protected String _preferencePrefix;

    /**
     * DOCUMENT ME!
     */
    private Object[][] data;

    /**
     * DOCUMENT ME!
     */
    JTable table;

    /**
     * Creates a new ColorPreferencesView object.
     *
     * @param preferences DOCUMENT ME!
     * @param prefix DOCUMENT ME!
     */
    public ColorPreferencesView(Preferences preferences, String prefix)
    {
        _preferencePrefix     = prefix;
        _preferences          = preferences;
        _preferences.addObserver(this);

        // Make data filled
        update(null, null);

        TableModel tableModel = new MyTableModel();
        table = new JTable(tableModel);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        //Set up renderer and editor for the Favorite Color column.
        table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        table.setDefaultEditor(Color.class, new ColorEditor());
        //Add the scroll pane to this panel.
        add(scrollPane);
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
        Enumeration e      = _preferences.getPreferences(_preferencePrefix);
        Vector      names  = new Vector();
        Vector      colors = new Vector();

        while (e.hasMoreElements())
        {
            String entry          = (String) e.nextElement();

            String preferenceName = entry.substring(_preferencePrefix.length());
            names.add(preferenceName);

            Color c;

            try
            {
                c = _preferences.getPreferenceAsColor(entry);
            }
            catch (Exception ex)
            {
                c = Color.white;
            }

            colors.add(c);
        }

        data = new Object[names.size()][2];

        for (int i = 0; i < names.size(); i++)
        {
            data[i][0]     = names.elementAt(i);
            data[i][1]     = colors.elementAt(i);
        }

        // repaint table 
        if (table != null)
        {
            table.repaint();
        }
    }

    class MyTableModel extends AbstractTableModel
    {
        private String[] columnNames = { "Property", "Favorite Color" };

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public int getRowCount()
        {
            return data.length;
        }

        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col)
        {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        public void setValueAt(Object value, int row, int col)
        {
            data[row][col] = value;
            fireTableCellUpdated(row, col);

            String preferenceName = _preferencePrefix + data[row][0];
            Color  newColor       = (Color) data[row][1];
            _preferences.setPreference(preferenceName,
                jmmc.mcs.util.ColorEncoder.encode(newColor));
        }
    }

    class ColorRenderer extends JLabel implements TableCellRenderer
    {
        Border  unselectedBorder = null;
        Border  selectedBorder   = null;
        boolean isBordered       = true;

        public ColorRenderer(boolean isBordered)
        {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(JTable table,
            Object color, boolean isSelected, boolean hasFocus, int row,
            int column)
        {
            Color newColor = (Color) color;
            setBackground(newColor);

            if (isBordered)
            {
                if (isSelected)
                {
                    if (selectedBorder == null)
                    {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5,
                                2, 5, table.getSelectionBackground());
                    }

                    setBorder(selectedBorder);
                }
                else
                {
                    if (unselectedBorder == null)
                    {
                        unselectedBorder = BorderFactory.createMatteBorder(2,
                                5, 2, 5, table.getBackground());
                    }

                    setBorder(unselectedBorder);
                }
            }

            setToolTipText("RGB value: " + newColor.getRed() + ", " +
                newColor.getGreen() + ", " + newColor.getBlue());

            return this;
        }
    }

    class ColorEditor extends AbstractCellEditor implements TableCellEditor,
        ActionListener
    {
        protected static final String EDIT         = "edit";
        Color                         currentColor;
        JButton                       button;
        JColorChooser                 colorChooser;
        JDialog                       dialog;

        public ColorEditor()
        {
            //Set up the editor (from the table's point of view),
            //which is a button.
            //This button brings up the color chooser dialog,
            //which is the editor from the user's point of view.
            button                                 = new JButton();
            button.setActionCommand(EDIT);
            button.addActionListener(this);
            button.setBorderPainted(false);

            //Set up the dialog that the button brings up.
            colorChooser     = new JColorChooser();
            dialog           = JColorChooser.createDialog(button,
                    "Pick a Color", true, //modal
                    colorChooser, this, //OK button handler
                    null); //no CANCEL button handler
        }

        /**
         * Handles events from the editor button and from
         * the dialog's OK button.
         */
        public void actionPerformed(ActionEvent e)
        {
            if (EDIT.equals(e.getActionCommand()))
            {
                //The user has clicked the cell, so
                //bring up the dialog.
                button.setBackground(currentColor);
                colorChooser.setColor(currentColor);
                dialog.setVisible(true);

                //Make the renderer reappear.
                fireEditingStopped();
            }
            else
            { //User pressed dialog's "OK" button.
                currentColor = colorChooser.getColor();
            }
        }

        //Implement the one CellEditor method that AbstractCellEditor doesn't.
        public Object getCellEditorValue()
        {
            return currentColor;
        }

        //Implement the one method defined by TableCellEditor.
        public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column)
        {
            currentColor = (Color) value;

            return button;
        }
    }
}
