package com.sms;

import com.model.*;
import com.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class Redeem extends LocalCoopServlet 
{
    private static final long serialVersionUID = 5428715563522558084L;

    public Redeem() 
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
    
    private void recordRedeemView(String offerId)
    {
     // Get UUID for naming new suggestion
        UUID itemName = UUID.randomUUID();
        
        String currentDatetime = Utility.getCurrentDatetimeInUTC();
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("version", "1.0", true));
        listAttributes.add(new ReplaceableAttribute("offerId", offerId, true));
        listAttributes.add(new ReplaceableAttribute("createdDatetime", currentDatetime, true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.REDEEMRECORD_DOMAIN, itemName.toString(), listAttributes);
    }
      
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {
            recordRedeemView(codeString);
            
            HashMap<String,String> offerInfo = getOfferInfo(codeString);
            if (offerInfo != null)
            {
                Business currentBiz = getBusiness(offerInfo.get("version"), offerInfo.get("offerBizCode"));
                
                if (currentBiz != null)
                {
                    setResponseAttributes(request, currentBiz, offerInfo);
                    
                    request.getRequestDispatcher("/redeem.jsp").forward(request, response);    
                }
                else
                {
                    request.setAttribute("error_message", "Redeem code invalid or expired");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);  
                }      
            }
            else
            {
                request.setAttribute("error_message", "Redeem code invalid or expired");
                request.getRequestDispatcher("/error.jsp").forward(request, response);  
            }  
        }
        else
        {
            request.setAttribute("error_message", "Redeem code missing");
            request.getRequestDispatcher("/error.jsp").forward(request, response);  
        }
    } 
}
