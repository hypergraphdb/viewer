package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.hypergraphdb.viewer.util.RefUtils;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

public class ObjectInspector extends Outline 
{
   
    public ObjectInspector(Object obj)
    {
        super();
        setRootVisible(false);
        setRenderDataProvider(new RenderData());
        setDefaultRenderer(Object.class, new MyDefaultOutlineCellRenderer());
        setModelObject(obj);
    }
    
    public void setModelObject(Object obj)
    {
        if(obj == null) return;
        
        TreeModel treeMdl = new PropsTreeModel(obj);
        DefaultOutlineModel mdl = (DefaultOutlineModel)
             DefaultOutlineModel.createOutlineModel(treeMdl,
                new PropsRowModel(), true);
        mdl.setNodesColumnLabel("Property");
        setModel(mdl);
    }

    static PropNode createPropNode(String name, Object o)
    {
        if(o != null)
        {
            PropNodeFactory f = getNodeFactory(o.getClass());
            if(f != null)
                return f.createNode(name, o);
        }
        return new PropNode(name, o);
    }
    
    public static class PropNode implements Comparable
    {
        public String name;
        public Object value;
        protected PropNode[] children;
        
        public PropNode(String name, Object object)
        {
            super();
            this.value = object;
            this.name = name;
        }

        public PropNode(Object object)
        {
            this(null, object);
        }
        
        public PropNode[] children()
        {
            if(children != null) return children;
            if(value == null)
                return children = new PropNode[0];
                
            populate_children();
            return children;
        }
        
        protected void populate_children()
        {
            Field[] fs = RefUtils.getFieldsForTypeArray(value.getClass());
            children = new PropNode[fs.length];
            for(int i = 0; i < fs.length; i++)
            {
                Object inner = RefUtils.getPrivateFieldValue(value, 
                        value.getClass(), fs[i].getName());
                children[i] = createPropNode(fs[i].getName(), inner);
            }
            Arrays.sort(children);
        }
        
        
        public int compareTo(Object o)
        {
            if(o instanceof PropNode)
            {
                PropNode p = (PropNode) o;
                if(p.name != null)
                    return - p.name.compareTo(name);
                else
                {
                    return (name != null) ? 1 : 0;
                }
            }
            return 0;
        }
    }
    
  
    public static interface PropNodeFactory
    {
        public boolean supportsClass(Class<?> c);
        public PropNode createNode(String name, Object o);
    }
    
    
    private static Set<PropNodeFactory> factories = new HashSet<PropNodeFactory>();
    static
    {
       registerNodeFactory(new CollectionNodeFactory());
       registerNodeFactory(new MapNodeFactory());
       registerNodeFactory(new ArrayNodeFactory());
    }
    
    public static void registerNodeFactory(PropNodeFactory model)
    {
        factories.add(model);
    }
    
    public static PropNodeFactory getNodeFactory(Class<?> clazz)
    {
        for(PropNodeFactory m : factories)
            if(m.supportsClass(clazz))
                return m;
        return null;
    }
    
    public static class ArrayNodeFactory implements PropNodeFactory
    {
        public boolean supportsClass(Class<?> c)
        {
           return c.isArray();   
        } 
        
        public PropNode createNode(final String name, final Object o)
        {
            return new PropNode(name, o)
            {
                protected void populate_children()
                {
                    int len = Array.getLength(value);
                    children = new PropNode[len];
                    for(int i = 0; i < len; i++)
                        children[i] = new PropNode("[" + i + "]", Array.get(value, i));
                    Arrays.sort(children);
                }
            };
        }
    }      
       
    
    public static class CollectionNodeFactory implements PropNodeFactory
    {
        public PropNode createNode(final String name, final Object o)
        {
            return new PropNode(name, o)
            {
                protected void populate_children()
                {
                    Collection<Object> list = (Collection<Object>) value;
                    children  = new PropNode[list.size()];
                    int i = 0;
                    for (Object o: list)
                    {
                        children[i] = new PropNode("[" + i + "]", o);
                        i++;
                    }
                }
            };
        }
        
        public boolean supportsClass(Class<?> c)
        {
           return Collection.class.isAssignableFrom(c);   
        } 
    }
    
    public static class MapNodeFactory implements PropNodeFactory
    {
        public PropNode createNode(final String name, final Object o)
        {
            return new PropNode(name, o)
            {
                protected void populate_children()
                {
                    Map<Object, Object> map = (Map<Object, Object>) value;
                    children  = new PropNode[map.size() + 1];
                    children[0] = new PropNode("size", map.size());
                    int i = 1;
                    for (Object key: map.keySet())
                    {
                        children[i] = new PropNode("" + key, map.get(key));
                        i++;
                    }
                }
            };
        }
        
        public boolean supportsClass(Class<?> c)
        {
           return Map.class.isAssignableFrom(c);   
        } 
    }

    private static class PropsTreeModel implements TreeModel
    {
        private PropNode node;

        public PropsTreeModel(Object object)
        {
            super();
            this.node = createPropNode(null, object);
        }

        public Object getChild(Object parent, int index)
        {
            PropNode n = (PropNode) parent;
            if(index > n.children().length -1) 
                System.err.println("Problem: " + n.value + ":" + index + ":" +
                        n.children().length);
            return n.children()[index];
        }

        public int getIndexOfChild(Object parent, Object child)
        {
            PropNode n = (PropNode) parent;
            return Arrays.asList(n.children()).indexOf(child);
        }

        public int getChildCount(Object parent)
        {
            PropNode n = (PropNode) parent;
            return n.children() != null ? n.children().length : 0;
        }

        public Object getRoot()
        {
            return node;
        }

        public boolean isLeaf(Object node)
        {
            if (node == null) return true;
           return getChildCount(node) == 0;
        }

        public void addTreeModelListener(TreeModelListener l)
        {
            // Do nothing
        }

        public void removeTreeModelListener(TreeModelListener l)
        {
            // Do nothing
        }

        public void valueForPathChanged(TreePath path, Object newValue)
        {
            // Do nothing
        }
    }

    private static class PropsRowModel implements RowModel
    {
        public Class<?> getColumnClass(int column)
        {
            switch (column)
            {
            case 0:
                return Object.class;
            default:
                assert false;
            }
            return null;
        }

        public int getColumnCount()
        {
            return 1;
        }

        public String getColumnName(int column)
        {
            return "Value";
        }

        public Object getValueFor(Object node, int column)
        {
            PropNode n = (PropNode) node;
            return n.value;
        }

        public boolean isCellEditable(Object node, int column)
        {
            return false;
        }

        public void setValueFor(Object node, int column, Object value)
        {
            // do nothing for now
        }
    }

    private static class RenderData implements RenderDataProvider
    {
        public java.awt.Color getBackground(Object o)
        {
            return null;
        }

        public String getDisplayName(Object o)
        {
            return ((PropNode) o).name;
        }

        public java.awt.Color getForeground(Object o)
        {
            return null;
        }

        public javax.swing.Icon getIcon(Object o)
        {
            return null;
        }

        public String getTooltipText(Object o)
        {
            return "" + o; 
        }

        public boolean isHtmlDisplayName(Object o)
        {
            return false;
        }

    }
    
    private static class MyDefaultOutlineCellRenderer extends DefaultOutlineCellRenderer
    {

        @Override
        public void setIcon(Icon icon)
        {
            //Do nothing
        }
    }
    
    
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
      Map<String, Object> l = new HashMap<String, Object>();
      l.put("First", "First");
      l.put("Sec",  new JButton("Test"));
      // Object[] l = new Object[]{ "First", new JButton("Test")};
      //Object obj = new JButton("Test");
        ObjectInspector outline = new ObjectInspector(l);
        f.getContentPane().add(new JScrollPane(outline), BorderLayout.CENTER);

        f.setBounds(20, 20, 700, 400);
        f.setVisible(true);
    }

}
