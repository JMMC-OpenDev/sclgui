/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import fr.jmmc.sclgui.preference.PreferenceKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic filter.
 */
public abstract class Filter extends Observable {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(Filter.class.getName());
    /* members */
    /** Enabled flag */
    private Boolean _enabledFlag = null;
    /** 'constraint name-constraint value' table */
    private HashMap<String, Object> _constraints = null;
    /** Ordered table of filter constraint names */
    private List<String> _orderedConstraintNames = null;

    /* filter execution variables */
    /** the 'distance' column Id */
    private int _distId = -1;
    /** preferred distance to detect the science object */
    private double _prefDistance = 0d;

    /**
     * Default constructor.
     */
    public Filter() {
        _enabledFlag = false;
        _constraints = new HashMap<String, Object>();
        _orderedConstraintNames = new ArrayList<String>();
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
    public final boolean isEnabled() {
        return _enabledFlag.booleanValue();
    }

    /**
     * Give internal access to the Boolean object representing the filter state.
     *
     * @return a Boolean object at TRUE if the filter is enabled, FALSE
     * otherwise.
     */
    public final Boolean getEnabled() {
        return _enabledFlag;
    }

    /**
     * Set whether the filter is enabled or not.
     *
     * @param enabledFlag if true enable the filter, disable it otherwise.
     */
    public final void setEnabled(final Boolean enabledFlag) {
        // avoid recursive loop:
        if (enabledFlag != _enabledFlag) {
            _enabledFlag = enabledFlag;

            setChanged();
            notifyObservers();
        }
    }

    /**
     * Get the number of filter constraints.
     *
     * @return the number of filter constraint(s).
     */
    public final int getNbOfConstraints() {
        return _orderedConstraintNames.size();
    }

    /**
     * Get the filter constraint name by order.
     *
     * @param index 
     * @return the filter constraint name.
     */
    public final String getConstraintNameByOrder(final int index) {
        return _orderedConstraintNames.get(index);
    }

    /**
     * Get the filter constraint value from its name.
     *
     * @param constraintName 
     * @return the filter constraint value.
     */
    public final Object getConstraintByName(final String constraintName) {
        return _constraints.get(constraintName);
    }

    /**
     * Create/Update a filter constraint with a given value.
     *
     * @param constraintName the name of the constraint to be created/updated.
     * @param constraintValue the value of the constraint to be created/updated.
     */
    public final void setConstraint(final String constraintName, final Object constraintValue) {
        _constraints.put(constraintName, constraintValue);
        _orderedConstraintNames.add(constraintName);

        setChanged();
        notifyObservers();
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    public final void prepare(final StarList starList) {
        // Get the 'distance' column Id
        _distId = starList.getColumnIdByName("dist");

        // Get the preferred distance to detect the science object
        _prefDistance = Preferences.getInstance().getPreferenceAsDouble(PreferenceKey.QUERY_SCIENCE_DETECTION);

        // call child class prepare():
        onPrepare(starList);
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    public void onPrepare(final StarList starList) {
        // to be implemented in child classes
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    public boolean shouldRemoveRow(final List<StarProperty> row) {
        return true;
    }

    /**
     * Return whether the given row is th science object or not.
     *
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row is the science object, false otherwise.
     */
    public final boolean isScienceObject(final List<StarProperty> row) {
        if (_distId != -1) {
            // Get the row's distance star property
            final Double rowDistance = row.get(_distId).getDoubleValue();

            // If the current row distance is close enough to be detected as a science object
            return (rowDistance < _prefDistance);
        }
        return false;
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(final StarList starList) {
        // If the filter is enabled
        if (isEnabled()) {
            final int initialSize = starList.size();

            if (initialSize == 0) {
                return;
            }

            final long start = System.nanoTime();

            // Prepare this filter execution:
            prepare(starList);

            // For each row of the star list
            int size = initialSize;
            int rowId = 0;
            List<StarProperty> row;

            while (rowId < size) {
                row = starList.get(rowId);

                // If the luminosity class was found in the current line
                if (shouldRemoveRow(row) && !isScienceObject(row)) {
                    // Remove the current star row from the star list
                    starList.remove(rowId);
                    size--;
                } else {
                    // Otherwise process the next row
                    rowId++;
                }
            }

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine(getName() + " (" + initialSize + " >> " + starList.size() + " stars) processed in "
                        + 1e-6d * (System.nanoTime() - start) + " ms.");
            }
        }
    }

    /**
     * Serialize a filter as a String for console representation.
     *
     * @return a String representing the filter internal state.
     */
    @Override
    public final String toString() {
        String filter = "Filter '" + getName() + "' ";
        filter += ((isEnabled() ? "enabled" : "disabled") + " :");
        filter += _constraints;
        filter += _orderedConstraintNames;

        return filter;
    }
}
/*___oOo___*/
