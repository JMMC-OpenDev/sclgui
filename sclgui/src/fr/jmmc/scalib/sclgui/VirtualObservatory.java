/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VirtualObservatory.java,v 1.2 2006-04-12 12:30:02 lafrasse Exp $"
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

import java.io.*;

import java.net.URL;

import java.util.logging.Logger;


/**
 * Handle JMMC GETCAL WebServices interactions.
 */
public class VirtualObservatory
{
    /** Data model to which the result should be passed */
    CalibratorsModel _calibratorsModel;

    /**
     * Contructor.
     */
    public VirtualObservatory(CalibratorsModel calibratorsModel)
    {
        _calibratorsModel = calibratorsModel;
    }

    /**
     * Get calibrator list as a raw VOTable from JMMC web service.
     *
     * @param queryModel the query to execute.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    public void GetCal(QueryModel queryModel) throws Exception
    {
        MCSLogger.trace();

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
}
/*___oOo___*/
