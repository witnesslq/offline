package cn.jpush.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CalendarUtil {

	private static Logger logger = LoggerFactory.getLogger(CalendarUtil.class);
	
    public static SimpleDateFormat hdf = new SimpleDateFormat("yyyyMMddHH");
    
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean isPlatform(String platform) {
        return platform.equals("A") || platform.equals("I") || platform.equals("W");
    }

    public static boolean isAppkey(String appkey) {
        return appkey.toLowerCase().matches("[0-9a-f]{24}");
    }

    public static boolean isCorrectStatisDate(String date) {
        return date.matches("[0-9]{10}");
    }

    public static String getYYYYMMDD(long time) {
        return format("YYYYMMDD", time);
    }

    public static long transTimeStrToStamp(String time, SimpleDateFormat df) {
        long re_time = 0L;
        Date d;
        try {
            d = df.parse(time);
            re_time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    public static long dayToStamp(String time) {
        if (time.length() == 8) {
            int curYear = new GregorianCalendar().get(Calendar.YEAR);
            int year = Integer.parseInt(time.substring(0, 4));
            int month = Integer.parseInt(time.substring(4, 6)) - 1;
            int day = Integer.parseInt(time.substring(6, 8));

            if (year > 2010 && year <= curYear && month <= 11 && day <= 31) {
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                return cal.getTimeInMillis();
            }
        }
        return -1;
    }

    public static boolean isYYYYMMDD(long itime) {
        String time = String.valueOf(itime);
        if (time.length() == 8) {
            int curYear = new GregorianCalendar().get(Calendar.YEAR);

            int year = Integer.parseInt(time.substring(0, 4));
            int month = Integer.parseInt(time.substring(4, 6));
            int day = Integer.parseInt(time.substring(6, 8));
            if (year > 2010 && year <= curYear && month <= 11 && day <= 31) return true;
        }
        return false;
    }

    public static String format(String format, long time) {
        String result = null;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        if (format.equalsIgnoreCase("yyyymmddhhmm"))
            result = String.format("%d%02d%02d%02d%02d", year, month, day, hour, minute);
        else if (format.equalsIgnoreCase("yyyymmddhh"))
            result = String.format("%d%02d%02d%02d", year, month, day, hour);
        else if (format.equalsIgnoreCase("yyyymmdd"))
            result = String.format("%d%02d%02d", year, month, day);
        else if (format.equalsIgnoreCase("yyyymm"))
            result = String.format("%d%02d", year, month);
        else if (format.equalsIgnoreCase("yyyy")) result = String.format("%d", year);
        return result;
    }

    public static int numOfDays(long time) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int dayMinus(long stampA, long stampB) {
        GregorianCalendar big = new GregorianCalendar();
        GregorianCalendar small = new GregorianCalendar();
        if (stampA > stampB) {
            big.setTimeInMillis(stampA);
            small.setTimeInMillis(stampB);
        } else {
            big.setTimeInMillis(stampB);
            small.setTimeInMillis(stampA);
        }
        big.set(Calendar.HOUR_OF_DAY, 12);
        big.set(Calendar.MINUTE, 0);
        big.set(Calendar.MILLISECOND, 0);

        small.set(Calendar.HOUR_OF_DAY, 12);
        small.set(Calendar.MINUTE, 0);
        small.set(Calendar.MILLISECOND, 0);

        return (int) ((big.getTimeInMillis() - small.getTimeInMillis()) / (3600 * 24 * 1000));
    }

    public static GregorianCalendar toMonday(GregorianCalendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        dayOfWeek += dayOfWeek == 1 ? 6 : -1;
        cal.add(Calendar.DAY_OF_MONTH, 1 - dayOfWeek);
        return cal;
    }
    
    public static String getCondition(String statsDate, String type)
    {   
    	
    	String month = statsDate.substring(0, 6);
		int day = Integer.valueOf(statsDate.substring(6,8));
		int hour = Integer.valueOf(statsDate.substring(8));
		
		String res= "";
		
		if("hour".equals(type))
		{
			res = " month = "+ month +" and day = "+ day +" and hms between " + hour+"0000  and " + hour+"5959 ";
		}else if ("day".equals(type))
		{
			res = " month = "+ month +" and day = "+ day +" ";
		}else if ("month".equals(type))
		{
			res = " month = "+ month + " ";
		}
		
		return res;
    	
    }
    
    public static String utcTobst(String timeZone, String utcStr)
    {
    	String retStr = "";
    	try {
    		
    		if("UTC".equals(timeZone)){
    			
    			
    			Calendar ca=Calendar.getInstance();
    			
    			DateFormat fmtUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); 
    			fmtUTC.setTimeZone(TimeZone.getTimeZone("GMT")); 
    			Date dateUTC = fmtUTC.parse(utcStr);
    			logger.info("CalendarUtil.utcTobst :dateUTC =" + dateUTC);
    			
    			ca.setTime(dateUTC);
    			ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - 1);
    			logger.info("CalendarUtil.utcTobst :dateUTC-1 =" + ca.getTime());
        		
        		/*
        		Date dateLocal = null;
        		
        		try{
        			
        			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a",Locale.ENGLISH);
        			dateLocal = sdf.parse(dateUTC.toLocaleString()); 
        			logger.info("CalendarUtil.utcTobst :dateLocal =" + dateLocal);
        			
        		}catch (ParseException e) {

        			SimpleDateFormat fmtlocal=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		dateLocal = fmtlocal.parse(dateUTC.toLocaleString());
            		logger.info("CalendarUtil.utcTobst :dateLocal =" + dateLocal);
        		}	
        		*/	
        		
        		SimpleDateFormat fmtloca2=new SimpleDateFormat("yyyyMMddHH");	
        		retStr = fmtloca2.format(ca.getTime());
    		}
    			
    		
    		
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		return retStr;
    }
}
