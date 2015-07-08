package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.painter.editor.ArrowEditor;
import org.hypergraphdb.viewer.painter.editor.FontEditor;
import org.hypergraphdb.viewer.painter.editor.LineTypeEditor;
import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;


/**
 * EdgePainter's BeanInfo used in properties editor
 */
public class DefaultEdgePainterBeanInfo extends BaseBeanInfo
{
	public DefaultEdgePainterBeanInfo()
	{
		super(DefaultEdgePainter.class);
		addProperty("color").setShortDescription("FEdge's line color.");
		ExtendedPropertyDescriptor e = addProperty("lineType");
		e.setShortDescription("FEdge's line type.");
		e.setPropertyEditorClass(LineTypeEditor.class);
		e.setPropertyTableRendererClass(LineTypeEditor.CellRenderer.class);
		
		e = addProperty("srcArrow");
		   e.setShortDescription("FEdge's Source Arrow.");
		e.setPropertyEditorClass(ArrowEditor.class);
		e.setPropertyTableRendererClass(ArrowEditor.CellRenderer.class);
		
		e = addProperty("tgtArrow");
		e.setShortDescription("FEdge's Target Arrow.");
		e.setPropertyEditorClass(ArrowEditor.class);
		e.setPropertyTableRendererClass(ArrowEditor.CellRenderer.class);
		
		
		addProperty("label").setShortDescription("FEdge's label text.");
		addProperty("labelColor").setShortDescription("FEdge's label color.");
		addProperty("tooltip").setShortDescription("FEdge's tooltip.");
		e = addProperty("font");
		e.setShortDescription("FEdge's font.");
		e.setPropertyEditorClass(FontEditor.class);
		e.setPropertyTableRendererClass(FontEditor.CellRenderer.class);
   }
}
