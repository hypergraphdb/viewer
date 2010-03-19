package org.hypergraphdb.viewer.props;

import java.awt.Dimension;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.SwingPropertyChangeSupport;

import org.hypergraphdb.type.Slot;

/**
 * 
 * @author User
 */
public class PropertySetPanel extends JScrollPane
{
	private JTable tabProperties_;
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
		tabProperties_ = new JTable();
		cell_editor = new PropertyCellEditor();
		PropertyDialogManager.setFrame(frame);
		setViewportView(tabProperties_);
		setPreferredSize(new Dimension(181, 300));
	}

	public void setModelObject(Object obj)
	{
		model_obj = obj;
		if (obj == null) return;
		
		PropertiesTableModel model = PropertyModelFactory.getModel(obj.getClass());
		if(model == null)
			model = new PropertiesTableModel();
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
		tabProperties_.setModel(model);
	}
	
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
	    return pcs;
	  }

	public static final void registerPropertyEditors()
	{
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
		
	}

	private static final Class<?> getKlass(String cls)
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

}