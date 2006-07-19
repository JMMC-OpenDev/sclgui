/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DistanceFilter.java,v 1.2 2006-07-19 16:22:31 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Vector;


/**
 * Distance filter.
 */
public class DistanceFilter extends Filter
{
    //TODO place a constant for every "dist" ;))

    /**
     * Default constructor.
     */
    public DistanceFilter()
    {
        super();

        _constraints.put("dist", new Double(0.0));
        _constraints.put("Maximum RA Separation (mn)", new Double(0.0));
        _constraints.put("Maximum DEC Separation (degree)", new Double(0.0));
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
     * Return the maximum distance allowed by this filter.
     *
     * @return the maximum distance allowed by this filter.
     */
    private double getAllowedDistance()
    {
        MCSLogger.trace();

        Double d = (Double) _constraints.get("dist");

        return d.doubleValue();
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList)
    {
        MCSLogger.trace();

        // If the filter is enabled
        if (isEnabled() == true)
        {
            // Get the id of the column contaning 'distance' star property
            int distanceId = starList.getColumnIdByName("dist");

            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
                // Get the distance value
                Vector       row             = ((Vector) starList.elementAt(rowId));
                StarProperty cell            = ((StarProperty) row.elementAt(distanceId));
                double       currentDistance = cell.getDoubleValue();

                // if the distance value is greater than the allowed one
                if (currentDistance > getAllowedDistance())
                {
                    // Remove this row from the star list
                    starList.remove(rowId);
                }
                else
                {
                    // Otherwise process the next row
                    rowId++;
                }
            }
        }
    }
}
/*___oOo___*/
