/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Generic filters view.
 */
public class FiltersView extends JPanel implements Printable {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(FiltersView.class.getName());

    /**
     * Constructor.
     *
     * @param model the model to display.
     */
    public FiltersView(FiltersModel model) {
        // Initialize layout and border
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Border lightBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        setBorder(new TitledBorder(lightBorder, "Filters"));

        // Add each model filter view to the view panel
        for (FilterView filterView : model.getFilterViews()) {
            int maxHeight = (int) filterView.getPreferredSize().getHeight();
            int maxWidth = (int) filterView.getMaximumSize().getWidth();
            filterView.setMaximumSize(new Dimension(maxWidth, maxHeight));
            add(filterView);
        }

        setMaximumSize(getPreferredSize());
        setMaximumSize(getMinimumSize());
    }

    /**
     * @param graphics 
     * @param pageFormat 
     * @param pageIndex 
     * @return 
     * @throws PrinterException 
     * @sa java.awt.print
     */
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        _logger.entering("FiltersView", "print");

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int fontHeight = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // Keep some space for page numbers
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth = pageFormat.getImageableWidth();

        g2d.drawString("Page: " + (pageIndex + 1), ((int) pageWidth / 2) - 35,
                (int) ((pageHeight + fontHeight) - fontDescent));

        g2d.scale(0.7, 0.7);
        paint(g2d);

        return (Printable.PAGE_EXISTS);
    }
}
/*___oOo___*/
