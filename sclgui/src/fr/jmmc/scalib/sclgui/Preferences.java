/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.20 2007-10-09 14:41:04 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
 * Updated MCSLogger APIs to more tightly reflect standard MCS 'log' module levels.
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

import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import java.util.Properties;


/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class Preferences extends fr.jmmc.mcs.util.Preferences
{
    /** Preference file name */
    static String _shortPreferenceFilename = "fr.jmmc.scalib.sclgui.properties";

    /** Singleton instance */
    private static Preferences _singleton = null;

    /**
     * Privatized constructor that must be empty.
     */
    private Preferences()
    {
    }

    /**
     * Return the singleton instance of Preferences.
     *
     * @return the singleton preference instance
     */
    public static Preferences getInstance()
    {
        MCSLogger.trace();

        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null)
        {
            _singleton = new Preferences();
            _singleton.setShortPreferenceFilename(_shortPreferenceFilename);
            _singleton.loadFromFile();

            Preferences defaults = new Preferences();

            try
            {
                // Store preference file version number
                defaults.setPreference("scalib.version", "4.0");

                // Place catalog origin colors
                defaults.setPreference("catalog.color.I/280", "#FFB6B6");
                defaults.setPreference("catalog.color.I/284", "#F1FB58");
                defaults.setPreference("catalog.color.II/225/catalog", "#F6B6FF");
                defaults.setPreference("catalog.color.II/7A/catalog", "#B9B6FF");
                defaults.setPreference("catalog.color.II/246/out", "#B6E8FF");
                defaults.setPreference("catalog.color.V/50/catalog", "#B6FFE6");
                defaults.setPreference("catalog.color.J/A+A/433/1155", "#C89292");
                defaults.setPreference("catalog.color.J/A+A/386/492/charm",
                    "#DFFFB6");
                defaults.setPreference("catalog.color.J/A+A/431/773/charm2",
                    "#B7FF5A");
                defaults.setPreference("catalog.color.B/denis", "#FFF4B6");
                defaults.setPreference("catalog.color.J/A+A/413/1037", "#FFFADD");
                defaults.setPreference("catalog.color.I/196/main", "#78FB8B");
                defaults.setPreference("catalog.color.J/A+A/393/183/catalog",
                    "#9778FB");
                defaults.setPreference("catalog.color.MIDI", "#C994CA");
                defaults.setPreference("catalog.color.V/36B/bsc4s", "#88A0A6");

                // Place confidence indexes color
                int i = 0;
                defaults.setPreference("confidence.color.LOW", i++, "#6E6E6E");
                defaults.setPreference("confidence.color.MEDIUM", i++, "#D8D8D8");
                defaults.setPreference("confidence.color.HIGH", i++, "#EFEFEF");

                // Place help behaviour
                defaults.setPreference("help.tooltips.show", "true");

                // Place view behaviour
                defaults.setPreference("view.legend.show", "false");
                defaults.setPreference("view.details.show", "false");

                // Simple 'Bright N' view
                defaults.setPreference("view.columns.simple.bright.N",
                    "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu)");
                // Detailled 'Bright N' view
                defaults.setPreference("view.columns.detailled.bright.N",
                    "dist HD RAJ2000 DEJ2000 vis2Flag vis2Err Dia12 e_dia12 orig F12 e_F12 SpType N vis2(8mu) vis2Err(8mu) vis2(13mu) vis2Err(13mu) Calib MultFlag VarFlag3 V H plx e_Plx pmRa pmDec A_V Chi2 SpTyp_Teff");

                // Simple 'Bright V' view
                defaults.setPreference("view.columns.simple.bright.V",
                    "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType V J H K");
                // Detailled 'Bright V' view
                defaults.setPreference("view.columns.detailled.bright.V",
                    "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av");

                // Simple 'Bright K' view
                defaults.setPreference("view.columns.simple.bright.K",
                    "dist HD RAJ2000 DEJ2000 vis2 vis2Err diam_vk e_diam_vk SpType V J H K");
                // Detailled 'Bright K' view
                defaults.setPreference("view.columns.detailled.bright.K",
                    "dist vis2 vis2Err diam_bv diam_vr diam_vk e_diam_vk HIP HD DM RAJ2000 DEJ2000 pmDec pmRa plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag GLAT GLON RadVel RotVel LD e_LD UD e_UD Meth lambda UDDK e_UDDK B V R I J H K L M N Av");

                // Simple 'Faint K' view
                defaults.setPreference("view.columns.simple.faint.K",
                    "dist 2MASS RAJ2000 DEJ2000 vis2 vis2Err diam_mean e_diam_mean V Vphg I Icous Iphg J Jcous H Hcous K Kcous");
                // Detailled 'Bright K' view
                defaults.setPreference("view.columns.detailled.faint.K",
                    "dist vis2 vis2Err diam_vk diam_ij diam_ik diam_jh diam_jk diam_hk diam_mean e_diam_mean 2MASS DENIS TYC1 TYC2 TYC3 HIP HD DM RAJ2000 DEJ2000 pmRa pmDec GLAT GLON plx SpType VarFlag1 VarFlag2 VarFlag3 MultFlag LD e_LD UD e_UD Meth B Bphg V Vphg Rphg I Icous Iphg J Jcous H Hcous K Kcous Av");

                /* Query default values preferences  */
                defaults.setPreference("query.magnitudeBand", "K");
                defaults.setPreference("query.instrumentalWavelength", "2.2");
                defaults.setPreference("query.instrumentalMaxBaseLine", "102.45");
                defaults.setPreference("query.scienceObjectName", "alf_PsA");
                defaults.setPreference("query.scienceObjectRA", "22:57:39.047");
                defaults.setPreference("query.scienceObjectDEC", "-29:37:20.050");
                defaults.setPreference("query.scienceObjectMagnitude", "0.83");
                defaults.setPreference("query.scienceObjectInclusionFlag",
                    "true");
                defaults.setPreference("query.scienceObjectDetectionDistance",
                    "0.01");
                defaults.setPreference("query.queryMinMagnitude", "0.5");
                defaults.setPreference("query.queryMaxMagnitude", "2");
                defaults.setPreference("query.queryBrightScenarioFlag", "true");
                defaults.setPreference("query.queryDiffRASize", "3600");
                defaults.setPreference("query.queryDiffDECSize", "1200");
                defaults.setPreference("query.queryRadialSize", "0.0");

                defaults.setPreference("query.queryMinMagnitudeDelta", "-3.0");
                defaults.setPreference("query.queryMaxMagnitudeDelta", "4.0");
            }
            catch (Exception e)
            {
                MCSLogger.debug("Default preference values creation FAILED.");
            }

            _singleton.setDefaultPreferences(defaults);
            _singleton.loadFromFile();
        }

        return _singleton;
    }
}
