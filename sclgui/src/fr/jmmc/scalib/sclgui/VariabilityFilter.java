/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VariabilityFilter.java,v 1.6 2007-02-13 13:58:44 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.4  2006/07/18 14:52:49  lafrasse
 * Changed name and behavior according to preliminary review scientists feedback
 *
 * Revision 1.3  2006/06/30 07:58:22  lafrasse
 * Jalopyzation
 *
 * Revision 1.2  2006/06/23 09:19:41  mella
 * Jalopization
 *
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
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.*;

import java.util.Vector;


/**
 * Variability filter.
 */
public class VariabilityFilter extends Filter
{
    /**
     * Default constructor.
     */
    public VariabilityFilter()
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

        return "Reject Variability :";
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

        // Get the ID of the column contaning 'variability3' star property
        int          variability3ID  = starList.getColumnIdByName("VarFlag3");

        StarProperty cell            = (StarProperty) row.elementAt(variability3ID);
        String       variabilityFlag = (String) cell.getValue();

        // If the "variability3" flag was found in the current line
        if ((variabilityFlag != null) && (variabilityFlag.length() != 0))
        {
            // This row should be removed
            return true;
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
