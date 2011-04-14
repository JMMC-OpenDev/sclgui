/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalServerClient.java,v 1.3 2011-04-14 08:14:05 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2011/04/06 15:34:15  bourgesl
 * use Urls.parseURL to handle properly malformed URL exception
 *
 * Revision 1.1  2011/04/01 14:49:46  bourgesl
 * Axis client code (locator, stub) and its configuration in this class
 *
 */
package fr.jmmc.scalib.sclgui.ws;

import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.util.NetworkSettings;
import fr.jmmc.mcs.util.Preferences;
import fr.jmmc.mcs.util.Urls;
import fr.jmmc.sclws_wsdl.SclwsLocator;
import fr.jmmc.sclws_wsdl.SclwsPortType;
import fr.jmmc.sclws_wsdl.SclwsStub;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisProperties;
import org.apache.axis.components.net.DefaultCommonsHTTPClientProperties;
import org.apache.axis.configuration.EngineConfigurationFactoryDefault;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * This class provides configuration and access to the SearchCal Web Service using apache Axis 1.4
 * @author bourgesl
 */
public final class SearchCalServerClient {

  /** Logger */
  private static final Logger _logger = Logger.getLogger(SearchCalServerClient.class.getName());
  /** singleton */
  private static SearchCalServerClient _instance;

  /* members */
  /** shared service locator */
  private SclwsLocator _sclwsLocator = null;
  /** shared service URL */
  private URL _sclwsURL = null;
  /** http headers (no chunk encoding transfer to support proxies) */
  private Hashtable<String, String> _httpHeaders = null;

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

    if (proxyHost != null && proxyHost.length() > 0) {
      // # http.proxyHost
      AxisProperties.setProperty("http.proxyHost", proxyHost);

      // # http.proxyPort
      AxisProperties.setProperty("http.proxyPort", proxyPort);

      // TODO : support also advanced proxy settings (user, password ...)
      // # http.proxyUser
      // # http.proxyPassword
      // # http.nonProxyHosts
    }

    if (_logger.isLoggable(Level.INFO)) {
      _logger.info("Axis properties:\n" + Preferences.dumpProperties(AxisProperties.getProperties()));
    }

    // Note: default retryhandler = 3 retry.
  }

  /**
   * Return the SearchCal web service locator (lazily instanciated once)
   * @return SclwsLocator
   */
  private SclwsLocator getSclwsLocator() {
    if (_sclwsLocator == null) {

      final SclwsLocator locator = new SclwsLocator();

      // Decipher which proxy script to use according to app version status (release, beta or alpha)
      final String userName;
      if (App.isBetaVersion()) {
        userName = "betaswmgr"; // beta release homedir
//        userName = "sclws"; // official release homedir

      } else if (App.isAlphaVersion()) {
        userName = "lafrasse"; // alpha release homedir
      } else {
        userName = "sclws"; // official release homedir
      }

      // TODO : externalize SearchCal server URL:
      final String proxyScriptURL = "http://apps.jmmc.fr/~" + userName + "/sclws-proxy.php";

      if (_logger.isLoggable(Level.INFO)) {
        _logger.info("SearchCal server URL: " + proxyScriptURL);
      }

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
  public SclwsPortType getSclWsClient() {
    final SclwsLocator locator = getSclwsLocator();

    if (_sclwsURL == null) {
      _sclwsURL = Urls.parseURL(locator.getsclwsAddress());
    }

    try {
      // Create the web service client
      final SclwsPortType sclws = locator.getsclws(_sclwsURL);

      if (_logger.isLoggable(Level.FINE)) {
        _logger.fine("Connected to '" + locator.getsclwsAddress() + "'.");
      }

      // set the stub timeout to 0 (to inherit values from Httpclient parameters):
      final SclwsStub stub = (SclwsStub) sclws;
      stub.setTimeout(0);

      // disable HTTP 1.1 chunk encoding because some proxy servers does not accept it:
      if (_httpHeaders == null) {
        _httpHeaders = new Hashtable<String, String>();
        _httpHeaders.put(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED, "false");
      }
      stub._setProperty(HTTPConstants.REQUEST_HEADERS, _httpHeaders);

      return stub;

    } catch (ServiceException se) {
      throw new IllegalStateException("failed to create the web service client : ", se);
    }
  }
}
