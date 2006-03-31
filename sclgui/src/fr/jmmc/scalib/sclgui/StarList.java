/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarList.java,v 1.2 2006-03-31 08:53:20 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
    // Trace has been deactvated because of numerous call

    /** DOCUMENT ME! */
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
     * DOCUMENT ME!
     *
     * @param fieldIdToColNumber DOCUMENT ME!
     */
    public void setHashTable(Hashtable fieldIdToColNumber)
    {
        MCSLogger.trace();

        _fieldIdToColNumber = fieldIdToColNumber;
    }

    /**
     * DOCUMENT ME!
     *
     * @param groupName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnIdByName(String groupName)
    {
        //MCSLogger.trace();
        Integer index = ((Integer) _fieldIdToColNumber.get(groupName));

        return index.intValue();
    }
}
/*___oOo___*/
