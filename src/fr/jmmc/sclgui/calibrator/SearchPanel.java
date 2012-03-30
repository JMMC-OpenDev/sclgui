/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.gui.MainMenuBar;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
 *
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 *
 * TODO : get search token from dedicated pasteboard (Mac!, Windows?, Linux...).
 * TODO : Handle case-sensitive searches.
 */
public class SearchPanel extends JFrame {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(SearchPanel.class.getName());
    // GUI stuff
    /** Window panel */
    private JPanel _panel;
    /** 'Find:" label */
    private JLabel _findLabel;
    /** Search Field */
    private JTextField _searchField;
    /** Get whether to use regexp syntax or not */
    private JCheckBox _regexpCheckBox;
    /** Find next searched token */
    private JButton _nextButton;
    /** Find previous searched token */
    private JButton _previousButton;
    // Action stuff
    /** Find action */
    public FindAction _findAction;
    /** Find Next action */
    public FindNextAction _findNextAction;
    /** Find Previous action */
    public FindPreviousAction _findPreviousAction;
    // Search Controler stuff
    /** Search and select algorithm */
    private QuickSearchHelper _searchHelper;
    /** The calibrators table */
    private JTable _calibratorsTable;
    /** The calibrator table sorter */
    private TableSorter _tableSorter;

    /**
     * Constructor
     * @param tableSorter the object to use to select found result.
     * @param calibratorsTable the data source to search in.
     */
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

    /** Create required actions */
    private void setupActions() {
        String classPath = getClass().getName();
        _findAction = new FindAction(classPath, "_findAction");
        _findNextAction = new FindNextAction(classPath, "_findNextAction");
        _findPreviousAction = new FindPreviousAction(classPath, "_findPreviousAction");
    }

    /** Create graphical widgets */
    private void createWidgets() {
        // The 'Find' window's panel
        _panel = new JPanel();

        // 'Find:' label
        _findLabel = new JLabel("Find:");
        _panel.add(_findLabel);

        // Search field
        _searchField = new JTextField();
        _panel.add(_searchField);

        // Regexp check box
        _regexpCheckBox = new JCheckBox("Use Regular Expression");
        _panel.add(_regexpCheckBox);

        // Previous button
        _previousButton = new JButton(_findPreviousAction);
        _previousButton.setText("Previous");
        _panel.add(_previousButton);

        // Next buton (the default one in the Find window)
        _nextButton = new JButton(_findNextAction);
        _nextButton.setText("Next");
        getRootPane().setDefaultButton(_nextButton);
        _panel.add(_nextButton);
    }

    /** Place graphical widgets on the 'Find' window */
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

    /** Start SearchField listening */
    private void monitorWidgets() {
        _searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doSearch(SEARCH_DIRECTION.UNDEFINED);
            }
        });
    }

    /** Finish window setup */
    private void prepareFrame() {

        getContentPane().add(_panel);

        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        WindowUtils.centerOnMainScreen(this);
        WindowUtils.setClosingKeyboardShortcuts(getRootPane(), this);
    }

    /**
     * (Dis)enable menu actions on demand.
     *
     * @param shouldBeEnabled Enables menu if true, disables them otherwise.
     */
    public void enableMenus(boolean shouldBeEnabled) {
        _findAction.setEnabled(shouldBeEnabled);
        _findNextAction.setEnabled(shouldBeEnabled);
        _findPreviousAction.setEnabled(shouldBeEnabled);
    }

    /**
     * Handle search requests.
     * @param direction Going 'NEXT' or 'PREVIOUS', or reset in 'UNDEFINED'.
     */
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

    /** Show the Search window when user click the 'Find' menu. */
    protected class FindAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        FindAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false); // Will be (dis)enabled dynamically on CalibratorView::tableChanged()
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FindAction", "actionPerformed");
            setVisible(true);
        }
    }

    /** Tries to find the next occurrence of the current searched token. */
    protected class FindNextAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        FindNextAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false); // Will be (dis)enabled dynamically on CalibratorView::tableChanged()
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FindNextAction", "actionPerformed");
            doSearch(SEARCH_DIRECTION.NEXT);
        }
    }

    /** Tries to find the previous occurrence of the current searched token. */
    protected class FindPreviousAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        FindPreviousAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            setEnabled(false); // Will be (dis)enabled dynamically on CalibratorView::tableChanged()
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FindPreviousAction", "actionPerformed");
            doSearch(SEARCH_DIRECTION.PREVIOUS);
        }
    }

    /** Quick search direction enumeration */
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

    /** Quick search algorithm supporting previous/next, the selected the result (if any) in the calibrator view. */
    private final class QuickSearchHelper {

        /** undefined */
        private final static int UNDEFINED_INDEX = -1;
        /* members */
        /** current found row index related to table view (visible rows) */
        private int _lastFoundRow;
        /** current found column index related to table view (visible columns)*/
        private int _lastFoundColumn;
        /** current search value */
        private String _searchValue;

        /** Protected constructor */
        protected QuickSearchHelper() {
            reset();
        }

        /** Reset current search state */
        private void reset() {
            _lastFoundRow = UNDEFINED_INDEX;
            _lastFoundColumn = UNDEFINED_INDEX;
            _searchValue = null;
        }

        /**
         * Tries to find the given token (regexp syntax or not) in the given direction.
         *
         * @param searchValue the string token to search for.
         * @param isRegExp if true the token is of regexp style, simple style otherwise.
         * @param givenDirection either NEXT or PREVIOUS search direction.
         *
         * @return true if something found, false otherwise.
         */
        protected boolean search(final String searchValue, final boolean isRegExp, final SEARCH_DIRECTION givenDirection) {

            // If the SearchField is empty or undefined
            boolean foundFlag = false;
            if (searchValue == null || searchValue.length() < 1) {
                return foundFlag;
            }

            // If the search token changed or a search reset was requested
            SEARCH_DIRECTION currentDirection = givenDirection;
            if (!searchValue.equals(_searchValue) || (currentDirection == SEARCH_DIRECTION.UNDEFINED)) {
                reset(); // Reset search context
                currentDirection = SEARCH_DIRECTION.NEXT; // Use NEXT direction by default
                _searchValue = searchValue; // Backup new search token
            }

            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("Searching value '" + searchValue + "' in '" + currentDirection + "' direction.");
            }

            // Convert search token to standard regexp if not yet in this syntax
            final String regexp;
            if (isRegExp) {
                regexp = searchValue; // Use given regexp straight away !
            } else {
                regexp = convertToRegExp(searchValue); // Otherwise convert simple syntax to regexp
            }

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Searched RegExp = '" + regexp + "'.");
            }

            // Use tableSorter to process only currently visible rows and columns
            final int nbOfRows = _tableSorter.getRowCount();
            final int nbOfColumns = _tableSorter.getColumnCount();

            int currentRow = 0;
            int currentColumn = 0;

            // If the backward direction is requested
            final int initColumn; // Either 0 for the first row column in forward mode, or (nbOfColumns - 1) for the last row in backward mode
            final int directionalIncrement; // Either -1 to go backward r +1 to go forward
            if (currentDirection == SEARCH_DIRECTION.PREVIOUS) { // Going backward
                initColumn = nbOfColumns - 1; // Will start search at the last cell of the current row
                directionalIncrement = -1; // Will decrement indexes
            } else { // Going forward
                initColumn = 0; // Will start search at the first cell of the row after the current one
                directionalIncrement = 1; // Will increment indexes
            }

            // Use previously found row/column if available
            if (_lastFoundRow != UNDEFINED_INDEX && _lastFoundColumn != UNDEFINED_INDEX) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("Current row = " + _lastFoundRow + ", col = " + _lastFoundColumn);
                }
                currentRow = _lastFoundRow;
                currentColumn = _lastFoundColumn + directionalIncrement; // Skip current cell (i.e last one found) anyway !
            }

            // Performance timer
            final long startTime = System.nanoTime();

            // Use insensitive regexp for the time being
            final Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

            // Traverse all rows
            String foundValue = null;
            boolean searchDone = false;
            int foundRow = UNDEFINED_INDEX;
            int foundColumn = UNDEFINED_INDEX;
            for (; currentRow >= 0 && currentRow < nbOfRows && !searchDone; currentRow += directionalIncrement) {

                // Traverse visible columns
                for (; currentColumn >= 0 && currentColumn < nbOfColumns; currentColumn += directionalIncrement) {

                    // Get current cell object
                    Object currentCell = _tableSorter.getValueAt(currentRow, currentColumn);
                    if (currentCell != null) {

                        // Get current cell string value
                        String currentValue = currentCell.toString();
                        if (currentValue.length() > 0) {
                            if (_logger.isLoggable(Level.FINE)) {
                                _logger.fine("Cell value '" + currentValue + "' at row " + currentRow + ", col = " + currentColumn + ".");
                            }

                            // Do current value matches searched regexp ?
                            Matcher matcher = pattern.matcher(currentValue);
                            if (matcher.matches()) {
                                foundValue = currentValue;
                                searchDone = true;
                                foundRow = currentRow;
                                foundColumn = currentColumn;
                                break;
                            }
                        }
                    }
                }

                // Reset column index (either first column in forward mode, last one in backward mode)
                currentColumn = initColumn;
            }

            if (foundValue == null) {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("Searched value '" + foundValue + "' not found.");
                }
            } else {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("Found value '" + foundValue + "' at row " + foundRow + ", col = " + foundColumn + ".");
                }

                // Clear previous selection and set new selection
                _calibratorsTable.changeSelection(foundRow, foundColumn, false, false);
                _calibratorsTable.changeSelection(foundRow, foundColumn, true, true);

                // Move view to show found cell
                _calibratorsTable.scrollRectToVisible(_calibratorsTable.getCellRect(foundRow, foundColumn, true));
                _calibratorsTable.requestFocus();

                // Memorize state for 'NEXT/PREVIOUS' purpose
                _lastFoundRow = foundRow;
                _lastFoundColumn = foundColumn;
                foundFlag = true;
            }

            _logger.info("QuickSearchHelper.search() done in " + 1e-6d * (System.nanoTime() - startTime) + " ms.");
            return foundFlag;
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

            // Replace non regexp value to '*value*' to performs one contains operation (case sensitive)
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
         * Replace the given source string by the destination string in the given string builder.
         * @param sb string builder to process
         * @param source source string
         * @param destination destination string
         */
        private void replace(final StringBuilder sb, final String source, final String destination) {

            for (int from = 0, position = -1; from != -1;) {
                position = sb.indexOf(source, from);

                if (position != -1) {
                    // ignore escaped string '\source'
                    if ((position == 0) || (position > 0 && sb.charAt(position - 1) != '\\')) {
                        sb.replace(position, position + source.length(), destination);

                        // find from last replaced char (avoid reentrance):
                        from = position + destination.length();
                    } else {
                        // find from last char (avoid reentrance):
                        from = position + source.length();
                    }
                } else {
                    break;
                }
            }
        }
    }
}
