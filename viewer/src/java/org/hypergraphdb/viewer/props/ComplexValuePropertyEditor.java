/*
 * Created on 2005-12-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hypergraphdb.viewer.props;

import java.awt.Component;
import java.beans.*;

import javax.swing.event.SwingPropertyChangeSupport;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.HGVKit;



public class ComplexValuePropertyEditor extends PropertyEditorSupport
{
      private Object obj;
      private PropertySetPanel panel;
      protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );
      
      public ComplexValuePropertyEditor()
      {
    	  
      }
      
      public ComplexValuePropertyEditor(Object obj)
      {
          this.obj = obj;
      }
      
      public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
  	    return pcs;
  	  }

      
      public void setValue(Object value)
      {
    	 // System.out.println("ComplexValuePropertyEditor - setValue: " + value);
    	  if(value == null)
    		  return;
    	  this.obj = value;
          ((PropertySetPanel) getCustomEditor()).setModelObject(obj);
      }
      
      public java.awt.Component getCustomEditor() 
      {
    	  if(panel == null)
    	  {
             panel = new PropertySetPanel();
             panel.setModelObject(obj);
             panel.getSwingPropertyChangeSupport().addPropertyChangeListener(
            		 new PropertyChangeListener(){
                    	public void propertyChange(PropertyChangeEvent evt) {
                    		 System.out.println("ComplexValuePropertyEditor - refire");
							pcs.firePropertyChange(evt);
						}
            		 });
    	  }
    	  return panel;
      }
      
      public Object getValue()
      {
    	  //if(panel != null)
    		  return obj;
      }

     public boolean supportsCustomEditor() {
    	  return true;
     }
       
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#getAsText()
     */
    
    public String getAsText()
    {
    	if(obj == null)
    		return "null";//complex value";
        return obj.getClass().getName().toString();
    }
    
    public void setAsText()
    {
    	//do nothing
    }
    
}