/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.2 2006-03-31 11:49:29 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.util.logging.Logger;

import javax.swing.*;
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

    /**
     * Constructor.
     */
    public MainMenuBar(MainWindow mainWindow)
    {
        _mainWindow      = mainWindow;
        _fileChooser     = new JFileChooser();

        fileMenu();
        editMenu();
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

        // Export... menu item
        menuItem = new JMenuItem("Export...");
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

        // Cut menu item
        menuItem = new JMenuItem("Cut");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Copy menu item
        menuItem = new JMenuItem("Copy");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Paste menu item
        menuItem = new JMenuItem("Paste");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Delete menu item
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Select All menu item
        menuItem = new JMenuItem("Select All");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        editMenu.add(menuItem);

        // Add a separator
        editMenu.add(new JSeparator());

        // Add a Comment... menu item
        menuItem = new JMenuItem("Add a Comment...");
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
     * Create and populate the 'View' menu
     */
    void viewMenu()
    {
        MCSLogger.trace();

        // Create the Edit menu.
        JMenu viewMenu = new JMenu("View");
        add(viewMenu);

        // Create each Edit menu item
        JMenuItem menuItem;

        // Cut menu item
        menuItem = new JMenuItem("Show Details");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        viewMenu.add(menuItem);

        // Copy menu item
        menuItem = new JMenuItem("Show Legend");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
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

        // Create each File menu item
        JMenuItem menuItem;

        // About SearchCal... menu item
        menuItem = new JMenuItem("About SearchCal...");
        menuItem.addActionListener(this);
        helpMenu.add(menuItem);

        menuItem = new JMenuItem("Active Help");
        menuItem.addActionListener(this);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                ActionEvent.CTRL_MASK));
        helpMenu.add(menuItem);
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
                System.out.println("Saving as: " + file.getName());
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

        // Export... handler
        if (actionName.equals("Export..."))
        {
            int returnVal = _fileChooser.showDialog(this, "Export...");

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = _fileChooser.getSelectedFile();
                System.out.println("Export: " + file.getName());
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

            Book       book      = new Book();
            book.append((Printable) _mainWindow, landscape);

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

        // Cut handler
        if (actionName.equals("Cut"))
        {
        }

        // Copy handler
        if (actionName.equals("Copy"))
        {
        }

        // Paste handler
        if (actionName.equals("Paste"))
        {
        }

        // Delete handler
        if (actionName.equals("Delete"))
        {
        }

        // Select All handler
        if (actionName.equals("Select All"))
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

        // Show Details handler
        if (actionName.equals("Show Details"))
        {
        }

        // Show Legend handler
        if (actionName.equals("Show Legend"))
        {
            // TODO _mainWindow.showLegendView();
        }
    }
}
/*___oOo___*/
