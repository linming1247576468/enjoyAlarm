package com.enjoyalarm.alarmliststate;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.enjoyalarm.drawcomponent.AlarmItemComponent;
import com.enjoyalarm.drawcomponent.Component;
import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;
import com.enjoyalarm.view.ViewControlInterface;

public class AnimToListState extends State {

	private Component mTopBitmapComponent;
	private Component mBottomBitmapComponent;
	private Component mLeftItemComponent;
	private Component mCenterTopItemComponent;
	private Component mCenterBottomItemComponent;
	private Component mRightItemComponent;
	private Bitmap mBitmap;
	private float mPositionY;//赋值时必须映射为相对坐标
	private float mViewWidth;
	private float mViewHeight;
	private int mDirection;// move mDirection:0.to Top 1.to Bottom
	private boolean mThreadFlag;

	
	/**
	 * init from ListingState only
	 * components:topBitmap,bottomBitmap
	 */
	public AnimToListState(ViewControlInterface controlInterface,
			Bitmap foregroundBitmap, float initPositionY, int initDirection, Component[] components) {
		super(controlInterface);
		
		controlInterface.handleScrollToListStarted();

		mBitmap = foregroundBitmap;
		mPositionY = initPositionY;
		mDirection = initDirection;
		mViewWidth = controlInterface.getViewWidth();
		mViewHeight = controlInterface.getViewHeight();

		mTopBitmapComponent = components[0];
		mBottomBitmapComponent = components[1];
		initMoreComponents();

		mThreadFlag = true;
		new AnimThread().start();
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			if (Math.abs(mPositionY) / mViewHeight < StatePeriod.LIST_FACTOR3) {
				mThreadFlag = false;
				mControlInterface.changeState(new ListingState(
						mControlInterface, mBitmap, mPositionY,
						new Component[] { mTopBitmapComponent,
								mBottomBitmapComponent }));
			}
		}

	}

	@Override
	public void handleDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		float factor = Math.abs(mPositionY) / mViewHeight;
		
		//first
		if (mLeftItemComponent != null) {
			mLeftItemComponent.draw(canvas, factor);
		}
		mRightItemComponent.draw(canvas, factor);
		
		Component bitmapComponent;
		Component centerItemComponent;
		if (mPositionY > 0) {
			bitmapComponent = mTopBitmapComponent;
			centerItemComponent = mCenterTopItemComponent;
		} else {
			bitmapComponent = mBottomBitmapComponent;
			centerItemComponent = mCenterBottomItemComponent;
		}
		//second
		bitmapComponent.draw(canvas, factor);
		//third
		centerItemComponent.draw(canvas, factor);
	}

	
	
	private void initMoreComponents() {
		Resources resources = mControlInterface.getViewContext().getResources();
		float density = mControlInterface.getDensity();
		List<AlarmBasicInfo> alarmsInfo = mControlInterface.getAlarmsInfo();
		List<Integer> alarmsColor = mControlInterface.getAlarmsColor();
		for (AlarmBasicInfo info: alarmsInfo) {
			System.out.println(info);
		}
		int currentIndex = mControlInterface.getCurrentAlarmIndex();
		System.out.println(currentIndex);
		float itemScale = StatePeriod.LIST_ITEM_SCALE;
		if (currentIndex > 0) {
			mLeftItemComponent = new AlarmItemComponent(
					alarmsInfo.get(currentIndex - 1),
					alarmsColor.get(currentIndex - 1), Color.WHITE,
					30 * density, mViewWidth * itemScale, mViewHeight * itemScale, 
					mViewWidth, mViewHeight, resources);

			mLeftItemComponent.addAlphaEntry(StatePeriod.LIST_LITEM_ALPHA_PERIOD1);
			mLeftItemComponent.addScaleEntry(StatePeriod.LIST_LITEM_SCALE_PERIOD1);
			mLeftItemComponent.addTranslationEntry(StatePeriod.LIST_LITEM_TRANS_PERIOD1);
			mLeftItemComponent.addTranslationEntry(StatePeriod.LIST_LITEM_TRANS_PERIOD2);
			mLeftItemComponent.addTranslationEntry(StatePeriod.LIST_LITEM_TRANS_PERIOD3);
		}
		mCenterTopItemComponent = new AlarmItemComponent(
				alarmsInfo.get(currentIndex), alarmsColor.get(currentIndex),
				Color.WHITE, 30 * density, mViewWidth * itemScale, mViewHeight
						* itemScale, mViewWidth, mViewHeight, resources);
		mCenterBottomItemComponent = new AlarmItemComponent(
				alarmsInfo.get(currentIndex), alarmsColor.get(currentIndex),
				Color.WHITE, 30 * density, mViewWidth * itemScale, mViewHeight
						* itemScale, mViewWidth, mViewHeight, resources);
		mRightItemComponent = new AlarmItemComponent(
				alarmsInfo.get(currentIndex + 1),
				alarmsColor.get(currentIndex + 1), Color.WHITE, 30 * density,
				mViewWidth * itemScale, mViewHeight * itemScale, mViewWidth,
				mViewHeight, resources);
		
		mRightItemComponent.addAlphaEntry(StatePeriod.LIST_LITEM_ALPHA_PERIOD1);
		mRightItemComponent.addScaleEntry(StatePeriod.LIST_LITEM_SCALE_PERIOD1);
		mRightItemComponent.addTranslationEntry(StatePeriod.getSymmetryValue1(StatePeriod.LIST_LITEM_TRANS_PERIOD1));
		mRightItemComponent.addTranslationEntry(StatePeriod.getSymmetryValue1(StatePeriod.LIST_LITEM_TRANS_PERIOD2));
		mRightItemComponent.addTranslationEntry(StatePeriod.getSymmetryValue1(StatePeriod.LIST_LITEM_TRANS_PERIOD3));
		
		mCenterTopItemComponent.addAlphaEntry(StatePeriod.LIST_CTITEM_ALPHA_PERIOD1);
		mCenterTopItemComponent.addAlphaEntry(StatePeriod.LIST_CTITEM_ALPHA_PERIOD2);
		mCenterTopItemComponent.addAlphaEntry(StatePeriod.LIST_CTITEM_ALPHA_PERIOD3);
		mCenterTopItemComponent.addScaleEntry(StatePeriod.LIST_CTITEM_SCALE_PERIOD1);
		mCenterTopItemComponent.addTranslationEntry(StatePeriod.LIST_CTITEM_TRANS_PERIOD1);
		mCenterTopItemComponent.addTranslationEntry(StatePeriod.LIST_CTITEM_TRANS_PERIOD2);
		mCenterTopItemComponent.addTranslationEntry(StatePeriod.LIST_CTITEM_TRANS_PERIOD3);
		
		mCenterBottomItemComponent.addAlphaEntry(StatePeriod.LIST_CTITEM_ALPHA_PERIOD1);
		mCenterBottomItemComponent.addAlphaEntry(StatePeriod.LIST_CTITEM_ALPHA_PERIOD2);
		mCenterBottomItemComponent.addAlphaEntry(StatePeriod.LIST_CTITEM_ALPHA_PERIOD3);
		mCenterBottomItemComponent.addScaleEntry(StatePeriod.LIST_CTITEM_SCALE_PERIOD1);
		mCenterBottomItemComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_CTITEM_TRANS_PERIOD1));
		mCenterBottomItemComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_CTITEM_TRANS_PERIOD2));
		mCenterBottomItemComponent.addTranslationEntry(StatePeriod.getSymmetryValue2(StatePeriod.LIST_CTITEM_TRANS_PERIOD3));
	}
	
	private void changeToListStateToHandle() {
		mControlInterface.changeState(new ListState(mControlInterface, -1));
	}

	private class AnimThread extends Thread {
		@Override
		public void run() {
			float velocity = Math.abs(mPositionY) / 64;
			float acceGap = mViewHeight * 0.002f;
			float limit = mViewHeight * StatePeriod.LIST_FACTOR4;
			float accelerateLimit = mViewHeight * StatePeriod.LIST_FACTOR2;
			boolean accelerate = true;
			
			while (mThreadFlag) {
				if (mDirection == 0) {// to top
					mPositionY -= velocity;
				} else {// to bottom
					mPositionY += velocity;
				}

				if (accelerate) {
					if (Math.abs(mPositionY) < accelerateLimit) {
						velocity += acceGap; // accelerate
					} else {
						accelerate = false;
						velocity = Math.abs(mPositionY) / 32;
					}
				}
				
				mControlInterface.refreshDraw();

				if (Math.abs(mPositionY) > limit) {
					changeToListStateToHandle();
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
