package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.util.CloseMe;
import org.hypergraphdb.viewer.dialogs.EnhancedMenu;
import org.hypergraphdb.viewer.dialogs.VisStylesProvider;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.view.HGVMenus;
import org.hypergraphdb.viewer.visual.ui.DropDownButton;

import phoebe.PEdgeView;
import phoebe.PNodeView;

import sun.awt.AppContext;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.swing.PScrollPane;

public class HGVComponent extends JPanel
{
    /**
     * This is the label that tells how many node/edges are in a HGVNetworkView
     * and how many are selected/hidden
     */
    protected JLabel statusLabel;
    protected HGVNetworkView view;

    private/* static */JToolBar toolbar;
    
    protected int depth = 2;
    private HGALGenerator generator = null;
    protected HyperGraph graph;
    
    public HGVComponent(HyperGraph db, HGHandle h, int depth, HGALGenerator generator)
    {
        super(new BorderLayout());
         this.graph = db;
         this.depth = depth;
         this.generator = generator;
         focused(this);
         HGWNReader reader = new HGWNReader(db);
         reader.read(h, depth, getGenerator()); 
         HGVKit.embeded = true;
         init(reader.getNodes(), reader.getEdges());
    } 

    public HGVComponent(HyperGraph db, Collection<FNode> nodes,  Collection<FEdge> edges)
    {
        super(new BorderLayout());
        addFocusListener(new HGVFocusListener());
        focused(this);
        graph = db;
        init(nodes, edges);
    }
    
    protected void init(Collection<FNode> nodes, Collection<FEdge> edges)
    {
        view = new HGVNetworkView(this, graph, nodes, edges);
        view.getCanvas().addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e)
            {
                focused(HGVComponent.this);
            }
        });
        view.addSelectionListener(new HGVNetworkView.SelectionListener() {
            public void selectionChanged()
            {
                updateStatusLabel();
            }
        });
        PScrollPane scroll = new PScrollPane(view.canvas);
        add(scroll, BorderLayout.CENTER);
        addStatusBar();
        initKeyBindings();
    }
    
    protected void addStatusBar()
    {
        // setup the StatusLabel
        statusLabel = new JLabel();
        statusLabel.setAlignmentX(SwingConstants.LEADING);
        statusLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        JPanel status = new JPanel(new GridBagLayout());
        GridBagConstraints gbg = new GridBagConstraints();
        gbg.gridy = 0;
        gbg.gridx = 0;
        gbg.fill = GridBagConstraints.HORIZONTAL;
        gbg.anchor = GridBagConstraints.WEST;
        gbg.weightx = 1.0;

        status.add(statusLabel, gbg);
        if (HGVKit.isEmbeded())
        {
            gbg = new GridBagConstraints();
            gbg.gridy = 0;
            gbg.gridx = 1;
            gbg.insets = new Insets(0, 0, 0, 5);
            gbg.anchor = GridBagConstraints.EAST;
            JToolBar bar = getBottomToolbar();
            status.add(bar, gbg);
            add(status, BorderLayout.SOUTH);
        }
        else
            add(statusLabel, BorderLayout.SOUTH);
    }
    
    protected void initKeyBindings()
    {
        InputMap inputMap = getInputMap();
        for (Action a : ActionManager.getInstance().getActions())
            if (a.getValue(Action.ACCELERATOR_KEY) != null)
                inputMap.put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), a);
    }
    
    public HyperGraph getHyperGraph()
    {
        return graph;
    }
    
    public void focus(HGHandle handle)
    {
        HGWNReader reader = new HGWNReader(graph);
        reader.read(handle, depth, getGenerator()); 
        clearView();
        for(FNode n: reader.getNodes())
            view.addNodeView(n);
        for(FEdge e: reader.getEdges())
            view.addEdgeView(e);
        HGVKit.getPreferedLayout().applyLayout(HGVKit.getCurrentView());
        view.redrawGraph();
        FNode node = HGVKit.getHGVNode(handle, false); 
        view.getNodeView(node).setSelected(true);
        view.getCanvas().getCamera().animateViewToCenterBounds( 
        view.getNodeView(node).getFullBounds(), false, 1550l );
    }
    
     void clearView()
    {
        for(PEdgeView e :  view.getEdgeViews())
            view.removeEdgeView(e);
        for(PNodeView nv :  view.getNodeViews())
            view.removeNodeView(nv.getNode());
    }

    public HGVNetworkView getView()
    {
        return view;
    }
    
    public void setDepth(int depth)
    {
        this.depth = depth;
    }
    
    public HGALGenerator getGenerator()
    {
        if (generator == null)
            generator = new DefaultALGenerator(graph, null, null);
        return generator;
    }
    
    public void setGenerator(HGALGenerator generator)
    {
        if (this.generator != null && this.generator instanceof CloseMe)
            ((CloseMe)this.generator).close();
        this.generator = generator;     
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        PCanvas pCanvas = view.getCanvas();
        pCanvas.setVisible(false);

        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                PCanvas pCanvas = view.getCanvas();
                pCanvas.setVisible(true);
                HGVKit.getPreferedLayout().applyLayout(view);
                view.getCanvas().getCamera().animateViewToCenterBounds(
                        view.getCanvas().getLayer().getFullBounds(), true, 0);
                view.redrawGraph();
                updateStatusLabel();
            }
        });
    }
    
    @Override
    public void removeNotify()
    {
        VisualManager.getInstance().save();
    }

    protected/* static */JToolBar getBottomToolbar()
    {
        // if (toolbar != null) return toolbar;
        toolbar = new JToolBar();
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // NOI18N
        final HGVMenus m = HGVMenus.getInstance();
        toolbar.add(createDropDown(toolbar, m.getSelectMenu(), "hand", "Edit"));
        toolbar.add(createDropDown(toolbar, m.getLayoutMenu(), "layout",
                "Layout"));
        JMenu menu = m.getVizMenu();
        menu.add(new EnhancedMenu("Set Current Style", new VisStylesProvider()), 0);
        toolbar.add(createDropDown(toolbar, menu, "visual",
                "Visual Properties"));
        toolbar
                .add(createDropDown(toolbar, m.getZoomMenu(), "zoom", "Zooming"));
        return toolbar;
    }

    /**
     * Resets the info label status bar text with the current number of nodes,
     * edges, selected nodes, and selected edges.
     */
    public void updateStatusLabel()
    {
        int nodeCount = view.getNodeViewCount();
        int edgeCount = view.getEdgeViewCount();
        int selectedNodes = view.getSelectedNodes().size();
        int selectedEdges = view.getSelectedEdges().size();
        statusLabel.setText("  Nodes: " + nodeCount + " (" + selectedNodes
                + " selected)" + " Edges: " + edgeCount + " (" + selectedEdges
                + " selected)");
    }

    private static DropDownButton createDropDown(JToolBar toolbar,
            final JMenu m, final String name, String tooltip)
    {
        DropDownButton dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                for (final Component c : m.getMenuComponents())
                {
                    if (c instanceof JMenuItem
                            && ((JMenuItem) c).getAction() != null) popup
                            .add(new JMenuItem(((JMenuItem) c).getAction()));
                    else if (c instanceof JMenu)
                    {
                        JMenu menu = new JMenu(((JMenu) c).getText());
                        for (Component cc : ((JMenu) c).getMenuComponents())
                            if (cc instanceof JMenuItem
                                    && ((JMenuItem) cc).getAction() != null)
                                menu.add(new JMenuItem(((JMenuItem) cc)
                                        .getAction()));
                        popup.add(menu);
                    }
                }
                return popup;
            }
        };
        dropdown.addToToolBar(toolbar);
        dropdown.putClientProperty("hideActionText", Boolean.TRUE);
        dropdown.setAction(createDummyAction(name));
        dropdown.setToolTipText(tooltip);
        return dropdown;
    }

    private static Action createDummyAction(final String name)
    {
        return new AbstractAction(name) {
            {
                putValue(Action.SMALL_ICON, new ImageIcon(getClass()
                        .getResource(
                                "/org/hypergraphdb/viewer/images/" + name
                                        + ".gif")));
            }

            public void actionPerformed(ActionEvent e)
            {
                ((DropDownButton)e.getSource()).showDropDown(e);
            }
        };
    }

    public static final Object FOCUSED_COMPONENT = new StringBuilder(
            "HGVComponent");

    public static final HGVComponent getFocusedComponent()
    {
        return (HGVComponent) AppContext.getAppContext().get(FOCUSED_COMPONENT);
    }

    public static final void setFocusedComponent(HGVComponent ui)
    {
        AppContext.getAppContext().put(FOCUSED_COMPONENT, (HGVComponent) ui);
    }

    private static void focused(Component c)
    {
        if (c instanceof HGVComponent) setFocusedComponent((HGVComponent) c);
    }

    private static class HGVFocusListener extends FocusAdapter
    {
        public void focusGained(FocusEvent e)
        {
            focused(e.getComponent());
        }

        public void focusLost(FocusEvent e)
        {
        }
    }

}
