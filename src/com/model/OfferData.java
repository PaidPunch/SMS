package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.server.Constants;
import com.server.SimpleDB;
import com.server.SimpleLogger;
import com.server.Utility;

public class OfferData 
{
    private String currentClassName;
    private HashMap<String,String> offerMap;
    private ArrayList<HashMap<String,String>> offerRecords;
    private ArrayList<HashMap<String,String>> redeemRecords;
    
    public OfferData()
    {
        currentClassName = OfferData.class.getSimpleName();
        offerMap = new HashMap<String,String>();
        offerRecords = new ArrayList<HashMap<String,String>>();
        redeemRecords = new ArrayList<HashMap<String,String>>();
    }
    
    public void insertOfferData(String name, String value)
    {
        offerMap.put(name, value);
    }
    
    public void retrieveAssociatedData()
    {
        retrieveOfferRecords();
        retrieveRedeemRecords();
    }
    
    public HashMap<String,String> getOfferMap()
    {
        return offerMap;
    }
    
    public ArrayList<HashMap<String,String>> getOfferRecords()
    {
        return offerRecords;
    }
    
    public ArrayList<HashMap<String,String>> getRedeemRecords()
    {
        return redeemRecords;
    }
    
    private void retrieveOfferRecords()
    {
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "select * from `" + Constants.OFFERSRECORD_DOMAIN + 
                "` where `offerId` = '" + offerMap.get("textId") + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList != null)
        {           
            for (Item currentItem : queryList)
            {
                HashMap<String,String> offerRecord = new HashMap<String,String>();
                offerRecord.put("offerRecordId", currentItem.getName());
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    String name = attribute.getName();
                    if (name.equals("expiryDatetime") || name.equals("createdDatetime"))
                    {
                        String date = Utility.convertToJavascriptDatetimeFormat(attribute.getValue());
                        offerRecord.put(name, date);
                    }
                    else
                    {
                        offerRecord.put(name, attribute.getValue());    
                    }
                }   
                offerRecords.add(offerRecord);
            }
        }
    }    
    
    private void retrieveRedeemRecords()
    {
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "select * from `" + Constants.REDEEMRECORD_DOMAIN + 
                "` where `offerId` = '" + offerMap.get("textId") + "'";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList != null)
        {           
            for (Item currentItem : queryList)
            {
                HashMap<String,String> redeemRecord = new HashMap<String,String>();
                redeemRecord.put("redeemRecordId", currentItem.getName());
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    String name = attribute.getName();
                    if (name.equals("expiryDatetime") || name.equals("createdDatetime"))
                    {
                        String date = Utility.convertToJavascriptDatetimeFormat(attribute.getValue());
                        redeemRecord.put(name, date);
                    }
                    else
                    {
                        redeemRecord.put(name, attribute.getValue());    
                    }
                }   
                redeemRecords.add(redeemRecord);
            }
        }
    }
}
