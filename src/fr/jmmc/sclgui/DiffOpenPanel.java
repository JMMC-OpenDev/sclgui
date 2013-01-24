/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DiffOpenPanel.java,v $"
 *
 * History
 * -------
 * $Log: DiffOpenPanel.java,v $
 */
package fr.jmmc.sclgui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.component.ComponentResizeAdapter;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.MimeType;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Open File action for SearchCal Diff tool
 * @author bourgesl
 */
public final class DiffOpenPanel extends javax.swing.JPanel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(DiffOpenPanel.class.getName());
    /* members */
    /** editor result = true if the user validates the inputs */
    private boolean result = false;
    /* Swing */
    /** dialog window */
    private JDialog dialog;

    /** Creates new form DiffOpenPanel */
    public DiffOpenPanel() {
        initComponents();
    }

    /**
     * Display the open diff tool
     * @param fileLeft predefined file on left side
     * @param fileRight predefined file on right side
     * @return File[fileLeft, fileRight] if the user validated the dialog or null
     */
    public static File[] showDialog(final File fileLeft, final File fileRight) {
        _logger.debug("open diff dialog ...");

        File[] result = null;

        JDialog dialog = null;

        try {

            final DiffOpenPanel form = new DiffOpenPanel();

            // initialise the diff panel :
            form.initialize(fileLeft, fileRight);

            // 1. Create the dialog
            dialog = new JDialog(App.getFrame(), "Open SearchCal diff tool", true);

            final Dimension dim = new Dimension(600, 200);
            dialog.setMinimumSize(dim);
            dialog.addComponentListener(new ComponentResizeAdapter(dim));

            // 2. Optional: What happens when the dialog closes ?
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 3. Create components and put them in the dialog
            dialog.add(form);

            // Associate the dialog to the form :
            form.setDialog(dialog);

            // 4. Size the dialog.
            dialog.pack();

            // Center it :
            dialog.setLocationRelativeTo(dialog.getOwner());

            // 5. Show it and waits until dialog is not visible or disposed :
            dialog.setVisible(true);

            // get editor result :
            if (form.isResult()) {
                result = new File[]{form.getFileLeft(), form.getFileRight()};
            }

        } finally {
            if (dialog != null) {
                _logger.debug("dispose diff dialog ...");

                dialog.dispose();
            }
        }

        return result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelHeader = new javax.swing.JLabel();
        jLabelFileLeft = new javax.swing.JLabel();
        jTextFieldFileLeft = new javax.swing.JTextField();
        jButtonOpenLeft = new javax.swing.JButton();
        jLabelRight = new javax.swing.JLabel();
        jTextFieldFileRight = new javax.swing.JTextField();
        jButtonOpenRight = new javax.swing.JButton();
        jPanelButtons = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabelHeader.setText("<html><h2>Please choose files to compare:</h2></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        add(jLabelHeader, gridBagConstraints);

        jLabelFileLeft.setText("Left:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabelFileLeft, gridBagConstraints);

        jTextFieldFileLeft.setColumns(40);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        add(jTextFieldFileLeft, gridBagConstraints);

        jButtonOpenLeft.setText("Open...");
        jButtonOpenLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenLeftActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jButtonOpenLeft, gridBagConstraints);

        jLabelRight.setText("Right:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabelRight, gridBagConstraints);

        jTextFieldFileRight.setColumns(40);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        add(jTextFieldFileRight, gridBagConstraints);

        jButtonOpenRight.setText("Open...");
        jButtonOpenRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jButtonOpenRight, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCancel);

        jButtonOK.setText("Ok");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonOK);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOpenLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenLeftActionPerformed

        final File selectedFile = openFileChooser(getFileLeft());

        if (selectedFile != null) {
            setFileLeft(selectedFile);
        }
    }//GEN-LAST:event_jButtonOpenLeftActionPerformed

    private void jButtonOpenRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenRightActionPerformed

        final File selectedFile = openFileChooser(getFileRight());

        if (selectedFile != null) {
            setFileRight(selectedFile);
        }
    }//GEN-LAST:event_jButtonOpenRightActionPerformed

    /**
     * Open a file chooser with the given selected file
     * @param file optional selected file
     * @return File instance or null if dialog was discarded
     */
    private File openFileChooser(final File file) {
        final File currentDir;
        final String defaultFileName;

        if (file != null) {
            currentDir = file.getParentFile();
            defaultFileName = file.getName();
        } else {
            currentDir = null;
            defaultFileName = null;
        }

        return FileChooser.showOpenFileChooser("Open a calibrator list from file...", currentDir, MimeType.SEARCHCAL_CALIBRATORLIST, defaultFileName);
    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        if (this.dialog != null) {
            this.dialog.setVisible(false);
        }
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        // delegate the validation to inner child panels :
        if (!validateForm()) {
            return;
        }

        // update the validation flag :
        this.result = true;

        if (this.dialog != null) {
            this.dialog.setVisible(false);
        }
    }//GEN-LAST:event_jButtonOKActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonOpenLeft;
    private javax.swing.JButton jButtonOpenRight;
    private javax.swing.JLabel jLabelFileLeft;
    private javax.swing.JLabel jLabelHeader;
    private javax.swing.JLabel jLabelRight;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JTextField jTextFieldFileLeft;
    private javax.swing.JTextField jTextFieldFileRight;
    // End of variables declaration//GEN-END:variables

    /**
     * Define the selected File on left side
     * @param file selected File on left side
     */
    public void setFileLeft(final File file) {
        if (file != null) {
            this.jTextFieldFileLeft.setText(file.getAbsolutePath());
        }
    }

    /**
     * Return the selected File on left side
     * @return selected File on left side
     */
    public File getFileLeft() {
        return FileUtils.getFile(this.jTextFieldFileLeft.getText());
    }

    /**
     * Define the selected File on right side
     * @param file selected File on right side
     */
    public void setFileRight(final File file) {
        if (file != null) {
            this.jTextFieldFileRight.setText(file.getAbsolutePath());
        }
    }

    /**
     * Return the selected File on right side
     * @return selected File on right side
     */
    public File getFileRight() {
        return FileUtils.getFile(this.jTextFieldFileRight.getText());
    }

    /**
     * Initialize the form
     * @param fileLeft predefined file on left side
     * @param fileRight predefined file on right side
     */
    void initialize(final File fileLeft, final File fileRight) {
        setFileLeft(fileLeft);
        setFileRight(fileRight);
    }

    /**
     * Validate the form
     * @return true only if the data are valid
     */
    boolean validateForm() {
        // Validate that both files exists:
        if (getFileLeft() == null) {
            MessagePane.showErrorMessage("Please choose an existing file on left side.", "Invalid file Left !");
            return false;
        }
        if (getFileRight() == null) {
            MessagePane.showErrorMessage("Please choose an existing file on right side.", "Invalid file Right !");
            return false;
        }
        return true;
    }

    /**
     * Return the editor result
     * @return true if the user validated (ok button)
     */
    private boolean isResult() {
        return result;
    }

    /**
     * Define the JDialog for this form
     * @param dialog JDialog instance
     */
    private void setDialog(final JDialog dialog) {
        this.dialog = dialog;
    }
}