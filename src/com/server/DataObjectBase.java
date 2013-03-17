package com.server;

import java.util.Date;

public class DataObjectBase 
{
    protected String currentClassName;
    protected long refreshInterval;
    protected Date lastRefreshTime;
    
    protected boolean timeForRefresh()
    {
        if (lastRefreshTime != null)
        {
            Date currentTime = new Date();
            Date refreshTime = new Date(lastRefreshTime.getTime() + refreshInterval);
            return (currentTime.compareTo(refreshTime) > 0);    
        }
        else
        {
            return true;
        }
    }
}
