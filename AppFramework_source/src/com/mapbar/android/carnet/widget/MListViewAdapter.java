package com.mapbar.android.carnet.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MListViewAdapter extends BaseAdapter
{
	public final static int EXTEND_LAYOUT_NORMAL = 0;
	public final static int EXTEND_LAYOUT_OVERCLIP = 1;
	public final static int EXTEND_LAYOUT_OVERFLOAT = 2;
	public final static int EXTEND_LAYOUT_OVERFLOATCLIP = 3;
	
	private MListView mListView;
	
	public View createExtendView(final int position, View convertView, ViewGroup parent)
	{
		return null;
	}
	
	public View refreshView(int position, View convertView, ViewGroup parent)
	{
		return null;
	}
	
	public boolean canHorizontalMove(int position)
	{
		return false;
	}
	
	public int getExtendLayoutType(int position)
	{
		return EXTEND_LAYOUT_NORMAL;
	}
	
	public int getExtendClipOffsetViewId(int position)
	{
		return -1;
	}
	
	public int getExtendClipOffsetWidth(int position)
	{
		return -1;
	}
	
	public boolean isOnTopInvisible(int position)
	{
		return false;
	}
	
	public boolean isOnBottomInvisible(int position)
	{
		return false;
	}
	
	public boolean isOnTopFloatVisible(int position)
	{
		return false;
	}
	
	public boolean isRefresh(int position)
	{
		return false;
	}
	
	public void recycle(View view)
	{
	}
	
	void setListView(MListView listview)
	{
		mListView = listview;
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		if(mListView != null)
			mListView.notifyDataSetChanged();
		super.notifyDataSetChanged();
	}
}
