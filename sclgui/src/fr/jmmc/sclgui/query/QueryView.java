/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.query;

import fr.jmmc.jmal.star.Star;
import fr.jmmc.jmal.star.StarResolverWidget;
import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.jmcs.gui.StatusBar;
import fr.jmmc.jmcs.gui.action.MCSAction;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Query view.
 */
public final class QueryView extends JPanel implements Observer,
        PropertyChangeListener, ActionListener, FocusListener, Printable {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(QueryView.class.getName());
    /** MVC associated model */
    public QueryModel _queryModel = null;
    /** Associated virtual observatory */
    private VirtualObservatory _vo = null;
    /** Instrument panel */
    private JPanel _instrumentPanel = new JPanel();
    /** Instrument magnitude band */
    private JComboBox _instrumentalMagnitudeBandCombo = new JComboBox();
    /** Instrument wavelentgh label */
    private JLabel _instrumentalWavelengthLabel = new JLabel("Wavelength [µm] : ",
            JLabel.TRAILING);
    /** Instrument wavelentgh */
    private JFormattedTextField _instrumentalWavelengthTextfield = new JFormattedTextField(new Double(
            0));
    /** Instrument maximun baseline */
    private JFormattedTextField _instrumentalMaxBaselineTextField = new JFormattedTextField(new Double(
            0));
    /** Science object panel */
    private JPanel _scienceObjectPanel = null;
    /** Science object name resolver widget */
    private StarResolverWidget _scienceObjectNameTextfield = null;
    /** Science object right ascension coordinate */
    private JTextField _scienceObjectRATextfield = new JTextField();
    /** Science object declinaison coordinate */
    private JTextField _scienceObjectDECTextfield = new JTextField();
    /** Science object magnitude label */
    private JLabel _scienceObjectMagnitudeLabel = new JLabel("Magnitude : ",
            JLabel.TRAILING);
    /** Science object magnitude */
    private JFormattedTextField _scienceObjectMagnitudeTextfield = new JFormattedTextField(new Double(
            0));
    /** SearchCal parameters panel */
    private JPanel _searchCalPanel = null;
    /** Search minimum magnitude label */
    private JLabel _minMagnitudeLabel = new JLabel("Min. Magnitude : ", JLabel.TRAILING);
    /** Search minimum magnitude */
    private JFormattedTextField _minMagnitudeTextfield = new JFormattedTextField(new Double(
            0));
    /** Search maximum magnitude label */
    private JLabel _maxMagnitudeLabel = new JLabel("Max. Magnitude : ", JLabel.TRAILING);
    /** Search maximum magnitude */
    private JFormattedTextField _maxMagnitudeTextfield = new JFormattedTextField(new Double(
            0));
    /** Bright query button */
    private JRadioButton _brightRadioButton = null;
    /** Faint query button */
    private JRadioButton _faintRadioButton = null;
    /** Search box RA size label */
    private JLabel _diffRASizeLabel = new JLabel("RA Range [mn] : ", JLabel.TRAILING);
    /** Search box RA size */
    private JFormattedTextField _diffRASizeTextfield = new JFormattedTextField(new Double(
            0));
    /** Search box DEC size label */
    private JLabel _diffDECSizeLabel = new JLabel("DEC Range [deg] : ", JLabel.TRAILING);
    /** Search box DEC size */
    private JFormattedTextField _diffDECSizeTextfield = new JFormattedTextField(new Double(
            0));
    /** Search box radial size label */
    private JLabel _radialSizeLabel = new JLabel("Radius [arcmin] : ", JLabel.TRAILING);
    /** Auto/Manual radius radio button group */
    private ButtonGroup _radiusButtonGroup = new ButtonGroup();
    /** Auto radius radio button */
    private JRadioButton _autoRadiusRadioButton = null;
    /** Auto radius radio button label */
    private JLabel _autoRadiusRadioButtonLabel = new JLabel("Auto", JLabel.LEADING);
    /** Manual radius radio button */
    private JRadioButton _manualRadiusRadioButton = null;
    /** Search box radial size */
    private JFormattedTextField _radialSizeTextfield = new JFormattedTextField(new Double(
            0));
    /** Query action and progression panel */
    private JPanel _actionPanel = new JPanel();
    /** Query launcher/canceler */
    private JButton _searchButton = null;
    /** Query progress bar */
    private JProgressBar _progressBar = new JProgressBar();
    /** Reset Values action */
    public ResetValuesAction _resetValuesAction = null;
    /** Reset Values action */
    public LoadDefaultValuesAction _loadDefaultValuesAction = null;
    /** Reset Values action */
    public SaveValuesAction _saveValuesAction = null;

    /**
     * Constructor.
     *
     * @param queryModel the object used to store all the query attributes.
     * @param vo the object used to query the JMMC GETCAL webservice.
     */
    public QueryView(QueryModel queryModel, VirtualObservatory vo) {
        String classPath = getClass().getName();

        // Store the model
        _queryModel = queryModel;

        // Store the virtual observatory
        _vo = vo;

        // Reset values action
        _resetValuesAction = new ResetValuesAction(classPath,
                "_resetValuesAction");

        // Load default values action
        _loadDefaultValuesAction = new LoadDefaultValuesAction(classPath,
                "_loadDefaultValuesAction");

        // Save values action
        _saveValuesAction = new SaveValuesAction(classPath,
                "_saveValuesAction");

        JLabel label;

        // JFormattedTextField formatter creation
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setNaN("???"); // Set the symbol for a Double.NaN to an empty String

        DefaultFormatter doubleFormater = new NumberFormatter(new DecimalFormat(
                "0.0####", symbols));
        doubleFormater.setValueClass(java.lang.Double.class);

        DefaultFormatterFactory doubleFormaterFactory = new DefaultFormatterFactory(doubleFormater,
                doubleFormater, doubleFormater);

        // Form panel global attributes and common objects
        JPanel tempPanel;
        Dimension textfieldDimension = new Dimension(100, 20);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Instrumental Configuration panel
        _instrumentPanel.setBorder(new TitledBorder(
                "1)  Instrumental Configuration"));
        _instrumentPanel.setLayout(new GridBagLayout());
        // Magnitude band field
        c.gridy = 0;
        c.gridx = 0;
        label = new JLabel("Magnitude Band : ", JLabel.TRAILING);
        _instrumentPanel.add(label, c);
        label.setLabelFor(_instrumentalMagnitudeBandCombo);
        c.gridx = 1;
        _instrumentPanel.add(_instrumentalMagnitudeBandCombo, c);
        _instrumentalMagnitudeBandCombo.addActionListener(this);
        // Wavelength field
        c.gridy++;
        c.gridx = 0;
        _instrumentPanel.add(_instrumentalWavelengthLabel, c);
        _instrumentalWavelengthLabel.setLabelFor(_instrumentalWavelengthTextfield);
        _instrumentalWavelengthTextfield.setFormatterFactory(doubleFormaterFactory);
        _instrumentalWavelengthTextfield.setMinimumSize(textfieldDimension);
        _instrumentalWavelengthTextfield.setPreferredSize(textfieldDimension);
        _instrumentalWavelengthTextfield.addActionListener(this);
        _instrumentalWavelengthTextfield.addFocusListener(this);
        c.gridx = 1;
        _instrumentPanel.add(_instrumentalWavelengthTextfield, c);
        // Maximum baseline
        c.gridy++;
        c.gridx = 0;
        label = new JLabel("Max. Baseline [m] : ", JLabel.TRAILING);
        _instrumentPanel.add(label, c);
        label.setLabelFor(_instrumentalMaxBaselineTextField);
        _instrumentalMaxBaselineTextField.setFormatterFactory(doubleFormaterFactory);
        _instrumentalMaxBaselineTextField.setMinimumSize(textfieldDimension);
        _instrumentalMaxBaselineTextField.setPreferredSize(textfieldDimension);
        _instrumentalMaxBaselineTextField.addActionListener(this);
        _instrumentalMaxBaselineTextField.addFocusListener(this);
        c.gridx = 1;
        _instrumentPanel.add(_instrumentalMaxBaselineTextField, c);

        // Science Object panel
        _scienceObjectPanel = new JPanel();
        _scienceObjectPanel.setBorder(new TitledBorder("2)  Science Object"));
        _scienceObjectPanel.setLayout(new GridBagLayout());
        // Star name search field
        c.gridy = 0;
        c.gridx = 0;
        label = new JLabel("Name : ", JLabel.TRAILING);
        _scienceObjectPanel.add(label, c);
        _scienceObjectNameTextfield = new StarResolverWidget(_queryModel);
        label.setLabelFor(_scienceObjectNameTextfield);
        _scienceObjectNameTextfield.addActionListener(this);
        _scienceObjectNameTextfield.addFocusListener(this);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectNameTextfield, c);
        // RA coordinate field
        c.gridy++;
        c.gridx = 0;
        label = new JLabel("RA 2000 [hh:mm:ss] : ", JLabel.TRAILING);
        _scienceObjectPanel.add(label, c);
        label.setLabelFor(_scienceObjectRATextfield);
        _scienceObjectRATextfield.setMinimumSize(textfieldDimension);
        _scienceObjectRATextfield.setPreferredSize(textfieldDimension);
        _scienceObjectRATextfield.addActionListener(this);
        _scienceObjectRATextfield.addFocusListener(this);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectRATextfield, c);
        // DEC coordinate field
        c.gridy++;
        c.gridx = 0;
        label = new JLabel("DEC 2000 [+/-dd:mm:ss] : ", JLabel.TRAILING);
        _scienceObjectPanel.add(label, c);
        label.setLabelFor(_scienceObjectDECTextfield);
        _scienceObjectDECTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectDECTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectDECTextfield.addActionListener(this);
        _scienceObjectDECTextfield.addFocusListener(this);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectDECTextfield, c);
        // Magnitude field
        c.gridy++;
        c.gridx = 0;
        _scienceObjectPanel.add(_scienceObjectMagnitudeLabel, c);
        _scienceObjectMagnitudeLabel.setLabelFor(_scienceObjectMagnitudeTextfield);
        _scienceObjectMagnitudeTextfield.setFormatterFactory(doubleFormaterFactory);
        _scienceObjectMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectMagnitudeTextfield.addActionListener(this);
        _scienceObjectMagnitudeTextfield.addFocusListener(this);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectMagnitudeTextfield, c);

        // Searching Parameters panel
        _searchCalPanel = new JPanel();
        _searchCalPanel.setBorder(new TitledBorder("3)  SearchCal Parameters"));
        _searchCalPanel.setLayout(new GridBagLayout());
        // Minimum magnitude field
        c.gridy = 0;
        c.gridx = 0;
        _searchCalPanel.add(_minMagnitudeLabel, c);
        _minMagnitudeLabel.setLabelFor(_minMagnitudeTextfield);
        _minMagnitudeTextfield.setFormatterFactory(doubleFormaterFactory);
        _minMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _minMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _minMagnitudeTextfield.addActionListener(this);
        _minMagnitudeTextfield.addFocusListener(this);
        c.gridx = 1;
        _searchCalPanel.add(_minMagnitudeTextfield, c);
        // Maximum magnitude field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_maxMagnitudeLabel, c);
        _maxMagnitudeLabel.setLabelFor(_maxMagnitudeTextfield);
        _maxMagnitudeTextfield.setFormatterFactory(doubleFormaterFactory);
        _maxMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _maxMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _maxMagnitudeTextfield.addActionListener(this);
        _maxMagnitudeTextfield.addFocusListener(this);
        c.gridx = 1;
        _searchCalPanel.add(_maxMagnitudeTextfield, c);
        // Bright/Faint Scenario
        c.gridy++;
        c.gridx = 0;
        label = new JLabel("Scenario : ", JLabel.TRAILING);
        _searchCalPanel.add(label, c);
        tempPanel = new JPanel();

        ButtonGroup brightFaintButtonGroup = new ButtonGroup();
        _brightRadioButton = new JRadioButton(new BrightQueryAction());
        brightFaintButtonGroup.add(_brightRadioButton);
        tempPanel.add(_brightRadioButton);
        _faintRadioButton = new JRadioButton(new FaintQueryAction());
        brightFaintButtonGroup.add(_faintRadioButton);
        c.gridx = 1;
        tempPanel.add(_faintRadioButton);
        label.setLabelFor(tempPanel);
        _searchCalPanel.add(tempPanel, c);
        // RA delta field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_diffRASizeLabel, c);
        _diffRASizeLabel.setLabelFor(_diffRASizeTextfield);
        _diffRASizeTextfield.setFormatterFactory(doubleFormaterFactory);
        _diffRASizeTextfield.setMinimumSize(textfieldDimension);
        _diffRASizeTextfield.setPreferredSize(textfieldDimension);
        _diffRASizeTextfield.addActionListener(this);
        _diffRASizeTextfield.addFocusListener(this);
        c.gridx = 1;
        _searchCalPanel.add(_diffRASizeTextfield, c);
        // DEC delta field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_diffDECSizeLabel, c);
        _diffDECSizeLabel.setLabelFor(_diffDECSizeTextfield);
        _diffDECSizeTextfield.setFormatterFactory(doubleFormaterFactory);
        _diffDECSizeTextfield.setMinimumSize(textfieldDimension);
        _diffDECSizeTextfield.setPreferredSize(textfieldDimension);
        _diffDECSizeTextfield.addActionListener(this);
        _diffDECSizeTextfield.addFocusListener(this);
        c.gridx = 1;
        _searchCalPanel.add(_diffDECSizeTextfield, c);
        // Radial size field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_radialSizeLabel, c);
        tempPanel = new JPanel();
        tempPanel.setLayout(new GridBagLayout());

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.weightx = 1;
        c2.gridy = 0;
        c2.gridx = 0;
        _autoRadiusRadioButton = new JRadioButton(new AutoManualRadiusAction());
        _radiusButtonGroup.add(_autoRadiusRadioButton);
        tempPanel.add(_autoRadiusRadioButton, c2);
        c2.gridx = 1;
        tempPanel.add(_autoRadiusRadioButtonLabel, c2);
        c2.gridy++;
        c2.gridx = 0;
        _manualRadiusRadioButton = new JRadioButton(new AutoManualRadiusAction());
        _radiusButtonGroup.add(_manualRadiusRadioButton);
        tempPanel.add(_manualRadiusRadioButton, c2);
        c2.gridx = 1;
        tempPanel.add(_radialSizeTextfield, c2);
        _radialSizeLabel.setLabelFor(tempPanel);
        c.gridx = 1;
        _searchCalPanel.add(tempPanel, c);

        // _radialSizeTextfield formatter creation
        DecimalFormatSymbols radialSymbols = new DecimalFormatSymbols();
        radialSymbols.setNaN("0.0"); // Set the symbol for a Double.NaN to an "auto" String

        DefaultFormatter radialDoubleFormater = new NumberFormatter(new DecimalFormat(
                "0.0####", radialSymbols));
        radialDoubleFormater.setValueClass(java.lang.Double.class);

        DefaultFormatterFactory radialDoubleFormaterFactory = new DefaultFormatterFactory(radialDoubleFormater,
                radialDoubleFormater, radialDoubleFormater);
        _radialSizeTextfield.setFormatterFactory(radialDoubleFormaterFactory);
        _radialSizeTextfield.setMinimumSize(textfieldDimension);
        _radialSizeTextfield.setPreferredSize(textfieldDimension);
        _radialSizeTextfield.addActionListener(this);
        _radialSizeTextfield.addFocusListener(this);

        // Status panel global attributes and common objects
        _actionPanel.setLayout(new BoxLayout(_actionPanel, BoxLayout.X_AXIS));
        // Fixed space
        _actionPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        // Progress label
        label = new JLabel("Progress : ", JLabel.TRAILING);
        _actionPanel.add(label);
        label.setLabelFor(_progressBar);
        // Fixed space
        _actionPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        // Progressbar
        _progressBar.setStringPainted(true);
        _actionPanel.add(_progressBar);
        // Fixed space
        _actionPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        // Search Button
        _searchButton = new JButton(_vo._getCalAction);
        _actionPanel.add(_searchButton);
        // Fixed space
        _actionPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        // Query panel global construction
        setBorder(new TitledBorder("Query Parameters"));
        setLayout(new GridBagLayout());
        c.anchor = GridBagConstraints.PAGE_START; //bottom of space
        c.gridy = 0;
        c.gridx = 0;
        add(_instrumentPanel, c);
        c.gridx++;
        add(_scienceObjectPanel, c);
        c.gridx++;
        add(_searchCalPanel, c);
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 3;
        add(_actionPanel, c);

        // Start listening to any updates of the monitored objects
        _queryModel.addObserver(this);
        _vo.addObserver(this);

        // Refresh the GUI with the model values
        update(null, null);

        // To overcome silent call on setQueryMxxMagnitude() by storeValues() after update(null, null)
        _queryModel.restoreMinMaxMagnitudeFieldsAutoUpdating();
    }

    /**
     * Automatically called on attached QueryModel changes.
     * @param o
     * @param arg  
     */
    @Override
    public void update(final Observable o, final Object arg) {
        _logger.entering("QueryView", "update");

        // handle Star resolver errors:

        if (arg instanceof Star.Notification) {
            final Star.Notification notification = (Star.Notification) arg;

            if (notification == Star.Notification.QUERY_ERROR) {
                // focus on object name:
                _scienceObjectNameTextfield.requestFocus();

                // do nothing, simply return to stop updating model:
                return;
            }
        }

        String instrumentalMagnitudeBand = _queryModel.getInstrumentalMagnitudeBand();

        // Instrumental parameters
        _instrumentalMagnitudeBandCombo.setModel(_queryModel.getInstrumentalMagnitudeBands());
        _instrumentalWavelengthLabel.setText("Wavelength ("
                + instrumentalMagnitudeBand + ") [µm] : ");
        _instrumentalWavelengthTextfield.setValue(_queryModel.getInstrumentalWavelength());
        _instrumentalMaxBaselineTextField.setValue(_queryModel.getInstrumentalMaxBaseLine());

        // Compute all the magnitude labels from the current magnitude band
        String magnitudeWithBand = "Magnitude (" + instrumentalMagnitudeBand
                + ") : ";

        // Science object parameters
        String scienceObjectName = _queryModel.getScienceObjectName();
        _scienceObjectNameTextfield.setText(scienceObjectName);
        _scienceObjectDECTextfield.setText(_queryModel.getScienceObjectDEC());
        _scienceObjectRATextfield.setText(_queryModel.getScienceObjectRA());
        _scienceObjectMagnitudeLabel.setText(magnitudeWithBand);
        _scienceObjectMagnitudeTextfield.setValue(_queryModel.getScienceObjectMagnitude());

        // SearchCal parameters
        _minMagnitudeLabel.setText("Min. " + magnitudeWithBand);
        _minMagnitudeTextfield.setValue(_queryModel.getQueryMinMagnitude());
        _maxMagnitudeLabel.setText("Max. " + magnitudeWithBand);
        _maxMagnitudeTextfield.setValue(_queryModel.getQueryMaxMagnitude());

        // Search box size handling
        _diffRASizeTextfield.setValue(_queryModel.getQueryDiffRASizeInMinutes());
        _diffDECSizeTextfield.setValue(_queryModel.getQueryDiffDECSizeInDegrees());
        _radialSizeTextfield.setValue(_queryModel.getQueryRadialSize());

        // Auto/Manual radius widgets handling
        boolean autoRadiusFlag = _queryModel.getQueryAutoRadiusFlag();
        _autoRadiusRadioButton.setSelected(autoRadiusFlag);
        _manualRadiusRadioButton.setSelected(!autoRadiusFlag);
        _radialSizeTextfield.setEnabled(!autoRadiusFlag);

        // Bright/faint scenarii handling
        boolean brightScenarioFlag = _queryModel.getQueryBrightScenarioFlag();
        _brightRadioButton.setSelected(brightScenarioFlag);
        _faintRadioButton.setSelected(!brightScenarioFlag);
        _diffRASizeTextfield.setVisible(brightScenarioFlag);
        _diffRASizeLabel.setVisible(brightScenarioFlag);
        _diffDECSizeTextfield.setVisible(brightScenarioFlag);
        _diffDECSizeLabel.setVisible(brightScenarioFlag);
        _autoRadiusRadioButton.setVisible(!brightScenarioFlag);
        _autoRadiusRadioButtonLabel.setVisible(!brightScenarioFlag);
        _manualRadiusRadioButton.setVisible(!brightScenarioFlag);
        _radialSizeTextfield.setVisible(!brightScenarioFlag);
        _radialSizeLabel.setVisible(!brightScenarioFlag);

        // Progress bar
        _progressBar.setValue(_queryModel.getCurrentStep());
        _progressBar.setMaximum(_queryModel.getTotalStep());
        _progressBar.setString(_queryModel.getCatalogName());

        // If the virtual obervatory is busy
        if (_vo.isQueryLaunched() == true) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        // If the query seems complete
        if (_queryModel.isConsumable() == true) {
            _vo.enableGetCalAction(true);
        } else {
            _vo.enableGetCalAction(false);
        }

        // If the magnitude band is either 'V', 'I', 'J', or 'H'
        if (instrumentalMagnitudeBand.matches("[V,I,J,H]") == true) {
            // Enable min & max magnitude textfields
            _minMagnitudeTextfield.setEnabled(true);
            _maxMagnitudeTextfield.setEnabled(true);

            // Disable bright/faint buttons
            _brightRadioButton.setEnabled(false);
            _faintRadioButton.setEnabled(false);

            // Select bright scenario
            _brightRadioButton.setSelected(true);
            _queryModel.setQueryBrightScenarioFlag(true);
        } else if (instrumentalMagnitudeBand.matches("N") == true) {
            // Disable min & max magnitude textfields
            _minMagnitudeTextfield.setEnabled(false);
            _maxMagnitudeTextfield.setEnabled(false);

            // Disable bright/faint buttons
            _brightRadioButton.setEnabled(false);
            _faintRadioButton.setEnabled(false);

            // Select bright scenario
            _brightRadioButton.setSelected(true);
            _queryModel.setQueryBrightScenarioFlag(true);
        } else {
            // Enable min & max magnitude textfields
            _minMagnitudeTextfield.setEnabled(true);
            _maxMagnitudeTextfield.setEnabled(true);

            // Enable bright/faint buttons
            _brightRadioButton.setEnabled(true);
            _faintRadioButton.setEnabled(true);
        }
    }

    /**
     * Called when the focus enters a widget.
     * @param e 
     */
    @Override
    public void focusGained(FocusEvent e) {
        // Does nothing (not needed)
    }

    /**
     * Called when the focus leaves a widget.
     *
     * Used to validate and store TextFields data when tabbing between them.
     *
     * @param fe focus event
     */
    @Override
    public void focusLost(FocusEvent fe) {
        _logger.entering("QueryView", "focusLost");

        // Store new data
        storeValues(fe);
    }

    /**
     * Called when a widget triggered an action.
     * @param ae action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        _logger.entering("QueryView", "actionPerformed");

        // Store new data
        storeValues(ae);
    }

    /**
     * Store form values in the model.
     *
     * @param ae awt event
     */
    public void storeValues(AWTEvent ae) {
        _logger.entering("QueryView", "storeValues");

        // Get back the widget
        Object source = ae.getSource();

        // If the widget is a JFormattedTextField
        if (source.getClass() == javax.swing.JFormattedTextField.class) {
            try {
                // Convert and commit the new value
                ((JFormattedTextField) source).commitEdit();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE, "Could not handle input", ex);
            }
        }

        // Check origin to mutualy exclude min and max magnitude as they depend on each other values
        if (source == _minMagnitudeTextfield) {
            _queryModel.setQueryMinMagnitude((Double) _minMagnitudeTextfield.getValue());
        }

        if (source == _maxMagnitudeTextfield) {
            _queryModel.setQueryMaxMagnitude((Double) _maxMagnitudeTextfield.getValue());
        }

        // Update instrumental wavelength only if the textfield was used
        // (done through the model when using _instrumentalMagnitudeBandCombo
        if (source == _instrumentalWavelengthTextfield) {
            _queryModel.setInstrumentalWavelength(((Double) _instrumentalWavelengthTextfield.getValue()));
        }

        // Try to inject user values into the model
        ComboBoxModel dcbm = _instrumentalMagnitudeBandCombo.getModel();
        String selectedMagnitudeBand = (String) dcbm.getSelectedItem();
        _queryModel.setInstrumentalMagnitudeBand(selectedMagnitudeBand);
        _queryModel.setInstrumentalMaxBaseLine(((Double) _instrumentalMaxBaselineTextField.getValue()));

        // validation exception handling:
        String validationMessages = "";
        try {
            _queryModel.setScienceObjectRA(_scienceObjectRATextfield.getText());
        } catch (IllegalArgumentException iae) {
            validationMessages += iae.getMessage() + "\n";
        }
        try {
            _queryModel.setScienceObjectDEC(_scienceObjectDECTextfield.getText());
        } catch (IllegalArgumentException iae) {
            validationMessages += iae.getMessage() + "\n";
        }
        if (validationMessages.length() > 0) {
            final String userMessage = validationMessages;
            // report messages to the user:
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    MessagePane.showErrorMessage(userMessage);
                }
            });
        }

        if (source == _scienceObjectNameTextfield) {
            _queryModel.setScienceObjectName(_scienceObjectNameTextfield.getText());
        }

        // Update science object magnitude only if the textfield was used
        if (source == _scienceObjectMagnitudeTextfield) {
            _queryModel.setScienceObjectMagnitude((Double) _scienceObjectMagnitudeTextfield.getValue());
        }

        _queryModel.setQueryDiffRASizeInMinutes((Double) _diffRASizeTextfield.getValue());
        _queryModel.setQueryDiffDECSizeInDegrees((Double) _diffDECSizeTextfield.getValue());
        _queryModel.setQueryRadialSize((Double) _radialSizeTextfield.getValue());

        // Refresh the whole view
        _queryModel.notifyObservers();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("query = " + _queryModel.getQueryAsMCSString());
        }
    }

    /**
     * Called when a field's "value" property changes.
     * @param e 
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        _logger.entering("QueryView", "propertyChange");

        boolean fileLoadedOk = (_queryModel.canBeEdited() == true);
        setEnabledComponents(_instrumentPanel, fileLoadedOk);
        setEnabledComponents(_actionPanel, fileLoadedOk);

        // Test if science object panel must be enabled
        boolean instrumentConfigOk = _instrumentalWavelengthTextfield.isEditValid()
                && (_instrumentalWavelengthTextfield.getText().length() > 0)
                && (_instrumentalMaxBaselineTextField.isEditValid() == true)
                && (_instrumentalMaxBaselineTextField.getText().length() > 0)
                && (_queryModel.canBeEdited() == true);
        setEnabledComponents(_scienceObjectPanel, instrumentConfigOk);

        // Test if SearchCal parameters panel must be enabled
        boolean sciencObjectOk = (instrumentConfigOk == true)
                && (_scienceObjectNameTextfield.getText().length() > 0)
                && (_queryModel.canBeEdited() == true);
        setEnabledComponents(_searchCalPanel, sciencObjectOk);

        repaint();
    }

    /**
     * Recursively enable/disable sub-components of a given container, or the
     * component itself if it does not contain any sub-component.
     *
     * @param component JComponent
     * @param flag boolean
     *
     * @TODO place it under common mcs area
     */
    public static void setEnabledComponents(JComponent component, boolean flag) {
        _logger.entering("QueryView", "setEnabledComponents");

        // If the given component contains sub-components
        if (component.getComponentCount() > 0) {
            // Get all the embedded sub-components
            Component[] components = component.getComponents();

            // For each sub-component
            for (int i = 0; i < components.length; i++) {
                // If the sub-component is not a combobox
                if (components[i].getClass() == JComboBox.class) {
                    // Try to enable/disable the combobox
                    ((JComponent) components[i]).setEnabled(flag);
                } else {
                    // Try to enable/disable any contained sub-sub-component
                    setEnabledComponents((JComponent) components[i], flag);
                }
            }
        } else // If the given component contains no sub-components
        {
            // Enable/disable the poor lonesome component
            component.setEnabled(flag);
        }
    }

    /**
     * @param graphics 
     * @param pageFormat 
     * @param pageIndex 
     * @return 
     * @throws PrinterException 
     * @sa java.awt.print
     */
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        _logger.entering("QueryView", "print");

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int fontHeight = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // laisser de l'espace pour le numero de page
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth = pageFormat.getImageableWidth();

        g2d.drawString("Page: " + (pageIndex + 1), ((int) pageWidth / 2) - 35,
                (int) ((pageHeight + fontHeight) - fontDescent));

        g2d.scale(0.7, 0.7);
        paint(g2d);

        return (Printable.PAGE_EXISTS);
    }

    protected class BrightQueryAction extends MCSAction {

        private static final long serialVersionUID = 1L;

        BrightQueryAction() {
            super("brightQuery");
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("QueryView", "actionPerformed");

            StatusBar.show("bright scenario selected.");
            _queryModel.setQueryBrightScenarioFlag(true);
            _queryModel.notifyObservers();
        }
    }

    protected class FaintQueryAction extends MCSAction {

        private static final long serialVersionUID = 1L;

        public FaintQueryAction() {
            super("faintQuery");
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("FaintQueryAction", "actionPerformed");

            StatusBar.show("faint scenario selected.");
            _queryModel.setQueryBrightScenarioFlag(false);
            _queryModel.notifyObservers();
        }
    }

    protected class AutoManualRadiusAction extends MCSAction {

        private static final long serialVersionUID = 1L;

        AutoManualRadiusAction() {
            super("autoManualRadius");
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("AutoManualRadiusAction", "actionPerformed");

            _queryModel.setQueryAutoRadiusFlag(_autoRadiusRadioButton.isSelected());
        }
    }

    protected class ResetValuesAction extends RegisteredAction {

        private static final long serialVersionUID = 1L;

        ResetValuesAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("ResetValuesAction", "actionPerformed");

            _queryModel.reset();
        }
    }

    protected class LoadDefaultValuesAction extends RegisteredAction {

        private static final long serialVersionUID = 1L;

        LoadDefaultValuesAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("LoadDefaultValuesAction", "actionPerformed");

            try {
                _queryModel.loadDefaultValues();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE, "LoadDefaultValuesAction error : ", ex);
            }
        }
    }

    protected class SaveValuesAction extends RegisteredAction {

        private static final long serialVersionUID = 1L;

        SaveValuesAction(String classPath, String fieldName) {
            super(classPath, fieldName);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("SaveValuesAction", "actionPerformed");

            try {
                _queryModel.saveDefaultValues();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE, "SaveValuesAction error : ", ex);
            }
        }
    }
}
/*___oOo___*/
