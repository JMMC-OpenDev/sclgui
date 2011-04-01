/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.54 2011-04-01 14:50:07 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.53  2011/02/15 15:36:32  lafrasse
 * Fixed a querying crash when science object magnitude is missing.
 *
 * Revision 1.52  2011/02/11 16:02:02  lafrasse
 * Fucking NullPointerException...
 *
 * Revision 1.51  2011/02/10 14:20:00  lafrasse
 * Restricted faint scenario magnitude bands to K.
 *
 * Revision 1.50  2011/01/13 14:13:47  lafrasse
 * Restricted faint scenario magnitude bands to I, J, H and K.
 * Fixed documentation typos.
 *
 * Revision 1.49  2010/10/11 13:53:01  lafrasse
 * Streamlined exception handling.
 * Cleaned imports.
 *
 * Revision 1.48  2010/10/10 22:45:03  lafrasse
 * Code reformating.
 *
 * Revision 1.47  2010/10/10 22:21:04  lafrasse
 * Fixed first round of NetBeans-detected warnings.
 *
 * Revision 1.46  2010/09/10 14:12:20  lafrasse
 * Jalopization.
 *
 * Revision 1.45  2010/09/10 14:11:42  lafrasse
 * Fixed Min. and Max. Magnitude textfields to automatically follow Science Object
 * Magnitude changes.
 *
 * Revision 1.44  2009/10/23 12:55:15  lafrasse
 * Replaced custom SIMBAD resolution system with common JMMC one.
 * Added white spaces as a valid separator for science object coordinates input.
 *
 * Revision 1.43  2009/04/30 09:20:32  lafrasse
 * Jalopization.
 *
 * Revision 1.42  2009/04/23 13:10:09  mella
 * add better debug message
 *
 * Revision 1.41  2008/10/16 12:56:11  lafrasse
 * Added limitations to RA and DEC ranges.
 *
 * Revision 1.40  2008/09/17 21:46:53  lafrasse
 * Added proper unit conversion for DiffRA and DiffDEC in saveDefaultValues().
 *
 * Revision 1.39  2008/09/10 22:34:48  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 *
 * Revision 1.38  2008/05/30 12:47:50  lafrasse
 * Changed QueryDiffRA & QueryDiffDEC APIs to more precisely handle unit conversion
 * between minutes/degrees and arcmin.
 *
 * Revision 1.37  2008/05/20 15:36:46  lafrasse
 * Changed default radius flag value from false to true.
 * Added handling of hypothetical negative radius values.
 *
 * Revision 1.36  2008/04/15 15:59:33  lafrasse
 * Changed RA unit to minutes and DEC unit to degrees.
 * Corrected auto radius checkbox behavior to be selected only when the associateds
 * textfield is enabled.
 * Corrected auto radius status loading to match preference state.
 * Changed default query preference to match the ASPRO default settings.
 *
 * Revision 1.35  2007/12/04 11:00:50  lafrasse
 * Corrected a bug during file loading that was preventing query parsing.
 *
 * Revision 1.34  2007/12/03 14:43:35  lafrasse
 * Added the possibility to explicitly ask for an automatically calculated radius
 * for a faint query.
 *
 * Revision 1.33  2007/10/04 15:04:00  lafrasse
 * Added bright/faint radio button disabled when 'V' magnitude band is selected.
 *
 * Revision 1.32  2007/10/04 10:54:53  lafrasse
 * Added 'N' magnitude band support.
 *
 * Revision 1.31  2007/09/18 11:28:33  lafrasse
 * Handle undefined magnitudde values returned by Simbad CDS.
 *
 * Revision 1.30  2007/08/09 07:54:33  lafrasse
 * Reverted automatic min and max magnitude computation.
 *
 * Revision 1.29  2007/08/03 13:21:32  lafrasse
 * Corrected a typo.
 *
 * Revision 1.28  2007/08/03 13:11:41  lafrasse
 * Enhanced wavelengthes et and science object magnitudes management according to
 * comments from Daniel BONNEAU.
 *
 * Revision 1.27  2007/08/02 12:19:57  lafrasse
 * Corrected delayed GUI updated when changing bright/faint scenarion radio buttons
 * as noted in Denis MOURARD review comments.
 *
 * Revision 1.26  2007/08/01 15:29:22  lafrasse
 * Added support for SIMDAD science object querying through URL+script (plus proof
 * of disfunctionning through SOAP).
 *
 * Revision 1.25  2007/06/29 09:56:50  lafrasse
 * Added support for science object inclusion.
 *
 * Revision 1.24  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.23  2007/04/11 13:51:47  lafrasse
 * Corrected a bug that prevented automatic update of wavelength on magnitude band changes.
 *
 * Revision 1.22  2007/02/16 17:17:18  lafrasse
 * Added support for true catalog namei queried.
 *
 * Revision 1.21  2007/02/16 15:20:54  lafrasse
 * Enhanced min & max magnitude interdependance.
 *
 * Revision 1.20  2007/02/13 16:17:58  lafrasse
 * Jalopyzation.
 *
 * Revision 1.19  2007/02/13 16:16:12  lafrasse
 * Enabled JMMC SearchCal SOAP webservice querying.
 *
 * Revision 1.18  2007/02/13 15:34:39  lafrasse
 * Jalopyzation.
 *
 * Revision 1.17  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.16  2006/11/29 22:44:20  lafrasse
 * Added support for the whole query panel disabling on values loaded from file.
 *
 * Revision 1.15  2006/11/27 16:58:37  lafrasse
 * Updated magnitude bands order and corresponding wavelength values.
 *
 * Revision 1.14  2006/11/27 15:49:23  lafrasse
 * Update preferences string tokens.
 *
 * Revision 1.13  2006/11/23 16:24:41  lafrasse
 * Added query parameters parsing and loading from VOTable files.
 *
 * Revision 1.12  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.11  2006/10/04 11:34:31  lafrasse
 * Added support for preferenced science object detection distance.
 *
 * Revision 1.10  2006/10/03 15:31:19  lafrasse
 * Added support for preferenced min and max magnitude auto-update deltas.
 *
 * Revision 1.9  2006/09/28 15:23:29  lafrasse
 * Updated to handle jmmc.util.Preferences API modifications.
 *
 * Revision 1.8  2006/09/15 14:20:54  lafrasse
 * Added query default values support.
 *
 * Revision 1.7  2006/08/22 14:50:51  mella
 * Complete API for setter functions to accept Double or double params
 *
 * Revision 1.6  2006/08/10 15:22:55  lafrasse
 * Fullfiled missing members and accessors
 * Used Double as default numeric type
 * Added full implementation of by-default auto-updated values
 * Streamlined magnitude bands and default wavelengthes support
 *
 * Revision 1.5  2006/07/18 13:16:10  lafrasse
 * Changed example values to mirror those of ETA_TAU in the current SearchCal
 * version
 *
 * Revision 1.4  2006/07/12 14:30:58  lafrasse
 * Code refinment
 * Added the status bar
 *
 * Revision 1.3  2006/07/03 13:34:23  lafrasse
 * Added maximum & minimum calibrators magnitude properties
 *
 * Revision 1.2  2006/06/30 07:59:44  lafrasse
 * Added progress status handling and initialitzation and example values
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import cds.savot.model.FieldSet;
import cds.savot.model.ParamSet;
import cds.savot.model.ResourceSet;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.model.TRSet;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;

import fr.jmmc.mcs.astro.ALX;
import fr.jmmc.mcs.astro.star.Star;
import fr.jmmc.mcs.util.PreferencesException;

import java.io.StringBufferInputStream;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;

/**
 * Query model.
 */
public class QueryModel extends Star implements Observer {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.QueryModel");
    /** For default values */
    private Preferences _preferences = null;
    /** Magnitude to Preselected Wavelength conversion table */
    private HashMap<String, Double> _magnitudeBandToWavelength = null;
    /**
     * All handled magnitude bands.
     *
     * @warning Do not change the order, as it is used to parse SIMBAD VOTables.
     */
    private static final String[] ALL_MAGNITUDE_BANDS = {"V", "I", "J", "H", "K", "N"};
    /**
     * Default magnitude band wavelengthes in micrometer.
     *
     * @warning Do not change the order,as it is linked to ALL_MAGNITUDE_BANDS.
     * @sa ALL_MAGNITUDE_BANDS
     */
    private double[] _defaultWavelengths = {0.55, 0.9, 1.25, 1.65, 2.2, 10};
    /** The instrumental magnitude band */
    private DefaultComboBoxModel _instrumentalMagnitudeBands = new DefaultComboBoxModel();
    /** Available magnitude band for BRIGHT scenario */
    private static final String[] BRIGHT_MAGNITUDE_BANDS = {"V", "I", "J", "H", "K", "N"};
    /** Available magnitude band for FAINT scenario */
    private static final String[] FAINT_MAGNITUDE_BANDS = {"K"};
    /** The instrumental maximum base line */
    private double _instrumentalMaxBaseLine;
    /** The science object name */
    private String _scienceObjectName;
    /** The science object detection distance */
    private boolean _scienceObjectDetectionDistance;
    /** The query minimum magnitude */
    private double _queryMinMagnitude;
    /** The query minimum magnitude auto-update flag */
    private boolean _queryMinMagnitudeAutoUpdate = true;
    /** The query maximum magnitude */
    private double _queryMaxMagnitude;
    /** The query maximum magnitude auto-update flag */
    private boolean _queryMaxMagnitudeAutoUpdate = true;
    /** The query bright scenario flag
     * Contains true if BRIGHT scenario is selected, false if FAINT.
     */
    private boolean _queryBrightScenarioFlag;
    /** The query diff. RA */
    private double _queryDiffRASize;
    /** The query diff. DEC */
    private double _queryDiffDECSize;
    /** The query radius */
    private double _queryRadialSize;
    /** The query radius automatic computation flag */
    private boolean _queryAutoRadiusFlag;
    /** The current step of the querying progress.
     * 0 < _currentStep < _totalStep
     */
    private int _currentStep;
    /** The total number of step of the querying progress.
     * _totalStep > 0
     */
    private int _totalStep;
    /** The current catalog name of the querying progress step. */
    private String _catalogName;
    /** Remind whether the query can be edited or not (when loaded from file for example) */
    private boolean _isEditable;

    /**
     * Default constructor.
     */
    public QueryModel() throws IllegalStateException, IllegalArgumentException {
        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        resetInstrumentalWavelengthes();
        resetScienceObjectMagnitudes();

        // Initialize values from user defined preferences
        try {
            // can throw IllegalArgumentException if parsing RA / DEC failed
            loadDefaultValues();
        } catch (PreferencesException pe) {
            // TODO : how to handle that : reset preferences ... ??
            throw new IllegalStateException("Incompatible Preferences found", pe);
        }
    }

    /**
     * Called when observed objects change.
     *
     * @param o the observed object that changed
     * @param arg not used
     */
    public void update(Observable o, Object arg) {
        _logger.entering("QueryModel", "update");

        // Called if the observe shared instance Preference object was updated.
        // Then inform any object that observe us that we also probably changed.
        setChanged();
        notifyObservers();
    }

    /**
     * Reset all properties.
     */
    public void reset() {
        _logger.entering("QueryModel", "reset");

        // Done first as the available magnitude band sets are not the same for Bright and Faint scenarii
        setQueryBrightScenarioFlag(true);

        setInstrumentalMagnitudeBand("K");
        setInstrumentalMaxBaseLine(0.0);
        resetInstrumentalWavelengthes();

        setScienceObjectName("");
        setScienceObjectRA("+00:00:00.00");
        setScienceObjectDEC("+00:00:00.00");
        resetScienceObjectMagnitudes();

        setQueryDiffRASizeInMinutes(0.0);
        setQueryDiffDECSizeInDegrees(0.0);
        setQueryAutoRadiusFlag(true);
        setQueryRadialSize(0.0);

        restoreMinMaxMagnitudeFieldsAutoUpdating();

        setCurrentStep(0);
        setTotalStep(0);
        setCatalogName("");

        // Enable the edition as the values where not loaded from file
        setEditableState(true);

        notifyObservers();
    }

    /**
     * Reset all properties to their default values.
     */
    public void loadDefaultValues() throws PreferencesException, IllegalArgumentException {
        _logger.entering("QueryModel", "loadDefaultValues");

        // Done first as the available magnitude band sets are not the same for Bright and Faint scenarii
        setQueryBrightScenarioFlag(_preferences.getPreferenceAsBoolean(
                "query.queryBrightScenarioFlag"));

        setInstrumentalMagnitudeBand(_preferences.getPreference(
                "query.magnitudeBand"));

        setInstrumentalMaxBaseLine(_preferences.getPreferenceAsDouble(
                "query.instrumentalMaxBaseLine"));

        setScienceObjectName(_preferences.getPreference(
                "query.scienceObjectName"));
        setScienceObjectRA(_preferences.getPreference(
                "query.scienceObjectRA"));
        setScienceObjectDEC(_preferences.getPreference(
                "query.scienceObjectDEC"));
        setScienceObjectMagnitude(_preferences.getPreferenceAsDouble(
                "query.scienceObjectMagnitude"));

        setQueryMinMagnitude(_preferences.getPreferenceAsDouble(
                "query.queryMinMagnitude"));
        setQueryMinMagnitudeDelta(_preferences.getPreferenceAsDouble(
                "query.queryMinMagnitudeDelta"));
        setQueryMaxMagnitude(_preferences.getPreferenceAsDouble(
                "query.queryMaxMagnitude"));
        setQueryMaxMagnitudeDelta(_preferences.getPreferenceAsDouble(
                "query.queryMaxMagnitudeDelta"));

        setQueryDiffRASizeInMinutes(ALX.arcmin2minutes(
                _preferences.getPreferenceAsDouble("query.queryDiffRASize")));
        setQueryDiffDECSizeInDegrees(ALX.arcmin2degrees(
                _preferences.getPreferenceAsDouble("query.queryDiffDECSize")));
        setQueryRadialSize(_preferences.getPreferenceAsDouble(
                "query.queryRadialSize"));
        setQueryAutoRadiusFlag(_preferences.getPreferenceAsBoolean(
                "query.queryAutoRadius"));

        // Enable the edition as the values where not loaded from file
        setEditableState(true);

        notifyObservers();
    }

    /**
     * Set all properties from the given SAVOT ParamSet.
     *
     * @param paramSet the part of the VOTable containing our query parameters.
     */
    public void loadParamSet(ParamSet paramSet) {
        _logger.entering("QueryModel", "loadParamSet");

        // Convert the ParamSet in an HashTable on parameters name -> value
        HashMap<String, String> parameters = new HashMap<String, String>();

        for (int i = 0; i < paramSet.getItemCount(); i++) {
            SavotParam param = (SavotParam) paramSet.getItemAt(i);
            String paramName = param.getName();
            String paramValue = param.getValue();

            _logger.fine(paramName + " = '" + paramValue + "'");
            parameters.put(paramName, paramValue);
        }

        // Set the query members from the ParamSet values
        // Done first as the available magnitude band sets are not the same for Bright and Faint scenarii
        setQueryBrightScenarioFlag(Boolean.valueOf(parameters.get("bright")));
        setInstrumentalMagnitudeBand(parameters.get("band"));
        setInstrumentalWavelength(Double.valueOf(parameters.get("wlen")));
        setInstrumentalMaxBaseLine(Double.valueOf(parameters.get("baseMax")));
        setScienceObjectName(parameters.get("objectName"));
        setScienceObjectRA(parameters.get("ra"));
        setScienceObjectDEC(parameters.get("dec"));
        setScienceObjectMagnitude(Double.valueOf(parameters.get("mag")));
        setQueryMinMagnitude(Double.valueOf(parameters.get("minMagRange")));
        setQueryMaxMagnitude(Double.valueOf(parameters.get("maxMagRange")));
        setQueryDiffRASizeInMinutes(ALX.arcmin2minutes(Double.valueOf(parameters.get("diffRa"))));
        setQueryDiffDECSizeInDegrees(ALX.arcmin2degrees(Double.valueOf(parameters.get("diffDec"))));
        setQueryAutoRadiusFlag(true);

        String radius = parameters.get("radius");
        Double radiusValue = Double.NaN;

        // If radius exists
        if (radius.length() != 0) {
            radiusValue = Double.valueOf(radius);
            setQueryRadialSize(radiusValue);

            // If radius is not irrevelant
            if (radiusValue > 0.0) {
                setQueryAutoRadiusFlag(false);
            }
        }

        // Disable the edition as the values where loaded from file
        setEditableState(false);

        notifyObservers();
    }

    /**
     * Reset all properties to their default values.
     */
    public void loadFromSimbadVOTable(String simbadVOTable)
            throws Exception {
        _logger.entering("QueryModel", "loadFromSimbadVOTable");

        try {
            // Parse using SAVOT
            // Put the whole VOTable file into memory
            SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(
                    simbadVOTable), SavotPullEngine.FULL, "UTF-8");

            // Parse the VOTable
            SavotVOTable parsedVOTable = parser.getVOTable();

            // Get the VOTable resources
            ResourceSet resourceSet = parsedVOTable.getResources();

            // Get the first table of the first resource
            // WARNING : this is not compatible with other VOTable than JMMC ones
            // (0 should not be used, but the name of the Resource instead)
            SavotResource resource = (SavotResource) resourceSet.getItemAt(0);
            SavotTable table = (SavotTable) resource.getTables().getItemAt(0);
            FieldSet fieldSet = table.getFields();

            // For first data row (if any)
            TRSet rows = resource.getTRSet(0);

            for (int rowId = 0; rowId < Math.min(1, rows.getItemCount());
                    rowId++) {
                // Get the data corresponding to the current row
                TDSet row = rows.getTDSet(rowId);

                // @TODO : highly unportable as it is heavily dependant on the order of
                //         the desired SIMBAD VOTable defined in
                //         VirtualObservatory::GetStarAction::GetStarThread::simbadResult()
                int index = 0;
                String str;
                str = row.getContent(index++);

                if (str.length() != 0) {
                    str = str.replace(' ', ':');
                    _logger.fine("loaded RA = '" + str + "'.");
                    setScienceObjectRA(str);
                }

                str = row.getContent(index++);

                if (str.length() != 0) {
                    str = str.replace(' ', ':');
                    _logger.fine("loaded DEC = '" + str + "'.");
                    setScienceObjectDEC(str);
                }

                // For each "magnitude-band" couple
                for (int i = 0; i < ALL_MAGNITUDE_BANDS.length; i++) {
                    String currentMagnitudeBand = ALL_MAGNITUDE_BANDS[i];
                    Double loadedValue = Double.NaN;

                    try {
                        str = row.getContent(index++);

                        if (str.length() != 0) {
                            _logger.fine("loaded '" + currentMagnitudeBand + "' Mag = '" + str + "'.");
                            loadedValue = new Double(str);
                        } else {
                            _logger.fine("loaded '" + currentMagnitudeBand + "' Mag = 'NaN'.");
                        }
                    } catch (Exception exc) {
                        _logger.fine("blanked '" + currentMagnitudeBand + "' Mag = 'NaN'.");
                    }

                    setPropertyAsDouble(Property.fromString("FLUX_" + currentMagnitudeBand), loadedValue);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        restoreMinMaxMagnitudeFieldsAutoUpdating();

        // Enable the edition as the values where not loaded from file
        setEditableState(true);
        notifyObservers();
    }

    /**
     * Store all current properties as the futur default values.
     */
    public void saveDefaultValues() throws Exception {
        _logger.entering("QueryModel", "saveDefaultValues");

        try {
            _preferences.setPreference("query.magnitudeBand", getInstrumentalMagnitudeBand());
            _preferences.setPreference("query.instrumentalWavelength", getInstrumentalWavelength());
            _preferences.setPreference("query.instrumentalMaxBaseLine", getInstrumentalMaxBaseLine());

            _preferences.setPreference("query.scienceObjectName", getScienceObjectName());
            _preferences.setPreference("query.scienceObjectRA", getScienceObjectRA());
            _preferences.setPreference("query.scienceObjectDEC", getScienceObjectDEC());
            _preferences.setPreference("query.scienceObjectMagnitude", getScienceObjectMagnitude());

            _preferences.setPreference("query.queryMinMagnitude", getQueryMinMagnitude());
            _preferences.setPreference("query.queryMaxMagnitude", getQueryMaxMagnitude());
            _preferences.setPreference("query.queryBrightScenarioFlag", getQueryBrightScenarioFlag());
            _preferences.setPreference("query.queryDiffRASize", ALX.minutes2arcmin(getQueryDiffRASizeInMinutes()));
            _preferences.setPreference("query.queryDiffDECSize", ALX.degrees2arcmin(getQueryDiffDECSizeInDegrees()));
            _preferences.setPreference("query.queryRadialSize", getQueryRadialSize());
            _preferences.setPreference("query.queryAutoRadius", getQueryAutoRadiusFlag());

            _preferences.saveToFile();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Return all current properties as an MCS-like sclsvr-formatted query.
     *
     * @return the query as a string.
     */
    public String getQueryAsMCSString() {
        _logger.entering("QueryModel", "getQueryAsMCSString");

        final StringBuilder query = new StringBuilder(255);

        // Object name
        query.append("-objectName ").append(getScienceObjectName()).append(" ");

        // Magnitude
        query.append("-mag ").append(getScienceObjectMagnitude()).append(" ");

        // Diff RA
        double arcminRA = ALX.minutes2arcmin(getQueryDiffRASizeInMinutes());
        query.append("-diffRa ").append(arcminRA).append(" ");

        // Diff DEC
        double arcminDEC = ALX.degrees2arcmin(getQueryDiffDECSizeInDegrees());
        query.append("-diffDec ").append(arcminDEC).append(" ");

        // Band
        query.append("-band ").append(getInstrumentalMagnitudeBand()).append(" ");

        // Min Magnitude
        query.append("-minMagRange ").append(getQueryMinMagnitude()).append(" ");

        // Max Magnitude
        query.append("-maxMagRange ").append(getQueryMaxMagnitude()).append(" ");

        // RA
        query.append("-ra ").append(getScienceObjectRA()).append(" ");

        // DEC
        query.append("-dec ").append(getScienceObjectDEC()).append(" ");

        // Max Baseline
        query.append("-baseMax ").append(getInstrumentalMaxBaseLine()).append(" ");

        // Wavelength
        query.append("-wlen ").append(getInstrumentalWavelength()).append(" ");

        // Radius
        final Boolean brightFlag = getQueryBrightScenarioFlag();

        if (!brightFlag.booleanValue()) {
            Double queryRadialSize = getQueryRadialSize();
            Boolean autoRadiusFlag = getQueryAutoRadiusFlag();

            if (autoRadiusFlag.booleanValue()) {
                queryRadialSize = 0.0d;
            }

            if (queryRadialSize == Double.NaN) {
                queryRadialSize = 0.0d;
            }

            query.append("-radius ").append(queryRadialSize).append(" ");
        }

        // Bright/Faint flag
        query.append("-bright ").append(brightFlag).append(" ");

        // Get the science star
        query.append("-noScienceStar false");

        _logger.fine("query = '" + query + "'.");

        return query.toString();
    }

    /**
     * Return all the instrumental magnitude bands.
     *
     * @return the instrumental magnitude bands.
     */
    public DefaultComboBoxModel getInstrumentalMagnitudeBands() {
        _logger.entering("QueryModel", "getInstrumentalMagnitudeBands");

        return _instrumentalMagnitudeBands;
    }

    /**
     * Return the instrumental magnitude band for the actual query.
     *
     * @return the instrumental magnitude band as a String object.
     */
    public String getInstrumentalMagnitudeBand() {
        _logger.entering("QueryModel", "getInstrumentalMagnitudeBand");

        return (String) _instrumentalMagnitudeBands.getSelectedItem();
    }

    /**
     * Change the instrumental magnitude band for the actual query.
     * Update the instrumental wavelength if needed.
     *
     * @param scienceObjectName the new instrumental magnitude band.
     */
    public void setInstrumentalMagnitudeBand(String magnitudeBand) {
        _logger.entering("QueryModel", "setInstrumentalMagnitudeBand");

        _instrumentalMagnitudeBands.setSelectedItem(magnitudeBand);

        setChanged();
    }

    /**
     * Reset the instrumental wavelength parameters.
     *
     * @param wavelength the new instrumental wavelength as a double value.
     */
    public void resetInstrumentalWavelengthes() {
        _logger.entering("QueryModel", "resetInstrumentalWavelengthes");

        _magnitudeBandToWavelength = new HashMap<String, Double>();

        // For each "magnitude band-predefined wavelength" couple
        for (int i = 0; i < ALL_MAGNITUDE_BANDS.length; i++) {
            // Construct the conversion table between both
            _magnitudeBandToWavelength.put(ALL_MAGNITUDE_BANDS[i], _defaultWavelengths[i]);
        }

        setChanged();
    }

    /**
     * Return the instrumental wavelength for the actual query.
     *
     * @return the instrumental wavelength as a Double value.
     */
    public Double getInstrumentalWavelength() {
        _logger.entering("QueryModel", "getInstrumentalWavelength");

        String currentMagnitudeBand = getInstrumentalMagnitudeBand();

        return _magnitudeBandToWavelength.get(currentMagnitudeBand);
    }

    /**
     * Change the instrumental wavelength parameter.
     * Calling this method once disable the instrumental wavelength auto-update.
     *
     * @param wavelength the new instrumental wavelength as a double value.
     */
    public void setInstrumentalWavelength(double wavelength) {
        setInstrumentalWavelength(new Double(wavelength));
    }

    /**
     * Change the instrumental wavelength parameter.
     * Calling this method once disable the instrumental wavelength auto-update.
     *
     * @param wavelength the new instrumental wavelength as a Double value.
     */
    public void setInstrumentalWavelength(Double wavelength) {
        _logger.entering("QueryModel", "setInstrumentalWavelength");

        String currentMagnitudeBand = getInstrumentalMagnitudeBand();
        _magnitudeBandToWavelength.put(currentMagnitudeBand, wavelength);

        setChanged();
    }

    /**
     * Return the instrumental maximum base line for the actual query.
     *
     * @return the instrumental maximum base line as a Double value.
     */
    public Double getInstrumentalMaxBaseLine() {
        _logger.entering("QueryModel", "getInstrumentalMaxBaseLine");

        return new Double(_instrumentalMaxBaseLine);
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param wavelength the new instrumental maximum base line as a double.
     */
    public void setInstrumentalMaxBaseLine(double maxBaseLine) {
        _logger.entering("QueryModel", "setInstrumentalMaxBaseLine");

        _instrumentalMaxBaseLine = maxBaseLine;

        setChanged();
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param wavelength the new instrumental maximum base line as a Double.
     */
    public void setInstrumentalMaxBaseLine(Double maxBaseLine) {
        setInstrumentalMaxBaseLine(maxBaseLine.doubleValue());
    }

    /**
     * Return the name of the science object for the actual query.
     *
     * @return the star name.
     */
    public String getScienceObjectName() {
        _logger.entering("QueryModel", "getScienceObjectName");

        return _scienceObjectName;
    }

    /**
     * Change the name of the science object for the actual query.
     *
     * @param scienceObjectName the new star name.
     */
    public void setScienceObjectName(String scienceObjectName) {
        _logger.entering("QueryModel", "setScienceObjectName");

        _scienceObjectName = scienceObjectName;

        setChanged();
    }

    /**
     * Return the right ascension for the actual query.
     *
     * @return the right ascension.
     */
    public String getScienceObjectRA() {
        _logger.entering("QueryModel", "getScienceObjectRA");

        return getPropertyAsString(Property.RA);
    }

    /**
     * Change the right ascension parameter for the actual query.
     *
     * @param ra the right ascension.
     */
    public void setScienceObjectRA(String rightAscension)
            throws IllegalArgumentException {
        _logger.entering("QueryModel", "setScienceObjectRA");

        // Check if the given parameter is not empty
        if (rightAscension.length() < 1) {
            throw new IllegalArgumentException("given RA is empty");
        }

        // Validate the format of the given value
        if (rightAscension.matches("[+|-]?[0-9]+[: ][0-9]+[: ][0-9]+.?[0-9]*") == false) {
            throw new IllegalArgumentException("wrong RA format: '"
                    + rightAscension + "' must be of form +30:00:00.00");
        }

        setPropertyAsString(Property.RA, rightAscension);
    }

    /**
     * Return the declinaison  for the actual query.
     *
     * @return the declinaison.
     */
    public String getScienceObjectDEC() {
        _logger.entering("QueryModel", "getScienceObjectDEC");

        return getPropertyAsString(Property.DEC);
    }

    /**
     * Change the declinaison parameter for the actual query.
     *
     * @param dec the declinaison.
     */
    public void setScienceObjectDEC(String declinaison)
            throws IllegalArgumentException {
        _logger.entering("QueryModel", "setScienceObjectDEC");

        // Check if given parameter is not empty
        if (declinaison.length() < 1) {
            throw new IllegalArgumentException("given DEC is empty");
        }

        // Validate the format of the given value
        if (declinaison.matches("[+|-]?[0-9]+[: ][0-9]+[: ][0-9]+.?[0-9]*") == false) {
            throw new IllegalArgumentException("wrong DEC format: '"
                    + declinaison + "' must be of form +30:00:00.00");
        }

        setPropertyAsString(Property.DEC, declinaison);
    }

    /**
     * Reset the science object magnitudes.
     *
     * @param wavelength the new instrumental wavelength as a double value.
     */
    public void resetScienceObjectMagnitudes() {
        _logger.entering("QueryModel", "resetScienceObjectMagnitudes");

        // For each "magnitude-band" couple
        for (int i = 0; i < ALL_MAGNITUDE_BANDS.length; i++) {
            // Construct the conversion table between both
            setPropertyAsDouble(Property.fromString("FLUX_"
                    + ALL_MAGNITUDE_BANDS[i]), Double.NaN);
        }
    }

    /**
     * Return the science object magnitude for the current band.
     *
     * @return the science object magnitude as a Double value.
     */
    public Double getScienceObjectMagnitude() {
        _logger.entering("QueryModel", "getScienceObjectMagnitude");

        String currentBand = (String) _instrumentalMagnitudeBands.getSelectedItem();

        Double value = getPropertyAsDouble(Property.fromString("FLUX_" + currentBand));
        if (value == null) {
            value = Double.NaN;
        }
        return value;
    }

    /**
     * Change the magnitude parameter for the current band.
     *
     * @param magnitude the new magnitude as a double value.
     */
    public void setScienceObjectMagnitude(Double magnitude) {
        _logger.entering("QueryModel", "setScienceObjectMagnitude");

        String currentBand = (String) _instrumentalMagnitudeBands.getSelectedItem();
        setPropertyAsDouble(Property.fromString("FLUX_" + currentBand),
                magnitude);
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a Double value.
     */
    public void setScienceObjectMagnitude(double magnitude) {
        setScienceObjectMagnitude(new Double(magnitude));
    }

    /**
     * Return the tolerated distance to detect the science object.
     *
     * @return the tolerated distance as a Double value.
     */
    public Double getScienceObjectDetectionDistance() {
        _logger.entering("QueryModel", "getScienceObjectDetectionDistance");

        return _preferences.getPreferenceAsDouble(
                "query.scienceObjectDetectionDistance");
    }

    /**
     * Change the tolerated distance to detect the science object.
     *
     * @param distance the new tolerated distance as a double value.
     */
    public void setScienceObjectDetectionDistance(double distance)
            throws Exception {
        _logger.entering("QueryModel", "setScienceObjectDetectionDistance");

        try {
            _preferences.setPreference("query.scienceObjectDetectionDistance",
                    new Double(distance));
        } catch (Exception e) {
            throw e;
        }

        setChanged();
    }

    /**
     * Restore min & max magnitude fields auto-updating.
     */
    public void restoreMinMaxMagnitudeFieldsAutoUpdating() {
        _logger.entering("QueryModel",
                "restoreMinMaxMagnitudeFieldsAutoUpdating");

        _queryMinMagnitudeAutoUpdate = true;
        _queryMaxMagnitudeAutoUpdate = true;
    }

    /**
     * Return the minimun calibrator magnitude for the actual query.
     *
     * @return the minimum magnitude as a Double value.
     */
    public Double getQueryMinMagnitude() {
        _logger.entering("QueryModel", "getQueryMinMagnitude");

        if (_queryMinMagnitudeAutoUpdate == true) {
            Double scienceObjectMagnitude = getScienceObjectMagnitude();

            // If science object magnitude is known
            if (Double.isNaN(scienceObjectMagnitude) == false) {
                // Compute min. magnitude accordinaly
                return scienceObjectMagnitude
                        + getQueryMinMagnitudeDelta();
            }
        }

        // Otherwise use default value in preference
        return new Double(_queryMinMagnitude);
    }

    /**
     * Change the minimum calibrator magnitude parameter.
     * Calling this method once disable the minimum calibrator magnitude
     * auto-update.
     *
     * @param minMagnitude the new minimum magnitude as a double value.
     */
    public void setQueryMinMagnitude(double minMagnitude) {
        _logger.entering("QueryModel", "setQueryMinMagnitude");

        // This value should never be auto-updated anymore
        _queryMinMagnitudeAutoUpdate = false;

        _queryMinMagnitude = minMagnitude;
        setChanged();

        // If the given min value is greater than the max magnitude
        if (_queryMinMagnitude > _queryMaxMagnitude) {
            // Update max magnitude accordinaly
            _queryMaxMagnitude = _queryMinMagnitude;
            notifyObservers();
        }
    }

    /**
     * Change the minimum calibrator magnitude parameter.
     * Calling this method once disable the minimum calibrator magnitude
     * auto-update.
     *
     * @param minMagnitude the new minimum magnitude as a Double value.
     */
    public void setQueryMinMagnitude(Double minMagnitude) {
        setQueryMinMagnitude(minMagnitude.doubleValue());
    }

    /**
     * Return the auto-update delta for the minimun calibrator magnitude.
     *
     * @return the minimum magnitude delta as a Double value.
     */
    public Double getQueryMinMagnitudeDelta() {
        _logger.entering("QueryModel", "getQueryMinMagnitudeDelta");

        return _preferences.getPreferenceAsDouble(
                "query.queryMinMagnitudeDelta");
    }

    /**
     * Change the auto-update delta for the minimum calibrator magnitude.
     *
     * @param delta the new delta as a double value.
     */
    public void setQueryMinMagnitudeDelta(double delta)
            throws PreferencesException {
        _logger.entering("QueryModel", "setQueryMinMagnitudeDelta");

        _preferences.setPreference("query.queryMinMagnitudeDelta",
                new Double(delta));

        setChanged();
    }

    /**
     * Return the maximum calibrator magnitude for the actual query.
     *
     * @return the maximum magnitude as a Double value.
     */
    public Double getQueryMaxMagnitude() {
        _logger.entering("QueryModel", "getQueryMaxMagnitude");

        if (_queryMaxMagnitudeAutoUpdate == true) {
            Double scienceObjectMagnitude = getScienceObjectMagnitude();

            // If science object magnitude is known
            if (Double.isNaN(scienceObjectMagnitude) == false) {
                // Compute max. magnitude accordinaly
                return scienceObjectMagnitude
                        + getQueryMaxMagnitudeDelta();
            }
        }

        // Otherwise use default value in preference
        return new Double(_queryMaxMagnitude);
    }

    /**
     * Change the maximum calibrator magnitude parameter.
     * Calling this method once disable the maximum calibrator magnitude
     * auto-update.
     *
     * @param maxMagnitude the new maximum magnitude as a double value.
     */
    public void setQueryMaxMagnitude(double maxMagnitude) {
        _logger.entering("QueryModel", "setQueryMaxMagnitude");

        // This value should never be auto-updated anymore
        _queryMaxMagnitudeAutoUpdate = false;

        _queryMaxMagnitude = maxMagnitude;
        setChanged();

        // If the given max value is lower than to the min magnitude
        if (_queryMaxMagnitude < _queryMinMagnitude) {
            // Update min magnitude accordinaly
            _queryMinMagnitude = _queryMaxMagnitude;
            notifyObservers();
        }
    }

    /**
     * Change the maximum calibrator magnitude parameter.
     * Calling this method once disable the maximum calibrator magnitude
     * auto-update.
     *
     * @param maxMagnitude the new maximum magnitude as a Double value.
     */
    public void setQueryMaxMagnitude(Double maxMagnitude) {
        setQueryMaxMagnitude(maxMagnitude.doubleValue());
    }

    /**
     * Return the auto-update delta for the maximun calibrator magnitude.
     *
     * @return the maximum magnitude delta as a Double value.
     */
    public Double getQueryMaxMagnitudeDelta() {
        _logger.entering("QueryModel", "getQueryMaxMagnitudeDelta");

        return _preferences.getPreferenceAsDouble(
                "query.queryMaxMagnitudeDelta");
    }

    /**
     * Change the auto-update delta for the maximun calibrator magnitude.
     *
     * @param delta the new delta as a double value.
     */
    public void setQueryMaxMagnitudeDelta(double delta)
            throws PreferencesException {
        _logger.entering("QueryModel", "setQueryMaxMagnitudeDelta");

        _preferences.setPreference("query.queryMaxMagnitudeDelta",
                new Double(delta));

        setChanged();
    }

    /**
     * Indicates whether the query is using the bright scenario or the faint one.
     *
     * @return true whether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public Boolean getQueryBrightScenarioFlag() {
        _logger.entering("QueryModel", "getQueryBrightScenarioFlag");

        return _queryBrightScenarioFlag;
    }

    /**
     * Set whether the query should use the bright scenario or the faint one.
     *
     * @param flag true for bright queries, false for faint ones.
     */
    public void setQueryBrightScenarioFlag(boolean flag) {
        _logger.entering("QueryModel", "setQueryBrightScenarioFlag");

        _queryBrightScenarioFlag = flag;

        // Use the right magnitude band set for the selected scenario
        Object selectedItem = _instrumentalMagnitudeBands.getSelectedItem();
        if (_queryBrightScenarioFlag == true) {
            _instrumentalMagnitudeBands = new DefaultComboBoxModel(BRIGHT_MAGNITUDE_BANDS);
        } else {
            _instrumentalMagnitudeBands = new DefaultComboBoxModel(FAINT_MAGNITUDE_BANDS);
        }
        _instrumentalMagnitudeBands.setSelectedItem(selectedItem);

        setChanged();
    }

    /**
     * Return the query box differential RA size.
     *
     * @return the query box differential RA size.
     */
    public Double getQueryDiffRASizeInMinutes() {
        _logger.entering("QueryModel", "getQueryDiffRASizeInMinutes");

        return new Double(_queryDiffRASize);
    }

    /**
     * Change the query box differential RA size.
     *
     * @warning negative values will be made positive.
     * @warning values greater than 240 will be replaced by 240.
     *
     * @param diffRASize the new query box differential RA size as a double.
     */
    public void setQueryDiffRASizeInMinutes(double diffRASize) {
        _logger.entering("QueryModel", "setQueryDiffRASizeInMinutes");

        // The value shall not be negative, nor greater than 240.
        _queryDiffRASize = Math.min(240, Math.abs(diffRASize));

        setChanged();
    }

    /**
     * Change the query box differential RA size.
     *
     * @param diffRASize the new query box differential RA size as a Double.
     */
    public void setQueryDiffRASizeInMinutes(Double diffRASize) {
        setQueryDiffRASizeInMinutes(diffRASize.doubleValue());
    }

    /**
     * Return the query box differential DEC size.
     *
     * @return the query box differential DEC size.
     */
    public Double getQueryDiffDECSizeInDegrees() {
        _logger.entering("QueryModel", "getQueryDiffDECSizeInDegrees");

        return new Double(_queryDiffDECSize);
    }

    /**
     * Change the query box differential DEC size.
     *
     * @warning negative values will be made positive.
     * @warning values greater than 30 will be replaced by 30.
     *
     * @param radiusSize the new query box differential DEC size as a double.
     */
    public void setQueryDiffDECSizeInDegrees(double diffDECSize) {
        _logger.entering("QueryModel", "setQueryDiffDECSizeInDegrees");

        // The value shall not be negative, nor greater than 30.
        _queryDiffDECSize = Math.min(30, Math.abs(diffDECSize));

        setChanged();
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param radiusSize the new query box differential DEC size as a Double.
     */
    public void setQueryDiffDECSizeInDegrees(Double diffDECSize) {
        setQueryDiffDECSizeInDegrees(diffDECSize.doubleValue());
    }

    /**
     * Return the query box radial size.
     *
     * @return the query box radial size.
     */
    public Double getQueryRadialSize() {
        _logger.entering("QueryModel", "getQueryRadialSize");

        return new Double(_queryRadialSize);
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a double.
     */
    public void setQueryRadialSize(double radiusSize) {
        _logger.entering("QueryModel", "setQueryRadialSize");

        _queryRadialSize = Math.abs(radiusSize);

        setChanged();
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a Double.
     */
    public void setQueryRadialSize(Double radiusSize) {
        if (radiusSize.isNaN() == false) {
            setQueryRadialSize(radiusSize.doubleValue());
        }
    }

    /**
     * Indicates whether the query should use automatic radius computation or not.
     *
     * @return true whether the radius will be automatically computed, false otherwise.
     */
    public Boolean getQueryAutoRadiusFlag() {
        _logger.entering("QueryModel", "getQueryAutoRadiusFlag");

        return _queryAutoRadiusFlag;
    }

    /**
     * Set whether the query should use automatic radius computation or not.
     *
     * @param flag true for the radius to be automatically computed, false otherwise.
     */
    public void setQueryAutoRadiusFlag(boolean flag) {
        _logger.entering("QueryModel", "setQueryAutoRadiusFlag");

        _queryAutoRadiusFlag = flag;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the current step of query progress.
     *
     * @return the current querying step.
     */
    public int getCurrentStep() {
        _logger.entering("QueryModel", "getCurrentStep");

        return _currentStep;
    }

    /**
     * Set the current step of query progress.
     *
     * @param the current querying step.
     */
    public void setCurrentStep(int currentStep) {
        _logger.entering("QueryModel", "setCurrentStep");

        if (currentStep <= _totalStep) {
            _currentStep = currentStep;
        } else {
            _currentStep = 0;
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Return the total number of step of query.
     *
     * @return the total number of querying step.
     */
    public int getTotalStep() {
        _logger.entering("QueryModel", "getTotalStep");

        return _totalStep;
    }

    /**
     * Set the total number of step of query.
     *
     * @param the total number of querying step.
     */
    public void setTotalStep(int totalStep) {
        _logger.entering("QueryModel", "setTotalStep");

        _totalStep = totalStep;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the current catalog name.
     *
     * @return the current catalog name.
     */
    public String getCatalogName() {
        _logger.entering("QueryModel", "getCatalogName");

        if (_currentStep == 0) {
            return " ";
        }

        return _catalogName;
    }

    /**
     * Set the current catalog name of query.
     *
     * @param the catalog name of querying step.
     */
    public void setCatalogName(String catalogName) {
        _logger.entering("QueryModel", "setCatalogName");

        _catalogName = catalogName;

        setChanged();
        notifyObservers();
    }

    /**
     * Indicates to listener if this query is well filled and can be consumed.
     * If it returns false, then one reason must be given TBD...
     */
    public boolean isConsumable() {
        _logger.entering("QueryModel", "isConsumable");

        // @TODO : Verify any mandatory missing parameter

        // If the RA coordinate is not defined
        if (getPropertyAsString(Property.RA).length() < 1) {
            return false;
        }

        // If the DEC coordinate is not defined
        if (getPropertyAsString(Property.DEC).length() < 1) {
            return false;
        }

        // The magnitude is not tested as any Double value is valid
        return true;
    }

    /**
     * Return whether the query can be edited or not.
     *
     * @return true if the query can be edited, false otherwise.
     */
    public boolean canBeEdited() {
        _logger.entering("QueryModel", "canBeEdited");

        return _isEditable;
    }

    /**
     * Set whether the query can be edited or not.
     *
     * @param state true if the query can be edited, false otherwise.
     */
    public void setEditableState(boolean state) {
        _logger.entering("QueryModel", "setEditableState");

        _isEditable = state;

        setChanged();
    }
}
/*___oOo___*/
