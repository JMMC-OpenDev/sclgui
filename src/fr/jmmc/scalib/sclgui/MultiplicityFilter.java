/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MultiplicityFilter.java,v 1.12 2009-04-22 15:17:06 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2008/09/10 22:29:48  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.10  2008/05/26 16:11:18  lafrasse
 * Removed un-needed ":" from filter name.
 *
 * Revision 1.9  2007/08/02 15:35:51  lafrasse
 * Streamlined GUI and enfored protection against missing data.
 *
 * Revision 1.8  2007/02/13 14:15:30  lafrasse
 * Corrected package and import pathes.
 *
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

import java.util.Vector;
import java.util.logging.*;


/**
 * Multiplicity filter.
 */
public class MultiplicityFilter extends Filter
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.MultiplicityFilter");

    /** Store the multiplicity flag column name */
    private String _multFlagColumnName = "MultFlag";

    /** Store the variability flag 1 column name */
    private String _sbc9ColumnName = "SBC9";

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
        _logger.entering("MultiplicityFilter", "getName");

        setEnabled(true);

        return "Reject Multiplicity";
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
        _logger.entering("MultiplicityFilter", "shouldRemoveRow");

        StarProperty cell = null;

        // Get the id of the column contaning 'multiplicity' star property
        int multiplicityId = starList.getColumnIdByName(_multFlagColumnName);

        // If the desired column name exists
        if (multiplicityId != -1)
        {
            // Get the cell of the desired column
            cell = ((StarProperty) row.elementAt(multiplicityId));

            // if the multiplicity flag exist
            if (cell.hasValue() == true)
            {
                // This row should be removed
                return true;
            }
        }

        // Get the id of the column contaning 'SBC9' star property
        int sbc9Id = starList.getColumnIdByName(_sbc9ColumnName);

        // If the desired column name exists
        if (sbc9Id != -1)
        {
            // Get the cell of the desired column
            cell = ((StarProperty) row.elementAt(sbc9Id));

            // if the multiplicity flag exist
            if (cell.hasValue() == true)
            {
                // This row should be removed
                return true;
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
