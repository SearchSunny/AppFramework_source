package com.mapbar.android.carnet.model;

import java.util.ArrayList;


public final class ProviderResult
{
	private String responseStatusLine;
	private int mAllCount;
	private long mActTime;
	private String mReason;
	private String mToast;
	private int mCode;
	private Object mObject;
	private ArrayList<Object> mObjArrs;
	
	public ProviderResult()
	{
	}
	
	public void setAllCount(int allCount)
	{
		mAllCount = allCount;
	}
	
	public int getAllCount()
	{
		return this.mAllCount;
	}
	
	public void setResponseCode(int code)
	{
		mCode = code;
	}
	
	public int getResponseCode()
	{
		return this.mCode;
	}
	
	public void setResponseStr(String str)
	{
		responseStatusLine = str;
	}
	
	public String getResponseStr()
	{
		return this.responseStatusLine;
	}
	
	public void setActTime(long time)
	{
		this.mActTime = time;
	}
	
	public long getActTime()
	{
		return this.mActTime;
	}
	
	public void setReason(String reason)
	{
		this.mReason = reason;
	}
	
	public String getReason()
	{
		return this.mReason;
	}
	
	public void setObject(Object obj)
	{
		this.mObject = obj;
	}
	
	public Object getObject()
	{
		return this.mObject;
	}
	
	public void setToast(String toast)
	{
		this.mToast = toast;
	}
	
	public String getToast()
	{
		return this.mToast;
	}
	
	public void setObjArrs(ArrayList<Object> arrs)
	{
		this.mObjArrs = arrs;
	}
	
	public ArrayList<Object> getObjArrs()
	{
		return this.mObjArrs;
	}
}
