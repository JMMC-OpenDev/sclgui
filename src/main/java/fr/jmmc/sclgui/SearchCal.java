/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.gui.PreferencesView;
import fr.jmmc.jmcs.gui.component.CommonPreferencesView;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.logging.LoggingService;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampMessageHandler;
import fr.jmmc.sclgui.calibrator.CalibratorsModel;
import fr.jmmc.sclgui.calibrator.CalibratorsView;
import fr.jmmc.sclgui.filter.FiltersModel;
import fr.jmmc.sclgui.filter.FiltersView;
import fr.jmmc.sclgui.preference.ColumnsPreferencesView;
import fr.jmmc.sclgui.preference.HelpPreferencesView;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.query.QueryModel;
import fr.jmmc.sclgui.query.QueryView;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchCal application launcher.
 * @author Sylvain LAFRASSE.
 */
public final class SearchCal extends App {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SearchCal.class.getName());
    /* members */
    /** Main application object used to perform the optional query received from ASPRO */
    /* note: do not set it to null as it is initialized in App.setupGui() by super() ie before class initialization (LBO) */
    private VirtualObservatory _vo = null;
    /** Store the optional query received from ASPRO by constructor */
    private String _query = null;
    private Preferences _preferences = null;

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

    @Override
    protected void initServices() {
        // Set default resource
        fr.jmmc.jmcs.util.PropertyUtils.setResourceName("fr/jmmc/sclgui/resource/Resources");

        // Get preferences
        _preferences = Preferences.getInstance();

        // Initialize server log:
        LoggingService.getInstance().addLogMapper("Server logs", CalibratorsModel.SCL_SERVER_LOG, "SERVERLOG");
    }

    /** Initialize application objects */
    @Override
    protected void setupGui() {

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
        final LinkedHashMap<String, JPanel> panels = new LinkedHashMap<String, JPanel>(8);
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
        panels.put("Misc. settings", new CommonPreferencesView());
        
        final PreferencesView preferencesView = new PreferencesView(_preferences, panels);
        preferencesView.init();

        // Show the user the app is been initialized
        StatusBar.show("application initialization...");

        // Build the main window
        App.setFrame(new MainWindow(_vo, queryView, calibratorsView, filtersView, StatusBar.getInstance()));
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
                _logger.debug("SearchCal.execute() handler called.");

                // Triggers all preferences observers notification to finish GUI setup.
                _preferences.triggerObserversNotification();

                // If a query was received (when instanciated by ASPRO)
                if (_query != null) {
                    // Launch the request
                    _vo.executeQuery(_query);
                }

                getFrame().setVisible(true);
            }
        });
    }

    /** Handle operations before closing application */
    @Override
    public boolean canBeTerminatedNow() {
        return _vo.canLostModifications();
    }

    @Override
    protected void cleanup() {
        _vo = null;
        _query = null;
        _preferences = null;
    }

    /**
     * Main entry point
     *
     * @param args command line arguments (open file ...)
     */
    public static void main(final String[] args) {
        Bootstrapper.launchApp(new SearchCal(args));
    }
}
/*___oOo___*/
