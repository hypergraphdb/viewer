package org.hypergraphdb.viewer.props;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;

/**
 * Description of a Bean property and operations on it.
 */
public  class BeanProperty extends PropertyDescriptorEx implements AbstractProperty
{
	/** Name of the 'value' property. */
    public static final String PROP_VALUE = "value";
    	
	/** Property change support. */
	private PropertyChangeSupport sup = new PropertyChangeSupport(this);

	private boolean initialized = false;
	private Object  cached_value = null;
	
	/**
	 * ConstructorLink.
	 * 
	 * @param valueType  type of the property
	 */
	public BeanProperty(Object bean, PropertyDescriptor pd) 
	{
		super(bean, pd);
	}

	/**
	 * Get the value.
	 * 
	 * @return the value of the property
	 * @exception IllegalAccessException
	 *                cannot access the called method
	 * @exception InvocationTargetException
	 *                an exception during invocation
	 */
	public Object getValue() throws InvocationTargetException
	{
		if(initialized)
			return cached_value;
		try
		{
			Method readMethod = this.getReadMethod();
			if(readMethod == null) 
	            return null;
			cached_value = readMethod.invoke(bean, new Object[] {});
			initialized = true;
	        return cached_value;
	    }
		catch (IllegalAccessException iae)
		{
			throw annotateException(iae);
		}
		catch (InvocationTargetException ite)
		{
			throw annotateException(ite);
		}
	}

	/**
	 * Set the value.
	 * 
	 * @param val
	 *            the new value of the property
	 * @exception IllegalAccessException
	 *                cannot access the called method
	 * @exception IllegalArgumentException
	 *                wrong argument
	 * @exception InvocationTargetException
	 *                an exception during invocation
	 */
	 public void setValue(Object v) throws InvocationTargetException
	 {
		System.out.println("BeanProperty:" + bean + " setValue: " + v + "name: " + this.getWriteMethod().getName()); 
		if(v != null && v.equals(cached_value)|| (v== null && cached_value == null))
			return;
		
		try
		{
			Method writeMethod = this.getWriteMethod();
			if (writeMethod != null) {
                writeMethod.invoke(bean, new Object[] {v});
            }
			Object old_cached_value = cached_value;
			cached_value = v;
			sup.firePropertyChange(PROP_VALUE, old_cached_value, v);
		}
		catch (IllegalAccessException iae)
		{
			throw annotateException(iae);
		}
		catch (IllegalArgumentException iaae)
		{
			throw annotateException(iaae);
		}
		catch (InvocationTargetException ite)
		{
			throw annotateException(ite);
		}
	}

	/**
	 * Annotates specified exception. Helper method.
	 * 
	 * @param exception
	 *            original exception to annotate
	 * @return <code>IvocationTargetException</code> which annotates the
	 *         original exception
	 */
	private InvocationTargetException annotateException(Exception exception)
	{
		if (exception instanceof InvocationTargetException)
		{
			return (InvocationTargetException) exception;
		} else
		{
			return new InvocationTargetException(exception);
		}
	}
	

	// Soft caching of property editor references to improve JTable
	// property sheet performance
	java.lang.ref.SoftReference edRef = null;

	/**
	 * Get a property editor for this property. The default implementation tries
	 * to use {@link java.beans.PropertyEditorManager}.
	 * 
	 * @return the property editor, or <CODE>null</CODE> if there is no editor
	 */
	public PropertyEditor getPropertyEditor() {
		//System.out.println("BeanProperty - getPropertyEditor(): " + getPropertyType()); 
		if (editor != null)
			return editor;
		
		PropertyEditor result = null;
		if (edRef != null) {
			result = (PropertyEditor) edRef.get();
		}
		if (result == null) {
			Class type = getPropertyType();
			try
			{
				//try to get more specific property type
				// suitable for the generic "Object" properties
				Object value = this.getValue();
				if(value != null)
					type = value.getClass();
			}catch(InvocationTargetException ex)
			{
				ex.printStackTrace();
			}
			if(type != null)
			  result = java.beans.PropertyEditorManager.findEditor(type);
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
			edRef = new java.lang.ref.SoftReference<PropertyEditor>(result);
		}
		
		return result;
	}

	/*
	 * Standard equals implementation for all property classes. @param property
	 * The object to compare to
	 */
	public boolean equals(Object property) {
		if (!(property instanceof BeanProperty)) {
			return false;
		}
		
        //TODO: add equals test for underlying PropertyDescriptor, PropertyEditor, etc
		return ((BeanProperty) property).getName().equals(getName());
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
		sup.firePropertyChange(PROP_VALUE, null, null);
	}

	/*
	 * Returns a hash code value for the object.
	 * 
	 * @return int hashcode
	 */
	public int hashCode() {
		Class valueType = getPropertyType();
		return getName().hashCode()
				* (valueType == null ? 1 : valueType.hashCode());
	}
}
