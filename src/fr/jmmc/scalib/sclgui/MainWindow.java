/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainWindow.java,v 1.2 2006-03-31 08:53:20 mella Exp $"
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

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;


/**
 * Main window.
 */
public class MainWindow extends JFrame implements Printable
{
    /** Main panel container, displaying the query and result views */
    Container contentPane;

    /**
     * Main window menu.
     */
    MainMenuBar menuBar;

    /**
     * Query view
     */
    QueryView _queryView;

    /**
     * Calibrators view
     */
    CalibratorsView _calibratorsView;

    /**
     * Preferences view
     */
    PreferencesView _preferencesView;

    /**
     * Filters view
     */
    FiltersView _filtersView;

    /**
     * Constructor.
     */
    public MainWindow(QueryView queryView, CalibratorsView calibratorsView,
        PreferencesView preferencesView, FiltersView filtersView)
    {
        _queryView           = queryView;
        _calibratorsView     = calibratorsView;
        _preferencesView     = preferencesView;
        _filtersView         = filtersView;

        try
        {
            //            UIManager.setLookAndFeel(new MetalLookAndFeel());
            setTitle("SearchCal");

            menuBar = new MainMenuBar(this);
            setJMenuBar(menuBar);

            contentPane = getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

            queryView.setVisible(true);
            contentPane.add(_queryView);

            _calibratorsView.setVisible(true);
            contentPane.add(_calibratorsView);

            _filtersView.setVisible(true);
            contentPane.add(_filtersView);

            pack();
            setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Specify that the main should quit the application when closed
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Display the registered preferences view
     */
    public void showPreferencesView()
    {
        _preferencesView.setVisible(true);
    }

    /**
     * print  -  Printing results
     * @param g Graphics
     * @param pf PageFormat
     * @param pi int for pageIndex
     * @return int
     * @throws PrinterException
     */
    public int print(Graphics g, PageFormat pf, int pi)
        throws PrinterException
    {
        MCSLogger.trace();

        if (pi > 0)
        {
            return (NO_SUCH_PAGE);
        }
        else
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());

            int fontHeight  = g2d.getFontMetrics().getHeight();
            int fontDescent = g2d.getFontMetrics().getDescent();

            // laisser de l'espace pour le numero de page
            double pageHeight = pf.getImageableHeight() - fontHeight;
            double pageWidth  = pf.getImageableWidth();

            g2d.drawString("Page: " + (pi + 1), ((int) pageWidth / 2) - 35,
                (int) ((pageHeight + fontHeight) - fontDescent));

            g2d.scale(0.7, 0.7);
            disableDoubleBuffering(_queryView);
            _queryView.paint(g2d);
            enableDoubleBuffering(_queryView);
            g2d.scale(1, 1);

            /*
               A placer dans le resultModel??
                        //Comment printing
                        if (this.resultPanel.noteinstancied == true) {
                            System.out.println("Note added");
                            disableDoubleBuffering(this.resultPanel.framenote);
                            g2d.drawString("Your Comment : ", 0,
                                           (int) (this.queryPanel.getHeight()) + 30);
                            Graphics2D gd2 = (Graphics2D) g2d.create(0,
                                    (int) (this.queryPanel.getHeight()) + 50,
                                    this.resultPanel.framenote.getWidth(),
                                    this.resultPanel.framenote.getHeight());
                            this.resultPanel.framenote.textpanenote.paint(gd2);
                            enableDoubleBuffering(this.resultPanel.framenote);
                        }
             */
            return PAGE_EXISTS;
        }
    }

    /**
     * Disable DoubleBuffering for the given component.
     * @param c Component
     */
    public static void disableDoubleBuffering(Component c)
    {
        MCSLogger.trace();

        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /**
     * Enable DoubleBuffering for the given component.
     * @param c Component
     */
    public static void enableDoubleBuffering(Component c)
    {
        MCSLogger.trace();

        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
/*___oOo___*/
