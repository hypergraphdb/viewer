package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;
import org.hypergraphdb.viewer.visual.LineType;

/**
 * This interface describes all visual properties used for drawing a node
 */
public interface PaintNodeInfo
{
    /**
     * Returns the color used to paint node's border
     */
    public Color getBorderColor();

    /**
     * Sets the color used to paint node's border
     */
    public void setBorderColor(Color borderColor);
    /**
     * Returns the color used to paint node
     */
    public Color getColor();
    /**
     * Sets the color used to paint node
     */
    public void setColor(Color color);

    /**
     * Returns the font used to paint node's label
     */
    public Font getFont();

    /**
     * Sets the font used to paint node's label
     */
    public void setFont(Font font);

    /**
     * Returns the node's height
     */
    public double getHeight();

    /**
     * Sets the node's height
     */
    public void setHeight(double height);
    /**
     * Returns the node's label
     */
    public String getLabel();
    /**
     * Sets the node's label
     */
    public void setLabel(String label);
 
     /**
     * Returns the node's label color
     */
    public Color getLabelColor();

    /**
     * Sets the node's label color
     */
    public void setLabelColor(Color labelColor);

    /**
     * Returns the LineType used for painting the node's border
     */
    public LineType getLineType();

    /**
     * Sets the LineType used for painting the node's border
     */
    public void setLineType(LineType lineType);
    
    /**
     * Returns the node's shape type
     */
    public byte getShape();
    /**
     * Sets the node's shape type
     * Supported values are: org.hypergraphdb.viewer.painter.Shape.
     * RECT, ROUND_RECT, RECT_3D,TRAPEZOID, TRAPEZOID_2,
     * TRIANGLE, PARALLELOGRAM, DIAMOND, ELLIPSE, HEXAGON, OCTAGON 
     */
    public void setShape(byte shape);

    /**
     * Returns the node's tooltip text
     */
    public String getTooltip();


    /**
     * Sets the node's tooltip text
     */
    public void setTooltip(String tooltip);

    /**
     * Returns the node's width
     */
    public double getWidth();

    /**
     * Sets the node's width
     */
    public void setWidth(double width);
    

}