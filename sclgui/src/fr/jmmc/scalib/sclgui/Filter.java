/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Filter.java,v 1.2 2006-07-19 16:26:21 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Hashtable;
import java.util.Observable;


/**
 * Generic filter.
 */
public abstract class Filter extends Observable
{
    /** Enabled flag */
    private Boolean _enabledFlag;

    /** 'key-value' table */
    protected Hashtable _constraints;

    /**
     * Default constructor.
     */
    public Filter()
    {
        _enabledFlag     = new Boolean(false);
        _constraints     = new Hashtable();
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
     * Get the filter constraints 'key-value' table.
     *
     * @return the filter constraint(s).
     */
    public Hashtable getConstraints()
    {
        MCSLogger.trace();

        return _constraints;
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

        return filter;
    }
}
/*___oOo___*/
