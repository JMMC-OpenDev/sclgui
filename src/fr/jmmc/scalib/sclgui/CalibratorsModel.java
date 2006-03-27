/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsModel.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import cds.savot.model.*;

import cds.savot.pull.*;

import cds.savot.writer.*;

import jmmc.mcs.log.MCSLogger;

import java.io.StringBufferInputStream;

import java.util.*;

import javax.swing.table.DefaultTableModel;


/**
 * Calibrators model.
 *
 * This class play the role of the mediator between any registered JTable views
 * and a VOTable originally given as a simple string. This is why its extends
 * DefaultTableModel (JTable data source).
 * It also implements Observer in order to be automatically notified any time
 * one of the filters managed by a registered instance of FiltersModel changes.
 */
public class CalibratorsModel extends DefaultTableModel implements Observer
{
    /** Original VOTable as a string */
    private String _voTable;

    /** Original VOTable as a star list */
    private StarList _originalStarList;

    /** Displayed star list (filtered and removed-star free) */
    private StarList _currentStarList;

    /** JTable column names */
    private Vector _columnNames;

    /** Filters */
    private FiltersModel _filtersModel;

    /**
     * Constructor.
     *
     * @param filtersModel the filter manager to monitor for changes.
     */
    public CalibratorsModel(FiltersModel filtersModel)
    {
        _filtersModel         = filtersModel;

        _originalStarList     = new StarList();
        _currentStarList      = new StarList();

        _columnNames          = new Vector();
    }

    /**
     * Parse a given string as a VOTable and update any attached JTable to show
     * its content.
     */
    public void parseVOTable(String voTable)
    {
        MCSLogger.trace();

        _voTable = voTable;

        // Clear all the internal list before new parsing
        _originalStarList.clear();
        _currentStarList.clear();
        _columnNames.clear();

        // Put the whole VOTable file into memory
        SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(
                    _voTable), SavotPullEngine.FULL, "UTF-8");

        // Parse the VOTable
        SavotVOTable parsedVOTable = parser.getVOTable();

        // Get the VOTable resources
        ResourceSet resourceSet = parsedVOTable.getResources();

        // Get the first table of the first resource
        // TODO this is may not be compatible with other VOTable than JMMC ones
        SavotResource resource = (SavotResource) resourceSet.getItemAt(0);
        SavotTable    table    = (SavotTable) resource.getTables().getItemAt(0);
        GroupSet      groupSet = table.getGroups();

        /*
         * For each group of the table, put its name and its index in a
         * hashtable, and put its name in a vector for JTable columns title
         * definition.
         */

        // TODO this is may not be compatible with other VOTable than JMMC ones
        Hashtable groupNameToGroupId = new Hashtable();

        for (int groupId = 0; groupId < groupSet.getItemCount(); groupId++)
        {
            SavotGroup currentGroup = (SavotGroup) groupSet.getItemAt(groupId);

            String     groupName    = currentGroup.getName();

            groupNameToGroupId.put(groupName, new Integer(groupId));
            _columnNames.add(groupName);
        }

        // Add the group name to group id conversion table to the star list
        _originalStarList.setHashTable(groupNameToGroupId);

        // For each data row
        TRSet rows = resource.getTRSet(0);

        for (int rowId = 0; rowId < rows.getItemCount(); rowId++)
        {
            // Get the data corresponding to the current row
            TDSet row = rows.getTDSet(rowId);

            // For each group of the row create a star property of each 3 cells
            Vector starProperties = new Vector();

            for (int groupId = 0; groupId < groupSet.getItemCount();
                    groupId++)
            {
                /*
                 * The index of first cell of the current group (as there is
                 * always 3 cells for each group).
                 */
                int mainGroupCellId = 3 * groupId;

                /*
                 * Create a new StarProperty instance from the retrieved value,
                 * origin and confidence.
                 */
                StarProperty starProperty = new StarProperty();

                // Store the group value (always the first group cell)
                starProperty.setValue(row.getContent(mainGroupCellId + 0));
                // Store the group origin (always the second group cell)
                starProperty.setOrigin(row.getContent(mainGroupCellId + 1));
                // Store the group confidence (always the third group cell)
                starProperty.setConfidence(row.getContent(mainGroupCellId + 2));

                // Add the newly created star property to the star property list
                starProperties.add(starProperty);
            }

            // Store each VOTable row as a list of star properties
            _originalStarList.add(starProperties);
        }

        // Update any attached observer
        update(null, null);
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        // TODO : the clone operation should only be done when the ay filter
        // has been deactivated, otherwise currentStarList is sufficient

        // Back up the original list for later use (reset, updated filter list)
        _currentStarList = (StarList) _originalStarList.clone();

        // Filter the displayed stra list
        _filtersModel.process(_currentStarList);

        // As a DefaultTableModel instance, set all the JTable needed vectors
        setDataVector(_currentStarList, _columnNames);

        // Ask all the attached JTable views to update
        fireTableDataChanged();
    }
}
/*___oOo___*/
