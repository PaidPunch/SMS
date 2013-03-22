package com.sms;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model.PrizeList;
import com.server.Constants;
import com.server.SimpleLogger;

public class Prizes extends LocalCoopServlet 
{
    private static final long serialVersionUID = 5776834999641032626L;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {          
        String prizeId = request.getParameter("prizeid");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        
        boolean success = PrizeList.getInstance().updatePrize(prizeId, phone, email);
        if (success)
        {
            response.getWriter().print("Prize claimed!");  
        }
        else
        {
            // Indicate failure
            response.setStatus(403);
            response.getWriter().print("Failed to claim prize!");  
        }
    } 
}
