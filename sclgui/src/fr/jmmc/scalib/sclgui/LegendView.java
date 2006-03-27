/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LegendView.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

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
    JPanel paneldata = new JPanel();

    /**
     * DOCUMENT ME!
     */
    JList listcolors;

    /**
     * DOCUMENT ME!
     */
    JList listindex;

    /**
     * DOCUMENT ME!
     */
    ListModel modelcolors;

    /**
     * DOCUMENT ME!
     */
    ListModel modelindex;

    /**
     * DOCUMENT ME!
     */
    CellColors cell = new CellColors();

    /**
     * DOCUMENT ME!
     */
    JLabel labelcolors = new JLabel();

    /**
     * DOCUMENT ME!
     */
    JLabel labelindex = new JLabel();

    /**
     * DOCUMENT ME!
     */
    JScrollPane scrollcolors;

    /**
     * DOCUMENT ME!
     */
    JScrollPane scrollindex;

    /**
     * LegendFrame  -  Constructor
     * @param title String
     */
    public LegendView()
    {
        super("Legend");

        try
        {
//            UIManager.setLookAndFeel(new MetalLookAndFeel());

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(new Dimension(600, 300));
            setResizable(false);

            //CATALOG ORIGIN
            Vector data = new Vector();
            data.add("2 MASS");
            data.add("DENIS");
            data.add("I280");
            data.add("BORDE");
            modelcolors     = new ListModel(data);
            listcolors      = new JList(modelcolors);
            listcolors.setCellRenderer(cell);
            labelcolors.setText("Catalog origin : ");
            paneldata.add(labelcolors);
            scrollcolors = new JScrollPane(listcolors);
            scrollcolors.setPreferredSize(new Dimension(90, 120));
            paneldata.add(scrollcolors);

            //CONFINDENCE INDEX
            data = new Vector();
            data.add("low");
            data.add("medium");
            data.add("hight");
            modelindex     = new ListModel(data);
            listindex      = new JList(modelindex);
            listindex.setCellRenderer(cell);
            labelindex.setText("Confidence index : ");
            paneldata.add(labelindex);
            scrollindex = new JScrollPane(listindex);
            scrollindex.setPreferredSize(new Dimension(90, 60));
            paneldata.add(scrollindex);
            paneldata.setLayout(new BoxLayout(paneldata, BoxLayout.Y_AXIS));

            getContentPane().add(paneldata);
            pack();
        }
/*        catch (UnsupportedLookAndFeelException exc)
        {
            exc.printStackTrace();
        }
*/        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     * @param arg DOCUMENT ME!
     */
    public void update(Observable o, Object arg)
    {
        // TODO : M�J des champs de la fenetre
    }
}


/**
 * <p>Titre : </p>
 *
 * <p>Description : </p>
 *
 * <p>Copyright : Copyright (c) 2006</p>
 *
 * <p>Société : </p>
 *
 * @author non attribuable
 * @version 1.0
 */
class CellColors extends DefaultListCellRenderer implements TableCellRenderer
{
    /**
     * DOCUMENT ME!
     */
    Vector vectColors;

    /**
     * DOCUMENT ME!
     */
    Vector vectConfidence;

    /**
     * CellColors  -  Constructor
     */
    public CellColors()
    {
        super();
        vectColors = new Vector();
        vectColors.add(Color.blue);
        vectColors.add(Color.orange);
        vectColors.add(Color.green);
        vectColors.add(Color.red);

        vectConfidence = new Vector();
        vectConfidence.add(Color.white);
        vectConfidence.add(Color.gray);
        vectConfidence.add(Color.darkGray);
    } //end CellColors

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

        if (list.getModel().getSize() == 3) //Confidence Index
        {
            setBackground((Color) vectConfidence.get(index));
        }
        else
        { //Catalog origin
            setBackground((Color) vectColors.get(index));
        }

        return this;
    } //end getListCellRendererComponent

    /**
     * getTableCellRendererComponent  -  return the component with renderer (Table)
     * @param table JTable
     * @param value Object
     * @param isSelected boolean
     * @param hasFocus boolean
     * @param row int
     * @param column int
     * @return Component
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        return this;
    }
}


/**
 * <p>Titre : </p>
 *
 * <p>Description : </p>
 *
 * <p>Copyright : Copyright (c) 2006</p>
 *
 * <p>Société : </p>
 *
 * @author non attribuable
 * @version 1.0
 */
class ListModel extends DefaultListModel
{
    /**
     * ListModel  -  Constructor
     * @param data Vector
     */
    public ListModel(Vector data)
    {
        Iterator i = data.iterator();

        while (i.hasNext())
        {
            addElement(i.next());
        }
    }
}
