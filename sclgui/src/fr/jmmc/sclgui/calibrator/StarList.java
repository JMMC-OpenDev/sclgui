/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Vector of star property vector.
 */
public class StarList extends Vector<Vector<StarProperty>> {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(StarList.class.getName());
    private static final long serialVersionUID = 1L;
    /** HashMap linking each colum group name to its ID */
    private HashMap<String, Integer> _fieldIdToColNumber;

    /**
     * Default constructor
     */
    public StarList() {
        super();

        _fieldIdToColNumber = new HashMap<String, Integer>();
    }

    /**
     * Defines the hash table that links each colum group name to its ID.
     *
     * @param fieldIdToColNumber the new hash table.
     */
    public void setHashMap(HashMap<String, Integer> fieldIdToColNumber) {
        _logger.entering("StarList", "setHashTable");

        _fieldIdToColNumber = fieldIdToColNumber;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(String groupName) {
        _logger.entering("StarList", "getColumnIdByName");

        int columnId = -1;

        if (groupName != null) {
            Integer foundIndex = ((Integer) _fieldIdToColNumber.get(groupName));

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

        for (int i = 0; i < size(); i++) {

            StarProperty deletedFlag = getPropertyAtRowByName(i, "deletedFlag");
            boolean starShouldBeRemoved = deletedFlag.getBooleanValue();

            if (starShouldBeRemoved == true) {
                _logger.fine("hasSomeDeletedStars = 'true'");
                return true;
            }
        }

        _logger.fine("hasSomeDeletedStars = 'false'");
        return false;
    }

    /**
     * Mark a star as deleted.
     * @param star 
     */
    public void markAsDeleted(Vector star) {
        _logger.entering("StarList", "markAsDeleted");

        int deletedFlagColumnID = getColumnIdByName("deletedFlag");
        StarProperty deletedFlag = (StarProperty) star.elementAt(deletedFlagColumnID);
        deletedFlag.setValue(Boolean.TRUE);
    }

    /**
     * Remove all "deleted" flagged stars.
     */
    public void removeAllDeletedStars() {
        _logger.entering("StarList", "removeAllDeletedStars");

        int i = 0;
        while (i < size()) {
            StarProperty deletedFlag = getPropertyAtRowByName(i, "deletedFlag");
            boolean starShouldBeRemoved = deletedFlag.getBooleanValue();

            if (starShouldBeRemoved == true) {
                remove(i);
            } else {
                // Jump to the next only if the current one as not been removed.
                i++;
            }
        }
    }

    /**
     * Mark all stars as NOT deleted.
     */
    public void undeleteAll() {
        _logger.entering("StarList", "undeleteAll");

        for (int i = 0; i < size(); i++) {
            StarProperty deletedFlag = getPropertyAtRowByName(i, "deletedFlag");
            deletedFlag.setValue(Boolean.FALSE);
        }
    }

    /**
     * Get StarProperty at row by name.
     * @param row the star identifier
     * @param name the StarProperty name
     * @return the sought StarProperty, null otherwise.
     */
    private StarProperty getPropertyAtRowByName(int row, String name) {

        Vector<StarProperty> star = get(row);
        if (star == null) {
            return null;
        }

        int columnID = getColumnIdByName(name);
        if (columnID < 0) {
            return null;
        }

        return star.get(columnID);
    }
}
/*___oOo___*/
