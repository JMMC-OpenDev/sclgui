/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.gui.component.SearchPanel;
import fr.jmmc.jmcs.gui.component.SearchPanel.SEARCH_DIRECTION;
import fr.jmmc.jmcs.gui.component.SearchPanelDelegate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calibrator search engine.
 * @author Laurent BOURGES, Sylvain LAFRASSE.
 */
public class CalibratorSearchPanelDelegate implements SearchPanelDelegate {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(CalibratorSearchPanelDelegate.class.getName());
    /** undefined */
    private final static int UNDEFINED_INDEX = -1;
    // Members
    /** current calibrators view */
    private CalibratorsView _currentView;
    /** current search value (regexp pattern) */
    private String _pattern;
    /** current found row index related to table view (visible rows) */
    private int _lastFoundRow;
    /** current found column index related to table view (visible columns) */
    private int _lastFoundColumn;

    public CalibratorSearchPanelDelegate() {
        reset();
    }

    /** Reset current search state */
    private void reset() {
        _currentView = null;
        _pattern = null;
        _lastFoundRow = UNDEFINED_INDEX;
        _lastFoundColumn = UNDEFINED_INDEX;
    }

    public boolean search(final Pattern pattern, final SEARCH_DIRECTION givenDirection) {

        if (pattern == null) {
            return false;
        }

        // Get current view:
        final CalibratorsView currentView = CalibratorsView.getCurrentView();

        // If the search token or view changed or a search reset was requested:
        SEARCH_DIRECTION currentDirection = givenDirection;

        // Get regexp from compiled Pattern:
        final String regexp = pattern.toString();
        
        if (currentView != _currentView || !regexp.equals(_pattern) || (currentDirection == SEARCH_DIRECTION.UNDEFINED)) {
            reset(); // Reset search context
            currentDirection = SearchPanel.SEARCH_DIRECTION.NEXT; // Use NEXT direction by default
            _pattern = regexp; // Backup new search token
            _currentView = currentView; // Backup new view
        }

        _logger.info("Searching pattern '{}' in '{}' direction.", regexp, currentDirection);

        // Use table sorter to traverse visible cells:
        final TableSorter _tableSorter = currentView._tableSorter;

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
            _logger.debug("Current row = {}, col = {}", _lastFoundRow, _lastFoundColumn);
            currentRow = _lastFoundRow;
            currentColumn = _lastFoundColumn + directionalIncrement; // Skip current cell (i.e last one found) anyway !
        }

        // Traverse all rows
        String foundValue = null;
        boolean searchDone = false;
        int foundRow = UNDEFINED_INDEX;
        int foundColumn = UNDEFINED_INDEX;

        Object currentCell;
        String currentValue;
        Matcher matcher;

        // Traverse all visible rows
        final boolean isLogDebug = _logger.isDebugEnabled();
        for (; currentRow >= 0 && currentRow < nbOfRows && !searchDone; currentRow += directionalIncrement) {

            // Traverse visible columns
            for (; currentColumn >= 0 && currentColumn < nbOfColumns; currentColumn += directionalIncrement) {

                // Get current cell object
                currentCell = _tableSorter.getValueAt(currentRow, currentColumn);

                if (currentCell != null) {
                    // Get current cell string value
                    currentValue = currentCell.toString();

                    if (currentValue.length() != 0) {
                        if (isLogDebug) {
                            _logger.debug("Cell value '{}' at row {}, col = {}.", currentValue, currentRow, currentColumn);
                        }

                        // Do current value matches searched regexp ?
                        matcher = pattern.matcher(currentValue);
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

        // If the searched value was not found
        if (foundValue == null) {
            return false;
        }

        // Select the found cell
        _logger.info("Found value '{}' at row {}, col = {}.", foundValue, foundRow, foundColumn);
        currentView.selectTableCell(foundRow, foundColumn);

        // Memorize state for 'NEXT/PREVIOUS' purpose
        _lastFoundRow = foundRow;
        _lastFoundColumn = foundColumn;

        return true;
    }
}
