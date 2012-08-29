/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This Panel is dedicated to manage help behavior configuration.
 */
public class HelpPreferencesView extends JPanel implements Observer, ChangeListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(HelpPreferencesView.class.getName());
    /** Data model */
    private Preferences _preferences;
    /** Tooltip enabling checkbox */
    private JCheckBox _enableToolTipCheckBox;
    /** Tooltip manager */
    private ToolTipManager _sharedToolTipManager;

    /**
     * Constructor.
     */
    public HelpPreferencesView() {
        _preferences = Preferences.getInstance();

        // Layout management
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        add(topPanel);
        topPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridy = 0;
        c.gridx = 0;

        // Get instance of shared tooltip to adjust behaviour in update code
        _sharedToolTipManager = ToolTipManager.sharedInstance();

        // Handle "Result Verbosity" radio buttons
        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);
        radioPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;

        JLabel label = new JLabel("Results Verbosity : ", JLabel.TRAILING);
        radioPanel.add(label, gbc);
        gbc.gridx++;

        ButtonGroup radioGroup = new ButtonGroup();
        JRadioButton syntheticRadioButton = new JRadioButton(CalibratorsView._syntheticResultsVerbosityAction);
        CalibratorsView._syntheticResultsVerbosityAction.addBoundButton(syntheticRadioButton);
        radioGroup.add(syntheticRadioButton);
        radioPanel.add(syntheticRadioButton, gbc);
        gbc.gridy++;

        JRadioButton detailedRadioButton = new JRadioButton(CalibratorsView._detailedResultsVerbosityAction);
        CalibratorsView._detailedResultsVerbosityAction.addBoundButton(detailedRadioButton);
        radioGroup.add(detailedRadioButton);
        radioPanel.add(detailedRadioButton, gbc);
        gbc.gridy++;

        JRadioButton fullRadioButton = new JRadioButton(CalibratorsView._fullResultsVerbosityAction);
        CalibratorsView._fullResultsVerbosityAction.addBoundButton(fullRadioButton);
        radioGroup.add(fullRadioButton);
        radioPanel.add(fullRadioButton, gbc);
        topPanel.add(radioPanel, c);
        c.gridy++;

        // Handle "Show Legend" checkbox
        JCheckBox showLegendCheckBox = new JCheckBox(CalibratorsView._showLegendAction);
        CalibratorsView._showLegendAction.addBoundButton(showLegendCheckBox);
        topPanel.add(showLegendCheckBox, c);
        c.gridy++;

        // Handle "Show Tooltips" checkbox
        _enableToolTipCheckBox = new JCheckBox("Show Tooltips");
        _sharedToolTipManager.registerComponent(_enableToolTipCheckBox);
        topPanel.add(_enableToolTipCheckBox, c);
    }

    public void init() {
        _preferences.addObserver(this);
        _enableToolTipCheckBox.addChangeListener(this);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        _logger.entering("HelpPreferencesView", "update");

        // Adjust view and behaviour according preferences entries
        boolean b = _preferences.getPreferenceAsBoolean(PreferenceKey.SHOW_TOOLTIPS_FLAG);
        _enableToolTipCheckBox.setSelected(b);
        _sharedToolTipManager.setEnabled(b);
    }

    /**
     * Update preferences according buttons change
     * @param ev 
     */
    @Override
    public void stateChanged(ChangeEvent ev) {
        _logger.entering("HelpPreferencesView", "stateChanged");

        Object source = ev.getSource();

        try {
            if (source.equals(_enableToolTipCheckBox)) {
                _preferences.setPreference(PreferenceKey.SHOW_TOOLTIPS_FLAG,
                        _enableToolTipCheckBox.isSelected());
            }
        } catch (Exception ex) {
            _logger.warning("Could not set preference : " + ex);
        }
    }
}
