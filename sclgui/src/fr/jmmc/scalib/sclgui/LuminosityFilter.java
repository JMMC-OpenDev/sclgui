/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LuminosityFilter.java,v 1.6 2006-11-08 22:25:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
public class LuminosityFilter extends Filter implements Observer
{
    //TODO place a constant for every "luminosity" ;))

    /** String containing all the luminosty class names to be rejected */
    String _rejectedLuminosityClasses = new String();

    /**
     * Default constructor.
     */
    public LuminosityFilter()
    {
        super();

        addObserver(this);

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
     * Update luminosity checboxes according to constraint changes.
     *
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        // For each spectral type constraint
        for (int i = 0; i < getNbOfConstraints(); i++)
        {
            // Get the spectral type name
            String luminosityClassName = getConstraintNameByOrder(i);

            // Get the spectral type state (checked or not)
            boolean luminosityClassState = ((Boolean) getConstraintByName(luminosityClassName)).booleanValue();

            // If the spectral type must be rejected (is checked)
            if (luminosityClassState == true)
            {
                // Add the its name in the rejected spectral type string
                _rejectedLuminosityClasses += luminosityClassName;
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

        // Get the corresponding frow the given row
        StarProperty cell = (StarProperty) row.elementAt(spectralTypeID);

        // Extract the spectral type from the cell
        String spectralType = (String) cell.getValue();

        // Extract the luminosity class of the given spectral type
        String extractedLuminosityClass = "";

        for (int i = 0; i < spectralType.length(); i++)
        {
            char c = spectralType.charAt(i);

            // If the luminosity class has been reached
            // eg. the part of a spectral type composed of I & V (roman number)
            if ((c == 'I') || (c == 'V'))
            {
                // Re-copy its content for later use
                extractedLuminosityClass = extractedLuminosityClass + c;
            }
        }

        // If no luminosity classes were found
        if (extractedLuminosityClass.length() == 0)
        {
            // The star should be removed
            return true;
        }

        // For each luminosity constraints
        for (int i = 0; i < getNbOfConstraints(); i++)
        {
            // Get the luminosity class name and boolean state
            String  luminosityClassName  = getConstraintNameByOrder(i);
            boolean luminosityClassState = ((Boolean) getConstraintByName(luminosityClassName)).booleanValue();

            // If the luminosity class must be kept
            if (luminosityClassState == true)
            {
                // if the luminosity class matches the extracted one
                if (luminosityClassName.compareTo(extractedLuminosityClass) == 0)
                {
                    // This line must be kept
                    return false;
                }
            }
        }

        // Otherwise reject the line
        return true;
    }
}
/*___oOo___*/
