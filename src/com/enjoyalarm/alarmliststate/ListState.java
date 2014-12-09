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
	private ClickMode mClickMode;
	
	enum ClickMode {
		NONE, CLICK, DELETE, SCROLL
	}

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
				30 * mControlInterface.getDensity(), mViewWidth * StatePeriod.LIST_ITEM_SCALE,
				mViewHeight * StatePeriod.LIST_ITEM_SCALE, mControlInterface.getViewContext().getResources());
		mAlarmListDrawer.setCurrentIndex(mControlInterface.getCurrentAlarmIndex());
		
		mTouchGap = 5 * mControlInterface.getDensity();
	}

	
	private float mX = -1;
	private float mY = -1;
	private float mTouchGap;//used to judge whether user has moved or not
	private float mGapXFactor;
	private float mThreadGapXFactor;//used to animation
	private int mClickIndex;
	private boolean mThreadFlag;
	
	
	@Override
	public void handleTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch(event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mThreadFlag = false;
			mX = x;
			mY = y;
			mClickIndex = mAlarmListDrawer.getClickItemIndex(x, y);
			if (mClickIndex == -1) {
				mClickMode = ClickMode.SCROLL;
			} else {
				mClickMode = ClickMode.NONE;
			}
			break;
		}
		
		case MotionEvent.ACTION_MOVE: {
			if (mX == -1) {
				mThreadFlag = false;
				mX = x;
				mY = y;
				mClickIndex = mAlarmListDrawer.getClickItemIndex(x, y);
				if (mClickIndex == -1) {
					mClickMode = ClickMode.SCROLL;
				} else {
					mClickMode = ClickMode.NONE;
				}
			} else {
				
				switch(mClickMode) {
				case NONE: {
					if (Math.abs(x - mX) < mTouchGap && Math.abs(y - mY) < mTouchGap) {//click
						mClickMode = ClickMode.CLICK;
						
					} else if (Math.abs(x - mX) >= Math.abs(y - mY)){//move horizon
						mClickMode = ClickMode.SCROLL;
						
					} else {//move vertical
						mClickMode = ClickMode.DELETE;
					}
					handleTouchEvent(event);
					break;
				}
				
				case CLICK: {
					float moveX = Math.abs(x - mX);
					float moveY = Math.abs(y - mY);
					if(moveX > mTouchGap || moveY > mTouchGap) {
						if (moveX >= moveY){//move horizon
							mClickMode = ClickMode.SCROLL;
							
						} else {//move vertical
							mClickMode = ClickMode.DELETE;
						}
						handleTouchEvent(event);
						
					} 
					break;
				}
				
				case DELETE: {
					mX = x;
					mY = y;
					//mControlInterface.refreshDraw();
					break;
				}
				
				case SCROLL: {
					if (Math.abs(x - mX) >= Math.abs(y - mY)) {
						mGapXFactor = 1.4f * (x - mX) / mViewWidth;
						mControlInterface.refreshDraw();
					}
					mX = x;
					mY = y;
					
					break;
				}
				}
				
			}
			break;
		}
		
		case MotionEvent.ACTION_UP: {
			mThreadGapXFactor = mGapXFactor;
			mGapXFactor = 0f;
			
			switch(mClickMode) {
			case CLICK: {
				mControlInterface.changeState(new AnimClickState(
						mControlInterface, mAlarmListDrawer.getItems(),
						mClickIndex, mAlarmListDrawer.getNowFactor()));
				break;
			}
			
			case SCROLL: {
				if (Math.abs(x - mX) >= Math.abs(y - mY)) {
					mThreadFlag = true;
					new AnimThread().start();
				}
				break;
			}
			
			}
			
			mX = -1;
			mY = -1;
			break;
		}
		}
	}

	
	
	@Override
	public void handleDraw(Canvas canvas) {
		if (mThreadFlag) {
			mAlarmListDrawer.draw(canvas, mThreadGapXFactor);
		} else {
			mAlarmListDrawer.draw(canvas, mGapXFactor);
		}
		
	}

	
	private class AnimThread extends Thread {
		
		@Override
		public void run() {
			float gap = mThreadGapXFactor * 0.005f;
			float gapGap = mThreadGapXFactor * 0.004f;
			float limit = Math.abs(mThreadGapXFactor) * 0.1f;
			
			while (mThreadFlag) {
				mThreadGapXFactor -= gap;
				gap += gapGap;
				if (Math.abs(mThreadGapXFactor) < limit) {
					mThreadGapXFactor = 0f;
					mThreadFlag = false;
					break;
				}
				
				mControlInterface.refreshDraw();
				
				if (!mThreadFlag) {
					break;
				}
				
				try {
					sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
