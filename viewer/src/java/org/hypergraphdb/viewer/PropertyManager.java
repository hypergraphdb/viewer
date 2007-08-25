package org.hypergraphdb.viewer;

import java.io.*;
import java.util.*;

class PropertyManager
{
    private Properties system = new Properties();
    private Properties user = new Properties();
    
    Properties getProperties()
    {
        Properties total = new Properties();
        total.putAll(system);
        total.putAll(user);
        return total;
    }
    
    void loadProps(InputStream in)
    throws IOException
    {
        loadProps(system,in);
    }
    
    
    void saveProps(OutputStream out)
    throws IOException
    {
    	getProperties().store(out,"HGViewer properties");
        out.close();
    }
    
    String getProperty(String name)
    {
        String value = user.getProperty(name);
        if(value != null)
            return value;
        else
            return getDefaultProperty(name);
    }
    
    void setProperty(String name, String value)
    {
        String prop = getDefaultProperty(name);
        
                /* if value is null:
                 * - if default is null, unset user prop
                 * - else set user prop to ""
                 * else
                 * - if default equals value, ignore
                 * - if default doesn't equal value, set user
                 */
        if(value == null)
        {
            if(prop == null || prop.length() == 0)
                user.remove(name);
            else
                user.put(name,"");
        }
        else
        {
            if(value.equals(prop))
                user.remove(name);
            else
                user.put(name,value);
        }
    }
    
    public void setTemporaryProperty(String name, String value)
    {
        user.remove(name);
        system.put(name,value);
    }
    
    void unsetProperty(String name)
    {
        if(getDefaultProperty(name) != null)
            user.put(name,"");
        else
            user.remove(name);
    }
    
    public void resetProperty(String name)
    {
        user.remove(name);
    }
    
    
    private String getDefaultProperty(String name)
    {
        return system.getProperty(name);
    } 
    
    private void loadProps(Properties into, InputStream in)
    throws IOException
    {
        try
        {
            into.load(in);
        }
        finally
        {
            in.close();
        }
    }
    
}

