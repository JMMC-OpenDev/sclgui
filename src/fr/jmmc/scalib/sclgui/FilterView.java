/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FilterView.java,v 1.11 2007-04-13 13:25:49 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.9  2006/08/09 13:53:24  lafrasse
 * Generalized code for ParamXListener
 *
 * Revision 1.8  2006/08/08 16:13:21  lafrasse
 * Updated to properly handle widget order
 *
 * Revision 1.7  2006/08/04 14:09:10  lafrasse
 * Added GUI enabling/disabling feature to filters
 *
 * Revision 1.6  2006/08/03 14:47:29  lafrasse
 * Jalopyzation
 *
 * Revision 1.5  2006/07/19 16:27:39  lafrasse
 * Generalized code for addXXXParam and setXXXParam
 *
 * Revision 1.4  2006/06/23 09:19:41  mella
 * Jalopization
 *
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
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.MCSLogger;

import java.awt.*;
import java.awt.event.*;

import java.text.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;


// Actuellement pour raison de simplicite de codage, on dispose d'une instance
// d'event listener pas widget on peut faire mieux...

/**
 * Generic filter view.
 *
 * @warning Only Strings, Doubles and Booleans constraints are supported.
 */
public class FilterView extends JPanel implements Observer
{
    /** The filter to represent */
    Filter _filter;

    /** Filter enabling checbox */
    JCheckBox _enabledCheckbox;

    /** Store all the GUI component used to represent the attached filter */
    Hashtable _widgets = new Hashtable();

    /** Store all the widget in the constraints order */
    Vector _orderedWidgets = new Vector();

    /** Widgets panel */
    JPanel _widgetsPanel = new JPanel();

    /**
     * Default constructor.
     */
    public FilterView(Filter filter)
    {
        // Members initialization
        _filter = filter;
        _filter.addObserver(this);

        // Create the filter enable/disable checkbox
        _enabledCheckbox = new JCheckBox(_filter.getName(), _filter.isEnabled());
        _enabledCheckbox.addActionListener(new ParamListener(_filter, null,
                _enabledCheckbox));
        add(_enabledCheckbox);

        // Add an empty panel to force filter to be aligned
        add(new JPanel());

        // For each constraint of the associated filter
        for (int i = 0; i < _filter.getNbOfConstraints(); i++)
        {
            // Get the constraint name
            String constraintName = _filter.getConstraintNameByOrder(i);

            // Get the corresponding constraint value
            Object constraintObject = _filter.getConstraintByName(constraintName);

            // Add the corresponding widget to the current filter view
            addParam(_widgetsPanel, constraintName, constraintObject);
        }

        // Layout and border initialization
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder((EtchedBorder) BorderFactory.createEtchedBorder());

        // Display the widget panel only if there are some constraints
        if (_filter.getNbOfConstraints() > 0)
        {
            _widgetsPanel.setLayout(new BoxLayout(_widgetsPanel,
                    BoxLayout.X_AXIS));
            _widgetsPanel.setBorder((EtchedBorder) BorderFactory.createEtchedBorder());
            add(_widgetsPanel);
        }

        // Refresh each widget with the filter values
        update(_filter, null);
    }

    /**
     * Add a constraint to the filter.
     *
     * @constraintName the name of the constraint (will be used as the widget
     * label).
     * @constraintValue the object holding the constraint value.
     */
    private void addParam(JPanel panel, String constraintName,
        Object constraintValue)
    {
        MCSLogger.trace();

        JLabel        label         = new JLabel(constraintName);
        ParamListener paramListener;

        // If the constraint is a Double object
        if (constraintValue.getClass() == java.lang.Double.class)
        {
            // Create the widget label
            panel.add(label);

            // Create the constraint widget
            JFormattedTextField widget = new JFormattedTextField((Double) constraintValue);
            paramListener = new ParamListener(_filter, constraintName, widget);
            widget.addActionListener(paramListener);
            widget.addFocusListener(paramListener);
            _widgets.put(constraintName, widget);
            _orderedWidgets.add(widget);
            panel.add(widget);
            add(panel);
        }

        // Else if the constraint is a String object
        else if (constraintValue.getClass() == java.lang.String.class)
        {
            // Create the widget label
            panel.add(label);

            // Create the constraint widget
            JTextField widget = new JTextField((String) constraintValue);
            paramListener = new ParamListener(_filter, constraintName, widget);
            widget.addActionListener(paramListener);
            widget.addFocusListener(paramListener);
            _widgets.put(constraintName, widget);
            _orderedWidgets.add(widget);
            panel.add(widget);
            add(panel);
        }

        // Else if the constraint is a Boolean object
        else if (constraintValue.getClass() == java.lang.Boolean.class)
        {
            // Create the constraint widget
            JCheckBox widget = new JCheckBox(constraintName,
                    ((Boolean) constraintValue).booleanValue());
            paramListener = new ParamListener(_filter, constraintName, widget);
            widget.addActionListener(paramListener);
            _widgets.put(constraintName, widget);
            _orderedWidgets.add(widget);
            panel.add(widget);
            add(panel);
        }
    }

    /**
     * Change a constraint value.
     *
     * @constraintName the name of the constraint to be updated.
     * @constraintValue the new constraint value.
     */
    private void setParam(String constraintName, Object constraintValue)
    {
        MCSLogger.trace();

        JComponent widget = (JComponent) _widgets.get(constraintName);

        // If the constraint is a Double object
        if (constraintValue.getClass() == java.lang.Double.class)
        {
            ((JFormattedTextField) widget).setValue((Double) constraintValue);
        }

        // Else if the constraint is a String object
        else if (constraintValue.getClass() == java.lang.String.class)
        {
            ((JTextField) widget).setText((String) constraintValue);
        }

        // Else if the constraint is a Boolean object
        else if (constraintValue.getClass() == java.lang.Boolean.class)
        {
            ((JCheckBox) widget).setSelected(((Boolean) constraintValue).booleanValue());
        }
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg)
    {
        // For each constraint of the associated filter
        for (int i = 0; i < _filter.getNbOfConstraints(); i++)
        {
            // Get the constraint name
            String constraintName = _filter.getConstraintNameByOrder(i);

            // Get the correspondingconstraint value
            Object constraintObject = _filter.getConstraintByName(constraintName);

            // Update the corresponding value from the widget
            setParam(constraintName, constraintObject);
        }

        // Enable or disable all the constraint widgets
        QueryView.setEnabledComponents(_widgetsPanel, ((Filter) o).isEnabled());
    }
}


/**
 * Handle any event coming from a given parameter widget.
 */
class ParamListener implements ActionListener, FocusListener
{
    /** The filter to update */
    Filter _filter;

    /** the constraint to handle */
    String _constraintName;

    /** The GUI component to display the constraint */
    JComponent _widget;

    /**
     * Constructor.
     *
     * @param filter the filter to update.
     * @param constraintName the name of the constraint to handle, or null for
     * the whole filter enabling checkbox.
     * @param widget The GUI component to display the constraint.
     */
    public ParamListener(Filter filter, String constraintName, JComponent widget)
    {
        _filter             = filter;
        _constraintName     = constraintName;
        _widget             = widget;
    }

    /**
     * Called when the focus enters a widget.
     */
    public void focusGained(FocusEvent e)
    {
        // Does nothing (not needed)
    }

    /**
     * Called when the focus leaves a widget.
     *
     * Used to validate and store TextFields data when tabbing between them.
     */
    public void focusLost(FocusEvent e)
    {
        MCSLogger.trace();

        // Store new data
        storeValues(e);
    }

    /**
     * Called when a widget triggered an action.
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        // Store new data
        storeValues(e);
    }

    /**
     * Store form values in the model.
     */
    public void storeValues(AWTEvent e)
    {
        // If there is no constraint name
        if (_constraintName == null)
        {
            // Enable or disable the whole filter
            _filter.setEnabled(new Boolean(((JCheckBox) _widget).isSelected()));
        }
        else // If a constraint name was given
        {
            // Get the corresponding constraint value
            Object constraintValue = _filter.getConstraintByName(_constraintName);

            // Refresh its value from the corresponding widget
            // If the constraint is a Double object
            if (constraintValue.getClass() == java.lang.Double.class)
            {
                try
                {
                    // Convert and commit the new value (focus lost)
                    ((JFormattedTextField) _widget).commitEdit();
                }
                catch (Exception ex)
                {
                    MCSLogger.error("Could not handle input");
                }

                _filter.setConstraint(_constraintName,
                    (Double) ((JFormattedTextField) _widget).getValue());
            }

            // Else if the constraint is a String object
            else if (constraintValue.getClass() == java.lang.String.class)
            {
                _filter.setConstraint(_constraintName,
                    ((JTextField) _widget).getText());
            }

            // Else if the constraint is a Boolean object
            else if (constraintValue.getClass() == java.lang.Boolean.class)
            {
                _filter.setConstraint(_constraintName,
                    new Boolean(((JCheckBox) _widget).isSelected()));
            }
        }
    }
}
/*___oOo___*/
