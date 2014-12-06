package com.enjoyalarm.drawcomponent;

import java.util.ArrayList;
import java.util.List;

import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;

public class AlarmListDrawer {

	private List<AlarmItemComponent> mItems;
	private float mItemGap;
	private float mItemWidth;
	private float mViewWidth;
	private float mViewHeight;
	private float mNowFactor;
	private float mStartFactor;
	private float mEndFactor;
	
	

	public AlarmListDrawer(float itemGap, float viewWidth, float viewHeight) {
		mItemGap = itemGap;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mNowFactor = 0f;
	}

	public void initItems(List<AlarmBasicInfo> basicInfos,
			List<Integer> bgColors, int textColor, float baseTextSize,
			float width, float height, Resources resources) {
		mItemWidth = width;
		mItems = new ArrayList<AlarmItemComponent>();
		for (int i = 0; i < basicInfos.size(); i++) {
			mItems.add(new AlarmItemComponent(basicInfos.get(i), bgColors
					.get(i), textColor, baseTextSize, width, height,
					mViewWidth, mViewHeight, resources));
		}

		setEntryForItems();
	}

	private void setEntryForItems() {
		int size = mItems.size();
		float gapFactor = (mItemGap + mItemWidth) / mViewWidth;
		mStartFactor = 0f;// at list start
		mEndFactor = (size - 1) * gapFactor;// at list end
		for (int i = 0; i < size; i++) {
			AlarmItemComponent item = mItems.get(i);
			item.addTranslationEntry(mStartFactor, mEndFactor, 0.5f + i
					* gapFactor, 0.5f + i * gapFactor
					- (mEndFactor - mStartFactor), 0.5f, 0.5f);
			item.addTranslationEntry(mEndFactor, mEndFactor + 1f, 0.5f + i
					* gapFactor - (mEndFactor - mStartFactor), 0.5f + i
					* gapFactor - (mEndFactor - mStartFactor), 0.5f, 0.5f);
		}
	}

	public void setCurrentIndex(int index) {
		mNowFactor = index * (mItemGap + mItemWidth) / mViewWidth;
	}

	
	/**
	 * @param changeFactor	positive in the x positive direction
	 */
	public void draw(Canvas canvas, float changeFactor) {
		canvas.drawColor(Color.BLACK);
		mNowFactor -= changeFactor;
		if (mNowFactor < mStartFactor) {
			mNowFactor = mStartFactor;
		} else if (mNowFactor > mEndFactor) {
			mNowFactor = mEndFactor;
		}
		for (AlarmItemComponent component: mItems) {
			component.draw(canvas, mNowFactor);
		}
	}
}
