/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalibratorsApplet.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import javax.swing.JApplet;


/**
 * Facility to enable applet-mode usage of SearchCalibrators
 */
public class SearchCalibratorsApplet extends JApplet
{
    /**
     * Applet initialization
     */
    public void init()
    {
        // Launch the SearchClibrators application
        SearchCalibrators searchCalibrators = new SearchCalibrators();
    }
}
