package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.AbstractAction;
import giny.model.*;
import giny.view.*;
import phoebe.*;
import java.util.*;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGValueLink;
import org.hypergraphdb.handle.UUIDPersistentHandle;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNode;


import org.hypergraphdb.viewer.util.*;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.HGVEdge;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.hg.HGUtils;

/**
 *
 */
public class AddLinkAction extends HGVAction
{
    
    /** Creates a new instance of AddLinkAction */
    public AddLinkAction()
    {
        super(ActionManager.ADD_LINK_ACTION);
    }
    
    public void actionPerformed( ActionEvent ev )
    {
        HGVNetworkView view = HGViewer.getCurrentView();
        if(view == null) return;
        HGVNetwork net = HGViewer.getCurrentNetwork();
        //GraphPerspective perspective = view.getGraphPerspective();
        int[] node_indicies = view.getSelectedNodeIndices();
        
         if(node_indicies.length == 0)
        {
            GUIUtilities.showWarning(GUIUtilities.getFrame(view.getComponent()),
            		"No nodes selected. The link could not be created");
            return;
        }
        
        HGHandle[] handles = new HGHandle[node_indicies.length];
        for(int i = 0; i<node_indicies.length; i++)
        {
        	handles[i]  = ((HGVNode) HGViewer.getRootGraph().getNode(node_indicies[i])).getHandle();
        }
        
        NotifyDescriptor d =
        new NotifyDescriptor.InputLine(GUIUtilities.getFrame(), "", "Specify the link value",
        NotifyDescriptor.PLAIN_MESSAGE,NotifyDescriptor.OK_CANCEL_OPTION);
        String name = null;
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
        {
            name = ((NotifyDescriptor.InputLine)d).getInputText();
            if (name == null || name.equals(""))
               return;
        }
       
        HGVNode node = HGUtils.addNode(net.getHyperGraph(), 
        		net.getHyperGraph().add(new HGValueLink(name, handles)));
        for(int i = 0; i< handles.length; i++)
        {
            HGVEdge edge = HGViewer.getHGVEdge(node, (HGVNode) HGViewer.getRootGraph().getNode(node_indicies[i]),
            true);
            net.addEdge(edge);
        }
        
        view.redrawGraph();
    }

}
