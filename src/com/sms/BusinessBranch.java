package com.sms;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessBranch 
{
    private String currentClassName;
    
    private String address_line1;
    private String city;
    private String state;
    private String zipcode;
    private String latitude;
    private String longitude;
    private String contactno;
    private Integer region;
    
    public BusinessBranch() 
    {
        currentClassName = BusinessBranch.class.getSimpleName();
    }
    
    public String getContactNo()
    {
        return contactno;
    }
    
    public String getAddressLine()
    {
        return address_line1;
    }
    
    public String getCity()
    {
        return city;
    }
    
    public String getState()
    {
        return state;
    }
    
    public String getZipcode()
    {
        return zipcode;
    }
    
    public String getLatitude()
    {
        return latitude;
    }
    
    public String getLongitude()
    {
        return longitude;
    }
    
    public long getRegion()
    {
        return region;
    }
    
    public void setContactNo(String contactno)
    {
        this.contactno = contactno;
    }
    
    public void setAddressLine(String address_line1)
    {
        this.address_line1 = address_line1;
    }
    
    public void setCity(String city)
    {
        this.city = city;
    }
    
    public void setState(String state)
    {
        this.state = state;
    }
    
    public void setZipcode(String zipcode)
    {
        this.zipcode = zipcode;
    }
    
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }
    
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
    
    public void setRegion(Integer region)
    {
        this.region = region;
    }
    
    public boolean isInRegionList(ArrayList<Integer> regions)
    {
        boolean found = false;
        for (Integer current:regions)
        {
            if (current.equals(region))
            {
                found = true;
                break;
            }
        }
        return found;
    }
    
    public JSONObject getJSONOfBranch()
    {
        JSONObject jsonOutput= new JSONObject();

        try
        {
            jsonOutput.put("contactno", contactno);
            jsonOutput.put("address_line1", address_line1); 
            jsonOutput.put("city", city);   
            jsonOutput.put("state", state); 
            jsonOutput.put("zipcode", zipcode); 
            jsonOutput.put("longitude", longitude); 
            jsonOutput.put("latitude", latitude);   
        }
        catch (JSONException ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        
        return jsonOutput;
    }
}
