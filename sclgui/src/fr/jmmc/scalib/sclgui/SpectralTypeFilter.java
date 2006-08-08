/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SpectralTypeFilter.java,v 1.5 2006-08-08 16:14:23 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

        return "Reject selected Spectral Types";
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

            int rowId          = 0;

            // For each row of the star list
            while (rowId < starList.size())
            {
                // Get the spectral type value of the star
                Vector       row        = ((Vector) starList.elementAt(rowId));
                StarProperty cell       = ((StarProperty) row.elementAt(spectralTypeId));
                String       cellString = cell.toString();

                // If the spectral type cell is not empty
                if ((cellString != null) && (cellString.equals("") == false))
                {
                    // If the cell contains one or more rejected spectral types
                    if (cellString.contains(_rejectedSpectralTypes) == true)
                    {
                        // Remove the corresponding star from the list
                        starList.remove(rowId);
                    }
                    else
                    {
                        // Otherwise process the next row
                        rowId++;
                    }
                }
                else // The cell is empty
                {
                    // Process the next row
                    rowId++;
                }
            }
        }
    }
}
/*___oOo___*/
