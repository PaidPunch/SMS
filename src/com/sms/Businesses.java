package com.sms;

import com.model.*;
import com.server.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class Businesses extends HttpServlet 
{
    private static final long serialVersionUID = -6688121421872277580L;
    private String currentClassName;

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
    
    private UUID insertBusiness(HttpServletRequest request)
    {
        String name = request.getParameter("name");
        String desc = request.getParameter("desc");
        String category = request.getParameter("category");
        String url = request.getParameter("url");
        String enabled = request.getParameter("enabled");
        
        UUID businessId = UUID.randomUUID();
        
        String currentDatetime = Utility.getCurrentDatetimeInUTC();
        
        // Add a http:// in front of url if one isn't there already
        if (!url.contains("http://") && !url.contains("https://"))
        {
            url = "http://" + url;
        }
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("name", name, true));
        listAttributes.add(new ReplaceableAttribute("desc", desc, true));
        listAttributes.add(new ReplaceableAttribute("category", category, true));
        listAttributes.add(new ReplaceableAttribute("url_path", url, true));
        listAttributes.add(new ReplaceableAttribute("enabled", enabled, true));
        listAttributes.add(new ReplaceableAttribute("createdDatetime", currentDatetime, true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.BUSINESS_DOMAIN, businessId.toString(), listAttributes);
        
        return businessId;
    }
    
    private HashMap<String,String> getGeocodeLocation(String address)
    {
        HashMap<String,String> response = null;
        try
        {
            String rawJSONString = callGeocodeAPI(address);
            if (rawJSONString != null)
            {
                JSONObject geocodeResponse = new JSONObject(rawJSONString);
                JSONArray results = geocodeResponse.getJSONArray("results");
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
                int region = GeoLocation.coordToRegion(latitude, longitude);
                response = new HashMap<String,String>();
                response.put("latitude", Double.toString(latitude));
                response.put("longitude", Double.toString(longitude));
                response.put("region", Integer.toString(region));
            }            
        }
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(currentClassName, e.getMessage());
        }
        return response;
    }
    
    private String callGeocodeAPI(String address)
    {
        URL url;
        HttpURLConnection connection = null;  
        try 
        {
              String templateURL = "http://maps.googleapis.com/maps/api/geocode/json?address=<ADDRESS>&sensor=true";
              String targetURL = templateURL.replace("<ADDRESS>", address);
            
              //Create connection
              url = new URL(targetURL);
              connection = (HttpURLConnection)url.openConnection();
              connection.setRequestMethod("GET"); 
                    
              connection.setUseCaches (false);
              connection.setDoOutput(true);
              connection.setReadTimeout(10000);
                        
              connection.connect();
    
              //Get Response    
              int responseCode = connection.getResponseCode();
              if (responseCode == 200)
              {
                  InputStream is = connection.getInputStream();
                  BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                  String line;
                  StringBuffer response = new StringBuffer(); 
                  while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                  }
                  rd.close();
                  return response.toString();
              }
              else
              {
                  return null;
              }
        } 
        catch (Exception e) 
        {
              e.printStackTrace();
              return null;

        } 
        finally 
        {
              if(connection != null) 
              {
                connection.disconnect(); 
              }
        }
    }
    
    private void insertBranch(HttpServletRequest request, UUID businessId)
    {            
        UUID branchId = UUID.randomUUID();
        
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String group = request.getParameter("group");
        String code = request.getParameter("code");
        
        String geocodeAddressString = address.replace(" ", "+");
        HashMap<String,String> location = getGeocodeLocation(geocodeAddressString);
        
        String numericOnlyPhone = phone.replaceAll("[^0-9]", "");
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("address", address, true));
        listAttributes.add(new ReplaceableAttribute("contactno", numericOnlyPhone, true));
        listAttributes.add(new ReplaceableAttribute("group", group.toUpperCase(), true));
        listAttributes.add(new ReplaceableAttribute("bizCode", code.toUpperCase(), true));
        listAttributes.add(new ReplaceableAttribute("businessid", businessId.toString(), true));
        listAttributes.add(new ReplaceableAttribute("latitude", location.get("latitude"), true));
        listAttributes.add(new ReplaceableAttribute("longitude", location.get("longitude"), true));
        listAttributes.add(new ReplaceableAttribute("region", location.get("region"), true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.BUSINESSBRANCH_DOMAIN, branchId.toString(), listAttributes);
    }
    
    private void insertOffer(HttpServletRequest request, UUID businessId)
    {            
        UUID offerId = UUID.randomUUID();
        
        String offer = request.getParameter("offer");
        String couponcode  = request.getParameter("couponcode");
        
        List<ReplaceableAttribute> listAttributes = new ArrayList<ReplaceableAttribute>();
        listAttributes.add(new ReplaceableAttribute("offertext", offer, true));
        listAttributes.add(new ReplaceableAttribute("couponcode", couponcode, true));
        listAttributes.add(new ReplaceableAttribute("businessid", businessId.toString(), true));
        
        SimpleDB sdb = SimpleDB.getInstance();
        sdb.updateItem(Constants.BUSINESSOFFER_DOMAIN, offerId.toString(), listAttributes);
    }
    
    @Override  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {          
        ArrayList<Business>  businesses = BusinessesList.getInstance().getAllBusinessesFromSimpleDB();
        JSONArray businessesArray = BusinessesList.getInstance().getJSONArrayOfBusinesses(businesses);
        Utility.jsonResponse(request, response, businessesArray);
    } 
    
    @Override  
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {          
        UUID businessId = insertBusiness(request);
        insertBranch(request, businessId);
        insertOffer(request, businessId);
        
        response.getWriter().print("Business created.");  
    } 
}
