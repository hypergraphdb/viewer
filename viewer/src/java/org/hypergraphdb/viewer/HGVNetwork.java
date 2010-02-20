package org.hypergraphdb.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.viewer.data.FlagFilter;
import org.hypergraphdb.viewer.event.HGVNetworkChangeListenerChain;
import org.hypergraphdb.viewer.event.HGVNetworkEdgesRemovedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkEdgesAddedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkNodesRemovedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkNodesAddedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkChangeListener;


/**
 * HGVNetwork is the primary class for algorithm writing.&nbsp; All algorithms
 * should take a HGVNetwork as input, and do their best to only use the API of
 * HGVNetwork.&nbsp; Plugins that want to affect the display of a graph can look
 * into using HGVNetworkView as well.<br>
 * <br>
 * A HGVNetwork can create Nodes or Edges.&nbsp; Any Nodes or Edges that wish to
 * be added to a HGVNetwork first need to be created in <span style="font-style:
 * italic;">org.hypergraphdb.viewer.HGVKit</span>&nbsp; <br>
 * <br>
 * The methods that are defined by HGVNetwork mostly deal with data integration
 * and flagging of nodes/edges.&nbsp; All methods that deal with graph traversal
 * are part of the inherited API of the GraphPerspective class.&nbsp; Links to
 * which can be found at the bottom of the methods list.&nbsp; <br>
 * <br>
 * In general, all methods are supported for working with Nodes/Edges as
 * objects.<br>
 */
public class HGVNetwork 
{
	private String identifier;
	protected String title;
	protected Map clientData;
	protected FlagFilter flagger;
	// This is an array of length 1 - we need an array as an extra reference
	// to a reference because some other inner classes need to know what the
	// current listener is.
	private final HGVNetworkChangeListener[] m_lis;
	private Set<FNode> nodes = new HashSet<FNode>();
	private Set<FEdge> edges = new HashSet<FEdge>();
	protected HyperGraph hg;
	
	public HGVNetwork(HyperGraph db, Collection<FNode> _nodes, Collection<FEdge> _edges)
	{
		hg = db;
		for (FNode n : _nodes)
			nodes.add(n);
		for (FEdge e : _edges)
			edges.add(e);
		m_lis = new HGVNetworkChangeListener[1];
		initialize();
	}

	protected void initialize()
	{
		clientData = new HashMap();
		flagger = new FlagFilter(this);
	}
	
	public HyperGraph getHyperGraph() {
		return hg;
	}

	public void addHGVNetworkChangeListener(
			HGVNetworkChangeListener listener) {
		// This method is not thread safe; synchronize on an object to make it
		// so.
		m_lis[0] = HGVNetworkChangeListenerChain.add(m_lis[0], listener);
	}

	public void removeHGVNetworkChangeListener(
			HGVNetworkChangeListener listener) {
		// This method is not thread safe; synchronize on an object to make it
		// so.
		m_lis[0] = HGVNetworkChangeListenerChain.remove(m_lis[0],
				listener);
	}

	public int getNodeCount() {
		return nodes.size();
	}

	public int getEdgeCount() {
		return edges.size();
	}

	public Iterator<FNode> nodesIterator() {
		return nodes.iterator();
	}

	public Iterator<FEdge> edgesIterator() {
		return edges.iterator();
	}

	public void removeNode(FNode node) {
		if(!nodes.contains(node)) return;
		nodes.remove(node);
		m_lis[0].networkChanged(new HGVNetworkNodesRemovedEvent(
				this, new FNode[] { node }));
	}

	public void removeNodes(FNode[] nodes0) {
		for(FNode n: nodes0)
			nodes.remove(n);
		if (m_lis[0] != null)
		   m_lis[0].networkChanged(
				new HGVNetworkNodesRemovedEvent(
				this, nodes0));
	}

	public void addNode(FNode node) {
		if (nodes.contains(node))return;
		if (m_lis[0] != null) {
			m_lis[0].networkChanged(new HGVNetworkNodesAddedEvent(
							this, new FNode[] { node }));
		}
	}

	public void removeEdge(FEdge e) {
		if (!edges.contains(e))return;
		m_lis[0].networkChanged(new HGVNetworkEdgesRemovedEvent(
				this, new FEdge[]{e}));
	}
	
	public void removeEdges(FEdge[] res) {
		for (FEdge e : res)
			edges.remove(e);
		m_lis[0].networkChanged(new HGVNetworkEdgesRemovedEvent(
				this, res));
	}

	public boolean addEdge(FEdge edge) {
		if (edges.contains(edge))
			return false;
		edges.add(edge);

		final HGVNetworkChangeListener listener = m_lis[0];
		if (listener != null) {
			listener
					.networkChanged(new HGVNetworkEdgesAddedEvent(
							this, new FEdge[] { edge }));
		}
		return true;
	}

	public FEdge[] getAdjacentEdges(FNode node, 
			boolean incoming, boolean outgoing)
	{
		if (node == null || !nodes.contains(node))	return new FEdge[0];
		HGHandle nH = node.getHandle();
		IncidenceSet handles = hg.getIncidenceSet(node.getHandle());
		Set<FEdge> res = new HashSet<FEdge>();
		for (HGHandle h : handles) 
		{
		    FNode incNode = new FNode(h);
		    if (!nodes.contains(incNode)) continue;
		    if(outgoing)
		    {
			   FEdge e = new FEdge(node, incNode);
			   if (edges.contains(e))
				   res.add(e);
			}
		    
		    if(incoming)
            {
               FEdge e = new FEdge(incNode, node);
               if (edges.contains(e))
                   res.add(e);
            }
		}
		Object o = hg.get(nH);
		if (o instanceof HGLink) {
			HGLink link = ((HGLink) o);
			for (int i = 0; i < link.getArity(); i++) 
			{
			    FNode incNode = new FNode(link.getTargetAt(i));
			    if (!nodes.contains(incNode)) continue;
			    if(outgoing)
	            {
				   FEdge e = new FEdge(incNode, node);
				   if (edges.contains(e))
					   res.add(e);
				}
			    if(incoming)
                {
                   FEdge e = new FEdge(node, incNode);
                   if (edges.contains(e))
                       res.add(e);
                }
			}
		}
		return res.toArray(new FEdge[res.size()]);
	}

//	public FEdge[] getConnectingEdges(FNode[] nodeInx) {
//		Set<FEdge> res = new HashSet<FEdge>();
//		for (FNode n : nodeInx) {
//
//		}
//		return res.toArray(new FEdge[res.size()]);
//	}

	/**
	 * Can Change
	 */
	public String getTitle()
	{
		if (title == null) return identifier;
		return title;
	}

	/**
	 * Can Change
	 */
	public void setTitle(String new_id)
	{
		title = new_id;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public String setIdentifier(String new_id)
	{
		identifier = new_id;
		return identifier;
	}

	// ------------------------------//
	// Client Data
	// ------------------------------//
	/**
	 * Networks can support client data.
	 * 
	 * @param data_name the name of this client data
	 */
	public void putClientData(String data_name, Object data)
	{
		clientData.put(data_name, data);
	}

	/**
	 * Get a list of all currently available ClientData objects
	 */
	public Collection getClientDataNames()
	{
		return clientData.keySet();
	}

	/**
	 * Get Some client data
	 * 
	 * @param data_name the data to get
	 */
	public Object getClientData(String data_name)
	{
		return clientData.get(data_name);
	}

	/**
	 * Returns the default object for flagging graph objects.
	 */
	public FlagFilter getFlagger()
	{
		return flagger;
	}

}
