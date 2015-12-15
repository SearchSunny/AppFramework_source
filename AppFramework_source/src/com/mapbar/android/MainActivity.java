package com.mapbar.android;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.mapbar.android.app.control.MainController;
import com.mapbar.android.carnet.control.AppActivity;
import com.mapbar.android.carnet.model.OnDialogListener;
import com.mapbar.android.carnet.model.PageObject;

public class MainActivity extends AppActivity
{
	private MainController mMainController;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_welcome);

		mMainController = new MainController(this);
	}

	@Override
	public PageObject createPage(int index)
	{
		return mMainController.createPage(index);
	}

	@Override
	public int getAnimatorResId()
	{
		return R.id.animator;
	}
	
	@Override
	public int getMainPosition()
	{
		return Configs.VIEW_POSITION_MAIN;
	}

	@Override
	public int getOutPosition()
	{
		return Configs.VIEW_POSITION_NONE;
	}

	@Override
	public int getNonePositioin()
	{
		return Configs.VIEW_POSITION_NONE;
	}

	@Override
	public void onFinishedInit(int flag)
	{
		mMainController.onResume(flag);
	}
	
	@Override
	public void onResume()
	{
		mMainController.onResume();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		mMainController.onPause();
		super.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mMainController.onDestroy();
	}
	
	private boolean isCanExit = false;

	@Override
	public void onPageActivity()
	{
		isCanExit = false;
	}
	
	@Override
	public boolean canExit()
	{
		if(!isCanExit)
		{
			this.isCanExit = true;
			this.showAlert(R.string.toast_againto_exit);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(mMainController.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void showDialog(int style, int resId, String title, String content, 
			String cancelText, String okText, final OnDialogListener listener)
	{
		/*
		try
		{
			final Dialog popDialog = new Dialog(this, style);
			
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(resId, null);// 得到加载view
			
			popDialog.setContentView(view, new LinearLayout.LayoutParams(
                		LinearLayout.LayoutParams.MATCH_PARENT, 
                		LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
			
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			if(TextUtils.isEmpty(title))
				tv_title.setVisibility(View.GONE);
			else
				tv_title.setText(title);
			
			TextView tv_desc = (TextView)view.findViewById(R.id.tv_desc);
			tv_desc.setText(content);
			
			Button btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
			btn_cancel.setText(cancelText);
			btn_cancel.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					popDialog.dismiss();
					if(listener != null)
						listener.onCancel();
				}
			});
			
			Button btn_ok = (Button)view.findViewById(R.id.btn_ok);
			btn_ok.setText(okText);
			btn_ok.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					popDialog.dismiss();
					if(listener != null)
						listener.onOk();
				}
			});
			
			popDialog.setCancelable(true);
			popDialog.show();
		}
		catch (Exception e)
		{
		}
		*/
	}

	@Override
	public void showDialog(String title, String content, String cancelText,
			String okText, final OnDialogListener listener)
	{
		//showDialog(R.style.pop_dialog, R.layout.confirm_dialog, title, content, cancelText,okText, listener);
	}
}
