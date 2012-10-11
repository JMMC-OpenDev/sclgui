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
     * Defines the hash table that links each colum group name to its ID.
     *
     * @param fieldIdToColNumber the new hash table.
     */
    public void setHashMap(final Map<String, Integer> fieldIdToColNumber) {
        _logger.entering("StarList", "setHashMap");

        _fieldIdToColNumber = fieldIdToColNumber;
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

            for (int i = 0, size = size(); i < size; i++) {
                final StarProperty deletedFlag = getPropertyAtRowByName(i, deletedFlagColumnID);

                if (deletedFlag != null) {
                    final boolean starShouldBeRemoved = deletedFlag.getBooleanValue();

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
     * Mark a star as deleted.
     * @param star 
     */
    public void markAsDeleted(final List<StarProperty> star) {
        _logger.entering("StarList", "markAsDeleted");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            final StarProperty deletedFlag = star.get(deletedFlagColumnID);
            deletedFlag.setValue(Boolean.TRUE);
        }
    }

    /**
     * Remove all "deleted" flagged stars.
     */
    public void removeAllDeletedStars() {
        _logger.entering("StarList", "removeAllDeletedStars");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            int i = 0;
            int size = size();

            while (i < size) {
                final StarProperty deletedFlag = getPropertyAtRowByName(i, deletedFlagColumnID);

                final boolean starShouldBeRemoved = (deletedFlag != null) ? deletedFlag.getBooleanValue() : true;

                if (starShouldBeRemoved) {
                    remove(i);
                    size--;
                } else {
                    // Jump to the next only if the current one as not been removed.
                    i++;
                }
            }
        }
    }

    /**
     * Mark all stars as NOT deleted.
     */
    public void undeleteAll() {
        _logger.entering("StarList", "undeleteAll");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {

            for (int i = 0, size = size(); i < size; i++) {
                final StarProperty deletedFlag = getPropertyAtRowByName(i, deletedFlagColumnID);
                if (deletedFlag != null) {
                    deletedFlag.setValue(Boolean.FALSE);
                }
            }
        }
    }

    /**
     * Get StarProperty at row by name.
     * @param row the star identifier
     * @param columnID the StarProperty id
     * @return the sought StarProperty, null otherwise.
     */
    private StarProperty getPropertyAtRowByName(final int row, final int columnID) {
        final List<StarProperty> star = get(row);
        if (star == null) {
            return null;
        }

        return star.get(columnID);
    }
}
/*___oOo___*/
