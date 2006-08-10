/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.6 2006-08-10 15:22:55 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import jmmc.mcs.log.MCSLogger;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;


/**
 * Query model.
 */
public class QueryModel extends Observable
{
    /** The instrumental magnitude band */
    private DefaultComboBoxModel _instrumentalMagnitudeBands;

    /** Magnitude to Preselected Wavelength conversion table */
    Hashtable _magnitudeBandToWavelength = new Hashtable();

    /** The instrumental wavelength */
    private Double _instrumentalWavelength;

    /** The instrumental wavelength auto-update flag */
    private boolean _instrumentalWavelengthAutoUpdate = true;

    /** The instrumental maximum base line */
    private Double _instrumentalMaxBaseLine;

    /** The science object name */
    private String _scienceObjectName;

    /** The science object right assention coordinate */
    private String _scienceObjectRA;

    /** The science object declinaison coordinate */
    private String _scienceObjectDEC;

    /** The science object right magnitude */
    private Double _scienceObjectMagnitude;

    /** The query minimum magnitude */
    private Double _queryMinMagnitude;

    /** The query minimum magnitude auto-update flag */
    private boolean _queryMinMagnitudeAutoUpdate = true;

    /** The query maximum magnitude */
    private Double _queryMaxMagnitude;

    /** The query maximum magnitude auto-update flag */
    private boolean _queryMaxMagnitudeAutoUpdate = true;

    /** The query bright scenario flag */
    private boolean _queryBrightScenarioFlag;

    /** The query diff. RA */
    private Double _queryDiffRASize;

    /** The query diff. DEC */
    private Double _queryDiffDECSize;

    /** The query radius */
    private Double _queryRadialSize;

    /** The current step of the querying progress.
     * 0 < _currentStep < _totalStep
     */
    private int _currentStep;

    /** The total number of step of the querying progress.
     * _totalStep > 0
     */
    private int _totalStep;

    /**
     * Default constructor.
     */
    public QueryModel()
    {
        MCSLogger.trace();

        String[] magnitudeBands = { "I", "J", "H", "K", "V" };
        Double[] wavelengths    = { 1.1, 2.2, 3.3, 4.4, 5.5 };

        // For each "magnitude band-predefined wavelength" couple
        for (int i = 0; i < magnitudeBands.length; i++)
        {
            // Construct the conversion table between both
            _magnitudeBandToWavelength.put(magnitudeBands[i], wavelengths[i]);
        }

        _instrumentalMagnitudeBands = new DefaultComboBoxModel(magnitudeBands);

        init();
    }

    /**
     * Reset all properties.
     */
    public void init()
    {
        MCSLogger.trace();

        // @TODO : retrieve all those values from preference
        setInstrumentalMagnitudeBand("K");
        setInstrumentalMaxBaseLine(100.0);

        setScienceObjectName("");
        setScienceObjectRA("+00:00:00.00");
        setScienceObjectDEC("+00:00:00.00");
        setScienceObjectMagnitude(0.0);

        setQueryBrightScenarion(true);
        setQueryDiffRASize(10.0);
        setQueryDiffDECSize(5.0);
        setQueryRadialSize(0.0);

        setCurrentStep(0);
        setTotalStep(0);
    }

    /**
     * Set default values for a bright search on ETA_TAU.
     */
    public void example()
    {
        MCSLogger.trace();

        setInstrumentalMagnitudeBand("V");
        setInstrumentalWavelength(1978.0);
        setInstrumentalMaxBaseLine(102.45);

        setScienceObjectName("eta_tau");
        setScienceObjectRA("+03:47:29.79");
        setScienceObjectDEC("+24:06:18.50");
        setScienceObjectMagnitude(0.0);

        setQueryMinMagnitude(2.0);
        setQueryMaxMagnitude(4.0);
        setQueryDiffRASize(60.0);
        setQueryDiffDECSize(5.0);
        setQueryRadialSize(0.0);

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
            _instrumentalWavelength = (Double) _magnitudeBandToWavelength.get(magnitudeBand);
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

        return _instrumentalWavelength;
    }

    /**
     * Change the instrumental wavelength parameter.
     * Calling this method once disable the instrumental wavelength auto-update.
     *
     * @param wavelength the new instrumental wavelength as a Double value.
     */
    public void setInstrumentalWavelength(Double wavelength)
    {
        MCSLogger.trace();

        // This value should never be auto-updated anymore
        _instrumentalWavelengthAutoUpdate     = false;

        _instrumentalWavelength               = wavelength;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the instrumental maximum base line for the actual query.
     *
     * @return the instrumental maximum base line as a Double value.
     */
    public Double getInstrumentalMaxBaseLine()
    {
        MCSLogger.trace();

        return _instrumentalMaxBaseLine;
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param wavelength the new instrumental maximum base line as a Double.
     */
    public void setInstrumentalMaxBaseLine(Double maxBaseLine)
    {
        MCSLogger.trace();

        _instrumentalMaxBaseLine = maxBaseLine;

        setChanged();
        notifyObservers();
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
        if (rightAscension.matches("[+|-][0-9]+:[0-9]+:[0-9]+.?[0-9]*") == false)
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
        if (declinaison.matches("[+|-][0-9]+:[0-9]+:[0-9]+.?[0-9]*") == false)
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

        return _scienceObjectMagnitude;
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a Double value.
     */
    public void setScienceObjectMagnitude(Double magnitude)
    {
        MCSLogger.trace();

        _scienceObjectMagnitude = magnitude;

        // Modify _queryMinMagnitude automatically if needed
        if (_queryMinMagnitudeAutoUpdate == true)
        {
            _queryMinMagnitude = _scienceObjectMagnitude - 2;
        }

        // Modify _queryMaxMagnitude automatically if needed
        if (_queryMaxMagnitudeAutoUpdate == true)
        {
            _queryMaxMagnitude = _scienceObjectMagnitude + 2;
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

        return _queryMinMagnitude;
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
     * Return the maximun calibrator magnitude for the actual query.
     *
     * @return the maximum magnitude as a Double value.
     */
    public Double getQueryMaxMagnitude()
    {
        MCSLogger.trace();

        return _queryMaxMagnitude;
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
     * Indicates wether the query is using the bright scenario or the faint one.
     *
     * @return true wether the query is of the bright type, otherwise false for
     * the faint ones.
     */
    public boolean isQueryScenarioBright()
    {
        return _queryBrightScenarioFlag;
    }

    /**
     * Set wether the query should use the bright scenario or the faint one.
     *
     * @param flag true for bright queries, false for faint ones.
     */
    public void setQueryBrightScenarion(boolean flag)
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

        return _queryDiffRASize;
    }

    /**
     * Change the query box differential RA size.
     *
     * @param diffRASize the new query box differential RA size as a Double.
     */
    public void setQueryDiffRASize(Double diffRASize)
    {
        MCSLogger.trace();

        _queryDiffRASize = diffRASize;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the query box differential DEC size.
     *
     * @return the query box differential DEC size.
     */
    public Double getQueryDiffDECSize()
    {
        MCSLogger.trace();

        return _queryDiffDECSize;
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param radiusSize the new query box differential DEC size as a Double.
     */
    public void setQueryDiffDECSize(Double diffDECSize)
    {
        MCSLogger.trace();

        _queryDiffDECSize = diffDECSize;

        setChanged();
        notifyObservers();
    }

    /**
     * Return the query box radial size.
     *
     * @return the query box radial size.
     */
    public Double getQueryRadialSize()
    {
        MCSLogger.trace();

        return _queryRadialSize;
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a Double.
     */
    public void setQueryRadialSize(Double radiusSize)
    {
        MCSLogger.trace();

        _queryRadialSize = radiusSize;

        setChanged();
        notifyObservers();
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

        // If the science object name is missing
        // TODO Verify that this parameter is really mandatory
        if (_scienceObjectName.length() < 1)
        {
            return false;
        }

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
}
/*___oOo___*/
