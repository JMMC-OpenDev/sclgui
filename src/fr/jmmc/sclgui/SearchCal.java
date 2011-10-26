/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.preference.PreferencesView;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import fr.jmmc.sclgui.calibrator.CalibratorsModel;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.query.QueryView;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.filter.FiltersView;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.StatusBar;
import fr.jmmc.jmcs.gui.SwingSettings;
import fr.jmmc.jmcs.gui.SwingUtils;
import java.util.logging.Logger;


/**
 * SearchCal application launcher
 * @author sylvain lafrasse
 */
public class SearchCal extends App {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(SearchCal.class.getName());
    /** Main application object used to perform the optional query received from ASPRO */
    public static VirtualObservatory _vo = null;
    /** Store the optional query received from ASPRO by constructor */
    private String _query = null;

    /**
     * Launch the SearchCal application.
     *
     * Create all objects needed by SearchCal and plug event responding
     * loop (Listener/Listenable, Observer/Observable) in.
     *
     * @param args command-line options.
     */
    public SearchCal(String[] args) {
        super(args);
    }

    /**
     * Launch the SearchCal application, plus automatically perform the given query if any.
     *
     * @param args command-line options.
     * @param query an XML formatted string containing an SCLSVR-like query.
     */
    public SearchCal(String[] args, String query) {
        /* Launch application initialization with:
         *    - execution delayed for further initilization;
         *    - without protection against current process killing by its father.
         */
        super(args, false, false);

        // Store received query for later execution
        _query = query;

        // Perform received query if any
        run();
    }

    /** Initialize application objects */
    @Override
    protected void init(String[] args) {      
        // Set default resource
        fr.jmmc.jmcs.util.Resources.setResourceName("fr/jmmc/sclgui/resource/Resources");

        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {

            /**
             * Initializes the swing components with their actions in EDT
             */
            @Override
            public void run() {

                // Get preferences
                Preferences preferences = Preferences.getInstance();

                // Create a query model
                QueryModel queryModel = new QueryModel();
                queryModel.init();

                // Create filters
                FiltersModel filtersModel = new FiltersModel(queryModel);
                FiltersView filtersView = new FiltersView(filtersModel);

                // Create a calibrators model and attach it to a calibrators view
                CalibratorsModel calibratorsModel = new CalibratorsModel(filtersModel);
                CalibratorsView calibratorsView = new CalibratorsView(calibratorsModel);
                calibratorsView.init();

                filtersModel.addObserver(calibratorsModel);

                // Link everything up
                _vo = new VirtualObservatory(queryModel, calibratorsModel);

                // Attach the query model to its query view
                QueryView queryView = new QueryView(queryModel, _vo);
                queryView.init();

                // Retrieve application preferences and attach them to their view
                // (This instance must be instanciated after dependencies)
                PreferencesView preferencesView = new PreferencesView();
                preferencesView.init();

                StatusBar statusBar = new StatusBar();
                // Show the user the app is been initialized
                StatusBar.show("application initialization...");

                // Build the main window
                MainWindow window = new MainWindow(_vo, queryView, calibratorsView,
                        preferencesView, filtersView, statusBar);
                App.setFrame(window);

                // Triggers all preferences observers notification to finnish GUI setup.
                preferences.triggerObserversNotification();
            }
        });
    }

    /** Execute application body */
    @Override
    protected void execute() {
        SwingUtils.invokeLaterEDT(new Runnable() {

            /**
             * Show the application frame using EDT
             */
            @Override
            public void run() {
                _logger.fine("SearchCal.ready : handler called.");

                getFrame().setVisible(true);

                // If a query was received (when instanciated by ASPRO)
                if (_query != null) {
                  // Launch the request
                  _vo.executeQuery(_query);
                }
            }
        });
    }

    /** Handle operations before closing application */
    @Override
    protected boolean finish() {
        return _vo.canLostModifications();
    }

    /**
     * Main entry point
     *
     * @param args command line arguments (open file ...)
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(final String[] args) {
        // init swing application for science
        SwingSettings.setup();        

        new SearchCal(args);
    }
}
/*___oOo___*/
