package org.hypergraphdb.viewer.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.hg.HGVUtils;
import org.hypergraphdb.viewer.painter.DefaultNodePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.visual.VisualStyle;
import org.hypergraphdb.viewer.visual.ui.PainterPropsPanel;

import phoebe.PNodeView;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class ContextMenuHelper extends PBasicInputEventHandler
{
    protected JPopupMenu global_menu;
    HGVNetworkView view;

    public ContextMenuHelper(HGVNetworkView view)
    {
        this.view = view;
    }

    public void mousePressed(PInputEvent event)
    {
        if (event.getPickedNode() instanceof PCamera != true)
        {
            event.setHandled(true);
            showMenu(event);
        }
    }

    private void showMenu(PInputEvent event)
    {
        PNodeView node = (PNodeView) event.getPickedNode();
        createPopup(node);
        Point pt = new Point((int) event.getCanvasPosition().getX(),
                (int) event.getCanvasPosition().getY());
        pt = GUIUtilities.adjustPointInPicollo((PCanvas) event.getComponent(), pt);
        global_menu.show(GUIUtilities.getFrame((PCanvas) event.getComponent()),
                pt.x + 10, pt.y);
     }
    
     private void createPopup(final PNodeView node)
     {
         global_menu = new JPopupMenu();
         global_menu.add(expandNodeAction(view, node));
         //global_menu.add(collapseNodeAction(view, node));
         global_menu.add(focusNodeAction(view, node));
         JMenuItem menuItem = new JMenuItem("Add Painter");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 HyperGraph hg = view.getHyperGraph();
                 HGHandle h = node.getNode().getHandle();
                 Object o = hg.get(h);
                 
                 //putInClipboard(o.getClass().getName());
                 
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
                         GUIUtilities.getFrame(), propsPanel,
                 ActionManager.VISUAL_PROPERTIES_ACTION);
                 if(DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION)
                 {
                     vs.addNodePainter(h, p);
                     VisualManager.getInstance().save();
                     view.redrawGraph();
                 }
//                 final PaintersPanel p = new PaintersPanel();
//                 p.setView(view);
//                 DialogDescriptor dd = new DialogDescriptor(GUIUtilities.getFrame(), p,
//                            ActionManager.VISUAL_PROPERTIES_ACTION);
//                 DialogDisplayer.getDefault().notify(dd);
//                 SwingUtilities.invokeLater(new Runnable()
//                 {
//                     public void run()
//                     { 
//                         p.addPainter(class_name);
//                     }
//                 });
//                
//                 view.redrawGraph();
             }
         });
         global_menu.add(menuItem);
         menuItem = new JMenuItem("Copy Class");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 HyperGraph hg = view.getHyperGraph();
                 HGHandle h = node.getNode().getHandle();
                 Object o = hg.get(h);
                 putInClipboard(o.getClass().getName());
             }
         });
         menuItem = new JMenuItem("Copy Handle");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
                 HyperGraph hg = view.getHyperGraph();
                 HGHandle h = node.getNode().getHandle();
                 putInClipboard("" + hg.getPersistentHandle(h));
             }
         });   
                 
     }
      
    
    // This method writes a string to the system clipboard.
    // otherwise it returns null.
    private void putInClipboard(String s) {
        StringSelection ss = new StringSelection(s);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

     static JMenuItem expandNodeAction(final HGVNetworkView v,
            final PNodeView node)
    {

        return new JMenuItem(new AbstractAction("Expand") {
            public void actionPerformed(ActionEvent e)
            {
                HGVUtils.expandNodes();
                //removeExtraNodes(node, 150);
                adjust(node);
            }
        });
    }

    static JMenuItem collapseNodeAction(final HGVNetworkView v, final PNodeView node)
    {

        return new JMenuItem(new AbstractAction("Collapse") {
            public void actionPerformed(ActionEvent e)
            {
                HGVUtils.collapseNode(v.getHyperGraph(),
                        node.getNode());
                adjust(node);
            }
        });
    }

    static JMenuItem focusNodeAction(final HGVNetworkView v, final PNodeView node)
    {
        return new JMenuItem(new AbstractAction("Focus") {
            public void actionPerformed(ActionEvent e)
            {
                v.getComponent().focus(node.getNode().getHandle());
            }
        });
    }

    private static void adjust(PNode node)
    {
        HGVNetworkView view = HGVKit.getCurrentView();
        HGVKit.getPreferedLayout().applyLayout(view);
        view.getCanvas().getCamera().animateViewToCenterBounds(
                node.getFullBounds(), false, 1550l);
    }

    // static int def = 15;
    private static void removeExtraNodes(PNode node, int def)
    {
        if (HGVKit.getCurrentView().getNodeViewCount() < def) return;
        TreeMap<Double, PNodeView> nodes = new TreeMap<Double, PNodeView>();
        java.awt.geom.Point2D n = node.getFullBounds().getCenter2D();

        for (PNodeView view : HGVKit.getCurrentView().getNodeViews())
            nodes.put(n.distance(view.getOffset()), view);
        
        // System.out.println("FNode count: " +
        // HGVKit.getCurrentView().nodeCount());
        for (int i = 0; nodes.size() - def > 0; i++)
        {
            Double key = nodes.lastKey();
            FNode nn = nodes.get(key).getNode();
            HGVUtils.removeNode(HGVKit.getCurrentView().getHyperGraph(), nn,
                    true);
            nodes.remove(key);
        }
    }
}
