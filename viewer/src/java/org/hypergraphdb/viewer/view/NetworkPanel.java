package org.hypergraphdb.viewer.view;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNetworkView;

import org.hypergraphdb.viewer.actions.CreateNetworkViewAction;
import org.hypergraphdb.viewer.data.*;

import org.hypergraphdb.viewer.util.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.beans.*;

public class NetworkPanel extends JPanel implements PropertyChangeListener,
		TreeSelectionListener, FlagEventListener {

	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);

	JTreeTable treeTable;
	NetworkTreeNode root;
	JPanel navigatorPanel;
	JPopupMenu popup;
	PopupActionListener popupActionListener;
	JMenuItem createViewItem;
	JMenuItem destroyViewItem;
	JMenuItem destroyNetworkItem;
	JSplitPane split;

	public NetworkPanel() {
		super();
		initialize();
	}

	protected void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(181, 400));
		root = new NetworkTreeNode("root", null); //"Network Root", "root");
		treeTable = new JTreeTable(new NetworkTreeTableModel(root));
		treeTable.getTree().addTreeSelectionListener(this);
		treeTable.getTree().setRootVisible(false);

		// ToolTipManager.sharedInstance().registerComponent(treeTable.getTree());
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
		destroyNetworkItem = new JMenuItem(PopupActionListener.DESTROY_NETWORK);

		// action listener which performs the tasks associated with the popup
		// listener
		popupActionListener = new PopupActionListener();
		createViewItem.addActionListener(popupActionListener);
		destroyViewItem.addActionListener(popupActionListener);
		destroyNetworkItem.addActionListener(popupActionListener);
		popup.add(createViewItem);
		popup.add(destroyViewItem);
		popup.add(destroyNetworkItem);
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

	public void removeNetwork(HGVNetwork view) {

		NetworkTreeNode node = getNetworkNode(view);
		Enumeration children = node.children();
		NetworkTreeNode child = null;
		ArrayList removed_children = new ArrayList();
		while (children.hasMoreElements()) {
			removed_children.add(children.nextElement());
		}

		for (Iterator i = removed_children.iterator(); i.hasNext();) {
			child = (NetworkTreeNode) i.next();
			child.removeFromParent();
			root.add(child);
		}
		view.getFlagger().removeFlagEventListener(this);
		node.removeFromParent();
		treeTable.getTree().collapsePath(new TreePath(new TreeNode[] { root }));
		treeTable.getTree().updateUI();
		treeTable.doLayout();

	}

	public void onFlagEvent(FlagEvent event) {
		// System.out.println("NetworkPanel - onFlagEvent: " + event);
		treeTable.getTree().updateUI();
		if (event.getEventType())
			HGVKit.getDesktop().updatePropsPanel();
	}

	public void addNetwork(HGVNetwork net, HGVNetwork par) {
		// first see if it exists
		if (getNetworkNode(net) == null) {
			NetworkTreeNode dmtn = new NetworkTreeNode(net.getTitle(), net);
			net.getFlagger().addFlagEventListener(this);
			if (par != null) {
				NetworkTreeNode parent = getNetworkNode(par);
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

	public void focusNetworkNode(HGVNetworkView view) {
		DefaultMutableTreeNode node = getNetworkNode(view.getNetwork());
		if (node != null) {
			treeTable.getTree().getSelectionModel().setSelectionPath(
					new TreePath(node.getPath()));
			treeTable.getTree().scrollPathToVisible(
					new TreePath(node.getPath()));
		}
	}

	public NetworkTreeNode getNetworkNode(HGVNetwork view) {

		Enumeration tree_node_enum = root.breadthFirstEnumeration();
		while (tree_node_enum.hasMoreElements()) {
			NetworkTreeNode node = (NetworkTreeNode) tree_node_enum
					.nextElement();
			if (node.getNetworkID() == view) {
				return node;
			}
		}
		return null;
	}

	public void fireFocus(HGVNetworkView view) {
		pcs.firePropertyChange(new PropertyChangeEvent(this,
				HGVDesktop.NETWORK_VIEW_FOCUS, null, view));
	}

	public void valueChanged(TreeSelectionEvent e) {
		NetworkTreeNode node = (NetworkTreeNode) treeTable.getTree()
				.getLastSelectedPathComponent();

		if (node == null)
			return;
		if (node.getUserObject() == null)
			return;
		fireFocus(HGVKit.getNetworkView(node.getNetworkID()));
   }

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName() == HGVKit.NETWORK_CREATED) {
			addNetwork((HGVNetwork) e.getNewValue(), (HGVNetwork) e.getOldValue());
		}

		if (e.getPropertyName() == HGVKit.NETWORK_DESTROYED) {
			removeNetwork((HGVNetwork) e.getNewValue());
		}

		else if (e.getPropertyName() == HGVDesktop.NETWORK_VIEW_FOCUSED) {
			focusNetworkNode((HGVNetworkView) e.getNewValue());
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
			else if (column == 1) {
				// return new Integer( HGVKit.getNetwork( ( ( NetworkTreeNode
				// )node).getNetworkID() ).getNodeCount() );
				HGVNetwork net = (((NetworkTreeNode) node).getNetworkID());
				if(net == null) return "";
				return "" + net.getNodeCount() + "("
						+ net.getFlagger().getFlaggedNodes().size() + ")";
			} else if (column == 2) {
				HGVNetwork net = (((NetworkTreeNode) node).getNetworkID());
				if(net == null) return "";
				return "" + net.getEdgeCount() + "("
						+ net.getFlagger().getFlaggedEdges().size() + ")";
			}
			return "";

		}

	} // NetworkTreeTableModel

	protected class NetworkTreeNode extends DefaultMutableTreeNode {

		protected HGVNetwork view;

		public NetworkTreeNode(Object userobj, HGVNetwork id) {
			super(userobj);
			view = id;
		}

		protected void setNetworkID(HGVNetwork id) {
			view = id;
		}

		protected HGVNetwork getNetworkID() {
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

			NetworkTreeNode node = (NetworkTreeNode) value;
			if(node.getNetworkID() == null) return false;
					
			setToolTipText(node.getNetworkID().getTitle());
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
					HGVNetwork net = ((NetworkTreeNode) treePath
							.getLastPathComponent()).getNetworkID();

						
						// disable or enable specific options with respect to
						// the actual network
						// that is selected
						if (HGVKit.viewExists(net))
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

	public static String DESTROY_NETWORK = "Destroy Network";

	/**
	 * This is the network which originated the mouse-click event (more
	 * appropriately, the network associated with the ID associated with the row
	 * associated with the JTable that originated the popup event
	 */
	protected HGVNetwork cyNetwork;

	/**
	 * Based on the action event, destroy or create a view, or destroy a network
	 */
	public void actionPerformed(ActionEvent ae) {
		String label = ((JMenuItem) ae.getSource()).getText();
		// Figure out the appropriate action
		if (label == DESTROY_VIEW) {
			HGVKit.destroyNetworkView(cyNetwork);
		} // end of if ()
		else if (label == CREATE_VIEW) {
			CreateNetworkViewAction.createViewFromCurrentNetwork(cyNetwork);
		} // end of if ()
		else if (label == DESTROY_NETWORK) {
			HGVKit.destroyNetwork(cyNetwork);
		} // end of if ()
		else {
			// throw an exception here?
			System.err.println("Unexpected network panel popup option");
		} // end of else
	}

	/**
	 * Right before the popup menu is displayed, this function is called so we
	 * know which network the user is clicking on to call for the popup menu
	 */
	public void setActiveNetwork(HGVNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}
}
