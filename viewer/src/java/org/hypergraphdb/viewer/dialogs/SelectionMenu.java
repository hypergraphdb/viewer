package org.hypergraphdb.viewer.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.hypergraphdb.viewer.HGVKit;

/**
 * Menu with the HGViewer selection options: nodes only, edges only, both. 
 */
public class SelectionMenu implements DynamicMenuProvider 
{
    private static final long serialVersionUID = -456032254718357127L;
    
    public boolean updateEveryTime()
    {
        return true;
    }

    public void update(final JMenu menu)
    {
        JCheckBoxMenuItem nodes = makeCheckBox("Nodes",HGVKit.SELECT_NODES_ONLY);
        nodes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
                        | ActionEvent.SHIFT_MASK));
        JCheckBoxMenuItem edges = makeCheckBox("Edges", HGVKit.SELECT_EDGES_ONLY);
        edges.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.CTRL_MASK
                        | ActionEvent.SHIFT_MASK));
        JCheckBoxMenuItem nodesAndEdges = makeCheckBox("Nodes and Edges", 
                HGVKit.SELECT_NODES_AND_EDGES);
        nodesAndEdges.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.CTRL_MASK
                        | ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK));
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(nodes);
        modeGroup.add(edges);
        modeGroup.add(nodesAndEdges);
        menu.add(nodes);
        menu.add(edges);
        menu.add(nodesAndEdges);
    }
    
    private JCheckBoxMenuItem makeCheckBox(String name, final int mode) 
    {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(
                makeAction(name, mode));
        item.setSelected(HGVKit.getSelectionMode() == mode);
        return item;
    }
    private AbstractAction makeAction(String name, final int mode)
    {
        return  new AbstractAction(name) {
            public void actionPerformed(ActionEvent e)
            {
                // Do this in the GUI Event Dispatch thread...
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                        HGVKit
                                .setSelectionMode(mode);
                    }
                });
            }
        };
    }
}