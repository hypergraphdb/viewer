package org.hypergraphdb.viewer.props;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;

public class AbstractPropertySupport implements AbstractProperty
{
	/** Property change support. */
	private PropertyChangeSupport sup = new PropertyChangeSupport(this);

	//protected PropertyEditor editor = null;
	
	public Object getValue() throws InvocationTargetException
	{
		return null;
	}

	public void setValue(Object v) throws InvocationTargetException
	{
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
                return (getName() != null) ? getName().compareTo(p.getName()) : 0;
            }
        }
        return 0;
    }


    SoftReference edRef = null;
	
	public PropertyEditor getPropertyEditor() 
	{
	    if(getPropertyType() == null) return null;
		PropertyEditor result = null;
		if (edRef != null) {
			result = (PropertyEditor) edRef.get();
		}
		if (result == null) {
			result = PropertyEditorManager.findEditor(getPropertyType());
			if (result == null)
			{
				result = new ComplexValuePropertyEditor();
				((ComplexValuePropertyEditor)result).getSwingPropertyChangeSupport().addPropertyChangeListener(
						new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent ev)
							{
								fireValueChanged();
							}
						});
			}
			edRef = new SoftReference(result);
		}
		return result;
	}

	public String getName()
	{
		return null;
	}

	public Class<?> getPropertyEditorClass()
	{
		return null;
	}

	public Class<?> getPropertyType()
	{
		Object obj = null;
		try{
			obj = getValue();
		}catch(InvocationTargetException ex)
		{
			ex.printStackTrace();
		}
		if(obj != null)
			return obj.getClass();
		return null;
	}

	

	public String getShortDescription()
	{
		return null;
	}

	
	public boolean canRead()
	{
		return true;
	}

	public boolean canWrite()
	{
		return false;
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
		sup.firePropertyChange(AbstractProperty.PROP_VALUE, null, null);
	}
}
