/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SpectralTypeFilter.java,v 1.7 2006-11-08 22:25:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.lang.Character;

import java.util.*;


/**
 *  SpectralTypeFilter filter.
 */
public class SpectralTypeFilter extends Filter implements Observer
{
    //TODO place a constant for every "spectral type" ;))

    /** String containing all the spectral type names to be rejected */
    String _rejectedSpectralTypes = new String();

    /**
     * Default constructor.
     */
    public SpectralTypeFilter()
    {
        super();

        addObserver(this);

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
        MCSLogger.trace();

        return "Reject Spectral Types other than :";
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        // For each spectral type constraint
        for (int i = 0; i < getNbOfConstraints(); i++)
        {
            // Get the spectral type name
            String spectralTypeName = getConstraintNameByOrder(i);

            // Get the spectral type state (checked or not)
            boolean spectralTypeState = ((Boolean) getConstraintByName(spectralTypeName)).booleanValue();

            // If the spectral type must be rejected (is checked)
            if (spectralTypeState == true)
            {
                // Add the its name in the rejected spectral type string
                _rejectedSpectralTypes += spectralTypeName;
            }
        }
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

        // Get the ID of the column contaning 'SpType' star property
        int spectralTypeID = starList.getColumnIdByName("SpType");

        // Get the spectral type from the row
        StarProperty cell         = (StarProperty) row.elementAt(spectralTypeID);
        String       spectralType = (String) cell.getValue();

        // Extract the alphabetic part of the spectral type
        String extractedSpectralType = "";

        for (int i = 0; i < spectralType.length(); i++)
        {
            char c = spectralType.charAt(i);

            // If the luminosity class has been reached
            if ((c == 'I') || (c == 'V'))
            {
                // Escaping the current loop
                break;
            }

            // If the spectral type has been reached
            // eg. the first alphabetic part of a spectral type
            if (Character.isLetter(c) == true)
            {
                // Re-copy its content for later use
                extractedSpectralType = extractedSpectralType + c;
            }
        }

        // If no spectral type were found
        if (extractedSpectralType.length() == 0)
        {
            // The star should be removed
            return true;
        }

        // For each spectral type constraints
        for (int i = 0; i < getNbOfConstraints(); i++)
        {
            // Get the spectral type name and boolean state
            String  spectralTypeClassName  = getConstraintNameByOrder(i);
            boolean spectralTypeClassState = ((Boolean) getConstraintByName(spectralTypeClassName)).booleanValue();

            // If the spectral type must be kept
            if (spectralTypeClassState == true)
            {
                // if the spectral type matches the extracted one
                if (spectralTypeClassName.compareTo(extractedSpectralType) == 0)
                {
                    // The current star should be kept
                    return false;
                }
            }
        }

        // Otherwise the current star row from the star list should be removed
        return true;
    }
}
/*___oOo___*/
