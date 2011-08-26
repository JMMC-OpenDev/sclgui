/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmal.ALX;

import fr.jmmc.mcs.util.*;

import java.awt.Color;

import java.util.HashMap;
import java.util.logging.*;

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class Preferences extends fr.jmmc.mcs.util.Preferences {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.Preferences");
    /** Singleton instance */
    private static Preferences _instance = null;
    /** Detailled bright N columns order list, as of default in preference version 3 */
    private static String _detailledBrightN_v3 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 orig F12 e_F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Calib MultFlag VarFlag3 V H plx e_Plx pmRa pmDec A_V Chi2 SpTyp_Teff";
    /** Detailled bright N columns order list, as of default in preference version 4 */
    private static String _detailledBrightN_v4 = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag VarFlag3 V H A_V Chi2 SpTyp_Teff";
    /** Detailled bright N columns order list, as of default in current preference version */
    private static String _detailledBrightN = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag BinFlag V H A_V Chi2 SpTyp_Teff";
    /** Detailled bright V columns order list, as of default in preference version 3 */
    private static String _detailledBrightV_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailled bright V columns order list, as of default in preference version 4 */
    private static String _detailledBrightV_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailled bright V columns order list, as of default in current preference version */
    private static String _detailledBrightV = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailled bright K columns order list, as of default in preference version 3 */
    private static String _detailledBrightK_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailled bright K columns order list, as of default in preference version 4 */
    private static String _detailledBrightK_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailled bright K columns order list, as of default in current preference version */
    private static String _detailledBrightK = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailled faint K columns order list, as of default in preference version 3 */
    private static String _detailledFaintK_v3 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa GLAT GLON plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailled faint K columns order list, as of default in preference version 4 */
    private static String _detailledFaintK_v4 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailled faint K columns order list, as of default in current preference version */
    private static String _detailledFaintK = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";

    /** Hidden constructor */
    protected Preferences() {
        // Insert your singleton initialization code here
    }

    /**
     * Return the preference filename.
     */
    protected String getPreferenceFilename() {
        _logger.entering("Preferences", "getPreferenceFilename");

        return "fr.jmmc.searchcal.properties";
    }

    /**
     * Return the preference revision number.
     */
    protected int getPreferencesVersionNumber() {
        _logger.entering("Preferences", "getPreferencesVersionNumber");

        return 11;
    }

    /**
     * Hook to handle updates of older preference file version.
     *
     * @param loadedVersionNumber the version of the loaded preference file.
     *
     * @return should return true if the update went fine and new values should
     * be saved, false otherwise to automatically trigger default values load.
     */
    @Override
    protected boolean updatePreferencesVersion(int loadedVersionNumber) {
        _logger.entering("Preferences", "updatePreferencesVersion");

        _logger.info("Upgrading preference file from version '"
                + loadedVersionNumber + "' to version '" + (loadedVersionNumber + 1)
                + "'.");

        switch (loadedVersionNumber) {
            // Wrong column identifiers in the the simple and detailled bright N columns order list
            case 1:
                return updateFromVersion1ToVersion2();

            // Added SBC9 column to each detailled view
            case 2:
                return updateFromVersion2ToVersion3();

            // Remove CHARM color preference
            case 3:
                return updateFromVersion3ToVersion4();

            // Re-order detailled view columns
            case 4:
                return updateFromVersion4ToVersion5();

            // Replaced VarFlag3 with VFlag in bright N detailled view
            case 5:
                return updateFromVersion5ToVersion6();

            // Replaced science object detection distance of 0.01 degrees by 1 arcsecond
            case 6:
                return updateFromVersion6ToVersion7();

            // Replace "view.details.show" preference, with "view.result.verbosity.synthetic", "view.result.verbosity.detailled", "view.result.verbosity.full" preferences to add 'Full View' support
            case 7:
                return updateFromVersion7ToVersion8();

            /*
             * Add UD_x diameter columns in all bright views but N;
             * Add WDS column in all detailled views but the Bright N;
             * Reorder pmRa & pmDec in all detailled views but the Bright N;
             * Fix variability flag from VarFlag3 to VFlag in detailled bright N view
             */
            case 8:
                return updateFromVersion8ToVersion9();

            // Insert sep1 and sep2 after WDS for all detailled views but bright N
            case 9:
                return updateFromVersion9ToVersion10();

            // Rename VFlag to BinFlag in detailled bright N
            case 10:
                return updateFromVersion10ToVersion11();

            // By default, triggers default values load.
            default:
                return false;
        }
    }

    /**
     * Set legend colors and order.
     *
     * @param defaultFlag If true sets values to defaults preferences, otherwise in current preferences.
     */
    protected void setLegendColorsAndOrders(boolean defaultFlag)
            throws PreferencesException {
        // Place catalog origin colors
        String catalogColorPrefPrefix = "catalog.color.";
        int i = 0;

        for (Catalog catalog : Catalog.values()) {
            Color catalogColor = Catalog.getDefaultColor(catalog);

            if (defaultFlag == true) {
                setDefaultPreference(catalogColorPrefPrefix
                        + catalog.reference(), i++, catalogColor);
            } else {
                setPreference(catalogColorPrefPrefix + catalog.reference(),
                        i++, catalogColor);
            }
        }

        // Place confidence indexes color
        i = 0;

        if (defaultFlag == true) {
            setDefaultPreference("confidence.color.HIGH", i++, "#ECECEC");
            setDefaultPreference("confidence.color.MEDIUM", i++, "#CCCCCC");
            setDefaultPreference("confidence.color.LOW", i++, "#6F6F6F");
        } else {
            setPreference("confidence.color.HIGH", i++, "#ECECEC");
            setPreference("confidence.color.MEDIUM", i++, "#CCCCCC");
            setPreference("confidence.color.LOW", i++, "#6F6F6F");
        }
    }

    /** Set preferences default values */
    protected void setDefaultPreferences() throws PreferencesException {
        _logger.entering("Preferences", "setDefaultPreferences");

        // Define legend colors and order for default preferences
        setLegendColorsAndOrders(true);

        // Place help behaviour
        setDefaultPreference("help.tooltips.show", "true");

        // Place view behaviour
        setDefaultPreference("view.legend.show", "false");
        setDefaultPreference("view.result.verbosity.synthetic", "true");
        setDefaultPreference("view.result.verbosity.detailled", "false");
        setDefaultPreference("view.result.verbosity.full", "false");

        // Simple 'Bright N' view
        setDefaultPreference("view.columns.simple.bright.N",
                "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu)");
        // Detailled 'Bright N' view
        setDefaultPreference("view.columns.detailled.bright.N",
                _detailledBrightN);

        // Simple 'Bright V' view
        setDefaultPreference("view.columns.simple.bright.V",
                "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_B UD_V UD_R UD_I SpType B V R I");
        // Detailled 'Bright V' view
        setDefaultPreference("view.columns.detailled.bright.V",
                _detailledBrightV);

        // Simple 'Bright K' view
        setDefaultPreference("view.columns.simple.bright.K",
                "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_V UD_J UD_H UD_K SpType V J H K");
        // Detailled 'Bright K' view
        setDefaultPreference("view.columns.detailled.bright.K",
                _detailledBrightK);

        // Simple 'Faint K' view
        setDefaultPreference("view.columns.simple.faint.K",
                "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_mean e_diam_mean V Icous J H K");
        // Detailled 'Faint K' view
        setDefaultPreference("view.columns.detailled.faint.K", _detailledFaintK);

        // Query default values preferences (ETA_TAU, bright V)
        setDefaultPreference("query.magnitudeBand", "V");
        setDefaultPreference("query.instrumentalWavelength",
                Double.toString(1.0d * ALX.MICRON));
        setDefaultPreference("query.instrumentalMaxBaseLine",
                Double.toString(102.45d * ALX.METER));
        setDefaultPreference("query.scienceObjectName", "ETA_TAU");
        setDefaultPreference("query.scienceObjectRA", "03:47:29.0765");
        setDefaultPreference("query.scienceObjectDEC", "24:06:18.494");
        setDefaultPreference("query.scienceObjectMagnitude", "0");
        setDefaultPreference("query.queryMinMagnitude", "2.0");
        setDefaultPreference("query.queryMaxMagnitude", "4.0");
        setDefaultPreference("query.queryBrightScenarioFlag", "true");
        setDefaultPreference("query.queryDiffRASize", Double.toString(3600d * ALX.ARCSEC));
        setDefaultPreference("query.queryDiffDECSize", ""
                + (1200 * ALX.ARCSEC));
        setDefaultPreference("query.queryRadialSize", Double.toString(5.0d * ALX.ARCMIN));
        setDefaultPreference("query.queryAutoRadius", "true");

        setDefaultPreference("query.scienceObjectDetectionDistance",
                Double.toString(1d * ALX.ARCSEC_IN_DEGREES));
        setDefaultPreference("query.queryMinMagnitudeDelta", "-2.0");
        setDefaultPreference("query.queryMaxMagnitudeDelta", "2.0");
    }

    /** Return the singleton instance */
    public static final synchronized Preferences getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new Preferences();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Replace a given string token in a preference by another string.
     *
     * @param preferencePath the path of the preference value to update.
     * @param searchedToken the string token to be replaced in the preference value.
     * @param replacingToken the new string to put in the preference value.
     *
     * @return true if everything went fine, false otherwise.
     */
    private boolean replaceTokenInPreference(String preferencePath,
            String searchedToken, String replacingToken) {
        _logger.entering("Preferences", "replaceTokenInPreference");

        // Get the preference current value
        String originalPreferenceValue = getPreference(preferencePath);
        
        if (_logger.isLoggable(Level.FINEST)) {
            _logger.finest("Preference '" + preferencePath + "' contains : '"
                + originalPreferenceValue + "'.");
        }

        // Search for the token and replace it
        if (_logger.isLoggable(Level.FINER)) {
            _logger.finer("Replacing '" + searchedToken + "' with '"
                + replacingToken + "' in '" + preferencePath + "'.");
        }

        String newPreferenceValue = originalPreferenceValue.replaceAll(searchedToken,
                replacingToken);

        // Store updated preference value
        try {
            setPreference(preferencePath, newPreferenceValue);

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("Preference '" + preferencePath + "' contains : '"
                    + getPreference(preferencePath) + "'.");
            }
        } catch (Exception ex) {
            _logger.log(Level.WARNING,
                    "Could not store '" + preferencePath + "' preference:", ex);

            return false;
        }

        return true;
    }

    /**
     * Correction : wrong column identifiers in the simple and detailled bright N columns order list.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion1ToVersion2() {
        _logger.entering("Preferences", "updateFromVersion1ToVersion2");

        boolean status = true;
        status &= replaceTokenInPreference("view.columns.simple.bright.N",
                "e_diam_vk", "e_dia12 F12");
        status &= replaceTokenInPreference("view.columns.simple.bright.N",
                "diam_vk", "Dia12");
        status &= replaceTokenInPreference("view.columns.detailled.bright.N",
                "vis2Flag", "vis2");

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : added SBC9 column to each detailled view.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion2ToVersion3() {
        _logger.entering("Preferences", "updateFromVersion2ToVersion3");

        boolean status = true;

        // Insert "SBC9" after "MultFlag" in the each detailled columns order list
        String[] viewList = {"bright.V", "bright.K", "faint.K"};

        for (String viewName : viewList) {
            // Get the current columns order list content
            String completePreferencePath = "view.columns.detailled."
                    + viewName;
            status &= replaceTokenInPreference(completePreferencePath,
                    "MultFlag", "MultFlag SBC9");
        }

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : remove CHARM color preference.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion3ToVersion4() {
        _logger.entering("Preferences", "updateFromVersion3ToVersion4");

        String preferenceToRemove = "catalog.color.J/A+A/386/492/charm";
        removePreference(preferenceToRemove);

        _logger.finer("Removed '" + preferenceToRemove + "' preference.");

        // Commit change to file
        return true;
    }

    /**
     * Correction : re-order detailled view columns if user never changed them.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion4ToVersion5() {
        _logger.entering("Preferences", "updateFromVersion4ToVersion5");

        // Updating detailled bright N columns order list if not changed by user
        String detailledBrightNViewColumnOrder = getPreference(
                "view.columns.detailled.bright.N");

        if (detailledBrightNViewColumnOrder.equals(_detailledBrightN_v3) == true) {
            detailledBrightNViewColumnOrder = _detailledBrightN_v4;
            _logger.finer("Re-ordered detailled bright N columns order list.");
        } else {
            _logger.finer(
                    "Leaved customized detailled bright N columns order list unchanged.");
        }

        // Updating detailled bright V columns order list if not changed by user
        String detailledBrightVViewColumnOrder = getPreference(
                "view.columns.detailled.bright.V");

        if (detailledBrightVViewColumnOrder.equals(_detailledBrightV_v3) == true) {
            detailledBrightVViewColumnOrder = _detailledBrightV_v4;
            _logger.finer("Re-ordered detailled bright V columns order list.");
        } else {
            _logger.finer(
                    "Leaved customized detailled bright V columns order list unchanged.");
        }

        // Updating detailled bright K columns order list if not changed by user
        String detailledBrightKViewColumnOrder = getPreference(
                "view.columns.detailled.bright.K");

        if (detailledBrightKViewColumnOrder.equals(_detailledBrightK_v3) == true) {
            detailledBrightKViewColumnOrder = _detailledBrightK_v4;
            _logger.finer("Re-ordered detailled bright K columns order list.");
        } else {
            _logger.finer(
                    "Leaved customized detailled bright K columns order list unchanged.");
        }

        // Updating detailled faint K columns order list if not changed by user
        String detailledFaintKViewColumnOrder = getPreference(
                "view.columns.detailled.faint.K");

        if (detailledFaintKViewColumnOrder.equals(_detailledFaintK_v3) == true) {
            detailledFaintKViewColumnOrder = _detailledFaintK_v4;
            _logger.finer("Re-ordered detailled faint K columns order list.");
        } else {
            _logger.finer(
                    "Leaved customized detailled faint K columns order list unchanged.");
        }

        // Store updated column order
        try {
            setPreference("view.columns.detailled.bright.N",
                    detailledBrightNViewColumnOrder);
            setPreference("view.columns.detailled.bright.V",
                    detailledBrightVViewColumnOrder);
            setPreference("view.columns.detailled.bright.K",
                    detailledBrightKViewColumnOrder);
            setPreference("view.columns.detailled.faint.K",
                    detailledFaintKViewColumnOrder);
        } catch (Exception ex) {
            _logger.log(Level.WARNING, "Could not store updated preference:", ex);

            return false;
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : replaced VarFlag3 with VFlag in bright N detailled view.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion5ToVersion6() {
        _logger.entering("Preferences", "updateFromVersion5ToVersion6");

        return replaceTokenInPreference("view.columns.detailled.bright.N",
                "VarFlag3", "VFlag");
    }

    /**
     * Correction : replaced science object detection distance of 0.01 degrees by 1 arcsecond.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion6ToVersion7() {
        _logger.entering("Preferences", "updateFromVersion6ToVersion7");

        String preferencePath = "query.scienceObjectDetectionDistance";
        String previousValue = getPreference(preferencePath);
        String newValue = Double.toString(1d * ALX.ARCSEC_IN_DEGREES);
        
        if (_logger.isLoggable(Level.FINER)) {
            _logger.finer("Replaced '" + previousValue + "' with '" + newValue
                + "' in '" + preferencePath + "'.");
        }

        // Store the updated column order
        try {
            setPreference(preferencePath, newValue);
        } catch (Exception ex) {
            _logger.log(Level.WARNING,
                    "Could not store updated preference in '" + preferencePath
                    + "' : ", ex);

            return false;
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : replace "view.details.show" preference, with "view.result.verbosity.synthetic", "view.result.verbosity.detailled", "view.result.verbosity.full" preferences to add 'Full View' support.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion7ToVersion8() {
        _logger.entering("Preferences", "updateFromVersion7ToVersion8");

        String preferenceToRemove = "view.details.show";

        boolean previousState = getPreferenceAsBoolean(preferenceToRemove);

        // Remove old preference
        removePreference(preferenceToRemove);

        if (_logger.isLoggable(Level.FINER)) {
            _logger.finer("Removed '" + preferenceToRemove + "' preference.");
        }

        // Store the new preferences with respect to previous state
        try {
            setPreference("view.result.verbosity.synthetic", (!previousState));
            setPreference("view.result.verbosity.detailled", previousState);
            setPreference("view.result.verbosity.full", "false");
        } catch (Exception ex) {
            _logger.log(Level.WARNING,
                    "Could not store updated preference for 'view.result.verbosity.*' : ",
                    ex);

            return false;
        }

        // Commit change to file
        return true;
    }

    /**
     * Corrections :
     *  - add UD_x diameter columns in all bright views but N;
     *  - add WDS column in all detailled views but the Bright N;
     *  - reorder pmRa & pmDec in all detailled views but the Bright N;
     *  - fix variability flag from VarFlag3 to VFlag in detailled bright N view.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion8ToVersion9() {
        _logger.entering("Preferences", "updateFromVersion8ToVersion9");

        boolean status = true;

        // Add UD_x to simple and detailled views
        final String UD_COLUMNS = "UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K";
        HashMap<String, String> udColumnsPreferencePathes = new HashMap<String, String>();
        udColumnsPreferencePathes.put("view.columns.simple.bright.V",
                "UD_B UD_V UD_R UD_I");
        udColumnsPreferencePathes.put("view.columns.detailled.bright.V",
                UD_COLUMNS);
        udColumnsPreferencePathes.put("view.columns.simple.bright.K",
                "UD_V UD_J UD_H UD_K");
        udColumnsPreferencePathes.put("view.columns.detailled.bright.K",
                UD_COLUMNS);

        // For each column list, insert UD_x after e_diam_vk
        for (String completePreferencePath : udColumnsPreferencePathes.keySet()) {
            status &= replaceTokenInPreference(completePreferencePath,
                    "e_diam_vk",
                    "e_diam_vk "
                    + udColumnsPreferencePathes.get(completePreferencePath));
        }

        // Insert WDS column in every detailled views column list but the Bright N (that doesn't have SBC9 anyway)
        // Replace "pmDec pmRa" by "pmRa pmDec" in the each detailled columns order list but bright N
        String[] viewList = {"bright.V", "bright.K", "faint.K"};

        for (String viewName : viewList) {
            // Get the current columns order list content
            String completePreferencePath = "view.columns.detailled."
                    + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "SBC9",
                    "SBC9 WDS");
            status &= replaceTokenInPreference(completePreferencePath,
                    "pmDec pmRa", "pmRa pmDec");
        }

        // Fix variability flag in detailled bright N view
        status &= replaceTokenInPreference("view.columns.detailled.bright.N",
                "VarFlag3", "VFlag");

        // Update legend colors and order
        try {
            setLegendColorsAndOrders(false);
            _logger.finest("Updated legend colors and order.");
        } catch (Exception ex) {
            _logger.log(Level.WARNING,
                    "Could updated legend colors and order:", ex);

            return false;
        }

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : Insert sep1 and sep2 after WDS for all detailled views but bright N.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion9ToVersion10() {
        _logger.entering("Preferences", "updateFromVersion9ToVersion10");

        boolean status = true;

        // Insert sep1 and sep2 after WDS for all detailled views but bright N
        String[] viewList = {"bright.V", "bright.K", "faint.K"};

        for (String viewName : viewList) {
            // Get the current columns order list content
            String completePreferencePath = "view.columns.detailled."
                    + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "WDS",
                    "WDS sep1 sep2");
        }

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : rename VFlag to BinFlag in detailled bright N.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion10ToVersion11() {
        _logger.entering("Preferences", "updateFromVersion10ToVersion11");

        return replaceTokenInPreference("view.columns.detailled.bright.N",
                "VFlag", "BinFlag");
    }
}
