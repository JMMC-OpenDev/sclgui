/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryView.java,v 1.2 2006-03-31 08:53:20 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;


/**
 * Query view.
 */
public class QueryView extends JPanel implements Observer
{
    //TextFields
    /** DOCUMENT ME! */
    JTextField textWave = new JTextField();

    /** DOCUMENT ME! */
    JTextField textMagMax = new JTextField();

    /** DOCUMENT ME! */
    JTextField textName = new JTextField();

    /** DOCUMENT ME! */
    JTextField textMagMin = new JTextField();

    /** DOCUMENT ME! */
    JTextField textRA = new JTextField();

    /** DOCUMENT ME! */
    JTextField textDEC = new JTextField();

    /** DOCUMENT ME! */
    JTextField textBase = new JTextField();

    /** DOCUMENT ME! */
    JTextField textMagnitude = new JTextField();

    /** DOCUMENT ME! */
    JTextField textDiffRA = new JTextField();

    /** DOCUMENT ME! */
    JTextField textDiffDEC = new JTextField();

    //Labels
    /** DOCUMENT ME! */
    JLabel select = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelObject = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelWave = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelMagMin = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelRA = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelDEC = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelBase = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelMagobj = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelDiffRA = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelDiffDEC = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelBlank = new JLabel();

    //Radio buttons
    /** DOCUMENT ME! */
    JRadioButton bright = new JRadioButton("Bright");

    /** DOCUMENT ME! */
    JRadioButton faint = new JRadioButton("Faint");

    /** DOCUMENT ME! */
    ButtonGroup groupRadio = new ButtonGroup();

    //Border
    /** DOCUMENT ME! */
    Border yellowBorder = BorderFactory.createLineBorder(Color.YELLOW, 2);

    /** DOCUMENT ME! */
    Border lightBorder = BorderFactory.createLineBorder(Color.GRAY, 1);

    /** DOCUMENT ME! */
    Border emptyBorder = BorderFactory.createEmptyBorder();

    //Panels
    /** DOCUMENT ME! */
    JPanel panelradio = new JPanel();

    /** DOCUMENT ME! */
    JPanel panelparams = new JPanel();

    /** DOCUMENT ME! */
    JLabel labelBand = new JLabel();

    /** DOCUMENT ME! */
    JLabel labelMagMax = new JLabel();

    /** DOCUMENT ME! */
    JPanel panelparamsscienceobj = new JPanel();

    /** DOCUMENT ME! */
    JPanel panelparamsconfig = new JPanel();

    /** DOCUMENT ME! */
    JPanel panelparamssearchcal = new JPanel();

    /** DOCUMENT ME! */
    JPanel panelparamsexplain = new JPanel();

    //Layout
    /** DOCUMENT ME! */
    FlowLayout flowLayout = new FlowLayout();

    /** DOCUMENT ME! */
    FlowLayout flowLayout4 = new FlowLayout();

    //buttons
    /** DOCUMENT ME! */
    JButton buttonGo = new JButton();

    /** DOCUMENT ME! */
    JButton buttonCancel = new JButton();

    //Combo
    /** DOCUMENT ME! */
    JComboBox comboBand;

    //ProgressBar
    /** DOCUMENT ME! */
    JProgressBar progressbar = new JProgressBar();

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

    /** MVC associated model */
    private QueryModel _model;

    /** DOCUMENT ME! */
    private VirtualObservatory _vo;

    /**
     * Constructor.
     *
     * @param model the object used to store all the query attributes.
     * @param vo the object used to query the JMMC GETCAL webservice.
     */
    public QueryView(QueryModel model, VirtualObservatory vo)
    {
        _model = model;
        _model.addObserver(this);

        _vo = vo;

        //        setBorder(new TitledBorder(yellowBorder, "QUERY PARAMETERS"));
        setBorder(new TitledBorder(lightBorder, "Query Parameters"));
        setLayout(flowLayout4);

        // Layout configuration
        flowLayout.setAlignment(FlowLayout.LEFT);

        try
        {
            setMinimumSize(new Dimension(width, 205));
            setSize(new Dimension(width, 205));
            setPreferredSize(new Dimension(width, 205));

            panelRadioInit();
            panelParamsInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        updateDataView(_model);
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
            QueryModel model = (QueryModel) o;

            updateDataView(model);
        }
    }

    /**
     * Read the model to update view according status changes.
     */
    private void updateStatusView(QueryModel queryModel)
    {
        MCSLogger.trace();

        // Depending on the queryModel state, change view...
        if (false)
        { // TODO Change condiion
          //Prepare barprogress
            progressbar.setVisible(true);
            progressbar.setIndeterminate(true);
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
            progressbar.setVisible(false);
            progressbar.setIndeterminate(false);

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
            progressbar.setVisible(false);
            progressbar.setIndeterminate(false);

            //panel enabled true
            enabledComponents(this, true);

            // Should get completion status to indicate proper work or not...TBD                
            textareaexplain.setText("Query has not been resolved successfully");
        }
    }

    /**
     * Read the model to update view according data changes.
     */
    private void updateDataView(QueryModel queryModel)
    {
        MCSLogger.trace();

        textName.setText(_model.getScienceObjectName());
        textDEC.setText(_model.getDec());
        textRA.setText(_model.getRa());
        textMagnitude.setText(String.valueOf(_model.getMagnitude()));
    }

    /**
     * Affect query model with correct values.
     */
    public void fullFillModel()
    {
        MCSLogger.trace();

        textareaexplain.setText("Parameters verification...");

        // Try to inject user values into the model
        try
        {
            _model.setScienceObjectName(textName.getText());
            _model.setRa(textRA.getText());
            _model.setDec(textDEC.getText());
            _model.setMagnitude(textMagnitude.getText());
            _model.notifyObservers();
            addText(textareaexplain, "Parameters ok");

            // If the model seems complete, launch query execution
            if (_model.isConsumable())
            {
                try
                {
                    _vo.GetCal(_model);
                }
                catch (Exception e)
                {
                    textareaexplain.setText("GETCAL crashed");
                    e.printStackTrace();
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            addText(textareaexplain, "Input error:" + e.getMessage());
        }
        catch (Exception e)
        {
            addText(textareaexplain, "ERROR:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * panelRadioInit
     */
    public void panelRadioInit()
    {
        MCSLogger.trace();

        select.setText("Select the type of star :  ");

        bright.setSelected(true);
        groupRadio.add(bright);
        groupRadio.add(faint);

        // EVTS
        TypeOfStarListener gestionnaire = new TypeOfStarListener(this);
        bright.addActionListener(gestionnaire);
        faint.addActionListener(gestionnaire);

        labelBlank.setPreferredSize(new Dimension(290, 25));

        buttonGo.setPreferredSize(new Dimension(90, 25));
        buttonGo.setText("GO");
        buttonCancel.setPreferredSize(new Dimension(90, 25));
        buttonCancel.setText("Cancel");

        LaunchSearchCalListener sclistener = new LaunchSearchCalListener(this);
        buttonGo.addActionListener(sclistener);
        buttonCancel.addActionListener(sclistener);

        panelradio.setPreferredSize(new Dimension(subpanelswidth, 30));
        panelradio.setLayout(flowLayout);

        progressbar.setVisible(false);
        progressbar.setPreferredSize(new Dimension(290, 25));

        panelradio.add(select);
        panelradio.add(bright);
        panelradio.add(faint);
        panelradio.add(labelBlank);
        panelradio.add(progressbar);
        panelradio.add(buttonCancel);
        panelradio.add(buttonGo);

        add(panelradio);
    }

    //end panelRadioInit

    /**
     * panelParamsInit
     */
    public void panelParamsInit()
    {
        MCSLogger.trace();

        //SCIENCE OBJECT
        panelparamsscienceobj.setPreferredSize(new Dimension(250, 127));
        panelparamsscienceobj.setBorder(new TitledBorder(lightBorder,
                "Science object"));
        panelparamsscienceobj.setLayout(flowLayout);
        setLabelsAndTexts(panelparamsscienceobj, labelObject, textName, 130,
            17, 90, 20, "Name : ");
        setLabelsAndTexts(panelparamsscienceobj, labelRA, textRA, 130, 17, 90,
            20, "RA 2000 (mn) : ");
        setLabelsAndTexts(panelparamsscienceobj, labelDEC, textDEC, 130, 17,
            90, 20, "DEC 2000 (deg) : ");
        setLabelsAndTexts(panelparamsscienceobj, labelMagobj, textMagnitude,
            130, 17, 90, 20, "Magnitude : ");

        //SEARCHCAL PARAMETERS
        panelparamssearchcal.setPreferredSize(new Dimension(250, 127));
        panelparamssearchcal.setBorder(new TitledBorder(lightBorder,
                "SearchCal parameters"));
        panelparamssearchcal.setLayout(flowLayout);
        setLabelsAndTexts(panelparamssearchcal, labelMagMin, textMagMin, 130,
            17, 90, 20, "Mag min to search : ");
        setLabelsAndTexts(panelparamssearchcal, labelMagMax, textMagMax, 130,
            17, 90, 20, "Mag max to search : ");
        setLabelsAndTexts(panelparamssearchcal, labelDiffRA, textDiffRA, 130,
            17, 90, 20, "Diff RA (arcmin) : ");
        setLabelsAndTexts(panelparamssearchcal, labelDiffDEC, textDiffDEC, 130,
            17, 90, 20, "Diff DEC (arcmin) : ");

        //INSTRUMENTAL CONFIGURATION
        panelparamsconfig.setPreferredSize(new Dimension(250, 127));
        panelparamsconfig.setBorder(new TitledBorder(lightBorder,
                "Instrumental configuration"));
        panelparamsconfig.setLayout(flowLayout);
        setLabelsAndTexts(panelparamsconfig, labelWave, textWave, 130, 17, 90,
            20, "Wavelength : ");
        labelBand.setPreferredSize(new Dimension(130, 17));
        labelBand.setText("Magnitude band : ");

        String[] bands = { "I", "J", "H", "K" };
        comboBand = new JComboBox(bands);
        comboBand.setSelectedIndex(3);
        comboBand.setPreferredSize(new Dimension(90, 23));
        panelparamsconfig.add(labelBand);
        panelparamsconfig.add(comboBand);
        setLabelsAndTexts(panelparamsconfig, labelBase, textBase, 130, 17, 90,
            20, "Max baseline used : ");

        //EXPLAIN
        panelparams.setPreferredSize(new Dimension(subpanelswidth, 135));
        panelparams.setLayout(flowLayout);

        panelparamsexplain.setPreferredSize(new Dimension(200, 127));
        panelparamsexplain.setBorder(new TitledBorder(lightBorder, "Explains"));
        textareaexplain.setBackground(UIManager.getColor("Button.background"));
        scrollexplain.setPreferredSize(new Dimension(180, 85));
        scrollexplain.setBorder(emptyBorder);

        textareaexplain.setEditable(false);
        textareaexplain.setText("");
        textareaexplain.setRows(5);

        panelparamsexplain.add(scrollexplain);

        //PANELPARAMS
        panelparams.add(panelparamsscienceobj);
        panelparams.add(panelparamssearchcal);
        panelparams.add(panelparamsconfig);
        panelparams.add(panelparamsexplain);

        add(panelparams);
    }

    //end panelParamsInit

    /**
     * setLabelsAndTexts  -  Labels and texfields configuration
     * @param panel JPanel
     * @param label JLabel
     * @param textfield JTextField
     * @param labelwidth int
     * @param labelheight int
     * @param textwidth int
     * @param textheight int
     * @param text String
     */
    public void setLabelsAndTexts(JPanel panel, JLabel label,
        JTextField textfield, int labelwidth, int labelheight, int textwidth,
        int textheight, String text)
    {
        MCSLogger.trace();

        textfield.setPreferredSize(new Dimension(textwidth, textheight));
        label.setPreferredSize(new Dimension(labelwidth, labelheight));
        label.setText(text);
        panel.add(label);
        panel.add(textfield);
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
}


//end class
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
class TypeOfStarListener implements ActionListener
{
    /** DOCUMENT ME! */
    QueryView _queryView;

    /**
     * TypeOfStarListener  -  Constructor
     * @param panel QueryView
     */
    public TypeOfStarListener(QueryView queryView)
    {
        _queryView          = queryView;
    }

    /**
     * actionPerformed  -  Enable or disable TextFields according to the type of star
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        if (e.getSource().equals(_queryView.bright))
        {
            _queryView.textDiffRA.setEnabled(true);
            _queryView.textDiffRA.setBackground(Color.white);
            _queryView.textDiffDEC.setEnabled(true);
            _queryView.textDiffDEC.setBackground(Color.white);
            _queryView.textareaexplain.setText("Case bright :\nBox used");
        }
        else if (e.getSource().equals(_queryView.faint))
        {
            _queryView.textDiffRA.setEnabled(false);
            _queryView.textDiffRA.setBackground(Color.lightGray);
            _queryView.textDiffDEC.setEnabled(false);
            _queryView.textDiffDEC.setBackground(Color.lightGray);
            _queryView.textareaexplain.setText("Case faint :\nBox not used");
        }
    }
}


/**
 * DOCUMENT ME!
 */
class LaunchSearchCalListener implements ActionListener
{
    /** DOCUMENT ME! */
    QueryView _queryView;

    /**
     * LaunchSearchCalListener  -  Constructor
     * @param queryView QueryView
     */
    public LaunchSearchCalListener(QueryView queryView)
    {
        _queryView = queryView;
    }

    /**
     * actionPerformed  -  Launching
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {
        MCSLogger.trace();

        if (e.getSource().equals(_queryView.buttonGo))
        {
            _queryView.fullFillModel();
        }

        if (e.getSource().equals(_queryView.buttonCancel))
        {
            System.out.println("Cancel SearchCal query");
        }
    }
}
/*___oOo___*/
