package com.mapbar.android.carnet.control;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbar.android.carnet.model.ActivityInterface;
import com.mapbar.android.carnet.model.FilterObj;
import com.mapbar.android.carnet.model.MAnimation;
import com.mapbar.android.carnet.model.OnDialogListener;
import com.mapbar.android.carnet.model.OnProviderListener;
import com.mapbar.android.carnet.model.PageObject;
import com.mapbar.android.carnet.model.PageRestoreData;
import com.mapbar.android.carnet.model.ProviderResult;
import com.mapbar.android.carnet.net.MyHttpHandler;
import com.mapbar.android.carnet.widget.MViewAnimator;
import com.mapbar.android.carnet.widget.MViewAnimator.OnAnimatorHelperListener;

public abstract class AppBaseActivity extends Activity implements ActivityInterface, SensorEventListener, OnProviderListener
{
	private MViewAnimator mViewAnimator;
	private PageObject mOutPage;
	
	private ArrayList<PageObject> mPageHistorys = new ArrayList<PageObject>();
	private Hashtable<Integer, PageRestoreData> mHt_MapRestores = new Hashtable<Integer, PageRestoreData>();
	
	private int mCurrentPageIndex = 0;

	public abstract PageObject createPage(int index);
	public abstract int getAnimatorResId();
	public abstract void onFinishedInit(int flag);
	
	private int mScreenWidth, mScreenHeight;
	
//	private SensorManager mSensorManager;
	private Location mMyLocPt;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
//		long time = System.currentTimeMillis();
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
	}
	
	@Override
	public int getScreenWidth()
	{
		return this.mScreenWidth;
	}
	
	@Override
	public int getScreenHeight()
	{
		return this.mScreenHeight;
	}
	
	private MViewAnimator getAnimator()
	{
		if(mViewAnimator == null)
		{
			mViewAnimator = (MViewAnimator)findViewById(getAnimatorResId());
			if(mViewAnimator == null)
				throw new IllegalArgumentException("the ViewAnimator is null, the layout must contain com.homelink.android.widget.MViewAnimator.");
			mViewAnimator.setDoMySelf(true);
			mViewAnimator.setOnAnimatorHelperListener(animationListener);
		}
		return mViewAnimator;
	}
	
	private boolean isShowingPage = false;
	
	@Override
	public void showPage(int flag, int index, FilterObj filter)
	{
		showPage(flag, index, filter, MAnimation.push_left_in, MAnimation.push_left_out);
	}
	
	public void onPageActivity()
	{
	}
	
	@Override
	public void showPage(int flag, int index, FilterObj filter, Animation in, Animation out)
	{
		if(isShowingPage)
			return;
		isShowingPage = true;
		onPageActivity();
		PageObject newPage = null;
		if(index == getOutPosition())
			newPage = getOutPage();
		else
			newPage = createPage(index);
		newPage.getPage().setFilterObj(flag, filter);
//		newPage.getPage().setDataType(dataType);

		int size = this.mPageHistorys.size();
		PageObject curPage = null;
		if(size != 0)
		{
			curPage = this.mPageHistorys.get(size-1);
			curPage.getPage().viewWillDisappear(getViewNoneFlag());
		}
		newPage.setIndex(size);
		this.mPageHistorys.add(newPage);
		
		/*
		boolean r2l = true;
		if(curPage != null)
			r2l = newPage.getIndex() > curPage.getIndex();
		this.onTitleChanged(newPage.getPage().getMyViewPosition(), r2l);
		*/
		mCurrentPageIndex = this.mPageHistorys.size() - 1;
		newPage.getPage().viewWillAppear(flag);
		
		MViewAnimator myAnimator = getAnimator();
		
		myAnimator.setInAnimation(in);
		myAnimator.setOutAnimation(out);

		if(index == getOutPosition())
		{
//			getOutPage().getView().setVisibility(View.VISIBLE);
			MAnimation.fade_out_map.setAnimationListener(animationListener);
			myAnimator.startAnimation(MAnimation.fade_out_map);
			myAnimator.setVisibility(View.GONE);
		}
		else
		{
//			boolean isOutFront = outFront;
			boolean isOutFront = false;
			if(in == MAnimation.push_none)
				isOutFront = true;
			if(curPage != null && curPage.getPage().getMyViewPosition() == getOutPosition())
			{
				isOutFront = false;
				myAnimator.setVisibility(View.VISIBLE);
				MAnimation.fade_in_map1.setAnimationListener(animationListener);
				myAnimator.startAnimation(MAnimation.fade_in_map1);
				myAnimator.setInAnimation(null);
				myAnimator.setOutAnimation(null);
				/*
				PageRestoreData prd = curPage.getPage().getRestoreData();
				if(prd != null)
					this.mHt_MapRestores.put(size-1, prd);
				*/
			}
			myAnimator.setDisplayedChild(newPage.getView(), isOutFront, 0, null, null, flag);
		}
		checkSensor(index);
	}

	@Override
	public void showPrevious(FilterObj filter)
	{
		int size = this.mPageHistorys.size();
		if(size < 1)
		{
			return;
		}
		PageObject outPage = this.mPageHistorys.get(size-1);
		showPrevious(outPage.getPage().getMyViewPosition(), -1, filter, MAnimation.push_right_in, MAnimation.push_right_out);
	}

	@Override
	public void showPrevious(int flag, int index, FilterObj filter)
	{
		showPrevious(flag, index, filter, MAnimation.push_right_in, MAnimation.push_right_out);
	}

	@Override
	public void showPrevious(int flag, int index, FilterObj filter, Animation in, Animation out)
	{
		showPrevious(flag, index, filter, in, out, null, null);
	}

	private void showPrevious(int flag, int index, FilterObj filter, 
			Animation in, Animation out, Point outOffset, Point inOffset)
	{
		if(isShowingPage)
			return;
		isShowingPage = true;
		onPageActivity();
		int size = this.mPageHistorys.size();
		if(size < 2)
		{
			isShowingPage = false;
//			this.goHome();
			return;
		}
		mCurrentPageIndex = size-2;
		PageObject inPage = this.mPageHistorys.get(size-2);
		inPage.getPage().setFilterObj(flag, filter);
		if(inPage.getPage().getMyViewPosition() != getOutPosition())
			inPage.getPage().viewWillAppear(flag);
		PageObject outPage = this.mPageHistorys.get(size-1);
		outPage.getPage().viewWillDisappear(getViewNoneFlag());
		
		/*
		boolean r2l =  outPage.getIndex() < inPage.getIndex();
		this.onTitleChanged(inPage.getPage().getMyViewPosition(), r2l);
		*/
		
		MViewAnimator myAnimator = getAnimator();
		
		myAnimator.setInAnimation(in);
		myAnimator.setOutAnimation(out);
		if(outPage.getPage().getMyViewPosition() == getOutPosition())
		{
			myAnimator.setVisibility(View.VISIBLE);
			MAnimation.fade_in_map.setAnimationListener(animationListener);
			myAnimator.startAnimation(MAnimation.fade_in_map);
			this.mHt_MapRestores.remove(size-1);
		}
		else
		{
			boolean isOutFront = false;
			if(in == MAnimation.push_none)
				isOutFront = true;
			if(inPage.getPage().getMyViewPosition() == getOutPosition())
			{
//				PageRestoreData prd = this.mHt_MapRestores.get(size-2);
//				inPage.getPage().onRestoreData(prd);
				inPage.getPage().viewWillAppear(flag);
				isOutFront = false;
				MAnimation.fade_out_map1.setAnimationListener(animationListener);
				myAnimator.startAnimation(MAnimation.fade_out_map1);
				checkSensor(index);
				return;
			}
			myAnimator.setDisplayedChild(inPage.getView(), isOutFront, 1, outOffset, inOffset, flag);
		}
		checkSensor(index);
	}

	@Override
	public void showJumpPrevious(int flag, int index, FilterObj filter)
	{
		showJumpPrevious(flag,index, filter, MAnimation.push_right_in, MAnimation.push_right_out);
	}

	@Override
	public void showJumpPrevious(int flag, int index, FilterObj filter, Animation in,
			Animation out)
	{
		if(isShowingPage)
			return;
		isShowingPage = true;
		onPageActivity();
		PageObject jumpToPage = null;
		int jumpIndex = 0;
		int size = this.mPageHistorys.size();
		for(int i = size - 1; i >= 0; i--)
		{
			PageObject page = this.mPageHistorys.get(i);
			if(i == this.mCurrentPageIndex)
			{
				page.getPage().viewWillDisappear(getViewNoneFlag());
				continue;
			}
			if(page.getPosition() == index)
			{
				jumpToPage = page;
//				jumpToPage.getPage().viewWillAppear(flag);
				jumpIndex = i;
				break;
			}
			page.getPage().viewDidDisappear(getViewNoneFlag());
			page.destroy();
			mPageHistorys.remove(i);
			this.mHt_MapRestores.remove(i);
			page = null;
		}
		if(jumpToPage == null)
		{
			if(index == getOutPosition())
				jumpToPage = getOutPage();
			else
				jumpToPage = createPage(index);
			jumpToPage.setIndex(0);
			this.mPageHistorys.add(0, jumpToPage);
		}

		mCurrentPageIndex = jumpIndex;
		jumpToPage.getPage().viewWillAppear(getNonePositioin());
		
		/*
		boolean r2l = false;
		PageObject curPage = this.getCurrentPageObj();
		if(curPage != null)
			r2l = curPage.getIndex() < jumpToPage.getIndex();
		this.onTitleChanged(jumpToPage.getPage().getMyViewPosition(), r2l);
		*/
		
		MViewAnimator myAnimator = getAnimator();
		
		if(index == getOutPosition())
		{
			MAnimation.fade_out_map.setAnimationListener(animationListener);
			myAnimator.startAnimation(MAnimation.fade_out_map);
			myAnimator.setVisibility(View.GONE);
		}
		else
		{
			myAnimator.setInAnimation(MAnimation.push_right_in);
			myAnimator.setOutAnimation(MAnimation.push_right_out);
			myAnimator.setDisplayedChild(jumpToPage.getView(), false, 1, flag);
		}
		checkSensor(index);
	}
	
//	private boolean isAddedSensor = false;
	
	public boolean checkSensorPage(int index)
	{
		return false;
	}
	
	/*
	private void stopSensor(int index)
	{
		if(!checkSensorPage(index))
		{
			if(isAddedSensor)
			{
				isAddedSensor = false;
				mSensorManager.unregisterListener(this);
			}
		}
	}
	*/
	
	private void checkSensor(int index)
	{
		/*
		if(checkSensorPage(index))
		{
			if(!isAddedSensor)
			{
				isAddedSensor = true;
				mSensorManager.registerListener(this, 
						mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
						SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
		else
		{
			if(isAddedSensor)
			{
				isAddedSensor = false;
				mSensorManager.unregisterListener(this);
			}
		}
		*/
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		/*
		int size = mPageHistorys.size();
		if(mCurrentPageIndex < size)
		{
			PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
			thePage.getPage().onSensorChanged(event);
		}
		*/
	}

	@Override
	public void hideSoftInput(EditText et)
	{
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				et.getWindowToken(), 0);
	}

	@Override
	public void showSoftInput(EditText et)
	{
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, 0);
	}

	@Override
	public int getCurrentPageIndex()
	{
		return this.mCurrentPageIndex;
	}

	@Override
	public PageObject getCurrentPageObj()
	{
		if(mCurrentPageIndex < this.mPageHistorys.size())
			return this.mPageHistorys.get(mCurrentPageIndex);
		return null;
	}

	@Override
	public PageObject getPageObjByPos(int position)
	{
		int size = this.mPageHistorys.size();
		for(int i = size - 1; i >= 0; i--)
		{
			PageObject page = this.mPageHistorys.get(i);
			if(page.getPage().getMyViewPosition() == position)
				return page;
		}
		return null;
	}
	
	private OnAnimatorHelperListener animationListener = new OnAnimatorHelperListener()
	{
		@Override
		public void onAnimationEnd(Animation animation, int flag, int fromFlag)
		{
			onAttachFinished(animation, fromFlag);
			int size = mPageHistorys.size();
			if(flag == 1)
			{
				for(int i = size - 1; i > mCurrentPageIndex; i--)
				{
					PageObject outPage = mPageHistorys.get(i);
					outPage.destroy();
					mPageHistorys.remove(i);
					outPage = null;
				}
			}
			else
			{
				if(mCurrentPageIndex > 0)
				{
					PageObject outPage = mPageHistorys.get(mCurrentPageIndex-1);
					outPage.getPage().viewDidDisappear(flag);
				}
			}
			isShowingPage = false;
		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			if(animation == MAnimation.fade_out_map)
				getOutPage().getPage().viewDidDisappear(-1);
			else if(animation == MAnimation.fade_out_map1)
			{
				MViewAnimator myAnimator = getAnimator();
				myAnimator.setInAnimation(null);
				myAnimator.setOutAnimation(null);
				int size = mPageHistorys.size();
				if(size - 3 < 0)
					throw new IllegalArgumentException("is not enough Page to return.");
				PageObject inPage = mPageHistorys.get(size-3);
				for(int i = size - 1; i > mCurrentPageIndex; i--)
				{
					PageObject outPage = mPageHistorys.get(i);
					outPage.destroy();
					mPageHistorys.remove(i);
					outPage = null;
				}
				myAnimator.setDisplayedChild(inPage.getView(), false, 0, -1);
				myAnimator.setVisibility(View.GONE);
			}
			else if(animation == MAnimation.fade_in_map)
			{
				onAttachFinished(animation, getOutPosition());
				int size = mPageHistorys.size();
				if(size > 0)
				{
					PageObject outPage = mPageHistorys.get(size-1);
					if(outPage.getPage().getMyViewPosition() == getOutPosition())
					{
//						outPage.getPage().onBackrun();
						mPageHistorys.remove(size-1);
						isShowingPage = false;
						return;
					}
				}
				throw new IllegalArgumentException("the last Page is not MapPage.");
			}
			else if(animation == MAnimation.fade_in_map1)
			{
				onAttachFinished(animation, getOutPosition());
//				getOutPage().getPage().onBackrun();
			}
			isShowingPage = false;
		}
	};
	
	private void onAttachFinished(Animation animation, int fromFlag)
	{
		int size = mPageHistorys.size();
		if(mCurrentPageIndex < size)
		{
			PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
			if(thePage.getPage().getMyViewPosition() != getOutPosition())
			{
				thePage.getPage().viewDidAppear(fromFlag);
			}
		}
	}
	
	private boolean isFinishInit = false;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		int curPosition = -1;
		int size = mPageHistorys.size();
		for(int i = 0; i < size; i++)
		{
			PageObject thePage = mPageHistorys.get(i);
			if(i == this.mCurrentPageIndex)
				curPosition = thePage.getPage().getMyViewPosition();
			thePage.getPage().onResume();
		}
		
		if(isFinishInit)
		{
			checkSensor(curPosition);
			return;
		}
		isFinishInit = true;
		
		this.onFinishedInit(1);
//		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	}
	
	private PageObject getOutPage()
	{
		if(this.mOutPage == null)
			this.mOutPage = this.createPage(this.getOutPosition());
		return this.mOutPage;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		checkSensor(-1);
		int size = mPageHistorys.size();
		for(int i = 0; i < size; i++)
		{
			PageObject thePage = mPageHistorys.get(i);
			thePage.getPage().onPause();
		}
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		int size = mPageHistorys.size();
		for(int i = 0; i < size; i++)
		{
			PageObject thePage = mPageHistorys.get(i);
			thePage.getPage().onRestart();
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		int size = mPageHistorys.size();
		for(int i = 0; i < size; i++)
		{
			PageObject thePage = mPageHistorys.get(i);
			thePage.getPage().onDestroy();
		}
		isFinishInit = false;
	}
	
	@Override
	public boolean canExit()
	{
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			PageObject curPage = this.mPageHistorys.get(mCurrentPageIndex);
			if(curPage.getPage().onKeyDown(keyCode, event))
			{
				return true;
			}
			if(!canExit())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		PageObject curPage = this.mPageHistorys.get(mCurrentPageIndex);
		if(curPage.getPage().onKeyUp(keyCode, event))
		{
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	*/
	
	@Override
	public void showAlert(int resId)
	{
		String word = this.getResources().getString(resId);
		showAlert(word);
	}
	
	@Override
	public void showAlert(String word)
	{
        Toast.makeText(this, word, Toast.LENGTH_SHORT).show();
	}
	
	public int getMainPosition()
	{
		return CConfigs.VIEW_POSITION_MAIN;
	}
	
	public int getOutPosition()
	{
		return CConfigs.VIEW_POSITION_MAP;
	}
	
	public int getNonePositioin()
	{
		return CConfigs.VIEW_POSITION_NONE;
	}
	
	public int getViewNoneFlag()
	{
		return CConfigs.VIEW_FLAG_NONE;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		int size = mPageHistorys.size();
		if(mCurrentPageIndex < size)
		{
			PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
			thePage.getPage().onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private Dialog mProgressDialog;
	
	@Override
	public void showProgressDialog(final MyHttpHandler http, int msgId)
	{
		showProgressDialog(http, msgId, false);
	}
	
	@Override
	public void showProgressDialog(final MyHttpHandler http, int msgId, boolean cancelable)
	{
		String msg = this.getResources().getString(msgId);
		showProgressDialog(http, null, msg, cancelable);
	}
	
	@Override
    public void showProgressDialog(final MyHttpHandler http, String title, String msg)
	{
		showProgressDialog(http, title, msg, false);
	}

	@Override
	public void showProgressDialog(int msgId)
	{
		String msg = this.getResources().getString(msgId);
		showProgressDialog(null, null, msg, false);
	}
	
    private void showProgressDialog(final MyHttpHandler http, String title, String msg, boolean cancelable)
    {
    	/*
        try
        {
            if (mProgressDialog == null)
            {

        		int loadingId = getResId(":style/loading_dialog");
        		int loadingLayoutId = getResId(":layout/loading_dialog");
        		int[] arrIds = new int[]{loadingLayoutId, loadingId};
        		TypedArray a = obtainStyledAttributes(attrs, arrIds);
        		int verticalTrackId = a.getResourceId(0, -1);
            	
                mProgressDialog = new Dialog(this, R.style.loading_dialog);
                
                LayoutInflater inflater = LayoutInflater.from(this);
                View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
                LinearLayout layout = (LinearLayout)v.findViewById(R.id.dialog_view);// 加载布局
                mProgressDialog.setContentView(layout, new LinearLayout.LayoutParams(
                		LinearLayout.LayoutParams.MATCH_PARENT, 
                		LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
            }

            if (msg == null)
                return;

            TextView tipTextView = (TextView)mProgressDialog.findViewById(R.id.tipTextView);// 提示文字
            tipTextView.setText(msg);// 设置加载信息
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new OnCancelListener()
            {
				@Override
				public void onCancel(DialogInterface dialog)
				{
					int size = mPageHistorys.size();
					if(mCurrentPageIndex < size)
					{
						PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
						thePage.getPage().cancelProgress();
					}
				}
            });
            mProgressDialog.setOnKeyListener(new OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent evnet)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        if(http != null)
                        {
                            http.cancel(true);
                        }
                        return false;
                    }
                    else if (keyCode == KeyEvent.KEYCODE_SEARCH)
                    {
                        return true;
                    }
                    return false;
                }
            });
            mProgressDialog.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
    }

	@Override
	public void hideProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		int size = mPageHistorys.size();
		if(mCurrentPageIndex < size)
		{
//			PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
//			thePage.getPage().onSaveInstanceState(outState);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		int size = mPageHistorys.size();
		if(mCurrentPageIndex < size)
		{
//			PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
//			thePage.getPage().onRestoreInstanceState(savedInstanceState);
		}
	}
	
	@Override
	public Location getMyLocation()
	{
		return this.mMyLocPt;
	}
	
	public void onLocationChanged(Location location)
	{
		this.mMyLocPt = location;
		int size = mPageHistorys.size();
		if(mCurrentPageIndex < size)
		{
			PageObject thePage = mPageHistorys.get(mCurrentPageIndex);
			thePage.getPage().onLocationChanged(location);
		}
	}
	
	/*
	private String getImei()
	{
		String imei = "";
		try
		{
			TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
		}
		catch(Exception ex)
		{
		}
		return imei;
	}
	
	private String getLocalMacAddress()
	{
		try
		{
			WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
		}
		catch(Exception e)
		{
		}
		return "";
	}
	*/

	@Override
	public final void onProviderResponse(int requestCode, int responseCode, ProviderResult result)
	{
	}
	
	@Override
	public String getVersion()
	{
		try
		{
			PackageInfo packageInfo = getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			if(packageInfo != null)
			{
				return packageInfo.versionName;
			}
		}
		catch(Exception e1)
		{
		}
		return "1.0.0";
	}

	@Override
	public void showDialog(String title, String content, String cancelText,
			String okText, OnDialogListener listener)
	{
	}
	
	@Override
	public void showDialog(int style, int resId, String title, String content, 
			String cancelText, String okText, OnDialogListener listener)
	{
	}
}
