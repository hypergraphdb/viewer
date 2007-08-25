package org.hypergraphdb.viewer.props;

import javax.swing.CellEditor;
import javax.swing.JComboBox;


public class PropertyComboBox  extends JComboBox
{
    private final CellEditor editor_;

    /***/
    public PropertyComboBox(final Object[] data, final CellEditor editor)
    {
        super(data);
        editor_ = editor;
        setEditable(true);
    }

    /***/
    public void setPopupVisible(boolean v)
    {
        if (v == false)
        {
            editor_.stopCellEditing();
        }
        super.setPopupVisible(v);
    }

}