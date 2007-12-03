/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryView.java,v 1.48 2007-12-03 14:43:35 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.47  2007/10/05 07:31:22  lafrasse
 * Added min/max magnitude textfields disabled when 'N' magnitude band is selected.
 *
 * Revision 1.46  2007/10/04 15:04:00  lafrasse
 * Added bright/faint radio button disabled when 'V' magnitude band is selected.
 *
 * Revision 1.45  2007/09/19 12:11:28  lafrasse
 * Chnged Science Object name field to a standard JTextField
 *
 * Revision 1.44  2007/09/18 11:28:33  lafrasse
 * Handle undefined magnitudde values returned by Simbad CDS.
 *
 * Revision 1.43  2007/08/27 07:38:49  lafrasse
 * TextFileds label enhancement.
 *
 * Revision 1.42  2007/08/16 12:19:58  lafrasse
 * Enhanced precision of formatted textfields for double values.
 *
 * Revision 1.41  2007/08/03 13:11:41  lafrasse
 * Enhanced wavelengthes et and science object magnitudes management according to
 * comments from Daniel BONNEAU.
 *
 * Revision 1.40  2007/08/02 12:19:57  lafrasse
 * Corrected delayed GUI updated when changing bright/faint scenarion radio buttons
 * as noted in Denis MOURARD review comments.
 *
 * Revision 1.39  2007/08/02 09:11:16  lafrasse
 * Changed coordinates and box size units according to Daniel BONNEAU review
 * comments.
 *
 * Revision 1.38  2007/08/01 15:29:22  lafrasse
 * Added support for SIMDAD science object querying through URL+script (plus proof
 * of disfunctionning through SOAP).
 *
 * Revision 1.37  2007/06/29 09:57:23  lafrasse
 * Reomoed unimplemented 'Get Star' button.
 *
 * Revision 1.36  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.35  2007/04/12 15:51:11  lafrasse
 * Added value storing on widget focus leaves (when tabbing between fields).
 *
 * Revision 1.34  2007/04/11 13:52:57  lafrasse
 * Corrected a bug that prevented automatic update of wavelength on magnitude band
 * changes.
 * Corrected a bug that prevented some field value update.
 *
 * Revision 1.33  2007/02/16 17:17:18  lafrasse
 * Added support for true catalog namei queried.
 *
 * Revision 1.32  2007/02/16 15:20:54  lafrasse
 * Enhanced min & max magnitude interdependance.
 *
 * Revision 1.31  2007/02/13 16:17:58  lafrasse
 * Jalopyzation.
 *
 * Revision 1.30  2007/02/13 16:13:18  lafrasse
 * Rationalized field changes tracking.
 *
 * Revision 1.29  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.28  2006/12/04 12:32:18  lafrasse
 * Enhanced support for the whole query panel disabling on values loaded from file.
 *
 * Revision 1.27  2006/11/29 22:44:20  lafrasse
 * Added support for the whole query panel disabling on values loaded from file.
 *
 * Revision 1.26  2006/11/27 15:51:01  lafrasse
 * Added support for bright/faint button status change according to query model
 * values.
 *
 * Revision 1.25  2006/11/23 16:24:41  lafrasse
 * Added query parameters parsing and loading from VOTable files.
 *
 * Revision 1.24  2006/11/18 23:05:39  lafrasse
 * Handled SCAction change to MCSAction.
 * Added actions for ResetValuesAction, LoadDefaultValuesAction and
 * SaveValuesAction.
 *
 * Revision 1.23  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.22  2006/09/15 14:20:54  lafrasse
 * Added query default values support.
 *
 * Revision 1.21  2006/08/10 15:27:41  lafrasse
 * Code refinments
 * Used Double as default numeric type
 * Added full implementation of by-default auto-updated values
 * Streamlined magnitude bands and default wavelengthes support
 *
 * Revision 1.20  2006/08/09 14:22:25  lafrasse
 * Added dynamic labels with the band for all the magnitude textfields
 *
 * Revision 1.19  2006/08/09 13:10:15  lafrasse
 * Refined VirtualObservatory::getCal() API
 *
 * Revision 1.18  2006/08/04 16:53:54  lafrasse
 * Re-added preliminary print support
 *
 * Revision 1.17  2006/08/04 16:35:43  lafrasse
 * Added queryModel & filtersModel hooks in VirtualObservatory
 *
 * Revision 1.16  2006/07/28 08:35:11  mella
 * make it compile
 *
 * Revision 1.15  2006/07/19 16:11:41  lafrasse
 * Changed Formatted TextFields for better Double handling
 * Added "V" magnitude band
 *
 * Revision 1.14  2006/07/18 13:08:39  lafrasse
 * Jalopyzation
 *
 * Revision 1.13  2006/07/12 15:52:25  lafrasse
 * Corrected package import to resolve a comilation problem with
 * jmmc.mcs.util.StatusBar
 *
 * Revision 1.12  2006/07/12 14:31:26  lafrasse
 * Code refinment
 * Added the status bar
 *
 * Revision 1.11  2006/07/11 11:15:20  mella
 * add include science object action and use gridbaglayout manager
 *
 * Revision 1.10  2006/07/04 09:54:41  mella
 * Use a gridBagLayout for form
 *
 * Revision 1.8  2006/07/03 13:35:15  lafrasse
 * Streamlined code, and added support for min & max calibrators magnitude
 *
 * Revision 1.7  2006/06/30 08:02:33  lafrasse
 * Reformatted the whole code using Actions
 *
 * Revision 1.6  2006/06/12 08:44:34  mella
 * Set Magnitude textfield using  setValue instead of setText
 *
 * Revision 1.5  2006/06/09 13:35:10  mella
 * add one part of code to give a sample of requested tooltip usage
 *
 * Revision 1.4  2006/06/07 12:49:37  mella
 * Check magnitude fields format and ajust min,mag according magnitude changes
 *
 * Revision 1.3  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.2  2006/03/31 08:53:20  mella
 * Handle catalog origin color and confidence indexes from preferences
 * And jalopyzation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.gui.*;
import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.beans.*;

import java.net.URL;

import java.text.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;


/**
 * Query view.
 */
public class QueryView extends JPanel implements Observer,
    PropertyChangeListener, ActionListener, FocusListener, Printable
{
    /** MVC associated model */
    public QueryModel _queryModel;

    /** Assocaited virtual observatory */
    VirtualObservatory _vo;

    /** Instrument panel */
    JPanel _instrumentPanel = new JPanel();

    /** Instrument magnitude band */
    JComboBox _instrumentalMagnitudeBandCombo = new JComboBox();

    /** Instrument wavelentgh label */
    JLabel _instrumentalWavelengthLabel = new JLabel("Wavelength [µm] : ",
            JLabel.TRAILING);

    /** Instrument wavelentgh */
    JFormattedTextField _instrumentalWavelengthTextfield = new JFormattedTextField(new Double(
                0));

    /** Instrument maximun baseline */
    JFormattedTextField _instrumentalMaxBaselineTextField = new JFormattedTextField(new Double(
                0));

    /** Science object panel */
    JPanel _scienceObjectPanel;

    /** Science object name */
    JTextField _scienceObjectNameTextfield = new JTextField();

    /** Science object right ascension coordinate */
    JTextField _scienceObjectRATextfield = new JTextField();

    /** Science object declinaison coordinate */
    JTextField _scienceObjectDECTextfield = new JTextField();

    /** Science object magnitude label */
    JLabel _scienceObjectMagnitudeLabel = new JLabel("Magnitude : ",
            JLabel.TRAILING);

    /** Science object magnitude */
    JFormattedTextField _scienceObjectMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** SearchCal parameters panel */
    JPanel _searchCalPanel;

    /** Search minimum magnitude label */
    JLabel _minMagnitudeLabel = new JLabel("Min. Magnitude : ", JLabel.TRAILING);

    /** Search minimum magnitude */
    JFormattedTextField _minMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search maximum magnitude label */
    JLabel _maxMagnitudeLabel = new JLabel("Max. Magnitude : ", JLabel.TRAILING);

    /** Search maximum magnitude */
    JFormattedTextField _maxMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** Bright/Faint query radion button group */
    ButtonGroup _brightFaintButtonGroup = new ButtonGroup();

    /** Bright query button */
    JRadioButton _brightRadioButton;

    /** Faint query button */
    JRadioButton _faintRadioButton;

    /** Search box RA size label */
    JLabel _diffRASizeLabel = new JLabel("RA Range [arcmin] : ", JLabel.TRAILING);

    /** Search box RA size */
    JFormattedTextField _diffRASizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box DEC size label */
    JLabel _diffDECSizeLabel = new JLabel("DEC Range [arcmin] : ",
            JLabel.TRAILING);

    /** Search box DEC size */
    JFormattedTextField _diffDECSizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box radial size label */
    JLabel _radialSizeLabel = new JLabel("Radius [arcmin] : ", JLabel.TRAILING);

    /** Search box radial size */
    JFormattedTextField _radialSizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box auto radial size checkbox */
    JCheckBox _autoRadiusCheckBox;

    /** Query action and progression panel */
    JPanel _actionPanel = new JPanel();

    /** Query launcher/canceler */
    JButton _searchButton;

    /** Query progress bar */
    JProgressBar _progressBar = new JProgressBar();

    /** Reset Values action */
    public ResetValuesAction _resetValuesAction;

    /** Reset Values action */
    public LoadDefaultValuesAction _loadDefaultValuesAction;

    /** Reset Values action */
    public SaveValuesAction _saveValuesAction;

    /**
     * Constructor.
     *
     * @param model the object used to store all the query attributes.
     * @param vo the object used to query the JMMC GETCAL webservice.
     */
    public QueryView(QueryModel queryModel, VirtualObservatory vo)
    {
        // Store the model
        _queryModel                  = queryModel;

        // Store the virtual observatory
        _vo                          = vo;

        // Reset values action
        _resetValuesAction           = new ResetValuesAction();

        // Load default values action
        _loadDefaultValuesAction     = new LoadDefaultValuesAction();

        // Save values action
        _saveValuesAction            = new SaveValuesAction();

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
        JPanel             tempPanel;
        Dimension          textfieldDimension         = new Dimension(100, 20);
        GridBagConstraints c                          = new GridBagConstraints();
        c.fill                                        = GridBagConstraints.HORIZONTAL;
        c.weightx                                     = 1;

        // Instrumental Configuration panel
        _instrumentPanel.setBorder(new TitledBorder(
                "1)  Instrumental Configuration"));
        _instrumentPanel.setLayout(new GridBagLayout());
        // Magnitude band field
        c.gridy     = 0;
        c.gridx     = 0;
        label       = new JLabel("Magnitude Band : ", JLabel.TRAILING);
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
        c.gridx     = 0;
        label       = new JLabel("Max. Baseline [m] : ", JLabel.TRAILING);
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
        // Star name field
        c.gridy     = 0;
        c.gridx     = 0;
        label       = new JLabel("Name : ", JLabel.TRAILING);
        _scienceObjectPanel.add(label, c);
        label.setLabelFor(_scienceObjectNameTextfield);
        tempPanel = new JPanel(new GridBagLayout());
        _scienceObjectNameTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectNameTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectNameTextfield.addActionListener(this);
        _scienceObjectNameTextfield.addFocusListener(this);
        tempPanel.add(_scienceObjectNameTextfield, c);
        c.gridx = 1;
        /* @TODO : decide whether we should display icon, text or icon+text
           String fullIconPath = "search.png";
           URL imgURL = getClass().getResource(fullIconPath);
           _vo._getStarAction.putValue(Action.SMALL_ICON, new ImageIcon(imgURL));
         */
        tempPanel.add(new JButton(_vo._getStarAction));
        _scienceObjectPanel.add(tempPanel, c);
        // RA coordinate field
        c.gridy++;
        c.gridx     = 0;
        label       = new JLabel("RA 2000 [hh:mm:ss] : ", JLabel.TRAILING);
        _scienceObjectPanel.add(label, c);
        label.setLabelFor(_scienceObjectRATextfield);
        _scienceObjectRATextfield.setMinimumSize(textfieldDimension);
        _scienceObjectRATextfield.setPreferredSize(textfieldDimension);
        _scienceObjectRATextfield.addActionListener(this);
        _scienceObjectRATextfield.addFocusListener(this);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectRATextfield, c);
        // DEG coordinate field
        c.gridy++;
        c.gridx     = 0;
        label       = new JLabel("DEC 2000 [+/-dd:mm:ss] : ", JLabel.TRAILING);
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
        c.gridy     = 0;
        c.gridx     = 0;
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
        c.gridx     = 0;
        label       = new JLabel("Scenario : ", JLabel.TRAILING);
        _searchCalPanel.add(label, c);
        tempPanel              = new JPanel();
        _brightRadioButton     = new JRadioButton(new BrightQueryAction());
        _brightFaintButtonGroup.add(_brightRadioButton);
        tempPanel.add(_brightRadioButton);
        _faintRadioButton = new JRadioButton(new FaintQueryAction());
        _brightFaintButtonGroup.add(_faintRadioButton);
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
        _radialSizeLabel.setLabelFor(_radialSizeTextfield);
        tempPanel               = new JPanel();
        _autoRadiusCheckBox     = new JCheckBox(new AutoRadiusAction());
        tempPanel.add(_autoRadiusCheckBox);
        _radialSizeTextfield.setEnabled(false);
        _radialSizeTextfield.setFormatterFactory(doubleFormaterFactory);
        _radialSizeTextfield.setMinimumSize(textfieldDimension);
        _radialSizeTextfield.setPreferredSize(textfieldDimension);
        _radialSizeTextfield.addActionListener(this);
        _radialSizeTextfield.addFocusListener(this);
        tempPanel.add(_radialSizeTextfield);
        c.gridx = 1;
        _searchCalPanel.add(tempPanel, c);

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
        c.anchor     = GridBagConstraints.PAGE_START; //bottom of space
        c.gridy      = 0;
        c.gridx      = 0;
        add(_instrumentPanel, c);
        c.gridx++;
        add(_scienceObjectPanel, c);
        c.gridx++;
        add(_searchCalPanel, c);
        c.gridy++;
        c.gridx         = 0;
        c.gridwidth     = 3;
        add(_actionPanel, c);

        // Start listening to any updates of the monitored objects
        _queryModel.addObserver(this);
        _vo.addObserver(this);

        // Refresh the GUI with the model values
        update(null, null);
        update(null, null);
    }

    /**
     * Automatically called on attached QueryModel changes.
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        String instrumentalMagnitudeBand = _queryModel.getInstrumentalMagnitudeBand();

        // Instrumental parameters
        _instrumentalMagnitudeBandCombo.setModel(_queryModel.getInstrumentalMagnitudeBands());
        _instrumentalWavelengthLabel.setText("Wavelength (" +
            instrumentalMagnitudeBand + ") [µm] : ");
        _instrumentalWavelengthTextfield.setValue(_queryModel.getInstrumentalWavelength());
        _instrumentalMaxBaselineTextField.setValue(_queryModel.getInstrumentalMaxBaseLine());

        // Compute all the magnitude labels from the current magnitude band
        String magnitudeWithBand = "Magnitude (" + instrumentalMagnitudeBand +
            ") : ";

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
        _diffRASizeTextfield.setValue(_queryModel.getQueryDiffRASize());
        _diffDECSizeTextfield.setValue(_queryModel.getQueryDiffDECSize());
        _radialSizeTextfield.setValue(_queryModel.getQueryRadialSize());

        boolean autoRadiusFlag = _queryModel.getQueryAutoRadiusFlag();
        _radialSizeTextfield.setEnabled(! autoRadiusFlag);
        _autoRadiusCheckBox.setSelected(autoRadiusFlag);

        // Bright/faint scenarii handling
        boolean brightScenarioFlag = _queryModel.getQueryBrightScenarioFlag();
        _brightRadioButton.setSelected(brightScenarioFlag);
        _faintRadioButton.setSelected(! brightScenarioFlag);
        _diffRASizeTextfield.setVisible(brightScenarioFlag);
        _diffRASizeLabel.setVisible(brightScenarioFlag);
        _diffDECSizeTextfield.setVisible(brightScenarioFlag);
        _diffDECSizeLabel.setVisible(brightScenarioFlag);
        _radialSizeTextfield.setVisible(! brightScenarioFlag);
        _radialSizeLabel.setVisible(! brightScenarioFlag);
        _autoRadiusCheckBox.setVisible(! brightScenarioFlag);

        // Progress bar
        _progressBar.setValue(_queryModel.getCurrentStep());
        _progressBar.setMaximum(_queryModel.getTotalStep());
        _progressBar.setString(_queryModel.getCatalogName());

        // If the virtual obervatory is busy
        if (_vo.isBusy() == true)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        // If the science object contains something
        if (scienceObjectName.length() > 0)
        {
            _vo._getStarAction.setEnabled(true);
        }
        else
        {
            _vo._getStarAction.setEnabled(false);
        }

        // If the query seems complete
        if (_queryModel.isConsumable() == true)
        {
            _vo._getCalAction.setEnabled(true);
        }
        else
        {
            _vo._getCalAction.setEnabled(false);
        }

        // If the magnitude band is 'V'
        if (_queryModel.getInstrumentalMagnitudeBand().matches("V") == true)
        {
            // Enable min & max magnitude textfields
            _minMagnitudeTextfield.setEnabled(true);
            _maxMagnitudeTextfield.setEnabled(true);

            // Disable bright/faint buttons
            _brightRadioButton.setEnabled(false);
            _faintRadioButton.setEnabled(false);

            // Select bright scenario
            _brightRadioButton.setSelected(true);
            _queryModel.setQueryBrightScenarioFlag(true);
        }
        else if (_queryModel.getInstrumentalMagnitudeBand().matches("N") == true)
        {
            // Disable min & max magnitude textfields
            _minMagnitudeTextfield.setEnabled(false);
            _maxMagnitudeTextfield.setEnabled(false);

            // Disable bright/faint buttons
            _brightRadioButton.setEnabled(false);
            _faintRadioButton.setEnabled(false);

            // Select bright scenario
            _brightRadioButton.setSelected(true);
            _queryModel.setQueryBrightScenarioFlag(true);
        }
        else
        {
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
        MCSLogger.trace();

        // Get back the widget
        Object source = e.getSource();

        // If the widget is a JFormattedTextField
        if (source.getClass() == javax.swing.JFormattedTextField.class)
        {
            try
            {
                // Convert and commit the new value
                ((JFormattedTextField) source).commitEdit();
            }
            catch (Exception ex)
            {
                MCSLogger.error("Could not handle input");
            }
        }

        // Check origin to mutualy exclude min and max magnitude as they depend on each other values
        if (source == _minMagnitudeTextfield)
        {
            _queryModel.setQueryMinMagnitude((Double) _minMagnitudeTextfield.getValue());
        }

        if (source == _maxMagnitudeTextfield)
        {
            _queryModel.setQueryMaxMagnitude((Double) _maxMagnitudeTextfield.getValue());
        }

        // Update instrumental wavelength only if the textfield was used
        // (done through the model when using _instrumentalMagnitudeBandCombo
        if (source == _instrumentalWavelengthTextfield)
        {
            _queryModel.setInstrumentalWavelength(((Double) _instrumentalWavelengthTextfield.getValue()));
        }

        // Try to inject user values into the model
        _queryModel.setInstrumentalMagnitudeBands((DefaultComboBoxModel) _instrumentalMagnitudeBandCombo.getModel());
        _queryModel.setInstrumentalMaxBaseLine(((Double) _instrumentalMaxBaselineTextField.getValue()));
        _queryModel.setScienceObjectName(_scienceObjectNameTextfield.getText());
        _queryModel.setScienceObjectRA(_scienceObjectRATextfield.getText());
        _queryModel.setScienceObjectDEC(_scienceObjectDECTextfield.getText());

        // Update science object magnitude only if the textfield was used
        // (done through the model when using _instrumentalMagnitudeBandCombo
        if (source == _scienceObjectMagnitudeTextfield)
        {
            _queryModel.setScienceObjectMagnitude((Double) _scienceObjectMagnitudeTextfield.getValue());
        }

        _queryModel.setQueryDiffRASize((Double) _diffRASizeTextfield.getValue());
        _queryModel.setQueryDiffDECSize((Double) _diffDECSizeTextfield.getValue());
        _queryModel.setQueryRadialSize((Double) _radialSizeTextfield.getValue());

        // Refress the whole view
        _queryModel.notifyObservers();

        MCSLogger.debug("query = " + _queryModel.getQueryAsMCSString());
    }

    /**
     * Called when a field's "value" property changes.
     */
    public void propertyChange(PropertyChangeEvent e)
    {
        MCSLogger.trace();

        boolean fileLoadedOk = (_queryModel.canBeEdited() == true);
        setEnabledComponents(_instrumentPanel, fileLoadedOk);
        setEnabledComponents(_actionPanel, fileLoadedOk);

        // Test if science object panel must be enabled
        boolean instrumentConfigOk = _instrumentalWavelengthTextfield.isEditValid() &&
            (_instrumentalWavelengthTextfield.getText().length() > 0) &&
            (_instrumentalMaxBaselineTextField.isEditValid() == true) &&
            (_instrumentalMaxBaselineTextField.getText().length() > 0) &&
            (_queryModel.canBeEdited() == true);
        setEnabledComponents(_scienceObjectPanel, instrumentConfigOk);

        // Test if SearchCal parameters panel must be enabled
        boolean sciencObjectOk = (instrumentConfigOk == true) &&
            (_scienceObjectNameTextfield.getText().length() > 0) &&
            (_queryModel.canBeEdited() == true);
        setEnabledComponents(_searchCalPanel, sciencObjectOk);

        repaint();
    }

    /**
     * Recursively enable/disable sub-components of a given container, or the
     * component itself if it does not contain any sub-component.
     *
     * @param compo JComponent
     * @param bool boolean
     *
     * @TODO place it under common mcs area
     */
    public static void setEnabledComponents(JComponent component, boolean flag)
    {
        MCSLogger.trace();

        // If the given component contains sub-components
        if (component.getComponentCount() > 0)
        {
            // Get all the embedded sub-components
            Component[] components = component.getComponents();

            // For each sub-component
            for (int i = 0; i < components.length; i++)
            {
                // If the sub-component is not a combobox
                if (components[i].getClass() == JComboBox.class)
                {
                    // Try to enable/disable the combobox
                    ((JComponent) components[i]).setEnabled(flag);
                }
                else
                {
                    // Try to enable/disable any contained sub-sub-component
                    setEnabledComponents((JComponent) components[i], flag);
                }
            }
        }
        else // If the given component contains no sub-components
        {
            // Enable/disable the poor lonesome component
            component.setEnabled(flag);
        }
    }

    /**
     * @sa java.awt.print
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
        throws PrinterException
    {
        MCSLogger.trace();

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int fontHeight  = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // laisser de l'espace pour le numero de page
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth  = pageFormat.getImageableWidth();

        g2d.drawString("Page: " + (pageIndex + 1), ((int) pageWidth / 2) - 35,
            (int) ((pageHeight + fontHeight) - fontDescent));

        g2d.scale(0.7, 0.7);
        paint(g2d);

        return (Printable.PAGE_EXISTS);
    }

    protected class BrightQueryAction extends MCSAction
    {
        public BrightQueryAction()
        {
            super("brightQuery");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            StatusBar.show("bright scenario selected.");
            _queryModel.setQueryBrightScenarioFlag(true);
            _queryModel.notifyObservers();
        }
    }

    protected class FaintQueryAction extends MCSAction
    {
        public FaintQueryAction()
        {
            super("faintQuery");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            StatusBar.show("faint scenario selected.");
            _queryModel.setQueryBrightScenarioFlag(false);
            _queryModel.notifyObservers();
        }
    }

    protected class AutoRadiusAction extends MCSAction
    {
        public AutoRadiusAction()
        {
            super("autoRadius");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _queryModel.setQueryAutoRadiusFlag(_autoRadiusCheckBox.isSelected());
        }
    }

    protected class ResetValuesAction extends MCSAction
    {
        public ResetValuesAction()
        {
            super("resetQueryValues");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _queryModel.reset();
        }
    }

    protected class LoadDefaultValuesAction extends MCSAction
    {
        public LoadDefaultValuesAction()
        {
            super("loadDefaultQueryValues");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            try
            {
                _queryModel.loadDefaultValues();
            }
            catch (Exception ex)
            {
                MCSLogger.error("LoadDefaultValuesAction error : " + ex);
            }
        }
    }

    protected class SaveValuesAction extends MCSAction
    {
        public SaveValuesAction()
        {
            super("saveQueryValues");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            try
            {
                _queryModel.saveDefaultValues();
            }
            catch (Exception ex)
            {
                MCSLogger.error("SaveValuesAction error : " + ex);
            }
        }
    }
}
/*___oOo___*/
