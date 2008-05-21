/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FacelessNonCalibratorsFilter.java,v 1.3 2008-05-21 15:24:18 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 10:46:22  lafrasse
 * Disabled diameter-based removal accorting to informations from Gerard ZINS.
 *
 * Revision 1.1  2007/08/16 09:46:30  lafrasse
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.*;

import java.util.Vector;


/**
 * Reject stars that are not calibrators (no visibility or visibility < 0.5, no diameters).
 */
public class FacelessNonCalibratorsFilter extends Filter
{
    /** Store the visibility column name */
    private String _visibilityColumnName = "vis2";

    /** Store the BV diameter column name */
    private String _diamBVColumnName = "diam_bv";

    /** Store the VR diameter column name */
    private String _diamVRColumnName = "diam_vr";

    /** Store the VK diameter column name */
    private String _diamVKColumnName = "diam_vk";

    /**
     * ...constructor FacelessNonCalibratorsFilter documentation comment...
     */
    public FacelessNonCalibratorsFilter()
    {
        super();

        // This faceless filter should always be activated
        // (set once here as no GUI can change it anywhere else)
        setEnabled(true);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Faceless Non-Calibrators Filter";
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

        // Get the ID of the column contaning 'visibility' star property
        int vis2Id = starList.getColumnIdByName(_visibilityColumnName);

        // If the desired column names exists
        if (vis2Id != -1)
        {
            // Get the cell of the desired column
            StarProperty vis2Cell = ((StarProperty) row.elementAt(vis2Id));

            // If the visibility is undefined
            if (vis2Cell.hasValue() == false)
            {
                MCSLogger.debug("No vis2 - Line removed.");

                // This row should be removed
                return true;
            }

            // If the visibility is less than 0.5
            if (vis2Cell.getDoubleValue() < 0.5)
            {
                MCSLogger.debug("vis2 < 0.5 - Line removed.");

                // This row should be removed
                return true;
            }
        }

        // Disabled as there seems to be (according to Gerard ZINS) some meaning
        // to star with visibility even without displayed diameters.
        /*
           // Get the ID of the column contaning 'diam_bv' star property
           int diamBVId = starList.getColumnIdByName(_diamBVColumnName);
           // Get the ID of the column contaning 'diam_vr' star property
           int diamVRId = starList.getColumnIdByName(_diamVRColumnName);
           // Get the ID of the column contaning 'diam_vk' star property
           int diamVKId = starList.getColumnIdByName(_diamVKColumnName);
           // If the desired column names exists
           if ((diamBVId != -1) && (diamVRId != -1) && (diamVKId != -1))
           {
               // Get the cell of the 'diam_bv' column
               StarProperty diamBVCell = ((StarProperty) row.elementAt(diamBVId));
               // Get the cell of the 'diam_vr' column
               StarProperty diamVRCell = ((StarProperty) row.elementAt(diamVRId));
               // Get the cell of the 'diam_vk' column
               StarProperty diamVKCell = ((StarProperty) row.elementAt(diamVKId));
               // If the all diameter are undefined
               if ((diamBVCell.hasValue() == false) &&
                       (diamVRCell.hasValue() == false) &&
                       (diamVKCell.hasValue() == false))
               {
                   MCSLogger.debug("No diam_xx defined - Line removed.");
                   // This row should be removed
                   return true;
               }
           }
         */
        MCSLogger.debug("Line kept.");

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
