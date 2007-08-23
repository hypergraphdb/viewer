package org.hypergraphdb.viewer.hg.test;


/**
 *
 * @author  User
 */
public class CompoundTestBean
{
    private TestBean inner;
    
    /** Creates a new instance of CompoundTestBean */
    public CompoundTestBean()
    {
    }
    
    public void setInner(TestBean inner)
    {
        this.inner = inner;
    }
    
    public TestBean getInner()
    {
        return inner;
    }
    
    public String toString()
    {
        return "CompoundTestBean_innerBean_" + inner;
    }
    
}
