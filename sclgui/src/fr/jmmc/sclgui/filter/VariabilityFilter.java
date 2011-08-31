/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Variability filter.
 */
public class VariabilityFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(VariabilityFilter.class.getName());
    /** Store the variability flag 1 column name */
    private String _varFlag1ColumnName = "VarFlag1";
    /** Store the variability flag 2 column name */
    private String _varFlag2ColumnName = "VarFlag2";
    /** Store the variability flag 3 column name */
    private String _varFlag3ColumnName = "VarFlag3";

    /**
     * Default constructor.
     */
    public VariabilityFilter() {
        super();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("VariabilityFilter", "getName");

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
    @Override
    public boolean shouldRemoveRow(StarList starList, Vector row) {
        _logger.entering("VariabilityFilter", "shouldRemoveRow");

        List<String> columns = new ArrayList<String>();
        int varFlagID = -1;
        StarProperty cell = null;

        // Set columns name whose rows should be removed if cell is not empty
        columns.add(_varFlag1ColumnName);
        columns.add(_varFlag2ColumnName);

        for (String columnName : columns) {
            // Get the ID of the column contaning 'varFlag1' star property
            varFlagID = starList.getColumnIdByName(columnName);

            // If the desired column name exists
            if (varFlagID != -1) {
                // Get the cell of the desired column
                cell = (StarProperty) row.elementAt(varFlagID);

                // If a variability flag was found
                if (cell.hasValue() == true) {
                    // This row should be removed
                    return true;
                }
            }
        }

        // Get the ID of the column contaning 'varFlag3' star property
        varFlagID = starList.getColumnIdByName(_varFlag3ColumnName);

        // If the desired column name exists
        if (varFlagID != -1) {
            // Get the cell of the desired column
            cell = (StarProperty) row.elementAt(varFlagID);

            // If "variability3" flag was found in the current line
            if (cell.hasValue() == true) {
                // If "variability3" value is not "C"
                String varFlag3Flag = cell.getStringValue();
                varFlag3Flag = varFlag3Flag.trim();

                if (varFlag3Flag.equalsIgnoreCase("C") == false) {
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
