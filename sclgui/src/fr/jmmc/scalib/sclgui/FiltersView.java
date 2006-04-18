/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FiltersView.java,v 1.2 2006-04-07 13:07:25 mella Exp $"
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

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
 * Generic filters view.
 */
public class FiltersView extends JPanel
{
    /** The model to represent */
    FiltersModel _model;

    /**
     * Constructor.
     *
     * @param model the model to display.
     */
    public FiltersView(FiltersModel model)
    {
        _model = model;

        Border lightBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        setBorder(new TitledBorder(lightBorder, "Filters"));

        // Set a layout manager
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Add each model filter view to the view panel
        Vector filterViews = _model.getFilterViewVector();

        for (Enumeration e = filterViews.elements(); e.hasMoreElements();)
        {
            FilterView filterView = (FilterView) e.nextElement();
            int maxHeight=(int)filterView.getPreferredSize().getHeight();
            int maxWidth= (int)filterView.getMaximumSize().getWidth();
            filterView.setMaximumSize(new Dimension(maxWidth, maxHeight)); 
            add(filterView);
        }
        setMaximumSize(getPreferredSize());
        setMaximumSize(getMinimumSize());
    }
}
/*___oOo___*/
