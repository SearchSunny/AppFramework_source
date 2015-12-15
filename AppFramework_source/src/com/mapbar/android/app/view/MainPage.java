package com.mapbar.android.app.view;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.mapbar.android.Configs;
import com.mapbar.android.R;
import com.mapbar.android.carnet.model.ActivityInterface;
import com.mapbar.android.carnet.model.BasePage;

public class MainPage extends BasePage implements OnClickListener/*, OnItemClickListener*/
{
//	private Context mContext;
	private ActivityInterface mAif;

	public MainPage(Context context, View view, ActivityInterface aif)
	{
		super(context, view, aif);
		
//		mContext = context;
		mAif = aif;
		
		View btn_showpage = view.findViewById(R.id.btn_showpage);
		btn_showpage.setOnClickListener(this);
	}

	@Override
	public void viewWillAppear(int flag)
	{
		super.viewWillAppear(flag);
		Log.e("MainPage", "MainPage=>viewWillAppear");
	}
	
	private boolean isFinishedInit = false;

	@Override
	public void viewDidAppear(int flag)
	{
		super.viewDidAppear(flag);
		Log.e("MainPage", "MainPage=>viewDidAppear");
		if(isFinishedInit)
			return;
		isFinishedInit = true;
	}

	@Override
	public void viewWillDisappear(int flag)
	{
		super.viewWillDisappear(flag);
		Log.e("MainPage", "MainPage=>viewWillDisappear");
	}

	@Override
	public void viewDidDisappear(int flag)
	{
		super.viewDidDisappear(flag);
		Log.e("MainPage", "MainPage=>viewDidDisappear");
	}

	@Override
	public int getMyViewPosition()
	{
		return Configs.VIEW_POSITION_MAIN;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return false;
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
			case R.id.btn_showpage:
			{
				mAif.showPage(getMyViewPosition(), Configs.VIEW_POSITION_NEXT, null);
				break;
			}
		}
	}

	@Override
	public void onRestart()
	{
		super.onRestart();
	}
}
