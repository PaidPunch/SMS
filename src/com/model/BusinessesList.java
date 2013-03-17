package com.model;

import com.server.*;
import com.server.DatabaseAccess.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

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
    
    private void getListOfBusinessesFromDatabase()
    {
        String queryString = "SELECT b.business_userid,b.business_code,b.business_name,b.buss_desc,a.contactno,b.logo_path,b.busi_enabled,b.url_path," + 
                "a.address_id,a.address_line1,a.city,a.state,a.zipcode,a.longitude,a.latitude,a.region," +
                "p.punch_card_id,p.no_of_punches_per_card,p.value_of_each_punch,p.selling_price_of_punch_card,p.restriction_time,p.punchcard_category,p.expirydays,p.minimumvalue,p.punchcard_code " +
                "FROM business_users b, bussiness_address a, punch_card p " +
                "WHERE b.business_userid = a.business_id AND b.business_userid = p.business_userid AND " +
                "a.city in ('Bellevue', 'Kirkland', 'Redmond') AND " +
                "b.business_userid != 506;";
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
                         
                         // Create a new branch
                         BusinessBranch currentBranch = new BusinessBranch();
                         
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
                         currentBusiness.insertBranch(currentBranch);  
                         
                         Punchcard currentPunchcard = new Punchcard();
                         
                         // Populate offer information
                         currentPunchcard.setPunchcardId(results.getString("punch_card_id"));
                         currentPunchcard.setNumPunches(results.getString("no_of_punches_per_card"));
                         currentPunchcard.setValuePerPunch(results.getString("value_of_each_punch"));
                         currentPunchcard.setCost(results.getString("selling_price_of_punch_card"));
                         currentPunchcard.setRestrictionTime(results.getString("restriction_time"));
                         currentPunchcard.setCategory(results.getString("punchcard_category"));
                         currentPunchcard.setExpiryDays(results.getString("expirydays"));
                         currentPunchcard.setMinValue(results.getString("minimumvalue"));
                         currentPunchcard.setCouponCode(results.getString("punchcard_code"));
                         
                         // Add current branch to the Business 
                         currentBusiness.insertPunchcard(currentPunchcard);
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
                
                getListOfBusinessesFromDatabase();
                
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
    
    public Business getABusinessCloseByV1(String bizCode)
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        
        double latitude = 0;
        double longitude = 0;
        Business current = currentBusinesses.get(bizCode);
        if (current != null)
        {
            // TODO: For now, assume a single business location
            BusinessBranch currentBranch = current.getBranches().get(0);
            latitude = Double.parseDouble(currentBranch.getLatitude());
            longitude = Double.parseDouble(currentBranch.getLongitude());
        }
        else
        {
            String queryString = "select `latitude`, `longitude` from `" + Constants.COOPCODES_DOMAIN + "` where `code` = '" + bizCode + "'";
            SimpleDB sdb = SimpleDB.getInstance();
            List<Item> items = sdb.retrieveFromSimpleDB(queryString, true);
            
            if (items.size() > 0)
            {                
                // get the first item
                Item currentItem = items.get(0);
                
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    if (attribute.getName().equals("latitude"))
                    {
                        latitude = Double.parseDouble(attribute.getValue());
                    }
                    else if (attribute.getName().equals("longitude"))
                    {
                        longitude = Double.parseDouble(attribute.getValue());
                    }
                }
            }
            else 
            {
                return null;
            }
        }
        
        ArrayList<Business> businessesCloseBy = getBusinessesCloseBy(bizCode, latitude, longitude);
        if (businessesCloseBy.size() > 0)
        {
            int sizeBusinessesCloseBy = businessesCloseBy.size();
            int index = (int)(Math.random() * sizeBusinessesCloseBy);
            
            return businessesCloseBy.get(index);    
        }
        else
        {
            return null;
        }
    }
    
    public Business getABusinessCloseByV2(String bizCode)
    {
        double latitude = 0;
        double longitude = 0;
        String group = null;
        boolean found = false;
        
        Business selectedBusiness = null;
        Business currentBiz = getBusinessByBizCodeV2(bizCode);
        // No such biz code found, so check list of custom coop codes
        if (currentBiz != null)
        {
            BusinessBranch branch = currentBiz.getBranches().get(0);
            latitude = Double.parseDouble(branch.getLatitude());
            longitude = Double.parseDouble(branch.getLongitude());
            group = branch.getGroup();
            found = true;
        }
        else
        {
            String queryString = "select `latitude`, `longitude`, `group` from `" + Constants.COOPCODES_DOMAIN + "` where `code` = '" + bizCode + "'";
            SimpleDB sdb = SimpleDB.getInstance();
            List<Item> items = sdb.retrieveFromSimpleDB(queryString, true);
            
            if (items.size() > 0)
            {                
                // get the first item
                Item currentItem = items.get(0);
                
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    if (attribute.getName().equals("latitude"))
                    {
                        latitude = Double.parseDouble(attribute.getValue());
                    }
                    else if (attribute.getName().equals("longitude"))
                    {
                        longitude = Double.parseDouble(attribute.getValue());
                    }
                    else if (attribute.getName().equals("group"))
                    {
                        group = attribute.getValue();
                    }
                }
                
                found = true;
            }
            else 
            {
                return null;
            }
        }
        
        if (found)
        {
            SimpleLogger.getInstance().info(currentClassName, "V2FoundBusiness|Latitude:" + latitude + "|Longitude:" + longitude + "|Group:" + group);
            
            SimpleDB sdb = SimpleDB.getInstance();
            String allQuery = "select * from `" + Constants.BUSINESSBRANCH_DOMAIN + 
                    "` where `group` = '" + group + 
                    "' and `bizCode` != '" + bizCode + "'";
            List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
            if (queryList.size() > 0)
            { 
                ArrayList<Business> businesses = new ArrayList<Business>();
                for (Item currentItem : queryList)
                {
                    BusinessBranch currentBranch = createBusinessBranchObject(currentItem);
                    if (currentBranch != null)
                    {
                        Business newBiz = retrieveSingleBusinessObjectWithBranch(currentBranch);
                        if (newBiz != null)
                        {
                            businesses.add(newBiz);
                        }
                    }
                }
                
                if (businesses.size() > 0)
                {
                    int businessesSize = businesses.size();
                    int index = (int)(Math.random() * businessesSize);
                    selectedBusiness = businesses.get(index);   
                }
            }
        }
        return selectedBusiness;
    }
    
    private ArrayList<Business> getBusinessesCloseBy(String bizCode, double latitude, double longitude)
    {        
        ArrayList<Business> arrayBusinesses = new ArrayList<Business>();
        for (Map.Entry<String, Business> entry : currentBusinesses.entrySet())
        {
            Business current = entry.getValue();
            if (current.getBusiEnabled() && !current.getBusinessCode().equals(bizCode))
            {
                arrayBusinesses.add(current);
            }
        }
        return arrayBusinesses;
    }
    
    private BusinessBranch getBranchFromBusinessCode(String bizCode)
    {
        BusinessBranch branch = null;
        
        // Biz codes are associated with branches, so start there
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "SELECT * FROM `" + Constants.BUSINESSBRANCH_DOMAIN + 
                "` where `bizCode` = '" + bizCode + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList.size() > 0)
        {            
            Item currentItem = queryList.get(0);
            branch = createBusinessBranchObject(currentItem);
        }
        
        return branch;
    }
    
    private ArrayList<BusinessBranch> retrieveBusinessBranchesObject(String businessId)
    {
        ArrayList<BusinessBranch> branches = null;
        
        // Biz codes are associated with branches, so start there
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "SELECT * FROM `" + Constants.BUSINESSBRANCH_DOMAIN + 
                "` where `businessid` = '" + businessId + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, false);
        if (queryList != null)
        {            
            branches = new ArrayList<BusinessBranch>();
            for (Item currentItem : queryList)
            {
                BusinessBranch branch = createBusinessBranchObject(currentItem);
                branches.add(branch);
            }
        }
        
        return branches;
    }
    
    private BusinessBranch createBusinessBranchObject(Item currentItem)
    {        
        BusinessBranch currentBranch = new BusinessBranch();
        currentBranch.setBranchId(currentItem.getName());
        for (Attribute attribute : currentItem.getAttributes()) 
        {
            // Populate business information
            if (attribute.getName().equals("businessid"))
            {           
                currentBranch.setBusinessId(attribute.getValue());    
            }
            else if (attribute.getName().equals("address"))
            {           
                currentBranch.setAddressLine(attribute.getValue());    
            }
            else if (attribute.getName().equals("longitude"))
            {                
                currentBranch.setLongitude(attribute.getValue());
            }
            else if (attribute.getName().equals("latitude"))
            {                
                currentBranch.setLatitude(attribute.getValue());
            }
            else if (attribute.getName().equals("contactno"))
            {                
                currentBranch.setContactNo(attribute.getValue());
            }
            else if (attribute.getName().equals("region"))
            {                
                currentBranch.setRegion(Integer.getInteger(attribute.getValue()));
            }
            else if (attribute.getName().equals("group"))
            {                
                currentBranch.setGroup(attribute.getValue());
            }
        }
        return currentBranch;
    }
    
    private ArrayList<BusinessOffer> retrieveBusinessOffersObject(String businessId)
    {
        ArrayList<BusinessOffer> offers = null;
        
        // Biz codes are associated with branches, so start there
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "SELECT * FROM `" + Constants.BUSINESSOFFER_DOMAIN + 
                "` where `businessid` = '" + businessId + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, false);
        if (queryList.size() > 0)
        {            
            offers = new ArrayList<BusinessOffer>();
            for (Item currentItem : queryList)
            {
                BusinessOffer offer = createBusinessOfferObject(currentItem);
                offers.add(offer);
            }
        }
        
        return offers;
    }
    
    private BusinessOffer createBusinessOfferObject(Item currentItem)
    {        
        BusinessOffer currentOffer = new BusinessOffer();
        currentOffer.setOfferId(currentItem.getName());
        for (Attribute attribute : currentItem.getAttributes()) 
        {
            // Populate business information
            if (attribute.getName().equals("offertext"))
            {           
                currentOffer.setOfferText(attribute.getValue());    
            }
            else if (attribute.getName().equals("offercode"))
            {           
                currentOffer.setOfferText(attribute.getValue());    
            }
        }
        return currentOffer;
    }
    
    private Business retrieveSingleBusinessObjectWithBranch(BusinessBranch branch)
    {
        Business currentBiz = null;
        
        // Biz codes are associated with branches, so start there
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "SELECT * FROM `" + Constants.BUSINESS_DOMAIN + 
                "` where itemName() = '" + branch.getBusinessId() + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList.size() > 0)
        {            
            Item currentItem = queryList.get(0);
            currentBiz = createBusinessObject(currentItem);
            
            // set branches for current business
            currentBiz.insertBranch(branch);
            
            // set offers for current business
            ArrayList<BusinessOffer> offers = retrieveBusinessOffersObject(branch.getBusinessId());
            currentBiz.setOffers(offers);
        }
        
        return currentBiz;
    }
    
    public Business retrieveSingleBusinessObjectWithBusinessId(String businessId)
    {
        Business currentBiz = null;
        
        // Biz codes are associated with branches, so start there
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "SELECT * FROM `" + Constants.BUSINESS_DOMAIN + 
                "` where itemName() = '" + businessId + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList.size() > 0)
        {            
            Item currentItem = queryList.get(0);
            currentBiz = createBusinessObject(currentItem);
            
            // set branches for current business
            ArrayList<BusinessBranch> branches = retrieveBusinessBranchesObject(businessId);
            currentBiz.setBranches(branches);
            
            // set offers for current business
            ArrayList<BusinessOffer> offers = retrieveBusinessOffersObject(businessId);
            currentBiz.setOffers(offers);
        }
        
        return currentBiz;
    }
    
    private Business createBusinessObject(Item currentItem)
    {
        Business currentBusiness = new Business();
        currentBusiness.setBusinessUserId(currentItem.getName());
        for (Attribute attribute : currentItem.getAttributes()) 
        {
            // Populate business information
            if (attribute.getName().equals("name"))
            {           
                currentBusiness.setName(attribute.getValue());    
            }
            else if (attribute.getName().equals("desc"))
            {                
                currentBusiness.setDesc(attribute.getValue());
            }
            else if (attribute.getName().equals("logo_path"))
            {                
                currentBusiness.setLogoPath(attribute.getValue());
            }
            else if (attribute.getName().equals("enabled"))
            {                
                currentBusiness.setBusiEnabled(attribute.getValue());
            }
            else if (attribute.getName().equals("url_path"))
            {                
                currentBusiness.setUrlPath(attribute.getValue());
            }
            else if (attribute.getName().equals("category"))
            {                
                currentBusiness.setUrlPath(attribute.getValue());
            }
        }
        return currentBusiness;
    }
    
    public Business getBusinessByBizCodeV1(String bizCode)
    {
        // Refresh the data if necessary
        refreshBusinessesFromDatabaseIfNecessary();
        
        Business foundBusiness = null;
        for (Map.Entry<String, Business> entry : currentBusinesses.entrySet())
        {
            Business current = entry.getValue();
            if (current.getBusiEnabled() && current.getBusinessUserId().equals(bizCode))
            {
                foundBusiness = current;
                break;
            }
        }
        
        return foundBusiness;
    }
    
    public Business getBusinessByBizCodeV2(String bizCode)
    {        
        Business foundBusiness = null;
        BusinessBranch branch = getBranchFromBusinessCode(bizCode);
        if (branch != null)
        {
            foundBusiness = retrieveSingleBusinessObjectWithBranch(branch);
        }
        
        return foundBusiness;
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
