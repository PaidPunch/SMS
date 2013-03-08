package com.sms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseAccess 
{    
    public interface ResultSetHandler
    {
        void handle(ResultSet results, Object returnObj) throws SQLException;
    }
    
    public static Connection createConnection() 
    {
        Connection l_conn = null;
        try 
        {
            Class.forName(Constants.JDBC_DRIVER).newInstance();
            l_conn = DriverManager.getConnection(Constants.JDBC_URL, Constants.USERID, Constants.PASSWORD);
            return l_conn;
        } 
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e);
            e.printStackTrace();
        }
        return l_conn;
    }
    
    private static PreparedStatement prepareSQLStatement(Connection conn, String queryString, ArrayList<String> parameters)
    {
        PreparedStatement prepStat = null;
        try
        {
            prepStat = conn.prepareStatement(queryString);  
            if (parameters != null)
            {
                int index = 1;
                for (String param: parameters)
                {
                    prepStat.setString(index, param);
                    index++;
                }   
            }
        }
        catch (SQLException e) 
        {
            try 
            {
                prepStat.close();
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            } catch (Exception ex) 
            {
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), ex.getMessage());
            }
        }
        return prepStat;
    }
    
    public static boolean updateDatabaseWithExistingConnection(Connection conn, String queryString, ArrayList<String> parameters) throws SQLException 
    {
        PreparedStatement prepStat = null;
        try 
        {
            // Creating SQL query string
            prepStat = prepareSQLStatement(conn, queryString, parameters);
            
            SimpleLogger.getInstance().info(DatabaseAccess.class.getSimpleName(), "" + prepStat);
            int result = prepStat.executeUpdate();
            prepStat.close();
            return (result != 0);
        } 
        catch (SQLException e) 
        {
            try 
            {
                prepStat.close();
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            } catch (Exception ex) 
            {
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), ex.getMessage());
            }
        }
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateDatabase(String queryString, ArrayList<String> parameters) throws SQLException 
    {
        Connection conn = null;
        boolean success = false;
        try 
        {
            conn = createConnection();
            success = updateDatabaseWithExistingConnection(conn, queryString, parameters);
            conn.close();
        } 
        catch (SQLException e) 
        {
            try 
            {
                conn.close();
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            } catch (Exception ex) 
            {
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), ex.getMessage());
            }
        }
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
        return success;
    }
    
    public static void queryDatabaseCustomWithExistingConnection(Connection conn, String queryString, ArrayList<String> parameters, Object returnObj, ResultSetHandler handler) throws SQLException 
    {
        PreparedStatement prepStat = null;
        ResultSet results = null;
        try 
        {            
            // Creating SQL query string
            prepStat = prepareSQLStatement(conn, queryString, parameters);
            
            SimpleLogger.getInstance().info(DatabaseAccess.class.getSimpleName(), "" + prepStat);
            results = prepStat.executeQuery();
            handler.handle(results, returnObj);
            results.close();
            prepStat.close();
        } 
        catch (SQLException e) 
        {
            try 
            {
                prepStat.close();
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            } catch (Exception ex) {
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), ex.getMessage());
            }
        }
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }
    
    // This function allows the caller to customize a delegate function that performs special processing on the
    // results before returning.
    public static void queryDatabaseCustom(String queryString, ArrayList<String> parameters, Object returnObj, ResultSetHandler handler) throws SQLException 
    {
        Connection conn = null;
        try 
        {
            conn = createConnection();  
            queryDatabaseCustomWithExistingConnection(conn, queryString, parameters, returnObj, handler);
            conn.close();
        } 
        catch (SQLException e) 
        {
            try 
            {
                conn.close();
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            } catch (Exception ex) 
            {
                SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), ex.getMessage());
            }
        }
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }

    }

    // This function is used if the caller simply wishes to get an array of hashmaps containing
    // the list of results from the query.
    public static ArrayList<HashMap<String,String>> queryDatabase(String queryString, ArrayList<String> parameters)
    {
        ArrayList<HashMap<String,String>> resultsArray = new ArrayList<HashMap<String,String>>();
        try
        {
            ResultSetHandler handler = new ResultSetHandler()
            {
                public void handle(ResultSet results, Object returnObj) throws SQLException
                {                        
                    // The cast here is a well-known one, so the suppression is OK
                    @SuppressWarnings("unchecked")
                    ArrayList<HashMap<String,String>> resultsArray = (ArrayList<HashMap<String,String>>)returnObj;
                    while(results.next())
                    {                        
                         // Populate information
                         HashMap<String,String> current = new HashMap<String,String>();
                         
                         ResultSetMetaData rsmd = results.getMetaData();
                         int numColumns = rsmd.getColumnCount();
                         for(int i=1; i <= numColumns; i++)
                         {
                             current.put(rsmd.getColumnName(i),results.getString(i));
                         }
                         
                         resultsArray.add(current);
                    }
                }
            };
            
            DatabaseAccess.queryDatabaseCustom(queryString, parameters, resultsArray, handler);
        }
        catch (SQLException ex)
        {
            SimpleLogger.getInstance().error(DatabaseAccess.class.getSimpleName(), ex.getMessage());
        }
        return resultsArray;
    }
}
