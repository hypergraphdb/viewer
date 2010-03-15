package org.hypergraphdb.viewer.props;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author User
 */
public abstract class PropertiesTableModel extends AbstractTableModel implements PropertyChangeListener
{
	
	/** Property change support. */
	private PropertyChangeSupport sup = new PropertyChangeSupport(this);
	
	protected AbstractProperty[][] data;
	/** The component which properties are currently edited. */
	protected Object bean;
	
	protected boolean attachListeners = false;

	public abstract AbstractProperty[][] getData();
	
	protected AbstractProperty[] introspect(Object obj)
	{
		try
		{
			final Class superC = 
				(obj.getClass().getSuperclass().getSuperclass() == null) ?
				 obj.getClass().getSuperclass() : obj.getClass().getSuperclass().getSuperclass();	
			final BeanInfo bi = 
				Introspector.getBeanInfo(obj.getClass(), superC);
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			AbstractProperty[] bps = new AbstractProperty[pds.length];
			for (int i = 0; i < pds.length; i++)
				bps[i] = new BeanProperty(obj, pds[i]);
			return bps;
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return new AbstractProperty[0];
	}

	/***/
	public void create(final Object dcSel)
	{
		bean = dcSel;
		data = getData();
		if (dcSel == null)
			data = new AbstractProperty[0][0];
		if(attachListeners)
			for(int i = 0; i < data.length; i++)
				for(int j = 0; j < data[i].length; j++)
					if(data[i][j].canWrite())
					   data[i][j].addPropertyChangeListener(this);
				
		fireTableDataChanged();
	}
	
	public int getRowCount()
	{
		return (data.length == 0) ? 0 : data[0].length; 
	}

	public Object getValueAt(int row, int col)
	{
		try
		{
			//System.out.println("getValueAt: " + data[col][row].getValue() + ":"
			//		+ data[col][row].getPropertyEditor());
			return data[col][row].getValue();
		}
		catch (InvocationTargetException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		
	}

	public boolean isCellEditable(int row, int col)
	{
		return data[col][row].canWrite();
	}

	public int getColumnCount()
	{
		return data.length; 
	}

	public String getColumnName(int col)
	{
		return "";
	}

	public PropertyEditor getPropertyEditor(final int row, int col)
	{
		AbstractProperty prop = getBeanProperty(row, col);
		PropertyEditor ed = prop.getPropertyEditor();
		if (ed == null) 
		try
		{
			ed = new ComplexValuePropertyEditor(prop.getValue());
			((ComplexValuePropertyEditor)ed).getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		}
		catch (InvocationTargetException ex)
		{
			ex.printStackTrace();
		}
		return ed;
	}

	public AbstractProperty getBeanProperty(final int row, final int col)
	{
		return data[col][row];
	}

	public void propertyChange(PropertyChangeEvent ev)
	{
		//System.out.println("PropertiesTableModel - fireValueChanged: " + ev);
		fireValueChanged();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		sup.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		sup.removePropertyChangeListener(l);
	}
	
	void fireValueChanged()
	{
		fireTableDataChanged();
		sup.firePropertyChange(BeanProperty.PROP_VALUE, null, null);
	}

	@Override
	public void fireTableCellUpdated(int row, int column)
	{
		super.fireTableCellUpdated(row, column);
		sup.firePropertyChange(BeanProperty.PROP_VALUE, null, null);
	}

	@Override
	public void fireTableDataChanged()
	{
		super.fireTableDataChanged();
		sup.firePropertyChange(BeanProperty.PROP_VALUE, null, null);
	}
}
