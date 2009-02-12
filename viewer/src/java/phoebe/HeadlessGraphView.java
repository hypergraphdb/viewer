package phoebe;

import fing.model.FEdge;
import fing.model.FNode;
import java.util.*;
import java.awt.Paint;

import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.util.PrimeFinder;

/**
 * This HeadlessGraphView is designed to be a "headless" GraphView. While it
 * implements GraphView all methods that actually deal with viewable things are
 * abstract.
 * 
 * This class would be used when you would want to compute a layout but not ever
 * put anything on the screen
 */
public abstract class HeadlessGraphView 
{
	// Keep Track of the Default FNode Position
	public double DEFAULT_X = 100;
	// Keep Track of the Default FNode Position
	public double DEFAULT_Y = 100;
	// Default FNode Paint
	public static Paint DEFAULT_NODE_PAINT = java.awt.Color.lightGray;
	// Default FNode Selection Paint
	public static Paint DEFAULT_NODE_SELECTION_PAINT = java.awt.Color.yellow;
	// Deafult Border Paint
	public static Paint DEFAULT_BORDER_PAINT = java.awt.Color.black;
	public static Paint DEFAULT_EDGE_STROKE_PAINT = java.awt.Color.black;
	public static Paint DEFAULT_EDGE_STROKE_PAINT_SELECTION = java.awt.Color.red;
	public static Paint DEFAULT_EDGE_END_PAINT = java.awt.Color.black;
	/**
	 * Data store for Nodes
	 */
	protected Map<FNode, Object[]> nodeDataStore;
	/**
	 * The Description and Class type associated with a FNode Data Type
	 */
	protected Map<Integer, Object> nodeDataDescription;
	/**
	 * Data store for Edges
	 */
	protected Map<FEdge, Object> edgeDataStore;
	/**
	 * The Description and Class type associated with a FEdge Data Type
	 */
	protected Map<Integer, Object> edgeDataDescription;
	/**
	 * A GraphView can have associated with it an array of pointers that are
	 * used to drive data-driven views. Models don't need this because the
	 * algorithm that would use that data object would already be able to access
	 * itself.
	 */
	protected ArrayList viewDataStore;
	
	protected HGVNetwork network;
	/**
	 * A unique Identifier for the Model
	 */
	protected String identifier;
	/**
	 * A static variable that is used to assign a default unique name to every
	 * GraphView
	 */
	private static int viewCount = 0;

	public HeadlessGraphView(HGVNetwork net)
	{
		this("View" + viewCount, net);
		viewCount++;
	}

	/**
	 * Creates a new HeadlessGraphView given
	 * @param perspective The one GraphPerspective that we are a view on
	 * @param id the unique identifier for this view
	 */
	public HeadlessGraphView(String identifier, HGVNetwork net)
	{
		this.network = net;
		this.identifier = identifier;
		// System.out.println( "NAME OF VIEW: "+identifier );
		// Create Data Stores and Data Descriptors
		nodeDataStore = new HashMap<FNode, Object[]>(PrimeFinder
				.nextPrime(net.getNodeCount()));
		edgeDataStore = new HashMap<FEdge, Object>(PrimeFinder
				.nextPrime(net.getEdgeCount()));
		viewDataStore = new ArrayList();
	}

	public HGVNetwork getNetwork()
	{
		return network;
	}

	/**
	 * Set All Data For a NOde <B>Big Bold Faced Warning</B> <BR>
	 * Talk to rowan before using.
	 */
	public void setAllNodePropertyData(FNode node_index, Object[] data)
	{
		nodeDataStore.put(node_index, data);
	}

	/*
	 * <B>Big Bold Faced Warning</B> <BR> Talk to rowan before using.
	 */
	public Object[] getAllNodePropertyData(FNode node_index)
	{
		return nodeDataStore.get(node_index);
	}

	/**
	 * Set All Data For an FEdge <BR>
	 * <B>Big Bold Faced Warning</B> <BR>
	 * Talk to rowan before using.
	 */
	public void setAllEdgePropertyData(FEdge edge_index, Object[] data)
	{
		edgeDataStore.put(edge_index, data);
	}

	/*
	 * <B>Big Bold Faced Warning</B> <BR> Talk to rowan before using.
	 */
	public Object[] getAllEdgePropertyData(FEdge edge_index)
	{
		return (Object[]) edgeDataStore.get(edge_index);
	}

	/**
	 * Return the stored value for the node for the given property
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 */
	public Object getNodeObjectProperty(FNode node_index, int property)
	{
		if (nodeDataStore.get(node_index) != null)
		{
			return nodeDataStore.get(node_index)[property];
		}
		// Error
		return null;
	}

	/**
	 * @param edge_index The FNode Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setNodeObjectProperty(FNode node_index, int property,
			Object value)
	{
		if (nodeDataStore.get(node_index) instanceof Object[])
		{
			Object[] data = nodeDataStore.get(node_index);
			if (data.length > property)
			{
				nodeDataStore.get(node_index)[property] = value;
				return true;
			}
			Object[] new_data = new Object[property + 1];
			System.arraycopy(data, 0, new_data, 0, data.length);
			new_data[property] = value;
			nodeDataStore.put(node_index, new_data);
			return true;
		} else
		{
			// FNode data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = value;
			nodeDataStore.put(node_index, new_data);
			return true;
		}
	}

	/**
	 * Return the stored value for the edge for the given property
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 */
	public Object getEdgeObjectProperty(FEdge edge_index, int property)
	{
		if (edgeDataStore.get(edge_index) != null)
		{
			return ((Object[]) edgeDataStore.get(edge_index))[property];
		}
		// Error
		return null;
	}

	/**
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setEdgeObjectProperty(FEdge edge_index, int property,
			Object value)
	{
		if (edgeDataStore.get(edge_index) instanceof Object[])
		{
			Object[] data = (Object[]) edgeDataStore.get(edge_index);
			if (data.length > property)
			{
				((Object[]) edgeDataStore.get(edge_index))[property] = value;
				return true;
			}
			Object[] new_data = new Object[property + 1];
			System.arraycopy(data, 0, new_data, 0, data.length);
			new_data[property] = value;
			edgeDataStore.put(edge_index, new_data);
			return true;
		} else
		{
			// FEdge data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = value;
			edgeDataStore.put(edge_index, new_data);
			return true;
		}
	}

	// ----------------------------------------
	// ----------------------------------------
	// Double Methods
	// Nodes
	/**
	 * This method stores the primitive type wrapped in a Double, however a
	 * double can be returned, if you don't want to use the Object property
	 * method.
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 */
	public double getNodeDoubleProperty(FNode node_index, int property)
	{
		if (nodeDataStore.get(node_index) != null)
			if (nodeDataStore.get(node_index)[property] instanceof Double)
				return ((Double) nodeDataStore.get(node_index)[property]);
		return 0;
	}

	/**
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setNodeDoubleProperty(FNode node_index, int property,
			double value)
	{
		if (nodeDataStore.get(node_index) instanceof Object[])
		{
			Object[] data = nodeDataStore.get(node_index);
			if (data.length > property)
			{
				if (nodeDataStore.get(node_index)[property] instanceof Double)
				{
					nodeDataStore.get(node_index)[property] = value;
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Double(value);
				nodeDataStore.put(node_index, new_data);
				return true;
			}
		} else
		{
			// FNode data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Double(value);
			nodeDataStore.put(node_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// Edges
	/**
	 * This method stores the primitive type wrapped in a Double, however a
	 * double can be returned, if you don't want to use the Object property
	 * method.
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 */
	public double getEdgeDoubleProperty(FEdge edge_index, int property)
	{
		if (edgeDataStore.get(edge_index) != null)
		{
			if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Double)
			{
				return ((Double) ((Object[]) edgeDataStore.get(edge_index))[property])
						.doubleValue();
			}
		}
		return 0;
	}

	/**
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setEdgeDoubleProperty(FEdge edge_index, int property,
			double value)
	{
		if (edgeDataStore.get(edge_index) instanceof Object[])
		{
			Object[] data = (Object[]) edgeDataStore.get(edge_index);
			if (data.length > property)
			{
				if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Double)
				{
					((Object[]) edgeDataStore.get(edge_index))[property] = new Double(
							value);
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Double(value);
				edgeDataStore.put(edge_index, new_data);
				return true;
			}
		} else
		{
			// FEdge data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Double(value);
			edgeDataStore.put(edge_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// ----------------------------------------
	// ----------------------------------------
	// Float Methods
	// Nodes
	/**
	 * This method stores the primitive type wrapped in a Float, however a float
	 * can be returned, if you don't want to use the Object property method.
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 */
	public float getNodeFloatProperty(FNode node_index, int property)
	{
		if (nodeDataStore.get(node_index) != null)
			if (nodeDataStore.get(node_index)[property] instanceof Float)
				return ((Float)nodeDataStore.get(node_index)[property]);
		return 0;
	}

	/**
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setNodeFloatProperty(FNode node_index, int property,
			float value)
	{
		if (nodeDataStore.get(node_index) instanceof Object[])
		{
			Object[] data = (Object[]) nodeDataStore.get(node_index);
			if (data.length > property)
			{
				if (nodeDataStore.get(node_index)[property] instanceof Float)
				{
					nodeDataStore.get(node_index)[property] = value;
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Float(value);
				nodeDataStore.put(node_index, new_data);
				return true;
			}
		} else
		{
			// FNode data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Float(value);
			nodeDataStore.put(node_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// Edges
	/**
	 * This method stores the primitive type wrapped in a Float, however a float
	 * can be returned, if you don't want to use the Object property method.
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 */
	public float getEdgeFloatProperty(FEdge edge_index, int property)
	{
		if (edgeDataStore.get(edge_index) != null)
		{
			if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Float)
			{
				return ((Float) ((Object[]) edgeDataStore.get(edge_index))[property])
						.floatValue();
			}
		}
		return 0;
	}

	/**
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setEdgeFloatProperty(FEdge edge_index, int property,
			float value)
	{
		if (edgeDataStore.get(edge_index) instanceof Object[])
		{
			Object[] data = (Object[]) edgeDataStore.get(edge_index);
			if (data.length > property)
			{
				if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Float)
				{
					((Object[]) edgeDataStore.get(edge_index))[property] = new Float(
							value);
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Float(value);
				edgeDataStore.put(edge_index, new_data);
				return true;
			}
		} else
		{
			// FEdge data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Float(value);
			edgeDataStore.put(edge_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// ----------------------------------------
	// ----------------------------------------
	// Boolean Methods
	// Nodes
	/**
	 * This method stores the primitive type wrapped in a Boolean, however a
	 * boolean can be returned, if you don't want to use the Object property
	 * method.
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 */
	public boolean getNodeBooleanProperty(FNode node_index, int property)
	{
		if (nodeDataStore.get(node_index) != null)
			if (nodeDataStore.get(node_index)[property] instanceof Boolean)
				return ((Boolean) nodeDataStore.get(node_index)[property]);
		return false;
	}

	/**
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setNodeBooleanProperty(FNode node_index, int property,
			boolean value)
	{
		if (nodeDataStore.get(node_index) instanceof Object[])
		{
			Object[] data = nodeDataStore.get(node_index);
			if (data.length > property)
			{
				if (nodeDataStore.get(node_index)[property] instanceof Boolean)
				{
					nodeDataStore.get(node_index)[property] = value;
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Boolean(value);
				nodeDataStore.put(node_index, new_data);
				return true;
			}
		} else
		{
			// FNode data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Boolean(value);
			nodeDataStore.put(node_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// Edges
	/**
	 * This method stores the primitive type wrapped in a Boolean, however a
	 * boolean can be returned, if you don't want to use the Object property
	 * method.
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 */
	public boolean getEdgeBooleanProperty(FEdge edge_index, int property)
	{
		if (edgeDataStore.get(edge_index) != null)
		{
			if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Boolean)
			{
				return ((Boolean) ((Object[]) edgeDataStore.get(edge_index))[property])
						.booleanValue();
			}
		}
		return false;
	}

	/**
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setEdgeBooleanProperty(FEdge edge_index, int property,
			boolean value)
	{
		if (edgeDataStore.get(edge_index) instanceof Object[])
		{
			Object[] data = (Object[]) edgeDataStore.get(edge_index);
			if (data.length > property)
			{
				if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Boolean)
				{
					((Object[]) edgeDataStore.get(edge_index))[property] = new Boolean(
							value);
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Boolean(value);
				edgeDataStore.put(edge_index, new_data);
				return true;
			}
		} else
		{
			// FEdge data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Boolean(value);
			edgeDataStore.put(edge_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// ----------------------------------------
	// ----------------------------------------
	// Integer Methods
	// Nodes
	/**
	 * This method stores the primitive type wrapped in a Integer, however a
	 * Integer can be returned, if you don't want to use the Object property
	 * method.
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 */
	public int getNodeIntProperty(FNode node_index, int property)
	{
		if(nodeDataStore.get(node_index) != null)
			if(nodeDataStore.get(node_index)[property] instanceof Integer)
				return ((Integer)nodeDataStore.get(node_index)[property]);
			
		return 0;
	}

	/**
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setNodeIntProperty(FNode node_index, int property, int value)
	{
		if (nodeDataStore.get(node_index) instanceof Object[])
		{
			Object[] data = nodeDataStore.get(node_index);
			if (data.length > property)
			{
				if (nodeDataStore.get(node_index)[property] instanceof Integer)
				{
					nodeDataStore.get(node_index)[property] = value;
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Integer(value);
				nodeDataStore.put(node_index, new_data);
				return true;
			}
		} else
		{
			// FNode data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Integer(value);
			nodeDataStore.put(node_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}

	// Edges
	/**
	 * This method stores the primitive type wrapped in a Integer, however a
	 * Integer can be returned, if you don't want to use the Object property
	 * method.
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 */
	public int getEdgeIntProperty(FEdge edge_index, int property)
	{
		if (edgeDataStore.get(edge_index) != null)
		{
			if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Integer)
			{
				return ((Integer) ((Object[]) edgeDataStore.get(edge_index))[property])
						.intValue();
			}
		}
		return 0;
	}

	/**
	 * @param edge_index The FEdge Index to be queried
	 * @param property the property to be accessed
	 * @param value the new value for this property
	 */
	public boolean setEdgeIntProperty(FEdge edge_index, int property, int value)
	{
		if (edgeDataStore.get(edge_index) instanceof Object[])
		{
			Object[] data = (Object[]) edgeDataStore.get(edge_index);
			if (data.length > property)
			{
				if (((Object[]) edgeDataStore.get(edge_index))[property] instanceof Integer)
				{
					((Object[]) edgeDataStore.get(edge_index))[property] = new Integer(
							value);
					return true;
				}
			} else
			{
				// array not long enough
				Object[] new_data = new Object[property + 1];
				System.arraycopy(data, 0, new_data, 0, data.length);
				new_data[property] = new Integer(value);
				edgeDataStore.put(edge_index, new_data);
				return true;
			}
		} else
		{
			// FEdge data not initialized
			Object[] new_data = new Object[property + 1];
			new_data[property] = new Integer(value);
			edgeDataStore.put(edge_index, new_data);
			return true;
		}
		// TODO: initilialize the data here.
		return false;
	}
}
