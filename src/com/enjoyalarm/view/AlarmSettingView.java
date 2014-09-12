package com.enjoyalarm.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import com.enjoyalarm.model.ModelUtil;
import com.scut.enjoyalarm.R;

public class AlarmSettingView extends ScrollView {

	private int mAlarmId;
	private Context mContext;
	private TextView mNameTextView;
	private TextView mHourTextView;
	private TextView mMinuteTextView;
	private TextView mRemainTextView;
	private TextView mWakeMusicTextView;
	private TextView mEncourageWordsTextView;
	private Button mRenameButton;
	private Button mWakeWayButton;
	private Button mSoundWayButton;
	private Button mShakeWayButton;
	private Button mWakeAgainButton;
	private Button mAddMediabButton;
	private CheckBox mRepeatCheckBox;
	private View[] mDaysViews;

	/**
	 * alarmId = -1 for setting a new alarm instead of reading from database
	 */
	public AlarmSettingView(Context context, int alarmId) {
		super(context);
		mContext = context;
		mAlarmId = alarmId;

		init();
	}

	/**
	 * alarmId = -1 for setting a new alarm instead of reading from database
	 */
	public AlarmSettingView(Context context, int alarmId, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mAlarmId = alarmId;

		init();
	}

	private void init() {
		initViews();
	}

	private void initViews() {
		ViewGroup mainLayout = (ViewGroup) inflate(mContext,
				R.layout.alarm_setting_layout, null);
		addView(mainLayout);

		mNameTextView = (TextView) mainLayout.findViewById(R.id.name_tv);
		mHourTextView = (TextView) mainLayout.findViewById(R.id.hour_tv);
		mMinuteTextView = (TextView) mainLayout.findViewById(R.id.minute_tv);
		mRemainTextView = (TextView) mainLayout
				.findViewById(R.id.remain_time_tv);
		mWakeMusicTextView = (TextView) mainLayout
				.findViewById(R.id.wake_music_tv);
		mEncourageWordsTextView = (TextView) mainLayout
				.findViewById(R.id.encourage_words_tv);
		mRenameButton = (Button) mainLayout.findViewById(R.id.rename_bt);
		mWakeWayButton = (Button) mainLayout.findViewById(R.id.wake_way_bt);
		mSoundWayButton = (Button) mainLayout.findViewById(R.id.sound_way_bt);
		mShakeWayButton = (Button) mainLayout.findViewById(R.id.shake_way_bt);
		mWakeAgainButton = (Button) mainLayout.findViewById(R.id.wake_again_bt);
		mAddMediabButton = (Button) mainLayout.findViewById(R.id.media_add_bt);
		mRepeatCheckBox = (CheckBox) mainLayout.findViewById(R.id.repeat_cb);
		mDaysViews = new View[7];
		int[] ids = new int[] { R.id.mon_tv, R.id.tue_tv, R.id.wed_tv,
				R.id.thu_tv, R.id.fri_tv, R.id.sat_tv, R.id.sun_tv };
		for (int i = 0; i < 7; i++) {
			mDaysViews[i] = mainLayout.findViewById(ids[i]);
		}
	}

	private void initSettings() {
		if (mAlarmId == -1) {// setting suggest data
			//set name
			List<String> names = ModelUtil.getAlarmNames(mContext);
			int noNameAlarmsNumber = 0;
			String initNameString = getResources()
					.getString(R.string.init_name);
			String regString = initNameString.replace("##", "\\d+");
			for (String name : names) {
				if (name.matches(regString)) {
					noNameAlarmsNumber++;
				}
			}
			mNameTextView.setText(initNameString.replace("##",
					String.valueOf(noNameAlarmsNumber + 1)));
			
			//set time, remainTime, day
			Time time = new Time();
			time.setToNow();
			String suggestTime = ModelUtil.getSuggestTime(mContext, time.hour);
			int suggestHour;
			int suggestMinute;
			if (suggestTime == null) {// set now time
				suggestHour = time.hour;
				suggestMinute = time.minute;

			} else {// set suggest time
				suggestHour = ModelUtil.getHourFromTime(suggestTime);
				suggestMinute = ModelUtil.getMinuteFromTime(suggestTime);
			}
			mHourTextView.setText(ViewUtil.getStringForView(suggestHour));
			mMinuteTextView.setText(ViewUtil.getStringForView(suggestMinute));
			mRemainTextView.setText(ViewUtil.getRemainTimeStringForView(
					getResources().getString(R.string.remain_time),
					suggestHour, suggestMinute, time.hour, time.minute));
			if ((suggestHour > time.hour)
					|| (suggestHour == time.hour && suggestMinute > time.minute)) {
				
			} else {
				
			}

		} else {// setting from database

		}
	}
}
