package org.hypergraphdb.viewer.props;

import java.lang.reflect.Array;
import java.util.*;

import org.hypergraphdb.type.Slot;

public class PropertyModelFactory
{
	private PropertyModelFactory(){}
	private static Set<PropertiesTableModel> models = new HashSet<PropertiesTableModel>();
	static
    {
	   registerModel(new CollectionModel());
       registerModel(new MapModel());
       registerModel(new ArrayModel());
    }
	
	public static void registerModel(PropertiesTableModel model)
	{
		models.add(model);
	}
	
	public static PropertiesTableModel getModel(Class<?> clazz)
	{
	    for(PropertiesTableModel m : models)
	        if(m.canModelClass(clazz))
		        return m;
	    return null;
	}
	
	public static class ArrayModel extends CollectionModel
    {
	    public boolean canModelClass(Class<?> c)
	    {
	       return c.isArray();   
	    } 
	    
	    public AbstractProperty[] introspect(Object obj)
        {
	        int len = Array.getLength(bean);
            AbstractProperty[] props = new AbstractProperty[len];
            
            for (int i = 0; i < len; i++)
                props[i] = new GenericProperty("[" + i + "]", Array.get(bean, i));
            return props;
        }
    }
    
    public static class CollectionModel extends PropertiesTableModel
    {
        public AbstractProperty[] introspect(Object obj)
        {
            Collection<Object> list = (Collection<Object>) bean;
            
            AbstractProperty[] props = new AbstractProperty[list.size()];
            int i = 0;
            for (Object o: list)
            {
                props[i] = new GenericProperty("[" + i + "]", o);
                i++;
            }
            return props;
        }
        
        public boolean canModelClass(Class<?> c)
        {
           return Collection.class.isAssignableFrom(c);   
        } 
    }
    
    public static class MapModel extends PropertiesTableModel
    {
        public AbstractProperty[] introspect(Object obj)
        {
            Map<Object, Object> map = (Map<Object, Object>) bean;
            
            AbstractProperty[] props = new AbstractProperty[map.size() + 1];
            props[0] = new GenericProperty("size", map.size());
            int i = 1;
            for (Object key: map.keySet())
            {
                props[i] = new GenericProperty("" + key, map.get(key));
                i++;
            }
            return props;
        }
        
        public boolean canModelClass(Class<?> c)
        {
           return Map.class.isAssignableFrom(c);   
        } 
    }
    
    static class SlotTableModel extends PropertiesTableModel
    {
        public AbstractProperty[] introspect(Object obj)
        {
            Slot slot = (Slot) bean;
            AbstractProperty[] data0 = new AbstractProperty[2];
            data0[0] = new GenericProperty("label", slot.getLabel());
            data0[1] = new GenericProperty("valueType",
                    slot.getValueType().getClass().getName());
            return data0;
        }
        
        
    }
//	public static class SimplePropertiesTableModel extends PropertiesTableModel
//	{
//		public AbstractProperty[][] getData()
//		{
//			AbstractProperty[][] data0 = new AbstractProperty[2][2];
//			data0[0][0] = new ReadOnlyProperty("class");
//			data0[1][0] = new ReadOnlyProperty(bean.getClass().getName());
//			data0[0][1] = new ReadOnlyProperty("value");
//			data0[1][1] = new ReadOnlyProperty(bean.toString());
//			return data0;
//		}
//     }
}
