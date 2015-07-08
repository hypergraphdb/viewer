package org.hypergraphdb.viewer.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.util.GUIUtilities;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;

/**
 * Panel for configuring app parameters.
 */
public class AppConfigPanel extends JPanel
{
	AppPropsPanel propPanel;

	/**
	 * Create the panel
	 */
	public AppConfigPanel()
	{
		init();
	}

	private void init()
	{
		setLayout(new GridBagLayout());
		final JButton addButton = new JButton();
		addButton.setText("Add");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridx = 4;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.weightx = 1;
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addProperty();
			}
		});
		add(addButton, gridBagConstraints_1);
		final JButton removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridx = 4;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.weightx = 1;
		add(removeButton, gridBagConstraints_2);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				removeProperty();
			}
		});
		propPanel = new AppPropsPanel();
		propPanel.setMinimumSize(new Dimension(300, 300));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.gridheight = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weighty = 2;
		gridBagConstraints.weightx = 2;
		add(propPanel, gridBagConstraints);
	}

	private void addProperty()
	{
		NameValuePanel panel = new NameValuePanel("Property Name: ",
				"Property Value: ");
		DialogDescriptor d = new DialogDescriptor(GUIUtilities.getFrame(), panel,
				"Add New Config Property");
		d.setModal(true);
		d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
		{
			AppConfig.getInstance().setProperty(
					panel.getName(), panel.getValue());
			propPanel.init();
		}
	}

	private void removeProperty()
	{
		int row = propPanel.getTable().getSelectedRow();
		if(row == -1) return;
		PropertySheetTableModel.Item item = (PropertySheetTableModel.Item)
			propPanel.getTable().getValueAt(row, 0);
		AppConfig.getInstance().removeProperty((String)
				item.getKey());
		//System.out.println("Key:" + item.getKey() + ":" + item.getName());
		propPanel.init();
	}

	class AppPropsPanel extends PropertySheetPanel
	{
		public AppPropsPanel()
		{
			init();
		}

		void init()
		{
			PropertySheetTable table = new PropertySheetTable();
			ArrayList<DefaultProperty> data = new ArrayList<DefaultProperty>();
			Map<String, Object> map = AppConfig.getInstance().getProperties();
			for (String info : map.keySet())
			{
				MyProperty prop = new MyProperty(info, map.get(info));
				data.add(prop);
			}
			table.setModel(new MyTableModel(data));
			setTable(table);
			setDescriptionVisible(true);
		}
	}

	class MyTableModel extends PropertySheetTableModel
	{
		private ArrayList<DefaultProperty> data;

		public MyTableModel(final ArrayList<DefaultProperty> data)
		{
			this.data = data;
			setProperties(data.toArray(new DefaultProperty[data.size()]));
		}

		public int getColumnCount()
		{
			return 2;
		}

		public int getRowCount()
		{
			return data.size();
		}
	}

	private static class MyProperty extends DefaultProperty
	{
		private String key;
		private Object value;

		public MyProperty(String key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		@Override
		public String getShortDescription()
		{
			return "";
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		@Override
		public String getName()
		{
			return key;
		}

		public String getDisplayName()
		{
			return getName();
		}

		@Override
		public Class<?> getType()
		{
			return (key != null) ? key.getClass() : Object.class;
		}

		@Override
		public void setValue(Object val)
		{
			if (val == null) return;
			value = val;
			AppConfig.getInstance().setProperty(key, val);
		}
	}
}
