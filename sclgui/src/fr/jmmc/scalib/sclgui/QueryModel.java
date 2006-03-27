/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

    /**
     * Default constructor.
     */
    public QueryModel()
    {
        _scienceObjectName     = "";
        _ra                    = "";
        _dec                   = "";
        _magnitude             = 0.0;
    }

    /**
     * Every method that change the data should call this method.
     * // Controllers should call notifyObservers on model to make views updated
     */
    private void dataChanged()
    {
        MCSLogger.trace();

        setChanged();

        // notifyObservers();
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
        dataChanged();
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
        dataChanged();
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
        dataChanged();
    }

    /**
     * Return the magnitude for the actual query.
     *
     * @return the magnitude.
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
        dataChanged();
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
