package org.hypergraphdb.viewer.props;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/***/
public class PropertyCellEditor extends DefaultCellEditor implements TableCellRenderer
{
	public PropertyCellEditor()
	{
		super(new JTextField());
		setClickCountToStart(1);
	}

	/***/
	public Component getTableCellEditorComponent(final JTable table,
			final Object value, final boolean isSelected, final int row,
			final int column)
	{
		return getCustom(table, value, row, column);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component c = getCustom(table, value, row, column);
		if (isSelected)
		{
			c.setForeground(table.getSelectionForeground());
			c.setBackground(table.getSelectionBackground());
		} else
		{
			c.setForeground(table.getForeground());
			c.setBackground(table.getBackground());
		}
		return c;
	}

	private JComponent getCustom(final JTable table, Object value,
			final int row, final int column)
	{
		final PropertiesTableModel model = (PropertiesTableModel) table
				.getModel();
		final PropertyPanelEx panel = new PropertyPanelEx(model
				.getBeanProperty(row, column), 0);
		panel.setBounds(table.getCellRect(row, column, false));
		return panel;
	}
}
