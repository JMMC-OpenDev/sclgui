/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SpectralTypeFilter.java,v 1.11 2007-04-13 13:26:28 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.9  2006/11/14 14:44:56  lafrasse
 * Moved all astronomical-related computations in the new jmmc.mcs.astro package.
 *
 * Revision 1.8  2006/11/09 16:03:41  lafrasse
 * Added "Multiple spectral types in on spectral type" support and test code.
 * Removed unnecessary Observer code.
 *
 * Revision 1.7  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.6  2006/08/22 14:50:51  mella
 * Complete API for setter functions to accept Double or double params
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
 * Revision 1.1  2006/04/05 12:44:08  yvander
 * type spectral devpt
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.astro.*;
import fr.jmmc.mcs.log.*;

import java.lang.Character;

import java.util.*;


/**
 *  SpectralTypeFilter filter.
 */
public class SpectralTypeFilter extends Filter
{
    /**
     * Default constructor.
     */
    public SpectralTypeFilter()
    {
        super();

        setConstraint("O", new Boolean(true));
        setConstraint("B", new Boolean(true));
        setConstraint("A", new Boolean(true));
        setConstraint("F", new Boolean(true));
        setConstraint("G", new Boolean(true));
        setConstraint("K", new Boolean(true));
        setConstraint("M", new Boolean(true));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject Spectral Types other than :";
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
               String rawSpectralType    = rawSpectralTypes[x];
               Vector foundSpectralTypes = spectralTypes(rawSpectralType);
               System.out.println("spectralTypes[" + x + "] = '" +
                   rawSpectralType + "' ->");
               int foundSpectralTypesSize = foundSpectralTypes.size();
               for (int y = 0; y < foundSpectralTypesSize; y++)
               {
                   System.out.println("\tfoundSpectralTypes[" + (y + 1) + "/" +
                       foundSpectralTypesSize + "] = '" +
                       (String) foundSpectralTypes.elementAt(y) + "'");
               }
               System.out.println();
           }
         */

        // Get the ID of the column contaning 'SpType' star property
        int rawSpectralTypeID = starList.getColumnIdByName("SpType");

        // Get the spectral type from the row
        StarProperty cell            = (StarProperty) row.elementAt(rawSpectralTypeID);
        String       rawSpectralType = (String) cell.getValue();

        // Get back the spectral types found in the given spectral type
        Vector spectralTypes = ALX.spectralTypes(rawSpectralType);

        // For each found spectral type
        for (int i = 0; i < spectralTypes.size(); i++)
        {
            // Get the spectral type check box boolean state
            String  spectralTypeName  = (String) spectralTypes.elementAt(i);
            boolean spectralTypeState = ((Boolean) getConstraintByName(spectralTypeName)).booleanValue();

            // If the current spectral type must be kept
            if (spectralTypeState == true)
            {
                // This line must be kept
                return false;
            }
        }

        // Otherwise the current star row from the star list should be removed
        return true;
    }
}
/*___oOo___*/
