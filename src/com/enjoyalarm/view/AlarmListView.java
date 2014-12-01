package com.enjoyalarm.view;

import java.util.ArrayList;
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
	private static final int WHAT_LIST = 3;
	private static final int WHAT_CLICK = 4;
	private State mState;
	private OnAlarmItemClickListener mItemClickListener;
	private OnScrollToSettingFinishedListener mScrollToSettingFinishedListener;
	private OnScrollToExitFinishedListerner mScrollToExitFinishedListerner;
	private OnScrollToListFinishedListener mScrollToListFinishedListener;
	private Handler mHandler;
	/**
	 * the first item is the new setting one(alarmId=-1), and the last one is help-about one(alarmId=-2)
	 */
	private List<AlarmBasicInfo> mAlarmsBasicInfo;
	private int mCurrentAlarmIndex;
	

	

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
				
				case WHAT_LIST: {
					mScrollToListFinishedListener
							.onScrollToListFinished(AlarmListView.this);
					break;
				}
				
				case WHAT_CLICK: {
					
					mItemClickListener.onAlarmItemClick(AlarmListView.this, msg.arg1);
					break;
				}
				}

			};
		};
	
		mAlarmsBasicInfo = new ArrayList<AlarmBasicInfo>();
		mAlarmsBasicInfo.add(new AlarmBasicInfo(-1, "unset", 0, 0, "0"));
		mAlarmsBasicInfo.addAll(1, ModelUtil.getAlarmsBasicInfo(getContext()));
		mAlarmsBasicInfo.add(new AlarmBasicInfo(-2, "help", 0, 0, null));
		mCurrentAlarmIndex = 0;
	}
	
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

	public void updateAlarmBasicInfo(int index, String name, int hour, int minute, String days) {
		AlarmBasicInfo info = mAlarmsBasicInfo.get(index);
		info.name = name;
		info.hour = hour;
		info.minute = minute;
		info.days = days;
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

	public void setOnScrollToListFinishedListener(
			OnScrollToListFinishedListener listener) {
		mScrollToListFinishedListener = listener;
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

	public interface OnScrollToListFinishedListener {
		public void onScrollToListFinished(View alarmListView);
	}
	
	
	//interface methods
	
	@Override
	public void changeState(State state) {
		mState = state;
	}

	@Override
	public void refreshDraw() {
		mHandler.sendEmptyMessage(WHAT_REFRESH);
	}

	@Override
	public void handleScrollToExitFinished() {
		mHandler.sendEmptyMessage(WHAT_EXIT);
	}

	@Override
	public void handleScrollToSettingFinished() {
		mHandler.sendEmptyMessage(WHAT_RECOVER);
	}

	@Override
	public void handleClickAlarmItem(int alarmId) {
		mHandler.obtainMessage(WHAT_CLICK, alarmId, 0).sendToTarget();;
	}

	@Override
	public void handleScrollToListFinished() {
		mHandler.sendEmptyMessage(WHAT_LIST);
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
	
	/**
	 * the first item is the new setting one(alarmId=-1), and the last one is help-about one(alarmId=-2)
	 */
	@Override
	public List<AlarmBasicInfo> getAlarmsInfo() {
		return mAlarmsBasicInfo;
	}

	@Override
	public int getCurrentAlarmIndex() {
		return mCurrentAlarmIndex;
	}
	
	@Override
	public void setCurrentAlarmIndex(int index) {
		mCurrentAlarmIndex = index;
	}
	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mState.handleDraw(canvas);
	}


}
