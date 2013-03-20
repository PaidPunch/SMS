package com.sms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.server.Constants;
import com.server.SimpleDB;
import com.server.SimpleLogger;
import com.server.Utility;

public class Feedback extends LocalCoopServlet 
{
    private static final long serialVersionUID = -5616086565307974250L;
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
    
    private void insertNoThanksFeedback(HttpServletRequest request)
    {
        String offerid = request.getParameter("offerid");
        String type = request.getParameter("type");
        String feedback = request.getParameter("feedback");
        
        UUID feedbackId = UUID.randomUUID();
        
        String currentDatetime = Utility.getCurrentDatetimeInUTC();
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("offerid", offerid, true));
        listAttributes.add(new ReplaceableAttribute("type", type, true));
        listAttributes.add(new ReplaceableAttribute("feedback", feedback, true));
        listAttributes.add(new ReplaceableAttribute("version", "1.0", true));
        listAttributes.add(new ReplaceableAttribute("createdDatetime", currentDatetime, true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.FEEDBACK_DOMAIN, feedbackId.toString(), listAttributes);
    }
    
    @Override  
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {          
        insertNoThanksFeedback(request);
        
        response.getWriter().print("Feedback received!");  
    } 
    
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {            
            request.setAttribute("offercode", codeString);
            
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
        }
    } 
}
