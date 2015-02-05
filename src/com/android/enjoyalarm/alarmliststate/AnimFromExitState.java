package com.android.enjoyalarm.alarmliststate;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.android.enjoyalarm.drawcomponent.Component;
import com.android.enjoyalarm.view.ViewControlInterface;

public class AnimFromExitState extends State {

	private Component mLeftTextComponent;
	private Component mRightTextComponent;
	private Component mLeftBitmapComponent;
	private Component mRightBitmapComponent;
	private float mPositionX;//赋值时必须映射为相对坐标
	private int mDirection;// 0.to left 1.to right
	private float mViewWidth;
	private boolean mThreadFlag;

	/**
	 * init from ExitingState only
	 * components:leftText,rightText,leftBitmap,rightBitmap
	 */
	public AnimFromExitState(ViewControlInterface controlInterface,
			float initPositionX, int initDirection, Component[] components) {
		super(controlInterface);

		mPositionX = initPositionX;
		mDirection = initDirection;
		mViewWidth = controlInterface.getViewWidth();
		
		mLeftTextComponent = components[0];
		mRightTextComponent = components[1];
		mLeftBitmapComponent = components[2];
		mRightBitmapComponent = components[3];

		mThreadFlag = true;
		new AnimThread().start();
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mThreadFlag = false;
			mControlInterface.changeState(new ExitingState(mControlInterface,
					mPositionX, new Component[] { mLeftTextComponent,
							mRightTextComponent, mLeftBitmapComponent,
							mRightBitmapComponent }));
		}
	}

	@Override
	public void handleDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		Component textComponent;
		Component bitmapComponent;
		if (mPositionX > 0) {
			textComponent = mLeftTextComponent;
			bitmapComponent = mLeftBitmapComponent;
		} else {
			textComponent = mRightTextComponent;
			bitmapComponent = mRightBitmapComponent;
		}

		float factor = Math.abs(mPositionX) / mViewWidth;
		textComponent.draw(canvas, factor);
		bitmapComponent.draw(canvas, factor);
	}

	private void changeToSetting() {
		mControlInterface.handleScrollToSettingFinished();
	}

	private class AnimThread extends Thread {
		@Override
		public void run() {
			float velocity = Math.abs(mPositionX) / 128;
			float limit = mViewWidth * 0.01f;

			if (mDirection == 0) {// to left
				while (mThreadFlag) {
					mPositionX -= velocity;
					velocity *= 1.5f; // decelerate
					mControlInterface.refreshDraw();

					if (mPositionX < limit) {
						changeToSetting();
						break;
					}

					try {
						sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} else {// to right
				while (mThreadFlag) {
					mPositionX += velocity;
					velocity *= 1.5f; // decelerate
					mControlInterface.refreshDraw();

					if (mPositionX > -limit) {
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
