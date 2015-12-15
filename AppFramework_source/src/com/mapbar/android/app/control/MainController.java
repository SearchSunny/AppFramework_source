package com.mapbar.android.app.control;

import android.location.Location;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.mapbar.android.Configs;
import com.mapbar.android.R;
import com.mapbar.android.carnet.control.AppActivity;
import com.mapbar.android.carnet.model.MAnimation;
import com.mapbar.android.carnet.model.PageObject;

public class MainController
{
	private AppActivity mBaseActivity;
	private PageManager mPageManager;
	
	private Handler mHandler;
	private boolean isFinishInitView = false;
	
	private boolean isFinishInit = false;
	
	public MainController(AppActivity activity)
	{
		this.mBaseActivity = activity;
		this.mHandler = new Handler();
	}
	
	public void sendShare(String msg)
	{
		if(TextUtils.isEmpty(msg))
			return;
	}
	
	public void onResume(int flag)
	{
		if(!isFinishInitView)
		{
			isFinishInitView = true;
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					initView();
				}
			}, 1000);
		}
	}
	
	public void onDestroy()
	{
	}
	
	public PageObject createPage(int index)
	{
		return mPageManager.createPage(index);
	}
	
	private void initView()
	{
		int position = Configs.VIEW_POSITION_MAIN;
		
		mBaseActivity.setContentView(R.layout.main);
		
		mPageManager = new PageManager(mBaseActivity, mBaseActivity);

		this.mBaseActivity.showPage(Configs.VIEW_POSITION_NONE, position, null, MAnimation.push_none, MAnimation.push_none);

		isFinishInit = true;
	}
	
	public void onLocationChanged(Location location)
	{
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(!isFinishInit)
			return true;
		return false;
	}
	
	public void onResume()
	{
	}
	
	public void onPause()
	{
	}
}
