package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.painter.editor.ArrowEditor;
import org.hypergraphdb.viewer.painter.editor.FontEditor;
import org.hypergraphdb.viewer.painter.editor.LineTypeEditor;
import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

public class DefaultEdgePainterBeanInfo extends BaseBeanInfo
{
	public DefaultEdgePainterBeanInfo()
	{
		super(DefaultEdgePainter.class);
		addProperty("color").setShortDescription("Edge's line color.");
		ExtendedPropertyDescriptor e = addProperty("lineType");
		e.setShortDescription("Edge's line type.");
		e.setPropertyEditorClass(LineTypeEditor.class);
		e.setPropertyTableRendererClass(LineTypeEditor.CellRenderer.class);
		
		e = addProperty("srcArrow");
		   e.setShortDescription("Edge's Source Arrow.");
		e.setPropertyEditorClass(ArrowEditor.class);
		e.setPropertyTableRendererClass(ArrowEditor.CellRenderer.class);
		
		e = addProperty("tgtArrow");
		e.setShortDescription("Edge's Target Arrow.");
		e.setPropertyEditorClass(ArrowEditor.class);
		e.setPropertyTableRendererClass(ArrowEditor.CellRenderer.class);
		
		
		addProperty("label").setShortDescription("Edge's label text.");
		addProperty("labelColor").setShortDescription("Edge's label color.");
		addProperty("tooltip").setShortDescription("Edge's tooltip.");
		e = addProperty("font");
		e.setShortDescription("Edge's font.");
		e.setPropertyEditorClass(FontEditor.class);
		e.setPropertyTableRendererClass(FontEditor.CellRenderer.class);
   }
}
