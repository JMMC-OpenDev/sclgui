/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

/**
 * Star property storing values as Object instances (Boolean, Integer, String)
 */
public final class ObjectStarProperty implements StarProperty {

    /* members */
    /** Origin index as integer */
    private int _originIndex;
    /** Confidence index as integer */
    private int _confidenceIndex;
    /** Value */
    private Object _value;

    /**
     * Custom constructor setting origin to COMPUTED and confidence to HIGH
     * @param value the new star property value.
     */
    public ObjectStarProperty(final Object value) {
        set(value, Origin.KEY_ORIGIN_COMPUTED, Confidence.KEY_CONFIDENCE_HIGH);
    }

    /**
     * Fully parameter-ed constructor.
     * @param value the new star property value.
     * @param originIndex the origin of the star property
     * @param confidenceIndex the confidence index of the star property
     */
    public ObjectStarProperty(final Object value, final int originIndex, final int confidenceIndex) {
        set(value, originIndex, confidenceIndex);
    }

    /**
     * Update the star property with new given values (use with care).
     * @param value the new star property value.
     * @param originIndex the origin of the star property
     * @param confidenceIndex the confidence index of the star property
     */
    @Override
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
    @Override
    public void setValue(final Object value) {
        _value = value;
    }

    /**
     * Get the value of the star property in its original form.
     *
     * @return an Object representing the star property value.
     */
    @Override
    public Object getValue() {
        return _value;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value or null if undefined
     */
    @Override
    public String getString() {
        return hasValue() ? _value.toString() : null;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    @Override
    public String getStringValue() {
        return hasValue() ? _value.toString() : "";
    }

    /**
     * Get the value of the star property as an Integer object.
     *
     * @return an Integer object representing the star property value.
     */
    @Override
    public Integer getInteger() {
        return (_value instanceof Integer) ? (Integer) _value : null;
    }

    /**
     * Get the value of the star property as an Integer object.
     *
     * @return an Integer object representing the star property value.
     */
    @Override
    public int getIntegerValue() {
        return (_value instanceof Integer) ? ((Integer) _value).intValue() : Integer.MIN_VALUE;
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    @Override
    public Double getDouble() {
        throw new UnsupportedOperationException("ObjectStarProperty.getDouble() not implemented !");
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    @Override
    public double getDoubleValue() {
        throw new UnsupportedOperationException("ObjectStarProperty.getDoubleValue() not implemented !");
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    @Override
    public Boolean getBoolean() {
        return (_value instanceof Boolean) ? (Boolean) _value : null;
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    @Override
    public boolean getBooleanValue() {
        return (_value instanceof Boolean) ? ((Boolean) _value).booleanValue() : false;
    }

    /**
     * Return whether the star property has a value.
     *
     * @return true if a value is set, false otherwise.
     */
    @Override
    public boolean hasValue() {
        return (_value != null);
    }

    /**
     * Get the origin of the star property as an Origin enum.
     *
     * @return an Origin enum representing the star property origin.
     */
    @Override
    public Origin getOrigin() {
        return Origin.parse(_originIndex);
    }

    /**
     * Get the origin index of the star property as an integer.
     *
     * @return Origin index as integer representing the star property origin index.
     */
    @Override
    public int getOriginIndex() {
        return _originIndex;
    }

    /**
     * Return whether the star property has a catalog origin (ie not undefined nor computed and (high or no) confidence index).
     *
     * @return true if a origin index is set, false otherwise.
     */
    @Override
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
    @Override
    public Confidence getConfidence() {
        return Confidence.parse(_confidenceIndex);
    }

    /**
     * Get the confidence index of the star property as an integer.
     *
     * @return Confidence index as integer representing the star property confidence index.
     */
    @Override
    public int getConfidenceIndex() {
        return _confidenceIndex;
    }

    /**
     * Return whether the star property has a confidence index.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    @Override
    public boolean hasConfidence() {
        return (_confidenceIndex != Confidence.KEY_CONFIDENCE_NO);
    }

    @Override
    public String toString() {
        return "StarProperty{" + "_value=" + _value + ", _origin=" + getOrigin() + ", _confidence=" + getConfidence() + '}';
    }
}
/*___oOo___*/
