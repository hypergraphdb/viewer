package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;
import org.hypergraphdb.viewer.painter.editor.FontEditor;
import org.hypergraphdb.viewer.painter.editor.LineTypeEditor;
import org.hypergraphdb.viewer.painter.editor.ShapeEditor;
import org.hypergraphdb.viewer.visual.LineType;
import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

public class DefaultNodePainterBeanInfo extends BaseBeanInfo
{
	public DefaultNodePainterBeanInfo()
	{
		super(DefaultNodePainter.class);
		addProperty("color").setShortDescription("Node's foreground color.");
		addProperty("borderColor").setShortDescription("Node's border color.");
		ExtendedPropertyDescriptor e = addProperty("lineType");
		e.setShortDescription("Node's line type.");
		e.setPropertyEditorClass(LineTypeEditor.class);
		e.setPropertyTableRendererClass(LineTypeEditor.CellRenderer.class);
		
		e = addProperty("shape");
		   e.setShortDescription("Node's shape.");
		e.setPropertyEditorClass(ShapeEditor.class);
		e.setPropertyTableRendererClass(ShapeEditor.CellRenderer.class);
		addProperty("label").setShortDescription("Node's label text.");
		addProperty("labelColor").setShortDescription("Node's label color.");
		addProperty("tooltip").setShortDescription("Node's tooltip.");
		e = addProperty("font");
		e.setShortDescription("Node's font.");
		e.setPropertyEditorClass(FontEditor.class);
		e.setPropertyTableRendererClass(FontEditor.CellRenderer.class);
		
		addProperty("height").setShortDescription("Node's height.");
		addProperty("width").setShortDescription("Node's width.");

	}
}
