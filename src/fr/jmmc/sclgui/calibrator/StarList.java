/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import java.util.List;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vector of star property vector.
 * note: This structure is required / imposed by javax.swing.table.DefaultTableModel
 */
public final class StarList extends Vector<List<StarProperty>> {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(StarList.class.getName());
    /** Empty Star List instance */
    public static final StarList EMPTY_LIST = new StarList();
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** Store the vis2 column name */
    public static final String Vis2ColumnName = "vis2";
    /** Store the SpType column name */
    public static final String SpTypeColumnName = "SpType";
    /** Store the deletedFlag column name */
    private static final String _deletedFlagColumnName = "deletedFlag";
    /* members */
    /** star list meta data */
    private final StarListMeta _metaData;

    /**
     * Empty star list Constructor
     */
    StarList() {
        super();
        _metaData = new StarListMeta();
    }

    /**
     * Constructor with given star list meta data
     * 
     * @param meta star list meta data
     */
    public StarList(final StarListMeta meta) {
        super();
        _metaData = meta;
    }

    /**
     * Return the star list meta data
     * @return star list meta data
     */
    public StarListMeta getMetaData() {
        return _metaData;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(final String groupName) {
        return _metaData.getColumnIdByName(groupName);
    }

    /**
     * Return the column ID of the deletedFlag column
     * @return column ID of the deletedFlag column
     */
    private int getDeletedFlagColumnID() {
        return getColumnIdByName(_deletedFlagColumnName);
    }

    /**
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars() {
        final int deletedFlagColumnID = getDeletedFlagColumnID();
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
                        _logger.debug("hasSomeDeletedStars = 'true'");
                        return true;
                    }
                }
            }
        }
        _logger.debug("hasSomeDeletedStars = 'false'");
        return false;
    }

    /**
     * Mark the given stars as deleted.
     * @param stars list of stars to mark and rmove from this list 
     */
    public void markAsDeleted(final List<List<StarProperty>> stars) {
        final int deletedFlagColumnID = getDeletedFlagColumnID();
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
        final int size = size();

        final StarList outputList = new StarList(_metaData);

        // ensure max capacity:
        outputList.ensureCapacity(size);

        final int deletedFlagColumnID = getDeletedFlagColumnID();
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
        final int deletedFlagColumnID = getDeletedFlagColumnID();
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
     * note: no more used so an illegal state exception is thrown
     *
     * @return  a clone of this star list
     * 
     * @throws IllegalStateException as it is not implemented.
     */
    @Override
    public StarList clone() throws IllegalStateException {
        throw new IllegalStateException("StarList.clone() is not implemented !");
    }
}
/*___oOo___*/
