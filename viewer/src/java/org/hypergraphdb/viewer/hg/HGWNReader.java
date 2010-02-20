package org.hypergraphdb.viewer.hg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.util.HGUtils;
import org.hypergraphdb.viewer.*;



/**
 */
public class HGWNReader 
{
	private HyperGraph hypergraph;
	/**
	 * The DB to be loaded
	 */
	protected File db;
	Set<FNode> nodes = new HashSet<FNode>();
    Set<FEdge> edges = new HashSet<FEdge>();
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
		nodes.clear();
		edges.clear();
		addNode(handle, depth, cond);
	}
	
	public void read(HGHandle handle, int depth, HGALGenerator generator)
	{
	    nodes.clear();
        edges.clear();
		LinkedList<HGHandle> remaining = new LinkedList<HGHandle>();
		depth--;
		FNode node = HGVKit.getHGVNode(handle, true);
		nodes.add(node);		
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
					nodes.add(linkNode);
					FEdge edge = HGVKit.getHGVEdge(node, linkNode, true);
					edges.add(edge);
				}
				FNode an = HGVKit.getHGVNode(a, true);				
				nodes.add(an);
				FEdge edge = HGVKit.getHGVEdge(linkNode, an, true);
				edges.add(edge);
				if (depth > 0)
					remaining.add(a);
			}
			i.close();
			if (depth > 0)
			{
				depth--;
			}
		}
		
		System.out.println("focus0: " + nodes.size() + ":" + edges.size());
	}
	
	
//	 The node should be presented in HG, this method adds it to the View
	private FNode addNode(HGHandle handle, int level, HGAtomPredicate cond)
	{
		FNode node = HGVKit.getHGVNode(handle, true);
		nodes.add(node);
		if (level > 0)
		{
			IncidenceSet h_links = hypergraph.getIncidenceSet(handle);
			for (HGHandle linkHandle : h_links)
			{
				if(cond != null && !cond.satisfies(hypergraph, linkHandle))
						continue;
				FEdge edge = HGVKit.getHGVEdge(addNode(linkHandle,
						level - 1, cond), node, true);
				if(edge != null)  
				  edges.add(edge);
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
						.getTargetAt(i), level - 1, cond), true);
				edges.add(edge);
			}
		}
		return node;
	}

	public Collection<FNode> getNodes()
    {
        return nodes;
    }

    public Collection<FEdge> getEdges()
    {
        return edges;
    }
}
