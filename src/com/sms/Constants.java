package com.sms;

import javax.servlet.ServletContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author user
 */
public class Constants 
{
    public final static String SQL_DATE_FORMAT = "yyyyMMdd";
    public final static String SQL_TIME_FORMAT = "HHmm";
    
    // Parameter names used for receiving input and providing output to client
    public static String TXTYPE_PARAMNAME = "txtype";
    public static String USERID_PARAMNAME = "user_id";
    public static String NAME_PARAMNAME = "username";
    public static String EMAIL_PARAMNAME = "email";
    public static String MOBILENO_PARAMNAME = "mobile_no";
    public static String PASSWORD_PARAMNAME = "password";
    public static String NEWPASSWORD_PARAMNAME = "new_password";
    public static String FBID_PARAMNAME = "fbid";
    public static String CREDIT_PARAMNAME = "credit";
    public static String DISABLED_PARAMNAME = "disabled";
    public static String REFERCODE_PARAMNAME = "refer_code";
    public static String USERCODE_PARAMNAME = "user_code";
    public static String SESSIONID_PARAMNAME = "sessionid";
    public static String PUNCHCARDID_PARAMNAME = "punchcardid";
    public static String BUSINESSID_PARAMNAME = "business_userid";
    public static String PRODUCTID_PARAMNAME = "product_id";
    public static String PROFILECREATED_PARAMNAME = "isprofile_created";
    public static String ZIPCODE_PARAMNAME = "zipcode";
    public static String BIZNAME_PARAMNAME = "business_name";
    public static String BIZINFO_PARAMNAME = "business_info";
    public static String AMOUNT_PARAMNAME = "Amount";
    
    // SimpleDB domain names
    public static String SUGGESTBUSINESSES_DOMAIN = "SuggestBusinesses";
    public static String VOTES_DOMAIN = "Votes";
    public static String CODES_DOMAIN = "Codes";
    public static String OFFERS_DOMAIN = "Offers";
    public static String COOPCODES_DOMAIN = "CoopCodes";

    // Used in database connection
    public static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static String JDBC_URL = "";
    public static String USERID = "";
    public static String PASSWORD = "";
    
    // MailChimp list ID
    public static String MAILCHIMP_LIST_ID = "";
    
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
            
            MAILCHIMP_LIST_ID = "e7350c242f";
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
            
            MAILCHIMP_LIST_ID = "4ded3248b9";
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