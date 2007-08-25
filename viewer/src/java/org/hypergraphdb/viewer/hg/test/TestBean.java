package org.hypergraphdb.viewer.hg.test;
import java.util.Collection;
import java.util.HashSet;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author  User
 */
public class TestBean
{
    private String str;
    private HashSet<String> mrufs = new HashSet<String>(10);
       
    public String[] getStringMrufs()
	{
		return mrufs.toArray(new String[mrufs.size()]);
	}

	public void setStringMrufs(String[] mrufs1)
	{
		for(String s: mrufs1)
		   mrufs.add(s);
	}


	/** Creates a new instance of TestBean */
    public TestBean()
    {
    }
    
    
    public String getStr()
    {
        return str;
    }
    
    public void setStr(String str)
    {
        this.str=str;
    }
    
    public String toString()
    {
        return "testBean_" + str + "_" + intT + "_" + getStringMrufs();
    }
    
    private int intT;
    public int getIntT()
    {
        return this.intT;
    }    
    
    public void setIntT(int intT)
    {
        this.intT = intT;
    }


	
	public void addMruf(String m)
	{
		mrufs.add(m);
	} 
	
	    
    /*
     public boolean equals(Object other)
    {
        if (! (other instanceof TestBean))
            return false;
        else
        {
            TestBean otherB = (TestBean)other;
            return str.equals(otherB.str) && (intT == otherB.intT);
        }
    }
    */
}
