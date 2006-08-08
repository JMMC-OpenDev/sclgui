/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Filter.java,v 1.3 2006-08-08 16:13:21 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/07/19 16:26:21  lafrasse
 * Generalized setConstraint code
 * Updated documentation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

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
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public abstract void process(StarList starList);

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
