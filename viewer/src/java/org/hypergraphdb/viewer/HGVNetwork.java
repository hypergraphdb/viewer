package org.hypergraphdb.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.data.FlagFilter;
import cytoscape.util.intr.IntIterator;
import fing.model.FEdge;
import fing.model.FGraphPerspective;
import fing.model.FNode;
import fing.model.FRootGraph;

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
public class HGVNetwork extends FGraphPerspective
{
	private static int uid_counter = 0;
	private String identifier;
	protected String title;
	/**
	 * The ClientData map
	 */
	protected Map clientData;
	/**
	 * The default object for flagging graph objects
	 */
	protected FlagFilter flagger;
	private HyperGraph hg;

	// ----------------------------------------//
	// Constructors
	// ----------------------------------------//
	/**
	 * rootGraphNodeInx need not contain all endpoint nodes corresponding to
	 * edges in rootGraphEdgeInx - this is calculated automatically by this
	 * constructor. If any index does not correspond to an existing node or
	 * edge, an IllegalArgumentException is thrown. The indices lists need not
	 * be non-repeating - the logic in this constructor handles duplicate
	 * filtering.
	 */
	public HGVNetwork(FRootGraph root, IntIterator rootGraphNodeInx,
			IntIterator rootGraphEdgeInx)
	{
		super(root, rootGraphNodeInx, rootGraphEdgeInx);
		initialize();
	}

	protected void initialize()
	{
		// TODO: get a better naming system in place
		Integer i = new Integer(uid_counter);
		identifier = i.toString();
		uid_counter++;
		clientData = new HashMap();
		flagger = new FlagFilter(this);
	}

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

	/**
	 * Add a node to this Network
	 * @return the node
	 */
	public FNode addNode(FNode cytoscape_node)
	{
		return restoreNode(cytoscape_node);
	}

	
	/**
	 * Add an edge to this Network
	 */
	public void addEdge(FEdge cytoscape_edge)
	{
		restoreEdge(cytoscape_edge.getRootGraphIndex());
	}

	public HyperGraph getHyperGraph()
	{
		return hg;
	}

	public void setHyperGraph(HyperGraph h)
	{
		hg = h;
	}
}
