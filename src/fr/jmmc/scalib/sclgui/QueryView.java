/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryView.java,v 1.26 2006-11-27 15:51:01 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
package jmmc.scalib.sclgui;

import jmmc.mcs.gui.*;

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.*;

import java.util.*;
import java.util.logging.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;


/**
 * Query view.
 */
public class QueryView extends JPanel implements Observer,
    PropertyChangeListener, ActionListener, Printable
{
    /** Include Science Object action */
    static Action _includeScienceObjectAction;

    /** MVC associated model */
    public QueryModel _queryModel;

    /** Assocaited virtual observatory */
    VirtualObservatory _vo;

    /** Instrument panel */
    JPanel _instrumentPanel = new JPanel();

    /** Instrument magnitude band */
    JComboBox _instrumentalMagnitudeBandCombo = new JComboBox();

    /** Instrument wavelentgh */
    JFormattedTextField _instrumentalWavelengthTextfield = new JFormattedTextField(new Double(
                0));

    /** Instrument maximun baseline */
    JFormattedTextField _instrumentalMaxBaselineTextField = new JFormattedTextField(new Double(
                0));

    /** Science object panel */
    JPanel _scienceObjectPanel;

    /** Science object name */
    JFormattedTextField _scienceObjectNameTextfield = new JFormattedTextField(new MessageFormat(
                "{0}"));

    /** Science object right ascension coordinate */
    JTextField _scienceObjectRATextfield = new JTextField();

    /** Science object declinaison coordinate */
    JTextField _scienceObjectDECTextfield = new JTextField();

    /** Science object magnitude label */
    JLabel _scienceObjectMagnitudeLabel = new JLabel("Magnitude : ");

    /** Science object magnitude */
    JFormattedTextField _scienceObjectMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** SearchCal parameters panel */
    JPanel _searchCalPanel;

    /** Search minimum magnitude label */
    JLabel _minMagnitudeLabel = new JLabel("Min. Magnitude : ");

    /** Search minimum magnitude */
    JFormattedTextField _minMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search maximum magnitude label */
    JLabel _maxMagnitudeLabel = new JLabel("Max. Magnitude : ");

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
    JLabel _diffRASizeLabel = new JLabel("Diff RA (arcmin) : ");

    /** Search box RA size */
    JFormattedTextField _diffRASizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box DEC size label */
    JLabel _diffDECSizeLabel = new JLabel("Diff DEC (arcmin) : ");

    /** Search box DEC size */
    JFormattedTextField _diffDECSizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box radial size label */
    JLabel _radialSizeLabel = new JLabel("Radius (arcmin) : ");

    /** Search box radial size */
    JFormattedTextField _radialSizeTextfield = new JFormattedTextField(new Double(
                0));

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
        MCSLogger.trace();

        // Store the model
        _queryModel                     = queryModel;

        // Store the virtual observatory
        _vo                             = vo;

        // Init every actions
        _includeScienceObjectAction     = new IncludeScienceObjectAction();

        // Reset values action
        _resetValuesAction              = new ResetValuesAction();

        // Load default values action
        _loadDefaultValuesAction        = new LoadDefaultValuesAction();

        // Save values action
        _saveValuesAction               = new SaveValuesAction();

        // Form panel global attributes and common objects
        JPanel             tempPanel;
        Dimension          textfieldDimension = new Dimension(100, 20);
        GridBagConstraints c                  = new GridBagConstraints();
        c.fill                                = GridBagConstraints.HORIZONTAL;
        c.weightx                             = 1;

        // Instrumental Configuration panel
        _instrumentPanel.setBorder(new TitledBorder(
                "1)  Instrumental Configuration"));
        _instrumentPanel.setLayout(new GridBagLayout());
        // Magnitude band field
        c.gridy     = 0;
        c.gridx     = 0;
        _instrumentPanel.add(new JLabel("Magnitude Band : "), c);
        c.gridx = 1;
        _instrumentPanel.add(_instrumentalMagnitudeBandCombo, c);
        _instrumentalMagnitudeBandCombo.addActionListener(this);
        // Wavelength field
        c.gridy++;
        c.gridx = 0;
        _instrumentPanel.add(new JLabel("Wavelength [µm] : "), c);
        _instrumentalWavelengthTextfield.setMinimumSize(textfieldDimension);
        _instrumentalWavelengthTextfield.setPreferredSize(textfieldDimension);
        _instrumentalWavelengthTextfield.addActionListener(this);
        c.gridx = 1;
        _instrumentPanel.add(_instrumentalWavelengthTextfield, c);
        // Maximum baseline
        c.gridy++;
        c.gridx = 0;
        _instrumentPanel.add(new JLabel("Max. Baseline [m] : "), c);
        _instrumentalMaxBaselineTextField.setMinimumSize(textfieldDimension);
        _instrumentalMaxBaselineTextField.setPreferredSize(textfieldDimension);
        _instrumentalMaxBaselineTextField.addPropertyChangeListener(this);
        c.gridx = 1;
        _instrumentPanel.add(_instrumentalMaxBaselineTextField, c);

        // Science Object panel
        _scienceObjectPanel = new JPanel();
        _scienceObjectPanel.setBorder(new TitledBorder("2)  Science Object"));
        _scienceObjectPanel.setLayout(new GridBagLayout());
        // Star name field
        c.gridy     = 0;
        c.gridx     = 0;
        _scienceObjectPanel.add(new JLabel("Name : "), c);
        tempPanel = new JPanel(new GridBagLayout());
        _scienceObjectNameTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectNameTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectNameTextfield.addPropertyChangeListener(this);
        tempPanel.add(_scienceObjectNameTextfield, c);
        c.gridx = 1;
        tempPanel.add(new JButton(_vo._getStarAction));
        _scienceObjectPanel.add(tempPanel, c);
        // RA coordinate field
        c.gridy++;
        c.gridx = 0;
        _scienceObjectPanel.add(new JLabel("RA 2000 (mn) : "), c);
        _scienceObjectRATextfield.setMinimumSize(textfieldDimension);
        _scienceObjectRATextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectRATextfield, c);
        // DEG coordinate field
        c.gridy++;
        c.gridx = 0;
        _scienceObjectPanel.add(new JLabel("DEC 2000 (deg) : "), c);
        _scienceObjectDECTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectDECTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        _scienceObjectPanel.add(_scienceObjectDECTextfield, c);
        // Magnitude field
        c.gridy++;
        c.gridx = 0;
        _scienceObjectPanel.add(_scienceObjectMagnitudeLabel, c);
        _scienceObjectMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectMagnitudeTextfield.addPropertyChangeListener(this);
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
        _minMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _minMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _minMagnitudeTextfield.addActionListener(this);
        c.gridx = 1;
        _searchCalPanel.add(_minMagnitudeTextfield, c);
        // Maximum magnitude field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_maxMagnitudeLabel, c);
        _maxMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _maxMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _maxMagnitudeTextfield.addActionListener(this);
        c.gridx = 1;
        _searchCalPanel.add(_maxMagnitudeTextfield, c);
        // Bright/Faint Scenario
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(new JLabel("Scenario : "), c);
        tempPanel              = new JPanel();
        _brightRadioButton     = new JRadioButton(new BrightQueryAction());
        _brightFaintButtonGroup.add(_brightRadioButton);
        tempPanel.add(_brightRadioButton);
        _faintRadioButton = new JRadioButton(new FaintQueryAction());
        _brightFaintButtonGroup.add(_faintRadioButton);
        c.gridx = 1;
        tempPanel.add(_faintRadioButton);
        _searchCalPanel.add(tempPanel, c);
        // RA delta field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_diffRASizeLabel, c);
        _diffRASizeTextfield.setMinimumSize(textfieldDimension);
        _diffRASizeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        _searchCalPanel.add(_diffRASizeTextfield, c);
        // DEC delta field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_diffDECSizeLabel, c);
        _diffDECSizeTextfield.setMinimumSize(textfieldDimension);
        _diffDECSizeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        _searchCalPanel.add(_diffDECSizeTextfield, c);
        // Radial size field
        c.gridy++;
        c.gridx = 0;
        _searchCalPanel.add(_radialSizeLabel, c);
        _radialSizeTextfield.setMinimumSize(textfieldDimension);
        _radialSizeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        _searchCalPanel.add(_radialSizeTextfield, c);

        // Status panel global attributes and common objects
        JPanel queryStatusPanel = new JPanel();
        queryStatusPanel.setLayout(new BoxLayout(queryStatusPanel,
                BoxLayout.X_AXIS));
        // Fixed space
        queryStatusPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        // Progress label
        queryStatusPanel.add(new JLabel("Progress : "));
        // Fixed space
        queryStatusPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        // Progressbar
        _progressBar.setStringPainted(true);
        queryStatusPanel.add(_progressBar);
        // Fixed space
        queryStatusPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        // Search Button
        _searchButton = new JButton(_vo._getCalAction);
        queryStatusPanel.add(_searchButton);
        // Fixed space
        queryStatusPanel.add(Box.createRigidArea(new Dimension(15, 0)));

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
        add(queryStatusPanel, c);

        // Start listening to any updates of the model
        _queryModel.addObserver(this);

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

        // Computeall the magnitude labels from the current magnitude band
        String magnitudeWithBand = "Magnitude (" +
            _queryModel.getInstrumentalMagnitudeBand() + ") : ";

        // Instrumental parameters
        _instrumentalMagnitudeBandCombo.setModel(_queryModel.getInstrumentalMagnitudeBands());
        _instrumentalWavelengthTextfield.setValue(_queryModel.getInstrumentalWavelength());
        _instrumentalMaxBaselineTextField.setValue(_queryModel.getInstrumentalMaxBaseLine());

        // Science object parameters
        String scienceObjectName = _queryModel.getScienceObjectName();
        _scienceObjectNameTextfield.setText(scienceObjectName);

        // If the science object contains something
        if (scienceObjectName.length() < 1)
        {
            _vo._getStarAction.setEnabled(true);
        }
        else
        {
            _vo._getStarAction.setEnabled(false);
        }

        _scienceObjectDECTextfield.setText(_queryModel.getScienceObjectDEC());
        _scienceObjectRATextfield.setText(_queryModel.getScienceObjectRA());
        _scienceObjectMagnitudeLabel.setText(magnitudeWithBand);
        _scienceObjectMagnitudeTextfield.setValue(_queryModel.getScienceObjectMagnitude());

        // SearchCal parameters
        _minMagnitudeLabel.setText("Min. " + magnitudeWithBand);
        _minMagnitudeTextfield.setValue(_queryModel.getQueryMinMagnitude());
        _maxMagnitudeLabel.setText("Max. " + magnitudeWithBand);
        _maxMagnitudeTextfield.setValue(_queryModel.getQueryMaxMagnitude());

        // Bright/faint scenarii handling
        boolean brightScenarioFlag = _queryModel.getQueryBrightScenarioFlag();
        _brightRadioButton.setSelected(brightScenarioFlag);
        _diffRASizeTextfield.setValue(_queryModel.getQueryDiffRASize());
        _diffRASizeTextfield.setVisible(brightScenarioFlag);
        _diffRASizeLabel.setVisible(brightScenarioFlag);
        _diffDECSizeTextfield.setValue(_queryModel.getQueryDiffDECSize());
        _diffDECSizeTextfield.setVisible(brightScenarioFlag);
        _diffDECSizeLabel.setVisible(brightScenarioFlag);
        _faintRadioButton.setSelected(! brightScenarioFlag);
        _radialSizeTextfield.setValue(_queryModel.getQueryRadialSize());
        _radialSizeTextfield.setVisible(! brightScenarioFlag);
        _radialSizeLabel.setVisible(! brightScenarioFlag);

        // Progress bar
        _progressBar.setValue(_queryModel.getCurrentStep());
        _progressBar.setMaximum(_queryModel.getTotalStep());
        _progressBar.setString(_queryModel.getCurrentStatus());

        // If the query seems complete
        if (_queryModel.isConsumable() == true)
        {
            _vo._getCalAction.setEnabled(true);
        }
        else
        {
            _vo._getCalAction.setEnabled(false);
        }

        /*
           // @TODO : debug circular reference "Model<->View" leading to a stack explosion !!!
           // If the query model should not be edited (eg was loaded from file)
           if (_queryModel.getEditableState() == false)
           {
               // Disable all the query view components
               setEnabledComponents(this, false);
           }
         */
    }

    /** Called when a widget triggered an action. */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        Object source = e.getSource();

        if (source == _instrumentalMagnitudeBandCombo)
        {
            _queryModel.setInstrumentalMagnitudeBands((DefaultComboBoxModel) _instrumentalMagnitudeBandCombo.getModel());
        }

        if (source == _instrumentalWavelengthTextfield)
        {
            _queryModel.setInstrumentalWavelength(((Double) _instrumentalWavelengthTextfield.getValue()));
        }

        if (source == _minMagnitudeTextfield)
        {
            _queryModel.setQueryMinMagnitude((Double) _minMagnitudeTextfield.getValue());
        }

        if (source == _maxMagnitudeTextfield)
        {
            _queryModel.setQueryMaxMagnitude((Double) _maxMagnitudeTextfield.getValue());
        }
    }

    /** Called when a field's "value" property changes. */
    public void propertyChange(PropertyChangeEvent e)
    {
        MCSLogger.trace();

        Object source = e.getSource();

        // test if science object panel must be shown
        boolean instrumentConfigOk = _instrumentalWavelengthTextfield.isEditValid() &&
            (_instrumentalWavelengthTextfield.getText().length() > 0) &&
            _instrumentalMaxBaselineTextField.isEditValid() &&
            (_instrumentalMaxBaselineTextField.getText().length() > 0);
        setEnabledComponents(_scienceObjectPanel, instrumentConfigOk);

        // test if searchCal parameters must be shown 
        boolean sciencObjectOk = instrumentConfigOk &&
            (_scienceObjectNameTextfield.getText().length() > 0);

        setEnabledComponents(_searchCalPanel, sciencObjectOk);

        if (source == _instrumentalMaxBaselineTextField)
        {
            _queryModel.setInstrumentalMaxBaseLine(((Double) _instrumentalMaxBaselineTextField.getValue()));
        }

        // Try to inject user values into the model
        if (source == _scienceObjectNameTextfield)
        {
            _queryModel.setScienceObjectName(_scienceObjectNameTextfield.getText());
        }

        if (source == _scienceObjectRATextfield)
        {
            _queryModel.setScienceObjectRA(_scienceObjectRATextfield.getText());
        }

        if (source == _scienceObjectDECTextfield)
        {
            _queryModel.setScienceObjectDEC(_scienceObjectDECTextfield.getText());
        }

        if (source == _scienceObjectMagnitudeTextfield)
        {
            _queryModel.setScienceObjectMagnitude((Double) _scienceObjectMagnitudeTextfield.getValue());
        }

        if (source == _diffRASizeTextfield)
        {
            _queryModel.setQueryDiffRASize((Double) _diffRASizeTextfield.getValue());
        }

        if (source == _diffDECSizeTextfield)
        {
            _queryModel.setQueryDiffDECSize((Double) _diffDECSizeTextfield.getValue());
        }

        if (source == _radialSizeTextfield)
        {
            _queryModel.setQueryRadialSize((Double) _radialSizeTextfield.getValue());
        }
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
        // Disabled traces as it is recursively called !
        // MCSLogger.trace();

        // If the given component contains sub-components
        if (component.getComponentCount() > 0)
        {
            // Get all the embedded sub-components
            Component[] components = component.getComponents();

            // For each sub-component
            for (int i = 0; i < components.length; i++)
            {
                // If the sub-component is not a combobox
                // SL -> GM : pourquoi ?
                if (components[i].getClass() != JComboBox.class)
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
        }
    }

    protected class IncludeScienceObjectAction extends MCSAction
    {
        Preferences _preferences = Preferences.getInstance();

        public IncludeScienceObjectAction()
        {
            super("scienceObject");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (e.getSource() instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) e.getSource();
                boolean        b      = button.isSelected();

                // @todo add line to adjust QueryModel according b value
            }
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

            _queryModel.loadDefaultValues();
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

            _queryModel.saveDefaultValues();
        }
    }
}
/*___oOo___*/
