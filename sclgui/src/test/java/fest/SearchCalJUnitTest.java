/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fest;

import fest.common.JmcsFestSwingJUnitTestCase;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.sclgui.preference.Preferences;
import javax.swing.JButton;
import org.fest.assertions.Fail;
import org.fest.swing.annotation.GUITest;
import static org.fest.swing.core.matcher.JButtonMatcher.*;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.timing.Condition;
import static org.fest.swing.timing.Pause.*;
import org.fest.swing.timing.Timeout;
import fr.jmmc.jmcs.util.timer.TimerFactory;
import fr.jmmc.jmcs.util.timer.TimerFactory.UNIT;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This simple tests concerns SearchCal GUI
 *
 * @author Laurent BOURGES, Sylvain LAFRASSE. 
 */
public final class SearchCalJUnitTest extends JmcsFestSwingJUnitTestCase {

    /** 60s timeout */
    private static final Timeout LONG_TIMEOUT = Timeout.timeout(60 * 1000l);
    /** queries to perform (500) */
    private static final int QUERY_ITERATIONS = 1;
    /** time to wait between queries (ms) */
    private static final long QUERY_PAUSE = 1 * 1000l;
    /** flag indicating to test cancel n queries */
    private static final boolean TEST_CANCEL = false;
    /** flag to perform big queries on V band */
    private static final boolean TEST_BIG = true;
    /** flag to perform queries on N band */
    private static final boolean TEST_BAND_N = false;

    /**
     * Initialize system properties & static variables and finally starts the application
     */
    @BeforeClass
    public static void intializeAndStartApplication() {

        // invoke Bootstrapper method to initialize logback now:
        Bootstrapper.getState();

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

        // define robot delays :
        defineRobotDelayBetweenEvents(SHORT_DELAY);

        // define delay before taking screenshot :
        defineScreenshotDelay(SHORT_DELAY);

        // disable tooltips :
        enableTooltips(false);

        // TimerFactory warmup and reset :
        TimerFactory.resetTimers();

        // Start application:
        JmcsFestSwingJUnitTestCase.startApplication(
                fr.jmmc.sclgui.SearchCal.class,
                "-open", TEST_FOLDER + fileName);
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

            // typical query duration = 0.5s:
            final long queryDuration = 500l;

            // 10 queries to consider that SearchCal server is stable within the wait delay:
            final int stepIteration = 10;

            final long stepWait = (queryDuration - startAt) / (QUERY_ITERATIONS / stepIteration);

            final String buttonText = VirtualObservatory.LABEL_GET_CAL;

            final JButtonFixture buttonFixture = window.button(withText(buttonText));

            int nStart = 0;
            int nCancel = 0;
            long delay = startAt;
            try {

                for (int i = 0; i < QUERY_ITERATIONS; i++) {

                    window.show();

                    if (buttonText.equals(buttonFixture.text())) {
                        logger.info("START  query:          " + i);

                        // click to START SearchCal query:
                        buttonFixture.click();

                        // Start done:
                        nStart++;
                    }

                    if (i % stepIteration == 0) {
                        delay += stepWait;
                    }
                    logger.info("pause before cancel (ms) : " + delay);
                    pause(delay);

                    // click to CANCEL SearchCal query:
                    if ("Cancel".equals(buttonFixture.text())) {
                        logger.info("CANCEL query:          " + i);

                        buttonFixture.click();

                        // Cancel done:
                        nCancel++;
                    }

                    pauseShort();

                    if (closeMessage()) {
                        Fail.fail("An error occured while cancelling query [" + nStart + "] !");
                    }

                    logger.info("pause (ms) : " + QUERY_PAUSE);
                    pause(QUERY_PAUSE);
                }
            } catch (RuntimeException re) {
                logger.error("runtime failure : ", re);
                throw re;
            } catch (Error e) {
                logger.error("runtime failure : ", e);
                throw e;
            } finally {
                logger.info("Queries started/cancelled: " + nStart + " / " + nCancel);
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

            final String buttonText = VirtualObservatory.LABEL_GET_CAL;

            final JButtonFixture buttonFixture = window.button(withText(buttonText));

            long start;
            int nStart = 0;
            try {

                for (int i = 0; i < QUERY_ITERATIONS; i++) {

                    logger.info("start query : " + i);

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

                                    logger.debug("SearchCalQueryRunning : text = {}", text);
                                    return done;
                                }
                            });

                        }
                    }, LONG_TIMEOUT);

                    TimerFactory.getTimer("SearchCal (ms)", UNIT.ms, 5000l).addMilliSeconds(start, System.nanoTime());

                    logger.info("pause (ms) : " + QUERY_PAUSE);

                    pause(QUERY_PAUSE);
                }
            } finally {
                logger.info("Queries started: " + nStart);

                if (!TimerFactory.isEmpty()) {
                    logger.info("TimerFactory : statistics : " + TimerFactory.dumpTimers());
                }
            }
        }
    }

    /**
     * Test the application exit sequence : ALWAYS THE LAST TEST
     */
    @Ignore
    @Test
    @GUITest
    public void shouldExit() {
        logger.info("shouldExit test");

        window.close();

        confirmDialogDontSave();
    }

    /*
     --- Utility methods  ---------------------------------------------------------
     */
}
