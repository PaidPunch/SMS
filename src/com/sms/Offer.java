package com.sms;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Offer extends HttpServlet 
{
    private static final long serialVersionUID = 3637588532737512970L;
    private String currentClassName;

    public Offer() 
    {  
        super();  
    }  
    
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
        String name = "Testing";
        request.setAttribute("name", name);
        request.getRequestDispatcher("/offer.jsp").forward(request, response);
    } 
}
