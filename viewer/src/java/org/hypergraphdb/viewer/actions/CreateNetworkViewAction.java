package org.hypergraphdb.viewer.actions;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class CreateNetworkViewAction extends HGVAction {

    public CreateNetworkViewAction() {
        super(ActionManager.CREATE_VIEW_ACTION);
        setAcceleratorCombo(java.awt.event.KeyEvent.VK_V, ActionEvent.ALT_MASK);
    }

    public CreateNetworkViewAction(boolean label) {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        HGVNetworkView cyNetwork = HGVKit.getCurrentView();
        createViewFromCurrentNetwork(cyNetwork);
    }

    public static void createViewFromCurrentNetwork(HGVNetworkView cyNetwork) {
        NumberFormat formatter = new DecimalFormat("#,###,###");
        if (cyNetwork.getNodeCount()
                > AppConfig.getInstance().getSecondaryViewThreshold()) {
            int n = JOptionPane.showConfirmDialog(GUIUtilities.getFrame(),
                    "Network contains "
                    + formatter.format(cyNetwork.getNodeCount())
                    + " nodes and " + formatter.format
                    (cyNetwork.getEdgeCount()) + " edges.  "
                    + "\nRendering a network this size may take several "
                    + "minutes.\n"
                    + "Do you wish to proceed?", "Rendering Large Network",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
            	HGVKit.createHGVComponent(cyNetwork);
            } else {
                JOptionPane.showMessageDialog(HGVKit.getDesktop(),
                        "Create View Request Cancelled by User.");
            }
        } else {
        	HGVKit.createHGVComponent(cyNetwork);            
        }
    }
}
