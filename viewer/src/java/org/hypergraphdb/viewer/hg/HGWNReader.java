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
        //read0(handle, depth, generator);
        //if(true) return;
        
        //TODO: fix this
        LinkedList<HGHandle> remaining = new LinkedList<HGHandle>();
        depth--;
        FNode node = new FNode(handle);
        nodes.add(node);
        remaining.add(handle);
        while (remaining.size() > 0)
        {
            HGHandle h = remaining.removeLast();
            if (h == null)
            {
                depth++;
                continue;
            }
            node = new FNode(h);
            HGSearchResult<HGHandle> i = generator.generate(h);
            if (depth > 0) remaining.add(null);
            HGHandle currLink = null;
            FNode linkNode = node;
            while (i.hasNext())
            {
                if (!HGUtils.eq(generator.getCurrentLink(), currLink))
                {
                    currLink = generator.getCurrentLink();
                    linkNode = new FNode(currLink);
                    nodes.add(linkNode);
                    add_edge(linkNode, node);
                }
                HGHandle a = i.next();
                FNode an = new FNode(a);
                nodes.add(an);
                add_edge(linkNode, an);
                if (depth > 0) remaining.add(a);
            }
            i.close();
            if (depth > 0)
            {
                depth--;
            }
        }

        System.out.println("focus0: " + nodes.size() + ":" + edges.size());
    }
    
    private void add_edge(FNode source, FNode target)
    {
        FEdge edge = getFEdge(source, target);
        if(edge != null)
           edges.add(edge);
        else
        {
            edge = getFEdge(target, source);
            if(edge != null)
                edges.add(edge);
            else
                System.err.println("No such edge: " + source.getHandle() +
                        ":" + target.getHandle() + ":" + hypergraph.get(source.getHandle()) +
                        ":" +  hypergraph.get(target.getHandle()));
        }
    }
    
    public void read0(HGHandle handle, int depth, HGALGenerator generator)
    {
        if(depth < 0)  return;
        FNode node = new FNode(handle);
        nodes.add(node);
        Set<HGHandle> next_layer = new HashSet<HGHandle>();
        HGHandle currentLink = null;
        FNode link_node = null;
        HGSearchResult<HGHandle> i = generator.generate(handle);
        while (i.hasNext())
        {
            if(!generator.getCurrentLink().equals(currentLink))
            {
                currentLink = generator.getCurrentLink();
                link_node = new FNode(currentLink);
                nodes.add(link_node);
                add_edge(link_node, node);
            } 
           HGHandle a = i.next();
           next_layer.add(a);
           FNode an = new FNode(a);
           nodes.add(an);
           add_edge(link_node, an);
        }
        i.close();
        for(HGHandle h: next_layer)
            read0(h, depth -1, generator);
    }
    
    //check if such edge is possible and creates one if true
    //source node should be link, and 
    //the target node should be in the target set of the source link
    FEdge getFEdge(FNode source, FNode target)
    {
        Object o = hypergraph.get(source.getHandle());
        if(!(o instanceof HGLink)) return null;
//        HGLink link = (HGLink) o;
//        for(int i = 0; i < link.getArity(); i++)
//           if(link.getTargetAt(i).equals(target.getHandle()))
//              return new FEdge(source, target);
        if(!hypergraph.getIncidenceSet(target.getHandle()).contains(
               source.getHandle()))
           return null;
        return new FEdge(source, target);
    }

    // The node should be presented in HG, this method adds it and related edges
    // to arrays
    private FNode addNode(HGHandle handle, int level, HGAtomPredicate cond)
    {
        FNode node = new FNode(handle);
        nodes.add(node);
        if (level > 0)
        {
            IncidenceSet h_links = hypergraph.getIncidenceSet(handle);
            for (HGHandle linkHandle : h_links)
            {
                if (cond != null && !cond.satisfies(hypergraph, linkHandle))
                    continue;
                FEdge edge = new FEdge(addNode(linkHandle, level - 1, cond),
                        node);
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
                FEdge edge = new FEdge(node, addNode(link.getTargetAt(i),
                        level - 1, cond));
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
