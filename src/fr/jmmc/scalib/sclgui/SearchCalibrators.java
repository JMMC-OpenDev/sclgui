/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalibrators.java,v 1.29 2010-10-10 22:21:05 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.28  2010/10/04 15:59:21  lafrasse
 * Removed redundant menu bar.
 * Now exits when a problem occurs during initialization.
 *
 * Revision 1.27  2009/04/30 12:56:51  lafrasse
 * Minor documentation update.
 *
 * Revision 1.26  2009/02/17 15:44:31  lafrasse
 * Updated comments.
 *
 * Revision 1.25  2008/10/16 12:39:33  lafrasse
 * Changed finnish() to finish() to reflect JMCS API typo correction.
 *
 * Revision 1.24  2008/09/18 21:03:21  lafrasse
 * Added Preference update notification triggering to fully finish GUI setup
 * (TableCellColors).
 *
 * Revision 1.23  2008/09/10 22:37:25  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Moved to new JMCS APIs.
 *
 * Revision 1.22  2008/05/29 10:10:16  mella
 * Accept null value for SearchCal Argument
 *
 * Revision 1.21  2008/05/19 15:39:29  lafrasse
 * Updated to add preliminary support for the new JMCS application framework.
 *
 * Revision 1.20  2007/06/26 08:39:27  lafrasse
 * Removed most TODOs by adding error handling through exceptions.
 *
 * Revision 1.19  2007/03/16 10:07:34  lafrasse
 * Added support for instanciation and execution from ASPRO.
 *
 * Revision 1.18  2007/02/14 13:51:17  lafrasse
 * Corrected path for resources.
 *
 * Revision 1.17  2007/02/13 15:34:39  lafrasse
 * Jalopyzation.
 *
 * Revision 1.16  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.15  2006/11/30 14:54:27  lafrasse
 * Added support for hidden legend view on startup.
 *
 * Revision 1.14  2006/11/23 16:20:39  lafrasse
 * Added different location (US, Fr, ...) handling for Double input (, vs .).
 *
 * Revision 1.13  2006/11/18 23:10:59  lafrasse
 * Handled StatusBar move.
 *
 * Revision 1.12  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.11  2006/11/08 22:25:00  lafrasse
 * Implemented filtering algorithm.
 *
 * Revision 1.10  2006/10/16 14:26:00  lafrasse
 * Added command line option parsing.
 * Added log level setting from command line option.
 *
 * Revision 1.9  2006/08/04 16:35:43  lafrasse
 * Added queryModel & filtersModel hooks in VirtualObservatory
 *
 * Revision 1.8  2006/07/28 08:30:43  mella
 * Use mcs generic Resources class
 *
 * Revision 1.7  2006/07/18 13:08:39  lafrasse
 * Jalopyzation
 *
 * Revision 1.6  2006/07/12 15:51:51  lafrasse
 * Corrected package import to resolve a comilation problem with
 * jmmc.mcs.util.StatusBar
 *
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
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.gui.*;

import java.util.*;
import java.util.logging.*;


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
public class SearchCalibrators extends App
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.scalib.sclgui.SearchCalibrators");

    /** Main application object used to perform the optionnal query received from ASPRO */
    public static VirtualObservatory _vo = null;

    /** Store the optionnal query received from ASPRO by constructor */
    private String _query = null;

    /**
     * Launch the SearchCalibrators application.
     *
     * Create all objects needed by SearchCalibrators and plug event responding
     * loop (Listener/Listenable, Observer/Observable) in.
     *
     * @param args command-line options.
     */
    public SearchCalibrators(String[] args)
    {
        super(args);
    }

    /**
     * Launch the SearchCalibrators application, plus automatically perform the given query if any.
     *
     * @param args command-line options.
     * @param query an XML formatted string containing an SCLSVR-like query.
     */
    public SearchCalibrators(String[] args, String query)
    {
        /* Launch application initialization with:
         *    - execution delayed for further initilization;
         *    - without splashscreen;
         *    - without protection against current process killing by its father.
         */
        super(args, false, false, false);

        // Store received query for later execution
        _query = query;

        // Perform received query if any
        run();
    }

    /** Initialize application objects */
    protected void init(String[] args)
    {
        // Set the default locale to custom locale (for Numerical Fields "." ",")
        Locale.setDefault(new Locale("en", "US"));

        // Set default resource
        fr.jmmc.mcs.util.Resources.setResourceName(
            "fr/jmmc/scalib/sclgui/Resources");

        try
        {
            // Get preferences
            Preferences preferences = Preferences.getInstance();

            // Create a query model
            QueryModel queryModel = new QueryModel();

            // Create filters
            FiltersModel filtersModel = new FiltersModel(queryModel);
            FiltersView  filtersView  = new FiltersView(filtersModel);

            // Create a calibrators model and attach it to a calibrators view
            CalibratorsModel calibratorsModel = new CalibratorsModel(filtersModel);
            CalibratorsView  calibratorsView  = new CalibratorsView(calibratorsModel);

            filtersModel.addObserver(calibratorsModel);

            // Link everything up
            _vo = new VirtualObservatory(queryModel, calibratorsModel,
                    filtersModel);

            // Attach the query model to its query view
            QueryView queryView = new QueryView(queryModel, _vo);

            // Retrieve application preferences and attach them to their view
            // (This instance must be instanciated after dependencies)
            PreferencesView preferencesView = new PreferencesView();
            StatusBar       statusBar       = new StatusBar();
            // Show the user the app is been initialized
            StatusBar.show("application initialization...");

            // Build the main window
            MainWindow window = new MainWindow(_vo, queryView, calibratorsView,
                    preferencesView, filtersView, statusBar);
            App.setFrame(window);

            // Triggers all preferences observers notification to finnish GUI setup.
            preferences.triggerObserversNotification();
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE, "Initialization error", ex);
            System.exit(1);
        }
    }

    /** Execute application body */
    protected void execute()
    {
        // If a query was received (when instanciated by ASPRO)
        if (_query != null)
        {
            // Launch the request
            _vo.executeQuery(_query);
        }
    }

    /** Handle operations before closing application */
    @Override
    protected boolean finish()
    {
        return _vo.canLostModifications();
    }

    /**
     * Main.
     */
    public static void main(String[] args)
    {
        // Start application
        SearchCalibrators searchCalibrators = new SearchCalibrators(args);
    }
}
/*___oOo___*/
