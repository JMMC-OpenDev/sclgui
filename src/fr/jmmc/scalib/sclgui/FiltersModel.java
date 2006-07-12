/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FiltersModel.java,v 1.7 2006-07-12 14:16:34 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2006/07/03 13:33:07  lafrasse
 * Modified default status of MaxDistance and Multiplicity filter from enabled to default (disabled)
 *
 * Revision 1.5  2006/06/23 09:19:41  mella
 * Jalopization
 *
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
        _filterList.add(distanceFilter);

        FilterView distanceFilterView = new FilterView(distanceFilter);
        _filterViews.add(distanceFilterView);

        MultiplicityFilter multiplicityFilter = new MultiplicityFilter();
        _filterList.add(multiplicityFilter);

        FilterView multiplicityFilterView = new FilterView(multiplicityFilter);
        _filterViews.add(multiplicityFilterView);

        SpectralTypeFilter spectralTypeFilter = new SpectralTypeFilter();
        _filterList.add(spectralTypeFilter);

        FilterView spectralTypeFilterView = new FilterView(spectralTypeFilter);
        _filterViews.add(spectralTypeFilterView);

        LuminosityFilter luminosityFilter = new LuminosityFilter();
        _filterList.add(luminosityFilter);

        FilterView luminosityFilterView = new FilterView(luminosityFilter);
        _filterViews.add(luminosityFilterView);

        VariabilityFilter variabilityFilter = new VariabilityFilter();
        _filterList.add(variabilityFilter);

        FilterView variabilityFilterView = new FilterView(variabilityFilter);
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
