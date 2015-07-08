package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;

import org.hypergraphdb.viewer.visual.Arrow;
import org.hypergraphdb.viewer.visual.LineType;

/**
 * This interface describes all visual properties used for drawing an edge
 */
public interface PaintEdgeInfo
{
    /**
     * Returns edge's color 
     */
    public Color getColor();
    
    /**
     * Sets edge's color 
     */
    public void setColor(Color color);
    
    /**
     * Returns edge's label font 
     */
    public Font getFont();

    /**
     * Sets edge's label font 
     */
    public void setFont(Font font);

    /**
     * Returns edge's label  
     */
    public String getLabel();

    /**
     * Sets edge's label  
     */
    public void setLabel(String label);

    /**
     * Returns edge's label color  
     */
    public Color getLabelColor();

    /**
     * Sets edge's label color  
     */
    public void setLabelColor(Color labelColor);

    /**
     * Returns edge's <code>LineType</code>  
     */
    public LineType getLineType();

    /**
     * Sets edge's <code>LineType</code>  
     */
    public void setLineType(LineType lineType);

    /**
     * Returns edge's source <code>Arrow</code>  
     */
    public Arrow getSrcArrow();
    /**
     * Sets edge's source <code>Arrow</code>  
     */
    public void setSrcArrow(Arrow srcArrow);

    /**
     * Returns edge's target <code>Arrow</code>  
     */
    public Arrow getTgtArrow();

    /**
     * Sets edge's target <code>Arrow</code>  
     */
    public void setTgtArrow(Arrow tgtArrow);

    /**
     * Returns edge's tooltip  
     */
    public String getTooltip();

    /**
     * Sets edge's tooltip  
     */
    public void setTooltip(String tooltip);
}