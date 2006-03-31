/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LegendView.java,v 1.3 2006-03-31 08:53:20 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/30 13:41:28  yvander
 * Mise en place des couleurs
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.util.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.Vector;

import javax.swing.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.TableCellRenderer;


// TODO handle close button correctly

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class LegendView extends JFrame implements Observer
{
    /**
     * DOCUMENT ME!
     */
    JList catalogList;

    /**
     * DOCUMENT ME!
     */
    JList confidenceList;

    /**
     * DOCUMENT ME!
     */
    Preferences _preferences;

    /**
     * LegendFrame  -  Constructor
     * @param title String
     */
    public LegendView(Preferences preferences)
    {
        super("Legend");

        // Store the application preferences and register against it
        _preferences = preferences;
        _preferences.addObserver(this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Get catalog and confidence list 
        JLabel catalogLabel = new JLabel("Catalog origin : ");
        catalogList = new JList();

        JLabel confidenceLabel = new JLabel("Confidence index :");
        confidenceList = new JList();

        // Place elements into main panel
        JPanel paneldata = new JPanel();
        paneldata.setLayout(new BoxLayout(paneldata, BoxLayout.Y_AXIS));
        paneldata.add(catalogLabel);
        paneldata.add(catalogList);
        paneldata.add(confidenceLabel);
        paneldata.add(confidenceList);
        getContentPane().add(paneldata);

        // force to load Preferences at first moment
        update(_preferences, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
     */
    public void update(Observable o, Object arg)
    {
        if (o.equals(_preferences))
        {
            // Fill catalog List
            String           prefix       = "catalog.color.";
            Enumeration      e            = _preferences.getPreferences(prefix);
            DefaultListModel catalogModel = new DefaultListModel();

            while (e.hasMoreElements())
            {
                String entry       = (String) e.nextElement();
                String catalogName = entry.substring(prefix.length());
                catalogModel.addElement(catalogName);
            }

            catalogList.setModel(catalogModel);

            CellColors catalogRenderer = new CellColors(_preferences, prefix);
            catalogList.setCellRenderer(catalogRenderer);

            // Fill confidence List
            prefix     = "confidence.color.";
            e          = _preferences.getPreferences(prefix);

            DefaultListModel confidenceModel = new DefaultListModel();

            while (e.hasMoreElements())
            {
                String entry     = (String) e.nextElement();
                String entryName = entry.substring(prefix.length());
                confidenceModel.addElement(entryName);
            }

            confidenceList.setModel(confidenceModel);

            CellColors confidenceRenderer = new CellColors(_preferences, prefix);
            confidenceList.setCellRenderer(confidenceRenderer);

            // GUI has been modified then pack window
            pack();
        }
    }
}


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
class CellColors extends DefaultListCellRenderer
{
    /**
     * DOCUMENT ME!
     */
    Vector _colors;

    /**
     * Creates a new CellColors object.
     *
     * @param preferences DOCUMENT ME!
     * @param prefix DOCUMENT ME!
     */
    public CellColors(Preferences preferences, String prefix)
    {
        super();
        _colors = new Vector();

        // read colors preferences of prefix group
        Enumeration e = preferences.getPreferences(prefix);

        while (e.hasMoreElements())
        {
            String entry = (String) e.nextElement();

            try
            {
                Color oneColor = preferences.getPreferenceAsColor(entry);
                _colors.add(oneColor);
            }
            catch (PreferencesException ex)
            {
                // TODO log as error instead of stderr...
                ex.printStackTrace();
            }
        }
    }

    /**
     * getListCellRendererComponent  -  return the component with renderer (List)
     * @param list JList
     * @param value Object
     * @param index int
     * @param isSelected boolean
     * @param cellHasFocus boolean
     * @return Component
     */
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);

        setBackground((Color) _colors.elementAt(index));

        return this;
    }
}
