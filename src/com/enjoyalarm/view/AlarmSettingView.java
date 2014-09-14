package com.enjoyalarm.view;

import java.util.List;

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
import com.enjoyalarm.model.ReadingModel;
import com.enjoyalarm.model.Variable;
import com.enjoyalarm.view.ViewUtil.TimeEntry;
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
	private Button mAddMediaButton;
	private CheckBox mRepeatCheckBox;
	private ToggleView mSoundWayToggleView;
	private ToggleView mShakeWayToggleView;
	private ToggleView[] mDaysViews;
	private boolean enableWakeMusic;

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
		mAddMediaButton = (Button) mainLayout.findViewById(R.id.media_add_bt);
		mRepeatCheckBox = (CheckBox) mainLayout.findViewById(R.id.repeat_cb);
		mSoundWayToggleView = (ToggleView) mainLayout
				.findViewById(R.id.sound_way_tgv);
		mShakeWayToggleView = (ToggleView) mainLayout
				.findViewById(R.id.shake_way_tgv);
		mDaysViews = new ToggleView[7];
		int[] ids = new int[] { R.id.mon_tgv, R.id.tue_tgv, R.id.wed_tgv,
				R.id.thu_tgv, R.id.fri_tgv, R.id.sat_tgv, R.id.sun_tgv };
		for (int i = 0; i < 7; i++) {
			mDaysViews[i] = (ToggleView) mainLayout.findViewById(ids[i]);
		}
	}

	private void initSettings() {
		if (mAlarmId == -1) {// setting suggest data
			settingFromSuggestion();

		} else {// setting from database
			settingFromDatabase();
		}
	}

	private void settingFromSuggestion() {
		// set name
		List<String> names = ModelUtil.getAlarmNames(mContext);
		int noNameAlarmsNumber = 0;
		String initNameString = getResources().getString(R.string.init_name);
		String regString = initNameString.replace("##", "\\d+");
		for (String name : names) {
			if (name.matches(regString)) {
				noNameAlarmsNumber++;
			}
		}
		mNameTextView.setText(initNameString.replace("##",
				String.valueOf(noNameAlarmsNumber + 1)));

		// set time
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
		mHourTextView.setText(ViewUtil.getDoubleBitStringForTime(suggestHour));
		mMinuteTextView.setText(ViewUtil
				.getDoubleBitStringForTime(suggestMinute));

		// set remainTime
		int suggestDay;
		if ((suggestHour > time.hour)
				|| (suggestHour == time.hour && suggestMinute > time.minute)) {// today
			suggestDay = time.weekDay;
		} else {// tomorrow
			suggestDay = (time.weekDay + 1) % 7;
		}
		TimeEntry timeEntry = ViewUtil.getRemainTime(suggestDay, suggestHour,
				suggestMinute, time.weekDay, time.hour, time.minute);
		String remainTimeString;
		if (timeEntry.day == 0) {
			remainTimeString = getResources()
					.getString(R.string.remain_time_hm)
					.replace("##", String.valueOf(timeEntry.hour))
					.replace("**", String.valueOf(timeEntry.minute));
		} else {
			remainTimeString = getResources()
					.getString(R.string.remain_time_dh)
					.replace("##", String.valueOf(timeEntry.day))
					.replace("**", String.valueOf(timeEntry.hour));
		}
		mRemainTextView.setText(remainTimeString);

		// set day
		mDaysViews[suggestDay].setChecked(true);

		// set repeat
		mRepeatCheckBox.setChecked(false);

		// set wake buttons
		mSoundWayToggleView.setChecked(true);
		enableWakeMusic(true);

		// set wake music
		List<String> datas = ModelUtil.getSuggestData(mContext,
				Variable.DATA_TYPE_MUSIC_URI);
		String musicName;
		if (datas.size() == 0) {// get default
			musicName = getResources()
					.getString(R.string.init_wake_music_words);
		} else {
			musicName = ViewUtil.getMusicName(datas.get(0));
		}
		mWakeMusicTextView.setText(musicName);

		// set encourage words
		datas = ModelUtil.getSuggestData(mContext, Variable.DATA_TYPE_TEXT);
		String text;
		if (datas.size() == 0) {
			text = getResources().getString(R.string.init_encourage_words);
		} else {
			text = datas.get(0);
		}
		mEncourageWordsTextView.setText(text);

		// extra data

	}

	private void settingFromDatabase() {
		ReadingModel model = new ReadingModel(mContext, mAlarmId);

		// set name
		mNameTextView.setText(model.getName());

		// set time
		String settingTime = model.getTime();
		int hour = ModelUtil.getHourFromTime(settingTime);
		int minute = ModelUtil.getMinuteFromTime(settingTime);
		mHourTextView.setText(ViewUtil.getDoubleBitStringForTime(hour));
		mMinuteTextView.setText(ViewUtil.getDoubleBitStringForTime(minute));

		// set repeat
		mRepeatCheckBox.setChecked(model.getRepeat());

		// set remain time
		List<Integer> days = model.getDays();
		Time time = new Time();
		time.setToNow();
		int nextDay = -1;
		for (int day : days) {
			if ((day == time.weekDay)
					&& ((hour > time.hour) || (hour == time.hour && minute > time.minute))
					|| (day > time.weekDay)) {
				nextDay = day;
				break;
			}
		}
		if (nextDay == -1) {// the closely next day is in next week
			nextDay = days.get(0);
		}
		TimeEntry timeEntry = ViewUtil.getRemainTime(nextDay, hour, minute,
				time.weekDay, time.hour, time.minute);
		String remainTimeString;
		if (timeEntry.day == 0) {
			remainTimeString = getResources()
					.getString(R.string.remain_time_hm)
					.replace("##", String.valueOf(timeEntry.hour))
					.replace("**", String.valueOf(timeEntry.minute));
		} else {
			remainTimeString = getResources()
					.getString(R.string.remain_time_dh)
					.replace("##", String.valueOf(timeEntry.day))
					.replace("**", String.valueOf(timeEntry.hour));
		}
		mRemainTextView.setText(remainTimeString);

		// set days
		for (int day : days) {
			mDaysViews[day].setChecked(true);
		}

		// set wake buttons
		String wakeWay = model.getWakeWay();
		if (Variable.ALARM_WAKE_WAY_SOUND.equals(wakeWay)) {//sound
			mSoundWayToggleView.setChecked(true);
			enableWakeMusic(true);
			
		} else if (Variable.ALARM_WAKE_WAY_SHAKE.equals(wakeWay)) {//shake
			mShakeWayToggleView.setChecked(true);
			enableWakeMusic(false);
			
		} else {//both
			mSoundWayToggleView.setChecked(true);
			mShakeWayToggleView.setChecked(true);
			enableWakeMusic(true);
		}
		
		//set wake music
		String musicUri = model.getMusicUri();
		String musicName = ViewUtil.getMusicName(musicUri);
		mWakeMusicTextView.setText(musicName);
		
		//set text
		String text = model.getText();
		mEncourageWordsTextView.setText(text);
		
		//extra data

	}

	private void enableWakeMusic(boolean enable) {
		enableWakeMusic = enable;
		if (enable) {
			
		} else {
			
		}
	}
}
