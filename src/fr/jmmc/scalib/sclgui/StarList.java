/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.util.*;
import java.util.logging.*;

/**
 * Vector of star property vector.
 */
public class StarList extends Vector {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.StarList");
    /** Hashtable linking each colum group name to its ID */
    private Hashtable _fieldIdToColNumber;

    /**
     * Default constructor
     */
    public StarList() {
        super();

        _fieldIdToColNumber = new Hashtable();
    }

    /**
     * Defines the hash table that links each colum group name to its ID.
     *
     * @param fieldIdToColNumber the new hash table.
     */
    public void setHashTable(Hashtable fieldIdToColNumber) {
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
            Vector star = (Vector) elementAt(i);
            int deletedFlagColumnID = getColumnIdByName("deletedFlag");
            StarProperty deletedFlag = (StarProperty) star.elementAt(deletedFlagColumnID);
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
            Vector star = (Vector) elementAt(i);
            int deletedFlagColumnID = getColumnIdByName("deletedFlag");
            StarProperty deletedFlag = (StarProperty) star.elementAt(deletedFlagColumnID);
            boolean starShouldBeRemoved = deletedFlag.getBooleanValue();

            if (starShouldBeRemoved == true) {
                removeElement(star);
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
            int deletedFlagColumnID = getColumnIdByName("deletedFlag");
            Vector star = (Vector) elementAt(i);
            StarProperty deletedFlag = (StarProperty) star.elementAt(deletedFlagColumnID);
            deletedFlag.setValue(Boolean.FALSE);
        }
    }
}
/*___oOo___*/
