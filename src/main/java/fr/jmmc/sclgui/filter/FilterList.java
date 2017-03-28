/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter list.
 */
public final class FilterList extends Filter implements Observer {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(FilterList.class.getName());
    /** Filter list */
    private List<Filter> _filters = null;

    /**
     * Default constructor.
     */
    public FilterList() {
        super();
        _filters = new ArrayList<Filter>(10);
    }

    /**
     * Reset the filter
     */
    @Override
    public void reset() {
        setEnabled(Boolean.FALSE);
        for (Filter filter : _filters) {
            filter.reset();
        }
        setEnabled(Boolean.TRUE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Filter List";
    }

    /**
     * Add a filter to the filter list, and start observing any of its changes.
     *
     * @param filter the filter to be added to the list of filter.
     */
    public void add(Filter filter) {
        _filters.add(filter);
        filter.addObserver(this);
    }

    /**
     * Return the number of filters in the filter list.
     *
     * @return the number of filters in the filter list.
     */
    public int size() {
        return _filters.size();
    }

    /**
     * Return the filter at the given index in the filter list.
     *
     * @param index 
     * @return filter at the given index
     */
    public Filter get(int index) {
        return _filters.get(index);
    }

    /**
     * Apply the filter (if enabled).
     * 
     * It is faster to add n times a star than remove n times (arrayCopy ...)
     *
     * @param starList the list of star to filter.
     * @return filtered star list or given star list if disabled
     */
    @Override
    public StarList process(final StarList starList) {
        final int listSize = starList.size();

        final StarList outputList;

        // If the filter is enabled
        if (isEnabled() && listSize != 0) {
            final long start = System.nanoTime();

            StarList workList = starList;
            StarList inputList;

            // Process each filter of the filter list against the star list:
            for (int filterId = 0, size = _filters.size(); filterId < size; filterId++) {
                inputList = workList;
                workList = _filters.get(filterId).process(inputList);
            }

            outputList = workList;

            _logger.info("{} ({} >> {} stars) processed in {} ms.", getName(), listSize, outputList.size(), 1e-6d * (System.nanoTime() - start));

        } else {
            // return given star list:
            outputList = starList;
        }

        return outputList;
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
        if (isEnabled()) {
            setChanged();
            notifyObservers();
        }
    }
}
/*___oOo___*/
