package org.hypergraphdb.viewer.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.hg.HGVUtils;
import org.hypergraphdb.viewer.util.GUIUtilities;

import phoebe.PGraphView;
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
        global_menu = new JPopupMenu();
        PNodeView node = (PNodeView) event.getPickedNode();
        global_menu.add(expandNodeAction(view, node));
        global_menu.add(collapseNodeAction(view, node));
        global_menu.add(focusNodeAction(view, node));
        Point pt = new Point((int) event.getCanvasPosition().getX(),
                (int) event.getCanvasPosition().getY());
        pt = GUIUtilities.adjustPointInPicollo((PCanvas) event.getComponent(),
                pt);
        System.out.println("ContextMenuHelper: showMenu");
        global_menu.show(GUIUtilities.getFrame((PCanvas) event.getComponent()),
                pt.x + 10, pt.y);
    }

     static JMenuItem expandNodeAction(final HGVNetworkView v,
            final PNodeView node)
    {

        return new JMenuItem(new AbstractAction("Expand") {
            public void actionPerformed(ActionEvent e)
            {
                HGVUtils.expandNode(
                        v.getNetwork().getHyperGraph(),  node.getNode());
                int def = 150;
                removeExtraNodes(node, def);
                adjust(node);
            }
        });
    }

    static JMenuItem collapseNodeAction(final HGVNetworkView v, final PNodeView node)
    {

        return new JMenuItem(new AbstractAction("Collapse") {
            public void actionPerformed(ActionEvent e)
            {
                HGVUtils.collapseNode(v.getNetwork().getHyperGraph(),
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
                HGViewer vvv = (HGViewer) v.getComponent().getClientProperty("HG_VIEWER");
                vvv.normal_focus(node.getNode().getHandle());
            }
        });
    }

    private static void adjust(PNode node)
    {
        HGVKit.getPreferedLayout().applyLayout();
        PGraphView view = HGVKit.getCurrentView();
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
            HGVUtils.removeNode(HGVKit.getCurrentNetwork().getHyperGraph(), nn,
                    true);
            nodes.remove(key);
        }
    }
}
