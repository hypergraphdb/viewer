package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;

import java.awt.Dimension;
import phoebe.event.BirdsEyeView;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.view.HGVDesktop;
import java.beans.*;

import edu.umd.cs.piccolo.PLayer;

public class BirdsEyeViewAction extends HGVAction implements
		PropertyChangeListener {

	BirdsEyeView bev;
    boolean on = false;

	public BirdsEyeViewAction() {
		super(ActionManager.TOGGLE_BIRDS_EYE_VIEW_ACTION);
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_FOCUSED
				|| e.getPropertyName() == HGVDesktop.NETWORK_VIEW_FOCUS) {
			bev.disconnect();
			try {
				bev.connect(HGVKit
						.getCurrentView().getCanvas(),
						new PLayer[] { HGVKit.getCurrentView().getCanvas()
								.getLayer() });
				bev.updateFromViewed();
			} catch (Exception ex) {
				// no newly focused network
			}
		}

		if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_DESTROYED) {
			bev.disconnect();
			try {
				bev.connect(HGVKit
						.getCurrentView().getCanvas(),
						new PLayer[] { HGVKit
								.getCurrentView().getCanvas()
								.getLayer() });
				bev.updateFromViewed();
			} catch (Exception ex) {
				// no newly focused network
			}
		}

	}

	public void actionPerformed(ActionEvent e) {

		if (!on) {
			bev = new BirdsEyeView();
			bev.connect(HGVKit.getCurrentView()
					.getCanvas(), new PLayer[] { HGVKit
					.getCurrentView().getCanvas().getLayer() });

			bev.setMinimumSize(new Dimension(180, 180));
			bev.setSize(new Dimension(180, 180));
			HGVKit.getDesktop().getNetworkPanel().setNavigator(bev);
			HGVKit.getDesktop().getSwingPropertyChangeSupport()
					.addPropertyChangeListener(this);
			bev.updateFromViewed();
			on = true;
		} else {
			if (bev != null) {
				bev.disconnect();
				bev = null;
			}
			HGVKit.getDesktop().getNetworkPanel()
					.setNavigator(
							HGVKit.getDesktop().getNetworkPanel()
									.getNavigatorPanel());
			HGVKit.getDesktop().getSwingPropertyChangeSupport()
					.removePropertyChangeListener(this);
			on = false;
		}
	}

}
