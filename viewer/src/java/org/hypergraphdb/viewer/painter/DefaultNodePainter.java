package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.visual.LineType;
import org.hypergraphdb.viewer.phoebe.PNodeView;
import org.hypergraphdb.viewer.phoebe.util.PLabel;

/**
 * Default implementation of the NodePainter and PaintNodeInfo interfaces
 */
public class DefaultNodePainter implements PaintNodeInfo, NodePainter
{
    public static final String NOT_EDITABLE = "NOT EDITABLE";
    private Color color = Color.lightGray;
    private Color borderColor = Color.BLACK;
    private LineType lineType = LineType.LINE_1;
    private byte shape = Shape.ROUND_RECT;
    private String label = null;
    private Color labelColor = Color.BLACK;
    private String tooltip = null;
    private Font font = new Font("Default", Font.PLAIN, 12);
    private double height = 30.0;
    private double width = 30.0;

    protected PNodeView nodeView;
   
    /**
     * Returns the color used to paint node's border
     */
    public Color getBorderColor()
    {
        return borderColor;
    }

    /**
     * Sets the color used to paint node's border
     */
    public void setBorderColor(Color borderColor)
    {
        this.borderColor = borderColor;
    }

    /**
     * Returns the color used to paint node
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Sets the color used to paint node
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Returns the font used to paint node's label
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * Sets the font used to paint node's label
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * Returns the node's height
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * Sets the node's height
     */
    public void setHeight(double height)
    {
        this.height = height;
    }

    /**
     * Returns the node's label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets the node's label
     */
    public void setLabel(String label)
    {
        this.label = label;
    }
 
     /**
     * Returns the node's label color
     */
    public Color getLabelColor()
    {
        return labelColor;
    }

    /**
     * Sets the node's label color
     */
    public void setLabelColor(Color labelColor)
    {
        this.labelColor = labelColor;
    }

    /**
     * Returns the LineType used for painting the node's border
     */
    public LineType getLineType()
    {
        return lineType;
    }

    /**
     * Sets the LineType used for painting the node's border
     */
    public void setLineType(LineType lineType)
    {
        this.lineType = lineType;
    }

    /**
     * Returns the node's shape type
     */
    public byte getShape()
    {
        return shape;
    }

    /**
     * Sets the node's shape type
     * Supported values are: org.hypergraphdb.viewer.painter.Shape.
     * RECT, ROUND_RECT, RECT_3D,TRAPEZOID, TRAPEZOID_2,
     * TRIANGLE, PARALLELOGRAM, DIAMOND, ELLIPSE, HEXAGON, OCTAGON 
     */
    public void setShape(byte shape)
    {
        this.shape = shape;
    }

    /**
     * Returns the node's tooltip text
     */
    public String getTooltip()
    {
        return tooltip;
    }


    /**
     * Sets the node's tooltip text
     */
    public void setTooltip(String tooltip)
    {
        this.tooltip = tooltip;
    }

    /**
     * Returns the node's width
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * Sets the node's width
     */
    public void setWidth(double width)
    {
        this.width = width;
    }

    
    /* (non-Javadoc)
     * @see org.hypergraphdb.viewer.painter.NodePainter#paintNode(org.hypergraphdb.viewer.phoebe.PNodeView)
     */
    public void paintNode(PNodeView nodeView)
    {
        GraphView graphView = nodeView.getGraphView();
        this.nodeView = nodeView;
        graphView.updateEdges = false;
        boolean change_made = false;
        Paint existingUnselectedColor = nodeView.getUnselectedPaint();
        Paint newUnselectedColor = getColor();
        if (!newUnselectedColor.equals(existingUnselectedColor))
        {
            change_made = true;
            nodeView.setUnselectedPaint(newUnselectedColor);
        }

        Paint existingBorderPaint = nodeView.getBorderPaint();
        Paint newBorderPaint = getBorderColor();
        if (!newBorderPaint.equals(existingBorderPaint))
        {
            change_made = true;
            nodeView.setBorderPaint(newBorderPaint);
        }
        Stroke existingBorderType = nodeView.getBorder();
        Stroke newBorderType = getLineType().getStroke();
        if (!newBorderType.equals(existingBorderType))
        {
            change_made = true;
            nodeView.setBorder(newBorderType);
        }
        double existingHeight = nodeView.getHeight();
        double newHeight = this.getHeight();
        double difference = newHeight - existingHeight;
        if (Math.abs(difference) > .1)
        {
            change_made = true;
            nodeView.setHeight(newHeight);
        }
        double existingWidth = nodeView.getWidth();
        double newWidth = getWidth();
        difference = newWidth - existingWidth;
        if (Math.abs(difference) > .1)
        {
            change_made = true;
            nodeView.setWidth(newWidth);
        }
        int existingShape = nodeView.getShape();
        int newShape = Shape.getGinyShape(getShape());
        if (existingShape != newShape)
        {
            change_made = true;
            nodeView.setShape(newShape);
        }
        
        PLabel label = nodeView.getLabel();
        String newLabel = getLabel();
        //if label text is not set or "", then use the one
        //provided by default painter
        if (newLabel != null && newLabel.length() != 0)
        {
            String existingLabel = label.getText();
            if (newLabel != null && !newLabel.equals(existingLabel))
            {
                change_made = true;
                label.setText(newLabel);
            }
        }
        Font existingFont = label.getFont();
        Font newFont = this.getFont();
        if (!newFont.equals(existingFont))
        {
            change_made = true;
            label.setFont(newFont);
        }
       
        Paint existingTextColor = label.getTextPaint();
        Paint newTextColor = getLabelColor();
        if (!newTextColor.equals(existingTextColor))
        {
            change_made = true;
            label.setTextPaint(newTextColor);
        }
        if (getTooltip() != null &&
                getTooltip().length() != 0)
            nodeView.setToolTip(getTooltip());

        graphView.updateEdges = true;
        if (change_made)
        {
            nodeView.invalidatePaint(); 
            nodeView.invalidateFullBounds();
        }

    }

    /**
     * Shortcut method to get underlying HyperGraphDB
     */
    protected final HyperGraph getHG()
    {
        return (nodeView == null) ? null :
                nodeView.getGraphView().getHyperGraph();
    }
}
