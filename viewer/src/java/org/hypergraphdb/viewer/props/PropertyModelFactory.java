package org.hypergraphdb.viewer.props;

import java.util.*;

public class PropertyModelFactory
{
	private PropertyModelFactory(){}
	private static Map<Class,PropertiesTableModel> models = new HashMap<Class,PropertiesTableModel>();
	static
    {
		models.put(Boolean.class, new SimplePropertiesTableModel());
		models.put(Byte.class, new SimplePropertiesTableModel());
		models.put(Character.class, new SimplePropertiesTableModel());
		models.put(Double.class, new SimplePropertiesTableModel());
		models.put(Float.class, new SimplePropertiesTableModel());
		models.put(Integer.class, new SimplePropertiesTableModel());
		models.put(Long.class, new SimplePropertiesTableModel());
		models.put(Short.class, new SimplePropertiesTableModel());
		models.put(String.class, new SimplePropertiesTableModel());
    }
	
	public static void registerModel(Class clazz, PropertiesTableModel model)
	{
		models.put(clazz, model);
	}
	
	public static PropertiesTableModel getModel(Class clazz)
	{
		return models.get(clazz);
	}
	
	public static class SimplePropertiesTableModel extends GeneralPropertiesTableModel
	{
		public AbstractProperty[][] getData()
		{
			AbstractProperty[][] data0 = new AbstractProperty[2][2];
			data0[0][0] = new ReadOnlyProperty("class");
			data0[1][0] = new ReadOnlyProperty(bean.getClass().getName());
			data0[0][1] = new ReadOnlyProperty("value");
			data0[1][1] = new ReadOnlyProperty(bean.toString());
			return data0;
		}
     }
}
