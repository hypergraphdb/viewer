package org.hypergraphdb.viewer.dialogs;

import java.util.List;


/**
 * Common panel representing labeled InputBox and labeled List 
 */
public class NameListPanel extends javax.swing.JPanel
{
    
    private List list;
    private String label1 = "";
    private String label2 = "";
    private final static String DEF = "";
    
    /** Creates new form BeanForm */
    public NameListPanel(String _label1, String _label2, List _list)
    {
        label1 = _label1;
        label2 = _label2;
        list = _list;
        initComponents();
        name.selectAll();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
   
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        value = new javax.swing.JComboBox(list.toArray());

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(label1);
        add(jLabel1, new java.awt.GridBagConstraints());

        name.setColumns(20);
        name.setText(DEF);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(name, gridBagConstraints);

        jLabel2.setText(label2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(value, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField name;
    private javax.swing.JComboBox value;
    // End of variables declaration//GEN-END:variables
    
     public String getName()
    {
        return (!name.getText().equals(DEF)) ? name.getText() : "";
    }
    
    public String getValue()
    {
        return (String) value.getSelectedItem();
    }
}
