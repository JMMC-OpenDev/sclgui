/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Filter list.
 */
public final class FilterList extends Filter implements Observer {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(FilterList.class.getName());
    /** Filter list */
    private List<Filter> _filters = null;

    /**
     * Default constructor.
     */
    public FilterList() {
        super();
        _filters = new ArrayList<Filter>();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
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
     * Return the filter at the given index in the filter list.
     *
     * @param index 
     * @return filter at the given index
     */
    public Filter get(int index) {
        _logger.entering("FilterList", "get");

        return _filters.get(index);
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    @Override
    public void process(final StarList starList) {
        // If the filter is enabled
        if (isEnabled()) {
            final int initialSize = starList.size();
            if (initialSize == 0) {
                return;
            }

            final long start = System.nanoTime();

            // Process each filter of the filter list against the star list:
            for (int filterId = 0, size = _filters.size(); filterId < size; filterId++) {
                _filters.get(filterId).process(starList);
            }

            _logger.info(getName() + " (" + initialSize + " >> " + starList.size() + " stars) processed in "
                    + 1e-6d * (System.nanoTime() - start) + " ms.");
        }
    }

    /**
     * @param o 
     * @param arg 
     * @sa java.util.Observer
     *
     * Called on each embedded filter change, and forward this event upward.
     */
    @Override
    public void update(Observable o, Object arg) {
        _logger.entering("FilterList", "update");

        setChanged();
        notifyObservers();
    }
}
/*___oOo___*/
