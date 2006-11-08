/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DistanceFilter.java,v 1.4 2006-11-08 22:25:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
        _deltaRA        = new Double(57.0);
        _deltaDEC       = new Double(24.0);

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
        _scienceObjectRA      = convertRA(_queryModel.getScienceObjectRA());
        _scienceObjectDEC     = convertDEC(_queryModel.getScienceObjectDEC());
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
        double       currentRA = convertRA(raString);

        // Get the current star DEC value
        StarProperty decCell    = ((StarProperty) row.elementAt(decId));
        String       decString  = (String) decCell.getValue();
        double       currentDEC = convertDEC(decString);

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

    /**
     * Convert the given Right Ascension (RA).
     *
     * @param raHms the right ascension as a HH:MM:SS.TT or HH MM SS.TT string.
     *
     * @return the right ascension as a double in degrees.
     */
    private double convertRA(String raHms)
    {
        MCSLogger.trace();

        double hh;
        double hm;
        double hs;

        // RA can be given as HH:MM:SS.TT or HH MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading pace
        raHms.replace(':', ' ');
        raHms.trim();

        // Parse the given string
        // sscanf(raHms, "%f %f %f", &hh, &hm, &hs)
        try
        {
            Scanner s = new Scanner(raHms).useDelimiter(" ");
            hh     = s.nextDouble();
            hm     = s.nextDouble();
            hs     = s.nextDouble();
            s.close();
        }
        catch (Exception e)
        {
            hh     = 0.0;
            hm     = 0.0;
            hs     = 0.0;
        }

        // Get sign of hh which has to be propagated to hm and hs
        double sign = (raHms.startsWith("-")) ? (-1.0) : 1.0;

        // Convert to degrees
        double ra = (hh + ((sign * hm) / 60.0) + ((sign * hs) / 3600.0)) * 15.0;

        // Set angle range [-180 - 180]
        if (ra > 180)
        {
            ra = -1.0 * (360 - ra);
        }

        return ra;
    }

    /**
     * Convert the given Declinaison (DEC).
     *
     * @param raHms the declinaison as a DD:MM:SS.TT or DD MM SS.TT string.
     *
     * @return the declinaison as a double in degrees.
     */
    private double convertDEC(String decDms)
    {
        MCSLogger.trace();

        double dd;
        double dm;
        double ds;

        // DEC can be given as DD:MM:SS.TT or DD MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading pace
        decDms.replace(':', ' ');
        decDms.trim();

        // Parse the given string
        // sscanf(decDms, "%f %f %f", &dd, &dm, &ds)
        try
        {
            Scanner s = new Scanner(decDms).useDelimiter(" ");
            dd     = s.nextDouble();
            dm     = s.nextDouble();
            ds     = s.nextDouble();
            s.close();
        }
        catch (Exception e)
        {
            dd     = 0.0;
            dm     = 0.0;
            ds     = 0.0;
        }

        // Get sign of hh which has to be propagated to hm and hs
        double sign = (decDms.startsWith("-")) ? (-1.0) : 1.0;

        // Convert to degrees
        double dec = dd + ((sign * dm) / 60.0) + ((sign * ds) / 3600.0);

        return dec;
    }
}
/*___oOo___*/
