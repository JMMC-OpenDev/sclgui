/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VisibilityAccuracyFilter.java,v 1.11 2010-10-10 22:21:04 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2008/09/10 22:41:51  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.9  2008/05/26 16:01:49  mella
 * Rename VisibilityFilter to VisibilityAccuracyFilter
 * Move hard coded vis2 < 0.5 filtering from FacelessNonCalibratorsFilter to VisibilityFilter
 *
 * Revision 1.8  2008/02/13 12:16:50  lafrasse
 * Corrected a bug reported by Myriam BENISTY that prevented calibrator with a NaN accurancy (0.0/0.0) to be properly rejected.
 *
 * Revision 1.7  2007/08/02 15:35:51  lafrasse
 * Streamlined GUI and enfored protection against missing data.
 *
 * Revision 1.6  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.5  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.4  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.3  2006/08/04 14:09:10  lafrasse
 * Added GUI enabling/disabling feature to filters
 *
 * Revision 1.2  2006/08/03 14:47:29  lafrasse
 * Jalopyzation
 *
 * Revision 1.1  2006/07/28 13:14:53  lafrasse
 * Creation
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.util.Vector;
import java.util.logging.*;


/**
 * Visibiliy accuracy filter.
 */
public class VisibilityAccuracyFilter extends Filter
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.VisibilityAccuracyFilter");

    /** Store the visibility constraint name */
    private String _visibilityColumnName = "vis2";

    /** Store the visibility error constraint name */
    private String _visibilityErrorColumnName = "vis2Err";

    /** Store the visibility accuracy constraint name */
    private String _visibilityAccuracyConstraintName = "vis2Err/vis2 (%)";

    /**
     * Default constructor.
     */
    public VisibilityAccuracyFilter()
    {
        super();

        //setConstraint(_visibilityAccuracyConstraintName, new Double(0.0));
        // @TODO : remove the demo value
        setConstraint(_visibilityAccuracyConstraintName, new Double(2.0));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        _logger.entering("VisibilityAccuracyFilter", "getName");

        return "Reject Visibility Accuracy above (or unknown) :";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibiliyAccurancy()
    {
        _logger.entering("VisibilityAccuracyFilter",
            "getAllowedVisibiliyAccurancy");

        Double d = (Double) getConstraintByName(_visibilityAccuracyConstraintName);

        return d.doubleValue() / 100;
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
        _logger.entering("VisibilityAccuracyFilter", "shouldRemoveRow");

        // Get the ID of the column contaning 'visibility' star property
        int vis2Id = starList.getColumnIdByName(_visibilityColumnName);

        // Get the ID of the column contaning 'visibilityErr' star property
        int vis2errId = starList.getColumnIdByName(_visibilityErrorColumnName);

        // If the desired column names exists
        if ((vis2Id != -1) && (vis2errId != -1))
        {
            // Get the cell of the desired column
            StarProperty vis2Cell = ((StarProperty) row.elementAt(vis2Id));

            // Get the cell of the desired column
            StarProperty vis2ErrCell = ((StarProperty) row.elementAt(vis2errId));

            // If the 2 values were found in the current line
            if ((vis2Cell.hasValue() == true) &&
                    (vis2ErrCell.hasValue() == true))
            {
                double currentVis2        = vis2Cell.getDoubleValue();
                double currentVis2err     = vis2ErrCell.getDoubleValue();
                double visibilityAccuracy = Math.abs(currentVis2err / currentVis2);

                // if the visibility is not a number (eg. 0.0/0.0)
                if (Double.isNaN(visibilityAccuracy) == true)
                {
                    // This row should be removed
                    return true;
                }

                // if the visibility value is greater than the allowed one
                if (visibilityAccuracy >= getAllowedVisibiliyAccurancy())
                {
                    // This row should be removed
                    return true;
                }
            }
            else // If any value is missing
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
