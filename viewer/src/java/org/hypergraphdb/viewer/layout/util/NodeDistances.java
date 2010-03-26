package org.hypergraphdb.viewer.layout.util;

import java.util.*;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.GraphView;

import org.hypergraphdb.viewer.phoebe.PNodeView;

/**
 * Calculates the all-pairs-shortest-paths (APSP) of a set of
 * <code>org.hypergraphdb.viewer.FNode</code> objects that reside in a
 * <code>org.hypergraphdb.viewer.GraphView</code>.
 * 
 * @see giny.util.IntNodeDistances
 */
public class NodeDistances // implements MonitorableTask
{

    public static final int INFINITY = Integer.MAX_VALUE;

    protected List<PNodeView> nodesList;
    protected GraphView perspective;
    protected int[][] distances;
    protected boolean directed;

    protected HashMap<PNodeView, Integer> nodeIndexToMatrixIndexMap; // a root
                                                                     // node
                                                                     // index to
                                                                     // matrix
                                                                     // index
                                                                     // map

    /**
     * The main constructor
     * 
     * @param nodesList
     *            List of nodes ordered by the index map
     * @param perspective
     *            The <code>giny.model.GraphPerspective</code> in which the
     *            nodes reside
     * @param nodeIndexToMatrixIndexMap
     *            An index map that maps your root graph indices to the returned
     *            matrix indices
     */
    public NodeDistances(List<PNodeView> nodesList, GraphView perspective,
            HashMap<PNodeView, Integer> nodeIndexToMatrixIndexMap)
    {
        this.nodesList = nodesList;
        this.nodeIndexToMatrixIndexMap = nodeIndexToMatrixIndexMap;
        this.perspective = perspective;
        this.distances = new int[nodesList.size()][];
        this.directed = false;
    }

    /**
     * Calculates the node distances.
     * 
     * @return the <code>int[][]</code> array of calculated distances or null if
     *         the task was canceled or there was an error
     */
    public int[][] calculate()
    {
        PNodeView[] nodes = new PNodeView[nodesList.size()];
        // We don't have to make new Integers all the time, so we store the
        // index Objects in this array for reuse.
        Integer[] integers = new Integer[nodes.length];

        // Fill the nodes array with the nodes in their proper index locations.
        for (int i = 0; i < nodes.length; i++)
        {
            PNodeView from_node = nodesList.get(i);
            if (from_node == null) continue;
            int index = nodeIndexToMatrixIndexMap.get(from_node);

            if ((index < 0) || (index >= nodes.length))
            {
                System.err.println("WARNING: GraphNode \"" + from_node
                        + "\" has an index value that is out of range: "
                        + index + ".  Graph indices should be maintained such "
                        + "that no index is unused.");
                return null;
            }
            if (nodes[index] != null)
            {
                System.err.println("WARNING: GraphNode \"" + from_node
                        + "\" has an index value ( " + index
                        + " ) that is the same as "
                        + "that of another GraphNode ( \"" + nodes[index]
                        + "\" ).  Graph indices should be maintained such "
                        + "that indices are unique.");
                return null;
            }
            nodes[index] = from_node;
            integers[index] = index;
        }

        LinkedList<Integer> queue = new LinkedList<Integer>();
        boolean[] completed_nodes = new boolean[nodes.length];
        
        for (int from_node_index = 0; from_node_index < nodes.length; from_node_index++)
        {
            PNodeView from_node = nodes[from_node_index];
            if (from_node == null)
            {
                // Make the distances in this row all Integer.MAX_VALUE.
                if (distances[from_node_index] == null)
                    distances[from_node_index] = new int[nodes.length];
                Arrays.fill(distances[from_node_index], Integer.MAX_VALUE);
                continue;
            }

            // Make the distances row and initialize it.
            if (distances[from_node_index] == null)
                distances[from_node_index] = new int[nodes.length];
           
            Arrays.fill(distances[from_node_index], Integer.MAX_VALUE);
            distances[from_node_index][from_node_index] = 0;

            // Reset the completed nodes array.
            Arrays.fill(completed_nodes, false);

            // Add the start node to the queue.
            queue.add(integers[from_node_index]);

            while (!(queue.isEmpty()))
            {
                int index = queue.removeFirst();
                if (completed_nodes[index])
                    continue;
                completed_nodes[index] = true;

                PNodeView to_node = nodes[index];
                int to_node_distance = distances[from_node_index][index];

                if (index < from_node_index)
                {
                    // Oh boy. We've already got every distance from/to this
                    // node.
                    int distance_through_to_node;
                    for (int i = 0; i < nodes.length; i++)
                    {
                        if (distances[index][i] == Integer.MAX_VALUE)
                            continue;
                        distance_through_to_node = to_node_distance
                                + distances[index][i];
                        if (distance_through_to_node <= distances[from_node_index][i])
                        {
                            // Any immediate neighbor of a node that's already
                            // been
                            // calculated for that does not already have a
                            // shorter path
                            // calculated from from_node never will, and is thus
                            // complete.
                            if (distances[index][i] == 1)
                                 completed_nodes[i] = true;
                            distances[from_node_index][i] = distance_through_to_node;
                        }
                    } // End for every node, update the distance using the
                      // distance from to_node.
                    // So now we don't need to put any neighbors on the queue or
                    // anything, since they've already been taken care of by the
                    // previous
                    // calculation.
                    continue;
                } // End if to_node has already had all of its distances
                  // calculated.

                Set<FNode> new_set = new HashSet<FNode>();
                FEdge[] ids = perspective.getAdjacentEdges(to_node.getNode(),
                        true, true);
                for (int i = 0; i < ids.length; i++)
                {
                    FEdge edge = ids[i];
                    new_set.add(edge.getTarget());
                    new_set.add(edge.getSource());
                }
                new_set.remove(to_node.getNode());

                for (FNode neighbor : new_set)
                {
                    int neighbor_index = nodeIndexToMatrixIndexMap.get(
                            perspective.getNodeView(neighbor));
                    // If this neighbor was not in the incoming List, we cannot
                    // include it in any paths.
                    if (nodes[neighbor_index] == null)
                    {
                        distances[from_node_index][neighbor_index] = Integer.MAX_VALUE;
                        continue;
                    }

                    if (completed_nodes[neighbor_index])
                        // We've already done everything we can here.
                        continue;
                 
                    int neighbor_distance = distances[from_node_index][neighbor_index];
                    if ((to_node_distance != Integer.MAX_VALUE)
                            && (neighbor_distance > (to_node_distance + 1)))
                    {
                        distances[from_node_index][neighbor_index] = (to_node_distance + 1);
                        queue.addLast(integers[neighbor_index]);
                    }
                } // For each of the next nodes' neighbors
            } // For each to_node, in order of their (present) distances
        } // For each from_node
        return distances;
    }// calculate

    /**
     * @return the <code>int[][]</code> 2D array of calculated distances or null
     *         if not yet calculated
     */
    public int[][] getDistances()
    {
        return this.distances;
    }// getDistances
}
