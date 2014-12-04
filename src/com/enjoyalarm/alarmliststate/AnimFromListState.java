package com.enjoyalarm.alarmliststate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.enjoyalarm.drawcomponent.Component;
import com.enjoyalarm.view.ViewControlInterface;

public class AnimFromListState extends State {

	private Component mTopBitmapComponent;
	private Component mBottomBitmapComponent;
	private Bitmap mBitmap;
	private float mPositionY;//赋值时必须映射为相对坐标
	private float mViewHeight;
	private int mDirection;// move mDirection:0.to Top 1.to Bottom
	private boolean mThreadFlag;
	
	
	/**
	 * init from ListingState only
	 * components:topBitmap,bottomBitmap
	 */
	public AnimFromListState(ViewControlInterface controlInterface,Bitmap foregroundBitmap,
			float initPositionY, int initDirection, Component[] components) {
		super(controlInterface);
		
		mBitmap = foregroundBitmap;
		mPositionY = initPositionY;
		mDirection = initDirection;
		mViewHeight = controlInterface.getViewHeight();

		mTopBitmapComponent = components[0];
		mBottomBitmapComponent = components[1];
		
		mThreadFlag = true;
		new AnimThread().start();
	}
	
	@Override
	public void handleTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mThreadFlag = false;
			mControlInterface.changeState(new ListingState(mControlInterface,
					mBitmap, mPositionY, new Component[] { mTopBitmapComponent,
							mBottomBitmapComponent }));
		}
	}

	@Override
	public void handleDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		Component bitmapComponent;
		if (mPositionY > 0) {
			bitmapComponent = mTopBitmapComponent;
		} else {
			bitmapComponent = mBottomBitmapComponent;
		}

		float factor = Math.abs(mPositionY) / mViewHeight;
		bitmapComponent.draw(canvas, factor);
	}
	
	private void changeToSetting() {
		mControlInterface.handleScrollToSettingFinished();
	}

	
	
	private class AnimThread extends Thread {
		@Override
		public void run() {
			float velocity = Math.abs(mPositionY) / 128;
			float limit = mViewHeight * 0.01f;

			if (mDirection == 0) {// to top
				while (mThreadFlag) {
					mPositionY -= velocity;
					velocity *= 1.5f; // accelerate
					mControlInterface.refreshDraw();

					if (mPositionY < limit) {
						changeToSetting();
						break;
					}

					try {
						sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} else {// to bottom
				while (mThreadFlag) {
					mPositionY += velocity;
					velocity *= 1.5f; // accelerate
					mControlInterface.refreshDraw();

					if (mPositionY > -limit) {
						changeToSetting();
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

}