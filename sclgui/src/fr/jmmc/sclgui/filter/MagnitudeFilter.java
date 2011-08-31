/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Magnitude filter.
 */
public class MagnitudeFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(MagnitudeFilter.class.getName());
    /** Store the magnitude constraint name */
    private final String _magnitudeConstraintName = "Magnitude";
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel;

    /**
     * Default constructor.
     *
     * @param queryModel the query parameters (for magnitude band)
     */
    public MagnitudeFilter(QueryModel queryModel) {
        super();

        _queryModel = queryModel;

        //setConstraint(_magnitudeConstraintName, new Double(0.0));
        // @TODO : remove the demo values
        setConstraint(_magnitudeConstraintName, new Double(1.5));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("MagnitudeFilter", "getName");

        return "Reject stars with magnitude above :";
    }

    /**
     * @return the user defined maximum magnitude.
     */
    private double getAllowedMagnitude() {
        _logger.entering("MagnitudeFilter", "getAllowedMagnitude");

        Double magnitude = (Double) getConstraintByName(_magnitudeConstraintName);

        return magnitude.doubleValue();
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
        _logger.entering("MagnitudeFilter", "shouldRemoveRow");

        // Get the query magnitude band
        String magnitudeBand = _queryModel.getInstrumentalMagnitudeBand();

        // Get the id of the column contaning the good magnitude
        int magnitudeId = starList.getColumnIdByName(magnitudeBand);

        // If the desired column name exists
        if (magnitudeId != -1) {
            // Get the cell of the desired column
            StarProperty cell = ((StarProperty) row.elementAt(magnitudeId));

            // Only test and eventualy remove if the cell has a value
            if (cell.hasValue() == true) {
                double currentMagnitude = cell.getDoubleValue();

                // if the magnitude is greater than the allowed one
                if (currentMagnitude > getAllowedMagnitude()) {
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
