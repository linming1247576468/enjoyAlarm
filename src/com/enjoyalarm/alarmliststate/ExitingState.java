package com.enjoyalarm.alarmliststate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.enjoyalarm.drawutil.BitmapComponent;
import com.enjoyalarm.drawutil.Component;
import com.enjoyalarm.drawutil.ExitTextComponent;
import com.enjoyalarm.view.ViewControlInterface;
import com.scut.enjoyalarm.R;

public class ExitingState extends State {

	private Component mLeftTextComponent;
	private Component mRightTextComponent;
	private Component mLeftBitmapComponent;
	private Component mRightBitmapComponent;
	private Bitmap mBitmap;
	private float mX;
	private float mPositionX;//赋值时必须映射为相对坐标,不能直接用MotionEvent的值（屏幕坐标）
	private float mViewWidth;
	private float mViewHeight;
	private int mDirection;// move mDirection:0.to left 1.to right
	private float mExitingLimit;// when mPositionX is within the limit, then it
								// should recover instead of exit

	/**
	 * @param initPositionX must be relative
	 */
	public ExitingState(ViewControlInterface controlInterface,
			Bitmap foregroundBitmap, float initPositionX) {
		super(controlInterface);

		mBitmap = foregroundBitmap;
		mX = -1;
		mPositionX = initPositionX;
		
		initData();
		initComponents();
	}

	/**
	 * init from other state for reusing resource 
	 * @param components :leftText, rightText, leftBitmap, RightBitmap;
	 * @param initPositionX must be relative
	 */
	public ExitingState(ViewControlInterface controlInterface,
			float initPositionX, Component... components) {
		super(controlInterface);

		mX = -1;
		mPositionX = initPositionX;
		
		initData();

		mLeftTextComponent = components[0];
		mRightTextComponent = components[1];
		mLeftBitmapComponent = components[2];
		mRightBitmapComponent = components[3];
	}

	private void initData() {
		mViewWidth = mControlInterface.getViewWidth();
		mViewHeight = mControlInterface.getViewHeight();
		mExitingLimit = mViewWidth * StatePeriod.EXIT_LIMIT;
	}
	
	private void initComponents() {
		String[] chars1 = mControlInterface.getContext().getResources()
				.getStringArray(R.array.exit_words1);
		String[] chars2 = mControlInterface.getContext().getResources()
				.getStringArray(R.array.exit_words2);
		mLeftTextComponent = new ExitTextComponent(chars1,
				48 * mControlInterface.getDensity(), Color.WHITE, mViewWidth,
				mViewHeight);
		mRightTextComponent = new ExitTextComponent(chars2,
				48 * mControlInterface.getDensity(), Color.WHITE, mViewWidth,
				mViewHeight);
		mLeftBitmapComponent = new BitmapComponent(mBitmap, mViewWidth,
				mViewHeight, mViewWidth, mViewHeight);
		mRightBitmapComponent = new BitmapComponent(mBitmap, mViewWidth,
				mViewHeight, mViewWidth, mViewHeight);

		mLeftTextComponent.addAlphaEntry(StatePeriod.EXIT_LTEXT_ALPHA_PERIOD1);
		mLeftTextComponent.addAlphaEntry(StatePeriod.EXIT_LTEXT_ALPHA_PERIOD2);
		mLeftTextComponent.addScaleEntry(StatePeriod.EXIT_LTEXT_SCALE_PERIOD1);
		mLeftTextComponent.addScaleEntry(StatePeriod.EXIT_LTEXT_SCALE_PERIOD2);
		mLeftTextComponent.addTranslationEntry(StatePeriod.EXIT_LTEXT_TRANS_PREIOD1);
		mLeftTextComponent.addTranslationEntry(StatePeriod.EXIT_LTEXT_TRANS_PREIOD2);

		mRightTextComponent.addAlphaEntry(StatePeriod.EXIT_LTEXT_ALPHA_PERIOD1);
		mRightTextComponent.addAlphaEntry(StatePeriod.EXIT_LTEXT_ALPHA_PERIOD2);
		mRightTextComponent.addScaleEntry(StatePeriod.EXIT_LTEXT_SCALE_PERIOD1);
		mRightTextComponent.addScaleEntry(StatePeriod.EXIT_LTEXT_SCALE_PERIOD2);
		mRightTextComponent.addTranslationEntry(StatePeriod.getSymmetryValue1(StatePeriod.EXIT_LTEXT_TRANS_PREIOD1));
		mRightTextComponent.addTranslationEntry(StatePeriod.getSymmetryValue1(StatePeriod.EXIT_LTEXT_TRANS_PREIOD2));

		mLeftBitmapComponent.addAlphaEntry(StatePeriod.EXIT_LBITMAP_ALPHA_PERIOD1);
		mLeftBitmapComponent.addAlphaEntry(StatePeriod.EXIT_LBITMAP_ALPHA_PERIOD2);
		mLeftBitmapComponent.addScaleEntry(StatePeriod.EXIT_LBITMAP_SCALE_PERIOD1);
		mLeftBitmapComponent.addTranslationEntry(StatePeriod.EXIT_LBITMAP_TRANS_PREIOD1);

		mRightBitmapComponent.addAlphaEntry(StatePeriod.EXIT_LBITMAP_ALPHA_PERIOD1);
		mRightBitmapComponent.addAlphaEntry(StatePeriod.EXIT_LBITMAP_ALPHA_PERIOD2);
		mRightBitmapComponent.addScaleEntry(StatePeriod.EXIT_LBITMAP_SCALE_PERIOD1);
		mRightBitmapComponent.addTranslationEntry(StatePeriod.getSymmetryValue1(StatePeriod.EXIT_LBITMAP_TRANS_PREIOD1));
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		float nowX = event.getX();

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mX = nowX;
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (mX == -1) {// down event has escaped
				mX = nowX;
			} else {
				if (nowX < mX) {
					mDirection = 0;
				} else if (nowX > mX) {
					mDirection = 1;
				}

				mPositionX += (nowX - mX) * 1.3f;
				mX = nowX;
			}
			break;
		}

		case MotionEvent.ACTION_UP: {
			Component[] components = new Component[] { mLeftTextComponent,
					mRightTextComponent, mLeftBitmapComponent,
					mRightBitmapComponent };

			if (mPositionX > mExitingLimit) {
				if (mDirection == 0) {// to left, recover
					mControlInterface.changeState(new AnimFromExitState(
							mControlInterface, mPositionX, 0, components));
				} else {// to right, exit
					mControlInterface.changeState(new AnimToExitState(
							mControlInterface, mPositionX, 1, components));
				}

			} else if (mPositionX > 0) {// only one choice, recover
				mControlInterface.changeState(new AnimFromExitState(
						mControlInterface, mPositionX, 0, components));

			} else if (mPositionX > -mExitingLimit) {// only one choice, recover
				mControlInterface.changeState(new AnimFromExitState(
						mControlInterface, mPositionX, 1, components));

			} else {
				if (mDirection == 0) {// to left, exit
					mControlInterface.changeState(new AnimToExitState(
							mControlInterface, mPositionX, 0, components));
				} else {// to right, recover
					mControlInterface.changeState(new AnimFromExitState(
							mControlInterface, mPositionX, 1, components));
				}
			}

			break;
		}
		}

		mControlInterface.refreshDraw();
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

}
