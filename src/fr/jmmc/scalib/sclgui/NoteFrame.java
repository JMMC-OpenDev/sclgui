/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.scalib.sclgui;

import java.awt.*;

import javax.swing.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class NoteFrame extends JFrame {

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
    public NoteFrame() {
        super("Note");
        textpanenote.setText("Add your comment...");
        textpanenote.setPreferredSize(new Dimension(600, 300));
        scrollnote = new JScrollPane(textpanenote);

        getContentPane().add(scrollnote);
        pack();
    }
}
