package org.hypergraphdb.viewer.hg;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.util.HGUtils;
import org.hypergraphdb.util.Pair;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;

/**
 * Class that "reads" a given HG and performs necessary operations to transform
 * requested data to the HGViewer suitable FNode/FEdge model
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

    // public void read(HGHandle handle, int depth, HGALGenerator generator)
    // {
    // nodes.clear();
    // edges.clear();
    // inner_read(handle, depth, generator);
    //
    // System.out.println("focus0: " + nodes.size() + ":" + edges.size());
    // }
    //
    // void inner_read(HGHandle handle, int depth, HGALGenerator generator)
    // {
    // if(depth < 0) return;
    // FNode node = new FNode(handle);
    // nodes.add(node);
    // Set<HGHandle> next_layer = new HashSet<HGHandle>();
    // HGSearchResult<Pair<HGHandle, HGHandle>> i = generator.generate(handle);
    // while (i.hasNext())
    // {
    // Pair<HGHandle, HGHandle> p = i.next();
    // FNode linkNode = new FNode(p.getFirst());
    // nodes.add(linkNode);
    // FNode tgtNode = new FNode(p.getSecond());
    // nodes.add(tgtNode);
    // add_edge(linkNode, tgtNode);
    // next_layer.add(p.getFirst());
    // }
    // i.close();
    // for (HGHandle h : next_layer)
    // read0(h, depth - 1, generator);
    //       
    // }

    public void read(HGHandle handle, int depth, HGALGenerator generator)
    {
        nodes.clear();
        edges.clear();
     
        LinkedList<HGHandle> remaining = new LinkedList<HGHandle>();
        FNode node = new FNode(handle);
        nodes.add(node);
        if(depth == 0) return;
        depth--;
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
            HGSearchResult<Pair<HGHandle, HGHandle>> i = generator.generate(h);
            if (depth > 0) remaining.add(null);
            HGHandle currLink = null;
            FNode linkNode = node;
            while (i.hasNext())
            {
                Pair<HGHandle, HGHandle> p = i.next();
                if (!HGUtils.eq(p.getFirst(), currLink))
                {
                    currLink = p.getFirst();
                    if (currLink != null)
                    {
                        linkNode = new FNode(currLink);
                        nodes.add(linkNode);
                        add_edge(linkNode, node);
                    }
                    else 
                    {
                        System.err.println("NULL currLink in Generator: "
                                + (depth) + ":" + nodes.size());
                    }
                }
                HGHandle a = p.getSecond();
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

        System.out.println("focus: " + nodes.size() + ":" + edges.size());
    }

    private void add_edge(FNode source, FNode target)
    {
        FEdge edge = getFEdge(source, target);
        if (edge != null) edges.add(edge);
        else
        {
            edge = getFEdge(target, source);
            if (edge != null) edges.add(edge);
            else
                System.err.println("No such edge: " + source.getHandle() + ":"
                        + target.getHandle() + ":"
                        + hypergraph.get(source.getHandle()) + ":"
                        + hypergraph.get(target.getHandle()));
        }
    }

//    public void read0(HGHandle handle, int depth, HGALGenerator generator)
//    {
//        if (depth < 0) return;
//        DefaultALGenerator defG = (generator != null && generator instanceof DefaultALGenerator) ? (DefaultALGenerator) generator
//                : null;
//        FNode node = new FNode(handle);
//        nodes.add(node);
//        Set<HGHandle> next_layer = new HashSet<HGHandle>();
//        HGSearchResult<HGHandle> i = hypergraph.getIncidenceSet(handle)
//                .getSearchResult();
//        while (i.hasNext())
//        {
//            // if(defG != null && !defG.getLinkPredicate().satisfies())
//            HGHandle a = i.next();
//            next_layer.add(a);
//            FNode an = new FNode(a);
//            nodes.add(an);
//            add_edge(an, node);
//        }
//        Object o = hypergraph.get(handle);
//        if (o instanceof HGLink)
//        {
//            // if(!generator.getSiblingPredicate().satisfies())
//            for (int j = 0; j < ((HGLink) o).getArity(); j++)
//            {
//                HGHandle h = ((HGLink) o).getTargetAt(j);
//                next_layer.add(h);
//                FNode an = new FNode(h);
//                nodes.add(an);
//                add_edge(node, an);
//            }
//        }
//
//        for (HGHandle h : next_layer)
//            read0(h, depth - 1, generator);
//    }

    // check if such edge is possible and creates one if true
    // source node should be link, and
    // the target node should be in the target set of the source link
    FEdge getFEdge(FNode source, FNode target)
    {
        Object o = hypergraph.get(source.getHandle());
        if (!(o instanceof HGLink)) return null;
        // HGLink link = (HGLink) o;
        // for(int i = 0; i < link.getArity(); i++)
        // if(link.getTargetAt(i).equals(target.getHandle()))
        // return new FEdge(source, target);
        if (!hypergraph.getIncidenceSet(target.getHandle()).contains(
                source.getHandle())) return null;
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
