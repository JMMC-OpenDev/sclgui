/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SCAction.java,v 1.1 2006-06-26 14:30:40 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.*;

import javax.swing.*;


/**
 * Use this class  to define new Actions.
 */
public abstract class SCAction extends AbstractAction
{
    /**
     * This constructor use the resource file to get text description and icon
     * of action.
     */
    public SCAction(String actionName)
    {
        // Collect action info
        String    text = Resources.getActionText(actionName);
        String    desc = Resources.getActionDescription(actionName);
        ImageIcon icon = Resources.getActionIcon(actionName);
        // Init action    
        // @TODO check if null must be checked...
        putValue(Action.NAME, text);
        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(Action.SMALL_ICON, icon);
    }
}
/*___oOo___*/
