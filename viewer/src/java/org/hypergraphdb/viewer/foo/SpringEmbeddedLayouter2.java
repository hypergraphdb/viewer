package org.hypergraphdb.viewer.foo;

import cytoscape.graph.legacy.GraphTopology;
import cytoscape.graph.legacy.IndexIterator;
import cytoscape.graph.legacy.util.GraphCompiler;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of Kamada and Kawai's spring embedded layout algorithm.<p>
 * Note from Nerius Landys on Wed Oct  6 11:07:24 PDT 2004: The algorithm in
 * this code has been adapted from an existing algorithm in the Cytoscape
 * project.  As little as possible has been done to change the algorithmic
 * logic, even where flaws might have been found.  The legacy class which
 * this class borrows code from is
 * <code>cytoscape.layout.SpringEmbeddedLayouter</code>.<p>
 * This layout algorithm does support subgraph layout - that is, it respects
 * <code>MutableGraphLayout</code> objects whose nodes are not all movable.<p>
 * This class is seriously due for a complete re-write.  The algorithm is
 * slow as heck.
 * Please avoid looking at or using this code -- this code
 *   is going away in the next Cytoscape release (the one after 2.1).
 **/
public final class SpringEmbeddedLayouter2 implements Task
{

  private static final
    int DEFAULT_NUM_LAYOUT_PASSES = 2;
  private static final
    double DEFAULT_AVERAGE_ITERATIONS_PER_NODE = 20.0;
  private static final
    double[] DEFAULT_NODE_DISTANCE_SPRING_SCALARS = new double[] { 1.0, 1.0 };
  private static final
    double DEFAULT_NODE_DISTANCE_STRENGTH_CONSTANT = 15.0;
  private static final
    double DEFAULT_NODE_DISTANCE_REST_LENGTH_CONSTANT = 200.0;
  private static final
    double DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_STRENGTH = 0.05;
  private static final
    double DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_REST_LENGTH = 2500.0;
  private static final
    double[] DEFAULT_ANTICOLLISION_SPRING_SCALARS = new double[] { 0.0, 1.0 };
  private static final
    double DEFAULT_ANTICOLLISION_SPRING_STRENGTH = 100.0;

  private final int m_numLayoutPasses;
  private final double m_averageIterationsPerNode;
  private final double[] m_nodeDistanceSpringScalars;
  private final double m_nodeDistanceStrengthConstant;
  private final double m_nodeDistanceRestLengthConstant;
  private final double m_disconnectedNodeDistanceSpringStrength;
  private final double m_disconnectedNodeDistanceSpringRestLength;
  private final double[] m_anticollisionSpringScalars;
  private final double m_anticollisionSpringStrength;

  private double[][] m_nodeDistanceSpringStrengths;
  private double[][] m_nodeDistanceSpringRestLengths;

  private final MutablePolyEdgeGraphLayout m_graph;
  private final int m_nodeCount;
  private final int m_edgeCount;
  private int m_layoutPass;

  private boolean m_halt = false;
  private TaskMonitor m_taskMonitor;

  private final AutoScalingGraphLayout m_autoScaleGraph;

  /**
   * Constructs an object which is able to perform a specific layout algorithm
   * on a graph.  An instance of this class will perform a layout at most
   * once.  The constructor returns quickly; <code>run()</code> does the
   * computations to perform the layout.<p>
   *
   * @param graph the graph layout object that this layout algorithm
   *   operates on.
   **/
  public SpringEmbeddedLayouter2(MutablePolyEdgeGraphLayout graph)
  {
    if (graph == null) throw new NullPointerException("graph is null");
    m_graph = graph;
    m_numLayoutPasses = DEFAULT_NUM_LAYOUT_PASSES;
    m_averageIterationsPerNode = DEFAULT_AVERAGE_ITERATIONS_PER_NODE;
    m_nodeDistanceSpringScalars = DEFAULT_NODE_DISTANCE_SPRING_SCALARS;
    m_nodeDistanceStrengthConstant = DEFAULT_NODE_DISTANCE_STRENGTH_CONSTANT;
    m_nodeDistanceRestLengthConstant =
      DEFAULT_NODE_DISTANCE_REST_LENGTH_CONSTANT;
    m_disconnectedNodeDistanceSpringStrength =
      DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_STRENGTH;
    m_disconnectedNodeDistanceSpringRestLength =
      DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_REST_LENGTH;
    m_anticollisionSpringScalars = DEFAULT_ANTICOLLISION_SPRING_SCALARS;
    m_anticollisionSpringStrength = DEFAULT_ANTICOLLISION_SPRING_STRENGTH;
    m_nodeCount = m_graph.getNumNodes();
    m_edgeCount = m_graph.getNumEdges();
    m_autoScaleGraph = new AutoScalingGraphLayout(m_graph);
  }

  public String getTitle()
  {
    return "Spring Embedded Network Layout";
  }

  /**
   * @param taskMonitor a hook that a parent application may pass in
   *   to get information regarding what percentage of the layout
   *   has been completed etc., or <code>null</code> to stop this layout
   *   algorithm from reporting how much progress it has made.
   **/
  public void setTaskMonitor(TaskMonitor taskMonitor)
  {
    m_taskMonitor =
      ((taskMonitor != null) ? taskMonitor :
       new TaskMonitor() {
         public void setPercentCompleted(int percent) {}
         public void setEstimatedTimeRemaining(long time) {}
         public void setException(Throwable t, String foo) {}
         public void setStatus(String message) {} } );
  }

  private static class AutoScalingGraphLayout
  {
    private MutablePolyEdgeGraphLayout graph;
    private boolean alreadyMoved = false;
    AutoScalingGraphLayout(MutablePolyEdgeGraphLayout graph) {
      this.graph = graph; }
    private Hashtable movedNodes = new Hashtable();
    int getNumNodes() {
      // This is a debugging statement.
      if (alreadyMoved) throw new IllegalStateException
                          ("already moved nodes in underlying graph");
      return graph.getNumNodes(); }
    boolean isMovableNode(int nodeIndex) {
      return graph.isMovableNode(nodeIndex); }
    void setNodePosition(boolean overrideMovableCheck,
                         int nodeIndex, double X, double Y) {
      // This is a debugging statement.
      if (alreadyMoved) throw new IllegalStateException
                          ("already moved nodes in underlying graph");
      // This is a debugging statement.
      if (nodeIndex < 0 || nodeIndex >= graph.getNumNodes())
        throw new IndexOutOfBoundsException
          ("nodeIndex out of bounds: " + nodeIndex);
      // This is a debugging statement.
      if ((!overrideMovableCheck) && !isMovableNode(nodeIndex))
        throw new UnsupportedOperationException();
      movedNodes.put(nodeIndex, new Point2D.Double(X, Y)); }
    Point2D getNodePosition(int nodeIndex) {
      // This is a debugging statement.
      if (alreadyMoved) throw new IllegalStateException
                          ("already moved nodes in underlying graph");
      // This is a debugging statement.
      if (nodeIndex < 0 || nodeIndex >= graph.getNumNodes())
        throw new IndexOutOfBoundsException
          ("nodeIndex out of bounds: " + nodeIndex);
      Object o = movedNodes.get(new Integer(nodeIndex));
      if (o == null) return new Point2D.Double
                       (graph.getNodePosition(nodeIndex, true),
                        graph.getNodePosition(nodeIndex, false));
      else return (Point2D) o; }
    void moveUnderlyingNodes() {
      if (alreadyMoved) throw new IllegalStateException
                          ("already moved nodes in underlying graph");
      alreadyMoved = true;
      double minX = Double.MAX_VALUE;
      double maxX = Double.MIN_VALUE;
      double minY = Double.MAX_VALUE;
      double maxY = Double.MIN_VALUE;
      // We iterate once through just to find min and max bounds for node pos.
      for (int nodeIx = 0; nodeIx < graph.getNumNodes(); nodeIx++) {
        Point2D nodePos;
        if ((nodePos = (Point2D) movedNodes.get(new Integer(nodeIx))) == null)
          nodePos = new Point2D.Double(graph.getNodePosition(nodeIx, true),
                                       graph.getNodePosition(nodeIx, false));
        minX = Math.min(minX, nodePos.getX());
        maxX = Math.max(maxX, nodePos.getX());
        minY = Math.min(minY, nodePos.getY());
        maxY = Math.max(maxY, nodePos.getY()); }
      // Compute scaling factors.
      double xScaleFactor;
      if (((float) (maxX - minX)) == 0.0) xScaleFactor = 1.0d;
      else xScaleFactor = graph.getMaxWidth() / (maxX - minX);
      double yScaleFactor;
      if (((float) (maxY - minY)) == 0.0) yScaleFactor = 1.0d;
      else yScaleFactor = graph.getMaxHeight() / (maxY - minY);
      // We now know min and max; iterate again to move all nodes.
      for (int nodeIx = 0; nodeIx < graph.getNumNodes(); nodeIx++) {
        Point2D nodePos;
        if ((nodePos = (Point2D) movedNodes.get(new Integer(nodeIx))) != null
            && isMovableNode(nodeIx))
          graph.setNodePosition
            (nodeIx,
             Math.min(Math.max(0, (nodePos.getX() - minX) * xScaleFactor),
                      graph.getMaxWidth()),
             Math.min(Math.max(0, (nodePos.getY() - minY) * yScaleFactor),
                      graph.getMaxHeight())); }
      movedNodes = null;
      graph = null; }
  }

  private static class PartialDerivatives
  {

    final int nodeIndex;
    double x;
    double y;
    double xx;
    double yy;
    double xy;
    double euclideanDistance;

    PartialDerivatives(int nodeIndex)
    {
      this.nodeIndex = nodeIndex;
    }

    PartialDerivatives(PartialDerivatives copyFrom)
    {
      this.nodeIndex = copyFrom.nodeIndex;
      copyFrom(copyFrom);
    }

    void reset ()
    {
      x = 0.0;
      y = 0.0;
      xx = 0.0;
      yy = 0.0;
      xy = 0.0;
      euclideanDistance = 0.0;
    }

    void copyFrom (PartialDerivatives otherPartialDerivatives)
    {
      x = otherPartialDerivatives.x;
      y = otherPartialDerivatives.y;
      xx = otherPartialDerivatives.xx;
      yy = otherPartialDerivatives.yy;
      xy = otherPartialDerivatives.xy;
      euclideanDistance = otherPartialDerivatives.euclideanDistance;
    }

  }

  /*
   * Some notes:
   */
  private static int[][] calculateNodeDistances(MutablePolyEdgeGraphLayout graph)
  {
    final GraphCompiler graphUtils =
      new GraphCompiler(graph,
                        GraphCompiler.COMPILE_NODE_NEIGHBORS);
    int[][] distances = new int[graph.getNumNodes()][];
    Object[] nodes = new Object[graph.getNumNodes()];
    for (int i = 0; i < nodes.length; i++)
      nodes[i] = new Object();
    LinkedList queue = new LinkedList();
    boolean[] completedNodes = new boolean[graph.getNumNodes()];
    IndexIterator neighbors;
    int toNode;
    int neighbor;
    int toNodeDistance;
    int neighborDistance;
    for (int fromNode = 0; fromNode < graph.getNumNodes(); fromNode++)
    {
      if (distances[fromNode] == null)
        distances[fromNode] = new int[nodes.length];
      Arrays.fill(distances[fromNode], Integer.MAX_VALUE);
      distances[fromNode][fromNode] = 0;
      Arrays.fill(completedNodes, false);
      queue.add(new Integer(fromNode));
      while (!(queue.isEmpty()))
      {
        int index = ((Integer) queue.removeFirst()).intValue();
        if (completedNodes[index]) continue;
        completedNodes[index] = true;
        toNode = index;
        toNodeDistance = distances[fromNode][index];
        if (index < fromNode)
        {
          // Oh boy.  We've already got every distance from/to this node.
          int distanceThroughToNode;
          for (int i = 0; i < nodes.length; i++)
          {
            if (distances[index][i] == Integer.MAX_VALUE) continue;
            distanceThroughToNode = toNodeDistance  + distances[index][i];
            if (distanceThroughToNode <= distances[fromNode][i]) {
              // Any immediate neighbor of a node that's already been
              // calculated for that does not already have a shorter path
              // calculated from fromNode never will, and is thus complete.
              if (distances[index][i] == 1) completedNodes[i] = true;
              distances[fromNode][i] = distanceThroughToNode;
            }
          }
          // End for every node, update the distance using the distance
          // from tuNode.  So now we don't need to put any neighbors on the
          // queue or anything, since they've already been taken care of by
          // the previous calculation.
          continue;
        } // End if toNode has already had all of its distances calculated.

        neighbors = graphUtils.getNeighboringNodeIndices(toNode);
        while (neighbors.numRemaining() > 0)
        {
          neighbor = neighbors.next();
          // We've already done everything we can here.
          if (completedNodes[neighbor]) continue;
          neighborDistance = distances[fromNode][neighbor];
          if ((toNodeDistance != Integer.MAX_VALUE) &&
              (neighborDistance > toNodeDistance + 1))
          {
            distances[fromNode][neighbor] = toNodeDistance + 1;
            queue.addLast(new Integer(neighbor));
          }
        }
      }
    }
    return distances;
  }
  
  private PartialDerivatives calculatePartials
    (PartialDerivatives partials,
     List partialsList,
     double[] potentialEnergy,
     boolean reversed,
     final AutoScalingGraphLayout graph)
  {
    partials.reset();
    int node = partials.nodeIndex;
    double nodeRadius = 0.0;
    double nodeX = graph.getNodePosition(node).getX();
    double nodeY = graph.getNodePosition(node).getY();
    PartialDerivatives otherPartials = null;
    int otherNode;
    double otherNodeRadius;
    PartialDerivatives furthestPartials = null;
    Iterator iterator;
    if (partialsList == null)
      iterator = new Iterator() {
          private int ix = 0;
          public void remove() { throw new UnsupportedOperationException(); }
          public boolean hasNext() {
            return ix < graph.getNumNodes(); }
          public Object next() {
            return new Integer(ix++); } };
    else
      iterator = partialsList.iterator();
    double deltaX;
    double deltaY;
    double euclideanDistance;
    double euclideanDistanceCubed;
    double distanceFromRest;
    double distanceFromTouching;
    double incrementalChange;
    while (iterator.hasNext()) {
      if (partialsList == null) {
        otherNode = ((Integer) iterator.next()).intValue(); }
      else {
        otherPartials = (PartialDerivatives) iterator.next();
        otherNode = otherPartials.nodeIndex; }
      if (node == otherNode) continue;
      otherNodeRadius = 0.0;
      while (true) {
        deltaX = nodeX - graph.getNodePosition(otherNode).getX();
        deltaY = nodeY - graph.getNodePosition(otherNode).getY();
        euclideanDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (((float) euclideanDistance) > 0.0001) break;
        graph.setNodePosition
          (true, otherNode,
           graph.getNodePosition(otherNode).getX() +
           (0.001d * (new java.util.Random()).nextDouble()),
           graph.getNodePosition(otherNode).getY() +
           (0.001d * (new java.util.Random()).nextDouble())); }
      euclideanDistanceCubed = Math.pow(euclideanDistance, 3);
      distanceFromTouching =
        euclideanDistance - (nodeRadius + otherNodeRadius);
      incrementalChange =
        (m_nodeDistanceSpringScalars[m_layoutPass] *
         (m_nodeDistanceSpringStrengths[node][otherNode] *
          (deltaX -
           (
            (m_nodeDistanceSpringRestLengths[node][otherNode] *
             deltaX) /
            euclideanDistance
            )
           )
          )
         );
      if (!reversed) partials.x += incrementalChange;
      if (otherPartials != null)
      {
        incrementalChange =
          (m_nodeDistanceSpringScalars[m_layoutPass] *
           (m_nodeDistanceSpringStrengths[otherNode][node] *
            (-deltaX -
             (
              (m_nodeDistanceSpringRestLengths[otherNode][node] *
               -deltaX) /
              euclideanDistance
              )
             )
            )
           );
        if (reversed) otherPartials.x -= incrementalChange;
        else otherPartials.x += incrementalChange;
      }
      if (distanceFromTouching < 0.0)
      {
        incrementalChange =
          (m_anticollisionSpringScalars[m_layoutPass] *
           (m_anticollisionSpringStrength *
            (deltaX -
             (
              ((nodeRadius + otherNodeRadius) *
               deltaX) /
              euclideanDistance
              )
             )
            )
           );
        if (!reversed) partials.x += incrementalChange;
        if (otherPartials != null)
        {
          incrementalChange =
            (m_anticollisionSpringScalars[m_layoutPass] *
             (m_anticollisionSpringStrength *
              (-deltaX -
               (
                ((nodeRadius + otherNodeRadius) *
                 -deltaX) /
                euclideanDistance
                )
               )
              )
             );
          if (reversed) otherPartials.x -= incrementalChange;
          else otherPartials.x += incrementalChange;
        }
      }
      incrementalChange =
        (m_nodeDistanceSpringScalars[m_layoutPass] *
         (m_nodeDistanceSpringStrengths[node][otherNode] *
          (deltaY -
           (
            (m_nodeDistanceSpringRestLengths[node][otherNode] *
             deltaY) /
            euclideanDistance
            )
           )
          )
         );
      if (!reversed) partials.y += incrementalChange;
      if (otherPartials != null)
      {
        incrementalChange =
          (m_nodeDistanceSpringScalars[m_layoutPass] *
           (m_nodeDistanceSpringStrengths[otherNode][node] *
            (-deltaY -
             (
              (m_nodeDistanceSpringRestLengths[otherNode][node] *
               -deltaY) /
              euclideanDistance
              )
             )
            )
           );
        if (reversed) otherPartials.y -= incrementalChange;
        else otherPartials.y += incrementalChange;
      }
      if (distanceFromTouching < 0.0)
      {
        incrementalChange =
          (m_anticollisionSpringScalars[m_layoutPass] *
           (m_anticollisionSpringStrength *
            (deltaY -
             (
              ((nodeRadius + otherNodeRadius) *
               deltaY) /
              euclideanDistance
              )
             )
            )
           );
        if (!reversed) partials.y += incrementalChange;
        if (otherPartials != null)
        {
          incrementalChange =
            (m_anticollisionSpringScalars[m_layoutPass] *
             (m_anticollisionSpringStrength *
              (-deltaY -
               (
                ((nodeRadius + otherNodeRadius) *
                 -deltaY) /
                euclideanDistance
                )
               )
              )
             );
          if (reversed) otherPartials.y -= incrementalChange;
          else otherPartials.y += incrementalChange;
        }
      }
      incrementalChange =
        (m_nodeDistanceSpringScalars[m_layoutPass] *
         (m_nodeDistanceSpringStrengths[node][otherNode] *
          (1.0 -
           (
            (m_nodeDistanceSpringRestLengths[node][otherNode] *
             (deltaY * deltaY)
             ) /
            euclideanDistanceCubed
            )
           )
          )
         );
      if (reversed) {
        if (otherPartials != null) otherPartials.xx -= incrementalChange; }
      else {
        partials.xx += incrementalChange;
        if (otherPartials != null) otherPartials.xx += incrementalChange; }
      if (distanceFromTouching < 0.0)
      {
        incrementalChange =
          (m_anticollisionSpringScalars[m_layoutPass] *
           (m_anticollisionSpringStrength *
            (1.0 -
             (
              ((nodeRadius + otherNodeRadius) *
               (deltaY * deltaY)
               ) /
              euclideanDistanceCubed
              )
             )
            )
           );
        if (reversed) {
          if (otherPartials != null) otherPartials.xx -= incrementalChange; }
        else {
          partials.xx += incrementalChange;
          if (otherPartials != null) otherPartials.xx += incrementalChange; }
      }
      incrementalChange =
        (m_nodeDistanceSpringScalars[m_layoutPass] *
         (m_nodeDistanceSpringStrengths[node][otherNode] *
          (1.0 -
           (
            (m_nodeDistanceSpringRestLengths[node][otherNode] *
             (deltaX * deltaX)
             ) /
            euclideanDistanceCubed
            )
           )
          )
         );
      if (reversed) {
        if (otherPartials != null) otherPartials.yy -= incrementalChange; }
      else {
        partials.yy += incrementalChange;
        if (otherPartials != null) otherPartials.yy += incrementalChange; }
      if (distanceFromTouching < 0.0)
      {
        incrementalChange =
          (m_anticollisionSpringScalars[m_layoutPass] *
           (m_anticollisionSpringStrength *
            (1.0 -
             (
              ((nodeRadius + otherNodeRadius) *
               (deltaX * deltaX)
               ) /
              euclideanDistanceCubed
              )
             )
            )
           );
        if (reversed) {
          if (otherPartials != null) otherPartials.yy -= incrementalChange; }
        else {
          partials.yy += incrementalChange;
          if (otherPartials != null) otherPartials.yy += incrementalChange; }
      }
      incrementalChange =
        (m_nodeDistanceSpringScalars[m_layoutPass] *
         (m_nodeDistanceSpringStrengths[node][otherNode] *
          ((m_nodeDistanceSpringRestLengths[node][otherNode] *
            (deltaX * deltaY)
            ) /
           euclideanDistanceCubed
           )
          )
         );
      if (reversed) {
        if (otherPartials != null) otherPartials.xy -= incrementalChange; }
      else {
        partials.xy += incrementalChange;
        if (otherPartials != null) otherPartials.xy += incrementalChange; }
      if (distanceFromTouching < 0.0)
      {
        incrementalChange =
          (m_anticollisionSpringScalars[m_layoutPass] *
           (m_anticollisionSpringStrength *
            (
             ((nodeRadius + otherNodeRadius) *
              (deltaX * deltaY)
              ) /
             euclideanDistanceCubed
             )
            )
           );
        if (reversed) {
          if (otherPartials != null) otherPartials.xy -= incrementalChange; }
        else {
          partials.xy += incrementalChange;
          if (otherPartials != null) otherPartials.xy += incrementalChange;
        }
      }
      distanceFromRest =
        (euclideanDistance -
         m_nodeDistanceSpringRestLengths[node][otherNode]
         );
      incrementalChange =
        (m_nodeDistanceSpringScalars[m_layoutPass] *
         ((m_nodeDistanceSpringStrengths[node][otherNode] *
           (distanceFromRest * distanceFromRest)
           ) /
          2
          )
         );
      if (reversed) {
        if (otherPartials != null) potentialEnergy[0] -= incrementalChange; }
      else {
        potentialEnergy[0] += incrementalChange;
        if (otherPartials != null) potentialEnergy[0] += incrementalChange; }
      if (distanceFromTouching < 0.0)
      {
        incrementalChange =
          (m_anticollisionSpringScalars[m_layoutPass] *
           ((m_anticollisionSpringStrength *
             (distanceFromTouching * distanceFromTouching)
             ) /
            2
            )
           );
        if (reversed) {
          if (otherPartials != null) potentialEnergy[0] -= incrementalChange; }
        else {
          potentialEnergy[0] += incrementalChange;
          if (otherPartials != null) potentialEnergy[0] += incrementalChange; }
      }
      if (otherPartials != null)
      {
        otherPartials.euclideanDistance =
          Math.sqrt((otherPartials.x * otherPartials.x) +
                    (otherPartials.y * otherPartials.y));
        if ((furthestPartials == null) ||
            (otherPartials.euclideanDistance >
             furthestPartials.euclideanDistance))
          furthestPartials = otherPartials;
      }
    }
    if (!reversed)
      partials.euclideanDistance =
        Math.sqrt((partials.x * partials.x) +
                  (partials.y * partials.y));
    if ((furthestPartials == null) ||
        (partials.euclideanDistance >
         furthestPartials.euclideanDistance))
      furthestPartials = partials;
    return furthestPartials;
  }

  private PartialDerivatives moveNode
    (PartialDerivatives partials,
     List partialsList,
     double[] potentialEnergy,
     AutoScalingGraphLayout graph)
  {
    int node = partials.nodeIndex;
    PartialDerivatives startingPartials = new PartialDerivatives(partials);
    calculatePartials(partials, partialsList, potentialEnergy, true, graph);
    simpleMoveNode(startingPartials, graph);
    return calculatePartials(partials, partialsList, potentialEnergy, false,
                             graph);
  }

  private static void simpleMoveNode(PartialDerivatives partials,
                                     AutoScalingGraphLayout graph)
  {
    int node = partials.nodeIndex;
    double denominator =
      ((partials.xx * partials.yy) -
       (partials.xy * partials.xy));
    if (((float) denominator) == 0.0)
      throw new RuntimeException("denominator too close to 0");
    double deltaX =
      (
       ((-partials.x * partials.yy) -
        (-partials.y * partials.xy)) /
       denominator
       );
    double deltaY =
      (
       ((-partials.y * partials.xx) -
        (-partials.x * partials.xy)) /
       denominator
       );
    Point2D p = graph.getNodePosition(node);
    graph.setNodePosition(false, node, p.getX() + deltaX, p.getY() + deltaY);
  }

  /**
   * This starts the layout process.  This method is called by a parent
   * application using this layout algorithm.
   **/
  public void run()
  {
    if (m_halt) return;
    m_taskMonitor.setStatus("Creating data structures");

    // Stop if all nodes are closer together than this euclidean distance.
    final double euclideanDistanceThreshold =
      (0.5 * (m_nodeCount + m_edgeCount));

    final int numIterations = (int)
      ((m_nodeCount * m_averageIterationsPerNode) / m_numLayoutPasses);

    List partialsList = new ArrayList();
    double[] potentialEnergy = new double[1];
    if (potentialEnergy[0] != 0.0) throw new RuntimeException();

    PartialDerivatives partials;
    PartialDerivatives furthestNodePartials = null;

    m_nodeDistanceSpringRestLengths = new double[m_nodeCount][m_nodeCount];
    m_nodeDistanceSpringStrengths = new double[m_nodeCount][m_nodeCount];

    if (m_halt) return;
    m_taskMonitor.setPercentCompleted(2);
    m_taskMonitor.setStatus("Calculating node distances");

    {
      final java.util.Random rand = new java.util.Random();
      final double maxX = m_graph.getMaxWidth();
      final double maxY = m_graph.getMaxHeight();
      for (int i = 0; i < m_graph.getNumNodes(); i++) {
        if (m_graph.isMovableNode(i)) {
          final double deltaX =
            ((double) rand.nextInt()) / 0x80000000;
          final double deltaY =
            ((double) rand.nextInt()) / 0x80000000;
          final double oldX = m_graph.getNodePosition(i, true);
          final double oldY = m_graph.getNodePosition(i, false);
          double newX = oldX + deltaX;
          double newY = oldY + deltaY;
          newX = Math.max(0.0d, newX);
          newY = Math.max(0.0d, newY);
          newX = Math.min(newX, maxX);
          newY = Math.min(newY, maxY);
          m_graph.setNodePosition(i, newX, newY); } }
    }

    int[][] nodeDistances = calculateNodeDistances((MutablePolyEdgeGraphLayout)m_graph);

    if (m_halt) return;
    m_taskMonitor.setPercentCompleted(4);
    m_taskMonitor.setStatus("Calculating spring constants");

    // Calculate rest lengths and strengths based on node distance data.
    for (int node_i = 0; node_i < m_nodeCount; node_i++)
    {
      for (int node_j = (node_i + 1); node_j < m_nodeCount; node_j++)
      {
        // BEGIN: Compute spring rest lengths.
        if (nodeDistances[node_i][node_j] == Integer.MAX_VALUE) {
          m_nodeDistanceSpringRestLengths[node_i][node_j] =
            m_disconnectedNodeDistanceSpringRestLength; }
        else {
          m_nodeDistanceSpringRestLengths[node_i][node_j] =
            m_nodeDistanceRestLengthConstant * nodeDistances[node_i][node_j]; }
        m_nodeDistanceSpringRestLengths[node_j][node_i] =
          m_nodeDistanceSpringRestLengths[node_i][node_j];
        // END: Compute spring rest lengths.

        // BEGIN: Compute spring strengths.
        if (nodeDistances[node_i][node_j] == Integer.MAX_VALUE) {
          m_nodeDistanceSpringStrengths[node_i][node_j] =
            m_disconnectedNodeDistanceSpringStrength; }
        else {
          m_nodeDistanceSpringStrengths[node_i][node_j] =
            m_nodeDistanceStrengthConstant /
            (nodeDistances[node_i][node_j] * nodeDistances[node_i][node_j]); }
        m_nodeDistanceSpringStrengths[node_j][node_i] =
          m_nodeDistanceSpringStrengths[node_i][node_j];
        // END: Compute spring strengths.
      }
    }

    final double percentCompletedBeforePasses = 5.0d;
    final double percentCompletedAfterPasses = 95.0d;
    // The second pass takes little time, and the ratio of time for second pass
    // to time for first pass approaches zero as the graph gets large.
    // Therefore we don't report progress on any pass but the first.

    for (m_layoutPass = 0; m_layoutPass < m_numLayoutPasses; m_layoutPass++)
    {
      double currentProgress = percentCompletedBeforePasses;
      final double percentProgressPerIter =
        (percentCompletedAfterPasses - percentCompletedBeforePasses) /
        (double) (m_nodeCount + numIterations);

      // Initialize this layout pass.
      potentialEnergy[0] = 0.0;
      partialsList.clear();
      furthestNodePartials = null;

      // Calculate all node distances.  Keep track of the furthest.
      for (int nodeIndex = 0; nodeIndex < m_nodeCount; nodeIndex++)
      {
        if (!m_autoScaleGraph.isMovableNode(nodeIndex)) continue;
        if (m_halt) return;
        if (m_layoutPass == 0) {
          m_taskMonitor.setPercentCompleted((int) currentProgress);
          m_taskMonitor.setStatus("Calculating partial derivatives"); }

        partials = new PartialDerivatives(nodeIndex);
        calculatePartials(partials, null, potentialEnergy, false,
                          m_autoScaleGraph);
        partialsList.add(partials);
        if ((furthestNodePartials == null) ||
            (partials.euclideanDistance >
             furthestNodePartials.euclideanDistance)) {
          furthestNodePartials = partials; }

        if (m_layoutPass == 0) currentProgress += percentProgressPerIter;
      }
      for (int iterations_i = 0; 
           (iterations_i < numIterations) &&
             (furthestNodePartials.euclideanDistance >=
              euclideanDistanceThreshold);
           iterations_i++)
      {
        if (m_halt) return;
        if (m_layoutPass == 0) {
          m_taskMonitor.setPercentCompleted((int) currentProgress);
          m_taskMonitor.setStatus("Executing spring logic"); }
        
        furthestNodePartials = moveNode(furthestNodePartials, partialsList,
                                        potentialEnergy,
                                        m_autoScaleGraph);

        if (m_layoutPass == 0) currentProgress += percentProgressPerIter;
      }
    }

    if (m_halt) return;
    m_taskMonitor.setPercentCompleted((int) percentCompletedAfterPasses);
    m_taskMonitor.setStatus("Moving nodes in underlying network");

    // The last thing we do is trigger node movement in the underlying graph.
    m_autoScaleGraph.moveUnderlyingNodes();

    m_taskMonitor.setPercentCompleted(100);
    m_taskMonitor.setStatus("Finished");
  }

  /**
   * Signals to a running layout that it's time to abort and exit.  This
   * method is called by a parent application using this layout algorithm.
   * This method will return immediately when called.  <code>run()</code>
   * will return eventually, and soon after calling <code>halt()</code>.
   **/
  public void halt()
  {
    m_halt = true;
  }

}
