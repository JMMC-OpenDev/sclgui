/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FiltersModel.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.Observer;
import java.util.Vector;

import javax.swing.table.*;


/**
 * Filters model that can be adopted by a JTable.
 */
public class FiltersModel
{
    /** List of filters to handle */
    private FilterList _filterList;

    /** Store each filter view */
    private Vector _filterViews;

    /**
     * Default constructor.
     */
    public FiltersModel()
    {
        _filterList = new FilterList();
        _filterList.setEnabled(Boolean.TRUE);
        _filterViews = new Vector();

        DistanceFilter distanceFilter1 = new DistanceFilter();
        distanceFilter1.setConstraint("dist", "0.5");
        distanceFilter1.setEnabled(Boolean.FALSE);

        FilterView     distanceFilterView1 = new FilterView(distanceFilter1);

        DistanceFilter distanceFilter2     = new DistanceFilter();
        distanceFilter2.setConstraint("dist", "10");
        distanceFilter2.setEnabled(Boolean.TRUE);

        FilterView         distanceFilterView2 = new FilterView(distanceFilter2);

        MultiplicityFilter multiplicityFilter  = new MultiplicityFilter();
        multiplicityFilter.setEnabled(Boolean.TRUE);

        FilterView multiplicityFilterView = new FilterView(multiplicityFilter);

        _filterList.add(distanceFilter1);
        _filterList.add(distanceFilter2);
        _filterList.add(multiplicityFilter);

        _filterViews.add(distanceFilterView1);
        _filterViews.add(distanceFilterView2);
        _filterViews.add(multiplicityFilterView);
    }

    /**
     * Return the filter view vector.
     *
     * @return the list of filter views.
     */
    public Vector getFilterViewVector()
    {
        MCSLogger.trace();

        return _filterViews;
    }

    /**
     * Register an observer for filter list change notification.
     *
     * @param o the observer to register.
     */
    public void addObserver(Observer o)
    {
        MCSLogger.trace();

        _filterList.addObserver(o);
    }

    /**
     * Apply the filters to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList)
    {
        MCSLogger.trace();

        _filterList.process(starList);
    }
}
/*___oOo___*/
