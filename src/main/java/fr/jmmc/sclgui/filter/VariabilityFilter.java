/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;

/**
 * Variability filter.
 */
public final class VariabilityFilter extends Filter {

    /** Set columns name whose rows should be removed if cell is not empty */
    private static final String[] VarFlagColumns = new String[]{StarList.VarFlag1ColumnName, StarList.VarFlag2ColumnName};
    /* members */
    /* filter execution variables */
    /** the VarFlag1 and VarFlag2 column Ids */
    private final int[] _varFlagIds = new int[]{-1, -1};
    /** the VarFlag3 column Id */
    private int _varFlag3Id = -1;

    /**
     * Default constructor.
     */
    public VariabilityFilter() {
        super();
    }

    /**
     * Reset the filter
     * @param bright true if bright mode; false otherwise
     */
    @Override
    public void reset(final boolean bright) {
        // no-op
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject Variability";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        int i = 0;
        for (String columnName : VarFlagColumns) {
            // Get the id of the current column name
            _varFlagIds[i++] = starList.getColumnIdByName(columnName);
        }

        // Get the ID of the column containing 'varFlag3' star property
        _varFlag3Id = starList.getColumnIdByName(StarList.VarFlag3ColumnName);
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

        for (int varFlagID : _varFlagIds) {
            // If the desired column name exists
            if (varFlagID != -1) {
                // Get the cell of the desired column
                cell = row.get(varFlagID);

                // If a variability flag was found
                if (cell.hasValue()) {
                    // This row should be removed
                    return true;
                }
            }
        }

        // If the desired column name exists
        if (_varFlag3Id != -1) {
            // Get the cell of the desired column
            cell = row.get(_varFlag3Id);

            // If "variability3" flag was found in the current line
            if (cell.hasValue()) {
                // If "variability3" value is not "C"
                final String varFlag3Flag = cell.getString().trim();

                if (!varFlag3Flag.equalsIgnoreCase("C")) {
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
