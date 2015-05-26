package cn.jpush.tool;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Time is a public class about time like
 * 
 * @author wangxb
 * 
 */
public class Time {
    /**
     * convert day string to timeStamp date string
     * 
     * @param time consists of year,month,day as 20140612
     * @return timeStamp based millisecond
     */
    public static long dayToStamp(String time) {
        return time.length() == 8 ? getTimeStamp(time) : -1;
    }

    /**
     * convert date string to timeStamp
     * 
     * @param time contain year at least
     * @return timeStamp based millisecond or -1
     */
    public static long getTimeStamp(String time) {
        if (time.length() < 4) return -1;
        String template = "201001010000";// 2010.01.01 00:00
        time += template.substring(time.length());

        int year = Integer.parseInt(time.substring(0, 4));
        int month = (Integer.parseInt(time.substring(4, 6)) - 1) % 12;
        int day = Integer.parseInt(time.substring(6, 8)) % 32;
        int hour = Integer.parseInt(time.substring(8, 10)) % 24;
        int minute = Integer.parseInt(time.substring(10)) % 60;

        return new GregorianCalendar(year, month, day, hour, minute).getTimeInMillis();
    }

    /**
     * get the start time and end time according to the date string, some examples of date string:<br>
     * "2014": from 2014.01.01 00:00:00 to 2015.01.01 00:00:00 "2014030223": from 2014.03.02
     * 23:00:00 to 2014.03.03 00:00:00
     * 
     * @param date
     * @return array with two timeStamp
     */
    public static long[] getTimeInterval(String date) {
        int fileAdd = 0;
        switch (date.length()) {
            case 12:
                fileAdd = Calendar.MINUTE;
                break;
            case 10:
                fileAdd = Calendar.HOUR_OF_DAY;
                break;
            case 8:
                fileAdd = Calendar.DAY_OF_MONTH;
                break;
            case 6:
                fileAdd = Calendar.MONTH;
                break;
            case 4:
                fileAdd = Calendar.YEAR;
                break;
            default:
                return null;
        }
        long min = getTimeStamp(date);
        GregorianCalendar maxCal = new GregorianCalendar();
        maxCal.setTimeInMillis(min);
        maxCal.add(fileAdd, 1);
        long max = maxCal.getTimeInMillis();

        return new long[] {min, max};
    }

    /**
     * test if a long number is a legal timeStamp<br>
     * 
     * @param itime a long number
     * @return true if legal timeStamp else false
     */
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

    /**
     * convert timeStamp to day string<br>
     * date string consists of year,month,day as 20140612
     * 
     * @param time timeStamp based millisecond
     * @return string of day
     */
    public static String getYYYYMMDD(long time) {
        return format("YYYYMMDD", time);
    }

    /**
     * covert timeStamp to date string as format<br>
     * format ignoreCase:<br>
     * YYYY: year<br>
     * YYYYMM: [year][month]<br>
     * YYYYMMDD: [year][month][day]<br>
     * YYYYMMDDHH: [year][month][day][hour]<br>
     * YYYYMMDDHHMM: [year][month][day][hour][minute]
     * 
     * @param format string to describe date string
     * @param time timeStamp
     * @return date string described as format
     */
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

    /**
     * get number of days for month that the given timeStamp is in
     * 
     * @param time the timeStamp
     * @return the number of days
     */
    public static int numOfDays(long time) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * get the number of days between two days
     * 
     * @param stampA one day
     * @param stampB another day
     * @return
     */
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

    /**
     * get the Monday of the week which the given day is in
     * 
     * @param cal the given GregorianCalendar
     * @return GregorianCalendar of Monday
     */
    public static GregorianCalendar toMonday(GregorianCalendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        dayOfWeek += dayOfWeek == 1 ? 6 : -1;
        cal.add(Calendar.DAY_OF_MONTH, 1 - dayOfWeek);
        return cal;
    }
}
