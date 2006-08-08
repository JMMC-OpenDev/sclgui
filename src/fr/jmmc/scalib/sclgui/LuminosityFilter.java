/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LuminosityFilter.java,v 1.5 2006-08-08 16:14:23 lafrasse Exp $"
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

        return "Reject selected Luminosity Classes";
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
            // TODO : implement (see SpectralTypeFilter.java for an example)
        }
    }
}
/*___oOo___*/
