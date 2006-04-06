/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MultiplicityFilter.java,v 1.3 2006-04-06 13:27:27 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/04/05 12:41:23  yvander
 * Voir commentaire dans le fichier
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Vector;


/**
 * Multiplicity filter.
 */
public class MultiplicityFilter extends Filter
{
    /**
     * Default constructor.
     */
    public MultiplicityFilter()
    {
        super();
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
            // Get the id of the column contaning 'multiplicity' star property
            int multiplicityId = starList.getColumnIdByName("MultFlag");

            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
                // Get the multiplicity flag value
                Vector       row              = ((Vector) starList.elementAt(rowId));
                StarProperty cell             = ((StarProperty) row.elementAt(multiplicityId));
                String       multiplicityFlag = cell.toString();

                // if the multiplicity flag exist
                if ((multiplicityFlag != null) &&
                        (multiplicityFlag.equals("") == false))
                {
                    // Remove current line
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
