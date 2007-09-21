package org.hypergraphdb.viewer.foo;

import java.util.Iterator;
import com.nerius.math.xform.*;
import cytoscape.util.intr.IntEnumerator;
import fing.model.FEdge;
import fing.model.FNode;

public final class RotationLayouter
{
	private final MutablePolyEdgeGraphLayout m_graph;
	private final Translation3D m_translationToOrig;
	private final Translation3D m_translationFromOrig;
	private final double m_pointBuff[] = new double[3];

	public RotationLayouter(MutablePolyEdgeGraphLayout graph)
	{
		m_graph = graph;
		double xMin = 1.7976931348623157E+308D;
		double xMax = 4.9406564584124654E-324D;
		double yMin = 1.7976931348623157E+308D;
		double yMax = 4.9406564584124654E-324D;
		for (Iterator<FEdge> it = m_graph.edgesIterator(); it.hasNext();)
		{
			int edge = it.next().getRootGraphIndex();
			if (m_graph.isMovableNode(m_graph.edgeSource(edge))
					&& m_graph.isMovableNode(m_graph.edgeTarget(edge)))
			{
				int numAnchors = m_graph.getNumAnchors(edge);
				int j = 0;
				while (j < numAnchors)
				{
					double anchXPosition = m_graph.getAnchorPosition(edge, j,
							true);
					double anchYPosition = m_graph.getAnchorPosition(edge, j,
							false);
					xMin = Math.min(xMin, anchXPosition);
					xMax = Math.max(xMax, anchXPosition);
					yMin = Math.min(yMin, anchYPosition);
					yMax = Math.max(yMax, anchYPosition);
					j++;
				}
			}
		}
		for (Iterator<FNode> it = m_graph.nodesIterator(); it.hasNext();)
		{
			int node = it.next().getRootGraphIndex();
			if (m_graph.isMovableNode(node))
			{
				double nodeXPosition = m_graph.getNodePosition(node, true);
				double nodeYPosition = m_graph.getNodePosition(node, false);
				xMin = Math.min(xMin, nodeXPosition);
				xMax = Math.max(xMax, nodeXPosition);
				yMin = Math.min(yMin, nodeYPosition);
				yMax = Math.max(yMax, nodeYPosition);
			}
		}
		if (xMax < 0.0D)
		{
			m_translationToOrig = null;
			m_translationFromOrig = null;
		} else
		{
			double xRectCenter = (xMin + xMax) / 2D;
			double yRectCenter = (yMin + yMax) / 2D;
			double rectWidth = xMax - xMin;
			double rectHeight = yMax - yMin;
			double hypotenuse = 0.5D * Math.sqrt(rectWidth * rectWidth
					+ rectHeight * rectHeight);
//			System.out.println(xRectCenter + ":" + hypotenuse + ":"
//					+ yRectCenter + ":" + m_graph.getMaxWidth() + ":"
//					+ m_graph.getMaxHeight());
			if (xRectCenter - hypotenuse < 0.0D
					|| xRectCenter + hypotenuse > m_graph.getMaxWidth()
					|| yRectCenter - hypotenuse < 0.0D
					|| yRectCenter + hypotenuse > m_graph.getMaxHeight())
				throw new IllegalStateException(
						"minimum bounding rectangle of  movable nodes and edge anchors not free to rotate within MutableGraphLayout boundaries");
			m_translationToOrig = new Translation3D(-xRectCenter, -yRectCenter,
					0.0D);
			m_translationFromOrig = new Translation3D(xRectCenter, yRectCenter,
					0.0D);
		}
	}

	public void rotateGraph(double radians)
	{
		if (m_translationToOrig == null) return;
		AffineTransform3D xform = m_translationToOrig
				.concatenatePost((new AxisRotation3D((byte) 4, radians))
						.concatenatePost(m_translationFromOrig));
		for (Iterator<FNode> it = m_graph.nodesIterator(); it.hasNext();)
		{
			int node = it.next().getRootGraphIndex();
			if (m_graph.isMovableNode(node))
			{
				m_pointBuff[0] = m_graph.getNodePosition(node, true);
				m_pointBuff[1] = m_graph.getNodePosition(node, false);
				m_pointBuff[2] = 0.0D;
				xform.transformArr(m_pointBuff);
				m_graph.setNodePosition(node, m_pointBuff[0], m_pointBuff[1]);
			}
		}
		for (Iterator<FEdge> it = m_graph.edgesIterator(); it.hasNext();)
		{
			int edge = it.next().getRootGraphIndex();
			if (m_graph.isMovableNode(m_graph.edgeSource(edge))
					&& m_graph.isMovableNode(m_graph.edgeTarget(edge)))
			{
				int numAnchors = m_graph.getNumAnchors(edge);
				int j = 0;
				while (j < numAnchors)
				{
					m_pointBuff[0] = m_graph.getAnchorPosition(edge, j, true);
					m_pointBuff[1] = m_graph.getAnchorPosition(edge, j, false);
					m_pointBuff[2] = 0.0D;
					xform.transformArr(m_pointBuff);
					m_graph.setAnchorPosition(edge, j, m_pointBuff[0],
							m_pointBuff[1]);
					j++;
				}
			}
		}
	}
}
