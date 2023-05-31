/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Panel is dedicated to manage general behavior configuration.
 */
public final class GeneralPreferencesView extends JPanel implements Observer, ChangeListener, ActionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(GeneralPreferencesView.class.getName());
    /** Data model */
    private final Preferences _preferences;
    /** Tooltip enabling checkbox */
    private final JCheckBox _enableToolTipCheckBox;
    /** Tooltip manager */
    private final ToolTipManager _sharedToolTipManager;
    /** radio button Jy */
    private final JRadioButton _jRadioButtonFluxJy;
    /** radio button Mag */
    private final JRadioButton _jRadioButtonFluxMag;
    /** Diagnose mode checkbox */
    private final JCheckBox _diagnoseMode;

    /**
     * Constructor.
     */
    public GeneralPreferencesView() {
        _preferences = Preferences.getInstance();

        // Layout management
        JPanel topPanel = new JPanel();
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
        radioPanel.setLayout(new GridBagLayout());
        topPanel.add(radioPanel, c);
        c.gridy++;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;

        JLabel label = new JLabel("Results Verbosity: ", JLabel.TRAILING);
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

        // Handle "Show Legend" checkbox
        JCheckBox showLegendCheckBox = new JCheckBox(CalibratorsView._showLegendAction);
        CalibratorsView._showLegendAction.addBoundButton(showLegendCheckBox);
        topPanel.add(showLegendCheckBox, c);
        c.gridy++;

        // Handle "Show Tooltips" checkbox
        _enableToolTipCheckBox = new JCheckBox("Show Tooltips");
        _sharedToolTipManager.registerComponent(_enableToolTipCheckBox);
        topPanel.add(_enableToolTipCheckBox, c);
        c.gridy++;

        // IR Flux conversion:
        final JPanel convPanel = new JPanel();
        convPanel.setLayout(new GridBagLayout());
        topPanel.add(convPanel, c);
        c.gridy++;

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;

        label = new JLabel("Flux unit:", JLabel.LEADING);
        convPanel.add(label, gbc);
        gbc.gridx++;

        final ButtonGroup buttonGroupFluxUnit = new javax.swing.ButtonGroup();

        _jRadioButtonFluxMag = new JRadioButton();
        buttonGroupFluxUnit.add(_jRadioButtonFluxMag);
        _jRadioButtonFluxMag.setText("mag");
        convPanel.add(_jRadioButtonFluxMag, gbc);
        gbc.gridy++;

        _jRadioButtonFluxJy = new JRadioButton();
        buttonGroupFluxUnit.add(_jRadioButtonFluxJy);
        _jRadioButtonFluxJy.setText("jy");
        convPanel.add(_jRadioButtonFluxJy, gbc);

        // Server settings:
        final JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new GridBagLayout());
        topPanel.add(serverPanel, c);
        c.gridy++;

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;

        label = new JLabel("Server: ", JLabel.LEADING);
        serverPanel.add(label, gbc);
        gbc.gridy++;

        // Handle "Diagnose mode" checkbox
        _diagnoseMode = new JCheckBox("Diagnose mode");
        serverPanel.add(_diagnoseMode, gbc);
    }

    public void init() {
        _preferences.addObserver(this);
        _enableToolTipCheckBox.addChangeListener(this);

        _jRadioButtonFluxJy.addActionListener(this);
        _jRadioButtonFluxMag.addActionListener(this);

        _diagnoseMode.addChangeListener(this);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        // Adjust view and behaviour according preferences entries
        boolean b = _preferences.getPreferenceAsBoolean(PreferenceKey.SHOW_TOOLTIPS_FLAG);
        _enableToolTipCheckBox.setSelected(b);
        _sharedToolTipManager.setEnabled(b);

        b = _preferences.getPreferenceAsBoolean(PreferenceKey.FLUX_EDITOR_JY);
        _jRadioButtonFluxJy.setSelected(b);
        _jRadioButtonFluxMag.setSelected(!b);

        _diagnoseMode.setSelected(_preferences.getPreferenceAsBoolean(PreferenceKey.SERVER_DIAGNOSE));
    }

    /**
     * Update preferences according buttons change
     * @param ev 
     */
    @Override
    public void stateChanged(ChangeEvent ev) {
        Object source = ev.getSource();

        try {
            if (source.equals(_enableToolTipCheckBox)) {
                _preferences.setPreference(PreferenceKey.SHOW_TOOLTIPS_FLAG, _enableToolTipCheckBox.isSelected());
            }
            if (source.equals(_diagnoseMode)) {
                _preferences.setPreference(PreferenceKey.SERVER_DIAGNOSE, _diagnoseMode.isSelected());
            }
        } catch (PreferencesException pe) {
            _logger.warn("Could not set preference : ", pe);
        }
    }

    /**
     * Process any radio change event
     * @param e action event
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            if (e.getSource() == _jRadioButtonFluxMag) {
                _preferences.setPreference(PreferenceKey.FLUX_EDITOR_JY, Boolean.FALSE);
            } else if (e.getSource() == _jRadioButtonFluxJy) {
                _preferences.setPreference(PreferenceKey.FLUX_EDITOR_JY, Boolean.TRUE);
            }
        } catch (PreferencesException pe) {
            _logger.warn("Could not set preference : ", pe);
        }
    }

}
