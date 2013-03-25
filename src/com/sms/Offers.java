package com.sms;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.model.OfferList;
import com.server.Constants;
import com.server.SimpleLogger;
import com.server.Utility;

public class Offers extends LocalCoopServlet 
{
    private static final long serialVersionUID = -5904600460559949891L;
    private String currentClassName;
    
    @Override
    public void init(ServletConfig config) throws ServletException
    {
       super.init(config);
       currentClassName = Processor.class.getSimpleName();

       try
       {
           ServletContext context = config.getServletContext();
           Constants.init(context);
       }
       catch(Exception e)
       {
           SimpleLogger.getInstance().error(currentClassName, e.getMessage());
       }
    }
    
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        JSONArray arrJSON = OfferList.getInstance().getOffersArray();
        Utility.jsonResponse(request, response, arrJSON);
    }
}
