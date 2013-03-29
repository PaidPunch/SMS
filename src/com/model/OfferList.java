package com.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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

    private JSONObject analyticsObject;
    
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
    
    public JSONObject getOffersAnalytics()
    {
        refreshBusinessesFromSDBIfNecessary();
           
        return analyticsObject;
    }
    
    private void refreshBusinessesFromSDBIfNecessary()
    {
        if (timeForRefresh())
        {
            try
            {
                offerArray = getOffersFromSDB();
                refreshAnalytics();
                
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
    
    private void refreshAnalytics()
    {
        analyticsObject = new JSONObject();
        
        // Start by bucketizing offers/offerRecords/redeemRecords
        HashMap<String, HashMap<String,ArrayList<JSONObject>>> analyticsStructure = bucketizeAllObjects();
        
        try
        {            
            JSONArray monthlyArray = createMonthlyArray(analyticsStructure);
            analyticsObject.put("monthByWeeks", monthlyArray);
            
            // Create per day structure for latest week
            Date latestWeek = null;
            HashMap<String,ArrayList<JSONObject>> latestWeekArray = null;
            // Start by finding latest week
            for (Map.Entry<String, HashMap<String,ArrayList<JSONObject>>> entry : analyticsStructure.entrySet())
            {
                Date currentWeek = Utility.parseDatetimeString(entry.getKey());
                if (latestWeek == null || latestWeek.before(currentWeek))
                {
                    latestWeek = currentWeek;
                    latestWeekArray = entry.getValue();
                }
            }
            
            analyticsObject.put("latestWeek", Utility.convertToJavascriptDatetimeFormat(Utility.getDatetimeInUTC(latestWeek)));
            
            JSONArray weeklyArray = createWeeklyArray(analyticsStructure, latestWeek, latestWeekArray);
            analyticsObject.put("weekByDays", weeklyArray);
            
            JSONObject businessesObject = createBusinessesObject(analyticsStructure, latestWeek, latestWeekArray);
            analyticsObject.put("businesses", businessesObject);
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        }         
    }
    
    private HashMap<String, HashMap<String,ArrayList<JSONObject>>> bucketizeAllObjects()
    {
        HashMap<String,HashMap<String,ArrayList<JSONObject>>> analyticsStructure = new HashMap<String,HashMap<String,ArrayList<JSONObject>>>();
        for (OfferData offer : offerArray)
        {
            bucketizeHashMapByWeek(offer.getOfferMap(), "Offers", analyticsStructure);
            
            ArrayList<HashMap<String,String>> offerRecords = offer.getOfferRecords();
            for (HashMap<String,String> offerRecord : offerRecords)
            {
                bucketizeHashMapByWeek(offerRecord, "OfferRecords", analyticsStructure);
            }
            
            ArrayList<HashMap<String,String>> redeemRecords = offer.getRedeemRecords();
            for (HashMap<String,String> redeemRecord : redeemRecords)
            {
                bucketizeHashMapByWeek(redeemRecord, "RedeemRecords", analyticsStructure);
            }
        }
        return analyticsStructure;
    }
    
    private JSONArray createMonthlyArray(HashMap<String,HashMap<String,ArrayList<JSONObject>>> analyticsStructure)
    {
        JSONArray monthlyArray = new JSONArray();
        try
        {
            // Create per-week structure
            for (Map.Entry<String, HashMap<String,ArrayList<JSONObject>>> entry : analyticsStructure.entrySet())
            {
                JSONObject listOfRecordsJSON = getJSONOfCounts(entry.getValue());
                // Insert week
                listOfRecordsJSON.put("week", entry.getKey());
                monthlyArray.put(listOfRecordsJSON);
            }            
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        }  
        return monthlyArray;
    }
    
    private JSONArray createWeeklyArray(HashMap<String,HashMap<String,ArrayList<JSONObject>>> analyticsStructure, Date latestWeek, HashMap<String,ArrayList<JSONObject>> latestWeekArray)
    {
        JSONArray weeklyArray = new JSONArray();
        try
        {                        
            // Handle offers for the week
            int offersCountByDay[] = new int[7];
            ArrayList<JSONObject> offersArray = latestWeekArray.get("Offers");
            if (offersArray != null)
            {
                for (JSONObject offer : offersArray)
                {
                    String currentDateString = offer.getString("createdDatetime");
                    Date currentDate = Utility.parseDatetimeString(currentDateString);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentDate);
                    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    offersCountByDay[dayOfWeek] = offersCountByDay[dayOfWeek] + 1;
                }    
            }
            // Handle offer records for the week
            int offerRecordsCountByDay[] = new int[7];
            ArrayList<JSONObject> offerRecordsArray = latestWeekArray.get("OfferRecords");
            if (offerRecordsArray != null)
            {
                for (JSONObject offerRecord : offerRecordsArray)
                {
                    Date currentDate = Utility.parseDatetimeString(offerRecord.getString("createdDatetime"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentDate);
                    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    offerRecordsCountByDay[dayOfWeek] = offerRecordsCountByDay[dayOfWeek] + 1;
                }    
            }
            // Handle redeem records for the week
            int redeemRecordsCountByDay[] = new int[7];
            ArrayList<JSONObject> redeemRecordsArray = latestWeekArray.get("RedeemRecords");
            if (redeemRecordsArray != null)
            {
                for (JSONObject redeemRecord : redeemRecordsArray)
                {
                    Date currentDate = Utility.parseDatetimeString(redeemRecord.getString("createdDatetime"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentDate);
                    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    redeemRecordsCountByDay[dayOfWeek] = redeemRecordsCountByDay[dayOfWeek] + 1;
                }    
            }
            // Create json structure
            Date currentDate = latestWeek;
            for (int i = 0; i < 7; i++)
            {
                JSONObject dayJSON = new JSONObject();
                dayJSON.put("Offers", offersCountByDay[i]);
                dayJSON.put("OfferRecords", offerRecordsCountByDay[i]);
                dayJSON.put("RedeemRecords", redeemRecordsCountByDay[i]);
                dayJSON.put("day", Utility.convertToJavascriptDatetimeFormat(Utility.getDatetimeInUTC(currentDate)));
                
                // Increment to next day
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.add(Calendar.DATE, 1);
                currentDate = cal.getTime();
                
                weeklyArray.put(dayJSON);
            }
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        } 
        return weeklyArray;
    }
    
    private JSONObject createBusinessesObject(HashMap<String,HashMap<String,ArrayList<JSONObject>>> analyticsStructure, Date latestWeek, HashMap<String,ArrayList<JSONObject>> latestWeekArray)
    {
        JSONObject businessesObject = new JSONObject();
        try
        {            
            // Count number of offers per bizCode
            HashMap<String,Integer> businessesMap = new HashMap<String,Integer>();
            ArrayList<JSONObject> offersArray = latestWeekArray.get("Offers");
            for (JSONObject offer : offersArray)
            {
                String bizCode = offer.getString("bizCode");
                if (businessesMap.containsKey(bizCode))
                {
                    Integer value = businessesMap.get(bizCode);
                    businessesMap.put(bizCode, value + 1);
                }
                else
                {
                    businessesMap.put(bizCode, 1);
                }
            }
            
            // Put into a JSONObject
            for (Map.Entry<String, Integer> entry : businessesMap.entrySet())
            {
                businessesObject.put(entry.getKey(), entry.getValue());
            }
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        } 
        return businessesObject;
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
                        String name = attribute.getName();
                        if (name.equals("expiryDatetime") || name.equals("createdDatetime"))
                        {
                            String date = Utility.convertToJavascriptDatetimeFormat(attribute.getValue());
                            currentOffer.insertOfferData(name, date);
                        }
                        else
                        {
                            currentOffer.insertOfferData(name, attribute.getValue());    
                        }
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
    
    private void bucketizeHashMapByWeek(HashMap<String,String> mp, String category, HashMap<String, HashMap<String,ArrayList<JSONObject>>> analyticsStructure)
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
    
    private JSONObject getJSONOfCounts(HashMap<String,ArrayList<JSONObject>> mp)
    {
        JSONObject listOfCountsJSON = new JSONObject();
        try
        {
            for (Map.Entry<String, ArrayList<JSONObject>> entry : mp.entrySet())
            {
                ArrayList<JSONObject> arrayOfJSONObjects = entry.getValue();
                listOfCountsJSON.put(entry.getKey(), arrayOfJSONObjects.size());
            }    
        }
        catch (Exception e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        } 
        return listOfCountsJSON;
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
