package org.hypergraphdb.viewer;

import java.util.ArrayList;

import java.util.Collection;

import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.HGAtomTypeBase;

import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.PNodeView;

/*
 * You could use this class to persist a HGViewer instance in a HyperGraphDB
 * You should first register the type:
 * <code>
 * HGViewerType type = new HGViewerType();
 * type.setHyperGraph(hg);
 * hg.getTypeSystem().addPredefinedType(HGViewerType.HGHANDLE, type,  HGViewer.class);
 * </code>
 * Then simply add the corresponding HGViewer to the graph.
 */
public class HGViewerType extends HGAtomTypeBase 
{
//    public static final HGPersistentHandle HGHANDLE = 
//        HGHandleFactory.makeHandle("f7c68999-f9b6-11dc-a7b2-19766bcee0fa");
    
    @SuppressWarnings("unchecked")
	public Object make(HGPersistentHandle valueHandle, LazyRef<HGHandle[]> targetSet, IncidenceSetRef incidenceSet) 
    {
        HGPersistentHandle [] layout = graph.getStore().getLink(valueHandle);
        HGAtomType stype = graph.getTypeSystem().getAtomType(String.class);
        HGAtomType collType = graph.getTypeSystem().getAtomType(ArrayList.class);  
        HyperGraph db = HGEnvironment.get((String) stype.make(layout[0], null, null));
        Collection<FNode> nodes = (Collection<FNode>)collType.make(layout[1], null, null);
        Collection<FEdge> edges = (Collection<FEdge>)collType.make(layout[2], null, null);
       
        return new HGViewer(db, nodes, edges);
    }

    public void release(HGPersistentHandle handle) 
    {
    	HGPersistentHandle [] layout = graph.getStore().getLink(handle);
    	HGAtomType stype = graph.getTypeSystem().getAtomType(String.class);
        HGAtomType collType = graph.getTypeSystem().getAtomType(ArrayList.class);  
        stype.release(layout[0]);
        collType.release(layout[1]);
        collType.release(layout[2]);
    }

    public HGPersistentHandle store(Object instance) 
    {       
        HGAtomType stype = graph.getTypeSystem().getAtomType(String.class);
        HGAtomType collType = graph.getTypeSystem().getAtomType(ArrayList.class);  
        HGViewer c = (HGViewer)instance;
       
        HGPersistentHandle [] layout = new HGPersistentHandle[3];
        layout[0] = stype.store(c.getView().getHyperGraph().getLocation());
        Collection<FNode> nodes = new ArrayList<FNode>();
        for(PNodeView v : c.getView().getNodeViews())
            nodes.add(v.getNode());
        layout[1] = collType.store(nodes);
        Collection<FEdge> edges = new ArrayList<FEdge>();
        for(PEdgeView v : c.getView().getEdgeViews())
            edges.add(v.getEdge());
        layout[2] = collType.store(edges);
        return graph.getStore().store(layout);
    }
}
    