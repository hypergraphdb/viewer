package org.hypergraphdb.viewer.foo;

import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PEdgeView;
import phoebe.PNodeView;
import cytoscape.util.intr.IntEnumerator;
import fing.model.FEdge;
import fing.model.FNode;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public final class GraphConverter2
{

  private GraphConverter2() {}

  /**
   * Returns a representation of HGVKit's current network view.
   * Returns a MutablePolyEdgeGraphLayout, which, when mutated,
   * has a direct effect on the underlying HGVKit network view.  You'd
   * sure as heck better be using the returned object from the AWT event
   * dispatch thread!  Better yet, lock the HGVKit desktop somehow
   * (with a modal dialog for example) while using this returned object.
   * Movable nodes are defined to be selected nodes in HGVKit - if no
   * nodes are selected then all nodes are movable.  If selected node
   * information changes while we have a reference to this return object,
   * then the movability of corresponding node also changes.  This is one
   * reason why it's important to lock the HGVKit desktop while operating
   * on this return object.
   **/
  public static MutablePolyEdgeGraphLayout getGraphReference
    (double percentBorder,
     boolean preserveEdgeAnchors,
     boolean onlySelectedNodesMovable)
  {
    if (percentBorder < 0.0d)
      throw new IllegalArgumentException("percentBorder < 0.0");

    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    final HGVNetworkView graphView = HGVKit.getCurrentView();
    Iterator iter = graphView.getNodeViewsIterator();
    while (iter.hasNext())
    {
      PNodeView currentNodeView = (PNodeView) iter.next();
      minX = Math.min(minX, currentNodeView.getXPosition());
      maxX = Math.max(maxX, currentNodeView.getXPosition());
      minY = Math.min(minY, currentNodeView.getYPosition());
      maxY = Math.max(maxY, currentNodeView.getYPosition());
    }
    iter = graphView.getEdgeViewsIterator();
    final boolean noNodesSelected = (!onlySelectedNodesMovable) ||
      (graphView.getSelectedNodeIndices().length == 0);
    while (iter.hasNext())
    {
      PEdgeView currentEdgeView = (PEdgeView) iter.next();
      if ((!preserveEdgeAnchors) &&
          (noNodesSelected ||
           graphView.getNodeView
           (currentEdgeView.getEdge().getSource()).isSelected() ||
           graphView.getNodeView
           (currentEdgeView.getEdge().getTarget()).isSelected())) {
        currentEdgeView.getBend().removeAllHandles(); }
      else {
        List handles = currentEdgeView.getBend().getHandles();
        for (int h = 0; h < handles.size(); h++) {
          Point2D point = (Point2D) handles.get(h);
          minX = Math.min(minX, point.getX());
          maxX = Math.max(maxX, point.getX());
          minY = Math.min(minY, point.getY());
          maxY = Math.max(maxY, point.getY()); } }
    }
    double border =
      Math.max(maxX - minX, maxY - minY) * percentBorder * 0.5d;
    final double width = maxX - minX + border + border;
    final double height = maxY - minY + border + border;
    final double xOff = minX - border;
    final double yOff = minY - border;

    final HGVNetwork fixedGraph = graphView.getNetwork();

    return new MutablePolyEdgeGraphLayout()
      {
    	public Iterator<FEdge> edgesIterator(){ return fixedGraph.edgesIterator(); }
        public Iterator<FNode> nodesIterator(){ return fixedGraph.nodesIterator(); }
	    public int edgeSource(int edge) { return fixedGraph.edgeSource(edge); }
        public int edgeTarget(int edge) { return fixedGraph.edgeTarget(edge); }
       
        // GraphLayout methods.
        public double getMaxWidth() { return width; }
        public double getMaxHeight() { return height; }
        public double getNodePosition(int node, boolean xPosition) {
          PNodeView nodeView = getNodeView(node);
          if(nodeView == null) return 0;
          if (xPosition) return nodeView.getXPosition() - xOff;
          return nodeView.getYPosition() - yOff; }

        // MutableGraphLayout methods.
        public boolean isMovableNode(int node) {return true;}
        public void setNodePosition(int node, double xPos, double yPos) {
          PNodeView nodeView = getNodeView(node);
          if(nodeView == null) return;
          checkPosition(xPos, yPos);
          if (!isMovableNode(node))
            throw new UnsupportedOperationException
              ("node " + node + " is not movable");
          nodeView.setXPosition(xPos + xOff);
          nodeView.setYPosition(yPos + yOff); }

        // PolyEdgeGraphLayout methods.
        public int getNumAnchors(int edge) {
          return getEdgeView(edge).getBend().getHandles().size(); }
        public double getAnchorPosition(int edge, int anchorIndex,
                                        boolean xPosition) {
          Point2D point = (Point2D)
            getEdgeView(edge).getBend().getHandles().get(anchorIndex);
          return (xPosition ? (point.getX() - xOff): (point.getY() - yOff)); }

        // MutablePolyEdgeGraphLayout methods.
        public void deleteAnchor(int edge, int anchorIndex) {
          checkAnchorIndexBounds(edge, anchorIndex, false);
          checkMutableAnchor(edge);
          getEdgeView(edge).getBend().removeHandle(anchorIndex); }
        public void createAnchor(int edge, int anchorIndex) {
          checkAnchorIndexBounds(edge, anchorIndex, true);
          checkMutableAnchor(edge);
          getEdgeView(edge).getBend().addHandle
            (anchorIndex, new Point2D.Double());
          Point2D src =
            ((anchorIndex == 0) ?
             (new Point2D.Double
              (getNodePosition(edgeSource(edge), true),
               getNodePosition(edgeSource(edge), false))) :
             (new Point2D.Double
              (getAnchorPosition(edge, anchorIndex - 1, true),
               getAnchorPosition(edge, anchorIndex - 1, false))));
          Point2D trg =
            ((anchorIndex == getNumAnchors(edge) - 1) ?
             (new Point2D.Double
              (getNodePosition(edgeTarget(edge), true),
               getNodePosition(edgeTarget(edge), false))) :
             (new Point2D.Double
              (getAnchorPosition(edge, anchorIndex + 1, true),
               getAnchorPosition(edge, anchorIndex + 1, false))));
          setAnchorPosition(edge, anchorIndex,
                            (src.getX() + trg.getX()) / 2.0d,
                            (src.getY() + trg.getY()) / 2.0d); }
        public void setAnchorPosition(int edge, int anchorIndex,
                                      double xPos, double yPos) {
          checkAnchorIndexBounds(edge, anchorIndex, false);
          checkMutableAnchor(edge);
          checkPosition(xPos, yPos);
          getEdgeView(edge).getBend().moveHandle
            (anchorIndex, new Point2D.Double(xPos + xOff, yPos + yOff)); }

        // Helper methods.
        private PNodeView getNodeView(int node) {
          PNodeView nodeView = graphView.getNodeView(node);
          if (nodeView == null) 
        	  throw new IllegalArgumentException
                     ("node " + node + " not in this graph");
          return nodeView; }
        private PEdgeView getEdgeView(int edge) {
          PEdgeView edgeView = graphView.getEdgeView(edge);
          if (edgeView == null) throw new IllegalArgumentException
                                  ("edge " + edge + " not in this graph");
          return edgeView; }
        private void checkPosition(double xPos, double yPos) {
          if (xPos < 0.0d || xPos > getMaxWidth())
            throw new IllegalArgumentException("X position out of bounds");
          if (yPos < 0.0d || yPos > getMaxHeight())
            throw new IllegalArgumentException("Y position out of bounds"); }
        private void checkAnchorIndexBounds(int edge, int anchorIndex,
                                            boolean create) {
          int numAnchors = getNumAnchors(edge) + (create ? 0 : -1);
          if (anchorIndex < 0 || anchorIndex > numAnchors)
            throw new IndexOutOfBoundsException
              ("anchor index out of bounds"); }
        private void checkMutableAnchor(int edge) {
          int srcNode = edgeSource(edge);
          int trgNode = edgeTarget(edge);
          if ((!isMovableNode(srcNode)) && (!isMovableNode(trgNode)))
            throw new UnsupportedOperationException
              ("anchors at specified edge cannot be changed"); }
      };
  }

}
