/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MultiplicityFilter.java,v 1.8 2007-02-13 14:15:30 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.6  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.5  2006/07/18 14:52:49  lafrasse
 * Changed name and behavior according to preliminary review scientists feedback
 *
 * Revision 1.4  2006/04/06 14:57:15  lafrasse
 * Generalized star property value from String type to Object
 *
 * Revision 1.3  2006/04/06 13:27:27  mella
 * Remove unused Constraint
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

        return "Reject Multiplicity :";
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

        // Get the id of the column contaning 'multiplicity' star property
        int          multiplicityId   = starList.getColumnIdByName("MultFlag");

        StarProperty cell             = ((StarProperty) row.elementAt(multiplicityId));
        String       multiplicityFlag = cell.getStringValue();

        // if the multiplicity flag exist
        if ((multiplicityFlag != null) && (multiplicityFlag.length() != 0))
        {
            // This row should be removed
            return true;
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
