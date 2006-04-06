/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarProperty.java,v 1.3 2006-04-06 14:36:21 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.lang.Float;


/**
 * Star property.
 */
public class StarProperty implements Comparable
{
    // Trace has been deactvated because of numerous call

    /** Value */
    private Object _value;

    /** Origin */
    private String _origin;

    /** Confidence */
    private String _confidence;

    /**
     * Fully parametred constructor.
     */
    public StarProperty(Object value, String origin, String confidence)
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
    public void setValue(Object value)
    {
        MCSLogger.trace();

        _value = value;
    }

    /**
     * Get the value of the star property in its original form.
     *
     * @return an Object representing the star property value.
     */
    public Object getValue()
    {
        MCSLogger.trace();

        return _value;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    public String getStringValue()
    {
        MCSLogger.trace();

        return _value.toString();
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    public double getDoubleValue()
    {
        MCSLogger.trace();

        return Double.parseDouble(_value.toString());
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

        return _origin;
    }

    /**
     * Indicates if the star property has a catalog origin.
     *
     * @return true if a confidence index is set or false
     */
    public boolean hasOrigin()
    {
        MCSLogger.trace();

        if (_confidence.length() > 0)
        {
            return false;
        }

        if (_origin.length() > 0)
        {
            return true;
        }

        return false;
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

        return _confidence;
    }

    /**
     * Indicates if the star property has a confidence index.
     *
     * @return true if a confidence index is set or false
     */
    public boolean hasConfidence()
    {
        MCSLogger.trace();

        if (_confidence.length() > 0)
        {
            return true;
        }

        return false;
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

        return ((Comparable) _value).compareTo((Comparable) o);

        /*
           // If the _value member instance class implements 'Comparable' interface
           Class[] interfaces = _value.getClass().getInterfaces();
           for (int i = 0; i < interfaces.length; i++)
           {
               // Compare it against the given object
               if (interfaces[i] == java.lang.Comparable.class)
               {
                   return ((Comparable)_value).compareTo((Comparable)o);
               }
           }
           // Otherwise only return whether the given object is the same or not
           return (_value.equals(o) == true ? 0 : 1);
         */
    }
}
/*___oOo___*/
