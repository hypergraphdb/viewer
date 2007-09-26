package fing.model;

import cytoscape.graph.dynamic.util.DynamicGraphRepresentation;
import cytoscape.util.intr.ArrayIntIterator;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntHash;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.viewer.HGVNetwork;

/**
 * <h2>Architecture</h2>
 * 
 * A graph model consisting of nodes and the edges between them. GINY graph
 * models are separated into RootGraphs and GraphPerspectives. RootGraphs
 * contain all Nodes and Edges in a graph, while GraphPerspectives contain some
 * subset of them (any given GraphPerspective may include all Nodes and Edges
 * from its RootGraph, but it does not need to). Every GraphPerspective has
 * exactly one RootGraph. RootGraphs may have any number of GraphPerspectives.
 * 
 * <h2>Indices</h2>
 * Every FNode and FEdge has a unique and unchanging index in its RootGraph; these
 * indices are always negative integers and are not guaranteed to be
 * consecutive. Note that if you would like to associate additional data with a
 * FNode or an FEdge, you can associate the data with the FNode's or the FEdge's
 * RootGraph index, which is guaranteed to be unique for the lifetime of the
 * RootGraph.
 */
public class FRootGraph 
{
	Map<HGHandle, Integer> node_name_index_map = new HashMap<HGHandle, Integer>(100);
	
	public FNode getNode(HGHandle identifier)
	{
		Integer i = node_name_index_map.get(identifier);
		return (i != null) ? getNode(i) : null;
	}

	public void setNodeIdentifier(HGHandle identifier, int index)
	{
		node_name_index_map.put(identifier, index);
	}
	
	public HGVNetwork createNetwork(Collection<FNode> nodes,
			Collection<FEdge> edges)
	{
		FNode[] node = (nodes != null) ? (FNode[]) nodes.toArray(new FNode[nodes
				.size()]) : new FNode[0];
		FEdge[] edge = (edges != null) ? (FEdge[]) edges.toArray(new FEdge[edges
				.size()]) : new FEdge[0];
		return createNetwork(node, edge);
	}

	/**
	 * Creates a new Network
	 */
	public HGVNetwork createNetwork(FNode[] nodes, FEdge[] edges)
	{
		final FNode[] nodeArr = ((nodes != null) ? nodes : new FNode[0]);
		final FEdge[] edgeArr = ((edges != null) ? edges : new FEdge[0]);
		final FRootGraph root = this;
		try
		{
			return new HGVNetwork(this, new IntIterator() {
				private int index = 0;

				public boolean hasNext()
				{
					return index < nodeArr.length;
				}

				public int nextInt()
				{
					if (nodeArr[index] == null
							|| nodeArr[index].getRootGraph() != root)
						throw new IllegalArgumentException();
					return nodeArr[index++].getRootGraphIndex();
				}
			}, new IntIterator() {
				private int index = 0;

				public boolean hasNext()
				{
					return index < edgeArr.length;
				}

				public int nextInt()
				{
					if (edgeArr[index] == null
							|| edgeArr[index].getRootGraph() != root)
						throw new IllegalArgumentException();
					return edgeArr[index++].getRootGraphIndex();
				}
			});
		}
		catch (IllegalArgumentException exc)
		{
			return null;
		}
	}

	/**
	 * Uses Code copied from ColtRootGraph to create a new Network.
	 */
	public HGVNetwork createNetwork(int[] nodeInx, int[] edgeInx)
	{
		if (nodeInx == null) nodeInx = new int[0];
		if (edgeInx == null) edgeInx = new int[0];
		try
		{
			return new HGVNetwork(this, new ArrayIntIterator(nodeInx, 0,
					nodeInx.length), new ArrayIntIterator(edgeInx, 0,
					edgeInx.length));
		}
		catch (IllegalArgumentException exc)
		{
			return null;
		}
	}
	
   // Not specified by giny.model.RootGraph. GraphPerspective implementation
	// in this package relies on this method.
	// ATTENTION! Before making this method public you need to change the
	// event implementations to return copied arrays in their methods instead
	// of always returning the same array reference. Also you need to enable
	// create node and create edge events - currently only remove node and
	// remove edge events are fired.
	void addRootGraphChangeListener(RootGraphChangeListener listener)
	{ // This method is not thread safe; synchronize on an object to make it
		// so.
		m_lis = RootGraphChangeListenerChain.add(m_lis, listener);
	}

	// Not specified by giny.model.RootGraph. GraphPerspective implementation
	// in this package relies on this method.
	// ATTENTION! Before making this method public you need to change the
	// event implementations to return copied arrays in their methods instead
	// of always returning the same array reference. Also you need to enable
	// create node and create edge events - currently only remove node and
	// remove edge events are fired.
	void removeRootGraphChangeListener(RootGraphChangeListener listener)
	{ // This method is not thread safe; synchronize on an object to make it
		// so.
		m_lis = RootGraphChangeListenerChain.remove(m_lis, listener);
	}

	public int getNodeCount()
	{
		return m_graph.nodes().numRemaining();
	}

	public int getEdgeCount()
	{
		return m_graph.edges().numRemaining();
	}

	public Iterator nodesIterator()
	{
		final IntEnumerator nodes = m_graph.nodes();
		final FRootGraph rootGraph = this;
		return new Iterator() {
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public boolean hasNext()
			{
				return nodes.numRemaining() > 0;
			}

			public Object next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				return rootGraph.getNode(~(nodes.nextInt()));
			}
		};
	}

	public int[] getNodeIndicesArray()
	{
		IntEnumerator nodes = m_graph.nodes();
		final int[] returnThis = new int[nodes.numRemaining()];
		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = ~(nodes.nextInt());
		return returnThis;
	}

	public Iterator edgesIterator()
	{
		final IntEnumerator edges = m_graph.edges();
		final FRootGraph rootGraph = this;
		return new Iterator() {
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public boolean hasNext()
			{
				return edges.numRemaining() > 0;
			}

			public Object next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				return rootGraph.getEdge(~(edges.nextInt()));
			}
		};
	}

	public FNode removeNode(FNode node)
	{
		if (node.getRootGraph() == this
				&& removeNode(node.getRootGraphIndex()) != 0)
			return node;
		else
			return null;
	}

	public int removeNode(final int nodeInx)
	{
		final int nativeNodeInx = ~nodeInx;
		if (!m_graph.nodeExists(nativeNodeInx)) return 0;
		final IntEnumerator nativeEdgeEnum = m_graph.edgesAdjacent(
				nativeNodeInx, true, true, true);
		final int[] removedEdgeArr = new int[nativeEdgeEnum.numRemaining()];
		for (int i = 0; i < removedEdgeArr.length; i++)
			removedEdgeArr[i] = m_edges
					.getEdgeAtIndex(nativeEdgeEnum.nextInt()).getRootGraphIndex();
		for (int i = 0; i < removedEdgeArr.length; i++)
		{
			final int nativeEdgeInx = ~(removedEdgeArr[i]);
			m_graph.edgeRemove(nativeEdgeInx);
			final FEdge removedEdge = m_edges.getEdgeAtIndex(nativeEdgeInx);
			m_edges.setEdgeAtIndex(null, nativeEdgeInx);
			m_edgeDepot.recycleEdge(removedEdge);
		}
		final FNode removedNode = m_nodes.getNodeAtIndex(nativeNodeInx);
		m_graph.nodeRemove(nativeNodeInx);
		m_nodes.setNodeAtIndex(null, nativeNodeInx);
		m_nodeDepot.recycleNode(removedNode);
		if (removedEdgeArr.length > 0)
			m_lis.rootGraphChanged(new RootGraphEdgesRemovedEvent (this,	removedEdgeArr));
		m_lis.rootGraphChanged(new RootGraphNodesRemovedEvent(this,
				new int[] { removedNode.getRootGraphIndex() }));
		return nodeInx;
	}

	public int createNode()
	{
		final int nativeNodeInx = m_graph.nodeCreate();
		final int returnThis = ~nativeNodeInx;
		FNode newNode = m_nodeDepot.getNode(this, returnThis, null);
		m_nodes.setNodeAtIndex(newNode, nativeNodeInx);
		return returnThis;
	}

	public FEdge removeEdge(FEdge edge)
	{
		if (edge.getRootGraph() == this
				&& removeEdge(edge.getRootGraphIndex()) != 0)
			return edge;
		else
			return null;
	}

	public int removeEdge(final int edgeInx)
	{
		final int nativeEdgeInx = ~edgeInx;
		if (m_graph.edgeType(nativeEdgeInx) < 0) return 0;
    	m_graph.edgeRemove(nativeEdgeInx);
		final FEdge removedEdge = m_edges.getEdgeAtIndex(nativeEdgeInx);
		m_edges.setEdgeAtIndex(null, nativeEdgeInx);
		m_edgeDepot.recycleEdge(removedEdge);
		m_lis.rootGraphChanged(new RootGraphEdgesRemovedEvent(this,
				new int[] { removedEdge.getRootGraphIndex() }));
		return edgeInx;
	}

	public int createEdge(FNode source, FNode target)
	{
		return createEdge(source, target, source.getRootGraphIndex() != target
				.getRootGraphIndex());
	}

	public int createEdge(FNode source, FNode target, boolean directed)
	{
		if (source.getRootGraph() == this && target.getRootGraph() == this)
			return createEdge(source.getRootGraphIndex(), target
					.getRootGraphIndex(), directed);
		else
			return 0;
	}

	/**
	 * 
	 * Create a directed FEdge from the FNode with the given <tt>source_index</tt>
	 * to the FNode with the given <tt>target_index</tt>, and return the new
	 * FEdge's index. This edge created will be directed, except in the case
	 * where the source and target nodes are the same node, in which case the
	 * created edge will be undirected.
	 * 
	 * @param source_index the index in this RootGraph of the source of the new
	 * directed FEdge
	 * @param target_index the index in this RootGraph of the target of the new
	 * directed FEdge
	 * @return the index of the newly created FEdge, or 0 if either the source
	 * node index or the target node index does not correspond to an existing
	 * FNode in this RootGraph.
	 */
	public int createEdge(int sourceNodeIndex, int targetNodeIndex)
	{
		return createEdge(sourceNodeIndex, targetNodeIndex,
				sourceNodeIndex != targetNodeIndex);
	}

	/**
	 * Create an FEdge from the FNode with the given <tt>source_index</tt> to
	 * the FNode with the given <tt>target_index</tt>, and return the new
	 * FEdge's index. The newly created FEdge will be directed iff the boolean
	 * argument is true.
	 * @param source_index the index in this RootGraph of the source of the new
	 * FEdge
	 * @param target_index the index in this RootGraph of the target of the new
	 * FEdge
	 * @param directed The new FEdge will be directed iff this argument is true.
	 * @return the index of the newly created FEdge, or 0 if either the source
	 * node index or the target node index does not correspond to an existing
	 * FNode in this RootGraph.
	 */
	public int createEdge(int sourceNodeIndex, int targetNodeIndex,
			boolean directed)
	{
		final int nativeEdgeInx = m_graph.edgeCreate(~sourceNodeIndex,
				~targetNodeIndex, directed);
		if (nativeEdgeInx < 0) return 0;
		final int returnThis = ~nativeEdgeInx;
		FEdge newEdge = m_edgeDepot.getEdge(this, returnThis, null);
		m_edges.setEdgeAtIndex(newEdge, nativeEdgeInx);
		return returnThis;
	}

	/**
	 * Returns indices of all edges adjacent to the node at
	 * specified index. See definitions of adjacency below.
	 * 
	 * @param node_index the index of the node whose adjacent edge
	 * information we're seeking.
	 * @param undirected_edges edge indices of all adjacent
	 * undirected edges are included in the return value of this
	 * method if this value is true, otherwise not a single
	 * index corresponding to an undirected edge is returned;
	 * undirected edge E is an adjacent undirected edge to node N
	 * 
	 * [definition:] if and only if E's source is N or E's target
	 * is N.
	 * @param incoming_directed_edges edge indices of all incoming
	 * directed edges are included in the return value of this
	 * method if this value is true, otherwise not a single index
	 * corresponding to an incoming directed edge is returned;
	 * directed edge E is an incoming directed edge to node N
	 * [definition:] if and only if N is E's target.
	 * @param outgoing_directed_edges edge indices of all outgoing
	 * directed edges are included in the return value of this
	 * method if this value is true, otherwise not a single index
	 * corresponding to an outgoing directed edge is returned;
	 * directed edge E is an outgoing directed edge from node N
	 * [definition:] if and only if N is E's source.
	 * 
	 * @return a set of edge indices corresponding to
	 * edges matched by our query; if all three of the boolean
	 * query parameters are false, the empty array is returned;
	 * null is returned if and only if this RootGraph has no node
	 * at the specified index.
	 */
	public int[] getAdjacentEdgeIndicesArray(int nodeInx, boolean undirected,
			boolean incomingDirected, boolean outgoingDirected)
	{
		final IntEnumerator adj = m_graph.edgesAdjacent(~nodeInx,
				outgoingDirected, incomingDirected, undirected);
		if (adj == null) return null;
		final int[] returnThis = new int[adj.numRemaining()];
		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = ~adj.nextInt();
		return returnThis;
	}
	private final IntHash m_hash2 = new IntHash();

	public int getIndex(FNode node)
	{
		if (node.getRootGraph() == this)
			return node.getRootGraphIndex();
		else
			return 0;
	}

	public FNode getNode(int nodeInx)
	{
		if (nodeInx < 0 && nodeInx != 0x80000000)
			return m_nodes.getNodeAtIndex(~nodeInx);
		else
			return null;
	}

	public int getIndex(FEdge edge)
	{
		if (edge.getRootGraph() == this)
			return edge.getRootGraphIndex();
		else
			return 0;
	}

	public FEdge getEdge(int edgeInx)
	{
		if (edgeInx < 0 && edgeInx != 0x80000000)
			return m_edges.getEdgeAtIndex(~edgeInx);
		else
			return null;
	}

	public int getEdgeSourceIndex(int edgeInx)
	{
		return ~(m_graph.edgeSource(~edgeInx));
	}

	public int getEdgeTargetIndex(int edgeInx)
	{
		return ~(m_graph.edgeTarget(~edgeInx));
	}

	public boolean isEdgeDirected(int edgeInx)
	{
		return m_graph.edgeType(~edgeInx) == 1;
	}

	
	
	// The relationship between indices (both node and edge) in this
	// RootGraph and in the DynamicGraph is "flip the bits":
	// rootGraphIndex == ~(dynamicGraphIndex)
	private final DynamicGraphRepresentation m_graph = new DynamicGraphRepresentation();
	// For the most part, there will always be a listener registered with this
	// RootGraph (all GraphPerspectives will have registered listeners). So,
	// instead of checking for null, just keep a permanent listener.
	private RootGraphChangeListener m_lis = new RootGraphChangeListener() {
		public void rootGraphChanged(RootGraphChangeEvent event)
		{
		}
	};
	
	// This is our "node factory" and "node recyclery".
	private final FingNodeDepot m_nodeDepot;
	// This is our "edge factory" and "edge recyclery".
	private final FingEdgeDepot m_edgeDepot;
	// This is our index-to-node mapping.
	private final NodeArray m_nodes = new NodeArray();
	// This is our index-to-edge mapping.
	private final EdgeArray m_edges = new EdgeArray();
	
	// Package visible constructor.
	public FRootGraph()
	{
		this(new NodeDepository(), new EdgeDepository());
	}

	// Package visible constructor.
	public FRootGraph(FingNodeDepot nodeDepot, FingEdgeDepot edgeDepot)
	{
		if (nodeDepot == null)
			throw new NullPointerException("nodeDepot is null");
		m_nodeDepot = nodeDepot;
		if (edgeDepot == null)
			throw new NullPointerException("edgeDepot is null");
		m_edgeDepot = edgeDepot;
	}
}
