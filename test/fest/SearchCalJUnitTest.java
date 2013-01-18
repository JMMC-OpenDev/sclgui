/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fest;

import fest.common.JmcsApplicationSetup;
import fest.common.JmcsFestSwingJUnitTestCase;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.Frame;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JList;
import org.fest.assertions.Fail;
import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.GenericTypeMatcher;
import static org.fest.swing.core.matcher.DialogMatcher.*;
import org.fest.swing.core.matcher.FrameMatcher;
import static org.fest.swing.core.matcher.JButtonMatcher.*;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.timing.Condition;
import static org.fest.swing.timing.Pause.*;
import org.fest.swing.timing.Timeout;
import org.ivoa.util.timer.TimerFactory;
import org.ivoa.util.timer.TimerFactory.UNIT;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This simple tests concerns SearchCal GUI
 *
 * @author Laurent BOURGES, Sylvain LAFRASSE. 
 */
public final class SearchCalJUnitTest extends JmcsFestSwingJUnitTestCase {

    /** absolute path to test folder to load observations */
    private final static String TEST_FOLDER = "/Users/lafrasse/Dev/sclgui/test/";
    /** 60s timeout */
    private static final Timeout LONG_TIMEOUT = Timeout.timeout(60 * 1000l);
    /** queries to perform (500) */
    private static final int QUERY_ITERATIONS = 1000;
    /** time to wait between queries (ms) */
    private static final long QUERY_PAUSE = 1 * 1000l;
    /** flag indicating to test cancel n queries */
    private static final boolean TEST_CANCEL = false;
    /** flag to perform big queries on V band */
    private static final boolean TEST_BIG = true;
    /** flag to perform queries on N band */
    private static final boolean TEST_BAND_N = false;

    /**
     * Define the application
     */
    static {
        // disable dev LAF menu :
        System.setProperty("jmcs.laf.menu", "false");

        // reset Preferences:
        Preferences.getInstance().resetToDefaultPreferences();

        final String fileName;
        if (TEST_BIG) {
            fileName = "SearchCal-HD_32617.scvot";
        } else if (TEST_BAND_N) {
            fileName = "SearchCal-BAND_N.scvot";
        } else {
            fileName = "SearchCal-ETA_TAU_fast.scvot";
        }

        JmcsApplicationSetup.define(
                fr.jmmc.sclgui.SearchCal.class,
                "-open", TEST_FOLDER + fileName);

        // define robot delays :
        defineRobotDelayBetweenEvents(SHORT_DELAY);

        // define delay before taking screenshot :
        defineScreenshotDelay(SHORT_DELAY);

        // disable tooltips :
        enableTooltips(false);

        // TimerFactory warmup and reset :
        TimerFactory.resetTimers();
    }

    /**
     * Test if the application started correctly
     */
    @Test
    @GUITest
    public void shouldStart() {
        window.requireVisible();

        saveScreenshot(window, "SearchCal-start.png");
    }

    /**
     * Test if the application can cancel N queries correctly
     */
    @Test
    @GUITest
    public void shouldNCancel() {
        window.requireVisible();

        if (TEST_CANCEL) {

            // initial delay:
            final long startAt = 0l;

            // typical query duration = 5s:
            final long queryDuration = 5000l;

            // 10 queries to consider that SearchCal server is stable within the wait delay:
            final int stepIteration = 10;

            final long stepWait = (queryDuration - startAt) / (QUERY_ITERATIONS / stepIteration);

            final String buttonText = "Get Calibrators";

            final JButtonFixture buttonFixture = window.button(withText(buttonText));

            int nStart = 0;
            int nCancel = 0;
            long delay = startAt;
            try {

                for (int i = 0; i < QUERY_ITERATIONS; i++) {

                    window.show();

                    if (buttonText.equals(buttonFixture.text())) {
                        if (logger.isLoggable(Level.INFO)) {
                            logger.info("START  query:          " + i);
                        }

                        // click to START SearchCal query:
                        buttonFixture.click();

                        // Start done:
                        nStart++;
                    }

                    if (i % stepIteration == 0) {
                        delay += stepWait;
                    }
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("pause before cancel (ms) : " + delay);
                    }
                    pause(delay);

                    // click to CANCEL SearchCal query:
                    if ("Cancel".equals(buttonFixture.text())) {
                        if (logger.isLoggable(Level.INFO)) {
                            logger.info("CANCEL query:          " + i);
                        }

                        buttonFixture.click();

                        // Cancel done:
                        nCancel++;
                    }

                    pauseShort();

                    if (closeMessage()) {
                        Fail.fail("An error occured while cancelling query [" + nStart + "] !");
                    }

                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("pause (ms) : " + QUERY_PAUSE);
                    }
                    pause(QUERY_PAUSE);
                }
            } catch (RuntimeException re) {
                logger.log(Level.SEVERE, "runtime failure : ", re);
                throw re;
            } catch (Error e) {
                logger.log(Level.SEVERE, "runtime failure : ", e);
                throw e;
            } finally {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Queries started/cancelled: " + nStart + " / " + nCancel);
                }
            }
        }
    }

    /**
     * Test if the application can make N queries correctly
     *
     * Timer [SearchCal (ms) - ms] [500] (threshold = 5000.0 ms) {
     *   Low  : Timer [SearchCal (ms) - ms] [494] {num = 494 : min = 1057.43562, avg = 3775.94762, max = 4905.56433, acc = 1865318.12799, std = 227.17308 [490] std low  = 246.11642 [139] std high = 222.81639 [351] }
     *   High : Timer [SearchCal (ms) - ms] [6] {num = 6 : min = 5130.75824, avg = 7191.16121, max = 10874.424, acc = 43146.96727, std = 0.0 [2] std low  = 0.0 [2] std high = 0.0 [0] }
     * }
     */
    @Test
    @GUITest
    public void shouldNQuery() {
        window.requireVisible();

        if (!TEST_CANCEL) {

            final String buttonText = "Get Calibrators";

            final JButtonFixture buttonFixture = window.button(withText(buttonText));

            long start;
            int nStart = 0;
            try {

                for (int i = 0; i < QUERY_ITERATIONS; i++) {

                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("start query : " + i);
                    }

                    start = System.nanoTime();

                    window.show();

                    // click to start SearchCal query:
                    buttonFixture.click();

                    // Start done:
                    nStart++;

                    pauseMedium();

                    if (closeMessage()) {
                        Fail.fail("An error occured while running query [" + nStart + "] !");
                    }

                    final JButton button = buttonFixture.component();

                    pause(new Condition("SearchCalQueryRunning") {
                        /**
                         * Checks if the condition has been satisfied.
                         * @return <code>true</code> if the condition has been satisfied, otherwise <code>false</code>.
                         */
                        public boolean test() {

                            return GuiActionRunner.execute(new GuiQuery<Boolean>() {
                                protected Boolean executeInEDT() {
                                    final String text = button.getText();
                                    final boolean done = buttonText.equals(text);

                                    if (logger.isLoggable(Level.FINE)) {
                                        logger.fine("SearchCalQueryRunning : text = " + text);
                                    }
                                    return done;
                                }
                            });

                        }
                    }, LONG_TIMEOUT);

                    TimerFactory.getTimer("SearchCal (ms)", UNIT.ms, 5000l).addMilliSeconds(start, System.nanoTime());

                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("pause (ms) : " + QUERY_PAUSE);
                    }

                    pause(QUERY_PAUSE);
                }
            } finally {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Queries started: " + nStart);
                }

                if (!TimerFactory.isEmpty()) {
                    logger.warning("TimerFactory : statistics : " + TimerFactory.dumpTimers());
                }
            }
        }
    }

    /**
     * Test Preferences
     */
    @Ignore
    @Test
    @GUITest
    public void shouldOpenPreferences() {
        window.menuItemWithPath("Edit", "Preferences...").click();

        final Frame prefFrame = robot().finder().find(FrameMatcher.withTitle("Preferences"));

        if (prefFrame != null) {
            final FrameFixture frame = new FrameFixture(robot(), prefFrame);

            frame.requireVisible();
            frame.moveToFront();

            saveScreenshot(frame, "SearchCal-prefs.png");

            // close frame :
            frame.close();
        }
    }

    /**
     * Test Interop menu : Start Aspro2 / topcat ... manually before this test
     */
    @Ignore
    @Test
    @GUITest
    public void showInteropMenu() {
        window.menuItemWithPath("Interop").click();
        saveScreenshot(window, "SearchCal-interop-menu.png");

        window.menuItemWithPath("Interop", "Show Hub Status").click();

        final Frame hubFrame = robot().finder().find(FrameMatcher.withTitle("SAMP Status"));

        if (hubFrame != null) {
            final FrameFixture frame = new FrameFixture(robot(), hubFrame);

            frame.requireVisible();
            frame.moveToFront();

            frame.list(new GenericTypeMatcher<JList>(JList.class) {
                @Override
                protected boolean isMatching(JList component) {
                    return "org.astrogrid.samp.gui.ClientListCellRenderer".equals(component.getCellRenderer().getClass().getName());
                }
            }).selectItem("SearchCal");

            saveScreenshot(frame, "SearchCal-interop-hubStatus.png");

            // close frame :
            frame.close();
        }
    }

    /**
     * Test Feedback report
     */
    @Ignore
    @Test
    @GUITest
    public void shouldOpenFeedbackReport() {

        // hack to solve focus trouble in menu items :
        window.menuItemWithPath("Help").focus();

        window.menuItemWithPath("Help", "Report Feedback to JMMC...").click();

        final DialogFixture dialog = window.dialog(withTitle("JMMC Feedback Report ").andShowing());

        dialog.requireVisible();
        dialog.moveToFront();

        final String myEmail = "laurent.bourges@obs.ujf-grenoble.fr";

        final JTextComponentFixture emailField = dialog.textBox(JTextComponentMatcher.withText(myEmail));

        // hide my email address :
        emailField.setText("type your email address here");

        saveScreenshot(dialog, "SearchCal-FeebackReport.png");

        // restore my preferences :
        emailField.setText(myEmail);

        // close dialog :
        dialog.close();
    }

    /**
     * Test the application exit sequence : ALWAYS THE LAST TEST
     */
    @Ignore
    @Test
    @GUITest
    public void shouldExit() {
        logger.severe("shouldExit test");

        window.close();

        confirmDialogDontSave();
    }

    /*
     --- Utility methods  ---------------------------------------------------------
     */
}
