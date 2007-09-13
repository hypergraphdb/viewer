package org.hypergraphdb.viewer.props;

import java.awt.Dimension;
import java.awt.Frame;
import java.beans.*;
import java.lang.reflect.Method;
import java.util.*;
import javax.swing.event.*;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import org.hypergraphdb.type.BonesOfBeans;
import org.hypergraphdb.type.Record;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.type.Slot;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.props.editors.ArrayListModel;

/**
 * 
 * @author User
 */
public class PropertySetPanel extends JScrollPane
{
	private JTable tabProperties_;
	private PropertiesTableModel pModel_;
	private Object model_obj;
	private static PropertyCellEditor cell_editor; 
	private static boolean editorsRegistered;
	static
	{
		registerPropertyEditors();
	}
	
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

	public PropertySetPanel(){
		this(null);
	}
	/** Creates a new instance of PropsPanel */
	public PropertySetPanel(Frame frame)
	{
		tabProperties_ = new javax.swing.JTable();
		cell_editor = new PropertyCellEditor();
		PropertyDialogManager.setFrame(frame);
		// setMinimumSize(new java.awt.Dimension(70, 70));
		// pModel_ = new PropertiesTableModel();
		// tabProperties_.setModel(pModel_);
		// tabProperties_.setRowHeight(tabProperties_.getRowHeight() + 4);
		// tabProperties_.getColumn(COLUMNNAMES[0]).setPreferredWidth(90);
		// tabProperties_.getColumn(COLUMNNAMES[1]).setPreferredWidth(100);
		// tabProperties_.getColumn("value").setCellEditor(cell_editor);
		setViewportView(tabProperties_);
		setPreferredSize(new Dimension(181, 300));
	}

	public void setModelObject(Object obj)
	{
		model_obj = obj;
		if (obj == null) return;
		
		PropertiesTableModel model = PropertyModelFactory.getModel(obj.getClass());
		if(model == null)
			model = new GeneralPropertiesTableModel();
		model.create(obj);
		tabProperties_.setModel(model);
		for(int i = 0; i < tabProperties_.getColumnCount(); i++)
		{
        	tabProperties_.getColumnModel().getColumn(i).setCellEditor(cell_editor);
        	tabProperties_.getColumnModel().getColumn(i).setCellRenderer(cell_editor);
        }
		
        model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev)
			{
				//System.out.println("PropertySetPanel - value changed: " + model_obj);
				pcs.firePropertyChange(ev);
			}
		});
	}
	
	public Object getModelObject()
	{
		return model_obj;
	}

	public void setModel(PropertiesTableModel model)
	{
		// pModel_ = model;
		tabProperties_.setModel(model);
	}
	
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
	    return pcs;
	  }

	public static final void registerPropertyEditors()
	{
		// issue 31879
		if (editorsRegistered) return;
		String[] syspesp = PropertyEditorManager.getEditorSearchPath();
		String[] nbpesp = new String[] { "org.hypergraphdb.viewer.props.editors", // NOI18N
		};
		String[] allpesp = new String[syspesp.length + nbpesp.length];
		System.arraycopy(nbpesp, 0, allpesp, 0, nbpesp.length);
		System.arraycopy(syspesp, 0, allpesp, nbpesp.length, syspesp.length);
		PropertyEditorManager.setEditorSearchPath(allpesp);
		PropertyEditorManager.registerEditor(java.lang.Character.TYPE,
				getKlass("org.hypergraphdb.viewer.props.editors.CharEditor")); // NOI18N
		PropertyEditorManager
				.registerEditor(
						getKlass("[Ljava.lang.String;"),
						getKlass("org.hypergraphdb.viewer.props.editors.StringArrayEditor")); // NOI18N
		PropertyEditorManager.registerEditor(Integer.TYPE,
				getKlass("org.hypergraphdb.viewer.props.editors.IntEditor"));
		PropertyEditorManager.registerEditor(Boolean.TYPE,
				getKlass("org.hypergraphdb.viewer.props.editors.BoolEditor"));
		editorsRegistered = true;
		
		PropertyModelFactory.registerModel(ArrayList.class, new ArrayListModel());
		
		//TODO:??? MOVE AWAY
		//PropertyModelFactory.registerModel(Record.class, new RecordTableModel());
		//PropertyModelFactory.registerModel(Slot.class, new SlotTableModel());
	}

	private static final Class getKlass(String cls)
	{
		try
		{
			return Class.forName(cls, false, PropertySetPanel.class
					.getClassLoader());
		}
		catch (ClassNotFoundException e)
		{
			throw new NoClassDefFoundError(e.getLocalizedMessage());
		}
	}

	static class SlotTableModel extends PropertiesTableModel
	{
		private Slot slot;

		public SlotTableModel()
		{
		}

		public AbstractProperty[][] getData()
		{
			this.slot = (Slot) bean;
			AbstractProperty[][] data0 = new AbstractProperty[2][2];
			data0[0][0] = new ReadOnlyProperty("label");
			data0[0][1] = new ReadOnlyProperty("valueType");
			data0[1][0] = new ReadOnlyProperty(slot.getLabel());
			data0[1][1] = new ReadOnlyProperty(slot.getValueType().getClass()
					.getName());
			return data0;
		}
	}
}