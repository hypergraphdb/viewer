package org.hypergraphdb.viewer.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;


public interface AbstractProperty
{
	public Object getValue() throws InvocationTargetException;

	public void setValue(Object v) throws InvocationTargetException;

	public PropertyEditor getPropertyEditor();

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	//PropertyDescriptor methods 
	public String getDisplayName();

	public String getName();

	public Class getPropertyEditorClass();

	public Class getPropertyType();

	//public Method getReadMethod();

	//public String getShortDescription();

	//public Method getWriteMethod();

	public boolean canRead();

	public boolean canWrite();
}
