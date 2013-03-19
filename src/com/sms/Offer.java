package com.sms;

import com.model.Business;
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

public class Offer extends LocalCoopServlet 
{
    private static final long serialVersionUID = 3637588532737512970L;
    private String currentClassName;
    private static String baseRedeemUrl = "redeem?Code=";

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
    
    private void recordOfferView(String offerId)
    {
        // Get UUID for naming new suggestion
        UUID itemName = UUID.randomUUID();
        
        String currentDatetime = Utility.getCurrentDatetimeInUTC();
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("version", "1.0", true));
        listAttributes.add(new ReplaceableAttribute("offerId", offerId, true));
        listAttributes.add(new ReplaceableAttribute("createdDatetime", currentDatetime, true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.OFFERSRECORD_DOMAIN, itemName.toString(), listAttributes);
        
        // Record item as offered so egg isn't displayed again
        List<ReplaceableAttribute> offerAttributes = new ArrayList<ReplaceableAttribute>();
        offerAttributes.add(new ReplaceableAttribute("displayoffer", "1", true));
        
        sdb.updateItem(Constants.OFFERS_DOMAIN, offerId, offerAttributes);
    }
      
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {            
            HashMap<String,String> offerInfo = getOfferInfo(codeString);
            if (offerInfo != null)
            {
                // Record that someone viewed this offer
                recordOfferView(codeString);
                
                Business currentBiz = getBusiness(offerInfo.get("version"), offerInfo.get("offerBizCode"));
                
                if (currentBiz != null)
                {
                    setResponseAttributes(request, currentBiz, offerInfo);
                    request.setAttribute("redeemlink", Constants.URL_ROOT + baseRedeemUrl + codeString);
                    
                    request.getRequestDispatcher("/offer.jsp").forward(request, response);    
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
