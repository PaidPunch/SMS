package com.sms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;

import com.sms.DatabaseAccess;
import com.sms.DatabaseAccess.ResultSetHandler;

public class BusinessesList extends DataObjectBase 
{
    private static BusinessesList singleton;
    private HashMap<String,Business> currentBusinesses;
    
    // Private constructor
    private BusinessesList() 
    {
        lastRefreshTime = null;
        currentBusinesses = null;
        currentClassName = Business.class.getSimpleName();
        // Refresh interval in milliseconds
        refreshInterval = 15 * 60 * 1000L;
    }
    
    private void getListOfBusinesses()
    {
        String queryString = "SELECT b.business_userid,b.business_code,b.business_name,b.buss_desc,a.contactno,b.logo_path,b.busi_enabled,b.url_path," + 
                "a.address_id,a.address_line1,a.city,a.state,a.zipcode,a.longitude,a.latitude,a.region," +
                "p.punch_card_id,p.no_of_punches_per_card,p.value_of_each_punch,p.selling_price_of_punch_card,p.restriction_time,p.punchcard_category,p.expirydays,p.minimumvalue,p.punchcard_code " +
                "FROM business_users b, bussiness_address a, punch_card p " +
                "WHERE b.business_userid = a.business_id AND b.business_userid = p.business_userid;";
        try
        {
            DatabaseAccess.queryDatabaseCustom(queryString, null, currentBusinesses, new ResultSetHandler()
            {
                 public void handle(ResultSet results, Object returnObj) throws SQLException
                 { 
                     // The cast here is a well-known one, so the suppression is OK
                     @SuppressWarnings("unchecked")
                     HashMap<String,Business> hashBusinesses = (HashMap<String,Business>)returnObj;
                     while(results.next())
                     {
                         // First check to see if a business already exists for this one
                         String bizId = results.getString("business_userid");
                         String bizCode = results.getString("business_code");
                         Business currentBusiness = hashBusinesses.get(bizId);
                         if (currentBusiness == null)
                         {
                             // No business exists, create a new one
                             currentBusiness = new Business();
                             
                             // Populate product information
                             currentBusiness.setBusinessUserId(bizId);
                             currentBusiness.setName(results.getString("business_name"));
                             currentBusiness.setDesc(results.getString("buss_desc"));
                             currentBusiness.setLogoPath(results.getString("logo_path"));
                             currentBusiness.setBusiEnabled(results.getString("busi_enabled"));    
                             currentBusiness.setUrlPath(results.getString("url_path"));    
                             currentBusiness.setBusinessCode(bizCode);
                             
                             // Add current Business to the Business list
                             hashBusinesses.put(bizCode, currentBusiness);
                         }
                         
                         String address_id = results.getString("address_id");
                         BusinessBranch currentBranch = null;
                         if (currentBusiness.getBranches() != null)
                         {
                             currentBusiness.getBranches().get(address_id);    
                         }
                         if (currentBranch == null)
                         {
                             // Create a new branch
                             currentBranch = new BusinessBranch();
                             
                             // Populate branch information
                             currentBranch.setAddressLine(results.getString("address_line1"));
                             currentBranch.setCity(results.getString("city"));
                             currentBranch.setState(results.getString("state"));
                             currentBranch.setZipcode(results.getString("zipcode"));
                             currentBranch.setLongitude(results.getString("longitude"));
                             currentBranch.setLatitude(results.getString("latitude"));
                             currentBranch.setContactNo(results.getString("contactno"));
                             currentBranch.setRegion(Integer.parseInt(results.getString("region")));
                             
                             // Add current branch to the Business 
                             currentBusiness.insertBranch(address_id, currentBranch);    
                         }
                         
                         String punchcard_id = results.getString("punch_card_id");
                         Punchcard currentPunchcard = null;
                         if (currentBusiness.getPunchcards() != null)
                         {
                             currentBusiness.getPunchcards().get(punchcard_id);
                         }
                         if (currentPunchcard == null)
                         {
                             // Create a new offer
                             currentPunchcard = new Punchcard();
                             
                             // Populate offer information
                             currentPunchcard.setPunchcardId(punchcard_id);
                             currentPunchcard.setNumPunches(results.getString("no_of_punches_per_card"));
                             currentPunchcard.setValuePerPunch(results.getString("value_of_each_punch"));
                             currentPunchcard.setCost(results.getString("selling_price_of_punch_card"));
                             currentPunchcard.setRestrictionTime(results.getString("restriction_time"));
                             currentPunchcard.setCategory(results.getString("punchcard_category"));
                             currentPunchcard.setExpiryDays(results.getString("expirydays"));
                             currentPunchcard.setMinValue(results.getString("minimumvalue"));
                             currentPunchcard.setCouponCode(results.getString("punchcard_code"));
                             
                             // Add current branch to the Business 
                             currentBusiness.insertPunchcard(punchcard_id, currentPunchcard);
                         }
                     } 
                 }
            });
        }
        catch (SQLException ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
    }
    
    private void refreshBusinessesFromDatabaseIfNecessary()
    {
        if (timeForRefresh())
        {
            try
            {
                currentBusinesses = new HashMap<String,Business>();
                
                getListOfBusinesses();
                
                lastRefreshTime = new Date();
            }
            catch (Exception ex)
            {
                SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
                lastRefreshTime = null;
                currentBusinesses = null;
            }
        }
    }
    
    public JSONArray getAllBusinesses()
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        JSONArray jsonBusinesses = new JSONArray();
        for (Map.Entry<String, Business> entry : currentBusinesses.entrySet())
        {
            jsonBusinesses.put(entry.getValue().getJSONOfBusiness());
        }
        return jsonBusinesses;
    }
    
    public JSONArray getAllEnabledBusinesses()
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        JSONArray jsonBusinesses = new JSONArray();
        for (Map.Entry<String, Business> entry : currentBusinesses.entrySet())
        {
            Business current = entry.getValue();
            if (current.getBusiEnabled())
            {
                jsonBusinesses.put(current.getJSONOfBusiness());    
            }
        }
        return jsonBusinesses;
    }
    
    /*
    public JSONObject getSingleBusiness(String business_id, boolean enabledOnly)
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        JSONObject jsonBusiness = null;
        Business current = currentBusinesses.get(business_id);
        if (current != null && (current.getBusiEnabled() || !enabledOnly))
        {
            jsonBusiness = current.getJSONOfBusiness();
        }
        return jsonBusiness;
    }
    
    public JSONArray getBusinessesCloseBy(double latitude, double longitude)
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        
        int region = GeoLocation.coordToRegion(latitude, longitude);
        ArrayList<Integer> surroundingRegions = GeoLocation.getSurroundingRegions(region);
        // Add self to region
        surroundingRegions.add(region);
        
        SimpleLogger.getInstance().info(currentClassName, "Regions: " + surroundingRegions.toString());
        
        JSONArray jsonBusinesses = new JSONArray();
        for (Map.Entry<String, Business> entry : currentBusinesses.entrySet())
        {
            Business current = entry.getValue();
            if (current.getBusiEnabled())
            {
                JSONObject jsonBusiness = current.getJSONOfBusiness(surroundingRegions);
                if (jsonBusiness != null)
                {
                    jsonBusinesses.put(jsonBusiness);   
                } 
            }
        }
        return jsonBusinesses;
    }
    */
    
    public Business getBusinessesCloseBy(String bizCode)
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        
        Business current = currentBusinesses.get(bizCode);
        
        // TODO: For now, assume a single business location
        Map.Entry<String, BusinessBranch> entry = current.getBranches().entrySet().iterator().next();
        BusinessBranch currentBranch = entry.getValue();
        double latitude = Double.parseDouble(currentBranch.getLatitude());
        double longitude = Double.parseDouble(currentBranch.getLongitude());
        
        ArrayList<Business> businessesCloseBy = getBusinessesCloseBy(bizCode, latitude, longitude);
        int sizeBusinessesCloseBy = businessesCloseBy.size();
        int index = (int)(Math.random() * sizeBusinessesCloseBy);
        
        return businessesCloseBy.get(index);
    }
    
    private ArrayList<Business> getBusinessesCloseBy(String bizCode, double latitude, double longitude)
    {        
        ArrayList<Business> arrayBusinesses = new ArrayList<Business>();
        for (Map.Entry<String, Business> entry : currentBusinesses.entrySet())
        {
            Business current = entry.getValue();
            if (current.getBusiEnabled())
            {
                arrayBusinesses.add(current);
            }
        }
        return arrayBusinesses;
    }
    
    // Disable cloning for singletons
    public Object clone() throws CloneNotSupportedException 
    {
        throw new CloneNotSupportedException();
    }
    
    // Singleton 
    public static synchronized BusinessesList getInstance() 
    {
        if (singleton == null) 
        {
            singleton = new BusinessesList();
        }
        return singleton;
    }
}
