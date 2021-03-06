/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.vo;

import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.network.NetworkSettings;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.sclgui.preference.PreferenceKey;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclws_wsdl.SclwsLocator;
import fr.jmmc.sclws_wsdl.SclwsPortType;
import fr.jmmc.sclws_wsdl.SclwsStub;
import java.util.Hashtable;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.Call;
import org.apache.axis.components.net.DefaultCommonsHTTPClientProperties;
import org.apache.axis.configuration.EngineConfigurationFactoryDefault;
import org.apache.axis.transport.http.HTTPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides configuration and access to the SearchCal Web Service using apache Axis 1.4
 * @author bourgesl
 */
public final class SearchCalServerClient {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SearchCalServerClient.class.getName());
    /** singleton */
    private static SearchCalServerClient _instance;
    // Constants
    /** JMMC application server to detect DNS */
    private final static String JMMC_APPS_SERVER_HOST = "apps.jmmc.fr";
    /** JMMC application server IP (hard-coded).
    was 152.77.114.210 until 27 april 2017,
    on dec 2018 also responds on the beta frontend 129.88.192.8,    
    on july 21, 2020 old apps server leaved 152.77.135.209 and set as backend 
    behind main frontend 129.88.192.7 */
    private final static String JMMC_APPS_SERVER_IP = "129.88.192.7";

    /** Official release path */
    private static final String OFFICIAL_SERVER_PATH = "/~sclws/";
    /** Beta release path */
    private static final String BETA_SERVER_PATH = "/~betaswmgr/";
    /** Development release path */
    private static final String ALPHA_SERVER_PATH = "/~bourgesl/";
    /** Server proxy script */
    private static final String DEFAULT_PROXY_SCRIPT = "sclwsProxy.php";

    /* members */
    /** shared service locator */
    private SclwsLocator _sclwsLocator = null;
    /** HTTP headers (no chunk encoding transfer to support proxies) */
    private final Hashtable<String, String> _httpHeaders;

    /**
     * This code returns the singleton instance.
     * @return SearchCalServerClient
     */
    public static synchronized SearchCalServerClient getInstance() {
        if (_instance == null) {
            _instance = new SearchCalServerClient();
        }
        return _instance;
    }

    /**
     * Public constructor
     */
    private SearchCalServerClient() {
        super();
        configureAxis();

        // disable HTTP 1.1 chunk encoding because some proxy servers does not accept it:
        _httpHeaders = new Hashtable<String, String>(4);
        _httpHeaders.put(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED, "false");
    }

    /**
     * Configure Axis library (SOAP client)
     */
    private void configureAxis() {
        // Define custom config to use AbortableCommonsHTTPSender (and SOAP logs in dev):
        AxisProperties.setProperty(EngineConfigurationFactoryDefault.OPTION_CLIENT_CONFIG_FILE, "sclgui-client-config.wsdd");

        // Total maximum connections:
        AxisProperties.setProperty(DefaultCommonsHTTPClientProperties.MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY,
                Integer.toString(NetworkSettings.DEFAULT_MAX_TOTAL_CONNECTIONS));

        // Total maximum connections per host:
        AxisProperties.setProperty(DefaultCommonsHTTPClientProperties.MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY,
                Integer.toString(NetworkSettings.DEFAULT_MAX_HOST_CONNECTIONS));

        // Timeout to get a connection from the pool in millis
        AxisProperties.setProperty(DefaultCommonsHTTPClientProperties.CONNECTION_POOL_TIMEOUT_KEY,
                Integer.toString(NetworkSettings.DEFAULT_CONNECT_TIMEOUT));

        // Timeout to establish connection in millis
        AxisProperties.setProperty(DefaultCommonsHTTPClientProperties.CONNECTION_DEFAULT_CONNECTION_TIMEOUT_KEY,
                Integer.toString(NetworkSettings.DEFAULT_CONNECT_TIMEOUT));

        // Timeout "waiting for data" (read timeout) in milliseconds:
        AxisProperties.setProperty(DefaultCommonsHTTPClientProperties.CONNECTION_DEFAULT_SO_TIMEOUT_KEY,
                // 30 minutes to get SearchCal response from our server:
                // Note: UJF proxy server automatically closes the connection after 5 minutes !!!
                Integer.toString(30 * 60 * 1000));

        // Get proxy settings defined by NetworkSettings:
        final String proxyHost = System.getProperty(NetworkSettings.PROPERTY_HTTP_PROXY_HOST);
        final String proxyPort = System.getProperty(NetworkSettings.PROPERTY_HTTP_PROXY_PORT);

        if (proxyHost != null && proxyHost.length() != 0) {
            // # http.proxyHost
            AxisProperties.setProperty("http.proxyHost", proxyHost);

            // # http.proxyPort
            AxisProperties.setProperty("http.proxyPort", proxyPort);

            // TODO : support also advanced proxy settings (user, password ...)
            // # http.proxyUser
            // # http.proxyPassword
            // # http.nonProxyHosts
        }

        _logger.info("Axis properties:\n{}", Preferences.dumpProperties(AxisProperties.getProperties()));

        // Note: default retryhandler = 0 retry
    }

    /**
     * Return the SearchCal web service locator (lazily instanced once)
     * @return SclwsLocator
     */
    private SclwsLocator getSclwsLocator() {
        if (_sclwsLocator == null) {
            // Retrieve preferred SearchCal server URL (if any)
            String proxyScriptURL = Preferences.getInstance().getPreference(PreferenceKey.SERVER_URL_ADDRESS);

            if (StringUtils.isTrimmedEmpty(proxyScriptURL)) {
                // Use JMMC server IP (ESO constraint):
                final String serverIP = NetworkSettings.getHostIP(JMMC_APPS_SERVER_HOST, JMMC_APPS_SERVER_IP);

                // If none found
                // Decipher which proxy script to use according to app version status (release, beta or alpha)
                String serverPath = OFFICIAL_SERVER_PATH;
                if (ApplicationDescription.isBetaVersion()) {
                    serverPath = BETA_SERVER_PATH;
                } else if (ApplicationDescription.isAlphaVersion()) {
                    serverPath = ALPHA_SERVER_PATH;
                }
                proxyScriptURL = "http://" + serverIP + serverPath + DEFAULT_PROXY_SCRIPT;
            }

            _logger.info("SearchCal server URL: {}", proxyScriptURL);

            final SclwsLocator locator = new SclwsLocator();

            // Re-route network traffic at specified address on standard 80 port
            // (e.g to overcome tightly filtered TCP outputs on public WiFi)
            locator.setsclwsEndpointAddress(proxyScriptURL);

            // do not maintain session between calls :
            locator.setMaintainSession(false);

            // define shared locator :
            _sclwsLocator = locator;
        }

        return _sclwsLocator;
    }

    /**
     * Return a new SearchCal web service client
     * @return SclwsPortType
     */
    public SclwsPortType getSclwsClient() {
        final SclwsLocator locator = getSclwsLocator();

        try {
            // Create the web service client
            final SclwsPortType sclws = locator.getsclws();

            if (_logger.isDebugEnabled()) {
                _logger.debug("Connected to '{}'.", locator.getsclwsAddress());
            }

            // set the stub timeout to 0 (to inherit values from Httpclient parameters):
            final SclwsStub stub = (SclwsStub) sclws;
            stub.setTimeout(0);

            // disable HTTP 1.1 chunk encoding because some proxy servers does not accept it:
            stub._setProperty(HTTPConstants.REQUEST_HEADERS, _httpHeaders);

            // enable gzip compression from client-side:
            stub._setProperty(HTTPConstants.MC_ACCEPT_GZIP, "true");

            // enable streaming for SAX performance issues:
            stub._setProperty(Call.STREAMING_PROPERTY, Boolean.TRUE);

            return stub;

        } catch (ServiceException se) {
            throw new IllegalStateException("failed to create the web service client : ", se);
        }
    }
}
