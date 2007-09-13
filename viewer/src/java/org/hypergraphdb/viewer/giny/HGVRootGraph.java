package org.hypergraphdb.viewer.giny;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.viewer.HGVEdge;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNode;
import cytoscape.util.intr.ArrayIntIterator;
import cytoscape.util.intr.IntIterator;
import fing.model.FRootGraph;
import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;

public class HGVRootGraph extends FRootGraph
{
	Map<HGHandle, Integer> node_name_index_map;
	static final HGVNodeDepot node_depot = new HGVNodeDepot();
	static final HGVEdgeDepot edge_depot = new HGVEdgeDepot();

	public HGVRootGraph()
	{
		super(node_depot, edge_depot);
		node_name_index_map = new HashMap<HGHandle, Integer>();
	}

	public HGVNetwork createNetwork(Collection<HGVNode> nodes,
			Collection<HGVEdge> edges)
	{
		Node[] node = (nodes != null) ? (Node[]) nodes.toArray(new Node[nodes
				.size()]) : new Node[0];
		Edge[] edge = (edges != null) ? (Edge[]) edges.toArray(new Edge[edges
				.size()]) : new Edge[0];
		return createNetwork(node, edge);
	}

	/**
	 * Creates a new Network
	 */
	public HGVNetwork createNetwork(Node[] nodes, Edge[] edges)
	{
		final Node[] nodeArr = ((nodes != null) ? nodes : new Node[0]);
		final Edge[] edgeArr = ((edges != null) ? edges : new Edge[0]);
		final RootGraph root = this;
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

	public HGVNode getNode(HGHandle identifier)
	{
		Integer i = node_name_index_map.get(identifier);
		return (i != null) ? (HGVNode) getNode(i) : null;
	}

	public void setNodeIdentifier(HGHandle identifier, int index)
	{
		node_name_index_map.put(identifier, index);
	}
}
