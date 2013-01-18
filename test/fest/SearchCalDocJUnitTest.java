/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fest;

import fest.common.JmcsApplicationSetup;
import fest.common.JmcsFestSwingJUnitTestCase;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.sclgui.preference.Preferences;
import java.awt.Frame;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import org.fest.assertions.Fail;
import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.matcher.FrameMatcher;
import static org.fest.swing.core.matcher.JButtonMatcher.*;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JProgressBarFixture;
import org.fest.swing.timing.Timeout;
import org.ivoa.util.timer.TimerFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This simple tests concerns SearchCal GUI
 *
 * Note: emergency abort listener associated to 'Ctrl + Shift + A' key combination
 *
 * @author Laurent BOURGES, Sylvain LAFRASSE. 
 */
public final class SearchCalDocJUnitTest extends JmcsFestSwingJUnitTestCase {

    /** absolute path to test folder to load observations */
    private final static String TEST_FOLDER = "/Users/lafrasse/Dev/sclgui/test/";
    /** 60s timeout */
    private static final Timeout LONG_TIMEOUT = Timeout.timeout(120 * 1000l);

    /**
     * Define the application
     */
    static {
        // disable dev LAF menu :
        System.setProperty("jmcs.laf.menu", "false");

        // reset Preferences:
        Preferences.getInstance().resetToDefaultPreferences();
        try {
            CommonPreferences.getInstance().setPreference(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN, false);
        } catch (PreferencesException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        JmcsApplicationSetup.define(
                fr.jmmc.sclgui.SearchCal.class,
                "-open", TEST_FOLDER + "SearchCal-ETA_TAU.scvot");

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
     * Test if the application query correctly
     */
    @Test
    @GUITest
    public void shouldQuery() {
        window.requireVisible();

        // TODO: test Simbad resolver

        // Mac OS X workaround to perform action in menu:
        final JMenuItemFixture menuItemWithPath = window.menuItemWithPath("Calibrators", "Show Legend");
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() {
                menuItemWithPath.target.doClick();
            }
        });

        // TODO: create constants ...
        final String buttonText = "Get Calibrators";
        final JButtonFixture buttonFixture = window.button(withText(buttonText));
        final JProgressBarFixture progressFixture = window.progressBar();

        // click to start SearchCal query:
        buttonFixture.click();
        pauseMedium();
        if (closeMessage()) {
            Fail.fail("An error occured while running query !");
        }

        // 2MASS catalog (4/14)
        progressFixture.waitUntilValueIs(4, LONG_TIMEOUT);

        saveScreenshot(window, "sc_ihm.png");

        // click to cancel SearchCal query:
        buttonFixture.click();
        pauseMedium();
        if (closeMessage()) {
            Fail.fail("An error occured while running query !");
        }
    }

    /**
     * Test Preferences
     */
    @Test
    @GUITest
    public void shouldOpenPreferences() {

        // TODO : generalize this over menu click method (cross-platform issue)
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() {
                final AbstractAction preferenceAction = ActionRegistrar.getInstance().getPreferenceAction();
                preferenceAction.actionPerformed(null);
            }
        });

        final Frame prefFrame = robot().finder().find(FrameMatcher.withTitle("Preferences"));
        if (prefFrame != null) {
            final FrameFixture frame = new FrameFixture(robot(), prefFrame);

            frame.requireVisible();
            frame.moveToFront();

            // Get the first tab screenshot
            frame.tabbedPane().selectTab("Columns Order");
            frame.list().selectItem(4);
            saveScreenshot(frame, "sc_ihm_pref_win_1.png");

            // Get the second tab screenshot
            frame.tabbedPane().selectTab("Legend Colors");
            saveScreenshot(frame, "sc_ihm_pref_win_2.png");

            // Get the third tab screenshot
            frame.tabbedPane().selectTab("Help Settings");
            saveScreenshot(frame, "sc_ihm_pref_win_3.png");

            // Close frame
            frame.close();
        }
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
}
