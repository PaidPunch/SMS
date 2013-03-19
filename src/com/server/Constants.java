package com.server;

import javax.servlet.ServletContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author user
 */
public class Constants 
{    
    // SimpleDB domain names
    public static String SUGGESTBUSINESSES_DOMAIN = "SuggestBusinesses";
    public static String VOTES_DOMAIN = "Votes";
    public static String CODES_DOMAIN = "Codes";
    public static String OFFERS_DOMAIN = "Offers";
    public static String COOPCODES_DOMAIN = "CoopCodes";
    public static String OFFERSRECORD_DOMAIN = "OffersRecord";
    public static String REDEEMRECORD_DOMAIN = "RedeemRecord";
    public static String FEEDBACK_DOMAIN = "CoopFeedback";
    
    public static String BUSINESS_DOMAIN = "CoopBusiness";
    public static String BUSINESSBRANCH_DOMAIN = "CoopBusinessBranch";
    public static String BUSINESSOFFER_DOMAIN = "CoopBusinessOffer";

    // Used in database connection
    public static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static String JDBC_URL = "";
    public static String USERID = "";
    public static String PASSWORD = "";
    
    // MailChimp list ID
    public static String MAILCHIMP_LIST_ID = "";
    
    // Root URL
    public static String URL_ROOT = "http://sms.paidpunch.com/";
    
    private static boolean initialized = false;
    
    private static boolean isProduction() 
    {
        Object o;
        try 
        {
           o = (new InitialContext()).lookup("java:comp/env/isProduction");
        }  
        catch (NamingException e) 
        {
           o = Boolean.FALSE; // assumes FALSE if the value isn't declared
        }
        return o == null ? Boolean.FALSE : (Boolean) o;
     }
    
    private static void initEndPoints()
    {
        if (isProduction())
        {
            SimpleLogger.getInstance().info(Constants.class.getSimpleName(), "Initializing production server endpoints");
            
            // Settings for production server
            JDBC_URL = "jdbc:mysql://paidpunchprod.csepczasc6nf.us-west-2.rds.amazonaws.com:3306/paidpunchprod";
            USERID = "paidpunchprod";
            PASSWORD = "Biscuit-1";
            
            SUGGESTBUSINESSES_DOMAIN = "SuggestBusinesses";
            VOTES_DOMAIN = "Votes";
            CODES_DOMAIN = "Codes";
            OFFERS_DOMAIN = "Offers";
            COOPCODES_DOMAIN = "CoopCodes";
            OFFERSRECORD_DOMAIN = "OffersRecord";
            REDEEMRECORD_DOMAIN = "RedeemRecord";
            FEEDBACK_DOMAIN = "CoopFeedback";
            
            BUSINESS_DOMAIN = "CoopBusiness";
            BUSINESSBRANCH_DOMAIN = "CoopBusinessBranch";
            BUSINESSOFFER_DOMAIN = "CoopBusinessOffer";
            
            MAILCHIMP_LIST_ID = "e7350c242f";
            
            URL_ROOT = "http://sms.paidpunch.com/";
        }
        else
        {
            SimpleLogger.getInstance().info(Constants.class.getSimpleName(), "Initializing test server endpoints");
            
            // Settings for test server
            JDBC_URL = "jdbc:mysql://paidpunchtest.csepczasc6nf.us-west-2.rds.amazonaws.com:3306/paidpunchtest";
            USERID = "paidpunch";
            PASSWORD = "Biscuit-1";
            
            SUGGESTBUSINESSES_DOMAIN = "SuggestBusinessesTest";
            VOTES_DOMAIN = "VotesTest";
            CODES_DOMAIN = "CodesTest";
            OFFERS_DOMAIN = "OffersTest";
            COOPCODES_DOMAIN = "CoopCodesTest";
            OFFERSRECORD_DOMAIN = "OffersRecordTest";
            REDEEMRECORD_DOMAIN = "RedeemRecordTest";
            FEEDBACK_DOMAIN = "CoopFeedbackTest";
            
            BUSINESS_DOMAIN = "CoopBusinessTest";
            BUSINESSBRANCH_DOMAIN = "CoopBusinessBranchTest";
            BUSINESSOFFER_DOMAIN = "CoopBusinessOfferTest";
            
            MAILCHIMP_LIST_ID = "4ded3248b9";
            
            URL_ROOT = "http://smstest.paidpunch.com/";
        }
    }

    public static void init(ServletContext context) 
    {        
        if (!initialized)
        {
            initEndPoints();
            initialized = true;
        }
    }
}