package com.mapbar.android.carnet.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Scroller;

public class MListView extends ViewGroup
{
	private int mAllItemHeight;
	private Drawable mVScrollbar;
	private boolean isFling = false;
	
//	private boolean isTopInvisible = false;
	private int mTopItemPosition;
	
	private int mTouchSlop;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	
	private MAdapterView mAdapterView;
	private int mFirsetHeight = -1;
	private boolean isAutoHeight = false;

	private View mHeaderView;
	private boolean useHeadViewRefresh;
	private boolean isHeaderFullShow = false;
	private boolean isRefreshing = false;
	
	public MListView(Context context)
	{
		this(context, null, 0x101008a);
	}
	
	public MListView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0x101008a);
	}
	
	public MListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		super.setWillNotDraw(false);
		mScroller = new Scroller(getContext());

		int isAutoHeightId = getResId(":attr/isAutoHeight");
		int[] arrIds = new int[]{android.R.attr.scrollbarTrackVertical, isAutoHeightId};
		TypedArray a = context.obtainStyledAttributes(attrs, arrIds);
		int verticalTrackId = a.getResourceId(0, -1);
		if(verticalTrackId != -1)
		{
			mVScrollbar = context.getResources().getDrawable(verticalTrackId);
		}
		isAutoHeight = a.getBoolean(1, false);
		a.recycle();
		
		final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		
		mAdapterView = new MAdapterView(this.getContext());
		
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				requestLayout();
			}
		};
	}
	
	private int getResId(String str)
	{
		return this.getResources().getIdentifier(this.getContext().getPackageName() + str, null, null);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}
	
	/*
//	@Override
	public void dispatchDrawTest(Canvas canvas)
	{
		if(this.mAdapter == null || this.mAdapter.getCount() <= 0)
			return;
		long time = System.currentTimeMillis();
//		super.dispatchDraw(canvas);
		boolean drawAgain = false;
		if(isAnimating)
		{
			int size = mMyAnimations.size();
			for(int i = 0; i < size; i++)
			{
				MyAnimation anima = mMyAnimations.get(i);
				if(!anima.hasEnded())
				{
					ArrayList<MyAnimaValue> values = new ArrayList<MyAnimaValue>();
					if(anima.getTransformation(this.getDrawingTime(), values))
					{
						int valueSize = values.size();
						for(int j = 0; j < valueSize; j++)
						{
							MyAnimaValue value = values.get(j);
							View child = super.getChildAt(value.index);
							int left = (int)value.valueX;
							int top = (int)value.valueY;
							LayoutParams lp = (LayoutParams)child.getLayoutParams();
							if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERCLIP)
							{
								lp.width = this.getWidth() + left;
								measureItem(child);
								child.layout(child.getLeft(), child.getTop(), 
										child.getLeft() + lp.width, child.getTop()+child.getMeasuredHeight());
							}
							else
							{
								child.layout(left, top, 
										left+child.getMeasuredWidth(), top+child.getMeasuredHeight());
							}
						}
						drawAgain = true;
					}
				}
			}
			if(!drawAgain)
			{
				mMyAnimations.clear();
				isAnimating = false;
				if(mMoveItemView != null && !isExtendShow)
				{
					mMoveItemView.setVisibility(View.GONE);
					this.isItemXMove = false;
				}
			}
		}

		int count = this.getChildCount();

		for(int i = count - 1; i >= 0; i--)
		{
			final View child = this.getChildAt(i);
			if(!checkInVisible(child))
			{
				this.removeViewAt(i);
				this.mAdapter.recycle(child);
			}
			else
			{
				this.drawChild(canvas, child, this.getDrawingTime());
			}
		}
		count = this.getChildCount();

		if(count > 0)
		{
			View topView = this.getChildAt(0);
			LayoutParams toplp = (LayoutParams)topView.getLayoutParams();
			mFirstVisibilePos = toplp.mAdapterPos;
			int topLeaveH = topView.getTop() - this.getScrollY();
			if(topLeaveH > 0)
			{
				int tmpPos = toplp.mAdapterPos - 1;
				if(tmpPos >= 0)
				{
					int tmpTop = topView.getTop();
					while(topLeaveH > 0)
					{
						View view = this.mAdapter.getView(tmpPos, null, this);
						this.addView(view, 0);
						this.measureItem(view);
						
						LayoutParams vl = (LayoutParams)view.getLayoutParams();
						vl.isOnTopInvisible = mAdapter.isOnTopInvisible(tmpPos);
						vl.isOnBottomInvisible = mAdapter.isOnBottomInvisible(tmpPos);
						vl.isOnTopFloatVisible = mAdapter.isOnTopFloatVisible(tmpPos);
						vl.mLayoutType = mAdapter.getExtendLayoutType(tmpPos);
						vl.mViewType = mAdapter.getItemViewType(tmpPos);
						vl.mAdapterPos = tmpPos;
						mFirstVisibilePos = tmpPos;
						boolean isEnabled = mAdapter.isEnabled(tmpPos);
						view.setClickable(true);
						view.setFocusable(true);
						if(isEnabled && !vl.isOnTopInvisible && !vl.isOnTopFloatVisible
								 && !vl.isOnBottomInvisible)
							view.setOnClickListener(new MItemClick(tmpPos));
						
						tmpTop = tmpTop - view.getMeasuredHeight();
						view.layout(0, tmpTop, view.getMeasuredWidth(), tmpTop + view.getMeasuredHeight());
						this.drawChild(canvas, view, this.getDrawingTime());
						topLeaveH = topLeaveH - view.getMeasuredHeight();
						tmpPos--;
						if(tmpPos < 0)
							break;
					}
				}
			}
			View bottomView = this.getChildAt(count-1);
			LayoutParams bottomlp = (LayoutParams)bottomView.getLayoutParams();
			mLastVisibilePos = bottomlp.mAdapterPos;
			int bottomLeaveH = bottomView.getTop() - this.getScrollY() - this.getHeight() + bottomView.getMeasuredHeight();
			if(bottomLeaveH < 0)
			{
				int tmpPos = bottomlp.mAdapterPos + 1;
				if(tmpPos < this.mAdapter.getCount())
				{
					int tmpTop = bottomView.getTop() + bottomView.getMeasuredHeight();
					while(bottomLeaveH < 0)
					{
						View view = this.mAdapter.getView(tmpPos, null, this);
						this.addView(view);
						this.measureItem(view);
						
						LayoutParams vl = (LayoutParams)view.getLayoutParams();
						vl.isOnTopInvisible = mAdapter.isOnTopInvisible(tmpPos);
						vl.isOnBottomInvisible = mAdapter.isOnBottomInvisible(tmpPos);
						vl.isOnTopFloatVisible = mAdapter.isOnTopFloatVisible(tmpPos);
						vl.mLayoutType = mAdapter.getExtendLayoutType(tmpPos);
						vl.mViewType = mAdapter.getItemViewType(tmpPos);
						vl.mAdapterPos = tmpPos;
						mLastVisibilePos = tmpPos;
						boolean isEnabled = mAdapter.isEnabled(tmpPos);
						view.setClickable(true);
						view.setFocusable(true);
						if(isEnabled && !vl.isOnTopInvisible && !vl.isOnTopFloatVisible
								 && !vl.isOnBottomInvisible)
							view.setOnClickListener(new MItemClick(tmpPos));
						
						view.layout(0, tmpTop, view.getMeasuredWidth(), tmpTop + view.getMeasuredHeight());
						this.drawChild(canvas, view, this.getDrawingTime());
						tmpTop = tmpTop + view.getMeasuredHeight();
						bottomLeaveH = bottomLeaveH + view.getMeasuredHeight();
						tmpPos++;
						if(tmpPos >= this.mAdapter.getCount())
							break;
					}
				}
			}
			count = this.mAdapter.getCount();
		}
//		super.dispatchDraw(canvas);
		drawAgain |= drawScrollBar(canvas);
		if(drawAgain)
		{
			this.invalidate();
		}
		System.out.println("All the draw time => "+(System.currentTimeMillis()-time));
	}
	*/
	
	@Override
	public void dispatchDraw(Canvas canvas)
	{
//		long time = System.currentTimeMillis();
//		super.dispatchDraw(canvas);
		final MListViewAdapter adapter = this.mAdapter;
		if(adapter == null)
			return;
		final int adapterCount = adapter.getCount();
		boolean drawAgain = false;
		if(isAnimating)
		{
			int size = mMyAnimations.size();
			for(int i = 0; i < size; i++)
			{
				MyAnimation anima = mMyAnimations.get(i);
				if(!anima.hasEnded())
				{
					ArrayList<MyAnimaValue> values = new ArrayList<MyAnimaValue>();
					if(anima.getTransformation(this.getDrawingTime(), values))
					{
						int valueSize = values.size();
						for(int j = 0; j < valueSize; j++)
						{
							MyAnimaValue value = values.get(j);
							View child = super.getChildAt(value.index);
							int left = (int)value.valueX;
							int top = (int)value.valueY;
							LayoutParams lp = (LayoutParams)child.getLayoutParams();
							if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERCLIP)
							{
								lp.width = this.getWidth() + left;
								measureItem(child);
								child.layout(child.getLeft(), child.getTop(), 
										child.getLeft() + lp.width, child.getTop()+child.getMeasuredHeight());
							}
							else if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERFLOATCLIP)
							{
								LayoutParams mLp = (LayoutParams)mMoveItemView.getLayoutParams();
								mLp.mClipWidth = left;
							}
							else
							{
								child.layout(left, top, 
										left+child.getMeasuredWidth(), top+child.getMeasuredHeight());
							}
						}
						drawAgain = true;
					}
				}
			}
			if(!drawAgain)
			{
				mMyAnimations.clear();
				isAnimating = false;
				if(mMoveItemView != null && !isExtendShow)
				{
					mMoveItemView.setVisibility(View.GONE);
					this.isItemXMove = false;
				}
			}
		}
		
		int count = this.getChildCount();
		int lastDrawPos = -1;
		int minPos = count - 1;
		int maxPos = 0;
		for(int i = 0; i < count; i++)
		{
			View child = super.getChildAt(i);
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
			if(lp.isNotInList)
				continue;
			if(checkInVisible(child, lp))
			{
				if(!lp.isOnTopFloatVisible && !lp.isNotInList && !lp.isOnTopInvisible)
				{
					minPos = Math.min(i, minPos);
					maxPos = Math.max(i, maxPos);
				}
			}
			else
			{
				lp.isRecycled = true;
				adapter.recycle(child);
				continue;
			}
			if(this.isItemXMove && i == this.mXmovePosition)
			{
				switch(lp.mLayoutType)
				{
					case MListViewAdapter.EXTEND_LAYOUT_OVERCLIP:
					{
						mMoveItemView.layout(child.getLeft(), child.getTop(), child.getLeft()+this.getWidth(), 
								child.getTop()+child.getMeasuredHeight());
						this.drawChild(canvas, mMoveItemView, this.getDrawingTime());
						this.drawChild(canvas, child, this.getDrawingTime());
						break;
					}
					case MListViewAdapter.EXTEND_LAYOUT_OVERFLOAT:
					{
						mMoveItemView.layout(0, child.getTop(), child.getMeasuredWidth(), 
								child.getTop()+child.getMeasuredHeight());
						this.drawChild(canvas, mMoveItemView, this.getDrawingTime());
						this.drawChild(canvas, child, this.getDrawingTime());
						break;
					}
					case MListViewAdapter.EXTEND_LAYOUT_OVERFLOATCLIP:
					{
						this.drawChild(canvas, child, this.getDrawingTime());
						LayoutParams mLp = (LayoutParams)mMoveItemView.getLayoutParams();
						int clipTop = child.getTop();
						canvas.save();
						canvas.clipRect(this.getWidth()-Math.abs(mLp.mClipWidth), clipTop, this.getWidth(), 
								clipTop + child.getMeasuredHeight());
						this.drawChild(canvas, mMoveItemView, this.getDrawingTime());
						canvas.restore();
						break;
					}
					default:
					{
						mMoveItemView.layout(child.getRight(), child.getTop(), child.getRight()+child.getMeasuredWidth(), 
								child.getTop()+child.getMeasuredHeight());
						this.drawChild(canvas, mMoveItemView, this.getDrawingTime());
						this.drawChild(canvas, child, this.getDrawingTime());
						break;
					}
				}
			}
			else
			{
				if(lp.isOnTopFloatVisible)
				{
					if(this.getScrollY() >= lp.mFloatTop)
					{
						lastDrawPos = i;
						continue;
					}
					else
					{
						child.layout(0, lp.mFloatTop, child.getMeasuredWidth(), 
								lp.mFloatTop + child.getMeasuredHeight());
					}
				}
				if(((lp.mAdapterPos < mFirstVisibilePos || lp.mAdapterPos > mLastVisibilePos) && 
						(lp.isRecycled || adapter.isRefresh(lp.mAdapterPos))))
				{
					if(lp.mAdapterPos < adapterCount)
					{
						lp.isRecycled = false;
						adapter.getView(lp.mAdapterPos, child, this);
//						child.setLayoutParams(lp);
					}
				}
				this.drawChild(canvas, child, this.getDrawingTime());
			}
		}
		mFirstVisibilePos = minPos;
		mLastVisibilePos = maxPos;
		if(lastDrawPos != -1)
		{
			View child = super.getChildAt(lastDrawPos);
			child.layout(0, 0 + this.getScrollY(), child.getMeasuredWidth(), 
					0 + this.getScrollY()+child.getMeasuredHeight());
			this.drawChild(canvas, child, this.getDrawingTime());
		}
		drawAgain |= drawScrollBar(canvas);
		if(drawAgain)
		{
			super.invalidate();
		}
//		System.out.println("All the draw time => "+(System.currentTimeMillis()-time));
	}
	
	private boolean checkInVisible(View child, LayoutParams lp)
	{
		if(lp.isOnTopFloatVisible)
			return true;
		if((child.getTop() >= this.getScrollY() && child.getTop() <= this.getScrollY() + this.getHeight())
				|| (child.getTop() + child.getMeasuredHeight() >= this.getScrollY() && child.getTop() + child.getMeasuredHeight() <= this.getScrollY() + this.getHeight())
				|| (child.getTop() <= this.getScrollY() && child.getTop() + child.getMeasuredHeight() >= this.getScrollY() + this.getHeight()))
		{
			return true;
		}
		return false;
	}
	
	/*
	private boolean checkInVisible(View child)
	{
		if((child.getTop() >= this.getScrollY() && child.getTop() <= this.getScrollY() + this.getHeight())
				|| (child.getTop() + child.getMeasuredHeight() >= this.getScrollY() && child.getTop() + child.getMeasuredHeight() <= this.getScrollY() + this.getHeight()))
		{
			return true;
		}
		return false;
	}
	*/
	
	private boolean mAllowFirstAnimate = false;
	
	public void setAllowFirstAnimate(boolean allow)
	{
		mAllowFirstAnimate = allow;
	}

	private void setAnimation(View view, long sTime)
	{
		AnimationSet mChildAnimation = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		mChildAnimation.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(150);
		mChildAnimation.addAnimation(animation);
		mChildAnimation.setStartOffset(sTime);
		view.startAnimation(mChildAnimation);
	}
	
	private boolean drawScrollBar(Canvas canvas)
	{
		if(mVScrollbar == null)
			return false;
		if(!this.isDrawScrollBar)
			return false;
		if(!this.doDrawScrollBar)
			return false;
		boolean drawAgain = false;
		canvas.save();
		int tmp = (this.getHeight() - mVScrollbar.getBounds().height()) * this.getScrollY() / (this.mAllItemHeight - this.getHeight());
		if(this.getScrollY() < 0)
		{
			tmp = 0;
		}
		else if(this.getScrollY() > this.mAllItemHeight - this.getHeight())
		{
			tmp = this.getHeight() - mVScrollbar.getBounds().height();
		}
		canvas.translate(0, this.getScrollY()+tmp);
		if(mScrollBarAnimate != null && !mScrollBarAnimate.hasEnded())
		{
			Transformation tmpTF = new Transformation();
			if(mScrollBarAnimate.getTransformation(this.getDrawingTime(), tmpTF))
			{
				mVScrollbar.setAlpha((int)(tmpTF.getAlpha()*255));
				drawAgain = true;
			}
			this.doDrawScrollBar = !mScrollBarAnimate.hasEnded();
		}
		mVScrollbar.draw(canvas);
		canvas.restore();
		return drawAgain;
	}

	private int mWidthMeasureSpec;
//	private int mHeightMeasureSpec;
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		mWidthMeasureSpec = widthMeasureSpec;
//		mHeightMeasureSpec = heightMeasureSpec;
	}
	
	private void measureItem(View child)
	{
		LayoutParams p = (LayoutParams)child.getLayoutParams();
		int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, 
				0, p.width);
//		int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, 
//				0, MeasureSpec.UNSPECIFIED);
		int lpHeight = p.height;
		int childHeightSpec;
		if(lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		}
		else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	
	private boolean isDrawScrollBar = false;
	private Animation mScrollBarAnimate;
	private boolean doDrawScrollBar = false;
	
	private void startScrollBarAnimation(boolean out)
	{
		if(mVScrollbar == null)
			return;
		if(!isDrawScrollBar)
			return;
		if(out)
		{
			mScrollBarAnimate = new AlphaAnimation(1.0f, 0.0f);
		}
		else
		{
			mScrollBarAnimate = new AlphaAnimation(0.0f, 1.0f);
		}
		mScrollBarAnimate.setDuration(500);
		mScrollBarAnimate.startNow();
		doDrawScrollBar = true;
		super.invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int count = this.getChildCount();
		if(count <= 0)
			return;
//		this.layout(this.getLeft(), this.getTop(), this.getLeft()+this.getWidth(), this.getTop()+415);
//		System.out.println("MListView.onLayout=>"+this.mFirstVisibilePos+","+this.mLastVisibilePos);
		int tmpHeight = 0;
		final int firstPos = this.mFirstVisibilePos;
		final int lastPos = this.mLastVisibilePos;
		for(int i = firstPos; i <= lastPos; i++)
		{
			View child = this.getChildAt(i);
			int oldH = child.getMeasuredHeight();
			this.measureItem(child);
			int newH = child.getMeasuredHeight();
			if(newH != oldH)
			{
				child.layout(child.getLeft(), child.getTop(), 
						child.getLeft() + child.getMeasuredWidth(), child.getTop() + child.getMeasuredHeight());
			}
			if(tmpHeight != 0)
			{
				int top = child.getTop() + tmpHeight;
				child.layout(child.getLeft(), top, 
						child.getLeft() + child.getMeasuredWidth(), top + child.getMeasuredHeight());
			}
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
			if(lp.isSurplus)
				oldH = newH;
			tmpHeight += newH - oldH;
		}
		if(tmpHeight != 0)
		{
			for(int i = lastPos + 1; i < count; i++)
			{
				View child = this.getChildAt(i);
				int top = child.getTop() + tmpHeight;
				child.layout(child.getLeft(), top, 
						child.getLeft() + child.getMeasuredWidth(), top + child.getMeasuredHeight());
			}
			this.mAllItemHeight += tmpHeight;
			initDrawScrollBar(this.getHeight());
		}
		/*
		if(isAutoHeight)
		{
			System.out.println("[0]this.mAllItemHeight="+this.mAllItemHeight+",mFirsetHeight="+mFirsetHeight+","+
					this.getHeight());
			ViewGroup.LayoutParams vl = (ViewGroup.LayoutParams)this.getLayoutParams();
			if(vl != null)
			{
				vl.height = Math.min(this.mAllItemHeight, mFirsetHeight);
				this.setLayoutParams(vl);
				if(vl.height != this.getHeight())
				{
					this.layout(this.getLeft(), this.getTop(), this.getLeft()+this.getWidth(), 
							this.getTop()+vl.height);
				}
			}
		}
		*/
	}
	
	private void doOnLayout()
	{
		int childTop = 0;
		final int count = getChildCount();
		mAllItemHeight = 0;
		for(int i = 0; i < count; i++)
		{
			final View child = super.getChildAt(i);
			if(child.getVisibility() != View.GONE)
			{
				measureItem(child);
				LayoutParams lp = (LayoutParams)child.getLayoutParams();
				final int childWidth = child.getMeasuredWidth();
				if(lp.isNotInList)
				{
					child.layout(0, 0, childWidth, child.getMeasuredHeight());
					continue;
				}
				if(lp.isOnTopInvisible)
				{
					child.layout(0, -child.getMeasuredHeight(), childWidth, 0);
				}
				else
				{
					if(lp.isOnTopFloatVisible)
						lp.mFloatTop = childTop;
					child.layout(0, childTop, childWidth, childTop+child.getMeasuredHeight());
					childTop += child.getMeasuredHeight();
					if(!lp.isOnBottomInvisible)
						mAllItemHeight += child.getMeasuredHeight();
				}
			}
		}
		int myHeight = this.getHeight();
		if(mAllItemHeight > myHeight)
		{
			if(this.getScrollY() + myHeight > mAllItemHeight)
			{
				this.scrollTo(0, mAllItemHeight - myHeight);
			}
		}
		else
		{
			this.scrollTo(0, 0);
		}
		if(isAutoHeight)
		{
			ViewGroup.LayoutParams vl = (ViewGroup.LayoutParams)this.getLayoutParams();
			if(vl != null)
			{
				myHeight = Math.min(this.mAllItemHeight, mFirsetHeight);
				if(vl.height != myHeight)
				{
					vl.height = myHeight;
					this.setLayoutParams(vl);
					this.layout(this.getLeft(), this.getTop(), this.getLeft()+this.getWidth(), 
							this.getTop()+myHeight);
				}
			}
		}
		isDrawScrollBar = false;
		if(mVScrollbar == null)
			return;
		if(mAllItemHeight > myHeight)
		{
			int tmp = myHeight - (mAllItemHeight - myHeight) * myHeight / mAllItemHeight;
			mVScrollbar.setBounds(this.getWidth()-mVScrollbar.getIntrinsicWidth(), 0, this.getWidth(), tmp);
			isDrawScrollBar = true;
		}
	}

	private boolean isMoveChanged = false;
	private float mLastMotionX;
	private float mLastMotionY;
	
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	
	private int mTouchState = TOUCH_STATE_REST;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private boolean isItemXMove = false;
	private int mXmovePosition;
	private View mMoveItemView;
	private boolean isAnimating = false;
	private boolean isExtendShow = false;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(this.isTopFloatItemTouch)
			return true;
		
		if(this.mTopFloatItemPos != -1)
		{
			if(this.mTopFloatItemPos < this.getChildCount())
			{
				View child = super.getChildAt(this.mTopFloatItemPos);
				Rect outRect = new Rect();
				child.getHitRect(outRect);
				if(outRect.contains((int)ev.getX()+this.getScrollX(), (int)ev.getY()+this.getScrollY()))
				{
					this.isTopFloatItemTouch = true;
					return true;
				}
			}
		}
		
		if(isAnimating)
			return true;
		final int action = ev.getAction();
		if(isItemXMove)
		{
			if(mMyAnimations.isEmpty())
			{
				View child = super.getChildAt(mXmovePosition);
				LayoutParams lp = (LayoutParams)child.getLayoutParams();
				int left = mMoveItemView.getLeft();
				if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERCLIP || 
						lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERFLOATCLIP)
				{
					int clipOffsetWidth = this.mAdapter.getExtendClipOffsetWidth(mXmovePosition);
					if(clipOffsetWidth < 0)
					{
						int clipOffsetViewId = this.mAdapter.getExtendClipOffsetViewId(mXmovePosition);
						View tmpChild = mMoveItemView.findViewById(clipOffsetViewId);
						if(tmpChild == null)
				            throw new IllegalArgumentException("The Layout type EXTEND_LAYOUT_OVERCLIP must have the ClipOffset View id.");
						left = tmpChild.getLeft();
					}
					else
					{
						left = mMoveItemView.getMeasuredWidth() - clipOffsetWidth;
					}
				}
				Rect r = new Rect(left, mMoveItemView.getTop() - this.getScrollY(), 
						mMoveItemView.getRight(), mMoveItemView.getBottom() - this.getScrollY());
				if(r.contains((int)ev.getX(), (int)ev.getY()))
				{
					return false;
				}
			}
			return true;
		}
		
		if((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST))
		{
			return true;
		}

		final float y = ev.getY();
		final float x = ev.getX();
		
		switch(action)
		{
			case MotionEvent.ACTION_MOVE:
				final int yDiff = (int)Math.abs(y - mLastMotionY);
				final int xDiff = (int)Math.abs(x - mLastMotionX);

				boolean yMoved = yDiff > mTouchSlop;
				boolean xMoved = xDiff > mTouchSlop;
				
				if(yMoved)
				{
					mLastMotionY = y;
					mTouchState = TOUCH_STATE_SCROLLING;
					if(mVScrollbar != null)
					{
						mScrollBarAnimate = null;
						this.mVScrollbar.setAlpha(255);
						this.doDrawScrollBar = true;
					}
				}
				else if(xMoved && !isExtendShow)
				{
					int count = this.getChildCount();
					for(int i = 0; i < count; i++)
					{
						boolean canXMove = this.mAdapter.canHorizontalMove(i);
						if(!canXMove)
							continue;
						View item = super.getChildAt(i);
						int itemTop = item.getTop() - this.getScrollY();
						Rect r = new Rect(item.getLeft(), itemTop, 
								item.getMeasuredWidth(), itemTop + item.getMeasuredHeight());
						if(r.contains((int)x, (int)y))
						{
							isItemXMove = true;
							if(mMoveItemView != null && i != mXmovePosition)
							{
								this.removeView(mMoveItemView);
								mMoveItemView = null;
							}
							if(mMoveItemView == null)
							{
								mMoveItemView = this.mAdapter.createExtendView(i, null, null);
								if(mMoveItemView == null)
						            throw new IllegalArgumentException("The Item must have extend View for Horizontal Moving.");
								LayoutParams lpMove = new LayoutParams(item.getMeasuredWidth(), item.getMeasuredHeight());
								this.addView(mMoveItemView, lpMove);
							}
							else
							{
								this.mAdapter.createExtendView(i, mMoveItemView, null);
							}
							this.measureItem(mMoveItemView);
							
							LayoutParams lpMove = (LayoutParams)mMoveItemView.getLayoutParams();
							lpMove.isNotInList = true;
							int toX = -this.getWidth();
							LayoutParams lp = (LayoutParams)item.getLayoutParams();
							if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERCLIP || 
									lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERFLOATCLIP)
							{
								int clipOffsetWidth = this.mAdapter.getExtendClipOffsetWidth(i);
								if(clipOffsetWidth < 0)
								{
									int clipOffsetViewId = this.mAdapter.getExtendClipOffsetViewId(i);
									View tmpChild = mMoveItemView.findViewById(clipOffsetViewId);
									if(tmpChild == null)
							            throw new IllegalArgumentException("The Layout type EXTEND_LAYOUT_OVERCLIP must have the ClipOffset View id.");
									mMoveItemView.layout(0, 0, mMoveItemView.getMeasuredWidth(), mMoveItemView.getMeasuredHeight());
									toX = tmpChild.getLeft()-this.getWidth();
								}
								else
								{
									toX = -clipOffsetWidth;
								}
								if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERFLOATCLIP)
									mMoveItemView.layout(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
							}
							mXmovePosition = i;
							ArrayList<MyAnimaParam> params = new ArrayList<MyAnimaParam>();
							isExtendShow = true;
							MyAnimaParam anim = new MyAnimaParam();
							anim.fromX = item.getLeft();
							anim.toX = toX;
							anim.fromY = item.getTop();
							anim.toY = item.getTop();
							anim.index = i;
							params.add(anim);
							mMoveItemView.setVisibility(View.VISIBLE);
							startAnimation(params);
							break;
						}
					}
				}
				initVelocityTrackerIfNotExists();
				mVelocityTracker.addMovement(ev);
				break;
			case MotionEvent.ACTION_DOWN:
				isMoveChanged = false;
				isItemXMove = false;
				mLastMotionX = x;
				mLastMotionY = y;
				isFling = false;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				
				initOrResetVelocityTracker();
				mVelocityTracker.addMovement(ev);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				isMoveChanged = false;
				recycleVelocityTracker();
				mTouchState = TOUCH_STATE_REST;
				break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}
	
	private void initOrResetVelocityTracker()
	{
		if(mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		else
		{
			mVelocityTracker.clear();
		}
	}
	
	private void initVelocityTrackerIfNotExists()
	{
		if(mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
	}
	
	private void recycleVelocityTracker()
	{
		if(mVelocityTracker != null)
		{
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		final int action = ev.getAction();

		if(this.isTopFloatItemTouch)
		{
			View child = super.getChildAt(this.mTopFloatItemPos);
			if(action == MotionEvent.ACTION_UP || 
					action == MotionEvent.ACTION_CANCEL)
			{
				this.isTopFloatItemTouch = false;
			}
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
			if(lp.mFloatTop-this.getScrollY() > 0)
			{
				ev.setLocation(ev.getX(), ev.getY()-(lp.mFloatTop-this.getScrollY()));
			}
			child.dispatchTouchEvent(ev);
			return true;
		}

		if(isItemXMove)
		{
			if(action == MotionEvent.ACTION_DOWN)
			{
				View item = super.getChildAt(mXmovePosition);
				ArrayList<MyAnimaParam> params = new ArrayList<MyAnimaParam>();
				
				isExtendShow = false;
				MyAnimaParam anim = new MyAnimaParam();
				int fromX = item.getLeft();
				LayoutParams lp = (LayoutParams)item.getLayoutParams();
				if(lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERCLIP || 
						lp.mLayoutType == MListViewAdapter.EXTEND_LAYOUT_OVERFLOATCLIP)
				{
					int clipOffsetWidth = this.mAdapter.getExtendClipOffsetWidth(mXmovePosition);
					if(clipOffsetWidth < 0)
					{
						int clipOffsetViewId = this.mAdapter.getExtendClipOffsetViewId(mXmovePosition);
						View tmpChild = mMoveItemView.findViewById(clipOffsetViewId);
						if(tmpChild == null)
				            throw new IllegalArgumentException("The Layout type EXTEND_LAYOUT_OVERCLIP must have the ClipOffset View id.");
						fromX = tmpChild.getLeft()-this.getWidth();
					}
					else
					{
						fromX = -clipOffsetWidth;
					}
				}
				anim.fromX = fromX;
				anim.toX = 0;
				anim.fromY = item.getTop();
				anim.toY = item.getTop();
				anim.index = mXmovePosition;
				params.add(anim);
				startAnimation(params);
			}
			else if(action == MotionEvent.ACTION_UP || 
					action == MotionEvent.ACTION_CANCEL)
			{
				recycleVelocityTracker();
			}
			return true;
		}
		if(this.isAnimating)
			return true;
		
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(ev);
		
		final float x = ev.getX();
		final float y = ev.getY();
		
		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
			{
				if(!mScroller.isFinished())
				{
					mScroller.abortAnimation();
				}
				isMoveChanged = false;
				isItemXMove = false;
				isFling = false;
				mLastMotionX = x;
				mLastMotionY = y;
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				if(mTouchState == TOUCH_STATE_SCROLLING)
				{
					if(!this.doDrawScrollBar)
					{
						if(mVScrollbar != null)
						{
							mScrollBarAnimate = null;
							this.mVScrollbar.setAlpha(255);
							this.doDrawScrollBar = true;
						}
					}
					int deltaY = (int)(mLastMotionY - y);
					checkDragRefresh();
					mLastMotionY = y;
					deltaY = fitDelta(deltaY);
					scrollBy(0, deltaY);
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				if(mTouchState == TOUCH_STATE_SCROLLING)
				{
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					int velocityY = (int) velocityTracker.getYVelocity();

					if(Math.abs(velocityY) > mMinimumVelocity)
					{
						mLastFlingY = 0;
						isFling = true;
						mScroller.fling(0, mLastFlingY, 0, (int)-velocityY, 0, 0, -8000, 8000);
						if(Math.abs(velocityY) < 3*mMinimumVelocity)
						{
							finishMove();
						}
						else
						{
							cancelMove();
						}
					}
					else
					{
						fixScrollY();
						finishMove();
					}
				}
				isMoveChanged = false;
				recycleVelocityTracker();
				mTouchState = TOUCH_STATE_REST;
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			{
				isMoveChanged = false;
				recycleVelocityTracker();
				mTouchState = TOUCH_STATE_REST;
				break;
			}
		}
		super.postInvalidate();
		return true;
	}
	
	private void checkDragRefresh()
	{
		if(isRefreshing)
			return;
		if(this.mHeaderView != null)
		{
			if(mOnListRefreshListener != null)
			{
				int scrollY = this.getScrollY();
				if(scrollY < 0)
				{
					if(Math.abs(scrollY) > this.mHeaderView.getMeasuredHeight())
					{
						if(!isHeaderFullShow)
						{
							isHeaderFullShow = true;
							mOnListRefreshListener.onDragRefreshed(this);
						}
					}
					else
					{
						if(isHeaderFullShow)
						{
							isHeaderFullShow = false;
							mOnListRefreshListener.onCancelRefreshed(this);
						}
					}
				}
			}
		}
	}
	
	public boolean isRefreshing()
	{
		return this.isRefreshing;
	}
	
	private void doRefresh(int flag)
	{
		if(isRefreshing)
			return;
		isRefreshing = true;
		if(mOnListRefreshListener != null)
		{
			mOnListRefreshListener.onStartRefreshed(this);
		}
	}
	
	public void onRefreshFinished(int flag)
	{
		if(!isRefreshing)
			return;
		if(mOnListRefreshListener != null)
		{
			isRefreshing = false;
			isHeaderFullShow = false;
			fixScrollY();
			mOnListRefreshListener.onFinishRefreshed(this);
		}
	}
	
	private void finishMove()
	{
		if(isMoveChanged && mOnListHelperListener != null && this.getChildCount() > 0)
		{
			if(this.getScrollY() < 0)
			{
				mOnListHelperListener.onMoveDownChanged(super.getChildAt(mTopItemPosition), 0, 
						Math.abs(this.getScrollY()), false);
			}
			else if(this.getScrollY() > this.mAllItemHeight - this.getHeight())
			{
				if(this.mAllItemHeight >= this.getHeight())
				{
					mOnListHelperListener.onMoveUpChanged(super.getChildAt(this.getChildCount()-1), 0, 
							Math.abs(this.getScrollY()-(this.mAllItemHeight-this.getHeight())), false);
				}
			}
		}
	}
	
	private void cancelMove()
	{
		if(isMoveChanged && mOnListHelperListener != null && this.getChildCount() > 0)
		{
			if(this.getScrollY() < 0)
			{
				mOnListHelperListener.onMoveCancel(super.getChildAt(mTopItemPosition));
			}
			else if(this.getScrollY() > this.mAllItemHeight - this.getHeight())
			{
				if(this.mAllItemHeight >= this.getHeight())
				{
					mOnListHelperListener.onMoveCancel(super.getChildAt(this.getChildCount()-1));
				}
			}
		}
	}
	
	private int fitDelta(int deltaY)
	{
		int newDelta = deltaY;
		if(this.getScrollY() < 0)
		{
			if(deltaY < 0)
				newDelta = (int)((1-(float)Math.abs(this.getScrollY())/(this.getHeight()/2))*deltaY);
			if(mOnListHelperListener != null)
			{
				if(this.getChildCount() > 0)
				{
					isMoveChanged = true;
					mOnListHelperListener.onMoveDownChanged(super.getChildAt(mTopItemPosition), 0, 
							Math.abs(this.getScrollY()), true);
				}
			}
		}
		else if(this.getScrollY() > this.mAllItemHeight - this.getHeight())
		{
			if(deltaY > 0)
				newDelta = (int)((1-(float)Math.abs(this.mAllItemHeight - this.getHeight() - this.getScrollY())/(this.getHeight()/2))*deltaY);
			if(mOnListHelperListener != null)
			{
				if(this.getChildCount() > 0 && this.mAllItemHeight >= this.getHeight())
				{
					isMoveChanged = true;
					mOnListHelperListener.onMoveUpChanged(super.getChildAt(this.getChildCount()-1), 0, 
							Math.abs(this.getScrollY()-(this.mAllItemHeight-this.getHeight())), true);
				}
			}
		}
		return newDelta;
	}
	
	private int mLastFlingY;
	
	@Override
	public void computeScroll()
	{
		if(mScroller.computeScrollOffset())
		{
			if(isFling)
			{
				int count = this.getChildCount();
				if(count > 0)
				{
					if(this.getScrollY() > this.mAllItemHeight - this.getHeight() + this.getHeight() / 6 || 
							this.getScrollY() < -this.getHeight() / 6)
					{
						if(!mScroller.isFinished())
							mScroller.abortAnimation();
						super.postInvalidate();
						return;
					}
				}
			}
            int y = mScroller.getCurrY() - mLastFlingY;
            mLastFlingY = mScroller.getCurrY();
			this.scrollTo(0, y+this.getScrollY());
			super.postInvalidate();
		}
		else
		{
			if(isFling && mScroller.isFinished())
			{
				isFling = false;
				fixScrollY();
			}
		}

//		System.out.println("this.getScrollY()="+this.getScrollY());
	}
	
	private void fixScrollY()
	{
		mLastFlingY = 0;
		if(this.getScrollY() < 0 || mAllItemHeight < this.getHeight())
		{
			int deltaY = -this.getScrollY();
			if(this.isHeaderFullShow)
			{
				doRefresh(0);
				deltaY -= this.mHeaderView.getMeasuredHeight();
			}
			this.mScroller.startScroll(0, 0, 0, deltaY, 800);
//			this.mScroller.startScroll(0, 0, 0, -this.getScrollY(), 800);
		}
		else if(this.getScrollY() > mAllItemHeight - this.getHeight())
		{
			this.mScroller.startScroll(0, 0, 0, mAllItemHeight - this.getHeight() - this.getScrollY(), 800);
		}
		startScrollBarAnimation(true);
		super.postInvalidate();
	}
	
	private MListViewAdapter mAdapter;
	
	public MListViewAdapter getAdapter()
	{
		return mAdapter;
	}
	
//	private boolean isStartAnima = false;
	
	private boolean isFinishInit = false;
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
//		ViewGroup.LayoutParams vl = this.getLayoutParams();
//		if(vl != null)
//		{
//			if(vl.height == ViewGroup.LayoutParams.WRAP_CONTENT)
//			{
//				isAutoHeight = true;
//			}
//		}
		if(mFirsetHeight == -1)
			mFirsetHeight = h;
		if(!isFinishInit)
		{
			isFinishInit = true;
			if(mAdapter != null)
				setAdapter(mAdapter, isOffsetFirstItem);
		}
		else
		{
			this.doOnLayout();
		}
	}
	
	private boolean isOffsetFirstItem;
	
	public void setAdapter(MListViewAdapter adapter)
	{
		setAdapter(adapter, false);
	}
	
	public void setAdapter(MListViewAdapter adapter, boolean offsetFirstItem)
	{
		isOffsetFirstItem = offsetFirstItem;
		if(!isFinishInit)
		{
			mAdapter = adapter;
			if(adapter == null)
				return;
			mAdapter.setListView(this);
			return;
		}
		this.scrollTo(0, 0);
		mAdapter = adapter;
		if(mAdapter != null)
		{
			mAdapter.setListView(this);
		}
		dealData(true);
		if(isOffsetFirstItem)
		{
			if(this.getChildCount() > 0)
			{
				View firstView = this.getChildAt(0);
				this.scrollTo(0, firstView.getMeasuredHeight());
			}
		}
		if(mHeaderView != null)
		{
			if(this.isRefreshing())
			{
				this.scrollTo(0, -this.mHeaderView.getMeasuredHeight());
			}
		}
		super.postInvalidate();
	}
	
	private int mTopFloatItemPos = -1;
	private boolean isTopFloatItemTouch = false;
	
	/*
	private Hashtable<Integer, Integer> mTypeViewHeights = new Hashtable<Integer, Integer>();
	private ArrayList<LayoutParams> mItemParams = new ArrayList<LayoutParams>();
	private void dealDataTest(boolean firstInit)
	{
		this.removeAllViews();
		mFirstVisibilePos = 0;
		mLastVisibilePos = 0;
		mTopFloatItemPos = -1;
		isTopFloatItemTouch = false;
		this.doDrawScrollBar = false;
		mMoveItemView = null;
		isItemXMove = false;
		mXmovePosition = 0;
		isAnimating = false;
		isExtendShow = false;
		mAllItemHeight = 0;
		mItemParams.clear();
		if(mAdapter == null)
			return;
		int count = mAdapter.getCount();
		int animateNum = 0;
		int childTop = 0;
		int minPos = count - 1;
		int maxPos = 0;
		
//		this.scrollTo(0, 400);
		int tmpCurScrollY = this.getScrollY();
		for(int i = 0; i < count; i++)
		{
			int viewType = mAdapter.getItemViewType(i);
			if(mAllItemHeight - tmpCurScrollY > this.getHeight() )
			{
				LayoutParams vl = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
						ViewGroup.LayoutParams.WRAP_CONTENT);
				vl.isOnTopInvisible = mAdapter.isOnTopInvisible(i);
				vl.isOnBottomInvisible = mAdapter.isOnBottomInvisible(i);
				vl.isOnTopFloatVisible = mAdapter.isOnTopFloatVisible(i);
				vl.mLayoutType = mAdapter.getExtendLayoutType(i);
				vl.mViewType = mAdapter.getItemViewType(i);
				vl.mAdapterPos = i;
				
				boolean isOnBottomInvisible = mAdapter.isOnBottomInvisible(i);
				if(mTypeViewHeights.containsKey(viewType))
				{
					vl.height = mTypeViewHeights.get(viewType);
					if(!isOnBottomInvisible)
						mAllItemHeight += mTypeViewHeights.get(viewType);
				}
				else
				{
					View view = mAdapter.getView(i, null, this);
					view.setLayoutParams(vl);
					this.measureItem(view);
					if(!isOnBottomInvisible)
						mAllItemHeight += view.getMeasuredHeight();
					mTypeViewHeights.put(viewType, view.getMeasuredHeight());
					vl.height = view.getMeasuredHeight();
				}
				mItemParams.add(vl);
			}
			else
			{
				int oldTypeHeight = 0;
				if(mTypeViewHeights.containsKey(viewType))
				{
					oldTypeHeight = mTypeViewHeights.get(viewType);
				}
				View view = mAdapter.getView(i, null, this);
				this.addView(view);
				this.measureItem(view);
				mTypeViewHeights.put(viewType, Math.max(oldTypeHeight, view.getMeasuredHeight()));
				
				//set view param
				boolean isEnabled = mAdapter.isEnabled(i);
				LayoutParams vl = (LayoutParams)view.getLayoutParams();
				if(vl == null)
				{
					vl = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
							ViewGroup.LayoutParams.WRAP_CONTENT);
					view.setLayoutParams(vl);
				}
				vl.isOnTopInvisible = mAdapter.isOnTopInvisible(i);
				vl.isOnBottomInvisible = mAdapter.isOnBottomInvisible(i);
				vl.isOnTopFloatVisible = mAdapter.isOnTopFloatVisible(i);
				vl.mLayoutType = mAdapter.getExtendLayoutType(i);
				vl.mViewType = mAdapter.getItemViewType(i);
				vl.mAdapterPos = i;
				view.setClickable(true);
				view.setFocusable(true);
				if(vl.isOnTopInvisible)
				{
					mTopItemPosition = i;
				}
				if(isEnabled && !vl.isOnTopInvisible && !vl.isOnTopFloatVisible
						 && !vl.isOnBottomInvisible)
					view.setOnClickListener(new MItemClick(i));
				
				final int childWidth = view.getMeasuredWidth();
				if(vl.isNotInList)
				{
					view.layout(0, 0, childWidth, view.getMeasuredHeight());
				}
				else
				{
					if(vl.isOnTopInvisible)
					{
						view.layout(0, -view.getMeasuredHeight(), childWidth, 0);
					}
					else
					{
						if(vl.isOnTopFloatVisible)
						{
							vl.mFloatTop = childTop;
//							mTopFloatItemPos = i;
						}
						view.layout(0, childTop, childWidth, childTop+view.getMeasuredHeight());
						childTop += view.getMeasuredHeight();
						if(!vl.isOnBottomInvisible)
							mAllItemHeight += view.getMeasuredHeight();
					}
				}
				if(!vl.isOnTopFloatVisible && !vl.isNotInList && !vl.isOnTopInvisible && !vl.isOnBottomInvisible)
				{
					animateNum++;
					minPos = Math.min(i, minPos);
					maxPos = Math.max(i, maxPos);
				}
				vl.height = view.getMeasuredHeight();
				mItemParams.add(vl);
			}
		}
		mFirstVisibilePos = minPos;
		mLastVisibilePos = maxPos;
		
		if(!firstInit)
		{
			if(mAllItemHeight < this.getHeight())
			{
				this.scrollTo(0, 0);
			}
			else if(this.getScrollY() > mAllItemHeight - this.getHeight())
			{
				this.scrollTo(0, mAllItemHeight - this.getHeight());
			}
		}

		initDrawScrollBar();
		if(firstInit)
			startAnimation(animateNum);
		
		System.out.println("[0]"+mFirstVisibilePos+","+mLastVisibilePos+","+mAllItemHeight+","+this.getChildCount());
	}
	*/
	
	private void dealData(boolean firstInit)
	{
		this.removeAllViews();
		mFirstVisibilePos = 0;
		mLastVisibilePos = -1;
		mTopFloatItemPos = -1;
		this.mHeaderView = null;
		isTopFloatItemTouch = false;
		this.doDrawScrollBar = false;
		mMoveItemView = null;
		isItemXMove = false;
		mXmovePosition = 0;
		isAnimating = false;
		isExtendShow = false;
		mAllItemHeight = 0;
		if(mAdapter == null)
			return;
		int count = mAdapter.getCount();
		int animateNum = 0;
		int childTop = 0;
		int minPos = count - 1;
		int maxPos = 0;
		for(int i = 0; i < count; i++)
		{
			int viewType = mAdapter.getItemViewType(i);
			
			View view = mAdapter.getView(i, null, this);
			boolean isEnabled = mAdapter.isEnabled(i);
			LayoutParams vl = (LayoutParams)view.getLayoutParams();
			if(vl == null)
			{
				vl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
						ViewGroup.LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(vl);
			}
			vl.mAdapterPos = i;
			vl.isOnTopInvisible = mAdapter.isOnTopInvisible(i);
			vl.isOnBottomInvisible = mAdapter.isOnBottomInvisible(i);
			vl.isOnTopFloatVisible = mAdapter.isOnTopFloatVisible(i);
			vl.mLayoutType = mAdapter.getExtendLayoutType(i);
			vl.mViewType = viewType;
			vl.mObject = mAdapter.getItem(i);
			view.setClickable(true);
			view.setFocusable(true);
			if(vl.isOnTopInvisible)
			{
				if(useHeadViewRefresh)
					this.mHeaderView = view;
//				isTopInvisible = true;
				mTopItemPosition = i;
			}
			if(isEnabled && !vl.isOnTopInvisible && !vl.isOnTopFloatVisible
					 && !vl.isOnBottomInvisible)
				view.setOnClickListener(new MItemClick(i));

			this.addView(view);
			this.measureItem(view);
			vl.mHeight = view.getMeasuredHeight();
			
			final int childWidth = view.getMeasuredWidth();
			if(vl.isNotInList)
			{
				view.layout(0, 0, childWidth, view.getMeasuredHeight());
			}
			else
			{
				if(vl.isOnTopInvisible)
				{
					view.layout(0, -view.getMeasuredHeight(), childWidth, 0);
				}
				else
				{
					int tmp = view.getMeasuredHeight();
					if(vl.isOnTopFloatVisible)
					{
						vl.mFloatTop = childTop;
						mTopFloatItemPos = i;
					}
					else if(vl.isSurplus)
					{
						tmp = this.getHeight() - mAllItemHeight;
						if(tmp > 0)
						{
							vl.mHeight = tmp;
							vl.height = tmp;
						}
						else
							tmp = view.getMeasuredHeight();
					}
					view.layout(0, childTop, childWidth, childTop+tmp);
					childTop += tmp;
					if(!vl.isOnBottomInvisible)
						mAllItemHeight += tmp;
				}
			}
			if(checkInVisible(view, vl))
			{
				if(!vl.isOnTopFloatVisible && !vl.isNotInList && !vl.isOnTopInvisible && !vl.isOnBottomInvisible)
				{
					animateNum++;
					minPos = Math.min(i, minPos);
					maxPos = Math.max(i, maxPos);
				}
			}
		}
		mFirstVisibilePos = minPos;
		mLastVisibilePos = maxPos;
		
		int myHeight = this.getHeight();
		
		if(isAutoHeight)
		{
			ViewGroup.LayoutParams vl = (ViewGroup.LayoutParams)this.getLayoutParams();
			if(vl != null)
			{
				myHeight = Math.min(this.mAllItemHeight, mFirsetHeight);
				if(vl.height != myHeight)
				{
//					this.requestFocus();
//					this.requestFocusFromTouch();
					vl.height = myHeight;
					this.setLayoutParams(vl);
//					this.layout(this.getLeft(), this.getTop(), this.getLeft()+this.getWidth(), 
//							this.getTop()+myHeight);
//					this.clearFocus();
//					System.out.println("this.getParent().isLayoutRequested()="+this.getParent().isLayoutRequested());
					this.requestLayout();
//					System.out.println("66666this.getParent().isLayoutRequested()="+getParent().isLayoutRequested());
					new Thread(new Runnable(){
						@Override
						public void run()
						{
							if(!isLayoutRequested())
							{
								mHandler.sendEmptyMessage(0);
								return;
							}
							while(isLayoutRequested())
							{
								try {
									Thread.sleep(20);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							mHandler.sendEmptyMessage(0);
						}
					}).start();
//					Rect outRect = new Rect();
//					this.getWindowVisibleDisplayFrame(outRect);
//					this.getRootView().invalidate(outRect);
				}
			}
		}
		
		if(!firstInit)
		{
			if(mAllItemHeight < myHeight)
			{
				this.scrollTo(0, 0);
			}
			else if(this.getScrollY() > mAllItemHeight - myHeight)
			{
				this.scrollTo(0, mAllItemHeight - myHeight);
			}
		}

		initDrawScrollBar(myHeight);
		if(firstInit)
			startAnimation(animateNum);
	}

	private Handler mHandler;/* = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			requestLayout();
		}
	};
	*/
	
	private void initDrawScrollBar(int h)
	{
		isDrawScrollBar = false;
		if(mVScrollbar != null)
		{
			if(mAllItemHeight > h)
			{
				int tmp = h - (mAllItemHeight - h) * h / mAllItemHeight;
				mVScrollbar.setBounds(this.getWidth()-mVScrollbar.getIntrinsicWidth(), 0, this.getWidth(), tmp);
				isDrawScrollBar = true;
			}
		}
	}
	
	private void startAnimation(int animateNum)
	{
		if(!mAllowFirstAnimate)
			return;
		int count = this.getChildCount();
		if(count == 0)
			return;
		for(int i = mFirstVisibilePos; i <= mLastVisibilePos; i++)
		{
			View child = super.getChildAt(i);
			LayoutParams vl = (LayoutParams)child.getLayoutParams();
			if(vl.isOnTopFloatVisible || vl.isNotInList || vl.isOnTopInvisible)
				continue;
			long startTime = (i-mFirstVisibilePos-1) * 300;
			setAnimation(child, startTime);
		}
		
		if(this.mAnimationListener != null)
		{
			if(animateNum < 1)
			{
				this.mAnimationListener.onAnimationEnd(null);
			}
			else
			{
				if(mLastVisibilePos < count)
				{
					View child = super.getChildAt(mLastVisibilePos);
					if(child.getAnimation() != null)
					{
						child.getAnimation().setAnimationListener(this.mAnimationListener);
					}
				}
			}
		}
	}
	
	private class MItemClick implements OnClickListener
	{
		private int mPos;
		
		public MItemClick(int position)
		{
			mPos = position;
		}
		
		@Override
		public void onClick(View v)
		{
			if(mOnItemClickListener != null)
			{
				mOnItemClickListener.onItemClick(mAdapterView, v, mPos, v.getId());
			}
		}
	}
	
	private class MAdapterView extends AdapterView<MListViewAdapter>
	{
		public MAdapterView(Context context)
		{
			super(context);
		}

		@Override
		public MListViewAdapter getAdapter()
		{
			return MListView.this.mAdapter;
		}

		@Override
		public View getSelectedView()
		{
			return null;
		}

		@Override
		public void setAdapter(MListViewAdapter adapter)
		{
			MListView.this.setAdapter(adapter, isOffsetFirstItem);
		}

		@Override
		public void setSelection(int position)
		{
		}

		@Override
		public int getId()
		{
			return MListView.this.getId();
		}
	}

	private int mFirstVisibilePos = 0;
	private int mLastVisibilePos = -1;
	
	public int getFirstVisiblePosition()
	{
		return mFirstVisibilePos;
	}
	
	public int getLastVisiblePosition()
	{
		return mLastVisibilePos;
	}
	
	private AnimationListener mAnimationListener;
	
	@Override
	public void setLayoutAnimationListener(AnimationListener listener)
	{
		mAnimationListener = listener;
	}
	
	private OnItemClickListener mOnItemClickListener;
	
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mOnItemClickListener = listener;
	}

	void notifyDataSetChanged()
	{
//		dealData(false);
		mFirstVisibilePos = 0;
		mLastVisibilePos = -1;
		mTopFloatItemPos = -1;
		isTopFloatItemTouch = false;
		this.doDrawScrollBar = false;
		mMoveItemView = null;
		isItemXMove = false;
		mXmovePosition = 0;
		this.mAllItemHeight = 0;
		isAnimating = false;
		isExtendShow = false;
		
		final MListViewAdapter adapter = this.mAdapter;
		int count = adapter.getCount();
		int oldCount = this.getChildCount();
		if(count < oldCount)
		{
			for(int i = oldCount - 1; i >= count; i--)
			{
				View view = this.getChildAt(i);
				adapter.recycle(view);
				this.removeView(view);
			}
			oldCount = this.getChildCount();
		}
		int tmpTop = 0;
		for(int i = 0; i < count; i++)
		{
			int viewType = adapter.getItemViewType(i);
			if(i < oldCount)
			{
				View oldChild = this.getChildAt(i);
				LayoutParams lp = (LayoutParams)oldChild.getLayoutParams();
				lp.isOnTopInvisible = adapter.isOnTopInvisible(i);
				lp.isOnBottomInvisible = adapter.isOnBottomInvisible(i);
				lp.isOnTopFloatVisible = adapter.isOnTopFloatVisible(i);
				lp.mLayoutType = adapter.getExtendLayoutType(i);
				lp.mClipWidth = 0;
				lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				if(viewType != lp.mViewType)
				{
					this.removeViewAt(i);
					lp.mViewType = viewType;
					oldChild = adapter.getView(i, null, this);
					this.addView(oldChild, i);
					oldChild.setClickable(true);
					oldChild.setFocusable(true);
					oldChild.setLayoutParams(lp);
					this.measureItem(oldChild);
					if(lp.isOnTopInvisible)
					{
						oldChild.layout(0, -oldChild.getMeasuredHeight(), oldChild.getMeasuredHeight(), 0);
					}
					else
					{
						if(lp.isOnTopFloatVisible)
						{
							lp.mFloatTop = tmpTop;
							mTopFloatItemPos = i;
						}
						oldChild.layout(0, tmpTop, oldChild.getMeasuredWidth(), 
								tmpTop+oldChild.getMeasuredHeight());
						this.mAllItemHeight += oldChild.getMeasuredHeight();
						tmpTop += oldChild.getMeasuredHeight();
					}
					lp.mHeight = oldChild.getMeasuredHeight();
					boolean isEnabled = adapter.isEnabled(i);
					if(isEnabled && !lp.isOnTopInvisible && !lp.isOnTopFloatVisible
							 && !lp.isOnBottomInvisible)
						oldChild.setOnClickListener(new MItemClick(i));
				}
				else
				{
					Object itemObj = adapter.getItem(i);
//					System.out.println("lp.mObject="+lp.mObject+",itemObj="+itemObj);
					if(lp.mObject != null && itemObj != null && !lp.mObject.equals(itemObj))
						adapter.getView(i, oldChild, this);
					this.measureItem(oldChild);
					oldChild.setLayoutParams(lp);
					if(lp.isOnTopInvisible)
					{
						oldChild.layout(0, -oldChild.getMeasuredHeight(), oldChild.getMeasuredWidth(), 0);
					}
					else
					{
						if(lp.isOnTopFloatVisible)
						{
							lp.mFloatTop = tmpTop;
							mTopFloatItemPos = i;
						}
						oldChild.layout(0, tmpTop, oldChild.getMeasuredWidth(), 
								tmpTop+oldChild.getMeasuredHeight());
						this.mAllItemHeight += oldChild.getMeasuredHeight();
						tmpTop += oldChild.getMeasuredHeight();
					}
					lp.mHeight = oldChild.getMeasuredHeight();
				}
				lp.mAdapterPos = i;
				lp.mObject = adapter.getItem(i);
			}
			else
			{
				View view = adapter.getView(i, null, this);
				LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
					ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.mAdapterPos = i;
				lp.isOnTopInvisible = adapter.isOnTopInvisible(i);
				lp.isOnBottomInvisible = adapter.isOnBottomInvisible(i);
				lp.isOnTopFloatVisible = adapter.isOnTopFloatVisible(i);
				lp.mLayoutType = adapter.getExtendLayoutType(i);
				lp.mViewType = viewType;
				lp.mClipWidth = 0;
				lp.mObject = adapter.getItem(i);
				view.setClickable(true);
				view.setFocusable(true);
				this.addView(view);
				this.measureItem(view);
				if(lp.isOnTopInvisible)
				{
					view.layout(0, -view.getMeasuredHeight(), view.getMeasuredHeight(), 0);
				}
				else
				{
					if(lp.isOnTopFloatVisible)
					{
						lp.mFloatTop = tmpTop;
						mTopFloatItemPos = i;
					}
					view.layout(0, tmpTop, view.getMeasuredWidth(), 
							tmpTop + view.getMeasuredHeight());
					this.mAllItemHeight += view.getMeasuredHeight();
					tmpTop += view.getMeasuredHeight();
				}
				lp.mHeight = view.getMeasuredHeight();
				view.setLayoutParams(lp);
				boolean isEnabled = adapter.isEnabled(i);
				if(isEnabled && !lp.isOnTopInvisible && !lp.isOnTopFloatVisible
						 && !lp.isOnBottomInvisible)
					view.setOnClickListener(new MItemClick(i));
			}
		}
		
		int myHeight = this.getHeight();
		if(isAutoHeight)
		{
			ViewGroup.LayoutParams vl = (ViewGroup.LayoutParams)this.getLayoutParams();
			if(vl != null)
			{
				myHeight = Math.min(this.mAllItemHeight, mFirsetHeight);
				if(vl.height != myHeight)
				{
					vl.height = myHeight;
					this.setLayoutParams(vl);
					this.layout(this.getLeft(), this.getTop(), this.getLeft()+this.getWidth(), 
							this.getTop()+myHeight);
				}
			}
		}
		
		if(this.getScrollY() > 0)
		{
			if(mAllItemHeight < myHeight)
			{
				this.scrollTo(0, 0);
			}
			else if(this.getScrollY() > mAllItemHeight - myHeight)
			{
				this.scrollTo(0, mAllItemHeight - myHeight);
			}
		}
		this.initDrawScrollBar(myHeight);
		this.postInvalidate();
	}
	
	public static class LayoutParams extends ViewGroup.LayoutParams
	{
		public boolean isOnTopInvisible;
		public boolean isOnBottomInvisible;
		public boolean isOnTopFloatVisible;
		public boolean isNotInList;
		int mFloatTop;
		int mViewType;
		int mAdapterPos;
		int mClipWidth;
		Object mObject;
		boolean isRecycled;
		int mHeight;
		public int mLayoutType = MListViewAdapter.EXTEND_LAYOUT_NORMAL;
		public boolean isSurplus;
		
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
		return p instanceof MListView.LayoutParams;
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
	
	public interface OnListHelperListener
	{
		void onMoveDownChanged(View view, int x, int y, boolean moving);
		void onMoveUpChanged(View view, int x, int y, boolean moving);
		void onMoveCancel(View view);
	}
	
	public interface OnListRefreshListener
	{
		void onDragRefreshed(View view);
		void onCancelRefreshed(View view);
		void onStartRefreshed(View view);
		void onFinishRefreshed(View view);
	}
	
	private OnListRefreshListener mOnListRefreshListener;
	
	public void setOnListRefreshListener(OnListRefreshListener listener)
	{
		this.mHeaderView = null;
		useHeadViewRefresh = false;
		if(listener != null)
			useHeadViewRefresh = true;
		this.mOnListRefreshListener = listener;
	}
	
	public static class SimpleOnListHelperListener implements OnListHelperListener
	{
		@Override
		public void onMoveDownChanged(View view, int x, int y, boolean moving)
		{
		}
		
		@Override
		public void onMoveUpChanged(View view, int x, int y, boolean moving)
		{
		}
		
		@Override
		public void onMoveCancel(View view)
		{
		}
	}
	
	private OnListHelperListener mOnListHelperListener;
	
	public void setOnListHelperListener(OnListHelperListener listener)
	{
		mOnListHelperListener = listener;
	}
	
	private ArrayList<MyAnimation> mMyAnimations = new ArrayList<MyAnimation>();
	
	private void startAnimation(ArrayList<MyAnimaParam> params)
	{
		MyAnimation anima = new MyAnimation(params);
		anima.setDuration(250);
		anima.startNow();
		mMyAnimations.add(anima);
		isAnimating = true;
		super.invalidate();
	}
	
	private class MyAnimaParam
	{
		public float fromX;
		public float toX;
		public float fromY;
		public float toY;
		public int index;
		
		public MyAnimaParam()
		{
		}
	}
	
	private class MyAnimaValue
	{
		public float valueX;
		public float valueY;
		public int index;
		
		public MyAnimaValue()
		{
		}
	}
	
	private class MyAnimation
	{
		private ArrayList<MyAnimaParam> mParams;
		private long mDuration = 500;
		private long mStartTime = -1;
		private long mStartOffset = 0;
		private boolean mEnd = true;
//		public boolean isAphachChanged = false;
		
		public MyAnimation(ArrayList<MyAnimaParam> params)
		{
			mParams = params;
//			isAphachChanged = false;
		}
		
		/*
		public MyAnimation(ArrayList<MyAnimaParam> params, boolean isAphache)
		{
			mParams = params;
//			isAphachChanged = isAphache;
		}
		
		public void setStartOffset(long startOffset)
		{
			mStartOffset = startOffset;
		}
		*/
		
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
		
		public boolean getTransformation(long when, ArrayList<MyAnimaValue> values)
		{
			if(mEnd)
				return false;
			
			if(mStartTime == -1)
			{
				mStartTime = when;
			}
			
			if(when - mStartTime < mStartOffset)
			{
				int size = mParams.size();
				for(int i = 0; i < size; i++)
				{
					MyAnimaParam aniParam = mParams.get(i);
					MyAnimaValue value = new MyAnimaValue();
					value.valueX = aniParam.fromX;
					value.valueY = aniParam.fromY;
					value.index = aniParam.index;
					values.add(value);
				}
				return true;
			}
			
			float normalizedTime = ((float)(when - mStartTime - mStartOffset)) / (float)mDuration;
			
			boolean expired = normalizedTime >= 1.0f;
			normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
			if(!expired)
			{
				if(normalizedTime >= 0.0f && normalizedTime <= 1.0f)
				{
					int size = mParams.size();
					for(int i = 0; i < size; i++)
					{
						MyAnimaParam aniParam = mParams.get(i);
						float segX = aniParam.toX - aniParam.fromX;
						float segY = aniParam.toY - aniParam.fromY;
						MyAnimaValue value = new MyAnimaValue();
						value.valueX = normalizedTime * segX + aniParam.fromX;
						value.valueY = normalizedTime * segY + aniParam.fromY;
						value.index = aniParam.index;
						values.add(value);
					}
				}
			}
			else
			{
				int size = mParams.size();
				for(int i = 0; i < size; i++)
				{
					MyAnimaParam aniParam = mParams.get(i);
					MyAnimaValue value = new MyAnimaValue();
					value.valueX = aniParam.toX;
					value.valueY = aniParam.toY;
					value.index = aniParam.index;
					values.add(value);
				}
				mEnd = true;
			}
			return true;
		}
	}
	
	public void refresh()
	{
		int count = this.getChildCount();
		for(int i = 0; i < count; i++)
		{
			View child = this.getChildAt(i);
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
//			if(lp.mAdapterPos >= mFirstVisibilePos && lp.mAdapterPos <= mLastVisibilePos)
			{
				this.mAdapter.refreshView(lp.mAdapterPos, this.getChildAt(i), this);
				this.measureItem(child);
				child.layout(child.getLeft(), child.getTop(), child.getLeft()+child.getMeasuredWidth(), 
						child.getTop()+child.getMeasuredHeight());
//				child.requestLayout();
//				child.invalidate();
//				System.out.println("***********333 *******************");
			}
		}
//		requestLayout();
//		invalidate();
	}
}
