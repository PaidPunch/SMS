package com.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public final class Utility 
{
    private static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    public static String getRandomAlphaNumericCode(int len) 
    {
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++) 
        {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }
    

    public static void jsonResponse(HttpServletRequest request, HttpServletResponse response, JSONObject responseMap)
            throws IOException 
    {
        try 
        {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            
            String res = responseMap.toString();
            
            out.print(res);
            SimpleLogger.getInstance().info(Utility.class.getSimpleName(), res);
        } 
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        }

    }
    
    public static void jsonResponse(HttpServletRequest request, HttpServletResponse response, JSONArray responseArray)
            throws IOException 
    {
        try 
        {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            
            String res = responseArray.toString();
            
            out.print(res);
            SimpleLogger.getInstance().info(Utility.class.getSimpleName(), res);
        } 
        catch (Exception e) 
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e.getMessage());
        }

    }
    
    public static int compareFloats(double f1, double f2, float delta)
    {
        if (Math.abs(f1 - f2) < delta)
        {
             return 0;
        } else
        {
            if (f1 < f2)
            {
                return -1;
            } else {
                return 1;
            }
        }
    }
    
    public static String getCurrentDatetimeInUTC()
    {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa z");
        datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return datetimeFormat.format(new java.util.Date().getTime()); 
    }
    
    public static String standardizePhoneNumber(String originalPhone)
    {
        String newPhone = originalPhone;
        // if phone number is of length 10, then it's just straight numbers
        if (newPhone.length() == 10)
        {
            newPhone = newPhone.substring(0, 3) + "-" + newPhone.substring(3, 6) + "-" + newPhone.substring(6);
        }
        return newPhone;
    }

    /**
     * Uses <code>0.001f</code> for delta.
     */
    public static int compareFloats(double f1, double f2)
    {
         return compareFloats(f1, f2, 0.001f);
    }
    
    public static double round(double value, int places) 
    {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, +days); // minus number would decrement the days
        return cal.getTime();
    }

    private final static char[] hexChar =
    {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static int dateDiff(java.util.Calendar d1, java.util.Calendar d2) {
        /*
         * if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end java.util.Calendar swap = d1; d1 = d2; d2
         * = swap; }
         */
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR) -
                d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);
        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        SimpleLogger.getInstance().info(Utility.class.getSimpleName(), "Utility.dateDiff()-> d1={}" + d1 + " ,d2={}" + d2 + " ,days={}" + days);
        return days;
    }

    public static int dateDiff(long d1, long d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(d1);
        cal2.setTimeInMillis(d2);
        return dateDiff(cal1, cal2);
    }

    public static int dateDiff(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return dateDiff(cal1, cal2);
    }

    public static boolean isPastDate(String p_strDate, String p_strFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(p_strFormat);
        Date date = null;
        try {
            date = sdf.parse(p_strDate);
        } catch (Exception e) {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e);
        }
        Calendar cal_date = Calendar.getInstance();
        cal_date.setTime(date);
        cal_date.set(Calendar.SECOND, 0);
        cal_date.set(Calendar.MILLISECOND, 0);
        // Date now = new Date();
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        return cal_date.before(now);
    }

    public static boolean isToday(String p_strDate, String p_strFormat) {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(p_strFormat);
        Date date = null;
        try {
            date = sdf.parse(p_strDate);
        } catch (Exception e) {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e);
        }
        boolean result = (dateDiff(today, date) == 0);
        return result;
    }

    /**
     * This function is used for find dates are in same week. The p_date2 must be greater than p_date1
     */
    public static boolean isInSameWeek(Date p_date1, Date p_date2) {
        boolean result = false;
        Calendar l_cal1 = Calendar.getInstance();
        l_cal1.setTimeInMillis(p_date1.getTime());
        Calendar l_cal2 = Calendar.getInstance();
        l_cal2.setTimeInMillis(p_date2.getTime());

        int year1 = l_cal1.get(Calendar.YEAR);
        int year2 = l_cal2.get(Calendar.YEAR);

        /**
         * To find out two dates in same week if two dates in same year then week of year for both date must be same.
         */
        if (year1 == year2) {
            int week1 = l_cal1.get(Calendar.WEEK_OF_YEAR);
            int week2 = l_cal2.get(Calendar.WEEK_OF_YEAR);

            result = (week1 == week2);
            /**
             * If dates are not in same year then year2 = year1 +1. Month of first date must be DEC and Month of 2nd
             * date must be JAN. First date must be last week of Month DEC and last week must be not a full week.
             * Similarly 2nd must be first week of Month JAN and first week must be not a full week.
             */
        } else if (year2 == year1 + 1) {
            int week1 = l_cal1.get(Calendar.WEEK_OF_YEAR);
            int week2 = l_cal2.get(Calendar.WEEK_OF_YEAR);

            result = (week1 == week2);
        }
        return result;
    }

    /**
     * This function is used for find 2nd date in next week. The p_date2 must be greater than p_date1
     */
    public static boolean isInNextWeek(Date p_date1, Date p_date2) {

        boolean result = false;
        Calendar l_cal1 = Calendar.getInstance();
        l_cal1.setTimeInMillis(p_date1.getTime());
        Calendar l_cal2 = Calendar.getInstance();
        l_cal2.setTimeInMillis(p_date2.getTime());
        int year1 = l_cal1.get(Calendar.YEAR);
        int year2 = l_cal2.get(Calendar.YEAR);
        /**
         * To find out two dates in same week if two dates in same year then week of year for 2nd must greater than
         * first
         */
        if (year1 == year2) {
            int week1 = l_cal1.get(Calendar.WEEK_OF_YEAR);
            int week2 = l_cal2.get(Calendar.WEEK_OF_YEAR);

            result = (week2 > week1);
            if (!result) {
                result = (week1 != week2) && (p_date2.after(p_date1));
            }
            /**
             * If dates are not in same year then year2 = year1 +1. Here to check two dates must not in same weeks
             */
        } else if (year2 == year1 + 1) {
            int week1 = l_cal1.get(Calendar.WEEK_OF_YEAR);
            int week2 = l_cal2.get(Calendar.WEEK_OF_YEAR);

            result = (week2 != week1) && (p_date2.after(p_date1));
        } else if (year2 > year1) {
            result = true;
        }
        return result;
    }

    /**
     * This method use for compress the bytes
     * 
     * @Param p_bytes byte data
     * @return ziped byte data
     */
    public static final synchronized byte[] compress(byte[] p_bytes) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // ZipOutputStream zout = new ZipOutputStream(out);
        JarOutputStream zout = new JarOutputStream(out);
        zout.setLevel(9);
        zout.putNextEntry(new ZipEntry("0"));
        zout.write(p_bytes);
        zout.closeEntry();
        byte[] compressed = out.toByteArray();
        zout.close();

        return compressed;
    }

    /**
     * This method use for decompress the compressed bytes
     * 
     * @Param p_compressed_bytes compressed byte data
     * @return decompressed byte data
     */
    public static final synchronized byte[] decompress(byte[] p_compressed_bytes) throws
            IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(p_compressed_bytes);
        // ZipInputStream zin = new ZipInputStream(in);
        JarInputStream zin = new JarInputStream(in);
        byte[] buffer = new byte[1024];
        int offset = -1;
        while ((offset = zin.read(buffer)) != -1) {
            out.write(buffer, 0, offset);
        }
        byte[] l_decompressed_bytes = out.toByteArray();
        out.close();
        zin.close();

        return l_decompressed_bytes;
    }

    public static java.sql.Timestamp getSQLTimestamp(Date p_date) {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(p_date.getTime());

        return timestamp;
    }

    public static java.sql.Timestamp getSQLTimestamp(String p_strdate, String p_strFormat)
            throws ParseException {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = l_sdf.parse(p_strdate);

        return getSQLTimestamp(l_date);
    }

    public static java.sql.Time getSQLTime(Date p_date) {
        java.sql.Time time = new java.sql.Time(p_date.getTime());
        return time;
    }

    public static java.sql.Time getSQLTime(String p_strdate, String p_strFormat)
            throws ParseException {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = l_sdf.parse(p_strdate);

        return getSQLTime(l_date);
    }

    public static java.sql.Date getSQLDate(Date p_date) {
        java.sql.Date date = new java.sql.Date(p_date.getTime());
        return date;
    }

    public static java.sql.Date getSQLDate(String p_strdate, String p_strFormat)
            throws ParseException {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = l_sdf.parse(p_strdate);
        return getSQLDate(l_date);
    }

    public static String convertToSQLFormat(String p_strdate, String p_strFormat)
            throws ParseException {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = l_sdf.parse(p_strdate);
        SimpleDateFormat l_sdf_new = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return l_sdf_new.format(l_date);
    }

    public static String convertTimeToSQLFormat(String p_strTime, String p_strFormat)
            throws ParseException {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = l_sdf.parse(p_strTime);
        SimpleDateFormat l_sdf_new = new SimpleDateFormat("HHMM");
        return l_sdf_new.format(l_date);
    }

    public static String convertDateToSQLFormat(String p_strDate, String p_strFormat)
            throws ParseException {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = l_sdf.parse(p_strDate);
        SimpleDateFormat l_sdf_new = new SimpleDateFormat("YYYYMMDD");
        return l_sdf_new.format(l_date);
    }

    public static Date getDateTime(String p_strDateTime, String p_strFormat) {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        Date l_date = null;
        try
        {
            l_date = l_sdf.parse(p_strDateTime);
        } catch (ParseException e)
        {
            SimpleLogger.getInstance().error(Utility.class.getSimpleName(), e);
        }
        SimpleLogger.getInstance().info(Utility.class.getSimpleName(), "Utility.getDateTime()->p_strDateTime={}" + p_strDateTime + " ,p_strFormat={}"
                + p_strFormat + " ,l_date={}" + l_date);
        return l_date;
    }

    public static boolean isBeforeDateTime(Date time1, Date time2) {
        boolean l_result = false;
        SimpleLogger.getInstance().info(Utility.class.getSimpleName(), "time1::{}" + time1 + "-time2{}" + time2);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(time1);
        cal2.setTime(time2);
        l_result = cal1.before(cal2);
        SimpleLogger.getInstance().info(Utility.class.getSimpleName(), "Utility.isBeforeDateTime()-> time1={}" + time1 + " ,time2={}" + time2 + " ,l_result={}"
                + l_result);
        return l_result;
    }

    public static Date HoureAdd(Date time1, int min) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(time1);
        // int hour= cal1.HOUR;
        cal1.add(Calendar.MINUTE, min);
        return cal1.getTime();
    }

    public static boolean isAfterDateTime(Date time1, Date time2) {
        boolean l_result = false;
        SimpleLogger.getInstance().info(Utility.class.getSimpleName(), "time1::{}" + time1 + "-time2{}" + time2);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(time1);
        cal2.setTime(time2);
        l_result = cal1.after(cal2);
        SimpleLogger.getInstance().info(Utility.class.getSimpleName(), "Utility.isAfterDateTime()-> time1={}" + time1 + " ,time2={}" + time2 + " ,l_result={}"
                + l_result);
        return l_result;
    }

    public static String getFormatedDate(String p_strFormat) {
        SimpleDateFormat l_sdf = new SimpleDateFormat(p_strFormat);
        String l_strdate = l_sdf.format(new Date());
        return l_strdate;
    }

    /**
     * This method is use for loading bytes data from file.
     * 
     * @Param p_strFile binary file name
     * @Return bytes array of binary file data.
     */
    public byte[] loadBinaryData(String p_strFile) throws IOException {
        byte[] l_data = null;
        File file = new File(p_strFile);
        l_data = new byte[(int) file.length()];

        FileInputStream in_File = new FileInputStream(file);
        in_File.read(l_data, 0, (int) file.length());
        in_File.close();
        return l_data;
    }

    public static String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String toHexString(byte[] p_data) {
        StringBuffer l_buffer = new StringBuffer(p_data.length * 2);
        for (int i = 0; i < p_data.length; i++) {
            l_buffer.append(hexChar[(p_data[i] & 0xf0) >>> 4]);
            l_buffer.append(hexChar[(p_data[i] & 0x0f)]);
        }
        return l_buffer.toString();
    }
    
    public static String convertInputStreamToString(InputStream in)
    {
    	StringBuilder sb = new StringBuilder();
    	try
    	{
    		//read it with BufferedReader
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	String line;
        	while ((line = br.readLine()) != null) 
        	{
        		sb.append(line);
        	}	
    	}
    	catch (IOException ex) 
		{
    	    SimpleLogger.getInstance().error(Utility.class.getSimpleName(), ex.getMessage());
		}
    	return sb.toString();
    }
    
    public static double HaverSineDistance(double lat1, double lng1, double lat2, double lng2) 
    {
        double earthRadius = 3958.75;
        
        // convert to radians
        lat1 = Math.toRadians(lat1);
        lng1 = Math.toRadians(lng1);
        lat2 = Math.toRadians(lat2);
        lng2 = Math.toRadians(lng2);

        double dlon = lng2 - lng1;
        double dlat = lat2 - lat1;

        double a = Math.pow((Math.sin(dlat/2)),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2),2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;
    } 
}