/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.util.ResourceUtils;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public final class LegendView extends JPanel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(LegendView.class.getName());
    /**
     * Application preferences
     */
    private Preferences _preferences = null;

    /**
     * Constructor.
     *
     * @param isEditable if true the catalog colors will be editable, otherwise the link to the catalog will be opened instead of editing.
     */
    public LegendView(boolean isEditable) {
        // Store the application preferences and register against it
        _preferences = Preferences.getInstance();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Append preference colors chooser component of catalogs
        ColorPreferencesView colorView = new ColorPreferencesView(_preferences,
                "catalog", "Catalogs Origin", isEditable);
        colorView.init();

        // Append preference colors chooser component of catalogs
        ColorPreferencesView confidenceView = new ColorPreferencesView(_preferences,
                "confidence", "Confidence Index", true);
        confidenceView.init();

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
    class ColorPreferencesView extends JPanel implements Observer {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private final static int COLOR = 0;
        private final static int REFERENCE = 1;
        private final static int TITLE = 2;
        private final static int DESCRIPTION = 3;
        /** Application preferences */
        protected Preferences _preferences = null;
        /** Preference prefix for Color values */
        protected String _colorPreferencePrefix = null;
        /** data of Table model */
        private Object[][] _data = null;
        /** Displayed table */
        private JTable _table = null;

        /**
         * Creates a new ColorPreferencesView object.
         *
         * @param preferences application preferences
         * @param prefix properties prefix
         * @param header table caolumn header value
         * @param isEditable if true, the catalog colors will be editable, otherwise the link to the catalog will be open instead of editing
         */
        ColorPreferencesView(Preferences preferences, String prefix,
                             String header, boolean isEditable) {
            // Register to catalog preferences
            _colorPreferencePrefix = prefix + ".color.";
            _preferences = preferences;

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
        }

        public void init() {
            _preferences.addObserver(this);
            // Make table resized
            update(null, null);
        }

        /**
         * Present fresh content according preference content.
         *
         * @param o preferences
         * @param arg not used
         */
        @Override
        public void update(Observable o, Object arg) {
            // Fill with preferences entries
            final Vector<Color> colors = new Vector<Color>();
            final Vector<String> references = new Vector<String>();
            final Vector<String> names = new Vector<String>();
            final Vector<String> descriptions = new Vector<String>();

            final Enumeration<String> e = _preferences.getPreferences(_colorPreferencePrefix);

            while (e.hasMoreElements()) {
                String entry = e.nextElement();

                // Use dedicated pref. name if available, pref. reference otherwise
                String preferenceReference = entry.substring(_colorPreferencePrefix.length());
                String title = Catalog.titleFromReference(preferenceReference);
                String description = Catalog.descriptionFromReference(preferenceReference);

                // Get color from preferences, white otherwise
                Color c;
                try {
                    c = _preferences.getPreferenceAsColor(entry);
                } catch (Exception pe) {
                    c = Color.white;
                }

                references.add(preferenceReference);
                names.add(title);
                descriptions.add(description);
                colors.add(c);
            }

            // Synthetise tadle data model
            int rowCount = names.size();
            _data = new Object[rowCount][4];

            for (int row = 0; row < rowCount; row++) {
                _data[row][COLOR] = colors.elementAt(row);
                _data[row][REFERENCE] = references.elementAt(row);
                _data[row][TITLE] = names.elementAt(row);
                _data[row][DESCRIPTION] = descriptions.elementAt(row);
            }

            // Repaint table 
            if (_table != null) {
                _table.repaint();

                initColumnSizes(_table);
                revalidate();
            }
        }

        /**
         * This method picks good column size.
         * @param table 
         */
        private void initColumnSizes(JTable table) {
            ColorPreferencesViewTableModel model = (ColorPreferencesViewTableModel) table.getModel();
            TableColumn column = null;
            Component comp = null;
            int cellWidth = 0;
            column = table.getColumnModel().getColumn(0);

            for (int row = 0, size = table.getRowCount(); row < size; row++) {
                comp = table.getDefaultRenderer(model.getColumnClass(0)).getTableCellRendererComponent(table,
                        _data[row][COLOR], false, false, row, 0);

                if (cellWidth < comp.getPreferredSize().width) {
                    cellWidth = comp.getPreferredSize().width;
                }
            }

            column.setPreferredWidth(cellWidth + 4);
        }

        class ColorPreferencesViewTableModel extends AbstractTableModel {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;
            private String[] columnNames = {"Favorite Color"};

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public int getRowCount() {
                return _data.length;
            }

            @Override
            public String getColumnName(int col) {
                return columnNames[col];
            }

            @Override
            public Object getValueAt(int row, int col) {
                return _data[row][col];
            }

            /*
             * JTable uses this method to determine the default renderer/
             * editor for each cell.  If we didn't implement this method,
             * then the last column would contain text ("true"/"false"),
             * rather than a check box.
             */
            @Override
            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                // Note that the data/cell address is constant, no matter where the cell appears onscreen.
                return true;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {
                _data[row][col] = value;
                fireTableCellUpdated(row, col);

                String preferenceName = _colorPreferencePrefix + _data[row][REFERENCE];
                Color newColor = (Color) _data[row][COLOR];

                try {
                    _preferences.setPreference(preferenceName, newColor);
                } catch (PreferencesException pe) {
                    _logger.error("Could not set preference '{}'.", preferenceName, pe);
                }
            }
        }

        class ColorRenderer extends JLabel implements TableCellRenderer {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;
            private Border unselectedBorder = null;
            private Border selectedBorder = null;
            private boolean isBordered = true;
            private boolean _isEditable = true;

            ColorRenderer(boolean isBordered, boolean isEditable) {
                this.isBordered = isBordered;
                _isEditable = isEditable;
                setOpaque(true); // MUST do this for background to show up.
            }

            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object color, boolean isSelected, boolean hasFocus, int row,
                                                           int column) {

                String cellReference = (String) _data[row][REFERENCE];
                String cellName = (String) _data[row][TITLE];
                String cellTitle = (String) _data[row][DESCRIPTION];
                String catalogDescription = "'" + cellTitle + "' ("
                        + cellReference + ")";

                Color newColor = (Color) color;
                setBackground(newColor);

                if (!_isEditable) {
                    setText("<html>&nbsp;<a href='#empty'>" + cellName
                            + "</a></html>");
                    setToolTipText("Click to open CDS page for catalog "
                            + catalogDescription);
                } else {
                    setText(cellName);
                    setToolTipText("Click to edit the color");
                }

                if (isBordered) {
                    if (isSelected) {
                        if (selectedBorder == null) {
                            selectedBorder = BorderFactory.createMatteBorder(1,
                                    3, 1, 3, table.getSelectionBackground());
                        }

                        setBorder(selectedBorder);
                    } else {
                        if (unselectedBorder == null) {
                            unselectedBorder = BorderFactory.createMatteBorder(1,
                                    3, 1, 3, table.getBackground());
                        }

                        setBorder(unselectedBorder);
                    }
                }

                if (_logger.isDebugEnabled()) {
                    _logger.debug("RGB color for cell '{}' = [R{}, G{}, B{}].", cellName, newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                }

                return this;
            }
        }

        class ColorEditor extends AbstractCellEditor implements TableCellEditor,
                                                                ActionListener {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;
            protected static final String EDIT = "edit";
            private Color _currentColor;
            private JButton _button;
            private JColorChooser _colorChooser;
            private JDialog _dialog;
            boolean _isEditable = true;
            private String _catalogReference;

            /**
             * Creates a new ColorEditor object.
             *
             * @param isEditable if true, the catalog colors will be editable, otherwise the link to the catalog will be open instead of editing
             */
            ColorEditor(boolean isEditable) {
                _isEditable = isEditable;

                // Set up the editor (from the table's point of view), which is a button.
                // This button brings up the color chooser dialog, which is the editor from the user's point of view.
                _button = new JButton();
                _button.setActionCommand(EDIT);
                _button.addActionListener(this);
                _button.setBorderPainted(false);

                //Set up the dialog that the button brings up.
                _colorChooser = new JColorChooser();
                _dialog = JColorChooser.createDialog(_button,
                        "Pick a Color", true, // Modal Window
                        _colorChooser, this, // OK button handler
                        null); // No CANCEL button handler
            }

            /**
             * Handles events from the editor button and from
             * the dialog's OK button.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (EDIT.equals(e.getActionCommand())) {
                    if (_isEditable) {
                        // The user has clicked the cell, so bring up the dialog.
                        _button.setBackground(_currentColor);
                        _colorChooser.setColor(_currentColor);
                        _dialog.setVisible(true);
                    } else {
                        String url = ResourceUtils.getResource("catalog.url." + _catalogReference, true);

                        if (url == null) {
                            url = ResourceUtils.getResource("catalog.url.header")
                                    + _catalogReference;
                        }

                        if (_logger.isDebugEnabled()) {
                            _logger.debug("User clicked on catalog '{}' in the LegendView, will open '{}' in default browser.", _catalogReference, url);
                        }
                        BrowserLauncher.openURL(url);
                    }

                    // Make the renderer reappear.
                    fireEditingStopped();
                } else {
                    // User pressed dialog's "OK" button.
                    _currentColor = _colorChooser.getColor();
                }
            }

            // Implement the one CellEditor method that AbstractCellEditor doesn't.
            @Override
            public Object getCellEditorValue() {
                return _currentColor;
            }

            // Implement the one method defined by TableCellEditor.
            @Override
            public Component getTableCellEditorComponent(JTable table,
                                                         Object value, boolean isSelected, int row, int column) {

                _currentColor = (Color) value;
                _catalogReference = (String) _data[row][REFERENCE];

                return _button;
            }
        }
    }
}
