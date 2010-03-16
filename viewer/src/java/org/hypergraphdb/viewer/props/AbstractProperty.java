package org.hypergraphdb.viewer.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;


public interface AbstractProperty extends Comparable
{
   /** Name of the 'value' property. */
    public static final String PROP_VALUE = "value";

    public Object getValue() throws InvocationTargetException;

	public void setValue(Object v) throws InvocationTargetException;

	public PropertyEditor getPropertyEditor();

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public String getName();

	public Class<?> getPropertyEditorClass();

	public Class<?> getPropertyType();

	public boolean canRead();

	public boolean canWrite();
	
	 
}
