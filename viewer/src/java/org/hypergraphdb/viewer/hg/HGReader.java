package org.hypergraphdb.viewer.hg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

import cytoscape.task.TaskMonitor;

/**
 */
public class HGReader
{
    /**
     * The DB to be loaded
     */
    protected File db;
    TaskMonitor taskMonitor;
    private HyperGraph graph;
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
        return graph;
    }

    public void read() throws IOException
    {
        String db_name = db.getAbsolutePath();
        System.out.println("HGReader -read: " + db_name);
        try
        {
            graph = HGEnvironment.get(db_name);
        }
        catch (HGException ex)
        {
            if (taskMonitor != null)
            {
                taskMonitor.setException(ex, "Unable to load HyperGraph");
                taskMonitor.setStatus(ex.toString());
            }
            if (graph != null) graph.close();
            throw new IOException(ex.toString());
        }
        Set<HGHandle> nodesH = new HashSet<HGHandle>();
        Set<HGHandle> links = new HashSet<HGHandle>();
        for (HGAtomType type : HGVUtils.getAllAtomTypes(graph))
            loadHG(graph, type, nodesH, links);
        nodes = new ArrayList<FNode>(nodesH.size());
        edges = new HashSet<FEdge>();
        for (HGHandle handle: nodesH)
            nodes.add(new FNode(handle));
       
        // ---------------------------------------------------------------------------
        // now loop over the interactions again, this time creating edges
        // between all sources and each of their respective targets.
        // ---------------------------------------------------------------------------
        for ( HGHandle link_handle: links)
        {
            FNode link_node = new FNode(link_handle);
            HGLink link = (HGLink) graph.get(link_handle);
            for (int l = 0; l < link.getArity(); l++)
            {
                HGHandle handle = link.getTargetAt(l);
                FEdge edge = new FEdge(link_node, new FNode(handle));
                edges.add(edge);
            } // for t
        } // for i
    }

    private static void loadHG(HyperGraph h, HGAtomType type,
            Set<HGHandle> nodes, Set<HGHandle> links)
    {
        HGHandle sHandle = h.getHandle(type);
        AtomTypeCondition cond = new AtomTypeCondition(sHandle);
        HGQuery<HGHandle> query = HGQuery.make(h, cond);
        HGSearchResult<HGHandle> it = query.execute();
        while (it.hasNext())
        {
            it.next();
            processHandles(h, it.current(), nodes, links);
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
