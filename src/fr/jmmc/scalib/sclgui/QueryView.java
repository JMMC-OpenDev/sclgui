/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryView.java,v 1.17 2006-08-04 16:35:43 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.StatusBar;

import java.awt.*;
import java.awt.event.*;

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
    PropertyChangeListener, ActionListener
{
    /**
     * bandToWavelength hashtable
     */
    static Hashtable bandToWavelength = new Hashtable();

    /** include science object action */
    static Action _includeScienceObjectAction;

    /** MVC associated model */
    QueryModel _queryModel;

    /** Assocaited virtual observatory */
    VirtualObservatory _vo;

    /** Instrument panel */
    JPanel instrumentPanel = new JPanel();

    /** Instrument magnitude band */
    JComboBox _instrumentalMagnitudeBandCombo;

    /** Instrument wavelentgh */
    JFormattedTextField _instrumentalWavelengthTextfield = new JFormattedTextField(new Double(
                0));

    /** Instrument maximun baseline */
    JFormattedTextField _instrumentalMaxBaselineTextField = new JFormattedTextField(new Double(
                0));

    /** Science object panel */
    JPanel scienceObjectPanel;

    /** Science object name */
    JFormattedTextField _scienceObjectNameTextfield = new JFormattedTextField(new MessageFormat(
                "{0}"));

    /** Science object right ascension coordinate */
    JTextField _scienceObjectRATextfield = new JTextField();

    /** Science object declinaison coordinate */
    JTextField _scienceObjectDECTextfield = new JTextField();

    /** Science object magnitude */
    JFormattedTextField _scienceObjectMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** SearchCal parameters panel */
    JPanel searchCalPanel;

    /** Search minimum magnitude */
    JFormattedTextField _minMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search maximum magnitude */
    JFormattedTextField _maxMagnitudeTextfield = new JFormattedTextField(new Double(
                0));

    /** Bright/Faint query radion button group */
    ButtonGroup _brightFaintButtonGroup = new ButtonGroup();

    /** Bright query button */
    JRadioButton _brightRadioButton;

    /** Faint query button */
    JRadioButton _faintRadioButton;

    /** Search box RA size */
    JFormattedTextField _diffRASizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box DEC size */
    JFormattedTextField _diffDECSizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Search box radial size */
    JFormattedTextField _radialSizeTextfield = new JFormattedTextField(new Double(
                0));

    /** Query launcher/canceler */
    JButton _searchButton;

    /** Query progress bar */
    JProgressBar _progressBar = new JProgressBar();

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

        // Fill bands and correspondance with wavelengths
        String[] bands       = { "I", "J", "H", "K", "V" };
        String[] wavelengths = { "1.1", "2.2", "3.3", "4.4", "5.5" };

        for (int i = 0; i < bands.length; i++)
        {
            bandToWavelength.put(bands[i], wavelengths[i]);
        }

        // Form panel global attributes and common objects
        JPanel             tempPanel;
        Dimension          textfieldDimension = new Dimension(100, 20);
        GridBagConstraints c                  = new GridBagConstraints();
        c.fill                                = GridBagConstraints.HORIZONTAL;
        c.weightx                             = 1;

        // Instrumental Configuration panel
        instrumentPanel.setBorder(new TitledBorder(
                "1)  Instrumental Configuration"));
        instrumentPanel.setLayout(new GridBagLayout());
        // Magnitude band field
        c.gridy     = 0;
        c.gridx     = 0;
        instrumentPanel.add(new JLabel("Magnitude Band : "), c);
        _instrumentalMagnitudeBandCombo = new JComboBox(bands);
        _instrumentalMagnitudeBandCombo.setSelectedIndex(3);
        _instrumentalMagnitudeBandCombo.addActionListener(this);
        c.gridx = 1;
        instrumentPanel.add(_instrumentalMagnitudeBandCombo, c);
        // Wavelength field
        c.gridy++;
        c.gridx = 0;
        instrumentPanel.add(new JLabel("Wavelength [µm] : "), c);
        _instrumentalWavelengthTextfield.setMinimumSize(textfieldDimension);
        _instrumentalWavelengthTextfield.setPreferredSize(textfieldDimension);
        _instrumentalWavelengthTextfield.addPropertyChangeListener(this);
        c.gridx = 1;
        instrumentPanel.add(_instrumentalWavelengthTextfield, c);
        // Maximum baseline
        c.gridy++;
        c.gridx = 0;
        instrumentPanel.add(new JLabel("Max. Baseline [m] : "), c);
        _instrumentalMaxBaselineTextField.setMinimumSize(textfieldDimension);
        _instrumentalMaxBaselineTextField.setPreferredSize(textfieldDimension);
        _instrumentalMaxBaselineTextField.addPropertyChangeListener(this);
        c.gridx = 1;
        instrumentPanel.add(_instrumentalMaxBaselineTextField, c);

        // Science Object panel
        scienceObjectPanel = new JPanel();
        scienceObjectPanel.setBorder(new TitledBorder("2)  Science Object"));
        scienceObjectPanel.setLayout(new GridBagLayout());
        // Star name field
        c.gridy     = 0;
        c.gridx     = 0;
        scienceObjectPanel.add(new JLabel("Name : "), c);
        tempPanel = new JPanel(new GridBagLayout());
        _scienceObjectNameTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectNameTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectNameTextfield.addPropertyChangeListener(this);
        tempPanel.add(_scienceObjectNameTextfield, c);
        c.gridx = 1;
        tempPanel.add(new JButton(new SearchScienceObjectAction()));
        scienceObjectPanel.add(tempPanel, c);
        // RA coordinate field
        c.gridy++;
        c.gridx = 0;
        scienceObjectPanel.add(new JLabel("RA 2000 (mn) : "), c);
        _scienceObjectRATextfield.setMinimumSize(textfieldDimension);
        _scienceObjectRATextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        scienceObjectPanel.add(_scienceObjectRATextfield, c);
        // DEG coordinate field
        c.gridy++;
        c.gridx = 0;
        scienceObjectPanel.add(new JLabel("DEC 2000 (deg) : "), c);
        _scienceObjectDECTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectDECTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        scienceObjectPanel.add(_scienceObjectDECTextfield, c);
        // Magnitude field
        c.gridy++;
        c.gridx = 0;
        scienceObjectPanel.add(new JLabel("Magnitude : "), c);
        _scienceObjectMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _scienceObjectMagnitudeTextfield.setPreferredSize(textfieldDimension);
        _scienceObjectMagnitudeTextfield.addPropertyChangeListener(this);
        c.gridx = 1;
        scienceObjectPanel.add(_scienceObjectMagnitudeTextfield, c);
        // Searching Parameters panel
        searchCalPanel = new JPanel();
        searchCalPanel.setBorder(new TitledBorder("3)  SearchCal Parameters"));
        searchCalPanel.setLayout(new GridBagLayout());
        // Minimum magnitude field
        c.gridy     = 0;
        c.gridx     = 0;
        searchCalPanel.add(new JLabel("Min. Magnitude : "), c);
        _minMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _minMagnitudeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_minMagnitudeTextfield, c);
        // Maximum magnitude field
        c.gridy++;
        c.gridx = 0;
        searchCalPanel.add(new JLabel("Max. Magnitude : "), c);
        _maxMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _maxMagnitudeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_maxMagnitudeTextfield, c);
        // Bright/Faint Scenario
        c.gridy++;
        c.gridx = 0;
        searchCalPanel.add(new JLabel("Scenario : "), c);
        tempPanel              = new JPanel();
        _brightRadioButton     = new JRadioButton(new BrightQueryAction());
        _brightRadioButton.setSelected(true);
        _brightFaintButtonGroup.add(_brightRadioButton);
        tempPanel.add(_brightRadioButton);
        _faintRadioButton = new JRadioButton(new FaintQueryAction());
        _brightFaintButtonGroup.add(_faintRadioButton);
        c.gridx = 1;
        tempPanel.add(_faintRadioButton);
        searchCalPanel.add(tempPanel, c);
        // RA delta field
        c.gridy++;
        c.gridx = 0;
        searchCalPanel.add(new JLabel("Diff RA (arcmin) : "), c);
        _diffRASizeTextfield.setMinimumSize(textfieldDimension);
        _diffRASizeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_diffRASizeTextfield, c);
        // DEC delta field
        c.gridy++;
        c.gridx = 0;
        searchCalPanel.add(new JLabel("Diff DEC (arcmin) : "), c);
        _diffDECSizeTextfield.setMinimumSize(textfieldDimension);
        _diffDECSizeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        // @TODO : radial size field
        searchCalPanel.add(_diffDECSizeTextfield, c);

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
        _searchButton = new JButton(new SearchCalibratorsAction());
        queryStatusPanel.add(_searchButton);
        // Fixed space
        queryStatusPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        // Query panel global construction
        setBorder(new TitledBorder("Query Parameters"));
        setLayout(new GridBagLayout());
        c.anchor     = GridBagConstraints.PAGE_START; //bottom of space
        c.gridy      = 0;
        c.gridx      = 0;
        add(instrumentPanel, c);
        c.gridx++;
        add(scienceObjectPanel, c);
        c.gridx++;
        add(searchCalPanel, c);
        c.gridy++;
        c.gridx         = 0;
        c.gridwidth     = 3;
        add(queryStatusPanel, c);

        // Start listening to any updates of the model
        _queryModel.addObserver(this);
    }

    /**
     * Automatically called on attached QueryModel changes.
     */
    public void update(Observable o, Object arg)
    {
        MCSLogger.trace();

        // Next line could be skipped because we already know the observable
        if (o instanceof QueryModel)
        {
            QueryModel queryModel = (QueryModel) o;

            // Instrumental parameters
            // @TODO : _instrumentalMagnitudeBandCombo.set???(queryModel.getInstrumentalMagnitudeBand());
            _instrumentalWavelengthTextfield.setValue(new Double(
                    queryModel.getInstrumentalWavelength()));
            _instrumentalMaxBaselineTextField.setValue(new Double(
                    queryModel.getInstrumentalMaxBaseLine()));

            // Science object parameters
            _scienceObjectNameTextfield.setText(queryModel.getScienceObjectName());
            _scienceObjectDECTextfield.setText(queryModel.getScienceObjectDEC());
            _scienceObjectRATextfield.setText(queryModel.getScienceObjectRA());
            _scienceObjectMagnitudeTextfield.setValue(new Double(
                    queryModel.getScienceObjectMagnitude()));

            // SearchCal parameters
            _minMagnitudeTextfield.setValue(new Double(
                    queryModel.getQueryMinMagnitude()));
            _maxMagnitudeTextfield.setValue(new Double(
                    queryModel.getQueryMaxMagnitude()));
            // @TODO : _brightFaintButtonGroup.set???(queryModel.isQueryScenarioBright()));
            _diffRASizeTextfield.setValue(new Double(
                    queryModel.getQueryDiffRASize()));
            _diffDECSizeTextfield.setValue(new Double(
                    queryModel.getQueryDiffDECSize()));
            _radialSizeTextfield.setValue(new Double(
                    queryModel.getQueryRadialSize()));

            // TODO : link to bright/faint radio buttons, and all the other missing fields
            _progressBar.setValue(queryModel.getCurrentStep());
            _progressBar.setMaximum(queryModel.getTotalStep());
            _progressBar.setString(queryModel.getCurrentStatus());
        }
    }

    /** Called when a widget triggered an action. */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        Object source = e.getSource();

        if (source == _instrumentalMagnitudeBandCombo)
        {
            String comboValue      = (String) _instrumentalMagnitudeBandCombo.getSelectedItem();
            String wavelengthValue = (String) bandToWavelength.get(comboValue);
            _instrumentalWavelengthTextfield.setText(wavelengthValue);
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
        setEnabledComponents(scienceObjectPanel, instrumentConfigOk);

        // test if searchCal parameters must be shown 
        boolean sciencObjectOk = instrumentConfigOk &&
            (_scienceObjectNameTextfield.getText().length() > 0);

        setEnabledComponents(searchCalPanel, sciencObjectOk);

        if (source == _instrumentalWavelengthTextfield)
        {
            _queryModel.setInstrumentalWavelength(((Double) _instrumentalWavelengthTextfield.getValue()).doubleValue());
        }

        if (source == _instrumentalMaxBaselineTextField)
        {
            _queryModel.setInstrumentalMaxBaseLine(((Double) _instrumentalMaxBaselineTextField.getValue()).doubleValue());
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

        if (source == _minMagnitudeTextfield)
        {
            //            _queryModel.setQueryMinMagnitude((Double) _minMagnitudeTextfield.getValue());
        }

        if (source == _maxMagnitudeTextfield)
        {
            //            _queryModel.setQueryMaxMagnitude((Double) _maxMagnitudeTextfield.getValue());
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
     * Read the model to update view according status changes.
     */
    private void updateStatusView(QueryModel queryModel)
    {
        MCSLogger.trace();

        /*
           // Depending on the queryModel state, change view...
           if (false)
           { // TODO Change condition
             //Prepare barprogress
               _progressBar.setVisible(true);
               _progressBar.setIndeterminate(true);
               labelBlank.setVisible(false);
               //change cursor
               setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               //panels enabled false
               setEnabledComponents(this, false);
               StatusBar.show("query is beeing resolved ... please wait.");
           }
           if (false)
           { // TODO Change condiion
             //default cursor
               setCursor(Cursor.getDefaultCursor());
               //begin state
               labelBlank.setVisible(true);
               _progressBar.setVisible(false);
               _progressBar.setIndeterminate(false);
               //panel enabled true
               setEnabledComponents(this, true);
               // Should get completion status to indicate proper work or not...TBD
               // @TODO : show time elapsed ?
               StatusBar.show("query has been resolved successfully.");
           }
           if (false)
           { // TODO Change condiion
             //default cursor
               setCursor(Cursor.getDefaultCursor());
               //begin state
               labelBlank.setVisible(true);
               _progressBar.setVisible(false);
               _progressBar.setIndeterminate(false);
               //panel enabled true
               setEnabledComponents(this, true);
               // Should get completion status to indicate proper work or not...TBD
               StatusBar.show("query has not been resolved.");
           }
         */
    }

    protected class SearchScienceObjectAction extends SCAction
    {
        public SearchScienceObjectAction()
        {
            super("searchScienceObject");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            if (_scienceObjectNameTextfield.getText().length() == 0)
            {
                _queryModel.example();
            }
            else
            {
                try
                {
                    _vo.getScienceObject();
                }
                catch (Exception ex)
                {
                    StatusBar.show("GETSTAR webservice unreachable.");
                    ex.printStackTrace();
                }
            }
        }
    }

    protected class BrightQueryAction extends SCAction
    {
        public BrightQueryAction()
        {
            super("brightQuery");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _diffRASizeTextfield.setEnabled(true);
            _diffDECSizeTextfield.setEnabled(true);
            StatusBar.show("bright scenario selected.");
            _queryModel.setQueryBrightScenarion(true);
        }
    }

    protected class FaintQueryAction extends SCAction
    {
        public FaintQueryAction()
        {
            super("faintQuery");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _diffRASizeTextfield.setEnabled(false);
            _diffDECSizeTextfield.setEnabled(false);
            StatusBar.show("faint scenario selected.");
            _queryModel.setQueryBrightScenarion(false);
        }
    }

    protected class IncludeScienceObjectAction extends SCAction
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

    protected class SearchCalibratorsAction extends SCAction
    {
        /** Store wether the query is running or not */
        boolean running;

        public SearchCalibratorsAction()
        {
            super("searchCalibrators");
            running = false;
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            //fullFillModel();

            /* TODO
               if (running = false)
               {
                   fullFillModel();
                   _searchButton.setText("Cancel");
               }
               else
               {
                   // TODO : _query.stop();
                   _searchButton.setText("Search");
               }
             */

            // If the model seems complete, launch query execution
            if (_queryModel.isConsumable())
            {
                StatusBar.show("query parameters seem OK.");

                try
                {
                    _vo.getCal(_queryModel);
                }
                catch (Exception ex)
                {
                    StatusBar.show("GETCAL webservice unreachable.");
                    ex.printStackTrace();
                }
            }
            else
            {
                StatusBar.show("query parameters seem wrong.");
            }
        }
    }
}
/*___oOo___*/
