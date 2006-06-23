/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FiltersModel.java,v 1.5 2006-06-23 09:19:41 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/04/07 13:51:13  yvander
 * conflict
 *
 * Revision 1.3  2006/04/06 14:38:12  lafrasse
 * Removed redondant distance filter (was here for test purpose only)
 *
 * Revision 1.2  2006/04/05 12:45:34  yvander
 * Ajout du type spectral et de la luminosite
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
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

        DistanceFilter distanceFilter = new DistanceFilter();
        distanceFilter.setConstraint("dist", "5");
        distanceFilter.setEnabled(Boolean.TRUE);

        FilterView         distanceFilterView = new FilterView(distanceFilter);

        MultiplicityFilter multiplicityFilter = new MultiplicityFilter();
        multiplicityFilter.setEnabled(Boolean.TRUE);

        FilterView         multiplicityFilterView = new FilterView(multiplicityFilter);

        SpectralTypeFilter spectralTypeFilter     = new SpectralTypeFilter();
        spectralTypeFilter.setEnabled(Boolean.FALSE);

        FilterView       spectralTypeFilterView = new FilterView(spectralTypeFilter);

        LuminosityFilter luminosityFilter       = new LuminosityFilter();
        luminosityFilter.setEnabled(Boolean.FALSE);

        FilterView        luminosityFilterView = new FilterView(luminosityFilter);

        VariabilityFilter variabilityFilter    = new VariabilityFilter();
        variabilityFilter.setEnabled(Boolean.FALSE);

        FilterView variabilityFilterView = new FilterView(variabilityFilter);

        _filterList.add(distanceFilter);
        _filterList.add(multiplicityFilter);
        _filterList.add(spectralTypeFilter);
        _filterList.add(luminosityFilter);
        _filterList.add(variabilityFilter);

        _filterViews.add(distanceFilterView);
        _filterViews.add(multiplicityFilterView);
        _filterViews.add(spectralTypeFilterView);
        _filterViews.add(luminosityFilterView);
        _filterViews.add(variabilityFilterView);
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
