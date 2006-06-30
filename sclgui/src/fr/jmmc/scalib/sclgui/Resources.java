/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Resources.java,v 1.6 2006-06-30 12:32:03 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2006/06/30 07:56:44  lafrasse
 * Corrected a bug preventing compilation
 *
 * Revision 1.4  2006/06/26 16:06:00  mella
 * Remove last space character for xpath queries
 *
 * Revision 1.3  2006/06/26 14:29:08  mella
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/23 09:19:41  mella
 * Jalopization
 *
 * Revision 1.1  2006/06/09 15:15:37  mella
 * First revision
 *
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.io.*;

import java.net.URL;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;

import javax.xml.parsers.*;


/**
 * Class used to get resources informations from one central point (xml file).
 */
public class Resources
{
    /** resource filename */
    private static String _resourceName = "Resources.prop";

    /** logger */
    private static Logger _logger = MCSLogger.getLogger();

    /** Properties */
    private static Properties _resources = null;

    /**
     * Get content from resource file.
     *
     * @param resourceName name of resource
     *
     * @return the content of the resource or null indicating error
     */
    public static String getResource(String resourceName)
    {
        MCSLogger.trace();

        if (_resources == null)
        {
            try
            {
                URL resourcesURL = Resources.class.getResource(_resourceName);
                _logger.finest("Using resource file named:" + resourcesURL);

                FileInputStream in = new FileInputStream(new File(
                            resourcesURL.getFile()));
                _resources = new Properties();
                _resources.load(in);
            }
            catch (Exception e)
            {
                _logger.warning("Resource File can't be read");

                return null;
            }
        }

        _logger.fine("getResource for " + resourceName);

        return _resources.getProperty(resourceName);
    }

    /**
     * Get the text of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated text
     */
    public static String getActionText(String actionName)
    {
        return getResource("actions.action." + actionName + ".text");
    }

    /**
     * Get the description of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated description
     */
    public static String getActionDescription(String actionName)
    {
        return getResource("actions.action." + actionName + ".description");
    }

    /**
     * Get the icon path of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated icon path
     */
    public static String getActionIconPath(String actionName)
    {
        return getResource("actions.action." + actionName + ".icon");
    }

    /**
     * Get the icon  of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated icon
     */
    public static ImageIcon getActionIcon(String actionName)
    {
        String    iconPath  = getActionIconPath(actionName);
        ImageIcon imageIcon = createImageIcon(iconPath,
                "Icon of action '" + actionName + "'");

        return imageIcon;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path, String description)
    {
        java.net.URL imgURL = Resources.class.getResource("./" + path);

        if (imgURL != null)
        {
            return new ImageIcon(imgURL, description);
        }
        else
        {
            _logger.warning("Couldn't find file: '" + path + "'");

            return null;
        }
    }

    /**
     * Get the tooltip text of widget related to the common widget group.
     *
     * @param widgetName the widgetInstanceName
     *
     * @return the tooltip text
     */
    public static String getToolTipText(String widgetName)
    {
        return getResource("widgets.widget." + widgetName + ".tooltip");
    }
}
/*___oOo___*/
