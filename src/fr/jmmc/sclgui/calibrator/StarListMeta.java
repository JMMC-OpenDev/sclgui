/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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
    /** Map linking each colum group name to its ID */
    private final Map<String, Integer> _fieldIdToColNumber;

    /**
     * Protected constructor for empty meta data
     */
    StarListMeta() {
        super();
        _propertyMetaList = Collections.emptyList();
        _fieldIdToColNumber = Collections.emptyMap();
    }

    /**
     * Protected constructor
     * @param capacity number of star property meta
     */
    StarListMeta(final int capacity) {
        super();
        _propertyMetaList = new ArrayList<StarPropertyMeta>(capacity);
        _fieldIdToColNumber = new HashMap<String, Integer>(capacity);
    }

    /**
     * Add the given star property meta data
     * @param propertyMeta star property meta data to add
     */
    void addPropertyMeta(final StarPropertyMeta propertyMeta) {
        _propertyMetaList.add(propertyMeta);

        if (_logger.isDebugEnabled()) {
            _logger.debug("addPropertyMeta: property {} @ {}", propertyMeta.getName(), _nProperties);
        }

        // Associate the group name with its index as a table column
        final Integer oldIndex = _fieldIdToColNumber.put(propertyMeta.getName(), Integer.valueOf(_nProperties));

        if (oldIndex != null) {
            _logger.warn("Duplicate column [{}]found at indexes {} - {}", propertyMeta.getName(), oldIndex, Integer.valueOf(_nProperties));
        }

        // increment then:
        _nProperties++;
    }

    /**
     * Return an array of property classes (for performance)
     * @return array of property classes
     */
    Class<?>[] getPropertyClasses() {
        final Class<?>[] colClasses = new Class<?>[_nProperties];
        int i = 0;
        for (StarPropertyMeta propertyMeta : _propertyMetaList) {
            colClasses[i++] = propertyMeta.getClassType();
        }
        return colClasses;
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
     * Return the mapping that links each colum group name to its ID.
     *
     * @return mapping
     */
    // TODO: rename
    public Map<String, Integer> getFieldIdToColNumberMap() {
        return _fieldIdToColNumber;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    // TODO: rename
    public int getColumnIdByName(final String groupName) {
        int columnId = -1;

        if (groupName != null) {
            final Integer foundIndex = _fieldIdToColNumber.get(groupName);

            if (foundIndex != null) {
                columnId = foundIndex.intValue();
            }
        }
        return columnId;
    }

    /**
     * Return the property name of the property at the given index
     *
     * @param index property index
     *
     * @return property name or "" if index is out of bounds
     */
    public String getPropertyName(final int index) {
        if (index >= 0 && index < _nProperties) {
            return _propertyMetaList.get(index).getName();
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
        if (index >= 0 && index < _nProperties) {
            return _propertyMetaList.get(index).getClassType();
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
        if (index >= 0 && index < _nProperties) {
            return _propertyMetaList.get(index).getDescription();
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
        if (index >= 0 && index < _nProperties) {
            return _propertyMetaList.get(index).getUnit();
        }
        return "";
    }

    /**
     * Return the optional property url of the property at the given index
     * @param index
     * @return property url or "" if undefined
     */
    public String getPropertyUrl(final int index) {
        if (index >= 0 && index < _nProperties) {
            return _propertyMetaList.get(index).getUrl();
        }
        return "";
    }
}
/*___oOo___*/
