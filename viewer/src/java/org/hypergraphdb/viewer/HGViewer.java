package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import org.hypergraphdb.viewer.hg.HGVUtils;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.visual.ui.DropDownButton;

import phoebe.PEdgeView;
import phoebe.PNodeView;

import sun.awt.AppContext;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.swing.PScrollPane;

public class HGViewer extends JPanel
{
    /**
     * This is the label that tells how many node/edges are in a HGVNetworkView
     * and how many are selected/hidden
     */
    protected JLabel statusLabel;
    protected GraphView view;

    private/* static */JToolBar toolbar;

    protected int depth = 2;
    private HGALGenerator generator = null;
    private HGHandle foc_handle;

    protected HyperGraph graph;
    
    public HGViewer(HyperGraph db, HGHandle h)
    {
        this(db, h, 2, null);
    }

    public HGViewer(HyperGraph db, HGHandle h, int depth,
            HGALGenerator generator)
    {
        super(new BorderLayout());
        this.graph = db;
        this.depth = depth;
        this.generator = generator;
        foc_handle = h;
        focused(this);
        HGWNReader reader = new HGWNReader(db);
        reader.read(h, depth, getGenerator());
        init(reader.getNodes(), reader.getEdges());
    }

    public HGViewer(HyperGraph db, Collection<FNode> nodes,
            Collection<FEdge> edges)
    {
        super(new BorderLayout());
        addFocusListener(new HGVFocusListener());
        focused(this);
        graph = db;
        init(nodes, edges);
    }

    protected void init(Collection<FNode> nodes, Collection<FEdge> edges)
    {
        view = new GraphView(this, graph, nodes, edges);
        HGVKit.getViewersList().add(view);
        view.getCanvas().addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e)
            {
                focused(HGViewer.this);
            }
        });
        view.addSelectionListener(new GraphView.SelectionListener() {
            public void selectionChanged()
            {
                updateStatusLabel();
            }
        });
        PScrollPane scroll = new PScrollPane(view.canvas);
        add(scroll, BorderLayout.CENTER);
        addStatusBar();
        setPreferredSize(new Dimension(600,400));
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
        for (FNode n : reader.getNodes())
            view.addNodeView(n);
        for (FEdge e : reader.getEdges())
            view.addEdgeView(e);
        HGVKit.getPreferedLayout().applyLayout(HGVKit.getCurrentView());
        view.redrawGraph();
        FNode node = new FNode(handle);
        view.getNodeView(node).setSelected(true);
        view.getCanvas().getCamera().animateViewToCenterBounds(
                view.getNodeView(node).getFullBounds(), false, 1550l);
    }

    public void refresh()
    {
        focus(foc_handle);
        //adjust_view();
    }

    void clearView()
    {
        for (PEdgeView e : view.getEdgeViews())
            view.removeEdgeView(e);
        for (PNodeView nv : view.getNodeViews())
            view.removeNodeView(nv.getNode());
    }

    public GraphView getView()
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
            ((CloseMe) this.generator).close();
        this.generator = generator;
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        adjust_view();
    }
    
    private void adjust_view()
    {
        final  PCanvas pCanvas = view.getCanvas();
        pCanvas.setVisible(false);
        HGVUtils.invokeLater(new Runnable() {
            public void run()
            {
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
        super.removeNotify();
        //VisualManager.getInstance().save();
    }

    protected/* static */JToolBar getBottomToolbar()
    {
        if (toolbar != null) return toolbar;
        toolbar = new JToolBar();
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // NOI18N
        final HGVMenus m = HGVMenus.getInstance();
        ActionManager man = ActionManager.getInstance();
        DropDownButton dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateSelectMenu(popup);
                return popup;
            }
        };
        
        toolbar.add(createDropDown(toolbar, dropdown, "hand", "Edit", 
                man.getAction(ActionManager.SELECTED_FIRST_NEIGHBORS_ACTION)));
        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateLayoutMenu(popup);
                return popup;
            }
        };
        
        toolbar.add(createDropDown(toolbar, dropdown, "layout", "Layout", 
                man.getAction(ActionManager.LAYOUT_ACTION)));
        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateVizMenu(popup);
                return popup;
            }
        };
        toolbar.add(createDropDown(toolbar, dropdown, "visual",
                "Visual Properties", 
                man.getAction(ActionManager.VISUAL_PROPERTIES_ACTION)));
        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateZoomMenu(popup);
                return popup;
            }
        };
        toolbar.add(createDropDown(toolbar, dropdown, "zoom", "Zooming",
                man.getAction(ActionManager.FIT_ACTION)));

        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateFileMenu(popup);
                return popup;
            }
        };
        toolbar.add(createDropDown(toolbar, dropdown, "print", "Print/Export",
                man.getAction(ActionManager.EXPORT_ACTION)));
        
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
            final DropDownButton dropdown, final String name, String tooltip, Action defaultAction)
    {
        dropdown.addToToolBar(toolbar);
        dropdown.putClientProperty("hideActionText", Boolean.TRUE);
        defaultAction.putValue(Action.SMALL_ICON, new ImageIcon(
                HGViewer.class.getResource("/org/hypergraphdb/viewer/images/" + name
                                + ".gif")));
        
        dropdown.setAction(defaultAction);
        dropdown.setToolTipText(tooltip);
        return dropdown;
    }

   
    public static final Object FOCUSED_COMPONENT = new StringBuilder(
            "HGVComponent");

    public static final HGViewer getFocusedComponent()
    {
        return (HGViewer) AppContext.getAppContext().get(FOCUSED_COMPONENT);
    }

    public static final void setFocusedComponent(HGViewer ui)
    {
        AppContext.getAppContext().put(FOCUSED_COMPONENT, (HGViewer) ui);
    }

    private static void focused(Component c)
    {
        if (c instanceof HGViewer) setFocusedComponent((HGViewer) c);
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
