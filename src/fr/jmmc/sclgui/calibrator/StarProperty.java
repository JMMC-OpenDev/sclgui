/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

/**
 * Star property.
 */
public final class StarProperty implements Comparable<StarProperty> {
    /* members */

    /** Value */
    private Object _value;
    /** Origin */
    private final String _origin;
    /** Confidence */
    private final String _confidence;

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
     * @return a String object representing the star property value.
     */
    public String getStringValue() {
        if (_value == null) {
            return "";
        }
        return _value.toString();
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    public double getDoubleValue() {
        if (_value instanceof Double) {
            return ((Double) _value).doubleValue();
        }
        return Double.parseDouble(_value.toString());
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    public boolean getBooleanValue() {
        if (_value instanceof Boolean) {
            return ((Boolean) _value).booleanValue();
        }
        return Boolean.parseBoolean(_value.toString());
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
        if (_origin == null) {
            return "";
        }
        return _origin;
    }

    /**
     * Return whether the star property has a catalog origin.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasOrigin() {
        if (_confidence != null) {
            return false;
        }

        if (_origin != null) {
            return true;
        }

        return false;
    }

    /**
     * Get the confidence index of the star property as a String object.
     *
     * @return a String object representing the star property confidence index.
     */
    public String getConfidence() {
        if (_confidence == null) {
            return "";
        }
        return _confidence;
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
}
/*___oOo___*/
