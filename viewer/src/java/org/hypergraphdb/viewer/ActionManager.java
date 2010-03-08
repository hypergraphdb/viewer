package org.hypergraphdb.viewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import org.hypergraphdb.viewer.actions.*;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.hg.*;
import org.hypergraphdb.viewer.layout.Layout;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;

import phoebe.PNodeView;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PBounds;

public class ActionManager
{
	public static final String LOAD_HYPER_GRAPH_ACTION = "HyperGraph...";
	public static final String LOAD_WORD_NET_ACTION = "WordNet...";
	public final static String PRINT_ACTION = "Print...";
	public final static String EXPORT_ACTION = "Export As...";
	public static final String EXIT_ACTION = "Exit";
	public static final String CREATE_VIEW_ACTION = "Create View";
	public static final String DESTROY_VIEW_ACTION = "Destroy View";
	public static final String DESTROY_SELECTED_NODES_EDGES_ACTION = "Destroy Selected Nodes/Edges";
	public final static String PREFERENCES_ACTION = "Preferences...";
	public static final String INVERT_NODE_SELECTION_ACTION = "Invert selection";
	public static final String HIDE_NODE_SELECTION_ACTION = "Hide selection";
	public static final String SELECT_ALL_NODES_ACTION = "Select all nodes";
	//public static final String DESELECT_ALL_NODES_ACTION = "Deselect all nodes";
	public static final String SELECTED_FIRST_NEIGHBORS_ACTION = "First neighbors of selected nodes";

	public static final String INVERT_EDGE_SELECTION_ACTION = "Invert edge selection";
	public static final String HIDE_EDGE_SELECTION_ACTION = "Hide edge selection";
	public static final String SHOW_ALL_EDGES_ACTION = "Show All Edges";
	public static final String SELECT_ALL_EDGES_ACTION = "Select all edges";
	//public static final String DESELECT_ALL_EDGES_ACTION = "Deselect all edges";
	public static final String NEW_WINDOW_SELECTED_NODES_ONLY_ACTION = "Selected nodes only";
	public static final String NEW_WINDOW_SELECTED_NODES_EDGES_ACTION = "Selected nodes, Selected edges";
	public static final String NEW_WINDOW_CLONE_WHOLE_GRAPH_ACTION = "Whole network";
	public static final String SELECT_ALL_ACTION = "Select all nodes and edges";
	//public static final String DESELECT_ALL_ACTION = "Deselect All Nodes and Edges";
	public static final String PREFERED_LAYOUT_ACTION = "Select Preferred Layout";
	public static final String TOGGLE_BIRDS_EYE_VIEW_ACTION = "Toggle Overview";
	public static final String BACKGROUND_COLOR_ACTION = "Change Background Color";
	public static final String VISUAL_PROPERTIES_ACTION = "Set Visual Properties";
	public static final String FIT_ACTION = "Zoom To Fit";
	
	public static final String ZOOM_IN_ACTION = "Zoom In";
	public static final String ZOOM_OUT_ACTION = "Zoom Out";
	public static final String ZOOM_SELECTED_ACTION = "Zoom Selected";
	public static final String LAYOUT_ACTION = "Layout"; 

	
	private static ActionManager instance;
		
	public static ActionManager getInstance(){
		if(instance == null)
			instance = new ActionManager();
		return instance;
	}

	static Map<String, Action> actions =
		new HashMap<String, Action>();
	static{
		actions.put(LOAD_HYPER_GRAPH_ACTION, new LoadHyperGraphFileAction());
		actions.put(LOAD_WORD_NET_ACTION, new LoadWordNetAction());
		actions.put(PRINT_ACTION, new PrintAction());
		actions.put(EXPORT_ACTION, new ExportAction());
		actions.put(EXIT_ACTION, new ExitAction());
		actions.put(CREATE_VIEW_ACTION, new CreateNetworkViewAction());
		actions.put(DESTROY_VIEW_ACTION, new DestroyNetworkViewAction());
		actions.put(DESTROY_SELECTED_NODES_EDGES_ACTION, new DestroySelectedAction());
		actions.put(PREFERENCES_ACTION, new PreferenceAction());
		actions.put(INVERT_NODE_SELECTION_ACTION, new InvertSelectedNodesAction());
		actions.put(HIDE_NODE_SELECTION_ACTION, new HideSelectedNodesAction());
		actions.put(SELECT_ALL_NODES_ACTION, new SelectAllNodesAction());
		actions.put(SELECTED_FIRST_NEIGHBORS_ACTION, new SelectFirstNeighborsAction());
		
		actions.put(INVERT_EDGE_SELECTION_ACTION, new InvertSelectedEdgesAction());
		actions.put(HIDE_EDGE_SELECTION_ACTION, new HideSelectedEdgesAction());
		actions.put(SELECT_ALL_EDGES_ACTION, new SelectAllEdgesAction());
		
		actions.put(NEW_WINDOW_SELECTED_NODES_ONLY_ACTION, new NewWindowSelectedNodesOnlyAction());
		actions.put(NEW_WINDOW_SELECTED_NODES_EDGES_ACTION, new NewWindowSelectedNodesEdgesAction());
		
		actions.put(SELECT_ALL_ACTION, new SelectAllAction());
		
		//actions.put(ROTATE_SCALE_ACTION, new RotationScaleLayoutAction());
		actions.put(PREFERED_LAYOUT_ACTION, new SelectPrefLayoutAction());
		actions.put(TOGGLE_BIRDS_EYE_VIEW_ACTION, new BirdsEyeViewAction());
		actions.put(BACKGROUND_COLOR_ACTION, new BackgroundColorAction());
		actions.put(VISUAL_PROPERTIES_ACTION, new SetVisualPropertiesAction());
		actions.put(FIT_ACTION, new FitContentAction());
		actions.put(ZOOM_IN_ACTION, new ZoomAction(1.1));
		actions.put(ZOOM_OUT_ACTION, new ZoomAction(0.9));
		actions.put(ZOOM_SELECTED_ACTION, new ZoomSelectedAction());
		
		actions.put(LAYOUT_ACTION, new LayoutAction());
	}

	public Action getAction(String name){
		return actions.get(name);
	}
	
	public Collection<Action> getActions(){
		return actions.values();
	}
	
	public Action putAction(Action a)
	{
		actions.put((String) a.getValue(Action.NAME), a);
		return a;
	}
	
	public Action putAction(Action a, KeyStroke k)
	{
		a.putValue(Action.ACCELERATOR_KEY, k);
		actions.put((String)
				a.getValue(Action.NAME), a);
		return a;
	}
	
	public Action putAction(Action a, KeyStroke k, Icon icon)
	{
		a.putValue(Action.SMALL_ICON, icon);
		return putAction(a, k);
	}
	
	private ActionManager()
	{
	}
	
	public static class ZoomAction extends HGVAction
	{
	    double factor;
	    public ZoomAction( double factor) {
	        super ((factor <= 1) ? ZOOM_OUT_ACTION: ZOOM_IN_ACTION);
	        this.factor = factor;
	       setAcceleratorCombo((factor <= 1) ? KeyEvent.VK_DOWN:KeyEvent.VK_UP,
	                ActionEvent.ALT_MASK);
	    }
	    
	    public void zoom () {
	       HGVKit.getCurrentView().setZoom( factor );
	    }

	    public void actionPerformed (ActionEvent e) 
	    {
	        if(HGVKit.getCurrentView() != null)
	           HGVKit.getCurrentView().setZoom( factor );
	    }
	}
	
	public static class ZoomSelectedAction extends HGVAction {
	       
	     public ZoomSelectedAction ()  {
	        super(ZOOM_SELECTED_ACTION);
	    }
	    
	    public void actionPerformed(ActionEvent e) {
	        
	      zoomSelected();
	    }

	   public void zoomSelected () {
	        GraphView view = HGVKit.getCurrentView();
	        if(view == null) return;
	        List selected_nodes = view.getSelectedNodes();

	        if ( selected_nodes.size() == 0 ) {return;}

	        Iterator selected_nodes_iterator = selected_nodes.iterator();
	        double bigX;
	        double bigY;
	        double smallX;
	        double smallY;
	        double W;
	        double H;
	        
	        PNodeView first = ( PNodeView )selected_nodes_iterator.next();
	        bigX = first.getXPosition();
	        smallX = bigX;
	        bigY = first.getYPosition();
	        smallY = bigY;
	    
	        while ( selected_nodes_iterator.hasNext() ) {
	          PNodeView nv = ( PNodeView )selected_nodes_iterator.next();
	          double x = nv.getXPosition();
	          double y = nv.getYPosition();

	          if ( x > bigX ) {
	            bigX = x;
	          } else if ( x < smallX ) {
	            smallX = x;
	          }

	          if ( y > bigY ) {
	            bigY = y;
	          } else if ( y < smallY ) {
	            smallY = y;
	          }
	        }
	        
	        PBounds zoomToBounds;
	        if (selected_nodes.size() == 1) {
	          zoomToBounds = new PBounds( smallX - 100 , smallY - 100 , ( bigX - smallX + 200 ), ( bigY - smallY + 200 ) );
	        } else {
	          zoomToBounds = new PBounds( smallX  , smallY  , ( bigX - smallX + 100 ), ( bigY - smallY + 100 ) );
	        }
	        PTransformActivity activity =  view.getCanvas().getCamera().animateViewToCenterBounds( zoomToBounds, true, 500 );
	    }
	}

	
	public static class LayoutAction extends HGVAction
    {

      public LayoutAction()
      {
         super(ActionManager.LAYOUT_ACTION);
         setAcceleratorCombo(KeyEvent.VK_L, ActionEvent.CTRL_MASK);
      }

      public void actionPerformed(ActionEvent e)
      {
          GraphView view = HGVKit.getCurrentView();
          if(view == null) return;
          HGVKit.prefered_layout.applyLayout(view);
      }
    }
	
	public static class SelectPrefLayoutAction extends AbstractAction
	  {

	    public SelectPrefLayoutAction()
	    {
	       super(ActionManager.PREFERED_LAYOUT_ACTION);
	    }

	    public void actionPerformed(ActionEvent e)
	    {
	        DialogDescriptor d = new DialogDescriptor(GUIUtilities.getFrame(), new SelectLayoutPanel(), 
	                ActionManager.PREFERED_LAYOUT_ACTION);
	        d.setModal(true);
	        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
	        DialogDisplayer.getDefault().notify(d);
	    }
	  }
	  
	  public static class SelectLayoutPanel extends JPanel
	  {
	    ButtonGroup group  = new ButtonGroup();
	    
	    /**
	     * This is the default constructor
	     */
	    public SelectLayoutPanel()
	    {
	        super();
	        initialize();
	    }

	    /**
	     * This method initializes this
	     * 
	     * @return void
	     */
	    private void initialize()
	    {
	        
	        Set<Layout> layouts = HGVKit.getLayouts(); 
	        this.setLayout(new GridBagLayout());
	        int i = 0;
	        final Map<String,Layout> layoutMap = new HashMap<String,Layout>(); 
	        for(Layout l : layouts)
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
	            butt.addActionListener(new ActionListener(){
	                public void actionPerformed(ActionEvent e) 
	                {
	                    HGVKit.setPreferedLayout(
	                            layoutMap.get(e.getActionCommand()));
	                }
	            });
	            group.add(butt);
	        }
	    }
	  }  
}
