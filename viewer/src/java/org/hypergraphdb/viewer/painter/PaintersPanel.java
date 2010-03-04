package org.hypergraphdb.viewer.painter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.visual.VisualStyle;
import org.hypergraphdb.viewer.visual.ui.VisualStylesComboModel;

public class PaintersPanel extends JPanel
{
	private JList paintersList;
	private JComboBox stylesCombo;
	private PainterPropsPanel propsPanel;
	/**
	 * Create the propsPanel
	 */
	public PaintersPanel()
	{
		super();
		init();
    }
	
	private void init(){
		setLayout(new GridBagLayout());

		final JLabel stylesLabel = new JLabel();
		stylesLabel.setText("Styles:");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipady = 15;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.ipadx = 120;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(0, 0, 0, -110);
		add(stylesLabel, gridBagConstraints);

		stylesCombo = new JComboBox(new VisualStylesComboModel());
		stylesCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				populatePaintersList((VisualStyle) stylesCombo.getSelectedItem());
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
		propsPanel.setBorder(new TitledBorder(null, "Properties", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weightx = 2.0;
		gridBagConstraints_4.weighty = 1.0;
		gridBagConstraints_4.gridheight = 5;
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
				NotifyDescriptor d = new NotifyDescriptor.InputLine(null, "",
	    				"Specify style name", NotifyDescriptor.PLAIN_MESSAGE,
	    				NotifyDescriptor.OK_CANCEL_OPTION);
	    		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
	    		{
	    			String style = ((NotifyDescriptor.InputLine) d).getInputText();
	    			if (style == null || style.equals("")) return;
	    			if(VisualManager.getInstance().getVisualStyle(style) != null){
	    				d = new NotifyDescriptor.Message(null, "Style: " + style +
	    						" already defined");
	    				DialogDisplayer.getDefault().notify(d);
	    				actionPerformed(e);
	    				return;
	    			}
	    			VisualStyle vs = new VisualStyle(style);
	    			VisualManager.getInstance().addVisualStyle(vs);
	    			((DefaultListModel) stylesCombo.getModel()).addElement(vs);
	    			stylesCombo.setSelectedItem(vs);
	        	}
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
		gridBagConstraints_2.insets = new Insets(0, 0, 0, -70);
		add(paintersLabel, gridBagConstraints_2);

		paintersList = new JList();
		paintersList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e)
			{
				propsPanel.setPainter(((VisualStyle) stylesCombo.getSelectedItem()).getNodePaintersMap().get(
						paintersList.getSelectedValue()));
			}
		});
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridheight = 3;
		gridBagConstraints_3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints_3.ipadx = 60;
		gridBagConstraints_3.ipady = 90;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 1;
		add(paintersList, gridBagConstraints_3);

		final JButton removeStyleButton = new JButton();
		removeStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
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
		final GridBagConstraints 
		  gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 3;
		add(addPainterButton, gridBagConstraints_7);

		final JButton removePainterButton = new JButton();
		removePainterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
			}
		});
		removePainterButton.setText("Remove Painter");
		final GridBagConstraints
		gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.weighty = 1.0;
		gridBagConstraints8.anchor = GridBagConstraints.NORTH;
		gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints8.gridy = 3;
		gridBagConstraints8.gridx = 3;
		add(removePainterButton, gridBagConstraints8);
		paintersList.setModel(new TypeHandleListModel());

        addComponentListener(new ComponentAdapter(){

            @Override
            public void componentHidden(ComponentEvent e)
            {
                VisualManager.getInstance().save();
            }
          });
	}
	
	void populatePaintersList(VisualStyle vs){
		paintersList.setModel(new TypeHandleListModel(
				vs.getNodePaintersMap().keySet()));
	}
	
	 private void addNodePainter(HyperGraph hg, VisualStyle vs,
     		ClassLoader cl, String t_cls, String p_cls)
	 {
     	try{
     		Class<?> clazz = cl.loadClass(t_cls);
     		HGHandle h = hg.getPersistentHandle(
     				hg.getTypeSystem().getTypeHandle(clazz));
     		 NodePainter p = (NodePainter) cl.loadClass(p_cls).newInstance();
     	     vs.addNodePainter(h, p);
     	     
     	}catch(Exception ex){
     		ex.printStackTrace();
     	}
     }
	
	private static class TypeHandleListModel extends DefaultListModel
	{

		public TypeHandleListModel()
		{
		}
		
		public TypeHandleListModel(Collection c)
		{
			for(Object o: c)
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
}
