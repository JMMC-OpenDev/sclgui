/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VariabilityFilter.java,v 1.10 2008-05-26 16:11:18 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2007/08/02 15:35:51  lafrasse
 * Streamlined GUI and enfored protection against missing data.
 *
 * Revision 1.8  2007/04/13 14:17:40  lafrasse
 * Modified to also remove lines with VarFlag1 and/or VarFlag2 not empty.
 *
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
    /** Store the variability flag 1 column name */
    private String _varFlag1ColumnName = "VarFlag1";

    /** Store the variability flag 2 column name */
    private String _varFlag2ColumnName = "VarFlag2";

    /** Store the variability flag 3 column name */
    private String _varFlag3ColumnName = "VarFlag3";

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

        return "Reject Variability";
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

        int          varFlagID = -1;
        StarProperty cell      = null;

        // Get the ID of the column contaning 'varFlag1' star property
        varFlagID = starList.getColumnIdByName(_varFlag1ColumnName);

        // If the desired column name exists
        if (varFlagID != -1)
        {
            // Get the cell of the desired column
            cell = (StarProperty) row.elementAt(varFlagID);

            // If "variability1" flag was found in the current line
            if (cell.hasValue() == true)
            {
                // This row should be removed
                return true;
            }
        }

        // Get the ID of the column contaning 'varFlag2' star property
        varFlagID = starList.getColumnIdByName(_varFlag2ColumnName);

        // If the desired column name exists
        if (varFlagID != -1)
        {
            // Get the cell of the desired column
            cell = (StarProperty) row.elementAt(varFlagID);

            // If "variability1" flag was found in the current line
            if (cell.hasValue() == true)
            {
                // This row should be removed
                return true;
            }
        }

        // Get the ID of the column contaning 'varFlag3' star property
        varFlagID = starList.getColumnIdByName(_varFlag3ColumnName);

        // If the desired column name exists
        if (varFlagID != -1)
        {
            // Get the cell of the desired column
            cell = (StarProperty) row.elementAt(varFlagID);

            // If "variability3" flag was found in the current line
            if (cell.hasValue() == true)
            {
                // If "variability3" value is not "C"
                String varFlag3Flag = cell.getStringValue();
                varFlag3Flag = varFlag3Flag.trim();

                if (varFlag3Flag.equalsIgnoreCase("C") == false)
                {
                    // This row should be removed
                    return true;
                }
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
