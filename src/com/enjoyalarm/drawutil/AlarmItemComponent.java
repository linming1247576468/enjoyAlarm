package com.enjoyalarm.drawutil;

import android.graphics.Canvas;

public class AlarmItemComponent extends Component {

	private int mId;
	private String mName;
	private String mShowDay;
	private String mShowTime;
	private int mColor;
	private float mWidth;
	private float mHeight;
	private float mViewWidth;
	private float mViewHeight;
	private boolean mDrawName;
	private boolean mDrawDay;
	private boolean mDrawTime;

	public AlarmItemComponent(int id, String name, String showDay,
			String showTime, int backgroundColor, float width, float height, float viewWidth, float viewHeight) {
		
		mId = id;
		mName = name;
		mShowDay = showDay;
		mShowTime = showTime;
		mColor = backgroundColor;
		mWidth = width;
		mHeight = height;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mDrawName = true;
		mDrawDay = true;
		mDrawTime = true;
	}

	public void setDrawName(boolean drawName) {
		mDrawName = drawName;
	}

	public void setDrawDay(boolean drawDay) {
		mDrawDay = drawDay;
	}

	public void setDrawTime(boolean drawTime) {
		mDrawTime = drawTime;
	}

	@Override
	public void draw(Canvas canvas, float nowFactor) {

	}

}
