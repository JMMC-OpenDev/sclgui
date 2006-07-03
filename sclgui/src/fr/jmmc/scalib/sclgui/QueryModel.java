/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.3 2006-07-03 13:34:23 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
    /** The science object name */
    private String _scienceObjectName;

    /** The science object right assention coordinate */
    private String _ra;

    /** The science object declinaison coordinate */
    private String _dec;

    /** The science object right magnitude */
    private double _magnitude;

    /** The calibrators minimum magnitude */
    private double _minMagnitude;

    /** The calibrators maximum magnitude */
    private double _maxMagnitude;

    /** The querying scenario */
    private boolean _bright;

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

        setScienceObjectName("");
        setRa("+00:00:00.00");
        setDec("+00:00:00.00");
        setMagnitude(0.0);

        isBright(true);
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

        setScienceObjectName("eta_tau");
        setRa("+03:47:29.79");
        setDec("+24:06:18.50");
        setMagnitude(3);

        setTotalStep(11);

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
    }

    /**
     * Return the right ascension for the actual query.
     *
     * @return the right ascension.
     */
    public String getRa()
    {
        MCSLogger.trace();

        return _ra;
    }

    /**
     * Change the right ascension parameter for the actual query.
     *
     * @param ra the right ascension.
     */
    public void setRa(String ra) throws IllegalArgumentException
    {
        MCSLogger.trace();

        // Check if the given parameter is not empty
        if (ra.length() < 1)
        {
            throw new IllegalArgumentException("given RA is empty");
        }

        // Validate the format of the given value
        if (ra.matches("[+|-][0-9]+:[0-9]+:[0-9]+.?[0-9]*") == false)
        {
            throw new IllegalArgumentException("wrong RA format: +30:00:00.00");
        }

        _ra = ra;

        setChanged();
    }

    /**
     * Return the declinaison  for the actual query.
     *
     * @return the declinaison.
     */
    public String getDec()
    {
        MCSLogger.trace();

        return _dec;
    }

    /**
     * Change the declinaison parameter for the actual query.
     *
     * @param dec the declinaison.
     */
    public void setDec(String dec) throws IllegalArgumentException
    {
        MCSLogger.trace();

        // Check if given parameter is not empty
        if (dec.length() < 1)
        {
            throw new IllegalArgumentException("given DEC is empty");
        }

        // Validate the format of the given value
        if (dec.matches("[+|-][0-9]+:[0-9]+:[0-9]+.?[0-9]*") == false)
        {
            throw new IllegalArgumentException("wrong DEC format: +30:00:00.00");
        }

        _dec = dec;

        setChanged();
    }

    /**
     * Return the science object magnitude for the actual query.
     *
     * @return the science object magnitude as a float value.
     */
    public double getMagnitude()
    {
        MCSLogger.trace();

        return _magnitude;
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a float value.
     */
    public void setMagnitude(double magnitude)
    {
        MCSLogger.trace();

        _magnitude = magnitude;
        _minMagnitude = _magnitude - 2;
        _maxMagnitude = _magnitude + 2;

        setChanged();
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a string value.
     */
    public void setMagnitude(String magnitude) throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setMagnitude(Double.parseDouble(magnitude));
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
    public double getMinMagnitude()
    {
        MCSLogger.trace();

        return _minMagnitude;
    }

    /**
     * Change the minimun calibrator magnitude parameter.
     *
     * @param minMagnitude the new minimum magnitude as a float value.
     */
    public void setMinMagnitude(double minMagnitude)
    {
        MCSLogger.trace();

        _minMagnitude = minMagnitude;

        setChanged();
    }

    /**
     * Change the minimun calibrator magnitude parameter.
     *
     * @param minMagnitude the new minimum magnitude as a string value.
     */
    public void setMinMagnitude(String magnitude) throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setMinMagnitude(Double.parseDouble(magnitude));
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
    public double getMaxMagnitude()
    {
        MCSLogger.trace();

        return _maxMagnitude;
    }

    /**
     * Change the maximun calibrator magnitude parameter.
     *
     * @param maxMagnitude the new maximum magnitude as a float value.
     */
    public void setMaxMagnitude(double maxMagnitude)
    {
        MCSLogger.trace();

        _maxMagnitude = maxMagnitude;

        setChanged();
    }

    /**
     * Change the maximun calibrator magnitude parameter.
     *
     * @param maxMagnitude the new minimum magnitude as a string value.
     */
    public void setMaxMagnitude(String magnitude) throws IllegalArgumentException
    {
        MCSLogger.trace();

        try
        {
            setMaxMagnitude(Double.parseDouble(magnitude));
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
    public boolean isBright()
    {
        return _bright;
    }

    /**
     * Set wether the query should use the bright scenario or the faint one.
     *
     * @param flag true for bright queries, false for faint ones.
     */
    public void isBright(boolean flag)
    {
        _bright = flag;

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

        // If the ra coordinate is not defined
        if (_ra.length() < 1)
        {
            return false;
        }

        // If the dec coordinate is not defined
        if (_dec.length() < 1)
        {
            return false;
        }

        // The magnitude is not tested as any float value is valid
        return true;
    }
}
/*___oOo___*/
