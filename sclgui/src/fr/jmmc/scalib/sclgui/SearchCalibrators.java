/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalibrators.java,v 1.2 2006-04-07 08:41:03 mella Exp $"
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

import java.util.logging.*;
import java.util.logging.Logger;


/**
 * @file
 * brief description of the program, which ends at this dot.
 *
 * @synopsis
 * \e \<Command Name\> [\e \<param1\> ... \e \<paramN\>]
 *                     [\e \<option1\> ... \e \<optionN\>]
 *
 * @param param1 : description of parameter 1
 * @param paramN : description of parameter N
 *
 * \n
 * @opt
 * @optname option1 : description of option 1
 * @optname optionN : description of option N
 *
 * \n
 * @details
 * OPTIONAL detailed description of the c main file follows here.
 *
 * @usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * @filename fileName1 :  usage description of fileName1
 * @filename fileName2 :  usage description of fileName2
 *
 * \n
 * @env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * @envvar envVar1 :  usage description of envVar1
 * @envvar envVar2 :  usage description of envVar2
 *
 * \n
 * @warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * \n
 * @ex
 * OPTIONAL. Command example if needed
 * \n Brief example description.
 * @code
 * Insert your command example here
 * @endcode
 *
 * @sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * @sa modcppOPERATION.C
 *
 * @bug OPTIONAL. Known bugs list if it exists.
 * @bug Bug 1 : bug 1 description
 *
 * @todo OPTIONAL. Things to forsee list.
 * @todo Action 1 : action 1 description
 *
 */
public class SearchCalibrators
{
    /**
     * Create all objects needed by SearchCalibrators and plug event responding
     * loop (Listenr/listenable, Observer/Observable) in.
     */
    public SearchCalibrators()
    {
        // Retrieve application preferences and attach them to their view
        PreferencesView preferencesView = new PreferencesView();

        // Create filters
        FiltersModel filtersModel = new FiltersModel();
        FiltersView  filtersView  = new FiltersView(filtersModel);

        // Create a calibrators model and attach it to a calibrators view
        CalibratorsModel calibratorsModel = new CalibratorsModel(filtersModel);
        CalibratorsView  calibratorsView  = new CalibratorsView(calibratorsModel);

        filtersModel.addObserver(calibratorsModel);

        VirtualObservatory vo = new VirtualObservatory(calibratorsModel);

        // Create a query model and attach it to a query view
        QueryModel queryModel = new QueryModel();
        QueryView  queryView  = new QueryView(queryModel, vo);

        queryModel.setScienceObjectName("eta_tau");
        queryModel.setRa("+30:00:00.00");
        queryModel.setDec("+30:00:00.00");
        queryModel.setMagnitude(1.0);
        queryModel.notifyObservers();

        MainWindow window = new MainWindow(queryView, calibratorsView,
                preferencesView, filtersView);
    }

    /**
     * Main.
     */
    public static void main(String[] args)
    {
        // Get a MCSLogger reference  and adjust for convenience
        Logger logger = MCSLogger.getLogger();
        logger.setLevel(java.util.logging.Level.ALL);
        MCSLogger.trace();

        // Create a console handler
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(java.util.logging.Level.ALL);
        logger.addHandler(handler);

        // Launch application
        SearchCalibrators searchCalibrators = new SearchCalibrators();
    }
}
/*___oOo___*/
