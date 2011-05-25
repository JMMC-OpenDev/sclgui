/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import javax.swing.JApplet;

/**
 * Facility to enable applet-mode usage of SearchCalibrators
 */
public class SearchCalibratorsApplet extends JApplet {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** Applet initialization */
    @Override
    public void init() {
        // Launch the SearchClibrators application
        SearchCalibrators searchCalibrators = new SearchCalibrators(null);
    }
}
