package org.hypergraphdb.viewer;

import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandleFactory;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.HGAtomTypeBase;

public class HGVComponentType extends HGAtomTypeBase 
{
    public static final HGPersistentHandle HGHANDLE = 
        HGHandleFactory.makeHandle("f7c68999-f9b6-11dc-a7b2-19766bcee0fa");
    
    public Object make(HGPersistentHandle valueHandle, LazyRef<HGHandle[]> targetSet, IncidenceSetRef incidenceSet) 
    {
        HGPersistentHandle [] layout = graph.getStore().getLink(valueHandle);
        HGAtomType stype = graph.getTypeSystem().getAtomType(String.class);
        HGAtomType itype = graph.getTypeSystem().getAtomType(Integer.class);
        HGViewer view = new HGViewer(HGEnvironment.get((String)
        		stype.make(layout[0], null, null)));
        view.setDepth((Integer)itype.make(layout[2], null, null));
        return view.focus(layout[1]);
    }

    public void release(HGPersistentHandle handle) 
    {
    	HGPersistentHandle [] layout = graph.getStore().getLink(handle);
    	HGAtomType stype = graph.getTypeSystem().getAtomType(String.class);
        HGAtomType itype = graph.getTypeSystem().getAtomType(Integer.class);
        stype.release(layout[0]);
        itype.release(layout[2]);
    }

    public HGPersistentHandle store(Object instance) 
    {       
         HGAtomType stype = graph.getTypeSystem().getAtomType(String.class);
        HGAtomType itype = graph.getTypeSystem().getAtomType(Integer.class);
        HGVComponent c = (HGVComponent)instance;
        HGViewer view = c.getViewer();
        HGPersistentHandle [] layout = new HGPersistentHandle[3];
        layout[0] = stype.store(view.hg.getLocation());
        layout[1] = graph.getPersistentHandle(view.foc_handle);
        layout[2] = itype.store(view.depth);
        return graph.getStore().store(layout);
    }
}
    