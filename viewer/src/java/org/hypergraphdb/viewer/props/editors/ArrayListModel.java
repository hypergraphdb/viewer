package org.hypergraphdb.viewer.props.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.hypergraphdb.type.Record;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.type.Slot;
import org.hypergraphdb.viewer.HGVLogger;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.props.AbstractProperty;
import org.hypergraphdb.viewer.props.AbstractPropertySupport;
import org.hypergraphdb.viewer.props.PropertiesTableModel;
import org.hypergraphdb.viewer.props.ReadOnlyProperty;

public class ArrayListModel extends PropertiesTableModel
{
    private ArrayList list;
    //private int count;
    
     
    public AbstractProperty[][] getData()
	{
         this.list = (ArrayList) bean;
        
        AbstractProperty[][] data0 = new AbstractProperty[2][list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            final Object obj = list.get(i);
            final int index = i;
            AbstractProperty prop = new AbstractPropertySupport()
            {
                @Override
				public boolean canWrite()
				{
					return true;
				}

				@Override
				public Object getValue() throws InvocationTargetException
				{
					return obj;
				}
				
				public String getDisplayName()
				{
					if(this.getPropertyEditor() != null)
						return this.getPropertyEditor().getAsText();
					return null;
				}

				@Override
				public void setValue(Object v) throws InvocationTargetException
				{
					if(v != null)
					   list.set(index, v);
				}
            	
            };
            data0[0][i] = new ReadOnlyProperty("Item"+ i);
            data0[1][i] = prop;
        }
        return data0;
    }
    
    public void setValueAt(Object value,   int    row,    int    col)
    {
        HGVLogger.getInstance().debug("RecordTableModel - setValueAt: " + value);
        //Slot slot = type.getAt(row);
        list.set(row, value);
        fireTableCellUpdated(row, col);
    }
 
    
}
