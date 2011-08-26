/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.jmal.ALX;

import java.util.*;
import java.util.logging.*;

/**
 *  LuminosityFilter filter.
 */
public class LuminosityFilter extends Filter {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.LuminosityFilter");
    /** Store the spectral type column name */
    private String _spTypeColumnName = "SpType";

    /**
     * Default constructor.
     */
    public LuminosityFilter() {
        super();

        setConstraint("I", false);
        setConstraint("II", false);
        setConstraint("III", false);
        setConstraint("IV", false);
        setConstraint("V", false);
        setConstraint("VI", false);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName() {
        _logger.entering("LuminosityFilter", "getName");

        return "Reject Luminosity Classes (and unknowns) :";
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
        _logger.entering("LuminosityFilter", "shouldRemoveRow");

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

        // Get the ID of the column contaning 'SpType' star property
        int rawSpectralTypeID = starList.getColumnIdByName(_spTypeColumnName);

        // If the desired column name exists
        if (rawSpectralTypeID != -1) {
            // Get the corresponding cell the given row
            StarProperty cell = (StarProperty) row.elementAt(rawSpectralTypeID);

            // If spectral type was found in the current line
            if (cell.hasValue() == true) {
                // Extract the spectral type from the cell
                String rawSpectralType = cell.getStringValue();

                // Get back the luminosity classes found in the given spectral type
                final List<String> luminosityClasses = ALX.luminosityClasses(rawSpectralType);

                // For each found luminosity class
                for (int i = 0; i < luminosityClasses.size(); i++) {
                    // Get the luminosity class check box boolean state
                    String luminosityClassName = luminosityClasses.get(i);
                    Boolean luminosityClassState = (Boolean) getConstraintByName(luminosityClassName);

                    // If the current luminosity class must be kept
                    if (luminosityClassState == false) {
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
