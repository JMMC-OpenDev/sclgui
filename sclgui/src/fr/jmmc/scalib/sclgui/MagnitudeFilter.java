/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MagnitudeFilter.java,v 1.9 2010-10-10 22:45:03 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2010/10/10 22:21:04  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.7  2008/09/10 22:27:12  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.6  2007/08/02 15:35:51  lafrasse
 * Streamlined GUI and enfored protection against missing data.
 *
 * Revision 1.5  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.4  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.3  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.2  2006/08/03 14:47:29  lafrasse
 * Jalopyzation
 *
 * Revision 1.1  2006/07/19 16:22:55  lafrasse
 * Creation
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.util.Vector;
import java.util.logging.*;

/**
 * Magnitude filter.
 */
public class MagnitudeFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.MagnitudeFilter");
    /** Store the magnitude constraint name */
    private final String _magnitudeConstraintName = "Magnitude";
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel;

    /**
     * Default constructor.
     *
     * @param queryModel the query parameters (for magnitude band)
     */
    public MagnitudeFilter(QueryModel queryModel) {
        super();

        _queryModel = queryModel;

        //setConstraint(_magnitudeConstraintName, new Double(0.0));
        // @TODO : remove the demo values
        setConstraint(_magnitudeConstraintName, new Double(1.5));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName() {
        _logger.entering("MagnitudeFilter", "getName");

        return "Reject stars with magnitude above :";
    }

    /**
     * Get the user defined maximum magnitude.
     */
    private double getAllowedMagnitude() {
        _logger.entering("MagnitudeFilter", "getAllowedMagnitude");

        Double magnitude = (Double) getConstraintByName(_magnitudeConstraintName);

        return magnitude.doubleValue();
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
        _logger.entering("MagnitudeFilter", "shouldRemoveRow");

        // Get the query magnitude band
        String magnitudeBand = _queryModel.getInstrumentalMagnitudeBand();

        // Get the id of the column contaning the good magnitude
        int magnitudeId = starList.getColumnIdByName(magnitudeBand);

        // If the desired column name exists
        if (magnitudeId != -1) {
            // Get the cell of the desired column
            StarProperty cell = ((StarProperty) row.elementAt(magnitudeId));

            // Only test and eventualy remove if the cell has a value
            if (cell.hasValue() == true) {
                double currentMagnitude = cell.getDoubleValue();

                // if the magnitude is greater than the allowed one
                if (currentMagnitude > getAllowedMagnitude()) {
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
