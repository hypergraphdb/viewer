package org.hypergraphdb.viewer;

import java.util.*;

import phoebe.PEdgeView;
import phoebe.PNodeView;

/**
 * Utility operations for selection and hiding/unhiding nodes and edges in a
 * GraphView. Most operations are self-explanatory.
 */
public class GraphViewU
{

    public static void hideSelectedNodes(GraphView view)
    {
        // hides nodes and edges between them
        Set<FEdge> del_edges = new HashSet<FEdge>(); 
        List<PNodeView> sel_nodes = view.getSelectedNodes();
        for (PNodeView nview : sel_nodes)
        {
           FEdge[] edges = view.getAdjacentEdges(nview.getNode(),
                    true, true);
          for (int j = 0; j < edges.length; j++)
               del_edges.add(edges[j]);
        }
        for(FEdge e: del_edges)
            view.removeEdgeView(e);
        for (PNodeView nview : sel_nodes)
            view.removeNodeView(nview.getNode()); 
    }

  
    public static void hideSelectedEdges(GraphView view)
    {
        for (PEdgeView eview : view.getSelectedEdges())
            view.removeEdgeView(eview);
    }

   
    public static void invertSelectedNodes(GraphView view)
    {
        for (PNodeView nview : view.getSelectedNodes())
            nview.setSelected(!nview.isSelected());
    }

    public static void invertSelectedEdges(GraphView view)
    {
        for (PEdgeView eview : view.getEdgeViews())
            eview.setSelected(!eview.isSelected());
    }

    public static void selectAllNodes(GraphView view)
    {
           for (PNodeView nview : view.getSelectedNodes())
            nview.setSelected(true);
    }

    public static void deselectAllNodes(GraphView view)
    {
        for (PNodeView nview : view.getNodeViews())
            nview.setSelected(false);
    }

    public static void selectAllEdges(GraphView view)
    {
        for (PEdgeView eview : view.getEdgeViews())
            eview.setSelected(true);
    }

    public static void deselectAllEdges(GraphView view)
    {
        for (PEdgeView eview : view.getEdgeViews())
            eview.setSelected(false);
    }

    public static void hideAllEdges(GraphView view)
    {
        for (PEdgeView eview : view.getEdgeViews())
           view.removeEdgeView(eview);
    }
}
