/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object Type filter.
 */
public final class ObjectTypeFilter extends Filter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(ObjectTypeFilter.class.getName());

    private final static boolean DO_STATS = false;

    public final static String[] FILTER_SIMBAD_OBJTYPES = new String[]{
        ",EB?,",
        ",Sy?,",
        ",CV?,",
        ",No?,",
        ",pr?,",
        ",TT?,",
        ",C*?,",
        ",S*?,",
        ",OH?,",
        ",CH?,",
        ",WR?,",
        ",Ae?,",
        ",RR?,",
        ",Ce?,",
        ",LP?,",
        ",Mi?,",
        ",sv?,",
        ",pA?,",
        ",WD?,",
        ",N*?,",
        ",BH?,",
        ",SN?,",
        ",BD?,",
        ",EB*,",
        ",Al*,",
        ",bL*,",
        ",WU*,",
        ",EP*,",
        ",SB*,",
        ",El*,",
        ",Sy*,",
        ",CV*,",
        ",NL*,",
        ",No*,",
        ",DN*,",
        ",Ae*,",
        ",C*,",
        ",S*,",
        ",pA*,",
        ",WD*,",
        ",ZZ*,",
        ",BD*,",
        ",N*,",
        ",OH*,",
        ",CH*,",
        ",pr*,",
        ",TT*,",
        ",WR*,",
        ",Ir*,",
        ",Or*,",
        ",RI*,",
        ",Er*,",
        ",FU*,",
        ",RC*,",
        ",RC?,",
        ",Psr,",
        ",BY*,",
        ",RS*,",
        ",Pu*,",
        ",RR*,",
        ",Ce*,",
        ",dS*,",
        ",RV*,",
        ",WV*,",
        ",bC*,",
        ",cC*,",
        ",gD*,",
        ",SX*,",
        ",LP*,",
        ",Mi*,",
        ",sr*,",
        ",SN*,"
    };

    /* filter execution variables */
    /** the Simbad ObjectTypes column Id */
    private int _ObjTypesID = -1;
    /** statistics */
    private final Map<String, AtomicInteger> _stats;

    /**
     * Default constructor.
     */
    public ObjectTypeFilter() {
        super();
        this._stats = (DO_STATS) ? new LinkedHashMap<String, AtomicInteger>(FILTER_SIMBAD_OBJTYPES.length) : null;
    }

    /**
     * Reset the filter
     */
    @Override
    public void reset() {
        setEnabled(Boolean.TRUE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject Invalid Object Types";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the id of the current column name
        _ObjTypesID = starList.getColumnIdByName(StarList.ObjTypesColumnName);

        // reset stats:
        if (_stats != null) {
            _stats.clear();

            for (String type : FILTER_SIMBAD_OBJTYPES) {
                _stats.put(type, new AtomicInteger());
            }
        }
    }

    /**
     * Finish the filter execution (potential cleanup / log).
     */
    @Override
    public void onFinish() {
        // dump stats:
        if (_stats != null) {
            _logger.info("Statistics:");

            for (Map.Entry<String, AtomicInteger> entry : _stats.entrySet()) {
                System.out.println(entry.getKey() + "\t" + entry.getValue().intValue());
            }
        }
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    @Override
    public boolean shouldRemoveRow(final List<StarProperty> row) {
        StarProperty cell;

        // If the desired column name exists
        if (_ObjTypesID != -1) {
            // Get the cell of the desired column
            cell = row.get(_ObjTypesID);

            // if the Simbad ObjectTypes exist
            if (cell.hasValue()) {
                final String ObjTypes = cell.getString();
                // Check the value:
                final boolean remove = filterObjTypes(ObjTypes);

                if (remove) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Filtered [{}]", ObjTypes);
                    }
                    if (_stats != null) {
                        // efficient loop:
                        final String[] filterTypes = FILTER_SIMBAD_OBJTYPES;
                        final Map<String, AtomicInteger> stats = _stats;

                        for (int i = 0; i < filterTypes.length; i++) {
                            final String type = filterTypes[i];

                            if (ObjTypes.contains(type)) {
                                stats.get(type).incrementAndGet();
                            }
                        }
                    }
                }
                return remove;
            }
        }

        // Otherwise this row should be kept
        return false;
    }

    private static boolean filterObjTypes(final String ObjTypes) {
        // efficient loop:
        final String[] filterTypes = FILTER_SIMBAD_OBJTYPES;

        for (int i = 0; i < filterTypes.length; i++) {
            if (ObjTypes.contains(filterTypes[i])) {
                return true;
            }
        }
        return false;
    }
}
/*___oOo___*/
