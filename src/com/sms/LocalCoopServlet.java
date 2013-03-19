package com.sms;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.model.Business;
import com.model.BusinessBranch;
import com.model.BusinessOffer;
import com.model.BusinessesList;
import com.model.Punchcard;
import com.server.Constants;
import com.server.SimpleDB;
import com.server.SimpleLogger;
import com.server.Utility;

public class LocalCoopServlet extends HttpServlet 
{
    private static final long serialVersionUID = -4102097652140999986L;
    protected String currentClassName;

    protected HashMap<String,String> getOfferInfo(String code)
    {
        HashMap<String,String> current = null;
        
        try
        {
            String currentDatetime = Utility.getCurrentDatetimeInUTC();
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select * from `" + Constants.OFFERS_DOMAIN + 
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
    
    protected Business getBusiness(String version, String offerBizCode)
    {
        Business currentBiz = null;
        if (version.equals("1.0"))
        {
            currentBiz = BusinessesList.getInstance().getBusinessByBizCodeV1(offerBizCode);
        }
        else if (version.equals("2.0"))
        {
            currentBiz = BusinessesList.getInstance().retrieveSingleBusinessObjectWithBusinessId(offerBizCode);
        }
        return currentBiz;
        
    }

    protected void setResponseAttributes(HttpServletRequest request, Business currentBiz, HashMap<String,String> offerInfo)
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
        
        String displayOffer = offerInfo.get("displayoffer");
        if (displayOffer != null)
        {
            request.setAttribute("displayoffer", displayOffer);  
        }
        else
        {
            request.setAttribute("displayoffer", "0"); 
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
    }
}
