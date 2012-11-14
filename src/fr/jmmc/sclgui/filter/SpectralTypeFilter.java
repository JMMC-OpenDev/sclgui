/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.jmal.ALX;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  SpectralTypeFilter filter.
 */
public final class SpectralTypeFilter extends Filter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SpectralTypeFilter.class.getName());
    /* members */
    /* filter execution variables */
    /** the 'SpType' column ID */
    private int _rawSpectralTypeID = -1;

    /**
     * Default constructor.
     */
    public SpectralTypeFilter() {
        super();

        setConstraint("O", Boolean.FALSE);
        setConstraint("B", Boolean.FALSE);
        setConstraint("A", Boolean.FALSE);
        setConstraint("F", Boolean.FALSE);
        setConstraint("G", Boolean.FALSE);
        setConstraint("K", Boolean.FALSE);
        setConstraint("M", Boolean.FALSE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject Spectral Types (and unknowns) :";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the ID of the column contaning 'SpType' star property
        _rawSpectralTypeID = starList.getColumnIdByName(StarList.SpTypeColumnName);
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
        // If the desired column name exists
        if (_rawSpectralTypeID != -1) {
            // Get the spectral type from the row
            StarProperty cell = row.get(_rawSpectralTypeID);

            // If spectral type was found in the current line
            if (cell.hasValue()) {
                String rawSpectralType = cell.getStringValue();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("rawSpectralType = '" + rawSpectralType + "'.");
                }

                // Get back the spectral types found in the given spectral type
                final List<String> foundSpectralTypes = ALX.spectralTypes(rawSpectralType);

                if (_logger.isDebugEnabled()) {
                    _logger.debug("foundSpectralTypes = '" + foundSpectralTypes + "'.");
                }

                // For each spectral type found
                for (String spectralTypeName : foundSpectralTypes) {
                    // Get the spectral type check box boolean state
                    Boolean spectralTypeCheckBoxState = (Boolean) getConstraintByName(spectralTypeName);

                    if (_logger.isDebugEnabled()) {
                        _logger.debug("spectralTypeName = '" + spectralTypeName + "'.");
                        _logger.debug("spectralTypeCheckBoxState = '" + spectralTypeCheckBoxState + "'.");
                    }

                    // If the current spectral type is not handled (eg R, N ,S, ...)
                    if (spectralTypeCheckBoxState == null) {
                        _logger.debug("spType not handled -> skipped.");

                        // Skip it
                        continue;
                    }

                    // If the current spectral type checkbox is checked
                    if (spectralTypeCheckBoxState.booleanValue()) {
                        _logger.debug("Line removed.\n");

                        // This line must be removed
                        return true;
                    }
                }
            } else // If the spectral type is undefined
            {
                _logger.debug("Undefined Spectral Type.\n");

                // This line must be removed
                return true;
            }
        } else {
            _logger.warn("Unknown Spectral Type Column Name = '{}'.", StarList.SpTypeColumnName);
        }

        _logger.debug("Line kept.\n");

        // Otherwise the current star row from the star list should be kept
        return false;
    }
}
/*___oOo___*/
