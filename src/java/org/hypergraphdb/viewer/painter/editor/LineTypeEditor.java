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
import org.hypergraphdb.viewer.visual.LineType;
import org.hypergraphdb.viewer.visual.ui.EditorConstants;
import org.hypergraphdb.viewer.visual.ui.PopupIconChooser;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * PropertyEditor for specifying {@link org.hypergraphdb.viewer.visual.LineType} type
 */
public class LineTypeEditor extends AbstractPropertyEditor
{
	private CellRenderer label;
	private JButton button;
	private LineType shapeByte;

	public LineTypeEditor()
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
		shapeByte = (LineType) value;
		label.setValue(shapeByte);
	}
	
    protected void selectShape()
	{
		PopupIconChooser chooser = new PopupIconChooser("Select FNode Line Type",
				    "BlahBlah",
				    EditorConstants.getLineTypeIcons(),
				    EditorConstants.getLineTypeIndex((LineType) getValue()),
				    GUIUtilities.getFrame(editor));
		int idx = chooser.showDialog();
		if (idx >= 0)
		{
			LineType oldColor = shapeByte;
			//System.out.println("selectLineType: " + idx);
			LineType newColor = EditorConstants.getLineTypes()[idx];
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
	        int idx = EditorConstants.getLineTypeIndex((LineType) value);
	        return (idx >=0) ? EditorConstants.getLineTypeIcons()[idx].getDescription(): null;
	    }

	    protected Icon convertToIcon(Object value)
	    {
	        if(value == null)
	            return null;
	        int idx = EditorConstants.getLineTypeIndex((LineType) value);
	        return (idx >=0) ? EditorConstants.getLineTypeIcons()[idx]: null;
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

