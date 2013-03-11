package com.sms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

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
    
    private Business getBusiness(String code)
    {
        return BusinessesList.getInstance().getBusinessByBizCode(code);
    }
    
    private HashMap<String,String> getOfferInfo(String code)
    {
        HashMap<String,String> current = null;
        try
        {
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa z");
            datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDatetime = datetimeFormat.format(new java.util.Date().getTime()); 
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select offerBizCode, expiryDatetime from `" + Constants.OFFERS_DOMAIN + 
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
      
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        String codeString = request.getParameter("Code");
        if (codeString != null)
        {
            HashMap<String,String> offerInfo = getOfferInfo(codeString);
            if (offerInfo != null)
            {
                Business currentBiz = getBusiness(offerInfo.get("offerBizCode"));
                if (currentBiz != null)
                {
                    // TODO: Assume single branch and offer for now
                    Map.Entry<String, BusinessBranch> entryBranches = currentBiz.getBranches().entrySet().iterator().next();
                    BusinessBranch currentBranch = entryBranches.getValue();
                    Map.Entry<String, Punchcard> entryOffers = currentBiz.getPunchcards().entrySet().iterator().next();
                    Punchcard currentOffer = entryOffers.getValue();
                    
                    request.setAttribute("name", currentBiz.getName());
                    request.setAttribute("desc", currentBiz.getDesc());
                    request.setAttribute("logo", currentBiz.getLogoPath());
                    String address = currentBranch.getAddressLine() + ", " + currentBranch.getCity() + ", " + 
                            currentBranch.getState() + " " + currentBranch.getZipcode();
                    request.setAttribute("address", address);
                    request.setAttribute("latitude", currentBranch.getLatitude());
                    request.setAttribute("longitude", currentBranch.getLongitude());
                    request.setAttribute("phone", currentBranch.getContactNo());
                    request.setAttribute("discount", currentOffer.getValuePerPunch());
                    request.setAttribute("minvalue", currentOffer.getMinValue());
                    request.setAttribute("expirydate", offerInfo.get("expiryDatetime"));
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
