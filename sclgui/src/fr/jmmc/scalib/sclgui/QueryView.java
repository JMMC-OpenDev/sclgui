/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryView.java,v 1.10 2006-07-04 09:54:41 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
    PropertyChangeListener
{
    /** MVC associated model */
    QueryModel _queryModel;

    /** Assocaited virtual observatory */
    VirtualObservatory _vo;

    /** Science object textfields */
    JTextField _starNameTextfield = new JTextField();

    /** Science object right ascension coordinate */
    JTextField _starRATextfield = new JTextField();

    /** Science object declinaison coordinate */
    JTextField _starDECTextfield = new JTextField();

    /** Science object magnitude */
    JFormattedTextField _starMagnitudeTextfield = new JFormattedTextField();

    /** Science object magnitude */
    JCheckBox _includeScienceObjectCheckbox = new JCheckBox();

    /** Search minimum magnitude */
    JFormattedTextField _minMagnitudeTextfield = new JFormattedTextField();

    /** Search maximum magnitude */
    JFormattedTextField _maxMagnitudeTextfield = new JFormattedTextField();

    /** Bright/Faint query radion button group */
    ButtonGroup _brightFaintButtonGroup = new ButtonGroup();

    /** Bright query button */
    JRadioButton _brightRadioButton;

    /** Faint query button */
    JRadioButton _faintRadioButton;

    /** Search box RA size */
    JTextField _diffRATextfield = new JTextField();

    /** Search box DEC size */
    JTextField _diffDECTextfield = new JTextField();

    /** Instrument wavelentgh */
    JTextField _wavelengthTextfield = new JTextField();

    /** Instrument magnitude band */
    JComboBox _magnitudeBandCombo;

    /** Instrument maximun baseline */
    JTextField _maxBaselineTextField = new JTextField();

    /** Query launcher/canceler */
    JButton _goButton;

    /** Query progress bar */
    JProgressBar _progressBar = new JProgressBar();

    //width
    /** DOCUMENT ME! */
    int width = 995;

    /** DOCUMENT ME! */
    int subpanelswidth = 970;

    //Explains
    /** DOCUMENT ME! */
    JTextArea textareaexplain = new JTextArea();

    /** DOCUMENT ME! */
    JScrollPane scrollexplain = new JScrollPane(textareaexplain);

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
        _queryModel     = queryModel;

        // Store the virtual observatory
        _vo             = vo;

        // Form panel global attributes and common objects
        JPanel       tempPanel;
        Dimension    textfieldDimension = new Dimension(100, 20);
        NumberFormat nf                 = NumberFormat.getNumberInstance();

        // Science Object panel
        JPanel scienceObjectPanel = new JPanel();
        scienceObjectPanel.setBorder(new TitledBorder("Science Object"));
        scienceObjectPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill        = GridBagConstraints.HORIZONTAL;
        c.weightx     = 1;

        // Star name field
        c.gridx       = 0;
        c.gridy       = 0;
        scienceObjectPanel.add(new JLabel("Name : "), c);
        tempPanel = new JPanel(new GridBagLayout());
        _starNameTextfield.setMaximumSize(textfieldDimension);
        _starNameTextfield.setMinimumSize(textfieldDimension);
        _starNameTextfield.setPreferredSize(textfieldDimension);
        tempPanel.add(_starNameTextfield, c);
        c.gridx = 1;
        tempPanel.add(new JButton(new SearchScienceObjectAction()));
        scienceObjectPanel.add(tempPanel, c);
        // RA coordinate field
        c.gridx     = 0;
        c.gridy     = 1;
        scienceObjectPanel.add(new JLabel("RA 2000 (mn) : "), c);
        _starRATextfield.setMaximumSize(textfieldDimension);
        _starRATextfield.setMinimumSize(textfieldDimension);
        _starRATextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        scienceObjectPanel.add(_starRATextfield, c);
        // DEG coordinate field
        c.gridx     = 0;
        c.gridy     = 2;
        scienceObjectPanel.add(new JLabel("DEC 2000 (deg) : "), c);
        _starDECTextfield.setMaximumSize(textfieldDimension);
        _starDECTextfield.setMinimumSize(textfieldDimension);
        _starDECTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        scienceObjectPanel.add(_starDECTextfield, c);
        // Magnitude field
        c.gridx     = 0;
        c.gridy     = 3;
        scienceObjectPanel.add(new JLabel("Magnitude : "), c);
        _starMagnitudeTextfield = new JFormattedTextField(nf);
        _starMagnitudeTextfield.setMaximumSize(textfieldDimension);
        _starMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _starMagnitudeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        scienceObjectPanel.add(_starMagnitudeTextfield, c);
        // Include the science objet in result
        c.gridx     = 0;
        c.gridy     = 4;
        scienceObjectPanel.add(new JLabel("Included : "), c);
        c.gridx = 1;
        scienceObjectPanel.add(_includeScienceObjectCheckbox, c);

        // Searching Parameters panel
        JPanel searchCalPanel = new JPanel();
        searchCalPanel.setBorder(new TitledBorder("SearchCal Parameters"));
        searchCalPanel.setLayout(new GridBagLayout());
        // Minimum magnitude field
        c.gridx     = 0;
        c.gridy     = 0;
        searchCalPanel.add(new JLabel("Min. Magnitude : "), c);
        _minMagnitudeTextfield = new JFormattedTextField(nf);
        _minMagnitudeTextfield.setMaximumSize(textfieldDimension);
        _minMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _minMagnitudeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_minMagnitudeTextfield, c);
        // Maximum magnitude field
        c.gridx     = 0;
        c.gridy     = 1;
        searchCalPanel.add(new JLabel("Max. Magnitude : "), c);
        _maxMagnitudeTextfield = new JFormattedTextField(nf);
        _maxMagnitudeTextfield.setMaximumSize(textfieldDimension);
        _maxMagnitudeTextfield.setMinimumSize(textfieldDimension);
        _maxMagnitudeTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_maxMagnitudeTextfield, c);
        // Bright/Faint Scenario
        c.gridx     = 0;
        c.gridy     = 2;
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
        c.gridx     = 0;
        c.gridy     = 3;
        searchCalPanel.add(new JLabel("Diff RA (arcmin) : "), c);
        _diffRATextfield.setMaximumSize(textfieldDimension);
        _diffRATextfield.setMinimumSize(textfieldDimension);
        _diffRATextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_diffRATextfield, c);
        // DEC delta field
        c.gridx     = 0;
        c.gridy     = 4;
        searchCalPanel.add(new JLabel("Diff DEC (arcmin) : "), c);
        _diffDECTextfield.setMaximumSize(textfieldDimension);
        _diffDECTextfield.setMinimumSize(textfieldDimension);
        _diffDECTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        searchCalPanel.add(_diffDECTextfield, c);

        // Instrumental Configuration panel
        JPanel instrumentPanel = new JPanel();
        instrumentPanel.setBorder(new TitledBorder("Instrumental Configuration"));
        instrumentPanel.setLayout(new GridBagLayout());
        // Wavelength field
        c.gridx     = 0;
        c.gridy     = 0;
        instrumentPanel.add(new JLabel("Wavelength : "), c);
        _wavelengthTextfield.setMaximumSize(textfieldDimension);
        _wavelengthTextfield.setMinimumSize(textfieldDimension);
        _wavelengthTextfield.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        instrumentPanel.add(_wavelengthTextfield, c);
        // Magnitude band field
        c.gridx     = 0;
        c.gridy     = 1;
        instrumentPanel.add(new JLabel("Magnitude Band : "), c);

        String[] bands = { "I", "J", "H", "K" };
        _magnitudeBandCombo = new JComboBox(bands);
        _magnitudeBandCombo.setSelectedIndex(3);
        c.gridx = 1;
        instrumentPanel.add(_magnitudeBandCombo, c);
        // Maximum baseline
        c.gridx     = 0;
        c.gridy     = 2;
        instrumentPanel.add(new JLabel("Max. Baseline : "), c);
        _maxBaselineTextField.setMaximumSize(textfieldDimension);
        _maxBaselineTextField.setMinimumSize(textfieldDimension);
        _maxBaselineTextField.setPreferredSize(textfieldDimension);
        c.gridx = 1;
        instrumentPanel.add(_maxBaselineTextField, c);

        // Status panel global attributes and common objects
        JPanel queryStatusPanel = new JPanel();
        queryStatusPanel.setLayout(new BoxLayout(queryStatusPanel,
                BoxLayout.X_AXIS));
        // Progress label
        queryStatusPanel.add(new JLabel("Progress : "));
        // Progressbar
        _progressBar.setStringPainted(true);
        queryStatusPanel.add(_progressBar);
        // GO Button
        _goButton = new JButton(new SearchCalibratorsAction());
        queryStatusPanel.add(_goButton);

        // Query panel global construction
        setBorder(new TitledBorder("Query Parameters"));
        setLayout(new GridBagLayout());
        c.gridx       = 0;
        c.gridy       = 0;
        //c.fill = GridBagConstraints.VERTICAL;   //request any extra vertical space
        c.ipady       = 0; //reset to default
        c.weighty     = 1.0; //request any extra vertical space
        c.anchor      = GridBagConstraints.PAGE_START; //bottom of space
        add(scienceObjectPanel, c);
        c.gridx = 1;
        add(searchCalPanel, c);
        c.gridx = 2;
        add(instrumentPanel, c);
        c.gridx         = 0;
        c.gridy         = 1;
        c.gridwidth     = 3;
        c.fill          = GridBagConstraints.HORIZONTAL;
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

            _starNameTextfield.setText(queryModel.getScienceObjectName());
            _starDECTextfield.setText(queryModel.getDec());
            _starRATextfield.setText(queryModel.getRa());
            _starMagnitudeTextfield.setValue(new Double(
                    queryModel.getMagnitude()));
            _minMagnitudeTextfield.setValue(new Double(
                    queryModel.getMinMagnitude()));
            _maxMagnitudeTextfield.setValue(new Double(
                    queryModel.getMaxMagnitude()));

            // TODO : link to bright/faint radio buttons, and all the other missing fields
            _progressBar.setValue(queryModel.getCurrentStep());
            _progressBar.setMaximum(queryModel.getTotalStep());
            _progressBar.setString(queryModel.getCurrentStatus());
        }
    }

    /** Called when a field's "value" property changes. */
    public void propertyChange(PropertyChangeEvent e)
    {
        MCSLogger.trace();

        Object source = e.getSource();

        // Try to inject user values into the model
        if (source == _starNameTextfield)
        {
            _queryModel.setScienceObjectName(_starNameTextfield.getText());
        }

        if (source == _starRATextfield)
        {
            _queryModel.setRa(_starRATextfield.getText());
        }

        if (source == _starDECTextfield)
        {
            _queryModel.setDec(_starDECTextfield.getText());
        }

        if (source == _starMagnitudeTextfield)
        {
            _queryModel.setMagnitude(_starMagnitudeTextfield.getText());
        }

        if (source == _minMagnitudeTextfield)
        {
            _queryModel.setMinMagnitude(_minMagnitudeTextfield.getText());
        }

        if (source == _maxMagnitudeTextfield)
        {
            _queryModel.setMaxMagnitude(_maxMagnitudeTextfield.getText());
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
               enabledComponents(this, false);
               textareaexplain.setText("Query is beeing resolved ");
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
               enabledComponents(this, true);
               // Should get completion status to indicate proper work or not...TBD
               textareaexplain.setText("Query has been resolved successfully");
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
               enabledComponents(this, true);
               // Should get completion status to indicate proper work or not...TBD
               textareaexplain.setText("Query has not been resolved successfully");
           }
         */
    }

    /**
     * addText
     * @param component JTextArea
     * @param text String
     */
    public void addText(JTextArea component, String text)
    {
        MCSLogger.trace();

        component.setText(component.getText() + "\n" + text);
    }

    /**
     * enabledComponents  -  Enable or disable component (recursive method)
     * @param compo JComponent
     * @param bool boolean
     */
    public void enabledComponents(JComponent compo, boolean bool)
    {
        MCSLogger.trace();

        //System.out.println("OBJECT "+compo.toString()+"SUB OBJECT "+compo.getComponentCount());
        if (compo.getComponentCount() > 0)
        {
            Component[] cmp = compo.getComponents();
            int         k;

            for (k = 0; k < cmp.length; k++)
            {
                // System.out.println("CALL WITH "+cmp[k].toString());
                if (cmp[k].getClass() != JComboBox.class)
                {
                    enabledComponents((JComponent) cmp[k], bool);
                }
            }
        }
        else
        {
            //System.out.println("ENABLED " + compo.getClass().getName());
            compo.setEnabled(bool);
        }
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

            if (_starNameTextfield.getText().length() == 0)
            {
                _queryModel.example();
            }
            else
            {
                try
                {
                    _vo.getScienceObject(_queryModel);
                }
                catch (Exception ex)
                {
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

            _diffRATextfield.setEnabled(true);
            _diffDECTextfield.setEnabled(true);
            textareaexplain.setText("Case bright :\nBox used");
            _queryModel.isBright(true);
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

            _diffRATextfield.setEnabled(false);
            _diffDECTextfield.setEnabled(false);
            textareaexplain.setText("Case faint :\nBox not used");
            _queryModel.isBright(false);
        }
    }

    protected class SearchCalibratorsAction extends SCAction
    {
        /** Store wether the query is running or not */
        boolean running;

        public SearchCalibratorsAction()
        {
            super("searchCalibrators");
            running         = false;
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            //StatusBar.show("TOTO vz lz");

            //fullFillModel();

            /* TODO
               if (running = false)
               {
                   fullFillModel();
                   _goButton.setText("Cancel");
               }
               else
               {
                   // TODO : _query.stop();
                   _goButton.setText("GO");
               }
             */

            // If the model seems complete, launch query execution
            if (_queryModel.isConsumable())
            {
                addText(textareaexplain, "Parameters ok");

                try
                {
                    _vo.getCal(_queryModel);
                }
                catch (Exception ex)
                {
                    textareaexplain.setText("GETCAL crashed");
                    ex.printStackTrace();
                }
            }
            else
            {
                addText(textareaexplain, "Parameters KO");
            }
        }
    }
}
/*___oOo___*/
