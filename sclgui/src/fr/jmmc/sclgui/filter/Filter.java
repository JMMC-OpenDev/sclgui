/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import fr.jmmc.sclgui.preference.PreferenceKey;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Generic filter.
 */
public abstract class Filter extends Observable {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(Filter.class.getName());
    /** Enabled flag */
    private Boolean _enabledFlag = null;
    /** 'constraint name-constraint value' table */
    private HashMap<String, Object> _constraints = null;
    /** Ordered table of filter constraint names */
    private Vector _orderedConstraintNames = null;

    /**
     * Default constructor.
     */
    public Filter() {
        _enabledFlag = false;
        _constraints = new HashMap<String, Object>();
        _orderedConstraintNames = new Vector();
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
    public boolean isEnabled() {
        _logger.entering("Filter", "isEnabled");

        return _enabledFlag.booleanValue();
    }

    /**
     * Give internal access to the Boolean object representing the filter state.
     *
     * @return a Boolean object at TRUE if the filter is enabled, FALSE
     * otherwise.
     */
    public Boolean getEnabled() {
        _logger.entering("Filter", "getEnabled");

        return _enabledFlag;
    }

    /**
     * Set whether the filter is enabled or not.
     *
     * @param enabledFlag if true enable the filter, disable it otherwise.
     */
    public void setEnabled(Boolean enabledFlag) {
        _logger.entering("Filter", "setEnabled");

        _enabledFlag = enabledFlag;

        setChanged();
        notifyObservers();
    }

    /**
     * Get the number of filter constraints.
     *
     * @return the number of filter constraint(s).
     */
    public int getNbOfConstraints() {
        _logger.entering("Filter", "getNbOfConstraints");

        return _orderedConstraintNames.size();
    }

    /**
     * Get the filter constraint name by order.
     *
     * @param index 
     * @return the filter constraint name.
     */
    public String getConstraintNameByOrder(int index) {
        _logger.entering("Filter", "getConstraintNameByOrder");

        return (String) _orderedConstraintNames.elementAt(index);
    }

    /**
     * Get the filter constraint value from its name.
     *
     * @param constraintName 
     * @return the filter constraint value.
     */
    public Object getConstraintByName(String constraintName) {
        _logger.entering("Filter", "getConstraintByName");

        return _constraints.get(constraintName);
    }

    /**
     * Create/Update a filter constraint with a given value.
     *
     * @param constraintName the name of the constraint to be created/updated.
     * @param constraintValue the value of the constraint to be created/updated.
     */
    public void setConstraint(String constraintName, Object constraintValue) {
        _logger.entering("Filter", "setConstraint");

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
    public boolean shouldRemoveRow(StarList starList, Vector row) {
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
    public boolean isScienceObject(StarList starList, Vector row) {
        boolean isScienceObject = false;

        // Get the 'distance' column Id
        int distId = starList.getColumnIdByName("dist");

        // Get the row's distance star property
        StarProperty distanceProperty = (StarProperty) row.elementAt(distId);
        Double rowDistance = distanceProperty.getDoubleValue();

        // Get the prefered distance to detect the science object
        Preferences preferences = Preferences.getInstance();
        Double prefDistance = preferences.getPreferenceAsDouble(
                PreferenceKey.QUERY_SCIENCE_DETECTION);

        // If the current row distance is close enough to be detected as a science object
        if (rowDistance < prefDistance) {
            isScienceObject = true;
        }

        return isScienceObject;
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList) {
        _logger.entering("Filter", "process");

        // If the filter is enabled
        if ((isEnabled() == true)) {
            // For each row of the star list
            int rowId = 0;

            while (rowId < starList.size()) {
                Vector row = ((Vector) starList.elementAt(rowId));

                // If the luminosity class was found in the current line
                if ((shouldRemoveRow(starList, row) == true)
                        && (isScienceObject(starList, row) == false)) {
                    // Remove the current star row from the star list
                    starList.remove(rowId);
                } else {
                    // Otherwise process the next row
                    rowId++;
                }
            }
        }
    }

    /**
     * Serialize a filter as a String for console representation.
     *
     * @return a String representing the filter internal state.
     */
    @Override
    public String toString() {
        _logger.entering("Filter", "toString");

        String filter = "Filter '" + getName() + "' ";
        filter += (((isEnabled() == true) ? "enabled" : "disabled") + " :");
        filter += _constraints;
        filter += _orderedConstraintNames;

        return filter;
    }
}
/*___oOo___*/
