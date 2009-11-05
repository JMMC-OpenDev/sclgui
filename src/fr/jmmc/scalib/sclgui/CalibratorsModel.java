/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CalibratorsModel.java,v 1.26 2009-11-05 14:16:28 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.25  2008/12/15 13:32:37  lafrasse
 * Corrected CSV and HTML export exception.
 *
 * Revision 1.24  2008/09/10 22:19:08  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Added clickable cell to open web page on star information from HIP and HD
 * catalog columns identifier.
 *
 * Revision 1.23  2007/10/09 14:38:12  lafrasse
 * Added the ability to return the current scenario and magnitude band.
 *
 * Revision 1.22  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.21  2007/06/21 07:43:23  lafrasse
 * Rationnalized XSLT sheets loading.
 *
 * Revision 1.20  2007/06/14 11:59:40  lafrasse
 * Added support for simple/detailled view.
 *
 * Revision 1.19  2007/05/28 16:25:49  lafrasse
 * Added support for typed values with column classes in order to trully support typed comparisons (eg: columns are not all treated like strings anymore !) for column sorting.
 *
 * Revision 1.18  2007/02/27 12:52:58  lafrasse
 * Completed Doxygen documentation.
 *
 * Revision 1.17  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.16  2006/11/30 23:03:53  lafrasse
 * Added a fixed column on the left with generated star IDs.
 *
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
package fr.jmmc.scalib.sclgui;

import cds.savot.model.*;

import cds.savot.pull.*;

import cds.savot.writer.*;

import java.io.*;

import java.net.*;

import java.util.*;
import java.util.logging.*;

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
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.CalibratorsModel");

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

    /** Flag indicating whether data have changed or not */
    boolean _dataHaveChanged;

    /** Raw headers */
    public RowHeadersModel _rowHeadersModel;

    /** Column data types */
    public Vector _columnClasses;

    /** Selected magnitude band */
    private String _magnitudeBand = "V";

    /** Selected scenarion */
    private Boolean _brightScenarioFlag = true;

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

        _columnClasses        = null;
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        _logger.entering("CalibratorsModel", "update");

        // OPTIMIZE : the clone operation should only be done when the ay filter
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

        // If the update was launched from TableSorter(just for a GUI refresh)
        if (arg != null)
        {
            String argClassName = arg.getClass().getName();

            if (argClassName.equals("fr.jmmc.scalib.sclgui.TableSorter") == true)
            {
                // Don't consider it as a data modification
                return;
            }
        }

        // Remember that data have changed
        _dataHaveChanged = true;
    }

    /**
     * Returns whether data have changed or not.
     *
     * @return true if inner data have changed, false otherwise.
     */
    public boolean dataHaveChanged()
    {
        _logger.entering("CalibratorsModel", "dataHaveChanged");

        return _dataHaveChanged;
    }

    /**
     * Returns false regardless of parameter values.
     *
     * Because the Jtable should not allow data edition at all.
     *
     * @param row
     * @param column
     *
     * @return false
     */
    public boolean isCellEditable(int row, int column)
    {
        _logger.entering("CalibratorsModel", "isCellEditable");

        // Fake editor in order to handle clickable cell to open page in browser (in TableSorter)
        return true;
    }

    /**
     * Called when a cell value is needed by the attached view.
     *
     * @param row
     * @param column
     *
     * @return the specified cell value.
     */
    public Object getValueAt(int row, int column)
    {
        _logger.entering("CalibratorsModel", "getValueAt");

        Object _value = getStarProperty(row, column).getValue();

        // Return the StarProperty value
        return _value;
    }

    /**
     * Called when a column class is needed by the attached view.
     *
     * @param column
     *
     * @return the specified cell value.
     */
    public Class getColumnClass(int column)
    {
        _logger.entering("CalibratorsModel", "getColumnClass");

        if (_columnClasses != null)
        {
            return (Class) _columnClasses.elementAt(column);
        }

        return null;
    }

    /**
     * Parse a VOTablegetting its content from an BufferReader and update any attached JTable to show
     * its content.
     *
     * @param reader BufferedReader used to read the voTable.
     */
    public void parseVOTable(BufferedReader reader) throws Exception
    {
        _logger.entering("CalibratorsModel", "parseVOTable");

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
            throw e;
        }
    }

    /**
     * Return the current magnitude band.
     *
     * @return the current magnitude band.
     */
    public String getMagnitudeBand()
    {
        _logger.entering("CalibratorsModel", "getMagnitudeBand");

        return _magnitudeBand;
    }

    /**
     * Return the current scenario bright flag.
     *
     * @return true wether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public Boolean getBrightScenarioFlag()
    {
        _logger.entering("CalibratorsModel", "getBrightScenarioFlag");

        return _brightScenarioFlag;
    }

    /**
     * Parse a given string as a VOTable and update any attached JTable to show
     * its content.
     *
     * @param voTable the string to parse.
     */
    public void parseVOTable(String voTable) throws Exception
    {
        _logger.entering("CalibratorsModel", "parseVOTable");

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
        // WARNING : this is not compatible with other VOTable than JMMC ones
        // (0 should not be used, but the name of the Resource instead)
        SavotResource resource = (SavotResource) resourceSet.getItemAt(0);
        SavotTable    table    = (SavotTable) resource.getTables().getItemAt(0);
        GroupSet      groupSet = table.getGroups();
        FieldSet      fieldSet = table.getFields();

        // Retrieve VOTable parameters
        _paramSet = table.getParams();

        /*
         * For each group of the table, put its name and its index in a
         * hashtable, and put its name in a vector for JTable columns title
         * definition.
         */

        // WARNING : this is not compatible with other VOTable than JMMC ones
        Hashtable groupNameToGroupId = new Hashtable();
        _columnClasses = new Vector();

        for (int groupId = 0; groupId < groupSet.getItemCount(); groupId++)
        {
            SavotGroup currentGroup = (SavotGroup) groupSet.getItemAt(groupId);

            String     groupName    = currentGroup.getName();

            // Associate the group name with its index as a table column
            groupNameToGroupId.put(groupName, new Integer(groupId));
            _columnNames.add(groupName);

            // Get back the field type
            SavotField field     = (SavotField) fieldSet.getItemAt(3 * groupId); // *3 as there is 3 fields per group
            String     fieldType = field.getDataType();

            if (fieldType != null)
            {
                // Default class
                Class columnClass = Object.class;

                if (fieldType.equals("char"))
                {
                    columnClass = String.class;
                }
                else if (fieldType.equals("float"))
                {
                    columnClass = Double.class;
                }
                else if (fieldType.equals("boolean"))
                {
                    columnClass = Boolean.class;
                }

                _columnClasses.add(columnClass);
            }
            else
            {
                throw new Exception("Invalid VOTable - empty fieldType");
            }
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

                // Get back the value type and convert the value accordinaly
                SavotField field     = (SavotField) fieldSet.getItemAt(mainGroupCellId +
                        0);
                String     fieldType = field.getDataType();

                if (fieldType.equals("char"))
                {
                    value = (String) value;
                }
                else if (fieldType.equals("float"))
                {
                    try
                    {
                        value = Double.valueOf((String) value);
                    }
                    catch (Exception e)
                    {
                        value = null;
                    }
                }
                else if (fieldType.equals("boolean"))
                {
                    try
                    {
                        value = Boolean.valueOf((String) value);
                    }
                    catch (Exception e)
                    {
                        value = null;
                    }
                }

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

        // Compute selected magnitude band and scenario
        Hashtable parameters = new Hashtable();

        for (int i = 0; i < _paramSet.getItemCount(); i++)
        {
            SavotParam param      = (SavotParam) _paramSet.getItemAt(i);
            String     paramName  = param.getName();
            String     paramValue = param.getValue();

            _logger.fine(paramName + " = '" + paramValue + "'");
            parameters.put(paramName, paramValue);
        }

        _magnitudeBand = (String) parameters.get("band");

        if (_magnitudeBand.matches("I") || _magnitudeBand.matches("J") ||
                _magnitudeBand.matches("H"))
        {
            _magnitudeBand = "K";
        }

        _brightScenarioFlag = Boolean.valueOf((String) parameters.get("bright"));
        _logger.fine("magnitude band = '" + _magnitudeBand +
            "'; bright scenario = '" + _brightScenarioFlag + "'.");

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
        _logger.entering("CalibratorsModel", "getParamSet");

        return _paramSet;
    }

    /**
     * Return the star property at the given row & column.
     *
     * @param row
     * @param column
     *
     * @return the StarProperty corresponding to the cell.
     */
    public StarProperty getStarProperty(int row, int column)
    {
        _logger.entering("CalibratorsModel", "getStarProperty");

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
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(String groupName)
    {
        _logger.entering("CalibratorsModel", "getColumnIdByName");

        return _originalStarList.getColumnIdByName(groupName);
    }

    /**
     * Return a SavotVOTable object of the given StarList object.
     *
     * @param starList the list of stars to be converted.
     *
     * @return a Savot VOTable object.
     */
    private SavotVOTable getSavotVOTable(StarList starList)
    {
        _logger.entering("CalibratorsModel", "getSavotVOTable");

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
        // WARNING : this is not compatible with other VOTable than JMMC ones
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
     *
     * @param file the file to be read.
     */
    public void openFile(File file) throws Exception
    {
        _logger.entering("CalibratorsModel", "openFile");

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
            throw e;
        }
    }

    /**
     * Save the original star list into the given file.
     *
     * @param file the file to be written.
     */
    public void saveVOTableFile(File file)
    {
        _logger.entering("CalibratorsModel", "saveVOTableFile");

        String       filename = file.getAbsolutePath();

        SavotVOTable voTable  = getSavotVOTable(_originalStarList);
        SavotWriter  wd       = new SavotWriter();
        wd.generateDocument(voTable, filename);
    }

    /**
     * Return a string containing the current star list as a VOTable.
     *
     * @return the VOTable corresponding to the SearchCal initial result.
     */
    public String getVOTable()
    {
        _logger.entering("CalibratorsModel", "getVOTable");

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
        _logger.entering("CalibratorsModel", "hasSomeDeletedStars");

        return _originalStarList.hasSomeDeletedStars();
    }

    /**
     * Delete the shown elements according to the given row indexes. The shown
     * elements are the not filtered elements.
     *
     * @param indices indices of the row to be removed.
     */
    public void deleteStars(int[] indices)
    {
        _logger.entering("CalibratorsModel", "deleteStars");

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
        _logger.entering("CalibratorsModel", "undeleteStars");

        // Unflag all previously flagged stars
        _originalStarList.undeleteAll();

        // Copy content of originalStarList into currentStarList
        _currentStarList = (StarList) _originalStarList.clone();

        update(null, null);
    }

    /**
     * Convert a given VOTable file to the CSV format.
     *
     * @param out the converted file
     */
    public void exportCurrentVOTableToCSV(File out)
    {
        _logger.entering("CalibratorsModel", "exportCurrentVOTableToCSV");

        applyXSLTranformationOnCurrentVOTable(out, "sclguiVOTableToCSV.xsl");
    }

    /**
     * Convert a given VOTable file to the HTML format.
     *
     * @param out the converted file
     */
    public void exportCurrentVOTableToHTML(File out)
    {
        _logger.entering("CalibratorsModel", "exportCurrentVOTableToHTML");

        applyXSLTranformationOnCurrentVOTable(out, "sclguiVOTableToHTML.xsl");
    }

    /**
     * Apply the given xsl transformation on the current VOTable.
     *
     * @param outputFile the File in which the transformation result should be written.
     * @param xslFile the XSL file containing the transformation to be applied
     */
    private void applyXSLTranformationOnCurrentVOTable(File outputFile,
        String xslFileName)
    {
        _logger.entering("CalibratorsModel",
            "applyXSLTranformationOnCurrentVOTable");

        URL xslFile = SearchCalibrators.getSharedInstance()
                                       .getURLFromResourceFilename(xslFileName);

        if (xslFile == null)
        {
            _logger.severe("Could not load XSL file '" + xslFileName + "'.");

            return;
        }

        try
        {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(
                        xslFile.openStream()));

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
        catch (FileNotFoundException ex)
        {
            _logger.log(Level.SEVERE, "File not found", ex);
        }
        catch (TransformerConfigurationException ex)
        {
            // An error occurred in the XSL file
            _logger.log(Level.SEVERE,
                "One error occured into the XSL file '" + xslFile + "'", ex);
        }
        catch (TransformerException ex)
        {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator  = ex.getLocator();
            int           col      = locator.getColumnNumber();
            int           line     = locator.getLineNumber();
            String        publicId = locator.getPublicId();
            String        systemId = locator.getSystemId();
            _logger.log(Level.SEVERE,
                "One error occured while applying XSL file '" + xslFile +
                "', on line '" + line + "' and column '" + col + "'", ex);
        }
        catch (IOException ex)
        {
            // An error occurred while opening the URL
            _logger.log(Level.SEVERE,
                "One error occured while opening URL '" + xslFile + "'", ex);
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
            _logger.entering("RowHeadersModel", "isCellEditable");

            // Return always false as no row header should be editable
            return false;
        }

        public void populate(int nbOfRows)
        {
            _logger.entering("RowHeadersModel", "populate");

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
