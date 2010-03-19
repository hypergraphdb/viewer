package org.hypergraphdb.viewer.props;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author User
 */
public class PropertiesTableModel extends AbstractTableModel implements PropertyChangeListener
{
    public static String[] COLUMNNAMES = { "Property", "Value" };
    
	/** Property change support. */
	private PropertyChangeSupport sup = new PropertyChangeSupport(this);
	
	protected AbstractProperty[][] data;
	/** The component which properties are currently edited. */
	protected Object bean;
	
	protected boolean attachListeners = false;

	public boolean canModelClass(Class<?> c)
	{
	   return false;   
	} 
	
    public AbstractProperty[][] getData()
    {
        this.attachListeners = true;
        final AbstractProperty[] pd = introspect(bean);
        AbstractProperty[][] data0 = new AbstractProperty[2][pd.length];
        for (int i = 0; i < pd.length; i++)
        {
            data0[0][i] = new ReadOnlyProperty(pd[i].getName());
            data0[1][i] = pd[i];
        }
        return data0;
    }

    public String getColumnName(int col)
    {
        return COLUMNNAMES[col];
    }
	
	protected AbstractProperty[] introspect(Object obj)
	{
	    Set<Field> slots = RefUtils.getFieldsForType(obj.getClass());
	    AbstractProperty[] props = new AbstractProperty[slots.size()];
	    int i = 0;
	    for(Field f : slots)
	    {
	        Object value = getFieldV(f, obj);
	        props[i] = new GenericProperty(f.getName(), value);
	        i++;
	    } 
	    
	    Arrays.sort(props);
	    AbstractProperty[] reverse = new AbstractProperty[props.length];
	    for(int j = 0; j< props.length; j++)
	        reverse[j] = props[props.length - 1 - j];
		return reverse;
	}
	
	private static Object getFieldV(Field f, Object obj)
	{
	    try
        {
           f.setAccessible(true);
           return f.get(obj);
        }
        catch (Exception e)
        {
            System.err.println("Unable to retrieve field: " + f.getName() + " for "
                    + obj + "in " + obj.getClass() + " Reason: " + e);
            //e.printStackTrace();
            return null; 
        }
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
		sup.firePropertyChange(AbstractProperty.PROP_VALUE, null, null);
	}

	@Override
	public void fireTableCellUpdated(int row, int column)
	{
		super.fireTableCellUpdated(row, column);
		sup.firePropertyChange(AbstractProperty.PROP_VALUE, null, null);
	}

	@Override
	public void fireTableDataChanged()
	{
		super.fireTableDataChanged();
		sup.firePropertyChange(AbstractProperty.PROP_VALUE, null, null);
	}
}
