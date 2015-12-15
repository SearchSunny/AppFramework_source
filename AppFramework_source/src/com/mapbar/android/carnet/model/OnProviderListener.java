package com.mapbar.android.carnet.model;

import com.mapbar.android.carnet.model.ProviderResult;

public interface OnProviderListener
{
	void onProviderResponse(int requestCode, int responseCode, ProviderResult result);
}
