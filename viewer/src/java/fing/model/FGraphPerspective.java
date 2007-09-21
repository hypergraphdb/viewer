package fing.model;

import cytoscape.graph.dynamic.util.DynamicGraphRepresentation;
//import cytoscape.graph.fixed.FixedGraph;
import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.MinIntHeap;


import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hypergraphdb.viewer.data.FlagFilter;
import phoebe.event.GraphPerspectiveChangeListener;

public class FGraphPerspective 
{
 
  ///////////////////////////////////
  // BEGIN: Impelements FixedGraph //
  ///////////////////////////////////
  public IntEnumerator nodes() {
    final IntEnumerator nativeNodes = m_graph.nodes();
    return new IntEnumerator() {
        public int numRemaining() { return nativeNodes.numRemaining(); }
        public int nextInt() {
          return ~m_nativeToRootNodeInxMap.getIntAtIndex
            (nativeNodes.nextInt()); } }; }
  public IntEnumerator edges() {
    final IntEnumerator nativeEdges = m_graph.edges();
    return new IntEnumerator() {
        public int numRemaining() { return nativeEdges.numRemaining(); }
        public int nextInt() {
          return ~m_nativeToRootEdgeInxMap.getIntAtIndex
            (nativeEdges.nextInt()); } }; }
  public boolean nodeExists(final int node) {
    if (node < 0) return false;
    final int nativeNodeInx = m_rootToNativeNodeInxMap.get(node);
    return m_graph.nodeExists(nativeNodeInx); }
  public byte edgeType(final int edge) {
    if (edge < 0) return -1;
    final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(edge);
    return m_graph.edgeType(nativeEdgeInx); }
  public int edgeSource(final int edge) {
    if (edge < 0) return -1;
    final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(edge);
    final int nativeSource = m_graph.edgeSource(nativeEdgeInx);
    if (nativeSource < 0) return -1;
    return ~m_nativeToRootNodeInxMap.getIntAtIndex(nativeSource); }
  public int edgeTarget(final int edge) {
    if (edge < 0) return -1;
    final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(edge);
    final int nativeTarget = m_graph.edgeTarget(nativeEdgeInx);
    if (nativeTarget < 0) return -1;
    return ~m_nativeToRootNodeInxMap.getIntAtIndex(nativeTarget); }
  public IntEnumerator edgesAdjacent(final int node, boolean outgoing,
                                     boolean incoming, boolean undirected) {
    if (node < 0) return null;
    final int nativeNodeInx = m_rootToNativeNodeInxMap.get(node);
    final IntEnumerator nativeEdges =
      m_graph.edgesAdjacent(nativeNodeInx, outgoing, incoming, undirected);
    if (nativeEdges == null) return null;
    return new IntEnumerator() {
        public int numRemaining() { return nativeEdges.numRemaining(); }
        public int nextInt() {
          return ~m_nativeToRootEdgeInxMap.getIntAtIndex
            (nativeEdges.nextInt()); } }; }
  public IntIterator edgesConnecting(final int node0, final int node1,
                                     boolean outgoing, boolean incoming,
                                     boolean undirected) {
    if (node0 < 0 || node1 < 0) return null;
    final int nativeNode0Inx = m_rootToNativeNodeInxMap.get(node0);
    final int nativeNode1Inx = m_rootToNativeNodeInxMap.get(node1);
    final IntIterator nativeEdges =
      m_graph.edgesConnecting(nativeNode0Inx, nativeNode1Inx,
                              outgoing, incoming, undirected);
    if (nativeEdges == null) return null;
    return new IntIterator() {
        public boolean hasNext() { return nativeEdges.hasNext(); }
        public int nextInt() {
          return ~m_nativeToRootEdgeInxMap.getIntAtIndex
            (nativeEdges.nextInt()); } }; }
  /////////////////////////////////
  // END: Impelements FixedGraph //
  /////////////////////////////////

  public void addGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener) {
    // This method is not thread safe; synchronize on an object to make it so.
    m_lis[0] = GraphPerspectiveChangeListenerChain.add(m_lis[0], listener); }

  public void removeGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener) {
    // This method is not thread safe; synchronize on an object to make it so.
    m_lis[0] = GraphPerspectiveChangeListenerChain.remove
      (m_lis[0], listener); }

  // The object returned shares the same RootGraph with this object.
  public Object clone()
  {
    final IntEnumerator nativeNodes = m_graph.nodes();
    final IntIterator rootGraphNodeInx = new IntIterator() {
        public boolean hasNext() { return nativeNodes.numRemaining() > 0; }
        public int nextInt() {
          return m_nativeToRootNodeInxMap.getIntAtIndex
            (nativeNodes.nextInt()); } };
    final IntEnumerator nativeEdges = m_graph.edges();
    final IntIterator rootGraphEdgeInx = new IntIterator() {
        public boolean hasNext() { return nativeEdges.numRemaining() > 0; }
        public int nextInt() {
          return m_nativeToRootEdgeInxMap.getIntAtIndex
            (nativeEdges.nextInt()); } };
    return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
  }

  public FRootGraph getRootGraph() {
    return m_root; }

  public int getNodeCount()
  {
    return m_graph.nodes().numRemaining();
  }

  public int getEdgeCount()
  {
    return m_graph.edges().numRemaining();
  }

  public Iterator<FNode> nodesIterator()
  {
    final IntEnumerator nodes = m_graph.nodes();
    return new Iterator<FNode>() {
        public void remove() {
          throw new UnsupportedOperationException(); }
        public boolean hasNext() {
          return nodes.numRemaining() > 0; }
        public FNode next() {
          if (!hasNext()) throw new NoSuchElementException();
          return m_root.getNode
            (m_nativeToRootNodeInxMap.getIntAtIndex(nodes.nextInt())); } };
  }

  public int[] getNodeIndicesArray()
  {
    IntEnumerator nodes = m_graph.nodes();
    final int[] returnThis = new int[nodes.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_nativeToRootNodeInxMap.getIntAtIndex(nodes.nextInt());
    return returnThis;
  }

  public Iterator<FEdge> edgesIterator()
  {
    final IntEnumerator edges = m_graph.edges();
    return new Iterator<FEdge>() {
        public void remove() {
          throw new UnsupportedOperationException(); }
        public boolean hasNext() {
          return edges.numRemaining() > 0; }
        public FEdge next() {
          if (!hasNext()) throw new NoSuchElementException();
          return m_root.getEdge
            (m_nativeToRootEdgeInxMap.getIntAtIndex(edges.nextInt())); } };
  }

  public int[] getEdgeIndicesArray()
  {
    IntEnumerator edges = m_graph.edges();
    final int[] returnThis = new int[edges.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(edges.nextInt());
    return returnThis;
  }

  public FNode hideNode(FNode node) {
    if (node.getRootGraph() == m_root &&
        hideNode(node.getRootGraphIndex()) != 0) return node;
    else return null; }

  public int hideNode(int rootGraphNodeInx) {
    return m_weeder.hideNode(this, rootGraphNodeInx); }

  public int[] hideNodes(int[] rootGraphNodeInx) {
    return m_weeder.hideNodes(this, rootGraphNodeInx); }

  public FNode restoreNode(FNode node) {
    if (node.getRootGraph() == m_root &&
        restoreNode(node.getRootGraphIndex()) != 0) return node;
    else return null; }

  public int restoreNode(int rootGraphNodeInx) {
    final int returnThis;
    if (_restoreNode(rootGraphNodeInx) != 0) returnThis = rootGraphNodeInx;
    else returnThis = 0;
    if (returnThis != 0) {
      final GraphPerspectiveChangeListener listener = m_lis[0];
      if (listener != null) {
        listener.graphPerspectiveChanged
          (new GraphPerspectiveNodesRestoredEvent
           (this, new int[] { rootGraphNodeInx })); } }
    return returnThis; }

  // Returns 0 if unsuccessful; returns the complement of the native node
  // index if successful.  Complement is '~', i.e., it's a negative value.
  private int _restoreNode(final int rootGraphNodeInx)
  {
    if (!(rootGraphNodeInx < 0)) return 0;
    int nativeNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);
    if (m_root.getNode(rootGraphNodeInx) == null ||
        !(nativeNodeInx < 0 || nativeNodeInx == Integer.MAX_VALUE)) return 0;
    nativeNodeInx = m_graph.nodeCreate();
    m_rootToNativeNodeInxMap.put(~rootGraphNodeInx, nativeNodeInx);
    m_nativeToRootNodeInxMap.setIntAtIndex(rootGraphNodeInx, nativeNodeInx);
    return ~nativeNodeInx;
  }

   public int[] restoreNodes(int[] rootGraphNodeInx) {
    m_heap.empty();
    final MinIntHeap successes = m_heap;
    final int[] returnThis = new int[rootGraphNodeInx.length];
    for (int i = 0; i < rootGraphNodeInx.length; i++)
      if (_restoreNode(rootGraphNodeInx[i]) != 0) {
        returnThis[i] = rootGraphNodeInx[i];
        successes.toss(returnThis[i]); }
    if (successes.size() > 0) {
      final GraphPerspectiveChangeListener listener = m_lis[0];
      if (listener != null) {
        final int[] successArr = new int[successes.size()];
        successes.copyInto(successArr, 0);
        listener.graphPerspectiveChanged
          (new GraphPerspectiveNodesRestoredEvent(this, successArr)); } }
    return returnThis; }

  public int hideEdge(int rootGraphEdgeInx) {
    return m_weeder.hideEdge(this, rootGraphEdgeInx); }

  public int[] hideEdges(int[] rootGraphEdgeInx) {
    return m_weeder.hideEdges(this, rootGraphEdgeInx); }

  public int restoreEdge(int rootGraphEdgeInx) {
    final int returnThis = _restoreEdge(rootGraphEdgeInx);
    if (returnThis != 0) {
      final GraphPerspectiveChangeListener listener = m_lis[0];
      if (listener != null) {
        listener.graphPerspectiveChanged
          (new GraphPerspectiveEdgesRestoredEvent
           (this, new int[] { rootGraphEdgeInx })); } }
    return returnThis; }

  // Use this only from _restoreEdge(int).  The heap will never grow
  // to more than the default size; it won't take up lots of memory.
  private final MinIntHeap m_heap__restoreEdge = new MinIntHeap();

  // Returns 0 if unsuccessful; otherwise returns the root index of edge.
  private int _restoreEdge(final int rootGraphEdgeInx)
  {
    if (!(rootGraphEdgeInx < 0)) return 0;
    int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);
    if (m_root.getEdge(rootGraphEdgeInx) == null ||
        !(nativeEdgeInx < 0 || nativeEdgeInx == Integer.MAX_VALUE)) return 0;
    final int rootGraphSourceNodeInx =
      m_root.getEdgeSourceIndex(rootGraphEdgeInx);
    final int rootGraphTargetNodeInx =
      m_root.getEdgeTargetIndex(rootGraphEdgeInx);
    int nativeSourceNodeInx =
      m_rootToNativeNodeInxMap.get(~rootGraphSourceNodeInx);
    int nativeTargetNodeInx =
      m_rootToNativeNodeInxMap.get(~rootGraphTargetNodeInx);
    m_heap__restoreEdge.empty();
    final MinIntHeap restoredNodeRootInx = m_heap__restoreEdge;
    if (nativeSourceNodeInx < 0 || nativeSourceNodeInx == Integer.MAX_VALUE) {
      nativeSourceNodeInx = ~(_restoreNode(rootGraphSourceNodeInx));
      restoredNodeRootInx.toss(rootGraphSourceNodeInx);
      if (rootGraphSourceNodeInx == rootGraphTargetNodeInx) {
        nativeTargetNodeInx = nativeSourceNodeInx; } }
    if (nativeTargetNodeInx < 0 || nativeTargetNodeInx == Integer.MAX_VALUE) {
      nativeTargetNodeInx = ~(_restoreNode(rootGraphTargetNodeInx));
      restoredNodeRootInx.toss(rootGraphTargetNodeInx); }
    if (restoredNodeRootInx.size() > 0) {
      final GraphPerspectiveChangeListener listener = m_lis[0];
      if (listener != null) {
        final int[] restoredNodesArr = new int[restoredNodeRootInx.size()];
        restoredNodeRootInx.copyInto(restoredNodesArr, 0);
        listener.graphPerspectiveChanged
          (new GraphPerspectiveNodesRestoredEvent(this, restoredNodesArr)); } }
    nativeEdgeInx = m_graph.edgeCreate
      (nativeSourceNodeInx, nativeTargetNodeInx,
       m_root.isEdgeDirected(rootGraphEdgeInx));
    m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx, nativeEdgeInx);
    m_nativeToRootEdgeInxMap.setIntAtIndex(rootGraphEdgeInx, nativeEdgeInx);
    return rootGraphEdgeInx;
  }

   public FGraphPerspective join(FGraphPerspective persp) {
    final FGraphPerspective thisPersp = this;
    if (!(persp instanceof FGraphPerspective)) return null;
    final FGraphPerspective otherPersp = (FGraphPerspective) persp;
    if (otherPersp.m_root != thisPersp.m_root) return null;
    final IntEnumerator thisNativeNodes = thisPersp.m_graph.nodes();
    final IntEnumerator otherNativeNodes = otherPersp.m_graph.nodes();
    final IntIterator rootGraphNodeInx = new IntIterator() {
        public boolean hasNext() {
          return thisNativeNodes.numRemaining() > 0 ||
            otherNativeNodes.numRemaining() > 0; }
        public int nextInt() {
          if (thisNativeNodes.numRemaining() > 0)
            return thisPersp.m_nativeToRootNodeInxMap.getIntAtIndex
              (thisNativeNodes.nextInt());
          else
            return otherPersp.m_nativeToRootNodeInxMap.getIntAtIndex
              (otherNativeNodes.nextInt()); } };
    final IntEnumerator thisNativeEdges = thisPersp.m_graph.edges();
    final IntEnumerator otherNativeEdges = otherPersp.m_graph.edges();
    final IntIterator rootGraphEdgeInx = new IntIterator() {
        public boolean hasNext() {
          return thisNativeEdges.numRemaining() > 0 ||
            otherNativeEdges.numRemaining() > 0; }
        public int nextInt() {
          if (thisNativeEdges.numRemaining() > 0)
            return thisPersp.m_nativeToRootEdgeInxMap.getIntAtIndex
              (thisNativeEdges.nextInt());
          else
            return otherPersp.m_nativeToRootEdgeInxMap.getIntAtIndex
              (otherNativeEdges.nextInt()); } };
    return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx); }

  
   public int[] neighborsArray(final int nodeIndex) {
    int[] adjacentEdgeIndices =
      getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);
    if (adjacentEdgeIndices == null) return null;
    m_hash.empty();
    final IntHash neighbors = m_hash;
    for (int i = 0; i < adjacentEdgeIndices.length; i++) {
      int neighborIndex = (nodeIndex ^
                           getEdgeSourceIndex(adjacentEdgeIndices[i]) ^
                           getEdgeTargetIndex(adjacentEdgeIndices[i]));
      neighbors.put(~neighborIndex); }
    IntEnumerator en = neighbors.elements();
    final int[] returnThis = new int[en.numRemaining()];
    int index = -1;
    while (en.numRemaining() > 0) returnThis[++index] = ~(en.nextInt());
    return returnThis; }

 
  public int getIndex(FNode node) {
    if (node.getRootGraph() == m_root &&
        getRootGraphNodeIndex(node.getRootGraphIndex()) ==
        node.getRootGraphIndex())
      return node.getRootGraphIndex();
    else return 0; }

  public int getNodeIndex(int rootGraphNodeInx) {
    return getRootGraphNodeIndex(rootGraphNodeInx); }

  public int getRootGraphNodeIndex(int rootGraphNodeInx) {
    if (!(rootGraphNodeInx < 0)) return 0;
    final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);
    if (nativeNodeInx < 0 || nativeNodeInx == Integer.MAX_VALUE) return 0;
    return rootGraphNodeInx; }

  public FNode getNode(int rootGraphNodeInx) {
    return m_root.getNode(getRootGraphNodeIndex(rootGraphNodeInx)); }

  public int getIndex(FEdge edge) {
    if (edge.getRootGraph() == m_root &&
        getRootGraphEdgeIndex(edge.getRootGraphIndex()) ==
        edge.getRootGraphIndex())
      return edge.getRootGraphIndex();
    else return 0; }

  public int getEdgeIndex(int rootGraphEdgeInx) {
    return getRootGraphEdgeIndex(rootGraphEdgeInx); }

  public int getRootGraphEdgeIndex(int rootGraphEdgeInx) {
    if (!(rootGraphEdgeInx < 0)) return 0;
    final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);
    if (nativeEdgeInx < 0 || nativeEdgeInx == Integer.MAX_VALUE) return 0;
    return rootGraphEdgeInx; }

  public FEdge getEdge(int rootGraphEdgeInx) {
    return m_root.getEdge(getRootGraphEdgeIndex(rootGraphEdgeInx)); }

  public int getEdgeSourceIndex(int edgeInx)
  {
    if (!(edgeInx < 0)) return 0;
    final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~edgeInx);
    final int nativeSrcNodeInx = m_graph.edgeSource(nativeEdgeInx);
    if (nativeSrcNodeInx < 0) return 0;
    return m_nativeToRootNodeInxMap.getIntAtIndex(nativeSrcNodeInx);
  }

  public int getEdgeTargetIndex(int edgeInx)
  {
    if (!(edgeInx < 0)) return 0;
    final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~edgeInx);
    final int nativeTrgNodeInx = m_graph.edgeTarget(nativeEdgeInx);
    if (nativeTrgNodeInx < 0) return 0;
    return m_nativeToRootNodeInxMap.getIntAtIndex(nativeTrgNodeInx);
  }

   public int[] getAdjacentEdgeIndicesArray(int nodeInx,
                                           boolean undirected,
                                           boolean incomingDirected,
                                           boolean outgoingDirected)
  {
    if (!(nodeInx < 0)) return null;
    final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx);
    final IntEnumerator adj = m_graph.edgesAdjacent
      (nativeNodeInx, outgoingDirected, incomingDirected, undirected);
    if (adj == null) return null;
    final int[] returnThis = new int[adj.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(adj.nextInt());
    return returnThis;
  }

  public int[] getConnectingEdgeIndicesArray(int[] nodeInx)
  {
    final IntHash nativeNodeBucket = new IntHash();
    for (int i = 0; i < nodeInx.length; i++) {
      if (!(nodeInx[i] < 0)) return null;
      final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx[i]);
      if (m_graph.nodeExists(nativeNodeInx))
        nativeNodeBucket.put(nativeNodeInx);
      else return null; }
    m_hash.empty();
    final IntHash nativeEdgeBucket = m_hash;
    final IntEnumerator nativeNodeEnum = nativeNodeBucket.elements();
    while (nativeNodeEnum.numRemaining() > 0)
    {
      final int nativeNodeIndex = nativeNodeEnum.nextInt();
      final IntEnumerator nativeAdjEdgeEnum =
        m_graph.edgesAdjacent(nativeNodeIndex, true, false, true);
      while (nativeAdjEdgeEnum.numRemaining() > 0)
      {
        final int nativeCandidateEdge = nativeAdjEdgeEnum.nextInt();
        final int nativeOtherEdgeNode =
          (nativeNodeIndex ^ m_graph.edgeSource(nativeCandidateEdge) ^
           m_graph.edgeTarget(nativeCandidateEdge));
        if (nativeOtherEdgeNode == nativeNodeBucket.get(nativeOtherEdgeNode))
          nativeEdgeBucket.put(nativeCandidateEdge);
      }
    }
    final IntEnumerator nativeReturnEdges = nativeEdgeBucket.elements();
    final int[] returnThis = new int[nativeReturnEdges.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex
        (nativeReturnEdges.nextInt());
    return returnThis;
  }

  public void finalize() {
    m_root.removeRootGraphChangeListener(m_changeSniffer); }

  // Nodes and edges in this graph are called "native indices" throughout
  // this class.
  private final DynamicGraphRepresentation m_graph;

  private final FRootGraph m_root;

  // This is an array of length 1 - we need an array as an extra reference
  // to a reference because some other inner classes need to know what the
  // current listener is.
  private final GraphPerspectiveChangeListener[] m_lis;

  // RootGraph indices are negative in these arrays.
  private final IntArray m_nativeToRootNodeInxMap;
  private final IntArray m_nativeToRootEdgeInxMap;

  // RootGraph indices are ~ (complements) of the real RootGraph indices
  // in these hashtables.
  private final IntIntHash m_rootToNativeNodeInxMap;
  private final IntIntHash m_rootToNativeEdgeInxMap;

  // This is a utilitarian heap that is used as a bucket of ints.
  // Don't forget to empty() it before using it.
  private final MinIntHeap m_heap;

  // This is a utilitarian hash that is used as a collision detecting
  // bucket of ints.  Don't forget to empty() it before using it.
  private final IntHash m_hash;

  private final GraphWeeder m_weeder;

  // We need to remove this listener from the RootGraph during finalize().
  private final RootGraphChangeSniffer m_changeSniffer;

  // rootGraphNodeInx need not contain all endpoint nodes corresponding to edges in
  // rootGraphEdgeInx - this is calculated automatically by this constructor.
  // If any index does not correspond to an existing node or edge, an
  // IllegalArgumentException is thrown.  The indices lists need not be
  // non-repeating - the logic in this constructor handles duplicate
  // filtering.
  public FGraphPerspective(FRootGraph root,  
                    IntIterator rootGraphNodeInx,
                    IntIterator rootGraphEdgeInx)
    throws IllegalArgumentException // If any index is not in RootGraph.
  {
    m_graph = new DynamicGraphRepresentation();
    m_root = root;
    m_lis = new GraphPerspectiveChangeListener[1];
    m_nativeToRootNodeInxMap = new IntArray();
    m_nativeToRootEdgeInxMap = new IntArray();
    m_rootToNativeNodeInxMap = new IntIntHash();
    m_rootToNativeEdgeInxMap = new IntIntHash();
    m_heap = new MinIntHeap();
    m_hash = new IntHash();
    m_weeder = new GraphWeeder(m_root, m_graph,
                               m_nativeToRootNodeInxMap,
                               m_nativeToRootEdgeInxMap,
                               m_rootToNativeNodeInxMap,
                               m_rootToNativeEdgeInxMap, m_lis, m_heap);
    m_changeSniffer = new RootGraphChangeSniffer(m_weeder);
    while (rootGraphNodeInx.hasNext()) {
      final int rootNodeInx = rootGraphNodeInx.nextInt();
      if (m_root.getNode(rootNodeInx) != null) {
        if (m_rootToNativeNodeInxMap.get(~rootNodeInx) >= 0) continue;
        final int nativeNodeInx = m_graph.nodeCreate();
        m_rootToNativeNodeInxMap.put(~rootNodeInx, nativeNodeInx);
        m_nativeToRootNodeInxMap.setIntAtIndex(rootNodeInx, nativeNodeInx); }
      else throw new IllegalArgumentException
             ("node with index " + rootNodeInx + " not in RootGraph"); }
    while (rootGraphEdgeInx.hasNext()) {
      final int rootEdgeInx = rootGraphEdgeInx.nextInt();
      if (m_root.getEdge(rootEdgeInx) != null) {
        if (m_rootToNativeEdgeInxMap.get(~rootEdgeInx) >= 0) continue;
        final int rootSrcInx = m_root.getEdgeSourceIndex(rootEdgeInx);
        final int rootTrgInx = m_root.getEdgeTargetIndex(rootEdgeInx);
        final boolean edgeDirected = m_root.isEdgeDirected(rootEdgeInx);
        int nativeSrcInx = m_rootToNativeNodeInxMap.get(~rootSrcInx);
        if (nativeSrcInx < 0) {
          nativeSrcInx = m_graph.nodeCreate();
          m_rootToNativeNodeInxMap.put(~rootSrcInx, nativeSrcInx);
          m_nativeToRootNodeInxMap.setIntAtIndex(rootSrcInx, nativeSrcInx); }
        int nativeTrgInx = m_rootToNativeNodeInxMap.get(~rootTrgInx);
        if (nativeTrgInx < 0) {
          nativeTrgInx = m_graph.nodeCreate();
          m_rootToNativeNodeInxMap.put(~rootTrgInx, nativeTrgInx);
          m_nativeToRootNodeInxMap.setIntAtIndex(rootTrgInx, nativeTrgInx); }
        final int nativeEdgeInx =
          m_graph.edgeCreate(nativeSrcInx, nativeTrgInx, edgeDirected);
        m_rootToNativeEdgeInxMap.put(~rootEdgeInx, nativeEdgeInx);
        m_nativeToRootEdgeInxMap.setIntAtIndex(rootEdgeInx, nativeEdgeInx); }
      else throw new IllegalArgumentException
             ("edge with index " + rootEdgeInx + " not in RootGraph"); }
    m_root.addRootGraphChangeListener(m_changeSniffer);
  }

  // Cannot have any recursize reference to a FGraphPerspective in this
  // object instance - we want to allow garbage collection of unused
  // GraphPerspective objects.
  private final static class RootGraphChangeSniffer
    implements RootGraphChangeListener
  {

    private final GraphWeeder m_weeder;

    private RootGraphChangeSniffer(GraphWeeder weeder)
    {
      m_weeder = weeder;
    }

    public final void rootGraphChanged(RootGraphChangeEvent evt)
    {
      if ((evt.getType() & RootGraphChangeEvent.NODES_REMOVED_TYPE) != 0)
        m_weeder.hideNodes((FGraphPerspective) evt.getSource(), evt.getRemovedNodeIndices());
      if ((evt.getType() & RootGraphChangeEvent.EDGES_REMOVED_TYPE) != 0)
        m_weeder.hideEdges((FGraphPerspective) evt.getSource(), 
        		evt.getRemovedEdgeIndices());
    }

  }

  // An instance of this class cannot have any recursive reference to a
  // FGraphPerspective object.  The idea behind this class is to allow
  // garbage collection of unused GraphPerspective objects.  This class
  // is used by the RootGraphChangeSniffer to remove nodes/edges from
  // a GraphPerspective; this class is also used by this GraphPerspective
  // implementation itself.
  private final static class GraphWeeder
  {

    private final FRootGraph m_root;
    private final DynamicGraphRepresentation m_graph;
    private final IntArray m_nativeToRootNodeInxMap;
    private final IntArray m_nativeToRootEdgeInxMap;
    private final IntIntHash m_rootToNativeNodeInxMap;
    private final IntIntHash m_rootToNativeEdgeInxMap;

    // This is an array of length 1 - we need an array as an extra reference
    // to a reference because the surrounding GraphPerspective will be
    // modifying the entry at index 0 in this array.
    private final GraphPerspectiveChangeListener[] m_lis;

    // This is a utilitarian heap that is used as a bucket of ints.
    // Don't forget to empty() it before using it.
    private final MinIntHeap m_heap;

    private GraphWeeder(FRootGraph root,
    		DynamicGraphRepresentation graph,
                        IntArray nativeToRootNodeInxMap,
                        IntArray nativeToRootEdgeInxMap,
                        IntIntHash rootToNativeNodeInxMap,
                        IntIntHash rootToNativeEdgeInxMap,
                        GraphPerspectiveChangeListener[] listener,
                        MinIntHeap heap)
    {
      m_root = root;
      m_graph = graph;
      m_nativeToRootNodeInxMap = nativeToRootNodeInxMap;
      m_nativeToRootEdgeInxMap = nativeToRootEdgeInxMap;
      m_rootToNativeNodeInxMap = rootToNativeNodeInxMap;
      m_rootToNativeEdgeInxMap = rootToNativeEdgeInxMap;
      m_lis = listener;
      m_heap = heap;
    }

    // RootGraphChangeSniffer is not to call this method.  We rely on
    // the specified node still existing in the RootGraph in this method.
    private final int hideNode(FGraphPerspective source, int rootGraphNodeInx)
    {
      final int returnThis = _hideNode(source, rootGraphNodeInx);
      if (returnThis != 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final FNode removedNode = m_root.getNode(rootGraphNodeInx);
          listener.graphPerspectiveChanged
            (new GraphPerspectiveNodesHiddenEvent
             (source, new FNode[] { removedNode })); } }
      return returnThis;
    }

    // Don't call this method from outside this inner class.
    // Returns 0 if and only if hiding this node was unsuccessful.
    // Otherwise returns the input parameter, the root node index.
    private int _hideNode(Object source, final int rootGraphNodeInx)
    {
      if (!(rootGraphNodeInx < 0)) return 0;
      final int nativeNodeIndex =
        m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);
      if (nativeNodeIndex < 0) return 0;
      final IntEnumerator nativeEdgeInxEnum =
        m_graph.edgesAdjacent(nativeNodeIndex, true, true, true);
      if (nativeEdgeInxEnum == null) return 0;
      if (nativeEdgeInxEnum.numRemaining() > 0) {
        final FEdge[] edgeRemoveArr =
          new FEdge[nativeEdgeInxEnum.numRemaining()];
        for (int i = 0; i < edgeRemoveArr.length; i++) {
          final int rootGraphEdgeInx = m_nativeToRootEdgeInxMap.getIntAtIndex
            (nativeEdgeInxEnum.nextInt());
          // The edge returned by the RootGraph won't be null even if this
          // hideNode operation is triggered by a node being removed from
          // the underlying RootGraph - this is because when a node is removed
          // from an underlying RootGraph, all touching edges to that node are
          // removed first from that RootGraph, and corresponding edge removal
          // events are fired before the node removal event is fired.
          edgeRemoveArr[i] = m_root.getEdge(rootGraphEdgeInx); }
        hideEdges(source, edgeRemoveArr); }
      // nativeNodeIndex tested for validity with adjacentEdges() above.
      if (m_graph.nodeRemove(nativeNodeIndex)) {
        m_rootToNativeNodeInxMap.put(~rootGraphNodeInx, Integer.MAX_VALUE);
        m_nativeToRootNodeInxMap.setIntAtIndex(0, nativeNodeIndex);
        return rootGraphNodeInx; }
      else throw new IllegalStateException
             ("internal error - node didn't exist, its adjacent edges did");
    }

    // This heap is to be used directly only by
    // hideNodes(GraphPerspective, int[]) and by hideNodes(Object, FNode[]).
    private final MinIntHeap m_heap_hideNodes = new MinIntHeap();

    // RootGraphChangeSniffer is not to call this method.  We rely on
    // the specified nodes still existing in the RootGraph in this method.
    private final int[] hideNodes(FGraphPerspective source, int[] rootNodeInx)
    {
      // We can't use m_heap here because it's potentially used by every
      // _hideNode() during hiding of edges.
      m_heap_hideNodes.empty();
      final MinIntHeap successes = m_heap_hideNodes;
      final int[] returnThis = new int[rootNodeInx.length];
      for (int i = 0; i < rootNodeInx.length; i++) {
        returnThis[i] = _hideNode(source, rootNodeInx[i]);
        if (returnThis[i] != 0) successes.toss(i); }
      if (successes.size() > 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final FNode[] successArr = new FNode[successes.size()];
          final IntEnumerator en = successes.elements();
          int index = -1;
          while (en.numRemaining() > 0)
            successArr[++index] = m_root.getNode(rootNodeInx[en.nextInt()]);
          listener.graphPerspectiveChanged
            (new GraphPerspectiveNodesHiddenEvent(source, successArr)); } }
      return returnThis;
    }

    // Entries in the nodes array may not be null.
    // This method is to be called by RootGraphChangeSniffer.  It may also
    // be called by others - therefore don't assume that the nodes to be
    // hidden here don't have any adjacent edges.
    private final void hideNodes(Object source, FNode[] nodes)
    {
      // We can't use m_heap here because it's potentially used by every
      // _hideNode() during hiding of edges.
      m_heap_hideNodes.empty();
      final MinIntHeap successes = m_heap_hideNodes;
      for (int i = 0; i < nodes.length; i++) {
        if (_hideNode(source, nodes[i].getRootGraphIndex()) != 0)
          successes.toss(i); }
      if (successes.size() > 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final FNode[] successArr = new FNode[successes.size()];
          final IntEnumerator en = successes.elements();
          int index = -1;
          while (en.numRemaining() > 0)
            successArr[++index] = nodes[en.nextInt()];
          listener.graphPerspectiveChanged
            (new GraphPerspectiveNodesHiddenEvent(source, successArr)); } }
    }

    // RootGraphChangeSniffer is not to call this method.  We rely on
    // the specified edge still existing in the RootGraph in this method.
    private final int hideEdge(FGraphPerspective source, int rootGraphEdgeInx)
    {
      final int returnThis = _hideEdge(rootGraphEdgeInx);
      if (returnThis != 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final FEdge removedEdge = m_root.getEdge(rootGraphEdgeInx);
          listener.graphPerspectiveChanged
            (new GraphPerspectiveEdgesHiddenEvent
             (source, new FEdge[] { removedEdge })); } }
      return returnThis;
    }

    // Don't call this method from outside this inner class.
    // Returns 0 if and only if hiding this edge was unsuccessful.
    // Otherwise returns the input parameter, the root edge index.
    private int _hideEdge(int rootGraphEdgeInx)
    {
      if (!(rootGraphEdgeInx < 0)) return 0;
      final int nativeEdgeIndex =
        m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);
      if (nativeEdgeIndex < 0) return 0;
      if (m_graph.edgeRemove(nativeEdgeIndex)) {
        m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx, Integer.MAX_VALUE);
        m_nativeToRootEdgeInxMap.setIntAtIndex(0, nativeEdgeIndex);
        return rootGraphEdgeInx; }
      else return 0;
    }

    // RootGraphChangeSniffer is not to call this method.  We rely on
    // the specified edges still existing in the RootGraph in this method.
    private final int[] hideEdges(FGraphPerspective source, int[] rootEdgeInx)
    {
      m_heap.empty();
      final MinIntHeap successes = m_heap;
      final int[] returnThis = new int[rootEdgeInx.length];
      for (int i = 0; i < rootEdgeInx.length; i++) {
        returnThis[i] = _hideEdge(rootEdgeInx[i]);
        if (returnThis[i] != 0) successes.toss(i); }
      if (successes.size() > 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final FEdge[] successArr = new FEdge[successes.size()];
          final IntEnumerator en = successes.elements();
          int index = -1;
          while (en.numRemaining() > 0)
            successArr[++index] = m_root.getEdge(rootEdgeInx[en.nextInt()]);
          listener.graphPerspectiveChanged
            (new GraphPerspectiveEdgesHiddenEvent(source, successArr)); } }
      return returnThis;
    }

    // Entries in the edges array may not be null.
    // This method is to be called by RootGraphChangeSniffer.
    private final void hideEdges(Object source, FEdge[] edges)
    {
      m_heap.empty();
      final MinIntHeap successes = m_heap;
      for (int i = 0; i < edges.length; i++)
        if (_hideEdge(edges[i].getRootGraphIndex()) != 0)
          successes.toss(i);
      if (successes.size() > 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final FEdge[] successArr = new FEdge[successes.size()];
          final IntEnumerator en = successes.elements();
          int index = -1;
          while (en.numRemaining() > 0)
            successArr[++index] = edges[en.nextInt()];
          listener.graphPerspectiveChanged
            (new GraphPerspectiveEdgesHiddenEvent(source, successArr)); } }
    }

  }

}
