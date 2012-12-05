/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.vo;

import fr.jmmc.jmal.Catalog;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.util.MCSExceptionHandler;
import fr.jmmc.sclgui.vo.VirtualObservatory.QueryState;
import fr.jmmc.sclws_wsdl.SclwsPortType;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.axis.transport.http.AbortableCommonsHTTPSender;
import org.apache.axis.transport.http.HttpMethodThreadMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get calibrator list as a raw VOTable from JMMC web service.
 */
public abstract class AbstractGetCalAction extends RegisteredAction {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(AbstractGetCalAction.class.getName());
    /* members */
    /** http query thread */
    private GetCalThread _getCalThread = null;
    /** query count */
    private final AtomicInteger _queryCounter = new AtomicInteger(0);

    /**
     * Constructor
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     * @param actionName the name of the action.
     */
    protected AbstractGetCalAction(final String classPath, final String fieldName, final String actionName) {
        super(classPath, fieldName, actionName);

        setEnabled(false);
    }

    /**
     * Return whether the query has been launched or not.
     *
     * @return true if the query is already launched, false otherwise.
     */
    protected abstract boolean isQueryLaunched();

    /**
     * Return all current properties as an MCS-like sclsvr-formatted query.
     *
     * @return the query as a string.
     */
    protected abstract String getQueryAsMCSString();

    /**
     * Set whether the query has been launched or not.
     *
     * @param running true to enable all menus, false otherwise.
     */
    protected abstract void setQueryLaunchedState(final boolean running);

    /**
     * Define the query progression
     * @param catalogName current catalog name
     * @param catalogIndex current catalog index
     * @param nbOfCatalogs total number of catalogs
     */
    protected abstract void setQueryProgress(final String catalogName, final int catalogIndex, final int nbOfCatalogs);

    /**
     * Parse the received VOTable
     * @param votable VOTable
     */
    protected abstract void parseResults(final String votable);

    /**
     * Display the given message to the user (status bar)
     * @param message message to show
     */
    protected abstract void showStatus(final String message);

    /**
     * Show an error with the given message plus the exception message (if any) using EDT if needed
     * and log the exception
     * @param message message to display
     * @param th exception to use
     */
    protected abstract void showErrorMessage(final String message, final Throwable th);

    /**
     * Set whether the query has been launched or not AND reset the GetCalThread field first
     *
     * @param running true to enable all menus, false otherwise.
     */
    private void defineQueryLaunchedState(final boolean running) {
        if (!running && _getCalThread != null) {
            // reset the http query thread used for cancellation:
            _getCalThread = null;
        }
        setQueryLaunchedState(running);
    }

    /**
     * Perform action
     * @param ae unused
     */
    @Override
    public final void actionPerformed(final ActionEvent ae) {
        // Launch a new thread only if no other one has been launched yet
        if (!isQueryLaunched()) {
            // Query is starting
            defineQueryLaunchedState(true);

            showStatus("searching calibrators... (please wait, this may take a while)");

            // Get the query from the GUI
            final String query = getQueryAsMCSString();

            _logger.info("Query = '{}'.", query);

            // Launch the query in the background in order to keed GUI updated
            _getCalThread = new GetCalThread(_queryCounter.incrementAndGet(), query);

            // define UncaughtExceptionHandler :
            MCSExceptionHandler.installThreadHandler(_getCalThread);

            _getCalThread.start();

        } else {

            showStatus("cancelling current calibrator search...");

            // If the GetCal thread has already been launched
            if (_getCalThread != null) {
                // Kill it
                _logger.debug("Killing GetCal thread ... ");

                // indicate that the user cancelled the query:
                _getCalThread.cancel(true);
                _getCalThread = null;

                _logger.debug("GetCal thread killed.");
            }

            // Query is finished
            defineQueryLaunchedState(false);

            showStatus("calibrator search cancelled.");
        }
    }

    /**
     * Return a thread name
     * @param name name prefix
     * @param number number to identify uniquely the thread
     * @return thread name : prefix + '-' + thread number
     */
    private static String getThreadName(final String name, final int number) {
        final StringBuilder sb = new StringBuilder(name).append('-');
        sb.append(number);
        return sb.toString();
    }

    /**
     * Wait for each CDS interrogation progress while another thread waits for the final response.
     */
    private final class GetCalThread extends Thread {

        /** query number */
        private final int _queryNumber;
        /** QuerySearchCal WebService client */
        private final SclwsPortType _sclws;
        /** QuerySearchCal query */
        private final String _query;
        /** QuerySearchCal Session ID */
        private String _id;
        /** flag to indicate that cancel is done */
        /* atomic stop flag used to indicate that cancel is done and coordinate state between threads */
        private final AtomicBoolean _cancel = new AtomicBoolean();
        /** flag to indicate an user cancellation */
        private boolean _userCancel = false;
        /** QuerySearchCal query thread */
        private QueryResultThread _queryResultThread = null;

        /**
         * Create a new QuerySearchCal Session thread (get Id, get statuses and waits for the final response)
         * @param queryNumber query number
         * @param query QuerySearchCal query
         */
        private GetCalThread(final int queryNumber, final String query) {
            super(getThreadName("GetCalThread", queryNumber));
            _queryNumber = queryNumber;
            _query = query;

            // Get one WS client:
            _sclws = SearchCalServerClient.getInstance().getSclWsClient();
        }

        @Override
        public void run() {
            // current state used to report proper message if an exception is caught:
            QueryState currentState = QueryState.OpenSession;
            try {

                // Check if the query was cancelled:
                if (isCancelled()) {
                    return;
                }

                // 1 - Get the connection ID :
                // this WS call can block if connection attempt fails :
                _id = _sclws.getCalOpenSession();

                if (_logger.isDebugEnabled()) {
                    _logger.debug("JMMC Connection ID = '{}'.", _id);
                }

                showStatus("searching calibrators... (connection established)");

                // 2 - Launch the querying thread
                _queryResultThread = new QueryResultThread(_queryNumber, _sclws, _id, _query);

                // define UncaughtExceptionHandler :
                MCSExceptionHandler.installThreadHandler(_queryResultThread);

                // Check if the query was cancelled:
                if (isCancelled()) {
                    return;
                }

                _queryResultThread.start();

                showStatus("searching calibrators... (querying catalogs)");

                // change the current state to QueryStatus:
                currentState = QueryState.QueryStatus;

                // 3 - GetCal status polling to update ProgressBar
                int requestStatus;
                do {

                    // Check if the query was cancelled:
                    if (isCancelled()) {
                        return;
                    }

                    // Get query progression status
                    // this WS call can block if connection attempt fails :
                    final String currentStatus = _sclws.getCalQueryStatus(_id);

                    // Deserialize the new status to update the GUI
                    final String[] splittedStatus = currentStatus.split("\t");

                    // Parse the received status
                    int i = 0;
                    requestStatus = Integer.parseInt(splittedStatus[i++]);

                    final String currentCatalogName;
                    final Integer catalogIndex;
                    final Integer nbOfCatalogs;

                    if (splittedStatus.length == 4) {
                        // Get the catalog name
                        currentCatalogName = Catalog.titleFromReference(splittedStatus[i++]);

                        // Get the catalog index
                        catalogIndex = Integer.valueOf(splittedStatus[i++]);

                        // Get the total number of catalogs
                        nbOfCatalogs = Integer.valueOf(splittedStatus[i++]);

                        // Compose the dispalyed query status
                        final String composedQueryStatus = currentCatalogName
                                + " - (" + catalogIndex + "/" + nbOfCatalogs
                                + ")";

                        // Update the query progress:
                        setQueryProgress(composedQueryStatus, catalogIndex.intValue(), nbOfCatalogs.intValue());

                    } else {
                        currentCatalogName = "";
                        catalogIndex = Integer.valueOf(0);
                        nbOfCatalogs = Integer.valueOf(0);
                    }

                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Status = '{}' - {}/{} (status = '{}').", currentCatalogName, catalogIndex, nbOfCatalogs, requestStatus);
                    }

                } while (requestStatus == 1);

                showStatus("searching calibrators... (waiting for result)");

                // change the current state to QueryResult:
                currentState = QueryState.QueryResult;

                // Check if the query was cancelled:
                if (isCancelled()) {
                    return;
                }

                // 4 - Wait for the query thread to finish:
                _queryResultThread.join();

                // Check if the query was cancelled:
                if (isCancelled()) {
                    return;
                }

                // 5 - get results and parse them :
                final String result = _queryResultThread.getResult();

                // Parse the received VOTable
                parseResults(result);

            } catch (NumberFormatException nfe) {
                // can occur when parsing status query (2)
                handleException(nfe, currentState);
            } catch (InterruptedException ie) {
                // can occur when thie thread wait for the query thread to finish (4)
                handleException(ie, currentState);
            } catch (RemoteException re) {
                // try to get result in case of GetCalStatus failure (Invalid server id):
                boolean hasResult = false;
                if (_queryResultThread != null) {

                    // Check if the query was cancelled:
                    if (!isCancelled()) {
                        final String result = _queryResultThread.getResult();

                        // check if null ie query terminated properly (no join):
                        if (result != null) {
                            _logger.debug("Silenced error (invalid server id): {}", _id, re);

                            hasResult = true;

                            // Parse the received VOTable
                            parseResults(result);
                        }
                    }
                }

                if (!hasResult) {
                    // server or http failure
                    handleException(re, currentState);
                }
            } finally {
                _logger.info("{} thread.run done.", getName());

                // ensure clean up (PostMethod object) in case releaseConnection() did not already do it 
                // in AbortableCommonsHTTPSender#relaseConnection() :
                HttpMethodThreadMap.get().remove(getName());

                // Query is finished
                defineQueryLaunchedState(false);
            }
        }

        /**
         * Handle exceptions coming from threads communicating with the SearchCal Server
         * @param e exception
         * @param state current state used to find user messages
         */
        private void handleException(final Exception e, final QueryState state) {
            // Handle error when no manual cancel
            if (isUserCancel() || isCancelled()) {
                // already handled:
                _logger.debug("Silenced error (cancellation) : ", e);

                return;
            }

            // cancel queries now to change cancel barrier:
            cancel(false);

            final String status;
            final String message;
            switch (state) {
                default:
                case OpenSession:
                    status = "calibrator search aborted (connection refused) !";
                    message = "Could not connect to JMMC server.";
                    break;

                case QueryStatus:
                    status = "calibrator search aborted (catalog error) !";
                    message = "Communication with the JMMC server failed.";
                    break;

                case QuerySearchCal:
                    status = "calibrator search aborted (could not send query) !";
                    message = "Could not send query to JMMC server.";
                    break;

                case QueryResult:
                    status = "calibrator search aborted (could not get result) !";
                    message = "Could not get result from JMMC server.";
            }

            // Use messages corresponding to state:
            showStatus(status);

            // Report Exception:
            showErrorMessage(message, e);

            // change the GUI state:
            defineQueryLaunchedState(false);
        }

        /**
         * Cancel current requests
         * @param userCancel flag to indicate if the user canceled requests
         */
        private void cancel(final boolean userCancel) {
            /* avoid multiple cancel calls and indicate to threads to halt at the next
             * opportunity. */
            if (_cancel.compareAndSet(false, true)) {
                _userCancel = userCancel;

                if (_logger.isDebugEnabled()) {
                    _logger.debug("cancel: {}", userCancel);
                }

                // First Cancel the request:
                // Disabled cancel query because it crashes the SearchCal Server !
                /*
                 if (_id != null) {
                 // Launch the cancel query thread to free resources on the server side:
                 final CancelQueryThread cancelThread = new CancelQueryThread(_queryNumber, _sclws, _id);
                
                 // define UncaughtExceptionHandler :
                 MCSExceptionHandler.installThreadHandler(cancelThread);
                
                 cancelThread.start();
                 }
                 */

                // If the QueryResult thread has already been launched
                if (_queryResultThread != null) {
                    // interrupt it:

                    _logger.debug("Killing QueryResult thread ... ");

                    // interrupt the QueryResult thread:

                    /* Interrupt the blocking thread.  This won't break out of a blocking
                     * I/O request, but will break out of a wait or sleep call.  While in
                     * this case we know that no such condition is possible, it is always a
                     * good idea to include an interrupt to avoid assumptions about the
                     * thread in question. */
                    _queryResultThread.interrupt();

                    // Close HTTP connection:
                    AbortableCommonsHTTPSender.abort(_queryResultThread);

                    _logger.debug("QueryResult thread killed.");
                }

                // interrupt this thread:

                /* Interrupt the blocking thread.  This won't break out of a blocking
                 * I/O request, but will break out of a wait or sleep call.  While in
                 * this case we know that no such condition is possible, it is always a
                 * good idea to include an interrupt to avoid assumptions about the
                 * thread in question. */
                this.interrupt();

                // Close HTTP connection:
                AbortableCommonsHTTPSender.abort(this);

                _logger.debug("cancel done.");
            }
        }

        /**
         * Return true if the user cancelled the query
         * @return true if the user cancelled the query
         */
        private boolean isUserCancel() {
            return _userCancel;
        }

        /**
         * Return true if the query is cancelled
         * @return true if the query is cancelled
         */
        private boolean isCancelled() {
            return _cancel.get();
        }

        /**
         * Wait for the final reply from the server while another thread monitor CDS interrogation progress.
         */
        private final class QueryResultThread extends Thread {

            /** QuerySearchCal WebService client */
            private final SclwsPortType _sclws;
            /** QuerySearchCal Session ID */
            private final String _id;
            /** QuerySearchCal query */
            private final String _query;
            /** QuerySearchCal server response (VOTable) */
            private volatile String _result = null;

            /**
             * Creates a new Thread to send query to the server
             * @param queryNumber query number
             * @param sclws QuerySearchCal WebService client
             * @param id QuerySearchCal Session ID
             * @param query QuerySearchCal query
             */
            private QueryResultThread(final int queryNumber, final SclwsPortType sclws, final String id, final String query) {
                super(getThreadName("QueryResultThread", queryNumber));
                _sclws = sclws;
                _query = query;
                _id = id;
            }

            /**
             * Runs the query i.e. calls getCalSearchCal(_id, _query)
             */
            @Override
            public void run() {
                try {
                    showStatus("searching calibrators... (sending query)");

                    // Check if the query was cancelled:
                    if (isCancelled()) {
                        return;
                    }

                    // Launch the query
                    final long start = System.nanoTime();

                    // this WS call can block if connection attempt fails :
                    _result = _sclws.getCalSearchCal(_id, _query);

                    _logger.info("{} duration = {} s.", getName(), (1e-9d * (System.nanoTime() - start)));

                    showStatus("searching calibrators... (result received)");

                } catch (RemoteException re) {
                    // server or http failure
                    handleException(re, QueryState.QuerySearchCal);
                } finally {
                    _logger.info("{} thread.run done.", getName());

                    // ensure clean up (PostMethod object) in case releaseConnection() did not already do it 
                    // in AbortableCommonsHTTPSender#relaseConnection() :
                    HttpMethodThreadMap.get().remove(getName());
                }
            }

            /**
             * Return the query result
             * @return result or null if interrupted or canceled
             */
            private String getResult() {
                return _result;
            }
        }

        /**
         * Send cancel message to the server asynchronously
         *
         * UNUSED until server handles properly getCalCancelSession() i.e. stable (no more crash)
         */
        private final class CancelQueryThread extends Thread {

            /** QuerySearchCal WebService client */
            private final SclwsPortType _sclws;
            /** QuerySearchCal Session ID */
            private final String _id;

            /**
             * Creates a new Thread to send cancel message to the server asynchronously
             * @param queryNumber query number
             * @param sclws QuerySearchCal WebService client
             * @param id QuerySearchCal Session ID
             */
            private CancelQueryThread(final int queryNumber, final SclwsPortType sclws, final String id) {
                super(getThreadName("CancelQueryThread", queryNumber));
                _sclws = sclws;
                _id = id;
            }

            /**
             * Send the cancel query i.e. calls getCalCancelSession(id)
             */
            @Override
            public void run() {
                _logger.info("{} cancel session with id = {}", getName(), _id);
                try {

                    // Ask for query cancellation:
                    // 1- free resources on the server side
                    // 2- remove id from active identifiers
                    // note: next calls using this id will fail.

                    // this WS call can block if connection attempt fails :
                    _sclws.getCalCancelSession(_id);

                } catch (RemoteException re) {
                    // do not report this failure to the user, only log the exception:
                    _logger.warn("cancel request failed : ", re);

                } finally {
                    _logger.info("{} thread.run done.", getName());

                    // ensure clean up (PostMethod object) in case releaseConnection() did not already do it 
                    // in AbortableCommonsHTTPSender#relaseConnection() :
                    HttpMethodThreadMap.get().remove(getName());
                }
            }
        }
    }
}
