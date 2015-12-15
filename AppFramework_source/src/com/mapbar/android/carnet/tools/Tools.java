package com.mapbar.android.carnet.tools;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

public class Tools
{
//    private static double DEGREE_E6_TO_METER = 0.1111;
    
	public static String formatSecondStr(int second)
	{
		int time = second / 60;
		String strTime = "";
		if(time == 0)
			strTime = "小于1";
		else if(time < 60)
			strTime = "" + time;
		else if(time < 24 * 60)
			strTime = time / 60 + "小时" + time % 60;
		else
			strTime = time / 1440 + "天" + (time % 1440) / 60 + "小时" + (time % 1440) % 60;
		return strTime;
	}
    
	public static String formatKmStr(int meter, String color)
	{
		if(meter > 1000)
		{
			double dis = (double)((int)((double)meter / 100 + 0.5)) / 10;
			if(color != null)
				return "<font color="+color+">" + dis + "</font>公里";
			else
				return dis + "公里";
		}
		else
		{
			if(color != null)
				return "<font color="+color+">" + meter + "</font>米";
			else
				return meter + "米";
		}
	}
	
	public static String md5(String str)
	{
		return hexdigest(str);
	}
	
	private static String hexdigest(String string)
	{
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(string.getBytes());
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		}
		catch (Exception e) {
		}
		return s;
	}
	
	public static String formatStr(String str)
	{
		if(!TextUtils.isEmpty(str))
			return str;
		return "";
	}
	
	public static String formatTime(long time)
	{
		if(time == 0)
			return "";
		Date myDate = new Date(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(myDate);
		int myYear = cal.get(Calendar.YEAR);//获取年份
		int myMonth = cal.get(Calendar.MONTH);//获取月份
		int myDay = cal.get(Calendar.DATE);//获取日
		int myWeekofYear = cal.get(Calendar.WEEK_OF_YEAR);
        int myWeekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;

		Date curDate = new Date(System.currentTimeMillis());
		cal.setTime(curDate);
		int curYear = cal.get(Calendar.YEAR);//获取年份
		int curMonth = cal.get(Calendar.MONTH);//获取月份
		int curDay = cal.get(Calendar.DATE);//获取日
		int curWeekofYear = cal.get(Calendar.WEEK_OF_YEAR);
		if(myYear == curYear)
		{
			if(myMonth == curMonth)
			{
				if(myDay == curDay)
					return "今天";
				else if(myDay + 1 == curDay)
					return "昨天";
				else if(myWeekofYear == curWeekofYear)
				{
					String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
					if(myWeekIndex < 0)
						myWeekIndex = 0;
					return weeks[myWeekIndex];
				}
			}
			return (myMonth + 1) + "月" + myDay + "日";
		}
		return myYear + "年" + (myMonth + 1) + "月" + myDay + "日";
	}
	
	public static String getWeek(Date date)
	{  
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;  
        if(week_index<0){  
            week_index = 0;  
        }   
        return weeks[week_index];  
    }
}
