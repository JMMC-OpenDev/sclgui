/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: HttpMethodThreadMap.java,v 1.1 2011-04-01 10:02:25 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 */
package org.apache.axis.transport.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.logging.Log;

/**
 * This class holds the mapping thread (name) <=> HTTPMethod
 *
 * @author bourgesl
 */
public final class HttpMethodThreadMap {

  /** Field log */
  private static Log log = LogFactory.getLog(HttpMethodThreadMap.class.getName());
  /** thread / method mapping singleton */
  private static HttpMethodThreadMap methodThreadMap = new HttpMethodThreadMap();

  /**
   * Return the singleton instance
   * @return singleton instance
   */
  public static HttpMethodThreadMap get() {
    return methodThreadMap;
  }

  /* members */
  /** mapping thread <name => HTTPMethod (thread-safe) */
  private final Map<String, HttpMethodBase> methodMap = new ConcurrentHashMap<String, HttpMethodBase>();

  /**
   * Protected constructor
   */
  protected HttpMethodThreadMap() {
    super();
  }

  /**
   * Return the HttpMethodBase instance associated to the given thread name
   * @param threadName thread name
   * @return HttpMethodBase instance or null if undefined
   */
  public HttpMethodBase get(final String threadName) {
    final HttpMethodBase method = methodMap.get(threadName);
    if (log.isDebugEnabled()) {
      log.debug("HttpMethodThreadMap.getHttpMethod : " + threadName + " = " + method);
    }
    return method;
  }

  /**
   * Define the HttpMethodBase instance associated to the given thread name
   * @param threadName thread name
   * @param method HttpMethodBase instance
   */
  public void set(final String threadName, final HttpMethodBase method) {
    if (log.isDebugEnabled()) {
      log.debug("HttpMethodThreadMap.setHttpMethod : " + threadName + " = " + method);
    }
    methodMap.put(threadName, method);
  }

  /**
   * Remove the HttpMethodBase instance associated to the given thread name
   * @param threadName thread name
   * @return HttpMethodBase removed HttpMethodBase instance or null if not found
   */
  public HttpMethodBase remove(final String threadName) {
    if (log.isDebugEnabled()) {
      log.debug("HttpMethodThreadMap.remove : " + threadName);
    }
    return methodMap.remove(threadName);
  }
}
