package com.enjoyalarm.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.enjoyalarm.alarmliststate.ExitingState;
import com.enjoyalarm.alarmliststate.InitState;
import com.enjoyalarm.alarmliststate.ListingState;
import com.enjoyalarm.alarmliststate.State;
import com.enjoyalarm.model.ModelUtil;
import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;

public class AlarmListView extends View implements ViewControlInterface {

	private static final int WHAT_REFRESH = 0;
	private static final int WHAT_EXIT = 1;
	private static final int WHAT_RECOVER = 2;
	private State mState;
	private OnAlarmItemClickListener mItemClickListener;
	private OnScrollToSettingFinishedListener mScrollToSettingFinishedListener;
	private OnScrollToExitFinishedListerner mScrollToExitFinishedListerner;
	private Handler mHandler;
	private List<AlarmBasicInfo> mAlarmsBasicInfo;
	

	public AlarmListView(Context context) {
		super(context);

		init();
	}

	public void handleTouchEvent(MotionEvent event) {
		mState.handleTouchEvent(event);
	}

	/**
	 * you must call this method first when this view begins to react to user's
	 * gesture
	 * 
	 * @param direction
	 *            0.exiting(horizon) 1.listing(vertical) bitmap the foreground
	 *            picture
	 */
	public void startStateMachine(int direction, Bitmap bitmap) {
		if (direction == 0) {
			mState = new ExitingState(this, bitmap, 0f);

		} else {
			mState = new ListingState(this, bitmap, 0f);
		}
	}

	public void setOnAlarmItemClickListener(OnAlarmItemClickListener listener) {
		mItemClickListener = listener;
	}

	public void setOnScrollToSettingFinishedListener(
			OnScrollToSettingFinishedListener listener) {
		mScrollToSettingFinishedListener = listener;
	}

	public void setOnScrollToExitFinishedListener(
			OnScrollToExitFinishedListerner listerner) {
		mScrollToExitFinishedListerner = listerner;
	}

	public interface OnAlarmItemClickListener {
		public void onAlarmItemClick(View alarmListView, int alarmId);
	}

	public interface OnScrollToSettingFinishedListener {
		public void onScrollToSettingFinished(View alarmListView);
	}

	public interface OnScrollToExitFinishedListerner {
		public void onScrollToExitFinished(View alarmListView);
	}

	@Override
	public void changeState(State state) {
		mState = state;
	}

	@Override
	public void refreshDraw() {
		mHandler.sendEmptyMessage(WHAT_REFRESH);
	}

	@Override
	public void exitApp() {
		mHandler.sendEmptyMessage(WHAT_EXIT);
	}

	@Override
	public void recoverToSettingView() {
		mHandler.sendEmptyMessage(WHAT_RECOVER);
	}

	@Override
	public void clickAlarmItem(int alarmId) {
		mItemClickListener.onAlarmItemClick(this, alarmId);
	}

	private void init() {
		mState = new InitState(this);
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case WHAT_REFRESH: {
					invalidate();
					break;
				}
				case WHAT_EXIT: {
					mScrollToExitFinishedListerner
							.onScrollToExitFinished(AlarmListView.this);
					break;
				}

				case WHAT_RECOVER: {
					mScrollToSettingFinishedListener
							.onScrollToSettingFinished(AlarmListView.this);
					break;
				}
				}

			};
		};
	}

	@Override
	public float getDensity() {
		return getResources().getDisplayMetrics().density;
	}
	
	@Override
	public float getViewWidth() {
		return getWidth();
	}

	@Override
	public float getViewHeight() {
		return getHeight();
	}
	
	@Override
	public List<AlarmBasicInfo> getAlarmsInfo() {
		if (mAlarmsBasicInfo == null) {
			mAlarmsBasicInfo = ModelUtil.getAlarmsBasicInfo(getContext());
			
		}
		return mAlarmsBasicInfo;
	}

	@Override
	public int getCurrentAlarmIndex() {
		return 0;
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mState.handleDraw(canvas);
	}

}
