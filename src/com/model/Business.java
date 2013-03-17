package com.model;

import java.util.ArrayList;

public class Business 
{    
    private String business_code;
	private String business_userid;
	private String name;
	private String desc;
	private String logo_path;
	private boolean busi_enabled;
	private String url_path;
	
	private ArrayList<BusinessBranch> businessBranches;
	private ArrayList<BusinessOffer> businessOffers;
	private ArrayList<Punchcard> punchcards;
	
	public String getBusinessCode()
    {
        return business_code;
    }
	
	public String getBusinessUserId()
	{
		return business_userid;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
	public String getLogoPath()
	{
		return logo_path;
	}
	
	public boolean getEnabled()
	{
		return busi_enabled;
	}
	
	public boolean getBusiEnabled()
	{
		return busi_enabled;
	}
	
	public ArrayList<BusinessBranch> getBranches()
	{
	    return businessBranches;
	}
	
	public ArrayList<Punchcard> getPunchcards()
    {
        return punchcards;
    }
	
	public ArrayList<BusinessOffer> getOffers()
    {
        return businessOffers;
    }
	
	public String getUrlPath()
	{
	    return url_path;
	}
	
	public void setBusinessCode(String business_code)
    {
        this.business_code = business_code;
    }
	
	public void setBusinessUserId(String business_userid)
	{
		this.business_userid = business_userid;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	
	public void setLogoPath(String logo_path)
	{
		this.logo_path = logo_path;
	}
	
	public void setBusiEnabled(String busi_enabled)
	{
		this.busi_enabled = busi_enabled.equalsIgnoreCase("Y");
	}
	
	public void setUrlPath(String url_path)
	{
	    this.url_path = url_path;
	}
	
	public void insertBranch(BusinessBranch branch)
	{
	    if (businessBranches == null)
	    {
	        businessBranches = new ArrayList<BusinessBranch>();
	    }
	    
	    businessBranches.add(branch);
	}
	
	public void setBranches(ArrayList<BusinessBranch> branches)
	{
	    businessBranches = branches;
	}
	
	public void insertPunchcard(Punchcard punchcard)
    {
        if (punchcards == null)
        {
            punchcards = new ArrayList<Punchcard>();
        }
        
        punchcards.add(punchcard);
    }
	
	public void insertOffer(BusinessOffer offer)
    {
        if (businessOffers == null)
        {
            businessOffers = new ArrayList<BusinessOffer>();
        }
        
        businessOffers.add(offer);
    }
	
	public void setOffers(ArrayList<BusinessOffer> offers)
    {
        this.businessOffers = offers;
    }
}
