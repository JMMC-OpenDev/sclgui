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
 *  LuminosityFilter filter.
 */
public final class LuminosityFilter extends Filter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(LuminosityFilter.class.getName());
    /* members */
    /* filter execution variables */
    /** the 'SpType' column ID */
    private int _rawSpectralTypeID = -1;

    /**
     * Default constructor.
     */
    public LuminosityFilter() {
        super();

        setConstraint("I", Boolean.FALSE);
        setConstraint("II", Boolean.FALSE);
        setConstraint("III", Boolean.FALSE);
        setConstraint("IV", Boolean.FALSE);
        setConstraint("V", Boolean.FALSE);
        setConstraint("VI", Boolean.FALSE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject Luminosity Classes (and unknowns) :";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the ID of the column containing 'SpType' star property
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
        /*
         // DO NOT REMOVE !!! - Validity test code
         String[] rawSpectralTypes = {
         "-", "A0", "A0Ia", "A0Ib", "A0IV", "A0V", "A1III/IV", "A1V",
         "A2", "A2m", "A3", "A3IV", "A5", "A5V", "A7IV", "A7IV-V",
         "A8/A9V", "A8Vn", "A9V", "A9V...", "Am...", "Ap...", "B0IV...",
         "B1.5V", "B2", "B2III", "B2:IIIpshev", "B3IIIe", "B5", "B5III",
         "B5V", "B6III", "B7/B8V", "B8III", "B8V", "B8Vn", "B9",
         "B9.5IV:", "B9IIIMNp...", "B9IV", "F0", "F0IV...", "F2II/III",
         "F3Ia", "F5", "F5V", "F8", "G0", "G0Ib", "G0III...", "G0V",
         "G3Ib", "G3V", "G4Ibp...", "G5", "G5II...", "G5III", "G5IV",
         "G6/G8III", "G7III", "G8III", "G8IV/V", "K", "K0", "K0III",
         "K0IV", "K1Iabv", "K1III", "K1III/IV", "K1IIIvar", "K1/K2III",
         "K2", "K2III", "K2IIIvar", "K2IV", "K2/K3III", "K3Ib", "K3III",
         "K3IIvar", "K4III", "K4/K5III", "K5", "K5II", "K5III",
         "K5/M0III", "K7", "M0", "M0III", "M0I-M4Ia", "M1", "M1III",
         "M1IIIb", "M2Iabpe", "M3", "M3III", "M3/M4III", "M4.5IIIa",
         "M4III", "M4III:", "M5III", "M5/M6IV", "M6", "M6e-M7", "M6III",
         "M7III", "M8III:e", "Ma", "Mb", "Mc", "Md", "O", "O...", "O7"
         };
         for (int x = 0; x < rawSpectralTypes.length; x++) {
         String rawSpectralType = rawSpectralTypes[x];
         Vector luminosityClasses = luminosityClasses(rawSpectralType);
         System.out.println("rawSpectralTypes[" + x + "] = '" + rawSpectralType
         + "' ->");
         int luminosityClassesSize = luminosityClasses.size();
         for (int y = 0; y < luminosityClassesSize; y++) {
         System.out.println("\tluminosityClasses[" + (y + 1) + "/"
         + luminosityClassesSize + "] = '"
         + (String) luminosityClasses.elementAt(y) + "'");
         }
         System.out.println();
         }
         */

        // If the desired column name exists
        if (_rawSpectralTypeID != -1) {
            // Get the corresponding cell the given row
            final StarProperty cell = row.get(_rawSpectralTypeID);

            // If spectral type was found in the current line
            if (cell.hasValue()) {
                // Extract the spectral type from the cell
                final String rawSpectralType = cell.getStringValue();

                // Get back the luminosity classes found in the given spectral type
                final List<String> luminosityClasses = ALX.luminosityClasses(rawSpectralType);

                // For each found luminosity class
                for (String luminosityClassName : luminosityClasses) {
                    // Get the luminosity class check box boolean state
                    Boolean luminosityClassState = (Boolean) getConstraintByName(luminosityClassName);

                    // If the current luminosity class must be kept
                    if (!luminosityClassState.booleanValue()) {
                        // This line must be kept
                        return false;
                    }
                }
            }
        }

        // Otherwise reject the line
        return true;
    }
}
/*___oOo___*/
