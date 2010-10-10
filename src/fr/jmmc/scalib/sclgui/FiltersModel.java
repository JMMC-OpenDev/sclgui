/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FiltersModel.java,v 1.15 2010-10-10 22:21:04 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.14  2008/09/10 22:24:48  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.13  2008/05/26 16:01:49  mella
 * Rename VisibilityFilter to VisibilityAccuracyFilter
 * Move hard coded vis2 < 0.5 filtering from FacelessNonCalibratorsFilter to VisibilityFilter
 *
 * Revision 1.12  2007/08/16 09:47:32  lafrasse
 * Added support for FacelessNonCalibratorsFilter to hide found stars without
 * visibilty nor diameters.
 *
 * Revision 1.11  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.10  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.9  2006/08/03 14:47:29  lafrasse
 * Jalopyzation
 *
 * Revision 1.8  2006/07/19 16:24:54  lafrasse
 * Added missing filters
 * Reordered according to preliminary review scientists feedback
 *
 * Revision 1.7  2006/07/12 14:16:34  lafrasse
 * Jalopyzation
 *
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
package fr.jmmc.scalib.sclgui;

import java.util.*;
import java.util.logging.*;


/**
 * Filters model that can be adopted by a JTable.
 */
public class FiltersModel
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.FiltersModel");

    /** List of filters to handle */
    private FilterList _filterList = null;

    /** Store each filter view */
    private Vector _filterViews = null;

    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel = null;

    /** Default constructor. */
    public FiltersModel(QueryModel queryModel)
    {
        _queryModel     = queryModel;

        _filterList     = new FilterList();
        _filterList.setEnabled(Boolean.TRUE);
        _filterViews = new Vector();

        // Non Calibrators filter (faceless)
        FacelessNonCalibratorsFilter facelessNonCalibratorsFilter = new FacelessNonCalibratorsFilter();
        _filterList.add(facelessNonCalibratorsFilter);

        // Distance filter
        DistanceFilter distanceFilter = new DistanceFilter(_queryModel);
        _filterList.add(distanceFilter);

        FilterView filterView = new FilterView(distanceFilter);
        _filterViews.add(filterView);

        // Magnitude filter
        MagnitudeFilter magnitudeFilter = new MagnitudeFilter(_queryModel);
        _filterList.add(magnitudeFilter);

        filterView = new FilterView(magnitudeFilter);
        _filterViews.add(filterView);

        // Spectral Type filter
        SpectralTypeFilter spectralTypeFilter = new SpectralTypeFilter();
        _filterList.add(spectralTypeFilter);

        filterView = new FilterView(spectralTypeFilter);
        _filterViews.add(filterView);

        // Luminosity filter
        LuminosityFilter luminosityFilter = new LuminosityFilter();
        _filterList.add(luminosityFilter);

        filterView = new FilterView(luminosityFilter);
        _filterViews.add(filterView);

        // Visibiliy filter
        VisibilityFilter visibiliyFilter = new VisibilityFilter();
        _filterList.add(visibiliyFilter);

        filterView = new FilterView(visibiliyFilter);
        _filterViews.add(filterView);

        // Visibility accuracy filter
        VisibilityAccuracyFilter visibilityAccuracyFilter = new VisibilityAccuracyFilter();
        _filterList.add(visibilityAccuracyFilter);

        filterView = new FilterView(visibilityAccuracyFilter);
        _filterViews.add(filterView);

        // Variability filter
        VariabilityFilter variabilityFilter = new VariabilityFilter();
        _filterList.add(variabilityFilter);

        filterView = new FilterView(variabilityFilter);
        _filterViews.add(filterView);

        // Multiplicity filter
        MultiplicityFilter multiplicityFilter = new MultiplicityFilter();
        _filterList.add(multiplicityFilter);

        filterView = new FilterView(multiplicityFilter);
        _filterViews.add(filterView);
    }

    /**
     * Return the filter view vector.
     *
     * @return the list of filter views.
     */
    public Vector getFilterViewVector()
    {
        _logger.entering("FiltersModel", "getFilterViewVector");

        return _filterViews;
    }

    /**
     * Register an observer for filter list change notification.
     *
     * @param o the observer to register.
     */
    public void addObserver(Observer o)
    {
        _logger.entering("FiltersModel", "addObserver");

        _filterList.addObserver(o);
    }

    /**
     * Apply the filters to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList)
    {
        _logger.entering("FiltersModel", "process");

        _filterList.process(starList);
    }
}
/*___oOo___*/
