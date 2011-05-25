/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.util.Vector;
import java.util.logging.Level;

/**
 * Visibiliy filter.
 */
public class VisibilityFilter extends Filter {

    /** Logger */
    private static final java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.scalib.sclgui.VisibilityFilter");
    /** Store the visibility constraint name */
    private String _visibilityColumnName = "vis2";
    /** Store the visibility constraint name */
    private String _visibilityConstraintName = "vis2";

    /**
     * Default constructor.
     */
    public VisibilityFilter() {
        super();

        setConstraint(_visibilityConstraintName, new Double(0.5));
        setEnabled(true);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName() {
        _logger.entering("VisibilityFilter", "getName");

        return "Reject Visiblity below :";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibility() {
        _logger.entering("VisibilityFilter", "getAllowedVisibility");

        Double d = (Double) getConstraintByName(_visibilityConstraintName);

        return d.doubleValue();
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param starList the list of stars from which the row may be removed.
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    public boolean shouldRemoveRow(StarList starList, Vector row) {
        _logger.entering("VisibilityFilter", "shouldRemoveRow");

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

            // If the visibility is less than 0.5
            if (vis2Cell.getDoubleValue() < getAllowedVisibility()) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("vis2 < " + getAllowedVisibility()
                        + " - Line removed.");
                }

                // This row should be removed
                return true;
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
