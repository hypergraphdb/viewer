package org.hypergraphdb.viewer.visual.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.visual.VisualStyle;

/**
 * List model representing all defined VisualStyles  
 */
public class VisualStylesComboModel extends AbstractListModel 
   implements MutableComboBoxModel, Serializable {

	protected Vector objects;
	protected Object selectedObject;

	public VisualStylesComboModel() {
		populate();
	}
	
	 // implements javax.swing.ListModel
    public Object getElementAt(int index) {
        if ( index >= 0 && index < objects.size() )
            return objects.elementAt(index);
        else
            return null;
    }

    public int getIndexOf(Object anObject) {
        return objects.indexOf(anObject);
    }

    // implements javax.swing.MutableComboBoxModel
    public void addElement(Object anObject) {
        objects.addElement(anObject);
        fireIntervalAdded(this,objects.size()-1, objects.size()-1);
        if ( objects.size() == 1 && selectedObject == null && anObject != null ) {
            setSelectedItem( anObject );
        }
    }

    // implements javax.swing.MutableComboBoxModel
    public void insertElementAt(Object anObject,int index) {
        objects.insertElementAt(anObject,index);
        fireIntervalAdded(this, index, index);
    }

    // implements javax.swing.MutableComboBoxModel
    public void removeElementAt(int index) {
        if ( getElementAt( index ) == selectedObject ) {
            if ( index == 0 ) {
                setSelectedItem( getSize() == 1 ? null : getElementAt( index + 1 ) );
            }
            else {
                setSelectedItem( getElementAt( index - 1 ) );
            }
        }
        objects.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }

    // implements javax.swing.MutableComboBoxModel
    public void removeElement(Object anObject) {
        int index = objects.indexOf(anObject);
        if ( index != -1 ) {
            removeElementAt(index);
        }
    }

    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if ( objects.size() > 0 ) {
            int firstIndex = 0;
            int lastIndex = objects.size() - 1;
            objects.removeAllElements();
	    selectedObject = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        } else {
	    selectedObject = null;
	}
    }

	public int getSize() {
		return objects.size();
	}
	
	public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals( anObject )) ||
	    selectedObject == null && anObject != null) {
	    selectedObject = anObject;
	    fireContentsChanged(this, -1, -1);
        }
    }

    public Object getSelectedItem() {
        return selectedObject;
    }
    
    private void populate(){
    	Collection<VisualStyle> styles = VisualManager.getInstance().getVisualStyles();
		objects = new Vector<VisualStyle>(styles.size());
		for(VisualStyle vs: styles)
			objects.add(vs);
    }
	
}
