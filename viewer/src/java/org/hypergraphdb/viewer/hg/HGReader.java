package org.hypergraphdb.viewer.hg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.AtomTypeCondition;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;

import cytoscape.task.TaskMonitor;

/**
 */
public class HGReader
{
    TaskMonitor taskMonitor;
    private HyperGraph hypergraph;
    /**
     * The DB to be loaded
     */
    protected File db;
    ArrayList<FNode> nodes;
    HashSet<FEdge> edges;

    public HGReader(File db)
    {
        this.db = db;
    }

    public HGReader(File db, TaskMonitor taskMonitor)
    {
        this.db = db;
        this.taskMonitor = taskMonitor;
    }

    public HyperGraph getHyperGraph()
    {
        return hypergraph;
    }

    public void read() throws IOException
    {
        String db_name = db.getAbsolutePath();
        System.out.println("HGReader -read: " + db_name);
        try
        {
            hypergraph = HGEnvironment.get(db_name);
        }
        catch (HGException ex)
        {
            if (taskMonitor != null)
            {
                taskMonitor.setException(ex, "Unable to load HyperGraph");
                taskMonitor.setStatus(ex.toString());
            }
            if (hypergraph != null) hypergraph.close();
            throw new IOException(ex.toString());
        }
        Set<HGHandle> nodesH = new HashSet<HGHandle>();
        Set<HGHandle> links = new HashSet<HGHandle>();
        Iterator<HGAtomType> it = HGVUtils.getAllAtomTypes(hypergraph)
                .iterator();
        while (it.hasNext())
            loadHG(hypergraph, it.next(), nodesH, links);
        nodes = new ArrayList<FNode>(nodesH.size());
        edges = new HashSet<FEdge>();
        for (Iterator<HGHandle> si = nodesH.iterator(); si.hasNext();)
        {
            HGHandle handle = hypergraph.getPersistentHandle(si.next());
            // System.out.println("FNode: " + handle);
            FNode node = HGVKit.getHGVNode(handle, true);
            nodes.add(node);
        }
        // ---------------------------------------------------------------------------
        // now loop over the interactions again, this time creating edges
        // between all sources and each of their respective targets.
        // ---------------------------------------------------------------------------
        for (Iterator t = links.iterator(); t.hasNext();)
        {
            HGHandle link_handle = hypergraph.getPersistentHandle((HGHandle) t
                    .next());
            HGLink link = (HGLink) hypergraph.get(link_handle);
            for (int l = 0; l < link.getArity(); l++)
            {
                HGHandle handle = hypergraph.getPersistentHandle(link
                        .getTargetAt(l));
                FEdge edge = HGVKit.getHGVEdge(link_handle, handle);
                edges.add(edge);
            } // for t
        } // for i
    }

    private static void loadHG(HyperGraph h, HGAtomType type,
            Set<HGHandle> nodes, Set<HGHandle> links)
    {
        HGHandle sHandle = h.getHandle(type);
        AtomTypeCondition cond = new AtomTypeCondition(h
                .getPersistentHandle(sHandle));
        HGQuery query = HGQuery.make(h, cond);
        HGSearchResult it = query.execute();
        while (it.hasNext())
        {
            it.next();
            processHandles(h, (HGHandle) it.current(), nodes, links);
        }
    }

    private static void processHandles(HyperGraph h, HGHandle handle,
            Set<HGHandle> nodes, Set<HGHandle> links)
    {
        if (nodes.contains(handle)) return;
        // TODO:Nasty hacks - everything here should be redesigned

        nodes.add(handle);
        for (HGHandle linkHandle : h.getIncidenceSet(handle))
        {
            HGLink link = (HGLink) h.get(linkHandle);
            if (links.contains(linkHandle)) continue;
            links.add(linkHandle);
            nodes.add(linkHandle);
            int arity = link.getArity();
            for (int j = 0; j < arity; j++)
            {
                processHandles(h, link.getTargetAt(j), nodes, links);
            }
        }
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
