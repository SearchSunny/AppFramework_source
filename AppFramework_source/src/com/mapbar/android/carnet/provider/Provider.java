package com.mapbar.android.carnet.provider;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.mapbar.android.carnet.model.OnProviderListener;
import com.mapbar.android.carnet.model.ProviderResult;
import com.mapbar.android.carnet.net.MyHttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpRequestType;


public class Provider
{	
	boolean isDebug = false;
	Context mContext;
	private Handler mHandler;
	
	private HashMap<String, String> mHeaders;
	
	public void setHeaders(HashMap<String, String> headers)
	{
		this.mHeaders = headers;
	}

	public final static int RESPONSE_ERROR = 0;
	public final static int RESPONSE_OK = 1;
	public final static int RESPONSE_LOGIN_TIMEOUT = 2;
	
	public final static int RESULT_OK = 0;
	public final static int RESULT_ERROR = -1;
	
	public Provider(Context context)
	{
		this.mContext = context;
		this.mHandler = new Handler();
	}
	
	private OnProviderListener mOnProviderListener;
	
	public void setOnProviderListener(OnProviderListener listener)
	{
		this.mOnProviderListener = listener;
	}
	
	static class PostParam
	{
		private String mKey;
		private String mValue;
		public boolean isFile;
		public PostParam(String key, String value)
		{
			this(key, value, false);
		}
		
		public PostParam(String key, String value, boolean file)
		{
			this.mKey = key;
			this.mValue = value;
			this.isFile = file;
		}
		
		public String getKey()
		{
			return this.mKey;
		}
		
		public String getValue()
		{
			return this.mValue;
		}
	}
	
	MyHttpHandler getDataFromNet(HttpRequestType method, int requestCode, int flag, String url, boolean save)
	{
		return getDataFromNet(method, requestCode, flag, url, null, null, null, null, save);
	}
	
	MyHttpHandler getDataFromNet(HttpRequestType method, int requestCode, int flag, String url)
	{
		return getDataFromNet(method, requestCode, flag, url, null, null, null, null, false);
	}
	
	MyHttpHandler getDataFromNet(HttpRequestType method, int requestCode, int flag, String url, 
			ArrayList<PostParam> params)
	{
		return getDataFromNet(method, requestCode, flag, url, 
				params, null, null, null, false);
	}
	
	private MyHttpHandler getDataFromNet(HttpRequestType method, final int requestCode, final int flag, final String url, 
			ArrayList<PostParam> params, byte[] bytes, String postString, ArrayList<PostParam> multiParams, 
			final boolean save)
	{
		MyHttpHandler http = null;
		http = new MyHttpHandler(mContext);
		http.setCache(CacheType.NOCACHE);
		http.setRequest(url, method);
		
		if(this.mHeaders != null && !this.mHeaders.isEmpty())
		{
			http.setHeader(mHeaders);
			/*
			for(Map.Entry<String, String> entry : this.mHeaders.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(isDebug)
					Log.e("Provider", "[header.params]key="+key+",value="+value);
				http.addPostParamete(key, value);
			}
			*/
		}

		/*
		if(postString != null)
		{
			http.setContentType("application/json");
			http.setPostData(postString);
		}
		*/
		if(isDebug)
			Log.e("Provider", url);
		if(params != null)
		{
			int size = params.size();
			for(int i = 0; i < size; i++)
			{
				PostParam param = params.get(i);
				String key = param.getKey();
				String value = param.getValue();
				if(isDebug)
					Log.e("Provider", "[post.params]key="+key+",value="+value);
				http.addPostParamete(key, value);
			}
		}
		/*
		if(multiParams != null)
		{
			int size = multiParams.size();
			for(int i = 0; i < size; i++)
			{
				PostParam param = multiParams.get(i);
				if(param.isFile)
					http.addPostFile(param.getKey(), new File(param.getValue()));
				else
					http.addPostMultiPartStr(param.getKey(), param.getValue());
			}
		}
		*/
		http.setPostData(bytes);
		http.setHttpHandlerListener(new MyHttpHandler.SampleHttpHandlerListener()
		{
			@Override
			public void onResponse(int resCode, String responseStatusLine, byte[] buffer)
			{
				if(isDebug)
					Log.e("Provider", "联网响应结束->["+resCode+"]"+responseStatusLine);
				if(resCode != 200 && resCode != 401)
				{
					if(mOnProviderListener != null)
					{
						mHandler.post(new Runnable()
						{
							@Override
							public void run()
							{
								ProviderResult result = ResultParse.parseErrorResult("网络异常，请检查您的网络！");
								mOnProviderListener.onProviderResponse(requestCode, RESULT_ERROR, result);
							}
						});
					}
					return;
				}
				ProviderResult mPr = null;

				{
					String data = null;
					try
					{
						data = new String(buffer, "utf-8");
					}
					catch(Exception e)
					{
					}
	    			if(data != null)
	    			{
	    				String responseStr = (String)data;
	    				if(isDebug)
	    					Log.e("Provider", responseStr);
	    				switch(flag)
	    				{
	    				}
	    			}
				}
				if(mOnProviderListener != null)
				{
					final ProviderResult result = mPr;
					mHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							int responseCode = RESULT_ERROR;
//							if(result != null && result.getResponseCode() == 1)
//								responseCode = RESULT_OK;
							mOnProviderListener.onProviderResponse(requestCode, responseCode, result);
						}
					});
				}
			}
		});
		http.execute();
		return http;
	}

	/*
	private String mUserAgent;
	
	public void setUserAgentString(String ua)
	{
		mUserAgent = ua;
	}
	
	private String getUserAgentString()
	{
		if(mUserAgent == null)
		{
			try
			{
				String packName = mContext.getPackageName();
				String platform = "Android";
				String product = "";
				String softVersion = "";
				String imei = "";
				String imsi = "";
				try
				{
					PackageInfo packageInfo = mContext.getPackageManager()
							.getPackageInfo(packName, 0);
					if(packageInfo != null)
					{
						softVersion = packageInfo.versionName;
						product = getProduct(packageInfo);
					}
					TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
					imei = tm.getDeviceId();
					imsi = tm.getSimSerialNumber();
				}
				catch(Exception e1)
				{
				}
				StringBuffer sb = new StringBuffer(packName);
				sb.append("_").append(platform).append("_").append(product).append("_").append(softVersion)
					.append(";").append(imei)
					.append(";").append(imsi)
					.append(";").append(android.os.Build.BRAND)
					.append(";").append(android.os.Build.MODEL)
					.append(";").append(android.os.Build.VERSION.SDK_INT);
				mUserAgent = sb.toString();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return mUserAgent;
	}
	
	private String getProduct(PackageInfo packageInfo)
	{
		try
		{
			String appName = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
			return URLEncoder.encode(appName, "utf-8");
		}
		catch(Exception e)
		{
		}
		return "";
	}
	*/
}
