/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.query;

import cds.savot.model.ParamSet;
import cds.savot.model.SavotParam;
import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.star.Star;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query model.
 */
public final class QueryModel extends Star implements Observer {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(QueryModel.class.getName());
    /**
     * All handled magnitude bands.
     *
     * @warning Do not change the order, as it is used to parse SIMBAD VOTables.
     */
    private static final String[] ALL_MAGNITUDE_BANDS = {"V", "J", "H", "K", "N"};
    /**
     * Default magnitude band wavelengthes in micrometer.
     *
     * @warning Do not change the order,as it is linked to ALL_MAGNITUDE_BANDS.
     * @sa ALL_MAGNITUDE_BANDS
     */
    private static final Double[] _defaultWavelengths = {0.55d, 1.25d, 1.65d, 2.2d, 10d};
    /** Available magnitude band for BRIGHT scenario */
    private static final String[] BRIGHT_MAGNITUDE_BANDS = {"V", "J", "H", "K", "N"};
    /** Available magnitude band for FAINT scenario */
    private static final String[] FAINT_MAGNITUDE_BANDS = {"K"};

    /**
     * Enumeration of all different observers notification a star can raise.
     */
    public enum Notification {

        /** model updated */
        MODEL,
        /** query progress */
        PROGRESS;
    };
    /** For default values */
    private Preferences _preferences = null;
    /** Magnitude to Preselected Wavelength conversion table */
    private HashMap<String, Double> _magnitudeBandToWavelength = null;
    /** The instrumental magnitude band */
    private DefaultComboBoxModel _instrumentalMagnitudeBands = new DefaultComboBoxModel();
    /** The instrumental maximum base line */
    private double _instrumentalMaxBaseLine;
    /** The query minimum magnitude */
    private double _queryMinMagnitude;
    /** The query minimum magnitude auto-update flag */
    private boolean _queryMinMagnitudeAutoUpdate = true;
    /** The query maximum magnitude */
    private double _queryMaxMagnitude;
    /** The query maximum magnitude auto-update flag */
    private boolean _queryMaxMagnitudeAutoUpdate = true;
    /** The query bright scenario flag: contains true if BRIGHT scenario is selected, false if FAINT.*/
    private boolean _queryBrightScenarioFlag;
    /** The query diff. RA */
    private double _queryDiffRASize;
    /** The query diff. DEC */
    private double _queryDiffDECSize;
    /** The query radius */
    private double _queryRadialSize;
    /** The query radius automatic computation flag */
    private boolean _queryAutoRadiusFlag;
    /** The current step of the querying progress. 0 < _currentStep < _totalStep */
    private int _currentStep;
    /** The total number of step of the querying progress. _totalStep > 0 */
    private int _totalStep;
    /** The current catalog name of the querying progress step. */
    private String _catalogName;
    /** Remind whether the query can be edited or not (when loaded from file for example) */
    private boolean _isEditable;
    /** flag to enable / disable observer notifications */
    private boolean _notify = true;

    /**
     * Default constructor.
     * @throws IllegalStateException if a PreferencesException was thrown
     * @throws IllegalArgumentException if parsing RA / DEC failed
     */
    public QueryModel() throws IllegalStateException, IllegalArgumentException {
        _preferences = Preferences.getInstance();

        resetInstrumentalWavelengthes();
        resetScienceObjectMagnitudes();

        // Initialize values from user defined preferences
        try {
            // Can throw IllegalArgumentException if parsing RA / DEC failed
            loadDefaultValues();
        } catch (Exception pe) {
            _logger.warn("Could not load user default values : ", pe);

            Object[] options = {"Use Factory Settings", "Quit"};
            String message = "An error occured while loading default preferences value from file:\n"
                    + "    '" + Preferences.getInstance().computePreferenceFilepath() + "'\n\n"
                    + "You could either quit and delete it manually, or try to use factory settings instead.";

            final int result = JOptionPane.showOptionDialog(App.getFrame(), message, null, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            // Handle user choice
            switch (result) {

                case 1: // Quit
                    SwingUtils.invokeLaterEDT(new Runnable() {
                        public void run() {
                            App.quit();
                        }
                    });
                    break;

                case 0: // Use Factory Settings
                case JOptionPane.CLOSED_OPTION: // 'esc' key
                default: // Any other case
                    // Try again with internal default values that should always work
                    Preferences.getInstance().resetToDefaultPreferences();
                    try {
                        loadDefaultValues();
                    } catch (PreferencesException pe2) {
                        _logger.error("Could not load factory default values : ", pe2);
                        throw new IllegalStateException("Incompatible Factory Preferences found", pe2);
                    }
                    break;
            }
        }
    }

    public void init() {
        _preferences.addObserver(this);
    }

    /**
     * @return flag to enable / disable observer notifications
     */
    private boolean isNotify() {
        return _notify;
    }

    /**
     * Enable / disable observer notifications. If enabled, notify observers
     * @param notify true to enable notifications (immediately)
     */
    private void setNotify(final boolean notify) {
        setNotify(notify, Notification.MODEL);
    }

    /**
     * Enable / disable observer notifications. If enabled, notify observers
     * @param notify true to enable notifications (immediately)
     * @param notification notification type
     */
    private void setNotify(final boolean notify, final Notification notification) {
        this._notify = notify;
        if (notify) {
            notifyObservers(notification);
        }
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * 
     * This overriden implementation provides the Notification.MODEL argument
     */
    @Override
    public void notifyObservers() {
        notifyObservers(Notification.MODEL);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * @param notification notification type
     * 
     * This overriden implementation provides the given notification argument
     */
    @Override
    public void notifyObservers(final Object notification) {
        if (this._notify) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("notifyObservers: {}", notification, new Throwable());
            }
            super.notifyObservers(notification);
        }
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param   observer   an observer to be added.
     * @throws NullPointerException   if the parameter o is null.
     */
    @Override
    public void addObserver(final Observer observer) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("addObserver: {}", observer);
        }
        super.addObserver(observer);
    }

    /**
     * Called when observed objects change.
     *
     * @param o the observed object that changed
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        // Called if the observe shared instance Preference object was updated.
        // Then inform any object that observe us that we also probably changed.
        setChanged();
        notifyObservers();
    }

    /**
     * Reset all properties.
     */
    public void reset() {
        final boolean prevNotify = isNotify();
        try {
            // avoid multiple observer notifications:
            setNotify(false);

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

            setQueryProgress("", 0, 0);

            // Enable the edition as the values where not loaded from file
            setEditableState(true);

        } finally {
            // do notifyObservers():
            setNotify(prevNotify);
        }
    }

    /**
     * Reset all properties to their default values.
     * @throws PreferencesException if preference error occurs
     * @throws IllegalArgumentException  if default values have wrong values
     */
    public final void loadDefaultValues() throws PreferencesException, IllegalArgumentException {
        final boolean prevNotify = isNotify();
        try {
            // avoid multiple observer notifications:
            setNotify(false);

            // Done first as the available magnitude band sets are not the same for Bright and Faint scenarii
            setQueryBrightScenarioFlag(_preferences.getPreferenceAsBoolean(PreferenceKey.QUERY_BRIGHT_FLAG));

            setInstrumentalMagnitudeBand(_preferences.getPreference(PreferenceKey.QUERY_MAGNITUDE_BAND));

            setInstrumentalMaxBaseLine(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_INSTRUMENTAL_BASELINE));

            setScienceObjectName(_preferences.getPreference(PreferenceKey.QUERY_SCIENCE_NAME));
            setScienceObjectRA(_preferences.getPreference(PreferenceKey.QUERY_SCIENCE_RA));
            setScienceObjectDEC(_preferences.getPreference(PreferenceKey.QUERY_SCIENCE_DEC));
            setScienceObjectMagnitude(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_SCIENCE_MAGNITUDE));

            setQueryMinMagnitude(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_MINIMUM_MAGNITUDE));
            setQueryMinMagnitudeDelta(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_MINIMUM_DELTA));
            setQueryMaxMagnitude(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_MAXIMUM_MAGNITUDE));
            setQueryMaxMagnitudeDelta(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_MAXIMUM_DELTA));

            setQueryDiffRASizeInMinutes(ALX.arcmin2minutes(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_DIFF_RA)));
            setQueryDiffDECSizeInDegrees(ALX.arcmin2degrees(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_DIFF_DEC)));
            setQueryRadialSize(_preferences.getPreferenceAsDouble(PreferenceKey.QUERY_RADIAL_SIZE));
            setQueryAutoRadiusFlag(_preferences.getPreferenceAsBoolean(PreferenceKey.QUERY_RADIAL_FLAG));

            restoreMinMaxMagnitudeFieldsAutoUpdating();

            // Enable the edition as the values where not loaded from file
            setEditableState(true);

        } finally {
            // do notifyObservers():
            setNotify(prevNotify);
        }
    }

    /**
     * Set all properties from the given SAVOT ParamSet.
     *
     * @param paramSet the part of the VOTable containing our query parameters.
     * @throws NumberFormatException  if the string does not contain a
     * parsable number.
     */
    public void loadParamSet(ParamSet paramSet) throws NumberFormatException {
        final int nParams = paramSet.getItemCount();
        // Convert the ParamSet in an HashTable on parameters name -> value
        // TODO: put in Savot Param finder by name:
        final Map<String, String> parameters = new HashMap<String, String>(nParams);

        String paramValue;

        for (int i = 0; i < nParams; i++) {
            final SavotParam param = paramSet.getItemAt(i);
            final String paramName = param.getName();
            paramValue = param.getValue();

            if (_logger.isDebugEnabled()) {
                _logger.debug("{} = '{}'", paramName, paramValue);
            }
            parameters.put(paramName, paramValue);
        }

        // Set the query members from the ParamSet values

        // optional "bright" parameter (Aspro2):
        paramValue = parameters.get("bright");

        /* scenario bright because it is the only available for any instrumental band */
        boolean bright = true;

        if (paramValue != null) {
            bright = Boolean.valueOf(paramValue).booleanValue();
        }

        final boolean prevNotify = isNotify();
        try {
            // avoid multiple observer notifications:
            setNotify(false);

            // Done first as the available magnitude band sets are not the same for Bright and Faint scenarii
            setQueryBrightScenarioFlag(bright);

            paramValue = parameters.get("band");
            if (paramValue != null) {
                setInstrumentalMagnitudeBand(paramValue);
            }

            paramValue = parameters.get("wlen");
            if (paramValue != null) {
                setInstrumentalWavelength(Double.valueOf(paramValue));
            }

            paramValue = parameters.get("baseMax");
            if (paramValue != null) {
                setInstrumentalMaxBaseLine(Double.valueOf(paramValue));
            }

            paramValue = parameters.get("objectName");
            if (paramValue != null) {
                setScienceObjectName(paramValue);
            }

            paramValue = parameters.get("ra");
            if (paramValue != null) {
                setScienceObjectRA(paramValue);
            }

            paramValue = parameters.get("dec");
            if (paramValue != null) {
                setScienceObjectDEC(paramValue);
            }

            paramValue = parameters.get("mag");
            if (paramValue != null) {
                setScienceObjectMagnitude(Double.valueOf(paramValue));
            }

            // optional "minMagRange" parameter (Aspro2):
            paramValue = parameters.get("minMagRange");
            if (paramValue != null) {
                setQueryMinMagnitude(Double.valueOf(paramValue));
            }

            // optional "maxMagRange" parameter (Aspro2):
            paramValue = parameters.get("maxMagRange");
            if (paramValue != null) {
                setQueryMaxMagnitude(Double.valueOf(paramValue));
            }

            // optional "diffRa" parameter (Aspro2):
            paramValue = parameters.get("diffRa");
            if (paramValue != null) {
                setQueryDiffRASizeInMinutes(ALX.arcmin2minutes(Double.valueOf(paramValue)));
            }

            // optional "diffDec" parameter (Aspro2):
            paramValue = parameters.get("diffDec");
            if (paramValue != null) {
                setQueryDiffDECSizeInDegrees(ALX.arcmin2degrees(Double.valueOf(paramValue)));
            }

            // optional "radius" parameter (Aspro2):
            paramValue = parameters.get("radius");

            if (paramValue != null) {
                setQueryAutoRadiusFlag(true);

                // If radius exists
                if (paramValue.length() != 0) {
                    final Double radiusValue = Double.valueOf(paramValue);
                    setQueryRadialSize(radiusValue);

                    // If radius is not irrevelant
                    if (radiusValue > 0d) {
                        setQueryAutoRadiusFlag(false);
                    }
                }
            }

            // Disable the edition as the values where loaded from file
            setEditableState(false);

        } finally {
            // do notifyObservers():
            setNotify(prevNotify);
        }
    }

    /**
     * Store all current properties as the futur default values.
     * @throws IllegalStateException if preferences exception occurs
     */
    public void saveDefaultValues() throws IllegalStateException {
        try {
            _preferences.setPreference(PreferenceKey.QUERY_MAGNITUDE_BAND, getInstrumentalMagnitudeBand());
            _preferences.setPreference(PreferenceKey.QUERY_INSTRUMENTAL_WAVELENGTH, getInstrumentalWavelength());
            _preferences.setPreference(PreferenceKey.QUERY_INSTRUMENTAL_BASELINE, getInstrumentalMaxBaseLine());

            _preferences.setPreference(PreferenceKey.QUERY_SCIENCE_NAME, getScienceObjectName());
            _preferences.setPreference(PreferenceKey.QUERY_SCIENCE_RA, getScienceObjectRA());
            _preferences.setPreference(PreferenceKey.QUERY_SCIENCE_DEC, getScienceObjectDEC());
            _preferences.setPreference(PreferenceKey.QUERY_SCIENCE_MAGNITUDE, getScienceObjectMagnitude());

            _preferences.setPreference(PreferenceKey.QUERY_MINIMUM_MAGNITUDE, getQueryMinMagnitude());
            _preferences.setPreference(PreferenceKey.QUERY_MAXIMUM_MAGNITUDE, getQueryMaxMagnitude());
            _preferences.setPreference(PreferenceKey.QUERY_BRIGHT_FLAG, getQueryBrightScenarioFlag());
            _preferences.setPreference(PreferenceKey.QUERY_DIFF_RA, ALX.minutes2arcmin(getQueryDiffRASizeInMinutes()));
            _preferences.setPreference(PreferenceKey.QUERY_DIFF_DEC, ALX.degrees2arcmin(getQueryDiffDECSizeInDegrees()));
            _preferences.setPreference(PreferenceKey.QUERY_RADIAL_SIZE, getQueryRadialSize());
            _preferences.setPreference(PreferenceKey.QUERY_RADIAL_FLAG, getQueryAutoRadiusFlag());

            _preferences.saveToFile();
        } catch (PreferencesException pex) {
            throw new IllegalStateException("Can't save default values", pex);
        }
    }

    /**
     * Return all current properties as an MCS-like sclsvr-formatted query.
     *
     * @return the query as a string.
     */
    public String getQueryAsMCSString() {
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

        if (_logger.isDebugEnabled()) {
            _logger.debug("query = '{}'.", query);
        }

        return query.toString();
    }

    /**
     * Return all the instrumental magnitude bands.
     *
     * @return the instrumental magnitude bands.
     */
    public DefaultComboBoxModel getInstrumentalMagnitudeBands() {
        return _instrumentalMagnitudeBands;
    }

    /**
     * Return the instrumental magnitude band for the actual query.
     *
     * @return the instrumental magnitude band as a String object.
     */
    public String getInstrumentalMagnitudeBand() {
        return (String) _instrumentalMagnitudeBands.getSelectedItem();
    }

    /**
     * Change the instrumental magnitude band for the actual query.
     * Update the instrumental wavelength if needed.
     *
     * @param magnitudeBand the new instrumental magnitude band.
     */
    public void setInstrumentalMagnitudeBand(final String magnitudeBand) {
        _instrumentalMagnitudeBands.setSelectedItem(magnitudeBand);

        setChanged();
    }

    /**
     * Reset the instrumental wavelength parameters.
     */
    public final void resetInstrumentalWavelengthes() {
        _magnitudeBandToWavelength = new HashMap<String, Double>(ALL_MAGNITUDE_BANDS.length);

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
        return new Double(_instrumentalMaxBaseLine);
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param maxBaseLine the new instrumental maximum base line as a double.
     */
    public void setInstrumentalMaxBaseLine(double maxBaseLine) {
        _instrumentalMaxBaseLine = maxBaseLine;

        setChanged();
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param maxBaseLine the new instrumental maximum base line as a Double.
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
        return getPropertyAsString(Property.NAME);
    }

    /**
     * Change the name of the science object for the actual query.
     *
     * @param scienceObjectName the new star name.
     */
    public void setScienceObjectName(String scienceObjectName) {
        setPropertyAsString(Property.NAME, scienceObjectName);
    }

    /**
     * Return the right ascension for the actual query.
     *
     * @return the right ascension.
     */
    public String getScienceObjectRA() {
        return getPropertyAsString(Property.RA);
    }

    /**
     * Change the right ascension parameter for the actual query.
     *
     * @param rightAscension the right ascension.
     * @throws IllegalArgumentException if RA text field is invalid
     */
    public void setScienceObjectRA(final String rightAscension) throws IllegalArgumentException {
        final String ra = rightAscension.trim();

        // Check if the given parameter is not empty
        if (ra.length() < 1) {
            throw new IllegalArgumentException("given RA is empty");
        }

        // Validate the format of the given value
        if (!ra.matches("[+|-]?[0-9]+[: ][0-9]+[: ][0-9]+.?[0-9]*")) {
            throw new IllegalArgumentException("wrong RA format: '"
                    + ra + "' must be of form +30:00:00.00");
        }

        setPropertyAsString(Property.RA, ra);
    }

    /**
     * Return the declinaison  for the actual query.
     *
     * @return the declinaison.
     */
    public String getScienceObjectDEC() {
        return getPropertyAsString(Property.DEC);
    }

    /**
     * Change the declinaison parameter for the actual query.
     *
     * @param declinaison the declinaison.
     * @throws IllegalArgumentException if RA text field is invalid
     */
    public void setScienceObjectDEC(final String declinaison) throws IllegalArgumentException {
        final String dec = declinaison.trim();

        // Check if given parameter is not empty
        if (dec.length() < 1) {
            throw new IllegalArgumentException("given DEC is empty");
        }

        // Validate the format of the given value
        if (!dec.matches("[+|-]?[0-9]+[: ][0-9]+[: ][0-9]+.?[0-9]*")) {
            throw new IllegalArgumentException("wrong DEC format: '"
                    + dec + "' must be of form +30:00:00.00");
        }

        setPropertyAsString(Property.DEC, dec);
    }

    /**
     * Reset the science object magnitudes.
     */
    public final void resetScienceObjectMagnitudes() {
        // For each "magnitude-band" couple
        for (int i = 0; i < ALL_MAGNITUDE_BANDS.length; i++) {
            // Construct the conversion table between both
            setPropertyAsDouble(Property.fromString("FLUX_" + ALL_MAGNITUDE_BANDS[i]), Double.NaN);
        }
    }

    /**
     * Return the science object magnitude for the current band.
     *
     * @return the science object magnitude as a Double value.
     */
    public Double getScienceObjectMagnitude() {
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
        String currentBand = (String) _instrumentalMagnitudeBands.getSelectedItem();
        setPropertyAsDouble(Property.fromString("FLUX_" + currentBand), magnitude);
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
        return _preferences.getPreferenceAsDouble(PreferenceKey.QUERY_SCIENCE_DETECTION);
    }

    /**
     * Change the tolerated distance to detect the science object.
     *
     * @param distance the new tolerated distance as a double value.
     * @throws IllegalStateException if preferences exception occurs
     */
    public void setScienceObjectDetectionDistance(double distance) throws IllegalStateException {
        try {
            _preferences.setPreference(PreferenceKey.QUERY_SCIENCE_DETECTION, new Double(distance));
        } catch (PreferencesException pe) {
            throw new IllegalStateException("Can't change the tolerated distance to detect the science object", pe);
        }

        setChanged();
    }

    /**
     * Restore min & max magnitude fields auto-updating.
     */
    public void restoreMinMaxMagnitudeFieldsAutoUpdating() {
        _queryMinMagnitudeAutoUpdate = true;
        _queryMaxMagnitudeAutoUpdate = true;
    }

    /**
     * Return the minimum calibrator magnitude for the actual query.
     *
     * @return the minimum magnitude as a Double value.
     */
    public Double getQueryMinMagnitude() {
        if (_queryMinMagnitudeAutoUpdate) {
            Double scienceObjectMagnitude = getScienceObjectMagnitude();

            // If science object magnitude is known
            if (!Double.isNaN(scienceObjectMagnitude)) {
                // Compute min. magnitude accordinaly
                return scienceObjectMagnitude + getQueryMinMagnitudeDelta();
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
     * Return the auto-update delta for the minimum calibrator magnitude.
     *
     * @return the minimum magnitude delta as a Double value.
     */
    public Double getQueryMinMagnitudeDelta() {
        return _preferences.getPreferenceAsDouble(PreferenceKey.QUERY_MINIMUM_DELTA);
    }

    /**
     * Change the auto-update delta for the minimum calibrator magnitude.
     *
     * @param delta the new delta as a double value.
     * @throws PreferencesException  
     */
    public void setQueryMinMagnitudeDelta(double delta) throws PreferencesException {
        _preferences.setPreference(PreferenceKey.QUERY_MINIMUM_DELTA, new Double(delta));

        setChanged();
    }

    /**
     * Return the maximum calibrator magnitude for the actual query.
     *
     * @return the maximum magnitude as a Double value.
     */
    public Double getQueryMaxMagnitude() {
        if (_queryMaxMagnitudeAutoUpdate) {
            Double scienceObjectMagnitude = getScienceObjectMagnitude();

            // If science object magnitude is known
            if (!Double.isNaN(scienceObjectMagnitude)) {
                // Compute max. magnitude accordinaly
                return scienceObjectMagnitude + getQueryMaxMagnitudeDelta();
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
     * Return the auto-update delta for the maximum calibrator magnitude.
     *
     * @return the maximum magnitude delta as a Double value.
     */
    public Double getQueryMaxMagnitudeDelta() {
        return _preferences.getPreferenceAsDouble(PreferenceKey.QUERY_MAXIMUM_DELTA);
    }

    /**
     * Change the auto-update delta for the maximum calibrator magnitude.
     *
     * @param delta the new delta as a double value.
     * @throws PreferencesException  
     */
    public void setQueryMaxMagnitudeDelta(double delta) throws PreferencesException {
        _preferences.setPreference(PreferenceKey.QUERY_MAXIMUM_DELTA, new Double(delta));

        setChanged();
    }

    /**
     * Indicates whether the query is using the bright scenario or the faint one.
     *
     * @return true whether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public Boolean getQueryBrightScenarioFlag() {
        return _queryBrightScenarioFlag;
    }

    /**
     * Set whether the query should use the bright scenario or the faint one.
     *
     * @param flag true for bright queries, false for faint ones.
     */
    public void setQueryBrightScenarioFlag(boolean flag) {
        _queryBrightScenarioFlag = flag;

        // Use the right magnitude band set for the selected scenario
        Object selectedItem = _instrumentalMagnitudeBands.getSelectedItem();
        if (_queryBrightScenarioFlag) {
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
        return new Double(_queryDiffDECSize);
    }

    /**
     * Change the query box differential DEC size.
     *
     * @warning negative values will be made positive.
     * @warning values greater than 30 will be replaced by 30.
     *
     * @param diffDECSize the new query box differential DEC size as a double.
     */
    public void setQueryDiffDECSizeInDegrees(double diffDECSize) {
        // The value shall not be negative, nor greater than 30.
        _queryDiffDECSize = Math.min(30, Math.abs(diffDECSize));

        setChanged();
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param diffDECSize the new query box differential DEC size as a Double.
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
        return new Double(_queryRadialSize);
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a double.
     */
    public void setQueryRadialSize(double radiusSize) {
        _queryRadialSize = Math.abs(radiusSize);

        setChanged();
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a Double.
     */
    public void setQueryRadialSize(Double radiusSize) {
        if (!radiusSize.isNaN()) {
            setQueryRadialSize(radiusSize.doubleValue());
        }
    }

    /**
     * Indicates whether the query should use automatic radius computation or not.
     *
     * @return true whether the radius will be automatically computed, false otherwise.
     */
    public Boolean getQueryAutoRadiusFlag() {
        return _queryAutoRadiusFlag;
    }

    /**
     * Set whether the query should use automatic radius computation or not.
     *
     * @param flag true for the radius to be automatically computed, false otherwise.
     */
    public void setQueryAutoRadiusFlag(boolean flag) {
        _queryAutoRadiusFlag = flag;

        setChanged();
        notifyObservers();
    }

    /**
     * Define the current catalog name, step and total number of steps
     * @param catalogName the catalog name of querying step.
     * @param currentStep the current querying step.
     * @param totalStep the total number of querying step.
     */
    public void setQueryProgress(final String catalogName, final int currentStep, final int totalStep) {
        try {
            // avoid multiple observer notifications:
            setNotify(false);

            setCatalogName(catalogName);
            setCurrentStep(currentStep);
            setTotalStep(totalStep);

        } finally {
            // do notifyObservers():
            setNotify(true, Notification.PROGRESS);
        }
    }

    /**
     * Return the current step of query progress.
     *
     * @return the current querying step.
     */
    public int getCurrentStep() {
        return _currentStep;
    }

    /**
     * Set the current step of query progress.
     *
     * @param currentStep the current querying step.
     */
    private void setCurrentStep(int currentStep) {
        if (currentStep <= _totalStep) {
            _currentStep = currentStep;
        } else {
            _currentStep = 0;
        }

        setChanged();
    }

    /**
     * Return the total number of step of query.
     *
     * @return the total number of querying step.
     */
    public int getTotalStep() {
        return _totalStep;
    }

    /**
     * Set the total number of step of query.
     *
     * @param totalStep the total number of querying step.
     */
    private void setTotalStep(int totalStep) {
        _totalStep = totalStep;

        setChanged();
    }

    /**
     * Return the current catalog name.
     *
     * @return the current catalog name.
     */
    public String getCatalogName() {
        if (_currentStep == 0) {
            return " ";
        }

        return _catalogName;
    }

    /**
     * Set the current catalog name of query.
     *
     * @param catalogName the catalog name of querying step.
     */
    private void setCatalogName(String catalogName) {
        _catalogName = catalogName;

        setChanged();
    }

    /**
     * Indicates to listener if this query is well filled and can be consumed.
     *
     * @return true if this query is well filled and can be consumed.
     */
    public boolean isConsumable() {
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
        return _isEditable;
    }

    /**
     * Set whether the query can be edited or not.
     *
     * @param state true if the query can be edited, false otherwise.
     */
    public void setEditableState(boolean state) {
        _isEditable = state;

        setChanged();
    }
}
/*___oOo___*/
