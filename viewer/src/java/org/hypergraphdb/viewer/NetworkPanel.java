package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.hypergraphdb.viewer.util.swing.AbstractTreeTableModel;
import org.hypergraphdb.viewer.util.swing.JTreeTable;
import org.hypergraphdb.viewer.util.swing.TreeTableModel;

/**
 * Desktop Viewer Only.
 * Panel containing the table with opened GraphViews + BirdsEyeView if toggled on
 */
public class NetworkPanel extends JPanel implements PropertyChangeListener,
		TreeSelectionListener
{

	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);

	JTreeTable treeTable;
	ViewTreeNode root;
	JPanel navigatorPanel;
	JPopupMenu popup;
	PopupActionListener popupActionListener;
	JMenuItem createViewItem;
	JMenuItem destroyViewItem;
	JSplitPane split;

	public NetworkPanel() {
		super();
		initialize();
	}

	protected void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(181, 400));
		root = new ViewTreeNode("root", null);
		treeTable = new JTreeTable(new NetworkTreeTableModel(root));
		treeTable.getTree().addTreeSelectionListener(this);
		treeTable.getTree().setRootVisible(false);

		ToolTipManager.sharedInstance().registerComponent(treeTable);

		treeTable.getTree().setCellRenderer(new MyRenderer());

		treeTable.getColumn("Network").setPreferredWidth(100);
		treeTable.getColumn("Nodes").setPreferredWidth(45);
		treeTable.getColumn("Edges").setPreferredWidth(45);

		navigatorPanel = new JPanel();
		navigatorPanel.setMinimumSize(new Dimension(180, 180));
		navigatorPanel.setMaximumSize(new Dimension(180, 180));
		navigatorPanel.setPreferredSize(new Dimension(180, 180));

		JScrollPane scroll = new JScrollPane(treeTable);
		scroll.setMinimumSize(new Dimension(180, 80));
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll,
				navigatorPanel);
		split.setResizeWeight(1);
		add(split);

		// this mouse listener listens for the right-click event and will show
		// the pop-up
		// window when that occurrs
		treeTable.addMouseListener(new PopupListener());

		// create and populate the popup window
		popup = new JPopupMenu();
		createViewItem = new JMenuItem(PopupActionListener.CREATE_VIEW);
		destroyViewItem = new JMenuItem(PopupActionListener.DESTROY_VIEW);
		

		// action listener which performs the tasks associated with the popup
		// listener
		popupActionListener = new PopupActionListener();
		createViewItem.addActionListener(popupActionListener);
		destroyViewItem.addActionListener(popupActionListener);
		popup.add(createViewItem);
		popup.add(destroyViewItem);
		
	}

	public void setNavigator(JComponent comp) {
		split.setRightComponent(comp);
		split.validate();
	}

	public JPanel getNavigatorPanel() {
		return navigatorPanel;
	}

	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	void removeGraphView(GraphView view) {

		ViewTreeNode node = getGraphViewNode(view);
		Enumeration children = node.children();
		ViewTreeNode child = null;
		ArrayList removed_children = new ArrayList();
		while (children.hasMoreElements()) {
			removed_children.add(children.nextElement());
		}

		for (Iterator i = removed_children.iterator(); i.hasNext();) {
			child = (ViewTreeNode) i.next();
			child.removeFromParent();
			root.add(child);
		}
		node.removeFromParent();
		treeTable.getTree().collapsePath(new TreePath(new TreeNode[] { root }));
		treeTable.getTree().updateUI();
		treeTable.doLayout();

	}

	void addGraphView(GraphView net, GraphView par) {
		// first see if it exists
		if (getGraphViewNode(net) == null) {
			ViewTreeNode dmtn = new ViewTreeNode(net.getIdentifier(), net);
			//net.getFlagger().addFlagEventListener(this);
			if (par != null) {
				ViewTreeNode parent = getGraphViewNode(par);
				parent.add(dmtn);
			} else {
				root.add(dmtn);
			}

			treeTable.getTree().collapsePath(
					new TreePath(new TreeNode[] { root }));
			treeTable.getTree().updateUI();
			TreePath path = new TreePath(dmtn.getPath());
			treeTable.getTree().expandPath(path);
			treeTable.getTree().scrollPathToVisible(path);
			treeTable.doLayout();
		}
	}

	public void focusGraphViewNode(GraphView view) {
		DefaultMutableTreeNode node = getGraphViewNode(view);
		if (node != null) {
			treeTable.getTree().getSelectionModel().setSelectionPath(
					new TreePath(node.getPath()));
			treeTable.getTree().scrollPathToVisible(
					new TreePath(node.getPath()));
		}
	}

	ViewTreeNode getGraphViewNode(GraphView view) {

		Enumeration tree_node_enum = root.breadthFirstEnumeration();
		while (tree_node_enum.hasMoreElements()) {
			ViewTreeNode node = (ViewTreeNode) tree_node_enum
					.nextElement();
			if (node.getView() == view) {
				return node;
			}
		}
		return null;
	}

	public void valueChanged(TreeSelectionEvent e) 
	{
    }

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName() == HGVDesktop.GRAPH_VIEW_CREATED) {
			addGraphView((GraphView) e.getNewValue(), (GraphView) e.getOldValue());
		}

		if (e.getPropertyName() == HGVDesktop.GRAPH_VIEW_DESTROYED) {
			removeGraphView((GraphView) e.getNewValue());
		}

		else if (e.getPropertyName() == HGVDesktop.GRAPH_VIEW_FOCUSED) {
			focusGraphViewNode((GraphView) e.getNewValue());
		}

	}

	/**
	 * Inner class that extends the AbstractTreeTableModel
	 */
	class NetworkTreeTableModel extends AbstractTreeTableModel {

		String[] columns = { "Network", "Nodes", "Edges" };

		Class[] columns_class = { TreeTableModel.class, String.class,
				String.class };

		public NetworkTreeTableModel(Object root) {
			super(root);
		}

		public Object getChild(Object parent, int index) {
			Enumeration tree_node_enum = ((DefaultMutableTreeNode) getRoot())
					.breadthFirstEnumeration();
			while (tree_node_enum.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_node_enum
						.nextElement();
				if (node == parent) {
					return node.getChildAt(index);
				}
			}
			return null;
		}

		public int getChildCount(Object parent) {
			Enumeration tree_node_enum = ((DefaultMutableTreeNode) getRoot())
					.breadthFirstEnumeration();
			while (tree_node_enum.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_node_enum
						.nextElement();
				if (node == parent) {
					return node.getChildCount();
				}
			}
			return 0;
		}

		public int getColumnCount() {
			return columns.length;

		}

		public String getColumnName(int column) {
			return columns[column];
		}

		public Class getColumnClass(int column) {
			return columns_class[column];
		}

		public Object getValueAt(Object node, int column) {
			if (column == 0)
				return ((DefaultMutableTreeNode) node).getUserObject();
			GraphView net = (((ViewTreeNode) node).getView());
			
			String s = "";
			if (column == 1) {
				s += "(" + net.getNodeViewCount() + ")";
				s += "(" + net.getSelectedNodes().size() + ")";
			} else if (column == 2) {
				s += "(" + net.getEdgeViewCount() + ")";
				s += "(" + net.getSelectedEdges().size() + ")";
			}
			return s;

		}

	} // NetworkTreeTableModel

	protected class ViewTreeNode extends DefaultMutableTreeNode {

		protected GraphView view;

		public ViewTreeNode(Object userobj, GraphView id) {
			super(userobj);
			view = id;
		}

		protected void setView(GraphView id) {
			view = id;
		}

		protected GraphView getView() {
			return view;
		}
	}

	private class MyRenderer extends DefaultTreeCellRenderer {
		Icon tutorialIcon;

		public MyRenderer() {

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			if (hasView(value)) {
				// setIcon(tutorialIcon);
				setBackgroundNonSelectionColor(java.awt.Color.green.brighter());
				setBackgroundSelectionColor(java.awt.Color.green.darker());
			} else {
				setBackgroundNonSelectionColor(java.awt.Color.red.brighter());
				setBackgroundSelectionColor(java.awt.Color.red.darker());

			}

			return this;
		}

		private boolean hasView(Object value) {

			ViewTreeNode node = (ViewTreeNode) value;
			if(node.getView() == null) return false;
					
			setToolTipText(node.getView().getIdentifier());
			setText(node.getView().getIdentifier());
			return true;
		}

	}

	/**
	 * This class listens to mouse events from the TreeTable, if the mouse event
	 * is one that is canonically associated with a popup menu (ie, a right
	 * click) it will pop up the menu with option for destroying view, creating
	 * view, and destroying network (this is platform specific apparently)
	 */
	protected class PopupListener extends MouseAdapter {
		/**
		 * Don't know why you need both of these, but this is how they did it in
		 * the example
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * Don't know why you need both of these, but this is how they did it in
		 * the example
		 */
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * if the mouse press is of the correct type, this function will maybe
		 * display hte popup
		 */
		private void maybeShowPopup(MouseEvent e) {
			// check for the popup type
			if (e.isPopupTrigger()) {
				// get the row where the mouse-click originated
				int row = treeTable.rowAtPoint(e.getPoint());
				if (row != -1) {
					JTree tree = treeTable.getTree();
					TreePath treePath = tree.getPathForRow(row);
					GraphView net = ((ViewTreeNode) treePath
							.getLastPathComponent()).getView();

						
						// disable or enable specific options with respect to
						// the actual network
						// that is selected
						if (true)//HGVKit.viewExists(net))
						{
							// disable the view creation item
							createViewItem.setEnabled(false);
							destroyViewItem.setEnabled(true);
						} // end of if ()
						else {
							createViewItem.setEnabled(true);
							destroyViewItem.setEnabled(false);
						} // end of else
						// let the actionlistener know which network it should
						// be operating
						// on when (if) it is called
						popupActionListener.setActiveNetwork(net);
						// display the popup
						popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}

}

/**
 * This class listens for actions from the popup menu, it is responsible for
 * performing actions related to destroying and creating views, and destroying
 * the network.
 */
class PopupActionListener implements ActionListener {
	/**
	 * Constants for JMenuItem labels
	 */
	public static String DESTROY_VIEW = "Destroy View";

	public static String CREATE_VIEW = "Create View";

	/**
	 * This is GraphView which originated the mouse-click event
	 */
	protected GraphView graphView;

	/**
	 * Based on the action event, destroy or create a view
	 */
	public void actionPerformed(ActionEvent ae) {
		String label = ((JMenuItem) ae.getSource()).getText();
		// Figure out the appropriate action
		if (label == DESTROY_VIEW) 
			HGVKit.destroyNetworkView(graphView);
		else if (label == CREATE_VIEW)
		    HGVKit.createHGViewer(graphView);
		else {
			// throw an exception here?
			System.err.println("Unexpected network panel popup option");
		} // end of else
	}

	/**
	 * Right before the popup menu is displayed, this function is called so we
	 * know which network the user is clicking on to call for the popup menu
	 */
	public void setActiveNetwork(GraphView cyNetwork) {
		this.graphView = cyNetwork;
	}
}
