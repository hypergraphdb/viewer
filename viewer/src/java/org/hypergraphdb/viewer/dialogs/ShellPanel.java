package org.hypergraphdb.viewer.dialogs;

import javax.swing.*;

/**
 *
 * @author  User
 */
public class ShellPanel extends javax.swing.JPanel
{
    private JComponent label;
    private JScrollPane jScrollPane1;
    private JEditorPane pane;
    private String editor_text;
    
    public ShellPanel(JComponent label, String text)
    {
        this.label = label;
        this.editor_text = text;
        initComponents();
    }
    
    public String getEnteredText()
    {
        return pane.getText();
    }
    
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        pane = new javax.swing.JEditorPane();
        setLayout(new java.awt.GridBagLayout());

        //label.setMaximumSize(new java.awt.Dimension(200, 200));
        label.setPreferredSize(new java.awt.Dimension(250, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(label, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 300));
        pane.setText(editor_text);
        jScrollPane1.setViewportView(pane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 3.0;
        add(jScrollPane1, gridBagConstraints);

    }
    
    
    
    
    
}
