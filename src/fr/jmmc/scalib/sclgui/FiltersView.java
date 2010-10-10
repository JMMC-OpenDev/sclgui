/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FiltersView.java,v 1.8 2010-10-10 22:21:04 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2008/09/10 22:25:42  lafrasse
 * Moved away from MCS Logger to standard Java logger API.
 * Documentation enhancement.
 *
 * Revision 1.6  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
 * Revision 1.5  2006/08/04 16:53:54  lafrasse
 * Re-added preliminary print support
 *
 * Revision 1.4  2006/08/04 14:09:10  lafrasse
 * Added GUI enabling/disabling feature to filters
 *
 * Revision 1.3  2006/06/23 09:19:41  mella
 * Jalopization
 *
 * Revision 1.2  2006/04/07 13:07:25  mella
 * Improve presentation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.awt.*;
import java.awt.print.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;


/**
 * Generic filters view.
 */
public class FiltersView extends JPanel implements Printable
{
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** Logger */
    private static final java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.scalib.sclgui.FiltersView");

    /**
     * Constructor.
     *
     * @param model the model to display.
     */
    public FiltersView(FiltersModel model)
    {
        // Initialize layout and border
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Border lightBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        setBorder(new TitledBorder(lightBorder, "Filters"));

        // Add each model filter view to the view panel
        Vector<FilterView> filterViews = model.getFilterViewVector();

        for (Enumeration<FilterView> e = filterViews.elements(); e.hasMoreElements();)
        {
            FilterView filterView = e.nextElement();
            int        maxHeight  = (int) filterView.getPreferredSize()
                                                    .getHeight();
            int        maxWidth   = (int) filterView.getMaximumSize().getWidth();
            filterView.setMaximumSize(new Dimension(maxWidth, maxHeight));
            add(filterView);
        }

        setMaximumSize(getPreferredSize());
        setMaximumSize(getMinimumSize());
    }

    /**
     * @sa java.awt.print
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
        throws PrinterException
    {
        _logger.entering("FiltersView", "print");

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int fontHeight  = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // Keep some space for page numbers
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth  = pageFormat.getImageableWidth();

        g2d.drawString("Page: " + (pageIndex + 1), ((int) pageWidth / 2) - 35,
            (int) ((pageHeight + fontHeight) - fontDescent));

        g2d.scale(0.7, 0.7);
        paint(g2d);

        return (Printable.PAGE_EXISTS);
    }
}
/*___oOo___*/
