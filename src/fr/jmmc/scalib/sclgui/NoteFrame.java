/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: NoteFrame.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import java.awt.*;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
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
     * @param title String
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
