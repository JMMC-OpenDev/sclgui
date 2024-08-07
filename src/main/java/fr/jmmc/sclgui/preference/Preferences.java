/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.sclgui.calibrator.Confidence;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public final class Preferences extends fr.jmmc.jmcs.data.preference.Preferences {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(Preferences.class.getName());
    /** prefix for catalog color preferences */
    public static final String PREFIX_CATALOG_COLOR = "catalog.color.";
    /** prefix for confidence color preferences */
    public static final String PREFIX_CONFIDENCE_COLOR = "confidence.color.";
    /** prefix for view columns in synthetic mode */
    public static final String PREFIX_VIEW_COLUMNS = "view.columns";
    /** prefix for view columns in synthetic mode */
    public static final String PREFIX_VIEW_COLUMNS_SIMPLE = PREFIX_VIEW_COLUMNS + ".simple.";
    /** prefix for view columns in detailed mode */
    public static final String PREFIX_VIEW_COLUMNS_DETAILED = PREFIX_VIEW_COLUMNS + ".detailed.";
    /** Singleton instance */
    private static Preferences _instance = null;
    /** Detailed bright N columns order list, as of default in preference version 3 */
    private static final String _detailedBrightN_v3 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 orig F12 e_F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Calib MultFlag VarFlag3 V H plx e_Plx pmRa pmDec A_V Chi2 SpTyp_Teff";
    /** Detailed bright N columns order list, as of default in preference version 4 */
    private static final String _detailedBrightN_v4 = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag VarFlag3 V H A_V Chi2 SpTyp_Teff";
    /** Detailed bright N columns order list, as of default in preference version 18 */
    private static final String _detailedBrightN_v18 = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag BinFlag V H A_V Chi2 SpTyp_Teff";
    /** Detailed bright N columns order list, as of default in preference version 20 */
    private static final String _detailedBrightN_v20 = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err Dia12 e_dia12 orig F12 e_F12 N SpType ObjTypes Calib MultFlag BinFlag V H A_V Chi2 SpTyp_Teff";
    /** Detailed bright N columns order list, as of default in current preference version */
    private static final String _detailedBrightN = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err diam_chi2 LDD e_LDD_rel L M N UD_L UD_M UD_N GroupSize SIMBAD SpType ObjTypes MultFlag V H";

    /** Detailed bright V columns order list, as of default in preference version 3 */
    private static final String _detailedBrightV_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright V columns order list, as of default in preference version 4 */
    private static final String _detailedBrightV_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright V columns order list, as of default in preference version 12 */
    private static final String _detailedBrightV_v12 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright V columns order list, as of default in preference version 15 */
    private static final String _detailedBrightV_v15 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel UDDK e_UDDK B V R I J H K N Av";
    /** Detailed bright V columns order list, as of default in preference version 20 */
    private static final String _detailedBrightV_v20 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel UDDK e_UDDK B V R Icous J H K N Av";
    /** Detailed bright V columns order list, as of default in current preference version */
    private static final String _detailedBrightV = "dist vis2 vis2Err diam_chi2 LDD e_LDD_rel UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GroupSize SIMBAD SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel B V R J H K N";

    /** Detailed bright K columns order list, as of default in preference version 3 */
    private static final String _detailedBrightK_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright K columns order list, as of default in preference version 4 */
    private static final String _detailedBrightK_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright K columns order list, as of default in preference version 12 */
    private static final String _detailedBrightK_v12 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";
    /** Detailed bright K columns order list, as of default in preference version 15 */
    private static final String _detailedBrightK_v15 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel UDDK e_UDDK B V R I J H K N Av";
    /** Detailed bright K columns order list, as of default in preference version 20 */
    private static final String _detailedBrightK_v20 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel UDDK e_UDDK B V R Icous J H K N Av";
    /** Detailed bright K columns order list, as of default in current preference version */
    private static final String _detailedBrightK = "dist vis2 vis2Err diam_chi2 LDD e_LDD_rel UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GroupSize SIMBAD SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 RadVel RotVel B V R J H K N";

    /** Detailed faint K columns order list, as of default in preference version 3 */
    private static final String _detailedFaintK_v3 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa GLAT GLON plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 4 */
    private static final String _detailedFaintK_v4 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 12 */
    private static final String _detailedFaintK_v12 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 15 */
    private static final String _detailedFaintK_v15 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 20 */
    private static final String _detailedFaintK_v20 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GLAT GLON SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 B Bphg V Rphg Icous Iphg J Jcous H Hcous K Kcous Av";
    /** Detailed faint K columns order list, as of default in preference version 20 */
    private static final String _detailedFaintK_v21 = "dist vis2 vis2Err diam_chi2 LDD e_LDD_rel 2MASS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GroupSize SIMBAD SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 B V J H K";
    /** Detailed faint K columns order list, as of default in current preference version */
    private static final String _detailedFaintK = "dist vis2 vis2Err diam_chi2 LDD e_LDD_rel UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K 2MASS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec plx GroupSize SIMBAD SpType ObjTypes VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 WDS sep1 sep2 B V J H K";

    /** Simple bright N columns order list, as of default in preference version 20 */
    private static final String _simpleBrightN_v20 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 F12 SpType ObjTypes N";
    /** Simple bright N columns order list, as of default in current preference version */
    private static final String _simpleBrightN = "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_chi2 LDD e_LDD_rel GroupSize SIMBAD SpType ObjTypes L M N UD_L UD_M UD_N";

    /** Simple bright V columns order list, as of default in preference version 15 */
    private static final String _simpleBrightV_v15 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_B UD_V UD_R UD_I SpType B V R I";
    /** Simple bright N columns order list, as of default in preference version 18 */
    private static final String _simpleBrightN_v18 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu)";
    /** Simple bright V columns order list, as of default in preference version 20 */
    private static final String _simpleBrightV_v20 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_B UD_V UD_R UD_I SpType ObjTypes B V R Icous";
    /** Simple bright V columns order list, as of default in current preference version */
    private static final String _simpleBrightV = "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_chi2 LDD e_LDD_rel UD_B UD_V UD_R UD_I GroupSize SIMBAD SpType ObjTypes B V R";

    /** Simple bright K columns order list, as of default in preference version 20 */
    private static final String _simpleBrightK_v20 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk UD_V UD_J UD_H UD_K SpType ObjTypes V J H K";
    /** Simple bright K columns order list, as of default in current preference version */
    private static final String _simpleBrightK = "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_chi2 LDD e_LDD_rel UD_V UD_J UD_H UD_K GroupSize SIMBAD SpType ObjTypes V J H K";

    /** Simple faint K columns order list, as of default in preference version 20 */
    private static final String _simpleFaintK_v20 = "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_mean e_diam_mean SpType ObjTypes V Icous J H K";
    /** Simple faint K columns order list, as of default in preference version 21 */
    private static final String _simpleFaintK_v21 = "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_chi2 LDD e_LDD_rel GroupSize SIMBAD SpType ObjTypes V J H K";
    /** Simple faint K columns order list, as of default in current preference version */
    private static final String _simpleFaintK = "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_chi2 LDD e_LDD_rel UD_V UD_J UD_H UD_K GroupSize SIMBAD SpType ObjTypes V J H K";

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
        return 23;
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
        _logger.info("Upgrading preference file from version '{}' to version '{}'.", loadedVersionNumber, loadedVersionNumber + 1);

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

            // Removed Vphg and replace I by Icous in all views
            case 15:
                return updateFromVersion15ToVersion16();

            // Added server URL preference
            case 16:
                return updateFromVersion16ToVersion17();

            // Reset legend colors (new HIP1 / HIP2 catalogs)
            case 17:
                return updateFromVersion17ToVersion18();

            // Fix Bright N scenario
            case 18:
                return updateFromVersion18ToVersion19();

            // Add ObjTypes
            case 19:
                return updateFromVersion19ToVersion20();

            // SearchCal v5
            case 20:
                return updateFromVersion20ToVersion21();

            // SearchCal v6
            case 21:
                return updateFromVersion21ToVersion22();

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
            setDefaultPreference(PREFIX_CONFIDENCE_COLOR + Confidence.CONFIDENCE_HIGH, i++, "#E0E0E0");
            setDefaultPreference(PREFIX_CONFIDENCE_COLOR + Confidence.CONFIDENCE_MEDIUM, i++, "#B0B0B0");
            setDefaultPreference(PREFIX_CONFIDENCE_COLOR + Confidence.CONFIDENCE_LOW, i++, "#6F6F6F");
        } else {
            setPreference(PREFIX_CONFIDENCE_COLOR + Confidence.CONFIDENCE_HIGH, i++, "#E0E0E0");
            setPreference(PREFIX_CONFIDENCE_COLOR + Confidence.CONFIDENCE_MEDIUM, i++, "#B0B0B0");
            setPreference(PREFIX_CONFIDENCE_COLOR + Confidence.CONFIDENCE_LOW, i++, "#6F6F6F");
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
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_N, _simpleBrightN);
        // detailed 'Bright N' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N, _detailedBrightN);

        // Simple 'Bright V' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_V, _simpleBrightV);
        // detailed 'Bright V' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_V, _detailedBrightV);

        // Simple 'Bright K' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_K, _simpleBrightK);
        // detailed 'Bright K' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_K, _detailedBrightK);

        // Simple 'Faint K' view
        setDefaultPreference(PreferenceKey.VIEW_SIMPLE_FAINT_K, _simpleFaintK);
        // detailed 'Faint K' view
        setDefaultPreference(PreferenceKey.VIEW_DETAILED_FAINT_K, _detailedFaintK);

        // TODO: add view preferences for LM bands
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
        setDefaultPreference(PreferenceKey.QUERY_RADIAL_SIZE, Double.toString(60.0d * ALX.ARCMIN));
        setDefaultPreference(PreferenceKey.QUERY_RADIAL_FLAG, "false");

        setDefaultPreference(PreferenceKey.QUERY_SCIENCE_DETECTION, Double.toString(1d * ALX.ARCSEC_IN_DEGREES));
        setDefaultPreference(PreferenceKey.QUERY_MINIMUM_DELTA, "-2.0");
        setDefaultPreference(PreferenceKey.QUERY_MAXIMUM_DELTA, "2.0");

        setDefaultPreference(PreferenceKey.FILTER_NON_CALIBRATORS, "true");
        setDefaultPreference(PreferenceKey.SERVER_URL_ADDRESS, "");
        setDefaultPreference(PreferenceKey.SERVER_DIAGNOSE, "false");

        setDefaultPreference(PreferenceKey.FLUX_EDITOR_JY, Boolean.TRUE);

    }

    /**
     * @return the singleton instance.
     */
    public static synchronized Preferences getInstance() {
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
            String completePreferencePath = Preferences.PREFIX_VIEW_COLUMNS_DETAILED + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "MultFlag", "MultFlag SBC9");
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
            _logger.debug("Removed '{}' preference.", preferenceToRemove);
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
            _logger.debug("Removed '{}' preference.", preferenceToRemove);
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
            String completePreferencePath = Preferences.PREFIX_VIEW_COLUMNS_DETAILED + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "SBC9", "SBC9 WDS");
            status &= replaceTokenInPreference(completePreferencePath, "pmDec pmRa", "pmRa pmDec");
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
            String completePreferencePath = Preferences.PREFIX_VIEW_COLUMNS_DETAILED + viewName;
            status &= replaceTokenInPreference(completePreferencePath, "WDS", "WDS sep1 sep2");
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
            _logger.debug("Removed '{}' preference.", preferenceToRemove);
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

    /**
     * Correction : removed Vphg and replace I by Icous in all views.
     *
     * @return true if fine and should write to file, false otherwise.
     */
    private boolean updateFromVersion15ToVersion16() {

        boolean status = true;
        String[] deprecatedColumns = {" Vphg ", " I "};
        // Remove all deprecated columns found
        for (String column : deprecatedColumns) {
            status &= removeTokenInPreference(PreferenceKey.VIEW_DETAILED_FAINT_K, column);
        }

        Object[] viewNames = {PreferenceKey.VIEW_SIMPLE_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_K};
        // For each views
        for (Object view : viewNames) {
            status &= replaceTokenInPreference(view, " I ", " Icous ");
        }

        // Commit all changes if and only if everything went fine.
        return status;
    }

    /**
     * Correction : added server URL address.
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion16ToVersion17() {
        return true; // To effectively save Preference file with the new empty value as default
    }

    /**
     * Correction : reset legend colors (new HIP1 / HIP2 catalogs).
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion17ToVersion18() {
        // Update legend colors and order
        try {
            setLegendColorsAndOrders(false);
            _logger.debug("Updated legend colors and order.");
        } catch (PreferencesException pe) {
            _logger.warn("Could updated legend colors and order:", pe);

            return false;
        }
        return true; // To effectively save Preference file with the new empty value as default
    }

    /**
     * Correction : removed vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) in Bright N scenario
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion18ToVersion19() {

        boolean status = true;
        String[] deprecatedColumns = {" vis2\\(8mu\\)", " vis2Err\\(8mu\\)", " vis2\\(13mu\\)", " vis2Err\\(13mu\\)"};
        // Remove all deprecated columns found
        for (String column : deprecatedColumns) {
            status &= removeTokenInPreference(PreferenceKey.VIEW_SIMPLE_BRIGHT_N, column);
            status &= removeTokenInPreference(PreferenceKey.VIEW_DETAILED_BRIGHT_N, column);
        }

        // Commit all changes if and only if everything went fine.
        return status;
    }

    /**
     * Correction : add ObjTypes in all scenarios
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion19ToVersion20() {
        boolean status = true;

        Object[] viewNames = {PreferenceKey.VIEW_SIMPLE_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_V,
                              PreferenceKey.VIEW_SIMPLE_BRIGHT_K, PreferenceKey.VIEW_DETAILED_BRIGHT_K,
                              PreferenceKey.VIEW_SIMPLE_FAINT_K, PreferenceKey.VIEW_DETAILED_FAINT_K,
                              PreferenceKey.VIEW_SIMPLE_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_N};
        // For each views
        for (Object view : viewNames) {
            String before = getPreference(view);

            if (!before.contains("ObjTypes")) {
                status &= replaceTokenInPreference(view, " SpType ", " SpType ObjTypes ");
            }
        }

        // Commit all changes if and only if everything went fine.
        return status;
    }

    /**
     * Correction : add SIMBAD, GroupSize, LDD, e_LDD_rel, diam_chi2 in all scenarios
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion20ToVersion21() {
        boolean status = true;

        try {
            setPreference(PreferenceKey.QUERY_RADIAL_FLAG, "false");
            setPreference(PreferenceKey.QUERY_RADIAL_SIZE, Double.toString(60.0d * ALX.ARCMIN));
        } catch (PreferencesException pe) {
            _logger.warn("Could not update query radial preferences:", pe);
            return false;
        }

        Object[] viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_V,
                                          PreferenceKey.VIEW_SIMPLE_BRIGHT_K, PreferenceKey.VIEW_DETAILED_BRIGHT_K,
                                          PreferenceKey.VIEW_SIMPLE_FAINT_K, PreferenceKey.VIEW_DETAILED_FAINT_K,
                                          PreferenceKey.VIEW_SIMPLE_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_N};
        // For each views
        for (Object view : viewNames) {
            String before = getPreference(view);

            if (!before.contains("GroupSize")) {
                status &= replaceTokenInPreference(view, " SpType ", " GroupSize SpType ");
            }

            if (!before.contains("SIMBAD")) {
                status &= replaceTokenInPreference(view, " SpType ", " SIMBAD SpType ");
            }
            if (!before.contains("LDD")) {
                status &= replaceTokenInPreference(view, " vis2Err ", " vis2Err LDD e_LDD_rel ");
            }
            if (!before.contains("diam_chi2")) {
                status &= replaceTokenInPreference(view, " vis2Err ", " vis2Err diam_chi2 ");
            }
        }

        // Remove any deprecated columns found
        List<String> deprecatedColumns;
        viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_V};
        // V:
        deprecatedColumns = Arrays.asList(
                ("e_diam_vk e_UDDK Av diam_bv diam_vk diam_vr UDDK GLAT GLON Icous")
                        .split(" ")
        );
        for (Object view : viewNames) {
            for (String column : deprecatedColumns) {
                status &= removeTokenInPreference(view, column);
            }
        }

        viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_BRIGHT_K, PreferenceKey.VIEW_DETAILED_BRIGHT_K};
        // K:
        deprecatedColumns = Arrays.asList(
                ("e_diam_vk e_UDDK Av diam_bv diam_vk diam_vr GLAT GLON Icous UDDK")
                        .split(" ")
        );
        for (Object view : viewNames) {
            for (String column : deprecatedColumns) {
                status &= removeTokenInPreference(view, column);
            }
        }

        viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_N};
        // N:
        deprecatedColumns = Arrays.asList(
                ("e_dia12 e_F12 A_V BinFlag Calib Chi2 Dia12 F12 orig SpTyp_Teff")
                        .split(" ")
        );
        for (Object view : viewNames) {
            for (String column : deprecatedColumns) {
                status &= removeTokenInPreference(view, column);
            }
            status &= replaceTokenInPreference(view, " N ", " L M N UD_L UD_M UD_N ");
        }

        viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_FAINT_K, PreferenceKey.VIEW_DETAILED_FAINT_K};
        // FAINT K:
        deprecatedColumns = Arrays.asList(
                ("e_diam_mean Av Bphg DENIS diam_hk diam_ij diam_ik diam_jh diam_jk diam_mean GLAT GLON Hcous Icous Iphg Jcous Kcous Rphg")
                        .split(" ")
        );
        for (Object view : viewNames) {
            for (String column : deprecatedColumns) {
                status &= removeTokenInPreference(view, column);
            }
        }

        // Show changes:
        viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_V,
                                 PreferenceKey.VIEW_SIMPLE_BRIGHT_K, PreferenceKey.VIEW_DETAILED_BRIGHT_K,
                                 PreferenceKey.VIEW_SIMPLE_FAINT_K, PreferenceKey.VIEW_DETAILED_FAINT_K,
                                 PreferenceKey.VIEW_SIMPLE_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_N
        };

        // For each views
        for (Object view : viewNames) {
            String before = getPreference(view);

            String after = StringUtils.replaceWhiteSpaces(before, " ");
            /*            
            System.out.println("Updating view: " + view);
            System.out.println("Pref: " + before);
             */
            try {
                setPreference(view, after);
            } catch (PreferencesException pe) {
                _logger.warn("Could not update view preference {}", view, pe);
                return false;
            }
        }
        // Commit all changes if and only if everything went fine.
        return status;
    }

    /**
     * Correction : fix Faint columns and update legend colors
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion21ToVersion22() {
        boolean status = true;

        Object[] viewNames = new Object[]{PreferenceKey.VIEW_DETAILED_FAINT_K};

        // For each views
        for (Object view : viewNames) {
            String before = getPreference(view);
            /*
            System.out.println("Updating view: " + view);
            System.out.println("Pref: " + before);      
             */
            if (!before.contains("UD_")) {
                status &= replaceTokenInPreference(view, " e_LDD_rel ", " e_LDD_rel UD_U UD_B UD_V UD_R UD_I UD_J UD_H UD_K ");
            }
            // System.out.println("After Pref: " + getPreference(view));      
        }

        viewNames = new Object[]{PreferenceKey.VIEW_SIMPLE_FAINT_K};

        // For each views
        for (Object view : viewNames) {
            String before = getPreference(view);
            /*
            System.out.println("Updating view: " + view);
            System.out.println("Pref: " + before);      
             */
            if (!before.contains("UD_")) {
                status &= replaceTokenInPreference(view, " e_LDD_rel ", " e_LDD_rel UD_V UD_J UD_H UD_K ");
            }
            // System.out.println("After Pref: " + getPreference(view));      
        }

        // Update legend colors and order
        try {
            setLegendColorsAndOrders(false);
            _logger.debug("Updated legend colors and order.");
        } catch (PreferencesException pe) {
            _logger.warn("Could updated legend colors and order:", pe);

            return false;
        }

        // Commit all changes if and only if everything went fine.
        return status;
    }

    /**
     * Correction : update legend colors
     *
     * @return always true to force Preference file write to disk with the new empty value as default.
     */
    private boolean updateFromVersion22ToVersion23() {
        boolean status = true;

        // Update legend colors and order
        try {
            setLegendColorsAndOrders(false);
            _logger.debug("Updated legend colors and order.");
        } catch (PreferencesException pe) {
            _logger.warn("Could updated legend colors and order:", pe);

            return false;
        }

        // Commit all changes if and only if everything went fine.
        return status;
    }
    
    public static void main(String[] args) {
        final Preferences prefs = Preferences.getInstance();

        final List<String> newCols = Arrays.asList(
                ("HD, HIP, DM, TYC1, TYC2, TYC3, 2MASS, WISE, "
                        + "RAJ2000, DEJ2000, GROUP_SIZE_5, pmRa, pmDec, plx, e_Plx, "
                        + "SpType, ObjTypes, SIMBAD, GroupSize, "
                        + "VarFlag1, VarFlag2, VarFlag3, MultFlag, SBC9, WDS, sep1, sep2, RadVel, RotVel, "
                        + "B, e_B, V, e_V, R, I, J, e_J, H, e_H, K, e_K, L, e_L, M, e_M, N, e_N, "
                        + "LDD, e_LDD, e_LDD_rel, diam_chi2, CalFlag, diamFlag, Teff_SpType, logg_SpType, "
                        + "UD_U, UD_B, UD_V, UD_R, UD_I, UD_J, UD_H, UD_K, UD_L, UD_M, UD_N, "
                        + "vis2, vis2Err, dist, SpType_JMMC, Name")
                        .split(", ")
        );

        Object[] viewNames = {PreferenceKey.VIEW_SIMPLE_BRIGHT_V, PreferenceKey.VIEW_DETAILED_BRIGHT_V,
                              PreferenceKey.VIEW_SIMPLE_BRIGHT_K, PreferenceKey.VIEW_DETAILED_BRIGHT_K,
                              PreferenceKey.VIEW_SIMPLE_FAINT_K, PreferenceKey.VIEW_DETAILED_FAINT_K,
                              PreferenceKey.VIEW_SIMPLE_BRIGHT_N, PreferenceKey.VIEW_DETAILED_BRIGHT_N};
        // For each views
        for (Object view : viewNames) {
            String before = prefs.getPreference(view);

            String[] oldCols = before.split(" ");

            System.out.println("Checking view: " + view);
            System.out.println("Pref: " + before);

            for (String col : oldCols) {
                if (!newCols.contains(col)) {
                    System.out.println("Deprecated column: " + col);
                }
            }
        }
    }
}
