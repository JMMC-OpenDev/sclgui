/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VisibilityAccuracyFilter.java,v 1.5 2006-11-08 22:25:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.3  2006/08/04 14:09:10  lafrasse
 * Added GUI enabling/disabling feature to filters
 *
 * Revision 1.2  2006/08/03 14:47:29  lafrasse
 * Jalopyzation
 *
 * Revision 1.1  2006/07/28 13:14:53  lafrasse
 * Creation
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.lang.Math;

import java.util.Vector;


/**
 * Visibiliy filter.
 */
public class VisibilityFilter extends Filter
{
    /** Store the visibility accuracy constraint name */
    private String _visibilityAccuracyConstraintName = "Accuracy (%)";

    /**
     * Default constructor.
     */
    public VisibilityFilter()
    {
        super();

        //setConstraint(_visibilityAccuracyConstraintName, new Double(0.0));
        // @TODO : remove the demo value
        setConstraint(_visibilityAccuracyConstraintName, new Double(0.2));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject Visibility Accuracy above :";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibiliy()
    {
        MCSLogger.trace();

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
    public boolean shouldRemoveRow(StarList starList, Vector row)
    {
        MCSLogger.trace();

        // Get the ID of the column contaning 'visibility' star property
        int vis2Id = starList.getColumnIdByName("vis2");

        // Get the ID of the column contaning 'visibilityErr' star property
        int          vis2errId      = starList.getColumnIdByName("vis2Err");

        StarProperty cell1          = ((StarProperty) row.elementAt(vis2Id));
        double       currentVis2    = cell1.getDoubleValue();

        StarProperty cell2          = ((StarProperty) row.elementAt(vis2errId));
        double       currentVis2err = cell2.getDoubleValue();

        // if the visibility value is greater than the allowed one
        if (Math.abs(currentVis2err / currentVis2) >= getAllowedVisibiliy())
        {
            // This row should be removed
            return true;
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
