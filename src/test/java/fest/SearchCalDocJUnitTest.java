/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fest;

import fest.common.JmcsFestSwingJUnitTestCase;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.data.preference.SessionSettingsPreferences;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.util.timer.TimerFactory;
import fr.jmmc.sclgui.preference.Preferences;
import fr.jmmc.sclgui.vo.VirtualObservatory;
import java.awt.Dialog;
import java.io.File;
import javax.swing.AbstractAction;
import org.apache.commons.lang.SystemUtils;
import org.fest.assertions.Fail;
import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.matcher.DialogMatcher;
import static org.fest.swing.core.matcher.JButtonMatcher.*;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JProgressBarFixture;
import org.fest.swing.timing.Timeout;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * This simple tests concerns SearchCal GUI
 *
 * Note: emergency abort listener associated to 'Ctrl + Shift + A' key combination
 *
 * @author Laurent BOURGES, Sylvain LAFRASSE. 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class SearchCalDocJUnitTest extends JmcsFestSwingJUnitTestCase {

    /** 20s timeout */
    private static final Timeout LONG_TIMEOUT = Timeout.timeout(20 * 1000l);

    /**
     * Initialize system properties & static variables and finally starts the application
     */
    @BeforeClass
    public static void intializeAndStartApplication() {
        // Hack to reset LAF & ui scale:
        CommonPreferences.getInstance().resetToDefaultPreferences();
        
        // invoke Bootstrapper method to initialize logback now:
        Bootstrapper.getState();

        // reset window Preferences:
        new File(SystemUtils.USER_HOME + "/.fr.jmmc.jmcs.session_settings.jmmc.searchcal.properties").delete();

        // reset Preferences:
        Preferences.getInstance().resetToDefaultPreferences();
        SessionSettingsPreferences.getInstance().resetToDefaultPreferences();
        try {
            CommonPreferences.getInstance().setPreference(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN, false);
        } catch (PreferencesException pe) {
            logger.error("setPreference failed", pe);
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
                "-open", TEST_FOLDER + "SearchCal-ETA_TAU.scvot");
    }

    /**
     * Test if the application query correctly
     */
    @Test
    @GUITest
    public void m01_shouldQuery() {
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

        final JButtonFixture buttonFixture = window.button(withText(VirtualObservatory.LABEL_GET_CAL));
        final JProgressBarFixture progressFixture = window.progressBar("QueryProgress");

        // click to start SearchCal query:
        buttonFixture.click();
        pauseMedium();
        if (closeMessage()) {
            Fail.fail("An error occured while running query !");
        }

        // JSDC query catalog (0/1)
        progressFixture.waitUntilValueIs(0, LONG_TIMEOUT);

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
    public void m02_shouldOpenPreferences() {

        // TODO : generalize this over menu click method (cross-platform issue)
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() {
                final AbstractAction preferenceAction = ActionRegistrar.getInstance().getPreferenceAction();
                preferenceAction.actionPerformed(null);
            }
        });

        final Dialog prefDialog = robot().finder().find(DialogMatcher.withTitle("Preferences"));
        if (prefDialog != null) {
            final DialogFixture dialog = new DialogFixture(robot(), prefDialog);

            dialog.requireVisible();
            dialog.moveToFront();

            // Get the first tab screenshot
            dialog.tabbedPane().selectTab("Columns Order");
            dialog.list().selectItem(4);
            saveScreenshot(dialog, "sc_ihm_pref_win_1.png");

            // Get the second tab screenshot
            dialog.tabbedPane().selectTab("Legend Colors");
            saveScreenshot(dialog, "sc_ihm_pref_win_2.png");

            // Get the third tab screenshot
            dialog.tabbedPane().selectTab("General Settings");
            saveScreenshot(dialog, "sc_ihm_pref_win_3.png");

            // Get the fourth tab screenshot
            dialog.tabbedPane().selectTab("Misc. settings");
            saveScreenshot(dialog, "sc_ihm_pref_win_4.png");
            
            dialog.close();
        }
    }

    /**
     * Test the application exit sequence : ALWAYS THE LAST TEST
     */
    @Test
    @GUITest
    public void m03_shouldExit() {
        logger.info("shouldExit test");

        window.close();
        confirmDialogDontSave();
    }
}
