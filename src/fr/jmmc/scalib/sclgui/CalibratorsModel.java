/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import cds.savot.model.*;

import cds.savot.pull.*;

import cds.savot.writer.*;
import fr.jmmc.mcs.util.FileUtils;

import java.io.*;

import java.net.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.table.DefaultTableModel;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * Calibrators model.
 *
 * This class play the role of the mediator between any registered JTable views
 * and a VOTable originally given as a simple string. This is why it extends
 * DefaultTableModel (JTable data source).
 * It also implements Observer in order to be automatically notified any time
 * one of the filters managed by a registered instance of FiltersModel changes.
 */
public class CalibratorsModel extends DefaultTableModel implements Observer {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.CalibratorsModel");
    /** Original VOTable as a string */
    private String _voTable = null;
    /** Current VOTable as a string */
    private String _currentVOTable = null;
    /** Original VOTable as a star list
     * NOTE: this object is actually not really used could be deleted?
     */
    private StarList _originalStarList = null;
    /** User star list (filtered one included) */
    private StarList _currentStarList = null;
    /** Displayed star list (filtered and removed-star free) */
    private StarList _filteredStarList = null;
    /** Store the selected stars displayed and updated by calibratorView */
    private int[] _selectedStarIndices = null;
    /** JTable column names */
    private Vector<String> _columnNames = null;
    /** JTable column URLs */
    private Vector _columnURLs = null;
    /** JTable column tooltips */
    private Vector _columnDescriptions = null;
    /** JTable column units */
    private Vector _columnUnits = null;
    /** Filters */
    private FiltersModel _filtersModel = null;
    /** Filters */
    private ParamSet _paramSet = null;
    /** Flag indicating whether data have changed or not */
    private boolean _dataHaveChanged;
    /** Raw headers */
    public RowHeadersModel _rowHeadersModel = null;
    /** Column data types */
    public Vector _columnClasses = null;
    /** Selected magnitude band */
    private String _magnitudeBand = "V";
    /** Selected scenario */
    private Boolean _brightScenarioFlag = true;

    /**
     * Constructor.
     *
     * @param filtersModel the filter manager to monitor for changes.
     */
    public CalibratorsModel(FiltersModel filtersModel) {
        _filtersModel = filtersModel;

        _originalStarList = new StarList();
        _currentStarList = (StarList) _originalStarList.clone();
        _filteredStarList = (StarList) _originalStarList.clone();

        _columnNames = new Vector();
        _columnURLs = new Vector();
        _columnDescriptions = new Vector();
        _columnUnits = new Vector();

        _paramSet = null;
        _dataHaveChanged = false;

        _rowHeadersModel = new RowHeadersModel();

        _columnClasses = null;
    }

    public int getTotalNumberOfStar() {
        return _originalStarList.size();
    }

    public int getHiddenNumberOfStar() {
        return (getTotalNumberOfStar() - _filteredStarList.size());
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg) {
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
        if (arg != null) {
            String argClassName = arg.getClass().getName();

            if (argClassName.equals("fr.jmmc.scalib.sclgui.TableSorter") == true) {
                // Don't consider it as a data modification
                return;
            }
        }

        // Remember that data have changed
        _dataHaveChanged = true;

        // Ask for SAMP export menu enabling if needed
        boolean shouldBeEnabled = (_currentStarList.size() > 0);
        SearchCalibrators._vo._shareCalibratorsThroughSAMPAction.couldBeEnabled(shouldBeEnabled);
    }

    /**
     * Returns whether data have changed or not.
     *
     * @return true if inner data have changed, false otherwise.
     */
    public boolean dataHaveChanged() {
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
    @Override
    public boolean isCellEditable(int row, int column) {
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
    @Override
    public Object getValueAt(int row, int column) {
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
    @Override
    public Class getColumnClass(int column) {
        _logger.entering("CalibratorsModel", "getColumnClass");

        if (_columnClasses != null) {
            return (Class) _columnClasses.elementAt(column);
        }

        return null;
    }

    /**
     * Called when a column header tooltip is needed by the attached view.
     *
     * @param column
     *
     * @return the specified column header tooltip.
     */
    public String getHeaderTooltipForColumn(int column) {
        return (String) _columnDescriptions.elementAt(column);
    }

    /**
     * Called when a column header unit is needed by the attached view.
     *
     * @param column
     *
     * @return the specified column header tooltip.
     */
    public String getHeaderUnitForColumn(int column) {
        return (String) _columnUnits.elementAt(column);
    }

    /**
     * Parse a VOTablegetting its content from an BufferReader and update any attached JTable to show
     * its content.
     *
     * @param reader BufferedReader used to read the voTable.
     */
    public void parseVOTable(final BufferedReader reader) throws Exception {
        _logger.entering("CalibratorsModel", "parseVOTable");

        try {
            // 32K buffer at least:
            final StringBuilder sb = new StringBuilder(32 * 1024);
            String str;

            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }

            parseVOTable(sb.toString());

        } catch (Exception e) {
            throw e;
        } finally {
            FileUtils.closeFile(reader);
        }
    }

    /**
     * Return the current magnitude band.
     *
     * @return the current magnitude band.
     */
    public String getMagnitudeBand() {
        _logger.entering("CalibratorsModel", "getMagnitudeBand");

        return _magnitudeBand;
    }

    /**
     * Return the current scenario bright flag.
     *
     * @return true wether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public Boolean getBrightScenarioFlag() {
        _logger.entering("CalibratorsModel", "getBrightScenarioFlag");

        return _brightScenarioFlag;
    }

    /**
     * Parse a given string as a VOTable and update any attached JTable to show
     * its content.
     *
     * @param voTable the string to parse.
     */
    public void parseVOTable(String voTable) throws Exception {
        _logger.entering("CalibratorsModel", "parseVOTable");

        _voTable = voTable;
        _currentVOTable = voTable;

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
        SavotTable table = (SavotTable) resource.getTables().getItemAt(0);
        GroupSet groupSet = table.getGroups();
        FieldSet fieldSet = table.getFields();

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
        _columnURLs = new Vector();
        _columnDescriptions = new Vector();
        _columnUnits = new Vector();

        for (int groupId = 0; groupId < groupSet.getItemCount(); groupId++) {
            SavotGroup currentGroup = (SavotGroup) groupSet.getItemAt(groupId);

            String groupName = currentGroup.getName();

            // Associate the group name with its index as a table column
            groupNameToGroupId.put(groupName, new Integer(groupId));
            _columnNames.add(groupName);

            // Get back the field type
            SavotField field = (SavotField) fieldSet.getItemAt(3 * groupId); // *3 as there is 3 fields per group
            String fieldType = field.getDataType();

            // Get back the field link
            LinkSet linkSet = field.getLinks();
            String url = "";

            if (linkSet.getItemCount() > 0) {
                SavotLink savotLink = (SavotLink) linkSet.getItemAt(0);
                url = savotLink.getHref();
            }

            _columnURLs.add(url);

            // Get back the field description
            String description = field.getDescription();
            _columnDescriptions.add(description);

            // Get back the field description
            String unit = field.getUnit();
            _columnUnits.add(unit);

            if (fieldType != null) {
                // Default class
                Class columnClass = Object.class;

                if (fieldType.equals("char")) {
                    columnClass = String.class;
                } else if (fieldType.equals("float")) {
                    columnClass = Double.class;
                } else if (fieldType.equals("boolean")) {
                    columnClass = Boolean.class;
                }

                _columnClasses.add(columnClass);
            } else {
                throw new Exception("Invalid VOTable - empty fieldType");
            }
        }

        // Add the group name to group id conversion table to the star list
        _originalStarList.setHashTable(groupNameToGroupId);

        // For each data row
        TRSet rows = resource.getTRSet(0);

        for (int rowId = 0; rowId < rows.getItemCount(); rowId++) {
            // Get the data corresponding to the current row
            TDSet row = rows.getTDSet(rowId);

            // For each group of the row create a star property of each 3 cells
            Vector starProperties = new Vector();

            for (int groupId = 0; groupId < groupSet.getItemCount();
                    groupId++) {
                /*
                 * The index of first cell of the current group (as there is
                 * always 3 cells for each group).
                 */
                int mainGroupCellId = 3 * groupId;

                // Store the group value (always the first group cell)
                Object value = row.getContent(mainGroupCellId + 0);

                // Get back the value type and convert the value accordinaly
                SavotField field = (SavotField) fieldSet.getItemAt(mainGroupCellId
                        + 0);
                String fieldType = field.getDataType();

                if (fieldType.equals("char")) {
                    value = (String) value;
                } else if (fieldType.equals("float")) {
                    try {
                        value = Double.valueOf((String) value);
                    } catch (Exception e) {
                        value = null;
                    }
                } else if (fieldType.equals("boolean")) {
                    try {
                        value = Boolean.valueOf((String) value);
                    } catch (Exception e) {
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
                        confidence, (String) _columnURLs.elementAt(groupId));

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
        HashMap<String, String> parameters = new HashMap<String, String>();

        for (int i = 0; i < _paramSet.getItemCount(); i++) {
            SavotParam param = (SavotParam) _paramSet.getItemAt(i);
            String paramName = param.getName();
            String paramValue = param.getValue();

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine(paramName + " = '" + paramValue + "'");
            }
            parameters.put(paramName, paramValue);
        }

        _magnitudeBand = (String) parameters.get("band");

        if (_magnitudeBand.matches("I") || _magnitudeBand.matches("J")
                || _magnitudeBand.matches("H")) {
            _magnitudeBand = "K";
        }

        _brightScenarioFlag = Boolean.valueOf((String) parameters.get("bright"));
        
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("magnitude band = '" + _magnitudeBand
                + "'; bright scenario = '" + _brightScenarioFlag + "'.");
        }

        // Update any attached observer
        update(null, null);
    }

    /**
     * Give back the VOTable parameters.
     *
     * @return a ParamSet.
     */
    public ParamSet getParamSet() {
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
    public StarProperty getStarProperty(int row, int column) {
        _logger.entering("CalibratorsModel", "getStarProperty");

        // The real column index
        Vector starsProperties = (Vector) _filteredStarList.get(row);

        // Return the StarProperty
        if (starsProperties.get(column) instanceof StarProperty) {
            return (StarProperty) starsProperties.get(column);
        }
        // TODO put error log or use Vector<Vector> for starlists
        return null;
    }

    /**
     * Return the star at the given row.
     *
     * @param row the indice of one star which in the filtered list of the model.
     *
     * @return the Star corresponding to the given index.
     */
    public Vector getStar(int row) {
        _logger.entering("CalibratorsModel", "getStar");

        Object s = _filteredStarList.get(row);

        if (s instanceof Vector) {
            return (Vector) s;
        }
        // TODO put error log or use Vector<Vector> for starlists
        return null;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(String groupName) {
        _logger.entering("CalibratorsModel", "getColumnIdByName");

        return _originalStarList.getColumnIdByName(groupName);
    }

    /**
     * Give back the column name from its ID.
     *
     * @param groupID the column ID.
     *
     * @return name of the column's ID.
     */
    public String getColumnNameById(int groupId) {
        _logger.entering("CalibratorsModel", "getColumnNameById");

        return _columnNames.elementAt(groupId);
    }

    /**
     * Return a SavotVOTable object of the given StarList object.
     *
     * @param starList the list of stars to be converted.
     *
     * @return a Savot VOTable object, or null.
     */
    private SavotVOTable getSavotVOTable(StarList starList) {
        _logger.entering("CalibratorsModel", "getSavotVOTable");

        // @TODO : if no change occured do not generate again

        // If null received
        if (starList.size() <= 0) {
            _logger.warning("Could not process 'null' StarList");
            // Abort
            return null;
        }

        // If no Votable already loaded to be used as a template for columns descriptions
        if (_voTable.length() <= 0) {
            _logger.warning("Could not generate metadata template");
            // Abort
            return null;
        }

        // Put the whole original VOTable file into memory
        SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(
                _voTable), SavotPullEngine.FULL, "UTF-8");

        // Parse the VOTable
        SavotVOTable parsedVOTable = parser.getVOTable();

        // Get the VOTable resources
        ResourceSet resourceSet = parsedVOTable.getResources();

        // Get the first table of the first resource
        // WARNING : this is not compatible with other VOTable than SearchCal ones
        SavotResource resource = (SavotResource) resourceSet.getItemAt(0);

        // Remove every row
        TRSet rows = resource.getTRSet(0);
        rows.removeAllItems();

        // And create one row per star entry
        Enumeration stars = starList.elements();

        while (stars.hasMoreElements()) {
            Vector starProperties = (Vector) stars.nextElement();
            Enumeration props = starProperties.elements();

            SavotTR tr = new SavotTR();
            TDSet tds = new TDSet();
            tr.setTDs(tds);
            rows.addItem(tr);

            while (props.hasMoreElements()) {
                StarProperty prop = (StarProperty) props.nextElement();

                SavotTD valueTd = new SavotTD();
                SavotTD originTd = new SavotTD();
                SavotTD confidenceTd = new SavotTD();
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
    public void openFile(final File file) throws Exception {
        _logger.entering("CalibratorsModel", "openFile");

        try {
            // Get a BufferedReader from file
            final BufferedReader fileReader = new BufferedReader(new FileReader(file));

            // Build CalibratorModel and parse votable
            parseVOTable(fileReader);
            
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Save the stars associated to given indexes into the given file
     * or all stars if indices array is null/empty.
     * Stars are extracted from filtered list.
     *
     * @param file output file
     */
    public void saveSelectionAsVOTableFile(File file) {
        StarList selectedStarsList;

        // If some calibrators are currently selected
        if (_selectedStarIndices != null && _selectedStarIndices.length > 0) {
            // Compute a dedicated star list
            selectedStarsList = new StarList();
            for (int index = 0; index < _selectedStarIndices.length; index++) {
                int selectedIndex = _selectedStarIndices[index];
                // Use filtered star list because selection works on filtered list
                Object selectedStar = _filteredStarList.get(selectedIndex);
                selectedStarsList.add(selectedStar);
            }
        } else {
            // Use all visible calibrators if none explicitly slected
            selectedStarsList = _filteredStarList;
        }

        // Save either selected or all calibrators to file
        saveVOTableFile(file, selectedStarsList);
    }

    /**
     * Save the original star list into the given file.
     *
     * @param file the file to be written.
     */
    public void saveVOTableFile(File file) {
        saveVOTableFile(file, _originalStarList);
    }

    /**
     * Save the given star list into the given file.
     *
     * @param file the file to be written.
     * @param listToSave starList to save.
     */
    public void saveVOTableFile(File file, StarList listToSave) {
        _logger.entering("CalibratorsModel", "saveVOTableFile");

        String filename = file.getAbsolutePath();
        _logger.fine("Saving one starlist as votable into file " + filename);

        SavotVOTable voTable = getSavotVOTable(listToSave);
        if (voTable == null) {
            _logger.warning("Could not get SAVOT VOTable from given StarList");
            return;
        }

        SavotWriter wd = new SavotWriter();
        wd.generateDocument(voTable, filename);
    }

    /**
     * Return a string containing the current star list as a VOTable.
     *
     * @return the VOTable corresponding to the SearchCal initial result, or null.
     */
    public String getVOTable() {
        _logger.entering("CalibratorsModel", "getVOTable");

        SavotVOTable voTable = getSavotVOTable(_currentStarList);
        if (voTable == null) {
            _logger.warning("Could not get SAVOT VOTable from current StarList");
            return null;
        }

        SavotWriter wd = new SavotWriter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wd.generateDocument(voTable, outputStream);
        // The original data
        return outputStream.toString();
    }

    /**
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars() {
        _logger.entering("CalibratorsModel", "hasSomeDeletedStars");

        return _originalStarList.hasSomeDeletedStars();
    }

    /**
     * Delete the shown elements according to the given row indexes. The shown
     * elements are the not filtered elements.
     *
     * @param indices indices of the row to be removed.
     */
    public void deleteStars(int[] indices) {
        _logger.entering("CalibratorsModel", "deleteStars");

        // Remove each selected row
        for (int i = 0; i < indices.length; i++) {
            Vector star = (Vector) _filteredStarList.get(indices[i]);
            _currentStarList.markAsDeleted(star);
            _currentStarList.removeElement(star);
        }

        update(null, null);
    }

    /**
     * Undelete all stars previously flagged as deleted.
     */
    public void undeleteStars() {
        _logger.entering("CalibratorsModel", "undeleteStars");

        // Unflag all previously flagged stars
        _originalStarList.undeleteAll();

        // Copy content of originalStarList into currentStarList
        _currentStarList = (StarList) _originalStarList.clone();

        update(null, null);
    }

    /**
     * Store the selected indices of the calibrator view.
     * This method should only be called by calibratorView.
     * @param selectedStarIndices selected indices of the calibrator view table.
     */
    public void setSelectedStars(int[] selectedStarIndices) {
        _selectedStarIndices = selectedStarIndices;

        // Set SAMP action text according to current selection
        String actionMenuText = "Send All Calibrators to...";

        if ((_selectedStarIndices != null) && (_selectedStarIndices.length > 0)) {
            // When some calibrators selected
            actionMenuText = "Send Selected Calibrators to...";
        }

        // Update Menu label
        SearchCalibrators._vo._shareCalibratorsThroughSAMPAction.setText(actionMenuText);
    }

    /**
     * Convert a given VOTable file to the CSV format.
     *
     * @param out the converted file
     */
    public void exportCurrentVOTableToCSV(File out) {
        _logger.entering("CalibratorsModel", "exportCurrentVOTableToCSV");

        applyXSLTranformationOnCurrentVOTable(out, "sclguiVOTableToCSV.xsl");
    }

    /**
     * Convert a given VOTable file to the HTML format.
     *
     * @param out the converted file
     */
    public void exportCurrentVOTableToHTML(File out) {
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
            String xslFileName) {
        _logger.entering("CalibratorsModel",
                "applyXSLTranformationOnCurrentVOTable");

        URL xslFile = SearchCalibrators.getSharedInstance().getURLFromResourceFilename(xslFileName);

        if (xslFile == null) {
            _logger.severe("Could not load XSL file '" + xslFileName + "'.");

            return;
        }

        try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(
                    xslFile.openStream()));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            String currentVOTable = getVOTable();
            Source source = new StreamSource(new StringBufferInputStream(
                    currentVOTable));
            Result result = new StreamResult(new FileOutputStream(
                    outputFile));

            // Apply the xsl file to the source file and write the result to
            // the output file
            xformer.transform(source, result);
        } catch (FileNotFoundException ex) {
            _logger.log(Level.SEVERE, "File not found", ex);
        } catch (TransformerConfigurationException ex) {
            // An error occurred in the XSL file
            _logger.log(Level.SEVERE,
                    "One error occured into the XSL file '" + xslFile + "'", ex);
        } catch (TransformerException ex) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = ex.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();

            _logger.log(Level.SEVERE,
                    "One error occured while applying XSL file '" + xslFile
                    + "', on line '" + line + "' and column '" + col + "'", ex);
        } catch (IOException ex) {
            // An error occurred while opening the URL
            _logger.log(Level.SEVERE,
                    "One error occured while opening URL '" + xslFile + "'", ex);
        }
    }

    protected class RowHeadersModel extends DefaultTableModel {

        public RowHeadersModel() {
            // Initialize the model with 0 row and 0 column
            super(0, 0);

            // Add one column labeled "Index"
            addColumn("Index");
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            _logger.entering("RowHeadersModel", "isCellEditable");

            // Return always false as no row header should be editable
            return false;
        }

        public void populate(int nbOfRows) {
            _logger.entering("RowHeadersModel", "populate");

            // Empty all the current row headers
            dataVector.clear();

            // Generate as many row headers as the given number of data rows
            for (int i = 1; i < (nbOfRows + 1); i++) {
                addRow(new Object[]{i});
            }
        }
    }
}
/*___oOo___*/
