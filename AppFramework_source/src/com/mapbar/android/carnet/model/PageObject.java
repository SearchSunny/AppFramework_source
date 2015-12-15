package com.mapbar.android.carnet.model;

import android.view.View;

public class PageObject
{
	private View mView;
	private BasePage mBasePage;
	private int mPosition;
	//private boolean isAttachedToWindow;
	private int mIndex;
	
	public PageObject(int position, View view, BasePage page)
	{
		this.mPosition = position;
		this.mView = view;
		this.mBasePage = page;
	}
	
	public View getView()
	{
		return this.mView;
	}
	
	public BasePage getPage()
	{
		return this.mBasePage;
	}
	
	public int getPosition()
	{
		return this.mPosition;
	}
	
	public int getIndex()
	{
		return this.mIndex;
	}
	
	public void setIndex(int index)
	{
		this.mIndex = index;
	}
	
	/*
	public void onAttachedToWindow(int flag, int position)
	{
		if(isAttachedToWindow)
			return;
		isAttachedToWindow = true;
		mBasePage.onAttachedToWindow(flag, position);
	}
	*/
	
	public void destroy()
	{
		mBasePage.onDestroy();
		mView = null;
		mBasePage = null;
	}
}
