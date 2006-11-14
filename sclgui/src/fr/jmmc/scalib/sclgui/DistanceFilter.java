/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DistanceFilter.java,v 1.5 2006-11-14 14:44:56 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.3  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.2  2006/07/19 16:22:31  lafrasse
 * Changed name and behavior according to preliminary review scientists feedback
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.astro.ALX;

import jmmc.mcs.log.MCSLogger;

import java.lang.Math;

import java.util.*;


/**
 * Distance filter.
 */
public class DistanceFilter extends Filter
{
    //TODO place a constant for every "dist" ;))

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
    public DistanceFilter(QueryModel queryModel)
    {
        super();

        _queryModel     = queryModel;

        //_deltaRA        = new Double(0.0);
        //_deltaDEC       = new Double(0.0);
        // @TODO : remove the demo values
        _deltaRA        = new Double(0.2);
        _deltaDEC       = new Double(0.2);

        setConstraint(_deltaRAConstraintName, _deltaRA);
        setConstraint(_deltaDECConstraintName, _deltaDEC);

        retrieveScienceObjectCoordinates();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject stars farther than :";
    }

    /**
     * Retrieve the science object RA and DEC values.
     */
    private void retrieveScienceObjectCoordinates()
    {
        MCSLogger.trace();

        // Get the science objct 'RA' and 'DEC' properties
        _scienceObjectRA      = ALX.convertRA(_queryModel.getScienceObjectRA());
        _scienceObjectDEC     = ALX.convertDEC(_queryModel.getScienceObjectDEC());
    }

    /**
     * Retrieve the user defined delta RA and delta DEC.
     */
    private void retrieveDeltas()
    {
        MCSLogger.trace();

        Double ra = (Double) getConstraintByName(_deltaRAConstraintName);
        _deltaRA = ra.doubleValue();

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
    public boolean shouldRemoveRow(StarList starList, Vector row)
    {
        MCSLogger.trace();

        // Get the IDs of the columns contaning 'RA' & 'DEC' star properties
        int raId  = starList.getColumnIdByName("RAJ2000");
        int decId = starList.getColumnIdByName("DEJ2000");

        // Get the current star RA value
        StarProperty raCell    = ((StarProperty) row.elementAt(raId));
        String       raString  = (String) raCell.getValue();
        double       currentRA = ALX.convertRA(raString);

        // Get the current star DEC value
        StarProperty decCell    = ((StarProperty) row.elementAt(decId));
        String       decString  = (String) decCell.getValue();
        double       currentDEC = ALX.convertDEC(decString);

        // if the current star is farther than the science object
        retrieveScienceObjectCoordinates();
        retrieveDeltas();

        // Compute separation between science object & the current star
        double raSeparation  = Math.abs(_scienceObjectRA - currentRA);
        double decSeparation = Math.abs(_scienceObjectDEC - currentDEC);

        // If the current star is out of range
        if ((raSeparation > _deltaRA) || (decSeparation > _deltaDEC))
        {
            // The current star row from the star list should be removed
            return true;
        }

        // Otherwise the current star should be kept
        return false;
    }
}
/*___oOo___*/
