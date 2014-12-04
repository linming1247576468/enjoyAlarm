package com.enjoyalarm.drawcomponent;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.format.Time;

import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;
import com.scut.enjoyalarm.R;


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
	private float mScale;
	private float mAlpha;
	private XYEntity mXyEntity;
	private Paint mPaint;
	

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
		mDrawName = true;
		mDrawDay = true;
		mDrawTime = true;

		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
		mXyEntity = new XYEntity(0.5f, 0.5f);
		mScale = 1f;
		mAlpha = 1f;
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
		mDrawName = true;
		mDrawDay = true;
		mDrawTime = true;

		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
		mXyEntity = new XYEntity(0.5f, 0.5f);
		mScale = 1f;
		mAlpha = 1f;
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
		// set mScale
		mScale = getScale(nowFactor, mScale);

		// set mAlpha
		mAlpha = getAlpha(nowFactor, mAlpha);

		// set translation
		XYEntity tempEntity = getTranslation(nowFactor);
		mXyEntity = tempEntity == null ? mXyEntity : tempEntity;
		
		System.out.println(mName + "  "+mScale+ "  "+ mAlpha + "  "+ mXyEntity.x + "  "+ mXyEntity.y);
		
		//draw item bg
		mPaint.setColor(mBgColor);
		mPaint.setAlpha((int) (255 * mAlpha));
		float gapX = mWidth * mScale * 0.5f;
		float gapY = mHeight * mScale * 0.5f;
		float x = mXyEntity.x * mViewWidth;
		float y = mXyEntity.y * mViewHeight;
		canvas.drawRect(x - gapX, y - gapY, x + gapX, y + gapY, mPaint);
		
		//draw name
		if (mDrawName) {
			mPaint.setColor(mTextColor);
			mPaint.setAlpha((int) (255 * mAlpha));
			mPaint.setTextSize(mBaseTextSize * 1.2f);
			canvas.drawText(mName, x, y - gapY * 1.2f, mPaint);
		}
		
		//draw day
		if (mDrawDay) {
			mPaint.setTextSize(mBaseTextSize);
			canvas.drawText(mShowDay, x, y - gapX * 0.5f, mPaint);
		}
		
		//draw time
		if (mDrawTime) {
			mPaint.setTextSize(mBaseTextSize * 1.1f);
			canvas.drawText(mShowTime, x, y, mPaint);
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
		char[] dayChars = days.toCharArray();
		Time time = new Time();
		time.setToNow();
		int nowDay = (time.weekDay + 6) % 7;//let Monday be first
		int nextDay = -1;
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
		}
		
		String showDay;
		if (nextDay == nowDay) {
			showDay = resources.getString(R.string.today);
			
		} else if (nextDay == nowDay + 1) {
			showDay = resources.getString(R.string.tomorrow);
			
		} else if (nextDay > nowDay) {
			showDay = resources.getStringArray(R.array.this_week)[nextDay];
			
		} else {
			showDay = resources.getStringArray(R.array.next_week)[nextDay];
		}
		
		return showDay;
	}
}
