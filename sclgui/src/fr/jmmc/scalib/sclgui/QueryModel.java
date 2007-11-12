/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: QueryModel.java,v 1.33 2007-10-04 15:04:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.32  2007/10/04 10:54:53  lafrasse
 * Added 'N' magnitude band support.
 *
 * Revision 1.31  2007/09/18 11:28:33  lafrasse
 * Handle undefined magnitudde values returned by Simbad CDS.
 *
 * Revision 1.30  2007/08/09 07:54:33  lafrasse
 * Reverted automatic min and max magnitude computation.
 *
 * Revision 1.29  2007/08/03 13:21:32  lafrasse
 * Corrected a typo.
 *
 * Revision 1.28  2007/08/03 13:11:41  lafrasse
 * Enhanced wavelengthes et and science object magnitudes management according to
 * comments from Daniel BONNEAU.
 *
 * Revision 1.27  2007/08/02 12:19:57  lafrasse
 * Corrected delayed GUI updated when changing bright/faint scenarion radio buttons
 * as noted in Denis MOURARD review comments.
 *
 * Revision 1.26  2007/08/01 15:29:22  lafrasse
 * Added support for SIMDAD science object querying through URL+script (plus proof
 * of disfunctionning through SOAP).
 *
 * Revision 1.25  2007/06/29 09:56:50  lafrasse
 * Added support for science object inclusion.
 *
 * Revision 1.24  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.23  2007/04/11 13:51:47  lafrasse
 * Corrected a bug that prevented automatic update of wavelength on magnitude band changes.
 *
 * Revision 1.22  2007/02/16 17:17:18  lafrasse
 * Added support for true catalog namei queried.
 *
 * Revision 1.21  2007/02/16 15:20:54  lafrasse
 * Enhanced min & max magnitude interdependance.
 *
 * Revision 1.20  2007/02/13 16:17:58  lafrasse
 * Jalopyzation.
 *
 * Revision 1.19  2007/02/13 16:16:12  lafrasse
 * Enabled JMMC SearchCal SOAP webservice querying.
 *
 * Revision 1.18  2007/02/13 15:34:39  lafrasse
 * Jalopyzation.
 *
 * Revision 1.17  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.16  2006/11/29 22:44:20  lafrasse
 * Added support for the whole query panel disabling on values loaded from file.
 *
 * Revision 1.15  2006/11/27 16:58:37  lafrasse
 * Updated magnitude bands order and corresponding wavelength values.
 *
 * Revision 1.14  2006/11/27 15:49:23  lafrasse
 * Update preferences string tokens.
 *
 * Revision 1.13  2006/11/23 16:24:41  lafrasse
 * Added query parameters parsing and loading from VOTable files.
 *
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
package fr.jmmc.scalib.sclgui;

import cds.savot.model.*;

import cds.savot.pull.*;

import cds.savot.writer.*;

import fr.jmmc.mcs.log.*;

import java.io.*;

import java.util.*;

import javax.swing.DefaultComboBoxModel;


/**
 * Query model.
 */
public class QueryModel extends Observable implements Observer
{
    /** For default values */
    private Preferences _preferences;

    /** The instrumental magnitude band */
    private DefaultComboBoxModel _instrumentalMagnitudeBands;

    /** Magnitude to Preselected Wavelength conversion table */
    private Hashtable _magnitudeBandToWavelength = new Hashtable();

    /**
     * Default magnitude bands.
     *
     * @warning Do not change the order, as it is used to parse Simbad VOTables.
     */
    private String[] _magnitudeBands = { "V", "I", "J", "H", "K", "N" };

    /**
     * Default magnitude band wavelengthes in micrometer.
     *
     * @warning Do not change the order,as it is linked to _magnitudeBands.
     * @sa _magnitudeBands
     */
    private double[] _defaultWavelengths = { 0.55, 0.9, 1.25, 1.65, 2.2, 10 };

    /** The instrumental maximum base line */
    private double _instrumentalMaxBaseLine;

    /** The science object name */
    private String _scienceObjectName;

    /** The science object right assention coordinate */
    private String _scienceObjectRA;

    /** The science object declinaison coordinate */
    private String _scienceObjectDEC;

    /** Band to Science Object Loaded Magnitude conversion table */
    private Hashtable _scienceObjectMagnitudes = new Hashtable();

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

    /** The current catalog name of the querying progress step. */
    private String _catalogName;

    /** Remind whether the query can be edited or not (when loaded from file for example) */
    private boolean _isEditable;

    /**
     * Default constructor.
     */
    public QueryModel() throws Exception
    {
        MCSLogger.trace();

        _preferences = Preferences.getInstance();
        _preferences.addObserver(this);

        resetInstrumentalWavelengthes();

        // For each "band-predefined magnitudes" couple
        for (int i = 0; i < _magnitudeBands.length; i++)
        {
            // Construct the conversion table between both
            _scienceObjectMagnitudes.put(_magnitudeBands[i], Double.NaN);
        }

        _instrumentalMagnitudeBands = new DefaultComboBoxModel(_magnitudeBands);

        // Initialize values from user defined preferences
        try
        {
            loadDefaultValues();
        }
        catch (Exception e)
        {
            throw e;
        }
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
        // Then inform any object that observe us that we also probably change.
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
        resetInstrumentalWavelengthes();

        setScienceObjectName("");
        setScienceObjectRA("+00:00:00.00");
        setScienceObjectDEC("+00:00:00.00");
        resetScienceObjectMagnitudes();

        setScienceObjectInclusionFlag(true);

        setQueryBrightScenarioFlag(true);
        setQueryDiffRASize(10.0);
        setQueryDiffDECSize(5.0);
        setQueryRadialSize(0.0);

        restoreMinMaxMagnitudeFieldsAutoUpdating();

        setCurrentStep(0);
        setTotalStep(0);
        setCatalogName("");

        // Enable the edition as the values where not loaded from file
        setEditableState(true);

        notifyObservers();
    }

    /**
     * Reset all properties to their default values.
     */
    public void loadDefaultValues() throws Exception
    {
        MCSLogger.trace();

        try
        {
            setInstrumentalMagnitudeBand(_preferences.getPreference(
                    "query.magnitudeBand"));

            setInstrumentalMaxBaseLine(_preferences.getPreferenceAsDouble(
                    "query.instrumentalMaxBaseLine"));

            setScienceObjectName(_preferences.getPreference(
                    "query.scienceObjectName"));
            setScienceObjectRA(_preferences.getPreference(
                    "query.scienceObjectRA"));
            setScienceObjectDEC(_preferences.getPreference(
                    "query.scienceObjectDEC"));
            setScienceObjectMagnitude(_preferences.getPreferenceAsDouble(
                    "query.scienceObjectMagnitude"));
            setScienceObjectInclusionFlag(_preferences.getPreferenceAsBoolean(
                    "query.scienceObjectInclusionFlag"));

            setQueryMinMagnitude(_preferences.getPreferenceAsDouble(
                    "query.queryMinMagnitude"));
            setQueryMinMagnitudeDelta(_preferences.getPreferenceAsDouble(
                    "query.queryMinMagnitudeDelta"));
            setQueryMaxMagnitude(_preferences.getPreferenceAsDouble(
                    "query.queryMaxMagnitude"));
            setQueryMaxMagnitudeDelta(_preferences.getPreferenceAsDouble(
                    "query.queryMaxMagnitudeDelta"));

            setQueryBrightScenarioFlag(_preferences.getPreferenceAsBoolean(
                    "query.queryBrightScenarioFlag"));
            setQueryDiffRASize(_preferences.getPreferenceAsDouble(
                    "query.queryDiffRASize"));
            setQueryDiffDECSize(_preferences.getPreferenceAsDouble(
                    "query.queryDiffDECSize"));
            setQueryRadialSize(_preferences.getPreferenceAsDouble(
                    "query.queryRadialSize"));
        }
        catch (Exception e)
        {
            throw e;
        }

        // Enable the edition as the values where not loaded from file
        setEditableState(true);

        notifyObservers();
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

        notifyObservers();
    }

    /**
     * Reset all properties to their default values.
     */
    public void loadFromSimbadVOTable(String simbadVOTable)
        throws Exception
    {
        MCSLogger.trace();

        try
        {
            // Parse using SAVOT
            // Put the whole VOTable file into memory
            SavotPullParser parser = new SavotPullParser(new StringBufferInputStream(
                        simbadVOTable), SavotPullEngine.FULL, "UTF-8");

            // Parse the VOTable
            SavotVOTable parsedVOTable = parser.getVOTable();

            // Get the VOTable resources
            ResourceSet resourceSet = parsedVOTable.getResources();

            // Get the first table of the first resource
            // WARNING : this is not compatible with other VOTable than JMMC ones
            // (0 should not be used, but the name of the Resource instead)
            SavotResource resource = (SavotResource) resourceSet.getItemAt(0);
            SavotTable    table    = (SavotTable) resource.getTables()
                                                          .getItemAt(0);
            FieldSet      fieldSet = table.getFields();

            // For first data row (if any)
            TRSet rows = resource.getTRSet(0);

            for (int rowId = 0; rowId < Math.min(1, rows.getItemCount());
                    rowId++)
            {
                // Get the data corresponding to the current row
                TDSet row = rows.getTDSet(rowId);

                // @TODO : highly unportable as it is heavily dependant on the order of
                //         the desired SIMBAD VOTable defined in
                //         VirtualObservatory::GetStarAction::GetStarThread::simbadResult()
                int    index = 0;
                String str;
                str          = (String) row.getContent(index++);

                if (str.length() != 0)
                {
                    str = str.replace(' ', ':');
                    MCSLogger.debug("loaded RA = '" + str + "'.");
                    setScienceObjectRA(str);
                }

                str = (String) row.getContent(index++);

                if (str.length() != 0)
                {
                    str = str.replace(' ', ':');
                    MCSLogger.debug("loaded DEC = '" + str + "'.");
                    setScienceObjectDEC(str);
                }

                // For each "magnitude-band" couple
                for (int i = 0; i < _magnitudeBands.length; i++)
                {
                    String currentMagnitudeBand = _magnitudeBands[i];
                    Double loadedValue          = Double.NaN;

                    try
                    {
                        str = (String) row.getContent(index++);

                        if (str.length() != 0)
                        {
                            MCSLogger.debug("loaded '" + currentMagnitudeBand +
                                "' Mag = '" + str + "'.");
                            loadedValue = new Double(str);
                        }
                        else
                        {
                            MCSLogger.debug("loaded '" + currentMagnitudeBand +
                                "' Mag = 'NaN'.");
                        }
                    }
                    catch (Exception exc)
                    {
                        MCSLogger.debug("blanked '" + currentMagnitudeBand +
                            "' Mag = 'NaN'.");
                    }

                    _scienceObjectMagnitudes.put(currentMagnitudeBand,
                        loadedValue);
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        restoreMinMaxMagnitudeFieldsAutoUpdating();

        // Enable the edition as the values where not loaded from file
        setEditableState(true);
        notifyObservers();
    }

    /**
     * Store all current properties as the futur default values.
     */
    public void saveDefaultValues() throws Exception
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

            _preferences.setPreference("query.queryMinMagnitude",
                getQueryMinMagnitude());
            _preferences.setPreference("query.queryMaxMagnitude",
                getQueryMaxMagnitude());
            _preferences.setPreference("query.queryBrightScenarioFlag",
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
            throw e;
        }
    }

    /**
     * Return all current properties as an MCS-like sclsvr-formatted query.
     *
     * @return the query as a string.
     */
    public String getQueryAsMCSString()
    {
        MCSLogger.trace();

        String query = "";

        // Object name
        query += ("-objectName " + getScienceObjectName() + " ");

        // Magnitude
        query += ("-mag " + getScienceObjectMagnitude() + " ");

        // Diff RA
        query += ("-diffRa " + getQueryDiffRASize() + " ");

        // Diff DEC
        query += ("-diffDec " + getQueryDiffDECSize() + " ");

        // Band
        query += ("-band " + getInstrumentalMagnitudeBand() + " ");

        // Min Magnitude
        query += ("-minMagRange " + getQueryMinMagnitude() + " ");

        // Max Magnitude
        query += ("-maxMagRange " + getQueryMaxMagnitude() + " ");

        // RA
        query += ("-ra " + getScienceObjectRA() + " ");

        // DEC
        query += ("-dec " + getScienceObjectDEC() + " ");

        // Max Baseline
        query += ("-baseMax " + getInstrumentalMaxBaseLine() + " ");

        // Wavelength
        query += ("-wlen " + getInstrumentalWavelength() + " ");

        // Radius
        Boolean brightFlag = getQueryBrightScenarioFlag();

        if (brightFlag == false)
        {
            query += ("-radius " + getQueryRadialSize() + " ");
        }

        // Bright/Faint flag
        query += ("-bright " + brightFlag + " ");

        // Get the science star
        query += ("-noScienceStar false");

        return query;
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

        setChanged();
    }

    /**
     * Reset the instrumental wavelength parameters.
     *
     * @param wavelength the new instrumental wavelength as a double value.
     */
    public void resetInstrumentalWavelengthes()
    {
        MCSLogger.trace();

        _magnitudeBandToWavelength = new Hashtable();

        // For each "magnitude band-predefined wavelength" couple
        for (int i = 0; i < _magnitudeBands.length; i++)
        {
            // Construct the conversion table between both
            _magnitudeBandToWavelength.put(_magnitudeBands[i],
                new Double(_defaultWavelengths[i]));
        }

        setChanged();
    }

    /**
     * Return the instrumental wavelength for the actual query.
     *
     * @return the instrumental wavelength as a Double value.
     */
    public Double getInstrumentalWavelength()
    {
        MCSLogger.trace();

        String currentMagnitudeBand = getInstrumentalMagnitudeBand();

        return (Double) _magnitudeBandToWavelength.get(currentMagnitudeBand);
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

        setInstrumentalWavelength(new Double(wavelength));
    }

    /**
     * Change the instrumental wavelength parameter.
     * Calling this method once disable the instrumental wavelength auto-update.
     *
     * @param wavelength the new instrumental wavelength as a Double value.
     */
    public void setInstrumentalWavelength(Double wavelength)
    {
        String currentMagnitudeBand = getInstrumentalMagnitudeBand();
        _magnitudeBandToWavelength.put(currentMagnitudeBand, wavelength);

        setChanged();
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
    }

    /**
     * Reset the science object magnitudes.
     *
     * @param wavelength the new instrumental wavelength as a double value.
     */
    public void resetScienceObjectMagnitudes()
    {
        MCSLogger.trace();

        _scienceObjectMagnitudes = new Hashtable();

        // For each "magnitude-band" couple
        for (int i = 0; i < _magnitudeBands.length; i++)
        {
            // Construct the conversion table between both
            _scienceObjectMagnitudes.put(_magnitudeBands[i], Double.NaN);
        }

        setChanged();
    }

    /**
     * Return the science object magnitude for the current band.
     *
     * @return the science object magnitude as a Double value.
     */
    public Double getScienceObjectMagnitude()
    {
        MCSLogger.trace();

        String currentBand = (String) _instrumentalMagnitudeBands.getSelectedItem();

        return (Double) _scienceObjectMagnitudes.get(currentBand);
    }

    /**
     * Change the magnitude parameter for the current band.
     *
     * @param magnitude the new magnitude as a double value.
     */
    public void setScienceObjectMagnitude(Double magnitude)
    {
        MCSLogger.trace();

        String currentBand = (String) _instrumentalMagnitudeBands.getSelectedItem();
        _scienceObjectMagnitudes.put(currentBand, magnitude);

        // Modify _queryMinMagnitude automatically if needed
        if (_queryMinMagnitudeAutoUpdate == true)
        {
            _queryMinMagnitude = magnitude + getQueryMinMagnitudeDelta();
        }

        // Modify _queryMaxMagnitude automatically if needed
        if (_queryMaxMagnitudeAutoUpdate == true)
        {
            _queryMaxMagnitude = magnitude + getQueryMaxMagnitudeDelta();
        }

        setChanged();
    }

    /**
     * Change the magnitude parameter.
     *
     * @param magnitude the new magnitude as a Double value.
     */
    public void setScienceObjectMagnitude(double magnitude)
    {
        setScienceObjectMagnitude(new Double(magnitude));
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
        throws Exception
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.scienceObjectDetectionDistance",
                new Double(distance));
        }
        catch (Exception e)
        {
            throw e;
        }

        setChanged();
    }

    /**
     * Restore min & max magnitude fields auto-updating.
     */
    public void restoreMinMaxMagnitudeFieldsAutoUpdating()
    {
        _queryMinMagnitudeAutoUpdate     = true;
        _queryMaxMagnitudeAutoUpdate     = true;
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
        _queryMinMagnitudeAutoUpdate     = false;

        _queryMinMagnitude               = minMagnitude;
        setChanged();

        // If the given min value is greater than the max magnitude
        if (_queryMinMagnitude > _queryMaxMagnitude)
        {
            // Update max magnitude accordinaly
            _queryMaxMagnitude = _queryMinMagnitude;
            notifyObservers();
        }
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
        throws Exception
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.queryMinMagnitudeDelta",
                new Double(delta));
        }
        catch (Exception e)
        {
            throw e;
        }

        setChanged();
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
        _queryMaxMagnitudeAutoUpdate     = false;

        _queryMaxMagnitude               = maxMagnitude;
        setChanged();

        // If the given max value is lower than to the min magnitude
        if (_queryMaxMagnitude < _queryMinMagnitude)
        {
            // Update min magnitude accordinaly
            _queryMinMagnitude = _queryMaxMagnitude;
            notifyObservers();
        }
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
        throws Exception
    {
        MCSLogger.trace();

        try
        {
            _preferences.setPreference("query.queryMaxMagnitudeDelta",
                new Double(delta));
        }
        catch (Exception e)
        {
            throw e;
        }

        setChanged();
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
     * Return the current catalog name.
     *
     * @return the current catalog name.
     */
    public String getCatalogName()
    {
        MCSLogger.trace();

        if (_currentStep == 0)
        {
            return " ";
        }

        return _catalogName;
    }

    /**
     * Set the current catalog name of query.
     *
     * @param the catalog name of querying step.
     */
    public void setCatalogName(String catalogName)
    {
        MCSLogger.trace();

        _catalogName = catalogName;

        setChanged();
        notifyObservers();
    }

    /**
     * Indicates to listener if this query is well filled and can be consumed.
     * If it returns false, then one reason must be given TBD...
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
    public boolean canBeEdited()
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
    }
}
/*___oOo___*/
