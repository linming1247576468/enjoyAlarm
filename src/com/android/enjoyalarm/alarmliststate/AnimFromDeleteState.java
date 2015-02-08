package com.android.enjoyalarm.alarmliststate;

import java.util.List;

import com.android.enjoyalarm.drawcomponent.AlarmItemComponent;
import com.android.enjoyalarm.view.ListViewControlInterface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;


public class AnimFromDeleteState extends State {

	private List<AlarmItemComponent> mItems;
	private AlarmItemComponent mDeleteItem;
	private int mDeleteIndex;
	private float mPositionY;
	private float mConstFactor;
	private float mViewHeight;
	private int mDirection;//0.to top 1.to bottom
	private boolean mThreadFlag;
	
	
	
	public AnimFromDeleteState(ListViewControlInterface controlInterface,
			AlarmItemComponent deleteItem, List<AlarmItemComponent> otherItems,
			int deleteIndex, float constFactor, float positionY, int direction) {
		super(controlInterface);

		mItems = otherItems;
		mDeleteItem = deleteItem;
		mDeleteIndex = deleteIndex;
		mConstFactor = constFactor;
		mViewHeight = mControlInterface.getViewHeight();
		mPositionY = positionY;
		mDirection = direction;
		
		mThreadFlag = true;
		new AnimThread().start();
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mThreadFlag = false;
			mControlInterface
					.changeState(new DeleteState(mControlInterface,
							mDeleteItem, mItems, mDeleteIndex, mConstFactor,
							mPositionY));
		}
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

	
	private void changeToListState() {
		mControlInterface.changeState(new ListState(mControlInterface, mConstFactor));
	}
	
	
	
	private class AnimThread extends Thread {
		@Override
		public void run() {
			float velocity = Math.abs(mPositionY) / 32;
			float gap = velocity * 0.3f;
			float limit = mViewHeight * 0.005f;

			if (mDirection == 0) {// to top
				while (mThreadFlag) {
					mPositionY -= velocity;
					velocity += gap; // accelerate
					mControlInterface.refreshDraw();

					if (mPositionY < limit) {
						changeToListState();
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
					velocity += gap; // accelerate
					mControlInterface.refreshDraw();

					if (mPositionY > -limit) {
						changeToListState();
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
