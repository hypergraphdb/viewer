package org.hypergraphdb.viewer.painter.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.visual.ui.EditorConstants;
import org.hypergraphdb.viewer.visual.ui.PopupIconChooser;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * PropertyEditor for specifying {@link org.hypergraphdb.viewer.painter.Shape} type
 */
public class ShapeEditor extends AbstractPropertyEditor
{
	private CellRenderer label;
	private JButton button;
	private byte shapeByte;

	public ShapeEditor()
	{
		editor = new JPanel(new PercentLayout(0, 0));
		((JPanel) editor).add("*", label = new CellRenderer());
		label.setOpaque(false);
		((JPanel) editor)
				.add(button = com.l2fprod.common.swing.ComponentFactory.Helper
						.getFactory().createMiniButton());
		button.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				selectShape();
			}
		});
		((JPanel) editor).setOpaque(false);
	}

	public Object getValue()
	{
		return shapeByte;
	}

	public void setValue(Object value)
	{
		shapeByte = (Byte) value;
		label.setValue(shapeByte);
	}
	
    protected void selectShape()
	{
		PopupIconChooser chooser = new PopupIconChooser("Select FNode Shape",
				    "BlahBlah",
				    EditorConstants.getShapeIcons(),
				    (Byte) getValue(),
				    GUIUtilities.getFrame(editor));
		int idx = chooser.showDialog();
		if (idx >= 0 && idx != shapeByte)
		{
			byte oldColor = shapeByte;
			//System.out.println("selectShape: " + icon.getDescription());
			byte newColor = (byte) idx;
		    label.setValue(newColor);
		    shapeByte = newColor;
			  firePropertyChange(oldColor, newColor);
		}
	}
	
	public static class CellRenderer extends DefaultCellRenderer
	  implements TableCellRenderer
	{
		protected String convertToString(Object value)
	    {
	        if(value == null) return null;
	       	byte shape = (Byte)value;
	        return EditorConstants.getShapeIcons()[shape].getDescription();
	    }

	    protected Icon convertToIcon(Object value)
	    {
	        if(value == null)
	            return null;
	        return EditorConstants.getShapeIcons()[(Byte) value];
	    }
	    
	    public void setValue(Object value)
	    {
	        String text = convertToString(value);
	        Icon icon = convertToIcon(value);
	        setText(text != null ? text : "");
	        setIcon(icon);
	    }
	    
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        setValue(value);
	        return this;
	    }
    }
}
