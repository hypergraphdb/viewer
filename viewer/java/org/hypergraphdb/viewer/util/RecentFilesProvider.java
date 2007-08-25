package org.hypergraphdb.viewer.util;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.*;
import java.util.*;
import org.hypergraphdb.viewer.*;
import org.hypergraphdb.viewer.hg.LoadHyperGraphFileAction;
import org.hypergraphdb.viewer.view.HGVNetworkView;


public class RecentFilesProvider implements PropertyChangeListener
{
    public static final String KEY = "recent";
    public static final String PROPS_FILE = "recent.props";
    private static RecentFilesProvider instance = null;
   // private Set<String> file_paths;
    private JMenu menu;
    
    private RecentFilesProvider()
    {
    	  HGViewer.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
    }
    
    public static RecentFilesProvider getInstance()
    {
        if(instance == null)
            instance = new RecentFilesProvider();
        return instance;
    }
    
       
   
    public void propertyChange( PropertyChangeEvent e )
    {
        
        if (//e.getPropertyName() == HGViewer.NETWORK_CREATED ||
        e.getPropertyName() == HGViewer.NETWORK_DESTROYED
        )
        {
           // System.out.println("RecentFilesProvider: - propertyChange: " + e.getNewValue());
           // if(HGViewer.getCurrentNetwork() == null)
            //    return;
            String f = ((HGVNetwork) e.getNewValue()).getHyperGraph().getStore().getDatabaseLocation();
            System.out.println("RecentFilesProvider: - propertyChange - f: " + f + " net: " + e.getNewValue());
            if(!(AppConfig.getInstance().getMRUF().contains(f)))
            {
            	AppConfig.getInstance().getMRUF().add(f);
                update(menu);
            }
        }else if (e.getPropertyName() == HGViewer.EXIT){
        	for(HGVNetwork net: HGViewer.getNetworkMap().keySet())
        		AppConfig.getInstance().getMRUF().add(
        				net.getHyperGraph().getStore().getDatabaseLocation());
        }
        
    }
    
    public void update(JMenu menu)
    {
        this.menu = menu;
        menu.removeAll();
        ActionListener actionListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                LoadHyperGraphFileAction.loadHyperGraph(
                   new File(evt.getActionCommand()));
            }
        };
        
        if(AppConfig.getInstance().getMRUF().size() == 0)
        {
            JMenuItem menuItem = new JMenuItem(
            "No recent files");
            menuItem.setEnabled(false);
            menu.add(menuItem);
            return;
        }
        
        Vector menuItems = new Vector();
        
        boolean sort = false;
        int maxItems = 20;
        
        Iterator iter = AppConfig.getInstance().getMRUF().iterator();
        while(iter.hasNext())
        {
            String path = (String) iter.next();
            JMenuItem menuItem = new JMenuItem(path);
            menuItem.setActionCommand(path);
            menuItem.addActionListener(actionListener);
            //menuItem.addMouseListener(mouseListener);
            //menuItem.setIcon(GUIUtilities.loadIcon("File.png"));
            if(sort)
                menuItems.addElement(menuItem);
            else
            {
                if(menu.getMenuComponentCount() >= maxItems
                && iter.hasNext())
                {
                    JMenu newMenu = new JMenu("more");
                    menu.add(newMenu);
                    menu = newMenu;
                }
                
                menu.add(menuItem);
            }
        }
        
        /*//TODO??? Implement it
        if(sort)
        {
            MiscUtilities.quicksort(menuItems,
            new MiscUtilities.MenuItemCompare());
            for(int i = 0; i < menuItems.size(); i++)
            {
                if(menu.getMenuComponentCount() >= maxItems
                && i != 0)
                {
                    JMenu newMenu = new JMenu(
                    HGViewer.getProperty("common.more"));
                    menu.add(newMenu);
                    menu = newMenu;
                }
                
                menu.add((JMenuItem)menuItems.elementAt(i));
            }
        }
         */
    }
}
