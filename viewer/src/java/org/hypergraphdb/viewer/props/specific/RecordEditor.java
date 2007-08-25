package org.hypergraphdb.viewer.props.specific;

import java.beans.PropertyEditorSupport;
import org.hypergraphdb.viewer.props.PropertySetPanel;

public class RecordEditor extends PropertyEditorSupport
{
	 private Object obj;
     private PropertySetPanel panel;
     public RecordEditor()
     {
   	  
     }
     
     public RecordEditor(Object obj)
     {
         this.obj = obj;
     }
     
     public void setValue(Object value)
     {
   	     this.obj = value;
         //System.out.println("ComplexValuePropertyEditor - setValue: " + value);
         ((PropertySetPanel) getCustomEditor()).setModelObject(obj);
     }
     
     public java.awt.Component getCustomEditor() 
     {
   	     if(panel == null)
   	     {
            panel = new PropertySetPanel();
            panel.setModelObject(obj);
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
   		return "complex value";
       return obj.getClass().getName().toString();
   }
}
