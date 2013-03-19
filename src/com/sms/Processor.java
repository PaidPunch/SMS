package com.sms;

import com.model.*;
import com.server.*;

import com.amazonaws.services.simpledb.model.Attribute;
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
    private static String reminderString = "You have a prize waiting from ";
    private static String congratsString = "Congratulations! You won a prize. Please click on the link to collect it. ";
    private static String baseOfferUrl = "offer?Code=";
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
                // Check if business is a Coop-only business first 
                String version = "2.0";
                Business currentBiz = BusinessesList.getInstance().getABusinessCloseByV2(bizCode);
                if (currentBiz == null)
                {
                    currentBiz = BusinessesList.getInstance().getABusinessCloseByV1(bizCode);
                    version = "1.0";
                }
                if (currentBiz != null)
                {
                    String offerId = createOffer(fromString, currentBiz, bizCode, version);    
                    responseString = congratsString + " " + Constants.URL_ROOT + baseOfferUrl + offerId;    
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
    
    private String createOffer(String fromNumber, Business currentBiz, String bizCode, String version)
    {
        String offerID = Utility.getRandomAlphaNumericCode(7);
        
        // Get current datetime
        Date currentDate = new java.util.Date();
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa z");
        datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDatetime = Utility.getCurrentDatetimeInUTC();
        
        // Get expiry datetime
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.HOUR, +expiryHours);
        Date expiryDate = cal.getTime();
        String expiryDateTime = datetimeFormat.format(expiryDate.getTime()); 
        
        // TODO: Get first offer. Assume there's only one for now.
        String offerId;
        if (version.equals("1.0"))
        {
            Punchcard currentOffer = currentBiz.getPunchcards().get(0);
            offerId = currentOffer.getPunchcardId();
        }
        else
        {
            BusinessOffer currentOffer = currentBiz.getOffers().get(0);
            offerId = currentOffer.getOfferId();
        }
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("version", version, true));
        listAttributes.add(new ReplaceableAttribute("phone", fromNumber, true));
        listAttributes.add(new ReplaceableAttribute("offerBizCode", currentBiz.getBusinessUserId(), true));
        listAttributes.add(new ReplaceableAttribute("name", currentBiz.getName(), true));
        listAttributes.add(new ReplaceableAttribute("offerId", offerId, true));
        listAttributes.add(new ReplaceableAttribute("bizCode", bizCode, true));
        listAttributes.add(new ReplaceableAttribute("displayoffer", "0", true));
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
            String currentDatetime = Utility.getCurrentDatetimeInUTC();
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select * from `" + Constants.OFFERS_DOMAIN + 
                    "` where `phone` = '" + fromNumber + 
                    "' and `bizCode` = '" + bizCode + 
                    "' and `expiryDatetime` > '" + currentDatetime + "'";
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList != null)
            {                
                // Get the first row
                Item current = queryList.get(0);
                
                // Get the business name
                String bizName = "";
                for (Attribute attribute : current.getAttributes()) 
                {
                    if (attribute.getName().equals("name"))
                    {
                        bizName = attribute.getValue();
                        break;
                    }
                }
                
                // Get the item name and construct the responseString
                responseString = reminderString + bizName + " " + Constants.URL_ROOT + baseOfferUrl + current.getName();
            }
        }
        catch (Exception ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        return responseString;
    }
}
