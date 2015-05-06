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
    /** Store the dist column name */
    public static final String DistColumnName = "dist";
    /** Store column names of each multiplicity column to consider */
    public static final String[] MultiplicityColumnNames = {"MultFlag", "BinFlag", "SBC9"};
    /** Store column names of each orbit separation column to consider */
    public static final String[] OrbitSeparationColumnNames = {"sep1", "sep2"};
    /** Store the RAJ2000 column name */
    public static final String RAJ2000ColumnName = "RAJ2000";
    /** Store the RAJ2000 column name */
    public static final String DEJ2000ColumnName = "DEJ2000";
    /** Store the SpType column name */
    public static final String SpTypeColumnName = "SpType";
    /** Store the variability flag 1 column name */
    public static final String VarFlag1ColumnName = "VarFlag1";
    /** Store the variability flag 2 column name */
    public static final String VarFlag2ColumnName = "VarFlag2";
    /** Store the variability flag 3 column name */
    public static final String VarFlag3ColumnName = "VarFlag3";
    /** Store the vis2 column name */
    public static final String Vis2ColumnName = "vis2";
    /** Store the vis2Err column name */
    public static final String Vis2ErrColumnName = "vis2Err";
    /** Store the deletedFlag column name */
    public static final String DeletedFlagColumnName = "deletedFlag";
    /** Store the ObjTypes column name */
    public static final String ObjTypesColumnName = "ObjTypes";
    /* dynamic columns */
    /** Store the RAdeg column name */
    public static final String RADegColumnName = "RAdeg";
    /** Store the DEdeg column name */
    public static final String DEDegColumnName = "DEdeg";
    /** Store the rowIdx column name */
    public static final String RowIdxColumnName = "rowIdx";
    /** Store the rowIdx column name */
    public static final String OtherRowIdxColumnName = "otherRowIdx";
    /* diff only */
    /** Store the left column name */
    public static final String LeftColumnName = "left";
    /** Store the right column name */
    public static final String RightColumnName = "right";
    /** Store the diff column name */
    public static final String DiffColumnName = "diff";
    /* members */
    /** star list meta data */
    private final StarListMeta _metaData;
    /** cached flag indicating if this star list has some deleted stars */
    private boolean _hasDeletedStars = false;

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
        return _metaData.getPropertyIndexByName(groupName);
    }

    /**
     * Return the column ID of the deletedFlag column
     * @return column ID of the deletedFlag column
     */
    public int getDeletedFlagColumnID() {
        return getColumnIdByName(DeletedFlagColumnName);
    }

    /**
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars() {
        return _hasDeletedStars;
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

        boolean deleted = false;

        final int deletedFlagColumnID = getDeletedFlagColumnID();
        if (deletedFlagColumnID != -1) {
            List<StarProperty> star;
            StarProperty deletedFlag;

            for (int rowId = 0; rowId < size; rowId++) {
                star = get(rowId);

                deletedFlag = star.get(deletedFlagColumnID);

                if (deletedFlag == null || deletedFlag.getBooleanValue()) {
                    deleted = true;
                } else {
                    outputList.add(star);
                }
            }

            // trim to size:
            outputList.trimToSize();

        } else {
            // copy given star list:
            outputList.addAll(this);
        }

        _logger.debug("_hasDeletedStars: {}", deleted);

        _hasDeletedStars = deleted;

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
