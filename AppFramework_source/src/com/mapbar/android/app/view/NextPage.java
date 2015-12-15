package com.mapbar.android.app.view;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.mapbar.android.Configs;
import com.mapbar.android.carnet.model.ActivityInterface;
import com.mapbar.android.carnet.model.BasePage;

public class NextPage extends BasePage implements OnClickListener/*, OnItemClickListener*/
{
//	private Context mContext;
	private ActivityInterface mAif;

	public NextPage(Context context, View view, ActivityInterface aif)
	{
		super(context, view, aif);
		
//		mContext = context;
		mAif = aif;
	}

	@Override
	public void viewWillAppear(int flag)
	{
		super.viewWillAppear(flag);
		Log.e("NextPage", "NextPage=>viewWillAppear");
	}
	
	private boolean isFinishedInit = false;

	@Override
	public void viewDidAppear(int flag)
	{
		super.viewDidAppear(flag);
		Log.e("NextPage", "NextPage=>viewDidAppear");
		if(isFinishedInit)
			return;
		isFinishedInit = true;
	}

	@Override
	public void viewWillDisappear(int flag)
	{
		super.viewWillDisappear(flag);
		Log.e("NextPage", "NextPage=>viewWillDisappear");
	}

	@Override
	public void viewDidDisappear(int flag)
	{
		super.viewDidDisappear(flag);
		Log.e("NextPage", "NextPage=>viewDidDisappear");
	}

	@Override
	public int getMyViewPosition()
	{
		return Configs.VIEW_POSITION_NEXT;
	}
	
	public void goBack()
	{
		mAif.showPrevious(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		goBack();
		return true;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onClick(View v)
	{
		int viewId = v.getId();
		switch(viewId)
		{
		}
	}

	@Override
	public void onRestart()
	{
		super.onRestart();
	}
}
