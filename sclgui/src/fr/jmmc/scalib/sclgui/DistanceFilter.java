/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.astro.*;

import java.util.*;
import java.util.logging.*;

/**
 * Distance filter.
 */
public class DistanceFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.DistanceFilter");
    /** Store the RA column name */
    private String _raColumnName = "RAJ2000";
    /** Store the DEC column name */
    private String _decColumnName = "DEJ2000";
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel;
    /** Store the science object RA value (in degrees) */
    private double _scienceObjectRA;
    /** Store the science object DEC value (in degrees) */
    private double _scienceObjectDEC;
    /** Store the allowed RA delta (in degrees) */
    private double _deltaRA;
    /** Store the RA delta constraint name */
    private String _deltaRAConstraintName = "Maximum RA Separation (mn)";
    /** Store the allowed DEC delta (in degrees) */
    private double _deltaDEC;
    /** Store the DEC delta constraint name */
    private String _deltaDECConstraintName = "Maximum DEC Separation (degree)";

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
    public String getName() {
        _logger.entering("DistanceFilter", "getName");

        return "Reject stars farther than :";
    }

    /**
     * Retrieve the science object RA and DEC values.
     */
    private void retrieveScienceObjectCoordinates() {
        _logger.entering("DistanceFilter", "retrieveScienceObjectCoordinates");

        // Get the science objct 'RA' and 'DEC' properties
        _scienceObjectRA = ALX.parseRA(_queryModel.getScienceObjectRA());
        _scienceObjectDEC = ALX.parseDEC(_queryModel.getScienceObjectDEC());
    }

    /**
     * Retrieve the user defined delta RA and delta DEC.
     */
    private void retrieveDeltas() {
        _logger.entering("DistanceFilter", "retrieveDeltas");

        Double ra = (Double) getConstraintByName(_deltaRAConstraintName);
        _deltaRA = ALX.minutes2degrees(ra.doubleValue());

        Double dec = (Double) getConstraintByName(_deltaDECConstraintName);
        _deltaDEC = dec.doubleValue();
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
        _logger.entering("DistanceFilter", "shouldRemoveRow");

        // Get the IDs of the columns contaning 'RA' & 'DEC' star properties
        int raId = starList.getColumnIdByName(_raColumnName);
        int decId = starList.getColumnIdByName(_decColumnName);

        // If the desired column names exists
        if ((raId != -1) && (decId != -1)) {
            // Get the current star RA value
            StarProperty raCell = ((StarProperty) row.elementAt(raId));

            // Get the current star DEC value
            StarProperty decCell = ((StarProperty) row.elementAt(decId));

            // If the 2 values were found in the current line
            if ((raCell.hasValue() == true) && (decCell.hasValue() == true)) {
                // Convert RA cell value
                String raString = (String) raCell.getValue();
                double currentRA = ALX.parseRA(raString);

                // Convert DEC cell value
                String decString = (String) decCell.getValue();
                double currentDEC = ALX.parseDEC(decString);

                // Get back query and filter values
                retrieveScienceObjectCoordinates();
                retrieveDeltas();

                // Compute separation between science object & the current star
                double raSeparation = Math.abs(_scienceObjectRA - currentRA);
                double decSeparation = Math.abs(_scienceObjectDEC - currentDEC);

                // If the current star is out of range
                if ((raSeparation > _deltaRA) || (decSeparation > _deltaDEC)) {
                    // The current star row should be removed
                    return true;
                }
            } else // If any value is missing
            {
                // This row should be removed
                return false;
            }
        }

        // Otherwise the current star should be kept
        return false;
    }
}
/*___oOo___*/
