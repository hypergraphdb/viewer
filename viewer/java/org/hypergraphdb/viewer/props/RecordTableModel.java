package org.hypergraphdb.viewer.props;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.hypergraphdb.type.Record;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.type.Slot;
import org.hypergraphdb.viewer.HGVLogger;
import org.hypergraphdb.viewer.HGViewer;

/**
 *
 */
public class RecordTableModel  extends PropertiesTableModel
{
    private Record rec;
    private RecordType type;
    //private int count;
    
     
    public AbstractProperty[][] getData()
	{
         this.rec = (Record) bean;
         type = (RecordType)
         HGViewer.getCurrentNetwork().getHyperGraph().get(rec.getTypeHandle());
        
        AbstractProperty[][] data0 = new AbstractProperty[2][type.slotCount()];
        //for (int i = 0; i < type.slotCount(); i++)
        int i = 0;
        Iterator it = rec.getSlots();
        while(it.hasNext())
        {
            final Slot slot = (Slot)it.next();
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
					return rec.get(slot);
				}

				@Override
				public void setValue(Object v) throws InvocationTargetException
				{
					rec.set(slot, v);
				}
            	
            };
            data0[0][i] = new ReadOnlyProperty(slot.getLabel());
            data0[1][i] = prop;
            i++;
        }
        return data0;
    }
    
    public void setValueAt(Object value,   int    row,    int    col)
    {
        HGVLogger.getInstance().debug("RecordTableModel - setValueAt: " + value);
        //Slot slot = type.getAt(row);
       // rec.set(slot, value);
        //fireTableCellUpdated(row, col);
    }
 
    
}
