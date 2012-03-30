/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.preference;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.sclgui.LegendView;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

// @TODO handle close button correctly
// @TODO add deleteObserver(this) to dispose() to dereference each subview properly
/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends JFrame implements ActionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(PreferencesView.class.getName());
    /** Data model */
    Preferences _preferences = null;
    /** Preferences... action */
    public final ShowPreferencesAction _showPreferencesAction;
    /** "Restore to Default Settings" button */
    protected JButton _restoreDefaultButton = null;
    /** "Save Modifications" button */
    protected JButton _saveModificationButton = null;

    /**
     * Constructor.
     */
    public PreferencesView() {
        super("Preferences");

        // Window size
        setSize(600, 400);
        setResizable(false);

        // Get and listen to data model modifications
        _preferences = Preferences.getInstance();

        // Build the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(tabbedPane);

        // Add the columns preferences pane
        ColumnsPreferencesView columnsView = new ColumnsPreferencesView("view.columns");
        columnsView.init();
        tabbedPane.add("Columns Order", columnsView);

        // Add the catalog preferences pane
        JPanel catalogView = new LegendView(true);
        tabbedPane.add("Legend Colors", catalogView);

        // Add the help preferences pane
        HelpPreferencesView helpView = new HelpPreferencesView();
        helpView.init();
        tabbedPane.add("Help Settings", helpView);

        // Add the restore and sace buttons
        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore Default Settings");
        buttonsPanel.add(_restoreDefaultButton);
        _saveModificationButton = new JButton("Save Modifications");
        buttonsPanel.add(_saveModificationButton);
        contentPane.add(buttonsPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();

        // Window screen position (centered)
        WindowUtils.centerOnMainScreen(this);
        WindowUtils.setClosingKeyboardShortcuts(rootPane, this);

        _showPreferencesAction = new ShowPreferencesAction(getClass().getName(), "_showPreferencesAction");
    }

    public void init() {
        _restoreDefaultButton.addActionListener(this);
        _saveModificationButton.addActionListener(this);
    }

    /**
     * actionPerformed  -  Listener
     *
     * @param evt ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        _logger.entering("PreferencesView", "actionPerformed");

        // If the "Restore to default settings" button has been pressed
        if (evt.getSource().equals(_restoreDefaultButton)) {
            try {
                _preferences.resetToDefaultPreferences();
            } catch (Exception ex) {
                _logger.log(Level.WARNING,
                        "Could not reset preferences to default.", ex);
            }
        }

        // If the "Save modifications" button has been pressed
        if (evt.getSource().equals(_saveModificationButton)) {
            try {
                _preferences.saveToFile();
            } catch (Exception ex) {
                _logger.log(Level.WARNING, "Could not save preferences.", ex);
            }
        }
    }

    /**
     * Called to show the preferences window.
     */
    protected class ShowPreferencesAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        ShowPreferencesAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            flagAsPreferenceAction();
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.entering("ShowPreferencesAction", "actionPerformed");

            // Show the Preferences window
            setVisible(true);
        }
    }
}
