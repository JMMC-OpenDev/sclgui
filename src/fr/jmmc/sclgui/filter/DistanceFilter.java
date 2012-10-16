/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.jmal.ALX;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import fr.jmmc.sclgui.query.QueryModel;
import java.util.List;
import java.util.logging.Logger;

/**
 * Distance filter.
 */
public final class DistanceFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(DistanceFilter.class.getName());
    /** Store the RA delta constraint name */
    private final static String _deltaRAConstraintName = "Maximum RA Separation (mn)";
    /** Store the DEC delta constraint name */
    private final static String _deltaDECConstraintName = "Maximum DEC Separation (degree)";

    /* members */
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel;
    /* filter execution variables */
    /** the 'RA' column ID */
    private int _raId = -1;
    /** the 'DEC' column ID */
    private int _decId = -1;
    /** Store the science object RA value (in degrees) */
    private double _scienceObjectRA;
    /** Store the science object DEC value (in degrees) */
    private double _scienceObjectDEC;
    /** Store the allowed RA delta (in degrees) */
    private double _deltaRA;
    /** Store the allowed DEC delta (in degrees) */
    private double _deltaDEC;

    /**
     * Default constructor.
     *
     * @param queryModel the query parameters (for science object coordinates)
     */
    public DistanceFilter(QueryModel queryModel) {
        super();

        _queryModel = queryModel;

        _deltaRA = new Double(10.0);
        _deltaDEC = new Double(10.0);

        setConstraint(_deltaRAConstraintName, _deltaRA);
        setConstraint(_deltaDECConstraintName, _deltaDEC);

        retrieveScienceObjectCoordinates();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("DistanceFilter", "getName");

        return "Reject stars farther than :";
    }

    /**
     * Retrieve the science object RA and DEC values.
     */
    private void retrieveScienceObjectCoordinates() {
        _logger.entering("DistanceFilter", "retrieveScienceObjectCoordinates");

        // Get the science object 'RA' and 'DEC' properties
        _scienceObjectRA = ALX.parseRA(_queryModel.getScienceObjectRA());
        _scienceObjectDEC = ALX.parseDEC(_queryModel.getScienceObjectDEC());
    }

    /**
     * Retrieve the user defined delta RA and delta DEC.
     */
    private void retrieveDeltas() {
        _logger.entering("DistanceFilter", "retrieveDeltas");

        final Double ra = (Double) getConstraintByName(_deltaRAConstraintName);
        _deltaRA = ALX.minutes2degrees(ra.doubleValue());

        final Double dec = (Double) getConstraintByName(_deltaDECConstraintName);
        _deltaDEC = dec.doubleValue();
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the IDs of the columns contaning 'RA' & 'DEC' star properties
        _raId = starList.getColumnIdByName("RAJ2000");
        _decId = starList.getColumnIdByName("DEJ2000");

        // Get back query and filter values
        retrieveScienceObjectCoordinates();
        retrieveDeltas();
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
        if ((_raId != -1) && (_decId != -1)) {
            // Get the current star RA value
            final StarProperty raCell = row.get(_raId);

            // Get the current star DEC value
            final StarProperty decCell = row.get(_decId);

            // If the 2 values were found in the current line
            if (raCell.hasValue() && decCell.hasValue()) {

                // TODO: use RA/DEC columns in degrees here:

                // Convert RA cell value
                final String raString = raCell.getStringValue();
                final double currentRA = ALX.parseRA(raString);

                // Convert DEC cell value
                final String decString = decCell.getStringValue();
                final double currentDEC = ALX.parseDEC(decString);

                // Compute separation between science object & the current star
                final double raSeparation = Math.abs(_scienceObjectRA - currentRA);
                final double decSeparation = Math.abs(_scienceObjectDEC - currentDEC);

                // If the current star is out of range
                if ((raSeparation > _deltaRA) || (decSeparation > _deltaDEC)) {
                    // TODO: use correct distance computation ?
                    // The current star row should be removed
                    return true;
                }
            } else {
                // If any value is missing
                // This row should be removed
                return false;
            }
        }

        // Otherwise the current star should be kept
        return false;
    }
}
/*___oOo___*/
