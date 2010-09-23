/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarProperty.java,v 1.11 2010-07-29 15:10:08 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2008/09/10 22:39:35  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.9  2007/08/02 12:45:19  lafrasse
 * Added a method to return whether the star property has a value or not.
 *
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

import java.net.URLEncoder;

import java.util.logging.*;


/**
 * Star property.
 */
public class StarProperty implements Comparable
{
    // Trace has been deactvated because of numerous call

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.StarProperty");

    /** Value */
    private Object _value;

    /** Origin */
    private String _origin;

    /** Confidence */
    private String _confidence;

    /** URL */
    private String _url;

    /**
     * Fully parametred constructor.
     */
    public StarProperty(Object value, String origin, String confidence,
        String url)
    {
        setValue(value);
        setConfidence(confidence);
        setOrigin(origin);
        setURL(url);
    }

    /**
     * Set the value of the star property.
     *
     * @param value the new star property value.
     */
    public void setValue(Object value)
    {
        // _logger.entering("StarProperty", "setValue");
        _value = value;
    }

    /**
     * Get the value of the star property in its original form.
     *
     * @return an Object representing the star property value.
     */
    public Object getValue()
    {
        // _logger.entering("StarProperty", "getValue");
        return _value;
    }

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    public String getStringValue()
    {
        // _logger.entering("StarProperty", "getStringValue");
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
        // _logger.entering("StarProperty", "getDoubleValue");
        return Double.parseDouble(_value.toString());
    }

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    public boolean getBooleanValue()
    {
        // _logger.entering("StarProperty", "getBooleanValue");
        return Boolean.parseBoolean(_value.toString());
    }

    /**
     * Return whether the star property has a value.
     *
     * @return true if a value is set, false otherwise.
     */
    public boolean hasValue()
    {
        // _logger.entering("StarProperty", "hasValue");
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
        // _logger.entering("StarProperty", "setOrigin");
        _origin = origin;
    }

    /**
     * Get the origin of the star property as a String object.
     *
     * @return a String object representing the star property origin.
     */
    public String getOrigin()
    {
        // _logger.entering("StarProperty", "getOrigin");
        return _origin;
    }

    /**
     * Return whether the star property has a catalog origin.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasOrigin()
    {
        // _logger.entering("StarProperty", "hasOrigin");
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
        // _logger.entering("StarProperty", "setConfidence");
        _confidence = confidence;
    }

    /**
     * Get the confidence index of the star property as a String object.
     *
     * @return a String object representing the star property confidence index.
     */
    public String getConfidence()
    {
        // _logger.entering("StarProperty", "getConfidence");
        return _confidence;
    }

    /**
     * Return whether the star property has a confidence index.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    public boolean hasConfidence()
    {
        // _logger.entering("StarProperty", "hasConfidence");
        if (_confidence.length() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Set the URL of the star property.
     *
     * @param url the new star property URL.
     */
    public void setURL(String url)
    {
        // _logger.entering("StarProperty", "setURL");
        _url = url;
    }

    /**
     * Get the URL of the star property as a String object.
     *
     * @return a String object representing the star property URL, null otherwise.
     */
    public String getURL()
    {
        // _logger.entering("StarProperty", "getURL");
        if (hasURL() == false)
        {
            return null;
        }

        if (hasValue() == false)
        {
            return null;
        }

        // Convert the current value to HTML compatible encoding
        String encodedValue = URLEncoder.encode(getStringValue());

        // Forge the URL by replacing any '${...}' token with the current value
        String url = _url.replaceAll("[${].+[}]", encodedValue);

        return url;
    }

    /**
     * Return whether the star property has an URL.
     *
     * @return true if a URL is set, false otherwise.
     */
    public boolean hasURL()
    {
        // _logger.entering("StarProperty", "hasURL");
        if (_url.length() <= 0)
        {
            return false;
        }

        // If more than, or less than 1 '${...}' token in the URL
        String[] array = _url.split("[$]");

        if (array.length != 2)
        {
            // Discard this URL
            return false;
        }

        return true;
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
        //_logger.entering("StarProperty", "compareTo");
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
