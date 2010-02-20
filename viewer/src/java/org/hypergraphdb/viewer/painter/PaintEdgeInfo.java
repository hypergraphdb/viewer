package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.viewer.visual.Arrow;
import org.hypergraphdb.viewer.visual.LineType;

public interface PaintEdgeInfo
{
	public Color getColor();

	public void setColor(Color color);

	public Font getFont();

	public void setFont(Font font);

	public String getLabel();

	public void setLabel(String label);

	public Color getLabelColor();

	public void setLabelColor(Color labelColor);

	public LineType getLineType();

	public void setLineType(LineType lineType);

	public Arrow getSrcArrow();

	public void setSrcArrow(Arrow srcArrow);

	public Arrow getTgtArrow();

	public void setTgtArrow(Arrow tgtArrow);

	public String getTooltip();

	public void setTooltip(String tooltip);
	
	public void setTargetNodeTypeHandle(HGHandle h);
	public HGHandle getTargetNodeTypeHandle();
	
}