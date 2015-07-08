package org.hypergraphdb.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.util.GUIUtilities;

import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.PNodeView;

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
        Collection<PNodeView> sel_nodes = new ArrayList<PNodeView>(view.getSelectedNodes());
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
        for (PEdgeView eview : new ArrayList<PEdgeView>(view.getSelectedEdges()))
            view.removeEdgeView(eview);
    }

   
    public static void invertSelectedNodes(GraphView view)
    {
        for (PNodeView nview : new ArrayList<PNodeView>(view.getSelectedNodes()))
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
    
    public static void expandNodes()
    {
        GraphView view = HGVKit.getCurrentView();
        // select the view, because the popup doesn't do this automaticaly
        for(PNodeView v : view.getSelectedNodes())
            expandNode(v);
        
        view.redrawGraph();
    }
    
    private static void expandNode(PNodeView node)
    {
        HyperGraph graph = node.getGraphView().getHyperGraph();
        IncidenceSet in_links = graph.getIncidenceSet(node.getNode().getHandle());
        HGHandle[] out_links = new HGHandle[0];
        Object obj = graph.get(node.getNode().getHandle());
        if (obj instanceof HGLink){
            out_links = new HGHandle[((HGLink) obj).getArity()];
                for(int i = 0; i < out_links.length; i++)
                    out_links[i] = ((HGLink) obj).getTargetAt(i);
            }
        if(!confirmExpanding(in_links.size() + out_links.length)) return;
        expandLinks(node.getGraphView(), node.getNode(), out_links, false, true);
        HGHandle[] inA = in_links.toArray(new HGHandle[0]);
        expandLinks(node.getGraphView(), node.getNode(), inA, true, false);
    }
    
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
    
    public static void collapseNode(HyperGraph hg, FNode node)
    {
        removeNode(hg, node, false);
    }
    
//   The node should be presented in HG, this method removes it from the View
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
    
    public static void invokeLater(final Runnable r)
    { 
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SwingUtilities.invokeLater(
                new Runnable()
                { 
                    public void run(){
                       Thread.currentThread().setContextClassLoader(
                               HGViewer.class.getClassLoader()); 
                        r.run();
                        // restore class laoder
                        Thread.currentThread().setContextClassLoader(cl); 
                    }
                   });
      }
}
