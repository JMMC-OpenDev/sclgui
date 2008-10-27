/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: NoteFrame.java,v 1.3 2007-02-13 13:58:44 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.awt.*;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class NoteFrame extends JFrame
{
    /**
     * DOCUMENT ME!
     */
    JTextPane textpanenote = new JTextPane();

    /**
     * DOCUMENT ME!
     */
    JScrollPane scrollnote;

    /**
     * NoteFrame  -  Constructor
     */
    public NoteFrame()
    {
        super("Note");
        textpanenote.setText("Add your comment...");
        textpanenote.setPreferredSize(new Dimension(600, 300));
        scrollnote = new JScrollPane(textpanenote);

        getContentPane().add(scrollnote);
        pack();
    }
}
