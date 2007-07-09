/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Filter.java,v 1.6 2007-07-09 12:53:37 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.4  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.3  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.2  2006/07/19 16:26:21  lafrasse
 * Generalized setConstraint code
 * Updated documentation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.MCSLogger;

import java.util.*;


/**
 * Generic filter.
 */
public abstract class Filter extends Observable
{
    /** Enabled flag */
    private Boolean _enabledFlag;

    /** 'constraint name-constraint value' table */
    private Hashtable _constraints = new Hashtable();

    /** Ordered table of filter constraint names */
    private Vector _orderedConstraintNames = new Vector();

    /**
     * Default constructor.
     */
    public Filter()
    {
        _enabledFlag = new Boolean(false);
    }

    /**
     * Must return the filter name.
     *
     * @return the name of the filter as a String object.
     */
    public abstract String getName();

    /**
     * Return whether the filter is enabled or not.
     *
     * @return true if the filter is enabled, false otherwise.
     */
    public boolean isEnabled()
    {
        MCSLogger.trace();

        return _enabledFlag.booleanValue();
    }

    /**
     * Give internal access to the Boolean object representing the filter state.
     *
     * @return a Boolean object at TRUE if the filter is enabled, FALSE
     * otherwise.
     */
    public Boolean getEnabled()
    {
        MCSLogger.trace();

        return _enabledFlag;
    }

    /**
     * Set whether the filter is enabled or not.
     *
     * @param enabledFlag if true enable the filter, disable it otherwise.
     */
    public void setEnabled(Boolean enabledFlag)
    {
        MCSLogger.trace();

        _enabledFlag = enabledFlag;

        setChanged();
        notifyObservers();
    }

    /**
     * Get the number of filter constraints.
     *
     * @return the number of filter constraint(s).
     */
    public int getNbOfConstraints()
    {
        MCSLogger.trace();

        return _orderedConstraintNames.size();
    }

    /**
     * Get the filter constraint name by order.
     *
     * @return the filter constraint name.
     */
    public String getConstraintNameByOrder(int index)
    {
        MCSLogger.trace();

        return (String) _orderedConstraintNames.elementAt(index);
    }

    /**
     * Get the filter constraint value from its name.
     *
     * @return the filter constraint value.
     */
    public Object getConstraintByName(String constraintName)
    {
        MCSLogger.trace();

        return _constraints.get(constraintName);
    }

    /**
     * Create/Update a filter constraint with a given value.
     *
     * @param constraintName the name of the constraint to be created/updated.
     * @param constraintValue the value of the constraint to be created/updated.
     */
    public void setConstraint(String constraintName, Object constraintValue)
    {
        MCSLogger.trace();

        _constraints.put(constraintName, constraintValue);
        _orderedConstraintNames.add(constraintName);

        setChanged();
        notifyObservers();
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
        return true;
    }

    /**
     * Return whether the given row is th science object or not.
     *
     * @param starList the list of stars from which the row must be tested.
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row is the science object, false otherwise.
     */
    public boolean isScienceObject(StarList starList, Vector row)
    {
        boolean isScienceObject = false;

        // Get the 'distance' column Id
        int distId = starList.getColumnIdByName("dist");

        // Get the row's distance star property
        StarProperty distanceProperty = (StarProperty) row.elementAt(distId);
        Double       rowDistance      = distanceProperty.getDoubleValue();

        // Get the prefered distance to detect the science object
        Preferences preferences  = Preferences.getInstance();
        Double      prefDistance = preferences.getPreferenceAsDouble(
                "query.scienceObjectDetectionDistance");

        // If the current row distance is close enough to be detected as a science object
        if (rowDistance < prefDistance)
        {
            isScienceObject = true;
        }

        return isScienceObject;
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
            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size())
            {
                Vector row = ((Vector) starList.elementAt(rowId));

                // If the luminosity class was found in the current line
                if ((shouldRemoveRow(starList, row) == true) &&
                        (isScienceObject(starList, row) == false))
                {
                    // Remove the current star row from the star list
                    starList.remove(rowId);
                }
                else
                {
                    // Otherwise process the next row
                    rowId++;
                }
            }
        }
    }

    /**
     * Serialze a filter as a String for console representation.
     *
     * @return a String representing the filter internal state.
     */
    public String toString()
    {
        String filter = "Filter '" + getName() + "' ";
        filter += (((isEnabled() == true) ? "enabled" : "disabled") + " :");
        filter += _constraints;
        filter += _orderedConstraintNames;

        return filter;
    }
}
/*___oOo___*/
