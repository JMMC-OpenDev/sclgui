/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MultiplicityFilter.java,v 1.2 2006-04-05 12:41:23 yvander Exp $"
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
/*******
*
*
* VOIR AVEC GUILLAUME !
* On ne prend pas en compte la checkbox multiple puisque si l'on selection multiplicity c'est que l'on interdit la multiplicite OU autorise(si autorise alors inverser le boolean).
*
*
*
*/

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
	//if ((isEnabled() == true) && (getMultiplicity() == true))
        {
            // Get the id of the column contaning 'multiplicity' star property
            int multiplicityId = starList.getColumnIdByName("MultFlag");

            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
	        // Get the multiplicity flag value
		Vector row = ((Vector) starList.elementAt(rowId));
		StarProperty cell = ((StarProperty) row.elementAt(multiplicityId));
		String multiplicityFlag = cell.toString();

		// if the multiplicity flag exist
		if ((multiplicityFlag != null) && (multiplicityFlag.equals("") == false))
		{
		    // if multiplicity is checked
		    /*if (getMultiplicity() == true)
		    {
		        // Remove this row from the star List
		*/	starList.remove(rowId);
		  /*  }
		    else
		    {
		        // Otherwise process the next row
			rowwId++;
		    }*/

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
