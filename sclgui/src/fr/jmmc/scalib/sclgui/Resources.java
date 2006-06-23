/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Resources.java,v 1.2 2006-06-23 09:19:41 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import javax.xml.parsers.*;


/**
 * Class used to get resources informations from one central point (xml file).
 */
public class Resources
{
    /** xml document */
    private static Document _document = null;

    /** xml filename */
    private static String _resourceName = "Resources.xml";

    /** logger */
    private static Logger _logger = MCSLogger.getLogger();

    /**
     * Constructor.
     *
     */
    public Resources()
    {
    }

    /**
     * Load xml file to init the _document instance.
     */
    private static void loadDocument()
    {
        MCSLogger.trace();

        URL _resourcesURL = Resources.class.getResource(_resourceName);
        _logger.finest("Using resource file named:" + _resourcesURL);

        try
        {
            // Create a builder factory whitout validation
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);

            // Create the builder and parse the URL
            _document = factory.newDocumentBuilder().parse("" + _resourcesURL);
        }
        catch (SAXException e)
        {
            // A parsing error occurred; the xml input  is not valid
        }
        catch (ParserConfigurationException e)
        {
        }
        catch (IOException e)
        {
        }
    }

    /**
     * Get content from resource file using xpath.
     *
     * @param xpath query used to dig resource
     *
     * @return the content of the xpath response
     *
     */
    private static String getFromXpath(String xpath)
    {
        MCSLogger.trace();

        if (_document == null)
        {
            loadDocument();
        }

        _logger.finest("Searching resource using xpath '" + xpath + "'");

        try
        {
            // Get the matching elements
            NodeList     nodelist = org.apache.xpath.XPathAPI.selectNodeList(_document,
                    xpath);
            StringBuffer sb       = new StringBuffer();

            // Process the elements in the nodelist
            for (int i = 0; i < nodelist.getLength(); i++)
            {
                // Get element
                CharacterData elem = (CharacterData) nodelist.item(i);
                _logger.finest("xpath returns '" + elem + "'");
                sb.append("" + elem.getData() + " ");
            }

            return sb.toString();
        }
        catch (javax.xml.transform.TransformerException e)
        {
            _logger.warning("" + e);

            return "";
        }
    }

    /**
     * Get content from resource file.
     *
     * @param resourceName xpath query used to dig resource
     *
     * @return the content of the xpath response
     */
    public static String getResource(String resourceName)
    {
        MCSLogger.trace();

        return getFromXpath(resourceName);
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
        return getToolTipText(widgetName, "common");
    }

    /**
     * Get the tooltip text of widget related to a specific widget group.
     *
     * @param widgetName the widgetInstanceName
     * @param widgetGroupName the widget group name
     *
     * @return the tooltip text
     */
    public static String getToolTipText(String widgetName,
        String widgetGroupName)
    {
        MCSLogger.trace();

        String xpath = "//widgets[./@name='" + widgetGroupName +
            "']/widget[./name='" + widgetName + "']/tooltip/text()";

        return getFromXpath(xpath);
    }
}
/*___oOo___*/
