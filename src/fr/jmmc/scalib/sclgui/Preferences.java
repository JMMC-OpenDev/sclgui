/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.36 2009-07-16 13:49:59 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.35  2009/07/15 12:53:43  lafrasse
 * Re-ordered default columns in detailled views.
 *
 * Revision 1.34  2009/05/04 12:04:32  lafrasse
 * Added hability to remove (ordered) preference.
 *
 * Revision 1.33  2009/04/22 15:17:06  lafrasse
 * Added spectral binary detection (from SBC9 catalog, with Multiplicity filter).
 *
 * Revision 1.32  2008/11/28 13:14:26  lafrasse
 * Changed queryMinMagnitudeDelta and queryMaxMagnitudeDelta respectively from -".0
 * and 4.0 to -2.0 and 2.0 .
 *
 * Revision 1.31  2008/11/19 15:01:24  lafrasse
 * Added missing F12 column in simple bright view for N band.
 *
 * Revision 1.30  2008/11/19 12:22:47  lafrasse
 * Corrected visibility column in detailled bright view for N band.
 *
 * Revision 1.29  2008/11/18 09:54:43  lafrasse
 * Corrected diameter columns in simple bright view for N band.
 *
 * Revision 1.28  2008/10/16 12:38:47  lafrasse
 * Jalopization.
 *
 * Revision 1.27  2008/09/22 20:54:13  lafrasse
 * Updated log and documntation.
 *
 * Revision 1.26  2008/09/19 08:13:14  lafrasse
 * Updated columns orders to more tightly match those described in the 'JMMC-SPE-2600-0001' document (as requested by Daniel BONNEAU).
 *
 * Revision 1.25  2008/09/10 22:31:30  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Moved to new JMCS APIs.
 *
 * Revision 1.24  2008/08/28 14:16:02  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Moved to new JMCS APIs.
 *
 * Revision 1.23  2008/05/30 12:44:12  lafrasse
 * Updated queryDiffRASize & queryDiffiDECSize default values to match previous
 * SearchCal version.
 *
 * Revision 1.22  2008/04/15 15:59:33  lafrasse
 * Changed RA unit to minutes and DEC unit to degrees.
 * Corrected auto radius checkbox behavior to be selected only when the associateds
 * textfield is enabled.
 * Corrected auto radius status loading to match preference state.
 * Changed default query preference to match the ASPRO default settings.
 *
 * Revision 1.21  2007/12/03 14:41:59  lafrasse
 * Removed science object inclusion preference.
 * Changed default query to match the one of the previous version.
 *
 * Revision 1.20  2007/10/09 14:41:04  lafrasse
 * Added handling of all different sets of columns order.
 *
 * Revision 1.19  2007/08/16 13:43:58  lafrasse
 * Updated preference file version number.
 *
 * Revision 1.18  2007/06/29 09:55:14  lafrasse
 * Changed default query to 'alf_Psa'.
 *
 * Revision 1.17  2007/06/14 08:39:25  lafrasse
 * Added true column selection for the simple/detailled views.
 *
 * Revision 1.16  2007/02/13 16:17:58  lafrasse
 * Jalopyzation.
 *
 * Revision 1.15  2007/02/13 16:15:51  lafrasse
 * Changed default query from ETA_TAU to Archenor.
 *
 * Revision 1.14  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.13  2006/11/07 17:14:48  lafrasse
 * Updated query default magnitudeBand to K and instrumentalWavelength to 1.0 .
 *
 * Revision 1.12  2006/10/10 09:09:37  lafrasse
 * Updated _logger APIs to more tightly reflect standard MCS 'log' module levels.
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
 * Revision 1.8  2006/09/15 14:19:11  lafrasse
 * Added query default values.
 *
 * Revision 1.7  2006/07/11 11:17:48  mella
 * Add scienceObject.include preference
 *
 * Revision 1.6  2006/06/22 12:32:21  lafrasse
 * Added legend view and detailed view status storage
 *
 * Revision 1.5  2006/06/19 11:24:51  mella
 * Add star property order entry with temporary value
 *
 * Revision 1.4  2006/06/09 13:37:37  mella
 * add default true help.tooltips.show preference
 *
 * Revision 1.3  2006/04/07 08:41:03  mella
 * Preferences singleton is accessed using Preferences.getInstance()
 *
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.util.*;

import java.util.Properties;
import java.util.logging.*;


/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class Preferences extends fr.jmmc.mcs.util.Preferences
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.Preferences");

    /** Singleton instance */
    private static Preferences _instance = null;

    /** Detailled bright N columns order list, as of default in preference version 3 */
    private static String _detailledBrightN_v3 = "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 orig F12 e_F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Calib MultFlag VarFlag3 V H plx e_Plx pmRa pmDec A_V Chi2 SpTyp_Teff";

    /** Detailled bright N columns order list, as of default in preference version 4 */
    private static String _detailledBrightN_v4 = "dist HD RAJ2000 DEJ2000 plx e_Plx pmRa pmDec vis2 vis2Err vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Dia12 e_dia12 orig F12 e_F12 N SpType Calib MultFlag VarFlag3 V H A_V Chi2 SpTyp_Teff";

    /** Detailled bright V columns order list, as of default in preference version 3 */
    private static String _detailledBrightV_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";

    /** Detailled bright V columns order list, as of default in preference version 4 */
    private static String _detailledBrightV_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";

    /** Detailled bright K columns order list, as of default in preference version 3 */
    private static String _detailledBrightK_v3 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";

    /** Detailled bright K columns order list, as of default in preference version 4 */
    private static String _detailledBrightK_v4 = "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av";

    /** Detailled faint K columns order list, as of default in preference version 3 */
    private static String _detailledFaintK_v3 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa GLAT GLON plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";

    /** Detailled faint K columns order list, as of default in preference version 4 */
    private static String _detailledFaintK_v4 = "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx GLAT GLON SpType VarFlag1 VarFlag2 VarFlag3 MultFlag SBC9 LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av";

    /** Hidden constructor */
    protected Preferences()
    {
        // Insert your singleton initialization code here
    }

    /**
     * Return the preference filename.
     */
    protected String getPreferenceFilename()
    {
        _logger.entering("Preferences", "getPreferenceFilename");

        return "fr.jmmc.searchcal.properties";
    }

    /**
     * Return the preference revision number.
     */
    protected int getPreferencesVersionNumber()
    {
        _logger.entering("Preferences", "getPreferencesVersionNumber");

        return 6;
    }

    /**
     * Hook to handle updates of older preference file version.
     *
     * @param loadedVersionNumber the version of the loaded preference file.
     *
     * @return should return true if the update went fine and new values should
     * be saved, false otherwise to automatically trigger default values load.
     */
    protected boolean updatePreferencesVersion(int loadedVersionNumber)
    {
        _logger.entering("Preferences", "updatePreferencesVersion");

        switch (loadedVersionNumber)
        {
        // Wrong column identifiers in the the simple and detailled bright N columns order list
        case 1:
            _logger.info(
                "Upgrading preference file from version 1 to version 2.");

            // Updating simple bright N columns order list
            String simpleBrightNViewColumnOrder = getPreference(
                    "view.columns.simple.bright.N");

            // Should replace "e_diam_vk" with "e_dia12 F12"
            _logger.finer(
                "Replacing 'e_diam_vk' with 'e_dia12 F12' in 'view.columns.simple.bright.N'.");
            simpleBrightNViewColumnOrder = simpleBrightNViewColumnOrder.replaceAll("e_diam_vk",
                    "e_dia12 F12");

            // Should replace "diam_vk" with "Dia12"
            _logger.finer(
                "Replacing 'diam_vk' with 'Dia12' in 'view.columns.simple.bright.N'.");
            simpleBrightNViewColumnOrder = simpleBrightNViewColumnOrder.replaceAll("diam_vk",
                    "Dia12");

            // Updating detailled bright N columns order list
            String detailledBrightNViewColumnOrder = getPreference(
                    "view.columns.detailled.bright.N");

            // Should replace "vis2Flag" with "vis2"
            _logger.finer(
                "Replacing 'vis2Flag' with 'vis2' in 'view.columns.detailled.bright.N'.");
            detailledBrightNViewColumnOrder = detailledBrightNViewColumnOrder.replaceAll("vis2Flag",
                    "vis2");

            // Store updated column order
            try
            {
                setPreference("view.columns.simple.bright.N",
                    simpleBrightNViewColumnOrder);
                setPreference("view.columns.detailled.bright.N",
                    detailledBrightNViewColumnOrder);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING,
                    "Could not store updated preference:", ex);

                return false;
            }

            // Commit change to file
            return true;

        // Added SBC9 column to each detailled view
        case 2:
            _logger.info(
                "Upgrading preference file from version 2 to version 3.");

            String preferenceToUpdate = "";

            // Insert "SBC9" after "MultFlag" in the each detailled columns order list
            String[] viewList = { "bright.V", "bright.K", "faint.K" };

            for (String viewName : viewList)
            {
                // Get the current columns order list content
                String completePreferencePath = "view.columns.detailled." +
                    viewName;
                preferenceToUpdate = getPreference(completePreferencePath);

                // Replace 'MultFlag' with 'MultFlag SBC9' in the current columns order list
                String searchedToken  = "MultFlag";
                String replacingToken = searchedToken + " " + "SBC9";
                preferenceToUpdate    = preferenceToUpdate.replaceAll(searchedToken,
                        replacingToken);
                _logger.finer("Replaced '" + searchedToken + "' with '" +
                    replacingToken + "' in '" + completePreferencePath + "'.");

                // Store the updated column order
                try
                {
                    setPreference(completePreferencePath, preferenceToUpdate);
                }
                catch (Exception ex)
                {
                    _logger.log(Level.WARNING,
                        "Could not store updated preference in '" +
                        completePreferencePath + "' : ", ex);

                    return false;
                }
            }

            // Commit change to file
            return true;

        // Remove CHARM color preference
        case 3:
            _logger.info(
                "Upgrading preference file from version 3 to version 4.");

            String preferenceToRemove = "catalog.color.J/A+A/386/492/charm";
            removePreference(preferenceToRemove);

            _logger.finer("Removed '" + preferenceToRemove + "' preference.");

            // Commit change to file
            return true;

        // Re-order detailled view columns
        case 4:
            _logger.info(
                "Upgrading preference file from version 4 to version 5.");
            // Updating detailled bright N columns order list if not changed by user
            detailledBrightNViewColumnOrder = getPreference(
                    "view.columns.detailled.bright.N");

            if (detailledBrightNViewColumnOrder.equals(_detailledBrightN_v3) == true)
            {
                detailledBrightNViewColumnOrder = _detailledBrightN_v4;
                _logger.finer(
                    "Re-ordered detailled bright N columns order list.");
            }
            else
            {
                _logger.finer(
                    "Lived customized detailled bright N columns order list unchanged.");
            }

            // Updating detailled bright V columns order list if not changed by user
            String detailledBrightVViewColumnOrder = getPreference(
                    "view.columns.detailled.bright.V");

            if (detailledBrightVViewColumnOrder.equals(_detailledBrightV_v3) == true)
            {
                detailledBrightVViewColumnOrder = _detailledBrightV_v4;
                _logger.finer(
                    "Re-ordered detailled bright V columns order list.");
            }
            else
            {
                _logger.finer(
                    "Lived customized detailled bright V columns order list unchanged.");
            }

            // Updating detailled bright K columns order list if not changed by user
            String detailledBrightKViewColumnOrder = getPreference(
                    "view.columns.detailled.bright.K");

            if (detailledBrightKViewColumnOrder.equals(_detailledBrightK_v3) == true)
            {
                detailledBrightKViewColumnOrder = _detailledBrightK_v4;
                _logger.finer(
                    "Re-ordered detailled bright K columns order list.");
            }
            else
            {
                _logger.finer(
                    "Lived customized detailled bright K columns order list unchanged.");
            }

            // Updating detailled faint K columns order list if not changed by user
            String detailledFaintKViewColumnOrder = getPreference(
                    "view.columns.detailled.faint.K");

            if (detailledFaintKViewColumnOrder.equals(_detailledFaintK_v3) == true)
            {
                detailledFaintKViewColumnOrder = _detailledFaintK_v4;
                _logger.finer(
                    "Re-ordered detailled faint K columns order list.");
            }
            else
            {
                _logger.finer(
                    "Lived customized detailled faint K columns order list unchanged.");
            }

            // Store updated column order
            try
            {
                setPreference("view.columns.detailled.bright.N",
                    detailledBrightNViewColumnOrder);
                setPreference("view.columns.detailled.bright.V",
                    detailledBrightVViewColumnOrder);
                setPreference("view.columns.detailled.bright.K",
                    detailledBrightKViewColumnOrder);
                setPreference("view.columns.detailled.faint.K",
                    detailledFaintKViewColumnOrder);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING,
                    "Could not store updated preference:", ex);

                return false;
            }

            // Commit change to file
            return true;

        // Replaced VarFlag3 with VFlag in bright N detailled view
        case 5:
            _logger.info(
                "Upgrading preference file from version 5 to version 6.");

            String completePreferencePath = "view.columns.detailled.bright.N";
            preferenceToUpdate = getPreference(completePreferencePath);

            // Replace 'VarFlag3' with 'VFlag' in the current columns order list
            String searchedToken  = "VarFlag3";
            String replacingToken = "VFlag";
            preferenceToUpdate = preferenceToUpdate.replaceAll(searchedToken,
                    replacingToken);
            _logger.finer("Replaced '" + searchedToken + "' with '" +
                replacingToken + "' in '" + completePreferencePath + "'.");

            // Store the updated column order
            try
            {
                setPreference(completePreferencePath, preferenceToUpdate);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING,
                    "Could not store updated preference in '" +
                    completePreferencePath + "' : ", ex);

                return false;
            }

            // Commit change to file
            return true;

        // By default, triggers default values load.
        default:
            return false;
        }
    }

    /** Set preferences default values */
    protected void setDefaultPreferences() throws PreferencesException
    {
        _logger.entering("Preferences", "setDefaultPreferences");

        // Place catalog origin colors
        setDefaultPreference("catalog.color.I/280", "#FFB6B6");
        setDefaultPreference("catalog.color.I/284", "#F1FB58");
        setDefaultPreference("catalog.color.II/225/catalog", "#F6B6FF");
        setDefaultPreference("catalog.color.II/7A/catalog", "#B9B6FF");
        setDefaultPreference("catalog.color.II/246/out", "#B6E8FF");
        setDefaultPreference("catalog.color.V/50/catalog", "#B6FFE6");
        setDefaultPreference("catalog.color.J/A+A/433/1155", "#C89292");
        setDefaultPreference("catalog.color.J/A+A/431/773/charm2", "#B7FF5A");
        setDefaultPreference("catalog.color.B/denis", "#FFF4B6");
        setDefaultPreference("catalog.color.J/A+A/413/1037", "#FFFADD");
        setDefaultPreference("catalog.color.I/196/main", "#78FB8B");
        setDefaultPreference("catalog.color.J/A+A/393/183/catalog", "#9778FB");
        setDefaultPreference("catalog.color.MIDI", "#C994CA");
        setDefaultPreference("catalog.color.V/36B/bsc4s", "#88A0A6");
        setDefaultPreference("catalog.color.B/sb9/main", "#A688A0");

        // Place confidence indexes color
        int i = 0;
        setDefaultPreference("confidence.color.LOW", i++, "#6E6E6E");
        setDefaultPreference("confidence.color.MEDIUM", i++, "#D8D8D8");
        setDefaultPreference("confidence.color.HIGH", i++, "#EFEFEF");

        // Place help behaviour
        setDefaultPreference("help.tooltips.show", "true");

        // Place view behaviour
        setDefaultPreference("view.legend.show", "false");
        setDefaultPreference("view.details.show", "false");

        // Simple 'Bright N' view
        setDefaultPreference("view.columns.simple.bright.N",
            "dist HD RAJ2000 DEJ2000 vis2 vis2Err Dia12 e_dia12 F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu)");
        // Detailled 'Bright N' view
        setDefaultPreference("view.columns.detailled.bright.N",
            _detailledBrightN_v4);

        // Simple 'Bright V' view
        setDefaultPreference("view.columns.simple.bright.V",
            "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType B V R I");
        // Detailled 'Bright V' view
        setDefaultPreference("view.columns.detailled.bright.V",
            _detailledBrightV_v4);

        // Simple 'Bright K' view
        setDefaultPreference("view.columns.simple.bright.K",
            "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType V J H K");
        // Detailled 'Bright K' view
        setDefaultPreference("view.columns.detailled.bright.K",
            _detailledBrightK_v4);

        // Simple 'Faint K' view
        setDefaultPreference("view.columns.simple.faint.K",
            "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_mean e_diam_mean V Icous J H K");
        // Detailled 'Faint K' view
        setDefaultPreference("view.columns.detailled.faint.K",
            _detailledFaintK_v4);

        // Query default values preferences
        setDefaultPreference("query.magnitudeBand", "V");
        setDefaultPreference("query.instrumentalWavelength", "1.0");
        setDefaultPreference("query.instrumentalMaxBaseLine", "102.45");
        setDefaultPreference("query.scienceObjectName", "ETA_TAU");
        setDefaultPreference("query.scienceObjectRA", "03:47:29.0765");
        setDefaultPreference("query.scienceObjectDEC", "24:06:18.494");
        setDefaultPreference("query.scienceObjectMagnitude", "0");
        setDefaultPreference("query.queryMinMagnitude", "2.0");
        setDefaultPreference("query.queryMaxMagnitude", "4.0");
        setDefaultPreference("query.queryBrightScenarioFlag", "true");
        setDefaultPreference("query.queryDiffRASize", "3600");
        setDefaultPreference("query.queryDiffDECSize", "1200");
        setDefaultPreference("query.queryRadialSize", "5.0");
        setDefaultPreference("query.queryAutoRadius", "true");

        setDefaultPreference("query.scienceObjectDetectionDistance", "0.01");
        setDefaultPreference("query.queryMinMagnitudeDelta", "-2.0");
        setDefaultPreference("query.queryMaxMagnitudeDelta", "2.0");
    }

    /** Return the singleton instance */
    public static final synchronized Preferences getInstance()
    {
        // DO NOT MODIFY !!!
        if (_instance == null)
        {
            _instance = new Preferences();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }
}
