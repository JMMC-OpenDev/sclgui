/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.jmal.ALX;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  SpectralTypeFilter filter.
 */
public class SpectralTypeFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(SpectralTypeFilter.class.getName());
    /** Store the spectral type column name */
    private String _spTypeColumnName = "SpType";

    /**
     * Default constructor.
     */
    public SpectralTypeFilter() {
        super();

        setConstraint("O", false);
        setConstraint("B", false);
        setConstraint("A", false);
        setConstraint("F", false);
        setConstraint("G", false);
        setConstraint("K", false);
        setConstraint("M", false);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        _logger.entering("SpectralTypeFilter", "getName");

        return "Reject Spectral Types (and unknowns) :";
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
        // Get the ID of the column contaning 'SpType' star property
        int rawSpectralTypeID = starList.getColumnIdByName(_spTypeColumnName);

        // If the desired column name exists
        if (rawSpectralTypeID != -1) {
            // Get the spectral type from the row
            StarProperty cell = (StarProperty) row.elementAt(rawSpectralTypeID);

            // If spectral type was found in the current line
            if (cell.hasValue() == true) {
                String rawSpectralType = (String) cell.getValue();
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("rawSpectralType = '" + rawSpectralType + "'.");
                }

                // Get back the spectral types found in the given spectral type
                List<String> foundSpectralTypes = ALX.spectralTypes(rawSpectralType);

                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("foundSpectralTypes = '" + foundSpectralTypes + "'.");
                }

                // For each spectral type found
                for (int i = 0; i < foundSpectralTypes.size(); i++) {
                    // Get the spectral type check box boolean state
                    String spectralTypeName = foundSpectralTypes.get(i);
                    Boolean spectralTypeCheckBoxState = (Boolean) getConstraintByName(spectralTypeName);

                    if (_logger.isLoggable(Level.FINE)) {
                        _logger.fine("spectralTypeName = '" + spectralTypeName + "'.");
                        _logger.fine("spectralTypeCheckBoxState = '" + spectralTypeCheckBoxState + "'.");
                    }

                    // If the current spectral type is not handled (eg R, N ,S, ...)
                    if (spectralTypeCheckBoxState == null) {
                        _logger.fine("spType not handled -> skipped.");

                        // Skip it
                        continue;
                    }

                    // If the current spectral type checkbox is checked
                    if (spectralTypeCheckBoxState == true) {
                        _logger.fine("Line removed.\n");

                        // This line must be removed
                        return true;
                    }
                }
            } else // If the spectral type is undefined
            {
                _logger.fine("Undefined Spectral Type.\n");

                // This line must be removed
                return true;
            }
        } else {
            _logger.warning("Unknown Spectral Type Column Name = '" + _spTypeColumnName + "'.");
        }

        _logger.fine("Line kept.\n");

        // Otherwise the current star row from the star list should be kept
        return false;
    }
}
/*___oOo___*/
