package org.hypergraphdb.viewer.painter;

import giny.view.EdgeView;
import giny.view.Label;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.viewer.HGVEdge;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.visual.Arrow;
import org.hypergraphdb.viewer.visual.LineType;

public class DefaultEdgePainter implements PaintEdgeInfo, EdgePainter
{
	private Color color = Color.BLACK;
	private LineType lineType = LineType.LINE_1;
	private Arrow srcArrow = Arrow.NONE;
	private Arrow tgtArrow = Arrow.NONE;
	private String label = "";
	private Color labelColor = Color.WHITE;
	private String tooltip = "";
	private Font font = new Font("Default", Font.PLAIN, 12);

	private HGPersistentHandle targetNodeTypeHandle;
	
	
	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getColor()
	 */
	public Color getColor()
	{
		return color;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setColor(java.awt.Color)
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getFont()
	 */
	public Font getFont()
	{
		return font;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setFont(java.awt.Font)
	 */
	public void setFont(Font font)
	{
		this.font = font;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getLabel()
	 */
	public String getLabel()
	{
		return label;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setLabel(java.lang.String)
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getLabelColor()
	 */
	public Color getLabelColor()
	{
		return labelColor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setLabelColor(java.awt.Color)
	 */
	public void setLabelColor(Color labelColor)
	{
		this.labelColor = labelColor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getLineType()
	 */
	public LineType getLineType()
	{
		return lineType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setLineType(org.hypergraphdb.viewer.visual.LineType)
	 */
	public void setLineType(LineType lineType)
	{
		this.lineType = lineType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getSrcArrow()
	 */
	public Arrow getSrcArrow()
	{
		return srcArrow;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setSrcArrow(org.hypergraphdb.viewer.visual.Arrow)
	 */
	public void setSrcArrow(Arrow srcArrow)
	{
		this.srcArrow = srcArrow;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getTgtArrow()
	 */
	public Arrow getTgtArrow()
	{
		return tgtArrow;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setTgtArrow(org.hypergraphdb.viewer.visual.Arrow)
	 */
	public void setTgtArrow(Arrow tgtArrow)
	{
		this.tgtArrow = tgtArrow;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#getTooltip()
	 */
	public String getTooltip()
	{
		return tooltip;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.PaintEdgeInfo#setTooltip(java.lang.String)
	 */
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	public void paintEdge(EdgeView edgeView, HGVNetworkView network_view)
	{
		Paint existingUnselectedPaint = edgeView.getUnselectedPaint();
		Paint newUnselectedPaint = this.getColor();
		if (!newUnselectedPaint.equals(existingUnselectedPaint))
		{
			edgeView.setUnselectedPaint(newUnselectedPaint);
		}
		Stroke existingStroke = edgeView.getStroke();
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
		Label label = edgeView.getLabel();
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
	
	public void setTargetNodeTypeHandle(HGPersistentHandle h){
		targetNodeTypeHandle = h;
	}
	public HGPersistentHandle getTargetNodeTypeHandle(){
		return targetNodeTypeHandle;
	}
}
