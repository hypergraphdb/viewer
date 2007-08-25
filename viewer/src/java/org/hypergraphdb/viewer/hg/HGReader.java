package org.hypergraphdb.viewer.hg;

import giny.model.Edge;
import giny.model.RootGraph;
import giny.view.GraphView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.handle.HGLiveHandle;
import org.hypergraphdb.query.AtomTypeCondition;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.viewer.*;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cytoscape.task.TaskMonitor;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.type.RecordType;

/**
 */
public class HGReader //implements GraphReader
{
	TaskMonitor taskMonitor;
	private HyperGraph hypergraph;
	/**
	 * The DB to be loaded
	 */
	protected File db;
	IntArrayList node_indices;
	OpenIntIntHashMap edges; 

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
		try{
		  hypergraph = new HyperGraph(db_name);
		}catch(HGException ex){
			if(taskMonitor != null){
			   taskMonitor.setException(ex, "Unable to load HyperGraph");
			   taskMonitor.setStatus(ex.toString());
			}
			if(hypergraph != null)
				hypergraph.close();
			throw new IOException(ex.toString());
		}
		Set<HGHandle> nodes = new HashSet<HGHandle>();
		Set<HGHandle> links = new HashSet<HGHandle>();
		Iterator<HGAtomType> it = HGUtils.getAllAtomTypes(hypergraph)
				.iterator();
		while (it.hasNext())
			loadHG(hypergraph, it.next(), nodes, links);
		node_indices = new IntArrayList(nodes.size());
		edges = new OpenIntIntHashMap();
		for (Iterator<HGHandle> si = nodes.iterator(); si.hasNext();)
		{
    		HGPersistentHandle handle = hypergraph.getPersistentHandle(si
					.next());
			//System.out.println("Node: " + handle);
			HGVNode node = (HGVNode) HGViewer.getHGVNode(handle, true);
			node.setHandle(handle);
			node_indices.add(node.getRootGraphIndex());
		}
		// ---------------------------------------------------------------------------
		// now loop over the interactions again, this time creating edges
		// between all sources and each of their respective targets.
		// ---------------------------------------------------------------------------
		for (Iterator t = links.iterator(); t.hasNext();)
		{
			HGPersistentHandle link_handle = hypergraph
					.getPersistentHandle((HGHandle) t.next());
			HGLink link = (HGLink) hypergraph.get(link_handle);
			for (int l = 0; l < link.getArity(); l++)
			{
				HGPersistentHandle handle = hypergraph.getPersistentHandle(link
						.getTargetAt(l));
				HGVEdge edge = HGViewer.getHGVEdge(link_handle, handle);
				edges.put(edge.getRootGraphIndex(), 0);
			} // for t
		} // for i
	}

	private static void loadHG(HyperGraph h, HGAtomType type,
			Set<HGHandle> nodes, Set<HGHandle> links)
	{
		HGHandle sHandle = h.getHandle(type);
		AtomTypeCondition cond = new AtomTypeCondition(
				h.getPersistentHandle(sHandle));
		HGQuery query = HGQuery.make(h, cond);
		HGSearchResult it = query.execute();
		while (it.hasNext())
		{
			it.next();
			processHandles(h, h.getPersistentHandle((HGHandle) it.current()), nodes, links);
		}
	}

	private static void processHandles(HyperGraph h, HGPersistentHandle handle,
			Set<HGHandle> nodes, Set<HGHandle> links)
	{
		if (nodes.contains(handle)) return;
		// TODO:Nasty hacks - everything here should be redesigned
		Object obj = h.get(handle);
		if (obj instanceof RecordType)
		{
			HGHandle[] recHandles = HGUtils.getAllForType(h,
					(HGPersistentHandle) handle);
			for (int i = 0; i < recHandles.length; i++)
				processHandles(h, h.getPersistentHandle(recHandles[i]), nodes, links);
			return;
		}
		nodes.add(handle);
		HGHandle[] all = h.getIncidenceSet(handle);
		for (int i = 0; i < all.length; i++)
		{
			HGLink link = (HGLink) h.get(all[i]);
			if (links.contains(all[i])) continue;
			links.add(all[i]); 
			nodes.add(all[i]);
			int arity = link.getArity();
			for (int j = 0; j < arity; j++)
			{
				processHandles(h, h.getPersistentHandle(
						link.getTargetAt(j)), nodes, links);
			}
		}
	}

	public int[] getNodeIndicesArray()
	{
		node_indices.trimToSize();
		return node_indices.elements();
	}

	public int[] getEdgeIndicesArray()
	{
		IntArrayList edge_indices = new IntArrayList(edges.size());
		edges.keys(edge_indices);
		edge_indices.trimToSize();
		return edge_indices.elements();
	}
/*

	public static HyperGraph populateTestDB(HyperGraph hg)
	{
		ArrayList<HGHandle> list = new ArrayList<HGHandle>();
		for (int i = 0; i < 2; i++)
			list.add(hg.add("String" + i));
		TestBean bean = new TestBean();
		bean.setStr("");
		bean.setIntT(70023);
		bean.addMruf("SomePath");
		CompoundTestBean comp = new CompoundTestBean();
		comp.setInner(bean);
		HGHandle recH = hg.add(bean);
		recH = hg.add(comp);
		list.add(recH);
		HGValueLink link1 = new HGValueLink("SomeLink1", (HGHandle[]) list
				.toArray(new HGHandle[list.size()]));
		HGHandle link_handle = hg.add(link1);
		list.add(link_handle);
		HGValueLink link2 = new HGValueLink("SomeLink2", (HGHandle[]) list
				.toArray(new HGHandle[list.size()]));
		hg.add(link2);
		return hg;
	}
	
	public static void main(String[] args)
	{
		HyperGraph hg = new HyperGraph("X:\\kosta\\ticl\\hypergraphdb\\XXX1");
		populateTestDB(hg);
		System.out.println("Adding succesfull.");
		HGSearchResult res = hg.find(new AtomTypeCondition(TestBean.class));
		if (res.hasNext())
		{
			TestBean bean = (TestBean) hg.get((HGHandle) res.next());
			System.out.println("Bean found: " + bean);
			
		}
		hg.close();
		System.exit(0);
	} */
}

