package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;
import org.hypergraphdb.viewer.visual.LineType;

public interface PaintNodeInfo
{
	public Color getBorderColor();
	public void setBorderColor(Color borderColor);	
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
	public byte getShape();
	public void setShape(byte shape);
	public String getTooltip();
	public void setTooltip(String tooltip);
	public double getWidth();
	public void setWidth(double width);
	public double getHeight();
	public void setHeight(double height);
	
	
	
	
}