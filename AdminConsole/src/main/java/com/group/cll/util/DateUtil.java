package com.group.cll.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ���ڴ�������
 * @author lizi
 *
 */
public class DateUtil {
	
	public static final String FULL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String NOMAL_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SHORT_DATE_FORMAT = "yyyy-MM";
	  
    /**
     * ��ȡ��ǰʱ�䣬��ȷ������
     *
     * @return 
     */
    public static String getNowTime() {
        return getNowTime(DateUtil.FULL_DATETIME_FORMAT);
    }
    
    /**
     * ����ָ�����ڸ�ʽ��ȡ��ǰʱ�䣬��ȷ������
     * @param sFormat ���ڸ�ʽ
     * @return
     */
    public static String getNowTime(String sFormat) {
    	SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
    	Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	return sdf.format(c.getTime());
    }
    
    /**
     * ����ָ�����ڸ�ʽ��ȡ��ǰ����
     * @param sFormat ���ڸ�ʽ
     * @return
     */
    public static String getToday(String sFormat) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        String s1 = sdf.format(gc.getTime());
        return s1;
    }

    /**
     * ��ȡ��ǰ����
     * @return
     */
    public static String getToday() {
        return getToday(DateUtil.NOMAL_DATE_FORMAT);
    }

    /**
     * ������ڶ�����������Ƽ��㣬�����졢���µ�
     *
     * @param date
     * @param iYear
     * @param iMonth
     * @param iDate
     * @param sFormat
     * @return
     */
    public static String getRelativeDate(java.util.Date date, int iYear, int iMonth, int iDate, String sFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);            //�����ʽ
        GregorianCalendar gc = new GregorianCalendar();            //

        gc.setTime(date);           //����ʱ��

        gc.add(Calendar.YEAR, iYear);               //�������
        gc.add(Calendar.MONTH, iMonth);
        gc.add(Calendar.DATE, iDate);

        return sdf.format(gc.getTime());
    }

    public static String getRelativeDate(String sDate, int iYear, int iMonth, int iDate, String sFormat)
    {
    	//System.out.println("getRelativeDate:"+sDate);
    	if(sDate==null) return null;
        Date date = parseString2Date(sDate, DateUtil.NOMAL_DATE_FORMAT );
        return getRelativeDate(date, iYear, iMonth, iDate, sFormat);
    }

    public static String getRelativeDate(java.util.Date date, int iYear, int iMonth, int iDate) {
        return getRelativeDate(date, iYear, iMonth, iDate, DateUtil.NOMAL_DATE_FORMAT);
    }

    public static String getRelativeDate(String sDate, int iYear, int iMonth, int iDate)
    {
        return getRelativeDate(sDate, iYear, iMonth, iDate, DateUtil.NOMAL_DATE_FORMAT);
    }

    public static String getRelativeMonth(java.util.Date date, int iYear, int iMonth, String s) {
        return getRelativeDate(date, iYear, iMonth, 0, s);
    }

    public static String getRelativeMonth(String sDate, int iYear, int iMonth, String s)
    {
        return getRelativeDate(sDate, iYear, iMonth, 0, s);
    }

    public static String getRelativeMonth(java.util.Date date, int iYear, int iMonth) {
        return getRelativeDate(date, iYear, iMonth, 0, DateUtil.SHORT_DATE_FORMAT);
    }

    public static String getRelativeMonth(String sDate, int iYear, int iMonth)
    {
        return getRelativeDate(sDate, iYear, iMonth, 0, DateUtil.SHORT_DATE_FORMAT);
    }

    //�ж��Ƿ�Ϊ��ĩ
    public static boolean monthEnd(String sEndDate)
    {
    	String sTommorow = getRelativeDate(sEndDate, 0, 0, 1);
    	if(sTommorow == null){ return false;}
        if (sTommorow.substring(8, 10).equals("01"))
            return true;
        else
            return false;

    }

    /**
     * ת�������ַ����ĸ�ʽ
     * @param sDate
     * @param format1
     * @return
     */
    public static String formatDate(String sDate, String format) {
    	
    	return formatDate(sDate,NOMAL_DATE_FORMAT,format);
    }
    
    /**
     * ת�������ַ����ĸ�ʽ
     * @param sDate
     * @param format1
     * @param format2
     * @return
     */
    public static String formatDate(String sDate,String format1 , String format2) {
        
    	Date date = parseString2Date(sDate,format1);
    	
    	return new SimpleDateFormat(format2).format(date);
    }

    /**
     * �������ַ�ת��ΪDate����
     * @param datestring
     * @return
     */
    public static Date parseString2Date(String datestring)
    {
        return parseString2Date(datestring, DateUtil.NOMAL_DATE_FORMAT);
    }

    /**
     * �������ַ���ָ����ʽת��ΪDate����
     *
     * @param datestring �����ַ��� 
     * @param format ���ڸ�ʽ
     * @return
     */
    public static Date parseString2Date(String datestring, String format)
    {
    	if (datestring==null) return null;
        try {
            Date date = new SimpleDateFormat(format).parse(datestring);
            return date;
        } catch (Exception e) {
        	System.out.println("����ת��'" + datestring + "'ת���쳣" + e);
        	return null;
        }
    }
    
    public static int daysBetween(String smdate,String bdate) {  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();    
        try {
			cal.setTime(sdf.parse(smdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}    
        long time1 = cal.getTimeInMillis();                 
        try {
			cal.setTime(sdf.parse(bdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }
}
