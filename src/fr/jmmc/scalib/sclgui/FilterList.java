/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.util.*;
import java.util.logging.*;

/**
 * Filter list.
 */
public class FilterList extends Filter implements Observer {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.FilterList");
    /** Filter list */
    private Vector _filters = null;

    /**
     * Default constructor.
     */
    public FilterList() {
        super();
        _filters = new Vector();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName() {
        _logger.entering("FilterList", "getName");

        return "Filter List";
    }

    /**
     * Add a filter to the filter list, and start observing any of its changes.
     *
     * @param filter the filter to be added to the list of filter.
     */
    public void add(Filter filter) {
        _logger.entering("FilterList", "add");

        _filters.add(filter);
        filter.addObserver(this);
    }

    /**
     * Return the number of filters in the filter list.
     *
     * @return the number of filters in the filter list.
     */
    public int size() {
        _logger.entering("FilterList", "size");

        return _filters.size();
    }

    /**
     * Return the number of filters in the filter list.
     *
     * @return the number of filters in the filter list.
     */
    public Filter elementAt(int index) {
        _logger.entering("FilterList", "elementAt");

        return ((Filter) _filters.elementAt(index));
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    @Override
    public void process(StarList starList) {
        _logger.entering("FilterList", "process");

        // If the filter list is enbled
        if (isEnabled() == true) {
            // Process each filter of the filter list against the star list
            for (int filterId = 0; filterId < _filters.size(); filterId++) {
                ((Filter) _filters.get(filterId)).process(starList);
            }
        }
    }

    /**
     * @sa java.util.Observer
     *
     * Called on each embedded filter change, and forward this event upward.
     */
    public void update(Observable o, Object arg) {
        _logger.entering("FilterList", "update");

        setChanged();
        notifyObservers();
    }
}
/*___oOo___*/
