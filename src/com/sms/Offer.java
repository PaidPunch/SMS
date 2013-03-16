package com.sms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class Offer extends HttpServlet 
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
    
    private String getBusinessName(String code)
    {
        String businessName = null;
        try
        {
            String currentDatetime = Utility.getCurrentDatetimeInUTC();
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select name from `" + Constants.OFFERS_DOMAIN + 
                    "` where itemName() = '" + code + 
                    "' and `expiryDatetime` > '" + currentDatetime + "'";
            SimpleLogger.getInstance().info(currentClassName, allQuery);
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList != null)
            {
                Item currentItem = queryList.get(0);
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    if (attribute.getName().equals("name"))
                    {
                        businessName = attribute.getValue();
                        break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        return businessName;
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
    }
      
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {
            // Record that someone viewed this offer
            recordOfferView(codeString);
            
            String businessName = getBusinessName(codeString);
            if (businessName != null)
            {
                request.setAttribute("business_name", businessName);
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
            request.setAttribute("error_message", "Redeem code missing");
            request.getRequestDispatcher("/error.jsp").forward(request, response);  
        }
    } 
}
