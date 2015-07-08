// $Id: Radial.java,v 1.1 2006/02/27 19:59:19 bizi Exp $
package org.hypergraphdb.viewer.layout; 

import java.util.*;
import java.awt.geom.*;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.layout.util.Coordinates;
import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.PNodeView;

/**
 * <br>
 * Radial Layout
 * </br>
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
    FNode center = null;
	double layerDistance = 10;
	HashMap<FNode, Coordinates> locations = new HashMap<FNode, Coordinates>();
	HashMap<FNode, Point2D> coords = new HashMap<FNode, Point2D>();
	HashMap<FNode, Integer> radialWidth = new HashMap<FNode, Integer>();
	HashSet<FNode> seen = new HashSet<FNode>();
	HashSet<FEdge> validEdges = new HashSet<FEdge>();

	GraphView view;
	/**
	 * ConstructorLink
	 */
	public Radial()
	{
		this(null);
	}
	/*
	 * 
	 */
	public Radial(FNode center)
	{
		this.center = center;
	}
	
	
	public String getName()
	{
		return "Radial";
	}

	private void clear()
	{
		center = null;
		locations.clear();
		coords.clear();
		radialWidth.clear();
		seen.clear();
		validEdges.clear();
	}

	 public void applyLayout (GraphView view)
	 {
	    this.view = view;
	    if (view == null) return;
	    clear();
		PNodeView center_view = view.getSelectedNodeView();
		done = false;
		for(PNodeView n : view.getNodeViews())
		{
			//if no center is specified, take the first view from the list
			if(center_view == null)	center_view = n;
			locations.put(n.getNode(), new Coordinates(n.getXPosition(), n.getYPosition()));
		}
		center = center_view.getNode();
		advancePositions();
		GEM.rescalePositions(view, 0.25, 0, locations);
	}

	private FNode getOpposite(FNode main, FEdge e)
	{
		if (e.getSource().equals(main)) 
		    return e.getTarget();
		return e.getSource();
	}

	private Collection<FEdge> getOutEdges(FNode node)
	{
		Set<FEdge> totalEdges = new HashSet<FEdge>();
		FEdge[] e = view.getAdjacentEdges(node, true, true);
		for (FEdge edge: e)
			totalEdges.add(edge);
		return totalEdges;
	}

	public Vector<FNode> getNextLayer(FNode center)
	{
		Vector<FNode> frontier = new Vector<FNode>();
		seen.add(center);
		for (FEdge e : getOutEdges(center))
		{
			FNode n = getOpposite(center, e);
			if (!n.equals(center) && (!seen.contains(n)))
			{
				validEdges.add(e);
				frontier.addElement(n);
				seen.add(n);
			}
		}
		return (frontier);
	}

	public void advancePositions()
	{
		if (done) return;
		Vector<FNode> front = null;
		front = getNextLayer(center);
		
		while (front.size() > 0)
		{
			Vector<FNode> nextLayer = new Vector<FNode>();
			for (int i = 0; i < front.size(); i++)
					nextLayer.addAll(getNextLayer(front.elementAt(i)));
		    front = nextLayer;
		}
		seen.clear();
		layerDistance = 10;
		for (PNodeView n : view.getNodeViews())
		    layerDistance = Math.max(layerDistance, 
					           (n.getWidth()+ n.getHeight()) * 4);
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
		Iterator<FEdge> neighbors = getOutEdges(center).iterator();
		while (neighbors.hasNext())
		{
			FEdge e = (FEdge) neighbors.next();
			if (!validEdges.contains(e))
				continue;
			FNode neighbor = getOpposite(center, e);
			Integer i = (radialWidth.get(neighbor));
			int neighborWidth = (i == null) ? 0 : i.intValue();
			alpha2 = alpha1 + (2 * Math.PI * neighborWidth / centerWidth);
			RadialSubTreeUndirected((FNode) center, neighbor, neighborWidth,
					rho, alpha1, alpha2, baseX);
			alpha1 = alpha2;
		}
		for (PNodeView n : view.getNodeViews())
		{
			if(n == null) continue;
			Point2D loc = (Point2D) coords.get(n.getNode());
			if (loc != null)
			{
				locations.put(n.getNode(), new Coordinates(loc.getX(), loc
						.getY()));
			} else
			{
				System.out.println("oops... ");
			}
		}
		done = true;
	}

	protected void RadialSubTreeUndirected(FNode forbiddenNeighbor, FNode node,
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
		for (FEdge e: getOutEdges(node))
		{
			if (!validEdges.contains(e))
				continue;
			FNode neighbor = getOpposite(node, e);
			if (!neighbor.equals(forbiddenNeighbor))
			{
				Integer i = radialWidth.get(neighbor);
				int neighborWidth = (i == null) ? 0 : i.intValue();
				if (neighborWidth == 0)
					System.out.println(neighbor);
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
	private int defineWidthProperty(FNode center, FNode enteringFrom)
	{
		//System.out.println("defineWidthProperty: " + center + ":" +  enteringFrom);
		if (radialWidth.containsKey(center))
		{
			System.out.println(center + " " + radialWidth.get(center));
			System.out.println("\treturning...\n");
			return ((Integer) radialWidth.get(center)).intValue();
		}
		//System.out.println("\trecursing\n");
		int width = 0;
		Iterator<FEdge> edges = getOutEdges(center).iterator();
		int validNeighbors = 0;
		while (edges.hasNext())
		{
			FEdge edge = edges.next();
			if (!validEdges.contains(edge))	continue;
			FNode goingTo = getOpposite(center, edge);
			if (enteringFrom != null && enteringFrom.equals(goingTo))
				continue;
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

	public double getX(FNode n) { 
		return getCoordinates(n).getX(); 
	}
	 
	public double getY(FNode n) { 
		return getCoordinates(n).getY();
	}
	
	public Coordinates getCoordinates(FNode v)
	{
		return locations.get(v);
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
