/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.util.NumberUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import fr.jmmc.jmcs.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Meta data of a star list.
 */
public final class StarListMeta {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(StarListMeta.class.getName());
    /* members */
    /** number of star properties */
    private int _nProperties = 0;
    /** List of star property meta data */
    private final List<StarPropertyMeta> _propertyMetaList;
    /** Map linking each property name to its index */
    private final Map<String, Integer> _mapPropertyIndexByName;

    /**
     * Protected constructor for empty meta data
     */
    StarListMeta() {
        super();
        _propertyMetaList = Collections.emptyList();
        _mapPropertyIndexByName = Collections.emptyMap();
    }

    /**
     * Protected constructor
     * @param capacity number of star property meta
     */
    StarListMeta(final int capacity) {
        super();
        _propertyMetaList = new ArrayList<StarPropertyMeta>(capacity);
        _mapPropertyIndexByName = new HashMap<String, Integer>(capacity);
    }

    /**
     * Add the given star property meta data
     * @param propertyMeta star property meta data to add
     * @return StarPropertyMeta index
     */
    int addPropertyMeta(final StarPropertyMeta propertyMeta) {
        _propertyMetaList.add(propertyMeta);

        if (_logger.isDebugEnabled()) {
            _logger.debug("addPropertyMeta: property {} @ {}", propertyMeta.getName(), _nProperties);
        }

        // Associate the group name with its index as a table column
        final Integer oldIndex = _mapPropertyIndexByName.put(propertyMeta.getName(), NumberUtils.valueOf(_nProperties));

        if (oldIndex != null) {
            _logger.warn("Duplicate column [{}]found at indexes {} - {}", propertyMeta.getName(), oldIndex, _nProperties);
        }

        // increment then:
        return _nProperties++;
    }

    /**
     * Return a vector of property names (for JTable DefaultTableModel)
     * @return vector of property names
     */
    Vector<String> getPropertyNames() {
        final Vector<String> colNames = new Vector<String>(_nProperties);

        for (StarPropertyMeta propertyMeta : _propertyMetaList) {
            colNames.add(propertyMeta.getName());
        }
        
        _logger.debug("PropertyNames: {}", colNames);
        
        return colNames;
    }

    /**
     * Return the number of star properties
     * @return number of star properties
     */
    public int getPropertyCount() {
        return _nProperties;
    }

    /**
     * Give back the property index from its name.
     *
     * @param propertyName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getPropertyIndexByName(final String propertyName) {
        int columnId = -1;

        if (propertyName != null) {
            final Integer foundIndex = _mapPropertyIndexByName.get(propertyName);

            if (foundIndex != null) {
                columnId = foundIndex.intValue();
            }
        }
        return columnId;
    }

    /**
     * Return the property meta at the given index
     *
     * @param index property index
     *
     * @return property meta or null if index is out of bounds
     */
    public StarPropertyMeta getPropertyMeta(final int index) {
        if (index >= 0 && index < _nProperties) {
            return _propertyMetaList.get(index);
        }
        return null;
    }

    /**
     * Return the property name of the property at the given index
     *
     * @param index property index
     *
     * @return property name or "" if index is out of bounds
     */
    public String getPropertyName(final int index) {
        final StarPropertyMeta propertyMeta = getPropertyMeta(index);
        if (propertyMeta != null) {
            return propertyMeta.getName();
        }
        return "";
    }

    /**
     * Return the property data type as Class of the property at the given index
     *
     * @param index property index
     *
     * @return property data type as Class or null if index is out of bounds
     */
    public Class<?> getPropertyClass(final int index) {
        final StarPropertyMeta propertyMeta = getPropertyMeta(index);
        if (propertyMeta != null) {
            return propertyMeta.getClassType();
        }

        return null;
    }

    /**
     * Return the property description of the property at the given index
     *
     * @param index property index
     *
     * @return property description or "" if index is out of bounds
     */
    public String getPropertyDescription(final int index) {
        final StarPropertyMeta propertyMeta = getPropertyMeta(index);
        if (propertyMeta != null) {
            return propertyMeta.getDescription();
        }
        return "";
    }

    /**
     * Return the property ucd (1.0) of the property at the given index
     *
     * @param index property index
     *
     * @return property ucd (1.0) or "" if index is out of bounds
     */
    public String getPropertyUcd(final int index) {
        final StarPropertyMeta propertyMeta = getPropertyMeta(index);
        if (propertyMeta != null) {
            return propertyMeta.getUcd();
        }
        return "";
    }

    /**
     * Return the property unit of the property at the given index
     *
     * @param index property index
     *
     * @return property unit or "" if index is out of bounds
     */
    public String getPropertyUnit(final int index) {
        final StarPropertyMeta propertyMeta = getPropertyMeta(index);
        if (propertyMeta != null) {
            return propertyMeta.getUnit();
        }
        return "";
    }

    /**
     * Return the optional property url of the property at the given index
     * @param index
     * @return property url or null if undefined
     */
    public String getPropertyUrl(final int index) {
        final StarPropertyMeta propertyMeta = getPropertyMeta(index);
        if (propertyMeta != null) {
            return propertyMeta.getUrl();
        }
        return null;
    }

    @Override
    public String toString() {
        return "StarListMeta[" + _nProperties + "]\n" + CollectionUtils.toString(_propertyMetaList, "\n    ", "{\n    ", "\n}");
    }
}
/*___oOo___*/
