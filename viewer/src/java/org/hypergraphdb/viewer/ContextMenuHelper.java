package org.hypergraphdb.viewer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.painter.DefaultEdgePainter;
import org.hypergraphdb.viewer.painter.DefaultNodePainter;
import org.hypergraphdb.viewer.painter.EdgePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.visual.VisualStyle;
import org.hypergraphdb.viewer.visual.ui.PainterPropsPanel;

import org.hypergraphdb.viewer.phoebe.PNodeView;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * The Piccolo handler responsible for showing popup(context) menu
 */
public class ContextMenuHelper extends PBasicInputEventHandler
{
    protected JPopupMenu global_menu;
    GraphView view;

    public ContextMenuHelper(GraphView view)
    {
        this.view = view;
    }

    public void mousePressed(PInputEvent event)
    {
        if (!(event.getPickedNode() instanceof PCamera))
        {
            event.setHandled(true);
            showMenu(event);
        }
    }

    private void showMenu(PInputEvent event)
    {
        if(!(event.getPickedNode() instanceof PNodeView)) return;
        PCanvas canvas = (PCanvas) event.getComponent();
        PNodeView node = (PNodeView) event.getPickedNode();
        Frame f = GUIUtilities.getFrame(canvas);
        createPopup(node);
        Point pt = new Point((int) event.getCanvasPosition().getX(),
                (int) event.getCanvasPosition().getY());
        if(HGVKit.isEmbeded()) 
           pt = GUIUtilities.adjustPointInPicollo(canvas, pt);
        //move a little from the right position to avoid an infinite
        //zoom clash that happen sometimes
        global_menu.show(canvas, pt.x + 10, pt.y);
     }
    
     private void createPopup(final PNodeView node)
     {
         global_menu = new JPopupMenu();
         global_menu.add(new JMenuItem(new AbstractAction("Expand") {
             public void actionPerformed(ActionEvent e)
             {
                 GraphViewU.expandNodes();
                 //removeExtraNodes(node, 150);
                 adjust(node);
             }
         }));
         global_menu.add(new JMenuItem(new AbstractAction("Focus") {
             public void actionPerformed(ActionEvent e)
             {
                 view.getViewer().focus(node.getNode().getHandle());
             }
         }));
         JMenu menu = new JMenu("Add Painter");
         JMenuItem menuItem = new JMenuItem("Node");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 addNodePainter(view, node);
             }
         });
         menu.add(menuItem);
         menuItem = new JMenuItem("Edge");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 addEdgePainter(view, node);
             }
         });
         menu.add(menuItem);
         global_menu.add(menu);
//         menuItem = new JMenuItem("Copy Class");
//         menuItem.addActionListener(new ActionListener() {
//             public void actionPerformed(ActionEvent e)
//             {
//                 HyperGraph hg = view.getHyperGraph();
//                 HGHandle h = node.getNode().getHandle();
//                 Object o = hg.get(h);
//                 putInClipboard(o.getClass().getName());
//             }
//         });
//         global_menu.add(menuItem);
         menuItem = new JMenuItem("Copy Handle");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 HyperGraph hg = view.getHyperGraph();
                 HGHandle h = node.getNode().getHandle();
                 putInClipboard("" + hg.getPersistentHandle(h));
             }
         });  
         global_menu.add(menuItem);
         
         if(!HGVKit.isEmbeded()) return;
         menuItem = new JMenuItem("Properties");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 properties(view, node);
             }
         });  
         global_menu.add(menuItem);
     }
      
    
    // This method writes a string to the system clipboard.
    private void putInClipboard(String s) {
        StringSelection ss = new StringSelection(s);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

     static JMenuItem expandNodeAction(final GraphView v,
            final PNodeView node)
    {

        return new JMenuItem(new AbstractAction("Expand") {
            public void actionPerformed(ActionEvent e)
            {
                GraphViewU.expandNodes();
                //removeExtraNodes(v, node, 150);
                adjust(node);
            }
        });
    }

    private static void adjust(PNode node)
    {
        GraphView view = HGVKit.getCurrentView();
        HGVKit.getPreferedLayout().applyLayout(view);
        view.getCanvas().getCamera().animateViewToCenterBounds(
                node.getFullBounds(), false, 1550l);
    }

    // static int def = 15;
    private static void removeExtraNodes(GraphView view, PNode node, int def)
    {
        if (view.getNodeViewCount() < def) return;
        TreeMap<Double, PNodeView> nodes = new TreeMap<Double, PNodeView>();
        Point2D n = node.getFullBounds().getCenter2D();

        for (PNodeView nview : view.getNodeViewsCopy())
            nodes.put(n.distance(nview.getOffset()), nview);
        
        // System.out.println("FNode count: " +  view.nodeCount());
        for (int i = 0; nodes.size() - def > 0; i++)
        {
            Double key = nodes.lastKey();
            FNode nn = nodes.get(key).getNode();
            GraphViewU.removeNode(view.getHyperGraph(), nn, true);
            nodes.remove(key);
        }
    }
    
    private static void properties(final GraphView view, final PNodeView node)
    {
        Object obj = view.getHyperGraph().get(node.getNode().getHandle());
        ObjectInspector propsPanel = new ObjectInspector(obj);
        DialogDescriptor dd = new DialogDescriptor(
                GUIUtilities.getFrame(), 
                new JScrollPane(propsPanel),
              "Properties: " + ((obj == null)? 
                      "null" : obj.getClass().getName()));
        DialogDisplayer.getDefault().notify(dd);
    }
    
    private static void addNodePainter(final GraphView view, final PNodeView node)
    {
        HyperGraph hg = view.getHyperGraph();
        HGHandle h = node.getNode().getHandle();
        Object o = hg.get(h);
        h = hg.getTypeSystem().getTypeHandle(o.getClass());
        if(h == null) return;
        VisualStyle vs = view.getVisualStyle();
        NodePainter p = vs.getNodePainter(h);
        if(p == null)
            p = new DefaultNodePainter();
        PainterPropsPanel propsPanel = new PainterPropsPanel();
        propsPanel.setBorder(new TitledBorder(null, "Properties",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        propsPanel.setMinimumSize(new Dimension(300, 300));
        propsPanel.setPreferredSize(new Dimension(300, 300));
        propsPanel.setPainter(p);
        
        DialogDescriptor dd = new DialogDescriptor(
                GUIUtilities.getFrame(), propsPanel,"Node Painter Properties");
        if(DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION)
        {
            vs.addNodePainter(h, p);
            VisualManager.getInstance().save();
            view.redrawGraph();
        }
    }
    
    private static void addEdgePainter(final GraphView view, final PNodeView node)
    {
        HyperGraph hg = view.getHyperGraph();
        HGHandle h = node.getNode().getHandle();
        Object o = hg.get(h);
        h = hg.getTypeSystem().getTypeHandle(o.getClass());
        if(h == null) return;
        VisualStyle vs = view.getVisualStyle();
        EdgePainter p = vs.getEdgePainter(h);
        if(p == null)
            p = new DefaultEdgePainter();
        PainterPropsPanel propsPanel = new PainterPropsPanel();
        propsPanel.setBorder(new TitledBorder(null, "Properties",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        propsPanel.setMinimumSize(new Dimension(300, 300));
        propsPanel.setPreferredSize(new Dimension(300, 300));
        propsPanel.setPainter(p);
        
        DialogDescriptor dd = new DialogDescriptor(
                GUIUtilities.getFrame(), propsPanel,
                "Edge Painter Properties");
        if(DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION)
        {
            vs.addEdgePainter(h, p);
            VisualManager.getInstance().save();
            view.redrawGraph();
        }
    }
}
