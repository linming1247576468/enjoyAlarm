package com.enjoyalarm.alarmliststate;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.enjoyalarm.drawcomponent.AlarmListDrawer;
import com.enjoyalarm.view.ViewControlInterface;

public class ListState extends State {

	private AlarmListDrawer mAlarmListDrawer;
	private float mViewWidth;
	private float mViewHeight;

	public ListState(ViewControlInterface controlInterface) {
		super(controlInterface);

		init();
	}

	private void init() {
		mViewWidth = mControlInterface.getViewWidth();
		mViewHeight = mControlInterface.getViewHeight();
		mAlarmListDrawer = new AlarmListDrawer(mViewWidth * 0.1f, mViewWidth,
				mViewHeight);
		mAlarmListDrawer.initItems(mControlInterface.getAlarmsInfo(),
				mControlInterface.getAlarmsColor(), Color.WHITE,
				30 * mControlInterface.getDensity(), mViewWidth * 0.6f,
				mViewHeight * 0.6f, mControlInterface.getViewContext().getResources());
		mAlarmListDrawer.setCurrentIndex(mControlInterface.getCurrentAlarmIndex());
	}

	
	private float mX = -1;
	private float mGapXFactor;
	@Override
	public void handleTouchEvent(MotionEvent event) {
		float x = event.getX();
		switch(event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mX = x;
			break;
		}
		
		case MotionEvent.ACTION_MOVE: {
			if (mX == -1) {
				mX = x;
			} else {
				mGapXFactor = 1.5f * (x - mX) / mViewWidth;
				mControlInterface.refreshDraw();
				mX = x;
			}
			break;
		}
		
		case MotionEvent.ACTION_POINTER_UP: {
			mX = -1;
			break;
		}
		}
	}

	@Override
	public void handleDraw(Canvas canvas) {
		mAlarmListDrawer.draw(canvas, mGapXFactor);
	}

}
