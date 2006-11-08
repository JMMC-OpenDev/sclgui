/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MagnitudeFilter.java,v 1.4 2006-11-08 22:25:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.2  2006/08/03 14:47:29  lafrasse
 * Jalopyzation
 *
 * Revision 1.1  2006/07/19 16:22:55  lafrasse
 * Creation
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Vector;


/**
 * Magnitude filter.
 */
public class MagnitudeFilter extends Filter
{
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel;

    /** Store the magnitude constraint name */
    private String _magnitudeConstraintName = "Magnitude";

    /**
     * Default constructor.
     *
     * @param queryModel the query parameters (for magnitude band)
     */
    public MagnitudeFilter(QueryModel queryModel)
    {
        super();

        _queryModel = queryModel;

        //setConstraint(_magnitudeConstraintName, new Double(0.0));
        // @TODO : remove the demo values
        setConstraint(_magnitudeConstraintName, new Double(6.0));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject stars with magnitude above :";
    }

    /**
     * Get the user defined maximum magnitude.
     */
    private double getAllowedMagnitude()
    {
        MCSLogger.trace();

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
    public boolean shouldRemoveRow(StarList starList, Vector row)
    {
        MCSLogger.trace();

        // Get the query magnitude band
        String magnitudeBang = _queryModel.getInstrumentalMagnitudeBand();

        // Get the id of the column contaning the good magnitude
        int          magnitudeId      = starList.getColumnIdByName(magnitudeBang);

        StarProperty cell             = ((StarProperty) row.elementAt(magnitudeId));
        double       currentMagnitude = cell.getDoubleValue();

        // if the magnitude is greater than the allowed one
        if (currentMagnitude > getAllowedMagnitude())
        {
            // This row should be removed
            return true;
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
