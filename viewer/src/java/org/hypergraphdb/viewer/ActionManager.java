package org.hypergraphdb.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.freehep.util.export.ExportDialog;
import org.hypergraphdb.viewer.dialogs.AppConfigPanel;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.hg.LoadHyperGraphFileAction;
import org.hypergraphdb.viewer.hg.LoadWordNetAction;
import org.hypergraphdb.viewer.layout.Layout;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.visual.VisualStyle;
import org.hypergraphdb.viewer.visual.ui.PaintersPanel;

import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.PNodeView;
import org.hypergraphdb.viewer.phoebe.event.BirdsEyeView;
import org.hypergraphdb.viewer.phoebe.util.PrintingFixTextNode;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Singleton class containing actions used in menuBar, toolBar and pop-up menus
 * @author Konstantin Vandev
 */
public class ActionManager
{
    public static final String LOAD_HYPER_GRAPH_ACTION = "HyperGraph...";
    public static final String LOAD_WORD_NET_ACTION = "WordNet...";
    public final static String PRINT_ACTION = "Print...";
    public final static String EXPORT_ACTION = "Export As...";
    public static final String EXIT_ACTION = "Exit";
    public static final String CREATE_VIEW_ACTION = "Create View";
    public static final String DESTROY_VIEW_ACTION = "Destroy View";
    public final static String PREFERENCES_ACTION = "Preferences...";
    public static final String HIDE_NODE_SELECTION_ACTION = "Hide selection";
    public static final String SELECT_ALL_NODES_ACTION = "Select all nodes";
    public static final String SELECTED_FIRST_NEIGHBORS_ACTION = "First neighbors of selected nodes";

    public static final String HIDE_EDGE_SELECTION_ACTION = "Hide edge selection";
    public static final String SHOW_ALL_EDGES_ACTION = "Show All Edges";
    public static final String SELECT_ALL_EDGES_ACTION = "Select all edges";
    public static final String NEW_WINDOW_SELECTED_NODES_ONLY_ACTION = "Selected nodes only";
    public static final String NEW_WINDOW_SELECTED_NODES_EDGES_ACTION = "Selected nodes, Selected edges";
    public static final String NEW_WINDOW_CLONE_WHOLE_GRAPH_ACTION = "Whole network";
    public static final String SELECT_ALL_ACTION = "Select all nodes and edges";
    public static final String HIDE_SELECTED_ACTION = "Hide Selected";
    public static final String PREFERED_LAYOUT_ACTION = "Select Preferred Layout";
    public static final String TOGGLE_BIRDS_EYE_VIEW_ACTION = "Toggle Overview";
    public static final String BACKGROUND_COLOR_ACTION = "Change Background Color";
    public static final String NODE_VISUAL_PROPERTIES_ACTION = "Visual Properties Nodes";
    public static final String EDGE_VISUAL_PROPERTIES_ACTION = "Visual Properties Edges";
    public static final String FIT_ACTION = "Zoom To Fit";

    public static final String ZOOM_IN_ACTION = "Zoom In";
    public static final String ZOOM_OUT_ACTION = "Zoom Out";
    public static final String ZOOM_SELECTED_ACTION = "Zoom Selected";
    public static final String LAYOUT_ACTION = "Layout";

    private static ActionManager instance;

    public static ActionManager getInstance()
    {
        if (instance == null)
        {
            instance = new ActionManager();
            // this will add different layout actions
            HGVMenus.getInstance();
        }
        return instance;
    }

    static Map<String, Action> actions = new HashMap<String, Action>();
    static
    {
        actions.put(LOAD_HYPER_GRAPH_ACTION, new LoadHyperGraphFileAction());
        actions.put(LOAD_WORD_NET_ACTION, new LoadWordNetAction());
        actions.put(PRINT_ACTION, new PrintAction());
        actions.put(EXPORT_ACTION, new ExportAction());
        actions.put(EXIT_ACTION, new ExitAction());
        actions.put(CREATE_VIEW_ACTION, new CreateHGViewerAction());
        actions.put(DESTROY_VIEW_ACTION, new DestroyHGViewerAction());
        actions.put(PREFERENCES_ACTION, new PreferenceAction());
        actions.put(HIDE_NODE_SELECTION_ACTION, new HideSelectedNodesAction());
        actions.put(SELECT_ALL_NODES_ACTION, new SelectAllNodesAction());
        actions.put(SELECTED_FIRST_NEIGHBORS_ACTION,
                new SelectFirstNeighborsAction());
        actions.put(HIDE_EDGE_SELECTION_ACTION, new HideSelectedEdgesAction());
        actions.put(SELECT_ALL_EDGES_ACTION, new SelectAllEdgesAction());

        actions.put(NEW_WINDOW_SELECTED_NODES_ONLY_ACTION,
                new NewWindowSelectedNodesOnlyAction());
        actions.put(NEW_WINDOW_SELECTED_NODES_EDGES_ACTION,
                new NewWindowSelectedNodesEdgesAction());

        actions.put(SELECT_ALL_ACTION, new SelectAllAction());

        // actions.put(ROTATE_SCALE_ACTION, new RotationScaleLayoutAction());
        actions.put(PREFERED_LAYOUT_ACTION, new SelectPrefLayoutAction());
        actions.put(TOGGLE_BIRDS_EYE_VIEW_ACTION, new BirdsEyeViewAction());
        actions.put(BACKGROUND_COLOR_ACTION, new BackgroundColorAction());
        actions.put(NODE_VISUAL_PROPERTIES_ACTION, new SetVisualPropertiesAction(true));
        actions.put(EDGE_VISUAL_PROPERTIES_ACTION, new SetVisualPropertiesAction(false));
        actions.put(FIT_ACTION, new FitContentAction());
        actions.put(ZOOM_IN_ACTION, new ZoomAction(1.1));
        actions.put(ZOOM_OUT_ACTION, new ZoomAction(0.9));
        actions.put(ZOOM_SELECTED_ACTION, new ZoomSelectedAction());

        actions.put(LAYOUT_ACTION, new LayoutAction());
        actions.put(HIDE_SELECTED_ACTION, new HideSelectedAction());
        // actions.put(SQUIGGLE_ACTION, new SquiggleAction());
    }

    /**
     * Returns an action given its name
    */
    public Action getAction(String name)
    {
        return actions.get(name);
    }

    /**
    * Returns all registered actions
    */
    public Collection<Action> getActions()
    {
        return actions.values();
    }

   /**
    * Add a given action to the ActionManager
    */
    public Action putAction(Action a)
    {
        actions.put((String) a.getValue(Action.NAME), a);
        return a;
    }

    /**
     * Add a given action to the ActionManager and assign it a KeyStroke
     */
    public Action putAction(Action a, KeyStroke k)
    {
        a.putValue(Action.ACCELERATOR_KEY, k);
        actions.put((String) a.getValue(Action.NAME), a);
        return a;
    }

    /**
     * Add a given action to the ActionManager and assign it a KeyStroke and an Icon
     */
    public Action putAction(Action a, KeyStroke k, Icon icon)
    {
        a.putValue(Action.SMALL_ICON, icon);
        return putAction(a, k);
    }

    private ActionManager()
    {
    }

    
    /**
     * Zooms the current HGViewer
     */
    public static class ZoomAction extends HGVAction
    {
        double factor;

        public ZoomAction(double factor)
        {
            super((factor <= 1) ? ZOOM_OUT_ACTION : ZOOM_IN_ACTION);
            this.factor = factor;
            setAcceleratorCombo((factor <= 1) ? KeyEvent.VK_DOWN
                    : KeyEvent.VK_UP, ActionEvent.ALT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            viewer.getView().setZoom(factor);
        }
    }

    /**
     * Zooms and centers the selected nodes in the current HGViewer
     */
    public static class ZoomSelectedAction extends HGVAction
    {

        public ZoomSelectedAction()
        {
            super(ZOOM_SELECTED_ACTION);
        }

        public void action(HGViewer viewer) throws Exception
        {
            zoomSelected(viewer.getView());
        }

        public void zoomSelected(GraphView view)
        {
            Collection<PNodeView> selected_nodes = view.getSelectedNodes();
            if (selected_nodes.size() == 0) { return; }

            Iterator<PNodeView> selected_nodes_iterator = selected_nodes
                    .iterator();
            double bigX;
            double bigY;
            double smallX;
            double smallY;
            double W;
            double H;

            PNodeView first = selected_nodes_iterator.next();
            bigX = first.getXPosition();
            smallX = bigX;
            bigY = first.getYPosition();
            smallY = bigY;

            while (selected_nodes_iterator.hasNext())
            {
                PNodeView nv = selected_nodes_iterator.next();
                double x = nv.getXPosition();
                double y = nv.getYPosition();

                if (x > bigX)
                {
                    bigX = x;
                }
                else if (x < smallX)
                {
                    smallX = x;
                }

                if (y > bigY)
                {
                    bigY = y;
                }
                else if (y < smallY)
                {
                    smallY = y;
                }
            }

            PBounds zoomToBounds;
            if (selected_nodes.size() == 1)
            {
                zoomToBounds = new PBounds(smallX - 100, smallY - 100, (bigX
                        - smallX + 200), (bigY - smallY + 200));
            }
            else
            {
                zoomToBounds = new PBounds(smallX, smallY,
                        (bigX - smallX + 100), (bigY - smallY + 100));
            }
            view.getCanvas().getCamera()
                    .animateViewToCenterBounds(zoomToBounds, true, 500);
        }
    }

    /**
     * Applies the preferred layout to current HGViewer 
     */
    public static class LayoutAction extends HGVAction
    {
        public LayoutAction()
        {
            super(LAYOUT_ACTION);
            setAcceleratorCombo(KeyEvent.VK_L, ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            HGVKit.prefered_layout.applyLayout(viewer.getView());
        }
    }

    /**
     * Sets the preferred layout 
     */
    public static class SelectPrefLayoutAction extends AbstractAction
    {
        public SelectPrefLayoutAction()
        {
            super(PREFERED_LAYOUT_ACTION);
        }

        public void actionPerformed(ActionEvent e)
        {
            DialogDescriptor d = new DialogDescriptor(GUIUtilities.getFrame(),
                    new SelectLayoutPanel(), PREFERED_LAYOUT_ACTION);
            d.setModal(true);
            d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
            DialogDisplayer.getDefault().notify(d);
        }
    }

    /**
     * Panel with the available layouts
     */
    private static class SelectLayoutPanel extends JPanel
    {
        ButtonGroup group = new ButtonGroup();

        public SelectLayoutPanel()
        {
            super();
            initialize();
        }

        private void initialize()
        {
            this.setLayout(new GridBagLayout());
            int i = 0;
            final Map<String, Layout> layoutMap = new HashMap<String, Layout>();
            for (Layout l : HGVKit.getLayouts())
            {
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridy = i++;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.anchor = GridBagConstraints.WEST;
                JRadioButton butt = new JRadioButton(l.getName());
                this.add(butt, gridBagConstraints);
                butt.setSelected(l.equals(HGVKit.getPreferedLayout()));
                butt.setActionCommand(l.getName());
                layoutMap.put(l.getName(), l);
                butt.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        HGVKit.setPreferedLayout(layoutMap.get(e
                                .getActionCommand()));
                    }
                });
                group.add(butt);
            }
        }
    }

    /**
     * Prompts User for New Background Color.
     */
    public static class BackgroundColorAction extends HGVAction
    {

        public BackgroundColorAction()
        {
            super(BACKGROUND_COLOR_ACTION);
            setAcceleratorCombo(KeyEvent.VK_B, ActionEvent.ALT_MASK);
        }

        public void action(final HGViewer viewer) throws Exception
        {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    JColorChooser chooser = new JColorChooser();
                    Color newPaint = JColorChooser.showDialog(viewer,
                            "Choose a Background Color", (Color) viewer
                                    .getView().getBackgroundPaint());

                    // Update the Current Background Color
                    // and Synchronize with current Visual Style
                    if (newPaint != null)
                    {
                        viewer.getView().setBackgroundPaint(newPaint);
                        synchronizeVisualStyle(viewer.getView(), newPaint);
                    }
                }
            });
        }

        /**
         * Synchronizes the New Background Color with the Current Visual Style.
         */
        private void synchronizeVisualStyle(GraphView view, Color newColor)
        {
            VisualStyle style = view.getVisualStyle();
            style.setBackgroundColor(newColor);
        }
    }

    
    /**
     * Toggles BirdsEyeView in the stand-alone version of HGViewer
     */
    public static class BirdsEyeViewAction extends HGVAction implements
            PropertyChangeListener
    {
        BirdsEyeView bev;
        boolean on = false;

        public BirdsEyeViewAction()
        {
            super(TOGGLE_BIRDS_EYE_VIEW_ACTION);
        }

        public void propertyChange(PropertyChangeEvent e)
        {
            if (e.getPropertyName() == HGVDesktop.GRAPH_VIEW_FOCUSED)
            {
                GraphView view = (GraphView) e.getNewValue();
                bev.disconnect();
                try
                {
                    bev.connect(view.getCanvas(), new PLayer[] { view
                            .getCanvas().getLayer() });
                    bev.updateFromViewed();
                }
                catch (Exception ex)
                {
                    // no newly focused network
                }
            }
        }

        public void action(final HGViewer viewer) throws Exception
        {
            if (!on)
            {
                bev = new BirdsEyeView();
                bev.connect(viewer.getView().getCanvas(), new PLayer[] { viewer
                        .getView().getCanvas().getLayer() });

                bev.setMinimumSize(new Dimension(180, 180));
                bev.setSize(new Dimension(180, 180));
                HGVKit.getDesktop().getNetworkPanel().setNavigator(bev);
                HGVKit.getDesktop().getSwingPropertyChangeSupport()
                        .addPropertyChangeListener(this);
                bev.updateFromViewed();
                on = true;
            }
            else
            {
                if (bev != null)
                {
                    bev.disconnect();
                    bev = null;
                }
                HGVKit.getDesktop().getNetworkPanel().setNavigator(
                        HGVKit.getDesktop().getNetworkPanel()
                                .getNavigatorPanel());
                HGVKit.getDesktop().getSwingPropertyChangeSupport()
                        .removePropertyChangeListener(this);
                on = false;
            }
        }
    }

    /**
     * Creates a new HGViewer and adds it in the TabbdePane in the stand-alone version of HGViewer
     */
    public static class CreateHGViewerAction extends HGVAction
    {
        public CreateHGViewerAction()
        {
            super(CREATE_VIEW_ACTION);
            setAcceleratorCombo(KeyEvent.VK_V, ActionEvent.ALT_MASK);
        }

        public void action(final HGViewer viewer) throws Exception
        {
            HGVKit.createHGViewer(viewer.getView());
        }
    }

    /**
     * Destroys a HGViewer and removes it from the TabbdePane in the stand-alone version of HGViewer
     */
    public static class DestroyHGViewerAction extends HGVAction
    {

        public DestroyHGViewerAction()
        {
            super(DESTROY_VIEW_ACTION);
            setAcceleratorCombo(KeyEvent.VK_W, ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            HGVKit.destroyNetworkView(viewer.getView());
        }
    }

    /**
     * Exit action.
     */
    public static class ExitAction extends AbstractAction
    {

        public ExitAction()
        {
            super(EXIT_ACTION);
        }

        public void actionPerformed(ActionEvent e)
        {
            HGVKit.exit();
        }
    }

    /**
     * Exports current HGViewer to one of the predefined formats.
     */
    public static class ExportAction extends HGVAction
    {
        public ExportAction()
        {
            super(EXPORT_ACTION);
            setAcceleratorCombo(KeyEvent.VK_P, ActionEvent.CTRL_MASK
                    | ActionEvent.SHIFT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphView view = viewer.getView();
            view.getCanvas().getCamera().addClientProperty(
                    PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, "true");
            ExportDialog export = new ExportDialog();
            export.showExportDialog(view.getCanvas(), "Export view as ...",
                    view.getCanvas(), "export");
            view.getCanvas().getCamera().addClientProperty(
                    PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, null);
        }
    }

    /**
     * Zooms the current view appropriately to fit in the viewable area.
     */
    public static class FitContentAction extends HGVAction
    {

        public FitContentAction()
        {
            super("Zoom To Fit");
            setAcceleratorCombo(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphView view = viewer.getView();
            view.getCanvas().getCamera().animateViewToCenterBounds(
                    view.getCanvas().getLayer().getFullBounds(), true, 50l);
        }
    }

    /**
     * Hides selected nodes and edges.
     */
    public static class HideSelectedAction extends HGVAction
    {
        public HideSelectedAction()
        {
            super(HIDE_SELECTED_ACTION);
        }

        public HideSelectedAction(boolean label)
        {
            super();
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphView view = viewer.getView();
            GraphViewU.hideSelectedNodes(view);
            GraphViewU.hideSelectedEdges(view);
        }
    }

    /**
     * Hides selected edges.
     */
    public static class HideSelectedEdgesAction extends HGVAction
    {

        public HideSelectedEdgesAction()
        {
            super(HIDE_EDGE_SELECTION_ACTION);
            setAcceleratorCombo(KeyEvent.VK_H, ActionEvent.ALT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphViewU.hideSelectedEdges(viewer.getView());
        }
    }

    /**
     * Hides selected nodes.
     */
    public static class HideSelectedNodesAction extends HGVAction
    {

        public HideSelectedNodesAction()
        {
            super(HIDE_NODE_SELECTION_ACTION);
            setAcceleratorCombo(java.awt.event.KeyEvent.VK_H,
                    ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphViewU.hideSelectedNodes(viewer.getView());
        }
    }

    /**
     * Creates a new HGViewer containing the selected nodes and edges from the current one 
     */
    public static class NewWindowSelectedNodesEdgesAction extends HGVAction
    {
        public NewWindowSelectedNodesEdgesAction()
        {
            super(NEW_WINDOW_SELECTED_NODES_EDGES_ACTION);
            setAcceleratorCombo(KeyEvent.VK_N, ActionEvent.CTRL_MASK
                    | ActionEvent.SHIFT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            if (HGVKit.isEmbeded()) return;
            GraphView view = viewer.getView();
            Collection<FNode> nodes = new ArrayList<FNode>();
            for (PNodeView v : view.getSelectedNodes())
                nodes.add(v.getNode());
            Collection<FEdge> edges = new ArrayList<FEdge>();
            for (PEdgeView v : view.getSelectedEdges())
                edges.add(v.getEdge());

            HGViewer new_view = HGVKit.createHGViewer(view.getHyperGraph(),
                    nodes, edges);
            new_view.getView().setIdentifier(
                    makeSubGraphTitle(view));
        }
    }

    /**
     * Creates a new HGViewer containing the selected nodes from the current one 
     */
    public static class NewWindowSelectedNodesOnlyAction extends HGVAction
    {

        public NewWindowSelectedNodesOnlyAction()
        {
            super(NEW_WINDOW_SELECTED_NODES_ONLY_ACTION);
            setAcceleratorCombo(KeyEvent.VK_N, ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            if (HGVKit.isEmbeded()) return;
            GraphView view = viewer.getView();
            Collection<FNode> nodes = new ArrayList<FNode>();
            for (PNodeView v : view.getSelectedNodes())
                nodes.add(v.getNode());
            Set<FEdge> edges = Collections.emptySet();
            HGViewer new_view = HGVKit.createHGViewer(view.getHyperGraph(),
                    nodes, edges);
            new_view.getView().setIdentifier(
                   makeSubGraphTitle(view));
        }
    }

    /**
     * Opens up the settings dialog 
     */
    public static class PreferenceAction extends AbstractAction
    {
        public PreferenceAction()
        {
            super(PREFERENCES_ACTION);
        }

        public void actionPerformed(ActionEvent e)
        {
            AppConfigPanel p = new AppConfigPanel();
            DialogDescriptor dd = new DialogDescriptor(GUIUtilities.getFrame(),
                    p, "HGVKit Properties");
            DialogDisplayer.getDefault().notify(dd);
        }
    }

    /**
     * Prints the current HGViewer
     */
    public static class PrintAction extends HGVAction
    {

        public PrintAction()
        {
            super(PRINT_ACTION);
            setAcceleratorCombo(KeyEvent.VK_P, ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            viewer.getView().getCanvas().getLayer().print();
        }
    }

    /**
     * Selects all nodes and edges in the current HGViewer
     */
    public static class SelectAllAction extends HGVAction
    {

        public SelectAllAction()
        {
            super(SELECT_ALL_ACTION);
            setAcceleratorCombo(KeyEvent.VK_A, ActionEvent.CTRL_MASK
                    | ActionEvent.ALT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            viewer.getView().selectAllNodes();
            viewer.getView().selectAllEdges();
        }
    }

    /**
     * Selects all edges in the current HGViewer
     */
    public static class SelectAllEdgesAction extends HGVAction
    {

        public SelectAllEdgesAction()
        {
            super(SELECT_ALL_EDGES_ACTION);
            setAcceleratorCombo(KeyEvent.VK_A, ActionEvent.ALT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            viewer.getView().selectAllEdges();
        }
    }

    /**
     * Selects all nodes in the current HGViewer
     */
    public static class SelectAllNodesAction extends HGVAction
    {

        public SelectAllNodesAction()
        {
            super(SELECT_ALL_NODES_ACTION);
            setAcceleratorCombo(KeyEvent.VK_A, ActionEvent.CTRL_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            viewer.getView().selectAllNodes();
        }
    }

    /**
     * Selects first neighbors of the currently selected node
     */
    public static class SelectFirstNeighborsAction extends HGVAction
    {
        public SelectFirstNeighborsAction()
        {
            super(SELECTED_FIRST_NEIGHBORS_ACTION);
            setAcceleratorCombo(KeyEvent.VK_F6, 0);
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphView view = viewer.getView();
            Set<FNode> set = new HashSet<FNode>();
            for (PNodeView v : view.getSelectedNodes())
                set.add(v.getNode());
            Set<PNodeView> new_set = new HashSet<PNodeView>();
            for (FNode o : set)
            {
                FEdge[] ids = view.getAdjacentEdges(o, true, true);
                for (int i = 0; i < ids.length; i++)
                {
                    FEdge edge = ids[i];
                    new_set.add(view.getNodeView(
                            edge.getTarget()));
                    new_set.add(view.getNodeView(
                            edge.getSource()));
                }
            }
            view.getNodeSelectionHandler().select(new_set);
        }
    }

    /**
     * Displays the dialog for visual settings   
     */
    public static class SetVisualPropertiesAction extends HGVAction
    {
        boolean nodes_or_edges;
        
        public SetVisualPropertiesAction(boolean nodes_or_edges)
         {
            super(nodes_or_edges ? "Nodes" : "Edges");
            this.nodes_or_edges = nodes_or_edges;
            if(nodes_or_edges)
               setAcceleratorCombo(KeyEvent.VK_N, ActionEvent.CTRL_MASK
                    | ActionEvent.ALT_MASK);
            else
               setAcceleratorCombo(KeyEvent.VK_E, ActionEvent.CTRL_MASK
                        | ActionEvent.ALT_MASK);
        }

        public void action(HGViewer viewer) throws Exception
        {
            GraphView view = viewer.getView();
            PaintersPanel p = new PaintersPanel(nodes_or_edges);
            p.setView(view);
            DialogDescriptor dd = new DialogDescriptor(GUIUtilities.getFrame(),
                    p, nodes_or_edges ? "Node Painters" : "Edge Painters");
            DialogDisplayer.getDefault().notify(dd);
            VisualManager.getInstance().save();
            view.redrawGraph();
        }
    }
    
    private static String makeSubGraphTitle(GraphView view)
    {
      for (int i = 0; true; i++) {
        String nameCandidate =
          view.getIdentifier() + "->child" + ((i == 0) ? "" : ("." + i));
        if (!isTitleTaken(nameCandidate)) 
            return nameCandidate; }
    }

    private static boolean isTitleTaken(String titleCandidate)
    {
      for (GraphView v: HGVKit.getViewersList()) 
        if (titleCandidate.equals(v.getIdentifier()))
            return true; 
      return false;
    }

}
