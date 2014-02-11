/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

/**
 * Star property.
 */
public final class StarProperty implements Comparable<StarProperty> {

    /** shared empty star property to reduce memory footprint */
    public final static StarProperty EMPTY_STAR_PROPERTY = new StarProperty(null, Origin.KEY_ORIGIN_NONE, Confidence.KEY_CONFIDENCE_NO);
    /* members */
    /** Value */
    private Object _value;
    /** Origin index as integer */
    private int _originIndex;
    /** Confidence index as integer */
    private int _confidenceIndex;

    /**
     * Custom constructor setting origin to COMPUTED and confidence to HIGH
     * @param value the new star property value.
     */
    public StarProperty(final Object value) {
        set(value, Origin.KEY_ORIGIN_COMPUTED, Confidence.KEY_CONFIDENCE_HIGH);
    }

    /**
     * Fully parameter-ed constructor.
     * @param value the new star property value.
     * @param originIndex the origin of the star property
     * @param confidenceIndex the confidence index of the star property
     */
    public StarProperty(final Object value, final int originIndex, final int confidenceIndex) {
        set(value, originIndex, confidenceIndex);
    }

    /**
     * Update the star property with new given values (use with care).
     * @param value the new star property value.
     * @param originIndex the origin of the star property
     * @param confidenceIndex the confidence index of the star property
     */
    public void set(final Object value, final int originIndex, final int confidenceIndex) {
        _value = value;
        _originIndex = originIndex;
        _confidenceIndex = confidenceIndex;
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
     * Get the origin of the star property as an Origin enum.
     *
     * @return an Origin enum representing the star property origin.
     */
    public Origin getOrigin() {
        return Origin.parse(_originIndex);
    }

    /**
     * Get the origin index of the star property as an integer.
     *
     * @return Origin index as integer representing the star property origin index.
     */
    public int getOriginIndex() {
        return _originIndex;
    }

    /**
     * Return whether the star property has a catalog origin (ie not undefined nor computed and (high or no) confidence index).
     *
     * @return true if a origin index is set, false otherwise.
     */
    public boolean hasOrigin() {
        // note: return false if confidence is not HIGH (bad conversion):
        return (_originIndex > Origin.KEY_ORIGIN_COMPUTED
                && ((_confidenceIndex == Confidence.KEY_CONFIDENCE_HIGH) || (_confidenceIndex == Confidence.KEY_CONFIDENCE_NO)));
    }

    /**
     * Get the confidence of the star property as a Confidence enum.
     *
     * @return a Confidence enum representing the star property confidence index.
     */
    public Confidence getConfidence() {
        return Confidence.parse(_confidenceIndex);
    }

    /**
     * Get the confidence index of the star property as an integer.
     *
     * @return Confidence index as integer representing the star property confidence index.
     */
    public int getConfidenceIndex() {
        return _confidenceIndex;
    }

    /**
     * Return whether the star property has a confidence index.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasConfidence() {
        return (_confidenceIndex != Confidence.KEY_CONFIDENCE_NO);
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
        return "StarProperty{" + "_value=" + _value + ", _origin=" + getOrigin() + ", _confidence=" + getConfidence() + '}';
    }
}
/*___oOo___*/
