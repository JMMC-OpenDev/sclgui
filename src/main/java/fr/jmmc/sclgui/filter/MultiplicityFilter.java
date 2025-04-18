/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;

/**
 * Multiplicity filter.
 */
public final class MultiplicityFilter extends Filter {

    /** In arcseconds. Any orbit separation below should reject */
    private static final double _orbitSeparationLimit = 2d;
    /* members */
 /* filter execution variables */
    /** the multiplicity column Ids */
    private final int[] _multiplicityIDs = new int[]{-1, -1, -1};
    /** the orbit separation column Ids */
    private final int[] _orbitSeparationIds = new int[]{-1, -1};
    /** the badcal column Id */
    private final int[] _badcalID = new int[]{-1};
    /** SBC9 or WDS constraint name */
    private static final String SBC9_WDS_CONSTRAINT = "SBC9 & WDS";
    /** badcal constraint name */
    private static final String BADCAL_CONSTRAINT = "Badcal";

    /**
     * Default constructor.
     */
    public MultiplicityFilter() {
        super();
    }

    /**
     * Reset the filter     
     * @param bright true if bright mode; false otherwise
     */
    @Override
    public void reset(final boolean bright) {
        setEnabled(Boolean.TRUE);

        setConstraint(SBC9_WDS_CONSTRAINT, Boolean.TRUE);
        setConstraint(BADCAL_CONSTRAINT, Boolean.TRUE);

    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject Multiplicity";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        int i = 0;
        for (String columnName : StarList.MultiplicityColumnNames) {
            // Get the id of the current column name
            _multiplicityIDs[i++] = starList.getColumnIdByName(columnName);
        }

        i = 0;
        for (String columnName : StarList.OrbitSeparationColumnNames) {
            // Get the id of the current column name
            _orbitSeparationIds[i++] = starList.getColumnIdByName(columnName);
        }

        // Get the badcal column id
        _badcalID[0] = starList.getColumnIdByName(StarList.BadcalColumnName);
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
        StarProperty cell;

        final Boolean sbc9WdsConstraint = (Boolean) getConstraintByName(SBC9_WDS_CONSTRAINT);
        if (sbc9WdsConstraint) {

            for (int columnId : _multiplicityIDs) {
                // If the desired column name exists
                if (columnId != -1) {
                    // Get the cell of the desired column
                    cell = row.get(columnId);

                    // if the multiplicity flag exists
                    if (cell.hasValue()) {
                        // This row should be removed
                        return true;
                    }
                }
            }

            for (int columnId : _orbitSeparationIds) {
                // If the desired column name exists
                if (columnId != -1) {
                    // Get the cell of the desired column
                    cell = row.get(columnId);

                    // if the orbit separation exist
                    if (cell.hasValue()) {
                        // If orbit separation is below limit
                        if (cell.getDoubleValue() < _orbitSeparationLimit) {
                            // This row should be removed
                            return true;
                        }
                    }
                }
            }

        }

        final Boolean badcalConstraint = (Boolean) getConstraintByName(BADCAL_CONSTRAINT);
        if (badcalConstraint) {

            for (int columnId : _badcalID) {
                // If the desired column name exists
                if (columnId != -1) {
                    // Get the cell of the desired column
                    cell = row.get(columnId);

                    // if a badcal id exists
                    if (cell.hasValue()) {
                        // This row should be removed
                        return true;
                    }
                }
            }

        }

        // Otherwise this row should be kept
        return false;
    }
}
/*___oOo___*/
