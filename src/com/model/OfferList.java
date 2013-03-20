package com.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.server.Constants;
import com.server.DataObjectBase;
import com.server.SimpleDB;
import com.server.SimpleLogger;
import com.server.Utility;

public class OfferList extends DataObjectBase 
{
    private static OfferList singleton;
    private ArrayList<OfferData> offerArray;
    
    private OfferList()
    {
        currentClassName = OfferList.class.getSimpleName();
        offerArray = getOffersFromSDB();
        
        lastRefreshTime = null;
        // Refresh interval in milliseconds
        refreshInterval = 15 * 60 * 1000L;
    }
    
    public ArrayList<OfferData> getOffers()
    {
        refreshBusinessesFromSDBIfNecessary();
        return offerArray;
    }
    
    private void refreshBusinessesFromSDBIfNecessary()
    {
        if (timeForRefresh())
        {
            try
            {
                getOffersFromSDB();
                
                lastRefreshTime = new Date();
            }
            catch (Exception ex)
            {
                SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
                lastRefreshTime = null;
                offerArray = null;
            }
        }
    }
    
    private Date getFirstOfPreviousMonth()
    {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        
        // get start of the month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        // get start of the previous month
        cal.add(Calendar.MONTH, -1);
        
        return cal.getTime();
    }
    
    private ArrayList<OfferData> getOffersFromSDB()
    {
        ArrayList<OfferData> current = null;
        try
        {
            Date prevMonthDatetime = getFirstOfPreviousMonth();
            String prevMonthDatetimeString = Utility.getDatetimeInUTC(prevMonthDatetime);
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select * from `" + Constants.OFFERS_DOMAIN + 
                    "` where `expiryDatetime` > '" + prevMonthDatetimeString + "'";
            SimpleLogger.getInstance().info(currentClassName, allQuery);
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList != null)
            {           
                current = new ArrayList<OfferData>();
                for (Item currentItem : queryList)
                {
                    OfferData currentOffer = new OfferData();
                    currentOffer.insertOfferData("offerId", currentItem.getName());
                    for (Attribute attribute : currentItem.getAttributes()) 
                    {
                        currentOffer.insertOfferData(attribute.getName(), attribute.getValue());
                    }   
                    currentOffer.getOfferRecords();
                    currentOffer.getRedeemRecords();
                    current.add(currentOffer);
                }
            }
        }
        catch (Exception ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        return current;
    }
    
    // Singleton 
    public static synchronized OfferList getInstance() 
    {
        if (singleton == null) 
        {
            singleton = new OfferList();
        }
        return singleton;
    }
}
