package org.hypergraphdb.viewer.hg;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGStats;
import org.hypergraphdb.atom.HGSubsumes;
import org.hypergraphdb.query.AtomTypeCondition;
import org.hypergraphdb.query.HGQueryCondition;
import org.hypergraphdb.type.BonesOfBeans;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.AppConfigPanel;
import org.hypergraphdb.viewer.view.HGVNetworkView;

/**
 *
 * @author  User
 */
public class Test
{
    
    private static JPanel createPanelProperties(Object dc)
    {
        javax.swing.JTable tabProperties_ = new javax.swing.JTable();
        CommonPropertiesTableModel0 pModel_ = new CommonPropertiesTableModel0();
        pModel_.set(dc);
        tabProperties_.setModel(pModel_);
        tabProperties_.setRowHeight(tabProperties_.getRowHeight() + 4);
        JPanel panel = new JPanel();
        panel.add(tabProperties_);
        return panel;
    }
    
    public static void main(String[] args)
    {
       
        JFrame fr = new JFrame("Test");
        Object dc = new String("dd") ;
        //fr.getContentPane().add(createPanelProperties(dc));
        HyperGraph hg = new HyperGraph("E:/temp/xxx");
        HGHandle h = hg.getTypeSystem().getTypeHandle(HGStats.class);
        h = hg.getPersistentHandle(h);
        AtomTypeCondition cond = new AtomTypeCondition(HGSubsumes.class);
       // HGVNetworkView view = HGViewer.getStandaloneView(hg, h, 3, cond);
        //view.redrawGraph();
        //fr.getContentPane().add(view.getComponent());
        fr.getContentPane().add(new AppConfigPanel());
        fr.setSize(600,600);
        //fr.setSize(120,60);
        fr.setVisible(true);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //HyperGraph hg = new HyperGraph("XXX12");
        //HGUtils.testSlotRecords(hg);
    }
    
    public static class CommonPropertiesTableModel0  extends AbstractTableModel
    {
        protected final String[] COLUMNNAMES =
        {"property", "value"};
        protected final int PROPERTYCOLUMN = 0;
        protected final int VALUECOLUMN = 1;
        
        private final SortedMap data_ = new TreeMap();
        private final List keys_ = new ArrayList();
        /**Map key (property name) to property descriptor.*/
        private final Map keyToDescr_ = new HashMap();
        
        /**The component which properties are currently edited.*/
        private Object dc_;
        /***/
        private final Map rowToEditor_=new HashMap();
        
        
        
        /***/
        public final void clear()
        {
            dc_ = null;
            data_.clear();
            keys_.clear();
            keyToDescr_.clear();
        }
        
        /***/
        public final void set(final Object dcSel)
        {
            clear();
            dc_ = dcSel;
            Map pds = BonesOfBeans.getAllPropertyDescriptors(dcSel.getClass());
            final PropertyDescriptor[] pd = (PropertyDescriptor[]) pds.values().toArray(new PropertyDescriptor[pds.size()]);
            try
            {
                for (int i = 0; i < pd.length; i++)
                {
                    final String key = pd[i].getName();
                    final Method readMethod = pd[i].getReadMethod();
                    if(readMethod != null)
                    {
                        final Object value = readMethod.invoke(dcSel, (Object[])null);
                        data_.put(key, value);
                        keyToDescr_.put(key, pd[i]);
                    }
                }
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
            
            keys_.addAll(data_.keySet());
            fireTableDataChanged();
        }
        
        
        public int getRowCount()
        {
            return data_.size();
        }
        
        
        public Object getValueAt(int row, int col)
        {
            Object result = null;
            
            if (col == VALUECOLUMN)
            {
                final Object key = keys_.get(row);
                result = data_.get(key);
            }
            else if (col == PROPERTYCOLUMN)
            {
                result = (String)keys_.get(row);
            }
            
            return result;
        }
        
        public int getColumnCount()
        {
            return COLUMNNAMES.length;
        }
        
        public String getColumnName(int col)
        {
            return COLUMNNAMES[col];
        }
        
        public PropertyEditor getPropertyEditor(final int row)
        {
            return null;
        }
        
    }
 
}
