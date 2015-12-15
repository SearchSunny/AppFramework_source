package com.mapbar.android.carnet.widget;


public class MyAnimation
{
	private MyAnimaParam mParam;
	private long mDuration = 500;
	private long mStartTime = -1;
	private boolean mEnd = true;
	private long mStartOffset = 0;
	
	public MyAnimation(MyAnimaParam param)
	{
		mParam = param;
	}
	
	public MyAnimaParam getParam()
	{
		return this.mParam;
	}
	
	public static class MyAnimaParam
	{
		public float fromX;
		public float toX;
		public float fromY;
		public float toY;
		public int fromAlpha;
		public int toAlpha;
		public int index;
		public boolean isAnimOut;
		
		public MyAnimaParam()
		{
		}
	}
	
	public static class MyAnimaValue
	{
		public float valueX;
		public float valueY;
		public int alpha;
		public int index;
		
		public MyAnimaValue()
		{
		}
	}
	
	public void setStartOffset(long startOffset)
	{
		mStartOffset = startOffset;
	}
	
	public void setDuration(long duration)
	{
		mDuration = duration;
	}
	
	public void startNow()
	{
		mStartTime = -1;
		mEnd = false;
	}
	
	public boolean hasEnded()
	{
		return mEnd;
	}
	
	public boolean getTransformation(long when, MyAnimaValue value)
	{
		if(mEnd)
			return false;
		
		if(mStartTime == -1)
		{
			mStartTime = when;
		}
		
		if(when - mStartTime < mStartOffset)
		{
			value.valueX = mParam.fromX;
			value.valueY = mParam.fromY;
			value.alpha = mParam.fromAlpha;
			value.index = mParam.index;
			return true;
		}

		float normalizedTime = ((float)(when - mStartTime - mStartOffset)) / (float)mDuration;
		
		boolean expired = normalizedTime >= 1.0f;
		normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
		if(!expired)
		{
			if(normalizedTime >= 0.0f && normalizedTime <= 1.0f)
			{
				float segX = mParam.toX - mParam.fromX;
				float segY = mParam.toY - mParam.fromY;
				float segA = mParam.toAlpha - mParam.fromAlpha;
				value.valueX = getInterpolation(normalizedTime) * segX + mParam.fromX;
				value.valueY = getInterpolation(normalizedTime) * segY + mParam.fromY;
				value.alpha = (int)(getInterpolation(normalizedTime) * segA + mParam.fromAlpha);
				value.index = mParam.index;
			}
		}
		else
		{
			value.valueX = mParam.toX;
			value.valueY = mParam.toY;
			value.alpha = mParam.toAlpha;
			value.index = mParam.index;
			mEnd = true;
		}
		return true;
	}

//    private float bounce(float t) {
//        return t * t * 8.0f;
//    }

    public float getInterpolation(float t)
    {
    	/*
        t *= 1.1226f;
        if (t < 0.3535f) return bounce(t);
        else if (t < 0.7408f) return bounce(t - 0.54719f) + 0.7f;
        else if (t < 0.9644f) return bounce(t - 0.8526f) + 0.9f;
        else return bounce(t - 1.0435f) + 0.95f;
//        t -= 1.0f;
//        return t * t * ((2 + 1) * t + 2) + 1.0f;
    	*/
        return (float)(Math.cos((t + 1) * Math.PI) / 2.0f) + 0.5f;
    }
}
