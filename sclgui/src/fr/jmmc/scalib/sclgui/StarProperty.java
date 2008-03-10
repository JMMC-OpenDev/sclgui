/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarProperty.java,v 1.9 2007-08-02 12:45:19 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2007/06/18 13:59:32  lafrasse
 * Enhanced getStringValue() to handle 'null' value properties.
 *
 * Revision 1.7  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.6  2006/11/29 17:32:24  lafrasse
 * Added support for boolean values.
 *
 * Revision 1.5  2006/06/23 09:19:41  mella
 * Jalopization
 *
 * Revision 1.4  2006/04/07 08:04:32  mella
 * Remove too often log calls
 *
 * Revision 1.3  2006/04/06 14:36:21  lafrasse
 * Generalized star property value from String type to Object
 *
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.*;

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
        // MCSLogger.trace();
        _value = value;
    }

    /**
     * Get the value of the star property in its original form.
     *
     * @return an Object representing the star property value.
     */
    public Object getValue()
    {
        // MCSLogger.trace();
        return _value;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    public String getStringValue()
    {
        // MCSLogger.trace();
        if (_value == null)
        {
            return "";
        }

        return _value.toString();
    }

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    public double getDoubleValue()
    {
        // MCSLogger.trace();
        return Double.parseDouble(_value.toString());
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    public boolean getBooleanValue()
    {
        // MCSLogger.trace();
        return Boolean.parseBoolean(_value.toString());
    }

    /**
     * Return whether the star property has a value.
     *
     * @return true if a value is set, false otherwise.
     */
    public boolean hasValue()
    {
        // MCSLogger.trace();
        if (getStringValue().length() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Set the origin of the star property.
     *
     * @param origin the new star property origin.
     */
    public void setOrigin(String origin)
    {
        // MCSLogger.trace();
        _origin = origin;
    }

    /**
     * Get the origin of the star property as a String object.
     *
     * @return a String object representing the star property origin.
     */
    public String getOrigin()
    {
        // MCSLogger.trace();
        return _origin;
    }

    /**
     * Return whether the star property has a catalog origin.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasOrigin()
    {
        // MCSLogger.trace();
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
        // MCSLogger.trace();
        _confidence = confidence;
    }

    /**
     * Get the confidence index of the star property as a String object.
     *
     * @return a String object representing the star property confidence index.
     */
    public String getConfidence()
    {
        // MCSLogger.trace();
        return _confidence;
    }

    /**
     * Return whether the star property has a confidence index.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasConfidence()
    {
        // MCSLogger.trace();
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