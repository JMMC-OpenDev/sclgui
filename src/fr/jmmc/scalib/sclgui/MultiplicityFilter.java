/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MultiplicityFilter.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Vector;


/**
 * Multiplicity filter.
 */
public class MultiplicityFilter extends Filter
{
    //TODO place a constant for every "multiple" ;))

    /**
     * Default constructor.
     */
    public MultiplicityFilter()
    {
        super();
        _constraints.put("multiple", new Boolean(true));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Multiplicity";
    }

    /**
     * Return if or not multiplicity is allowed by this filter.
     *
     * @return the multiplicity flag.
     */
    private boolean getMultiplicity()
    {
        MCSLogger.trace();

        Boolean b = (Boolean) _constraints.get("multiple");

        return b.booleanValue();
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
            //int distanceId = starList.getColumnIdByName("mult???dist");

            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
                rowId++;
            }
        }
    }
}
/*___oOo___*/
