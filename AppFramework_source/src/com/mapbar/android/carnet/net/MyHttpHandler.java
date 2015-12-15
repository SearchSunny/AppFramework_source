package com.mapbar.android.carnet.net;

import android.content.Context;

import com.mapbar.android.net.HttpHandler;

public class MyHttpHandler extends HttpHandler
{
    private final static String TASK_TAG = "CarnetTask";
    
    public MyHttpHandler(Context context)
    {
    	super(TASK_TAG, context);
    }
    
    @Override
    public void setRequest(String url, HttpRequestType requestType)
    {
    	super.setRequest(url, requestType);
    }
	
	public static class SampleHttpHandlerListener implements HttpHandlerListener
	{
		@Override
		public void onResponse(int resCode, String responseStatusLine, byte[] buffer)
		{
		}
	}
}
