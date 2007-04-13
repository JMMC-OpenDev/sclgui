/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VariabilityFilter.java,v 1.8 2007-04-13 14:17:40 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2007/03/19 08:54:33  lafrasse
 * Modified to keep lines with 'VarFlag3 = C'.
 *
 * Revision 1.6  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
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
        int          variability1ID   = starList.getColumnIdByName("VarFlag1");
        int          variability2ID   = starList.getColumnIdByName("VarFlag2");
        int          variability3ID   = starList.getColumnIdByName("VarFlag3");

        StarProperty cell             = (StarProperty) row.elementAt(variability1ID);
        String       variability1Flag = (String) cell.getValue();
        cell                          = (StarProperty) row.elementAt(variability2ID);

        String variability2Flag       = (String) cell.getValue();
        cell                          = (StarProperty) row.elementAt(variability3ID);

        String variability3Flag       = (String) cell.getValue();

        // If "variability1" flag was found in the current line
        if ((variability1Flag != null) && (variability1Flag.length() != 0))
        {
            // This row should be removed
            return true;
        }

        // If "variability2" flag was found in the current line
        if ((variability2Flag != null) && (variability2Flag.length() != 0))
        {
            // This row should be removed
            return true;
        }

        // If "variability3" flag was found in the current line, and is not "C"
        if ((variability3Flag != null) && (variability3Flag.length() != 0) &&
                (variability3Flag.trim().equalsIgnoreCase("C") == false))
        {
            // This row should be removed
            return true;
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
