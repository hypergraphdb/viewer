package org.hypergraphdb.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import org.hypergraphdb.viewer.actions.*;
import org.hypergraphdb.viewer.hg.*;

public class ActionManager
{
	public static final String LOAD_HYPER_GRAPH_ACTION = "HyperGraph...";
	public static final String LOAD_WORD_NET_ACTION = "WordNet...";
	public final static String PRINT_ACTION = "Print...";
	public final static String EXPORT_ACTION = "Export As...";
	public static final String EXIT_ACTION = "Exit";
	public static final String CREATE_VIEW_ACTION = "Create View";
	public static final String DESTROY_VIEW_ACTION = "Destroy View";
	public static final String DESTROY_NETWORK_ACTION = "Destroy Network";
	public static final String DESTROY_SELECTED_NODES_EDGES_ACTION = "Destroy Selected Nodes/Edges";
	public final static String PREFERENCES_ACTION = "Preferences...";
	public static final String INVERT_NODE_SELECTION_ACTION = "Invert selection";
	public static final String HIDE_NODE_SELECTION_ACTION = "Hide selection";
	public static final String SHOW_ALL_NODES_ACTION = "Show All";
	public static final String SELECT_ALL_NODES_ACTION = "Select all nodes";
	public static final String DESELECT_ALL_NODES_ACTION = "Deselect all nodes";
	public static final String SELECTED_FIRST_NEIGHBORS_ACTION = "First neighbors of selected nodes";

	public static final String INVERT_EDGE_SELECTION_ACTION = "Invert edge selection";
	public static final String HIDE_EDGE_SELECTION_ACTION = "Hide edge selection";
	public static final String SHOW_ALL_EDGES_ACTION = "Show All Edges";
	public static final String SELECT_ALL_EDGES_ACTION = "Select all edges";
	public static final String DESELECT_ALL_EDGES_ACTION = "Deselect all edges";
	public static final String NEW_WINDOW_SELECTED_NODES_ONLY_ACTION = "Selected nodes only";
	public static final String NEW_WINDOW_SELECTED_NODES_EDGES_ACTION = "Selected nodes, Selected edges";
	public static final String NEW_WINDOW_CLONE_WHOLE_GRAPH_ACTION = "Whole network";
	public static final String SELECT_ALL_ACTION = "Select all nodes and edges";
	public static final String DESELECT_ALL_ACTION = "Deselect All Nodes and Edges";
	public static final String ROTATE_SCALE_ACTION = "Rotate/Scale Network";
	public static final String PREFERED_LAYOUT_ACTION = "Select Preferred Layout";
	public static final String TOGGLE_BIRDS_EYE_VIEW_ACTION = "Toggle Overview";
	public static final String BACKGROUND_COLOR_ACTION = "Change Background Color";
	public static final String VISUAL_PROPERTIES_ACTION = "Set Visual Properties";
	public static final String ABOUT_ACTION = "About";

	
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
		actions.put(DESTROY_NETWORK_ACTION, new DestroyNetworkAction());
		actions.put(DESTROY_SELECTED_NODES_EDGES_ACTION, new DestroySelectedAction());
		actions.put(PREFERENCES_ACTION, new PreferenceAction());
		actions.put(INVERT_NODE_SELECTION_ACTION, new InvertSelectedNodesAction());
		actions.put(HIDE_NODE_SELECTION_ACTION, new HideSelectedNodesAction());
		actions.put(SELECT_ALL_NODES_ACTION, new SelectAllNodesAction());
		actions.put(DESELECT_ALL_NODES_ACTION, new DeSelectAllNodesAction());
		actions.put(SELECTED_FIRST_NEIGHBORS_ACTION, new SelectFirstNeighborsAction());
		
		actions.put(INVERT_EDGE_SELECTION_ACTION, new InvertSelectedEdgesAction());
		actions.put(HIDE_EDGE_SELECTION_ACTION, new HideSelectedEdgesAction());
		actions.put(SELECT_ALL_EDGES_ACTION, new SelectAllEdgesAction());
		actions.put(DESELECT_ALL_EDGES_ACTION, new DeSelectAllEdgesAction());
		
		actions.put(NEW_WINDOW_SELECTED_NODES_ONLY_ACTION, new NewWindowSelectedNodesOnlyAction());
		actions.put(NEW_WINDOW_SELECTED_NODES_EDGES_ACTION, new NewWindowSelectedNodesEdgesAction());
		actions.put(NEW_WINDOW_CLONE_WHOLE_GRAPH_ACTION, new CloneGraphInNewWindowAction());
		
		actions.put(SELECT_ALL_ACTION, new SelectAllAction());
		actions.put(DESELECT_ALL_ACTION, new DeselectAllAction());
		actions.put(ROTATE_SCALE_ACTION, new RotationScaleLayoutAction());
		actions.put(PREFERED_LAYOUT_ACTION, new LayoutsMenu.SelectPrefLayoutAction());
		actions.put(TOGGLE_BIRDS_EYE_VIEW_ACTION, new BirdsEyeViewAction());
		actions.put(BACKGROUND_COLOR_ACTION, new BackgroundColorAction());
		actions.put(VISUAL_PROPERTIES_ACTION, new SetVisualPropertiesAction());
		actions.put(ABOUT_ACTION, new HelpAboutAction());
		
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
}
