package com.enjoyalarm.alarmliststate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.enjoyalarm.drawcomponent.BitmapComponent;
import com.enjoyalarm.drawcomponent.Component;
import com.enjoyalarm.view.ViewControlInterface;

public class ListingState extends State {

	private Component mTopBitmapComponent;
	private Component mBottomBitmapComponent;
	private Bitmap mBitmap;
	private float mY;
	private float mPositionY;
	private float mViewWidth;
	private float mViewHeight;
	private int mDirection;// move mDirection:0.to Top 1.to Bottom
	private float mListingLimit;// when mPositionY is within the limit, then it
								// should recover instead of list
	private float mPositionYLimit;

	/**
	 * @param initPositionY
	 *            must be relative
	 */
	public ListingState(ViewControlInterface controlInterface,
			Bitmap foregroundBitmap, float initPositionY) {
		super(controlInterface);

		mBitmap = foregroundBitmap;
		mY = -1;
		mPositionY = initPositionY;
		mViewWidth = controlInterface.getViewWidth();
		mViewHeight = controlInterface.getViewHeight();
		mListingLimit = mViewHeight * StatePeriod.LIST_LIMIT;
		mPositionYLimit = mViewHeight * StatePeriod.LIST_FACTOR2;

		initComponents();
	}

	/**
	 * 
	 * @param initPositionY		must be relative
	 * @param components	top,bottom
	 */
	public ListingState(ViewControlInterface controlInterface,
			Bitmap foregroundBitmap, float initPositionY, Component[] components) {
		super(controlInterface);

		mBitmap = foregroundBitmap;
		mY = -1;
		mPositionY = initPositionY;
		mViewWidth = controlInterface.getViewWidth();
		mViewHeight = controlInterface.getViewHeight();
		mListingLimit = mViewHeight * StatePeriod.LIST_LIMIT;
		mPositionYLimit = mViewHeight * StatePeriod.LIST_FACTOR2;

		mTopBitmapComponent = components[0];
		mBottomBitmapComponent = components[1];
	}
	
	private void initComponents() {
		mTopBitmapComponent = new BitmapComponent(mBitmap, mViewWidth,
				mViewHeight, mViewWidth, mViewHeight);
		mBottomBitmapComponent = new BitmapComponent(mBitmap, mViewWidth,
				mViewHeight, mViewWidth, mViewHeight);

		mTopBitmapComponent.addAlphaEntry(StatePeriod.LIST_TBITMAP_ALPHA_PERIOD1);
		mTopBitmapComponent.addAlphaEntry(StatePeriod.LIST_TBITMAP_ALPHA_PERIOD2);
		mTopBitmapComponent.addScaleEntry(StatePeriod.LIST_TBITMAP_SCALE_PERIOD1);
		mTopBitmapComponent.addScaleEntry(StatePeriod.LIST_TBITMAP_SCALE_PERIOD2);
		mTopBitmapComponent.addScaleEntry(StatePeriod.LIST_TBITMAP_SCALE_PERIOD3);
		mTopBitmapComponent.addTranslationEntry(StatePeriod.LIST_TBITMAP_TRANS_PERIOD1);
		mTopBitmapComponent.addTranslationEntry(StatePeriod.LIST_TBITMAP_TRANS_PERIOD2);
		mTopBitmapComponent.addTranslationEntry(StatePeriod.LIST_TBITMAP_TRANS_PERIOD3);
		mTopBitmapComponent.addTranslationEntry(StatePeriod.LIST_TBITMAP_TRANS_PERIOD4);
		mTopBitmapComponent.addTranslationEntry(StatePeriod.LIST_TBITMAP_TRANS_PERIOD5);

		mBottomBitmapComponent.addAlphaEntry(StatePeriod.LIST_TBITMAP_ALPHA_PERIOD1);
		mBottomBitmapComponent.addAlphaEntry(StatePeriod.LIST_TBITMAP_ALPHA_PERIOD2);
		mBottomBitmapComponent.addScaleEntry(StatePeriod.LIST_TBITMAP_SCALE_PERIOD1);
		mBottomBitmapComponent.addScaleEntry(StatePeriod.LIST_TBITMAP_SCALE_PERIOD2);
		mBottomBitmapComponent.addScaleEntry(StatePeriod.LIST_TBITMAP_SCALE_PERIOD3);
		mBottomBitmapComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_TBITMAP_TRANS_PERIOD1));
		mBottomBitmapComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_TBITMAP_TRANS_PERIOD2));
		mBottomBitmapComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_TBITMAP_TRANS_PERIOD3));
		mBottomBitmapComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_TBITMAP_TRANS_PERIOD4));
		mBottomBitmapComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_TBITMAP_TRANS_PERIOD5));
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		float nowY = event.getY();

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mY = nowY;
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (mY == -1) {// down event has escaped
				mY = nowY;
			} else {
				if (nowY < mY) {
					mDirection = 0;
				} else if (nowY > mY) {
					mDirection = 1;
				}

				mPositionY += nowY - mY;
				if (mPositionY > mPositionYLimit) {
					mPositionY = mPositionYLimit;
				} else if (mPositionY < -mPositionYLimit) {
					mPositionY = -mPositionYLimit;
				}
				mY = nowY;
			}
			break;
		}

		case MotionEvent.ACTION_UP: {
			Component[] components = new Component[] { mTopBitmapComponent,
					mBottomBitmapComponent };
			if (mPositionY > mListingLimit) {
				if (mDirection == 0) {// to Top, recover
					mControlInterface.changeState(new AnimFromListState(
							mControlInterface, mBitmap, mPositionY, 0,
							components));
				} else {// to Bottom, List
					mControlInterface.changeState(new AnimToListState(
							mControlInterface, mBitmap, mPositionY, 1,
							components));
				}

			} else if (mPositionY > 0) {// only one choice, recover
				mControlInterface.changeState(new AnimFromListState(
						mControlInterface, mBitmap, mPositionY, 0, components));

			} else if (mPositionY > -mListingLimit) {// only one choice, recover
				mControlInterface.changeState(new AnimFromListState(
						mControlInterface, mBitmap, mPositionY, 1, components));

			} else {
				if (mDirection == 0) {// to Top, List
					mControlInterface.changeState(new AnimToListState(
							mControlInterface, mBitmap, mPositionY, 0,
							components));
				} else {// to Bottom, recover
					mControlInterface.changeState(new AnimFromListState(
							mControlInterface, mBitmap, mPositionY, 1,
							components));
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

		Component bitmapComponent;
		if (mPositionY > 0) {
			bitmapComponent = mTopBitmapComponent;
		} else {
			bitmapComponent = mBottomBitmapComponent;
		}

		float factor = Math.abs(mPositionY) / mViewHeight;
		bitmapComponent.draw(canvas, factor);
	}

}
