/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

/**
 * Enumeration of all SearchCal preference keys.
 * @author Sylvain LAFRASSE
 */
public enum PreferenceKey {

        SHOW_TOOLTIPS_FLAG("help.tooltips.show"),
        SHOW_LEGEND_FLAG("view.legend.show"),
        SERVER_URL_ADDRESS("server.url.address"),

        VERBOSITY_SYNTHETIC_FLAG("view.result.verbosity.synthetic"),
        VERBOSITY_DETAILED_FLAG("view.result.verbosity.detailed"),
        VERBOSITY_FULL_FLAG("view.result.verbosity.full"),

        VIEW_SIMPLE_BRIGHT_N("view.columns.simple.bright.N"),
        VIEW_DETAILED_BRIGHT_N("view.columns.detailed.bright.N"),
        VIEW_SIMPLE_BRIGHT_V("view.columns.simple.bright.V"),
        VIEW_DETAILED_BRIGHT_V("view.columns.detailed.bright.V"),
        VIEW_SIMPLE_BRIGHT_K("view.columns.simple.bright.K"),
        VIEW_DETAILED_BRIGHT_K("view.columns.detailed.bright.K"),
        VIEW_SIMPLE_FAINT_K("view.columns.simple.faint.K"),
        VIEW_DETAILED_FAINT_K("view.columns.detailed.faint.K"),

        QUERY_MAGNITUDE_BAND("query.magnitudeBand"),
        QUERY_INSTRUMENTAL_WAVELENGTH("query.instrumentalWavelength"),
        QUERY_INSTRUMENTAL_BASELINE("query.instrumentalMaxBaseLine"),

        QUERY_SCIENCE_NAME("query.scienceObjectName"),
        QUERY_SCIENCE_RA("query.scienceObjectRA"),
        QUERY_SCIENCE_DEC("query.scienceObjectDEC"),
        QUERY_SCIENCE_DETECTION("query.scienceObjectDetectionDistance"),
        QUERY_SCIENCE_MAGNITUDE("query.scienceObjectMagnitude"),

        QUERY_MINIMUM_MAGNITUDE("query.queryMinMagnitude"),
        QUERY_MINIMUM_DELTA("query.queryMinMagnitudeDelta"),
        QUERY_MAXIMUM_MAGNITUDE("query.queryMaxMagnitude"),
        QUERY_MAXIMUM_DELTA("query.queryMaxMagnitudeDelta"),

        QUERY_BRIGHT_FLAG("query.queryBrightScenarioFlag"),
        QUERY_DIFF_RA("query.queryDiffRASize"),
        QUERY_DIFF_DEC("query.queryDiffDECSize"),
        QUERY_RADIAL_SIZE("query.queryRadialSize"),
        QUERY_RADIAL_FLAG("query.queryAutoRadius"),
        
        FILTER_NON_CALIBRATORS("filter.nonCalibrators");
    
    /** the preferenced value identifying token */
    private final String _key;

    /**
     * Constructor
     * @param key the preferenced value identifying token
     */
    PreferenceKey(String key) {
        _key = key;
    }

    /**
     * @return the preferenced value identifying token
     */
    @Override
    public String toString() {
        return _key;
    }

    /**
     * For unit testing purpose only.
     * @param args
     */
    public static void main(String[] args) {
        for (PreferenceKey k : PreferenceKey.values()) {
            System.out.println("Key '" + k.name() + "' = ['" + k + "'].");
        }
    }
}
