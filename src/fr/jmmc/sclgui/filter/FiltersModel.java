/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.query.QueryModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Filters model that can be adopted by a JTable.
 */
public final class FiltersModel {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(FiltersModel.class.getName());
    /** List of filters to handle */
    private final FilterList _filterList;
    /** Store each filter view */
    private final List<FilterView> _filterViews;
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
        _filterViews = new ArrayList<FilterView>(8);

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
     * Return the filter views.
     *
     * @return the list of filter views.
     */
    public List<FilterView> getFilterViews() {
        _logger.entering("FiltersModel", "getFilterViews");

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
     * @return filtered star list or given star list if filters are disabled
     */
    public StarList process(final StarList starList) {
        _logger.entering("FiltersModel", "process");

        return _filterList.process(starList);
    }
}
/*___oOo___*/
