/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarProperty.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.lang.Float;


/**
 * Star property.
 */
public class StarProperty implements Comparable
{
    /** Value */
    private String _value;

    /** Origin */
    private String _origin;

    /** Confidence */
    private String _confidence;

    /**
     * Default constructor.
     */
    public StarProperty()
    {
        setValue("");
        setConfidence("");
        setOrigin("");
    }

    /**
     * Fully parametred constructor.
     */
    public StarProperty(String value, String confidence, String origin)
    {
        setValue(value);
        setConfidence(confidence);
        setOrigin(origin);
    }

    /**
     * Set the value of the star property.
     *
     * @param value the new star property value.
     */
    public void setValue(String value)
    {
        MCSLogger.trace();

        _value = value;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    public String getStringValue()
    {
        MCSLogger.trace();

        return _value;
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    public double getDoubleValue()
    {
        MCSLogger.trace();

        return Double.parseDouble(_value);
    }

    /**
     * Set the origin of the star property.
     *
     * @param origin the new star property origin.
     */
    public void setOrigin(String origin)
    {
        MCSLogger.trace();

        _origin = origin;
    }

    /**
     * Get the origin of the star property as a String object.
     *
     * @return a String object representing the star property origin.
     */
    public String getOrigin()
    {
        MCSLogger.trace();

        return _origin.toString();
    }

    /**
     * Set the confidence index of the star property.
     *
     * @param confidence the new star property confidence index.
     */
    public void setConfidence(String confidence)
    {
        MCSLogger.trace();

        _confidence = confidence;
    }

    /**
     * Get the confidence index of the star property as a String object.
     *
     * @return a String object representing the star property confidence index.
     */
    public String getConfidence()
    {
        MCSLogger.trace();

        return _confidence.toString();
    }

    /**
     * Give back the string representation of a star property.
     *
     * @return the representation of the star property as a String object.
     */
    public String toString()
    {
        MCSLogger.trace();

        return _value.toString();
    }

    /**
     * Compare the star property the given object for sorting.
     *
     * @param o the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o)
    {
        MCSLogger.trace();

        // TODO fiish implementation
        return _value.compareTo(o);
    }
}
/*___oOo___*/
