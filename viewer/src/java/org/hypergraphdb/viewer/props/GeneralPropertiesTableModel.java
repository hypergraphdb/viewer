package org.hypergraphdb.viewer.props;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

public class GeneralPropertiesTableModel extends PropertiesTableModel
{
	public static String[] COLUMNNAMES = { "property", "value" };
	
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
}
