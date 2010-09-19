package org.hypergraphdb.viewer.hg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGIndex;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HGTypeSystem;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.atom.HGSubsumes;
import org.hypergraphdb.atom.HGTypeStructuralInfo;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.hypergraphdb.query.AtomTypeCondition;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.JavaBeanBinding;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.util.GUIUtilities;

//import phoebe.PNodeView;

/**
 * 
 */
public class HGVUtils
{
	// temporary here, will go away
	public static Set<HGAtomType> getAllAtomTypes(HyperGraph graph)
	{
		Set<HGAtomType> types = new HashSet<HGAtomType>();
		HGSearchResult it = graph.find(hg.all()); //ind.scanValues();
		while (it.hasNext())
		{
			Object obj = graph.get((HGHandle) it.next());
			HGAtomType type = graph.getTypeSystem().getAtomType(obj);
			types.add(type);
		}
		it.close();
		return types;
	}

	public static HGHandle lookup(HyperGraph hg, String typeAlias,
			String keyProperty, Object keyValue)
	{
		HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(typeAlias);
		ByPartIndexer byProperty = new ByPartIndexer(typeHandle, new String[] { keyProperty });
		HGIndex<String, HGHandle> index = hg.getIndexManager().register(byProperty);
		return index.findFirst((String)keyValue);
	}

	public static HGHandle[] getAllRecordTypes(HyperGraph graph)
	{
		HGSearchResult<HGHandle> it = null;
		try
		{
			it = graph.find(hg.type(HGSubsumes.class));
			Set<HGHandle> list = new HashSet<HGHandle>();
			while (it.hasNext())
			{
				HGSubsumes h = (HGSubsumes) graph.get(it.next());
				list.add(h.getSpecific());
			}
			return (HGHandle[]) list.toArray(new HGHandle[list
					.size()]);
		}
		finally
		{
			if(it!=null)
			   it.close();
		}
	}

	public static RecordType getRecordType(HyperGraph hg, HGHandle h)
	{
		Object obj = hg.get(h);
		if (obj instanceof JavaBeanBinding)
			return (RecordType) ((JavaBeanBinding) obj).getHGType();
		return (RecordType) hg.get(h);
	}

	public static HGHandle[] getAllForType(HyperGraph graph, HGHandle handle)
	{
		HGSearchResult<HGHandle> ite = null;
		try
		{
			ite = graph.find(hg.type(handle));
			List<HGHandle> list = new ArrayList<HGHandle>();
			while (ite.hasNext())
				list.add(ite.next());
			return (HGHandle[]) list.toArray(
					new HGHandle[list.size()]);
		}
		finally
		{
			ite.close();
		}
	}

//	public static void expandNodes()
//	{
//		GraphView view = HGVKit.getCurrentView();
//		// select the view, because the popup doesn't do this automaticaly
//		for(PNodeView v : view.getSelectedNodes())
//		    expandNode(v);
//		
//		view.redrawGraph();
//	}
//	
//	private static void expandNode(PNodeView node)
//	{
//	    HyperGraph graph = node.getGraphView().getHyperGraph();
//        IncidenceSet in_links = graph.getIncidenceSet(node.getNode().getHandle());
//        HGHandle[] out_links = new HGHandle[0];
//        Object obj = graph.get(node.getNode().getHandle());
//        if (obj instanceof HGLink){
//            out_links = new HGHandle[((HGLink) obj).getArity()];
//                for(int i = 0; i < out_links.length; i++)
//                    out_links[i] = ((HGLink) obj).getTargetAt(i);
//            }
//        if(!confirmExpanding(in_links.size() + out_links.length)) return;
//        expandLinks(node.getGraphView(), node.getNode(), out_links, false, true);
//        HGHandle[] inA = in_links.toArray(new HGHandle[0]);
//        expandLinks(node.getGraphView(), node.getNode(), inA, true, false);
//	}
	
	private static void expandLinks(GraphView view, FNode node, HGHandle[] links, 
	        boolean incoming, boolean confirm_expanding){
		if(confirm_expanding && !confirmExpanding(links.length)) return;
		for(int i = 0; i < links.length; i++){
			FNode n = new FNode(links[i]);
			view.addNodeView(n);
			FEdge e = (incoming) ? new FEdge(new FNode(links[i]), node) :
			    new FEdge(node, new FNode(links[i]));
			view.addEdgeView(e);
		}
	}
	
	private static boolean confirmExpanding(int edges_count)
	{
		if(edges_count > 100)
		{
			NotifyDescriptor d = new NotifyDescriptor.Confirmation(
					GUIUtilities.getFrame(), "The node contains " + edges_count + " edges. Are you sure that you want to expand them?", 
					NotifyDescriptor.OK_CANCEL_OPTION);
			return DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION;
		}
		return true;
	}
	
	private static Set<HGAtomType> dir_links = null;
   
	private static Set<HGAtomType> getDirLinks(HyperGraph hg)
	{
		if (dir_links != null) return dir_links;
		dir_links = new HashSet<HGAtomType>();
/*		HGHandle sHandle = hg.getTypeSystem().getTypeHandle(HGTypeStructuralInfo.class);
		AtomTypeCondition cond = new AtomTypeCondition(hg.getPersistentHandle(sHandle)); */
		HGHandle sHandle = hg.getHandle(HGTypeStructuralInfo.class);
		//TODO: ??? after last major HG changes this throws NPE
		if(sHandle == null || sHandle.equals(hg.getHandleFactory().nullHandle()))
			return dir_links;
	    AtomTypeCondition cond = new AtomTypeCondition(hg.getPersistentHandle(sHandle));
		HGQuery query = HGQuery.make(hg, cond);
		HGSearchResult it = query.execute();
		while (it.hasNext())
		{
			it.next();
			HGTypeStructuralInfo info = 
				(HGTypeStructuralInfo) hg.get((HGHandle)it.current());
			if(info != null && info.isOrdered() && info.getArity() == 2)
			{
				dir_links.add(hg.getTypeSystem().getType(info.getTypeHandle()));
			}
		}
		it.close();
		return dir_links;
	}
	

	public static void collapseNode(HyperGraph hg, FNode node)
	{
		removeNode(hg, node, false);
	}
	
//	 The node should be presented in HG, this method removes it from the View
	public static void removeNode(HyperGraph hg, FNode node, boolean remove_center_too)
	{
		if(node == null) return;
	    GraphView view = HGVKit.getCurrentView();
				
		Set<FNode> nodesToRemove = new HashSet<FNode>();
		Set<FEdge> edgesToRemove = new HashSet<FEdge>();
		FEdge[] edges = view.getAdjacentEdges(node, true, true);
		for(int i = 0; i < edges.length; i++)
		{
			FEdge e = edges[i];
			FNode out = getOppositeNode(node, e);
			FEdge[] in_edges = view.getAdjacentEdges(out, true, true);
			if( in_edges.length <= 1)
			{
				nodesToRemove.add(out);
				for(int j = 0; j < in_edges.length; j++)
				   edgesToRemove.add(in_edges[j]);
				edgesToRemove.add(e);
			}
		}
		
		for(FEdge edge: edgesToRemove)
			view.removeEdgeView(edge);
		
		if( remove_center_too)    
		    view.removeNodeView(node);
		
		for(FNode n: nodesToRemove)
		   removeNode(hg, n, true);
	}

	public static FNode getOppositeNode(FNode center, FEdge e)
	{
		if (e.getSource().equals(center)) return e.getTarget();
		return e.getSource();
	}
	
	public static String deduceAliasName(HyperGraph hg, HGHandle h)
	{
		HGTypeSystem ts = hg.getTypeSystem();
		String total = "";
		Set<String> aliases = ts.findAliases(h);
		for (String al : aliases)
    		total += al + "/";// \s
		if (total.length() > 1)
			total = total.substring(0, total.length() - 1);
		if (total.length() == 0)
			total = hg.<Object>get(h).getClass().getName();
		return total;
	}

	public static void invokeLater(final Runnable r)
	{ 
	    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    SwingUtilities.invokeLater(
	            new Runnable()
	            { 
	                public void run(){
	                   Thread.currentThread().setContextClassLoader(
	                           HGViewer.class.getClassLoader());
	                   try
	                   {
	                       r.run();
	                   }
	                   finally
	                   {
	                       Thread.currentThread().setContextClassLoader(cl);
	                   }
	                }
	               });
	  }

	private HGVUtils()
	{ 
	}
}
