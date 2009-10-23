/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarList.java,v 1.7 2008-09-10 22:38:44 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2007/06/14 08:43:00  lafrasse
 * Enhanced getColumnIdByName() by returning -1 if no index was found for the given group name.
 *
 * Revision 1.5  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
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
import java.util.logging.*;


/**
 * Vector of star property vector.
 */
public class StarList extends Vector
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.StarList");

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
    public int getColumnIdByName(String groupName)
    {
        _logger.entering("StarList", "getColumnIdByName");

        int columnId = -1;

        if (groupName != null)
        {
            Integer foundIndex = ((Integer) _fieldIdToColNumber.get(groupName));

            if (foundIndex != null)
            {
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
    public boolean hasSomeDeletedStars()
    {
        _logger.entering("StarList", "hasSomeDeletedStars");

        for (int i = 0; i < size(); i++)
        {
            Vector       star                = (Vector) elementAt(i);
            int          deletedFlagColumnID = getColumnIdByName("deletedFlag");
            StarProperty deletedFlag         = (StarProperty) star.elementAt(deletedFlagColumnID);
            boolean      starShouldBeRemoved = deletedFlag.getBooleanValue();

            if (starShouldBeRemoved == true)
            {
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
    public void markAsDeleted(Vector star)
    {
        _logger.entering("StarList", "markAsDeleted");

        int          deletedFlagColumnID = getColumnIdByName("deletedFlag");
        StarProperty deletedFlag         = (StarProperty) star.elementAt(deletedFlagColumnID);
        deletedFlag.setValue(Boolean.TRUE);
    }

    /**
     * Remove all "deleted" flagged stars.
     */
    public void removeAllDeletedStars()
    {
        _logger.entering("StarList", "removeAllDeletedStars");

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
        _logger.entering("StarList", "undeleteAll");

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
