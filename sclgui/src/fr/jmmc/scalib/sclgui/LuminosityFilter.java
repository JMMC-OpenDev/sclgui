/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LuminosityFilter.java,v 1.3 2006-04-06 14:38:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.util.Enumeration;
import java.util.Vector;


/**
 *  LuminosityFilter filter.
 */
public class LuminosityFilter extends Filter
{
    //TODO place a constant for every "luminosity" ;))

    /**
     * Default constructor.
     */
    public LuminosityFilter()
    {
        super();

        _constraints.put("I", new Boolean(true));
        _constraints.put("II", new Boolean(true));
        _constraints.put("III", new Boolean(true));
        _constraints.put("IV", new Boolean(true));
        _constraints.put("V", new Boolean(true));
        _constraints.put("VI", new Boolean(true));
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Luminosity Class";
    }

    /**
     * Return the luminosity classes selected.
     *
     * @return lumClass a vector of luminosity class.
     */
    private Vector getAllowedLuminosity()
    {
        MCSLogger.trace();

        Vector lumClass = new Vector();

        // Checkboxes values with elements (true or false) and keys (indexes)
        Enumeration elements = _constraints.elements();
        Enumeration keys     = _constraints.keys();

        while (elements.hasMoreElements())
        {
            // Next data
            boolean bool          = ((Boolean) elements.nextElement()).booleanValue();
            String  lumClassValue = (String) keys.nextElement();

            // Checkbox selected
            if (bool == true)
            {
                // Add in Vector
                lumClass.add(lumClassValue);
            }
        }

        // Return a vector
        return lumClass;
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList)
    {
        MCSLogger.trace();

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

                // To determine if the end of luminosity class in spectral type
                boolean endOfLum = false;

                // The spectral type exist
                if ((spectralTypeValue != null) &&
                        (spectralTypeValue.equals("") == false))
                {
                    // Meter of character
                    int beginIdx = 0;
                    int endIdx   = 0;

                    // Loop until the beginning of luminosity class
                    while ((beginIdx < spectralTypeValue.length()) &&
                            (spectralTypeValue.charAt(beginIdx) != 'I') &&
                            (spectralTypeValue.charAt(beginIdx) != 'V'))
                    {
                        beginIdx++;
                    }

                    //endIdx >= beginIdx
                    endIdx = beginIdx;

                    // Loop until the end of luminosity class
                    while ((endIdx < spectralTypeValue.length()) &&
                            (endOfLum != true))
                    {
                        //
                        if ((spectralTypeValue.charAt(endIdx) == 'I') ||
                                (spectralTypeValue.charAt(endIdx) == 'V'))
                        {
                            //
                            endIdx++;
                        }
                        else
                        {
                            //
                            endOfLum = true;
                        }
                    }

                    // Get the luminosity classes selected
                    Vector lumClassSelected = getAllowedLuminosity();

                    // If luminosity classes are checked
                    if (lumClassSelected.size() > 0)
                    {
                        // Find or not a luminosity class corresponding to the selection
                        boolean find = false;

                        // For each luminosity class checked
                        for (int i = 0; i < lumClassSelected.size(); i++)
                        {
                            // The value of spectral type checked
                            String valueSelected  = (String) lumClassSelected.elementAt(i);
                            String starLuminosity = spectralTypeValue.substring(beginIdx,
                                    endIdx);

                            // Luminosity class are identicals
                            if (starLuminosity.equals(valueSelected) == true)
                            {
                                // Do not remove
                                find = true;
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
                        // There is not luminosity class in spectral type
                        if (beginIdx == spectralTypeValue.length())
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
