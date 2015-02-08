package com.android.enjoyalarm.alarmliststate;

import com.android.enjoyalarm.view.ListViewControlInterface;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class InitState extends State {

	public InitState(ListViewControlInterface controlInterface) {
		super(controlInterface);
	}

	/**
	 * do nothing
	 */
	@Override
	public void handleTouchEvent(MotionEvent event) {
	}

	/**
	 * do nothing
	 */
	@Override
	public void handleDraw(Canvas canvas) {
	}

}
