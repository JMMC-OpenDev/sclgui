/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VirtualObservatory.java,v 1.4 2006-08-04 16:35:43 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/06/30 08:01:23  lafrasse
 * Added fake progress bar updates and science object completion API
 *
 * Revision 1.2  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.net.URL;

import java.util.logging.Logger;

import javax.swing.*;


/**
 * Handle JMMC GETCAL WebServices interactions.
 */
public class VirtualObservatory
{
    /** Query model */
    QueryModel _queryModel;

    /** Data model to which the result should be passed */
    CalibratorsModel _calibratorsModel;

    /** Filters model */
    FiltersModel _filtersModel;

    /**
     * Contructor.
     */
    public VirtualObservatory(QueryModel queryModel,
        CalibratorsModel calibratorsModel, FiltersModel filtersModel)
    {
        _queryModel           = queryModel;
        _calibratorsModel     = calibratorsModel;
        _filtersModel         = filtersModel;
    }

    /**
     * Get calibrator list as a raw VOTable from JMMC web service.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    public void getCal(QueryModel queryModel) throws Exception
    {
        MCSLogger.trace();
        class ProgressBarThread extends Thread
        {
            QueryModel _queryModel;
            int        _millisecond;

            ProgressBarThread(QueryModel queryModel, int millisecond)
            {
                _queryModel      = queryModel;
                _millisecond     = millisecond;
            }

            public void run()
            {
                for (int i = 0; i <= _queryModel.getTotalStep(); i++)
                {
                    _queryModel.setCurrentStep(i);

                    try
                    {
                        Thread.sleep(_millisecond);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }

        ProgressBarThread thread = new ProgressBarThread(_queryModel, 200);
        thread.start();

        try
        {
            // TODO temp hack to load a VOTable file name according to the query science object name, for test purpose only
            //String         fileName   = queryModel.getScienceObjectName() + ".vot";
            //FileReader     fileReader = new FileReader(fileName);
            //BufferedReader in         = new BufferedReader(fileReader);
            String resourceName = "eta_tau.vot";
            URL    votableURL   = VirtualObservatory.class.getResource(resourceName);
            // Read all the text returned by the embedded file
            System.out.println(resourceName + "-->" + votableURL);

            BufferedReader in  = new BufferedReader(new InputStreamReader(
                        votableURL.openStream()));

            StringBuffer   sb  = new StringBuffer();
            String         str;

            while ((str = in.readLine()) != null)
            {
                sb.append(str);
            }

            in.close();

            _calibratorsModel.parseVOTable(sb.toString());
        }
        catch (IOException e)
        {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Get science object properties from is name through Simbad web service.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    public void getScienceObject() throws Exception
    {
        MCSLogger.trace();

        // TODO : Querying Simbad and fill the query model accordinally
        _queryModel.init();
    }
}
/*___oOo___*/
