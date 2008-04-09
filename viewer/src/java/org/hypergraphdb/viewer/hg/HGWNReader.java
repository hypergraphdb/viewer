package org.hypergraphdb.viewer.hg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.util.HGUtils;
import org.hypergraphdb.viewer.*;
import fing.model.FEdge;
import fing.model.FNode;


/**
 */
public class HGWNReader 
{
	private HyperGraph hypergraph;
	/**
	 * The DB to be loaded
	 */
	protected File db;
	int[] nodes;
	int[] edges;
	private boolean showLinksAsNodes = true;
	
	public HGWNReader(File db)
	{
		this.db = db;
		hypergraph = new HyperGraph(db.getAbsolutePath());
	}
	
	public HGWNReader(HyperGraph hg)
	{
		hypergraph = hg;
	}

	public HyperGraph getHyperGraph()
	{
		return hypergraph;
	}

	// ----------------------------------------------------------------------------------------
	public void read(HGHandle handle, int depth, HGAtomPredicate cond)
	{
		Set<Integer> nodes1 = new HashSet<Integer>();
		Set<Integer> edges1 = new HashSet<Integer>();
		addNode(handle, depth, cond, nodes1, edges1);
		nodes = new int[nodes1.size()];
		int i = 0;
		for(Integer in: nodes1)
			nodes[i++] = in.intValue();
		edges = new int[edges1.size()];
		i = 0;
		for(Integer in: edges1)
			edges[i++] = in.intValue();
	}
	
	public void read(HGHandle handle, int depth, HGALGenerator generator)
	{
		Set<Integer> nodes = new HashSet<Integer>();
		Set<Integer> edges = new HashSet<Integer>();
		LinkedList<HGHandle> remaining = new LinkedList<HGHandle>();
		depth--;
		FNode node = HGVKit.getHGVNode(handle, true);
		nodes.add(node.getRootGraphIndex());		
		remaining.add(handle);		
		while (remaining.size() > 0)
		{
			HGHandle h = remaining.removeLast();
			node = HGVKit.getHGVNode(h, false);
			if (h == null)
			{
				depth++;
				continue;
			}
			HGSearchResult<HGHandle> i = generator.generate(h);
			if (depth > 0)
				remaining.add(null);
			HGHandle currLink = null;
			FNode linkNode = node;
			while (i.hasNext())
			{
				HGHandle a = i.next();	
				
				if (showLinksAsNodes && !HGUtils.eq(generator.getCurrentLink(), currLink))
				{
					currLink = generator.getCurrentLink();
					linkNode = HGVKit.getHGVNode(currLink, true);
					nodes.add(linkNode.getRootGraphIndex());
					FEdge edge = HGVKit.getHGVEdge(node, linkNode, true);
					edges.add(edge.getRootGraphIndex());
				}
				FNode an = HGVKit.getHGVNode(a, true);				
				nodes.add(an.getRootGraphIndex());
				FEdge edge = HGVKit.getHGVEdge(linkNode, an, true);
				edges.add(edge.getRootGraphIndex());
				if (depth > 0)
					remaining.add(a);
			}
			i.close();
			if (depth > 0)
			{
				depth--;
			}
		}
		this.nodes = new int[nodes.size()];
		int i = 0;
		for(Integer in: nodes)
			this.nodes[i++] = in.intValue();
		this.edges = new int[edges.size()];
		i = 0;
		for(Integer in: edges)
			this.edges[i++] = in.intValue();	
		System.out.println("focus0: " + this.nodes.length + ":" + this.edges.length);
	}
	
	
//	 The node should be presented in HG, this method adds it to the View
	private FNode addNode(HGHandle handle, int level,
			HGAtomPredicate cond, Set<Integer> nodes, Set<Integer> edges)
	{
		FNode node = HGVKit.getHGVNode(handle, true);
		nodes.add(node.getRootGraphIndex());
		if (level > 0)
		{
			HGHandle[] h_links = hypergraph.getIncidenceSet(handle);
			for (int i = 0; i < h_links.length; i++)
			{
				if(cond != null && !cond.satisfies(hypergraph, h_links[i]))
						continue;
				FEdge edge = HGVKit.getHGVEdge(addNode(h_links[i],
						level - 1, cond, nodes, edges), node, true);
				if(edge != null)  
				  edges.add(edge.getRootGraphIndex());
			}
		}
		Object obj = hypergraph.get(handle);
		if (obj instanceof HGLink)
		{
			if (level < 1) return node;
			HGLink link = ((HGLink) obj);
			for (int i = 0; i < link.getArity(); i++)
			{
				FEdge edge = HGVKit.getHGVEdge(node, addNode(link
						.getTargetAt(i), level - 1, cond, nodes, edges), true);
				edges.add(edge.getRootGraphIndex());
			}
		}
		return node;
	}

	public int[] getNodeIndicesArray()
	{
		return nodes;
	}

	public int[] getEdgeIndicesArray()
	{
		return edges;
	}
}
