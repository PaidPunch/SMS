package com.server;

import org.apache.log4j.*;

public class SimpleLogger 
{
    private Logger logger;
    private static SimpleLogger singleton;
    
    // Private constructor
    private SimpleLogger() 
    {
        logger = Logger.getLogger(Constants.class.getName());
    }
    
    public void trace(String classname, String message)
    {
        String combined = classname + "|" + message;
        logger.trace(combined);
    }
    
    public void info(String classname, String message)
    {
        String combined = classname + "|" + message;
        logger.info(combined);
    }
    
    public void warn(String classname, String message)
    {
        String combined = classname + "|" + message;
        logger.warn(combined);
    }
    
    public void error(String classname, String message)
    {
        String combined = classname + "|" + message;
        logger.error(combined);
    }
    
    public void error(String classname, Exception e)
    {
        String combined = classname + "|" + e.getMessage();
        logger.error(combined);
    }
    
    // Common errors
    public void sessionMismatch(String currentClassName, String user_id)
    {
        error(currentClassName, "SessionMismatch|User_id:" + user_id);
    }

    public void unknownUser(String currentClassName, String user_id)
    {
        error(currentClassName, "UnknownUser|User_id:" + user_id);
    }
    
    // Singleton 
    public static synchronized SimpleLogger getInstance() 
    {
        if (singleton == null) 
        {
            singleton = new SimpleLogger();
        }
        return singleton;
    }
}
