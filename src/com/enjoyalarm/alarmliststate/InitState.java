package com.enjoyalarm.alarmliststate;

import com.enjoyalarm.view.ViewControlInterface;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class InitState extends State {

	public InitState(ViewControlInterface controlInterface) {
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
