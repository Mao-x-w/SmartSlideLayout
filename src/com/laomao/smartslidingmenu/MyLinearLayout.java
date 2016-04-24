package com.laomao.smartslidingmenu;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	private SmartSlidingMenu slidingMenu;

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//如果当前是打开的状态，那么就不能让listview拿到事件
		slidingMenu=(SmartSlidingMenu) getParent();
		if (slidingMenu.getCurrentState()==SmartSlidingMenu.State_Open) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (slidingMenu.getCurrentState()==SmartSlidingMenu.State_Open) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				slidingMenu.close();
			}
			return true;
		}
		return super.onTouchEvent(event);
	}
	
}
