/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import cds.savot.model.FieldSet;
import cds.savot.model.GroupSet;
import cds.savot.model.ParamSet;
import cds.savot.model.ResourceSet;
import cds.savot.model.SavotData;
import cds.savot.model.SavotField;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTD;
import cds.savot.model.SavotTR;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotTableData;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;
import cds.savot.writer.SavotWriter;
import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.task.TaskSwingWorker;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.jmcs.util.UrlUtils;
import fr.jmmc.jmcs.util.XmlFactory;
import fr.jmmc.sclgui.SearchCal;
import fr.jmmc.sclgui.filter.FacelessNonCalibratorsFilter;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calibrators model.
 *
 * This class play the role of the mediator between any registered JTable views
 * and a VOTable originally given as a simple string. This is why it extends
 * DefaultTableModel (JTable data source).
 * It also implements Observer in order to be automatically notified any time
 * one of the filters managed by a registered instance of FiltersModel changes.
 */
public final class CalibratorsModel extends DefaultTableModel implements Observer {

    /** default serial UID for Serializable interface */
    private final static long serialVersionUID = 1L;
    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(CalibratorsModel.class.getName());
    /** table size threshold to load them asynchronously */
    private final static int LOAD_TABLE_ASYNC_THRESHOLD = 5000;
    /** number of extra properties (dynamically added and computed in the GUI) */
    private final static int N_EXTRA_PROPERTIES = 8;
    /** parameter SearchCalGuiVersion (string) */
    public final static String PARAMETER_SCL_GUI_VERSION = "SearchCalGuiVersion";
    /** parameter SearchCalServerVersion (string) */
    public final static String PARAMETER_SCL_SERVER_VERSION = "SearchCalServerVersion";
    /** url token replace regexp */
    private final Pattern patternURL_REPLACE_TOKEN = Pattern.compile("[${].+[}]");
    /* members */
    /** Filters */
    private final FiltersModel _filtersModel;
    /** Store the current query model in order to allow later update to progress bar and parse query parameters (may be null) */
    final QueryModel _queryModel;
    /** Store the main application object used to perform menu activation (may be null) */
    VirtualObservatory _vo;
    /** filter non calibrators */
    private final FacelessNonCalibratorsFilter _facelessNonCalibratorsFilter;
    /** Savot VOTable structure without data (rows are removed) */
    SavotVOTable _parsedVOTable = null;
    /** Original VOTable as a star list */
    StarList _originalStarList;
    /** Only calibrator star list (non calibrator filtered) */
    private StarList _calibratorStarList;
    /** User star list (removed-star free but not filtered) */
    private StarList _currentStarList;
    /** Displayed star list in JTable (filtered) (required Vector type) */
    private StarList _filteredStarList;
    /** (cached) number of stars in filtered star list */
    int _nFilteredStars;
    /** optional rowIdx / index map */
    private Map<Integer, Integer> _filteredStarIndexMap = null;
    /** (cached) star list meta data */
    StarListMeta _starListMeta;
    /** (cached) number of star properties */
    int _nProperties;
    /** Store the selected stars displayed and updated by calibratorView */
    private int[] _selectedStarIndices = null;
    /** Filters */
    ParamSet _paramSet = null;
    /** Flag indicating whether data have changed or not */
    private boolean _dataHaveChanged;
    /** Raw headers */
    final RowHeadersModel _rowHeadersModel;
    /** Selected magnitude band */
    String _magnitudeBand = "V";
    /** Selected scenario */
    Boolean _brightScenarioFlag = true;
    /** custom view = star property names (diff tool) */
    String _customPropertyView = null;
    /** flag to enable/disable update events (ie multiple calls) */
    private boolean _autoUpdateEvent = true;

    /**
     * Constructor.
     *
     * @param filtersModel the filter manager to monitor for changes.
     * @param queryModel the query model to use its progress bar
     */
    public CalibratorsModel(final FiltersModel filtersModel, final QueryModel queryModel) {
        _filtersModel = filtersModel;
        _queryModel = queryModel;

        // create empty star lists:
        setOriginalStarList(StarList.EMPTY_LIST);
        _calibratorStarList = StarList.EMPTY_LIST;
        _currentStarList = StarList.EMPTY_LIST;
        setFilteredStarList(StarList.EMPTY_LIST);

        _paramSet = null;
        _dataHaveChanged = false;

        _rowHeadersModel = new RowHeadersModel();

        // Non Calibrators filter (faceless):
        _facelessNonCalibratorsFilter = new FacelessNonCalibratorsFilter();

        // This faceless filter should always be activated
        // (set once here as no GUI can change it anywhere else)
        _facelessNonCalibratorsFilter.setEnabled(Preferences.getInstance().getPreferenceAsBoolean(PreferenceKey.FILTER_NON_CALIBRATORS));
    }

    /**
     * Define the VirtualObservatory instance
     * @param vo VirtualObservatory instance
     */
    public void setVirtualObservatory(VirtualObservatory vo) {
        this._vo = vo;
    }

    /**
     * Return the total number of stars
     * @return total number of stars
     */
    public int getTotalNumberOfStar() {
        return _originalStarList.size();
    }

    /**
     * Return the number of displayed stars
     * @return number of displayed stars
     */
    public int getNumberOfDisplayedStar() {
        return _nFilteredStars;
    }

    /**
     * Return the number of hidden stars
     * @return number of hidden stars
     */
    public int getHiddenNumberOfStar() {
        return (getTotalNumberOfStar() - _nFilteredStars);
    }

    /**
     * @param o 
     * @param arg 
     * @sa java.util.Observer
     */
    @Override
    public void update(final Observable o, final Object arg) {
        if (this._autoUpdateEvent) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("update: o {} with arg = {}", o, arg);
            }

            // Filter the displayed star list
            setFilteredStarList(_filtersModel.process(_currentStarList));

            // As a DefaultTableModel instance, set all the JTable needed vectors
            // it calls fireTableStructureChanged()
            setDataVector(_filteredStarList, _starListMeta.getPropertyNames());

            // Generate as many raw headers as data raws
            _rowHeadersModel.populate(_nFilteredStars);

            // Ask all the attached JTable views to update
            fireTableDataChanged();

            // If the update was launched from TableSorter (just for a GUI refresh)
            if (arg != null) {
                if (TableSorter.class == arg.getClass()) {
                    // Don't consider it as a data modification
                    return;
                }
            }

            // Remember that data have changed (unless change went from us)
            if (arg != this) {
                _dataHaveChanged = true;
            }

            // Diff tool:
            if (_vo != null) {
                // Ask for SAMP export menu enabling if needed
                _vo.couldEnableShareCalibratorsThroughSAMPAction(_nFilteredStars != 0);
            }
        }
    }

    /**
     * Enable / Disable the automatic update of the observation when any swing component changes.
     * Return its previous value.
     *
     * Typical use is as following :
     * // disable the automatic update observation :
     * final boolean prevAutoUpdateObservation = this.setAutoUpdateObservation(false);
     * try {
     *   // operations ...
     *
     * } finally {
     *   // restore the automatic update observation :
     *   this.setAutoUpdateObservation(prevAutoUpdateObservation);
     * }
     *
     * @param value new value
     * @return previous value
     */
    public boolean setAutoUpdate(final boolean value) {
        // first backup the state of the automatic update :
        final boolean previous = this._autoUpdateEvent;

        // then change its state :
        this._autoUpdateEvent = value;

        if (value) {
            // force an update now:
            update(null, null);
        }

        // return previous state :
        return previous;
    }

    /**
     * Returns whether data have changed or not.
     *
     * @return true if inner data have changed, false otherwise.
     */
    public boolean haveDataChanged() {
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
    public boolean isCellEditable(final int row, final int column) {
        // Fake editor in order to handle clickable cell to open page in browser (in TableSorter)
        return hasURL(column);
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
     * Return the original star list (diff tool only)
     * @return original star list
     */
    StarList getOriginalStarList() {
        return _originalStarList;
    }

    /**
     * Public setter for original star list and update also star list meta data (cached)
     * @param starList star list to set
     */
    private void setOriginalStarList(final StarList starList) {
        _originalStarList = starList;
        _starListMeta = starList.getMetaData();
        _nProperties = _starListMeta.getPropertyCount();
    }

    /**
     * Return the filtered star list (diff tool only)
     * @return filtered star list
     */
    StarList getFilteredStarList() {
        return _filteredStarList;
    }

    /**
     * Return the rowIdx / index map (diff tool only) lazily initialized
     * @return rowIdx / index map
     */
    Map<Integer, Integer> getFilteredStarIndexMap() {
        if (_filteredStarIndexMap == null) {
            final int size = _nFilteredStars;
            final Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>(size);

            if (size != 0) {
                final StarList starList = _filteredStarList;
                final int rowIdxIdx = starList.getColumnIdByName(StarList.RowIdxColumnName);

                if (rowIdxIdx != -1) {
                    Integer rowIdx;

                    for (int i = 0; i < size; i++) {
                        rowIdx = starList.get(i).get(rowIdxIdx).getInteger();

                        if (rowIdx != null) {
                            indexMap.put(rowIdx, NumberUtils.valueOf(i));
                        }
                    }

                }
            }
            _filteredStarIndexMap = indexMap;
        }
        return _filteredStarIndexMap;
    }

    /**
     * Protected setter for filtered star list and cache its size
     * @param starList star list to set
     */
    private void setFilteredStarList(final StarList starList) {
        _filteredStarList = starList;
        _nFilteredStars = starList.size();

        // clear lazy filter index map:
        _filteredStarIndexMap = null;
    }

    /**
     * Give back the column ID from its name.
     *
     * @param groupName name of the column's group we are looking for the ID.
     *
     * @return the column ID, or -1 if nothing found.
     */
    public int getColumnIdByName(final String groupName) {
        return _starListMeta.getPropertyIndexByName(groupName);
    }

    /**
     * Give back the column name from its ID.
     *
     * @param groupId 
     * @return name of the column's ID.
     */
    public String getColumnNameById(final int groupId) {
        return _starListMeta.getPropertyName(groupId);
    }

    /**
     * Called when a column class is needed by the attached view.
     *
     * @param column
     *
     * @return the specified cell value.
     */
    @Override
    public Class<?> getColumnClass(final int column) {
        return _starListMeta.getPropertyClass(column);
    }

    /**
     * Called when a column header tooltip is needed by the attached view.
     *
     * @param column
     *
     * @return the specified column header tooltip.
     */
    public String getHeaderTooltipForColumn(final int column) {
        return _starListMeta.getPropertyDescription(column);
    }

    /**
     * Called when a column header unit is needed by the attached view.
     *
     * @param column
     *
     * @return the specified column header tooltip.
     */
    public String getHeaderUnitForColumn(final int column) {
        return _starListMeta.getPropertyUnit(column);
    }

    /**
     * Return the URL defined for the given column or "" if undefined
     * @param column
     * @return URL defined for the given column or "" if undefined
     */
    private String getColumnURL(final int column) {
        return _starListMeta.getPropertyUrl(column);
    }

    /**
     * Get the URL of the star property as a String object.
     *
     * @param column
     * @param value star property's string value of that column
     * @return a String object representing the star property URL, null otherwise.
     */
    public String getURL(final int column, final String value) {
        final String url = getColumnURL(column);

        if (url == null) {
            return null;
        }

        // Convert the current value to HTML compatible encoding
        final String encodedValue = UrlUtils.encode(value);

        // Forge the URL by replacing any '${...}' token with the current value
        return patternURL_REPLACE_TOKEN.matcher(url).replaceAll(encodedValue);
    }

    /**
     * Return whether the star property has an URL.
     *
     * @param column
     * @return true if a URL is set, false otherwise.
     */
    public boolean hasURL(final int column) {
        final String url = getColumnURL(column);

        return (url != null);
    }

    /**
     * Return the current magnitude band.
     *
     * @return the current magnitude band.
     */
    public String getMagnitudeBand() {
        return _magnitudeBand;
    }

    /**
     * Return the current scenario bright flag.
     *
     * @return true whether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public Boolean getBrightScenarioFlag() {
        return _brightScenarioFlag;
    }

    /**
     * Parse the given file as VOTable and update any attached JTable to show its content.
     * 
     * Note: this action uses a SwingWorker to parse the VOTable in background (async)
     *
     * @param file the VOTable file to parse as File
     * 
     * @throws IllegalArgumentException if given VOTable is not compatible with SearchCal format
     */
    public void parseVOTable(final File file) throws IllegalArgumentException {
        parseVOTable(file, null, file.length(), false, true);
    }

    /**
     * Parse the given file as VOTable and update any attached JTable to show its content.
     * 
     * Note: this action uses a SwingWorker to parse the VOTable in background (async)
     *
     * @param file the VOTable file to parse as File
     * @param async true to use asynchronous parsing (in background); false to use EDT (synchronous)
     * 
     * @throws IllegalArgumentException if given VOTable is not compatible with SearchCal format
     */
    public void parseVOTable(final File file, final boolean async) throws IllegalArgumentException {
        parseVOTable(file, null, file.length(), false, async);
    }

    /**
     * Parse the given string as VOTable and update any attached JTable to show its content.
     * 
     * Note: this action uses a SwingWorker to parse the VOTable in background (async)
     *
     * @param voTable the VOTable content to parse as String
     * @param startQuery true to start query after parsing votable
     * 
     * @throws IllegalArgumentException if given VOTable is not compatible with SearchCal format
     */
    public void parseVOTable(final String voTable, final boolean startQuery) throws IllegalArgumentException {
        parseVOTable(null, voTable, voTable.length(), startQuery, false);
    }

    /**
     * Parse a given VOTable and update any attached JTable to show its content.
     *
     * @param file the VOTable file to parse as File
     * @param content the VOTable content to parse as String
     * @param length VOTable size in bytes
     * @param startQuery true to start query after parsing votable
     * @param async true to use asynchronous parsing (in background); false to use EDT (synchronous)
     *
     * @throws IllegalArgumentException if given VOTable is not compatible with SearchCal format
     */
    private void parseVOTable(final File file, final String content, final long length,
                              final boolean startQuery, final boolean async) throws IllegalArgumentException {
        // Diff tool:
        if (_vo != null) {
            _vo.enableDataRelatedMenus(false);
        }

        if (file == null && content == null) {
            throw new IllegalArgumentException("Incorrect VOTable input");
        }

        if (_logger.isInfoEnabled()) {
            _logger.info("CalibratorsModel.parseVOTable: VOTable size = {} bytes.", length);
        }

        final long startTime = System.nanoTime();

        final SavotPullParser parser;
        if (file != null) {
            parser = new SavotPullParser(file.getAbsolutePath(), SavotPullEngine.ROWREAD);
        } else {
            parser = new SavotPullParser(new StringReader(content), SavotPullEngine.ROWREAD);
        }

        // start parsing document and get first TR if data are present:
        final SavotTR tr = parser.getNextTR();

        // Get the VOTable
        final SavotVOTable savotVoTable = parser.getVOTable();
        final ResourceSet resources = savotVoTable.getResources();

        SavotResource resource = null;
        SavotTable table = null;

        if (resources.getItemCount() == 1) {
            // SearchCal VOTable must have 1 resource:
            resource = resources.getItemAt(0);

            if (resource.getTables().getItemCount() == 1) {
                // SearchCal resource must have 1 table:
                table = resource.getTables().getItemAt(0);
            }
        }

        // check that the votable has one resource / table containing groups and fields:
        if (resource == null || table == null) {
            _logger.warn("Incorrect VOTable format");
            throw new IllegalArgumentException("Incorrect VOTable format");
        }

        // check that the votable corresponds to the SearchCal VOTable format:
        if (!resource.getName().startsWith("SearchCal")) {
            _logger.warn("Ressource should be 'SearchCal' but is : {}", resource.getName());
            throw new IllegalArgumentException("Incorrect VOTable format; expected one SearchCal VOTable");
        }

        // TODO: try also to check PARAMETER_SCL_SERVER_VERSION !

        // Clear all internal lists before new parsing:
        _originalStarList.clear();
        _calibratorStarList.clear();
        _currentStarList.clear();

        // Update any attached observer TO FREE MEMORY:
        update(null, this);

        // Create parse votable task worker :
        final ParseVoTableSwingWorker loadWorker = new ParseVoTableSwingWorker(file, startQuery, this, parser, savotVoTable, tr, startTime);

        if (async && table.getNrowsValue() > LOAD_TABLE_ASYNC_THRESHOLD) {
            // Cancel other tasks and execute this new task in background:
            loadWorker.executeTask();
        } else {
            // Execute the worker using EDT:
            loadWorker.executeTaskInEDT();
        }
    }

    /**
     * TaskSwingWorker child class to parse the VOTable to get a StarList
     */
    private final static class ParseVoTableSwingWorker extends TaskSwingWorker<StarList> {

        /* members */
        /** file in file mode or null in remote mode */
        private final File file;
        /** true to start query after parsing votable */
        private final boolean startQuery;
        /** calibrators model used for refreshUI callback */
        private final CalibratorsModel calModel;
        /** Store the current query model in order to allow later update to progress bar and parse query parameters */
        private final QueryModel queryModel;
        /** SAVOT pull parser initialized */
        private final SavotPullParser parser;
        /** first SavotTR tr */
        private final SavotTR trFirst;
        /** start time */
        private final long startTime;
        /* meta data */
        /** SAVOT VOTable partially loaded */
        private final SavotVOTable savotVoTable;
        /** Parameter set */
        private ParamSet paramSet = null;

        /**
         * Hidden constructor
         *
         * @param file file in file mode or null in remote mode
         * @param startQuery true to start query after parsing votable
         * @param calModel calibrators model
         * @param parser SAVOT pull parser initialized
         * @param savotVoTable SAVOT VOTable partially loaded
         * @param trFirst first SavotTR tr
         * @param startTime start time (nano seconds)
         */
        private ParseVoTableSwingWorker(final File file, final boolean startQuery, final CalibratorsModel calModel,
                                        final SavotPullParser parser, final SavotVOTable savotVoTable, final SavotTR trFirst,
                                        final long startTime) {
            // get current observation version :
            super(SearchCalTaskRegistry.TASK_LOAD);
            this.file = file;
            this.startQuery = startQuery;
            this.calModel = calModel;
            this.queryModel = calModel._queryModel;
            this.parser = parser;
            this.savotVoTable = savotVoTable;
            this.trFirst = trFirst;
            this.startTime = startTime;
        }

        /**
         * Parse the VOTable in background
         * This code is executed by a Worker thread (Not Swing EDT)
         * @return star list
         */
        @Override
        public StarList computeInBackground() {

            /** Get the current thread to check if the task is interrupted */
            final Thread currentThread = Thread.currentThread();

            final boolean async = !SwingUtils.isEDT();

            final long start = System.nanoTime();

            final SavotResource resource = savotVoTable.getResources().getItemAt(0);
            final SavotTable table = resource.getTables().getItemAt(0);

            final int tableRows = table.getNrowsValue(); // optional

            // Retrieve VOTable parameters
            paramSet = table.getParams();

            final GroupSet groupSet = table.getGroups();
            final FieldSet fieldSet = table.getFields();

            final int nGroups = groupSet.getItemCount();

            // number of properties to size collections properly:
            final int nProperties = nGroups + N_EXTRA_PROPERTIES;

            // Create a new star list meta data:
            final StarListMeta starListMeta = new StarListMeta(nProperties);
            // Create a new star list:
            final StarList starList = new StarList(starListMeta);

            StarPropertyMeta starPropertyMeta;
            SavotField field;
            String url;
            String fieldDataType;
            Class<?> columnClass;

            for (int groupId = 0; groupId < nGroups; groupId++) {
                // Get back the field type
                field = fieldSet.getItemAt(3 * groupId); // x3 as there is 3 fields per group

                fieldDataType = field.getDataType();
                if (fieldDataType != null) {
                    // Default class
                    columnClass = Object.class;

                    if (fieldDataType.equals("char")) {
                        columnClass = String.class;
                    } else if (fieldDataType.equals("float")) {
                        columnClass = Double.class;
                    } else if (fieldDataType.equals("boolean")) {
                        columnClass = Boolean.class;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid VOTable - empty fieldType for " + field.getName());
                }

                // Get back the field link
                if (field.getLinks().getItemCount() != 0) {
                    url = field.getLinks().getItemAt(0).getHref();
                } else {
                    url = "";
                }

                // Define star property meta data:
                starPropertyMeta = new StarPropertyMeta(groupSet.getItemAt(groupId).getName(), columnClass,
                        field.getDescription(), field.getUcd(), field.getUnit(), url);

                // Add it to the star list meta data:
                starListMeta.addPropertyMeta(starPropertyMeta);
            }

            // origin and confidence index are enumerations: use shared instance cache to use less memory:
            final Map<String, String> originValues = new HashMap<String, String>(32);
            final Map<String, String> confidenceValues = new HashMap<String, String>(16);

            // reserve space:
            if (tableRows != 0) {
                starList.ensureCapacity(tableRows);
            }

            // local copies for performance:
            final Class<?>[] colClasses = starListMeta.getPropertyClasses();

            int nRow = 0;

            if (this.trFirst != null) {
                SavotTR tr = trFirst; // consume first TR
                TDSet row;
                List<StarProperty> star;
                Object propertyValue;
                int groupId, mainGroupCellId;
                String value, origin, originValue, confidence, confidenceValue;
                StarProperty starProperty;
                boolean isSet;
                Catalog catalog;

                // only use Progress bar for large tables (JSDC):
                final int step = (async && tableRows > LOAD_TABLE_ASYNC_THRESHOLD) ? tableRows / 100 : Integer.MAX_VALUE;

                // Iterate on rows:
                do {

                    if (async && (nRow % step == 0) && (nRow != 0)) {
                        // fast interrupt :
                        if (currentThread.isInterrupted()) {
                            return null;
                        }

                        // query model may be null in SearchCal Diff tool:
                        if (queryModel != null) {
                            // progress bar:
                            queryModel.setQueryProgress("loading ...", nRow, tableRows);
                        }
                    }

                    // Get the data corresponding to the current row
                    row = tr.getTDSet();

                    // For each group of the row create a star property of each 3 cells
                    // note: Vector type is required by swing DefaultTableModel:
                    star = new Vector<StarProperty>(nProperties);

                    for (groupId = 0; groupId < nGroups; ++groupId) {

                        /*
                         * The index of first cell of the current group (as there is
                         * always 3 cells for each group).
                         */
                        mainGroupCellId = 3 * groupId;

                        propertyValue = null;
                        isSet = false;

                        // Get the value (always the first group cell)
                        value = row.getContent(mainGroupCellId);

                        // replace "" by null to use less memory:
                        if (value.length() != 0) {
                            isSet = true;

                            // Get column field type:
                            columnClass = colClasses[groupId];

                            if (columnClass == Double.class) {
                                try {
                                    propertyValue = Double.valueOf(value);
                                } catch (NumberFormatException nfe) {
                                    _logger.warn("invalid Double [{}]", value);
                                }
                            } else if (columnClass == String.class) {
                                propertyValue = value;
                            } else if (columnClass == Boolean.class) {
                                try {
                                    propertyValue = Boolean.valueOf(value);
                                } catch (NumberFormatException nfe) {
                                    _logger.warn("invalid Boolean [{}]", value);
                                }
                            } else {
                                isSet = false;
                                _logger.warn("unsupported data type [{}]", columnClass);
                            }
                        }

                        // Store the group origin (always the second group cell)
                        ++mainGroupCellId;
                        origin = row.getContent(mainGroupCellId);

                        // replace "" or "-" (blanking value used up to SearchCal release 4.4) 
                        // by null to use less memory:
                        if ((origin.length() == 0) || ("-".equals(origin))) {
                            origin = null;
                        } else {
                            isSet = true;

                            originValue = originValues.get(origin);
                            if (originValue == null) {
                                if (!StarProperty.ORIGIN_COMPUTED.equals(origin)) {
                                    // Resolve possible alias ie fix catalog name:
                                    catalog = Catalog.catalogFromReference(origin);
                                    if (catalog != null) {
                                        origin = catalog.reference();
                                    } else {
                                        _logger.warn("Unable to resolve catalog for origin = '{}'", origin);
                                        origin = null;
                                    }
                                }
                                if (origin != null) {
                                    // cache resolved origin:
                                    originValues.put(origin, origin);
                                }
                            } else {
                                // use shared instance
                                origin = originValue;
                            }
                        }

                        // Store the group confidence (always the third group cell)
                        ++mainGroupCellId;
                        confidence = row.getContent(mainGroupCellId);

                        // replace "" by null to use less memory:
                        if (confidence.length() == 0) {
                            confidence = null;
                        } else {
                            isSet = true;

                            confidenceValue = confidenceValues.get(confidence);
                            if (confidenceValue == null) {
                                // use interned string:
                                confidence = confidence.intern();
                                confidenceValues.put(confidence, confidence);
                            } else {
                                // use shared instance
                                confidence = confidenceValue;
                            }
                        }

                        /*
                         * Create a new StarProperty instance from the retrieved value, origin and confidence.
                         */
                        starProperty = (isSet) ? new StarProperty(propertyValue, origin, confidence) : StarProperty.EMPTY_STAR_PROPERTY;

                        // Add the newly created star property to the star property list
                        star.add(starProperty);
                    }

                    // Store each VOTable row as a list of star properties
                    starList.add(star);

                    nRow++;

                } while ((tr = parser.getNextTR()) != null);
            }

            if (_logger.isInfoEnabled()) {
                _logger.info("CalibratorsModel.parseVOTable: {} rows read in {} ms.", nRow, 1e-6d * (System.nanoTime() - start));
            }

            return starList;
        }

        /**
         * Refresh the GUI using the given star list
         * This code is executed by the Swing Event Dispatcher thread (EDT)
         * @param starList parsed StarList
         */
        @Override
        public void refreshUI(final StarList starList) {

            // update calibrators model:
            calModel.updateModelFromVOTable(savotVoTable, paramSet, starList);

            if (_logger.isInfoEnabled()) {
                _logger.info("CalibratorsModel.parseVOTable done: {} ms.", 1e-6d * (System.nanoTime() - startTime));
            }

            // query model may be null in SearchCal Diff tool:
            if (queryModel != null) {
                // reset progress bar:
                queryModel.setQueryProgress("", 0, 0);
            }

            // TODO: Perform such actions using callbacks ASAP
            if (file != null) {
                // query model may be null in SearchCal Diff tool:
                if (queryModel != null) {
                    // Loading the file in the query model
                    StatusBar.show("loading file (parsing query)...");
                    try {
                        queryModel.loadParamSet(calModel.getParamSet());
                        StatusBar.show("loading file (query successfully parsed)...");
                        StatusBar.show("file succesfully loaded.");

                    } catch (NumberFormatException nfe) {
                        StatusBar.show("loading aborted (query parsing error) !");
                        MessagePane.showErrorMessage("Could not open file (query parsing error).", nfe);
                        // TODO reset table content ?
                    }
                }
            } else if (startQuery) {
                // query model may be null in SearchCal Diff tool:
                if (queryModel != null) {

                    Exception e = null;
                    try {
                        // load default values to reset completely the query model:
                        queryModel.loadDefaultValues();
                        // load given parameters (some may be missing)
                        queryModel.loadParamSet(calModel.getParamSet());

                    } catch (NumberFormatException nfe) {
                        e = nfe;
                    } catch (IllegalArgumentException iae) {
                        e = iae;
                    } catch (PreferencesException pe) {
                        e = pe;
                    } finally {
                        if (e != null) {
                            StatusBar.show("calibrator search aborted (could not parse query) !");
                            MessagePane.showErrorMessage("Could not parse query.", e);
                            return;
                        }
                    }
                    // Launch the request
                    StatusBar.show("Launching search...");

                    // Diff tool:
                    if (calModel._vo != null) {
                        calModel._vo.executeGetCal();
                    }
                }
            }

            // Diff tool:
            if (calModel._vo != null) {
                // Enabling the 'Save' menus
                calModel._vo.enableDataRelatedMenus(true);
            }
        }

        @Override
        public void handleException(ExecutionException ee) {
            if (ee.getCause() instanceof IllegalArgumentException) {
                final String errorMsg = "Loading aborted: calibrators parsing error " + ((file != null) ? " in file : " + file.getAbsolutePath() : "");
                StatusBar.show(errorMsg);
                MessagePane.showErrorMessage(errorMsg, ee.getCause());
            } else {
                super.handleException(ee);
            }
        }
    }

    /**
     * Update this calibrators model from parsed VOTable
     * @param savotVoTable  parsed SAVOT VOTable 
     * @param starList parsed StarList
     * @param paramSet parameter set
     */
    void updateModelFromVOTable(final SavotVOTable savotVoTable, final ParamSet paramSet, final StarList starList) {
        // Keep only VOTable structure (without data):
        _parsedVOTable = savotVoTable;

        // Compute selected magnitude band and scenario
        // TODO: put in Savot Param finder by name:
        final int nParams = paramSet.getItemCount();
        final HashMap<String, String> parameters = new HashMap<String, String>(nParams);

        for (int i = 0; i < nParams; i++) {
            final SavotParam param = paramSet.getItemAt(i);
            final String paramName = param.getName();
            final String paramValue = param.getValue();

            if (_logger.isDebugEnabled()) {
                _logger.debug(paramName + " = '" + paramValue + "'");
            }
            parameters.put(paramName, paramValue);
        }

        _magnitudeBand = parameters.get("band");

        if ((_magnitudeBand != null) && (_magnitudeBand.matches("I") || _magnitudeBand.matches("J") || _magnitudeBand.matches("H"))) {
            _magnitudeBand = "K";
        }

        _brightScenarioFlag = Boolean.valueOf(parameters.get("bright"));

        if (_logger.isDebugEnabled()) {
            _logger.debug("magnitude band = '" + _magnitudeBand + "'; bright scenario = '" + _brightScenarioFlag + "'.");
        }

        // Add SearchCal versions as PARAM if missing (before sclsvr 4.1):
        if (parameters.get(PARAMETER_SCL_SERVER_VERSION) == null) {
            final SavotResource resource = savotVoTable.getResources().getItemAt(0);

            final SavotParam param = new SavotParam();
            param.setName(PARAMETER_SCL_SERVER_VERSION);
            param.setDataType("char");
            param.setArraySize("*");
            param.setValue(resource.getName());
            paramSet.addItem(param);
        }
        if (parameters.get(PARAMETER_SCL_GUI_VERSION) == null) {
            final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
            final String version = applicationDataModel.getProgramName() + " v" + applicationDataModel.getProgramVersion();

            final SavotParam param = new SavotParam();
            param.setName(PARAMETER_SCL_GUI_VERSION);
            param.setDataType("char");
            param.setArraySize("*");
            param.setValue(version);
            paramSet.addItem(param);
        }

        _paramSet = paramSet;

        // Process star list:
        postProcess(starList);

        // apply filters and fire Update:
        updateModel(starList);
    }

    /**
     * Update this calibrators model, apply filters and fire update
     * @param starList parsed StarList
     */
    void updateModel(final StarList starList) {
        // Update the star list:
        setOriginalStarList(starList);

        // remove non calibrator stars once:
        removeNonCalibrators();

        // fire Update:
        removeDeletedStars(true);
    }

    /**
     * Post process the given star list to add dynamic properties
     * @param starList star list to process
     */
    void postProcess(final StarList starList) {
        final StarListMeta starListMeta = starList.getMetaData();

        // Add custom properties:

        // Add RA (deg) property:
        if (starListMeta.getPropertyIndexByName(StarList.RADegColumnName) == -1) {
            starListMeta.addPropertyMeta(new StarPropertyMeta(StarList.RADegColumnName, Double.class, "Right Ascension - J2000", "POS_EQ_RA_MAIN", "deg", ""));

            if (!starList.isEmpty()) {
                // Get the ID of the column containing 'RA' star properties
                final int raId = starList.getColumnIdByName(StarList.RAJ2000ColumnName);
                if (raId != -1) {
                    for (List<StarProperty> star : starList) {

                        // Get the current star RA value
                        final StarProperty cell = star.get(raId);

                        if (cell.hasValue()) {
                            final String raString = cell.getStringValue();
                            final double currentRA = ALX.parseRA(raString);

                            // add RADeg value:
                            star.add(new StarProperty(Double.valueOf(currentRA),
                                    cell.hasOrigin() ? cell.getOrigin() : null,
                                    cell.hasConfidence() ? cell.getConfidence() : null));
                        }
                    }
                }
            }
        }

        // Add DEC (deg) property:
        if (starListMeta.getPropertyIndexByName(StarList.DEDegColumnName) == -1) {
            starListMeta.addPropertyMeta(new StarPropertyMeta(StarList.DEDegColumnName, Double.class, "Declination - J2000", "POS_EQ_DEC_MAIN", "deg", ""));

            if (!starList.isEmpty()) {
                // Get the ID of the column containing 'DEC' star properties
                final int decId = starList.getColumnIdByName(StarList.DEJ2000ColumnName);
                if (decId != -1) {
                    for (List<StarProperty> star : starList) {

                        // Get the current star DEC value
                        final StarProperty cell = star.get(decId);

                        if (cell.hasValue()) {
                            final String decString = cell.getStringValue();
                            final double currentDEC = ALX.parseDEC(decString);

                            // add RADeg value:
                            star.add(new StarProperty(Double.valueOf(currentDEC),
                                    cell.hasOrigin() ? cell.getOrigin() : null,
                                    cell.hasConfidence() ? cell.getConfidence() : null));
                        }
                    }
                }
            }
        }

        // Add row index property:
        if (starListMeta.getPropertyIndexByName(StarList.RowIdxColumnName) == -1) {
            starListMeta.addPropertyMeta(new StarPropertyMeta(StarList.RowIdxColumnName, Integer.class, "row index", "ID_NUMBER", "", ""));

            if (!starList.isEmpty()) {
                int i = 0;
                for (List<StarProperty> star : starList) {
                    // add rowIdx value:
                    star.add(new StarProperty(NumberUtils.valueOf(++i)));
                }
            }
        }

        if (!starList.isEmpty()) {
            // First star:
            final Vector<StarProperty> first = (Vector<StarProperty>) starList.get(0);

            _logger.info("{} Star Properties.", first.size());

            if (first.size() > first.capacity()) {
                _logger.warn("Incorrect Property capacity: {} != {} ", first.size(), first.capacity());
            }
        }
    }

    /**
     * Give back the VOTable parameters.
     *
     * @return a ParamSet.
     */
    public ParamSet getParamSet() {
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
        final List<StarProperty> star = getStar(row);
        if (star != null) {
            if (column >= 0 && column < _nProperties) {
                return star.get(column);
            }
        }

        _logger.error("Could get StarProperty at row '{}' and column '{}'.", row, column);

        return null;
    }

    /**
     * Return the star at the given row.
     *
     * @param row the index of one star which in the filtered list of the model.
     *
     * @return the Star corresponding to the given index.
     */
    public List<StarProperty> getStar(final int row) {
        final List<StarProperty> star = (row >= 0 && row < _nFilteredStars) ? _filteredStarList.get(row) : null;

        if (star == null) {
            _logger.error("Could get Star at row '{}'.", row);
        }

        return star;
    }

    /**
     * Open the given file as a VOTable.
     *
     * @param file the file to be read.
     * @throws IOException if I/O error occurs reading the file
     * @throws IllegalArgumentException if given votable is not compatible with SearchCal format
     */
    public void openFile(final File file) throws IOException, IllegalArgumentException {
        if (!file.isFile() || !file.exists()) {
            throw new FileNotFoundException("File '" + file.getAbsolutePath() + "'does not exist.");
        }
        parseVOTable(file);
    }

    /**
     * Save the stars associated to given indexes into the given file
     * or all stars if indices array is null/empty.
     * Stars are extracted from filtered list.
     *
     * @param file output file
     */
    public void saveSelectionAsVOTableFile(final File file) {
        StarList selectedStarsList;

        final int selSize = (_selectedStarIndices != null) ? _selectedStarIndices.length : 0;

        // If some calibrators are currently selected
        if (selSize != 0) {
            // Compute a dedicated star list
            selectedStarsList = new StarList(_starListMeta);
            selectedStarsList.ensureCapacity(selSize);

            for (int index = 0; index < selSize; index++) {
                // Use filtered star list because selection works on filtered list
                selectedStarsList.add(_filteredStarList.get(_selectedStarIndices[index]));
            }
        } else {
            // Use all visible calibrators if none explicitly selected
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
     * @param starList starList to save.
     * @return true if successful
     */
    public boolean saveVOTableFile(final File file, final StarList starList) {
        final int size = starList.size();

        // If null received
        if (size <= 0) {
            _logger.warn("Could not process 'null' StarList");
            return false;
        }

        // If no Votable already loaded to be used as a template for columns descriptions
        if (_parsedVOTable == null) {
            _logger.warn("Could not generate metadata template");
            return false;
        }

        final String filename = file.getAbsolutePath();

        if (_logger.isInfoEnabled()) {
            _logger.info("Saving the starlist as votable into file: {}", filename);
        }

        boolean ok = false;

        final long start = System.nanoTime();
        try {
            final SavotVOTable votable = _parsedVOTable;

            // Use SavotWriter TR / TR:
            final SavotWriter wd = new SavotWriter();
            // do not encode 
            wd.enableElementEntities(false);
            wd.enableAttributeEntities(true);

            // @See SavotWriter.generateDocument():
            wd.initStream(filename);

            wd.writeDocumentHead(votable);

            // write COOSYS elements - deprecated since VOTable 1.2
            wd.writeCoosys(votable.getCoosys());

            // RESOURCE
            // @See SavotWriter.writeResource()

            final SavotResource resource = votable.getResources().getItemAt(0);

            // <RESOURCE>
            wd.writeResourceBegin(resource);

            // TABLE elements
            final SavotTable table = resource.getTables().getItemAt(0);

            // set TABLE nrows correctly:
            table.setNrowsValue(size);

            // <TABLE>
            wd.writeTableBegin(table);

            // write FIELD elements
            wd.writeField(table.getFields());

            // write PARAM elements
            wd.writeParam(table.getParams());

            // write GROUP elements
            wd.writeGroup(table.getGroups());

            // write LINK elements
            wd.writeLink(table.getLinks());

            final SavotData data = table.getData();

            if (data != null) {
                // <DATA>
                wd.writeDataBegin();

                final SavotTableData tableData = data.getTableData();

                if (tableData != null) {
                    // <TABLEDATA>
                    wd.writeTableDataBegin();

                    // Get only server properties not dynamic ones:
                    final int nGroups = table.getGroups().getItemCount();
                    final int nProps = 3 * nGroups;

                    final SavotTD[] cacheTD = new SavotTD[nProps];

                    int i, c;
                    for (c = 0; c < nProps; c++) {
                        cacheTD[c] = new SavotTD();
                    }

                    // shared empty TD instance
                    final SavotTD emptyTD = new SavotTD();

                    final SavotTR tr = new SavotTR();
                    final TDSet tdSet = tr.getTDSet(nProps);

                    SavotTD valueTd;
                    SavotTD originTd;
                    SavotTD confidenceTd;
                    StarProperty property;

                    // And create one row per star entry
                    for (final List<StarProperty> star : starList) {

                        tr.clear(); // recycle TR instance (also TDSet)
                        c = 0;

                        // Get only server properties not dynamic ones:
                        for (i = 0; i < nGroups; i++) {
                            property = star.get(i);

                            // value:
                            if (property.hasValue()) {
                                valueTd = cacheTD[c];
                                // convert double / boolean to string:
                                valueTd.setContent(property.getStringValue());
                            } else {
                                valueTd = emptyTD;
                            }
                            tdSet.addItem(valueTd);

                            // origin:
                            if (property.hasOrigin()) {
                                originTd = cacheTD[c + 1];
                                originTd.setContent(property.getOrigin());
                            } else {
                                originTd = emptyTD;
                            }
                            tdSet.addItem(originTd);

                            // confidence index:
                            if (property.hasConfidence()) {
                                confidenceTd = cacheTD[c + 2];
                                confidenceTd.setContent(property.getConfidence());
                            } else {
                                confidenceTd = emptyTD;
                            }
                            tdSet.addItem(confidenceTd);

                            c += 3;
                        }

                        // <TR>
                        wd.writeTR(tr);
                    }

                    // </TABLE>
                    wd.writeTableDataEnd();

                } // TABLE / DATA / TABLEDATA

                // </DATA>
                wd.writeDataEnd();

            } // TABLE / DATA

            // </TABLE>
            wd.writeTableEnd();

            // </RESOURCE>
            wd.writeResourceEnd();

            wd.writeDocumentEnd();

            ok = true;

        } catch (IOException ioe) {
            _logger.error("Unable to save the starlist as votable into file: " + filename, ioe);
        } finally {
            if (ok) {
                if (_logger.isInfoEnabled()) {
                    _logger.info("CalibratorsModel.saveVOTableFile: file saved in {} ms.", 1e-6d * (System.nanoTime() - start));
                }
            }
        }
        return ok;
    }

    /**
     * Return whether the star list has some stars flagged deleted.
     *
     * @return true if the list contains some stars flagged, false otherwise.
     */
    public boolean hasSomeDeletedStars() {
        return _calibratorStarList.hasSomeDeletedStars();
    }

    /**
     * Delete the shown elements according to the given row indexes. 
     * The shown elements are the not filtered elements.
     *
     * @param indices indices of the row to be removed.
     */
    public void deleteStars(final int[] indices) {
        // Remove each selected row
        final List<List<StarProperty>> stars = new ArrayList<List<StarProperty>>(indices.length);

        for (int idx : indices) {
            stars.add(_filteredStarList.get(idx));
        }

        // flag stars
        _originalStarList.markAsDeleted(stars);

        // fire Update:
        removeDeletedStars(false);
    }

    /**
     * Remove all non calibrator stars.
     */
    private void removeNonCalibrators() {
        // filter non calibrator stars:
        _calibratorStarList = _facelessNonCalibratorsFilter.process(_originalStarList);
    }

    /**
     * Remove all stars previously flagged as deleted.
     * 
     * @param silently true to ignore that data have changed
     */
    private void removeDeletedStars(boolean silently) {
        // Remove all the stars flagged as deleted
        _currentStarList = _calibratorStarList.removeAllDeletedStars();

        update(null, (silently == true ? this : null));
    }

    /**
     * Undelete all stars previously flagged as deleted.
     */
    public void undeleteStars() {
        // Unflag all previously flagged stars
        _originalStarList.undeleteAll();

        // fire Update:
        removeDeletedStars(false);
    }

    /**
     * Store the selected indices of the calibrator view.
     * This method should only be called by calibratorView.
     * @param selectedStarIndices selected indices of the calibrator view table.
     */
    public void setSelectedStars(final int[] selectedStarIndices) {
        _selectedStarIndices = selectedStarIndices;

        // Diff tool:
        if (_vo != null) {
            // Set SAMP action text according to current selection
            String actionMenuText = "Send All Calibrators to...";

            if ((_selectedStarIndices != null) && (_selectedStarIndices.length != 0)) {
                // When some calibrators selected
                actionMenuText = "Send Selected Calibrators to...";
            }

            // Update Menu label
            _vo.setShareCalibratorsThroughSAMPActionText(actionMenuText);
        }
    }

    /**
     * Return the number of selected stars
     * @return number of selected stars
     */
    public int getNumberOfSelectedStars() {
        return (_selectedStarIndices != null) ? _selectedStarIndices.length : 0;
    }

    /**
     * Convert a given VOTable file to the CSV format.
     *
     * @param out the converted file
     */
    public void exportCurrentVOTableToCSV(File out) {
        applyXSLTranformationOnCurrentVOTable(out, "sclguiVOTableToCSV.xsl");
    }

    /**
     * Convert a given VOTable file to the HTML format.
     *
     * @param out the converted file
     */
    public void exportCurrentVOTableToHTML(File out) {
        applyXSLTranformationOnCurrentVOTable(out, "sclguiVOTableToHTML.xsl");
    }

    /**
     * Apply the given xsl transformation on the current VOTable.
     *
     * @param outputFile the File in which the transformation result should be written.
     * @param xslFileName the XSL file containing the transformation to be applied
     */
    private void applyXSLTranformationOnCurrentVOTable(final File outputFile, final String xslFileName) {
        final String xsltFile = SearchCal.getSharedInstance().getPathFromResourceFilename(xslFileName);

        if (xsltFile == null) {
            _logger.error("Could not load XSL file '{}'.", xslFileName);

            return;
        }

        try {
            // Prepare the input and output files
            final File tmpFile = FileUtils.getTempFile("currentStarList.scvot");

            if (saveVOTableFile(tmpFile, _currentStarList)) {
                final InputStream sourceStream = new BufferedInputStream(new FileInputStream(tmpFile));
                final OutputStream resultStream = new BufferedOutputStream(new FileOutputStream(outputFile));

                final long startTime = System.nanoTime();

                // Apply the xsl file to the source file and write the result to
                // the output file
                // use an XSLT to transform the XML document to an HTML representation :
                XmlFactory.transform(sourceStream, xsltFile, resultStream);

                if (_logger.isInfoEnabled()) {
                    _logger.info("applyXSLTranformationOnCurrentVOTable done: {} ms.", 1e-6d * (System.nanoTime() - startTime));
                }
            }

        } catch (FileNotFoundException fnfe) {
            _logger.error("File not found", fnfe);
        } catch (IllegalArgumentException iae) {
            // An error occurred in the XSL file
            _logger.error("An error occured during XSLT transformation '" + xsltFile + "'", iae);

            if (iae.getCause() instanceof TransformerException) {
                // An error occurred while applying the XSL file
                // Get location of error in input file
                final SourceLocator locator = ((TransformerException) iae.getCause()).getLocator();

                _logger.error("One error occured while applying XSL file '" + xsltFile
                        + "', on line '" + locator.getLineNumber() + "' and column '" + locator.getColumnNumber() + "'");
            }
        }
    }

    /**
     * Custom row header model to add a column labeled "Index"
     */
    protected static class RowHeadersModel extends DefaultTableModel {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1L;

        /**
         * Protected constructor
         */
        RowHeadersModel() {
            // Initialize the model with 0 row and 0 column
            super(0, 0);

            // Add a column labeled "Index"
            addColumn("Index");
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            // Return always false as no row header should be editable
            return false;
        }

        /**
         *
         * @param nbOfRows number of rows
         */
        public void populate(final int nbOfRows) {
            // Empty all the current row headers
            dataVector.clear();

            // Generate as many row headers as the given number of data rows
            for (int i = 0; i < nbOfRows; i++) {
                final Vector<Integer> row = new Vector<Integer>(1);
                row.add(NumberUtils.valueOf(i + 1));
                addRow(row);
            }
        }
    }
}
/*___oOo___*/
