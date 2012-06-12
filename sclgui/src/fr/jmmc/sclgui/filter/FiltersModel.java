/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.calibrator.StarList;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Filters model that can be adopted by a JTable.
 */
public class FiltersModel {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(FiltersModel.class.getName());
    /** List of filters to handle */
    private FilterList _filterList = null;
    /** Store each filter view */
    private Vector _filterViews = null;
    /**
     * Store the current query model in order to allow later retrieves of
     * any science object properties if needed (eg DistanceFilter).
     */
    private QueryModel _queryModel = null;

    /** Default constructor.
     * @param queryModel 
     */
    public FiltersModel(QueryModel queryModel) {
        _queryModel = queryModel;

        _filterList = new FilterList();
        _filterList.setEnabled(Boolean.TRUE);
        _filterViews = new Vector();

        // Non Calibrators filter (faceless)
        FacelessNonCalibratorsFilter facelessNonCalibratorsFilter = new FacelessNonCalibratorsFilter();
        _filterList.add(facelessNonCalibratorsFilter);

        // Distance filter
        DistanceFilter distanceFilter = new DistanceFilter(_queryModel);
        _filterList.add(distanceFilter);

        FilterView filterView = new FilterView(distanceFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Magnitude filter
        MagnitudeFilter magnitudeFilter = new MagnitudeFilter(_queryModel);
        _filterList.add(magnitudeFilter);

        filterView = new FilterView(magnitudeFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Spectral Type filter
        SpectralTypeFilter spectralTypeFilter = new SpectralTypeFilter();
        _filterList.add(spectralTypeFilter);

        filterView = new FilterView(spectralTypeFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Luminosity filter
        LuminosityFilter luminosityFilter = new LuminosityFilter();
        _filterList.add(luminosityFilter);

        filterView = new FilterView(luminosityFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Visibiliy filter
        VisibilityFilter visibiliyFilter = new VisibilityFilter();
        _filterList.add(visibiliyFilter);

        filterView = new FilterView(visibiliyFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Visibility accuracy filter
        VisibilityAccuracyFilter visibilityAccuracyFilter = new VisibilityAccuracyFilter();
        _filterList.add(visibilityAccuracyFilter);

        filterView = new FilterView(visibilityAccuracyFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Variability filter
        VariabilityFilter variabilityFilter = new VariabilityFilter();
        _filterList.add(variabilityFilter);

        filterView = new FilterView(variabilityFilter);
        filterView.init();
        _filterViews.add(filterView);

        // Multiplicity filter
        MultiplicityFilter multiplicityFilter = new MultiplicityFilter();
        _filterList.add(multiplicityFilter);

        filterView = new FilterView(multiplicityFilter);
        filterView.init();
        _filterViews.add(filterView);
    }

    /**
     * Return the filter view vector.
     *
     * @return the list of filter views.
     */
    public Vector getFilterViewVector() {
        _logger.entering("FiltersModel", "getFilterViewVector");

        return _filterViews;
    }

    /**
     * Register an observer for filter list change notification.
     *
     * @param o the observer to register.
     */
    public void addObserver(Observer o) {
        _logger.entering("FiltersModel", "addObserver");

        _filterList.addObserver(o);
    }

    /**
     * Apply the filters to the given star list.
     *
     * @param starList the list of star to filter.
     */
    public void process(StarList starList) {
        _logger.entering("FiltersModel", "process");

        _filterList.process(starList);
    }
}
/*___oOo___*/
