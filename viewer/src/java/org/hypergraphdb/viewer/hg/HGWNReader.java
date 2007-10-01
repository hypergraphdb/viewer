package org.hypergraphdb.viewer.hg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.query.HGQueryCondition;
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
	public void read(HGHandle handle, int depth, HGAtomPredicate cond) throws IOException
	{
		Set<Integer> nodes1 = new HashSet<Integer>();
		Set<Integer> edges1 = new HashSet<Integer>();
		addNode(hypergraph, handle, depth, cond, nodes1, edges1);
		nodes = new int[nodes1.size()];
		int i = 0;
		for(Integer in: nodes1)
			nodes[i++] = in.intValue();
		edges = new int[edges1.size()];
		i = 0;
		for(Integer in: edges1)
			edges[i++] = in.intValue();
	}
	
//	 The node should be presented in HG, this method adds it to the View
	public static FNode addNode(HyperGraph hg, HGHandle handle, int level,
			HGAtomPredicate cond, Set<Integer> nodes, Set<Integer> edges)
	{
		FNode node = HGVKit.getHGVNode(handle, true);
		nodes.add(node.getRootGraphIndex());
		if (level > 0)
		{
			HGHandle[] h_links = hg.getIncidenceSet(handle);
			for (int i = 0; i < h_links.length; i++)
			{
				if(cond != null && !cond.satisfies(hg, h_links[i]))
						continue;
				FEdge edge = HGVKit.getHGVEdge(addNode(hg, h_links[i],
						level - 1, cond, nodes, edges), node, true);
				if(edge != null)  
				  edges.add(edge.getRootGraphIndex());
			}
		}
		Object obj = hg.get(handle);
		if (obj instanceof HGLink)
		{
			if (level < 1) return node;
			HGLink link = ((HGLink) obj);
			for (int i = 0; i < link.getArity(); i++)
			{
				FEdge edge = HGVKit.getHGVEdge(node, addNode(hg, link
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
