/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.sclgui.preference.Preferences;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confidence (custom enum like) 
 * @author bourgesl
 */
public final class Confidence {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(Confidence.class.getName());
    /* Confidence enum keys as integer */
    /** Confidence - NO */
    public final static int KEY_CONFIDENCE_NO = 0;
    /** Confidence - LOW */
    public final static int KEY_CONFIDENCE_LOW = 1;
    /** Confidence - MEDIUM */
    public final static int KEY_CONFIDENCE_MEDIUM = 2;
    /** Confidence - HIGH */
    public final static int KEY_CONFIDENCE_HIGH = 3;
    /** custom confidence negative integer values (starts at -1) */
    private static int customInt = -1;
    /* Confidence instances */
    /** Confidence - NO */
    public final static Confidence CONFIDENCE_NO = new Confidence(KEY_CONFIDENCE_NO, "NO");
    /** Confidence - LOW */
    public final static Confidence CONFIDENCE_LOW = new Confidence(KEY_CONFIDENCE_LOW, "LOW");
    /** Confidence - MEDIUM */
    public final static Confidence CONFIDENCE_MEDIUM = new Confidence(KEY_CONFIDENCE_MEDIUM, "MEDIUM");
    /** Confidence - HIGH */
    public final static Confidence CONFIDENCE_HIGH = new Confidence(KEY_CONFIDENCE_HIGH, "HIGH");

    /**
     * Parse the given string as a Confidence
     * @param str string to parse (key as string or string value)
     * @return Confidence or CONFIDENCE_NO if no match found
     */
    public static Confidence parse(final String str) {
        // integer mapping for SearchCal 5.0:
        Confidence confidence = ConfidenceMapping.keyStringMap.get(str);
        if (confidence == null) {
            // fallback mode for SearchCal 4.x:
            confidence = ConfidenceMapping.valueMap.get(str);
        }
        if (confidence == null) {
            _logger.debug("unable to parse confidence value [{}] !", str);
            confidence = CONFIDENCE_NO;
        }
        return confidence;
    }

    /**
     * Parse the given integer as a Confidence
     * @param key enum key as integer
     * @return Confidence or CONFIDENCE_NO if no match found
     */
    public static Confidence parse(final int key) {
        Confidence confidence = ConfidenceMapping.keyIntegerMap.get(NumberUtils.valueOf(key));
        if (confidence == null) {
            confidence = CONFIDENCE_NO;
        }
        return confidence;
    }

    /**
     * Parse or create a new custom confidence index
     * @param str string representation
     * @return Confidence or custom Confidence
     */
    public static Confidence parseCustomConfidence(final String str) {
        Confidence confidence = parse(str);
        if (confidence == CONFIDENCE_NO) {
            // create a new custom confidence
            confidence = new Confidence(customInt--, str); // Should not be stored except for tooltip: use an extra table ??
        }
        return confidence;
    }

    /** @return Confidence instances */
    public static Set<Confidence> values() {
        return ConfidenceMapping.set;
    }

    /* members */
    /** enum key as integer */
    private final int key;
    /** key as string ("0", "1", "2", "3") */
    private final String keyString;
    /** string value ("NO", "LOW", "MEDIUM", "HIGH") */
    private final String value;

    /**
     * Private constructor
     * @param key enum key as integer
     * @param value string value
     */
    private Confidence(final int key, final String value) {
        this.key = key;
        this.keyString = String.valueOf(key);
        this.value = value;
        ConfidenceMapping.addConfidence(this);
    }

    /**
     * Return the enum key as integer
     * @return enum key as integer
     */
    public int getKey() {
        return key;
    }

    /**
     * Return the key as string ("0", "1", "2", "3")
     * @return key as string
     */
    public String getKeyString() {
        return keyString;
    }

    /**
     * Return the string value ("NO", "LOW", "MEDIUM", "HIGH")
     * @return string value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /** mappings: specific class to ensure mappings are defined before Confidence instances (initialization order issue) */
    private final static class ConfidenceMapping {

        /** default map capacity */
        private final static int CAPACITY = 64;
        /** Confidence map keyed by key as integer */
        final static Map<Integer, Confidence> keyIntegerMap = new HashMap<Integer, Confidence>(CAPACITY);
        /** Confidence map keyed by key as string */
        final static Map<String, Confidence> keyStringMap = new HashMap<String, Confidence>(CAPACITY);
        /** Confidence map keyed by string value */
        final static Map<String, Confidence> valueMap = new HashMap<String, Confidence>(CAPACITY);
        /** Confidence ordered set */
        final static Set<Confidence> set = new LinkedHashSet<Confidence>(CAPACITY);

        static void addConfidence(final Confidence confidence) {
            keyIntegerMap.put(NumberUtils.valueOf(confidence.getKey()), confidence);
            keyStringMap.put(confidence.getKeyString(), confidence);
            valueMap.put(confidence.getValue(), confidence);
            set.add(confidence);
        }

        /* Forbidden constructor */
        private ConfidenceMapping() {
        }
    }

    /**
     * To generate CSS styles in sclguiVOTableToHTML.xsl
     * @param args unused arguments
     */
    public static void main(String[] args) {

        // Initialize logs first:
        Bootstrapper.getState();

        // Use default color mapping:
        Preferences prefs = Preferences.getInstance();
        prefs.resetToDefaultPreferences();

        System.out.println("CSS version  :\n<!-- confidence indexes generated by fr.jmmc.sclgui.calibrator.Confidence.main() -->");

        for (Confidence confidence : Confidence.values()) {
            System.out.println("<set>");
            System.out.print("<key>c");
            System.out.print(confidence.getKeyString());
            System.out.println("</key>");
            // empty value
            System.out.println("<value/>");
            System.out.print("<title>");
            System.out.print(confidence.getValue());
            System.out.println("</title>");
            System.out.print("<description>");
            System.out.print(confidence.getValue());
            System.out.print(" confidence index");
            System.out.println("</description>");
            final String color = prefs.getPreference(Preferences.PREFIX_CONFIDENCE_COLOR + confidence, true); // ignore missing
            System.out.print("<color>");
            // CSS background-color: none if missing
            System.out.print((color != null) ? color : "none");
            System.out.println("</color>");
            System.out.println("</set>");
        }
    }
}
