/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.9 2006-09-28 15:23:29 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
package jmmc.scalib.sclgui;

import java.util.Properties;


/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class Preferences extends jmmc.mcs.util.Preferences
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
                defaults.setPreference("scalib.version", "1.0");

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

                /* Place confidence indexes color */
                int i = 0;
                defaults.setPreference("confidence.color.LOW", i++, "#6E6E6E");
                defaults.setPreference("confidence.color.MEDIUM", i++, "#D8D8D8");
                defaults.setPreference("confidence.color.HIGH", i++, "#EFEFEF");

                /* Place help behaviour */
                defaults.setPreference("help.tooltips.show", "true");

                /* Place view behaviour */
                defaults.setPreference("view.legend.show", "false");
                defaults.setPreference("view.details.show", "false");

                /* Place star properties order */
                defaults.setPreference("star.properties.order", "HD HIP RAJ2000");

                /* query default values preferences  */
                defaults.setPreference("query.magnitudeBand", "V");
                defaults.setPreference("query.instrumentalWavelength", "1978.0");
                defaults.setPreference("query.instrumentalMaxBaseLine", "102.45");
                defaults.setPreference("query.scienceObjectName", "eta_tau");
                defaults.setPreference("query.scienceObjectRA", "+03:47:29.79");
                defaults.setPreference("query.scienceObjectDEC", "+24:06:18.50");
                defaults.setPreference("query.scienceObjectMagnitude", "0.0");
                defaults.setPreference("query.scienceObjectInclusionFlag",
                    "true");
                defaults.setPreference("query.queryMinMagnitude", "2.0");
                defaults.setPreference("query.queryMaxMagnitude", "4.0");
                defaults.setPreference("query.queryBrightScenarioFlag", "true");
                defaults.setPreference("query.queryDiffRASize", "60.0");
                defaults.setPreference("query.queryDiffDECSize", "5.0");
                defaults.setPreference("query.queryRadialSize", "0.0");
            }
            catch (Exception e)
            {
            }

            _singleton.setDefaultPreferences(defaults);
            _singleton.loadFromFile();
        }

        return _singleton;
    }
}
