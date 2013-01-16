/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.PreferencesView;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampMessageHandler;
import fr.jmmc.sclgui.calibrator.CalibratorsModel;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.filter.FiltersView;
import fr.jmmc.sclgui.preference.ColumnsPreferencesView;
import fr.jmmc.sclgui.preference.HelpPreferencesView;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.query.QueryView;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import org.astrogrid.samp.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchCal application launcher
 * @author sylvain lafrasse
 */
public final class SearchCal extends App {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SearchCal.class.getName());
    /* members */
    /** Main application object used to perform the optional query received from ASPRO */
    /* note: do not set it to null as it is initialized in App.init() by super() ie before class initialization (LBO) */
    private VirtualObservatory _vo;
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
        super(args, true, false);

        // Store received query for later execution
        _query = query;

        // Perform received query if any
        run();
    }

    /** Initialize application objects */
    @Override
    protected void init() {
        // Set default resource
        fr.jmmc.jmcs.util.ResourceUtils.setResourceName("fr/jmmc/sclgui/resource/Resources");

        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            /**
             * Initializes the swing components with their actions in EDT
             */
            @Override
            public void run() {

                // Get preferences
                final Preferences preferences = Preferences.getInstance();

                // Create a query model
                final QueryModel queryModel = new QueryModel();
                queryModel.init();

                // Create filters
                final FiltersModel filtersModel = new FiltersModel(queryModel);
                final FiltersView filtersView = new FiltersView(filtersModel);

                // Create a calibrators model and attach it to a calibrators view
                final CalibratorsModel calibratorsModel = new CalibratorsModel(filtersModel, queryModel);

                final CalibratorsView calibratorsView = new CalibratorsView(calibratorsModel);

                filtersModel.addObserver(calibratorsModel);

                // Link everything up
                // note: _vo member is defined by App constructor !
                _vo = new VirtualObservatory(queryModel, calibratorsModel);

                // Attach the query model to its query view
                final QueryView queryView = new QueryView(queryModel, _vo);
                queryView.init();

                // Retrieve application preferences and attach them to their view
                // (This instance must be instanciated after dependencies)
                final LinkedHashMap<String, JPanel> panels = new LinkedHashMap<String, JPanel>();
                // Add the columns preferences pane
                final ColumnsPreferencesView columnsView = new ColumnsPreferencesView(Preferences.PREFIX_VIEW_COLUMNS);
                columnsView.init();
                panels.put("Columns Order", columnsView);
                // Add the catalog preferences pane
                final JPanel catalogView = new LegendView(true);
                panels.put("Legend Colors", catalogView);
                // Add the help preferences pane
                final HelpPreferencesView helpView = new HelpPreferencesView();
                helpView.init();
                panels.put("Help Settings", helpView);
                final PreferencesView preferencesView = new PreferencesView(preferences, panels);
                preferencesView.init();

                final StatusBar statusBar = new StatusBar();
                // Show the user the app is been initialized
                StatusBar.show("application initialization...");

                // Build the main window
                final MainWindow window = new MainWindow(_vo, queryView, calibratorsView, filtersView, statusBar);
                App.setFrame(window);

                // Triggers all preferences observers notification to finnish GUI setup.
                preferences.triggerObserversNotification();
            }
        });
    }

    /**
     * Create SAMP Message handlers
     */
    @Override
    protected void declareInteroperability() {

        // Add handler to load query params and launch calibrator search
        new SampMessageHandler(SampCapability.SEARCHCAL_START_QUERY) {
            /**
             * Implements message processing
             *
             * @param senderId public ID of sender client
             * @param message message with MType this handler is subscribed to
             * @throws SampException if any error occurred while message processing
             */
            @Override
            protected void processMessage(final String senderId, final Message message) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("\tReceived '{}' message from '{}' : '{}'.", this.handledMType(), senderId, message);
                }

                final String query = (String) message.getParam("query");
                if (query != null) {

                    SwingUtils.invokeLaterEDT(new Runnable() {
                        /**
                         * Synchronized by EDT
                         */
                        @Override
                        public void run() {
                            // bring this application to front :
                            App.showFrameToFront();

                            _vo.executeQuery(query);
                        }
                    });

                } else {
                    StatusBar.show("Could not start query from SAMP.");
                }
            }
        };

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
                _logger.debug("SearchCal.ready : handler called.");

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
