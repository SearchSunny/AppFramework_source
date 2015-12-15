package com.mapbar.android.carnet.widget;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.mapbar.android.carnet.widget.MyAnimation.MyAnimaParam;
import com.mapbar.android.carnet.widget.MyAnimation.MyAnimaValue;

public class MViewAnimator extends ViewGroup
{
	/*
	private GradientDrawable leftShadow;
	private GradientDrawable rightShadow;
//	private static final int[] SHADOWS_COLORS = new int[]{0xFF80807d,
//		0x00929290};
	private static final int[] SHADOWS_COLORS = new int[]{0xB2000000,
		0x32000000, Color.TRANSPARENT};
	*/
	private View mCurView;
	private View mLastView;
	
	private boolean isDoMySelf = false;
	
	private Paint mFlashPaint;
	private Handler mHandler;
	
	public MViewAnimator(Context context)
	{
		this(context, null, 0x101008a);
	}
	
	public MViewAnimator(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0x101008a);
	}
	
	public MViewAnimator(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs);
		this.setWillNotDraw(false);

//		leftShadow = new GradientDrawable(Orientation.RIGHT_LEFT, SHADOWS_COLORS);
//		rightShadow = new GradientDrawable(Orientation.LEFT_RIGHT, SHADOWS_COLORS);
		mFlashPaint = new Paint();
		mFlashPaint.setColor(0xff4d6672);
		mFlashPaint.setTextSize(sp2px(20));
		mFlashPaint.setAntiAlias(true);
		mFlashPaint.setDither(true); 
		mFlashPaint.setShadowLayer(1.0f, 0.0f, 1.0f, 0xffffffff);
		
		this.mHandler = new Handler();
	}
	
	public void setDoMySelf(boolean doMy)
	{
		this.isDoMySelf = doMy;
		if(doMy)
		{
			if(this.mCurView == null && this.getChildCount() != 0)
			{
				this.mCurView = this.getChildAt(0);
			}
		}
	}
	
	public void stopAnimation()
	{
		isAnimating = false;
		this.clearAnimation();
		if(mInAnimation != null)
			mInAnimation.setAnimationListener(null);
		if(mOutAnimation != null)
			mOutAnimation.setAnimationListener(null);
		this.setInAnimation(null);
		this.setOutAnimation(null);
	}
	
	private Animation mInAnimation;
	private Animation mOutAnimation;
	
	public void setInAnimation(Animation animation)
	{
		this.mInAnimation = animation;
	}
	
	public Animation getInAnimation()
	{
		return this.mInAnimation;
	}
	
	public void setOutAnimation(Animation animation)
	{
		this.mOutAnimation = animation;
	}
	
	public Animation getOutAnimation()
	{
		return this.mOutAnimation;
	}
	
	private boolean isAnimating = false;
	private boolean isOutFront = false;
	private int mFlag;
	
	public void setDisplayedChild(View child, boolean outFront, int flag, int fromFlag)
	{
		setDisplayedChild(child, outFront, flag, null, null, fromFlag);
	}
	
	private int mFromFlag = -1;

	public void setDisplayedChild(View child, boolean outFront, int flag, Point outOffset, Point inOffset, int fromFlag)
	{
		mFlag = flag;
		mFromFlag = fromFlag;
		if(!isDoMySelf)
		{
			this.removeAllViews();
		}
		isDoMySelf = true;
		int index = this.indexOfChild(child);
		if(index == -1 && child.getParent() == null)
		{
			this.addView(child);
			index = this.getChildCount() - 1;
		}
		mLastView = mCurView;
		mCurView = child;
		if(mLastView != null)
		{
			LayoutParams lp = (LayoutParams)mLastView.getLayoutParams();
			lp.mOffset = outOffset;
			
		}
		if(mCurView != null)
		{
			LayoutParams lp = (LayoutParams)mCurView.getLayoutParams();
			lp.mOffset = inOffset;
		}
		isOutFront = outFront;
		if(isOutFront)
		{
			if(mLastView != null)
			{
				mLastView.bringToFront();
				index = this.indexOfChild(child);
			}
		}
		setDisplayedChild(index);
	}
	
	/*
	public void setDisplayedChild(View child, boolean outFront, int flag)
	{
		mFlag = flag;
		if(!isDoMySelf)
		{
			this.removeAllViews();
		}
		isDoMySelf = true;
		int index = this.indexOfChild(child);
		if(index == -1)
		{
			this.addView(child);
			index = this.getChildCount() - 1;
		}
		mLastView = mCurView;
		mCurView = child;
		isOutFront = outFront;
		if(isOutFront)
		{
			if(mLastView != null)
			{
				mLastView.bringToFront();
				index = this.indexOfChild(child);
			}
		}
		setDisplayedChild(index);
	}
	*/

	private void setDisplayedChild(int whichChild)
	{
//		super.setDisplayedChild(whichChild);
		long inDuration = 0;
		long outDuration = 0;
		if(this.getInAnimation() != null)
		{
			inDuration = this.getInAnimation().getDuration();
			if(mCurView != null)
				mCurView.startAnimation(this.getInAnimation());
			isAnimating = true;
		}
		if(this.getOutAnimation() != null)
		{
			outDuration = this.getOutAnimation().getDuration();
			if(mLastView != null)
				mLastView.startAnimation(this.getOutAnimation());
			isAnimating = true;
		}
		if(isAnimating)
		{
			if(inDuration > outDuration)
			{
				this.getInAnimation().setAnimationListener(animationListener);
			}
			else
			{
				this.getOutAnimation().setAnimationListener(animationListener);
			}
		}
		else
		{
			if(this.mLastView != null)
			{
				LayoutParams lp = (LayoutParams)this.mLastView.getLayoutParams();
				if(lp.mOffset == null)
				{
					this.removeView(this.mLastView);
					this.mLastView = null;
				}
			}
		}
		this.mCurView.requestFocus();
//		this.mCurView.requestFocusFromTouch();
		this.requestLayout();
		this.postInvalidate();
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
//		super.dispatchDraw(canvas);
//		int count = this.getChildCount();
//		System.out.println("count="+count);
		if(isDoMySelf)
		{
//			System.out.println("isOutFront="+isOutFront+",mLastView="+mLastView+",mCurView="+mCurView);
			if(isOutFront)
			{
				if(this.mCurView != null)
					super.drawChild(canvas, this.mCurView, this.getDrawingTime());
				if(this.mLastView != null)
					super.drawChild(canvas, this.mLastView, this.getDrawingTime());
			}
			else
			{
				if(this.mLastView != null)
					super.drawChild(canvas, this.mLastView, this.getDrawingTime());
				if(this.mCurView != null)
					super.drawChild(canvas, this.mCurView, this.getDrawingTime());
			}
		}
		else
		{
			super.dispatchDraw(canvas);
		}

		if(isFlashAnimating)
		{
			if(mFlashAnimtion != null)
			{
				final int index = mFlashAnimtion.getParam().index;
				MyFlashObj obj = mMyFlashArrs.get(index);
				if(!mFlashAnimtion.hasEnded())
				{
					MyAnimaValue value = new MyAnimaValue();
					if(mFlashAnimtion.getTransformation(this.getDrawingTime(), value))
					{
//						System.out.println("["+value.index+"]"+value.alpha+","+value.valueY);
						mFlashPaint.setAlpha(value.alpha);
						float x = obj.x + value.valueX;
						float y = obj.y + value.valueY;
						canvas.drawText(obj.text, x, y, mFlashPaint);
					}
				}
				else
				{
					isFlashAnimating = false;
					if(mFlashAnimtion.getParam().isAnimOut)
					{
						this.mHandler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								returnFlashAnimation(index);
							}
						}, obj.stayTime);
					}
					else
					{
						if(index < mMyFlashArrs.size() - 1)
							this.startFlashAnimation(index + 1);
						else
							mFlashAnimtion = null;
					}
				}
			}
			this.invalidate();
		}
		else
		{
			if(mFlashAnimtion != null)
			{
				final int index = mFlashAnimtion.getParam().index;
				MyFlashObj obj = mMyFlashArrs.get(index);
				float x = obj.x;
				float y = obj.y;
				canvas.drawText(obj.text, x, y, mFlashPaint);
			}
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		/*
		if(mMyFlashArrs == null)
		{
			mMyFlashArrs = new ArrayList<MyFlashObj>();
			MyFlashObj obj = new MyFlashObj();
			obj.text = "锟�?????锟�???????;
			Rect bound = new Rect();
			mFlashPaint.getTextBounds(obj.text, 0, obj.text.length(), bound);
			obj.x = (this.getWidth() - bound.width()) / 2;
			obj.y = this.getHeight() - bound.height() - 100;
			obj.stayTime = 3000;
			mMyFlashArrs.add(obj);

			MyFlashObj obj1 = new MyFlashObj();
			obj1.text = "锟�??璁╋拷?锟�???锟借捣锟�?锟斤拷锟�?;
			mFlashPaint.getTextBounds(obj1.text, 0, obj1.text.length(), bound);
			obj1.x = (this.getWidth() - bound.width()) / 2;
			obj1.y = this.getHeight() - bound.height() - 100;
			obj1.stayTime = 3000;
			mMyFlashArrs.add(obj1);
		}
		*/
	}

	private boolean isFlashAnimating = false;
	private MyAnimation mFlashAnimtion;
	private ArrayList<MyFlashObj> mMyFlashArrs;
	
	private class MyFlashObj
	{
		public String text;
		public float x;
		public float y;
		public long stayTime;
		
		public MyFlashObj()
		{
		}
	}
	
	public void startFlashAnimation()
	{
		startFlashAnimation(0);
	}
	
	public void setFlashJson(JSONObject obj)
	{
		if(obj == null)
			return;
		/*
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"align\":\"center\"");
		sb.append(",\"valign\":\"bottom\"");
		sb.append(",\"margin\":[0,0,0,100]");
		sb.append(",\"color\":0xff4d6672");
		sb.append(",\"fontSize\":20");
		sb.append(",\"shadow\":");
			sb.append("{");
			sb.append("\"radius\":1.0");
			sb.append(",\"dx\":0.0");
			sb.append(",\"dy\":1.0");
			sb.append(",\"color\":0xffffffff");
			sb.append("}");
		sb.append(",\"flashTxts\":");
			sb.append("[");
			sb.append("\"锟�?????锟�???????"");
			sb.append(",\"锟�??璁╋拷?锟�???锟借捣锟�?锟斤拷锟�?"");
			sb.append(",\"??锟斤拷锟�???锟界┓瀛╋拷?\"");
			sb.append(",\"瀛╋拷?灏憋拷?锟�???????"");
			sb.append(",\"锟�??灏憋拷?瀛╋拷????锟�?"");
			sb.append("]");
		sb.append(",\"stayTime\":3000");
		sb.append(",\"inAnim\":");
			sb.append("{");
			sb.append("\"fromYDelta\":50");
			sb.append(",\"toYDelta\":0");
			sb.append(",\"fromAlpha\":127");
			sb.append(",\"toAlpha\":255");
			sb.append("}");
		sb.append(",\"outAnim\":");
			sb.append("{");
			sb.append("\"fromYDelta\":0");
			sb.append(",\"toYDelta\":-50");
			sb.append(",\"fromAlpha\":255");
			sb.append(",\"toAlpha\":0");
			sb.append("}");
		sb.append("}");
		*/
		try
		{
//			JSONObject obj;
//			obj = new JSONObject(sb.toString());
			if(!obj.has("flashTxts"))
				return;
			
			if(obj.has("color"))
				mFlashPaint.setColor(obj.getInt("color"));
			if(obj.has("fontSize"))
				mFlashPaint.setTextSize(sp2px(obj.getInt("fontSize")));
			if(obj.has("shadow"))
			{
				JSONObject shadowObj = obj.getJSONObject("shadow");
				double radius = 1.0;
				if(shadowObj.has("radius"))
					radius = shadowObj.getDouble("radius");
				double dx = 0;
				if(shadowObj.has("dx"))
					dx = shadowObj.getDouble("dx");
				double dy = 1.0;
				if(shadowObj.has("dy"))
					dy = shadowObj.getDouble("dy");
				int shadowColor = 0xffffffff;
				if(shadowObj.has("color"))
					shadowColor = shadowObj.getInt("color");
				mFlashPaint.setShadowLayer((float)radius, (float)dx, (float)dy, shadowColor);
			}
			
			long stayTime = 0;
			if(obj.has("stayTime"))
				stayTime = obj.getLong("stayTime");

			int top = 100;
			int bottom = 100;
			if(obj.has("margin"))
			{
				JSONArray margins = obj.getJSONArray("margin");
				if(margins.length() > 1)
					top = margins.getInt(1);
				if(margins.length() > 3)
					bottom = margins.getInt(3);
			}
			
//			sb.append("\"align\":\"center\"");
//			sb.append(",\"valign\":\"bottom\"");
			
			boolean isValignBottom = true;
			String valign = "bottom";
			if(obj.has("valign"))
				valign = obj.getString("valign");
			if(valign.equals("top"))
				isValignBottom = false;

			mMyFlashArrs = new ArrayList<MyFlashObj>();
			Rect bound = new Rect();
			JSONArray arrs = obj.getJSONArray("flashTxts");
			for(int i = 0; i < arrs.length(); i++)
			{
				String txt = arrs.getString(i);
				MyFlashObj flashObj = new MyFlashObj();
				flashObj.text = txt;
				mFlashPaint.getTextBounds(flashObj.text, 0, flashObj.text.length(), bound);
				if(isValignBottom)
				{
					flashObj.x = (this.getWidth() - bound.width()) / 2;
					flashObj.y = this.getHeight() - bound.height() - sp2px(bottom);
				}
				else
				{
					flashObj.x = (this.getWidth() - bound.width()) / 2;
					flashObj.y = sp2px(top);
				}
				flashObj.stayTime = stayTime;
				mMyFlashArrs.add(flashObj);
			}

//			public float fromXDelta, toXDelta, fromYDelta, toYDelta;
//			public int fomtAlpha = 255, toAlpha = 255;
			
			if(obj.has("inAnim"))
			{
				JSONObject inAnimObj = obj.getJSONObject("inAnim");
				mInMyAnimObj = new MyAnimObj();
				if(inAnimObj.has("fromXDelta"))
					mInMyAnimObj.fromXDelta = (float)inAnimObj.getDouble("fromXDelta");
				if(inAnimObj.has("toXDelta"))
					mInMyAnimObj.toXDelta = (float)inAnimObj.getDouble("toXDelta");
				if(inAnimObj.has("fromYDelta"))
					mInMyAnimObj.fromYDelta = (float)inAnimObj.getDouble("fromYDelta");
				if(inAnimObj.has("toYDelta"))
					mInMyAnimObj.toYDelta = (float)inAnimObj.getDouble("toYDelta");
				if(inAnimObj.has("fromAlpha"))
					mInMyAnimObj.fromAlpha = inAnimObj.getInt("fromAlpha");
				if(inAnimObj.has("toAlpha"))
					mInMyAnimObj.toAlpha = inAnimObj.getInt("toAlpha");
			}
			
			if(obj.has("outAnim"))
			{
				JSONObject outAnimObj = obj.getJSONObject("outAnim");
				mOutMyAnimObj = new MyAnimObj();
				if(outAnimObj.has("fromXDelta"))
					mOutMyAnimObj.fromXDelta = (float)outAnimObj.getDouble("fromXDelta");
				if(outAnimObj.has("toXDelta"))
					mOutMyAnimObj.toXDelta = (float)outAnimObj.getDouble("toXDelta");
				if(outAnimObj.has("fromYDelta"))
					mOutMyAnimObj.fromYDelta = (float)outAnimObj.getDouble("fromYDelta");
				if(outAnimObj.has("toYDelta"))
					mOutMyAnimObj.toYDelta = (float)outAnimObj.getDouble("toYDelta");
				if(outAnimObj.has("fromAlpha"))
					mOutMyAnimObj.fromAlpha = outAnimObj.getInt("fromAlpha");
				if(outAnimObj.has("toAlpha"))
					mOutMyAnimObj.toAlpha = outAnimObj.getInt("toAlpha");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private float sp2px(float spValue)
	{
		DisplayMetrics metrics = this.getContext().getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
	}
	
	private class MyAnimObj
	{
		public float fromXDelta, toXDelta, fromYDelta, toYDelta;
		public int fromAlpha = 255, toAlpha = 255;
		
		public MyAnimObj()
		{
		}
	}
	
	private MyAnimObj mInMyAnimObj;
	private MyAnimObj mOutMyAnimObj;
	
	public void startFlashAnimation(int index)
	{
		if(this.mMyFlashArrs == null || index < 0 || index >= this.mMyFlashArrs.size())
			return;
		MyAnimaParam param = new MyAnimaParam();
		if(mInMyAnimObj != null)
		{
			param.fromX = mInMyAnimObj.fromXDelta;
			param.toX = mInMyAnimObj.toXDelta;
			param.fromY = mInMyAnimObj.fromYDelta;
			param.toY = mInMyAnimObj.toYDelta;
			param.fromAlpha = mInMyAnimObj.fromAlpha;
			param.toAlpha = mInMyAnimObj.toAlpha;
		}
		else
		{
			param.fromY = 50;
			param.toY = 0;
			param.fromAlpha = 127;
			param.toAlpha = 255;
		}
		
		param.index = index;
		param.isAnimOut = true;
		mFlashAnimtion = new MyAnimation(param);
		mFlashAnimtion.startNow();
		isFlashAnimating = true;
		this.postInvalidate();
	}
	
	private void returnFlashAnimation(int index)
	{
		MyAnimaParam param = new MyAnimaParam();
		if(mOutMyAnimObj != null)
		{
			param.fromX = mOutMyAnimObj.fromXDelta;
			param.toX = mOutMyAnimObj.toXDelta;
			param.fromY = mOutMyAnimObj.fromYDelta;
			param.toY = mOutMyAnimObj.toYDelta;
			param.fromAlpha = mOutMyAnimObj.fromAlpha;
			param.toAlpha = mOutMyAnimObj.toAlpha;
		}
		else
		{
			param.fromY = 0;
			param.toY = -50;
			param.fromAlpha = 255;
			param.toAlpha = 0;
		}
		param.index = index;
		param.isAnimOut = false;
		mFlashAnimtion = new MyAnimation(param);
		mFlashAnimtion.startNow();
		isFlashAnimating = true;
		this.postInvalidate();
	}
	
	private AnimationListener animationListener = new AnimationListener()
	{
		@Override
		public void onAnimationStart(Animation animation)
		{
			if(mListener != null)
			{
				mListener.onAnimationStart(animation);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{
			if(mListener != null)
			{
				mListener.onAnimationRepeat(animation);
			}
		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			stopAnimation();
			animation.setAnimationListener(null);
			
			if(mLastView != null)
			{
				mLastView.clearAnimation();
				LayoutParams lp = (LayoutParams)mLastView.getLayoutParams();
				if(lp.mOffset == null)
				{
					removeView(mLastView);
					mLastView = null;
				}
			}
			if(mListener != null)
			{
				mListener.onAnimationEnd(animation, mFlag, mFromFlag);
			}
			mCurView.clearAnimation();
//			mCurView.requestFocus();
		}
	};
	
	private OnAnimatorHelperListener mListener;
	
	public void setOnAnimatorHelperListener(OnAnimatorHelperListener listener)
	{
		mListener = listener;
	}
	
	public static class OnAnimatorHelperListener implements AnimationListener
	{
		@Override
		public void onAnimationEnd(Animation animation)
		{
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{
		}

		@Override
		public void onAnimationStart(Animation animation)
		{
		}
		
		public void onAnimationEnd(Animation animation, int flag, int fromFlag)
		{
		}
	}

	@Override
	protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int count = this.getChildCount();
		for(int i = 0; i < count; i++)
		{
			View child = this.getChildAt(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int count = this.getChildCount();
		for(int i = 0; i < count; i++)
		{
			View child = this.getChildAt(i);
//			measureItem(child);
			int left = 0;
			int top = 0;
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
			if(lp.mOffset != null)
			{
				left = lp.mOffset.x;
				top = lp.mOffset.y;
			}
			child.layout(left, top, left+this.getWidth() + lp.mWidthExp, top+this.getHeight());
		}
	}
	
	public static class LayoutParams extends ViewGroup.LayoutParams
	{
		public Point mOffset;
		public int mWidthExp;
		
		public LayoutParams(int width, int height)
		{
			super(width, height);
		}
		
		public LayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);
		}
		
		public LayoutParams(ViewGroup.LayoutParams source)
		{
			super(source);
		}
	}
	
	@Override
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p)
	{
		return p instanceof MViewAnimator.LayoutParams;
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams()
	{
		return new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	public android.view.ViewGroup.LayoutParams generateLayoutParams(
			AttributeSet attrs)
	{
		return new LayoutParams(getContext(), attrs);
	}
	
	@Override
	protected android.view.ViewGroup.LayoutParams generateLayoutParams(
			android.view.ViewGroup.LayoutParams p)
	{
		return new LayoutParams(p);
	}
}
