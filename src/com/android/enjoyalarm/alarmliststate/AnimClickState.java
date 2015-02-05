package com.android.enjoyalarm.alarmliststate;

import java.util.List;

import com.android.enjoyalarm.drawcomponent.AlarmItemComponent;
import com.android.enjoyalarm.drawcomponent.Component.XYEntity;
import com.android.enjoyalarm.view.ViewControlInterface;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class AnimClickState extends State {

	private List<AlarmItemComponent> mItems;
	private AlarmItemComponent mClickItem;
	private int mClickIndex;
	private float mNowFactor;
	private float mConstFactor;
	
	
	public AnimClickState(ViewControlInterface controlInterface,
			List<AlarmItemComponent> items, int clickIndex, float constFactor) {
		super(controlInterface);
		
		mItems = items;
		mClickIndex = clickIndex;
		mConstFactor = constFactor;
		mClickItem = mItems.get(clickIndex);
		mItems.remove(clickIndex);
		
		mClickItem.removeAllALphaEntry();
		mClickItem.removeAllScaleEntry();
		mClickItem.addAlphaEntry(StatePeriod.CLICK_ITEM_ALPHA_PERIOD1);
		mClickItem.addScaleEntry(StatePeriod.CLICK_ITEM_SCALE_PERIOD1);
		XYEntity xy = mClickItem.getTranslation(constFactor);
		mClickItem.removeAllTransEntry();
		mClickItem.addTranslationEntry(0f, 1f, xy.x, xy.y, 0.5f, 0.5f);
		
		new AnimThread().start();
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		//do nothing
	}

	@Override
	public void handleDraw(Canvas canvas) {
		for (AlarmItemComponent item: mItems) {
			item.draw(canvas, mConstFactor);
		}
		
		mClickItem.draw(canvas, mNowFactor);
	}

	
	private void changeToSettingFromClick() {
		mControlInterface.handleClickAlarmItem(mClickIndex);
	}
	
	
	private class AnimThread extends Thread {
		@Override
		public void run() {
			float velocity = 0.04f;
			float gap = 0.004f;
			while (mNowFactor < 1f) {
				mNowFactor += velocity;
				velocity += gap;//accelerate
				mControlInterface.refreshDraw();
				
				if (mNowFactor > 1f) {
					changeToSettingFromClick();
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
