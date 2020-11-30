/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import cds.savot.model.FieldRefSet;
import cds.savot.model.FieldSet;
import cds.savot.model.GroupSet;
import cds.savot.model.OptionSet;
import cds.savot.model.ParamRefSet;
import cds.savot.model.ResourceSet;
import cds.savot.model.SavotData;
import cds.savot.model.SavotField;
import cds.savot.model.SavotGroup;
import cds.savot.model.SavotOption;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTD;
import cds.savot.model.SavotTR;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotTableData;
import cds.savot.model.SavotVOTable;
import cds.savot.model.SavotValues;
import cds.savot.model.TDSet;
import cds.savot.stax.SavotStaxParser;
import cds.savot.writer.SavotWriter;
import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.CoordUtils;
import static fr.jmmc.jmal.star.StarResolver.USE_CACHE_DEV;
import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.task.TaskSwingWorker;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.service.XslTransform;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.jmcs.util.ObjectUtils;
import fr.jmmc.jmcs.util.ResourceUtils;
import fr.jmmc.jmcs.util.UrlUtils;
import fr.jmmc.sclgui.calibrator.VisibilityUtils.VisibilityResult;
import fr.jmmc.sclgui.filter.FacelessNonCalibratorsFilter;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.query.QueryModel.Notification;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
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
    /** Enable CrossIds checker (development only) */
    public final static boolean DO_CROSS_ID_CHECKER = USE_CACHE_DEV;
    /** Enable Vis2 computation comparison vs original column (development only) */
    public final static boolean DO_COMPARE_VIS2 = false;
    /** Enable Distance computation comparison vs original column (development only) */
    public final static boolean DO_COMPARE_DIST = false;
    /** server log */
    public final static String SCL_SERVER_LOG = "fr.jmmc.sclgui.server";
    /** Server Log */
    private final static Logger _serverLog = LoggerFactory.getLogger(SCL_SERVER_LOG);
    /** SearchCal output format undefined */
    public static final double OUTPUT_FORMAT_UNDEFINED = 0.0;
    /** SearchCal output format 2013.7 */
    public static final double OUTPUT_FORMAT_2013_7 = 2013.7;
    /** SearchCal GUI supported output format */
    public static final String GUI_OUTPUT_FORMAT = "2013.7";
    /** table size threshold to load them asynchronously */
    private final static int LOAD_TABLE_ASYNC_THRESHOLD = 5000;
    /** number of extra properties (dynamically added and computed in the GUI): RA/DEC (deg), rowIdx, otherRowIdx */
    private final static int N_EXTRA_PROPERTIES = 4;
    /** parameter 'band' */
    public final static String PARAMETER_BAND = "band";
    /** parameter SearchCalServerVersion (string) */
    public final static String PARAMETER_BRIGHT = "bright";
    /** parameter 'band' */
    public final static String PARAMETER_BASE_MAX = "baseMax";
    /** parameter 'wlen' */
    public final static String PARAMETER_WAVELENGTH = "wlen";
    /** parameter 'ra' */
    public final static String PARAMETER_RA = "ra";
    /** parameter 'dec' */
    public final static String PARAMETER_DEC = "dec";
    /** parameter SearchCalGuiVersion (string) */
    public final static String PARAMETER_SCL_GUI_VERSION = "SearchCalGuiVersion";
    /** parameter SearchCalServerVersion (string) */
    public final static String PARAMETER_SCL_SERVER_VERSION = "SearchCalServerVersion";
    /** parameter outputFormat (double) */
    public final static String PARAMETER_SCL_OUTPUT_FORMAT = "outputFormat";
    /** parameter ConfidenceIndexes (int) */
    public final static String PARAMETER_SCL_CONFIDENCE_INDEXES = "ConfidenceIndexes";
    /** parameter OriginIndexes (int) */
    public final static String PARAMETER_SCL_ORIGIN_INDEXES = "OriginIndexes";
    /** undefined (blanking value) */
    public final static String STR_UNDEFINED = "-";
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
    SavotVOTable _parsedVOTable;
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
    /** VOTable parameters */
    ParameterSetWrapper _paramSetWrapper = null;
    /** Field Save mapping */
    private VOTableSaveMapping[] _saveMappings = null;
    /** Flag indicating whether data have changed or not */
    private boolean _dataHaveChanged;
    /** Row headers */
    final RowHeadersModel _rowHeadersModel;
    /** Selected magnitude band */
    String _magnitudeBand = "V";
    /** Selected scenario */
    Boolean _brightScenarioFlag = true;
    /** flag to enable/disable update events (ie multiple calls) */
    private boolean _autoUpdateEvent = true;
    /** unique column names that are missing (avoid repeated messages in logs) */
    final HashSet<String> _ignoreMissingColumns = new HashSet<String>(32);
    /** dist computation state used to avoid redundant computations */
    final DistParameters distState = new DistParameters();
    /** vis2 computation state used to avoid redundant computations */
    final Vis2Parameters vis2State = new Vis2Parameters();

    /**
     * Constructor.
     *
     * @param filtersModel the filter manager to monitor for changes.
     * @param queryModel the query model to use its progress bar
     */
    public CalibratorsModel(final FiltersModel filtersModel, final QueryModel queryModel) {
        _filtersModel = filtersModel;
        _queryModel = queryModel;

        // add observer to query model (to compute dynamic columns):
        _queryModel.addObserver(new QueryModelObserverForDynamicColumns());

        reset();

        _rowHeadersModel = new RowHeadersModel();

        // Non Calibrators filter (faceless):
        _facelessNonCalibratorsFilter = new FacelessNonCalibratorsFilter();

        // This faceless filter should always be activated
        // (set once here as no GUI can change it anywhere else)
        // TODO: do not filter GetStar results:
        _facelessNonCalibratorsFilter.setEnabled(Preferences.getInstance().getPreferenceAsBoolean(PreferenceKey.FILTER_NON_CALIBRATORS));
    }

    /**
     * Reset the model
     */
    private void reset() {
        // create empty star lists:
        setOriginalStarList(StarList.EMPTY_LIST);
        _calibratorStarList = StarList.EMPTY_LIST;
        _currentStarList = StarList.EMPTY_LIST;
        setFilteredStarList(StarList.EMPTY_LIST);

        _parsedVOTable = null;
        _paramSetWrapper = null;
        _saveMappings = null;
        _dataHaveChanged = false;
    }

    /**
     * Reset the model and update any attached observer
     */
    public void resetAndUpdate() {
        reset();

        // Update any attached observer:
        update(null, this);
    }

    void resetFilters() {
        _filtersModel.resetFilters(getBrightScenarioFlag().booleanValue());
    }

    public Set<String> getIgnoreMissingColumns() {
        return _ignoreMissingColumns;
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
                _logger.debug("update: {} with arg = {}", o, arg);
            }

            // Filter the displayed star list
            setFilteredStarList(_filtersModel.process(_currentStarList));

            // As a DefaultTableModel instance, set all the JTable needed vectors
            // it calls fireTableStructureChanged()
            setDataVector(_filteredStarList, _starListMeta.getPropertyNames());

            // Generate as many row headers as data rows
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
            final HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>(size);

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

        if (file != null) {
            _logger.info("Loading votable file: {}", file.getAbsolutePath());
        }

        _logger.info("CalibratorsModel.parseVOTable: VOTable size = {} bytes.", length);

        final long startTime = System.nanoTime();

        try {
            final SavotStaxParser parser;
            if (file != null) {
                parser = new SavotStaxParser(file.getAbsolutePath(), SavotStaxParser.ROWREAD);
            } else {
                parser = new SavotStaxParser(new StringReader(content), SavotStaxParser.ROWREAD);
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

            reset();

            // Create parse votable task worker :
            final ParseVoTableSwingWorker loadWorker = new ParseVoTableSwingWorker(file, startQuery, this, parser, savotVoTable, tr, startTime);

            if (async && table.getNrowsValue() > LOAD_TABLE_ASYNC_THRESHOLD) {
                // Cancel other tasks and execute this new task in background:
                loadWorker.executeTask();
            } else {
                // Execute the worker using EDT:
                loadWorker.executeTaskInEDT();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("VOTable Parsing error", e);
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
        /** SAVOT stax parser initialized */
        private final SavotStaxParser parser;
        /** first SavotTR tr */
        private final SavotTR trFirst;
        /** start time */
        private final long startTime;
        /* meta data */
        /** SAVOT VOTable partially loaded */
        private final SavotVOTable savotVoTable;
        /** ParameterSet wrapper */
        private ParameterSetWrapper paramSetWrapper = null;
        /** Field Save mapping */
        private VOTableSaveMapping[] saveMappings = null;

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
                                        final SavotStaxParser parser, final SavotVOTable savotVoTable, final SavotTR trFirst,
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
            this.paramSetWrapper = new ParameterSetWrapper(table.getParams());

            /* Parse optional output format parameter */
            double outputFormat = OUTPUT_FORMAT_UNDEFINED; // 0 means default format (SearchCal < 5.0)
            final SavotParam paramOutputFormat = paramSetWrapper.get(PARAMETER_SCL_OUTPUT_FORMAT);
            if (paramOutputFormat != null) {
                outputFormat = Double.valueOf(paramOutputFormat.getValue());
                _logger.debug("outputFormat: {}", outputFormat);
            }

            final boolean isOutputFormatUndefined = (outputFormat == OUTPUT_FORMAT_UNDEFINED);

            /* Origin index mapping (int to string) */
            // Parse PARAM name="OriginIndexes"
            final SavotParam paramOriginIndexes = paramSetWrapper.get(PARAMETER_SCL_ORIGIN_INDEXES);
            if (paramOriginIndexes != null) {
                final SavotValues values = paramOriginIndexes.getValues();
                if (values != null) {
                    for (final SavotOption option : values.getOptions().getItems()) {
                        // name="int" - value="catalog id" mapping
                        // check mapping with built-in Origin enum:
                        Origin origin = Origin.parse(option.getName());

                        if (_logger.isDebugEnabled()) {
                            _logger.debug("origin found for key={}: '{}' vs '{}'", option.getName(), origin.getValue(), option.getValue());
                        }

                        // TODO: validate support for extra origin values !
                        if (!origin.getValue().equals(option.getValue())) {
                            if (origin.getKeyString().equals(option.getName())) {
                                _logger.warn("origin for key={} mismatchs: '{}' vs '{}'", option.getName(), origin.getValue(), option.getValue());
                            } else {
                                _logger.warn("missing origin index for key={}: '{}'", option.getName(), option.getValue());
                                // new origin value:
                                Origin.createOrigin(Integer.parseInt(option.getName()), option.getValue());
                            }
                        }
                    }
                }
            }

            final FieldSet fieldSet = table.getFields();
            final int nFields = fieldSet.getItemCount();

            // FIELD by id:
            final HashMap<String, SavotField> fieldById = new HashMap<String, SavotField>(nFields);
            final HashMap<String, Integer> fieldPosById = new HashMap<String, Integer>(nFields);

            for (int i = 0; i < nFields; i++) {
                final SavotField field = fieldSet.getItemAt(i);
                final String id = field.getId();
                if (id.length() != 0) {
                    fieldById.put(id, field);
                    fieldPosById.put(id, NumberUtils.valueOf(i));
                }
            }

            final GroupSet groupSet = table.getGroups();
            final int nGroups = groupSet.getItemCount();

            // number of properties to size collections properly:
            int nProperties = nGroups + N_EXTRA_PROPERTIES;

            // Create a new star list meta data:
            final StarListMeta starListMeta = new StarListMeta(nProperties * 5 / 4); // add 25% capacity for fake error properties
            // Create a new star list:
            final StarList starList = new StarList(starListMeta);

            // Interpret VOTable Groups
            final VOTableLoadMapping[] loadMappings = new VOTableLoadMapping[nGroups];
            saveMappings = new VOTableSaveMapping[nFields];

            /* TODO: use UCD or UTYPE to discrimminate FIELD/PARAM corresponding to value/origin/confidence/error ... */
            VOTableLoadMapping loadMapping;
            int type;
            int nStrFields = 0;

            for (int groupId = 0; groupId < nGroups; groupId++) {
                // Get Group to analyze its content (property, origin, confidence)
                // TODO: in future it will also contain error field/param

                loadMapping = new VOTableLoadMapping();
                loadMappings[groupId] = loadMapping;

                final SavotGroup group = groupSet.getItemAt(groupId);

                if (_logger.isDebugEnabled()) {
                    _logger.debug("parsing group: {}", group.getName());
                }

                // Parse field refs:
                final FieldRefSet fieldRefs = group.getFieldsRef();
                final int nFieldRefs = fieldRefs.getItemCount();

                StarPropertyMeta propertyMeta = null;
                StarPropertyMeta propertyErrorMeta = null;

                for (int i = 0; i < nFieldRefs; i++) {
                    final String ref = fieldRefs.getItemAt(i).getRef();

                    // Resolve the reference (field)
                    final SavotField field = fieldById.get(ref);
                    final Integer pos = fieldPosById.get(ref);

                    if (field == null || pos == null) {
                        throw new IllegalArgumentException("Invalid VOTable - invalid FIELDref '" + ref + "'");
                    }

                    boolean valid = false;

                    // Check field name as ucd may be used also by properties
                    // (CODE_QUALITY or REFER_CODE for example ...)
                    // TODO: use FieldRef utypes ?
                    final String name = field.getName();

                    if (name.length() != 0) {
                        if (name.endsWith("origin")) {
                            valid = true;
                            // origin
                            loadMapping.originPos = pos;
                            if (isOutputFormatUndefined) {
                                // fix datatype and remove arraysize:
                                field.setDataType("int");
                                field.setArraySize(null);
                            }
                        } else if (name.endsWith("confidence")) {
                            valid = true;
                            // confidence
                            loadMapping.confidencePos = pos;
                            if (isOutputFormatUndefined) {
                                // fix datatype and remove arraysize:
                                field.setDataType("int");
                                field.setArraySize(null);
                            }
                        } else {
                            // Check first if the property value is defined:
                            // Old format (one group representing a property error)
                            if (loadMapping.valuePos == null) {
                                valid = true;
                                // field name
                                loadMapping.name = name;
                                // field value
                                loadMapping.valuePos = pos;

                                // Parse field's datatype attribute:
                                final String fieldDataType = field.getDataType();

                                if (fieldDataType != null) {
                                    type = StarPropertyMeta.TYPE_ANY;

                                    if (fieldDataType.equals("double")) {
                                        type = StarPropertyMeta.TYPE_DOUBLE;
                                    } else if (isOutputFormatUndefined && fieldDataType.equals("float")) {
                                        /* note: datatype="float" used by SearchCal 4;x */
                                        type = StarPropertyMeta.TYPE_DOUBLE;
                                        // fix datatype:
                                        field.setDataType("double");
                                    } else if (fieldDataType.equals("char")) {
                                        if (isOutputFormatUndefined) {
                                            if (StarList.DiamFlagColumnName.equals(name) || "plxFlag".equals(name)) {
                                                // convert "diamFlag" and "PlxFlag" columns to use 'boolean' datatype:
                                                type = StarPropertyMeta.TYPE_BOOLEAN;
                                                // fix datatype:
                                                field.setDataType("boolean");
                                            } else {
                                                type = StarPropertyMeta.TYPE_STRING;
                                                nStrFields++;
                                            }
                                        } else {
                                            type = StarPropertyMeta.TYPE_STRING;
                                            nStrFields++;
                                        }
                                    } else if (fieldDataType.equals("boolean")) {
                                        type = StarPropertyMeta.TYPE_BOOLEAN;
                                    } else if (fieldDataType.equals("int")) {
                                        type = StarPropertyMeta.TYPE_INTEGER;
                                    }
                                    // set parsed field type
                                    loadMapping.valueType = type;
                                } else {
                                    throw new IllegalArgumentException("Invalid VOTable - empty datatype attribute for field " + name);
                                }

                                // Get back the optional field link
                                final String url;

                                if (field.getLinks().getItemCount() != 0) {
                                    url = field.getLinks().getItemAt(0).getHref();
                                } else {
                                    url = "";
                                }

                                // Define a new star property meta data to hold the star property value:
                                propertyMeta = new StarPropertyMeta(name, type,
                                        field.getDescription(), field.getUcd(), field.getUnit(), url);

                            } else {
                                // Check if it is an error FIELD:
                                final String ucd = field.getUcd();
                                if (loadMapping.errorPos == null && ucd != null && ucd.endsWith("_ERROR")) {
                                    valid = true;
                                    // error field name
                                    loadMapping.errorName = name;

                                    // field error
                                    loadMapping.errorPos = pos;

                                    if (_logger.isDebugEnabled()) {
                                        _logger.debug("ERROR Field: name='{}' ucd='{}'", name, ucd);
                                    }

                                    // Define a new star property meta data to hold the star property error:
                                    propertyErrorMeta = new StarPropertyMeta(name, StarPropertyMeta.TYPE_DOUBLE,
                                            field.getDescription(), ucd, field.getUnit(), "");
                                }
                            }
                        }
                    }
                    if (!valid) {
                        throw new IllegalArgumentException("Invalid VOTable - invalid Field: name='" + name + "' ucd='" + field.getUcd() + "'");
                    }
                } // FieldRefs

                // Update property mapping (based on Field mapping only):
                if (loadMapping.valuePos != null) {

                    // Add the star property meta data to the star list meta data:
                    final Integer propertyPos = starListMeta.addPropertyMeta(propertyMeta);

                    // StarProperty.value mapping
                    saveMappings[loadMapping.valuePos] = new VOTableSaveMapping(loadMapping.name, propertyPos, VOTableSaveMapping.FieldType.VALUE, loadMapping.valueType);

                    if (loadMapping.originPos != null) {
                        saveMappings[loadMapping.originPos] = new VOTableSaveMapping(loadMapping.name, propertyPos, VOTableSaveMapping.FieldType.ORIGIN);
                    }
                    if (loadMapping.confidencePos != null) {
                        saveMappings[loadMapping.confidencePos] = new VOTableSaveMapping(loadMapping.name, propertyPos, VOTableSaveMapping.FieldType.CONFIDENCE);
                    }
                    if (loadMapping.errorPos != null) {
                        // Add the property error meta data to the star list meta data:
                        final Integer errorPos = starListMeta.addPropertyMeta(propertyErrorMeta);

                        saveMappings[loadMapping.errorPos] = new VOTableSaveMapping(loadMapping.errorName, errorPos, VOTableSaveMapping.FieldType.ERROR);
                    }
                }

                // Parse param refs:
                final ParamRefSet paramRefs = group.getParamsRef();
                final int nParamRefs = paramRefs.getItemCount();

                for (int i = 0; i < nParamRefs; i++) {
                    final String ref = paramRefs.getItemAt(i).getRef();

                    // Resolve the reference (field)
                    final SavotParam param = paramSetWrapper.getById(ref);

                    if (param == null) {
                        throw new IllegalArgumentException("Invalid VOTable - invalid PARAMref '" + ref + "'");
                    }

                    boolean valid = false;

                    // Check param name as ucd may be used also by properties
                    // (CODE_QUALITY or REFER_CODE for example ...)
                    final String name = param.getName();

                    if (name.length() != 0) {
                        if (name.endsWith("origin")) {
                            valid = true;
                            // define Origin constant value as resolved Origin enum:
                            loadMapping.originConst = Origin.parse(param.getValue());
                        } else if (name.endsWith("confidence")) {
                            valid = true;
                            // define Confidence constant value as resolved Confidence enum:
                            loadMapping.confidenceConst = Confidence.parse(param.getValue());
                        }
                    }
                    if (!valid) {
                        throw new IllegalArgumentException("Invalid VOTable - invalid Param: name='" + name + "' ucd='" + param.getUcd() + "'");
                    }
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("load mappings: {}", Arrays.toString(loadMappings));
                _logger.debug("save mappings: {}", Arrays.toString(saveMappings));
            }

            // update number of properties to size collections properly:
            nProperties = starListMeta.getPropertyCount() + N_EXTRA_PROPERTIES;

            _logger.debug("nProperties: {}", nProperties);
            _logger.debug("Properties: {}", starListMeta.getPropertyNames());

            // reserve space:
            if (tableRows != 0) {
                starList.ensureCapacity(tableRows);
            }

            int nRow = 0;

            if (this.trFirst != null) {
                SavotTR tr = trFirst; // consume first TR
                TDSet row;
                Vector<StarProperty> star;
                Object propertyValue;
                int groupId;
                String value, originValue, confidenceValue;
                Origin origin;
                Confidence confidence;
                StarProperty starProperty;
                int nStrValues = 0;
                boolean isDbl;
                double dblValue;
                final Object fakeDblValue = "1";

                // Origin and Confidence index are enumerations: use instance cache to use less memory
                // Origin resolved instance cache:
                final HashMap<String, Origin> originValues = new HashMap<String, Origin>(32);

                // TODO: put origin values (default mapping) for SearchCal release 4.4 !
                // => format conversion (2013.7) ...
                // Confidence resolved instance cache:
                final HashMap<String, Confidence> confidenceValues = new HashMap<String, Confidence>(8); // only 4 different values

                if (_logger.isDebugEnabled()) {
                    _logger.debug("nStrFields: {} / max value count: {}", nStrFields, nStrFields * tableRows);
                }

                // String instance cache to use less memory (25% capacity):
                final HashMap<String, String> stringCache = new HashMap<String, String>(nStrFields * tableRows >> 2);

                // note: Double instance cache has too much overhead (cpu / memory) for only 50% gain (number of instances)
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
                    // note: use TDSet that ensures range checks
                    row = tr.getTDSet();

                    // For each group of the row create a star property of each 3 cells
                    // note: Vector type is required by swing DefaultTableModel:
                    star = new Vector<StarProperty>(nProperties);

                    for (groupId = 0; groupId < nGroups; ++groupId) {
                        isDbl = false;
                        propertyValue = null;
                        dblValue = Double.NaN;
                        origin = Origin.ORIGIN_NONE;
                        confidence = Confidence.CONFIDENCE_NO;

                        // use field mapping:
                        loadMapping = loadMappings[groupId];

                        // Get the value
                        value = (loadMapping.valuePos != null) ? row.getRawContent(loadMapping.valuePos) : null;

                        if (value != null) {
                            // Get property type:
                            type = loadMapping.valueType;

                            switch (type) {
                                case StarPropertyMeta.TYPE_DOUBLE:
                                    // Stilts uses NaN for null double values:
                                    if ("NaN".equals(value)) {
                                        propertyValue = null;
                                    } else {
                                        try {
                                            dblValue = Double.parseDouble(value);
                                            propertyValue = fakeDblValue;
                                            isDbl = true;
                                        } catch (NumberFormatException nfe) {
                                            _logger.warn("invalid Double value [{}] at column index={}", value, loadMapping.valuePos);
                                        }
                                    }
                                    break;
                                case StarPropertyMeta.TYPE_STRING:
                                    nStrValues++;
                                    propertyValue = stringCache.get(value);
                                    if (propertyValue == null) {
                                        propertyValue = value;
                                        // cache String (never null):
                                        stringCache.put(value, value);
                                    }
                                    break;
                                case StarPropertyMeta.TYPE_BOOLEAN:
                                    // Test first "0" or "1" values (
                                    if (value.length() == 1) {
                                        propertyValue = parseBoolean(value.charAt(0));
                                    } else {
                                        if (isOutputFormatUndefined) {
                                            // fix OK / NOK handling (SearchCal 4.x):
                                            if ("OK".equals(value)) {
                                                propertyValue = Boolean.TRUE;
                                                break;
                                            }
                                            // NOK will become Boolean.FALSE (below):
                                        }
                                        // deleted flag (true|false) before SearchCal 5.0:
                                        propertyValue = Boolean.valueOf(value);
                                    }
                                    break;
                                case StarPropertyMeta.TYPE_INTEGER:
                                    try {
                                    propertyValue = NumberUtils.valueOf(value);
                                } catch (NumberFormatException nfe) {
                                    _logger.warn("invalid Integer value [{}] at column index={}", value, loadMapping.valuePos);
                                }
                                break;
                                default:
                                    _logger.warn("unsupported data type [{}]: {}", type, value);
                            }
                        }

                        if (propertyValue == null) {
                            starProperty = StarProperty.EMPTY_STAR_PROPERTY;
                        } else {
                            // Get the origin
                            if (loadMapping.originPos != null) {
                                originValue = row.getRawContent(loadMapping.originPos);

                                if (originValue != null) {
                                    // use cached instance if available:
                                    origin = originValues.get(originValue);

                                    if (origin == null) {
                                        origin = Origin.parse(originValue);

                                        // cache resolved Origin (never null):
                                        originValues.put(originValue, origin);
                                    }
                                }
                            } else {
                                // Use Origin constant value:
                                origin = loadMapping.originConst;
                            }

                            // Get the confidence
                            if (loadMapping.confidencePos != null) {
                                confidenceValue = row.getRawContent(loadMapping.confidencePos);

                                if (confidenceValue != null) {
                                    // use cached instance if available:
                                    confidence = confidenceValues.get(confidenceValue);

                                    if (confidence == null) {
                                        confidence = Confidence.parse(confidenceValue);

                                        // cache resolved Confidence (never null):
                                        confidenceValues.put(confidenceValue, confidence);
                                    }
                                }
                            } else {
                                // Use Confidence constant value:
                                confidence = loadMapping.confidenceConst;
                            }

                            /*
                             * Create a new StarProperty instance from the retrieved value, origin and confidence.
                             */
                            if (isDbl) {
                                starProperty = new DoubleStarProperty(dblValue, origin.getKey(), confidence.getKey());
                            } else {
                                starProperty = new ObjectStarProperty(propertyValue, origin.getKey(), confidence.getKey());
                            }

                        } // value is set

                        // Add the newly created star property to the star property list
                        star.add(starProperty);

                        // handle error field:
                        if (loadMapping.errorPos != null) {
                            isDbl = false;
                            propertyValue = null;
                            dblValue = Double.NaN;

                            // Get the error
                            value = row.getRawContent(loadMapping.errorPos);

                            if (value != null) {
                                // Parse error value:
                                // Stilts uses NaN for null double values:
                                if ("NaN".equals(value)) {
                                    propertyValue = null;
                                } else {
                                    try {
                                        dblValue = Double.parseDouble(value);
                                        propertyValue = fakeDblValue;
                                        isDbl = true;
                                    } catch (NumberFormatException nfe) {
                                        _logger.warn("invalid Double error [{}] at column index={}", value, loadMapping.errorPos);
                                    }
                                }
                            }
                            if (propertyValue == null) {
                                starProperty = StarProperty.EMPTY_STAR_PROPERTY;
                            } else {
                                /*
                                 * Create a new StarProperty instance from the retrieved error, origin and confidence.
                                 */
                                if (isDbl) {
                                    starProperty = new DoubleStarProperty(dblValue, origin.getKey(), confidence.getKey());
                                } else {
                                    starProperty = new ObjectStarProperty(propertyValue, origin.getKey(), confidence.getKey());
                                }
                            }

                            // Add the newly created star property to the star property list
                            star.add(starProperty);
                        }
                    }

                    // Store each VOTable row as a list of star properties
                    starList.add(star);

                    nRow++;

                } while ((tr = parser.getNextTR()) != null);

                if (_logger.isDebugEnabled()) {
                    _logger.debug("stringCache: size: {} / values: {}", stringCache.size(), nStrValues);
                }

                // trim to size (jsdc):
                starList.trimToSize();
            }

            _logger.info("CalibratorsModel.parseVOTable: {} rows read in {} ms.", nRow, 1e-6d * (System.nanoTime() - start));

            return starList;
        }

        /**
         * Refresh the GUI using the given star list
         * This code is executed by the Swing Event Dispatcher thread (EDT)
         * @param starList parsed StarList
         */
        @Override
        public void refreshUI(final StarList starList) {
            // Clear missing cols before new parsing:
            calModel.getIgnoreMissingColumns().clear();

            // reset computation states:
            calModel.vis2State.reset();
            calModel.distState.reset();

            // update calibrators model:
            calModel.updateModelFromVOTable(savotVoTable, paramSetWrapper, starList, saveMappings);

            _logger.info("CalibratorsModel.parseVOTable done: {} ms.", 1e-6d * (System.nanoTime() - startTime));

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
                        queryModel.loadParameters(calModel.getParameters());
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
                        queryModel.loadParameters(calModel.getParameters());

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
     * @param paramSetWrapper parameterSet wrapper
     * @param starList parsed StarList
     * @param saveMappings Field Save mapping
     */
    void updateModelFromVOTable(final SavotVOTable savotVoTable,
                                final ParameterSetWrapper paramSetWrapper,
                                final StarList starList,
                                final VOTableSaveMapping[] saveMappings) {
        // Keep only VOTable structure (without data):
        _parsedVOTable = savotVoTable;

        // Update Field Save mapping:
        _saveMappings = saveMappings;

        // Update Parameters:
        _paramSetWrapper = paramSetWrapper;

        // If server log are present, dump them:
        if (savotVoTable.getInfos().getItemCount() != 0) {
            final String log = savotVoTable.getInfos().getItemAt(0).getContent();
            _serverLog.info("Server Log\n---------------------------------------\n{}\n---------------------------------------", log);
        }

        // Compute selected magnitude band and scenario
        // Retrieve VOTable parameters
        _magnitudeBand = paramSetWrapper.getValue(PARAMETER_BAND, QueryModel.DEF_MAGNITUDE_BAND);

        /* scenario bright because it is the only available for any instrumental band */
        _brightScenarioFlag = Boolean.valueOf(paramSetWrapper.getValue(PARAMETER_BRIGHT, "true"));

        if (_logger.isDebugEnabled()) {
            _logger.debug("magnitude band = '{}'; bright scenario = '{}'.", _magnitudeBand, _brightScenarioFlag);
        }

        // Add SearchCal versions as PARAM if missing (before sclsvr 4.1):
        if (paramSetWrapper.get(PARAMETER_SCL_SERVER_VERSION) == null) {
            final SavotResource resource = savotVoTable.getResources().getItemAt(0);

            paramSetWrapper.add(PARAMETER_SCL_SERVER_VERSION, "char", resource.getName());
        }
        if (paramSetWrapper.get(PARAMETER_SCL_GUI_VERSION) == null) {
            final String version = ApplicationDescription.getInstance().getProgramVersion();

            paramSetWrapper.add(PARAMETER_SCL_GUI_VERSION, "char", version);
        }
        // Add SearchCal outputFormat parameter (sclsvr 5.0):
        if (paramSetWrapper.get(PARAMETER_SCL_OUTPUT_FORMAT) == null) {
            paramSetWrapper.add(PARAMETER_SCL_OUTPUT_FORMAT, "double", GUI_OUTPUT_FORMAT);
        }

        // Add SearchCal ConfidenceIndexes parameter (sclsvr 5.0):
        if (paramSetWrapper.get(PARAMETER_SCL_CONFIDENCE_INDEXES) == null) {
            final SavotParam param = ParameterSetWrapper.newParam(PARAMETER_SCL_CONFIDENCE_INDEXES, "int",
                    Confidence.CONFIDENCE_NO.getKeyString());

            final SavotValues values = new SavotValues();
            final OptionSet options = values.getOptions();
            for (Confidence c : Confidence.values()) {
                SavotOption option = new SavotOption();
                option.setName(c.getKeyString());
                option.setValue(c.getValue());
                options.addItem(option);
            }
            param.setValues(values);
            paramSetWrapper.add(param);
        }

        // Add SearchCal OriginIndexes parameter (sclsvr 5.0):
        if (paramSetWrapper.get(PARAMETER_SCL_ORIGIN_INDEXES) == null) {
            final SavotParam param = ParameterSetWrapper.newParam(PARAMETER_SCL_ORIGIN_INDEXES, "int", Origin.ORIGIN_NONE.getKeyString());

            final SavotValues values = new SavotValues();
            final OptionSet options = values.getOptions();
            for (Origin o : Origin.values()) {
                SavotOption option = new SavotOption();
                option.setName(o.getKeyString());
                option.setValue(o.getValue());
                options.addItem(option);
            }
            param.setValues(values);
            paramSetWrapper.add(param);
        }

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
        removeDeletedStars(false);
    }

    /**
     * Post process the given star list to add dynamic properties
     * @param starList star list to process
     */
    void postProcess(final StarList starList) {
        if (!starList.isEmpty()) {
            final StarListMeta starListMeta = starList.getMetaData();

            // Add custom properties:
            // Add RA (deg) property:
            if (starListMeta.getPropertyIndexByName(StarList.RADegColumnName) == -1) {
                starListMeta.addPropertyMeta(new StarPropertyMeta(StarList.RADegColumnName, StarPropertyMeta.TYPE_DOUBLE,
                        "Right Ascension - J2000", "POS_EQ_RA_MAIN", "deg", ""));

                // Get the ID of the column containing 'RA' star properties
                final int raId = starList.getColumnIdByName(StarList.RAJ2000ColumnName);
                if (raId != -1) {
                    for (List<StarProperty> star : starList) {

                        // Get the current star RA value
                        final StarProperty cell = star.get(raId);

                        final double raDeg = cell.hasValue() ? ALX.parseRA(cell.getString()) : Double.NaN;

                        // add RADeg value:
                        star.add(new DoubleStarProperty(raDeg,
                                cell.getOriginIndex(), cell.getConfidenceIndex()));
                    }
                }
            }

            // Add DEC (deg) property:
            if (starListMeta.getPropertyIndexByName(StarList.DEDegColumnName) == -1) {
                starListMeta.addPropertyMeta(new StarPropertyMeta(StarList.DEDegColumnName, StarPropertyMeta.TYPE_DOUBLE,
                        "Declination - J2000", "POS_EQ_DEC_MAIN", "deg", ""));

                // Get the ID of the column containing 'DEC' star properties
                final int decId = starList.getColumnIdByName(StarList.DEJ2000ColumnName);
                if (decId != -1) {
                    for (List<StarProperty> star : starList) {

                        // Get the current star DEC value
                        final StarProperty cell = star.get(decId);

                        final double deDeg = cell.hasValue() ? ALX.parseDEC(cell.getString()) : Double.NaN;

                        // add DEDeg value:
                        star.add(new DoubleStarProperty(deDeg,
                                cell.getOriginIndex(), cell.getConfidenceIndex()));
                    }
                }
            }

            // Add row index property:
            if (starListMeta.getPropertyIndexByName(StarList.RowIdxColumnName) == -1) {
                starListMeta.addPropertyMeta(new StarPropertyMeta(StarList.RowIdxColumnName, StarPropertyMeta.TYPE_INTEGER,
                        "row index", "ID_NUMBER", "", ""));

                int i = 0;
                for (List<StarProperty> star : starList) {
                    // add rowIdx value:
                    star.add(new ObjectStarProperty(NumberUtils.valueOf(++i)));
                }
            }

            // Update distance:
            final String scienceRA = _paramSetWrapper.getValue(PARAMETER_RA);
            final String scienceDE = _paramSetWrapper.getValue(PARAMETER_DEC);

            // Compute distance:
            computeDistance(starList, scienceRA, scienceDE);

            // Update vis2/vis2Err properties:
            // Get instrument band
            final String band = _magnitudeBand;
            // Get value of the wavelength (m)
            final double wavelength = 1e-6 * Double.parseDouble(_paramSetWrapper.getValue(PARAMETER_WAVELENGTH));
            // Get value of the base max (m)
            final double baseMax = Double.parseDouble(_paramSetWrapper.getValue(PARAMETER_BASE_MAX));

            // Compute visibilities BEFORE filters:
            computeVisibility(starList, band, wavelength, baseMax);

            // First star:
            final Vector<StarProperty> first = (Vector<StarProperty>) starList.get(0);

            _logger.info("{} Star Properties.", first.size());

            if (first.size() > first.capacity()) {
                _logger.warn("Incorrect Property capacity: {} != {} ", first.size(), first.capacity());
            }

            // DEV: Check cross identification with Simbad
            if (DO_CROSS_ID_CHECKER) {
                fr.jmmc.sclgui.vo.CrossIdChecker.check(starList);
            }

        }
    }

    /**
     * Update the square visibility and its error on the given star list
     * @param starList
     * @param band instrumental band
     * @param wavelength wavelength (m)
     * @param baseMax base max (m)
     * @return true if computation done; false otherwise
     */
    private boolean computeVisibility(final StarList starList, final String band, final double wavelength, final double baseMax) {

        // Compare with current state:
        if (!vis2State.setIfModified(band, wavelength, baseMax)) {
            return false;
        }

        // Get the ID of the column containing 'vis2' star properties
        final int vis2Id = starList.getColumnIdByName(StarList.Vis2ColumnName);
        final int e_vis2Id = starList.getColumnIdByName(StarList.Vis2ErrColumnName);

        // Use existing vis2 columns:
        if ((vis2Id == -1) || (e_vis2Id == -1)) {
            // Missing vis2 / vis2Err properties (should be present in any server response)
            _logger.warn("computeVisibility: bad case: vis2 / vis2Err are missing !");
            return false;
        }

        // Get star properties:
        // Get the ID of the column containing 'diamFlag' star properties
        final int diamFlagId = starList.getColumnIdByName(StarList.DiamFlagColumnName);

        // Get the ID of the column containing 'UD_i' and 'e_LDD' star properties
        final String udColName = "UD_" + band;

        _logger.debug("computeVisibility: using diameter {}", udColName);

        final int diamUdId = starList.getColumnIdByName(udColName);

        if ((diamFlagId == -1) || (diamUdId == -1)) {
            // Missing diamFlag / UD_i properties (should be present in any server response)
            _logger.warn("computeVisibility: bad case: diamFlag / {} / e_LDD are missing !", udColName);
            return false;
        }

        // Consider e_UD_<i> = e_LDD:
        final int diamLddId = starList.getColumnIdByName("LDD");
        final int e_diamLddId = starList.getColumnIdByName("e_LDD");

        // Get the IDs of other error columns (SearchCal 4.x or older):
        final int e_diamVKId = starList.getColumnIdByName("e_diam_vk");

        if ((diamLddId == -1) && (e_diamLddId == -1) && (e_diamVKId == -1)) {
            // Missing e_LDD / e_diam_mean properties (should be present in any server response)
            _logger.warn("computeVisibility: bad case: LDD / e_LDD / e_diam_vk are missing !");
            return false;
        }

        final int originIndex = Origin.KEY_ORIGIN_COMPUTED;
        int confidenceIndex;

        StarProperty pDiam, pErrDiam, diamFlag;
        StarProperty pVis2, pErrVis2;
        double diam, diamError;
        boolean valid;

        pDiam = pErrDiam = null;

        final VisibilityResult visRes = new VisibilityResult();

        final long start = System.nanoTime();

        for (List<StarProperty> star : starList) {
            valid = false;

            // Get the current star diamFlag value
            diamFlag = star.get(diamFlagId);

            // If computed diameter is OK
            if (diamFlag.hasValue() && diamFlag.getBooleanValue()) {

                // Get the UD diameter for the given band with e_UD_i = e_LDD:
                pDiam = star.get(diamUdId);

                // if no UD (no Teff...) then use LDD:
                if (!pDiam.hasValue() && (diamLddId != -1)) {
                    pDiam = star.get(diamLddId);
                }

                valid = pDiam.hasValue();

                if (valid) {
                    pErrDiam = null;
                    if (pErrDiam == null && e_diamLddId != -1) {
                        pErrDiam = star.get(e_diamLddId);
                        if (!pErrDiam.hasValue()) {
                            pErrDiam = null;
                        }
                    }
                    if (pErrDiam == null && e_diamVKId != -1) {
                        pErrDiam = star.get(e_diamVKId);
                        if (!pErrDiam.hasValue()) {
                            pErrDiam = null;
                        }
                    }
                    valid = (pErrDiam != null);
                }
            }

            if (valid) {
                // Get confidence index of computed diameter
                confidenceIndex = pDiam.getConfidenceIndex();

                // Get values
                diam = pDiam.getDoubleValue();
                diamError = pErrDiam.getDoubleValue();

                VisibilityUtils.computeVisibility(diam, diamError, baseMax, wavelength, visRes);
                // only 9 digits:
                visRes.vis2 = NumberUtils.trimTo9Digits(visRes.vis2);
                visRes.vis2Err = NumberUtils.trimTo9Digits(visRes.vis2Err);
            } else {
                // reset:
                confidenceIndex = Confidence.KEY_CONFIDENCE_NO;
                visRes.vis2 = Double.NaN;
                visRes.vis2Err = Double.NaN;
            }
            // Get the current star vis2 value
            pVis2 = star.get(vis2Id);
            pErrVis2 = star.get(e_vis2Id);

            if (DO_COMPARE_VIS2) {
                if (pVis2.hasValue() && pErrVis2.hasValue()) {
                    _logger.info("DELTA vis2={} vis2Err={}",
                            NumberUtils.format(Math.abs(pVis2.getDoubleValue() - visRes.vis2)),
                            NumberUtils.format(Math.abs(pErrVis2.getDoubleValue() - visRes.vis2Err)));
                }
            }

            // Update values:
            setStarProperty(star, pVis2, vis2Id, visRes.vis2, originIndex, confidenceIndex);
            setStarProperty(star, pErrVis2, e_vis2Id, visRes.vis2Err, originIndex, confidenceIndex);
        }
        _logger.info("CalibratorsModel.computeVisibility: {} rows done in {} ms.", starList.size(), 1e-6d * (System.nanoTime() - start));

        return true;
    }

    /**
     * Update the distance on the given star list
     * @param starList
     * @param scienceRA right ascension
     * @param scienceDE declination
     * @return true if computation done; false otherwise
     */
    private boolean computeDistance(final StarList starList, final String scienceRA, final String scienceDE) {

        // Compare with current state:
        if (!distState.setIfModified(scienceRA, scienceDE)) {
            return false;
        }

        // Get the ID of the column containing 'dist' star properties
        final int distId = starList.getColumnIdByName(StarList.DistColumnName);

        // Use existing dist column:
        if (distId == -1) {
            // Missing dist properties (should be present in any server response)
            _logger.warn("computeDistance: bad case: dist are missing !");
            return false;
        }

        // Get star properties:
        // Get the IDs of the DYNAMIC columns containing 'RA' & 'DEC' (degrees) star properties
        final int raId = starList.getColumnIdByName(StarList.RADegColumnName);
        final int decId = starList.getColumnIdByName(StarList.DEDegColumnName);

        if ((raId == -1) || (decId == -1)) {
            // Missing raId /decId properties
            return false;
        }

        // Get the science object 'RA' and 'DEC' properties
        final double scienceObjectRA = ALX.parseRA(scienceRA);
        final double scienceObjectDE = ALX.parseDEC(scienceDE);

        final int originIndex = Origin.KEY_ORIGIN_COMPUTED;
        final int confidenceIndex = Confidence.KEY_CONFIDENCE_HIGH;

        StarProperty pRA, pDE;
        StarProperty pDist;
        double separation;

        final long start = System.nanoTime();

        for (List<StarProperty> star : starList) {

            // Get the current star RA value
            pRA = star.get(raId);

            // Get the current star DEC value
            pDE = star.get(decId);

            // If the 2 values were found in the current line
            if (pRA.hasValue() && pDE.hasValue()) {
                separation = CoordUtils.computeDistanceInDegrees(scienceObjectRA, scienceObjectDE,
                        pRA.getDoubleValue(), pDE.getDoubleValue());
                // only 9 digits:
                separation = NumberUtils.trimTo9Digits(separation);
            } else {
                separation = Double.NaN;
            }

            // Get the current star dist value
            pDist = star.get(distId);

            if (DO_COMPARE_DIST) {
                if (pDist.hasValue()) {
                    _logger.info("DELTA dist={}",
                            NumberUtils.format(Math.abs(pDist.getDoubleValue() - separation)));
                }
            }

            // Update values:
            setStarProperty(star, pDist, distId, separation, originIndex, confidenceIndex);
        }
        _logger.info("CalibratorsModel.computeDistance: {} rows done in {} ms.", starList.size(), 1e-6d * (System.nanoTime() - start));

        return true;
    }

    private static void setStarProperty(final List<StarProperty> star, final StarProperty prop, final int propId,
                                        final double value, final int originIndex, final int confidenceIndex) {
        if (Double.isNaN(value)) {
            if (prop != StarProperty.EMPTY_STAR_PROPERTY) {
                // replace star property:
                star.set(propId, StarProperty.EMPTY_STAR_PROPERTY);
            }
        } else {
            if (prop == StarProperty.EMPTY_STAR_PROPERTY) {
                // replace star property:
                star.set(propId, new DoubleStarProperty(value, originIndex, confidenceIndex));
            } else {
                // update star property:
                prop.set(value, originIndex, confidenceIndex);
            }
        }
    }

    /**
     * Give back the query parameters.
     *
     * @return query parameters.
     */
    public ParameterSetWrapper getParameters() {
        return _paramSetWrapper;
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

        // If no Field Save Mapping
        if (_saveMappings == null) {
            _logger.warn("Could not get field mapping");
            return false;
        }

        final String filename = file.getAbsolutePath();

        _logger.info("Saving votable file: {}", filename);

        boolean ok = false;

        final long start = System.nanoTime();
        try {
            final SavotVOTable votable = _parsedVOTable;
            final VOTableSaveMapping[] saveMappings = _saveMappings;

            // Use SavotWriter TR / TR:
            final SavotWriter wd = new SavotWriter();
            wd.enableAttributeEntities(true);

            // @See SavotWriter.generateDocument():
            wd.initStream(filename);

            wd.writeDocumentHead(votable);

            // write COOSYS elements - deprecated since VOTable 1.2
            wd.writeCoosys(votable.getCoosys());

            // write INFO elements
            wd.writeInfo(votable.getInfos());

            // do not encode elements below:
            wd.enableElementEntities(false);

            // write PARAM elements
            wd.writeParam(votable.getParams());

            // write GROUP elements - since VOTable 1.2
            wd.writeGroup(votable.getGroups());

            // RESOURCE: custom writer to perform row by row processing
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

            // write PARAM elements (FIRST)
            wd.writeParam(table.getParams());

            // write FIELD elements
            wd.writeField(table.getFields());

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
                    final int nFields = saveMappings.length;

                    final SavotTD[] cacheTD = new SavotTD[nFields];

                    int fieldId;
                    for (fieldId = 0; fieldId < nFields; fieldId++) {
                        cacheTD[fieldId] = new SavotTD();
                    }

                    // shared empty TD instance
                    final SavotTD emptyTD = new SavotTD();

                    final SavotTR tr = new SavotTR();
                    // Get raw TDSet from TR:
                    final List<SavotTD> row = tr.getTDSet(nFields).getItems();

                    StarProperty property;
                    int type, propertyPos;
                    VOTableSaveMapping saveMapping;
                    SavotTD td;

                    // And create one row per star entry
                    for (final List<StarProperty> star : starList) {

                        row.clear(); // recycle row ie TR and TDSet instance

                        // Get only server properties not dynamic ones:
                        for (fieldId = 0; fieldId < nFields; fieldId++) {
                            saveMapping = saveMappings[fieldId];

                            td = emptyTD;

                            if (saveMapping != null) {
                                propertyPos = saveMapping.propertyPos;
                                property = star.get(propertyPos);

                                td = cacheTD[fieldId];

                                switch (saveMapping.fieldType) {
                                    case VALUE:
                                        // Get property type:
                                        type = saveMapping.valueType;

                                        // Set field value:
                                        if (property.hasValue()) {
                                            if (type == StarPropertyMeta.TYPE_BOOLEAN) {
                                                // custom boolean to string conversion:
                                                td.setContent(property.getBooleanValue() ? "1" : "0");
                                            } else {
                                                // implicitely convert double / integer to string:
                                                td.setContent(property.getStringValue());
                                            }
                                            break;
                                        }

                                        /* handle / fix null value handling
                                         * as VOTABLE 1.1 does not support nulls for integer (-INF) / double values (NaN)
                                         * note: stilts complains and replaces empty cells by (-INF) and (NaN) */

 /* TODO: switch to VOTABLE 1.3 that supports null values */
                                        switch (type) {
                                            case StarPropertyMeta.TYPE_DOUBLE:
                                            /* do not use NaN (useless and annoying in XSLT scripts) */
//                                                td.setContent("NaN");
//                                                break;
                                            case StarPropertyMeta.TYPE_STRING:
                                            default:
                                                td = emptyTD;
                                                break;
                                            case StarPropertyMeta.TYPE_INTEGER:
                                            case StarPropertyMeta.TYPE_BOOLEAN:
                                                td.setContent("0"); // 0 or false as defaults
                                        }
                                        break;
                                    case ORIGIN:
                                        // Origin index (always defined even if property has no value):
                                        td.setContent(property.getOrigin().getKeyString());
                                        break;
                                    case CONFIDENCE:
                                        // Confidence index (always defined even if property has no value):
                                        td.setContent(property.getConfidence().getKeyString());
                                        break;
                                    case ERROR:
                                        // Set field value:
                                        if (property.hasValue()) {
                                            // implicitely convert double to string:
                                            td.setContent(property.getStringValue());
                                            break;
                                        }
                                    /* do not use NaN (useless and annoying in XSLT scripts) */
//                                                td.setContent("NaN");
//                                                break;
                                    case NONE:
                                    default:
                                        td = emptyTD;
                                }
                            }
                            // add cell:
                            row.add(td);
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
            _logger.error("Unable to save the starlist as votable into file: {}", filename, ioe);
        } finally {
            if (ok) {
                _logger.info("CalibratorsModel.saveVOTableFile: file saved in {} ms.", 1e-6d * (System.nanoTime() - start));
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

        final String xsltFile = ResourceUtils.getPathFromResourceFilename(xslFileName);
        if (xsltFile == null) {
            _logger.error("Could not load XSL file '{}'.", xslFileName);

            return;
        }

        try {
            // Prepare the input and output files
            final File tmpFile = FileUtils.getTempFile("currentStarList.scvot");

            // TODO: decide to export all or only filtered:
            final StarList starList = _filteredStarList; // _currentStarList

            if (saveVOTableFile(tmpFile, starList)) {
                final InputStream sourceStream = new BufferedInputStream(new FileInputStream(tmpFile));
                final OutputStream resultStream = new BufferedOutputStream(new FileOutputStream(outputFile));

                final long startTime = System.nanoTime();

                // Apply the xsl file to the source file and write the result to
                // the output file
                // use an XSLT to transform the XML document to an HTML representation :
                XslTransform.transform(sourceStream, xsltFile, resultStream);

                _logger.info("applyXSLTranformationOnCurrentVOTable done: {} ms.", 1e-6d * (System.nanoTime() - startTime));
            }

        } catch (FileNotFoundException fnfe) {
            throw new IllegalStateException("File not found: ", fnfe);
        } catch (IllegalArgumentException iae) {
            // An error occurred in the XSL file
            if (iae.getCause() instanceof TransformerException) {
                // An error occurred while applying the XSL file
                // Get location of error in input file
                final SourceLocator locator = ((TransformerException) iae.getCause()).getLocator();

                if (locator != null) {
                    throw new IllegalStateException("An error occured while applying XSL file '" + xsltFile + "',"
                            + " on line '" + locator.getLineNumber() + "' and column '" + locator.getColumnNumber() + "'", iae);
                }
            }
            throw new IllegalStateException("An error occured during XSLT transformation '" + xsltFile + "'", iae);

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
            for (int i = 1; i <= nbOfRows; i++) {
                final Vector<Integer> row = new Vector<Integer>(1);
                row.add(NumberUtils.valueOf(i));
                addRow(row);
            }
        }
    }

    private static Boolean parseBoolean(final char ch) {
        // 0/1 or T/F cases:
        switch (ch) {
            case '1':
                return Boolean.TRUE;
            case '0':
                return Boolean.FALSE;
            case 't':
            case 'T':
                return Boolean.TRUE;
            case 'f':
            case 'F':
                return Boolean.FALSE;
            default:
                _logger.warn("invalid Boolean [{}]", ch);
                return Boolean.FALSE;

        }
    }

    private final class QueryModelObserverForDynamicColumns implements Observer {

        @Override
        public void update(final Observable o, final Object arg) {
            if (Notification.isModel(arg)) {
                final QueryModel qm = (QueryModel) o;

                final StarList starList = getOriginalStarList();

                if (!starList.isEmpty()) {
                    boolean changed = false;

                    // Update distance:
                    final String scienceRA = qm.getScienceObjectRA();
                    final String scienceDE = qm.getScienceObjectDEC();

                    // Compute distance:
                    if (computeDistance(starList, scienceRA, scienceDE)) {
                        changed = true;
                    }

                    // Update vis2/vis2Err properties:
                    // Get instrument band
                    final String band = qm.getInstrumentalMagnitudeBand();

                    // Get value of the wavelength (m)
                    final double wavelength = 1e-6 * qm.getInstrumentalWavelength();

                    // Get value of the base max (m)
                    final double baseMax = qm.getInstrumentalMaxBaseLine();

                    // Compute visibilities:
                    if (computeVisibility(starList, band, wavelength, baseMax)) {
                        changed = true;

                        // update parameters into VOTable:
                        // Set instrument band
                        _paramSetWrapper.setValue(PARAMETER_BAND, band);

                        // Set value of the wavelength (micro-m)
                        _paramSetWrapper.setValue(PARAMETER_WAVELENGTH, Double.toString(NumberUtils.trimTo3Digits(1e6 * wavelength)));

                        // Get value of the base max (m)
                        _paramSetWrapper.setValue(PARAMETER_BASE_MAX, Double.toString(NumberUtils.trimTo3Digits(baseMax)));
                    }

                    // Finally refresh calibrator list (and filters):
                    if (changed) {
                        // apply filters and fire Update:
                        updateModel(starList);
                    }
                }
            }
        }
    }

    /**
     * VOTable load mapping: it contains Group parsing results to load fields into StarProperty instances
     */
    private static final class VOTableLoadMapping {

        /** field name ie StarProperty name */
        String name = null;
        /** parsed field type as integer */
        int valueType = StarPropertyMeta.TYPE_ANY;
        /** field position of the StarProperty value */
        Integer valuePos = null;
        /** optional field position of the StarProperty origin index (FieldRef) */
        Integer originPos = null;
        /** optional resolved Origin of the StarProperty origin index (PARAMRef) */
        Origin originConst = Origin.ORIGIN_NONE;
        /** optional field position of the StarProperty confidence index (FieldRef) */
        Integer confidencePos = null;
        /** optional resolved Confidence of the StarProperty confidence index (PARAMRef) */
        Confidence confidenceConst = Confidence.CONFIDENCE_NO;
        /** error field name ie StarProperty error */
        String errorName = null;
        /** field position of the StarProperty error */
        Integer errorPos = null;

        @Override
        public String toString() {
            return "VOTableLoadMapping{" + "name=" + name
                    + ", valuePos=" + valuePos
                    + ", valueType=" + StarPropertyMeta.getClassType(valueType)
                    + ((originPos != null) ? ", originPos=" + originPos : "")
                    + ((originConst != Origin.ORIGIN_NONE) ? ", originConst=" + originConst : "")
                    + ((confidencePos != null) ? ", confidencePos=" + confidencePos : "")
                    + ((confidenceConst != Confidence.CONFIDENCE_NO) ? ", confidenceConst=" + confidenceConst : "")
                    + ((errorName != null) ? ", errorName=" + errorName : "")
                    + ((errorPos != null) ? ", errorPos=" + errorPos : "")
                    + '}';
        }
    }

    /**
     * VOTable save mapping: it contains field mapping to save fields from StarProperty instances
     */
    private static final class VOTableSaveMapping {

        enum FieldType {

            NONE,
            VALUE,
            ORIGIN,
            CONFIDENCE,
            ERROR;
        }
        /** field name ie StarProperty name */
        final String name;
        /** StarProperty position of the field */
        final Integer propertyPos;
        /** field type */
        final FieldType fieldType;
        /** parsed field type as integer */
        int valueType = StarPropertyMeta.TYPE_ANY;

        VOTableSaveMapping(final String name, final Integer propertyPos, final FieldType fieldType) {
            this.name = name;
            this.propertyPos = propertyPos;
            this.fieldType = fieldType;
        }

        VOTableSaveMapping(final String name, final Integer propertyPos, final FieldType fieldType, final int valueType) {
            this.name = name;
            this.propertyPos = propertyPos;
            this.fieldType = fieldType;
            this.valueType = valueType;
        }

        @Override
        public String toString() {
            return "VOTableSaveMapping{" + "name=" + name
                    + ((propertyPos != null) ? ", propertyPos=" + propertyPos : "")
                    + ((fieldType != FieldType.NONE) ? ", type=" + fieldType : "")
                    + ", valueType=" + StarPropertyMeta.getClassType(valueType)
                    + '}';
        }
    }

    private static final class DistParameters {

        private String ra;
        private String de;

        DistParameters() {
            reset();
        }

        void reset() {
            this.ra = null;
            this.de = null;
        }

        boolean setIfModified(final String ra, final String de) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("setIfModified: ra: [{} vs {}] - de: [{} vs {}]",
                        this.ra, ra, this.de, de
                );
            }
            boolean changed = false;
            if (!ObjectUtils.areEquals(this.ra, ra)) {
                this.ra = ra;
                changed = true;
            }
            if (!ObjectUtils.areEquals(this.de, de)) {
                this.de = de;
                changed = true;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("setIfModified: changed: {}", changed);
            }
            return changed;
        }
    }

    private static final class Vis2Parameters {

        private String band;
        /** wavelength (m) */
        private double wavelength;
        /** base max (m) */
        private double baseMax;

        Vis2Parameters() {
            reset();
        }

        void reset() {
            this.band = null;
            this.wavelength = Double.NaN;
            this.baseMax = Double.NaN;
        }

        boolean setIfModified(final String band, final double wavelength, final double baseMax) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("setIfModified: band: [{} vs {}] - wlen: [{} vs {}] - baseMax: [{} vs {}]",
                        this.band, band, this.wavelength, wavelength, this.baseMax, baseMax
                );
            }
            boolean changed = false;
            if (!ObjectUtils.areEquals(this.band, band)) {
                this.band = band;
                changed = true;
            }
            if (Double.compare(this.wavelength, wavelength) != 0) {
                this.wavelength = wavelength;
                changed = true;
            }
            if (Double.compare(this.baseMax, baseMax) != 0) {
                this.baseMax = baseMax;
                changed = true;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("setIfModified: changed: {}", changed);
            }
            return changed;
        }
    }
}
/*___oOo___*/
