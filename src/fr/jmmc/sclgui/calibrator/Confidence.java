/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import java.util.HashMap;
import java.util.Map;

/**
 * Confidence (custom enum like) 
 * @author bourgesl
 */
public final class Confidence {

    /** int String index */
    private final static Map<String, Confidence> intStringIndex = new HashMap<String, Confidence>(64);
    /** representation index */
    private final static Map<String, Confidence> representationIndex = new HashMap<String, Confidence>(64);
    /** undefined */
    public final static Confidence UNDEFINED = new Confidence(0, "UNDEFINED");
    /** Confidence - LOW */
    public final static Confidence CONFIDENCE_LOW = new Confidence(1, "LOW");
    /** Confidence - MEDIUM */
    public final static Confidence CONFIDENCE_MEDIUM = new Confidence(2, "MEDIUM");
    /** Confidence - HIGH */
    public final static Confidence CONFIDENCE_HIGH = new Confidence(3, "HIGH");
    /** custom confidence int value */
    private static int customInt = -100;

    /**
     * Parse the given string as a Confidence
     * @param str string to parse
     * @return Confidence
     */
    public static Confidence parse(final String str) {
        Confidence confidence = intStringIndex.get(str);
        if (confidence == null) {
            // fallback mode for SearchCal votable < 5.0:
            confidence = representationIndex.get(str);
        }
        if (confidence == null) {
            confidence = UNDEFINED;
        }
        return confidence;
    }

    /**
     * Parse or create new custom confidence index
     * @param str string representation
     * @return Confidence or custom Confidence
     */
    public static Confidence parseCustomConfidence(final String str) {
        Confidence confidence = parse(str);
        if (confidence == UNDEFINED) {
            // create a new custom confidence
            confidence = new Confidence(customInt--, str);
        }
        return confidence;
    }

    private static void addConfidence(final Confidence confidence) {
        intStringIndex.put(confidence.getIntString(), confidence);
        representationIndex.put(confidence.getRepresentation(), confidence);
    }
    /* members */
    /** enum value */
    private final int value;
    /** int string representation ("1", "2", "3") */
    private final String intString;
    /** string representation ("LOW", "MEDIUM", "HIGH") */
    private final String representation;

    private Confidence(final int value, final String representation) {
        this.value = value;
        this.intString = (value != 0) ? String.valueOf(value) : ""; // empty string for undefined value
        this.representation = representation;

        addConfidence(this);
    }

    /**
     * Return the enum value
     * @return enum value
     */
    public int getValue() {
        return value;
    }

    /**
     * Return the int string representation ("1", "2", "3")
     * @return int string representation
     */
    public String getIntString() {
        return intString;
    }

    /**
     * Return the string representation ("LOW", "MEDIUM", "HIGH")
     * @return string representation
     */
    public String getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}
