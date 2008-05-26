/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VisibilityFilter.java,v 1.9 2008-05-26 16:01:49 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import fr.jmmc.mcs.log.*;

import java.lang.Math;

import java.util.Vector;


/**
 * Visibiliy filter.
 */
public class VisibilityFilter extends Filter
{
    /** Store the visibility constraint name */
    private String _visibilityColumnName = "vis2";

    /** Store the visibility constraint name */
    private String _visibilityConstraintName = "vis2";

    /**
     * Default constructor.
     */
    public VisibilityFilter()
    {
        super();

        setConstraint(_visibilityConstraintName, new Double(0.5));
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

        return "Reject Visiblity below :";
    }

    /**
     * Return the maximum visibility accuracy allowed by this filter.
     *
     * @return the visibility accuracy allowed by this filter.
     */
    private double getAllowedVisibiliy()
    {
        MCSLogger.trace();

        Double d = (Double) getConstraintByName(_visibilityConstraintName);

        return d.doubleValue();
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
            if (vis2Cell.getDoubleValue() < getAllowedVisibiliy())
            {
                MCSLogger.debug("vis2 < " + getAllowedVisibiliy() +
                    " - Line removed.");

                // This row should be removed
                return true;
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
