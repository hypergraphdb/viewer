package org.hypergraphdb.viewer.visual.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGStats;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.painter.DefaultNodePainterBeanInfo;
import com.l2fprod.common.demo.BeanBinder;
import com.l2fprod.common.model.DefaultBeanInfoResolver;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;

public class PainterPropsPanel extends PropertySheetPanel
{
	private static final Property[] EMPTY = new Property[0];
	private static final BeanInfo DEFAULT_BEAN_INFO = new DefaultNodePainterBeanInfo();
	private PropertyChangeListener listener = new MyPropertyChangeListener();
	private Object painter;
	
	public PainterPropsPanel()
	{
		setPreferredSize(new Dimension(250, 200));
		setDescriptionVisible(true);
		setSortingCategories(true);
		setSortingProperties(true);
		setRestoreToggleStates(true);
	}

	public void init()
	{
		removePropertySheetChangeListener(listener);
		if (painter != null)
		{
			//Thread.currentThread().setContextClassLoader(
			//	 AppConfig.getInstance().getClassLoader());
			//DefaultBeanInfoResolver res = new DefaultBeanInfoResolver(); 
			BeanInfo beanInfo = getBeanInfo(painter);
			if(beanInfo != null) {
			   setProperties(beanInfo.getPropertyDescriptors());
			   readFromObject(painter);
			}
			else
				setProperties(EMPTY);
		} else
			setProperties(EMPTY);
		
		addPropertySheetChangeListener(listener);
	}
	
	private BeanInfo getBeanInfo(Object o){
		try{
			ClassLoader cl = AppConfig.getInstance().getClassLoader();
			return (BeanInfo) cl.loadClass(o.getClass().getName() + "BeanInfo").newInstance();
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		return DEFAULT_BEAN_INFO;
	}

	public void readFromObject(Object data)
	{
		getTable().cancelEditing();
		PropertySheetTableModel model = (PropertySheetTableModel) getTable()
				.getModel();
		// System.out.println("readFromObject: " + data + ":" + model);
		if (model == null) return;
		Property[] properties = model.getProperties();
		int i = 0;
		for (int c = properties.length; i < c; i++)
		{
			// System.out.println("readFromObject0: " +
			// properties[i].getValue());
			properties[i].readFromObject(data);
			// System.out.println("readFromObject1: " +
			// properties[i].getValue());
		}
		// model.setProperties(new Property[0]);
		model.setProperties(properties);
		repaint();
	}

	public Object getPainter()
	{
		return painter;
	}

	public void setPainter(Object painter)
	{
		this.painter = painter;
		init();
	}
	
	class MyPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if(!(evt.getSource() instanceof Property)) return;
			Property prop = (Property) evt.getSource();
			try
			{
				prop.writeToObject(getPainter());
			}
			catch (RuntimeException e)
			{
				if (e.getCause() instanceof PropertyVetoException)
				{
					UIManager.getLookAndFeel().provideErrorFeedback(
							PainterPropsPanel.this);
					prop.setValue(evt.getOldValue());
				}
			}
		}
	}
}
