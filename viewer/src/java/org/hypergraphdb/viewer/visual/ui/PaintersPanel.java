package org.hypergraphdb.viewer.visual.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.handle.UUIDPersistentHandle;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.JavaBeanBinding;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NameValuePanel;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.painter.DefaultNodePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.visual.VisualStyle;

public class PaintersPanel extends JPanel
{
	private JList paintersList;
	private JComboBox stylesCombo;
	private PainterPropsPanel propsPanel;
	private HyperGraph hg;
	private HGVNetworkView view;

	/**
	 * Create the propsPanel
	 */
	public PaintersPanel()
	{
		super();
		setPreferredSize(new Dimension(700, 300));
		setMinimumSize(getPreferredSize());
		init();
		final JButton addPainterClassButton = new JButton();
		addPainterClassButton.setText("Add Painter Class");
		addPainterClassButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addPainterClass();
			}
		});
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridx = 3;
		add(addPainterClassButton, gridBagConstraints);
		final JButton removePainterButton = new JButton();
		removePainterButton.setText("Remove Painter");
		removePainterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				removePainter();
			}
		});
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_1.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_1.weighty = 2.0;
		gridBagConstraints_1.gridy = 5;
		gridBagConstraints_1.gridx = 3;
		add(removePainterButton, gridBagConstraints_1);
		stylesCombo.setSelectedIndex(0);
		paintersList.setSelectedIndex(0);
	}

	private void init()
	{
		setLayout(new GridBagLayout());
		final JLabel stylesLabel = new JLabel();
		stylesLabel.setText("Styles:");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipady = 15;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.ipadx = 120;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(0, 0, 0, -110);
		add(stylesLabel, gridBagConstraints);
		stylesCombo = new JComboBox(new VisualStylesComboModel());
		stylesCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				populatePaintersList((VisualStyle) stylesCombo
						.getSelectedItem());
			}
		});
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
		add(stylesCombo, gridBagConstraints_1);
		propsPanel = new PainterPropsPanel();
		propsPanel.setBorder(new TitledBorder(null, "Properties",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		propsPanel.setMinimumSize(new Dimension(200,200));
		propsPanel.setPreferredSize(new Dimension(200,200));
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weightx = 2.0;
		gridBagConstraints_4.weighty = 2.0;
		gridBagConstraints_4.gridheight = 6;
		gridBagConstraints_4.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 2;
		add(propsPanel, gridBagConstraints_4);
		final JButton addStyleButton = new JButton();
		addStyleButton.setMargin(new Insets(2, 14, 2, 14));
		addStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addStyle(e);
			}
		});
		addStyleButton.setText("Add Style");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 3;
		add(addStyleButton, gridBagConstraints_5);
		final JLabel paintersLabel = new JLabel();
		paintersLabel.setText("Painters:");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.insets = new Insets(0, 0, 0, 5);
		add(paintersLabel, gridBagConstraints_2);
		paintersList = new JList();
		paintersList.setCellRenderer(new CustomCellRenderer());
		paintersList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e)
			{
				propsPanel.setPainter(((VisualStyle) stylesCombo
						.getSelectedItem()).getNodePaintersMap().get(
						paintersList.getSelectedValue()));
			}
		});
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridheight = 5;
		gridBagConstraints_3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 1;
		add(paintersList, gridBagConstraints_3);
		final JButton removeStyleButton = new JButton();
		removeStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				removeStyle();
			}
		});
		removeStyleButton.setText("Remove Style");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_6.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_6.gridy = 1;
		gridBagConstraints_6.gridx = 3;
		add(removeStyleButton, gridBagConstraints_6);
		final JButton addPainterButton = new JButton();
		addPainterButton.setText("Add Painter");
		addPainterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addPainter();
			}
		});
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 3;
		add(addPainterButton, gridBagConstraints_7);
		paintersList.setModel(new TypeHandleListModel());
	}

	void populatePaintersList(VisualStyle vs)
	{
		Collection<HGHandle> res = new HashSet<HGHandle>();
		Map<HGHandle, NodePainter> map = vs.getNodePaintersMap();
		for (Map.Entry<HGHandle, NodePainter> e : map.entrySet())
		{
			// System.out.println("" + e.getValue() + ":" +
			// (e.getValue() instanceof DefaultNodePainter));
			if (e.getValue() instanceof DefaultNodePainter)
				res.add(e.getKey());
		}
		paintersList.setModel(new TypeHandleListModel(res));
		if (paintersList.getModel().getSize() > 0)
			paintersList.setSelectedIndex(0);
	}

	private void addStyle(ActionEvent e)
	{
		NotifyDescriptor d = new NotifyDescriptor.InputLine(null, "",
				"Specify style name", NotifyDescriptor.PLAIN_MESSAGE,
				NotifyDescriptor.OK_CANCEL_OPTION);
		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
		{
			String style = ((NotifyDescriptor.InputLine) d).getInputText();
			if (style == null || style.equals("")) return;
			if (VisualManager.getInstance().getVisualStyle(style) != null)
			{
				d = new NotifyDescriptor.Message(null, "Style: " + style
						+ " already defined");
				DialogDisplayer.getDefault().notify(d);
				addStyle(e);
				return;
			}
			VisualStyle vs = new VisualStyle(style);
			VisualManager.getInstance().addVisualStyle(vs);
			stylesCombo.setModel(new VisualStylesComboModel());
			stylesCombo.setSelectedItem(vs);
		}
	}

	private void removeStyle()
	{
		VisualStyle vs = (VisualStyle) stylesCombo.getSelectedItem();
		if (vs == null) return;
		if (VisualManager.DEFAULT_STYLE_NAME.equals(vs.getName()))
		{
			NotifyDescriptor d = new NotifyDescriptor.Message(null,
					"Can't delete default style");
			DialogDisplayer.getDefault().notify(d);
			return;
		} else
		{
			NotifyDescriptor d = new NotifyDescriptor.Confirmation(null,
					"Are you sure you want to completely remove style: "
							+ vs.getName());
			if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
			{
				VisualManager.getInstance().removeVisualStyle(vs.getName());
				stylesCombo.setModel(new VisualStylesComboModel());
				stylesCombo.setSelectedIndex(0);
			}
		}
	}

	private void addPainterClass()
	{
		NameValuePanel panel = new NameValuePanel("Painter Type: ",
				"Painter Class: ");
		DialogDescriptor d = new DialogDescriptor(GUIUtilities.getFrame(), panel,
				"Add atom to hypergraph");
		d.setModal(true);
		d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
		{
			addNodePainter(hg, (VisualStyle) stylesCombo.getSelectedItem(),
					panel.getName(), panel.getValue());
		}
	}

	private void addPainter()
	{
		NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine(
				GUIUtilities.getFrame(), "ClassName/TypeHandle:",
				"Specify painter's type class or handle", NotifyDescriptor.PLAIN_MESSAGE,
				NotifyDescriptor.OK_CANCEL_OPTION);
		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
		{
			HGHandle h = null;
			try{
				h = UUIDPersistentHandle.makeHandle(d.getInputText());
			}catch(NumberFormatException ex){}
			try
			{
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				Class clazz = cl.loadClass(d.getInputText());
				h = hg.getPersistentHandle(hg
						.getTypeSystem().getTypeHandle(clazz));
			}
			catch (Exception ex)
			{
				NotifyDescriptor dex = new NotifyDescriptor.Exception(
						GUIUtilities.getFrame(), ex, "Unable to create painter");
				DialogDisplayer.getDefault().notify(dex);
				ex.printStackTrace();
			}
			if(h == null) return;
			NodePainter p = new DefaultNodePainter();
			VisualStyle vs = ((VisualStyle) stylesCombo.getSelectedItem());
			vs.addNodePainter(h, p);
			populatePaintersList(vs);
		}
	}

	private void removePainter()
	{
		HGHandle h = (HGHandle) paintersList
				.getSelectedValue();
		if (h == null) return;
		NotifyDescriptor d = new NotifyDescriptor.Confirmation(null,
				"Are you sure you want to completely remove painter: " + h);
		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
		{
			VisualStyle vs = (VisualStyle) stylesCombo.getSelectedItem();
			vs.removeNodePainter(h);
			populatePaintersList(vs);
		}
	}

	private void addNodePainter(HyperGraph hg, VisualStyle vs, String t_cls,
			String p_cls)
	{
		try
		{
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Class clazz = cl.loadClass(t_cls);
			HGHandle h = hg.getTypeSystem().getTypeHandle(clazz);
			NodePainter p = (NodePainter) cl.loadClass(p_cls).newInstance();
			vs.addNodePainter(h, p);
		}
		catch (Exception ex)
		{
			NotifyDescriptor dex = new NotifyDescriptor.Exception(null, ex,
					"Unable to create painter");
			DialogDisplayer.getDefault().notify(dex);
		}
	}

	private static class TypeHandleListModel extends DefaultListModel
	{
		public TypeHandleListModel()
		{
		}

		public TypeHandleListModel(Collection c)
		{
			for (Object o : c)
				this.addElement(o);
		}

		public Object getElementAt(int index)
		{
			return super.getElementAt(index);
		}

		public int getSize()
		{
			return super.getSize();
		}
	}

	public void setHyperGraph(HyperGraph hg)
	{
		this.hg = hg;
	}
	
	public void setView(HGVNetworkView v){
		view = v;
		hg = v.getNetwork().getHyperGraph();
		stylesCombo.setSelectedItem(view.getVisualStyle());
	}

	class CustomCellRenderer extends DefaultListCellRenderer
	{
		public CustomCellRenderer()
		{
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
		    setComponentOrientation(list.getComponentOrientation());
			if (isSelected) {
			    setBackground(list.getSelectionBackground());
			    setForeground(list.getSelectionForeground());
			}
			else {
			    setBackground(list.getBackground());
			    setForeground(list.getForeground());
			}

			setIcon(null);
			if(hg != null){
				HGAtomType cls = hg.getTypeSystem().getType((HGHandle)value);
				if(cls != null){
					if(cls instanceof JavaBeanBinding)
						setText(cls.toString());
					else
					   setText(cls.getClass().getName());
				}
				
			}else
			  setText((value == null) ? "" : value.toString());
			
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return this;
		}
	}
}
