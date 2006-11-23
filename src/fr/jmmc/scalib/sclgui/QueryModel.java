/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.13 2006-11-23 16:24:41 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.11  2006/10/04 11:34:31  lafrasse
 * Added support for preferenced science object detection distance.
 *
 * Revision 1.10  2006/10/03 15:31:19  lafrasse
 * Added support for preferenced min and max magnitude auto-update deltas.
 *
 * Revision 1.9  2006/09/28 15:23:29  lafrasse
 * Updated to handle jmmc.util.Preferences API modifications.
 *
 * Revision 1.8  2006/09/15 14:20:54  lafrasse
 * Added query default values support.
 *
 * Revision 1.7  2006/08/22 14:50:51  mella
 * Complete API for setter functions to accept Double or double params
 *
 * Revision 1.6  2006/08/10 15:22:55  lafrasse
 * Fullfiled missing members and accessors
 * Used Double as default numeric type
 * Added full implementation of by-default auto-updated values
 * Streamlined magnitude bands and default wavelengthes support
 *
 * Revision 1.5  2006/07/18 13:16:10  lafrasse
 * Changed example values to mirror those of ETA_TAU in the current SearchCal
 * version
 *
 * Revision 1.4  2006/07/12 14:30:58  lafrasse
 * Code refinment
 * Added the status bar
 *
 * Revision 1.3  2006/07/03 13:34:23  lafrasse
 * Added maximum & minimum calibrators magnitude properties
 *
 * Revision 1.2  2006/06/30 07:59:44  lafrasse
 * Added progress status handling and initialitzation and example values
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import cds.savot.model.*;

import jmmc.mcs.log.MCSLogger;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;


/**
 * Query model.
 */
public class QueryModel extends Observable implements Observer
{
    /** For default values */
    Preferences _preferences;

    /** The instrumental magnitude band */
    private DefaultComboBoxModel _instrumentalMagnitudeBands;

    /** Magnitude to Preselected Wavelength conversion table */
    Hashtable _magnitudeBandToWavelength = new Hashtable();

    /** The instrumental wavelength */
    private double _instrumentalWavelength;

    /** The instrumental wavelength auto-update flag */
    private boolean _instrumentalWavelengthAutoUpdate = true;

    /** The instrumental maximum base line */
    private double _instrumentalMaxBaseLine;

    /** The science object name */
    private String _scienceObjectName;

    /** The science object right assention coordinate */
    private String _scienceObjectRA;

    /** The science object declinaison coordinate */
    private String _scienceObjectDEC;

    /** The science object right magnitude */
    private double _scienceObjectMagnitude;

    /** The science object inclusion flag */
    private boolean _scienceObjectInclusionFlag;

    /** The science object detection distance */
    private boolean _scienceObjectDetectionDistance;

    /** The query minimum magnitude */
    private double _queryMinMagnitude;

    /** The query minimum magnitude auto-update flag */
    private boolean _queryMinMagnitudeAutoUpdate = true;

    /** The query maximum magnitude */
    private double _queryMaxMagnitude;

    /** The query maximum magnitude auto-update flag */
    private boolean _queryMaxMagnitudeAutoUpdate = true;

    /** The query bright scenario flag */
    private boolean _queryBrightScenarioFlag;

    /** The query diff. RA */
    private double _queryDiffRASize;

    /** The query diff. DEC */
    private double _queryDiffDECSize;

    /** The query radius */
    private double _queryRadialSize;

    /** The current step of the querying progress.
     * 0 < _currentStep < _totalStep
     */
    private int _currentStep;

    /** The total number of step of the querying progress.
     * _totalStep > 0
     */
    private int _totalStep;

    /** Remind whether the query can be edited or not (when loaded fromfile for example) */
    private boolean _isEditable;

    /**
     * Default constructor.
     */
    public QueryModel()
    {
        MCSLogger.trace();

        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        String[] magnitudeBands = { "I", "J", "H", "K", "V" };
        double[] wavelengths    = { 1.1, 2.2, 3.3, 4.4, 5.5 };

        // For each "magnitude band-predefined wavelength" couple
        for (int i = 0; i < magnitudeBands.length; i++)
        {
            // Construct the conversion table between both
            _magnitudeBandToWavelength.put(magnitudeBands[i],
                new Double(wavelengths[i]));
        }

        _instrumentalMagnitudeBands = new DefaultComboBoxModel(magnitudeBands);

        // Initialize values from user defined preferences
        loadDefaultValues();
    }

    /**
     * Called when observed objects change.
     *
     * @param o the observed object that changed
     * @param arg not used
     */
    public void update(Observable o, Object arg)
    {
        // Called if the observe shared instance Preference object was updated.
        // Then inform any object than observe us that we also probably change.
        setChanged();
        notifyObservers();
    }

    /**
     * Reset all properties.
     */
    public void reset()
    {
        MCSLogger.trace();

        setInstrumentalMagnitudeBand("K");
        setInstrumentalMaxBaseLine(100.0);

        setScienceObjectName("");
        setScienceObjectRA("+00:00:00.00");
        setScienceObjectDEC("+00:00:00.00");
        setScienceObjectMagnitude(0.0);
        setScienceObjectInclusionFlag(true);

        setQueryBrightScenarioFlag(true);
        setQueryDiffRASize(10.0);
        setQueryDiffDECSize(5.0);
        setQueryRadialSize(0.0);

        setCurrentStep(0);
        setTotalStep(0);

        // Enable the edition as the values where not loaded from file
        setEditableState(true);
    }

    /**
     * Reset all properties to their default values.
     */
    public void loadDefaultValues()
    {
        MCSLogger.trace();

        setInstrumentalMagnitudeBand(_preferences.getPreference(
                "query.magnitudeBand"));
        setInstrumentalWavelength(_preferences.getPreferenceAsDouble(
                "query.instrumentalWavelength"));
        setInstrumentalMaxBaseLine(_preferences.getPreferenceAsDouble(
                "query.instrumentalMaxBaseLine"));

        setScienceObjectName(_preferences.getPreference(
                "query.scienceObjectName"));
        setScienceObjectRA(_preferences.getPreference("query.scienceObjectRA"));
        setScienceObjectDEC(_preferences.getPreference("query.scienceObjectDEC"));
        setScienceObjectMagnitude(_preferences.getPreferenceAsDouble(
                "query.scienceObjectMagnitude"));
        setScienceObjectInclusionFlag(_preferences.getPreferenceAsBoolean(
                "query.scienceObjectInclusionFlag"));

        setQueryMinMagnitude(_preferences.getPreferenceAsDouble(
                "query.queryMinMagnitude"));
        setQueryMaxMagnitude(_preferences.getPreferenceAsDouble(
                "query.queryMaxMagnitude"));
        setQueryBrightScenarioFlag(_preferences.getPreferenceAsBoolean(
                "query.brightScenario"));
        setQueryDiffRASize(_preferences.getPreferenceAsDouble(
                "query.queryDiffRASize"));
        setQueryDiffDECSize(_preferences.getPreferenceAsDouble(
                "query.queryDiffDECSize"));
        setQueryRadialSize(_preferences.getPreferenceAsDouble(
                "query.queryRadialSize"));

        // Enable the edition as the values where not loaded from file
        setEditableState(true);
    }

    /**
     * Set all properties from the given SAVOT ParamSet.
     *
     * @param paramSet the part of the VOTable containing our query parameters.
     */
    public void loadParamSet(ParamSet paramSet)
    {
        MCSLogger.trace();

        // Convert the ParamSet in an HashTable on parameters name -> value
        Hashtable parameters = new Hashtable();

        for (int i = 0; i < paramSet.getItemCount(); i++)
        {
            SavotParam param      = (SavotParam) paramSet.getItemAt(i);
            String     paramName  = param.getName();
            String     paramValue = param.getValue();

            MCSLogger.debug(paramName + " = '" + paramValue + "'");
            parameters.put(paramName, paramValue);
        }

        // Set the query members from the ParamSet values
        setInstrumentalMagnitudeBand((String) parameters.get("band"));
        setInstrumentalWavelength(Double.valueOf(
                (String) parameters.get("wlen")));
        setInstrumentalMaxBaseLine(Double.valueOf(
                (String) parameters.get("baseMax")));
        setScienceObjectName((String) parameters.get("objectName"));
        setScienceObjectRA((String) parameters.get("ra"));
        setScienceObjectDEC((String) parameters.get("dec"));
        setScienceObjectMagnitude(Double.valueOf((String) parameters.get("mag")));
        setQueryMinMagnitude(Double.valueOf(
                (String) parameters.get("minMagRange")));
        setQueryMaxMagnitude(Double.valueOf(
                (String) parameters.get("maxMagRange")));
        setQueryBrightScenarioFlag(Boolean.valueOf(
                (String) parameters.get("bright")));
        setQueryDiffRASize(Double.valueOf((String) parameters.get("diffRa")));
        setQueryDiffDECSize(Double.valueOf((String) parameters.get("diffDec")));
        setQueryRadialSize(Double.valueOf((String) parameters.get("radius")));

        // Disable the edition as the values where loaded from file
        setEditableState(false);
    }

    /**
     * Store all current properties as the futur default values.
     */
    public void saveDefaultValues()
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.magnitudeBand",
                getInstrumentalMagnitudeBand());
            _preferences.setPreference("query.instrumentalWavelength",
                getInstrumentalWavelength());
            _preferences.setPreference("query.instrumentalMaxBaseLine",
                getInstrumentalMaxBaseLine());

            _preferences.setPreference("query.scienceObjectName",
                getScienceObjectName());
            _preferences.setPreference("query.scienceObjectRA",
                getScienceObjectRA());
            _preferences.setPreference("query.scienceObjectDEC",
                getScienceObjectDEC());
            _preferences.setPreference("query.scienceObjectMagnitude",
                getScienceObjectMagnitude());
            _preferences.setPreference("query.scienceObjectInclusionFlag",
                getScienceObjectInclusionFlag());

            _preferences.setPreference("query.queryMinMagnitude",
                getQueryMinMagnitude());
            _preferences.setPreference("query.queryMaxMagnitude",
                getQueryMaxMagnitude());
            _preferences.setPreference("query.brightScenario",
                getQueryBrightScenarioFlag());
            _preferences.setPreference("query.queryDiffRASize",
                getQueryDiffRASize());
            _preferences.setPreference("query.queryDiffDECSize",
                getQueryDiffDECSize());
            _preferences.setPreference("query.queryRadialSize",
                getQueryRadialSize());

            _preferences.saveToFile();
        }
        catch (Exception e)
        {
            // @TODO
        }
    }

    /**
     * Set default values for a bright search on ETA_TAU.
     */
    public void example()
    {
        MCSLogger.trace();

        // @TODO : this method should diappear at last !
        loadDefaultValues();

        setTotalStep(11);
    }

    /**
     * Return all the instrumental magnitude bands.
     *
     * @return the instrumental magnitude bands.
     */
    public DefaultComboBoxModel getInstrumentalMagnitudeBands()
    {
        MCSLogger.trace();

        return _instrumentalMagnitudeBands;
    }

    /**
     * Change all the instrumental magnitude bands for the actual query.
     * Update the instrumental wavelength if needed.
     *
     * @param scienceObjectName the new instrumental magnitude band.
     */
    public void setInstrumentalMagnitudeBands(
        DefaultComboBoxModel magnitudeBands)
    {
        MCSLogger.trace();

        _instrumentalMagnitudeBands = magnitudeBands;

        String magnitudeBand = (String) _instrumentalMagnitudeBands.getSelectedItem();
        setInstrumentalMagnitudeBand(magnitudeBand);

        setChanged();
        notifyObservers();
    }

    /**
     * Return the instrumental magnitude band for the actual query.
     *
     * @return the instrumental magnitude band as a String object.
     */
    public String getInstrumentalMagnitudeBand()
    {
        MCSLogger.trace();

        return (String) _instrumentalMagnitudeBands.getSelectedItem();
    }

    /**
     * Change the instrumental magnitude band for the actual query.
     * Update the instrumental wavelength if needed.
     *
     * @param scienceObjectName the new instrumental magnitude band.
     */
    public void setInstrumentalMagnitudeBand(String magnitudeBand)
    {
        MCSLogger.trace();

        _instrumentalMagnitudeBands.setSelectedItem(magnitudeBand);

        // Modify _instrumentalWavelength automatically if needed
        if (_instrumentalWavelengthAutoUpdate == true)
        {
            Double d = (Double) _magnitudeBandToWavelength.get(magnitudeBand);
            _instrumentalWavelength = d.doubleValue();
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Return the instrumental wavelength for the actual query.
     *
     * @return the instrumental wavelength as a Double value.
     */
    public Double getInstrumentalWavelength()
    {
        MCSLogger.trace();

        return new Double(_instrumentalWavelength);
    }

    /**
     * Change the instrumental wavelength parameter.
     * Calling this method once disable the instrumental wavelength auto-update.
     *
     * @param wavelength the new instrumental wavelength as a double value.
     */
    public void setInstrumentalWavelength(double wavelength)
    {
        MCSLogger.trace();

        // This value should never be auto-updated anymore
        _instrumentalWavelengthAutoUpdate     = false;

        _instrumentalWavelength               = wavelength;

        setChanged();
        notifyObservers();
    }

    /**
     * Change the instrumental wavelength parameter.
     * Calling this method once disable the instrumental wavelength auto-update.
     *
     * @param wavelength the new instrumental wavelength as a Double value.
     */
    public void setInstrumentalWavelength(Double wavelength)
    {
        setInstrumentalWavelength(wavelength.doubleValue());
    }

    /**
     * Return the instrumental maximum base line for the actual query.
     *
     * @return the instrumental maximum base line as a Double value.
     */
    public Double getInstrumentalMaxBaseLine()
    {
        MCSLogger.trace();

        return new Double(_instrumentalMaxBaseLine);
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param wavelength the new instrumental maximum base line as a double.
     */
    public void setInstrumentalMaxBaseLine(double maxBaseLine)
    {
        MCSLogger.trace();

        _instrumentalMaxBaseLine = maxBaseLine;

        setChanged();
        notifyObservers();
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param wavelength the new instrumental maximum base line as a Double.
     */
    public void setInstrumentalMaxBaseLine(Double maxBaseLine)
    {
        setInstrumentalMaxBaseLine(maxBaseLine.doubleValue());
    }

    /**
     * Return the name of the science object for the actual query.
     *
     * @return the star name.
     */
    public String getScienceObjectName()
    {
        MCSLogger.trace();

        return _scienceObjectName;
    }

    /**
     * Change the name of the science object for the actual query.
     *
     * @param scienceObjectName the new star name.
     */
    public void setScienceObjectName(String scienceObjectName)
    {
        MCSLogger.trace();

        _scienceObjectName = scienceObjectName;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the right ascension for the actual query.
     *
     * @return the right ascension.
     */
    public String getScienceObjectRA()
    {
        MCSLogger.trace();

        return _scienceObjectRA;
    }

    /**
     * Change the right ascension parameter for the actual query.
     *
     * @param ra the right ascension.
     */
    public void setScienceObjectRA(String rightAscension)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        // Check if the given parameter is not empty
        if (rightAscension.length() < 1)
        {
            throw new IllegalArgumentException("given RA is empty");
        }

        // Validate the format of the given value
        if (rightAscension.matches("[+|-]?[0-9]+:[0-9]+:[0-9]+.?[0-9]*") == false)
        {
            throw new IllegalArgumentException("wrong RA format: +30:00:00.00");
        }

        _scienceObjectRA = rightAscension;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the declinaison  for the actual query.
     *
     * @return the declinaison.
     */
    public String getScienceObjectDEC()
    {
        MCSLogger.trace();

        return _scienceObjectDEC;
    }

    /**
     * Change the declinaison parameter for the actual query.
     *
     * @param dec the declinaison.
     */
    public void setScienceObjectDEC(String declinaison)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        // Check if given parameter is not empty
        if (declinaison.length() < 1)
        {
            throw new IllegalArgumentException("given DEC is empty");
        }

        // Validate the format of the given value
        if (declinaison.matches("[+|-]?[0-9]+:[0-9]+:[0-9]+.?[0-9]*") == false)
        {
            throw new IllegalArgumentException("wrong DEC format: +30:00:00.00");
        }

        _scienceObjectDEC = declinaison;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the science object magnitude for the actual query.
     *
     * @return the science object magnitude as a Double value.
     */
    public Double getScienceObjectMagnitude()
    {
        MCSLogger.trace();

        return new Double(_scienceObjectMagnitude);
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a double value.
     */
    public void setScienceObjectMagnitude(double magnitude)
    {
        MCSLogger.trace();

        _scienceObjectMagnitude = magnitude;

        // Modify _queryMinMagnitude automatically if needed
        if (_queryMinMagnitudeAutoUpdate == true)
        {
            _queryMinMagnitude = _scienceObjectMagnitude +
                getQueryMinMagnitudeDelta();
        }

        // Modify _queryMaxMagnitude automatically if needed
        if (_queryMaxMagnitudeAutoUpdate == true)
        {
            _queryMaxMagnitude = _scienceObjectMagnitude +
                getQueryMaxMagnitudeDelta();
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a Double value.
     */
    public void setScienceObjectMagnitude(Double magnitude)
    {
        setScienceObjectMagnitude(magnitude.doubleValue());
    }

    /**
     * Indicates wether the query result should include the science object.
     *
     * @return true wether the query results should include the science object,
     * false otherwise.
     */
    public Boolean getScienceObjectInclusionFlag()
    {
        return _scienceObjectInclusionFlag;
    }

    /**
     * Set wether the result should include the science object or not.
     *
     * @param flag true for scinec objection inclusion, false otherwise.
     */
    public void setScienceObjectInclusionFlag(boolean flag)
    {
        _scienceObjectInclusionFlag = flag;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the tolerated distance to detect the science object.
     *
     * @return the tolerated distance as a Double value.
     */
    public Double getScienceObjectDetectionDistance()
    {
        MCSLogger.trace();

        return _preferences.getPreferenceAsDouble(
            "query.scienceObjectDetectionDistance");
    }

    /**
     * Change the tolerated distance to detect the science object.
     *
     * @param distance the new tolerated distance as a double value.
     */
    public void setScienceObjectDetectionDistance(double distance)
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.scienceObjectDetectionDistance",
                new Double(distance));
        }
        catch (Exception e)
        {
            // @TODO
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Return the minimun calibrator magnitude for the actual query.
     *
     * @return the minimum magnitude as a Double value.
     */
    public Double getQueryMinMagnitude()
    {
        MCSLogger.trace();

        return new Double(_queryMinMagnitude);
    }

    /**
     * Change the minimun calibrator magnitude parameter.
     * Calling this method once disable the minimun calibrator magnitude
     * auto-update.
     *
     * @param minMagnitude the new minimum magnitude as a double value.
     */
    public void setQueryMinMagnitude(double minMagnitude)
    {
        MCSLogger.trace();

        // This value should never be auto-updated anymore
        _queryMinMagnitudeAutoUpdate = false;

        // Update only if the given value is less or equal to the minimum one
        if (minMagnitude <= _queryMaxMagnitude)
        {
            _queryMinMagnitude = minMagnitude;
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Change the minimun calibrator magnitude parameter.
     * Calling this method once disable the minimun calibrator magnitude
     * auto-update.
     *
     * @param minMagnitude the new minimum magnitude as a Double value.
     */
    public void setQueryMinMagnitude(Double minMagnitude)
    {
        setQueryMinMagnitude(minMagnitude.doubleValue());
    }

    /**
     * Return the auto-update delta for the minimun calibrator magnitude.
     *
     * @return the minimum magnitude delta as a Double value.
     */
    public Double getQueryMinMagnitudeDelta()
    {
        MCSLogger.trace();

        return _preferences.getPreferenceAsDouble(
            "query.queryMinMagnitudeDelta");
    }

    /**
     * Change the auto-update delta for the minimun calibrator magnitude.
     *
     * @param delta the new delta as a double value.
     */
    public void setQueryMinMagnitudeDelta(double delta)
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.queryMinMagnitudeDelta",
                new Double(delta));
        }
        catch (Exception e)
        {
            // @TODO
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Return the maximun calibrator magnitude for the actual query.
     *
     * @return the maximum magnitude as a Double value.
     */
    public Double getQueryMaxMagnitude()
    {
        MCSLogger.trace();

        return new Double(_queryMaxMagnitude);
    }

    /**
     * Change the maximun calibrator magnitude parameter.
     * Calling this method once disable the maximun calibrator magnitude
     * auto-update.
     *
     * @param maxMagnitude the new maximum magnitude as a double value.
     */
    public void setQueryMaxMagnitude(double maxMagnitude)
    {
        MCSLogger.trace();

        // This value should never be auto-updated anymore
        _queryMaxMagnitudeAutoUpdate = false;

        // Update only if the given value is greater or equal to the minimum one
        if (maxMagnitude >= _queryMinMagnitude)
        {
            _queryMaxMagnitude = maxMagnitude;
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Change the maximun calibrator magnitude parameter.
     * Calling this method once disable the maximun calibrator magnitude
     * auto-update.
     *
     * @param maxMagnitude the new maximum magnitude as a Double value.
     */
    public void setQueryMaxMagnitude(Double maxMagnitude)
    {
        setQueryMaxMagnitude(maxMagnitude.doubleValue());
    }

    /**
     * Return the auto-update delta for the maximun calibrator magnitude.
     *
     * @return the maximum magnitude delta as a Double value.
     */
    public Double getQueryMaxMagnitudeDelta()
    {
        MCSLogger.trace();

        return _preferences.getPreferenceAsDouble(
            "query.queryMaxMagnitudeDelta");
    }

    /**
     * Change the auto-update delta for the maximun calibrator magnitude.
     *
     * @param delta the new delta as a double value.
     */
    public void setQueryMaxMagnitudeDelta(double delta)
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.queryMaxMagnitudeDelta",
                new Double(delta));
        }
        catch (Exception e)
        {
            // @TODO
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Indicates wether the query is using the bright scenario or the faint one.
     *
     * @return true wether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public Boolean getQueryBrightScenarioFlag()
    {
        return _queryBrightScenarioFlag;
    }

    /**
     * Set wether the query should use the bright scenario or the faint one.
     *
     * @param flag true for bright queries, false for faint ones.
     */
    public void setQueryBrightScenarioFlag(boolean flag)
    {
        _queryBrightScenarioFlag = flag;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the query box differential RA size.
     *
     * @return the query box differential RA size.
     */
    public Double getQueryDiffRASize()
    {
        MCSLogger.trace();

        return new Double(_queryDiffRASize);
    }

    /**
     * Change the query box differential RA size.
     *
     * @param diffRASize the new query box differential RA size as a double.
     */
    public void setQueryDiffRASize(double diffRASize)
    {
        MCSLogger.trace();

        _queryDiffRASize = diffRASize;

        setChanged();
        notifyObservers();
    }

    /**
     * Change the query box differential RA size.
     *
     * @param diffRASize the new query box differential RA size as a Double.
     */
    public void setQueryDiffRASize(Double diffRASize)
    {
        setQueryDiffRASize(diffRASize.doubleValue());
    }

    /**
     * Return the query box differential DEC size.
     *
     * @return the query box differential DEC size.
     */
    public Double getQueryDiffDECSize()
    {
        MCSLogger.trace();

        return new Double(_queryDiffDECSize);
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param radiusSize the new query box differential DEC size as a double.
     */
    public void setQueryDiffDECSize(double diffDECSize)
    {
        MCSLogger.trace();

        _queryDiffDECSize = diffDECSize;

        setChanged();
        notifyObservers();
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param radiusSize the new query box differential DEC size as a Double.
     */
    public void setQueryDiffDECSize(Double diffDECSize)
    {
        setQueryDiffDECSize(diffDECSize.doubleValue());
    }

    /**
     * Return the query box radial size.
     *
     * @return the query box radial size.
     */
    public Double getQueryRadialSize()
    {
        MCSLogger.trace();

        return new Double(_queryRadialSize);
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a double.
     */
    public void setQueryRadialSize(double radiusSize)
    {
        MCSLogger.trace();

        _queryRadialSize = radiusSize;

        setChanged();
        notifyObservers();
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a Double.
     */
    public void setQueryRadialSize(Double radiusSize)
    {
        setQueryRadialSize(radiusSize.doubleValue());
    }

    /**
     * Return the current step of query progress.
     *
     * @return the current querying step.
     */
    public int getCurrentStep()
    {
        MCSLogger.trace();

        return _currentStep;
    }

    /**
     * Set the current step of query progress.
     *
     * @param the current querying step.
     */
    public void setCurrentStep(int currentStep)
    {
        MCSLogger.trace();

        if (currentStep <= _totalStep)
        {
            _currentStep = currentStep;
        }
        else
        {
            currentStep = 0;
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Return the total number of step of query.
     *
     * @return the total number of querying step.
     */
    public int getTotalStep()
    {
        MCSLogger.trace();

        return _totalStep;
    }

    /**
     * Set the total number of step of query.
     *
     * @param the total number of querying step.
     */
    public void setTotalStep(int totalStep)
    {
        MCSLogger.trace();

        _totalStep = totalStep;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the current querying status.
     *
     * @return the current querying status.
     */
    public String getCurrentStatus()
    {
        MCSLogger.trace();

        if (_currentStep == 0)
        {
            return " ";
        }

        return "Catalog " + getCurrentStep() + "/" + getTotalStep();
    }

    /**
     * Indicates to listener if this query is well filled and can be consumed.
     * If it returns false, then one reason must be given TBD...
     *
     * TODO add exception
     */
    public boolean isConsumable()
    {
        MCSLogger.trace();

        // @TODO : Verify any mandatory missing parameter

        // If the RA coordinate is not defined
        if (_scienceObjectRA.length() < 1)
        {
            return false;
        }

        // If the DEC coordinate is not defined
        if (_scienceObjectDEC.length() < 1)
        {
            return false;
        }

        // The magnitude is not tested as any Double value is valid
        return true;
    }

    /**
     * Return whether the query can be edited or not.
     *
     * @return true if the query can be edited, false otherwise.
     */
    public boolean getEditableState()
    {
        MCSLogger.trace();

        return _isEditable;
    }

    /**
     * Set whether the query can be edited or not.
     *
     * @param state true if the query can be edited, false otherwise.
     */
    public void setEditableState(boolean state)
    {
        MCSLogger.trace();

        _isEditable = state;

        setChanged();
        notifyObservers();
    }
}
/*___oOo___*/
