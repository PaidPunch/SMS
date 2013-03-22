package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import com.server.Constants;
import com.server.DataObjectBase;
import com.server.SimpleDB;
import com.server.SimpleLogger;
import com.server.Utility;

public class PrizeList extends DataObjectBase 
{
    private static PrizeList singleton;
    private static final int weeklyPrizeNumberOfTexts = 3;
    
    // Private constructor
    private PrizeList() 
    {
        lastRefreshTime = null;
        currentClassName = PrizeList.class.getSimpleName();
        // Refresh interval in milliseconds
        refreshInterval = 15 * 60 * 1000L;
    }
    
    public int getWeeklyTextPercentageComplete(int numberOfTextsThisWeek)
    {
        int percentOfBar = 100;
        if (numberOfTextsThisWeek < weeklyPrizeNumberOfTexts)
        {
            percentOfBar = (numberOfTextsThisWeek * 100) / weeklyPrizeNumberOfTexts;
        }
        return percentOfBar;
    }
    
    public int getRemainingTexts(int numberOfTextsThisWeek)
    {
        return (weeklyPrizeNumberOfTexts - numberOfTextsThisWeek);
    }
    
    public String createPrizeIfNecessary(int numberOfTextsThisWeek, String phone)
    {                 
        String prizeId = null;
        if (numberOfTextsThisWeek == weeklyPrizeNumberOfTexts)
        {
            // Start by making sure prize doesn't already exist for current week
            SimpleDB sdb = SimpleDB.getInstance();
            String currentWeek = Utility.getDatetimeInUTC(Utility.getSundayOfCurrentWeek());
            
            String queryString = "SELECT * FROM `" + Constants.PRIZE_DOMAIN + "` " +
                    "WHERE `phone` = '" + phone + "' AND  `week` = '" + currentWeek + "'";
            SimpleLogger.getInstance().info(currentClassName, queryString);
            List<Item> queryList = sdb.retrieveFromSimpleDB(queryString, true);
            if (queryList == null || queryList.size() == 0)
            {            
                prizeId = UUID.randomUUID().toString();
                
                List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
                listAttributes.add(new ReplaceableAttribute("type", "StarbucksWeekly", true));
                listAttributes.add(new ReplaceableAttribute("version", "1.0", true));
                listAttributes.add(new ReplaceableAttribute("phone", phone, true));
                listAttributes.add(new ReplaceableAttribute("claimed", "0", true));
                listAttributes.add(new ReplaceableAttribute("week", currentWeek, true));

                sdb.updateItem(Constants.PRIZE_DOMAIN, prizeId, listAttributes);   
            } 
            else
            {
                Item currentItem = queryList.get(0);
                for (Attribute attribute : currentItem.getAttributes())
                {
                    if (attribute.getName().equals("claimed"))
                    {
                        if (attribute.getValue().equals("0"))
                        {
                            prizeId = currentItem.getName();
                        }
                       
                        break;
                    }
                }
            }
        }
        return prizeId;
    }
    
    public boolean updatePrize(String prizeId, String phone, String email)
    {         
        // Remove all non-numeric characters from phone number
        String formattedPhone = "+1" + phone.replaceAll("[^0-9]", "");
        UpdateCondition condition = new UpdateCondition("phone", formattedPhone, true);
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("email", email, true));
        listAttributes.add(new ReplaceableAttribute("claimed", "1", true));

        SimpleDB sdb = SimpleDB.getInstance();
        return sdb.updateItem(Constants.PRIZE_DOMAIN, prizeId, listAttributes, condition);
    }
    
    // Disable cloning for singletons
    public Object clone() throws CloneNotSupportedException 
    {
        throw new CloneNotSupportedException();
    }
    
    // Singleton 
    public static synchronized PrizeList getInstance() 
    {
        if (singleton == null) 
        {
            singleton = new PrizeList();
        }
        return singleton;
    }
}
