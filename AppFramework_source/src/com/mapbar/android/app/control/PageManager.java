package com.mapbar.android.app.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mapbar.android.Configs;
import com.mapbar.android.R;
import com.mapbar.android.app.view.MainPage;
import com.mapbar.android.app.view.NextPage;
import com.mapbar.android.carnet.model.ActivityInterface;
import com.mapbar.android.carnet.model.BasePage;
import com.mapbar.android.carnet.model.PageObject;

public class PageManager
{
	private Context mContext;
	private LayoutInflater mInflater;
	private ActivityInterface mActivityInterface;
	
	public PageManager(Context context, ActivityInterface activityInterface)
	{
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mActivityInterface = activityInterface;
	}
	
	public PageObject createPage(int index)
	{
		BasePage page = null;
		View view = null;
		switch(index)
		{
			case Configs.VIEW_POSITION_MAIN:
			{
				view = mInflater.inflate(R.layout.layout_main, null);
				page = new MainPage(mContext, view, mActivityInterface);
				break;
			}
			case Configs.VIEW_POSITION_NEXT:
			{
				view = mInflater.inflate(R.layout.layout_next, null);
				page = new NextPage(mContext, view, mActivityInterface);
				break;
			}
		}
		if(page == null || view == null)
			throw new IllegalArgumentException("the Page is null or the View is null.");
		return new PageObject(index, view, page);
	}
}
