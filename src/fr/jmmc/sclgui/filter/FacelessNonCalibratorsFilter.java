/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Reject stars that are not calibrators (no visibility).
 */
public class FacelessNonCalibratorsFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(FacelessNonCalibratorsFilter.class.getName());
    /** Store the visibility column name */
    private String _visibilityColumnName = "vis2";

    /**
     * ...constructor FacelessNonCalibratorsFilter documentation comment...
     */
    public FacelessNonCalibratorsFilter() {
        super();

        // This faceless filter should always be activated
        // (set once here as no GUI can change it anywhere else)
        setEnabled(true);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("FacelessNonCalibratorsFilter", "getName");

        return "Faceless Non-Calibrators Filter";
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
        _logger.entering("FacelessNonCalibratorsFilter", "shouldRemoveRow");

        // Get the ID of the column contaning 'visibility' star property
        int vis2Id = starList.getColumnIdByName(_visibilityColumnName);

        // If the desired column names exists
        if (vis2Id != -1) {
            // Get the cell of the desired column
            StarProperty vis2Cell = ((StarProperty) row.elementAt(vis2Id));

            // If the visibility is undefined
            if (vis2Cell.hasValue() == false) {
                _logger.fine("No vis2 - Line removed.");

                // This row should be removed
                return true;
            }
        }

        _logger.fine("Line kept.");

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
