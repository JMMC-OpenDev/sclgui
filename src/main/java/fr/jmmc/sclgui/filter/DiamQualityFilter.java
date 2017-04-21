/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;

/**
 * Diameter quality filter.
 */
public final class DiamQualityFilter extends Filter {

    /** Store the diam_chi2 constraint name */
    private final static String _chi2ConstraintName = "Maximum chi square";
    /** Store the e_LDD_rel constraint name */
    private final static String _relDiamErrorConstraintName = "Maximum relative error (%)";

    /* members */
 /* filter execution variables */
    /** the 'diam_chi2' column ID */
    private int _chi2Id = -1;
    /** the 'e_LDD_rel' column ID */
    private int _relErrLddId = -1;
    /** Store the max allowed chi2 */
    private double _maxChi2;
    /** Store the allowed relative error (%) */
    private double _maxRelDiamErr;

    /**
     * Default constructor.
     */
    public DiamQualityFilter() {
        super();

        _maxChi2 = new Double(2.0);
        _maxRelDiamErr = new Double(5.0);

        setConstraint(_chi2ConstraintName, _maxChi2);
        setConstraint(_relDiamErrorConstraintName, _maxRelDiamErr);
    }

    /**
     * Reset the filter
     */
    @Override
    public void reset() {
        setEnabled(Boolean.TRUE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Diameter quality :";
    }

    /**
     * Retrieve the user defined constaint values.
     */
    private void retrieveDeltas() {
        _maxChi2 = (Double) getConstraintByName(_chi2ConstraintName);
        _maxRelDiamErr = (Double) getConstraintByName(_relDiamErrorConstraintName);
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the IDs of the columns containing 'diam_chi2' & 'e_LDD_rel' star properties
        _chi2Id = starList.getColumnIdByName("diam_chi2");
        _relErrLddId = starList.getColumnIdByName("e_LDD_rel");

        // Get filter values
        retrieveDeltas();
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    @Override
    public boolean shouldRemoveRow(final List<StarProperty> row) {
        // Note: do not reject stars if chi2 or relative diam error columns are undefined or missing

        // If the desired column name exists
        if (_chi2Id != -1) {
            // Get the cell of the desired column
            final StarProperty cell = row.get(_chi2Id);

            if (cell.hasValue()) {
                // If the chi2 is higher than max
                if (cell.getDoubleValue() > _maxChi2) {
                    // This row should be removed
                    return true;
                }
            }
        }
        if (_relErrLddId != -1) {
            // Get the cell of the desired column
            final StarProperty cell = row.get(_relErrLddId);

            if (cell.hasValue()) {
                // If the relatve error is higher than max
                if (cell.getDoubleValue() > _maxRelDiamErr) {
                    // This row should be removed
                    return true;
                }
            }
        }
        // Otherwise the current star should be kept
        return false;
    }
}
/*___oOo___*/
