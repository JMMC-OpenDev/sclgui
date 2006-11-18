/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.17 2006-11-18 23:21:57 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
package jmmc.scalib.sclgui;

import jmmc.mcs.log.*;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.BadLocationException;


/**
 * Handle main application menu bar.
 */
public class MainMenuBar extends JMenuBar
{
    /** The window in which the menu bar should be displayed */
    MainWindow _mainWindow;

    /** Shared instances */
    Preferences _preferences;

    /**
     * Constructor.
     */
    public MainMenuBar(MainWindow mainWindow)
    {
        _mainWindow      = mainWindow;

        _preferences     = Preferences.getInstance();

        LegendView legendView = new LegendView();
        legendView.setVisible(false);

        fileMenu();
        editMenu();
        queryMenu();
        calibratorsMenu();
        helpMenu();
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

        // Add a separator
        fileMenu.add(new JSeparator());

        // Quit menu item
        fileMenu.add(_mainWindow._vo._quitAction);
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
        // Cut menu item
        Action cutAction = new DefaultEditorKit.CutAction();
        cutAction.putValue(Action.NAME, "Cut");
        cutAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke("ctrl X"));
        editMenu.add(cutAction);

        // Copy menu item
        Action copyAction = new DefaultEditorKit.CopyAction();
        copyAction.putValue(Action.NAME, "Copy");
        copyAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke("ctrl C"));
        editMenu.add(copyAction);

        // Paste menu item
        Action pasteAction = new DefaultEditorKit.PasteAction();
        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke("ctrl V"));
        editMenu.add(pasteAction);

        // Delete menu item
        editMenu.add(_mainWindow._calibratorsView._deleteAction);

        // Add a separator
        editMenu.add(new JSeparator());

        // Preferences... menu item
        editMenu.add(_mainWindow._showPreferencesAction);
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
        calibratorsMenu.add(_mainWindow._plotInAladinAction);
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

        // About SearchCal... menu item
        helpMenu.add(_mainWindow._aboutAction);

        // Add a separator
        helpMenu.add(new JSeparator());

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
}
/*___oOo___*/
