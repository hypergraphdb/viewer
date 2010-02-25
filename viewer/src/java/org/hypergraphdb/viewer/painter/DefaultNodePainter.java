package org.hypergraphdb.viewer.painter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.visual.LineType;
import phoebe.PNodeView;
import phoebe.util.PLabel;

public class DefaultNodePainter implements PaintNodeInfo, NodePainter
{
	public static final String NOT_EDITABLE = "NOT EDITABLE";
	private Color color = Color.lightGray; 
	private Color borderColor = Color.BLACK;
	private LineType lineType = LineType.LINE_1; 
	private byte shape = Shape.ROUND_RECT;//RECT;//ELLIPSE;
	private String label = null;
	private Color labelColor = Color.BLACK; 
	private String tooltip = null; 
	private Font font = new Font("Default", Font.PLAIN , 12);
	private double height = 30.0;
	private double width = 30.0;
	
	protected PNodeView nodeView;
	protected HGVNetworkView network_view;

	
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getBorderColor()
	 */
	public Color getBorderColor()
	{
		return borderColor;
	}
	
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}
	
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getColor()
	 */
	public Color getColor()
	{
		return color;
	}
	public void setColor(Color color)
	{
		this.color = color;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getFont()
	 */
	public Font getFont()
	{
		return font;
	}
	public void setFont(Font font)
	{
		this.font = font;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getHeight()
	 */
	public double getHeight()
	{
		return height;
	}
	public void setHeight(double height)
	{
		this.height = height;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getLabel()
	 */
	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getLabelColor()
	 */
	public Color getLabelColor()
	{
		return labelColor;
	}
	public void setLabelColor(Color labelColor)
	{
		this.labelColor = labelColor;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getLineType()
	 */
	public LineType getLineType()
	{
		return lineType;
	}
	public void setLineType(LineType lineType)
	{
		this.lineType = lineType;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getShape()
	 */
	public byte getShape()
	{
		return shape;
	}
	public void setShape(byte shape)
	{
		this.shape = shape;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getTooltip()
	 */
	public String getTooltip()
	{
		return tooltip;
	}
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}
	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintNodeInfo#getWidth()
	 */
	public double getWidth()
	{
		return width;
	}
	public void setWidth(double width)
	{
		this.width = width;
	}
	
	public void paintNode(PNodeView nodeView, HGVNetworkView network_view){
		this.network_view = network_view;
		this.nodeView = nodeView;
		network_view.updateEdges = false;
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
		String existingLabel = label.getText();
		String newLabel = this.getLabel();
		if (newLabel != null && !newLabel.equals(existingLabel))
		{
			change_made = true;
			label.setText(newLabel);
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
		if(getTooltip() != null)
		   nodeView.setToolTip(getTooltip());
		
		network_view.updateEdges = true;
        if (change_made) 
        {
            nodeView.invalidatePaint(); //.setNodePosition(false );
            nodeView.invalidateFullBounds();
        }
       
	}
	
	protected final FNode getNode(){
		if(nodeView == null) return null;
		return  nodeView.getNode();
    }
	
	protected final HyperGraph getHG(){
		if(network_view == null)return null;
		return network_view.getHyperGraph();
	}
}
