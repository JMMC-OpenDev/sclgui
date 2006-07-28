/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VisibilityFilter.java,v 1.1 2006-07-28 13:14:53 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Vector;


/**
 * Visibiliy filter.
 */
public class VisibilityFilter extends Filter
{
    /**
     * Default constructor.
     */
    public VisibilityFilter()
    {
        super();

        _constraints.put("accuracy (%)", new Double(0.0));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject visibility accuracy above";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibiliy()
    {
        MCSLogger.trace();

        Double d = new Double((String) _constraints.get("accuracy (%)"));

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
            // Get the id of the column contaning 'visibility' star property
            int visibilityId = starList.getColumnIdByName("dist");

            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
                // Get the visibility value
                Vector       row             = ((Vector) starList.elementAt(rowId));
                StarProperty cell            = ((StarProperty) row.elementAt(visibilityId));
                double       currentVisibiliy = cell.getDoubleValue();

                // if the visibility value is greater than the allowed one
                if (currentVisibiliy > getAllowedVisibiliy())
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
