/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarList.java,v 1.5 2007-02-13 13:58:44 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/11/29 17:33:28  lafrasse
 * Added support for stars flagged as deleted.
 * Added support undelete stars flagged as deleted.
 *
 * Revision 1.3  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.*;

import java.util.*;


/**
 * Vector of star property vector.
 */
public class StarList extends Vector
{
    /** Hashtable linking each colum group name to its ID */
    private Hashtable _fieldIdToColNumber;

    /**
     * Default constructor
     */
    public StarList()
    {
        super();

        _fieldIdToColNumber = new Hashtable();
    }

    /**
     * Defines the hashtable that links each colum group name to its ID.
     *
     * @param fieldIdToColNumber the new hastable.
     */
    public void setHashTable(Hashtable fieldIdToColNumber)
    {
        MCSLogger.trace();

        _fieldIdToColNumber = fieldIdToColNumber;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(String groupName)
    {
        MCSLogger.trace();

        Integer index = ((Integer) _fieldIdToColNumber.get(groupName));

        return index.intValue();
    }

    /**
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars()
    {
        for (int i = 0; i < size(); i++)
        {
            Vector       star                = (Vector) elementAt(i);
            int          deletedFlagColumnID = getColumnIdByName("deletedFlag");
            StarProperty deletedFlag         = (StarProperty) star.elementAt(deletedFlagColumnID);
            boolean      starShouldBeRemoved = deletedFlag.getBooleanValue();

            if (starShouldBeRemoved == true)
            {
                MCSLogger.debug("hasSomeDeletedStars = 'true'");

                return true;
            }
        }

        MCSLogger.debug("hasSomeDeletedStars = 'false'");

        return false;
    }

    /**
     * Mark a star as deleted.
     */
    public void markAsDeleted(Vector star)
    {
        int          deletedFlagColumnID = getColumnIdByName("deletedFlag");
        StarProperty deletedFlag         = (StarProperty) star.elementAt(deletedFlagColumnID);
        deletedFlag.setValue(Boolean.TRUE);
    }

    /**
     * Remove all "deleted" flagged stars.
     */
    public void removeAllDeletedStars()
    {
        int i = 0;

        while (i < size())
        {
            Vector       star                = (Vector) elementAt(i);
            int          deletedFlagColumnID = getColumnIdByName("deletedFlag");
            StarProperty deletedFlag         = (StarProperty) star.elementAt(deletedFlagColumnID);
            boolean      starShouldBeRemoved = deletedFlag.getBooleanValue();

            if (starShouldBeRemoved == true)
            {
                removeElement(star);
            }
            else
            {
                // Jump to the next only if the current one as not been removed.
                i++;
            }
        }
    }

    /**
     * Mark all stars as NOT deleted.
     */
    public void undeleteAll()
    {
        for (int i = 0; i < size(); i++)
        {
            int          deletedFlagColumnID = getColumnIdByName("deletedFlag");
            Vector       star                = (Vector) elementAt(i);
            StarProperty deletedFlag         = (StarProperty) star.elementAt(deletedFlagColumnID);
            deletedFlag.setValue(Boolean.FALSE);
        }
    }
}
/*___oOo___*/
