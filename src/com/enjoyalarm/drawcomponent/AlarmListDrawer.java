package com.enjoyalarm.drawcomponent;

import java.util.ArrayList;
import java.util.List;

import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;

public class AlarmListDrawer {

	private List<AlarmItemComponent> mItems;
	private float mItemWidth;
	private float mItemHeight;
	private float mViewWidth;
	private float mViewHeight;
	private float mItemGapFactor;
	private float mNowFactor;
	private float mStartFactor;
	private float mEndFactor;
	
	

	public AlarmListDrawer(float itemGap, float viewWidth, float viewHeight) {
		mItemGapFactor = itemGap / viewWidth;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mNowFactor = 0f;
	}

	public void initItems(List<AlarmBasicInfo> basicInfos,
			List<Integer> bgColors, int textColor, float baseTextSize,
			float width, float height, Resources resources) {
		mItemWidth = width;
		mItemHeight = height;
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
		float gapFactor = mItemGapFactor + mItemWidth / mViewWidth;
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
		mNowFactor = index * (mItemGapFactor + mItemWidth / mViewWidth );
	}

	public void setIfDrawClickEffect(int index, boolean ifDraw) {
		if (index < 0) {
			return;
		}
		mItems.get(index).setDrawClickEffect(ifDraw);
	}
	
	/**
	 * return -1 if no item matches
	 */
	public int getClickItemIndex(float x, float y) {
		if (y < (mViewHeight - mItemHeight) / 2
				|| y > mViewHeight - (mViewHeight - mItemHeight) / 2) {
			return -1;
		}
		
		float gapFactor = mItemGapFactor + mItemWidth / mViewWidth;
		float xFactor = x / mViewWidth;
		float listFactor = mNowFactor + (xFactor - 0.5f);
		float extraFactor = listFactor % gapFactor;
		if (extraFactor < mItemWidth / mViewWidth / 2) {
			return (int)(listFactor / gapFactor);
			
		} else if (extraFactor < (mItemWidth / 2 / mViewWidth + mItemGapFactor)) {
			return -1;
			
		} else {
			return (int)(listFactor / gapFactor) + 1;
		}
		
		
	}
	
	public float getNowFactor() {
		return mNowFactor;
	}
	
	public List<AlarmItemComponent> getItems() {
		return mItems;
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
