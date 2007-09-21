package org.hypergraphdb.viewer.view;

import org.hypergraphdb.viewer.HGVNetworkView;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

public class NetworkViewManager implements PropertyChangeListener,
		 WindowFocusListener, ChangeListener {
	private JTabbedPane container;
    private Map<HGVNetworkView, Component> networkViewMap;
	private Map<Component, HGVNetworkView> componentMap;
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);
	private static NetworkViewManager instance;
	
	public static NetworkViewManager getInstance(){
		if(instance == null)
			instance = new NetworkViewManager();
		return instance;
	}
	
	protected NetworkViewManager() {
		initialize();
	}

	protected void initialize() {
		// create a Tabbed Style NetworkView manager
		container = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);
		container.addChangeListener(this);

		// add Help hooks
		// TODO: temporarily switched off
		// cytoscapeDesktop.getHelpBroker().enableHelp(container,
		// "network-view-manager", null);
		networkViewMap = new HashMap<HGVNetworkView, Component>();
		componentMap = new HashMap<Component, HGVNetworkView>();
	}

	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	public JTabbedPane getTabbedPane() {
		return container;
	}

	// Fire Events when a Managed Network View gets the Focus
	/**
	 * For Tabbed Panes
	 */
	public void stateChanged(ChangeEvent e) {
		HGVNetworkView view =  (HGVNetworkView) componentMap.get(container
				.getSelectedComponent());
		if (view == null) {
			return;
		}
		firePropertyChange(HGVDesktop.NETWORK_VIEW_FOCUSED, null, view);
	}

	/**
	 * For Exteernal Frames
	 */
	public void windowGainedFocus(WindowEvent e) {
		HGVNetworkView view = componentMap.get(e.getWindow());
		// System.out.println( " Window Gained Focus: "+ view );
		if (view == null) {
			return;
		}
		firePropertyChange(HGVDesktop.NETWORK_VIEW_FOCUSED, null, view);
	}

	public void windowLostFocus(WindowEvent e) {
	}

	/**
	 * This handles all of the incoming PropertyChangeEvents. If you are going
	 * to have multiple NetworkViewManagers, then this method should be extended
	 * such that the desired behaviour is achieved, assuming of course that you
	 * want your NetworkViewManagers to behave differently.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		// handle events
		// handle focus event
		if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_FOCUS) {
			HGVNetworkView view = (HGVNetworkView) e.getNewValue();
			e = null;
			setFocus(view);
		}
		// handle putting a newly created HGVNetworkView into a Container
		else if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_CREATED) {
			HGVNetworkView new_view = (HGVNetworkView) e.getNewValue();
			createContainer(new_view);
			e = null;
		}
		// handle a NetworkView destroyed
		else if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_DESTROYED) {
			HGVNetworkView view = (HGVNetworkView) e.getNewValue();
			removeView(view);
			e = null;
		}
	}

	/**
	 * Fires a PropertyChangeEvent
	 */
	public void firePropertyChange(String property_type, Object old_value,
			Object new_value) {
		pcs.firePropertyChange(new PropertyChangeEvent(this, property_type,
				old_value, new_value));
	}

	/**
	 * Sets the focus of the passed network, if possible The Network ID
	 * corresponds to the HGVNetworkView.getNetwork().getIdentifier()
	 */
	protected void setFocus(HGVNetworkView view) {
		if (networkViewMap.containsKey(view)) {
			// there is a NetworkView for this network
			try {
				container.setSelectedComponent((Component) networkViewMap
								.get(view));
			} catch (Exception e) {
				// e.printStackTrace();
				// System.err.println( "Network View unable to be focused"
				// );
			}
		}
	}

	protected void removeView(HGVNetworkView view) {
		try {
			container.remove((Component) networkViewMap
					.get(view));
		} catch (Exception e) {
			// possible error
		}

		networkViewMap.remove(view);
	}

	/**
	 * Contains a HGVNetworkView according to the view type of this
	 * NetworkViewManager
	 */
	protected void createContainer(HGVNetworkView view) {
		if (networkViewMap.containsKey(view)) {
			// already contains
			return;
		}
		// put the CyNetworkViews Component into the Tabbed Pane
		container.addTab(view.getIdentifier(), view.getComponent());
		networkViewMap.put(view, view.getComponent());
		componentMap.put(view.getComponent(), view);
		//for(PropertyChangeListener l: pcs.getPropertyChangeListeners())
		//	System.out.println("" + l);
		
		firePropertyChange(HGVDesktop.NETWORK_VIEW_FOCUSED, null, view);
	}
}
