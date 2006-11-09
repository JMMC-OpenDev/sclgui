/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LuminosityFilter.java,v 1.8 2006-11-09 15:39:35 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/11/09 14:02:40  lafrasse
 * Added "Multiple luminosity classes in on spectral type" support and test code.
 *
 * Revision 1.6  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.5  2006/08/08 16:14:23  lafrasse
 * Updated to properly handle widget order
 * Streamlined filter processing code
 *
 * Revision 1.4  2006/08/04 14:09:10  lafrasse
 * Added GUI enabling/disabling feature to filters
 *
 * Revision 1.3  2006/04/06 14:38:58  lafrasse
 * Layout update
 *
 * Revision 1.2  2006/04/06 14:24:54  mella
 * Add default constraints to true
 *
 * Revision 1.1  2006/04/05 12:44:57  yvander
 * luminosite devpt
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.*;


/**
 *  LuminosityFilter filter.
 */
public class LuminosityFilter extends Filter
{
    /**
     * Default constructor.
     */
    public LuminosityFilter()
    {
        super();

        setConstraint("I", new Boolean(false));
        setConstraint("II", new Boolean(false));
        setConstraint("III", new Boolean(false));
        setConstraint("IV", new Boolean(false));
        setConstraint("V", new Boolean(false));
        setConstraint("VI", new Boolean(false));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject Luminosity Classes other than :";
    }

    /**
     * Extract one or more luminosity classes of the given spectral type.
     *
     * @param rawSpectralType the spectral type to analyze.
     *
     * @return a Vector of String containing found luminosity classes (if any).
     */
    public static Vector luminosityClasses(String rawSpectralType)
    {
        MCSLogger.trace();

        Vector  foundLuminosityClasses = new Vector();
        String  foundLuminosityClass   = "";
        boolean luminosityClassFound   = false;

        int     rawSpectralTypeSize    = rawSpectralType.length();

        // Scan every given spectral type characters
        for (int i = 0; i < rawSpectralTypeSize; i++)
        {
            char c = rawSpectralType.charAt(i);

            // If a luminosity class has been reached
            // eg. a part of a spectral type composed of I & V (roman numbers)
            if ((c == 'I') || (c == 'V'))
            {
                // Re-copy its content to build a result string
                foundLuminosityClass     = foundLuminosityClass + c;

                // Mark the discovery
                luminosityClassFound     = true;

                // If we are on the last char of the spectral type
                if (i == (rawSpectralTypeSize - 1))
                {
                    // Store the luminosity class as a result
                    foundLuminosityClasses.add(foundLuminosityClass);
                }
            }
            else
            {
                // if a luminosiy class was just entirely found
                if (luminosityClassFound == true)
                {
                    // Store the luminosity class as a result
                    foundLuminosityClasses.add(foundLuminosityClass);

                    // Reset in case another luminosity class can be found
                    foundLuminosityClass     = "";
                    luminosityClassFound     = false;
                }
            }
        }

        return foundLuminosityClasses;
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param starList the list of stars from which the row may be removed.
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    public boolean shouldRemoveRow(StarList starList, Vector row)
    {
        MCSLogger.trace();

        /*
           // DO NOT REMOVE !!! - Validity test code
           String[] rawSpectralTypes =
               {
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
           for (int x = 0; x < rawSpectralTypes.length; x++)
           {
               String rawSpectralType   = rawSpectralTypes[x];
               Vector luminosityClasses = luminosityClasses(rawSpectralType);
               System.out.println("rawSpectralTypes[" + x + "] = '" + rawSpectralType +
                   "' ->");
               int luminosityClassesSize = luminosityClasses.size();
               for (int y = 0; y < luminosityClassesSize; y++)
               {
                   System.out.println("\tluminosityClasses[" + (y + 1) + "/" +
                       luminosityClassesSize + "] = '" +
                       (String) luminosityClasses.elementAt(y) + "'");
               }
               System.out.println();
           }
         */

        // Get the ID of the column contaning 'SpType' star property
        int rawSpectralTypeID = starList.getColumnIdByName("SpType");

        // Get the corresponding cell the given row
        StarProperty cell = (StarProperty) row.elementAt(rawSpectralTypeID);

        // Extract the spectral type from the cell
        String rawSpectralType = (String) cell.getValue();

        // Get back the luminosity classes found in the given spectral type
        Vector luminosityClasses = luminosityClasses(rawSpectralType);

        // For each found luminosity class
        for (int i = 0; i < luminosityClasses.size(); i++)
        {
            // Get the luminosity class check box boolean state
            String  luminosityClassName  = (String) luminosityClasses.elementAt(i);
            boolean luminosityClassState = ((Boolean) getConstraintByName(luminosityClassName)).booleanValue();

            // If the current luminosity class must be kept
            if (luminosityClassState == true)
            {
                // This line must be kept
                return false;
            }
        }

        // Otherwise reject the line
        return true;
    }
}
/*___oOo___*/
