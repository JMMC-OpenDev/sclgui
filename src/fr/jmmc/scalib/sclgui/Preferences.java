/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.28 2008-10-16 12:38:47 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

        return 1;
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
        setDefaultPreference("catalog.color.J/A+A/386/492/charm", "#DFFFB6");
        setDefaultPreference("catalog.color.J/A+A/431/773/charm2", "#B7FF5A");
        setDefaultPreference("catalog.color.B/denis", "#FFF4B6");
        setDefaultPreference("catalog.color.J/A+A/413/1037", "#FFFADD");
        setDefaultPreference("catalog.color.I/196/main", "#78FB8B");
        setDefaultPreference("catalog.color.J/A+A/393/183/catalog", "#9778FB");
        setDefaultPreference("catalog.color.MIDI", "#C994CA");
        setDefaultPreference("catalog.color.V/36B/bsc4s", "#88A0A6");

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
            "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu)");
        // Detailled 'Bright N' view
        setDefaultPreference("view.columns.detailled.bright.N",
            "dist HD RAJ2000 DEJ2000 vis2Flag vis2Err Dia12 e_dia12 orig F12 e_F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Calib MultFlag VarFlag3 V H plx e_Plx pmRa pmDec A_V Chi2 SpTyp_Teff");

        // Simple 'Bright V' view
        setDefaultPreference("view.columns.simple.bright.V",
            "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType B V R I");
        // Detailled 'Bright V' view
        setDefaultPreference("view.columns.detailled.bright.V",
            "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av");

        // Simple 'Bright K' view
        setDefaultPreference("view.columns.simple.bright.K",
            "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType V J H K");
        // Detailled 'Bright K' view
        setDefaultPreference("view.columns.detailled.bright.K",
            "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av");

        // Simple 'Faint K' view
        setDefaultPreference("view.columns.simple.faint.K",
            "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_mean e_diam_mean V Icous J H K");
        // Detailled 'Bright K' view
        setDefaultPreference("view.columns.detailled.faint.K",
            "dist vis2 vis2Err diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmDec pmRa GLAT GLON plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag LD e_LD UD e_UD Meth lambda B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av");

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
        setDefaultPreference("query.queryMinMagnitudeDelta", "-3.0");
        setDefaultPreference("query.queryMaxMagnitudeDelta", "4.0");
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
