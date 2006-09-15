/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.13 2006-09-15 14:20:54 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.BadLocationException;


/**
 * Handle main application menu bar.
 */
public class MainMenuBar extends JMenuBar implements ActionListener
{
    /** The window in which the menu bar should be displayed */
    MainWindow _mainWindow;

    /** Shared file chooser accross load, save and export */
    JFileChooser _fileChooser;

    /** Shared instances */
    Preferences _preferences;

    /** Interaction with Aladin */
    VOInteraction _aladinInteraction = null;

    /** Logger instance */
    Logger _logger = MCSLogger.getLogger();

    /**
     * Constructor.
     */
    public MainMenuBar(MainWindow mainWindow)
    {
        _mainWindow      = mainWindow;
        _fileChooser     = new JFileChooser();

        _preferences     = Preferences.getInstance();

        LegendView legendView = new LegendView(_preferences);
        legendView.setVisible(false);

        fileMenu();
        editMenu();
        queryMenu();
        viewMenu();
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
        menuItem = new JMenuItem("Open...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Save menu item
        menuItem = new JMenuItem("Save");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Save As... menu item
        menuItem = new JMenuItem("Save As...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Revert to Saved menu item
        menuItem = new JMenuItem("Revert to Saved");
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Export as CSV... menu item
        menuItem = new JMenuItem("Export as CSV...");
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Export as HTML... menu item
        menuItem = new JMenuItem("Export as HTML...");
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Page Setup... menu item
        menuItem = new JMenuItem("Page Setup...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);
        // Print... menu item
        menuItem = new JMenuItem("Print...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);

        // Add a separator
        fileMenu.add(new JSeparator());

        // Quit menu item
        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        fileMenu.add(menuItem);
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

        // Undo menu item
        menuItem = new JMenuItem("Undo");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Redo menu item
        menuItem = new JMenuItem("Redo");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Add a separator
        editMenu.add(new JSeparator());

        //These actions come from the default editor kit.
        //Get the ones we want and stick them in the menu.
        editMenu.add(new DefaultEditorKit.CutAction());
        editMenu.add(new DefaultEditorKit.CopyAction());
        editMenu.add(new DefaultEditorKit.PasteAction());

        // Delete menu item
        Action action;
        action = _mainWindow._calibratorsView._deleteAction;
        editMenu.add(action);

        // Select All menu item
        menuItem = new JMenuItem("Select All");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Add a separator
        editMenu.add(new JSeparator());

        // Preferences... menu item
        menuItem = new JMenuItem("Preferences...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);
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
        menuItem = new JMenuItem("Reset Values");
        menuItem.addActionListener(this);
        queryMenu.add(menuItem);

        // Add a separator
        queryMenu.add(new JSeparator());

        // Default Values menu item
        menuItem = new JMenuItem("Load Default Values");
        menuItem.addActionListener(this);
        queryMenu.add(menuItem);

        // Save Values as Default Values menu item
        menuItem = new JMenuItem("Save Current Values as Default");
        menuItem.addActionListener(this);
        queryMenu.add(menuItem);
    }

    /**
     * Create and populate the 'View' menu
     */
    void viewMenu()
    {
        MCSLogger.trace();

        Action action;

        // Create the View menu.
        JMenu viewMenu = new JMenu("View");
        add(viewMenu);

        // Create each View menu item
        JMenuItem menuItem;

        // Show Details item
        JCheckBoxMenuItem _showDetailsMenuItem = new JCheckBoxMenuItem();
        _showDetailsMenuItem.setModel(PreferencedButtonModel.getInstance(
                _preferences, "view.details.show"));
        action = CalibratorsView._showDetailsAction;
        _showDetailsMenuItem.setAction(action);
        viewMenu.add(_showDetailsMenuItem);

        // Show Legend item
        JCheckBoxMenuItem _showLegendMenuItem = new JCheckBoxMenuItem();
        _showLegendMenuItem.setModel(PreferencedButtonModel.getInstance(
                _preferences, "view.legend.show"));
        action = CalibratorsView._showLegendAction;
        _showLegendMenuItem.setAction(action);
        viewMenu.add(_showLegendMenuItem);

        // Add a separator
        viewMenu.add(new JSeparator());

        // Plot in Aladin... menu item
        menuItem = new JMenuItem("Plot in Aladin...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        viewMenu.add(menuItem);
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
        menuItem = new JMenuItem("About SearchCal...");
        menuItem.addActionListener(this);
        helpMenu.add(menuItem);

        // Add a separator
        helpMenu.add(new JSeparator());

        // SearchCal Tutorial menu item
        menuItem = new JMenuItem("SearchCal Tutorial");
        menuItem.addActionListener(this);
        helpMenu.add(menuItem);

        // SearchCal FAQ menu item
        menuItem = new JMenuItem("SearchCal FAQ");
        menuItem.addActionListener(this);
        helpMenu.add(menuItem);

        // SearchCal Help menu item
        menuItem = new JMenuItem("SearchCal Help");
        menuItem.addActionListener(this);
        helpMenu.add(menuItem);

        // SearchCal Feedback menu item
        menuItem = new JMenuItem("SearchCal Feedback");
        menuItem.addActionListener(this);
        helpMenu.add(menuItem);

        // Add a separator
        helpMenu.add(new JSeparator());

        // Activate Help menu item
        PreferencedCheckBoxMenuItem _activeHelpMenuItem = new PreferencedCheckBoxMenuItem("Show Tooltips",
                _preferences, "help.tooltips.show");

        helpMenu.add(_activeHelpMenuItem);
    }

    /**
     * Menu controller.
     *
     * TODO split inner code!
     *
     * @param evt ActionEvent to handle
     */
    public void actionPerformed(ActionEvent evt)
    {
        MCSLogger.trace();

        String actionName = evt.getActionCommand();

        // Display configuration opening
        if (actionName.equals("Data display configuration"))
        {
            _mainWindow.showPreferencesView();
        }

        // Open... handler
        if (actionName.equals("Open..."))
        {
            int returnVal = _fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = _fileChooser.getSelectedFile();
                _mainWindow._calibratorsView.openFile(file);
            }
            else
            {
                System.out.println("'" + actionName + "' command cancelled.");
            }
        }

        // Save handler
        if (actionName.equals("Save"))
        {
            // TODO Overwrite file if loaded from file, Save as... otherwise
        }

        // Save As... handler
        if (actionName.equals("Save As..."))
        {
            int returnVal = _fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = _fileChooser.getSelectedFile();
                // @TODO change this part of code
                _mainWindow._calibratorsView._calibratorsModel.saveVOTable(file);
            }
            else
            {
                System.out.println("'" + actionName + "' command cancelled.");
            }
        }

        // Revert to Saved handler
        if (actionName.equals("Revert to Saved"))
        {
            // TODO reparse VOTable
        }

        // Export as HTML... handler
        if (actionName.equals("Export as HTML..."))
        {
            int returnVal = _fileChooser.showDialog(this, "Export");

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = _fileChooser.getSelectedFile();
                _mainWindow.exportVOTableToHTML(file.getName());
            }
            else
            {
                System.out.println("'" + actionName + "' command cancelled.");
            }
        }

        // Export as CSV... handler
        if (actionName.equals("Export as CSV..."))
        {
            int returnVal = _fileChooser.showDialog(this, "Export");

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = _fileChooser.getSelectedFile();
                _mainWindow.exportVOTableToCSV(file.getName());
            }
            else
            {
                System.out.println("'" + actionName + "' command cancelled.");
            }
        }

        // Page Setup... handler
        if (actionName.equals("Page Setup..."))
        {
            // TODO put printJob, landscape upward to be shared with Print... handler below
            PrinterJob printJob = PrinterJob.getPrinterJob();

            // Set up Book
            PageFormat landscape = printJob.defaultPage();
            landscape.setOrientation(PageFormat.LANDSCAPE);
            printJob.pageDialog(landscape);
        }

        // Print... handler
        if (actionName.equals("Print..."))
        {
            // TODO put printJob, landscape upward to be shared with Print... handler below
            PrinterJob printJob  = PrinterJob.getPrinterJob();
            PageFormat landscape = printJob.defaultPage();
            landscape.setOrientation(PageFormat.LANDSCAPE);

            Book book = new Book();
            book.append((Printable) _mainWindow._queryView, landscape);
            book.append((Printable) _mainWindow._calibratorsView, landscape);
            book.append((Printable) _mainWindow._filtersView, landscape);

            // TODO check if previous line is not sufficient
            //book.append((Printable) _mainWindow.resultspanel, landscape);
            printJob.setPageable(book);

            //Print dialog
            if (printJob.printDialog())
            {
                try
                {
                    printJob.print();
                }
                catch (PrinterException e)
                {
                    e.printStackTrace();
                }
            }
        }

        // Quit handler
        if (actionName.equals("Quit"))
        {
            // TODO check if unsaved before this !!!
            System.exit(0);
        }

        // Undo handler
        if (actionName.equals("Undo"))
        {
        }

        // Redo handler
        if (actionName.equals("Redo"))
        {
        }

        // Add a Comment... handler
        if (actionName.equals("Add a Comment..."))
        {
            // _mainWindow.showCommentView();
        }

        // Preferences... handler
        if (actionName.equals("Preferences..."))
        {
            _mainWindow.showPreferencesView();
        }

        // Reset Values handler
        if (actionName.equals("Reset Values"))
        {
            _mainWindow._queryView._queryModel.reset();
        }

        // Default Values handler
        if (actionName.equals("Load Default Values"))
        {
            _mainWindow._queryView._queryModel.loadDefaultValues();
        }

        // Save Values as Default handler
        if (actionName.equals("Save Current Values as Default"))
        {
            _mainWindow._queryView._queryModel.saveDefaultValues();
        }

        // Show Details handler
        if (actionName.equals("Show Details"))
        {
            // TODO
        }

        // Show Legend handler
        if (actionName.equals("Show Legend"))
        {
            // TODO _mainWindow.showLegendView();
            //            legendView.setVisible(true);
        }

        // Plot data in Aladin handler
        if (actionName.equals("Plot in Aladin..."))
        {
            CalibratorsModel calibratorsModel = _mainWindow._calibratorsView._calibratorsModel;

            if (calibratorsModel.getVOTable() != null)
            {
                if (_aladinInteraction == null)
                {
                    //
                    _aladinInteraction = new VOInteraction();
                    _aladinInteraction.startAladin(calibratorsModel.getVOTable());
                    _aladinInteraction._aladin.execCommand("sync");
                }
                else
                {
                    InputStream in  = new StringBufferInputStream(calibratorsModel.getVOTable());
                    String      oid;
                    oid             = _aladinInteraction.putVOTable(_aladinInteraction,
                            in, "SearchCal");
                    _logger.fine("Aladin return new oid for votable : " + oid);
                    //
                    _aladinInteraction._aladin.setVisible(true);
                }
            }
        }
    }
}
/*___oOo___*/
