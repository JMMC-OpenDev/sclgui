/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import fr.jmmc.sclgui.query.QueryModel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Magnitude filter.
 */
public final class MagnitudeFilter extends Filter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(MagnitudeFilter.class.getName());
    /** Store the magnitude constraint name below */
    private final static String _belowMagnitudeConstraintName = "below";
    /** Store the magnitude constraint name above */
    private final static String _aboveMagnitudeConstraintName = "and above";
    /* members */
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel;

    /* filter execution variables */
    /** the good magnitude column Id */
    private int _magnitudeId = -1;
    /** user defined minimum magnitude */
    private double _lowerMag = 0d;
    /** user defined maximum magnitude */
    private double _upperMag = 0d;

    /**
     * Default constructor.
     *
     * @param queryModel the query parameters (for magnitude band)
     */
    public MagnitudeFilter(QueryModel queryModel) {
        super();

        _queryModel = queryModel;

        setConstraint(_belowMagnitudeConstraintName, new Double(0));
        setConstraint(_aboveMagnitudeConstraintName, new Double(10));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject stars with magnitude :";
    }

    /**
     * @return the user defined minimum magnitude.
     */
    private double getLowerLimitAllowedMagnitude() {
        Double magnitude = (Double) getConstraintByName(_belowMagnitudeConstraintName);

        return magnitude.doubleValue();
    }

    /**
     * @return the user defined maximum magnitude.
     */
    private double getUpperLimitAllowedMagnitude() {
        Double magnitude = (Double) getConstraintByName(_aboveMagnitudeConstraintName);

        return magnitude.doubleValue();
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the query magnitude band
        final String magnitudeBand = _queryModel.getInstrumentalMagnitudeBand();

        // Get the id of the column contaning the good magnitude
        _magnitudeId = starList.getColumnIdByName(magnitudeBand);

        // Get the magnitude range allowed
        _lowerMag = getLowerLimitAllowedMagnitude();
        _upperMag = getUpperLimitAllowedMagnitude();
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
        // If the desired column name exists
        if (_magnitudeId != -1) {
            // Get the cell of the desired column
            final StarProperty cell = row.get(_magnitudeId);

            // Only test and eventualy remove if the cell has a value
            if (cell.hasValue()) {
                final double currentMagnitude = cell.getDoubleValue();

                // if the magnitude is greater than the allowed one
                if ((currentMagnitude < _lowerMag) || (currentMagnitude > _upperMag)) {
                    // This row should be removed
                    return true;
                }
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
