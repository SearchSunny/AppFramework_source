package com.mapbar.android.carnet.model;

import java.util.ArrayList;


public class FilterObj
{
	private int mFlag;
	private Object mTag;
	private ArrayList<Object> mObjs;
	private String mKey;
	private int action;
	
	public FilterObj()
	{
	}
	
	public FilterObj(int flag)
	{
		this.mFlag = flag;
	}
	
	public String getKey() {
		return mKey;
	}

	public void setKey(String key)
	{
		this.mKey = key;
	}

	public void setAction(int action)
	{
		this.action = action;
	}
	
	public int getAction()
	{
		return this.action;
	}
	
	public ArrayList<Object> getObjs()
	{
		return mObjs;
	}

	public void setObjs(ArrayList<Object> objs)
	{
		this.mObjs = objs;
	}

	public FilterObj setFlag(int flag)
	{
		this.mFlag = flag;
		return this;
	}
	
	public int getFlag()
	{
		return this.mFlag;
	}
	
	public FilterObj setTag(Object tag)
	{
		this.mTag = tag;
		return this;
	}
	
	public Object getTag()
	{
		return this.mTag;
	}
	
	public void destroy()
	{
	}
}
