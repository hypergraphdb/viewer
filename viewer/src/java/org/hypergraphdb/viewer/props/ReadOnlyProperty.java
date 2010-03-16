package org.hypergraphdb.viewer.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;


public class ReadOnlyProperty implements AbstractProperty
{
	private String value;
	
	public ReadOnlyProperty(String value)
	{
	   this.value = value;	
	}
	public Object getValue() throws InvocationTargetException
	{
		return value;
	}
	
	public void setValue(Object v) throws InvocationTargetException
	{
		// DO NOTHING;
	}

	public String getName()
	{
		return value;
	}

	public Class<?> getPropertyEditorClass()
	{
		return null;
	}

	public Class<?> getPropertyType()
	{
		return String.class;
	}

	public String getShortDescription()
	{
		return value;
	}

	public boolean canRead()
	{
		return true;
	}

	public boolean canWrite()
	{
		return false;
	}
	
	public int compareTo(Object o)
    {
        if(o instanceof AbstractProperty)
        {
            AbstractProperty p = (AbstractProperty) o;
            if(p.getName() != null)
                return p.getName().compareTo(getName());
            else
            {
                return (getName() != null) ? 1 : 0;
            }
        }
        return 0;
    }
	
	static final PropertyEditor editor = new StringEditorEx();
    public PropertyEditor getPropertyEditor()
    {
        return editor;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        // DO NOTHING;
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        // // DO NOTHING;
    }
}
