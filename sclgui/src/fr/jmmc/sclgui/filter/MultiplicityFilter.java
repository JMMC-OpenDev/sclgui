/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Multiplicity filter.
 */
public class MultiplicityFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(MultiplicityFilter.class.getName());
    /** Store identifiers of each multiplicity IDs column to consider */
    private static final String[] _multiplicityIDs = {
        "MultFlag", "BinFlag", "SBC9"
    };
    /** Store identifiers of each orbit separation column to consider */
    private static final String[] _orbitSeparationIDs = {"sep1", "sep2"};
    /** In arcseconds. Any orbit separation below should reject */
    private static final double _orbitSeparationLimit = 2.0;

    /**
     * Default constructor.
     */
    public MultiplicityFilter() {
        super();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("MultiplicityFilter", "getName");

        setEnabled(true);

        return "Reject Multiplicity";
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param starList the list of stars from which the row may be removed.
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    @Override
    public boolean shouldRemoveRow(StarList starList, Vector row) {
        StarProperty cell = null;

        for (String columnName : _multiplicityIDs) {
            // Get the id of the current column name
            int columnId = starList.getColumnIdByName(columnName);

            // If the desired column name exists
            if (columnId != -1) {
                // Get the cell of the desired column
                cell = ((StarProperty) row.elementAt(columnId));

                // if the multiplicity flag exist
                if (cell.hasValue() == true) {
                    // This row should be removed
                    return true;
                }
            }
        }

        for (String columnName : _orbitSeparationIDs) {
            // Get the id of the current column name
            int columnId = starList.getColumnIdByName(columnName);

            // If the desired column name exists
            if (columnId != -1) {
                // Get the cell of the desired column
                cell = ((StarProperty) row.elementAt(columnId));

                // if the orbit separation exist
                if (cell.hasValue() == true) {
                    // If orbit separation is below limit
                    if (cell.getDoubleValue() < _orbitSeparationLimit) {
                        // This row should be removed
                        return true;
                    }
                }
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
