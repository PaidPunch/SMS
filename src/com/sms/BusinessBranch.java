package com.sms;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessBranch 
{
    private String currentClassName;
    
    private String branchId;
    private String businessId;
    private String bizCode;
    private String address;
    private String city;
    private String state;
    private String zipcode;
    private String latitude;
    private String longitude;
    private String contactno;
    private Integer region;
    private String group;
    
    public BusinessBranch() 
    {
        currentClassName = BusinessBranch.class.getSimpleName();
    }
    
    public String getBranchId()
    {
        return branchId;
    }

    public String getBusinessId()
    {
        return businessId;
    }
    
    public String getBizCode()
    {
        return bizCode;
    }
    
    public String getContactNo()
    {
        return contactno;
    }
    
    public String getAddressLine()
    {
        return address;
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
    
    public String getGroup()
    {
        return group;
    }
    
    public void setBranchId(String branchId)
    {
        this.branchId = branchId;
    }
    
    public void setBusinessId(String businessId)
    {
        this.businessId = businessId;
    }
    
    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }
    
    public void setContactNo(String contactno)
    {
        this.contactno = contactno;
    }
    
    public void setAddressLine(String address)
    {
        this.address = address;
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
    
    public void setGroup(String group)
    {
        this.group = group;
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
            jsonOutput.put("address", address); 
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
