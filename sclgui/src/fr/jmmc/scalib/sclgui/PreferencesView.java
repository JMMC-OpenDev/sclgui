/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.7 2006-04-12 12:30:02 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.awt.*;
import java.awt.event.*;

import java.util.*;

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

        // Append preference colors chooser component of catalogs
        ColorPreferencesView colorView = new ColorPreferencesView(_preferences,
                "catalog.color.");
        prefTabbedPane.add("Catalog Origin", colorView);

        // Append preference colors chooser component of catalogs
        ColorPreferencesView confidenceView = new ColorPreferencesView(_preferences,
                "confidence.color.");
        prefTabbedPane.add("Confidence index", confidenceView);

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
        // TODO : MˆJ des champs de la fenetre
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
 * @version $Revision: 1.7 $
 */
class SpecificPreferencesView extends JPanel
{
    /**
     * DOCUMENT ME!
     */
    Preferences _preferences;

    /**
     * Constructor.
     * @param preferences the application preferences
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
 * @version $Revision: 1.7 $
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
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
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
