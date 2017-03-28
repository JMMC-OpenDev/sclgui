/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.CoordUtils;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import fr.jmmc.sclgui.query.QueryModel;
import java.util.List;

/**
 * Distance filter.
 */
public final class DistanceFilter extends Filter {

    /** Store the RA delta constraint name */
    private final static String _deltaRAConstraintName = "Maximum RA Separation (mn)";
    /** Store the DEC delta constraint name */
    private final static String _deltaDECConstraintName = "Maximum DEC Separation (degree)";

    /* members */
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private final QueryModel _queryModel;
    /* filter execution variables */
    /** the 'RA' (deg) column ID */
    private int _raId = -1;
    /** the 'DEC' (deg) column ID */
    private int _decId = -1;
    /** Store the science object RA value (in degrees) */
    private double _scienceObjectRA;
    /** Store the science object DEC value (in degrees) */
    private double _scienceObjectDE;
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
     * Reset the filter
     */
    @Override
    public void reset() {
        // no-op
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject stars farther than :";
    }

    /**
     * Retrieve the science object RA and DEC values.
     */
    private void retrieveScienceObjectCoordinates() {
        // Get the science object 'RA' and 'DEC' properties
        _scienceObjectRA = ALX.parseRA(_queryModel.getScienceObjectRA());
        _scienceObjectDE = ALX.parseDEC(_queryModel.getScienceObjectDEC());
    }

    /**
     * Retrieve the user defined delta RA and delta DEC.
     */
    private void retrieveDeltas() {
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
        // Get the IDs of the DYNAMIC columns containing 'RA' & 'DEC' (degrees) star properties
        _raId = starList.getColumnIdByName(StarList.RADegColumnName);
        _decId = starList.getColumnIdByName(StarList.DEDegColumnName);

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

                // Get RA cell value in degrees:
                final double currentRA = raCell.getDoubleValue();

                // Get DEC cell value in degrees:
                final double currentDEC = decCell.getDoubleValue();

                // Compute separation between science object and the current star:
                // use distance computation to correct RA arround poles (by cos(dec)) : 
                // TODO: use a correct distance ?
                final double raSeparation = CoordUtils.computeDistanceInDegrees(_scienceObjectRA, _scienceObjectDE, currentRA, _scienceObjectDE);
                final double deSeparation = Math.abs(_scienceObjectDE - currentDEC);

                // If the current star is out of range
                if ((raSeparation > _deltaRA) || (deSeparation > _deltaDEC)) {
                    // TODO: use correct distance computation ? because it will be false close to poles
                    // The current star row should be removed
                    return true;
                }
            } else {
                // If any value is missing
                // This row should be removed
                return true;
            }
        }

        // Otherwise the current star should be kept
        return false;
    }
}
/*___oOo___*/
