/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VariabilityFilter.java,v 1.2 2006-06-23 09:19:41 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/04/07 13:32:23  yvander
 * Variability devpt
 *
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


/*******
 *
 *
 * VOIR AVEC GUILLAUME !
 * On ne prend pas en compte la checkbox variable puisque si l'on selection variability c'est que l'on interdit la variabilite OU autorise(si autorise alors inverser le boolean et deplacer le remove).
 *
 *
 *
 */

/**
 * Variability filter.
 */
public class VariabilityFilter extends Filter
{
    //TODO place a constant for every "variable" ;))

    /**
     * Default constructor.
     */
    public VariabilityFilter()
    {
        super();
        _constraints.put("variable", new Boolean(true));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Variability";
    }

    /**
     * Return if or not variability is allowed by this filter.
     *
     * @return the variability flag.
     */
    private boolean getVariability()
    {
        MCSLogger.trace();

        Boolean b = (Boolean) _constraints.get("variable");

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
        if (isEnabled() == true)//if ((isEnabled() == true) && (getVariability() == true))
        {
            // Get the ids of the column contaning 'variability' star property
            int[] variabilityId = new int[3];
            variabilityId[0]     = starList.getColumnIdByName("VarFlag1");
            variabilityId[1]     = starList.getColumnIdByName("VarFlag2");
            variabilityId[2]     = starList.getColumnIdByName("VarFlag3");

            // Search statment
            boolean find = false;

            // For each row of the star list
            int rowId = 0;

            // Index of variability flag
            int i = 0;

            while (rowId < starList.size())
            {
                // Restart search
                find     = false;
                i        = 0;

                // Get the variability flags value
                while ((find != true) && (i < variabilityId.length))
                {
                    Vector       row             = ((Vector) starList.elementAt(rowId));
                    StarProperty cell            = ((StarProperty) row.elementAt(variabilityId[i]));
                    String       variabilityFlag = cell.toString();

                    // if the variability flag exist
                    if ((variabilityFlag != null) &&
                            (variabilityFlag.equals("") == false))
                    {
                        // if variability is checked
                        /*if (getVariability() == true)
                           {
                               // Remove this row from the star List
                         */
                        starList.remove(rowId);

                        // Data found
                        find = true;

                        //  }
                    }

                    // Next variability flag
                    i++;
                }

                // Next line
                if (find == false)
                {
                    // Otherwise process the next row
                    rowId++;
                }
            }
        }
    }
}
/*___oOo___*/
