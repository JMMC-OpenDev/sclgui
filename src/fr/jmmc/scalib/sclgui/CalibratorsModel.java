/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsModel.java,v 1.16 2006-11-30 23:03:53 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.15  2006/11/30 16:02:33  lafrasse
 * Changed exporting rules to only export currently displayed stars.
 *
 * Revision 1.14  2006/11/29 17:33:28  lafrasse
 * Added support for stars flagged as deleted.
 * Added support undelete stars flagged as deleted.
 *
 * Revision 1.13  2006/11/23 16:24:41  lafrasse
 * Added query parameters parsing and loading from VOTable files.
 *
 * Revision 1.12  2006/11/18 23:00:24  lafrasse
 * Added support for unchanged modification detection before quitting.
 *
 * Revision 1.11  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.10  2006/10/16 14:29:51  lafrasse
 * Updated to reflect MCSLogger API changes.
 *
 * Revision 1.9  2006/06/23 09:19:41  mella
 * Jalopization
 *
 * Revision 1.8  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.7  2006/04/06 14:40:51  lafrasse
 * Updated to reflect StarProperty default constructor disparition in favour of a fully parametrized one
 *
 * Revision 1.6  2006/04/06 13:07:53  yvander
 * Add getVOTable method
 *
 * Revision 1.5  2006/03/31 14:30:42  mella
 * Support some color preferences changes
 *
 * Revision 1.4  2006/03/31 11:49:29  mella
 * Make file-open menu work
 *
 * Revision 1.3  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.2  2006/03/30 13:40:57  yvander
 * Mise en place des couleurs
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import cds.savot.model.*;

import cds.savot.pull.*;

import cds.savot.writer.*;

import jmmc.mcs.log.MCSLogger;

import java.io.*;

import java.net.*;

import java.util.*;

import javax.swing.table.DefaultTableModel;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


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

    /** Current VOTable as a string */
    private String _currentVOTable;

    /** Original VOTable as a star list
     * NOTE: this object is actually not really used could be deleted?
     */
    private StarList _originalStarList;

    /** User star list (filtered one included) */
    private StarList _currentStarList;

    /** Displayed star list (filtered and removed-star free) */
    private StarList _filteredStarList;

    /** JTable column names */
    private Vector _columnNames;

    /** Filters */
    private FiltersModel _filtersModel;

    /** Filters */
    private ParamSet _paramSet;

    /** Flag indicated whether data have changed or not */
    boolean _dataHaveChanged;

    /** Raw headers */
    public RowHeadersModel _rowHeadersModel;

    /**
     * Constructor.
     *
     * @param filtersModel the filter manager to monitor for changes.
     */
    public CalibratorsModel(FiltersModel filtersModel)
    {
        _filtersModel         = filtersModel;

        _originalStarList     = new StarList();
        _currentStarList      = (StarList) _originalStarList.clone();
        _filteredStarList     = (StarList) _originalStarList.clone();

        _columnNames          = new Vector();

        _paramSet             = null;
        _dataHaveChanged      = false;

        _rowHeadersModel      = new RowHeadersModel();
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        // TODO : the clone operation should only be done when the ay filter
        // has been deactivated, otherwise currentStarList is sufficient

        // Back up the original list for later use (reset, updated filter list)
        _filteredStarList = (StarList) _currentStarList.clone();

        // Filter the displayed stra list
        _filtersModel.process(_filteredStarList);

        // As a DefaultTableModel instance, set all the JTable needed vectors
        setDataVector(_filteredStarList, _columnNames);

        // Generate as many raw headers as data raws
        _rowHeadersModel.populate(_filteredStarList.size());

        // Ask all the attached JTable views to update
        fireTableDataChanged();

        // Remember that data have changed
        _dataHaveChanged = true;
    }

    /**
     * Returns whether data have changed or not.
     *
     * @return true if inner data have changed, false otherwise
     */
    public boolean dataHaveChanged()
    {
        MCSLogger.trace();

        return _dataHaveChanged;
    }

    /**
     * Returns false regardless of parameter values.
     *
     * @return false
     */
    public boolean isCellEditable(int row, int column)
    {
        MCSLogger.trace();

        return false;
    }

    /**
     * This method is called when a cell value is needed by the attached view.
     *
     * @return the specified cell value.
     */
    public Object getValueAt(int row, int column)
    {
        MCSLogger.trace();

        // Return the StarProperty value
        return getStarProperty(row, column).getValue();
    }

    /**
     * Parse a VOTablegetting its content from an BufferReader and update any attached JTable to show
     * its content.
     *
     * @param reader BufferedReader used to read the voTable.
     */
    public void parseVOTable(BufferedReader reader)
    {
        MCSLogger.trace();

        try
        {
            StringBuffer sb  = new StringBuffer();
            String       str;

            while ((str = reader.readLine()) != null)
            {
                sb.append(str);
            }

            reader.close();
            parseVOTable(sb.toString());
        }
        catch (Exception e)
        {
            // TODO handle exception
            e.printStackTrace();
        }
    }

    /**
     * Parse a given string as a VOTable and update any attached JTable to show
     * its content.
     * @param voTable the voTable content
     */
    public void parseVOTable(String voTable)
    {
        MCSLogger.trace();

        _voTable             = voTable;
        _currentVOTable      = voTable;

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

        // Retrieve VOTable parameters
        _paramSet = table.getParams();

        /*
         * For each group of the table, put its name and its index in a
         * hashtable, and put its name in a vector for JTable columns title
         * definition.
         */

        // @TODO this is may not be compatible with other VOTable than JMMC ones
        Hashtable groupNameToGroupId = new Hashtable();

        for (int groupId = 0; groupId < groupSet.getItemCount(); groupId++)
        {
            SavotGroup currentGroup = (SavotGroup) groupSet.getItemAt(groupId);

            String     groupName    = currentGroup.getName();

            groupNameToGroupId.put(groupName, new Integer(groupId));
            _columnNames.add(groupName);

            // @todo : store the datatype to later affect the right object (amongst String, Double, RA & Dec) as each starProperty value.
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

                // Store the group value (always the first group cell)
                Object value = row.getContent(mainGroupCellId + 0);

                // Store the group origin (always the second group cell)
                String origin = row.getContent(mainGroupCellId + 1);

                // Store the group confidence (always the third group cell)
                String confidence = row.getContent(mainGroupCellId + 2);

                /*
                 * Create a new StarProperty instance from the retrieved value,
                 * origin and confidence.
                 */
                StarProperty starProperty = new StarProperty(value, origin,
                        confidence);

                // Add the newly created star property to the star property list
                starProperties.add(starProperty);
            }

            // Store each VOTable row as a list of star properties
            _originalStarList.add(starProperties);
        }

        // Copy content of originalStarList into currentStarList
        _currentStarList = (StarList) _originalStarList.clone();

        // Remove all the stars flagged as deleted
        _currentStarList.removeAllDeletedStars();

        // Update any attached observer
        update(null, null);
    }

    /**
     * Give back the VOTable parameters.
     *
     * @return a ParamSet.
     */
    public ParamSet getParamSet()
    {
        MCSLogger.trace();

        return _paramSet;
    }

    /**
     * getStarProperty.
     *
     * Return the StarProperty corresponding to the cell.
     */
    public StarProperty getStarProperty(int row, int column)
    {
        MCSLogger.trace();

        // The real column index
        Vector starsProperties = (Vector) _filteredStarList.get(row);

        // Return the StarProperty
        if (starsProperties.get(column) instanceof StarProperty)
        {
            return (StarProperty) starsProperties.get(column);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private SavotVOTable getSavotVOTable(StarList starList)
    {
        MCSLogger.trace();

        // This method must be optimized (if no change occured do not generate
        // again...)

        // Put the whole original VOTable file into memory
        SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(
                    _voTable), SavotPullEngine.FULL, "UTF-8");

        // Parse the VOTable
        SavotVOTable parsedVOTable = parser.getVOTable();

        // Get the VOTable resources
        ResourceSet resourceSet = parsedVOTable.getResources();

        // Get the first table of the first resource
        // TODO this is may not be compatible with other VOTable than JMMC ones
        SavotResource resource = (SavotResource) resourceSet.getItemAt(0);

        // Remove every row
        TRSet rows = resource.getTRSet(0);
        rows.removeAllItems();

        // And create one row per star entry
        Enumeration stars = starList.elements();

        while (stars.hasMoreElements())
        {
            Vector      starProperties = (Vector) stars.nextElement();
            Enumeration props          = starProperties.elements();

            SavotTR     tr             = new SavotTR();
            TDSet       tds            = new TDSet();
            tr.setTDs(tds);
            rows.addItem(tr);

            while (props.hasMoreElements())
            {
                StarProperty prop         = (StarProperty) props.nextElement();

                SavotTD      valueTd      = new SavotTD();
                SavotTD      originTd     = new SavotTD();
                SavotTD      confidenceTd = new SavotTD();
                tds.addItem(valueTd);
                tds.addItem(originTd);
                tds.addItem(confidenceTd);
                valueTd.setContent(prop.getStringValue());
                originTd.setContent(prop.getOrigin());
                confidenceTd.setContent(prop.getConfidence());
            }
        }

        return parsedVOTable;
    }

    /**
     * Open the given file as a VOTable.
     */
    public void openFile(File file)
    {
        MCSLogger.trace();

        try
        {
            // Get a BufferedReader from file
            String         fileName   = file.getAbsolutePath();
            FileReader     fileReader = new FileReader(fileName);
            BufferedReader in         = new BufferedReader(fileReader);

            // Build CalibratorModel and parse votable
            parseVOTable(in);
        }
        catch (Exception e)
        {
            // TODO handle this exception
            e.printStackTrace();
        }
    }

    /**
     * Save the current votable into the given file.
     */
    public void saveVOTableFile(File file)
    {
        MCSLogger.trace();

        String       filename = file.getAbsolutePath();

        SavotVOTable voTable  = getSavotVOTable(_originalStarList);
        SavotWriter  wd       = new SavotWriter();
        wd.generateDocument(voTable, filename);
    }

    /**
     * getVOTable.
     *
     * @return the VOTable corresponding to the SearchCal initial result.
     */
    public String getVOTable()
    {
        MCSLogger.trace();

        SavotVOTable          voTable      = getSavotVOTable(_currentStarList);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SavotWriter           wd           = new SavotWriter();
        wd.generateDocument(voTable, outputStream);

        // The original data
        return outputStream.toString();
    }

    /**
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars()
    {
        MCSLogger.trace();

        return _originalStarList.hasSomeDeletedStars();
    }

    /**
     * Delete the shown elements according given index. The shown elements are
     * the not filtered elements.
     *
     * @param indices array of indices.
     */
    public void deleteStars(int[] indices)
    {
        MCSLogger.trace();

        // Remove each selected row
        for (int i = 0; i < indices.length; i++)
        {
            Vector star = (Vector) _filteredStarList.get(indices[i]);
            _currentStarList.markAsDeleted(star);
            _currentStarList.removeElement(star);
        }

        update(null, null);
    }

    /**
     * Undelete all stars previously flagged as deleted.
     */
    public void undeleteStars()
    {
        MCSLogger.trace();

        // Unflag all previously flagged stars
        _originalStarList.undeleteAll();

        // Copy content of originalStarList into currentStarList
        _currentStarList = (StarList) _originalStarList.clone();

        update(null, null);
    }

    /**
     * Convert a given VOTable file to the CSV format.
     *
     * @param in the file to convert
     * @param out the converted file
     */
    public void exportCurrentVOTableToCSV(File out)
    {
        MCSLogger.trace();

        URL xslURL = MainWindow.class.getResource("voTableToCSV.xsl");
        applyXSLTranformationOnCurrentVOTable(out, xslURL);
    }

    /**
     * Convert a given VOTable file to the HTML format.
     *
     * @param in the file to convert
     * @param out the converted file
     */
    public void exportCurrentVOTableToHTML(File out)
    {
        MCSLogger.trace();

        URL xslURL = MainWindow.class.getResource("voTableToHTML.xsl");
        applyXSLTranformationOnCurrentVOTable(out, xslURL);
    }

    /**
     * Apply the given xsl transformation on the current VOTable.
     *
     * @param outputFile the File in which the transformation result should be written.
     * @param xslFile the XSL file containing the transformation to be applied
     */
    private void applyXSLTranformationOnCurrentVOTable(File outputFile,
        URL xslFile)
    {
        MCSLogger.trace();

        try
        {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource("" +
                        xslFile));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            String currentVOTable = getVOTable();
            Source source         = new StreamSource(new StringBufferInputStream(
                        currentVOTable));
            Result result         = new StreamResult(new FileOutputStream(
                        outputFile));

            // Apply the xsl file to the source file and write the result to
            // the output file
            xformer.transform(source, result);
        }
        catch (FileNotFoundException e)
        {
            MCSLogger.error("File not found '" + e + "'");
        }
        catch (TransformerConfigurationException e)
        {
            // An error occurred in the XSL file
            MCSLogger.error("One error occured into the xsl file '" + xslFile +
                "'");
        }
        catch (TransformerException e)
        {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator  = e.getLocator();
            int           col      = locator.getColumnNumber();
            int           line     = locator.getLineNumber();
            String        publicId = locator.getPublicId();
            String        systemId = locator.getSystemId();
            MCSLogger.error("One error occured applying xsl (xsl='" + xslFile +
                "', error on line " + line + " column " + col + ")");
        }
    }

    protected class RowHeadersModel extends DefaultTableModel
    {
        public RowHeadersModel()
        {
            // Initialize the model with 0 row and 0 column
            super(0, 0);

            // Add one column labeled "Index"
            addColumn("Index");
        }

        public boolean isCellEditable(int row, int column)
        {
            MCSLogger.trace();

            // Return always false as no row header should be editable
            return false;
        }

        public void populate(int nbOfRows)
        {
            MCSLogger.trace();

            // Empty all the current row headers
            dataVector.clear();

            // Generate as many row headers as the given number of data rows
            for (int i = 1; i < (nbOfRows + 1); i++)
            {
                addRow(new Object[] { i });
            }
        }
    }
}
/*___oOo___*/
