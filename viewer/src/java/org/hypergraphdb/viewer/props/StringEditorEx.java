package org.hypergraphdb.viewer.props;

import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.beans.PropertyEditorSupport;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.Dimension;

public class StringEditorEx extends PropertyEditorSupport
{
    private String[] tags = null;
    private CustomPanel  editor;
    
    public StringEditorEx()
    {
        
    }
    
    //this way we can pass the tags for combobox representation
    public StringEditorEx(String[] _tags)
    {
        tags = _tags;
    }
    
    public String getAsText()
    {
        Object o = StringEditorEx.this.getValue();
        return (o!= null) ? o.toString() : "";
    }
    
    public Object getValue()
    {
    	if(editor != null)
    		return editor.getText();
    	return super.getValue();
    }
    
    public void setAsText(String text) throws IllegalArgumentException
    {
        StringEditorEx.this.setValue(text);
    }
    
    public String[] getTags()
    {
        return tags; 
    }
    
    public boolean supportsCustomEditor()
    {
        return true;
    }
    
    public Component getCustomEditor()
    {
        return editor = new CustomPanel(getAsText());
    }
    
    private class CustomPanel extends JPanel
    {
        private String text = "";
        JTextArea textArea = new JTextArea();
               
        
        public CustomPanel(String _text)
        {
            text = _text;
            try
            {
                jbInit();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
        String getText()
        {
        	return textArea.getText();
        }
        
        
        private void jbInit() throws Exception
        {
            textArea.setText(text);
                        
            textArea.addKeyListener(new java.awt.event.KeyListener()
            {
                public void keyTyped(KeyEvent e)
                {
                    StringEditorEx.this.setValue(textArea.getText());
                }
                public void keyPressed(KeyEvent e)
                {
                    StringEditorEx.this.setValue(textArea.getText());
                }
                public void keyReleased(KeyEvent e)
                {
                    StringEditorEx.this.setValue(textArea.getText());
                }
            });
             
            //JScrollPane pane = new JScrollPane();
            //pane.add(textArea);
            this.add(textArea);
            textArea.setPreferredSize(new Dimension (400, 400));
            this.setPreferredSize(new Dimension (400, 400));
        }
        
    }
}




