/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FilterList.java,v 1.2 2006-08-04 14:09:10 lafrasse Exp $"
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

import java.util.*;


/**
 * Filter list.
 */
public class FilterList extends Filter implements Observer
{
    /** Filter list */
    private Vector _filters;

    /**
     * Default constructor.
     */
    public FilterList()
    {
        super();
        _filters = new Vector();
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    public String getName()
    {
        MCSLogger.trace();

        return "Filter List";
    }

    /**
     * Add a filter to the filter list, and start observing any of its changes.
     *
     * @param filter the filter to be added to the list of filter.
     */
    public void add(Filter filter)
    {
        MCSLogger.trace();

        _filters.add(filter);
        filter.addObserver(this);
    }

    /**
     * Return the number of filters in the filter list.
     *
     * @return the number of filters in the filter list.
     */
    public int size()
    {
        MCSLogger.trace();

        return _filters.size();
    }

    /**
     * Return the number of filters in the filter list.
     *
     * @return the number of filters in the filter list.
     */
    public Filter elementAt(int index)
    {
        MCSLogger.trace();

        return ((Filter) _filters.elementAt(index));
    }

    /**
     * Apply the filter (if enabled) to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList)
    {
        MCSLogger.trace();

        // If the filter list is enbled
        if (isEnabled() == true)
        {
            // Process each filter of the filter list against the star list
            for (int filterId = 0; filterId < _filters.size(); filterId++)
            {
                ((Filter) _filters.get(filterId)).process(starList);
            }
        }
    }

    /**
     * @sa java.util.Observer
     *
     * Called on each embedded filter change, and forward this event upward.
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        setChanged();
        notifyObservers();
    }
}
/*___oOo___*/
