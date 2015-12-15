package com.mapbar.android.carnet.model;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;

public abstract class BasePage implements PageInterface
{
	private BasePage mParent;
	
	public BasePage(Context context, View rootview, ActivityInterface aif)
	{
	}
	
	public BasePage(Context context, View rootview, ActivityInterface aif, BasePage parent)
	{
		mParent = parent;
	}
	
	public BasePage getParent()
	{
		return this.mParent;
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onRestart() {
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public int getMyViewPosition() {
		return 0;
	}

	@Override
	public void viewWillAppear(int flag) {
	}

	@Override
	public void viewDidAppear(int flag) {
	}

	@Override
	public void viewWillDisappear(int flag) {
	}

	@Override
	public void viewDidDisappear(int flag) {
	}

	@Override
	public void setFilterObj(int flag, FilterObj filter) {
	}

	@Override
	public FilterObj getFilterObj() {
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	@Override
	public void onProviderResponse(int requestCode, int responseCode,
			ProviderResult result) {
	}

	@Override
	public void cancelProgress() {
	}

	@Override
	public void onLocationChanged(Location location) {
	}
}
