/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

/**
 * Star property.
 */
public final class StarProperty implements Comparable<StarProperty> {

    /** shared empty star property to reduce memory footprint */
    public final static StarProperty EMPTY_STAR_PROPERTY = new StarProperty(null, null, null);
    /** Origin - computed */
    public final static String ORIGIN_COMPUTED = "computed";
    /** Confidence - HIGH */
    public final static String CONFIDENCE_HIGH = "HIGH";
    /** Confidence - MEDIUM */
    public final static String CONFIDENCE_MEDIUM = "MEDIUM";
    /** Confidence - LOW */
    public final static String CONFIDENCE_LOW = "LOW";
    /* members */
    /** Value */
    private Object _value;
    /** Origin */
    private final String _origin;
    /** Confidence */
    private final String _confidence;

    /**
     * Custom constructor defining origin = "computed" and confidence to HIGH
     * @param value the new star property value.
     */
    public StarProperty(final Object value) {
        this(value, ORIGIN_COMPUTED, CONFIDENCE_HIGH);
    }

    /**
     * Fully parameter-ed constructor.
     * @param value the new star property value.
     * @param origin the origin of the star property
     * @param confidence the confidence index of the star property 
     */
    public StarProperty(final Object value, final String origin, final String confidence) {
        setValue(value);
        _origin = origin;
        _confidence = confidence;
    }

    /**
     * Set the value of the star property.
     *
     * @param value the new star property value.
     */
    public void setValue(final Object value) {
        _value = value;
    }

    /**
     * Get the value of the star property in its original form.
     *
     * @return an Object representing the star property value.
     */
    public Object getValue() {
        return _value;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value or null if undefined
     */
    public String getString() {
        return (_value == null) ? null : _value.toString();
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    public String getStringValue() {
        return (_value == null) ? "" : _value.toString();
    }

    /**
     * Get the value of the star property as an Integer object.
     *
     * @return an Integer object representing the star property value.
     */
    public Integer getInteger() {
        return (_value instanceof Integer) ? (Integer) _value : null;
    }

    /**
     * Get the value of the star property as an Integer object.
     *
     * @return an Integer object representing the star property value.
     */
    public int getIntegerValue() {
        return (_value instanceof Integer) ? ((Integer) _value).intValue() : Integer.MIN_VALUE;
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    public Double getDouble() {
        return (_value instanceof Double) ? (Double) _value : null;
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    public double getDoubleValue() {
        return (_value instanceof Double) ? ((Double) _value).doubleValue() : Double.NaN;
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    public Boolean getBoolean() {
        return (_value instanceof Boolean) ? (Boolean) _value : null;
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    public boolean getBooleanValue() {
        return (_value instanceof Boolean) ? ((Boolean) _value).booleanValue() : false;
    }

    /**
     * Return whether the star property has a value.
     *
     * @return true if a value is set, false otherwise.
     */
    public boolean hasValue() {
        return (_value != null);
    }

    /**
     * Get the origin of the star property as a String object.
     *
     * @return a String object representing the star property origin.
     */
    public String getOrigin() {
        return (_origin == null) ? "" : _origin;
    }

    /**
     * Return whether the star property has a catalog origin.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasOrigin() {
        return (_confidence != null) ? false : (_origin != null);
    }

    /**
     * Get the confidence index of the star property as a String object.
     *
     * @return a String object representing the star property confidence index.
     */
    public String getConfidence() {
        return (_confidence == null) ? "" : _confidence;
    }

    /**
     * Return whether the star property has a confidence index.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasConfidence() {
        return (_confidence != null);
    }

    /**
     * Compare the star property the given object for sorting.
     *
     * @param o the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(final StarProperty o) {
        return ((Comparable) _value).compareTo((Comparable) o);
    }

    @Override
    public String toString() {
        return "StarProperty{" + "_value=" + _value + ", _origin=" + _origin + ", _confidence=" + _confidence + '}';
    }
}
/*___oOo___*/
