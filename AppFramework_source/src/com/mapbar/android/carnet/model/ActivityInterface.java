package com.mapbar.android.carnet.model;

import android.content.Intent;
import android.location.Location;
import android.view.animation.Animation;
import android.widget.EditText;

import com.mapbar.android.carnet.net.MyHttpHandler;


public interface ActivityInterface
{
	public void showPage(int flag, int index, FilterObj filter);
	public void showPage(int flag, int index, FilterObj filter, Animation in, Animation out);
	public void showPrevious(FilterObj filter);
	public void showPrevious(int flag, int index, FilterObj filter);
	public void showPrevious(int flag, int index, FilterObj filter, Animation in, Animation out);
	public void showJumpPrevious(int flag, int index, FilterObj filter);
	public void showJumpPrevious(int flag, int index, FilterObj filter, Animation in, Animation out);
	
	public void hideSoftInput(EditText et);
	public void showSoftInput(EditText et);
	
	public int getCurrentPageIndex();
	public PageObject getCurrentPageObj();
	public PageObject getPageObjByPos(int position);

	public void showProgressDialog(final MyHttpHandler http, String title, String msg);
	public void showProgressDialog(final MyHttpHandler http, int msgId, boolean cancelable);
	public void showProgressDialog(final MyHttpHandler http, int msgId);
	public void showAlert(int resId);
	public void showAlert(String word);
	public void showProgressDialog(int msgId);
	public void hideProgressDialog();
	public void showDialog(String title, String content, String cancelText, String okText, OnDialogListener listener);
	public void showDialog(int style, int resId, String title, String content, String cancelText, String okText, OnDialogListener listener);

	public int getScreenWidth();
	public int getScreenHeight();
	//public void goHome();
	//public void goHome(int flag, int position);
	public boolean canExit();
	public void finish();
	
	public void startActivityForResult(Intent intent, int requestCode);
	
	public Location getMyLocation();
	public String getVersion();
}
