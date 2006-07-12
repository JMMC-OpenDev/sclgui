/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalibrators.java,v 1.6 2006-07-12 15:51:51 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2006/07/12 14:29:39  lafrasse
 * Added the status bar
 *
 * Revision 1.4  2006/07/11 11:11:57  mella
 * Change instanciation order
 *
 * Revision 1.3  2006/06/30 07:57:58  lafrasse
 * Removed default query parameters definition
 *
 * Revision 1.2  2006/04/07 08:41:03  mella
 * Preferences singleton is accessed using Preferences.getInstance()
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;
import jmmc.mcs.util.StatusBar;

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

        // Retrieve application preferences and attach them to their view
        // (This instance must be instanciated after dependencies)
        PreferencesView preferencesView = new PreferencesView();
        StatusBar       statusBar       = new StatusBar();
        // Show the user the app is been initialized
        StatusBar.show("application initialization...");

        MainWindow window = new MainWindow(queryView, calibratorsView,
                preferencesView, filtersView, statusBar);

        // Make application presentation coherent with preferences
        Preferences.getInstance().notifyObservers();
    }

    /**
     * Main.
     */
    public static void main(String[] args)
    {
        // Get a MCSLogger reference  and adjust for convenience
        // @TODO : move this in MCSLogger
        Logger logger = MCSLogger.getLogger();
        logger.setLevel(java.util.logging.Level.ALL);
        MCSLogger.trace();

        // Create a console handler
        // @TODO : move this in MCSLogger
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(java.util.logging.Level.ALL);
        logger.addHandler(handler);

        // Launch application
        SearchCalibrators searchCalibrators = new SearchCalibrators();
    }
}
/*___oOo___*/
