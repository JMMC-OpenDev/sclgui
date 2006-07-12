/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.4 2006-07-12 14:30:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.util.Observable;
import java.util.logging.Logger;


/**
 * Query model.
 */
public class QueryModel extends Observable
{
    /** The instrumental magnitude band */
    private String _instrumentalMagnitudeBand;

    /** The instrumental wavelength */
    private double _instrumentalWavelength;

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

    /** The query minimum magnitude */
    private double _queryMinMagnitude;

    /** The calibrators maximum magnitude */
    private double _queryMaxMagnitude;

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

    /**
     * Default constructor.
     */
    public QueryModel()
    {
        MCSLogger.trace();

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
        setInstrumentalMaxBaseLine(100);

        setScienceObjectName("");
        setScienceObjectRA("+00:00:00.00");
        setScienceObjectDEC("+00:00:00.00");
        setScienceObjectMagnitude(0.0);

        setQueryBrightScenarion(true);
        setQueryDiffRASize(10);
        setQueryDiffDECSize(5);

        setCurrentStep(0);
        setTotalStep(0);

        notifyObservers();
    }

    /**
     * Set default values to all properties.
     */
    public void example()
    {
        MCSLogger.trace();

        setInstrumentalMagnitudeBand("V");
        setInstrumentalWavelength(3.3);
        setInstrumentalMaxBaseLine(123.4);

        setScienceObjectName("eta_tau");
        setScienceObjectRA("+03:47:29.79");
        setScienceObjectDEC("+24:06:18.50");
        setScienceObjectMagnitude(3);

        setQueryMinMagnitude(2);
        setQueryMaxMagnitude(5);
        setQueryDiffRASize(11);
        setQueryDiffDECSize(6);

        setTotalStep(11);

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

        return _instrumentalMagnitudeBand;
    }

    /**
     * Change the instrumental magnitude band for the actual query.
     *
     * @param scienceObjectName the new instrumental magnitude band.
     */
    public void setInstrumentalMagnitudeBand(String magnitudeBand)
    {
        MCSLogger.trace();

        _instrumentalMagnitudeBand = magnitudeBand;

        // @TODO : should modify _instrumentalWavelength automatically if unset
        setChanged();
    }

    /**
     * Return the instrumental wavelength for the actual query.
     *
     * @return the instrumental wavelength as a float value.
     */
    public double getInstrumentalWavelength()
    {
        MCSLogger.trace();

        return _instrumentalWavelength;
    }

    /**
     * Change the instrumental wavelength parameter.
     *
     * @param wavelength the new instrumental wavelength as a float value.
     */
    public void setInstrumentalWavelength(double wavelength)
    {
        MCSLogger.trace();

        _instrumentalWavelength = wavelength;

        setChanged();
    }

    /**
     * Return the instrumental maximum base line for the actual query.
     *
     * @return the instrumental maximum base line as a float value.
     */
    public double getInstrumentalMaxBaseLine()
    {
        MCSLogger.trace();

        return _instrumentalMaxBaseLine;
    }

    /**
     * Change the instrumental maximum base line parameter.
     *
     * @param wavelength the new instrumental maximum base line as a float.
     */
    public void setInstrumentalMaxBaseLine(double maxBaseLine)
    {
        MCSLogger.trace();

        _instrumentalMaxBaseLine = maxBaseLine;

        setChanged();
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
    }

    /**
     * Return the science object magnitude for the actual query.
     *
     * @return the science object magnitude as a float value.
     */
    public double getScienceObjectMagnitude()
    {
        MCSLogger.trace();

        return _scienceObjectMagnitude;
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a float value.
     */
    public void setScienceObjectMagnitude(double magnitude)
    {
        MCSLogger.trace();

        _scienceObjectMagnitude     = magnitude;
        _queryMinMagnitude          = _scienceObjectMagnitude - 2;
        _queryMaxMagnitude          = _scienceObjectMagnitude + 2;

        setChanged();
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a string value.
     */
    public void setScienceObjectMagnitude(String magnitude)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setScienceObjectMagnitude(Double.parseDouble(magnitude));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Magnitude must be a number");
        }
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a Double value.
     */
    public void setScienceObjectMagnitude(Double magnitude)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setScienceObjectMagnitude(magnitude.doubleValue());
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Magnitude must be a number");
        }
    }

    /**
     * Return the minimun calibrator magnitude for the actual query.
     *
     * @return the minimum magnitude as a float value.
     */
    public double getQueryMinMagnitude()
    {
        MCSLogger.trace();

        return _queryMinMagnitude;
    }

    /**
     * Change the minimun calibrator magnitude parameter.
     *
     * @param minMagnitude the new minimum magnitude as a float value.
     */
    public void setQueryMinMagnitude(double minMagnitude)
    {
        MCSLogger.trace();

        _queryMinMagnitude = minMagnitude;

        setChanged();
    }

    /**
     * Change the minimun calibrator magnitude parameter.
     *
     * @param minMagnitude the new minimum magnitude as a string value.
     */
    public void setQueryMinMagnitude(String magnitude)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setQueryMinMagnitude(Double.parseDouble(magnitude));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Magnitude must be a number");
        }

        setChanged();
    }

    /**
     * Return the maximun calibrator magnitude for the actual query.
     *
     * @return the maximum magnitude as a float value.
     */
    public double getQueryMaxMagnitude()
    {
        MCSLogger.trace();

        return _queryMaxMagnitude;
    }

    /**
     * Change the maximun calibrator magnitude parameter.
     *
     * @param maxMagnitude the new maximum magnitude as a float value.
     */
    public void setQueryMaxMagnitude(double maxMagnitude)
    {
        MCSLogger.trace();

        _queryMaxMagnitude = maxMagnitude;

        setChanged();
    }

    /**
     * Change the maximun calibrator magnitude parameter.
     *
     * @param maxMagnitude the new minimum magnitude as a string value.
     */
    public void setQueryMaxMagnitude(String magnitude)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setQueryMaxMagnitude(Double.parseDouble(magnitude));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Magnitude must be a number");
        }

        setChanged();
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
    }

    /**
     * Return the query box differential RA size.
     *
     * @return the query box differential RA size.
     */
    public double getQueryDiffRASize()
    {
        MCSLogger.trace();

        return _queryDiffRASize;
    }

    /**
     * Change the query box differential RA size.
     *
     * @param diffRASize the new query box differential RA size as a float.
     */
    public void setQueryDiffRASize(double diffRASize)
    {
        MCSLogger.trace();

        _queryDiffRASize = diffRASize;

        setChanged();
    }

    /**
     * Change the query box differential RA size.
     *
     * @param diffRASize the new query box differential RA size as a string.
     */
    public void setQueryDiffRASize(String diffRASize)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setQueryDiffRASize(Double.parseDouble(diffRASize));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Diff. RA must be a number");
        }

        setChanged();
    }

    /**
     * Return the query box differential DEC size.
     *
     * @return the query box differential DEC size.
     */
    public double getQueryDiffDECSize()
    {
        MCSLogger.trace();

        return _queryDiffDECSize;
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param radiusSize the new query box differential DEC size as a float.
     */
    public void setQueryDiffDECSize(double diffDECSize)
    {
        MCSLogger.trace();

        _queryDiffDECSize = diffDECSize;

        setChanged();
    }

    /**
     * Change the query box differential DEC size.
     *
     * @param diffDECSize the new query box differential DEC size as a string.
     */
    public void setQueryDiffDECSize(String diffDECSize)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setQueryDiffDECSize(Double.parseDouble(diffDECSize));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Diff. DEC must be a number");
        }

        setChanged();
    }

    /**
     * Return the query box radial size.
     *
     * @return the query box radial size.
     */
    public double getQueryRadialSize()
    {
        MCSLogger.trace();

        return _queryRadialSize;
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a float.
     */
    public void setQueryRadialSize(double radiusSize)
    {
        MCSLogger.trace();

        _queryRadialSize = radiusSize;

        setChanged();
    }

    /**
     * Change the query box radial size.
     *
     * @param radiusSize the new query box radial size as a string.
     */
    public void setQueryRadialSize(String radiusSize)
        throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setQueryRadialSize(Double.parseDouble(radiusSize));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Diff. DEC must be a number");
        }

        setChanged();
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

        // The magnitude is not tested as any float value is valid
        return true;
    }
}
/*___oOo___*/
