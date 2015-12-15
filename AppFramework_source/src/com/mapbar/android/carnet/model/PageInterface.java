package com.mapbar.android.carnet.model;

import android.content.Intent;
import android.location.Location;
import android.view.KeyEvent;


public interface PageInterface extends OnProviderListener
{
	public void onPause();
	public void onRestart();
	public void onResume();
	public void onDestroy();
	public boolean onKeyDown(int keyCode, KeyEvent event);
	public int getMyViewPosition();
	
	public void viewWillAppear(int flag);
	public void viewDidAppear(int flag);
	public void viewWillDisappear(int flag);
	public void viewDidDisappear(int flag);

	public void setFilterObj(int flag, FilterObj filter);
	public FilterObj getFilterObj();
	
	public void onActivityResult(int requestCode, int resultCode, Intent data);
	public void onProviderResponse(int requestCode, int responseCode, ProviderResult result);
	public void cancelProgress();
	
	public void onLocationChanged(Location location);
}
