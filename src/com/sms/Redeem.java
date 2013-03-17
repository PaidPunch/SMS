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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class Redeem extends HttpServlet 
{
    private static final long serialVersionUID = 5428715563522558084L;
    private String currentClassName;

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
    
    private HashMap<String,String> getOfferInfo(String code)
    {
        HashMap<String,String> current = null;
        try
        {
            String currentDatetime = Utility.getCurrentDatetimeInUTC();
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select version, offerBizCode, expiryDatetime from `" + Constants.OFFERS_DOMAIN + 
                    "` where itemName() = '" + code + 
                    "' and `expiryDatetime` > '" + currentDatetime + "'";
            SimpleLogger.getInstance().info(currentClassName, allQuery);
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList != null)
            {                
                current = new HashMap<String,String>();
                Item currentItem = queryList.get(0);
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    current.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        catch (Exception ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        return current;
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
                Business currentBiz = null;
                
                if (offerInfo.get("version").equals("1.0"))
                {
                    currentBiz = BusinessesList.getInstance().getBusinessByBizCodeV1(offerInfo.get("offerBizCode"));
                }
                else if (offerInfo.get("version").equals("2.0"))
                {
                    currentBiz = BusinessesList.getInstance().retrieveSingleBusinessObjectWithBusinessId(offerInfo.get("offerBizCode"));
                }
                
                if (currentBiz != null)
                {
                    // TODO: Assume single branch and offer for now
                    BusinessBranch currentBranch = currentBiz.getBranches().get(0);
                    
                    request.setAttribute("name", currentBiz.getName());
                    request.setAttribute("desc", currentBiz.getDesc());
                    String logo_path = currentBiz.getLogoPath();
                    if (logo_path != null && logo_path.length() > 0)
                    {
                        String imgElement = "<img src=\"" + logo_path + "\" alt=\"" + currentBiz.getName() + "\">";
                        request.setAttribute("logo", imgElement);    
                    }
                    
                    request.setAttribute("latitude", currentBranch.getLatitude());
                    request.setAttribute("longitude", currentBranch.getLongitude());
                    request.setAttribute("phone", Utility.standardizePhoneNumber(currentBranch.getContactNo()));
                    request.setAttribute("expirydate", offerInfo.get("expiryDatetime"));
                    
                    if (offerInfo.get("version").equals("1.0"))
                    {
                        String address = currentBranch.getAddressLine() + ", " + currentBranch.getCity() + ", " + 
                                currentBranch.getState() + " " + currentBranch.getZipcode();
                        request.setAttribute("address", address);
                        
                        Punchcard currentOffer = currentBiz.getPunchcards().get(0);
                        
                        String offer = "$" + currentOffer.getValuePerPunch() + " off on purchases of $" +
                                currentOffer.getMinValue() + " or more";
                        request.setAttribute("offer", offer);
                        
                        String codeText = currentOffer.getCouponCode();
                        if (codeText != null && codeText.length() > 0)
                        {
                            String couponCodeString = "<h2><b>Code: " + codeText + "</b></h2>";
                            request.setAttribute("couponcode", couponCodeString);
                        }   
                    }
                    else if (offerInfo.get("version").equals("2.0"))
                    {
                        request.setAttribute("address", currentBranch.getAddressLine());
                        
                        BusinessOffer currentOffer = currentBiz.getOffers().get(0);
                        request.setAttribute("offer", currentOffer.getOfferText());
                        
                        String codeText = currentOffer.getOfferCode();
                        if (codeText != null && codeText.length() > 0)
                        {
                            String couponCodeString = "<h2><b>Code: " + codeText + "</b></h2>";
                            request.setAttribute("couponcode", couponCodeString);
                        } 
                    }
                    
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
