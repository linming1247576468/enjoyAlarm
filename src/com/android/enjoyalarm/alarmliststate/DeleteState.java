package com.android.enjoyalarm.alarmliststate;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.android.enjoyalarm.drawcomponent.AlarmItemComponent;
import com.android.enjoyalarm.drawcomponent.Component.XYEntity;
import com.android.enjoyalarm.view.ListViewControlInterface;


public class DeleteState extends State {

	private List<AlarmItemComponent> mItems;
	private AlarmItemComponent mDeleteItem;
	private int mDeleteIndex;
	private float mPositionY;
	private float mConstFactor;
	private float mViewHeight;
	private float mY;
	private int mDirection;//0.to top 1.to bottom
	private float mDeleteLimit;
	
	
	
	/**
	 * init from ListState, so items contain item at clickIndex
	 */
	public DeleteState(ListViewControlInterface controlInterface,
			List<AlarmItemComponent> items, int deleteIndex, float constFactor) {
		super(controlInterface);
		
		mItems = items;
		mDeleteItem = mItems.get(deleteIndex);
		mItems.remove(deleteIndex);
		mDeleteIndex = deleteIndex;
		mPositionY = 0f;
		mConstFactor = constFactor;
		init();
		
		XYEntity xy = mDeleteItem.getTranslation(constFactor);
		mDeleteItem.removeAllALphaEntry();
		mDeleteItem.removeAllScaleEntry();
		mDeleteItem.removeAllTransEntry();
		mDeleteItem.addAlphaEntry(-1f, -0.5f, 0f, 0f);
		mDeleteItem.addAlphaEntry(-0.5f, 0f, 0f, 1f);
		mDeleteItem.addAlphaEntry(0f, 0.5f, 1f, 0f);
		mDeleteItem.addAlphaEntry(0.5f, 1f, 0f, 0f);
		mDeleteItem.addScaleEntry(-1f, 1f, 1f, 1f);
		mDeleteItem.addTranslationEntry(-1f, 1f, xy.x, xy.x, -0.5f, 1.5f);
	}
	
	
	/**
	 * init from Anim*DeleteState,so items don't contain item at clickIndex
	 */
	public DeleteState(ListViewControlInterface controlInterface, AlarmItemComponent deleteItem, 
			List<AlarmItemComponent> items, int deleteIndex, float constFactor, float positionY) {
		super(controlInterface);
		
		mItems = items;
		mDeleteItem = deleteItem;
		mDeleteIndex = deleteIndex;
		mConstFactor = constFactor;
		mPositionY = positionY;
		init();
	}
	
	private void init() {
		mY = -1;
		mViewHeight = mControlInterface.getViewHeight();
		mDeleteLimit = mViewHeight * StatePeriod.DELETE_LIMIT;
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
				} else if (nowY > mY){
					mDirection = 1;
				}
				mPositionY += nowY - mY;
				mY = nowY;
			}
			break;
		}

		case MotionEvent.ACTION_UP: {
			if (mPositionY > mDeleteLimit) {
				if (mDirection == 0 || mDeleteIndex == 0 || mDeleteIndex == mItems.size()) {
					mControlInterface.changeState(new AnimFromDeleteState(
							mControlInterface, mDeleteItem, mItems, mDeleteIndex,
							mConstFactor, mPositionY, 0));
				} else {
					mControlInterface.changeState(new AnimToDeleteState(
							mControlInterface, mDeleteItem, mItems, mDeleteIndex,
							mConstFactor,  mPositionY, 1));
				}
			} else if (mPositionY > 0) {
				mControlInterface.changeState(new AnimFromDeleteState(
						mControlInterface, mDeleteItem, mItems, mDeleteIndex,
						mConstFactor, mPositionY, 0));
				
			} else if (mPositionY > -mDeleteLimit) {
				mControlInterface.changeState(new AnimFromDeleteState(
						mControlInterface, mDeleteItem, mItems, mDeleteIndex,
						mConstFactor, mPositionY, 1));
				
			} else {
				if (mDirection == 1 || mDeleteIndex == 0 || mDeleteIndex == mItems.size()) {
					mControlInterface.changeState(new AnimFromDeleteState(
							mControlInterface, mDeleteItem, mItems, mDeleteIndex,
							mConstFactor, mPositionY, 1));
				} else {
					mControlInterface.changeState(new AnimToDeleteState(
							mControlInterface, mDeleteItem, mItems, mDeleteIndex,
							mConstFactor, mPositionY, 0));
				}
			}
			mY = -1;
			break;
		}
		}

		mControlInterface.refreshDraw();
	}

	@Override
	public void handleDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		for (AlarmItemComponent item: mItems) {
			item.draw(canvas, mConstFactor);
		}
		
		float factor = mPositionY / mViewHeight;
		mDeleteItem.draw(canvas, factor);
	}

}
