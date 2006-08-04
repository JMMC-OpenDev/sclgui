/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SpectralTypeFilter.java,v 1.4 2006-08-04 14:09:10 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.util.Enumeration;
import java.util.Vector;


/**
 *  SpectralTypeFilter filter.
 */
public class SpectralTypeFilter extends Filter
{
    //TODO place a constant for every "spectral type" ;))

    /**
     * Default constructor.
     */
    public SpectralTypeFilter()
    {
        super();

        _constraints.put("O", new Boolean(false));
        _constraints.put("B", new Boolean(false));
        _constraints.put("A", new Boolean(false));
        _constraints.put("F", new Boolean(false));
        _constraints.put("G", new Boolean(false));
        _constraints.put("K", new Boolean(false));
        _constraints.put("M", new Boolean(false));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Reject selected Spectral Types";
    }

    /**
     * Return the spectrals types selected.
     *
     * @return spType a vector of spectral type.
     */
    private Vector getAllowedSpectralType()
    {
        MCSLogger.trace();

        Vector spType = new Vector();

        // Checkboxes values with elements(true or false) and keys( indexes)
        Enumeration elements = _constraints.elements();
        Enumeration keys     = _constraints.keys();

        while (elements.hasMoreElements())
        {
            // Next data
            boolean bool        = ((Boolean) elements.nextElement()).booleanValue();
            String  spTypeValue = (String) keys.nextElement();

            // Checkbox selected
            if (bool == true)
            {
                // Add in Vector
                spType.add(spTypeValue);
            }
        }

        // Return a vector
        return spType;
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList)
    {
        MCSLogger.trace();

        // Get the spectral types selected
        Vector spTypeSelected = getAllowedSpectralType();

        // If the filter is enabled
        if ((isEnabled() == true))
        {
            // Get the id of the column contaning 'SpType' star property
            int spectralTypeId = starList.getColumnIdByName("SpType");

            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
                // Get the spectral type value of the star
                Vector       row               = ((Vector) starList.elementAt(rowId));
                StarProperty cell              = ((StarProperty) row.elementAt(spectralTypeId));
                String       spectralTypeValue = cell.toString();

                // Find or not a spectral type corresponding to the selection
                boolean find = false;

                // The spectral type exist
                if ((spectralTypeValue != null) &&
                        (spectralTypeValue.equals("") == false))
                {
                    // If something is checked
                    if (spTypeSelected.size() > 0)
                    {
                        for (int i = 0; i < spTypeSelected.size(); i++)
                        {
                            // The value of spectral type checked
                            String valueSelected = (String) spTypeSelected.elementAt(i);

                            // Meter of character
                            int spcharIdx = 0;

                            // Loop to verify data
                            while ((spcharIdx < spectralTypeValue.length()) &&
                                    (spectralTypeValue.charAt(spcharIdx) != 'I') &&
                                    (spectralTypeValue.charAt(spcharIdx) != 'V') &&
                                    (find == false))
                            {
                                // Check if
                                if (spectralTypeValue.charAt(spcharIdx) == valueSelected.charAt(
                                            0))
                                {
                                    find = true;
                                }

                                spcharIdx++;
                            }
                        }

                        // Data Not found
                        if (find == false)
                        {
                            // Remove star to the starList
                            starList.remove(rowId);
                        }
                        else
                        {
                            // Otherwise process the next row
                            rowId++;
                        }
                    }
                    else
                    {
                        // The spectral type contains only the luminosity class
                        if ((spectralTypeValue.charAt(0) == 'I') ||
                                (spectralTypeValue.charAt(0) == 'V') ||
                                Character.isDigit(spectralTypeValue.charAt(0)))
                        {
                            // Remove star to the starList
                            starList.remove(rowId);
                        }
                        else
                        {
                            // Otherwise process the next row
                            rowId++;
                        }
                    }
                }

                // The spectral type does not exist
                else
                {
                    // Remove star to the starList
                    starList.remove(rowId);

                    /* OR
                       // Otherwise process the next row
                       rowId++;
                     */
                }
            }
        }
    }
}
/*___oOo___*/
