package org.hypergraphdb.viewer.props;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

public class GenericProperty extends AbstractPropertySupport
{
    String name;
    Object value;
    
    public GenericProperty(String name, Object value)
    {
        this.name = name;
        this.value = value;
    }
 
    public Class<?> getPropertyEditorClass()
    {
        return getPropertyEditor().getClass();
    }

    public boolean canRead()
    {
        return true;
    }

    public boolean canWrite()
    {
        return value != null;
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getPropertyType()
    {
        return (value != null) ? value.getClass() : null;
    }

    public Object getValue() throws InvocationTargetException
    {
        return value;
    }

    public void setValue(Object v) throws InvocationTargetException
    {
    }
    
 
}
