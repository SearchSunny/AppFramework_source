package com.mapbar.android.carnet.provider;

import com.mapbar.android.carnet.model.ProviderResult;


public class ResultParse
{
	public static ProviderResult parseErrorResult(String reason)
	{
		try
		{
			ProviderResult pr = new ProviderResult();
			pr.setResponseCode(0);
			pr.setActTime(System.currentTimeMillis());
			pr.setReason(reason);
			return pr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
