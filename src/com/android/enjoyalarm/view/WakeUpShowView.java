package com.android.enjoyalarm.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import com.android.enjoyalarm.R;
import com.android.enjoyalarm.model.ModelUtil;
import com.android.enjoyalarm.model.ReadingModel;

public class WakeUpShowView extends View {

	private OnDragFinishedListener mOnDragFinishedListener;
	private List<String> mName;
	private String mTime;
	private String mDay;
	private List<String> mWord;
	private String mMusicName;
	private String mDragWord;
	private int mWidth;
	private int mHeight;
	private float mDensity;
	private Paint mPaint = new Paint();
	private boolean mIfRefresh = true;
	private StringBuilder mMusicLight = new StringBuilder();
	private Random mRandom = new Random();
	private int mSlowCounterMusic;
	private int mSlowCounterFinger;
	private boolean mDrawFingerEffect;
	private float mRotationDegree;
	private float mPointX;
	private float mPointY;
	private float mMovePointX;
	private float mMovePointY;
	private float mOldX;
	private float mOldY; 
	private float mUpX;
	private float mUpY;
	private boolean mDragSuccess;
	private boolean mDragFail;
	private float mDragSuccessGapX;
	private float mDragSuccessGapY;
	private List<Integer> mFingerEffectXs = new ArrayList<Integer>();
	private List<Integer> mFingerEffectYs = new ArrayList<Integer>();
	private float mNameSize;
	private float mWordSize;
	
	private List<String> splitStringIntoArray(String s, float unitSize) {
		if (s == null) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		int numOneLine = (int)mWidth / (int)unitSize - 2;
		StringBuilder builder = new StringBuilder();
		int index = 0;
		while (index < s.length()) {
			builder.delete(0, builder.length());
			for (int i=index; i<index+numOneLine&&i<s.length(); i++) {
				builder.append(s.charAt(i));
			}
			result.add(builder.toString());
			index += numOneLine;
		}
		return result;
	}
	
	public WakeUpShowView(Context context, int viewWidth, int viewHeight, ReadingModel data) {
		super(context);
		
		mWidth = viewWidth;
		mHeight = viewHeight;
		mDensity = context.getResources().getDisplayMetrics().density;
		mDragWord = context.getResources().getString(R.string.wake_up_drag_word);
		mNameSize = 45 * mDensity;
		mWordSize = 30 * mDensity;
		
		mName = splitStringIntoArray(data.getName(),mNameSize);
		int hour = ModelUtil.getHourFromTime(data.getTime());
		int minute = ModelUtil.getMinuteFromTime(data.getTime());
		mTime = ViewUtil.getDoubleBitStringForTime(hour) + ":" + 
				ViewUtil.getDoubleBitStringForTime(minute);
		mWord = splitStringIntoArray(data.getText(),mWordSize);
		Time time = new Time();
		time.setToNow();
		mDay = context.getResources().getStringArray(R.array.wake_show_day)[time.weekDay];
		
		
		mRotationDegree = 0;
		mPointX = mWidth/2;
		mPointY = mHeight/2;
		mMovePointX = mPointX;
		mMovePointY = mPointY;
		
		new RefreshThread().start();
	}
	
	/**
	 * Since ReadingModel doesn't contain this data and music name may not match model's uri, 
	 * so you must call this method
	 */
	public void setMusicName(String name) {
		mMusicName = name;
		if (mMusicName != null && mMusicName.length() > 20) {
			mMusicName = mMusicName.substring(0, 20) + "..";
		}
	}
	
	public void setTime(int hour, int minute) {
		mTime = ViewUtil.getDoubleBitStringForTime(hour) + ":" + 
				ViewUtil.getDoubleBitStringForTime(minute);
	}

	public void stop() {
		mIfRefresh = false;
	}
	
	public void setOnDragFinishedListener(OnDragFinishedListener listener) {
		mOnDragFinishedListener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float nowX = event.getX();
		float nowY = event.getY();
		
		switch(event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mOldX = nowX;
			mOldY = nowY;
			mDragSuccess = false;
			mDragFail = false;
			mDrawFingerEffect = true;
			break;
		}
		
		case MotionEvent.ACTION_MOVE: {
			calculateDegree(nowX, nowY);
			calculateTranslation(nowX, nowY);
			mUpX = mOldX;//save mOldX temporarily
			mUpY = mOldY;//save mOldY temporarily
			mOldX = nowX;
			mOldY = nowY;
			break;
		}
		
		case MotionEvent.ACTION_UP: {
			mOldX = mUpX;
			mOldY = mUpY;
			mUpX = nowX;
			mUpY = nowY;
			judgeDrag();
			mDrawFingerEffect = false;
			break;
		}
		}
		return true;
	}
	
	
	
	private void calculateDegree(float newX, float newY) {
		float f;
		float distance;
		float degreeX, degreeY;
		
		// calculate in x direction
		f = Math.abs(newX - mOldX);
		distance = Math.abs(mOldY - mMovePointY);
		degreeX = f * distance * 0.1f / mHeight;

		// calculate in y direction
		f = Math.abs(newY - mOldY);
		distance = Math.abs(mOldX - mMovePointX);
		degreeY = f * distance * 0.1f / mHeight;

		// combine
		if ((mOldY < mMovePointY && newX < mOldX)
				|| (mOldY > mMovePointY && newX > mOldX)) {
			degreeX = -degreeX;
		}
		if ((mOldX < mMovePointX && newY > mOldY)
				|| (mOldX > mMovePointX && newY < mOldY)) {
			degreeY = -degreeY;
		}
		
		mRotationDegree += (degreeX + degreeY);
	}
	
	private void calculateTranslation(float newX, float newY) {
		mMovePointX += (newX - mOldX)* 0.5f;
		mMovePointY += (newY - mOldY)* 0.6f;
	}
	
	private void judgeDrag() {
		double pointDistance = Math.sqrt(Math.pow(mMovePointX - mPointX, 2)
				+ Math.pow(mMovePointY - mPointY, 2));
		if (pointDistance > mWidth/5) {
			if ((mMovePointY < mPointY && mUpY < mOldY)
					|| (mMovePointY > mPointY && mUpY > mOldY)
					|| (mMovePointX < mPointX && mUpX < mOldX)
					|| (mMovePointX > mPointX && mUpX > mOldX)) {
				mDragSuccess = true;
				mDragFail = false;
				mDragSuccessGapX = mUpX - mOldX;
				mDragSuccessGapY = mUpY - mOldY;
				if (mDragSuccessGapX == 0) {
					mDragSuccessGapX = 10 * mDensity;
				}
				if (mDragSuccessGapY == 0) {
					mDragSuccessGapY = 12 * mDensity;
				}
				while (Math.abs(mDragSuccessGapX) < 10 * mDensity) {
					mDragSuccessGapX *= 2;
				}
				while (Math.abs(mDragSuccessGapY) < 10 * mDensity) {
					mDragSuccessGapY *= 2;
				}
				
			} else {
				mDragSuccess = false;
				mDragFail = true;
			}
			
		} else {
			mDragSuccess = false;
			mDragFail = true;
		}
	}
	
	private void handleDragSuccess(){
		mDragSuccessGapX *= 1.1f;
		mDragSuccessGapY *= 1.1f;
		mMovePointX += mDragSuccessGapX;
		mMovePointY += mDragSuccessGapY;
		if (mRotationDegree > 0) {
			mRotationDegree += 2;
		} else {
			mRotationDegree -= 2;
		}
		
		if (Math.abs(mMovePointX - mPointX) > mHeight * 0.8f
				|| Math.abs(mMovePointY - mPointY) > mHeight * 0.8f) {
			mOnDragFinishedListener.onDragFinished(this);
			stop();
		}
	}
	
	private void handleDragFail() {
		mMovePointX = (mPointX - mMovePointX) * 0.2f + mMovePointX;
		mMovePointY = (mPointY - mMovePointY) * 0.2f + mMovePointY;
		mRotationDegree *= 0.8f;

		if ((Math.abs(mMovePointX - mPointX) < 1 * mDensity && Math
				.abs(mMovePointY - mPointY) < 1 * mDensity)
				|| (Math.abs(mRotationDegree) < 0.1)) {
			mMovePointX = mPointX;
			mMovePointY = mPointY;
			mRotationDegree = 0;
			mDragFail = false;
		}
	}
	
	private void drawMusic(Canvas canvas) {
		mPaint.setColor(Color.BLUE);
		mPaint.setTextSize(20 * mDensity);
		mPaint.setTextAlign(Align.LEFT);
		canvas.drawText(mMusicName ,20 * mDensity, mHeight - 25 * mDensity, mPaint);
		
		mPaint.setTextSize(40 * mDensity);
		canvas.drawText(mMusicLight.toString(),20 * mDensity, mHeight - 10 * mDensity, mPaint);
		mSlowCounterMusic++;
		if (mSlowCounterMusic > 20) {
			if (mMusicLight.length() < mMusicName.length() && mMusicLight.length() < 5) {
				mMusicLight.append('.');
				
			} else {
				mMusicLight.delete(0, mMusicLight.length());
			}
			
			mSlowCounterMusic = 0;
		}
	}

	private void drawFingerEffect(Canvas canvas) {
		mSlowCounterFinger++;
		if (mSlowCounterFinger > 5) {
			mFingerEffectXs.clear();
			mFingerEffectYs.clear();
			
			int min = (int)(5 * mDensity);
			int max = (int)(40 * mDensity);
			int dx = 0; 
			int dy = 0;
			int op = 0;//0.+x+y  1.-x+y  2.-x-y  3.+x-y
			for (int i=0; i<4; i++) {
				op = 0;
				for (int j=0; j<4; j++,op++) {
					while (dx<min) {
						dx = mRandom.nextInt(max);
					}
					while (dy<min) {
						dy = mRandom.nextInt(max);
					}
					switch(op) {
					case 0: {
						mFingerEffectXs.add(dx);
						mFingerEffectYs.add(dy);
						break;
					}
					case 1: {
						mFingerEffectXs.add(-dx);
						mFingerEffectYs.add(dy);
						break;
					}
					case 2: {
						mFingerEffectXs.add(-dx);
						mFingerEffectYs.add(-dy);
						break;
					}
					case 3: {
						mFingerEffectXs.add(dx);
						mFingerEffectYs.add(-dy);
						break;
					}
					}
				
					dx = 0;
					dy = 0;
				}
			}
			
			mSlowCounterFinger = 0;
		}
		
		mPaint.setColor(Color.parseColor("#8ad5f0"));
		for (int i=0; i<mFingerEffectXs.size(); i++) {
			canvas.drawCircle(mOldX+mFingerEffectXs.get(i), mOldY+mFingerEffectYs.get(i), 2*mDensity, mPaint);
		}
	}
	
	private int getNowAlpha() {
		double pointDistance = Math.sqrt(Math.pow(mMovePointX - mPointX, 2)
				+ Math.pow(mMovePointY - mPointY, 2));
		pointDistance = pointDistance * 1.2f > mHeight? mHeight : pointDistance * 1.2f;
		return (int)((1 - pointDistance / mHeight) * 250);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.save();//used to cancel the transform behind
		
		canvas.drawColor(Color.BLACK);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(Color.LTGRAY);
		mPaint.setTextSize(30 * mDensity);
		mPaint.setAlpha(getNowAlpha());
		if (mMovePointY < mPointY) {
			canvas.drawText(mDragWord, mWidth /2, mHeight - 20 * mDensity, mPaint);
		} else {
			canvas.drawText(mDragWord, mWidth /2, 40 * mDensity, mPaint);
		}
		mPaint.setAlpha(255);
		
		
		if (mDragSuccess) {
			handleDragSuccess();
		} else if (mDragFail) {
			handleDragFail();
		}
		
		//the transform order is reverse
		canvas.translate(mMovePointX-mPointX, mMovePointY-mPointY);
		canvas.rotate(mRotationDegree, mPointX, mPointY);
		
		
		mPaint.setColor(Color.YELLOW);
		canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
		
		mPaint.setColor(Color.parseColor("#d22ff5"));
		mPaint.setTextSize(mNameSize);
		for (int i=0; i<mName.size(); i++) {
			canvas.drawText(mName.get(i), mWidth/2, 60 * mDensity + i*mNameSize, mPaint);
		}
		
		mPaint.setColor(Color.GRAY);
		mPaint.setTextSize(90 * mDensity);
		canvas.drawText(mTime, mWidth/2, mHeight/2 - 30 * mDensity, mPaint);
		
		mPaint.setColor(Color.LTGRAY);
		mPaint.setTextSize(35 * mDensity);
		canvas.drawText(mDay, mWidth/2, mHeight/2 + 35 * mDensity, mPaint);
		
		
		if (mWord != null) {
			mPaint.setColor(Color.GREEN);
			mPaint.setTextSize(mWordSize);
			for (int i=0; i<mWord.size(); i++) {
				canvas.drawText(mWord.get(i), mWidth/2, mHeight/2 + 100 * mDensity 
						+ i*mWordSize, mPaint);
			}
		}
		
		if (mMusicName != null) {
			drawMusic(canvas);
		}
		
		if (mDrawFingerEffect) {
			canvas.restore();
			drawFingerEffect(canvas);
		}
	}
	
	
	
	class RefreshThread extends Thread {
		@Override
		public void run() {
			while (mIfRefresh) {
				postInvalidate();
				
				try {
					sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public interface OnDragFinishedListener {
		void onDragFinished(View view);
	}
}
