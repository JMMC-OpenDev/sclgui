/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: NoteFrame.java,v 1.4 2010-10-10 22:21:04 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/02/13 13:58:44  lafrasse
 * Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
 *
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
 * @version $Revision: 1.4 $
 */
public class NoteFrame extends JFrame
{
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /**
     * DOCUMENT ME!
     */
    private JTextPane textpanenote = new JTextPane();

    /**
     * DOCUMENT ME!
     */
    private JScrollPane scrollnote;

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
