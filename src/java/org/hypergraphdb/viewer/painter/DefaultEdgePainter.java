package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.util.PLabel;
import org.hypergraphdb.viewer.visual.Arrow;
import org.hypergraphdb.viewer.visual.LineType;

/**
 * Default implementation of the EdgePainter and PaintEdgeInfo interfaces
 */
public class DefaultEdgePainter implements PaintEdgeInfo, EdgePainter
{
    private Color color = Color.BLACK;
    private LineType lineType = LineType.LINE_1;
    private Arrow srcArrow = Arrow.NONE;
    private Arrow tgtArrow = Arrow.BLACK_ARROW;
    private String label = "";
    private Color labelColor = Color.WHITE;
    private String tooltip = "";
    private Font font = new Font("Default", Font.PLAIN, 12);

    protected PEdgeView edgeView;
    
    /**
     * Returns edge's color 
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Sets edge's color 
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Returns edge's label font 
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * Sets edge's label font 
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * Returns edge's label  
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets edge's label  
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Returns edge's label color  
     */
    public Color getLabelColor()
    {
        return labelColor;
    }

    /**
     * Sets edge's label color  
     */
    public void setLabelColor(Color labelColor)
    {
        this.labelColor = labelColor;
    }

    /**
     * Returns edge's <code>LineType</code>  
     */
    public LineType getLineType()
    {
        return lineType;
    }

    /**
     * Sets edge's <code>LineType</code>  
     */
    public void setLineType(LineType lineType)
    {
        this.lineType = lineType;
    }

    /**
     * Returns edge's source <code>Arrow</code>  
     */
    public Arrow getSrcArrow()
    {
        return srcArrow;
    }

    /**
     * Sets edge's source <code>Arrow</code>  
     */
    public void setSrcArrow(Arrow srcArrow)
    {
        this.srcArrow = srcArrow;
    }

    /**
     * Returns edge's target <code>Arrow</code>  
     */
    public Arrow getTgtArrow()
    {
        return tgtArrow;
    }

    /**
     * Sets edge's target <code>Arrow</code>  
     */
    public void setTgtArrow(Arrow tgtArrow)
    {
        this.tgtArrow = tgtArrow;
    }

    /**
     * Returns edge's tooltip  
     */
    public String getTooltip()
    {
        return tooltip;
    }

    /**
     * Sets edge's tooltip  
     */
    public void setTooltip(String tooltip)
    {
        this.tooltip = tooltip;
    }

    /* (non-Javadoc)
     * @see org.hypergraphdb.viewer.painter.EdgePainter#paintEdge(org.hypergraphdb.viewer.phoebe.PEdgeView)
     */
    public void paintEdge(PEdgeView edgeView)
    {
        this.edgeView = edgeView;
        Paint existingUnselectedPaint = edgeView.getUnselectedPaint();
        Paint newUnselectedPaint = this.getColor();
        if (!newUnselectedPaint.equals(existingUnselectedPaint))
        {
            edgeView.setUnselectedPaint(newUnselectedPaint);
        }
        Stroke existingStroke = edgeView.getStroke();
        // System.out.println("FEdge: " + edgeView + ":" + existingStroke);
        Stroke newStroke = getLineType().getStroke();
        if (!newStroke.equals(existingStroke))
        {
            edgeView.setStroke(newStroke);
        }
        int existingSourceEdge = edgeView.getSourceEdgeEnd();
        int newSourceEdge = getSrcArrow().getGinyArrow();
        if (newSourceEdge != existingSourceEdge)
        {
            edgeView.setSourceEdgeEnd(newSourceEdge);
        }
        int existingTargetEdge = edgeView.getTargetEdgeEnd();
        int newTargetEdge = getTgtArrow().getGinyArrow();
        if (newTargetEdge != existingTargetEdge)
        {
            edgeView.setTargetEdgeEnd(newTargetEdge);
        }
        PLabel label = edgeView.getLabel();

        String existingText = label.getText();
        String newText = getLabel();
        if (!newText.equals(existingText))
        {
            label.setText(newText);
        }
        Font existingFont = label.getFont();
        Font newFont = getFont();
        if (!newFont.equals(existingFont))
        {
            label.setFont(newFont);
        }
        edgeView.setToolTip(getTooltip());
    }

    /**
     * Shortcut method to get underlying HyperGraphDB
     */
    protected final HyperGraph getHG()
    {
        if (edgeView == null) return null;
        return edgeView.getGraphView().getHyperGraph();
    }
}
