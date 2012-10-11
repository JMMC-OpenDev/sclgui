/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;
import java.util.logging.Logger;

/**
 * Multiplicity filter.
 */
public final class MultiplicityFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(MultiplicityFilter.class.getName());
    /** Store column names of each multiplicity column to consider */
    private static final String[] MultiplicityColumnNames = {
        "MultFlag", "BinFlag", "SBC9"
    };
    /** Store column names of each orbit separation column to consider */
    private static final String[] OrbitSeparationColumnNames = {"sep1", "sep2"};
    /** In arcseconds. Any orbit separation below should reject */
    private static final double _orbitSeparationLimit = 2d;
    /* members */
    /* filter execution variables */
    /** the multiplicity column Ids */
    private final int[] _multiplicityIDs = new int[]{-1, -1, -1};
    /** the orbit separation column Ids */
    private final int[] _orbitSeparationIds = new int[]{-1, -1};

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

        // TODO: enable the filter somewhere else not in getName() !
        setEnabled(Boolean.TRUE);

        return "Reject Multiplicity";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        int i = 0;
        for (String columnName : MultiplicityColumnNames) {
            // Get the id of the current column name
            _multiplicityIDs[i++] = starList.getColumnIdByName(columnName);
        }

        i = 0;
        for (String columnName : OrbitSeparationColumnNames) {
            // Get the id of the current column name
            _orbitSeparationIds[i++] = starList.getColumnIdByName(columnName);
        }
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    @Override
    public boolean shouldRemoveRow(final List<StarProperty> row) {
        StarProperty cell;

        for (int columnId : _multiplicityIDs) {
            // If the desired column name exists
            if (columnId != -1) {
                // Get the cell of the desired column
                cell = row.get(columnId);

                // if the multiplicity flag exist
                if (cell.hasValue()) {
                    // This row should be removed
                    return true;
                }
            }
        }

        for (int columnId : _orbitSeparationIds) {
            // If the desired column name exists
            if (columnId != -1) {
                // Get the cell of the desired column
                cell = row.get(columnId);

                // if the orbit separation exist
                if (cell.hasValue()) {
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
