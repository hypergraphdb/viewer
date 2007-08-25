package org.hypergraphdb.viewer;


/**
 * <p>
 * This class deals with all aspects of logging in webfaces, from debug traces
 * to status display, to error reporting. All code in WebFaces should use this class for all
 * tracing and exception reporting purposes.
 * </p>
 *
 * <p>
 * Exception reporting usually gives immediate feedback to the user about what happened.
 * Logging on the other hand stores the information in some persistent medium for future
 * reference. Thus a call to <code>handleException</code> will generally result in a
 * popup message for the end-user while a call to <code>error</code> or <code>log(ERROR,...)</code>
 * will record the exception in a log file. It is up to a concrete implementation of this class
 * whether handling an exception also logs it.
 * </p>
 * @author bolerio
 */
public class HGVLogger
{
    // For now, we are only logging and error reporting in Netbeans.
    private static HGVLogger instance; 
    
    /**
     * A constant specifying a log severity level of <code>debug</code>.
     */
    public static final int DEBUG = 0;
    
    /**
     * A constant specifying a log severity level of <code>information</code>.
     */
    public static final int INFORMATION = 1;
    
    /**
     * A constant specifying a log severity level of <code>warning</code>.
     */
    public static final int WARNING = 2;
    
    /**
     * A constant specifying a log severity level of <code>error</code>.
     */
    public static final int ERROR = 3;
    
    protected String severityToString(int severity)
    {
        switch (severity)
        {
            case DEBUG: return "debug";
            case INFORMATION: return "information";
            case WARNING: return "warning";
            case ERROR: return "error";
            default: return "<unkown severity>";
        }
    }
    
    protected int logging_severity = DEBUG;
    
    public static HGVLogger getInstance()
    {
        if(instance != null)
            return instance;
        return instance = new HGVLogger();
    }
    
     public void clearStatusText()
    {
    }
    
    public void handleException(Throwable exception)
    {
        exception.printStackTrace();
    }
    
    public void log(int severity, String message)
    {
        System.out.println("[" + severityToString(severity) + "] " + message);
    }
    
    public void setStatusText(String text)
    {
    }
    
    /**
     * <p>Return the current logging severity.</p>
     */
    public int getSeverity()
    {
        return logging_severity;
    }
    
    /**
     * <p>Set the logging severity for the logger. Any attempt to log something with
     * severity lower to the preset one will be ignored.</p>
     */
    public void setSeverity(int severity)
    {
        logging_severity = severity;
    }
    
    /**
     * <p>Return <code>true</code> if current logging severity is >= DEBUG and
     * <code>false</code> otherwise.</p>
     */
    public boolean isDebug()
    {
        return logging_severity >= DEBUG;
    }
    
    /**
     * <p>Log a <code>Throwable</code> with a given severity.</p>
     */
    public void log(int severity, Throwable t)
    {
        log(severity, t.toString());
    }
    
    /**
     * <p>Log a <code>Throwable</code> with default severity <code>ERROR</code>.</p>
     */
    public void exception(Throwable t)
    {
        log(ERROR, t);
    }
    
    /**
     * <p>Log a message with default severity code of <code>INFORMATION</code>.</p>
     */
    public void information(String msg)
    {
        log(INFORMATION, msg);
    }
    
    /**
     * <p>Log a message with default severity code of <code>INFORMATION</code>.</p>
     */
    public void debug(String msg)
    {
        log(DEBUG, msg);
    }
    
    /**
     * <p>Log a message with default severity code of <code>INFORMATION</code>.</p>
     */
    public void warning(String msg)
    {
        log(WARNING, msg);
    }
    
    /**
     * <p>Log a message with default severity code of <code>ERROR</code>.</p>
     */
    public void error(String msg)
    {
        log(ERROR, msg);
    }
}