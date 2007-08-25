/*
 * BeanShell.java - BeanShell scripting support
 *
 * Copyright (C) 2000, 2004 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.hypergraphdb.viewer.beanshell;

import bsh.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.ref.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.hypergraphdb.viewer.util.*;
import org.hypergraphdb.viewer.*;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.dialogs.ShellPanel;


/**
 * BeanShell is based on jEdit's extension language.<p>
 *
 * When run from HGViewer, BeanShell code has access to the following predefined
 * variables:
 *
 * <ul>
 *<li><code>hg</code> - the currently active {@link HyperGraph}.</li>
 * <li><code>network</code> - the currently active {@link Network}.</li>
 * <li><code>networkView</code> - the currently active {@link NetworkView}.</li>
 * <li><code>scriptPath</code> - the path name of the currently executing
 * BeanShell script.</li>
 * </ul>
 *
 * @author Slava Pestov
 * @version $Id: BeanShell.java,v 1.3 2006/02/27 19:59:19 bizi Exp $
 */
public class BeanShell
{
    private static final String REQUIRED_VERSION = "2.0b1.1-jedit-1";
    
    private static final String LABEL_TEXT = "Variables:\nview - The current view instance\nnetwork - The current network instance\nhg - The current HyperGraph instance\n\n";
    
    /**
     * Prompts for a BeanShell expression to evaluate.
     */
    public static void showEvaluateDialog()
    {
        ShellPanel panel = new ShellPanel(
        		GUIUtilities.createMultilineLabel(LABEL_TEXT), ""); 
        DialogDescriptor d = new DialogDescriptor(null, panel, "Evaluate BeanShell Expression");
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
        {
            String command = panel.getEnteredText();
       
            if(!command.endsWith(";"))
                command = command + ";";
            
            int repeat = 1;//view.getInputHandler().getRepeatCount();
            
            Object returnValue = null;
            try
            {
                for(int i = 0; i < repeat; i++)
                {
                    returnValue = _eval(global,command);
                }
            }
            catch(Throwable e)
            {
                Log.log(Log.ERROR,BeanShell.class,e);
                handleException(null,e);
            }
            
            if(returnValue != null)
            {
                String[] args =
                { returnValue.toString() };
                JOptionPane.showMessageDialog(GUIUtilities.getFrame(),
                        args, "BeanShell Evaluation",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    
    /**
     * Runs a BeanShell script. Errors are shown in a dialog box.<p>
     *
     * If the <code>in</code> parameter is non-null, the script is
     * read from that stream; otherwise it is read from the file identified
     * by <code>path</code>.<p>
     *
     * The <code>scriptPath</code> BeanShell variable is set to the path
     * name of the script.
     *
     * @param path The script file's VFS path.
     * @param in The reader to read the script from, or <code>null</code>.
     * @param ownNamespace If set to <code>false</code>, methods and
     * variables defined in the script will be available to all future
     * uses of BeanShell; if set to <code>true</code>, they will be lost as
     * soon as the script finishes executing. jEdit uses a value of
     * <code>false</code> when running startup scripts, and a value of
     * <code>true</code> when running all other macros.
     *
     */
    public static void runScript(String path, Reader in,  boolean ownNamespace)
    {
        try
        {
            _runScript(path,in,ownNamespace);
        }
        catch(Throwable e)
        {
            Log.log(Log.ERROR,BeanShell.class,e);
            
            handleException(path,e);
        }
    } 
    
    /**
     * Runs a BeanShell script. Errors are shown in a dialog box.<p>
     *
     * If the <code>in</code> parameter is non-null, the script is
     * read from that stream; otherwise it is read from the file identified
     * by <code>path</code>.<p>
     *
     * The <code>scriptPath</code> BeanShell variable is set to the path
     * name of the script.
     *
     * @param path The script file's VFS path.
     * @param in The reader to read the script from, or <code>null</code>.
     * @param namespace The namespace to run the script in.
     *
     */
    public static void runScript(String path, Reader in,
    NameSpace namespace)
    {
        try
        {
            _runScript(path,in,namespace);
        }
        catch(Throwable e)
        {
            Log.log(Log.ERROR,BeanShell.class,e);
            
            handleException(path,e);
        }
    } 
    
    /**
     * Runs a BeanShell script. Errors are passed to the caller.<p>
     *
     * If the <code>in</code> parameter is non-null, the script is
     * read from that stream; otherwise it is read from the file identified
     * by <code>path</code>.<p>
     *
     * The <code>scriptPath</code> BeanShell variable is set to the path
     * name of the script.
     *
     * @param path The script file's VFS path.
     * @param in The reader to read the script from, or <code>null</code>.
     * @param ownNamespace If set to <code>false</code>, methods and
     * variables defined in the script will be available to all future
     * uses of BeanShell; if set to <code>true</code>, they will be lost as
     * soon as the script finishes executing. jEdit uses a value of
     * <code>false</code> when running startup scripts, and a value of
     * <code>true</code> when running all other macros.
     * @exception Exception instances are thrown when various BeanShell errors
     * occur
     */
    public static void _runScript(String path, Reader in,
    boolean ownNamespace) throws Exception
    {
        _runScript(path,in,ownNamespace
        ? new NameSpace(global,"script namespace")
        : global);
    } 
    
    /**
     * Runs a BeanShell script. Errors are passed to the caller.<p>
     *
     * If the <code>in</code> parameter is non-null, the script is
     * read from that stream; otherwise it is read from the file identified
     * by <code>path</code>.<p>
     *
     * The <code>scriptPath</code> BeanShell variable is set to the path
     * name of the script.
     *
     * @param path The script file's VFS path.
     * @param in The reader to read the script from, or <code>null</code>.
     * @param namespace The namespace to run the script in.
     * @exception Exception instances are thrown when various BeanShell errors
     * occur
     */
    public static void _runScript(String path, Reader in,
    NameSpace namespace) throws Exception
    {
        Log.log(Log.MESSAGE,BeanShell.class,"Running script " + path);
        
        Interpreter interp = createInterpreter(namespace);
        
        Object session = null;
        try
        {
            if(in == null)
            {
                /*
                Buffer buffer = jEdit.openTemporary(null,
                null,path,false);
                VFSManager.waitForRequests();
                
                in = new StringReader(buffer.getText(0,
                buffer.getLength()));
                 */
                return;
            }
            
            setupDefaultVariables(namespace);
            interp.set("scriptPath",path);
            running = true;
            interp.eval(in,namespace,path);
        }
        catch(Exception e)
        {
            unwrapException(e);
        }
        finally
        {
            running = false;
            try
            {
                // no need to do this for macros!
                if(namespace == global)
                {
                    resetDefaultVariables(namespace);
                    interp.unset("scriptPath");
                }
            }
            catch(EvalError e)
            {
                // do nothing
            }
        }
    } 
    
    /**
     * Evaluates the specified BeanShell expression. Errors are reported in
     * a dialog box.
     * @param namespace The namespace
     * @param command The expression
     */
    public static Object eval(NameSpace namespace, String command)
    {
        try
        {
            return _eval(namespace,command);
        }
        catch(Throwable e)
        {
            Log.log(Log.ERROR,BeanShell.class,e);
            
            handleException(null,e);
        }
        
        return null;
    } 
    
    /**
     * Evaluates the specified BeanShell expression. Unlike
     * <code>eval()</code>, this method passes any exceptions to the caller.
     *
     * @param namespace The namespace
     * @param command The expression
     * @exception Exception instances are thrown when various BeanShell
     * errors occur
     */
    public static Object _eval(NameSpace namespace, String command)
    throws Exception
    {
        Interpreter interp = createInterpreter(namespace);
        
        try
        {
            setupDefaultVariables(namespace);
            //if(Debug.BEANSHELL_DEBUG)
            //    Log.log(Log.DEBUG,BeanShell.class,command);
            return interp.eval(command);
        }
        catch(Exception e)
        {
            unwrapException(e);
            // never called
            return null;
        }
        finally
        {
            try
            {
                resetDefaultVariables(namespace);
            }
            catch(UtilEvalError e)
            {
                // do nothing
            }
        }
    } 
    
    /**
     * Caches a block of code, returning a handle that can be passed to
     * runCachedBlock().
     * @param id An identifier. If null, a unique identifier is generated
     * @param code The code
     * @param namespace If true, the namespace will be set
     * @exception Exception instances are thrown when various BeanShell errors
     * occur
     */
    public static BshMethod cacheBlock(String id, String code, boolean namespace)
    throws Exception
    {
        String name = "__internal_" + id;
        
        // evaluate a method declaration
        if(namespace)
        {
            _eval(global,name + "(ns) {\nthis.callstack.set(0,ns);\n" + code + "\n}");
            return global.getMethod(name,new Class[]
            { NameSpace.class });
        }
        else
        {
            _eval(global,name + "() {\n" + code + "\n}");
            return global.getMethod(name,new Class[0]);
        }
    }
    
    /**
     * Runs a cached block of code in the specified namespace. Faster than
     * evaluating the block each time.
     * @param method The method instance returned by cacheBlock()
     * @param namespace The namespace to run the code in
     * @exception Exception instances are thrown when various BeanShell
     * errors occur
     */
    public static Object runCachedBlock(BshMethod method, NameSpace namespace) throws Exception
    {
        boolean useNamespace;
        if(namespace == null)
        {
            useNamespace = false;
            namespace = global;
        }
        else
            useNamespace = true;
        
        try
        {
            setupDefaultVariables(namespace);
            
            Object retVal = method.invoke(useNamespace
            ? new Object[]
            { namespace }
            : NO_ARGS,
            interpForMethods,new CallStack());
            if(retVal instanceof Primitive)
            {
                if(retVal == Primitive.VOID)
                    return null;
                else
                    return ((Primitive)retVal).getValue();
            }
            else
                return retVal;
        }
        catch(Exception e)
        {
            unwrapException(e);
            // never called
            return null;
        }
        finally
        {
            resetDefaultVariables(namespace);
        }
    } 
    
    /**
     * Returns if a BeanShell script or macro is currently running.
     */
    public static boolean isScriptRunning()
    {
        return running;
    } 
    
    /**
     * Returns the global namespace.
     */
    public static NameSpace getNameSpace()
    {
        return global;
    } 
   
    
    static void init()
    {
        try
        {
            NameSpace.class.getMethod("addCommandPath",
            new Class[]
            { String.class, Class.class });
        }
        catch(Exception e)
        {
            Log.log(Log.ERROR,BeanShell.class,"You have BeanShell version " + getVersion() + " in your CLASSPATH.");
            Log.log(Log.ERROR,BeanShell.class,"Please remove it from the CLASSPATH since HGViewer can only run with the bundled BeanShell version " + REQUIRED_VERSION);
            System.exit(1);
        }
        
        classManager = new CustomClassManager();
        classManager.setClassLoader(new JARClassLoader());
        
        global = new NameSpace(classManager,
        "HGViewer embedded BeanShell interpreter");
        global.importPackage("org.hypergraphdb");
        global.importPackage("org.hypergraphdb.handle");
        global.importPackage("org.hypergraphdb.type");
        global.importPackage("org.hypergraphdb.query");
        global.importPackage("org.hypergraphdb.storage");
        global.importPackage("org.hypergraphdb.handle");
        global.importPackage("org.hypergraphdb.viewer");
        global.importPackage("org.hypergraphdb.viewer.util");
        global.importPackage("org.hypergraphdb.viewer.actions");
        global.importPackage("org.hypergraphdb.viewer.beanshell");
        global.importPackage("org.hypergraphdb.viewer.dialogs");
        global.importPackage("org.hypergraphdb.viewer.hg");
        global.importPackage("org.hypergraphdb.viewer.util");
        global.importPackage("org.hypergraphdb.viewer.visual");
        global.importPackage("org.hypergraphdb.viewer.view");
        global.importPackage("giny.model");
        global.importPackage("giny.view");
        global.importPackage("phoebe");
        
        interpForMethods = createInterpreter(global);
    } 
    
    /**
     * Causes BeanShell internal structures to drop references to cached
     * Class instances.
     */
    static void resetClassManager()
    {
        classManager.reset();
    } 
    
    
    private static final Object[] NO_ARGS = new Object[0];
    private static CustomClassManager classManager;
    private static Interpreter interpForMethods;
    private static NameSpace global;
    private static boolean running;
    
    private static void setupDefaultVariables(NameSpace namespace)
    throws UtilEvalError
    {
        namespace.setVariable("view", HGViewer.getCurrentView());
        namespace.setVariable("network",HGViewer.getCurrentNetwork());
        namespace.setVariable("hg",HGViewer.getCurrentNetwork().getHyperGraph());
    }
    
    private static void resetDefaultVariables(NameSpace namespace)
    throws UtilEvalError
    {
        namespace.setVariable("view", null);
        namespace.setVariable("network", null);
        namespace.setVariable("hg", null);
    }
    
    /**
     * This extracts an exception from a 'wrapping' exception, as BeanShell
     * sometimes throws. This gives the user a more accurate error traceback
     */
    private static void unwrapException(Exception e) throws Exception
    {
        if(e instanceof TargetError)
        {
            Throwable t = ((TargetError)e).getTarget();
            if(t instanceof Exception)
                throw (Exception)t;
            else if(t instanceof Error)
                throw (Error)t;
        }
        
        if(e instanceof InvocationTargetException)
        {
            Throwable t = ((InvocationTargetException)e).getTargetException();
            if(t instanceof Exception)
                throw (Exception)t;
            else if(t instanceof Error)
                throw (Error)t;
        }
        
        throw e;
    } 
    
    private static void handleException(String path, Throwable t)
    {
    	Component c = (HGViewer.getCurrentView()!= null) ?
    			HGViewer.getCurrentView().getComponent() : null;
       
        if(t instanceof IOException)
        {
        	 GUIUtilities.error(c, path, "",
            new String[]
            { t.toString() });
            JOptionPane.showMessageDialog(c,
                    path, "BeanShell Error", JOptionPane.ERROR_MESSAGE, 
                    UIManager.getIcon("OptionPane.errorIcon"));
        }
        else
            new BeanShellErrorDialog(GUIUtilities.getFrame(c),t);
    } 
    
    private static Interpreter createInterpreter(NameSpace nameSpace)
    {
        return new Interpreter(null,System.out,System.err,false,nameSpace);
    } 
    
    private static String getVersion()
    {
        try
        {
            return (String)Interpreter.class.getField("VERSION").get(null);
        }
        catch(Exception e)
        {
            return "unknown";
        }
    } 
    
    
    static class CustomClassManager extends BshClassManager
    {
        private LinkedList listeners = new LinkedList();
        private ReferenceQueue refQueue = new ReferenceQueue();
        
        // copy and paste from bsh/classpath/ClassManagerImpl.java...
        public synchronized void addListener( Listener l )
        {
            listeners.add( new WeakReference( l, refQueue) );
            
            // clean up old listeners
            Reference deadref;
            while ( (deadref = refQueue.poll()) != null )
            {
                boolean ok = listeners.remove( deadref );
                if ( ok )
                {
                    ;//System.err.println("cleaned up weak ref: "+deadref);
                }
                else
                {
                    if ( Interpreter.DEBUG ) Interpreter.debug(
                    "tried to remove non-existent weak ref: "+deadref);
                }
            }
        }
        
        public void removeListener( Listener l )
        {
            throw new Error("unimplemented");
        }
        
        public void reset()
        {
            classLoaderChanged();
        }
        
        protected synchronized void classLoaderChanged()
        {
            // clear the static caches in BshClassManager
            clearCaches();
            
            for (Iterator iter = listeners.iterator();
            iter.hasNext(); )
            {
                WeakReference wr = (WeakReference)
                iter.next();
                Listener l = (Listener)wr.get();
                if ( l == null )  // garbage collected
                    iter.remove();
                else
                    l.classLoaderChanged();
            }
        }
    } 
 
}
