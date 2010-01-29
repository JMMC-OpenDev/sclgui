/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LegendView.java,v 1.13 2010-01-29 13:12:25 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2009/12/01 11:29:32  lafrasse
 * Added easily readable catalog names in cell tooltips, legend view and legend
 * preference pane.
 *
 * Revision 1.11  2008/09/10 22:51:29  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Added clickable cell to open catalog web pages.
 *
 * Revision 1.10  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.9  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.8  2006/10/03 15:25:20  lafrasse
 * Use of the Preference shared instance instead of receiving it through the
 * constructor.
 *
 * Revision 1.7  2006/09/28 15:23:29  lafrasse
 * Updated to handle jmmc.util.Preferences API modifications.
 *
 * Revision 1.6  2006/07/03 12:37:04  mella
 * Add header to legend sections
 *
 * Revision 1.5  2006/06/30 11:53:17  mella
 * Change GUI presentation
 *
 * Revision 1.4  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.3  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.2  2006/03/30 13:41:28  yvander
 * Mise en place des couleurs
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.astro.Catalog;
import fr.jmmc.mcs.gui.*;
import fr.jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.*;
import javax.swing.table.TableCellRenderer;


/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class LegendView extends JPanel
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.LegendView");

    /**
     * Application preferences
     */
    Preferences _preferences;

    /**
     * Constructor.
     *
     * @param isEditable if true the catalog colors will be editable, otherwise the link to the catalog will be opened instead of editing.
     */
    public LegendView(boolean isEditable)
    {
        // Store the application preferences and register against it
        _preferences = Preferences.getInstance();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Append preference colors chooser component of catalogs
        ColorPreferencesView colorView = new ColorPreferencesView(_preferences,
                "catalog", "Catalogs Origin", isEditable);

        // Append preference colors chooser component of catalogs
        ColorPreferencesView confidenceView = new ColorPreferencesView(_preferences,
                "confidence", "Confidence Index", true);

        // Create the scrollpanes and add the tables.
        JScrollPane scrollPane = new JScrollPane(colorView);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // If you prefer to be sure all text is readable uncomment next line
        // scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane2 = new JScrollPane(confidenceView);
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JSplitPane vPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scrollPane, scrollPane2);
        vPane.setContinuousLayout(true);
        vPane.setOneTouchExpandable(true);
        vPane.setResizeWeight(0.75);
        add(vPane);
    }

    /**
     *  This panel is dedicated to manage color preferences.
     */
    class ColorPreferencesView extends JPanel implements Observer
    {
        private final static int COLOR       = 0;
        private final static int REFERENCE   = 1;
        private final static int TITLE       = 2;
        private final static int DESCRIPTION = 3;

        /** Application preferences */
        protected Preferences _preferences;

        /** Preference prefix for Color values */
        protected String _colorPreferencePrefix;

        /** data of Table model */
        private Object[][] _data;

        /** Displayed table */
        JTable _table;

        /**
         * Creates a new ColorPreferencesView object.
         *
         * @param preferences application preferences
         * @param prefix properties prefix
         * @param header table caolumn header value
         * @param isEditable if true, the catalog colors will be editable, otherwise the link to the catalog will be open instead of editing
         */
        public ColorPreferencesView(Preferences preferences, String prefix,
            String header, boolean isEditable)
        {
            // Register to catalog preferences
            _colorPreferencePrefix     = prefix + ".color.";
            _preferences               = preferences;
            _preferences.addObserver(this);

            // Make data filled
            update(null, null);

            TableModel tableModel = new ColorPreferencesViewTableModel();

            _table = new JTable(tableModel);
            _table.getColumnModel().getColumn(0).setHeaderValue(header);

            // Set up renderer and editor for the Favorite Color column.
            _table.setDefaultRenderer(Color.class,
                new ColorRenderer(false, isEditable));
            _table.setDefaultEditor(Color.class, new ColorEditor(isEditable));
            _table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            setLayout(new BorderLayout());

            // Add the header and the table 
            JTableHeader tableHeader = _table.getTableHeader();
            add(tableHeader, BorderLayout.NORTH);
            add(_table, BorderLayout.CENTER);

            // Make table resized
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
            _logger.entering("ColorPreferencesView", "update");

            // Fill with preferences entries
            Enumeration e            = _preferences.getPreferences(_colorPreferencePrefix);
            Vector      colors       = new Vector();
            Vector      references   = new Vector();
            Vector      names        = new Vector();
            Vector      descriptions = new Vector();

            while (e.hasMoreElements())
            {
                String entry = (String) e.nextElement();

                // Use dedicated pref. name if available, pref. reference otherwise
                String preferenceReference = entry.substring(_colorPreferencePrefix.length());
                references.add(preferenceReference);

                String title = Catalog.titleFromReference(preferenceReference);
                names.add(title);

                String description = Catalog.descriptionFromReference(preferenceReference);
                descriptions.add(description);

                // Get color from preferences, white otherwise
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

            // Synthetise tadle data model
            int rowCount = names.size();
            _data = new Object[rowCount][4];

            for (int row = 0; row < rowCount; row++)
            {
                _data[row][COLOR]           = colors.elementAt(row);
                _data[row][REFERENCE]       = references.elementAt(row);
                _data[row][TITLE]           = names.elementAt(row);
                _data[row][DESCRIPTION]     = descriptions.elementAt(row);
            }

            // Repaint table 
            if (_table != null)
            {
                _table.repaint();
                /* set size according bigest component
                   TableColumn tableColumn = _table.getColumnModel().getColumn(0);
                   TableCellRenderer renderer = _table.getTableCellRenderer();
                   int max=0;
                   for (int i=0; i < _table.getRowCount()){
                   }
                 */
                initColumnSizes(_table);
                revalidate();
            }
        }

        /*
         * This method picks good column size.
         */
        private void initColumnSizes(JTable table)
        {
            _logger.entering("ColorPreferencesView", "initColumnSizes");

            ColorPreferencesViewTableModel model     = (ColorPreferencesViewTableModel) table.getModel();
            TableColumn                    column    = null;
            Component                      comp      = null;
            int                            cellWidth = 0;
            column                                   = table.getColumnModel()
                                                            .getColumn(0);

            for (int row = 0; row < table.getRowCount(); row++)
            {
                comp = table.getDefaultRenderer(model.getColumnClass(0))
                            .getTableCellRendererComponent(table,
                        _data[row][COLOR], false, false, row, 0);

                if (cellWidth < comp.getPreferredSize().width)
                {
                    cellWidth = comp.getPreferredSize().width;
                }
            }

            column.setPreferredWidth(cellWidth + 4);
        }

        class ColorPreferencesViewTableModel extends AbstractTableModel
        {
            private String[] columnNames = { "Favorite Color" };

            public int getColumnCount()
            {
                _logger.entering("ColorPreferencesViewTableModel",
                    "getColumnCount");

                return columnNames.length;
            }

            public int getRowCount()
            {
                _logger.entering("ColorPreferencesViewTableModel", "getRowCount");

                return _data.length;
            }

            public String getColumnName(int col)
            {
                _logger.entering("ColorPreferencesViewTableModel",
                    "getColumnName");

                return columnNames[col];
            }

            public Object getValueAt(int row, int col)
            {
                _logger.entering("ColorPreferencesViewTableModel", "getValueAt");

                return _data[row][col];
            }

            /*
             * JTable uses this method to determine the default renderer/
             * editor for each cell.  If we didn't implement this method,
             * then the last column would contain text ("true"/"false"),
             * rather than a check box.
             */
            public Class getColumnClass(int c)
            {
                _logger.entering("ColorPreferencesViewTableModel",
                    "getColumnClass");

                return getValueAt(0, c).getClass();
            }

            public boolean isCellEditable(int row, int col)
            {
                _logger.entering("ColorPreferencesViewTableModel",
                    "isCellEditable");

                // Note that the data/cell address is constant, no matter where the cell appears onscreen.
                return true;
            }

            public void setValueAt(Object value, int row, int col)
            {
                _logger.entering("ColorPreferencesViewTableModel", "setValueAt");

                _data[row][col] = value;
                fireTableCellUpdated(row, col);

                String preferenceName = _colorPreferencePrefix +
                    _data[row][REFERENCE];
                Color  newColor       = (Color) _data[row][COLOR];

                try
                {
                    _preferences.setPreference(preferenceName, newColor);
                }
                catch (Exception ex)
                {
                    _logger.log(Level.SEVERE,
                        "Could not set preference '" + preferenceName + "'.", ex);
                }
            }
        }

        class ColorRenderer extends JLabel implements TableCellRenderer
        {
            Border  unselectedBorder = null;
            Border  selectedBorder   = null;
            boolean isBordered       = true;
            boolean _isEditable      = true;

            public ColorRenderer(boolean isBordered, boolean isEditable)
            {
                this.isBordered     = isBordered;
                _isEditable         = isEditable;
                setOpaque(true); // MUST do this for background to show up.
            }

            public Component getTableCellRendererComponent(JTable table,
                Object color, boolean isSelected, boolean hasFocus, int row,
                int column)
            {
                _logger.entering("ColorRenderer",
                    "getTableCellRendererComponent");

                String cellReference      = (String) _data[row][REFERENCE];
                String cellName           = (String) _data[row][TITLE];
                String cellTitle          = (String) _data[row][DESCRIPTION];
                String catalogDescription = "'" + cellTitle + "' (" +
                    cellReference + ")";

                Color  newColor           = (Color) color;
                setBackground(newColor);

                if (_isEditable == false)
                {
                    setText("<html>&nbsp;<a href='#empty'>" + cellName +
                        "</a></html>");
                    setToolTipText("Click to open CDS page for catalog " +
                        catalogDescription);
                }
                else
                {
                    setText(cellName);
                    setToolTipText("Click to edit the color");
                }

                if (isBordered)
                {
                    if (isSelected)
                    {
                        if (selectedBorder == null)
                        {
                            selectedBorder = BorderFactory.createMatteBorder(1,
                                    3, 1, 3, table.getSelectionBackground());
                        }

                        setBorder(selectedBorder);
                    }
                    else
                    {
                        if (unselectedBorder == null)
                        {
                            unselectedBorder = BorderFactory.createMatteBorder(1,
                                    3, 1, 3, table.getBackground());
                        }

                        setBorder(unselectedBorder);
                    }
                }

                _logger.fine("RGB color for cell '" + cellName + "' = [R" +
                    newColor.getRed() + ", G" + newColor.getGreen() + ", B" +
                    newColor.getBlue() + "].");

                return this;
            }
        }

        class ColorEditor extends AbstractCellEditor implements TableCellEditor,
            ActionListener
        {
            protected static final String EDIT              = "edit";
            Color                         _currentColor;
            JButton                       _button;
            JColorChooser                 _colorChooser;
            JDialog                       _dialog;
            boolean                       _isEditable       = true;
            String                        _catalogReference;

            /**
             * Creates a new ColorEditor object.
             *
             * @param isEditable if true, the catalog colors will be editable, otherwise the link to the catalog will be open instead of editing
             */
            public ColorEditor(boolean isEditable)
            {
                _isEditable                                 = isEditable;

                // Set up the editor (from the table's point of view), which is a button.
                // This button brings up the color chooser dialog, which is the editor from the user's point of view.
                _button                                     = new JButton();
                _button.setActionCommand(EDIT);
                _button.addActionListener(this);
                _button.setBorderPainted(false);

                //Set up the dialog that the button brings up.
                _colorChooser     = new JColorChooser();
                _dialog           = JColorChooser.createDialog(_button,
                        "Pick a Color", true, // Modal Window
                        _colorChooser, this, // OK button handler
                        null); // No CANCEL button handler
            }

            /**
             * Handles events from the editor button and from
             * the dialog's OK button.
             */
            public void actionPerformed(ActionEvent e)
            {
                _logger.entering("ColorEditor", "actionPerformed");

                if (EDIT.equals(e.getActionCommand()))
                {
                    if (_isEditable == true)
                    {
                        // The user has clicked the cell, so bring up the dialog.
                        _button.setBackground(_currentColor);
                        _colorChooser.setColor(_currentColor);
                        _dialog.setVisible(true);
                    }
                    else
                    {
                        String url = Resources.getResource("catalog.url." +
                                _catalogReference);

                        if (url == null)
                        {
                            url = Resources.getResource("catalog.url.header") +
                                _catalogReference;
                        }

                        _logger.fine("User clicked on catalog '" +
                            _catalogReference +
                            "' in the LegendView, will open '" + url +
                            "' in default browser.");
                        BrowserLauncher.openURL(url);
                    }

                    // Make the renderer reappear.
                    fireEditingStopped();
                }
                else
                {
                    // User pressed dialog's "OK" button.
                    _currentColor = _colorChooser.getColor();
                }
            }

            // Implement the one CellEditor method that AbstractCellEditor doesn't.
            public Object getCellEditorValue()
            {
                _logger.entering("ColorEditor", "getCellEditorValue");

                return _currentColor;
            }

            // Implement the one method defined by TableCellEditor.
            public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column)
            {
                _logger.entering("ColorEditor", "getTableCellEditorComponent");

                _currentColor         = (Color) value;
                _catalogReference     = (String) _data[row][REFERENCE];

                return _button;
            }
        }
    }
}
