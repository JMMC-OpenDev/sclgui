/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SpectralTypeFilter.java,v 1.16 2009-12-01 14:24:20 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.15  2008/09/10 22:38:06  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.14  2007/08/09 12:23:31  lafrasse
 * Enhanced to remove undefined spectral types.
 *
 * Revision 1.13  2007/08/03 10:35:27  lafrasse
 * Correted a bud found by Gilles DUVERT with 'Uy_Aur', causing the filter to crash when encounting a not handled spectral type (eg R, S, N, ...).
 *
 * Revision 1.12  2007/08/02 15:35:51  lafrasse
 * Streamlined GUI and enfored protection against missing data.
 *
 * Revision 1.11  2007/04/13 13:26:28  lafrasse
 * Changed default CheckBox state to checked.
 *
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
import java.util.logging.*;


/**
 *  SpectralTypeFilter filter.
 */
public class SpectralTypeFilter extends Filter
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.SpectralTypeFilter");

    /** Store the spectral type column name */
    private String _spTypeColumnName = "SpType";

    /**
     * Default constructor.
     */
    public SpectralTypeFilter()
    {
        super();

        setConstraint("O", new Boolean(false));
        setConstraint("B", new Boolean(false));
        setConstraint("A", new Boolean(false));
        setConstraint("F", new Boolean(false));
        setConstraint("G", new Boolean(false));
        setConstraint("K", new Boolean(false));
        setConstraint("M", new Boolean(false));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
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
    public boolean shouldRemoveRow(StarList starList, Vector row)
    {
        _logger.entering("SpectralTypeFilter", "shouldRemoveRow");

        // Get the ID of the column contaning 'SpType' star property
        int rawSpectralTypeID = starList.getColumnIdByName(_spTypeColumnName);

        // If the desired column name exists
        if (rawSpectralTypeID != -1)
        {
            // Get the spectral type from the row
            StarProperty cell = (StarProperty) row.elementAt(rawSpectralTypeID);

            // If spectral type was found in the current line
            if (cell.hasValue() == true)
            {
                String rawSpectralType = (String) cell.getValue();
                _logger.fine("rawSpectralType = '" + rawSpectralType + "'.");

                // Get back the spectral types found in the given spectral type
                Vector foundSpectralTypes = ALX.spectralTypes(rawSpectralType);
                _logger.fine("foundSpectralTypes = '" + foundSpectralTypes +
                    "'.");

                // For each spectral type found
                for (int i = 0; i < foundSpectralTypes.size(); i++)
                {
                    // Get the spectral type check box boolean state
                    String  spectralTypeName          = (String) foundSpectralTypes.elementAt(i);
                    Boolean spectralTypeCheckBoxState = (Boolean) getConstraintByName(spectralTypeName);

                    _logger.fine("spectralTypeName = '" + spectralTypeName +
                        "'.");
                    _logger.fine("spectralTypeCheckBoxState = '" +
                        spectralTypeCheckBoxState + "'.");

                    // If the current spectral type is not handled (eg R, N ,S, ...)
                    if (spectralTypeCheckBoxState == null)
                    {
                        _logger.fine("spType not handled -> skipped.");

                        // Skip it
                        continue;
                    }

                    // If the current spectral type checkbox is checked
                    if (spectralTypeCheckBoxState == true)
                    {
                        _logger.fine("Line removed.\n");

                        // This line must be removed
                        return true;
                    }
                }
            }
            else // If the spectral type is undefined
            {
                _logger.fine("Undefined Spectral Type.\n");

                // This line must be removed
                return true;
            }
        }
        else
        {
            // @TODO : ASSERTION FAILED
            _logger.warning("Unknown Spectral Type Column Name = '" +
                _spTypeColumnName + "'.");
        }

        _logger.fine("Line kept.\n");

        // Otherwise the current star row from the star list should be kept
        return false;
    }
}
/*___oOo___*/
