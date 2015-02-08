package com.android.enjoyalarm.alarmliststate;

import com.android.enjoyalarm.view.ListViewControlInterface;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class State {

	protected ListViewControlInterface mControlInterface;
	
	public State(ListViewControlInterface controlInterface) {
		mControlInterface = controlInterface;
	}
	
	abstract public void handleTouchEvent(MotionEvent event);
	
	abstract public void handleDraw(Canvas canvas);
}
