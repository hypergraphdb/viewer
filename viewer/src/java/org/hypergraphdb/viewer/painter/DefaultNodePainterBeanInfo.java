package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.painter.editor.FontEditor;
import org.hypergraphdb.viewer.painter.editor.LineTypeEditor;
import org.hypergraphdb.viewer.painter.editor.ShapeEditor;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

/**
 * NodePainter's BeanInfo used in properties editor
 */
public class DefaultNodePainterBeanInfo extends BaseBeanInfo
{
	public DefaultNodePainterBeanInfo()
	{
		super(DefaultNodePainter.class);
		addProperty("color").setShortDescription("FNode's foreground color.");
		addProperty("borderColor").setShortDescription("FNode's border color.");
		ExtendedPropertyDescriptor e = addProperty("lineType");
		e.setShortDescription("FNode's line type.");
		e.setPropertyEditorClass(LineTypeEditor.class);
		e.setPropertyTableRendererClass(LineTypeEditor.CellRenderer.class);
		
		e = addProperty("shape");
		   e.setShortDescription("FNode's shape.");
		e.setPropertyEditorClass(ShapeEditor.class);
		e.setPropertyTableRendererClass(ShapeEditor.CellRenderer.class);
		addProperty("label").setShortDescription("FNode's label text.");
		addProperty("labelColor").setShortDescription("FNode's label color.");
		addProperty("tooltip").setShortDescription("FNode's tooltip.");
		e = addProperty("font");
		e.setShortDescription("FNode's font.");
		e.setPropertyEditorClass(FontEditor.class);
		e.setPropertyTableRendererClass(FontEditor.CellRenderer.class);
		
		addProperty("height").setShortDescription("FNode's height.");
		addProperty("width").setShortDescription("FNode's width.");

	}
}
