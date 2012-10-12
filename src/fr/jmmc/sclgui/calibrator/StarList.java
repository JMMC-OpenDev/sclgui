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
            List<StarProperty> star;
            StarProperty deletedFlag;
            boolean starShouldBeRemoved;

            for (int i = 0, size = size(); i < size; i++) {
                star = get(i);
                if (star != null) {
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
     */
    public void removeAllDeletedStars() {
        _logger.entering("StarList", "removeAllDeletedStars");

        final int deletedFlagColumnID = getColumnIdByName(_deletedFlagColumnName);
        if (deletedFlagColumnID != -1) {
            List<StarProperty> star;
            int i = 0;
            int size = size();
            StarProperty deletedFlag;

            while (i < size) {
                star = get(i);
                if (star != null) {
                    deletedFlag = star.get(deletedFlagColumnID);

                    if (deletedFlag == null || deletedFlag.getBooleanValue()) {
                        remove(i);
                        size--;
                    } else {
                        // Jump to the next only if the current one as not been removed.
                        i++;
                    }
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
            List<StarProperty> star;
            StarProperty deletedFlag;

            for (int i = 0, size = size(); i < size; i++) {
                star = get(i);
                if (star != null) {
                    deletedFlag = star.get(deletedFlagColumnID);

                    if (deletedFlag != null) {
                        deletedFlag.setValue(Boolean.FALSE);
                    }
                }
            }
        }
    }
}
/*___oOo___*/
