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
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.task.TaskSwingWorker;
import fr.jmmc.jmcs.util.FileUtils;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

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
    private static final long serialVersionUID = 1L;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(CalibratorsModel.class.getName());
    /** parameter SearchCalGuiVersion (string) */
    public final static String PARAMETER_SCL_GUI_VERSION = "SearchCalGuiVersion";
    /** parameter SearchCalServerVersion (string) */
    public final static String PARAMETER_SCL_SERVER_VERSION = "SearchCalServerVersion";
    /* members */
    /** Filters */
    private final FiltersModel _filtersModel;
    /** Store the current query model in order to allow later update to progress bar */
    final QueryModel _queryModel;
    /** Store the main application object used to perform menu activation */
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
    /** Store the selected stars displayed and updated by calibratorView */
    private int[] _selectedStarIndices = null;
    /** number of columns */
    int _nColumns = 0;
    /** JTable column names (required Vector type) */
    final Vector<String> _columnNames = new Vector<String>();
    /** JTable column URLs */
    final ArrayList<String> _columnURLs = new ArrayList<String>();
    /** JTable column tool-tips */
    final ArrayList<String> _columnDescriptions = new ArrayList<String>();
    /** JTable column units */
    final ArrayList<String> _columnUnits = new ArrayList<String>();
    /** Column data types */
    final ArrayList<Class<?>> _columnClasses = new ArrayList<Class<?>>();
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
        _originalStarList = new StarList();
        _calibratorStarList = new StarList();
        _currentStarList = new StarList();
        _filteredStarList = new StarList();

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
     * Return the number of hidden stars
     * @return number of hidden stars
     */
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

        // Filter the displayed star list
        _filteredStarList = _filtersModel.process(_currentStarList);

        // As a DefaultTableModel instance, set all the JTable needed vectors
        setDataVector(_filteredStarList, _columnNames);

        // Generate as many raw headers as data raws
        _rowHeadersModel.populate(_filteredStarList.size());

        // Ask all the attached JTable views to update
        fireTableDataChanged();

        // If the update was launched from TableSorter(just for a GUI refresh)
        if (arg != null) {
            if (TableSorter.class == arg.getClass()) {
                // Don't consider it as a data modification
                return;
            }
        }

        // Remember that data have changed
        _dataHaveChanged = true;

        // Ask for SAMP export menu enabling if needed
        boolean shouldBeEnabled = (_currentStarList.size() != 0);
        _vo.couldEnableShareCalibratorsThroughSAMPAction(shouldBeEnabled);
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
    public Class<?> getColumnClass(final int column) {
        if (column >= 0 && column < _nColumns) {
            return _columnClasses.get(column);
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
            return _columnDescriptions.get(column);
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
            return _columnUnits.get(column);
        }
        return "";
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

        if (url.length() == 0) {
            return null;
        }

        // Convert the current value to HTML compatible encoding
        final String encodedValue = UrlUtils.encode(value);

        // Forge the URL by replacing any '${...}' token with the current value
        return url.replaceAll("[${].+[}]", encodedValue);
    }

    /**
     * Return whether the star property has an URL.
     *
     * @param column
     * @return true if a URL is set, false otherwise.
     */
    public boolean hasURL(final int column) {
        final String url = getColumnURL(column);
        if (url.length() == 0) {
            return false;
        }

        // If more than, or less than 1 '${...}' token in the URL
        final String[] array = url.split("[$]");

        if (array.length != 2) {
            // Discard this URL
            return false;
        }

        return true;
    }

    /**
     * Return the URL defined for the given column or "" if undefined
     * @param column
     * @return URL defined for the given column or "" if undefined
     */
    private String getColumnURL(final int column) {
        if (column >= 0 && column < _nColumns) {
            return _columnURLs.get(column);
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
     * Parse the given file as VOTable and update any attached JTable to show its content.
     *
     * @param file the votable file to parse as File
     * @throws IllegalArgumentException if given votable is not compatible with SearchCal format
     */
    public void parseVOTable(final File file) throws IllegalArgumentException {
        parseVOTable(file, null, file.length());
    }

    /**
     * Parse the given string as VOTable and update any attached JTable to show its content.
     *
     * @param voTable the votable content to parse as String
     * @throws IllegalArgumentException if given votable is not compatible with SearchCal format
     */
    public void parseVOTable(final String voTable) throws IllegalArgumentException {
        parseVOTable(null, voTable, voTable.length());
    }

    /**
     * Parse a given VOTable and update any attached JTable to show its content.
     *
     * @param file the votable file to parse as File
     * @param content the votable content to parse as String
     * @param length votable size in bytes
     * @throws IllegalArgumentException if given votable is not compatible with SearchCal format
     */
    private void parseVOTable(final File file, final String content, final long length) throws IllegalArgumentException {
        _vo.enableDataRelatedMenus(false);

        if (file == null && content == null) {
            throw new IllegalArgumentException("Incorrect VOTable input");
        }

        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("CalibratorsModel.parseVOTable: VOTable size = " + length + " bytes.");
        }

        final long startTime = System.nanoTime();

        final SavotPullParser parser;
        if (file != null) {
            parser = new SavotPullParser(file.getAbsolutePath(), SavotPullEngine.ROWREAD);
        } else {
            parser = new SavotPullParser(new StringReader(content), SavotPullEngine.ROWREAD);
        }

        // start parsing document and get first TR:
        SavotTR tr = parser.getNextTR();

        // Get the VOTable
        final SavotVOTable savotVoTable = parser.getVOTable();
        final ResourceSet resources = savotVoTable.getResources();

        SavotResource resource = null;
        SavotTable table = null;
        GroupSet groupSet = null;
        FieldSet fieldSet = null;

        if (tr != null && resources.getItemCount() == 1) {
            // SearchCal VOTable must have 1 resource:
            resource = resources.getItemAt(0);

            if (resource.getTables().getItemCount() == 1) {
                // SearchCal resource must have 1 table:
                table = resource.getTables().getItemAt(0);
                groupSet = table.getGroups();
                fieldSet = table.getFields();
            }
        }

        // check that the votable has one resource / table containing groups and fields:
        if (resource == null || table == null || groupSet.getItemCount() == 0 || fieldSet.getItemCount() == 0) {
            _logger.warning("Incorrect VOTable format");
            throw new IllegalArgumentException("Incorrect VOTable format");
        }

        // check that the votable corresponds to the SearchCal VOTable format:
        if (!resource.getName().startsWith("SearchCal")) {
            _logger.warning("Ressource should be 'SearchCal' but is : " + resource.getName());
            throw new IllegalArgumentException("Incorrect VOTable format; expected one SearchCal VOTable");
        }

        // TODO: try also to check PARAMETER_SCL_SERVER_VERSION !

        // Clear all internal lists before new parsing:
        _originalStarList.clear();
        _calibratorStarList.clear();
        _currentStarList.clear();

        // Update any attached observer TO FREE MEMORY:
        update(null, null);

        // Create parse votable task worker :
        // Cancel other tasks and execute this new task :
        new ParseVoTableSwingWorker(file, this, parser, savotVoTable, tr, startTime).executeTask();
    }

    /**
     * TaskSwingWorker child class to parse the votable to get a starlist
     */
    private final static class ParseVoTableSwingWorker extends TaskSwingWorker<StarList> {

        /* members */
        /** file in file mode or null in remote mode */
        private final File file;
        /** calibrators model used for refreshUI callback */
        private final CalibratorsModel calModel;
        /** Savot pull parser initialized */
        private final SavotPullParser parser;
        /** Savot VOTable partially loaded */
        private final SavotVOTable savotVoTable;
        /** first SavotTR tr */
        private final SavotTR trFirst;
        /** start time */
        private final long startTime;
        /* meta data */
        /** Param set */
        private ParamSet paramSet = null;
        /** JTable column names (required Vector type) */
        private final Vector<String> columnNames = new Vector<String>();
        /** JTable column URLs */
        private final ArrayList<String> columnURLs = new ArrayList<String>();
        /** JTable column tool-tips */
        private final ArrayList<String> columnDescriptions = new ArrayList<String>();
        /** JTable column units */
        private final ArrayList<String> columnUnits = new ArrayList<String>();
        /** Column data types */
        private final ArrayList<Class<?>> columnClasses = new ArrayList<Class<?>>();

        /**
         * Hidden constructor
         *
         * @param file file in file mode or null in remote mode
         * @param calModel calibrators model
         * @param parser Savot pull parser initialized
         * @param savotVoTable Savot VOTable partially loaded
         * @param trFirst first SavotTR tr
         * @param startTime start time (nano seconds)
         */
        private ParseVoTableSwingWorker(final File file, final CalibratorsModel calModel,
                                        final SavotPullParser parser, final SavotVOTable savotVoTable, final SavotTR trFirst,
                                        final long startTime) {
            // get current observation version :
            super(SearchCalTaskRegistry.TASK_LOAD);
            this.file = file;
            this.calModel = calModel;
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

            final long start = System.nanoTime();

            final SavotResource resource = savotVoTable.getResources().getItemAt(0);
            final SavotTable table = resource.getTables().getItemAt(0);
            final GroupSet groupSet = table.getGroups();
            final FieldSet fieldSet = table.getFields();

            // Retrieve VOTable parameters
            paramSet = table.getParams();

            final int nGroups = groupSet.getItemCount();
            /*
             * For each group of the table, put its name and its index in a
             * hashtable, and put its name in a vector for JTable columns title
             * definition.
             */
            final Map<String, Integer> groupNameToGroupId = new HashMap<String, Integer>(nGroups);

            // reset JTable column meta data:
            columnNames.ensureCapacity(nGroups);
            columnURLs.ensureCapacity(nGroups);
            columnDescriptions.ensureCapacity(nGroups);
            columnUnits.ensureCapacity(nGroups);
            columnClasses.ensureCapacity(nGroups);

            String groupName;
            SavotField field;
            String url;
            String fieldDataType;
            Class<?> columnClass;

            for (int groupId = 0; groupId < nGroups; groupId++) {
                groupName = groupSet.getItemAt(groupId).getName();

                columnNames.add(groupName);
                // Associate the group name with its index as a table column
                groupNameToGroupId.put(groupName, Integer.valueOf(groupId));

                // Get back the field type
                field = fieldSet.getItemAt(3 * groupId); // x3 as there is 3 fields per group

                // Get back the field link
                url = "";

                if (field.getLinks().getItemCount() != 0) {
                    url = field.getLinks().getItemAt(0).getHref();
                }

                columnURLs.add(url);

                // Get back the field description
                columnDescriptions.add(field.getDescription());

                // Get back the field description
                columnUnits.add(field.getUnit());

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

                    columnClasses.add(columnClass);
                } else {
                    throw new IllegalArgumentException("Invalid VOTable - empty fieldType for " + field.getName());
                }
            }

            // Create a new star list and set the group name to group id conversion table to the star list
            final StarList starList = new StarList(groupNameToGroupId);

            // origin and confidence index are enumerations: use shared instance cache to use less memory:
            final Map<String, String> originValues = new HashMap<String, String>(32);
            final Map<String, String> confidenceValues = new HashMap<String, String>(16);

            final int tableRows = table.getNrowsValue(); // optional

            // reserve space:
            if (tableRows != 0) {
                starList.ensureCapacity(tableRows);
            }

            // local copies for performance:
            final Class<?>[] colClasses = new Class<?>[nGroups];
            columnClasses.toArray(colClasses);

            SavotTR tr = trFirst; // consume first TR
            TDSet row;
            List<StarProperty> star;
            Object propertyValue;
            int groupId, mainGroupCellId;
            String value, origin, originValue, confidence, confidenceValue;
            StarProperty starProperty;

            // only use Progress bar for large tables (JSDC):
            final int step = (tableRows > 5000) ? tableRows / 100 : Integer.MAX_VALUE;
            int nRow = 0;
            do {

                if (nRow % step == 0 && nRow != 0) {
                    // fast interrupt :
                    if (currentThread.isInterrupted()) {
                        return null;
                    }
                    /*                    
                     if (_logger.isLoggable(Level.INFO)) {
                     _logger.info("CalibratorsModel.parseVOTable: processing " + nRow + " row / total = " + tableRows);
                     }
                     */

                    // progress bar:
                    calModel._queryModel.setQueryProgress("loading ...", nRow, tableRows);
                }

                // Get the data corresponding to the current row
                row = tr.getTDSet();

                // For each group of the row create a star property of each 3 cells
                // note: Vector type is required by swing DefaultTableModel:
                star = new Vector<StarProperty>(nGroups);

                for (groupId = 0; groupId < nGroups; ++groupId) {

                    /*
                     * The index of first cell of the current group (as there is
                     * always 3 cells for each group).
                     */
                    mainGroupCellId = 3 * groupId;

                    propertyValue = null;

                    // Get the value (always the first group cell)
                    value = row.getContent(mainGroupCellId);

                    // replace "" by null to use less memory:
                    if (value.length() != 0) {
                        // Get column field type:
                        columnClass = colClasses[groupId];

                        if (columnClass == Double.class) {
                            try {
                                propertyValue = Double.valueOf(value);
                            } catch (NumberFormatException nfe) {
                                _logger.warning("invalid Double [" + value + "]");
                            }
                        } else if (columnClass == String.class) {
                            propertyValue = value;
                        } else if (columnClass == Boolean.class) {
                            try {
                                propertyValue = Boolean.valueOf(value);
                            } catch (NumberFormatException nfe) {
                                _logger.warning("invalid Boolean [" + value + "]");
                            }
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
                        originValue = originValues.get(origin);
                        if (originValue == null) {
                            originValues.put(origin, origin);
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
                        confidenceValue = confidenceValues.get(confidence);
                        if (confidenceValue == null) {
                            confidenceValues.put(confidence, confidence);
                        } else {
                            // use shared instance
                            confidence = confidenceValue;
                        }
                    }

                    // _logger.severe("property: " + propertyValue + " - " + origin + "- " + confidence);
                    /*
                     * Create a new StarProperty instance from the retrieved value,
                     * origin and confidence.
                     */
                    starProperty = new StarProperty(propertyValue, origin, confidence);

                    // Add the newly created star property to the star property list
                    star.add(starProperty);
                }

                // Store each VOTable row as a list of star properties
                starList.add(star);

                nRow++;

            } while ((tr = parser.getNextTR()) != null);

            _logger.info("CalibratorsModel.parseVOTable: " + nRow + " rows read in " + 1e-6d * (System.nanoTime() - start) + " ms.");

            return starList;
        }

        /**
         * Refresh the GUI using the given star list
         * This code is executed by the Swing Event Dispatcher thread (EDT)
         * @param starList parsed StarList
         */
        @Override
        public void refreshUI(final StarList starList) {

            calModel._queryModel.setQueryProgress("", 0, 0);

            // Keep only VOTable structure (without data):
            calModel._parsedVOTable = savotVoTable;

            calModel._columnNames.clear();
            calModel._columnURLs.clear();
            calModel._columnDescriptions.clear();
            calModel._columnUnits.clear();
            calModel._columnClasses.clear();
            calModel._columnNames.addAll(columnNames);
            calModel._columnURLs.addAll(columnURLs);
            calModel._columnDescriptions.addAll(columnDescriptions);
            calModel._columnUnits.addAll(columnUnits);
            calModel._columnClasses.addAll(columnClasses);

            calModel._nColumns = columnNames.size();

            // Compute selected magnitude band and scenario
            // TODO: put in Savot Param finder by name:
            final int nParams = paramSet.getItemCount();
            final HashMap<String, String> parameters = new HashMap<String, String>(nParams);

            for (int i = 0; i < nParams; i++) {
                final SavotParam param = paramSet.getItemAt(i);
                final String paramName = param.getName();
                final String paramValue = param.getValue();

                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine(paramName + " = '" + paramValue + "'");
                }
                parameters.put(paramName, paramValue);
            }

            calModel._magnitudeBand = parameters.get("band");

            if ((calModel._magnitudeBand != null) && (calModel._magnitudeBand.matches("I")
                    || calModel._magnitudeBand.matches("J") || calModel._magnitudeBand.matches("H"))) {
                calModel._magnitudeBand = "K";
            }

            calModel._brightScenarioFlag = Boolean.valueOf(parameters.get("bright"));

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("magnitude band = '" + calModel._magnitudeBand + "'; bright scenario = '" + calModel._brightScenarioFlag + "'.");
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

            calModel._paramSet = paramSet;

            // Copy the star list and add the group name to group id conversion table to the star list
            calModel._originalStarList = starList;

            // remove non calibrator stars once:
            calModel.removeNonCalibrators();

            // fire Update:
            calModel.removeDeletedStars();

            _logger.info("CalibratorsModel.parseVOTable done: " + 1e-6d * (System.nanoTime() - startTime) + " ms.");

            if (file != null) {
                // Loading the file in the query model
                StatusBar.show("loading file (parsing query)...");
                try {
                    calModel._queryModel.loadParamSet(calModel.getParamSet());
                    StatusBar.show("loading file (query successfully parsed)...");
                    StatusBar.show("file succesfully loaded.");

                } catch (NumberFormatException nfe) {
                    StatusBar.show("loading aborted (query parsing error) !");
                    MessagePane.showErrorMessage("Could not open file (query parsing error).", nfe);
                    // TODO reset table content ?
                }
            }

            // Enabling the 'Save' menus
            calModel._vo.enableDataRelatedMenus(true);
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
        final List<StarProperty> star = getStar(row);
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
    public List<StarProperty> getStar(final int row) {
        final List<StarProperty> star = (row >= 0 && row < _filteredStarList.size()) ? _filteredStarList.get(row) : null;

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
            return _columnNames.get(groupId);
        }
        return "";
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

        if (file.isFile()) {
            if (!file.exists()) {
                throw new FileNotFoundException("File '" + file.getAbsolutePath() + "'does not exist.");
            }
            parseVOTable(file);
        }
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
            selectedStarsList = new StarList(_filteredStarList.getFieldIdToColNumberMap());
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
     * @return true if successfull
     */
    public boolean saveVOTableFile(final File file, final StarList starList) {
        _logger.entering("CalibratorsModel", "saveVOTableFile");

        final int size = starList.size();

        // If null received
        if (size <= 0) {
            _logger.warning("Could not process 'null' StarList");
            return false;
        }

        // If no Votable already loaded to be used as a template for columns descriptions
        if (_parsedVOTable == null) {
            _logger.warning("Could not generate metadata template");
            return false;
        }

        final String filename = file.getAbsolutePath();

        _logger.info("Saving the starlist as votable into file: " + filename);

        boolean ok = false;

        final long start = System.nanoTime();
        try {
            final SavotVOTable votable = _parsedVOTable;

            // shared empty TD instance
            final SavotTD emptyTD = new SavotTD();

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

                    SavotTD[] cacheTD = null;
                    final SavotTR tr = new SavotTR();
                    TDSet tdSet = null;
                    SavotTD valueTd;
                    SavotTD originTd;
                    SavotTD confidenceTd;

                    int nProps, c;

                    // And create one row per star entry
                    for (List<StarProperty> star : starList) {

                        tr.clear(); // recycle TR instance (also TDSet)

                        if (cacheTD == null) {
                            nProps = 3 * star.size();

                            tdSet = tr.getTDSet(nProps);

                            cacheTD = new SavotTD[nProps];

                            for (c = 0; c < nProps; c++) {
                                cacheTD[c] = new SavotTD();
                            }
                        }

                        c = 0;

                        for (StarProperty prop : star) {

                            // value:
                            if (prop.hasValue()) {
                                valueTd = cacheTD[c];
                                // convert double / boolean to string:
                                valueTd.setContent(prop.getStringValue());
                            } else {
                                valueTd = emptyTD;
                            }
                            tdSet.addItem(valueTd);

                            // origin:
                            if (prop.hasOrigin()) {
                                originTd = cacheTD[c + 1];
                                originTd.setContent(prop.getOrigin());
                            } else {
                                originTd = emptyTD;
                            }
                            tdSet.addItem(originTd);

                            // confidence index:
                            if (prop.hasConfidence()) {
                                confidenceTd = cacheTD[c + 2];
                                confidenceTd.setContent(prop.getConfidence());
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
            _logger.log(Level.SEVERE, "Unable to save the starlist as votable into file: " + filename, ioe);
        } finally {
            if (ok) {
                _logger.info("CalibratorsModel.saveVOTableFile: file saved in " + 1e-6d * (System.nanoTime() - start) + " ms.");
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
        _logger.entering("CalibratorsModel", "hasSomeDeletedStars");

        return _calibratorStarList.hasSomeDeletedStars();
    }

    /**
     * Delete the shown elements according to the given row indexes. 
     * The shown elements are the not filtered elements.
     *
     * @param indices indices of the row to be removed.
     */
    public void deleteStars(final int[] indices) {
        _logger.entering("CalibratorsModel", "deleteStars");

        // Remove each selected row
        final List<List<StarProperty>> stars = new ArrayList<List<StarProperty>>(indices.length);

        for (int idx : indices) {
            stars.add(_filteredStarList.get(idx));
        }

        // flag stars
        _originalStarList.markAsDeleted(stars);

        // fire Update:
        removeDeletedStars();
    }

    /**
     * Remove all non calibrator stars.
     */
    private void removeNonCalibrators() {
        _logger.entering("CalibratorsModel", "removeNonCalibrators");

        // filter non calibrator stars:
        _calibratorStarList = _facelessNonCalibratorsFilter.process(_originalStarList);
    }

    /**
     * Remove all stars previously flagged as deleted.
     */
    private void removeDeletedStars() {
        _logger.entering("CalibratorsModel", "removeDeletedStars");

        // Remove all the stars flagged as deleted
        _currentStarList = _calibratorStarList.removeAllDeletedStars();

        update(null, null);
    }

    /**
     * Undelete all stars previously flagged as deleted.
     */
    public void undeleteStars() {
        _logger.entering("CalibratorsModel", "undeleteStars");

        // Unflag all previously flagged stars
        _originalStarList.undeleteAll();

        // fire Update:
        removeDeletedStars();
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

        if ((_selectedStarIndices != null) && (_selectedStarIndices.length != 0)) {
            // When some calibrators selected
            actionMenuText = "Send Selected Calibrators to...";
        }

        // Update Menu label
        _vo.setShareCalibratorsThroughSAMPActionText(actionMenuText);
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
    private void applyXSLTranformationOnCurrentVOTable(final File outputFile, final String xslFileName) {
        _logger.entering("CalibratorsModel", "applyXSLTranformationOnCurrentVOTable");

        final String xsltFile = SearchCal.getSharedInstance().getPathFromResourceFilename(xslFileName);

        if (xsltFile == null) {
            _logger.severe("Could not load XSL file '" + xslFileName + "'.");

            return;
        }

        try {
            // Prepare the input and output files
            final File tmpFile = FileUtils.getTempFile("currentStarList.scvot");

            if (saveVOTableFile(tmpFile, _currentStarList)) {
                final InputStream sourceStream = new BufferedInputStream(new FileInputStream(tmpFile));
                final OutputStream resultStream = new BufferedOutputStream(new FileOutputStream(outputFile));

                // Apply the xsl file to the source file and write the result to
                // the output file
                // use an XSLT to transform the XML document to an HTML representation :
                XmlFactory.transform(sourceStream, xsltFile, resultStream);
            }

        } catch (FileNotFoundException fnfe) {
            _logger.log(Level.SEVERE, "File not found", fnfe);
        } catch (IllegalArgumentException iae) {
            // An error occurred in the XSL file
            _logger.log(Level.SEVERE, "An error occured during XSLT transformation '" + xsltFile + "'", iae);

            if (iae.getCause() instanceof TransformerException) {
                // An error occurred while applying the XSL file
                // Get location of error in input file
                final SourceLocator locator = ((TransformerException) iae.getCause()).getLocator();

                _logger.log(Level.SEVERE, "One error occured while applying XSL file '" + xsltFile
                        + "', on line '" + locator.getLineNumber() + "' and column '" + locator.getColumnNumber() + "'");
            }
        } catch (IllegalStateException ise) {
            // An error occurred in the XSL file
            _logger.log(Level.SEVERE, "An error occured in the XSL file '" + xsltFile + "'", ise);
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
            _logger.entering("RowHeadersModel", "populate");

            // Empty all the current row headers
            dataVector.clear();

            // Generate as many row headers as the given number of data rows
            for (int i = 0; i < nbOfRows; i++) {
                addRow(new Object[]{i + 1});
            }
        }
    }
}
/*___oOo___*/
