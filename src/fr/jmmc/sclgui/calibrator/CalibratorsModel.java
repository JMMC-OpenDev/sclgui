/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import cds.savot.model.FieldSet;
import cds.savot.model.GroupSet;
import cds.savot.model.LinkSet;
import cds.savot.model.ParamSet;
import cds.savot.model.ResourceSet;
import cds.savot.model.SavotField;
import cds.savot.model.SavotGroup;
import cds.savot.model.SavotLink;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTD;
import cds.savot.model.SavotTR;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.model.TRSet;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;
import cds.savot.writer.SavotWriter;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.sclgui.SearchCal;
import fr.jmmc.sclgui.filter.FiltersModel;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
    private static final long serialVersionUID = 1L;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(CalibratorsModel.class.getName());
    /** 
     * Original VOTable as a string 
     * NOTE: it can waste a lot of memory for big tables.
     */
    private String _voTable = null;
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
    /** number of columns */
    private int _nColumns = 0;
    /** JTable column names */
    private Vector<String> _columnNames = null;
    /** JTable column URLs */
    private Vector _columnURLs = null;
    /** JTable column tool-tips */
    private Vector _columnDescriptions = null;
    /** JTable column units */
    private Vector _columnUnits = null;
    /** Column data types */
    public Vector _columnClasses = null;
    /** Filters */
    private FiltersModel _filtersModel = null;
    /** Filters */
    private ParamSet _paramSet = null;
    /** Flag indicating whether data have changed or not */
    private boolean _dataHaveChanged;
    /** Raw headers */
    public RowHeadersModel _rowHeadersModel = null;
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
     * @param o 
     * @param arg 
     * @sa java.util.Observer
     */
    @Override
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

            if (argClassName.equals(TableSorter.class.getName()) == true) {
                // Don't consider it as a data modification
                return;
            }
        }

        // Remember that data have changed
        _dataHaveChanged = true;

        // Ask for SAMP export menu enabling if needed
        boolean shouldBeEnabled = (_currentStarList.size() > 0);
        SearchCal._vo.couldEnableShareCalibratorsThroughSAMPAction(shouldBeEnabled);
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
     * Because the JTable should not allow data edition at all.
     *
     * @param row
     * @param column
     *
     * @return false
     */
    @Override
    public boolean isCellEditable(int row, int column) {
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
    public Object getValueAt(final int row, final int column) {
        final StarProperty property = getStarProperty(row, column);
        // Return the StarProperty value
        return (property != null) ? property.getValue() : null;
    }

    /**
     * Called when a column class is needed by the attached view.
     *
     * @param column
     *
     * @return the specified cell value.
     */
    @Override
    public Class getColumnClass(final int column) {
        if (_columnClasses != null && column >= 0 && column < _nColumns) {
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
    public String getHeaderTooltipForColumn(final int column) {
        if (column >= 0 && column < _nColumns) {
            return (String) _columnDescriptions.elementAt(column);
        }
        return "";
    }

    /**
     * Called when a column header unit is needed by the attached view.
     *
     * @param column
     *
     * @return the specified column header tooltip.
     */
    public String getHeaderUnitForColumn(final int column) {
        if (column >= 0 && column < _nColumns) {
            return (String) _columnUnits.elementAt(column);
        }
        return "";
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
     * @return true whether the query is of the bright type, otherwise false for
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
     * @throws IllegalArgumentException if given votable is not compatible with SearchCal format
     */
    public void parseVOTable(final String voTable) throws IllegalArgumentException{
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("CalibratorsModel.parseVOTable: VOTable size = " + voTable.length() + " bytes.");
        }
        
        // Perform GC if possible before loading any VOTable (memory consuming task)
        System.gc();
        
        _logger.fine("CalibratorsModel.parseVOTable: SavotPullParser(FULL) ...");
        
        // TODO: see getSavotVOTable: 
        // when loading Votable, keep the parsed SavotVOTable without tabledata
        // ie TRSet rows = resource.getTRSet(0).removeAllItems() (see below)

        // TODO: for very big tables, enhance SavotPullParser to parse TRs chunk by chunk (partial parsing)
        // for performance (less memory used)
        
        // TODO: DO not load the complete votable in memory (as string) and free such string once parsed !!
        // but use directly streams when loading files (for example jsdc.vot)
        // correct for remote votables (already loaded into memory by Axis SOAP)
        
        // Put the whole VOTable file into memory
        final SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(voTable), SavotPullEngine.FULL, "UTF-8");
        
        _logger.fine("CalibratorsModel.parseVOTable: SavotPullParser(FULL) done.");
        
        // Parse the VOTable
        final SavotVOTable parsedVOTable = parser.getVOTable();
        
        // Get the VOTable resources
        final ResourceSet resourceSet = parsedVOTable.getResources();

        // Get the first table of the first resource
        // WARNING : this is not compatible with other VOTable than JMMC ones
        // (0 should not be used, but the name of the Resource instead)
        final SavotResource resource = (SavotResource) resourceSet.getItemAt(0);
        
        // check that we found one votable
        if (resource==null) {
            _logger.warning("Incorrect VOTable format");
            throw new IllegalArgumentException("Incorrect VOTable format");
        }
        
        // check that the votable corresponds to the SearchCal VOTable format:
        if (!resource.getName().startsWith("SearchCal")) {
            _logger.warning("Ressource should be 'SearchCal' but is : "+resource.getName());
            throw new IllegalArgumentException("Incorrect VOTable format; expected one SearchCal VOTable");
        }
        
        // reset internal data model:
        _voTable = voTable;

        // Clear all the internal list before new parsing
        _originalStarList.clear();
        _currentStarList.clear();

        final SavotTable table = (SavotTable) resource.getTables().getItemAt(0);
        final GroupSet groupSet = table.getGroups();
        final FieldSet fieldSet = table.getFields();

        // Retrieve VOTable parameters
        _paramSet = table.getParams();

        /*
         * For each group of the table, put its name and its index in a
         * hashtable, and put its name in a vector for JTable columns title
         * definition.
         */
        final int nGroups = groupSet.getItemCount();

        final Map<String, Integer> groupNameToGroupId = new HashMap<String, Integer>(nGroups);
        _columnNames.clear();
        _columnNames.ensureCapacity(nGroups);
        _columnURLs = new Vector(nGroups);
        _columnDescriptions = new Vector(nGroups);
        _columnUnits = new Vector(nGroups);
        _columnClasses = new Vector(nGroups);

        for (int groupId = 0; groupId < nGroups; groupId++) {
            final SavotGroup currentGroup = (SavotGroup) groupSet.getItemAt(groupId);

            final String groupName = currentGroup.getName();

            // Associate the group name with its index as a table column
            groupNameToGroupId.put(groupName, Integer.valueOf(groupId));
            _columnNames.add(groupName);

            // Get back the field type
            final SavotField field = (SavotField) fieldSet.getItemAt(3 * groupId); // *3 as there is 3 fields per group
            final String fieldType = field.getDataType();

            // Get back the field link
            final LinkSet linkSet = field.getLinks();
            String url = "";

            if (linkSet.getItemCount() > 0) {
                final SavotLink savotLink = (SavotLink) linkSet.getItemAt(0);
                url = savotLink.getHref();
            }

            _columnURLs.add(url);

            // Get back the field description
            final String description = field.getDescription();
            _columnDescriptions.add(description);

            // Get back the field description
            final String unit = field.getUnit();
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
                throw new IllegalArgumentException("Invalid VOTable - empty fieldType for "+field.getName());
            }
        }
        
        _nColumns = _columnNames.size();

        // Add the group name to group id conversion table to the star list
        _originalStarList.setHashMap(groupNameToGroupId);

        // origin and confidence index are enumerations: use shared instance cache to use less memory:
        final Map<String, String> originValues = new HashMap<String, String>();
        final Map<String, String> confidenceValues = new HashMap<String, String>();
        
        // For each data row
        final TRSet rows = resource.getTRSet(0);
        final int nRows = rows.getItemCount();

        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("CalibratorsModel.parseVOTable: extracting " + nRows + " rows ...");
        }

        _originalStarList.ensureCapacity(nRows);

        for (int rowId = 0; rowId < nRows; rowId++) {
            // Get the data corresponding to the current row
            final TDSet row = rows.getTDSet(rowId);

            // For each group of the row create a star property of each 3 cells
            final Vector starProperties = new Vector(nGroups);

            for (int groupId = 0; groupId < nGroups; groupId++) {
                /*
                 * The index of first cell of the current group (as there is
                 * always 3 cells for each group).
                 */
                final int mainGroupCellId = 3 * groupId;

                // Store the group value (always the first group cell)
                Object value = row.getContent(mainGroupCellId);

                // Get back the value type and convert the value accordinaly
                final SavotField field = (SavotField) fieldSet.getItemAt(mainGroupCellId);
                final String fieldType = field.getDataType();

                if (fieldType.equals("char")) {
                    value = (String) value;
                    // replace "" by null to use less memory:
                    if (value.toString().length() == 0) {
                        value = null;
                    }
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
                // replace "" or "-" (blanking value used up to SearchCal release 4.4) 
                // by null to use less memory:
                if ((origin.length() == 0) || ("-".equals(origin))) {
                    origin = null;
                } else {
                    String originValue = originValues.get(origin);
                    if (originValue == null) {
                        originValues.put(origin, origin);
                    } else {
                        // use shared instance
                        origin = originValue;
                    }
                }

                // Store the group confidence (always the third group cell)
                String confidence = row.getContent(mainGroupCellId + 2);
                // replace "" by null to use less memory:
                if (confidence.length() == 0) {
                    confidence = null;
                } else {
                    String confidenceValue = confidenceValues.get(confidence);
                    if (confidenceValue == null) {
                        confidenceValues.put(confidence, confidence);
                    } else {
                        // use shared instance
                        confidence = confidenceValue;
                    }
                }

                /*
                 * Create a new StarProperty instance from the retrieved value,
                 * origin and confidence.
                 */
                final StarProperty starProperty = new StarProperty(value, origin, confidence, (String) _columnURLs.elementAt(groupId));

                // Add the newly created star property to the star property list
                starProperties.add(starProperty);
            }

            // Store each VOTable row as a list of star properties
            _originalStarList.add(starProperties);
            
            // free memory related to the previously read row:
            row.removeAllItems();
        }

        _logger.fine("CalibratorsModel.parseVOTable: rows read.");

        // Copy content of originalStarList into currentStarList
        _currentStarList = (StarList) _originalStarList.clone();

        // Remove all the stars flagged as deleted
        _currentStarList.removeAllDeletedStars();

        // Compute selected magnitude band and scenario
        final int nParams = _paramSet.getItemCount();
        final HashMap<String, String> parameters = new HashMap<String, String>(nParams);

        for (int i = 0; i < nParams; i++) {
            final SavotParam param = (SavotParam) _paramSet.getItemAt(i);
            final String paramName = param.getName();
            final String paramValue = param.getValue();

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine(paramName + " = '" + paramValue + "'");
            }
            parameters.put(paramName, paramValue);
        }

        _magnitudeBand = parameters.get("band");

        if ((_magnitudeBand != null) && 
            (_magnitudeBand.matches("I") || _magnitudeBand.matches("J") || _magnitudeBand.matches("H"))) {
            _magnitudeBand = "K";
        }

        _brightScenarioFlag = Boolean.valueOf(parameters.get("bright"));

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
    public StarProperty getStarProperty(final int row, final int column) {
        final Vector<StarProperty> star = getStar(row);
        if (star != null) {
            if (column >= 0 && column < _nColumns) {
                return star.get(column);
            }
        }

        _logger.severe("Could get StarProperty at row '" + row + "' and column '" + column + "'.");
        return null;
    }

    /**
     * Return the star at the given row.
     *
     * @param row the index of one star which in the filtered list of the model.
     *
     * @return the Star corresponding to the given index.
     */
    public Vector<StarProperty> getStar(final int row) {
        final Vector<StarProperty> star = (row >= 0 && row < _filteredStarList.size()) ? _filteredStarList.get(row) : null;

        if (star == null) {
            _logger.severe("Could get Star at row '" + row + "'.");
        }

        return star;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(final String groupName) {
        return _originalStarList.getColumnIdByName(groupName);
    }

    /**
     * Give back the column name from its ID.
     *
     * @param groupId 
     * @return name of the column's ID.
     */
    public String getColumnNameById(final int groupId) {
        if (groupId >= 0 && groupId < _nColumns) {
            return _columnNames.elementAt(groupId);
        }
        return "";
    }

    /**
     * Return a SavotVOTable object of the given StarList object.
     *
     * @param starList the list of stars to be converted.
     *
     * @return a SAVOT VOTable object, or null.
     */
    private SavotVOTable getSavotVOTable(final StarList starList) {
        _logger.entering("CalibratorsModel", "getSavotVOTable");

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
        
        // TODO: avoid using Savot to parse again votable as string again 
        // to only get table structure (fields / parameters)
        // solution: when loading Votable, keep the parsed SavotVOTable without tabledata
        // ie TRSet rows = resource.getTRSet(0).removeAllItems() (see below)

        // Put the whole original VOTable file into memory
        final SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(_voTable), SavotPullEngine.FULL, "UTF-8");

        // Parse the VOTable
        final SavotVOTable parsedVOTable = parser.getVOTable();

        // Get the VOTable resources
        final ResourceSet resourceSet = parsedVOTable.getResources();

        // Get the first table of the first resource
        // WARNING : this is not compatible with other VOTable than SearchCal ones
        final SavotResource resource = (SavotResource) resourceSet.getItemAt(0);

        // Remove every row
        final TRSet rows = resource.getTRSet(0);
        rows.removeAllItems();

        // shared empty TD instance
        final SavotTD emptyTD = new SavotTD();

        SavotTR tr;
        TDSet tdSet;
        SavotTD valueTd;
        SavotTD originTd;
        SavotTD confidenceTd;
        
        // And create one row per star entry
        for (Vector<StarProperty> starProperties : starList) {

            tr = new SavotTR();
            tdSet = new TDSet();
            tr.setTDs(tdSet);
            
            for (StarProperty prop : starProperties) {
                
                // value:
                if (prop.hasValue()) {
                    valueTd = new SavotTD();
                    valueTd.setContent(prop.getStringValue());
                } else {
                    valueTd = emptyTD;
                }
                tdSet.addItem(valueTd);
                
                // origin:
                if (prop.hasOrigin()) {
                    originTd = new SavotTD();
                    originTd.setContent(prop.getOrigin());
                } else {
                    originTd = emptyTD;
                }
                tdSet.addItem(originTd);
                
                // confidence index:
                if (prop.hasConfidence()) {
                    confidenceTd = new SavotTD();
                    confidenceTd.setContent(prop.getConfidence());
                } else {
                    confidenceTd = emptyTD;
                }
                tdSet.addItem(confidenceTd);
            }
            
            // add TR
            rows.addItem(tr);
        }

        return parsedVOTable;
    }

    /**
     * Open the given file as a VOTable.
     *
     * @param file the file to be read.
     * @throws IOException if I/O error occurs reading the file
     * @throws IllegalArgumentException if given votable is not compatible with SearchCal format
     */
    public void openFile(final File file) throws IOException, IllegalArgumentException {
        _logger.entering("CalibratorsModel", "openFile");

        // Read the buffer into memory into one String:
        // NOTE: it can waste a lot of memory for big tables.
        final String voTable = FileUtils.readFile(file);

        // Build CalibratorModel and parse votable
        parseVOTable(voTable);
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
                Vector<StarProperty> selectedStar = _filteredStarList.get(selectedIndex);
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

        final SavotWriter wd = new SavotWriter();
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

        // Use 128K buffer:
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128 * 1024);
        final SavotWriter wd = new SavotWriter();
        wd.generateDocument(voTable, outputStream);
        
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("CalibratorsModel.getVOTable: VOTable size = " + outputStream.size() + " bytes.");
        }
        
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
        SearchCal._vo.setShareCalibratorsThroughSAMPActionText(actionMenuText);
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
     * @param xslFileName the XSL file containing the transformation to be applied
     */
    private void applyXSLTranformationOnCurrentVOTable(File outputFile,
            String xslFileName) {
        _logger.entering("CalibratorsModel", "applyXSLTranformationOnCurrentVOTable");

        URL xslFile = SearchCal.getSharedInstance().getURLFromResourceFilename(xslFileName);

        if (xslFile == null) {
            _logger.severe("Could not load XSL file '" + xslFileName + "'.");

            return;
        }

        try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(xslFile.openStream()));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            String currentVOTable = getVOTable();
            Source source = new StreamSource(new StringBufferInputStream(currentVOTable));
            Result result = new StreamResult(new FileOutputStream(outputFile));

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

        private static final long serialVersionUID = 1L;

        RowHeadersModel() {
            // Initialize the model with 0 row and 0 column
            super(0, 0);

            // Add one column labeled "Index"
            addColumn("Index");
        }

        @Override
        public boolean isCellEditable(int row, int column) {
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
