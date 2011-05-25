/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.awt.*;
import java.awt.event.*;

import java.text.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

// Actuellement pour raison de simplicite de codage, on dispose d'une instance
// d'event listener pas widget on peut faire mieux...
/**
 * Generic filter view.
 *
 * @warning Only Strings, Doubles and Booleans constraints are supported.
 */
public final class FilterView extends JPanel implements Observer {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.FilterView");
    /** The filter to represent */
    private Filter _filter = null;
    /** Filter enabling checbox */
    JCheckBox _enabledCheckbox = null;
    /** Store all the GUI component used to represent the attached filter */
    private HashMap<String, JComponent> _widgets = null;
    /** Store all the widget in the constraints order */
    private Vector _orderedWidgets = null;
    /** Widgets panel */
    private JPanel _widgetsPanel = null;
    /** JFormattedTextField formatter for Double constraints */
    private DefaultFormatterFactory _doubleFormaterFactory = null;

    /**
     * Default constructor.
     */
    public FilterView(Filter filter) {
        // Members initialization
        _widgets = new HashMap<String, JComponent>();
        _orderedWidgets = new Vector();
        _widgetsPanel = new JPanel();

        // Members initialization
        _filter = filter;
        _filter.addObserver(this);

        // JFormattedTextField formatter creation
        DefaultFormatter doubleFormater = new NumberFormatter(new DecimalFormat(
                "0.0####"));
        doubleFormater.setValueClass(java.lang.Double.class);
        _doubleFormaterFactory = new DefaultFormatterFactory(doubleFormater,
                doubleFormater, doubleFormater);

        // Create the filter enable/disable checkbox
        _enabledCheckbox = new JCheckBox(_filter.getName(),
                _filter.isEnabled());
        _enabledCheckbox.addActionListener(new ParamListener(_filter, null,
                _enabledCheckbox));
        add(_enabledCheckbox);

        // Add an empty panel to force filter to be aligned
        add(new JPanel());

        // For each constraint of the associated filter
        for (int i = 0; i < _filter.getNbOfConstraints(); i++) {
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
        if (_filter.getNbOfConstraints() > 0) {
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
            Object constraintValue) {
        _logger.entering("FilterView", "addParam");

        JLabel label = new JLabel(constraintName + " : ",
                JLabel.TRAILING);
        ParamListener paramListener;

        // If the constraint is a Double object
        if (constraintValue.getClass() == java.lang.Double.class) {
            // Create the widget label
            panel.add(label);

            // Create the constraint widget
            JFormattedTextField widget = new JFormattedTextField();
            label.setLabelFor(widget);
            widget.setFormatterFactory(_doubleFormaterFactory);
            widget.setValue((Double) constraintValue);
            paramListener = new ParamListener(_filter, constraintName, widget);
            widget.addActionListener(paramListener);
            widget.addFocusListener(paramListener);
            _widgets.put(constraintName, widget);
            _orderedWidgets.add(widget);
            panel.add(widget);
            add(panel);
        } // Else if the constraint is a String object
        else if (constraintValue.getClass() == java.lang.String.class) {
            // Create the widget label
            panel.add(label);

            // Create the constraint widget
            JTextField widget = new JTextField((String) constraintValue);
            label.setLabelFor(widget);
            paramListener = new ParamListener(_filter, constraintName, widget);
            widget.addActionListener(paramListener);
            widget.addFocusListener(paramListener);
            _widgets.put(constraintName, widget);
            _orderedWidgets.add(widget);
            panel.add(widget);
            add(panel);
        } // Else if the constraint is a Boolean object
        else if (constraintValue.getClass() == java.lang.Boolean.class) {
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
    private void setParam(String constraintName, Object constraintValue) {
        _logger.entering("FilterView", "setParam");

        JComponent widget = _widgets.get(constraintName);

        // If the constraint is a Double object
        if (constraintValue.getClass() == java.lang.Double.class) {
            ((JFormattedTextField) widget).setValue((Double) constraintValue);
        } // Else if the constraint is a String object
        else if (constraintValue.getClass() == java.lang.String.class) {
            ((JTextField) widget).setText((String) constraintValue);
        } // Else if the constraint is a Boolean object
        else if (constraintValue.getClass() == java.lang.Boolean.class) {
            ((JCheckBox) widget).setSelected(((Boolean) constraintValue).booleanValue());
        }
    }

    /**
     * @sa java.util.Observer
     */
    public void update(Observable o, Object arg) {
        _logger.entering("FilterView", "update");

        // For each constraint of the associated filter
        for (int i = 0; i < _filter.getNbOfConstraints(); i++) {
            // Get the constraint name
            String constraintName = _filter.getConstraintNameByOrder(i);

            // Get the correspondingconstraint value
            Object constraintObject = _filter.getConstraintByName(constraintName);

            // Update the corresponding value from the widget
            setParam(constraintName, constraintObject);
        }

        // Disabled to make filter GUI always enabled.
        /*
        // Enable or disable all the constraint widgets
        QueryView.setEnabledComponents(_widgetsPanel, ((Filter) o).isEnabled());
         */
    }
}

/**
 * Handle any event coming from a given parameter widget.
 */
class ParamListener implements ActionListener, FocusListener {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.ParamListener");
    /** The filter to update */
    private Filter _filter = null;
    /** the constraint to handle */
    private String _constraintName = null;
    /** The GUI component to display the constraint */
    private JComponent _widget = null;

    /**
     * Constructor.
     *
     * @param filter the filter to update.
     * @param constraintName the name of the constraint to handle, or null for
     * the whole filter enabling checkbox.
     * @param widget The GUI component to display the constraint.
     */
    public ParamListener(Filter filter, String constraintName, JComponent widget) {
        _filter = filter;
        _constraintName = constraintName;
        _widget = widget;
    }

    /**
     * Called when the focus enters a widget.
     */
    public void focusGained(FocusEvent e) {
        // Does nothing (not needed)
    }

    /**
     * Called when the focus leaves a widget.
     *
     * Used to validate and store TextFields data when tabbing between them.
     */
    public void focusLost(FocusEvent e) {
        _logger.entering("ParamListener", "focusLost");

        // Store new data
        storeValues(e);
    }

    /**
     * Called when a widget triggered an action.
     */
    public void actionPerformed(ActionEvent e) {
        _logger.entering("ParamListener", "actionPerformed");

        // Store new data
        storeValues(e);
    }

    /**
     * Store form values in the model.
     */
    public void storeValues(AWTEvent e) {
        _logger.entering("ParamListener", "storeValues");

        // If there is no constraint name
        if (_constraintName == null) {
            // Enable or disable the whole filter
            Boolean checkBoxState = ((JCheckBox) _widget).isSelected();
            _filter.setEnabled(checkBoxState);
        } else // If a constraint name was given
        {
            // Get the corresponding constraint value
            Object constraintValue = _filter.getConstraintByName(_constraintName);

            // Refresh its value from the corresponding widget
            // If the constraint is a Double object
            if (constraintValue.getClass() == java.lang.Double.class) {
                try {
                    // Convert and commit the new value (focus lost)
                    ((JFormattedTextField) _widget).commitEdit();
                } catch (Exception ex) {
                    _logger.severe("Could not handle input");
                }

                Double doubleValue = (Double) ((JFormattedTextField) _widget).getValue();
                _filter.setConstraint(_constraintName, doubleValue);
            } // Else if the constraint is a String object
            else if (constraintValue.getClass() == java.lang.String.class) {
                String stringValue = ((JTextField) _widget).getText();
                _filter.setConstraint(_constraintName, stringValue);
            } // Else if the constraint is a Boolean object
            else if (constraintValue.getClass() == java.lang.Boolean.class) {
                Boolean booleanValue = ((JCheckBox) _widget).isSelected();
                _filter.setConstraint(_constraintName, booleanValue);
            }
        }
    }
}
/*___oOo___*/
