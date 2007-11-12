/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.25 2007-10-01 07:53:05 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.24  2007/08/17 12:09:33  lafrasse
 * Added tight integration into Mac OS X default menubar layout when running on Mac.
 *
 * Revision 1.23  2007/06/26 08:34:41  lafrasse
 * Added support for customizek keybord shortcut on Mac OS X.
 *
 * Revision 1.22  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.21  2006/11/29 17:34:19  lafrasse
 * Added new menu item to undelete stars flagged as deleted.
 *
 * Revision 1.20  2006/11/27 15:01:50  lafrasse
 * Moved PlotInAladin action from MainWindow to VirtualObservatory in order to
 * ensure menu ennabling/disabling.
 *
 * Revision 1.19  2006/11/20 15:40:25  lafrasse
 * Jalopization.
 *
 * Revision 1.18  2006/11/18 23:28:26  lafrasse
 * Double commit to add commit log after wrong manipulation.
 * Moved all menus support to the Action mecanism through MCSActions.
 *
 * Revision 1.17  2006/11/18 23:21:57  lafrasse
 * *** empty log message ***
 *
 * Revision 1.16  2006/11/13 17:12:18  lafrasse
 * Moved all file Open, Save, and Export code into CalibratorsModel.
 * Moved to Action based management for File menu and Query buttons.
 * Added preliminary file Param parsing.
 * Code and documentation refinments.
 *
 * Revision 1.15  2006/10/16 14:29:51  lafrasse
 * Updated to reflect MCSLogger API changes.
 *
 * Revision 1.14  2006/10/03 15:28:29  lafrasse
 * Updated to match new LegendView constructor API.
 *
 * Revision 1.13  2006/09/15 14:20:54  lafrasse
 * Added query default values support.
 *
 * Revision 1.12  2006/09/15 08:26:59  lafrasse
 * Added Help menu entries.
 * Code refinments.
 *
 * Revision 1.11  2006/08/04 16:53:54  lafrasse
 * Re-added preliminary print support
 *
 * Revision 1.10  2006/07/28 08:39:18  mella
 * Use shared PreferenceButtonModels
 *
 * Revision 1.9  2006/07/11 11:17:21  mella
 * Add better code for preferenced checkboxes
 *
 * Revision 1.8  2006/06/30 11:53:17  mella
 * Change GUI presentation
 *
 * Revision 1.7  2006/06/26 14:34:18  mella
 * Add delete action link
 *
 * Revision 1.6  2006/06/23 09:19:41  mella
 * Jalopization
 *
 * Revision 1.5  2006/06/22 12:31:43  lafrasse
 * Added CheckBox Menu Items for legend view and detailed view
 *
 * Revision 1.4  2006/06/19 11:25:17  mella
 * Add export capability and remove printing one
 *
 * Revision 1.3  2006/06/09 14:30:02  mella
 * Lynk ActiveHelp checkbox to the preference
 *
 * Revision 1.2  2006/03/31 11:49:29  mella
 * Make file-open menu work
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import java.awt.*;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.io.*;

import java.lang.reflect.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.BadLocationException;


/**
 * Handle main application menu bar.
 */
public class MainMenuBar extends JMenuBar
{
    /** Store whether the execution platform is a Mac or not */
    private static boolean MAC_OS_X = (System.getProperty("os.name")
                                             .toLowerCase()
                                             .startsWith("mac os x"));

    /** The window in which the menu bar should be displayed */
    private MainWindow _mainWindow;

    /** Shared instances */
    private Preferences _preferences;

    /**
     * Constructor.
     */
    public MainMenuBar(MainWindow mainWindow)
    {
        _mainWindow      = mainWindow;

        _preferences     = Preferences.getInstance();

        fileMenu();
        editMenu();
        queryMenu();
        calibratorsMenu();
        helpMenu();

        macOSXRegistration();
    }

    /**
     * Create and populate the 'File' menu
     */
    void fileMenu()
    {
        MCSLogger.trace();

        // Create the File menu.
        JMenu fileMenu = new JMenu("File");
        add(fileMenu);

        // Create each File menu item
        JMenuItem menuItem;

        // Open... menu item
        fileMenu.add(_mainWindow._vo._openFileAction);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Save menu item
        fileMenu.add(_mainWindow._vo._saveFileAction);

        // Save As... menu item
        fileMenu.add(_mainWindow._vo._saveFileAsAction);

        // Revert to Saved menu item
        fileMenu.add(_mainWindow._vo._revertToSavedFileAction);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Export as CSV... menu item
        fileMenu.add(_mainWindow._vo._exportToCSVFileAction);

        // Export as HTML... menu item
        fileMenu.add(_mainWindow._vo._exportToHTMLFileAction);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Page Setup... menu item
        fileMenu.add(_mainWindow._pageSetupAction);

        // Print... menu item
        fileMenu.add(_mainWindow._printAction);

        // If not running under Mac OS X
        if (MAC_OS_X == false)
        {
            // Quit menu item is provided on Mac OS X.

            // Add a separator
            fileMenu.add(new JSeparator());

            // Quit menu item
            fileMenu.add(_mainWindow._vo._quitAction);
        }
    }

    /**
     * Create and populate the 'Edit' menu
     */
    void editMenu()
    {
        MCSLogger.trace();

        // Create the Edit menu.
        JMenu editMenu = new JMenu("Edit");
        add(editMenu);

        // Create each Edit menu item
        JMenuItem menuItem;

        // The following 3 actions come from the default editor kit.
        // The 'control' key is used on Linux and Windows
        // If the execution is on Mac OS X
        String keyStringPrefix = "ctrl ";

        // If running under Mac OS X
        if (MAC_OS_X == true)
        {
            // The 'command' key (aka Apple key) is used
            keyStringPrefix = "meta ";
        }

        // Cut menu item
        Action cutAction = new DefaultEditorKit.CutAction();
        cutAction.putValue(Action.NAME, "Cut");
        cutAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyStringPrefix + "X"));
        editMenu.add(cutAction);

        // Copy menu item
        Action copyAction = new DefaultEditorKit.CopyAction();
        copyAction.putValue(Action.NAME, "Copy");
        copyAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyStringPrefix + "C"));
        editMenu.add(copyAction);

        // Paste menu item
        Action pasteAction = new DefaultEditorKit.PasteAction();
        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyStringPrefix + "V"));
        editMenu.add(pasteAction);

        // Delete menu item
        editMenu.add(_mainWindow._calibratorsView._deleteAction);

        // Undelete menu item
        editMenu.add(_mainWindow._calibratorsView._undeleteAction);

        // If not running under Mac OS X
        if (MAC_OS_X == false)
        {
            // Preferences menu item is provided on Mac OS X.

            // Add a separator
            editMenu.add(new JSeparator());

            // Preferences... menu item
            editMenu.add(_mainWindow._showPreferencesAction);
        }
    }

    /**
     * Create and populate the 'Query' menu
     */
    void queryMenu()
    {
        MCSLogger.trace();

        Action action;

        // Create the View menu.
        JMenu queryMenu = new JMenu("Query");
        add(queryMenu);

        // Create each View menu item
        JMenuItem menuItem;

        // Reset Values menu item
        queryMenu.add(_mainWindow._queryView._resetValuesAction);

        // Default Values menu item
        queryMenu.add(_mainWindow._queryView._loadDefaultValuesAction);

        // Save Values as Default Values menu item
        queryMenu.add(_mainWindow._queryView._saveValuesAction);

        // Add a separator
        queryMenu.add(new JSeparator());

        // Get Star menu item
        queryMenu.add(_mainWindow._vo._getStarAction);

        // Get Cal menu item
        queryMenu.add(_mainWindow._vo._getCalAction);
    }

    /**
     * Create and populate the 'Calibrators' menu
     */
    void calibratorsMenu()
    {
        MCSLogger.trace();

        // Create the View menu.
        JMenu calibratorsMenu = new JMenu("Calibrators");
        add(calibratorsMenu);

        // Create each View menu item
        JMenuItem menuItem;

        // Show Details item
        JCheckBoxMenuItem _showDetailsMenuItem = new JCheckBoxMenuItem();
        _showDetailsMenuItem.setModel(PreferencedButtonModel.getInstance(
                _preferences, "view.details.show"));
        _showDetailsMenuItem.setAction(CalibratorsView._showDetailsAction);
        calibratorsMenu.add(_showDetailsMenuItem);

        // Show Legend item
        JCheckBoxMenuItem _showLegendMenuItem = new JCheckBoxMenuItem();
        _showLegendMenuItem.setModel(PreferencedButtonModel.getInstance(
                _preferences, "view.legend.show"));
        _showLegendMenuItem.setAction(CalibratorsView._showLegendAction);
        calibratorsMenu.add(_showLegendMenuItem);

        // Add a separator
        calibratorsMenu.add(new JSeparator());

        // Plot in Aladin... menu item
        calibratorsMenu.add(_mainWindow._vo._plotInAladinAction);
    }

    /**
     * Create and populate the 'Help' menu
     */
    void helpMenu()
    {
        MCSLogger.trace();

        // Create the Help menu.
        JMenu helpMenu = new JMenu("Help");
        add(helpMenu);

        // Create each Help menu item
        JMenuItem menuItem;

        // If not running under Mac OS X
        if (MAC_OS_X == false)
        {
            // About... menu item is provided on Mac OS X.

            // About SearchCal... menu item
            helpMenu.add(_mainWindow._aboutAction);

            // Add a separator
            helpMenu.add(new JSeparator());
        }

        // SearchCal Tutorial menu item
        helpMenu.add(_mainWindow._helpView._tutorialAction);

        // SearchCal FAQ menu item
        helpMenu.add(_mainWindow._helpView._faqAction);

        // SearchCal Help menu item
        helpMenu.add(_mainWindow._helpView._helpAction);

        // SearchCal Feedback menu item
        helpMenu.add(_mainWindow._helpView._feedbackAction);

        // Add a separator
        helpMenu.add(new JSeparator());

        // Activate Help menu item
        PreferencedCheckBoxMenuItem _activeHelpMenuItem = new PreferencedCheckBoxMenuItem("Show Tooltips",
                _preferences, "help.tooltips.show");
        helpMenu.add(_activeHelpMenuItem);
    }

    /**
     * Generic registration with the Mac OS X application menu.
     *
     * Checks the platform, then attempts.
     */
    public void macOSXRegistration()
    {
        // If running under Mac OS X
        if (MAC_OS_X == true)
        {
            try
            {
                Class   osxAdapter     = this.getClass().getClassLoader()
                                             .loadClass("fr.jmmc.scalib.sclgui.OSXAdapter");

                Class[] defArgs        = { MainWindow.class };
                Method  registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication",
                        defArgs);

                if (registerMethod != null)
                {
                    Object[] args = { _mainWindow };
                    registerMethod.invoke(osxAdapter, args);
                }

                // This is slightly gross.  to reflectively access methods with boolean args, 
                // use "boolean.class", then pass a Boolean object in as the arg, which apparently
                // gets converted for you by the reflection system.
                defArgs[0] = boolean.class;

                Method prefsEnableMethod = osxAdapter.getDeclaredMethod("enablePrefs",
                        defArgs);

                if (prefsEnableMethod != null)
                {
                    Object[] args = { Boolean.TRUE };
                    prefsEnableMethod.invoke(osxAdapter, args);
                }
            }
            catch (NoClassDefFoundError e)
            {
                // This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
                // because OSXAdapter extends ApplicationAdapter in its def
                System.err.println(
                    "This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" +
                    e + ")");
            }
            catch (ClassNotFoundException e)
            {
                // This shouldn't be reached; if there's a problem with the OSXAdapter we should get the 
                // above NoClassDefFoundError first.
                System.err.println(
                    "This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" +
                    e + ")");
            }
            catch (Exception e)
            {
                System.err.println("Exception while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }
}
/*___oOo___*/
