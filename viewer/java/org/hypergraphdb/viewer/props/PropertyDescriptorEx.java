package org.hypergraphdb.viewer.props;

import java.beans.PropertyEditor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 *
 * @author  Administrator
 */
public class PropertyDescriptorEx 
{
    protected PropertyEditor editor = null;
    protected PropertyDescriptor descriptor = null; 
    protected Object bean;
    
    /** Creates a new instance of PropertyDescriptorEx */
    public PropertyDescriptorEx(Object bean, PropertyDescriptor e) 
    {
    	this.bean = bean;
        descriptor = e;
    }
    
    public PropertyEditor getPropertyEditor()
    {
        return editor;
    }
    
    public void setPropertyEditor(PropertyEditor ed)
    {
        editor = ed;
    }
    
    ///////////PropertyDescriptor methods///////////////////
    
    public java.util.Enumeration attributeNames()
    {
        return descriptor.attributeNames();
    }
    
    public boolean equals(Object obj)
    {
       return descriptor.equals(obj);
    }
    
    public String getDisplayName()
    {
        return descriptor.getDisplayName();
    }
    
    public String getName()
    {
       return descriptor.getName();
    }
    
    public Class getPropertyEditorClass()
    {
       return  descriptor.getPropertyEditorClass();
    }
    
    public Class getPropertyType()
    {
        return descriptor.getPropertyType();
    }
    
    public Method getReadMethod()
    {
       return descriptor.getReadMethod();
    }
    
    public String getShortDescription()
    {
        return descriptor.getShortDescription();
    }
    
    public Object getValue(String str)
    {
       return descriptor.getValue(str);
    }
    
    public Method getWriteMethod()
    {
       return descriptor.getWriteMethod();
    }
    
    public int hashCode()
    {
       return descriptor.hashCode();
    }
    
    public boolean isBound()
    {
       return descriptor.isBound();
    }
    
    public boolean isConstrained()
    {
        return descriptor.isConstrained();
    }
    
    public boolean isExpert()
    {
        return descriptor.isExpert();
    }
    
    public boolean isHidden()
    {
        return descriptor.isHidden();
    }
    
    public boolean isPreferred()
    {
       return descriptor.isPreferred();
    }
    
    public void setBound(boolean param)
    {
        descriptor.setBound(param);
    }
    
    public void setConstrained(boolean param)
    {
        descriptor.setConstrained(param);
    }
    
    public void setDisplayName(String str)
    {
        descriptor.setDisplayName(str);
    }
    
    public void setExpert(boolean param)
    {
        descriptor.setExpert(param);
    }
    
    public void setHidden(boolean param)
    {
        descriptor.setHidden(param);
    }
    
    public void setName(String str)
    {
        descriptor.setName(str);
    }
    
    public void setPreferred(boolean param)
    {
        descriptor.setPreferred(param);
    }
    
    public void setPropertyEditorClass(Class clazz)
    {
        descriptor.setPropertyEditorClass(clazz);
    }
    
    public void setReadMethod(Method method) throws java.beans.IntrospectionException
    {
        descriptor.setReadMethod(method);
    }
    
    public void setShortDescription(String str)
    {
        descriptor.setShortDescription(str);
    }
    
    public void setValue(String str, Object obj)
    {
        descriptor.setValue(str, obj);
    }
    
    public void setWriteMethod(Method method) throws java.beans.IntrospectionException
    {
        descriptor.setWriteMethod(method);
    }
    
    public String toString()
    {
        return descriptor.toString();
    }
    
    /////Utility methods
    public boolean canRead()
    {
        return descriptor.getReadMethod() != null;
    }
    
    public boolean canWrite()
    {
        return descriptor.getWriteMethod() != null;
    }
    
}
