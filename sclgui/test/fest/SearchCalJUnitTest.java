/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchCalJUnitTest.java,v 1.1 2011-03-18 16:22:33 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fest;

import static org.fest.swing.core.matcher.JButtonMatcher.*;
import static org.fest.swing.core.matcher.DialogMatcher.*;
import static org.fest.swing.timing.Pause.*;

import fest.common.JmcsApplicationSetup;

import fest.common.JmcsFestSwingJUnitTestCase;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JList;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Timeout;

import org.junit.Test;

/**
 * This simple tests concerns SearchCal GUI
 * 
 * @author bourgesl
 */
public final class SearchCalJUnitTest extends JmcsFestSwingJUnitTestCase {

  /** 20s timeout */
  protected static final Timeout LONG_TIMEOUT = Timeout.timeout(30000l);

  /**
   * Define the application
   */
  static {
    // disable dev LAF menu :
    System.setProperty("jmcs.laf.menu", "false");

    JmcsApplicationSetup.define(
            fr.jmmc.scalib.sclgui.SearchCalibrators.class,
            "-open", "/home/bourgesl/dev/sclgui/test/SearchCal-ETA_TAU_fast.scvot");

    // define robot delays :
    defineRobotDelayBetweenEvents(SHORT_DELAY);

    // define delay before taking screenshot :
    defineScreenshotDelay(SHORT_DELAY);

    // disable tooltips :
    enableTooltips(false);
  }

  /**
   * Test if the application started correctly
   */
  @Test
  @GUITest
  public void shouldStart() {
    window.requireVisible();

    saveScreenshot(window, "SearchCal-start.png");

    final String buttonText = "Get Calibrators";

    final JButtonFixture buttonFixture = window.button(withText(buttonText)).click();

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
  }

  /**
   * Test Preferences
   */
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
  @Test
  @GUITest
  public void shouldOpenFeedbackReport() {

    // hack to solve focus trouble in menu items :
    window.menuItemWithPath("Help").focus();

    window.menuItemWithPath("Help", "Report Feedback to JMMC...").click();

    final DialogFixture dialog = window.dialog(withTitle("JMMC Feedback Report ").andShowing());

    dialog.requireVisible();
    dialog.moveToFront();

    final String myEmail = "bourges.laurent@obs.ujf-grenoble.fr";

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
  /**
   * Close File overwrite confirm dialog clicking on "Replace" button
   */
  private void confirmDialogFileOverwrite() {
    try {
      // if file already exists, a confirm message appears :
      final JOptionPaneFixture optionPane = window.optionPane();

      if (optionPane != null) {
        // confirm file overwrite :
        optionPane.buttonWithText("Replace").click();
      }

    } catch (RuntimeException re) {
      // happens when the confirm message does not occur :
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "lookup failure : ", re);
      }
    }
  }

  /**
   * Close Save confirm dialog clicking on "Don't Save" button
   */
  private void confirmDialogDontSave() {
    // close confirm dialog :
    window.optionPane().buttonWithText("Don't Save").click();
  }

  /**
   * Close any option pane
   */
  private void closeMessage() {
    try {
      // if a message appears :
      final JOptionPaneFixture optionPane = window.optionPane();

      if (optionPane != null) {
        // click OK :
        optionPane.okButton().click();
      }

    } catch (RuntimeException re) {
      // happens when the confirm message does not occur :
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "lookup failure : ", re);
      }
    }
  }

  /**
   * Capture a screenshot of the application window, crop it and save it using the given file name
   * @param fileName the file name (including the png extension)
   * @param x the X coordinate of the upper-left corner of the
   *          specified rectangular region
   * @param y the Y coordinate of the upper-left corner of the
   *          specified rectangular region
   * @param w the width of the specified rectangular region (<=0 indicates to use the width of screenshot image)
   * @param h the height of the specified rectangular region (<=0 indicates to use the height of screenshot image)
   */
  private void saveCroppedScreenshotOf(final String fileName, final int x, final int y, final int w, final int h) {
    final BufferedImage image = takeScreenshotOf(window);

    final int width = (w <= 0) ? image.getWidth() : w;
    final int height = (h <= 0) ? image.getHeight() : h;

    final BufferedImage croppedImage = image.getSubimage(x, y, width, height);

    saveImage(croppedImage, fileName);
  }
}
