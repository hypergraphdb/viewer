package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.awt.Dimension;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import phoebe.event.BirdsEyeView;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.view.HGVDesktop;
import java.beans.*;

import edu.umd.cs.piccolo.PLayer;

public class BirdsEyeViewAction extends HGVAction implements
		PropertyChangeListener {

	BirdsEyeView bev;
    boolean on = false;

	public BirdsEyeViewAction() {
		super("Toggle Overview");
		setPreferredMenu("Visualization");
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_FOCUSED
				|| e.getPropertyName() == HGVDesktop.NETWORK_VIEW_FOCUS) {
			bev.disconnect();
			try {
				bev.connect(HGViewer
						.getCurrentView().getCanvas(),
						new PLayer[] { HGViewer.getCurrentView().getCanvas()
								.getLayer() });
				bev.updateFromViewed();
			} catch (Exception ex) {
				// no newly focused network
			}
		}

		if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_DESTROYED) {
			bev.disconnect();
			try {
				bev.connect(HGViewer
						.getCurrentView().getCanvas(),
						new PLayer[] { HGViewer
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
			bev.connect(HGViewer.getCurrentView()
					.getCanvas(), new PLayer[] { HGViewer
					.getCurrentView().getCanvas().getLayer() });

			bev.setMinimumSize(new Dimension(180, 180));
			bev.setSize(new Dimension(180, 180));
			HGViewer.getDesktop().getNetworkPanel().setNavigator(bev);
			HGViewer.getDesktop().getSwingPropertyChangeSupport()
					.addPropertyChangeListener(this);
			bev.updateFromViewed();
			on = true;
		} else {
			if (bev != null) {
				bev.disconnect();
				bev = null;
			}
			HGViewer.getDesktop().getNetworkPanel()
					.setNavigator(
							HGViewer.getDesktop().getNetworkPanel()
									.getNavigatorPanel());
			HGViewer.getDesktop().getSwingPropertyChangeSupport()
					.removePropertyChangeListener(this);
			on = false;
		}
	}

}
