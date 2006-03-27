/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
        setDefaultPreferences(myDefaultProperties);
        _shortPreferenceFilename = "fr.jmmc.scalib.sclgui.properties";

        loadFromFile();
    }
}
