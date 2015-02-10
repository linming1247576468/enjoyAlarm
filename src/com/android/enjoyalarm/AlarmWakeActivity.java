package com.android.enjoyalarm;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Time;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.enjoyalarm.alarm.AlarmUtils;
import com.android.enjoyalarm.model.ModelUtil;
import com.android.enjoyalarm.model.ReadingModel;
import com.android.enjoyalarm.view.ViewUtil;
import com.android.enjoyalarm.view.ViewUtil.TimeEntry;
import com.android.enjoyalarm.view.WakeUpShowView;
import com.android.enjoyalarm.view.WakeUpShowView.OnDragFinishedListener;

public class AlarmWakeActivity extends Activity {

	private boolean mIsFirstWake;
	private int mAlarmId;
	private ReadingModel mData;
	private String mMusicName;
	private Uri mMusicUri;
	private MediaPlayer mMusicPlayer;
	private AudioManager mAudioManager;
	private Vibrator mVibrator;
	private final static int MSG_WHAT_END = 0;
	private final static int MSG_WHAT_VOLUME1 = 1;
	private final static int MSG_WHAT_VOLUME2 = 2;
	private int mDefaultVolume;
	private int mMaxVolume;
	private float mVolume0 = 0.1f;
	private float mVolume1 = 0.3f;
	private float mVolume2 = 0.5f;
	static private Handler mHandler;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// the activity must full screen and no title bar
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		readData();
		WakeUpShowView view = new WakeUpShowView(this, getResources().getDisplayMetrics().widthPixels,
				getResources().getDisplayMetrics().heightPixels, mData);
		view.setMusicName(mMusicName);
		if (!mIsFirstWake) {
			Time time = new Time();
			time.setToNow();
			view.setTime(time.hour, time.minute);
		}
		view.setOnDragFinishedListener(new OnDragFinishedListener() {
			
			@Override
			public void onDragFinished(View view) {
				exitAlarmWithoutWakingAgain();
				finish();
			}
		});
		setContentView(view);
		
		startMusicOrVibration();
		startTimeCounting();
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}


	private void readData() {
		mAlarmId = getIntent().getIntExtra(ActivityVariable.INTENT_EXTRA_ALARM_ID, -1);
		mData = new ReadingModel(this, mAlarmId);
		if (mData.isWakeWayMusic()) {
			mMusicName = null;
			if (mData.getWakeMusicUri() != null) {
				mMusicName = ModelUtil.getMusicName(this, mData.getWakeMusicUri());
			}
			if (mMusicName == null) {
				mMusicUri = RingtoneManager.getValidRingtoneUri(this);
				mMusicName = ModelUtil.getMusicName(this, mMusicUri.toString());
			} else {
				mMusicUri = Uri.parse(mData.getWakeMusicUri());
			}
		}
		
		if (getSharedPreferences(
				ActivityVariable.PREFERENCE_NAME_WAKE_ACTIVITY, 0).getInt(
				ActivityVariable.PREFERENCE_INT_ALARM_NOT_WAKE_NUM + mAlarmId,
				0) == 0) {
			mIsFirstWake = true;
		}
	}
	
	private void startMusicOrVibration() {
		if (mData.isWakeWayMusic()) {
			mMusicPlayer = new MediaPlayer();
			mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
			mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(mMaxVolume * mVolume0), 0);
			
			try {
				mMusicPlayer.setDataSource(this, mMusicUri);
				mMusicPlayer.setLooping(true);
				mMusicPlayer.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						mp.start();
					}
				});
				mMusicPlayer.prepareAsync();
				
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (mData.isWakeWayShake()) {
			mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
			mVibrator.vibrate(new long[]{800, 600}, 0);
		}
	}
	
	private void startTimeCounting() {
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case MSG_WHAT_END: {
					int num = getSharedPreferences(
							ActivityVariable.PREFERENCE_NAME_WAKE_ACTIVITY, 0)
							.getInt(ActivityVariable.PREFERENCE_INT_ALARM_NOT_WAKE_NUM
									+ mAlarmId, 0);
	
					if (num < 2) {// alarm in a while
						exitAlarmWithWakingAgain(num+1);

					} else {// stop this time
						startNotification();
						exitAlarmWithoutWakingAgain();
					}
					break;
				}

				case MSG_WHAT_VOLUME1: {
					if (mMusicPlayer != null) {
						mAudioManager.setStreamVolume(
								AudioManager.STREAM_MUSIC,
								(int) (mMaxVolume * mVolume1), 0);
					}
					break;
				}

				case MSG_WHAT_VOLUME2: {
					if (mMusicPlayer != null) {
						mAudioManager.setStreamVolume(
								AudioManager.STREAM_MUSIC,
								(int) (mMaxVolume * mVolume2), 0);
					}
					break;
				}
				}
				
			} 
		};
		mHandler.sendEmptyMessageDelayed(MSG_WHAT_VOLUME1, 30000);
		mHandler.sendEmptyMessageDelayed(MSG_WHAT_VOLUME2, 50000);
		mHandler.sendEmptyMessageDelayed(MSG_WHAT_END, 70000);
	}

	@SuppressWarnings("deprecation")
	private void startNotification() {
		int hour = ModelUtil.getHourFromTime(mData.getTime());
		int minute = ModelUtil.getMinuteFromTime(mData
				.getTime());
		String time = ViewUtil.getDoubleBitStringForTime(hour)
				+ ":"
				+ ViewUtil.getDoubleBitStringForTime(minute);
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification n = new Notification(
				R.drawable.ic_launcher,
				getResources().getString(
						R.string.notification_ticker),
				System.currentTimeMillis());
		n.setLatestEventInfo(
				getBaseContext(),
				null,
				getResources()
						.getString(R.string.notification_text)
						.replace("##", time)
						.replace("**", mData.getName()), null);
		nm.notify(1, n);
	}
	
	private void exitAlarmWithWakingAgain(int updateWakeNum) {
		//stop music or vibration
		if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
			mMusicPlayer.stop();
		}
		if (mVibrator != null) {
			mVibrator.cancel();
		}
		
		recoverAudioVolume();
		
		getSharedPreferences(ActivityVariable.PREFERENCE_NAME_WAKE_ACTIVITY, 0)
				.edit()
				.putInt(ActivityVariable.PREFERENCE_INT_ALARM_NOT_WAKE_NUM
						+ mAlarmId, updateWakeNum).commit();
		Toast.makeText(this, getResources().getString(R.string.alarm_wake_again).replace("##", "5"),
				Toast.LENGTH_LONG).show();
		AlarmUtils.settingAlarm(getBaseContext(), mAlarmId,
				System.currentTimeMillis() + 5 * 60 * 1000);// 5 minutes 

		finish();
	}

	private void exitAlarmWithoutWakingAgain() {
		removeHandlerMsgs();
		
		//stop music or vibration
		if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
			mMusicPlayer.stop();
		}
		if (mVibrator != null) {
			mVibrator.cancel();
		}
		
		recoverAudioVolume();

		getSharedPreferences(ActivityVariable.PREFERENCE_NAME_WAKE_ACTIVITY, 0)
				.edit()
				.remove(ActivityVariable.PREFERENCE_INT_ALARM_NOT_WAKE_NUM
						+ mAlarmId).commit();

		startNextAlarm();

		finish();
	}
	
	private void removeHandlerMsgs() {
		mHandler.removeMessages(MSG_WHAT_VOLUME1);
		mHandler.removeMessages(MSG_WHAT_VOLUME2);
		mHandler.removeMessages(MSG_WHAT_END);
	}
	
	private void recoverAudioVolume() {
		if (mAudioManager != null) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mDefaultVolume, 0);
		}
	}
	

	private void startNextAlarm() {
		List<Integer> days = mData.getDays();
		Time time = new Time();
		time.setToNow();
		int nowDay = (time.weekDay + 6) % 7;//let monday be first
		int nextDay = -1;
		
		for (int i = 0; i < days.size(); i++) {
			if (days.get(i) > nowDay) {//find the first future day
				nextDay = days.get(i);
				break;
			}
		}
		
		if (mData.getRepeat()) {
			if (nextDay  == -1) {
				nextDay = days.get(0);//get the first of next week
			}
			
		} else {
			days.remove(days.indexOf(nowDay));//remove today
			ModelUtil.updateAlarmWithDays(this, mAlarmId, days);
			if (nextDay == -1 && days.size() > 0) {
				nextDay = days.get(0);//get the first of next week
			}
		}
		
		if (nextDay != -1) {//set next alarm
			TimeEntry entry = ViewUtil.getRemainTime(nextDay, ModelUtil.getHourFromTime(mData.getTime()), 
					ModelUtil.getMinuteFromTime(mData.getTime()), nowDay, time.hour, time.minute);
			long mills;
			if (entry.day == 0 && entry.hour == 0 && entry.minute == 0) {
				//only when nextDay = nowDay+7
				mills = 7 * 24 * 60 * 60 * 1000;//one week
				
			} else {
				mills = entry.day * 24 * 60 * 60
						* 1000 + entry.hour * 60 * 60 * 1000 + entry.minute * 60 * 1000;
			}
			AlarmUtils.settingAlarm(this, mAlarmId, mills + System.currentTimeMillis());
			
			
		} else {//delete this alarm because of no repeating and future day
			ModelUtil.deleteAlarm(this, mAlarmId);
		}
	}
	

	@Override
	public void onBackPressed() {
		exitAlarmWithoutWakingAgain();
	}

	
	/*
	 * Tells the StatusBar whether the alarm is enabled or disabled
	
	private void setStatusBarIcon(Context context, boolean enabled) {
		Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
		alarmChanged.putExtra("alarmSet", enabled);
		context.sendBroadcast(alarmChanged);
	} */
	
	/*private float getVolumeForMediaPlayer(int currVolume) {
	return 1 - (float)(Math.log(mMaxVolume-currVolume)/Math.log(mMaxVolume));
	}*/
}
