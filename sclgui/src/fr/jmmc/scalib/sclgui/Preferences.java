/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.2 2006-03-31 08:53:20 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

    /**
     * Creates a new Preferences object.
     */
    Preferences()
    {
        super(_shortPreferenceFilename);

        Properties myDefaultProperties = new Properties();
        myDefaultProperties.put("scalib.version", "1.0");

        // Place catalog origin colors
        myDefaultProperties.put("scalib.version", "1.0");
        myDefaultProperties.put("catalog.color.I/280", "#FFB6B6");
        myDefaultProperties.put("catalog.color.I/284", "#F1FB58");
        myDefaultProperties.put("catalog.color.II/225/catalog", "#F6B6FF");
        myDefaultProperties.put("catalog.color.II/7A/catalog", "#B9B6FF");
        myDefaultProperties.put("catalog.color.II/246/out", "#B6E8FF");
        myDefaultProperties.put("catalog.color.V/50/catalog", "#B6FFE6");
        myDefaultProperties.put("catalog.color.J/A+A/433/1155", "#C89292");
        myDefaultProperties.put("catalog.color.J/A+A/386/492/charm", "#DFFFB6");
        myDefaultProperties.put("catalog.color.J/A+A/431/773/charm2", "#B7FF5A");
        myDefaultProperties.put("catalog.color.B/denis", "#FFF4B6");
        myDefaultProperties.put("catalog.color.J/A+A/413/1037", "#FFFADD");
        myDefaultProperties.put("catalog.color.I/196/main", "#78FB8B");
        myDefaultProperties.put("catalog.color.J/A+A/393/183/catalog", "#9778FB");
        myDefaultProperties.put("catalog.color.MIDI", "#C994CA");
        myDefaultProperties.put("catalog.color.V/36B/bsc4s", "#88A0A6");

        /* Place confidence indexes color */
        myDefaultProperties.put("confidence.color.LOW", "#6E6E6E");
        myDefaultProperties.put("confidence.color.MEDIUM", "#D8D8D8");
        myDefaultProperties.put("confidence.color.HIGH", "#EFEFEF");

        setDefaultPreferences(myDefaultProperties);
        _shortPreferenceFilename = "fr.jmmc.scalib.sclgui.properties";

        loadFromFile();
    }
}
