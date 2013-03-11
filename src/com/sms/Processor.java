package com.sms;

import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.Sms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Processor extends HttpServlet 
{      
    private static final long serialVersionUID = 1L;  
    private String currentClassName;
    private static int expiryHours = 24;
    private static String baseOfferUrl = "http://sms.paidpunch.com/offer?Code=";
    private static String errorMsg = "We're experiencing some difficulties. Please try again later.";
      
    public Processor() 
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
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String responseString = null;
        String fromString = request.getParameter("From");
        String bodyString = request.getParameter("Body");
        if (fromString == null || bodyString == null)
        {
            responseString = errorMsg;
        }
        else
        {
            String bizCode = bodyString.trim();
            bizCode = bizCode.toUpperCase();
            
            responseString = checkIfOfferExists(fromString, bizCode);
            if (responseString == null)
            {
                String offerBizCode = BusinessesList.getInstance().getBusinessesCloseBy(bizCode);
                if (offerBizCode != null)
                {
                    String offerId = createOffer(fromString, offerBizCode, bizCode);    
                    responseString = baseOfferUrl + offerId;    
                }
                else
                {
                    SimpleLogger.getInstance().error(currentClassName, "ErrorOccurred");
                    responseString = errorMsg;
                }
            }  
        }
        
        TwiMLResponse twiml = new TwiMLResponse();
        Sms sms = new Sms(responseString);
        try 
        {
            twiml.append(sms);
        } 
        catch (TwiMLException e) 
        {
            SimpleLogger.getInstance().error(currentClassName, e.getMessage());
        }
 
        response.setContentType("application/xml");
        response.getWriter().print(twiml.toXML());  
    } 
    
    private String createOffer(String fromNumber, String offerBizCode, String bizCode)
    {
        String offerID = Utility.getRandomAlphaNumericCode(7);
        
        // Get current datetime
        Date currentDate = new java.util.Date();
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa z");
        datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDatetime = datetimeFormat.format(currentDate.getTime()); 
        
        // Get expiry datetime
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.HOUR, +expiryHours);
        Date expiryDate = cal.getTime();
        String expiryDateTime = datetimeFormat.format(expiryDate.getTime()); 
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("version", "1.0", true));
        listAttributes.add(new ReplaceableAttribute("phone", fromNumber, true));
        listAttributes.add(new ReplaceableAttribute("offerBizCode", offerBizCode, true));
        listAttributes.add(new ReplaceableAttribute("bizCode", bizCode, true));
        listAttributes.add(new ReplaceableAttribute("createdDatetime", currentDatetime, true));
        listAttributes.add(new ReplaceableAttribute("expiryDatetime", expiryDateTime, true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.OFFERS_DOMAIN, offerID, listAttributes);
        
        return offerID;
    }
    
    private String checkIfOfferExists(String fromNumber, String bizCode)
    {
        String responseString = null;
        try
        {
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDatetime = datetimeFormat.format(new java.util.Date().getTime()); 
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select * from `" + Constants.OFFERS_DOMAIN + 
                    "` where `phone` = '" + fromNumber + 
                    "' and `bizCode` = '" + bizCode + 
                    "' and `expiryDatetime` > '" + currentDatetime + "'";
            List<Item> queryList = sdb.selectQuery(allQuery);
            if (queryList.size() > 0)
            {
                if (queryList.size() > 1)
                {
                    // Warn that there appear to be multiple active coupons for this business code
                    SimpleLogger.getInstance().warn(currentClassName, "MultipleActiveOffers|Phone:" + fromNumber + "|bizCode:" + bizCode);
                }
                
                // Get the first row
                Item current = queryList.get(0);
                
                // Get the item name and construct the responseString
                responseString = baseOfferUrl + current.getName();
            }
        }
        catch (Exception ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        return responseString;
    }
}
