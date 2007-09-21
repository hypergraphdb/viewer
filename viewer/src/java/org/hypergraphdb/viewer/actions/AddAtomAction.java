// $Revision: 1.3 $
// $Date: 2006/02/03 18:24:16 $
// $Author: bizi $


package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.AbstractAction;
import java.lang.reflect.Constructor;
import java.util.*;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.handle.HGLiveHandle;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.type.Slot;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.*;
import org.hypergraphdb.viewer.dialogs.*;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.hg.HGUtils;

public class AddAtomAction extends HGVAction
{
    
    public AddAtomAction()
    {
        super("Add atom to hypergraph");
        setEnabled(false);
    }
    
    
    public void actionPerformed( ActionEvent ev )
    {
        HyperGraph hg = HGVKit.getCurrentNetwork().getHyperGraph(); 
        List<String> list = new LinkedList<String>();
        for(Iterator<HGAtomType> it = HGUtils.getAllAtomTypes(hg).iterator(); it.hasNext();)
        {
           Class clazz = it.next().getClass();
           if(clazz.isAssignableFrom(Slot.class) || clazz.isAssignableFrom(RecordType.class))
               continue;
           list.add(clazz.getName());
        }
        Collections.sort(list);
        NameListPanel panel = new NameListPanel("Type: ", "Value: ", list);
        DialogDescriptor d = new DialogDescriptor(
        		GUIUtilities.getFrame(), panel, "Add atom to hypergraph");
        d.setModal(true);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
        {
            String clazz = panel.getValue();
            String value = panel.getName();
            Object obj = null;
            try
            {
              //Character is the only exception
                if(clazz.indexOf("Character") != -1)
                {
                    obj = new Character(value.charAt(0));
                }
                else
                {
                   Constructor ctr = Class.forName(clazz).getConstructor(new Class[]{String.class});
                   obj = ctr.newInstance(new Object[]{value});
                }
            }catch(Exception ex)
            {
                ex.printStackTrace();
                return;
            }
            HGUtils.addNode(hg, hg.add(obj));
            HGVKit.getCurrentView().redrawGraph();
        }
        
    }//action performed
}

