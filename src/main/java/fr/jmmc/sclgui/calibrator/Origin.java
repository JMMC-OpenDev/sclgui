/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.util.ColorEncoder;
import fr.jmmc.jmcs.util.NumberUtils;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Origin (custom enum like) 
 * @author bourgesl
 */
public final class Origin {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(Origin.class.getName());

    /* Origin enum keys as integer */
    /** Origin - ORIGIN NONE / NO CATALOG */
    public final static int KEY_ORIGIN_NONE = 0;
    /** Origin - MIXED_CATALOG (unused in GUI) */
    public final static int KEY_ORIGIN_MIXED_CATALOG = 1;
    /** Origin - COMPUTED */
    public final static int KEY_ORIGIN_COMPUTED = 2;
    /** Origin - AKARI */
    public final static int KEY_ORIGIN_AKARI = 3;
    /** Origin - ASCC */
    public final static int KEY_ORIGIN_ASCC = 4;
    /** Origin - ASCC LOCAL */
    public final static int KEY_ORIGIN_ASCC_LOCAL = 5;
    /** Origin - BSC */
    public final static int KEY_ORIGIN_BSC = 6;
    /** Origin - CIO */
    public final static int KEY_ORIGIN_CIO = 7;
    /** Origin - DENIS */
    public final static int KEY_ORIGIN_DENIS = 8;
    /** Origin - DENIS JK */
    public final static int KEY_ORIGIN_DENIS_JK = 9;
    /** Origin - HIC */
    public final static int KEY_ORIGIN_HIC = 10;
    /** Origin - HIP 1 */
    public final static int KEY_ORIGIN_HIP1 = 11;
    /** Origin - HIP 2 */
    public final static int KEY_ORIGIN_HIP2 = 12;
    /** Origin - LBSI */
    public final static int KEY_ORIGIN_LBSI = 13;
    /** Origin - 2MASS */
    public final static int KEY_ORIGIN_MASS = 14;
    /** Origin - MERAND */
    public final static int KEY_ORIGIN_MERAND = 15;
    /** Origin - MIDI */
    public final static int KEY_ORIGIN_MIDI = 16;
    /** Origin - PHOTO */
    public final static int KEY_ORIGIN_PHOTO = 17;
    /** Origin - SBSC */
    public final static int KEY_ORIGIN_SBSC = 18;
    /** Origin - SB9 */
    public final static int KEY_ORIGIN_SB9 = 19;
    /** Origin - USNO */
    public final static int KEY_ORIGIN_USNO = 20;
    /** Origin - WDS */
    public final static int KEY_ORIGIN_WDS = 21;
    /** Origin - JSDC FAINT LOCAL */
    public final static int KEY_ORIGIN_JSDC_FAINT_LOCAL = 22;
    /** Origin - SIMBAD */
    public final static int KEY_ORIGIN_SIMBAD = 23;
    /** Origin - WISE */
    public final static int KEY_ORIGIN_WISE = 24;
    /** Origin - WISE */
    public final static int KEY_ORIGIN_GAIA = 25;
    /** Origin - WISE */
    public final static int KEY_ORIGIN_GAIA_DIST = 26;
    /** Origin - MDFC */
    public final static int KEY_ORIGIN_MDFC = 27;
    /** Origin - USER */
    public final static int KEY_ORIGIN_USER = 28;
    /** custom origin negative integer values (starts at -1) */
    private static int customInt = -1;
    /* Origin instances */
    /** Origin - ORIGIN NONE */
    public final static Origin ORIGIN_NONE = new Origin(KEY_ORIGIN_NONE, "NO CATALOG");
    /** Origin - MIXED_CATALOG */
    public final static Origin ORIGIN_MIXED_CATALOG = new Origin(KEY_ORIGIN_MIXED_CATALOG, "MIXED CATALOG");
    /** Origin - COMPUTED */
    public final static Origin ORIGIN_COMPUTED = new Origin(KEY_ORIGIN_COMPUTED, "computed");
    /** Origin - AKARI */
    public final static Origin ORIGIN_AKARI = new Origin(KEY_ORIGIN_AKARI, Catalog.AKARI);
    /** Origin - ASCC */
    public final static Origin ORIGIN_ASCC = new Origin(KEY_ORIGIN_ASCC, Catalog.ASCC_2_5);
    /** Origin - ASCC LOCAL */
    public final static Origin ORIGIN_ASCC_LOCAL = new Origin(KEY_ORIGIN_ASCC_LOCAL, "I/280B", Catalog.ASCC_2_5);
    /** Origin - BSC */
    public final static Origin ORIGIN_BSC = new Origin(KEY_ORIGIN_BSC, Catalog.BSC);
    /** Origin - CIO */
    public final static Origin ORIGIN_CIO = new Origin(KEY_ORIGIN_CIO, Catalog.CIO);
    /** Origin - DENIS */
    public final static Origin ORIGIN_DENIS = new Origin(KEY_ORIGIN_DENIS, Catalog.DENIS);
    /** Origin - DENIS JK */
    public final static Origin ORIGIN_DENIS_JK = new Origin(KEY_ORIGIN_DENIS_JK, "J/A+A/413/1037/table1", Catalog.J_K_DENIS);
    /** Origin - HIC */
    public final static Origin ORIGIN_HIC = new Origin(KEY_ORIGIN_HIC, Catalog.HIC);
    /** Origin - HIP 1 */
    public final static Origin ORIGIN_HIP1 = new Origin(KEY_ORIGIN_HIP1, Catalog.HIP1);
    /** Origin - HIP 2 */
    public final static Origin ORIGIN_HIP2 = new Origin(KEY_ORIGIN_HIP2, Catalog.HIP2);
    /** Origin - LBSI */
    public final static Origin ORIGIN_LBSI = new Origin(KEY_ORIGIN_LBSI, Catalog.LBSI);
    /** Origin - 2MASS */
    public final static Origin ORIGIN_MASS = new Origin(KEY_ORIGIN_MASS, Catalog._2MASS);
    /** Origin - MERAND */
    public final static Origin ORIGIN_MERAND = new Origin(KEY_ORIGIN_MERAND, Catalog.Merand);
    /** Origin - MIDI */
    public final static Origin ORIGIN_MIDI = new Origin(KEY_ORIGIN_MIDI, Catalog.MIDI);
    /** Origin - PHOTO */
    public final static Origin ORIGIN_PHOTO = new Origin(KEY_ORIGIN_PHOTO, Catalog.JP11);
    /** Origin - SBSC */
    public final static Origin ORIGIN_SBSC = new Origin(KEY_ORIGIN_SBSC, Catalog.SBSC);
    /** Origin - SB9 */
    public final static Origin ORIGIN_SB9 = new Origin(KEY_ORIGIN_SB9, Catalog.SB9);
    /** Origin - USNO */
    public final static Origin ORIGIN_USNO = new Origin(KEY_ORIGIN_USNO, Catalog.USNO_B);
    /** Origin - WDS */
    public final static Origin ORIGIN_WDS = new Origin(KEY_ORIGIN_WDS, Catalog.WDS);
    /** Origin - ASCC LOCAL FAINT */
    public final static Origin ORIGIN_ASCC_FAINT_LOCAL = new Origin(KEY_ORIGIN_JSDC_FAINT_LOCAL, "I/280B", Catalog.ASCC_2_5);
    /** Origin - SIMBAD */
    public final static Origin ORIGIN_SIMBAD = new Origin(KEY_ORIGIN_SIMBAD, Catalog.SIMBAD);
    /** Origin - WISE */
    public final static Origin ORIGIN_WISE = new Origin(KEY_ORIGIN_WISE, Catalog.WISE);
    /** Origin - GAIA */
    public final static Origin ORIGIN_GAIA = new Origin(KEY_ORIGIN_GAIA, Catalog.GAIA);
    /** Origin - GAIA AP */
    public final static Origin ORIGIN_GAIA_AP = new Origin(KEY_ORIGIN_GAIA_DIST, Catalog.GAIA_AP);
    /** Origin - MDFC */
    public final static Origin ORIGIN_MDFC = new Origin(KEY_ORIGIN_MDFC, Catalog.MDFC);
    /** Origin - USER */
    public final static Origin ORIGIN_USER = new Origin(KEY_ORIGIN_USER, "USER");

    static {
        /* initialize alternate string representations used */
        /* declare former CDS reference for Denis JK (before 2011) */
        OriginMapping.valueMap.put("J/A+A/413/1037", ORIGIN_DENIS_JK);

        // handle "-" (blanking value used up to SearchCal release 4.4) 
        OriginMapping.valueMap.put(CalibratorsModel.STR_UNDEFINED, ORIGIN_NONE);
    }

    /**
     * Parse the given string as a Origin
     * @param str string to parse (key as string or string value)
     * @return Origin or ORIGIN_NONE if no match found
     */
    public static Origin parse(final String str) {
        // integer mapping for SearchCal 5.0:
        Origin origin = OriginMapping.keyStringMap.get(str);
        if (origin == null) {
            // fallback mode for SearchCal 4.x:
            origin = OriginMapping.valueMap.get(str);
        }
        if (origin == null) {
            _logger.debug("unable to parse origin value [{}] !", str);
            origin = ORIGIN_NONE;
        }
        return origin;
    }

    /**
     * Parse the given integer as a Origin
     * @param key enum key as integer
     * @return Origin or ORIGIN_NONE if no match found
     */
    public static Origin parse(final int key) {
        Origin origin = OriginMapping.keyIntegerMap.get(NumberUtils.valueOf(key));
        if (origin == null) {
            origin = ORIGIN_NONE;
        }
        return origin;
    }

    /**
     * Create a new custom origin index
     * @param key enum key as integer
     * @param str string representation
     * @return custom Origin
     */
    public static Origin createOrigin(final int key, final String str) {
        Origin origin = parse(str);
        if (origin == ORIGIN_NONE) {
            // create a new custom origin
            origin = new Origin(key, str); // Should not be stored except for tooltip: use an extra table ??
        }
        return origin;
    }

    /**
     * Parse or create a new custom origin index
     * @param str string representation
     * @return Origin or custom Origin
     */
    public static Origin parseCustomOrigin(final String str) {
        Origin origin = parse(str);
        if (origin == ORIGIN_NONE) {
            // create a new custom origin
            origin = new Origin(customInt--, str); // Should not be stored except for tooltip: use an extra table ??
        }
        return origin;
    }

    /** @return Origin instances */
    public static Set<Origin> values() {
        return OriginMapping.set;
    }
    /* members */
    /** enum key as integer */
    private final int key;
    private final String keyString;
    private final String value;
    /** optional catalog reference */
    private final Catalog catalog;

    /**
     * Private constructor
     * @param key enum key as integer
     * @param value string value
     */
    private Origin(final int key, final String value) {
        this(key, value, null);
    }

    /**
     * Private constructor
     * @param key enum key as integer
     * @param catalog optional catalog reference
     */
    private Origin(final int key, final Catalog catalog) {
        this.key = key;
        this.keyString = String.valueOf(key);
        this.value = catalog.reference();
        this.catalog = catalog;
        OriginMapping.addOrigin(this);
    }

    /**
     * Private constructor
     * @param key enum key as integer
     * @param value string value
     * @param catalog optional catalog reference
     */
    private Origin(final int key, final String value, final Catalog catalog) {
        this.key = key;
        this.keyString = String.valueOf(key);
        this.value = value;
        this.catalog = catalog;
        OriginMapping.addOrigin(this);
    }

    /**
     * Return the enum key as integer
     * @return enum key as integer
     */
    public int getKey() {
        return key;
    }

    /**
     * Return the key as string
     * @return key as string
     */
    public String getKeyString() {
        return keyString;
    }

    /**
     * Return the string value
     * @return string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the optional catalog reference
     * @return optional catalog reference
     */
    public Catalog getCatalog() {
        return catalog;
    }

    @Override
    public String toString() {
        return value;
    }

    /** mappings: specific class to ensure mappings are defined before Origin instances (initialization order issue) */
    private final static class OriginMapping {

        /** default map capacity */
        private final static int CAPACITY = 32;
        /** Origin map keyed by key as integer */
        final static Map<Integer, Origin> keyIntegerMap = new HashMap<Integer, Origin>(CAPACITY);
        /** Origin map keyed by key as string */
        final static Map<String, Origin> keyStringMap = new HashMap<String, Origin>(CAPACITY);
        /** Origin map keyed by string value */
        final static Map<String, Origin> valueMap = new HashMap<String, Origin>(CAPACITY);
        /** Origin ordered set */
        final static Set<Origin> set = new LinkedHashSet<Origin>(CAPACITY);

        static void addOrigin(final Origin origin) {
            keyIntegerMap.put(NumberUtils.valueOf(origin.getKey()), origin);
            keyStringMap.put(origin.getKeyString(), origin);
            valueMap.put(origin.getValue(), origin);
            set.add(origin);
        }

        /* Forbidden constructor */
        private OriginMapping() {
        }
    }

    /**
     * To generate CSS styles in sclguiVOTableToHTML.xsl
     * and describes origins in http://apps.jmmc.fr/exist/apps/myapp/origins.html
     * @param args unused arguments
     */
    public static void main(String[] args) {
        System.out.println("<originIndexes>");
        System.out.println("<!-- origin indexes (catalog) generated by "+Origin.class.getName()+".main() -->");
        for (Origin origin : Origin.values()) {            
            System.out.println("<set>");
            System.out.print("<key>o");
            System.out.print(origin.getKeyString());
            System.out.println("</key>");
            final Catalog catalog = origin.getCatalog();
            if (catalog != null) {
                System.out.print("<value>");
                System.out.print(origin.getValue());
                System.out.println("</value>");

                System.out.print("<title>");
                System.out.print(catalog.title());
                System.out.println("</title>");
                System.out.print("<description>");
                System.out.print(catalog.description());
                System.out.println("</description>");

                System.out.print("<color>");
                System.out.print(ColorEncoder.encode(Catalog.getDefaultColor(catalog)));
                System.out.println("</color>");
            } else {
                // empty value
                System.out.println("<value/>");

                System.out.print("<title>");
                System.out.print(origin.getValue());
                System.out.println("</title>");
                // empty description
                System.out.println("<description/>");

                // CSS background-color: none if missing
                if ("USER".equals(origin.getValue())) {
                    System.out.println("<color>MidnightBlue</color>");
                } else {
                    System.out.println("<color>none</color>");
                }
            }
            System.out.println("</set>");
        }
        System.out.println("</originIndexes>");
    }
}