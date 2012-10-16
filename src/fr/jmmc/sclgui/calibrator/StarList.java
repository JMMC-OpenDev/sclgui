/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Vector of star property vector.
 * note: This structure is required / imposed by javax.swing.table.DefaultTableModel
 */
public final class StarList extends Vector<List<StarProperty>> {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(StarList.class.getName());
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** Store the vis2 column name */
    public static final String Vis2ColumnName = "vis2";
    /** Store the SpType column name */
    public static final String SpTypeColumnName = "SpType";
    /** Store the deletedFlag column name */
    private static final String _deletedFlagColumnName = "deletedFlag";
    /* members */
    /** HashMap linking each colum group name to its ID */
    private Map<String, Integer> _fieldIdToColNumber;

    /**
     * Default constructor
     */
    public StarList() {
        super();
        _fieldIdToColNumber = Collections.emptyMap();
    }

    /**
     * Constructor with given mapping
     * 
     * @param fieldIdToColNumber the new mapping
     */
    public StarList(final Map<String, Integer> fieldIdToColNumber) {
        super();
        _fieldIdToColNumber = fieldIdToColNumber;
    }

    /**
     * Defines the mapping that links each colum group name to its ID.
     *
     * @param fieldIdToColNumber the new mapping
     */
    public void setFieldIdToColNumberMap(final Map<String, Integer> fieldIdToColNumber) {
        _fieldIdToColNumber = fieldIdToColNumber;
    }

    /**
     * return the mapping that links each colum group name to its ID.
     *
     * @return mapping
     */
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
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars() {
        _logger.entering("StarList", "hasSomeDeletedStars");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            List<StarProperty> star;
            StarProperty deletedFlag;
            boolean starShouldBeRemoved;

            for (int rowId = 0, size = size(); rowId < size; rowId++) {
                star = get(rowId);

                deletedFlag = star.get(deletedFlagColumnID);

                if (deletedFlag != null) {
                    starShouldBeRemoved = deletedFlag.getBooleanValue();

                    if (starShouldBeRemoved) {
                        _logger.fine("hasSomeDeletedStars = 'true'");
                        return true;
                    }
                }
            }
        }
        _logger.fine("hasSomeDeletedStars = 'false'");
        return false;
    }

    /**
     * Mark the given stars as deleted.
     * @param stars list of stars to mark and rmove from this list 
     */
    public void markAsDeleted(final List<List<StarProperty>> stars) {
        _logger.entering("StarList", "markAsDeleted");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            StarProperty deletedFlag;

            for (List<StarProperty> star : stars) {
                if (star != null) {
                    deletedFlag = star.get(deletedFlagColumnID);
                    deletedFlag.setValue(Boolean.TRUE);
                }
            }
        }
    }

    /**
     * Remove all "deleted" flagged stars.
     * 
     * It is faster to add n times a star than remove n times (arrayCopy ...)
     * 
     * @return new star list without "deleted" stars.
     */
    public StarList removeAllDeletedStars() {
        _logger.entering("StarList", "removeAllDeletedStars");

        final int size = size();

        final StarList outputList = new StarList(getFieldIdToColNumberMap());

        // ensure max capacity:
        outputList.ensureCapacity(size);

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            List<StarProperty> star;
            StarProperty deletedFlag;

            for (int rowId = 0; rowId < size; rowId++) {
                star = get(rowId);

                deletedFlag = star.get(deletedFlagColumnID);

                if (!(deletedFlag == null || deletedFlag.getBooleanValue())) {
                    outputList.add(star);
                }
            }

            // trim to size:
            outputList.trimToSize();

        } else {
            // copy given star list:
            outputList.addAll(this);
        }

        return outputList;
    }

    /**
     * Mark all stars as NOT deleted.
     */
    public void undeleteAll() {
        _logger.entering("StarList", "undeleteAll");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            List<StarProperty> star;
            StarProperty deletedFlag;

            for (int rowId = 0, size = size(); rowId < size; rowId++) {
                star = get(rowId);
                deletedFlag = star.get(deletedFlagColumnID);

                if (deletedFlag != null) {
                    deletedFlag.setValue(Boolean.FALSE);
                }
            }
        }
    }

    /**
     * Returns a clone of this star list (shallow copy)
     * 
     * note: no more used
     *
     * @return  a clone of this star list
     */
    @Override
    public StarList clone() {

        // Note: Vector.clone() calls super.clone():
        // Vector<E> v = (Vector<E>) super.clone();
        // so internal fields are copied too like _fieldIdToColNumber (same pointer)
        // that's enough as this mapping must be the same (read-only) for all StarList copies.

        return (StarList) super.clone();
    }
}
/*___oOo___*/
