package com.sms;

import com.model.Business;
import com.model.PrizeList;
import com.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class Offer extends LocalCoopServlet 
{
    private static final long serialVersionUID = 3637588532737512970L;
    private String currentClassName;
    private static String baseRedeemUrl = "redeem?Code=";
    
    private static final String progressBarTemplate = "<span style=\"float:left;padding:3px; width:25px;height:32px;\"><img src=\"images/egg.png\" alt=\"Golden Egg\"></span>" +
            "<div style=\"padding-top:6px;padding-right:3px;\"><div class=\"progress progress-striped\" style=\"height:20px;\"><div class=\"bar bar-success\" style=\"width: <PERCENT>%;\"></div></div>" +
            "Text <REMAINING> more <TIME> to claim a special prize!";
    private static final String prizeButtonTemplate = "<div><a class=\"btn btn-large btn-warning\" href=\"claimprize.jsp?PrizeCode=<PRIZECODE>\">Claim Your Starbucks Giftcard!</a></div>";

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
    }
      
    private void recordOfferDisplayed(String offerId)
    {
        SimpleDB sdb = SimpleDB.getInstance();
        // Record item as offered so egg isn't displayed again
        List<ReplaceableAttribute> offerAttributes = new ArrayList<ReplaceableAttribute>();
        offerAttributes.add(new ReplaceableAttribute("displayoffer", "1", true));
        
        sdb.updateItem(Constants.OFFERS_DOMAIN, offerId, offerAttributes);
    }
    
    private int getNumberOfTextsThisWeek(String phone)
    {        
        int numberOfTexts = 0;
        try
        {
            Date sundayOfCurrentWeek = Utility.getSundayOfCurrentWeek();
            String sundayOfCurrentWeekString = Utility.getDatetimeInUTC(sundayOfCurrentWeek);
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select count(*) from `" + Constants.OFFERS_DOMAIN + 
                    "` where `phone` = '" + phone + 
                    "' and `createdDatetime` >= '" + sundayOfCurrentWeekString + "'";
            SimpleLogger.getInstance().info(currentClassName, allQuery);
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList != null)
            {                
                Item currentItem = queryList.get(0);
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    if (attribute.getName().equals("Count"))
                    {
                        numberOfTexts = Integer.parseInt(attribute.getValue());
                        break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        return numberOfTexts;
    }
    
    private String getPrizeString(int numberOfTextsThisWeek, String prizeCode)
    {
        String prizeString = null;
        int percentOfBar = PrizeList.getInstance().getWeeklyTextPercentageComplete(numberOfTextsThisWeek);
        if (percentOfBar < 100)
        {
            prizeString = progressBarTemplate.replace("<PERCENT>", Integer.toString(percentOfBar));
            int remainingTexts = PrizeList.getInstance().getRemainingTexts(numberOfTextsThisWeek);
            prizeString = prizeString.replace("<REMAINING>", Integer.toString(remainingTexts));
            if (remainingTexts == 1)
            {
                prizeString = prizeString.replace("<TIME>", "time");
            }
            else
            {
                prizeString = prizeString.replace("<TIME>", "times");
            }
        }
        else
        {
            prizeString = prizeButtonTemplate.replace("<PRIZECODE>", prizeCode);
        }
        return prizeString;
    }
    
    @Override  
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {  
            recordOfferDisplayed(codeString);
            
            SimpleLogger.getInstance().info(currentClassName, "IndicateOfferDisplayed|Code:" + codeString);
        }
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
                    // Handle prize 
                    String phone = offerInfo.get("phone");
                    int numberOfTextsThisWeek = getNumberOfTextsThisWeek(phone);
                    String prizeCode = PrizeList.getInstance().createPrizeIfNecessary(numberOfTextsThisWeek, phone);
                    String prizeString = getPrizeString(numberOfTextsThisWeek, prizeCode);
                    
                    setResponseAttributes(request, currentBiz, offerInfo);
                    request.setAttribute("offercode", codeString);
                    request.setAttribute("prize", prizeString);
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
