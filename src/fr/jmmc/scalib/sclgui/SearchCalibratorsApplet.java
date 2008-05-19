/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalibratorsApplet.java,v 1.3 2008-05-19 15:39:29 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

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
        SearchCalibrators searchCalibrators = new SearchCalibrators(null);
    }
}
