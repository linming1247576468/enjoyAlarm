package com.enjoyalarm.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;
import com.enjoyalarm.view.AlarmListView.OnAlarmItemClickListener;
import com.enjoyalarm.view.AlarmListView.OnScrollToExitFinishedListerner;
import com.enjoyalarm.view.AlarmListView.OnScrollToListStartedListener;
import com.enjoyalarm.view.AlarmListView.OnScrollToSettingFinishedListener;
import com.scut.enjoyalarm.R;

public class MainViewManager {

	private Activity mActivity;
	private MyFrameLayout mMainView;
	private AlarmListView mAlarmListView;
	private View mAlarmSettingView;
	private AlarmSettingViewManager mAlarmSettingViewManager;
	private int mNowAlarmId;
	private boolean mIsSettingViewVisiable = true;
	private float mTouchGap;
	
	
	public MainViewManager(Activity activity) {
		mActivity = activity;
		mMainView = new MyFrameLayout(activity);
		mAlarmListView = new AlarmListView(activity);
		mAlarmSettingViewManager = new AlarmSettingViewManager(activity, -1);
		mAlarmSettingView = mAlarmSettingViewManager.getMainView();
		mNowAlarmId = -1;
		mTouchGap = activity.getResources().getDisplayMetrics().density * 10;
		
		init();
	}
	
	public View getMainView() {
		return mMainView;
	}
	
	/**
	 * the activity using this manager should call this method in activity's onActivityResult to handle get text case
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mAlarmSettingViewManager.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void init() {
		mAlarmSettingView.setDrawingCacheEnabled(true);
		
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mMainView.addView(mAlarmListView,layoutParams);
		mMainView.addView(mAlarmSettingView, layoutParams);
		mMainView.setBackgroundColor(Color.BLACK);
		
		mMainView.setOnTouchListener(new OnTouchListener() {
			float downX = -1;
			float downY = -1;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (mIsSettingViewVisiable) {//decide whether to hide setting view
					float nowX = event.getX();
					float nowY = event.getY();
					
					switch(event.getActionMasked()) {
					
					case MotionEvent.ACTION_DOWN: {
						downX = nowX;
						downY = nowY;
						break;
					}
					
					case MotionEvent.ACTION_MOVE: {
						if (downX == -1 && downY == -1) {//down event may escape, not sure
							downX = nowX;
							downY = nowY;
							
						} else {
							if (Math.abs(downX-nowX) > Math.abs(downY-nowY)) {//move horizon
								if (Math.abs(downX-nowX) > mTouchGap) {//move enough
									mIsSettingViewVisiable = false;
									mAlarmSettingView.destroyDrawingCache();
									mAlarmSettingView.buildDrawingCache();
									mAlarmListView.startStateMachine(0, mAlarmSettingView.getDrawingCache());
									mAlarmListView.handleTouchEvent(event);
									mAlarmListView.invalidate();//must
									mAlarmSettingView.setVisibility(View.INVISIBLE);//GONE will release the bitmap cache
									downX = -1;
									downY = -1;
								}
								
							} else {//move vertical
								if (Math.abs(downY-nowY) > mTouchGap) {//move enough
									mIsSettingViewVisiable = false;
									mAlarmSettingView.destroyDrawingCache();
									mAlarmSettingView.buildDrawingCache();
									mAlarmListView.startStateMachine(1, mAlarmSettingView.getDrawingCache());
									mAlarmListView.handleTouchEvent(event);
									mAlarmListView.invalidate();//must
									mAlarmSettingView.setVisibility(View.INVISIBLE);//GONE will release the bitmap cache
									downX = -1;
									downY = -1;
								}
							}
						}
						
						break;
					}
					
					case MotionEvent.ACTION_UP: {
						downX = -1;
						downY = -1;
						break;
					}
					
					}
					
				} else {//setting view is inVisiable, let alarmListView handle it
					mAlarmListView.handleTouchEvent(event);
				}
				
				
				return true;
			}
		});
		
		mAlarmListView.setOnAlarmItemClickListener(new OnAlarmItemClickListener() {
			
			@Override
			public void onAlarmItemClick(View alarmListView, int alarmId) {
				if (alarmId != mNowAlarmId) {
					mAlarmSettingViewManager.replaceAlarm(alarmId);
					mNowAlarmId = alarmId;
				}
				
				//let the alarmSettingView be the facing view
				mAlarmSettingView.setVisibility(View.VISIBLE);
				mIsSettingViewVisiable = true;
			}
		});
		
		mAlarmListView.setOnScrollToSettingFinishedListener(new OnScrollToSettingFinishedListener() {
			
			@Override
			public void onScrollToSettingFinished(View alarmListView) {
				//let the alarmSettingView be the facing view
				mAlarmSettingView.setVisibility(View.VISIBLE);
				mIsSettingViewVisiable = true;
			}
		});
		
		mAlarmListView.setOnScrollToExitFinishedListener(new OnScrollToExitFinishedListerner() {
			
			@Override
			public void onScrollToExitFinished(View alarmListView) {
				if (mNowAlarmId == -1 || mAlarmSettingViewManager.getIsChanged()) {
					mAlarmSettingViewManager.saveAndStartAlarm();
					String toastString = ViewUtil.getRemainTimeForToast(mActivity, mAlarmSettingViewManager.getRemainTime());
					Toast.makeText(mActivity, toastString, Toast.LENGTH_SHORT).show();
					mActivity.finish();
				}
			}
		});

		mAlarmListView.setOnScrollToListStartedListener(new OnScrollToListStartedListener() {

			@Override
			public void onScrollToListStarted(View alarmListView) {
				if (mNowAlarmId == -1) {
					mAlarmSettingViewManager.saveTemp();
					
				} else if (mAlarmSettingViewManager.getIsChanged()) {
					String toastString = mActivity.getResources().getString(R.string.has_save);
					Toast.makeText(mActivity, toastString, Toast.LENGTH_SHORT).show();
					mAlarmSettingViewManager.saveAndStartAlarm();
				}
				AlarmBasicInfo info = mAlarmSettingViewManager.getCurrentAlarmInfo();
				mAlarmListView.updateAlarmBasicInfo(
						mAlarmListView.getCurrentAlarmIndex(),
						info.name, info.hour, info.minute, info.days);
			}
		});
	}
}

