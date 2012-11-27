/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

/**
 * Meta data of a star property.
 */
public final class StarPropertyMeta {
    /* members */

    /** property name */
    private final String _name;
    /** property data type as Class */
    private final Class<?> _classType;
    /** property description */
    private final String _description;
    /** property ucd (1.0) */
    private final String _ucd;
    /** optional property unit */
    private final String _unit;
    /** optional link URL */
    private final String _url;

    /**
     * Protected constructor
     * @param name property name
     * @param classType property data type as Class
     * @param description property description
     * @param ucd property ucd
     * @param unit optional property unit
     * @param url optional link URL
     */
    StarPropertyMeta(final String name, final Class<?> classType, final String description, final String ucd, final String unit, final String url) {
        this._name = name;
        this._classType = classType;
        this._description = description;
        this._ucd = ucd;
        this._unit = unit;

        // If more than, or less than 1 '${...}' token in the URL
        final String[] array = url.split("[$]");

        // Discard this URL
        this._url = (array.length == 2) ? url : "";
    }

    /**
     * Return the property name
     * @return property name
     */
    public String getName() {
        return _name;
    }

    /**
     * Return the property data type as Class
     * @return property data type as Class
     */
    public Class<?> getClassType() {
        return _classType;
    }

    /**
     * Return the property description
     * @return property description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Return the property ucd (1.0)
     * @return property ucd
     */
    public String getUcd() {
        return _ucd;
    }

    /**
     * Return the optional property unit
     * @return optional property unit
     */
    public String getUnit() {
        return _unit;
    }

    /**
     * Return the optional link URL
     * @return optional link URL
     */
    public String getUrl() {
        return _url;
    }

    @Override
    public String toString() {
        return _name + "[" + _classType.getSimpleName() + "][" + _unit + "][" + _description + ']';
    }
}
/*___oOo___*/
