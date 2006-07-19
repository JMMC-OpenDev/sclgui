/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MagnitudeFilter.java,v 1.1 2006-07-19 16:22:55 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
     * Default constructor.
     */
    public MagnitudeFilter()
    {
        super();

        _constraints.put("magnitude", new Double(0.0));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject stars with magnitude below :";
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
/*
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
*/
        }
    }
}
/*___oOo___*/
