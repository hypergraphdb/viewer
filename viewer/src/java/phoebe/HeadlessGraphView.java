package phoebe;

import fing.model.FGraphPerspective;
import fing.model.FRootGraph;
import java.util.*;
import java.awt.Paint;
import org.hypergraphdb.viewer.util.PrimeFinder;

/**
 * This HeadlessGraphView is designed to be a "headless" GraphView. While it
 * implements GraphView all methods that actually deal with viewable things are
 * abstract.
 * 
 * This class would be used when you would want to compute a layout but not ever
 * put anything on the screen
 */
public abstract class HeadlessGraphView //implements GraphView
{
	// Keep Track of the Default FNode Position
	public double DEFAULT_X = 100;
	// Keep Track of the Default FNode Position
	public double DEFAULT_Y = 100;
	// Default FNode Paint
	public static Paint DEFAULT_NODE_PAINT = java.awt.Color.lightGray;
	// Default FNode Selction Pain
	public static Paint DEFAULT_NODE_SELECTION_PAINT = java.awt.Color.yellow;
	// Deafult Border Paint
	public static Paint DEFAULT_BORDER_PAINT = java.awt.Color.black;
	public static Paint DEFAULT_EDGE_STROKE_PAINT = java.awt.Color.black;
	public static Paint DEFAULT_EDGE_STROKE_PAINT_SELECTION = java.awt.Color.red;
	public static Paint DEFAULT_EDGE_END_PAINT = java.awt.Color.black;
	/**
	 * Data store for Nodes
	 */
	protected Map<Integer, Object[]> nodeDataStore;
	/**
	 * The Description and Class type associated with a FNode Data Type
	 */
	protected Map<Integer, Object> nodeDataDescription;
	/**
	 * Data store for Edges
	 */
	protected Map<Integer, Object> edgeDataStore;
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
	/**
	 * The GraphPerspective that we hold view information for
	 */
	protected FGraphPerspective perspective;
	/**
	 * A unique Identifier for the Model
	 */
	protected String identifier;
	/**
	 * A static variable that is used to assign a default unique name to every
	 * GraphView
	 */
	private static int viewCount = 0;

	public HeadlessGraphView(FGraphPerspective perspective)
	{
		this("View" + viewCount, perspective);
		viewCount++;
	}

	/**
	 * Creates a new HeadlessGraphView given
	 * @param perspective The one GraphPerspective that we are a view on
	 * @param id the unique identifier for this view
	 */
	public HeadlessGraphView(String identifier, FGraphPerspective perspective)
	{
		this.perspective = perspective;
		this.identifier = identifier;
		// System.out.println( "NAME OF VIEW: "+identifier );
		// Create Data Stores and Data Descriptors
		nodeDataStore = new HashMap<Integer, Object[]>(PrimeFinder
				.nextPrime(perspective.getNodeCount()));
		edgeDataStore = new HashMap<Integer, Object>(PrimeFinder
				.nextPrime(perspective.getEdgeCount()));
		viewDataStore = new ArrayList();
	}

	public FRootGraph getRootGraph()
	{
		return perspective.getRootGraph();
	}

	public FGraphPerspective getGraphPerspective()
	{
		return perspective;
	}

	/**
	 * Set All Data For a NOde <B>Big Bold Faced Warning</B> <BR>
	 * Talk to rowan before using.
	 */
	public void setAllNodePropertyData(int node_index, Object[] data)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
		nodeDataStore.put(node_index, data);
	}

	/*
	 * <B>Big Bold Faced Warning</B> <BR> Talk to rowan before using.
	 */
	public Object[] getAllNodePropertyData(int node_index)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
		return nodeDataStore.get(node_index);
	}

	/**
	 * Set All Data For an FEdge <BR>
	 * <B>Big Bold Faced Warning</B> <BR>
	 * Talk to rowan before using.
	 */
	public void setAllEdgePropertyData(int edge_index, Object[] data)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
		edgeDataStore.put(edge_index, data);
	}

	/*
	 * <B>Big Bold Faced Warning</B> <BR> Talk to rowan before using.
	 */
	public Object[] getAllEdgePropertyData(int edge_index)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
		return (Object[]) edgeDataStore.get(edge_index);
	}

	/**
	 * Return the stored value for the node for the given property
	 * @param node_index The FNode Index to be queried
	 * @param property the property to be accessed
	 */
	public Object getNodeObjectProperty(int node_index, int property)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public boolean setNodeObjectProperty(int node_index, int property,
			Object value)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public Object getEdgeObjectProperty(int edge_index, int property)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public boolean setEdgeObjectProperty(int edge_index, int property,
			Object value)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public double getNodeDoubleProperty(int node_index, int property)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public boolean setNodeDoubleProperty(int node_index, int property,
			double value)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public double getEdgeDoubleProperty(int edge_index, int property)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public boolean setEdgeDoubleProperty(int edge_index, int property,
			double value)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public float getNodeFloatProperty(int node_index, int property)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public boolean setNodeFloatProperty(int node_index, int property,
			float value)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public float getEdgeFloatProperty(int edge_index, int property)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public boolean setEdgeFloatProperty(int edge_index, int property,
			float value)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public boolean getNodeBooleanProperty(int node_index, int property)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public boolean setNodeBooleanProperty(int node_index, int property,
			boolean value)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public boolean getEdgeBooleanProperty(int edge_index, int property)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public boolean setEdgeBooleanProperty(int edge_index, int property,
			boolean value)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public int getNodeIntProperty(int node_index, int property)
	{
		if(node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public boolean setNodeIntProperty(int node_index, int property, int value)
	{
		if (node_index >= 0)
		{
			node_index = perspective.getRootGraphNodeIndex(node_index);
		}
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
	public int getEdgeIntProperty(int edge_index, int property)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	public boolean setEdgeIntProperty(int edge_index, int property, int value)
	{
		if (edge_index >= 0)
		{
			edge_index = perspective.getRootGraphEdgeIndex(edge_index);
		}
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
	// ----------------------------------------//
	// Stubs
	// ----------------------------------------//
	// /**
	// * @return an int array of the graph perspective indices of the selected
	// nodes
	// */
	// public int[] getSelectedNodeIndices(){ return null; }
	// /**
	// * @return a list of the selected PNodeView
	// */
	// public List getSelectedNodes(){ return null; }
	// /**
	// * @return an int array of the graph perspective indices of the selected
	// edges
	// */
	// public int[] getSelectedEdgeIndices(){ return null; }
	// /**
	// * @return a list of the selected PEdgeView
	// */
	// public List getSelectedEdges(){ return null; }
	// /**
	// * Adds a new GraphViewChangeListener to this GraphViews list of
	// listeners.
	// */
	// public void addGraphViewChangeListener(GraphViewChangeListener listener){
	// }
	// /**
	// * Removes a GraphViewChangeListener from this GraphViews list of
	// listeners.
	// */
	// public void removeGraphViewChangeListener(GraphViewChangeListener
	// listener){ }
	// /**
	// * @param the new Paint for the background
	// */
	// public void setBackgroundPaint(Paint paint){ }
	// /**
	// * @return the backgroundPaint
	// */
	// public Paint getBackgroundPaint(){ return null; }
	// /**
	// * @return the java.awt.Component that can be added to most screen thingys
	// */
	// public Component getComponent(){ return null; }
	// /**
	// * @param node_index the index of a node to have a view created for it
	// * @return a new PNodeView based on the node with the given index
	// */
	// public PNodeView addNodeView(int node_index){ return null; }
	// /**
	// * @param edge_index the index of an edge
	// * @return the newly created edgeview
	// */
	// public PEdgeView addEdgeView(int edge_index){ return null; }
	// /**
	// * To facilitate adding Custome EdgeViews
	// * It is recomended that All Custom FEdge Views follow the patterns
	// outlined
	// * in PEdgeView0 and BasicPEdgeView.
	// * @param class_name the name of the class that implements PEdgeView and
	// esnted PEdge
	// * @param edge_index the index of the edge
	// */
	// public PEdgeView addEdgeView(String class_name, int edge_index){ return
	// null; }
	// /**
	// * To facilitate adding Custome NodeViews
	// * It is recomended that All Custom FNode Views follow the patterns
	// outlined
	// * in PNodeView and BasicPNodeView.
	// * @param class_name the name of the class that implements PNodeView and
	// esnted PNode
	// * @param node_index the index of the node
	// */
	// public PNodeView addNodeView(String class_name, int node_index){ return
	// null; }
	// /**
	// * Add in a PNodeView for a FNode in the GraphPerspective.
	// * Note that this means that if there already was a PNodeView for this
	// node,
	// * the new PNodeView will take its place.
	// * @return If it is replacing, it returns the <B>old</B> PNodeView.
	// * @return If it is new, it returns the <B>new</b> PNodeView.
	// */
	// public PNodeView addNodeView(
	// int node_index,
	// PNodeView node_view_replacement){ return null; }
	// /**
	// * @return The Unique Identifier of this GraphView
	// */
	// public String getIdentifier(){ return null; }
	// /**
	// * @param new_identifier The New Identifier for this GraphView
	// */
	// public void setIdentifier(String new_identifier){ }
	// /**
	// * @return The Current Zoom Level
	// */
	// public double getZoom(){ return 0; }
	// /**
	// * @param zoom The New ZoomLevel
	// */
	// public void setZoom(double zoom){ }
	// /**
	// * Fits all Viewable elements onto the Graph
	// */
	// public void fitContent(){ }
	// /**
	// * Do a global redraw of the entire canvas
	// */
	// public void updateView(){ }
	// /**
	// * nodeViewsIterator only returns the NodeViews that are explicitly
	// * associated with this GraphView
	// */
	// public Iterator getNodeViewsIterator(){ return null; }
	// /**
	// * @return the number of node views present
	// */
	// public int getNodeViewCount(){ return 0; }
	// /**
	// * @return the number of EdgeViews present
	// */
	// public int getEdgeViewCount(){ return 0; }
	// /**
	// * @param node The FNode whose view is requested
	// *
	// * @return The PNodeView of the given FNode
	// */
	// public PNodeView getNodeView(FNode node){ return null; }
	// /**
	// * @param index the index of the node whose view is requested
	// * @return The PNodeView of the given FNode
	// */
	// public PNodeView getNodeView(int index){ return null; }
	// /**
	// * Return all of the EdgeViews in this GraphView
	// */
	// public java.util.List getEdgeViewsList(){ return null; }
	// /**
	// * Note that this will return a list of FEdge objects, the other one will
	// return indices
	// * @return The list of EdgeViews connecting these two nodes. Possibly
	// null.
	// */
	// public java.util.List getEdgeViewsList(
	// FNode oneNode,
	// FNode otherNode){ return null; }
	// /**
	// * @return a List of indicies
	// */
	// public java.util.List getEdgeViewsList(
	// int from_node_index,
	// int to_node_index,
	// boolean include_undirected_edges){ return null; }
	// /**
	// * @return the PEdgeView that corresponds to the given index
	// */
	// public PEdgeView getEdgeView(int edge_index){ return null; }
	// /**
	// * Return all of the EdgeViews in this GraphView
	// */
	// public Iterator getEdgeViewsIterator(){ return null; }
	// /**
	// * @return the PEdgeView that corresponds to the given FEdge
	// */
	// public PEdgeView getEdgeView(FEdge edge){ return null; }
	// /**
	// * @return the number of edges
	// */
	// public int edgeCount(){ return 0; }
	// /**
	// * @return The number of Nodes, same number as the perspective
	// */
	// public int nodeCount(){ return 0; }
	// /**
	// * use this to hide a node or edge
	// */
	// public boolean hideGraphObject(Object object){ return false; }
	// /**
	// * use this to show a node or edge
	// */
	// public boolean showGraphObject(Object object){ return false; }
	// /**
	// * <B> Warning!!!!!!!</B><BR>
	// * Only to be used for homogenous groups!!!!
	// */
	// public boolean hideGraphObjects(List objects){ return false; }
	// /**
	// * <B> Warning!!!!!!!</B><BR>
	// * Only to be used for homogenous groups!!!!
	// */
	// public boolean showGraphObjects(List objects){ return false; }
	// /**
	// * Context Menu Support
	// */
	// public Object[] getContextMethods(
	// String class_name,
	// boolean plus_superclass){ return null; }
	// /**
	// * Context Menu Support
	// */
	// public Object[] getContextMethods(
	// String class_name,
	// Object[] methods){ return null; }
	// /**
	// * Context Menu Support
	// */
	// public boolean addContextMethod(
	// String class_name,
	// String method_class_name,
	// String method_name,
	// String action_name){ return false; }
}
