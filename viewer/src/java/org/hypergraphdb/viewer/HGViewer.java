package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
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
import org.hypergraphdb.util.CloseMe;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.phoebe.PNodeView;
import org.hypergraphdb.viewer.visual.ui.DropDownButton;

import sun.awt.AppContext;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.swing.PScrollPane;

/**
 * Swing Component for displaying HyperGraphDB atoms and links. Contains a
 * <link>org.hypergraphdb.viewer.GraphView</link> that actually displays HG
 * stuff in a Piccolo canvas and a combination of status bar and menu bar.
 * 
 * HGViewer has to visual representation: standalone and as part of the
 * HGVDesktop. In the first case viewer contains its own toolbar alongside the
 * status bar at the bottom. In the other case it contains only status bar,
 * because it shares the common HGVDesktop toolbar.
 * 
 * @author Konstantin Vandev
 */
public class HGViewer extends JPanel
{
    private static final Object FOCUSED_COMPONENT = new StringBuilder(
            "HGVComponent");

    /**
     * This is the label that shows how many node/edges are presented and
     * selected in the GraphView
     */
    protected JLabel statusLabel;
    protected GraphView view;

    private JToolBar toolbar;

    protected int depth = 2;
    private HGALGenerator generator = null;
    private HGHandle foc_handle;

    protected HyperGraph graph;

    /**
     * Constructor
     * 
     * @param db
     *            The HyperGraph to be viewed
     * @param h
     *            The HGHandle to be focused
     */
    public HGViewer(HyperGraph db, HGHandle h)
    {
        this(db, h, 2, null);
    }

    /**
     * Constructor
     * 
     * @param db
     *            The HyperGraph to be viewed
     * @param h
     *            The HGHandle to be focused
     * @param depth
     *            The depth of displayed nodes : 1 = first neighbours
     * @param generator
     *            HGALGenerator to filter specific nodes, could be null
     */
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

    /**
     * Constructor
     * 
     * @param db
     *            The HyperGraph to be viewed
     * @param nodes
     *            FNodes to be displayed
     * @param edges
     *            FEdges to be displayed
     */
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
        setPreferredSize(new Dimension(600, 400));
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

    /**
     * Returns the viewed HyperGraph
     * 
     * @return the viewed HyperGraph
     */
    public HyperGraph getHyperGraph()
    {
        return graph;
    }

    /**
     * Focus on specific handle
     * 
     * @param handle
     *            The handle to focus on
     */
    public void focus(HGHandle handle)
    {
        foc_handle = handle;
        final HGWNReader reader = new HGWNReader(graph);
        reader.read(handle, depth, getGenerator());
        try
        {
            Runnable task = new Runnable() {
                public void run()
                {
                    view.removeAll();
                    for (FNode n : reader.getNodes())
                        view.addNodeView(n);
                    for (FEdge e : reader.getEdges())
                        view.addEdgeView(e);

                    HGVKit.getPreferedLayout().applyLayout(view);
                    view.redrawGraph();
                    final PNodeView nview = view.getNodeView(new FNode(
                            foc_handle));
                    view.getNodeSelectionHandler().select(nview);

                    view.getCanvas().getCamera().animateViewToCenterBounds(
                            nview.getFullBounds(), false, 1550l);
                }
            };
            if (EventQueue.isDispatchThread())
            	task.run();
            else
            	SwingUtilities.invokeAndWait(task);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
   }

    /**
     * Refreshes viewer - e.g. clears the view and focuses again on the
     * specified focus handle
     */
    public void refresh()
    {
        focus(foc_handle);
        // adjust_view();
    }

    /**
     * Returns the underlying <code>GraphView</code>
     * 
     * @return the underlying <code>GraphView</code>
     */
    public GraphView getView()
    {
        return view;
    }

    /**
     * Sets the depth to focus on
     * 
     * @param depth
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * Returns the generator
     * 
     * @return the generator
     */
    public HGALGenerator getGenerator()
    {
        if (generator == null)
            generator = new DefaultALGenerator(graph, null, null);
        return generator;
    }

    /**
     * Sets the generator
     * 
     * @param generator
     */
    public void setGenerator(HGALGenerator generator)
    {
        if (this.generator != null && this.generator instanceof CloseMe)
            ((CloseMe) this.generator).close();
        this.generator = generator;
    }

    private boolean first_time = true;

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        if (first_time && width > 0 && height > 0)
        {
            adjust_view();
            first_time = false;
        }
    }

    private void adjust_view()
    {
        final PCanvas pCanvas = view.getCanvas();
        pCanvas.setVisible(false);
        GraphViewU.invokeLater(new Runnable() {
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

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#removeNotify()
     */
    @Override
    public void removeNotify()
    {
        super.removeNotify();
        // VisualManager.getInstance().save();
    }

    /**
     * Returns the toolbar displayed in the bottom. Stand alone version only.
     */
    public JToolBar getBottomToolbar()
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

        toolbar.add(createDropDown(toolbar, dropdown, "hand", "Edit", man
                .getAction(ActionManager.SELECTED_FIRST_NEIGHBORS_ACTION)));
        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateLayoutMenu(popup);
                return popup;
            }
        };

        toolbar.add(createDropDown(toolbar, dropdown, "layout", "Layout", man
                .getAction(ActionManager.LAYOUT_ACTION)));
        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateVizMenu(popup);
                return popup;
            }
        };
        toolbar
                .add(createDropDown(
                        toolbar,
                        dropdown,
                        "visual",
                        "Visual Properties",
                        man
                                .getAction(ActionManager.NODE_VISUAL_PROPERTIES_ACTION)));
        dropdown = new DropDownButton() {
            protected JPopupMenu getPopupMenu()
            {
                JPopupMenu popup = new JPopupMenu();
                m.populateZoomMenu(popup);
                return popup;
            }
        };
        toolbar.add(createDropDown(toolbar, dropdown, "zoom", "Zooming", man
                .getAction(ActionManager.FIT_ACTION)));

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
    protected void updateStatusLabel()
    {
        int nodeCount = view.getNodeViewCount();
        int edgeCount = view.getEdgeViewCount();
        int selectedNodes = view.getSelectedNodes().size();
        int selectedEdges = view.getSelectedEdges().size();
        statusLabel.setText("  Nodes: " + nodeCount + " (" + selectedNodes
                + " selected)" + " Edges: " + edgeCount + " (" + selectedEdges
                + " selected)");
    }

    /**
     * Returns the label that shows how many node/edges are presented/selected
     * in a GraphView
     */
    public JLabel getStatusLabel()
    {
        return statusLabel;
    }

    private static DropDownButton createDropDown(JToolBar toolbar,
            final DropDownButton dropdown, final String name, String tooltip,
            Action defaultAction)
    {
        dropdown.addToToolBar(toolbar);
        dropdown.putClientProperty("hideActionText", Boolean.TRUE);
        defaultAction.putValue(Action.SMALL_ICON, new ImageIcon(
                HGViewer.class.getResource("/org/hypergraphdb/viewer/images/"
                        + name + ".gif")));

        dropdown.setAction(defaultAction);
        dropdown.setToolTipText(tooltip);
        return dropdown;
    }

    /**
     * Returns the last focused viewer.
     * 
     * @return the last focused HGViewer
     */
    public static final HGViewer getFocusedComponent()
    {
        return (HGViewer) AppContext.getAppContext().get(FOCUSED_COMPONENT);
    }

    /**
     * Sets the focused viewer
     * 
     * @param ui
     *            the focused HGViewer
     */
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
    }

}
