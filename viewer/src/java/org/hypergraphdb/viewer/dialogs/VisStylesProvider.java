/*
 * This file is part of the Scriba source distribution. This is free, open-source 
 * software. For full licensing information, please see the LicensingInformation file
 * at the root level of the distribution.
 *
 * Copyright (c) 2006-2007 Kobrix Software, Inc.
 */
package org.hypergraphdb.viewer.dialogs;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.visual.VisualStyle;

/**
 * Dynamic menu showing the list of available Visual Styles
 */
public class VisStylesProvider implements DynamicMenuProvider
{
    private static final long serialVersionUID = 5406878794567628416L;
    
    public VisStylesProvider()
    {
    }

    public boolean updateEveryTime()
    {
        return true;
    }

    public void update(final JMenu menu)
    {
        GraphView view = HGVKit.getCurrentView(); 
        if (view == null) return;
        Collection<JMenuItem> items = getStyles(view);
        for (JMenuItem item : items)
            menu.add(item);
    }

    static final Collection<JMenuItem> EMPTY = new LinkedList<JMenuItem>();
    static Collection<JMenuItem> langMenuItems;

    public static Collection<JMenuItem> getStyles(final GraphView view)
    {
       initStyleMenuItems(view);
       for (final JMenuItem m : langMenuItems)
        {
            for (ItemListener l : m.getItemListeners())
                m.removeItemListener(l);
            m.setSelected(view.getVisualStyle().getName().equals(m.getText()));
            m.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e)
                {
                    if (m.isSelected())
                    {
                        VisualStyle vs = 
                            VisualManager.getInstance().getVisualStyle(m.getText());
                        view.setVisualStyle(vs);
                    }
                }
            });
        }

        return langMenuItems;
    }

    private static void initStyleMenuItems(final GraphView view)
    {   
        ButtonGroup group = new ButtonGroup();
        langMenuItems = new HashSet<JMenuItem>();
        for(String s: VisualManager.getInstance().getVisualStyleNames())
        {
            JMenuItem m = new JRadioButtonMenuItem(s);
            group.add(m);
            langMenuItems.add(m);
        }
    }
}
