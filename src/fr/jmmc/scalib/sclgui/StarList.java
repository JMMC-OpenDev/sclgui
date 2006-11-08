/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarList.java,v 1.3 2006-11-08 22:25:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.util.*;


/**
 * Vector of star property vector.
 */
public class StarList extends Vector
{
    /** Hashtable linking each colum group name to its ID */
    private Hashtable _fieldIdToColNumber = null;

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
}
/*___oOo___*/
