package com.model;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.server.Constants;
import com.server.SimpleDB;
import com.server.SimpleLogger;

public class OfferData 
{
    private String currentClassName;
    private HashMap<String,String> offerMap;
    private HashMap<String,String> offerRecords;
    private HashMap<String,String> redeemRecords;
    
    public OfferData()
    {
        currentClassName = OfferData.class.getSimpleName();
        offerMap = new HashMap<String,String>();
        offerRecords = new HashMap<String,String>();
        redeemRecords = new HashMap<String,String>();
    }
    
    public void insertOfferData(String name, String value)
    {
        offerMap.put(name, value);
    }
    
    public void getOfferRecords()
    {
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "select * from `" + Constants.OFFERSRECORD_DOMAIN + 
                "` where `offerId` = '" + offerMap.get("offerId") + "`";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList != null)
        {           
            for (Item currentItem : queryList)
            {
                offerRecords.put("offerRecordId", currentItem.getName());
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    offerRecords.put(attribute.getName(), attribute.getValue());
                }   
            }
        }
    }
    
    public void getRedeemRecords()
    {
        SimpleDB sdb = SimpleDB.getInstance();
        String allQuery = "select * from `" + Constants.REDEEMRECORD_DOMAIN + 
                "` where `offerId` = '" + offerMap.get("offerId") + "`";
        SimpleLogger.getInstance().info(currentClassName, allQuery);
        List<Item> queryList = sdb.retrieveFromSimpleDB(allQuery, true);
        if (queryList != null)
        {           
            for (Item currentItem : queryList)
            {
                redeemRecords.put("redeemRecordId", currentItem.getName());
                for (Attribute attribute : currentItem.getAttributes()) 
                {
                    redeemRecords.put(attribute.getName(), attribute.getValue());
                }   
            }
        }
    }
}
