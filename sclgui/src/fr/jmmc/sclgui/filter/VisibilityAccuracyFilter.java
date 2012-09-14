/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Visibiliy accuracy filter.
 */
public class VisibilityAccuracyFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(VisibilityAccuracyFilter.class.getName());
    /** Store the visibility constraint name */
    private String _visibilityColumnName = "vis2";
    /** Store the visibility error constraint name */
    private String _visibilityErrorColumnName = "vis2Err";
    /** Store the visibility accuracy constraint name */
    private String _visibilityAccuracyConstraintName = "vis2Err/vis2 (%)";

    /**
     * Default constructor.
     */
    public VisibilityAccuracyFilter() {
        super();

        setConstraint(_visibilityAccuracyConstraintName, new Double(2.0));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("VisibilityAccuracyFilter", "getName");

        return "Reject Visibility Accuracy above (or unknown) :";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibiliyAccurancy() {
        _logger.entering("VisibilityAccuracyFilter",
                "getAllowedVisibiliyAccurancy");

        Double d = (Double) getConstraintByName(_visibilityAccuracyConstraintName);

        return d.doubleValue() / 100;
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
        // Get the ID of the column contaning 'visibility' star property
        int vis2Id = starList.getColumnIdByName(_visibilityColumnName);

        // Get the ID of the column contaning 'visibilityErr' star property
        int vis2errId = starList.getColumnIdByName(_visibilityErrorColumnName);

        // If the desired column names exists
        if ((vis2Id != -1) && (vis2errId != -1)) {
            // Get the cell of the desired column
            StarProperty vis2Cell = ((StarProperty) row.elementAt(vis2Id));

            // Get the cell of the desired column
            StarProperty vis2ErrCell = ((StarProperty) row.elementAt(vis2errId));

            // If the 2 values were found in the current line
            if ((vis2Cell.hasValue() == true)
                    && (vis2ErrCell.hasValue() == true)) {
                double currentVis2 = vis2Cell.getDoubleValue();
                double currentVis2err = vis2ErrCell.getDoubleValue();
                double visibilityAccuracy = Math.abs(currentVis2err / currentVis2);

                // if the visibility is not a number (eg. 0.0/0.0)
                if (Double.isNaN(visibilityAccuracy) == true) {
                    // This row should be removed
                    return true;
                }

                // if the visibility value is greater than the allowed one
                if (visibilityAccuracy >= getAllowedVisibiliyAccurancy()) {
                    // This row should be removed
                    return true;
                }
            } else // If any value is missing
            {
                // This row should be removed
                return true;
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
