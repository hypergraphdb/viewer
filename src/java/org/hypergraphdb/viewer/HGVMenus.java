/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
// $Revision: 1.3 $
// $Date: 2006/02/27 19:59:19 $
// $Author: bizi $
//------------------------------------------------------------------------------
package org.hypergraphdb.viewer;

//------------------------------------------------------------------------------
import java.awt.Component;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.hypergraphdb.viewer.dialogs.SelectionMenu;
import org.hypergraphdb.viewer.dialogs.SquiggleMenu;
import org.hypergraphdb.viewer.dialogs.EnhancedMenu;
import org.hypergraphdb.viewer.dialogs.VisStylesProvider;
import org.hypergraphdb.viewer.event.GraphViewChangeEvent;
import org.hypergraphdb.viewer.event.GraphViewChangeListener;
import org.hypergraphdb.viewer.layout.Layout;
import org.hypergraphdb.viewer.layout.LayoutAction;
import org.hypergraphdb.viewer.util.RecentFilesProvider;

//------------------------------------------------------------------------------
/**
 * This class creates the menu and tool bars for the HGVDesktop. It also
 * provides access to individual menus and items.
 */
public class HGVMenus implements GraphViewChangeListener
{
    private static final String IMG_BASE = "org/hypergraphdb/viewer/images/";

    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu editMenu;
    JMenu selectMenu;
    JMenu displayNWSubMenu;
    JMenu layoutMenu;
    JMenu vizMenu;
    JMenu zoomMenu;

    JToolBar toolBar;
    boolean nodesRequiredItemsEnabled;
    public static HGVMenus instance = null;

    /**
     * Returns the singleton instance of this class
     * @return the singleton
     */
    public static HGVMenus getInstance()
    {
        if (instance == null) instance = new HGVMenus();
        return instance;
    }

    protected HGVMenus()
    {
        createMenuBar();
        initializeMenus();
    }

    /**
     * Returns the main menu bar constructed by this object.
     */
    public JMenuBar getMenuBar()
    {
        return menuBar;
    }

    /**
     * Returns the menu with items related to zooming.
     */
    public JMenu getZoomMenu()
    {
        if (zoomMenu != null) return zoomMenu;
        zoomMenu = new JMenu("Zoom");
        populateZoomMenu(zoomMenu);

        return zoomMenu;
    }

    void populateZoomMenu(JComponent zoomMenu)
    {
        zoomMenu.add(mi(ActionManager.ZOOM_IN_ACTION));
        zoomMenu.add(mi(ActionManager.ZOOM_OUT_ACTION));
        zoomMenu.add(mi(ActionManager.ZOOM_SELECTED_ACTION));
        zoomMenu.add(mi(ActionManager.FIT_ACTION));
    }

    /**
     * Returns the menu with items related to file operations.
     */
    public JMenu getFileMenu()
    {
        if (fileMenu != null) return fileMenu;
        fileMenu = new JMenu("File");
        populateFileMenu(fileMenu);
        return fileMenu;
    }

    void populateFileMenu(JComponent fileMenu)
    {
        JMenu loadSubMenu = new JMenu("Load");
        if (!HGVKit.isEmbeded())
        {
            // fill the Load submenu
            loadSubMenu.add(mi(ActionManager.LOAD_HYPER_GRAPH_ACTION));
            loadSubMenu.add(mi(ActionManager.LOAD_WORD_NET_ACTION));
            fileMenu.add(loadSubMenu);
            JMenu recentSubMenu = new JMenu("Recent");
            fileMenu.add(recentSubMenu);
            RecentFilesProvider.getInstance().update(recentSubMenu);
        }
        fileMenu.add(mi(ActionManager.PRINT_ACTION));
        fileMenu.add(mi(ActionManager.EXPORT_ACTION));
        if (!HGVKit.isEmbeded())
           fileMenu.add(mi(ActionManager.EXIT_ACTION));
    }

    /**
     * returns the menu with items related to editing the graph.
     */
    public JMenu getEditMenu()
    {
        if (editMenu != null) return editMenu;

        // TODO: make the Squiggle Stuff be better
        editMenu = new HGVMenu("Edit");
        populateEditMenu(editMenu);
        return editMenu;
    }

    void populateEditMenu(JComponent editMenu)
    {
        editMenu.add(new EnhancedMenu("Squiggle", new SquiggleMenu()));
        editMenu.add(mi(ActionManager.CREATE_VIEW_ACTION));
        editMenu.add(mi(ActionManager.DESTROY_VIEW_ACTION));
        editMenu.add(mi(ActionManager.HIDE_NODE_SELECTION_ACTION));
        // add Preferences...
        editMenu.add(new JSeparator());
        editMenu.add(new GlobMenuItem(ActionManager.getInstance().getAction(
                ActionManager.PREFERENCES_ACTION)));
    }

    /**
     * Returns the menu with items related to selecting nodes and edges in the
     * graph.
     */
    public JMenu getSelectMenu()
    {
        if (selectMenu != null) return selectMenu;
        selectMenu = new HGVMenu("Select");
        populateSelectMenu(selectMenu);
        return selectMenu;
    }

    void populateSelectMenu(JComponent menu)
    {
        if (HGVKit.isEmbeded())
            menu.add(new EnhancedMenu("Squiggle", new SquiggleMenu()));
        menu.add(new EnhancedMenu("Mouse Drag Selects", new SelectionMenu()));
        if (!HGVKit.isEmbeded())
        {
            displayNWSubMenu = new JMenu("To New Network");
            menu.add(displayNWSubMenu);
            displayNWSubMenu
                    .add(mi(ActionManager.NEW_WINDOW_SELECTED_NODES_ONLY_ACTION));
            displayNWSubMenu
                    .add(mi(ActionManager.NEW_WINDOW_SELECTED_NODES_EDGES_ACTION));
            displayNWSubMenu
                    .add(mi(ActionManager.NEW_WINDOW_CLONE_WHOLE_GRAPH_ACTION));
        }
        JMenu nodes = new JMenu("Nodes");
        menu.add(nodes);
        //nodes.add(mi(ActionManager.INVERT_NODE_SELECTION_ACTION));
        nodes.add(mi(ActionManager.HIDE_NODE_SELECTION_ACTION));
        nodes.add(mi(ActionManager.SELECT_ALL_NODES_ACTION));
        nodes.add(mi(ActionManager.SELECTED_FIRST_NEIGHBORS_ACTION));

        JMenu edges = new JMenu("Edges");
        menu.add(edges);
       // edges.add(mi(ActionManager.INVERT_EDGE_SELECTION_ACTION));
        edges.add(mi(ActionManager.HIDE_EDGE_SELECTION_ACTION));
        edges.add(mi(ActionManager.SELECT_ALL_EDGES_ACTION));

        //menu.add(mi(ActionManager.SELECT_ALL_ACTION));
    }

    /**
     * Returns the menu with items realted to layout actions.
     */
    public JMenu getLayoutMenu()
    {
        if (layoutMenu != null) return layoutMenu;
        layoutMenu = new JMenu("Layout");
        populateLayoutMenu(layoutMenu);
        return layoutMenu;
    }

    void populateLayoutMenu(JComponent layoutMenu)
    {
        int i = 1;
        for (Layout l : HGVKit.getLayouts())
        {
            LayoutAction action = new LayoutAction(l, i);
            ActionManager.getInstance().putAction(action);
            layoutMenu.add(new JMenuItem(action));
            i++;
        }
        layoutMenu.add(new JSeparator());
        layoutMenu.add(mi(ActionManager.PREFERED_LAYOUT_ACTION));
    }

    /**
     * Returns the menu with items related to visualiation.
     */
    public JMenu getVizMenu()
    {
        if (vizMenu != null) return vizMenu;
        vizMenu = new JMenu("Visualization");
        populateVizMenu(vizMenu);

        return vizMenu;
    }

    void populateVizMenu(JComponent vizMenu)
    {
        if (!HGVKit.isEmbeded())
            vizMenu.add(mi(ActionManager.TOGGLE_BIRDS_EYE_VIEW_ACTION));
        if (HGVKit.isEmbeded())
          vizMenu.add(new EnhancedMenu("Set Current Style",
                new VisStylesProvider()), 0);
        vizMenu.add(mi(ActionManager.BACKGROUND_COLOR_ACTION));
        JMenu menu = new JMenu("Set Visual Properties");
        menu.add(mi(ActionManager.NODE_VISUAL_PROPERTIES_ACTION));
        menu.add(mi(ActionManager.EDGE_VISUAL_PROPERTIES_ACTION));
        vizMenu.add(menu);
    }

     /**
     * Returns the toolbar object constructed by this class.
     */
    public JToolBar getToolBar()
    {
        boolean big = new Boolean(
                (String) AppConfig.getInstance().getProperty(
                        AppConfig.BIG_ICONS, "false"));
        String size = big ? "36" : "16";
        if (toolBar != null) return toolBar;
        ActionManager man = ActionManager.getInstance();
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton loadButton = toolBar.add(man
                .getAction(ActionManager.LOAD_HYPER_GRAPH_ACTION));
        loadButton.setIcon(new ImageIcon(getImgResource("new/load" + size + ".gif")));
        loadButton.setToolTipText("Load Network");
        loadButton.setBorderPainted(false);
        loadButton.setRolloverEnabled(true);
        loadButton.setText("");
       
        JButton hideSelectedButton = toolBar.add(man
                .getAction(ActionManager.HIDE_SELECTED_ACTION));
        hideSelectedButton.setIcon(new ImageIcon(
                getImgResource("new/delete" + size + ".gif")));
        hideSelectedButton.setToolTipText("Hide Selected Region");
        hideSelectedButton.setText("");
        hideSelectedButton.setBorderPainted(false);
       // toolBar.addSeparator();

        JButton zoomInButton = new JButton(man
                .getAction(ActionManager.ZOOM_IN_ACTION));
        toolBar.add(zoomInButton);
        zoomInButton
                .setIcon(new ImageIcon(getImgResource("new/zoom_in" + size + ".gif")));
        zoomInButton.setToolTipText("Zoom In");
        zoomInButton.setBorderPainted(false);
        zoomInButton.setRolloverEnabled(true);
        zoomInButton.setText("");

        JButton zoomOutButton = new JButton(man
                .getAction(ActionManager.ZOOM_OUT_ACTION));
        toolBar.add(zoomOutButton);
        zoomOutButton.setIcon(new ImageIcon(
                getImgResource("new/zoom_out" + size + ".gif")));
        zoomOutButton.setToolTipText("Zoom Out");
        zoomOutButton.setBorderPainted(false);
        zoomOutButton.setRolloverEnabled(true);
        zoomOutButton.setText("");

        JButton zoomSelectedButton = toolBar.add(man
                .getAction(ActionManager.ZOOM_SELECTED_ACTION));
        zoomSelectedButton.setIcon(new ImageIcon(
                getImgResource("new/crop" + size + ".gif")));
        zoomSelectedButton.setToolTipText("Zoom Selected Region");
        zoomSelectedButton.setBorderPainted(false);
        zoomSelectedButton.setText("");
        JButton zoomDisplayAllButton = toolBar.add(man
                .getAction(ActionManager.FIT_ACTION));
        zoomDisplayAllButton.setIcon(new ImageIcon(
                getImgResource("new/fit" + size + ".gif")));
        zoomDisplayAllButton
                .setToolTipText("Zoom out to display all of current Network");
        zoomDisplayAllButton.setBorderPainted(false);
        zoomDisplayAllButton.setText("");

        JButton vizButton = toolBar.add(man
                .getAction(ActionManager.NODE_VISUAL_PROPERTIES_ACTION));
        vizButton
                .setIcon(new ImageIcon(getImgResource("new/color_wheel" + size + ".gif")));
        vizButton.setToolTipText("Set Visual Properties");
        vizButton.setBorderPainted(false);
        vizButton.setText("");
        return toolBar;
    }

    /**
     * Enables or disables save, print, and display nodes in new window GUI
     * functions, based on the number of nodes in this window's graph
     * perspective. This function should be called after every operation which
     * adds or removes nodes from the current window.
     */
    public void setNodesRequiredItemsEnabled()
    {
        boolean newState = HGVKit.getCurrentView() != null
                && HGVKit.getCurrentView().getNodeCount() > 0;
        newState = true; // TODO: remove this once the
        if (!HGVKit.isEmbeded()) displayNWSubMenu.setEnabled(newState);
        nodesRequiredItemsEnabled = newState;
    }

    /**
     * Update the UI menus and buttons. When the graph view is changed, this
     * method is the listener which will update the UI items, enabling or
     * disabling items which are only available when the graph view is
     * non-empty.
     * 
     * @param e
     */
    public void graphChanged(GraphViewChangeEvent e)
    {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                setNodesRequiredItemsEnabled();
            }
        });
    }

    /**
     * Creates the menu bar and the various menus and submenus, but defers
     * filling those menus with items until later.
     */
    private void createMenuBar()
    {
        menuBar = new JMenuBar();
        menuBar.add(getFileMenu());

        menuBar.add(getEditMenu());

        menuBar.add(getSelectMenu());

        menuBar.add(getLayoutMenu());

        menuBar.add(getVizMenu());
        menuBar.add(getZoomMenu());

    }

    private void initializeMenus()
    {
        nodesRequiredItemsEnabled = false;
        if (!HGVKit.isEmbeded()) displayNWSubMenu.setEnabled(false);
        setNodesRequiredItemsEnabled();

    }

    private JMenuItem mi(String name)
    {
        return new JMenuItem(ActionManager.getInstance().getAction(name));
    }

    private static URL getImgResource(String res)
    {
        URL url = HGVMenus.class.getClassLoader().getResource(IMG_BASE + res);
        if (url == null)
        {
            System.out.println("Can't find resourse: " + res);
        }
        return url;
    }

    static class HGVMenu extends JMenu implements MenuListener
    {
        public HGVMenu()
        {
            super();
            addMenuListener(this);
        }

        public HGVMenu(String s)
        {
            super(s);
            addMenuListener(this);
        }

        public void menuSelected(MenuEvent e)
        {
            boolean b = HGVKit.getCurrentView() != null;
            for (int i = 0; i < getMenuComponentCount(); i++)
            {
                Component c = getMenuComponent(i);
                if (/* b == true && */c instanceof JMenuItem)
                {
                    Action a = ((JMenuItem) c).getAction();
                    if (a != null) b = a.isEnabled();
                }
                c.setEnabled(b);
            }
        }

        public void menuCanceled(MenuEvent e)
        {
        }

        public void menuDeselected(MenuEvent e)
        {
        }
    }

    // JMenuItem that can't be disabled
    static class GlobMenuItem extends JMenuItem
    {
        public GlobMenuItem()
        {
        }

        public GlobMenuItem(String text)
        {
            super(text);
        }

        public GlobMenuItem(Action a)
        {
            super(a);
        }

        @Override
        public boolean isEnabled()
        {
            return true;
        }

        public void setEnabled(boolean b)
        {
            // DO NOTHING
        }
    }

    static class GlobMenu extends JMenu
    {
        public GlobMenu(String text)
        {
            super(text);
        }

        @Override
        public boolean isEnabled()
        {
            return true;
        }

        public void setEnabled(boolean b)
        {
            // DO NOTHING
        }
    }
}
