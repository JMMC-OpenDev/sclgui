/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import java.awt.Color;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public final class Preferences extends fr.jmmc.jmcs.data.preference.Preferences {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(Preferences.class.getName());
    /** prefix for catalog color preferences */
    public final static String PREFIX_CATALOG_COLOR = "catalog.color.";
    /** prefix for confidence color preferences */
    public final static String PREFIX_CONFIDENCE_COLOR = "confidence.color.";
    /** Singleton instance */
    private static Preferences _instance = null;
    /** Detailed bright N columns order list, as of default in preference version 3 */
    private static String _detailedBrightN_v3 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 orig F12 e_F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Calib MultFlag VarFlag3 V H plx e_Plx pmRa pmDec A_V Chi2 SpTyp_Teff";
    /** Detailed bright N columns order list, as of default in preference version 4 */
    private static String _detailedBrightN_v4 = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag VarFlag3 V H A_V Chi2 SpTyp_Teff";
    /** Detailed bright N columns order list, as of default in current preference version */
    private static String _detailedBrightN = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag BinFlag V H A_V Chi2 SpTyp_Teff";
    /** Detailed bright V columns order list, as of default in preference version 3 */
    private static String _detailedBrightV_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright V columns order list, as of default in preference version 4 */
    private static String _detailedBrightV_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright V columns order list, as of default in preference version 12 */
    private static String _detailedBrightV_v12 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright V columns order list, as of default in current preference version */
    private static String _detailedBrightV = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel UDDK e_UDDK B V R I J H K N Av";
    /** Detailed bright K columns order list, as of default in preference version 3 */
    private static String _detailedBrightK_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright K columns order list, as of default in preference version 4 */
    private static String _detailedBrightK_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright K columns order list, as of default in preference version 12 */
    private static String _detailedBrightK_v12 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright K columns order list, as of default in current preference version */
    private static String _detailedBrightK = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel UDDK e_UDDK B V R I J H K N Av";
    /** Detailed faint K columns order list, as of default in preference version 3 */
    private static String _detailedFaintK_v3 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa GLAT GLON plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 4 */
    private static String _detailedFaintK_v4 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 12 */
    private static String _detailedFaintK_v12 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in current preference version */
    private static String _detailedFaintK = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";

    /** Hidden constructor */
    protected Preferences() {
        // Insert your singleton initialization code here
    }

    /**
     * Return the preference filename.
     */
    @Override
    protected String getPreferenceFilename() {
        return "fr.jmmc.searchcal.properties";
    }

    /**
     * Return the preference revision number.
     */
    @Override
    protected int getPreferencesVersionNumber() {
        return 15;
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
        if (_logger.isInfoEnabled()) {
            _logger.info("Upgrading preference file from version '{}' to version '{}'.", loadedVersionNumber, loadedVersionNumber + 1);
        }

        switch (loadedVersionNumber) {
            // Wrong column identifiers in the the simple and detailed bright N columns order list
            case 1:
                return updateFromVersion1ToVersion2();

            // Added SBC9 column to each detailed view
            case 2:
                return updateFromVersion2ToVersion3();

            // Remove CHARM color preference
            case 3:
                return updateFromVersion3ToVersion4();

            // Re-order detailed view columns
            case 4:
                return updateFromVersion4ToVersion5();

            // Replaced VarFlag3 with VFlag in bright N detailed view
            case 5:
                return updateFromVersion5ToVersion6();

            // Replaced science object detection distance of 0.01 degrees by 1 arcsecond
            case 6:
                return updateFromVersion6ToVersion7();

            // Replace "view.details.show" preference, with PreferenceKey.VERBOSITY_SYNTHETIC_FLAG, PreferenceKey.VERBOSITY_DETAILED_FLAG, PreferenceKey.VERBOSITY_FULL_FLAG preferences to add 'Full View' support
            case 7:
                return updateFromVersion7ToVersion8();

            /*
             * Add UD_x diameter columns in all bright views but N;
             * Add WDS column in all detailed views but the Bright N;
             * Reorder pmRa & pmDec in all detailed views but the Bright N;
             * Fix variability flag from VarFlag3 to VFlag in detailed bright N view
             */
            case 8:
                return updateFromVersion8ToVersion9();

            // Insert sep1 and sep2 after WDS for all detailed views but bright N
            case 9:
                return updateFromVersion9ToVersion10();

            // Rename VFlag to BinFlag in detailed bright N
            case 10:
                return updateFromVersion10ToVersion11();

            // Rename detailed preferences from 'detaiLLed'
            case 11:
                return updateFromVersion11ToVersion12();

            // Remove deprecated columns as of server version 4.0.2
            case 12:
                return updateFromVersion12ToVersion13();

            // Remove CHARM2 color preference
            case 13:
                return updateFromVersion13ToVersion14();

            // Removed L and M columns and added UD_N in bright detailed views
            case 14:
                return updateFromVersion14ToVersion15();

            // By default, triggers default values load.
            default:
                return false;
        }
    }

    /**
     * Set legend colors and order.
     *
     * @param defaultFlag If true sets values to defaults preferences, otherwise in current preferences.
     * @throws PreferencesException  
     */
    void setLegendColorsAndOrders(boolean defaultFlag) throws PreferencesException {
        // Place catalog origin colors
        int i = 0;

        for (Catalog catalog : Catalog.values()) {
            Color catalogColor = Catalog.getDefaultColor(catalog);

            if (defaultFlag) {
                setDefaultPreference(PREFIX_CATALOG_COLOR + catalog.reference(), i++, catalogColor);
            } else {
                setPreference(PREFIX_CATALOG_COLOR + catalog.reference(), i++, catalogColor);
            }
        }

        // Place confidence indexes color
        i = 0;

        if (defaultFlag) {
            setDefaultPreference(PREFIX_CONFIDENCE_COLOR + "HIGH", i++, "#ECECEC");
            setDefaultPreference(PREFIX_CONFIDENCE_COLOR + "MEDIUM", i++, "#CCCCCC");
            setDefaultPreference(PREFIX_CONFIDENCE_COLOR + "LOW", i++, "#6F6F6F");
        } else {
            setPreference(PREFIX_CONFIDENCE_COLOR + "HIGH", i++, "#ECECEC");
            setPreference(PREFIX_CONFIDENCE_COLOR + "MEDIUM", i++, "#CCCCCC");
            setPreference(PREFIX_CONFIDENCE_COLOR + "LOW", i++, "#6F6F6F");
        }
    }

    /** Set preferences default values */
    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        // Define legend colors and order for default preferences
        setLegendColorsAndOrders(true);

        // Place help behaviour
        setDefaultPreference(PreferenceKey.SHOW_TOOLTIPS_FLAG, "true");

        // Place view behaviour
        setDefaultPreference(PreferenceKey.SHOW_LEGEND_FLAG, "false");
        setDefaultPreference(PreferenceKey.VERBOSITY_SYNTHETIC_FLAG, "true");
        setDefaultPreference(PreferenceKey.VERBOSITY_DETAILED_FLAG, "false");
        setDefaultPreference(PreferenceKey.VERBOSITY_FULL_FLAG, "false");

        // Simple 'Bright N' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_N,
                "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu)");
        // detailed 'Bright N' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N,
                _detailedBrightN);

        // Simple 'Bright V' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_V,
                "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_B UD_V UD_R UD_I SpType B V R I");
        // detailed 'Bright V' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_V,
                _detailedBrightV);

        // Simple 'Bright K' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_K,
                "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_V UD_J UD_H UD_K SpType V J H K");
        // detailed 'Bright K' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_K,
                _detailedBrightK);

        // Simple 'Faint K' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_FAINT_K,
                "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_mean e_diam_mean V Icous J H K");
        // detailed 'Faint K' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_FAINT_K, _detailedFaintK);

        // Query default values preferences (ETA_TAU, bright V)
        setDefaultPreference(PreferenceKey.QUERY_MAGNITUDE_BAND, "V");
        setDefaultPreference(PreferenceKey.QUERY_INSTRUMENTAL_WAVELENGTH, Double.toString(1.0d * ALX.MICRON));
        setDefaultPreference(PreferenceKey.QUERY_INSTRUMENTAL_BASELINE, Double.toString(102.45d * ALX.METER));
        setDefaultPreference(PreferenceKey.QUERY_SCIENCE_NAME, "ETA TAU");
        setDefaultPreference(PreferenceKey.QUERY_SCIENCE_RA, "03:47:29.0765");
        setDefaultPreference(PreferenceKey.QUERY_SCIENCE_DEC, "24:06:18.494");
        setDefaultPreference(PreferenceKey.QUERY_SCIENCE_MAGNITUDE, "2.873");
        setDefaultPreference(PreferenceKey.QUERY_MINIMUM_MAGNITUDE, "2.0");
        setDefaultPreference(PreferenceKey.QUERY_MAXIMUM_MAGNITUDE, "4.0");
        setDefaultPreference(PreferenceKey.QUERY_BRIGHT_FLAG, "true");
        setDefaultPreference(PreferenceKey.QUERY_DIFF_RA, Double.toString(3600d * ALX.ARCSEC));
        setDefaultPreference(PreferenceKey.QUERY_DIFF_DEC, "" + (1200 * ALX.ARCSEC));
        setDefaultPreference(PreferenceKey.QUERY_RADIAL_SIZE, Double.toString(5.0d * ALX.ARCMIN));
        setDefaultPreference(PreferenceKey.QUERY_RADIAL_FLAG, "true");

        setDefaultPreference(PreferenceKey.QUERY_SCIENCE_DETECTION, Double.toString(1d * ALX.ARCSEC_IN_DEGREES));
        setDefaultPreference(PreferenceKey.QUERY_MINIMUM_DELTA, "-2.0");
        setDefaultPreference(PreferenceKey.QUERY_MAXIMUM_DELTA, "2.0");

        setDefaultPreference(PreferenceKey.FILTER_NON_CALIBRATORS, "true");
    }

    /**
     * @return the singleton instance.
     */
    public static final synchronized Preferences getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new Preferences();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Correction : wrong column identifiers in the simple and detailed bright N columns order list.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion1ToVersion2() {
        boolean status = true;
        status &= replaceTokenInPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_N,
                "e_diam_vk", "e_dia12 F12");
        status &= replaceTokenInPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_N,
                "diam_vk", "Dia12");
        status &= replaceTokenInPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N,
                "vis2Flag", "vis2");

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : added SBC9 column to each detailed view.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion2ToVersion3() {
        boolean status = true;

        // Insert "SBC9" after "MultFlag" in each detailed columns order list
        String[] viewList = {"bright.V", "bright.K", "faint.K"};

        for (String viewName : viewList) {
            // Get the current columns order list content
            String completePreferencePath = "view.columns.detailed."
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
        String preferenceToRemove = Preferences.PREFIX_CATALOG_COLOR + "J/A+A/386/492/charm";
        removePreference(preferenceToRemove);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Removed '" + preferenceToRemove + "' preference.");
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : re-order detailed view columns if user never changed them.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion4ToVersion5() {
        // Updating detailed bright N columns order list if not changed by user
        String detailedBrightNViewColumnOrder = getPreference(
                PreferenceKey.VIEW_DETAILED_BRIGHT_N);

        if (detailedBrightNViewColumnOrder.equals(_detailedBrightN_v3)) {
            detailedBrightNViewColumnOrder = _detailedBrightN_v4;
            _logger.debug("Re-ordered detailed bright N columns order list.");
        } else {
            _logger.debug("Leaved customized detailed bright N columns order list unchanged.");
        }

        // Updating detailed bright V columns order list if not changed by user
        String detailedBrightVViewColumnOrder = getPreference(
                PreferenceKey.VIEW_DETAILED_BRIGHT_V);

        if (detailedBrightVViewColumnOrder.equals(_detailedBrightV_v3)) {
            detailedBrightVViewColumnOrder = _detailedBrightV_v4;
            _logger.debug("Re-ordered detailed bright V columns order list.");
        } else {
            _logger.debug("Leaved customized detailed bright V columns order list unchanged.");
        }

        // Updating detailed bright K columns order list if not changed by user
        String detailedBrightKViewColumnOrder = getPreference(
                PreferenceKey.VIEW_DETAILED_BRIGHT_K);

        if (detailedBrightKViewColumnOrder.equals(_detailedBrightK_v3)) {
            detailedBrightKViewColumnOrder = _detailedBrightK_v4;
            _logger.debug("Re-ordered detailed bright K columns order list.");
        } else {
            _logger.debug("Leaved customized detailed bright K columns order list unchanged.");
        }

        // Updating detailed faint K columns order list if not changed by user
        String detailedFaintKViewColumnOrder = getPreference(
                PreferenceKey.VIEW_DETAILED_FAINT_K);

        if (detailedFaintKViewColumnOrder.equals(_detailedFaintK_v3)) {
            detailedFaintKViewColumnOrder = _detailedFaintK_v4;
            _logger.debug("Re-ordered detailed faint K columns order list.");
        } else {
            _logger.debug("Leaved customized detailed faint K columns order list unchanged.");
        }

        // Store updated column order
        try {
            setPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N, detailedBrightNViewColumnOrder);
            setPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_V, detailedBrightVViewColumnOrder);
            setPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_K, detailedBrightKViewColumnOrder);
            setPreference(PreferenceKey.VIEW_DETAILED_FAINT_K, detailedFaintKViewColumnOrder);
        } catch (PreferencesException pe) {
            _logger.warn("Could not store updated preference:", pe);

            return false;
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : replaced VarFlag3 with VFlag in bright N detailed view.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion5ToVersion6() {
        return replaceTokenInPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N,
                "VarFlag3", "VFlag");
    }

    /**
     * Correction : replaced science object detection distance of 0.01 degrees by 1 arcsecond.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion6ToVersion7() {
        Object preferencePath = PreferenceKey.QUERY_SCIENCE_DETECTION;
        String previousValue = getPreference(preferencePath);
        String newValue = Double.toString(1d * ALX.ARCSEC_IN_DEGREES);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Replaced '" + previousValue + "' with '" + newValue
                    + "' in '" + preferencePath + "'.");
        }

        // Store the updated column order
        try {
            setPreference(preferencePath, newValue);
        } catch (PreferencesException pe) {
            _logger.warn("Could not store updated preference in '{}' : ", preferencePath, pe);

            return false;
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : replace "view.details.show" preference, with PreferenceKey.VERBOSITY_SYNTHETIC_FLAG, PreferenceKey.VERBOSITY_DETAILED_FLAG, PreferenceKey.VERBOSITY_FULL_FLAG preferences to add 'Full View' support.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion7ToVersion8() {
        String preferenceToRemove = "view.details.show";

        boolean previousState = getPreferenceAsBoolean(preferenceToRemove);

        // Remove old preference
        removePreference(preferenceToRemove);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Removed '" + preferenceToRemove + "' preference.");
        }

        // Store the new preferences with respect to previous state
        try {
            setPreference(PreferenceKey.VERBOSITY_SYNTHETIC_FLAG, (!previousState));
            setPreference(PreferenceKey.VERBOSITY_DETAILED_FLAG, previousState);
            setPreference(PreferenceKey.VERBOSITY_FULL_FLAG, "false");
        } catch (PreferencesException pe) {
            _logger.warn("Could not store updated preference for 'view.result.verbosity.*' : ", pe);

            return false;
        }

        // Commit change to file
        return true;
    }

    /**
     * Corrections :
     *  - add UD_x diameter columns in all bright views but N;
     *  - add WDS column in all detailed views but the Bright N;
     *  - reorder pmRa & pmDec in all detailed views but the Bright N;
     *  - fix variability flag from VarFlag3 to VFlag in detailed bright N view.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion8ToVersion9() {
        boolean status = true;

        // Add UD_x to simple and detailed views
        final String UD_COLUMNS = "UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K";
        HashMap<Object, String> udColumnsPreferencePathes = new HashMap<Object, String>();
        udColumnsPreferencePathes.put(PreferenceKey.VIEW_SIMPLE_BRIGHT_V,
                "UD_B UD_V UD_R UD_I");
        udColumnsPreferencePathes.put(PreferenceKey.VIEW_DETAILED_BRIGHT_V,
                UD_COLUMNS);
        udColumnsPreferencePathes.put(PreferenceKey.VIEW_SIMPLE_BRIGHT_K,
                "UD_V UD_J UD_H UD_K");
        udColumnsPreferencePathes.put(PreferenceKey.VIEW_DETAILED_BRIGHT_K,
                UD_COLUMNS);

        // For each column list, insert UD_x after e_diam_vk
        for (Object completePreferencePath : udColumnsPreferencePathes.keySet()) {
            status &= replaceTokenInPreference(completePreferencePath,
                    "e_diam_vk",
                    "e_diam_vk "
                    + udColumnsPreferencePathes.get(completePreferencePath));
        }

        // Insert WDS column in every detailed views column list but the Bright N (that doesn't have SBC9 anyway)
        // Replace "pmDec pmRa" by "pmRa pmDec" in the each detailed columns order list but bright N
        String[] viewList = {"bright.V", "bright.K", "faint.K"};

        for (String viewName : viewList) {
            // Get the current columns order list content
            String completePreferencePath = "view.columns.detailed."
                    + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "SBC9",
                    "SBC9 WDS");
            status &= replaceTokenInPreference(completePreferencePath,
                    "pmDec pmRa", "pmRa pmDec");
        }

        // Fix variability flag in detailed bright N view
        status &= replaceTokenInPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N,
                "VarFlag3", "VFlag");

        // Update legend colors and order
        try {
            setLegendColorsAndOrders(false);
            _logger.debug("Updated legend colors and order.");
        } catch (PreferencesException pe) {
            _logger.warn("Could updated legend colors and order:", pe);

            return false;
        }

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : Insert sep1 and sep2 after WDS for all detailed views but bright N.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion9ToVersion10() {
        boolean status = true;

        // Insert sep1 and sep2 after WDS for all detailed views but bright N
        String[] viewList = {"bright.V", "bright.K", "faint.K"};

        for (String viewName : viewList) {
            // Get the current columns order list content
            String completePreferencePath = "view.columns.detailed."
                    + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "WDS",
                    "WDS sep1 sep2");
        }

        // Commit change to file if everything went fine
        return status;
    }

    /**
     * Correction : rename VFlag to BinFlag in detailed bright N.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion10ToVersion11() {
        return replaceTokenInPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N,
                "VFlag", "BinFlag");
    }

    /**
     * Correction : Rename detailed preferences from 'detaiLLed' to 'detaiLed'.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion11ToVersion12() {
        String[] oldNames = {"view.result.verbosity.detailled", "view.columns.detailled.bright.N", "view.columns.detailled.bright.V", "view.columns.detailled.bright.K", "view.columns.detailled.faint.K"};
        Object[] newNames = {PreferenceKey.VERBOSITY_DETAILED_FLAG, PreferenceKey.VIEW_DETAILED_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_K, PreferenceKey.VIEW_DETAILED_FAINT_K};
        for (int i = 0; i < oldNames.length; i++) {

            // Get stored value
            String oldName = oldNames[i];
            String value = getPreference(oldName);

            // Store the value in the new renamed preference
            String newName = newNames[i].toString();
            try {
                setPreference(newName, value);
            } catch (PreferencesException pe) {
                _logger.warn("Could not store updated preference in '{}' : ", newName, pe);
                return false;
            }

            // Remove the old preference
            removePreference(oldName);
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : Remove deprecated columns as of server version 4.0.2.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion12ToVersion13() {
        boolean status = true;
        String[] deprecatedColumns = {"Jflag ", "Kflag ", "e_LD ", "LD ", "e_UD ", "UD ", "color ", "Meth ", "lambda ", "photflux ", "units ", "vis2Flag "};
        Object[] viewNames = {PreferenceKey.VIEW_DETAILED_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_K, PreferenceKey.VIEW_DETAILED_FAINT_K};

        // For each detailed views
        for (Object view : viewNames) {
            String before = getPreference(view);

            // Remove any deprecated columns found
            for (String column : deprecatedColumns) {
                status &= removeTokenInPreference(view, column);
                //System.out.println("\tstatus['" + column + "'] = " + status);
            }

            String after = getPreference(view);
        }

        // Commit all changes if and only if all went fine.
        return status;
    }

    /**
     * Correction : remove CHARM2 color preference.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion13ToVersion14() {
        String preferenceToRemove = Preferences.PREFIX_CATALOG_COLOR + "J/A+A/431/773/charm2";
        removePreference(preferenceToRemove);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Removed '" + preferenceToRemove + "' preference.");
        }

        // Commit change to file
        return true;
    }

    /**
     * Correction : removed L and M columns and added UD_N in bright detailed views.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion14ToVersion15() {
        boolean status = true;
        String[] deprecatedColumns = {" L ", " M "};
        Object[] viewNames = {PreferenceKey.VIEW_DETAILED_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_K};

        // For each detailed views
        for (Object view : viewNames) {
            String before = getPreference(view);

            // Remove all deprecated columns found
            for (String column : deprecatedColumns) {
                status &= removeTokenInPreference(view, column);
            }

            // Add new column UD_N column after UD_K
            replaceTokenInPreference(view, "UD_K ", "UD_K UD_N ");

            String after = getPreference(view);
        }

        // Commit all changes if and only if all went fine.
        return status;
    }
}
