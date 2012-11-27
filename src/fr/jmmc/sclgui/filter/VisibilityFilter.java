/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visibility filter.
 */
public final class VisibilityFilter extends Filter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(VisibilityFilter.class.getName());
    /** Store the visibility constraint name */
    private static final String _visibilityConstraintName = "vis2";
    /* members */
    /* filter execution variables */
    /** the 'visibility' column ID */
    private int _vis2Id = -1;
    /** maximum visibility allowed */
    private double _allowedVis = 0d;

    /**
     * Default constructor.
     */
    public VisibilityFilter() {
        super();

        setConstraint(_visibilityConstraintName, new Double(0.5));
        setEnabled(Boolean.TRUE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject Visiblity below :";
    }

    /**
     * Return the maximum visibility allowed by this filter.
     *
     * @return the visibility allowed by this filter.
     */
    private double getAllowedVisibility() {
        Double d = (Double) getConstraintByName(_visibilityConstraintName);

        return d.doubleValue();
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the ID of the column containing 'visibility' star property
        _vis2Id = starList.getColumnIdByName(StarList.Vis2ColumnName);

        // Get the maximum visibility accuracy allowed
        _allowedVis = getAllowedVisibility();
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
        // If the desired column names exists
        if (_vis2Id != -1) {
            // Get the cell of the desired column
            final StarProperty cell = row.get(_vis2Id);

            // If the visibility is undefined
            if (!cell.hasValue()) {
                _logger.debug("No vis2 - Line removed.");

                // This row should be removed
                return true;
            }

            // If the visibility is less than 0.5
            if (cell.getDoubleValue() < _allowedVis) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("vis2 < " + _allowedVis + " - Line removed.");
                }

                // This row should be removed
                return true;
            }
        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
