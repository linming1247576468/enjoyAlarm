package com.android.enjoyalarm.drawcomponent;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.format.Time;

import com.android.enjoyalarm.R;
import com.android.enjoyalarm.model.ModelUtil.AlarmBasicInfo;


/**
 * 闹钟单项绘制器，初始位移，缩放度和透明度分别为(0.5f,0.5f), 1.0f, 1.0f 若某个时间点没有设置相应的变化曲线，则保持原先的值
 */
public class AlarmItemComponent extends Component {

	private int mId;
	private String mName;
	private String mShowDay;
	private String mShowTime;
	private int mBgColor;
	private int mTextColor;
	private float mBaseTextSize;
	private float mWidth;
	private float mHeight;
	private float mViewWidth;
	private float mViewHeight;
	private boolean mDrawName;
	private boolean mDrawDay;
	private boolean mDrawTime;
	private boolean mDrawClickEffect;
	private float mScale;
	private float mAlpha;
	private XYEntity mXyEntity;
	private Paint mPaint;
	private String mUnsaveWord;
	private String mHelpWord;
	private Resources mResources;
	
	
	
	private void dealWithUnAlarmItem() {
		if (mId == -1) {//temp alarm
			mUnsaveWord = mResources.getString(R.string.not_save);
			
		} else if (mId == -2) {//help
			mHelpWord = mResources.getString(R.string.help);
			setDrawDay(false);
			setDrawName(false);
			setDrawTime(false);
		}
	}

	public AlarmItemComponent(int id, String name, String showDay,
			String showTime, int backgroundColor, int textColor, float baseTextSize, float width,
			float height, float viewWidth, float viewHeight, Resources resources) {

		mId = id;
		mName = name;
		mShowDay = showDay;
		mShowTime = showTime;
		mBgColor = backgroundColor;
		mTextColor = textColor;
		mBaseTextSize = baseTextSize;
		mWidth = width;
		mHeight = height;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mResources = resources;
		mDrawName = true;
		mDrawDay = true;
		mDrawTime = true;

		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
		mXyEntity = new XYEntity(0.5f, 0.5f);
		mScale = 1f;
		mAlpha = 1f;
		
		dealWithUnAlarmItem();
	}

	public AlarmItemComponent(AlarmBasicInfo alarmBasicInfo,
			int backgroundColor, int textColor, float baseTextSize,
			float width, float height, float viewWidth, float viewHeight, Resources resources) {

		mId = alarmBasicInfo.id;
		mName = alarmBasicInfo.name;
		mShowDay = getShowDay(alarmBasicInfo.hour, alarmBasicInfo.minute,
				alarmBasicInfo.days, resources);
		mShowTime = getShowTime(alarmBasicInfo.hour, alarmBasicInfo.minute);
		mBgColor = backgroundColor;
		mTextColor = textColor;
		mBaseTextSize = baseTextSize;
		mWidth = width;
		mHeight = height;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mResources = resources;
		mDrawName = true;
		mDrawDay = true;
		mDrawTime = true;

		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
		mXyEntity = new XYEntity(0.5f, 0.5f);
		mScale = 1f;
		mAlpha = 1f;
		
		dealWithUnAlarmItem();
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
	
	public void setDrawClickEffect(boolean drawClickEffect) {
		mDrawClickEffect = drawClickEffect;
	}

	@Override
	public void draw(Canvas canvas, float nowFactor) {
		// set mScale
		mScale = getScale(nowFactor, mScale);

		// set mAlpha
		mAlpha = getAlpha(nowFactor, mAlpha);

		// set translation
		XYEntity tempEntity = getTranslation(nowFactor);
		mXyEntity = tempEntity == null ? mXyEntity : tempEntity;
		
		
		//draw item bg
		mPaint.setColor(mBgColor);//the color has alpha data
		mPaint.setAlpha((int) (255 * mAlpha));
		float gapX = mWidth * mScale * 0.5f;
		float gapY = mHeight * mScale * 0.5f;
		float x = mXyEntity.x * mViewWidth;
		float y = mXyEntity.y * mViewHeight;
		canvas.drawRect(x - gapX, y - gapY, x + gapX, y + gapY, mPaint);
		
		//draw click effect
		if (mDrawClickEffect) {
			mPaint.setColor(Color.BLACK);
			mPaint.setAlpha(60);
			canvas.drawRect(x - gapX, y - gapY, x + gapX, y + gapY, mPaint);
		}
		
		//draw name
		if (mDrawName) {
			mPaint.setColor(mTextColor);//the color has alpha data
			mPaint.setAlpha((int) (255 * mAlpha));
			mPaint.setTextSize(mBaseTextSize * mScale);
			canvas.drawText(mName, x, y - gapY * 1.2f, mPaint);
		}
		
		//draw day
		if (mDrawDay) {
			mPaint.setColor(mTextColor);//the color has alpha data
			mPaint.setAlpha((int) (255 * mAlpha));
			mPaint.setTextSize(mBaseTextSize * 0.8f * mScale);
			canvas.drawText(mShowDay, x, y - mBaseTextSize * mScale, mPaint);
		}
		
		//draw time
		if (mDrawTime) {
			mPaint.setColor(mTextColor);//the color has alpha data
			mPaint.setAlpha((int) (255 * mAlpha));
			mPaint.setTextSize(mBaseTextSize * 1.5f * mScale);
			canvas.drawText(mShowTime, x, y + mBaseTextSize * 0.8f * mScale, mPaint);
		}
		
		if (mId == -1) {//draw unsave word
			mPaint.setColor(Color.WHITE);
			mPaint.setAlpha((int) (255 * mAlpha * 0.7f));
			mPaint.setTextSize(mBaseTextSize * 0.9f * mScale);
			canvas.drawText(mUnsaveWord, x, y - mBaseTextSize * mScale * 3, mPaint);
			
		} else if (mId == -2) {//draw help word
			mPaint.setColor(mTextColor);
			mPaint.setAlpha((int) (255 * mAlpha));
			mPaint.setTextSize(mBaseTextSize * mScale);
			canvas.drawText(mHelpWord, x, y, mPaint);
		}
		
	}
	
	public int getId() {
		return mId;
	}

	
	
	private String getShowTime(int hour, int minute) {
		String hourString = String.valueOf(hour);
		hourString = hourString.length() == 1 ? "0" + hourString : hourString;
		String minuteString = String.valueOf(minute);
		minuteString = minuteString.length() == 1 ? "0" + minuteString : minuteString;
		return hourString + ":" + minuteString;
	}
	
	private String getShowDay(int hour, int minute, String days, Resources resources) {
		if (days == null) {
			return null;
		}
		char[] dayChars = days.toCharArray();
		Time time = new Time();
		time.setToNow();
		int nowDay = (time.weekDay + 6) % 7;//let Monday be first
		int nextDay = -1;
		boolean nextWeek = false;
		for (char c : dayChars) {
			int day = c - 48;
			if (((day == nowDay) && ((hour > time.hour) || (hour == time.hour && minute >= time.minute)))
					|| (day > nowDay)) {
				nextDay = day;
				break;
			}
		}
		if (nextDay == -1) {// the closely next day is in next week
			nextDay = dayChars[0] - 48;
			nextWeek = true;
		}
		
		String showDay;
		if (nextWeek) {
			showDay = resources.getStringArray(R.array.next_week)[nextDay];
			
		} else if (nextDay == nowDay) {
			showDay = resources.getString(R.string.today);
			
		} else if (nextDay == nowDay + 1) {
			showDay = resources.getString(R.string.tomorrow);
			
		} else {
			showDay = resources.getStringArray(R.array.this_week)[nextDay];
		}
		
		return showDay;
	}
}
