/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visibiliy accuracy filter.
 */
public final class VisibilityAccuracyFilter extends Filter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(VisibilityAccuracyFilter.class.getName());
    /** Store the visibility accuracy constraint name */
    private final static String _visibilityAccuracyConstraintName = "vis2Err/vis2 (%)";
    /* members */
    /* filter execution variables */
    /** the 'visibility' column ID */
    private int _vis2Id = -1;
    /** the 'visibilityErr' column ID */
    private int _vis2errId = -1;
    /** maximum visibility accuracy allowed */
    private double _allowedAccuracyVis = 0d;

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
        return "Reject Visibility Accuracy above (or unknown) :";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibilityAccurancy() {
        Double d = (Double) getConstraintByName(_visibilityAccuracyConstraintName);

        return d.doubleValue() / 100d;
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the ID of the column containing 'visibility' star property
        _vis2Id = starList.getColumnIdByName(StarList.Vis2ColumnName);

        // Get the ID of the column containing 'visibilityErr' star property
        _vis2errId = starList.getColumnIdByName(StarList.Vis2ErrColumnName);

        // Get the maximum visibility accuracy allowed:
        _allowedAccuracyVis = getAllowedVisibilityAccurancy();
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

        // If the desired column names exists
        if ((_vis2Id != -1) && (_vis2errId != -1)) {
            // Get the cell of the desired column
            final StarProperty vis2Cell = row.get(_vis2Id);

            // Get the cell of the desired column
            final StarProperty vis2ErrCell = row.get(_vis2errId);

            // If the 2 values were found in the current line
            if (vis2Cell.hasValue() && vis2ErrCell.hasValue()) {
                final double currentVis2 = vis2Cell.getDoubleValue();
                final double currentVis2err = vis2ErrCell.getDoubleValue();
                final double visibilityAccuracy = Math.abs(currentVis2err / currentVis2);

                // if the visibility is not a number (eg. 0.0/0.0)
                if (Double.isNaN(visibilityAccuracy)) {
                    // This row should be removed
                    return true;
                }

                // if the visibility value is greater than the allowed one
                if (visibilityAccuracy >= _allowedAccuracyVis) {
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
