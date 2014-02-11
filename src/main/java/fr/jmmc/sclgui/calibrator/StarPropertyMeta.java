/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

/**
 * Meta data of a star property.
 */
public final class StarPropertyMeta {
    /* constants */

    /** undefined type */
    public static final int TYPE_ANY = 0;
    /** String type */
    public static final int TYPE_STRING = 1;
    /** boolean type */
    public static final int TYPE_BOOLEAN = 2;
    /** integer type */
    public static final int TYPE_INTEGER = 3;
    /** double type */
    public static final int TYPE_DOUBLE = 4;
    /** type to Class mapping */
    private static final Class<?>[] TYPE_TO_CLASS = new Class<?>[]{Object.class, String.class, Boolean.class, Integer.class, Double.class};

    /**
     * Return the data type as Class corresponding to the given data type as integer
     * @param type data type as integer
     * @return data type as Class
     */
    public static Class<?> getClassType(final int type) {
        return TYPE_TO_CLASS[type]; // no range check
    }
    /* members */
    /** property name */
    private final String _name;
    /** property data type as integer */
    private final int _type;
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
     * @param type property data type as integer
     * @param description property description
     * @param ucd property ucd
     * @param unit optional property unit
     * @param url optional link URL
     */
    StarPropertyMeta(final String name, int type, final String description, final String ucd, final String unit, final String url) {
        this._name = name;
        this._type = type;
        this._classType = getClassType(type);
        this._description = description;
        this._ucd = ucd;
        this._unit = unit;

        // If more than, or less than 1 '${...}' token in the URL
        final String[] array = url.split("[$]");

        // Discard this URL
        this._url = (array.length == 2) ? url : null;
    }

    /**
     * Return the property name
     * @return property name
     */
    public String getName() {
        return _name;
    }

    /**
     * Return the property data type as integer
     * @return property data type as integer
     */
    public int getType() {
        return _type;
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
