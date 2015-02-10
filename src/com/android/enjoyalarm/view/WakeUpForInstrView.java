package com.android.enjoyalarm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

public class WakeUpForInstrView extends View {

	private OnDragFinishedListener mOnDragFinishedListener;
	private String mName;
	private String mTime;
	private String mDay;
	private String mWord;
	private String mMusicName;
	private Paint mPaint = new Paint();
	private float mDensity;
	private float mRotationDegree;
	private float mPointX;
	private float mPointY;
	private float mMovePointX;
	private float mMovePointY;
	private int mWidth;
	private int mHeight;
	private float mDragSuccessGapX;
	private float mDragSuccessGapY;
	private boolean mIfRefresh = true;
	private boolean mDrag = false;
	
	
	
	public WakeUpForInstrView(Context context) {
		super(context);
		
		mName = "早起";
		mTime = "07:30";
		mDay = "周一";
		mWord = "起床！加油！";
		mMusicName = "匆匆那年";
		
		mDensity = context.getResources().getDisplayMetrics().density;
		
		mRotationDegree = -15;
		mPointX = mWidth/2;
		mPointY = mHeight/2;
		mMovePointX = mPointX + mWidth / 8;
		mMovePointY = mPointY - mHeight / 8;
		
		mDragSuccessGapX = 10 * mDensity;
		mDragSuccessGapY = -12 * mDensity;
		
		
	}
	
	public void setWidthAndHeight(int viewWidth, int viewHeight) {
		mWidth = viewWidth;
		mHeight = viewHeight;
	}
	
	public void drag() {
		new RefreshThread().start();
		mDrag = true;
	}
	

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.BLACK);
		mPaint.setTextAlign(Align.CENTER);
		
		if (mDrag) {
			handleDragSuccess();
		}
		
		//the transform order is reverse
		canvas.translate(mMovePointX-mPointX, mMovePointY-mPointY);
		canvas.rotate(mRotationDegree, mPointX, mPointY);
		
		
		mPaint.setColor(Color.YELLOW);
		canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
		
		mPaint.setColor(Color.parseColor("#d22ff5"));
		mPaint.setTextSize(45 * mDensity);
		canvas.drawText(mName, mWidth/2, 60 * mDensity, mPaint);
		
		mPaint.setColor(Color.GRAY);
		mPaint.setTextSize(90 * mDensity);
		canvas.drawText(mTime, mWidth/2, mHeight/2 - 30 * mDensity, mPaint);
		
		mPaint.setColor(Color.LTGRAY);
		mPaint.setTextSize(35 * mDensity);
		canvas.drawText(mDay, mWidth/2, mHeight/2 + 35 * mDensity, mPaint);
		
		
		mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(30 * mDensity);
		canvas.drawText(mWord, mWidth / 2, mHeight / 2 + 100 * mDensity, mPaint);
		
		mPaint.setColor(Color.BLUE);
		mPaint.setTextSize(20 * mDensity);
		mPaint.setTextAlign(Align.LEFT);
		canvas.drawText(mMusicName ,20 * mDensity, mHeight - 25 * mDensity, mPaint);
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
			mIfRefresh = false;
			mOnDragFinishedListener.onDragFinished(this);
		}
	}
	
	
	public void setOnDragFinishedListener(OnDragFinishedListener listener) {
		mOnDragFinishedListener = listener;
	}
	
	public interface OnDragFinishedListener {
		void onDragFinished(View view);
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
	
	
}
