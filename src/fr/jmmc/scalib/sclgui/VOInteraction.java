/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: VOInteraction.java,v 1.1 2006-04-06 13:09:40 yvander Exp $"
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

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import cds.aladin.*;
import cds.tools.VOObserver;
import cds.tools.VOApp;

public class VOInteraction implements VOApp {

    /** aladin Software */
    VOApp _aladin;

    /**
     * Constructor.
     */
    public VOInteraction(){

    }

    /**
     * Launch aladin and put votable data.
     * @param votable The votable data loaded
     */
    public void startAladin(String votable) {

        // Instantiate Aladin
        _aladin = Aladin.launch();

        try {
            // Ask Aladin to load a VOTable file (you pass your own object reference
            // for possible callbacks. You can use the simpler method putVOTable(in,"MyVOTable")
            // if you do not want to be called back for showing or selecting objects
            // of this VOTable)

            // Prepare stream
            InputStream in = (InputStream) (new StringBufferInputStream(votable));

	    // Put data in Aladin
	    _aladin.putVOTable(this,in,"SearchCal Results");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When Aladin call putVOTable method
     */
    public String putVOTable(VOApp vOApp, InputStream inputStream,
                             String string) {
        return null;
    }

    /**
     * When Aladin call putVOTable method
     */
    public String putVOTable(InputStream in, String label) {
        return null;
    }

    /**
     * When Aladin call getVOTable method
     */
    public InputStream getVOTable(String dataID) {
        return null;
    }

    /**
     * When Aladin call putFits method
     */
    public String putFITS(InputStream in, String label) {
        return null;
    }

    /**
     * When Aladin call getFits method
     */
    public InputStream getFITS(String dataID) {
        return null;
    }

    /**
     * When Aladin call showVOTableObject method
     */
    public void showVOTableObject(String oid[]) {
        /*
         System.out.print("I have to show:");
         for (int i = 0; i < oid.length; i++) System.out.print(" " + oid[i]);
                 System.out.println();
         */
    }

    /**
     * When Aladin call selectVOTableObject method
     */
    public void selectVOTableObject(String oid[]) {
        /*System.out.print("I have to select:");

        for (int i = 0; i < oid.length; i++) {
            System.out.print(" " + oid[i]);

            //this.panelres.tableresult.addRowSelectionInterval(this.panelres.sorter.viewIndex(Integer.parseInt(oid[i])),				                                                                   this.panelres.sorter.viewIndex(Integer.parseInt(oid[i])));
        }
        System.out.println();
*/
    }

    /**
     * When Aladin call setVisible method
     */
    public void setVisible(boolean flag) {}

    /**
     * When Aladin call execCommand method
     */
    public String execCommand(String cmd) {
        return null;
    }

    /**
     * When Aladin call addObserve method
     */
    public void addObserver(VOObserver vOObserver, int _int) {
    }
}
/*___oOo___*/
