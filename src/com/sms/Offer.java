package com.sms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

public class Offer extends HttpServlet 
{
    private static final long serialVersionUID = 3637588532737512970L;
    private String currentClassName;
    private static String baseRedeemUrl = "http://sms.paidpunch.com/redeem?Code=";

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
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa z");
            datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDatetime = datetimeFormat.format(new java.util.Date().getTime()); 
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select offerBizCode from `" + Constants.OFFERS_DOMAIN + 
                    "` where itemName() = '" + code + 
                    "' and `expiryDatetime` > '" + currentDatetime + "'";
            SimpleLogger.getInstance().info(currentClassName, allQuery);
            List<Item> queryList = sdb.selectQuery(allQuery);
            if (queryList.size() > 0)
            {
                if (queryList.size() > 1)
                {
                    // Warn that there appear to be multiple active coupons for this business code
                    SimpleLogger.getInstance().warn(currentClassName, "MultipleActiveOffers|itemName:" + code);
                }
                
                Item currentItem = queryList.get(0);
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    if (attribute.getName().equals("offerBizCode"))
                    {
                        Business current = BusinessesList.getInstance().getBusinessByBizCode(attribute.getValue());
                        if (current != null)
                        {
                            businessName = current.getName();
                        }
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
      
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {
            String businessName = getBusinessName(codeString);
            if (businessName != null)
            {
                request.setAttribute("business_name", businessName);
                request.setAttribute("redeemlink", baseRedeemUrl + codeString);
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
