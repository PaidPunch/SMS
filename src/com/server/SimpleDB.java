package com.server;

import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.UpdateCondition;

import org.json.*;

public class SimpleDB 
{
	private static SimpleDB singleton;
	private AmazonSimpleDB sdb;
	private String currentClassName;
	
	// Private constructor
	private SimpleDB() 
	{
		try
		{
			sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
					this.getClass().getClassLoader().getResourceAsStream("com/server/AwsCredentials.properties")));
			sdb.setEndpoint("sdb.us-west-2.amazonaws.com");
			currentClassName = SimpleDB.class.getSimpleName();
		}
		catch (IOException ex)
		{
		    SimpleLogger.getInstance().error(currentClassName, "Error : " + ex.getMessage());
		}
	}
	
	// This is used for both inserts and updates
	public boolean updateItem(String domainName, String itemName, List<ReplaceableAttribute> attributes)
    {   
	    return updateItem(domainName, itemName, attributes, null);
    }
	
	public boolean updateItem(String domainName, String itemName, List<ReplaceableAttribute> attributes, UpdateCondition condition)
	{			
	    boolean success = true;
		try
		{
			PutAttributesRequest request = new PutAttributesRequest(domainName, itemName, attributes);
			
			if (condition != null)
			{
			    request.setExpected(condition);
			}
			
			sdb.putAttributes(request);
		} 
		catch (AmazonServiceException ase) 
		{
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonServiceException, which means your request made it "
	                + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
		    SimpleLogger.getInstance().error(currentClassName, "Error Message:    " + ase.getMessage());
		    SimpleLogger.getInstance().error(currentClassName, "HTTP Status Code: " + ase.getStatusCode());
		    SimpleLogger.getInstance().error(currentClassName, "AWS Error Code:   " + ase.getErrorCode());
		    SimpleLogger.getInstance().error(currentClassName, "Error Type:       " + ase.getErrorType());
		    SimpleLogger.getInstance().error(currentClassName, "Request ID:       " + ase.getRequestId());
		    success = false;
	    } 
		catch (AmazonClientException ace) 
	    {
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonClientException, which means the client encountered "
	                + "a serious internal problem while trying to communicate with SimpleDB, "
	                + "such as not being able to access the network.");
			SimpleLogger.getInstance().error(currentClassName, "Error Message: " + ace.getMessage());
			success = false;
	    }
		return success;
	}
	
	private List<Item> selectQuery(String queryString)
	{
		try
		{
			SelectRequest selectRequest = new SelectRequest(queryString);
			
			return sdb.select(selectRequest).getItems();
		} 
		catch (AmazonServiceException ase) 
		{
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonServiceException, which means your request made it "
	                + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
		    SimpleLogger.getInstance().error(currentClassName, "Error Message:    " + ase.getMessage());
		    SimpleLogger.getInstance().error(currentClassName, "HTTP Status Code: " + ase.getStatusCode());
		    SimpleLogger.getInstance().error(currentClassName, "AWS Error Code:   " + ase.getErrorCode());
		    SimpleLogger.getInstance().error(currentClassName, "Error Type:       " + ase.getErrorType());
		    SimpleLogger.getInstance().error(currentClassName, "Request ID:       " + ase.getRequestId());
	    } 
		catch (AmazonClientException ace) 
	    {
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonClientException, which means the client encountered "
	                + "a serious internal problem while trying to communicate with SimpleDB, "
	                + "such as not being able to access the network.");
		    SimpleLogger.getInstance().error(currentClassName, "Error Message: " + ace.getMessage());
	    }

		return null;
	}
	
	public List<Item> retrieveFromSimpleDB(String queryString, boolean singleItem)
    {
        List<Item> retrievedItems = null;
        
        SimpleLogger.getInstance().info(currentClassName, queryString);
        retrievedItems = selectQuery(queryString);
        if (retrievedItems != null && retrievedItems.size() == 0)
        {
            // Warn that there appear to be multiple active coupons for this business code
            SimpleLogger.getInstance().warn(currentClassName, "NoItemsRetrieved|Query:" + queryString);
        }
        else if (retrievedItems != null && singleItem && retrievedItems.size() > 1)
        {
            // Warn that there appear to be multiple active coupons for this business code
            SimpleLogger.getInstance().warn(currentClassName, "MultipleItemsRetrievedUnexpectedly|Query:" + queryString);
        }
        
        return retrievedItems;
    }
	
	public JSONArray selectQueryAsJSON(String queryString)
	{
		// Results consist of an array of JSONObjects. Each JSONObject
		// contains the attributes for a single item. 
		JSONArray results = new JSONArray();
		
		try
		{
			SelectRequest selectRequest = new SelectRequest(queryString);
			
	        for (Item item : sdb.select(selectRequest).getItems()) 
	        {
	        	try
	        	{        		
	        		JSONObject currentItem = new JSONObject();
	        		
	        		// Store item name first
	        		currentItem.put("itemName", item.getName());
	        		
	        		// Store remaining attributes
	                for (Attribute attribute : item.getAttributes()) 
	                {
	                	currentItem.put(attribute.getName(), attribute.getValue());
	                }
	                
	                // Put current JSONObject (item) into larger results array
	                results.put(currentItem);
	        	}
	        	catch (JSONException ex)
	        	{
	        	    SimpleLogger.getInstance().error(currentClassName, "Error : " + ex.getMessage());
	        	}
	        }
		} 
		catch (AmazonServiceException ase) 
		{
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonServiceException, which means your request made it "
	                + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
		    SimpleLogger.getInstance().error(currentClassName, "Error Message:    " + ase.getMessage());
		    SimpleLogger.getInstance().error(currentClassName, "HTTP Status Code: " + ase.getStatusCode());
		    SimpleLogger.getInstance().error(currentClassName, "AWS Error Code:   " + ase.getErrorCode());
		    SimpleLogger.getInstance().error(currentClassName, "Error Type:       " + ase.getErrorType());
		    SimpleLogger.getInstance().error(currentClassName, "Request ID:       " + ase.getRequestId());
	    } 
		catch (AmazonClientException ace) 
	    {
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonClientException, which means the client encountered "
	                + "a serious internal problem while trying to communicate with SimpleDB, "
	                + "such as not being able to access the network.");
		    SimpleLogger.getInstance().error(currentClassName, "Error Message: " + ace.getMessage());
	    }

		return results;
	}
	
	public void deleteItem(String domainName, String itemName)
	{
		try
		{
			sdb.deleteAttributes(new DeleteAttributesRequest(domainName, itemName));
		} 
		catch (AmazonServiceException ase) 
		{
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonServiceException, which means your request made it "
	                + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
			SimpleLogger.getInstance().error(currentClassName, "Error Message:    " + ase.getMessage());
			SimpleLogger.getInstance().error(currentClassName, "HTTP Status Code: " + ase.getStatusCode());
			SimpleLogger.getInstance().error(currentClassName, "AWS Error Code:   " + ase.getErrorCode());
			SimpleLogger.getInstance().error(currentClassName, "Error Type:       " + ase.getErrorType());
			SimpleLogger.getInstance().error(currentClassName, "Request ID:       " + ase.getRequestId());
	    } 
		catch (AmazonClientException ace) 
	    {
		    SimpleLogger.getInstance().error(currentClassName, "Caught an AmazonClientException, which means the client encountered "
	                + "a serious internal problem while trying to communicate with SimpleDB, "
	                + "such as not being able to access the network.");
			SimpleLogger.getInstance().error(currentClassName, "Error Message: " + ace.getMessage());
	    }
	}
	
	// Disable cloning for singletons
	public Object clone() throws CloneNotSupportedException 
	{
		throw new CloneNotSupportedException();
	}
	
	// Singleton 
	public static synchronized SimpleDB getInstance() 
	{
		if (singleton == null) 
		{
			singleton = new SimpleDB();
		}
		return singleton;
	}
}
