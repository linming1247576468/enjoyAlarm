package com.enjoyalarm.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {

	private float mDownX;
	private float mDownY;
	private float mInterceptLimit;
	
	public MyFrameLayout(Context context) {
		super(context);
		
		mInterceptLimit = 10 * getResources().getDisplayMetrics().density;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		float nowX = ev.getX();
		float nowY = ev.getY();
		
		switch(ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mDownX = nowX;
			mDownY = nowY;
			break;
		}
		
		case MotionEvent.ACTION_MOVE: {
			if (Math.abs(nowX - mDownX) > mInterceptLimit || Math.abs(nowY - mDownY) > mInterceptLimit) {
				return true;
			}
		}
		}
			
		return false;
		
	}

}
