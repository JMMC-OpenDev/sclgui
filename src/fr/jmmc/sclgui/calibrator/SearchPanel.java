/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.gui.MainMenuBar;
import fr.jmmc.jmcs.gui.WindowCenterer;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Search Panel
 * @author Sylvain LAFRASSE
 *
 * TODO : handle ctrl-W or Escape to close the window.
 * TODO : get search token from dedicated pasteboard (Mac!, Windows?, Linux...).
 */
public class SearchPanel extends JFrame {

    private static final Logger _logger = Logger.getLogger(SearchPanel.class.getName());
    private JPanel _panel;
    private JLabel _findLabel;
    private JTextField _searchField;
    private JCheckBox _regexpCheckBox;
    private JButton _nextButton;
    private JButton _previousButton;
    private QuickSearchHelper _searchHelper;
    /** Find action */
    public FindAction _findAction;
    /** Find Next action */
    public FindNextAction _findNextAction;
    /** Find Previous action */
    public FindPreviousAction _findPreviousAction;
    /** The calibrators table */
    private JTable _calibratorsTable;
    /** The calibrator table sorter */
    private TableSorter _tableSorter;

    public SearchPanel(TableSorter tableSorter, JTable calibratorsTable) {
        super("Find");

        _calibratorsTable = calibratorsTable;
        _tableSorter = tableSorter;

        _searchHelper = new QuickSearchHelper();

        setupActions();
        createWidgets();
        layoutWidgets();
        monitorWidgets();
        prepareFrame();
    }

    private void setupActions() {
        String classPath = getClass().getName();
        _findAction = new FindAction(classPath, "_findAction");
        _findNextAction = new FindNextAction(classPath, "_findNextAction");
        _findPreviousAction = new FindPreviousAction(classPath, "_findPreviousAction");
    }

    private void createWidgets() {
        _panel = new JPanel();

        _findLabel = new JLabel("Find:");
        _panel.add(_findLabel);

        _searchField = new JTextField();
        _panel.add(_searchField);

        _regexpCheckBox = new JCheckBox("Use Regular Expression");
        _panel.add(_regexpCheckBox);

        _previousButton = new JButton(_findPreviousAction);
        _previousButton.setText("Previous");
        _panel.add(_previousButton);

        _nextButton = new JButton(_findNextAction);
        _nextButton.setText("Next");
        getRootPane().setDefaultButton(_nextButton);
        _panel.add(_nextButton);
    }

    private void layoutWidgets() {
        GroupLayout layout = new GroupLayout(_panel);
        _panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup().addComponent(_findLabel).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(_searchField).addComponent(_regexpCheckBox)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(_previousButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(_nextButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        layout.setVerticalGroup(
                layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(_findLabel).addComponent(_searchField).addComponent(_previousButton)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(_regexpCheckBox).addComponent(_nextButton)));
    }

    private void monitorWidgets() {
        _searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doSearch(SEARCH_DIRECTION.UNDEFINED);
            }
        });
    }

    private void prepareFrame() {
        getContentPane().add(_panel);
        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        WindowCenterer.centerOnMainScreen(this);
    }

    public void enableMenus(boolean flag) {
        _findAction.setEnabled(flag);
        _findNextAction.setEnabled(flag);
        _findPreviousAction.setEnabled(flag);
    }

    private void doSearch(SEARCH_DIRECTION direction) {
        final String text = _searchField.getText().trim();

        final boolean isRegExp = _regexpCheckBox.isSelected();
        if (text.length() > 0) {
            if (!_searchHelper.search(text, isRegExp, direction)) {
                _searchField.setBackground(Color.red);
            } else {
                _searchField.setBackground(Color.WHITE);
            }
        }
    }

    protected class FindAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        FindAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false); // Will be (dis)enabled dynamically by CalibratorView::tableChanged()
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FindAction", "actionPerformed");
            setVisible(true);
        }
    }

    protected class FindNextAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        FindNextAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false); // Will be (dis)enabled dynamically by CalibratorView::tableChanged()
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FindNextAction", "actionPerformed");
            doSearch(SEARCH_DIRECTION.NEXT);
        }
    }

    protected class FindPreviousAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        FindPreviousAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false); // Will be (dis)enabled dynamically by CalibratorView::tableChanged()
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FindPreviousAction", "actionPerformed");
            doSearch(SEARCH_DIRECTION.PREVIOUS);
        }
    }

    /** Quick search direction */
    private static enum SEARCH_DIRECTION {

        /** previous */
        PREVIOUS,
        /** next */
        NEXT,
        /** reset */
        UNDEFINED
    };
    /* regexp tokens */
    /** tokens to replace from "([{\^-=$!|]})?*+." except "*?" */
    private final static String[] REGEXP_TOKEN_FROM = new String[]{
        "(", "[", "{", "\\", "^", "-", "=", "$", "!", "|", "]", "}", ")", "+", ".", "*", "?"
    };
    /** tokens to replace by */
    private final static String[] REGEXP_TOKEN_REPLACE = new String[]{
        "\\(", "\\[", "\\{", "\\\\", "\\^", "\\-", "\\=", "\\$", "\\!", "\\|", "\\]", "\\}", "\\)", "\\+", "\\.", ".*", ".?"
    };

    /**
     * Quick search algorithm supporting previous / next ...
     */
    private final class QuickSearchHelper {

        /** undefined */
        private final static int UNDEFINED = -1;
        /* members */
        /** current found row index related to table view (visible rows) */
        private int _currentRow;
        /** current found column index related to table view (visible columns)*/
        private int _currentCol;
        /** current search value */
        private String _searchValue;

        /**
         * Protected constructor
         */
        protected QuickSearchHelper() {
            reset();
        }

        /**
         * Reset current state
         */
        private void reset() {
            _currentRow = UNDEFINED;
            _currentCol = UNDEFINED;
            _searchValue = null;
        }

        protected boolean search(final String searchValue, final boolean isRegExp, final SEARCH_DIRECTION direction) {
            boolean found = false;
            if (searchValue != null && searchValue.length() > 0) {

                SEARCH_DIRECTION currentDir = direction;
                if (!searchValue.equals(this._searchValue) || (direction == SEARCH_DIRECTION.UNDEFINED)) {
                    reset();
                    currentDir = SEARCH_DIRECTION.NEXT;
                    this._searchValue = searchValue;
                }

                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("Searching value '" + searchValue + "' in direction " + currentDir);
                }

                final String regexp = (isRegExp) ? searchValue : convertToRegExp(searchValue);

                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("RegExp '" + regexp + "'");
                }

                // use tableSorter to process only visible rows and columns:
                final int nRows = _tableSorter.getRowCount();
                final int nCols = _tableSorter.getColumnCount();

                int row = 0;
                int col = 0;

                final int initCol;
                final int dir;

                if (direction == SEARCH_DIRECTION.PREVIOUS) {
                    dir = -1;
                    initCol = nCols - 1;
                } else {
                    dir = 1;
                    initCol = 0;
                }

                // Use current row/col:
                if (_currentRow != UNDEFINED && _currentCol != UNDEFINED) {
                    if (_logger.isLoggable(Level.FINE)) {
                        _logger.fine("Current row = " + _currentRow + ", col = " + _currentCol);
                    }
                    row = _currentRow;
                    col = _currentCol + dir; // skip current cell
                }

                final long start = System.nanoTime();

                String foundValue = null;
                int foundRow = -1;
                int foundCol = -1;
                boolean done = false;
                Object value;
                String textValue;

                // insensitive regexp:
                final Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
                Matcher matcher;

                // Traverse all rows:
                for (; row >= 0 && row < nRows && !done; row += dir) {

                    // Traverse visible columns:
                    for (; col >= 0 && col < nCols; col += dir) {

                        value = _tableSorter.getValueAt(row, col);

                        if (value != null) {
                            textValue = value.toString();

                            if (textValue.length() > 0) {
                                if (_logger.isLoggable(Level.FINE)) {
                                    _logger.fine("Cell value '" + textValue + "' at row " + row + ", col = " + col + ".");
                                }

                                matcher = pattern.matcher(textValue);

                                if (matcher.matches()) {
                                    foundValue = textValue;
                                    foundRow = row;
                                    foundCol = col;
                                    done = true;
                                    break;
                                }
                            }
                        }
                    }

                    // reset column index:
                    col = initCol;
                }

                if (foundValue != null) {
                    if (_logger.isLoggable(Level.INFO)) {
                        _logger.info("Found value '" + foundValue + "' at row " + foundRow + ", col = " + foundCol + ".");
                    }

                    // Clear previous selection:
                    _calibratorsTable.changeSelection(foundRow, foundCol, false, false);
                    _calibratorsTable.changeSelection(foundRow, foundCol, true, true);

                    _calibratorsTable.scrollRectToVisible(_calibratorsTable.getCellRect(foundRow, foundCol, true));
                    _calibratorsTable.requestFocus();

                    // memorize state:
                    _currentRow = foundRow;
                    _currentCol = foundCol;
                    found = true;
                }

                _logger.info("QuickSearchHelper.search: " + 1e-6d * (System.nanoTime() - start) + " ms.");

            }
            return found;
        }

        /**
         * Convert the given string value to become one regexp:
         * - escape "([{\^-=$!|]})?*+." by using '\' prefix
         * - '*' replaced by '.*'
         * - '?' replaced by '.?'
         * @param value string value
         * @return regexp string
         */
        private String convertToRegExp(final String value) {
            final StringBuilder regexp = new StringBuilder(value.length() + 16);

            // replace non regexp value to '*value*' to performs one contains operation (case sensitive):
            regexp.append("*").append(value).append("*");

            String token, replace;
            for (int i = 0, len = REGEXP_TOKEN_FROM.length; i < len; i++) {
                token = REGEXP_TOKEN_FROM[i];
                replace = REGEXP_TOKEN_REPLACE[i];

                replace(regexp, token, replace);
            }

            return regexp.toString();
        }

        /**
         * Replace the given source string by the dest string in the given string builder
         * @param sb string builder to process
         * @param source source string
         * @param dest destination string
         */
        private void replace(final StringBuilder sb, final String source, final String dest) {

            for (int from = 0, pos = -1; from != -1;) {
                pos = sb.indexOf(source, from);

                if (pos != -1) {
                    // ignore escaped string '\source'
                    if ((pos == 0) || (pos > 0 && sb.charAt(pos - 1) != '\\')) {
                        sb.replace(pos, pos + source.length(), dest);

                        // find from last replaced char (avoid reentrance):
                        from = pos + dest.length();
                    } else {
                        // find from last char (avoid reentrance):
                        from = pos + source.length();
                    }
                } else {
                    break;
                }
            }
        }
    }
}
