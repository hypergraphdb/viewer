package org.hypergraphdb.viewer.props.editors;

import java.util.ArrayList;

import org.hypergraphdb.viewer.props.AbstractProperty;
import org.hypergraphdb.viewer.props.GenericProperty;
import org.hypergraphdb.viewer.props.PropertiesTableModel;

public class ArrayListModel extends PropertiesTableModel
{
    public AbstractProperty[] introspect(Object obj)
	{
        ArrayList<Object> list = (ArrayList<Object>) bean;
        
        AbstractProperty[] props = new AbstractProperty[list.size()];
        for (int i = 0; i < list.size(); i++)
            props[i] = new GenericProperty("[" + i + "]", list.get(i));
        return props;
    }
}
