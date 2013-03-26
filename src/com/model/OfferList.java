package com.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

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
    
    private HashMap<String, HashMap<String,ArrayList<JSONObject>>> analyticsStructure;
    
    private OfferList()
    {
        currentClassName = OfferList.class.getSimpleName();
        offerArray = getOffersFromSDB();
        
        lastRefreshTime = null;
        // Refresh interval in milliseconds
        refreshInterval = 60 * 60 * 1000L;
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
            Date sundayOfFirstOfPreviousMonth = Utility.getSundayOfWeek(prevMonthDatetime);
            String sundayOfFirstOfPreviousMonthString = Utility.getDatetimeInUTC(sundayOfFirstOfPreviousMonth);
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select * from `" + Constants.OFFERS_DOMAIN + 
                    "` where `createdDatetime` > '" + sundayOfFirstOfPreviousMonthString + "'";
            SimpleLogger.getInstance().info(currentClassName, allQuery);
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList != null)
            {           
                current = new ArrayList<OfferData>();
                for (Item currentItem : queryList)
                {
                    OfferData currentOffer = new OfferData();
                    currentOffer.insertOfferData("textId", currentItem.getName());
                    for (Attribute attribute : currentItem.getAttributes()) 
                    {
                        currentOffer.insertOfferData(attribute.getName(), attribute.getValue());
                    }   
                    currentOffer.retrieveAssociatedData();
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
    
    private void bucketizeHashMapByWeek(HashMap<String,String> mp, String category)
    {
        Date offerDate = Utility.getCreatedDatetime(mp);
        Date sundayOfWeek = Utility.getSundayOfWeek(offerDate);
        String sundayOfWeekString = Utility.getDatetimeInUTC(sundayOfWeek);
        
        // Get the list of objects for that week
        HashMap<String,ArrayList<JSONObject>> weekObjs = analyticsStructure.get(sundayOfWeekString);
        if (weekObjs == null)
        {
            weekObjs = new HashMap<String,ArrayList<JSONObject>>();
            analyticsStructure.put(sundayOfWeekString, weekObjs);
        }
        
        // Get the list of items in that category for that week
        ArrayList<JSONObject> categoryItems = weekObjs.get(category);
        if (categoryItems == null)
        {
            categoryItems = new ArrayList<JSONObject>();
            weekObjs.put(category, categoryItems);
        }
        
        // Insert json object of offer into array for that week
        categoryItems.add(Utility.convertHashMapToJSONObject(mp));
    }
    
    private JSONArray getJSONOfCategory(HashMap<String,ArrayList<JSONObject>> mp)
    {
        JSONArray arrayOfCategories = new JSONArray();
        try
        {
            for (Map.Entry<String, ArrayList<JSONObject>> entry : mp.entrySet())
            {
                JSONObject objJSON = new JSONObject();
                JSONArray arrayJSON = new JSONArray();
                ArrayList<JSONObject> arrayOfJSONObjects = entry.getValue();
                for (JSONObject obj : arrayOfJSONObjects)
                {
                    arrayJSON.put(obj);
                }
                objJSON.put(entry.getKey(), arrayJSON);
                arrayOfCategories.put(objJSON);
            }    
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        } 
        return arrayOfCategories;
    }
    
    public JSONArray getOffersArray()
    {
        analyticsStructure = new HashMap<String,HashMap<String,ArrayList<JSONObject>>>();
        refreshBusinessesFromSDBIfNecessary();
        for (OfferData offer : offerArray)
        {
            bucketizeHashMapByWeek(offer.getOfferMap(), "Offers");
            
            ArrayList<HashMap<String,String>> offerRecords = offer.getOfferRecords();
            for (HashMap<String,String> offerRecord : offerRecords)
            {
                bucketizeHashMapByWeek(offerRecord, "OfferRecords");
            }
            
            ArrayList<HashMap<String,String>> redeemRecords = offer.getRedeemRecords();
            for (HashMap<String,String> redeemRecord : redeemRecords)
            {
                bucketizeHashMapByWeek(redeemRecord, "RedeemRecords");
            }
        }
        
        JSONArray arrayOfWeeks = new JSONArray();
        try
        {
            for (Map.Entry<String, HashMap<String,ArrayList<JSONObject>>> entry : analyticsStructure.entrySet())
            {
                JSONArray arrayOfCategories = getJSONOfCategory(entry.getValue());
                JSONObject objWeek = new JSONObject();
                objWeek.put(entry.getKey(), arrayOfCategories);
                arrayOfWeeks.put(objWeek);
            }    
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        } 
        
        return arrayOfWeeks;
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
