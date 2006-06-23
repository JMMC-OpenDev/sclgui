/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FilterView.java,v 1.4 2006-06-23 09:19:41 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.2  2006/04/06 13:24:04  mella
 * Add empty panel for presentation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;


// Actuellement pour raison de simplicite de codage, on dispose d'une instance
// d'event listener pas widget on peut faire mieux...

/**
 * Generic filter view.
 */
public class FilterView extends JPanel implements Observer
{
    /** The filter to represent */
    Filter _model;

    /** Store all the GUI component used to represent the attached filter */
    Hashtable widgets;

    /**
     * Default constructor.
     */
    public FilterView(Filter model)
    {
        _model = model;
        _model.addObserver(this);

        widgets = new Hashtable();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JCheckBox enabledCheckbox = new JCheckBox(_model.getName(),
                _model.isEnabled());
        add(enabledCheckbox);

        EnablerListener cbListener = new EnablerListener(_model);
        enabledCheckbox.addActionListener(cbListener);

        // Add an empty panel to force filter to be aligned
        JPanel panel = new JPanel();
        add(panel);

        // Get constraints of model
        Hashtable constraints = _model.getConstraints();

        for (Enumeration e = constraints.keys(); e.hasMoreElements();)
        {
            String constraintName   = (String) e.nextElement();
            Object constraintObject = constraints.get(constraintName);

            if (constraintObject instanceof Double)
            {
                String constraintValue = (String) constraintObject;
                addStringParam(constraintName, constraintValue);
            }
            else if (constraintObject instanceof String)
            {
                addStringParam(constraintName, (String) constraintObject);
            }
            else if (constraintObject instanceof Boolean)
            {
                addBooleanParam(constraintName, (Boolean) constraintObject);
            }
        }

        EtchedBorder border = (EtchedBorder) BorderFactory.createEtchedBorder();
        setBorder(border);
    }

    /**
     * DOCUMENT ME!
     *
     * @param constraintName DOCUMENT ME!
     * @param constraintValue DOCUMENT ME!
     */
    public void addStringParam(String constraintName, String constraintValue)
    {
        MCSLogger.trace();

        JPanel              panel         = new JPanel();
        JLabel              label         = new JLabel(constraintName);
        JTextField          widget        = new JTextField(constraintValue);
        StringParamListener paramListener = new StringParamListener(_model,
                constraintName, widget);
        widget.addActionListener(paramListener);

        panel.add(label);
        panel.add(widget);
        add(panel);
        widgets.put(constraintName, widget);
    }

    /**
     * DOCUMENT ME!
     *
     * @param constraintName DOCUMENT ME!
     * @param constraintValue DOCUMENT ME!
     */
    public void setStringParam(String constraintName, String constraintValue)
    {
        MCSLogger.trace();

        JTextField widget = (JTextField) widgets.get(constraintName);
        widget.setText(constraintValue);
    }

    /**
     * DOCUMENT ME!
     *
     * @param constraintName DOCUMENT ME!
     * @param constraintValue DOCUMENT ME!
     */
    public void addBooleanParam(String constraintName, Boolean constraintValue)
    {
        MCSLogger.trace();

        JPanel               panel         = new JPanel();
        JCheckBox            widget        = new JCheckBox(constraintName,
                constraintValue.booleanValue());
        BooleanParamListener paramListener = new BooleanParamListener(_model,
                constraintName, widget);
        widget.addActionListener(paramListener);

        panel.add(widget);
        add(panel);
        widgets.put(constraintName, widget);
    }

    /**
     * DOCUMENT ME!
     *
     * @param constraintName DOCUMENT ME!
     * @param constraintValue DOCUMENT ME!
     */
    public void setBooleanParam(String constraintName, Boolean constraintValue)
    {
        MCSLogger.trace();

        JCheckBox widget = (JCheckBox) widgets.get(constraintName);
        widget.setSelected(constraintValue.booleanValue());
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        // TODO update fields
        // get constraints of model
        Hashtable constraints = _model.getConstraints();

        for (Enumeration e = constraints.keys(); e.hasMoreElements();)
        {
            String constraintName   = (String) e.nextElement();
            Object constraintObject = constraints.get(constraintName);

            if (constraintObject instanceof Double)
            {
                String constraintValue = (String) constraintObject;
                setStringParam(constraintName, constraintValue);
            }
            else if (constraintObject instanceof String)
            {
                setStringParam(constraintName, (String) constraintObject);
            }
            else if (constraintObject instanceof Boolean)
            {
                setBooleanParam(constraintName, (Boolean) constraintObject);
            }
        }
    }
}


/**
 * Handle any event coming from a given parameter widget.
 */
class StringParamListener implements ActionListener
{
    /** The filter to update */
    Filter _filter;

    /** the constraint to handle */
    String _constraintName;

    /** The GUI component to display the constraint */
    JTextField _widget;

    /**
     * Constructor.
     *
     * @param filter the filter to update.
     * @param constraintName the name of the constraint to handle.
     * @param widget The GUI component to display the constraint.
     */
    public StringParamListener(Filter filter, String constraintName,
        JTextField widget)
    {
        _filter             = filter;
        _constraintName     = constraintName;
        _widget             = widget;
    }

    /**
     * Called whenever an event was issued by the GUI component.
     *
     * @param e the event.
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        // Update the filter value with the one given by the GUI component
        _filter.setConstraint(_constraintName, _widget.getText());
    }
}


/**
 * Handle any event coming from a given parameter widget.
 */
class BooleanParamListener implements ActionListener
{
    /** The filter to update */
    Filter _filter;

    /** the constraint to handle */
    String _constraintName;

    /** The GUI component to display the constraint */
    JCheckBox _widget;

    /**
     * Constructor.
     *
     * @param filter the filter to update.
     * @param constraintName the name of the constraint to handle.
     * @param widget The GUI component to display the constraint.
     */
    public BooleanParamListener(Filter filter, String constraintName,
        JCheckBox widget)
    {
        _filter             = filter;
        _constraintName     = constraintName;
        _widget             = widget;
    }

    /**
     * Called whenever an event was issued by the GUI component.
     *
     * @param e the event.
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        // Update the filter value with the one given by the GUI component
        _filter.setConstraint(_constraintName, new Boolean(_widget.isSelected()));
    }
}


/**
 * Handle any event coming from the filter enabling view.
 */
class EnablerListener implements ActionListener
{
    /** The filter to update */
    Filter _filter;

    /**
     * Constructor.
     *
     * @param filter the filter to update.
     */
    public EnablerListener(Filter filter)
    {
        _filter = filter;
    }

    /**
     * Called whenever an event was issued by the enabling checkbox.
     *
     * @param e the event.
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        // Update the filter enabled state with the one given by the checkbox
        JCheckBox cb = (JCheckBox) e.getSource();
        _filter.setEnabled(new Boolean(cb.isSelected()));
    }
}
/*___oOo___*/
