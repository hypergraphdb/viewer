// $Id: Radial.java,v 1.1 2006/02/27 19:59:19 bizi Exp $
package org.hypergraphdb.viewer.layout; 

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.layout.util.Coordinates;

/**
 * The layout computePositions method follows the algorithm as given by Eades in
 * his paper "Drawing Free Trees", Bulletin of the Institute for Combinatorics
 * and its Applications, vol. 5, 10-36, 1992.
 * 
 * Note: as described by Eades, the algorithm does not allow variable node size.
 * 
 * @author Hacked by Eytan Adar for Guess
 */
public class Radial implements Layout
{
	protected double layerDistance = 10;
	Node center = null;
	HashSet ve = null;
	HashMap<Node, Coordinates> locations = new HashMap<Node, Coordinates>();
	HashMap<Node, Point2D> coords = new HashMap<Node, Point2D>();
	HashMap<Node, Integer> radialWidth = new HashMap<Node, Integer>();
	HashSet<Node> seen = new HashSet<Node>();
	HashSet<Edge> validEdges = new HashSet<Edge>();

	/**
	 * ConstructorLink
	 */
	public Radial()
	{
		this(null, null);
	}
	/*
	 * 
	 */
	public Radial(Node center, HashSet ve)
	{
		this.center = center;
		this.ve = ve;
	}

	public Radial(Node center)
	{
		this(center, null);
	}
	
	public String getName()
	{
		return "Radial";
	}

	private void clear()
	{
		center = null;
		ve = null;
		locations.clear();
		coords.clear();
		radialWidth.clear();
		seen.clear();
		validEdges.clear();
	}

	// this one takes into account a predefined set of edges
	public Vector<Node> getNextLayerEdgesPredef(Node center)
	{
		Vector<Node> frontier = new Vector<Node>();
		Iterator it = getOutEdges(center).iterator();
		seen.add(center);
		while (it.hasNext())
		{
			EdgeView e = (EdgeView) it.next();
			if (validEdges.contains(e))
			{
				Node n = (Node) getOpposite(center, e.getEdge());
				if ((n != center) && (!seen.contains(n)))
				{
					// e.getRep().set("width",new Double(5));
					frontier.addElement(n);
					seen.add(n);
				}
			}
		}
		return (frontier);
	}

	public void applyLayout()
	{
		HGVNetworkView view = HGVKit.getCurrentView();
		clear();
		int[] node_indicies = view.getSelectedNodeIndices();
		NodeView center_view = null;
		if(node_indicies.length > 0)
			center_view = view.getNodeView(node_indicies[0]);
		done = false;
		Iterator it = view.getNodeViewsIterator();
		while (it.hasNext())
		{
			NodeView n = (NodeView) it.next();
			//if no center is specified, take the first view from the list
			if(center_view == null)
				center_view = n;
			if(n!= null)
			 locations.put(n.getNode(), new Coordinates(n.getXPosition(), n
					.getYPosition()));
		}
		center = center_view.getNode();
		advancePositions();
		double scale = 1;
		GEM.rescalePositions(scale, 0, locations);
	}

	private Node getOpposite(Node center, giny.model.Edge e)
	{
		if (e.getSource().equals(center)) return e.getTarget();
		return e.getSource();
	}

	private Collection<Edge> getOutEdges(Node node)
	{
		HGVNetwork net = HGVKit.getCurrentNetwork();
		Set<Edge> totalEdges = new HashSet<Edge>();
		int[] e = net.getAdjacentEdgeIndicesArray(
				node.getRootGraphIndex(), true, true, true);
		for (int i = 0; i < e.length; i++)
			totalEdges.add(net.getEdge(e[i]));
		return totalEdges;
	}

	public Vector<Node> getNextLayer(Node center)
	{
		Vector<Node> frontier = new Vector<Node>();
		Iterator<Edge> it = getOutEdges(center).iterator();
		seen.add(center);
		while (it.hasNext())
		{
			Edge e = (Edge) it.next();
			Node n = (Node) getOpposite(center, e);
			if ((n != center) && (!seen.contains(n)))
			{
				validEdges.add(e);
				// e.getRep().set("width",new Double(5));
				frontier.addElement(n);
				seen.add(n);
			}
		}
		return (frontier);
	}

	public void advancePositions()
	{
		if (done) return;
		boolean predef = false;
		if (ve != null)
		{
			predef = true;
			validEdges = ve;
		}
		Vector<Node> front = null;
		if (!predef)
		{
			front = getNextLayer(center);
		} else
		{
			front = getNextLayerEdgesPredef(center);
		}
		while (front.size() > 0)
		{
			Vector<Node> nextLayer = new Vector<Node>();
			for (int i = 0; i < front.size(); i++)
			{
				if (!predef)
				{
					nextLayer.addAll(getNextLayer(front.elementAt(i)));
				} else
				{
					nextLayer.addAll(getNextLayerEdgesPredef(
							front.elementAt(i)));
				}
			}
			front = nextLayer;
		}
		seen.clear();
		// Iterator it = tree.getNodes().iterator();
		Iterator it = HGVKit.getCurrentView().getNodeViewsIterator();
		while (it.hasNext())
		{
			NodeView n = (NodeView) it.next();
			layerDistance = Math.max(Math.max(layerDistance, n.getWidth() * 4),
					n.getHeight() * 4);
		}
		double baseX = 0.0;
		// System.out.println("\tsetting width prop...");
		defineWidthProperty(center, null);
		// System.out.println("\tsetting laying out...");
		double rho = 0.0;
		double alpha1 = 0.0;
		double alpha2 = 2 * Math.PI;
		Point2D nodeCoord = polarToCartesian(rho, (alpha1 + alpha2) / 2, baseX);
		coords.put(center, nodeCoord);
		int centerWidth = (radialWidth.get(center)).intValue();
		rho += layerDistance;
		Iterator<Edge> neighbors = getOutEdges(center).iterator();
		while (neighbors.hasNext())
		{
			Edge e = (Edge) neighbors.next();
			if (!validEdges.contains(e))
			{
				continue;
			}
			Node neighbor = getOpposite(center, e);
			Integer i = (radialWidth.get(neighbor));
			int neighborWidth = (i == null) ? 0 : i.intValue();
			alpha2 = alpha1 + (2 * Math.PI * neighborWidth / centerWidth);
			RadialSubTreeUndirected((Node) center, neighbor, neighborWidth,
					rho, alpha1, alpha2, baseX);
			alpha1 = alpha2;
		}
		// it = tree.getNodes().iterator();
		it = HGVKit.getCurrentView().getNodeViewsIterator();
		while (it.hasNext())
		{
			NodeView n = (NodeView) it.next();
			Point2D loc = (Point2D) coords.get(n.getNode());
			if (loc != null)
			{
				locations.put(n.getNode(), new Coordinates(loc.getX(), loc
						.getY()));
			} else
			{
				//System.out.println("oops... ");
			}
		}
		done = true;
	}

	protected void RadialSubTreeUndirected(Node forbiddenNeighbor, Node node,
			double width, double rho, double alpha1, double alpha2, double baseX)
	{
		Point2D nodeCoord = polarToCartesian(rho, (alpha1 + alpha2) / 2, baseX);
		coords.put(node, nodeCoord);
		double tau = 2 * Math.acos(rho / (rho + layerDistance));
		double alpha = 0.0;
		double s = 0.0;
		if (tau < (alpha2 - alpha1))
		{
			alpha = (alpha1 + alpha2 - tau) / 2.0;
			s = tau / width;
			//System.out.println("1: " + node + " " + s);
		} else
		{
			alpha = alpha1;
			s = (alpha2 - alpha1) / width;
			// System.out.println("2: " + node + " " + s);
		}
		Iterator<Edge> neighbors = getOutEdges(node).iterator();
		while (neighbors.hasNext())
		{
			Edge e = neighbors.next();
			if (!validEdges.contains(e))
			{
				continue;
			}
			Node neighbor = getOpposite(node, e);
			if (neighbor != forbiddenNeighbor)
			{
				Integer i = radialWidth.get(neighbor);
				int neighborWidth = (i == null) ? 0 : i.intValue();
				if (neighborWidth == 0)
				{
					System.out.println(neighbor);
				}
				RadialSubTreeUndirected(node, neighbor, neighborWidth, rho
						+ layerDistance, alpha, alpha += s * neighborWidth,
						baseX);
			}
		}
	}

	private Point2D polarToCartesian(double rho, double alpha,
			double Xtranslation)
	{
		// System.out.println(rho + " " + alpha + Xtranslation);
		return new Point2D.Double(rho * Math.cos(alpha) + Xtranslation, rho
				* Math.sin(alpha));
	}

	/**
	 * This method is actually called only once with the center of the graph as
	 * a parameter.
	 */
	private int defineWidthProperty(Node center, Node enteringFrom)
	{
		//System.out.println("at: " + center);
		if (radialWidth.containsKey(center))
		{
			System.out.println(center + " " + radialWidth.get(center));
			System.out.println("\treturning...\n");
			return ((Integer) radialWidth.get(center)).intValue();
		}
		//System.out.println("\trecursing\n");
		int width = 0;
		Iterator<Edge> edges = getOutEdges(center).iterator();
		int validNeighbors = 0;
		while (edges.hasNext())
		{
			Edge edge = edges.next();
			if (!validEdges.contains(edge))
			{
				continue;
			}
			Node goingTo = getOpposite(center, edge);
			if (enteringFrom == goingTo)
			{
				continue;
			}
			validNeighbors++;
			width += defineWidthProperty(goingTo, center);
		}
		if (validNeighbors != 0)
		{
			radialWidth.put(center, new Integer(width));
			return (width);
		} else
		{
			radialWidth.put(center, new Integer(1));
			return (1);
		}
	}

	public double getX(Node n) { 
		return getCoordinates(n).getX(); 
	}
	 
	public double getY(Node n) { 
		return getCoordinates(n).getY();
	}
	
	public Coordinates getCoordinates(Node v)
	{
		return ((Coordinates) locations.get(v));
	}
	public boolean done = false;

	public boolean incrementsAreDone()
	{
		return (done);
	}

	public void initialize_local()
	{
	}

	public boolean isIncremental()
	{
		return (false);
	}
}
