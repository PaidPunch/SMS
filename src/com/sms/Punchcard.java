package com.sms;

import org.json.JSONException;
import org.json.JSONObject;

public class Punchcard 
{
private String currentClassName;
    
    private String punchcardId;
    private String numPunches;
    private String valuePerPunch;
    private String cost;
    private String restrictionTime;
    private String category;
    private String expiryDays;
    private String minValue;
    private String couponCode;
    
    public Punchcard() 
    {
        currentClassName = Punchcard.class.getSimpleName();
    }
    
    public String getPunchcardId()
    {
        return punchcardId;
    }
    
    public String getNumPunches()
    {
        return numPunches;
    }
    
    public String getValuePerPunch()
    {
        return valuePerPunch;
    }
    
    public String getCouponCode()
    {
        return couponCode;
    }
    
    public String getCost()
    {
        return cost;
    }
    
    public String getRestrictionTime()
    {
        return restrictionTime;
    }
    
    public String getCategory()
    {
        return category;
    }
    
    public String getExpiryDays()
    {
        return expiryDays;
    }
    
    public String getMinValue()
    {
        return minValue;
    }
    
    public void setPunchcardId(String punchcardId)
    {
        this.punchcardId = punchcardId;
    }
    
    public void setNumPunches(String numPunches)
    {
        this.numPunches = numPunches;
    }
    
    public void setValuePerPunch(String valuePerPunch)
    {
        this.valuePerPunch = valuePerPunch;
    }
    
    public void setCost(String cost)
    {
        this.cost = cost;
    }
    
    public void setCouponCode(String couponCode)
    {
        this.couponCode = couponCode;
    }
    
    public void setRestrictionTime(String restrictionTime)
    {
        this.restrictionTime = restrictionTime;
    }
    
    public void setCategory(String category)
    {
        this.category = category;
    }
    
    public void setExpiryDays(String expiryDays)
    {
        this.expiryDays = expiryDays;
    }
    
    public void setMinValue(String minValue)
    {
        this.minValue = minValue;
    }
    
    public JSONObject getJSONOfOffer()
    {
        JSONObject jsonOutput= new JSONObject();

        try
        {
            jsonOutput.put("punchcardid", punchcardId);
            jsonOutput.put("numpunches", numPunches); 
            jsonOutput.put("valueperpunch", valuePerPunch);   
            jsonOutput.put("cost", cost); 
            jsonOutput.put("couponcode", couponCode); 
            jsonOutput.put("restrictiontime", restrictionTime);   
            jsonOutput.put("category", category); 
            jsonOutput.put("expirydays", expiryDays); 
            jsonOutput.put("minvalue", minValue);  
        }
        catch (JSONException ex)
        {
            SimpleLogger.getInstance().error(currentClassName, ex.getMessage());
        }
        
        return jsonOutput;
    }
}
