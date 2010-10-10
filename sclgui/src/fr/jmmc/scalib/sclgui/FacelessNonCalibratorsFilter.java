/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FacelessNonCalibratorsFilter.java,v 1.8 2010-10-10 22:45:03 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2010/10/10 22:21:04  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.6  2009/11/04 10:12:49  lafrasse
 * Code and documentation cleanup.
 *
 * Revision 1.5  2008/09/10 22:21:27  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.4  2008/05/26 16:01:49  mella
 * Rename VisibilityFilter to VisibilityAccuracyFilter
 * Move hard coded vis2 < 0.5 filtering from FacelessNonCalibratorsFilter to VisibilityFilter
 *
 * Revision 1.3  2008/05/21 15:24:18  lafrasse
 * Added systematic deletion of calibrators with Vis2 < 0.5 .
 *
 * Revision 1.2  2007/08/16 10:46:22  lafrasse
 * Disabled diameter-based removal accorting to informations from Gerard ZINS.
 *
 * Revision 1.1  2007/08/16 09:46:30  lafrasse
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.util.Vector;
import java.util.logging.*;

/**
 * Reject stars that are not calibrators (no visibility).
 */
public class FacelessNonCalibratorsFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.FacelessNonCalibratorsFilter");
    /** Store the visibility column name */
    private String _visibilityColumnName = "vis2";

    /**
     * ...constructor FacelessNonCalibratorsFilter documentation comment...
     */
    public FacelessNonCalibratorsFilter() {
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
    public String getName() {
        _logger.entering("FacelessNonCalibratorsFilter", "getName");

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
    @Override
    public boolean shouldRemoveRow(StarList starList, Vector row) {
        _logger.entering("FacelessNonCalibratorsFilter", "shouldRemoveRow");

        // Get the ID of the column contaning 'visibility' star property
        int vis2Id = starList.getColumnIdByName(_visibilityColumnName);

        // If the desired column names exists
        if (vis2Id != -1) {
            // Get the cell of the desired column
            StarProperty vis2Cell = ((StarProperty) row.elementAt(vis2Id));

            // If the visibility is undefined
            if (vis2Cell.hasValue() == false) {
                _logger.fine("No vis2 - Line removed.");

                // This row should be removed
                return true;
            }
        }

        _logger.fine("Line kept.");

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
