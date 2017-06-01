/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.query.QueryModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters model that can be adopted by a JTable.
 */
public final class FiltersModel {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(FiltersModel.class.getName());

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
        _filterViews = new ArrayList<FilterView>(8);

        // Distance filter
        addFilter(new DistanceFilter(_queryModel));

        // Magnitude filter
        addFilter(new MagnitudeFilter(_queryModel));

        // Spectral Type filter
        addFilter(new SpectralTypeFilter());

        // Luminosity filter
        addFilter(new LuminosityFilter());

        // Visibiliy filter
        addFilter(new VisibilityFilter());

        // Visibility accuracy filter
        addFilter(new VisibilityAccuracyFilter());

        // Variability filter
        addFilter(new VariabilityFilter());

        // Multiplicity filter
        addFilter(new MultiplicityFilter());

        // ObjectTypes filter
        addFilter(new ObjectTypeFilter());
        
        // Diameter quality filter
        addFilter(new DiameterQualityFilter());
    }

    private void addFilter(final Filter filter) {
        // call reset to prepare constraints and enable filters by default:
        filter.reset(true);
        _filterList.add(filter);

        final FilterView filterView = new FilterView(filter);
        filterView.init();
        _filterViews.add(filterView);
    }

    /**
     * Reset filters
     * @param bright true if bright mode; false otherwise
     */
    public void resetFilters(final boolean bright) {
        _filterList.reset(bright);
    }

    /**
     * Return the filter views.
     *
     * @return the list of filter views.
     */
    public List<FilterView> getFilterViews() {
        return _filterViews;
    }

    /**
     * Register an observer for filter list change notification.
     *
     * @param o the observer to register.
     */
    public void addObserver(Observer o) {
        _filterList.addObserver(o);
    }

    /**
     * Apply the filters to the given star list.
     *
     * @param starList the list of star to filter.
     * @return filtered star list or given star list if filters are disabled
     */
    public StarList process(final StarList starList) {
        return _filterList.process(starList);
    }
}
/*___oOo___*/
