package com.enjoyalarm.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.enjoyalarm.GetTextActivity;
import com.enjoyalarm.model.ModelUtil;
import com.enjoyalarm.model.ModelVariable;
import com.enjoyalarm.model.ReadingModel;
import com.enjoyalarm.model.WritingModel;
import com.enjoyalarm.view.ViewUtil.TimeEntry;
import com.scut.enjoyalarm.R;

public class AlarmSettingViewManager {

	public static final int REQUEST_CODE_GET_TEXT = 1;
	public static final String GET_TEXT_EXTRA_SOURCE = "GET_TEXT_EXTRA_SOURCE";
	public static final String GET_TEXT_EXTRA_RESULT = "GET_TEXT_EXTRA_RESULT";
	private int mGetTextType;
	private Activity mActivity;
	private ViewGroup mMainView;
	private boolean mIsChanged;
	private int mAlarmId;
	private View mMediaLayout;
	private View mInputLayout;
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
	private Button[] mInputViews;
	private int mEditHourOrMinute; // 0.none 1.hour 2.minute
	private boolean mClearWhenClickInput;
	private Handler mHandler;
	private int mWhatMessage;

	
	/**
	 * alarmId = -1 for setting a new alarm instead of reading from database
	 */
	public AlarmSettingViewManager(Activity activity, int alarmId) {
		mActivity = activity;
		mAlarmId = alarmId;

		init();
		startUpdatingRemainTime();
	}

	public View getMainView() {
		return mMainView;
	}
	
	public TimeEntry getRemainTime() {
		// get data from views
		String hourString = mHourTextView.getText().toString();
		String minuteString = mMinuteTextView.getText().toString();
		if (hourString.length() == 0 || minuteString.length() == 0) {// hadn't
																		// completed
			return null;
		}
		int hour = Integer.parseInt(hourString);
		int minute = Integer.parseInt(minuteString);
		List<Integer> days = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			if (mDaysViews[i].isChecked()) {
				days.add(i);
			}
		}

		// calculate time
		Time time = new Time();
		time.setToNow();
		int nowDay = (time.weekDay + 6) % 7;
		int nextDay = -1;
		for (int day : days) {
			if (((day == nowDay) && ((hour > time.hour) || (hour == time.hour && minute >= time.minute)))
					|| (day > nowDay)) {
				nextDay = day;
				break;
			}
		}
		if (nextDay == -1) {// the closely next day is in next week
			nextDay = days.get(0);
		}
		return ViewUtil.getRemainTime(nextDay, hour, minute, nowDay, time.hour,
				time.minute);
	}

	/**
	 * @return if user had touch the settings
	 */
	public boolean mIsChanged() {
		return mIsChanged;
	}

	public void save() {
		WritingModel model = new WritingModel(mActivity);
		// name
		model.setName(mNameTextView.getText().toString());
		// time
		model.setTime(Integer.parseInt(mHourTextView.getText().toString()),
				Integer.parseInt(mMinuteTextView.getText().toString()));
		// days
		List<Integer> days = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			if (mDaysViews[i].isChecked()) {
				days.add(i);
			}
		}
		model.setDays(days);
		// repeated
		model.setRepeated(mRepeatCheckBox.isChecked());
		// wake way
		String wakeWay;
		if (mSoundWayToggleView.isSelected()) {
			if (mShakeWayToggleView.isSelected()) {
				wakeWay = ModelVariable.ALARM_WAKE_WAY_SOUND_SHAKE;
			} else {
				wakeWay = ModelVariable.ALARM_WAKE_WAY_SOUND;
			}
		} else {
			wakeWay = ModelVariable.ALARM_WAKE_WAY_SHAKE;
		}
		model.setWakeWay(wakeWay);
		// wake music
		model.setWakeMusicUri(mWakeMusicTextView.getText().toString());
		// encourage words
		model.setText(mEncourageWordsTextView.getText().toString());
		// media

		// save
		if (mAlarmId == -1) {
			model.createAndSave();
		} else {
			model.update(mAlarmId);
		}
	}

	public void startAlarm() {

	}

	public void saveAndStartAlarm() {
		save();
		startAlarm();
	}

	/**
	 * the activity using this manager should call this method in activity's onActivityResult to handle get text case
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GET_TEXT && resultCode == Activity.RESULT_OK) {
			String result = data.getStringExtra(GET_TEXT_EXTRA_RESULT);
			if (mGetTextType == 0) {// set name
				if (result.length() > 0 ) {// if input nothing, then remain the old name
					if (!mNameTextView.getText().toString().equals(result)) {
						mNameTextView.setText(result);
						mIsChanged = true;
					}
				}
				
			} else if (mGetTextType == 1) {//set encourage words
				if (!mEncourageWordsTextView.getText().toString().equals(result)) {
					mEncourageWordsTextView.setText(result);
					mIsChanged = true;
				}
			}
		}
	}
	
	/**
	 * replace all data for alarm from an existing alarmId
	 * @param newId
	 */
	public void replaceAlarm(int newId) {
		mAlarmId = newId;
		settingFromDatabase();
	}
	
	
	
	private void startUpdatingRemainTime() {
		mWhatMessage = 0;
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == mWhatMessage) {
					updateRemainTime();
				}
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					mHandler.obtainMessage(mWhatMessage).sendToTarget();
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void init() {
		initViews();
		alarmSettings();
	}

	private void initViews() {
		mMainView = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.alarm_setting_layout, null);

		// find view
		mMediaLayout = mMainView.findViewById(R.id.media_layout);
		mInputLayout = mMainView.findViewById(R.id.input_layout);
		mNameTextView = (TextView) mMainView.findViewById(R.id.name_tv);
		mHourTextView = (TextView) mMainView.findViewById(R.id.hour_tv);
		mMinuteTextView = (TextView) mMainView.findViewById(R.id.minute_tv);
		mRemainTextView = (TextView) mMainView
				.findViewById(R.id.remain_time_tv);
		mWakeMusicTextView = (TextView) mMainView
				.findViewById(R.id.wake_music_tv);
		mEncourageWordsTextView = (TextView) mMainView
				.findViewById(R.id.encourage_words_tv);
		mRenameButton = (Button) mMainView.findViewById(R.id.rename_bt);
		mAddMediaButton = (Button) mMainView.findViewById(R.id.media_add_bt);
		mRepeatCheckBox = (CheckBox) mMainView.findViewById(R.id.repeat_cb);
		mSoundWayToggleView = (ToggleView) mMainView
				.findViewById(R.id.sound_way_tgv);
		mShakeWayToggleView = (ToggleView) mMainView
				.findViewById(R.id.shake_way_tgv);
		mDaysViews = new ToggleView[7];
		int[] ids = new int[] { R.id.mon_tgv, R.id.tue_tgv, R.id.wed_tgv,
				R.id.thu_tgv, R.id.fri_tgv, R.id.sat_tgv, R.id.sun_tgv };
		for (int i = 0; i < 7; i++) {
			mDaysViews[i] = (ToggleView) mMainView.findViewById(ids[i]);
		}
		mInputViews = new Button[12];
		ids = new int[] { R.id.input0, R.id.input1, R.id.input2, R.id.input3,
				R.id.input4, R.id.input5, R.id.input6, R.id.input7,
				R.id.input8, R.id.input9, R.id.input_back, R.id.input_done };
		for (int i = 0; i < 12; i++) {
			mInputViews[i] = (Button) mMainView.findViewById(ids[i]);
		}

		// set something
		setViewAttrs();
		setViewListeners();
	}

	private void setViewAttrs() {
		mInputLayout.setVisibility(View.GONE);
		mHourTextView.setTextSize(ViewVariable.UNEDIT_TIME_TEXT_SIZE);
		mMinuteTextView.setTextSize(ViewVariable.UNEDIT_TIME_TEXT_SIZE);
	}

	/**
	 * 
	 * @param state
	 *            0.hour first 1.hour 2.minute first 3.minute 4.not editting
	 */
	private void changeEditTimeState(int state) {
		if (mEditHourOrMinute == 0) {// the input is hidden
			changeToInputMode();
		}

		switch (state) {
		case 0: {
			mEditHourOrMinute = 1;
			mClearWhenClickInput = true;
			mHourTextView.setBackgroundResource(R.drawable.time_circle_full);
			mMinuteTextView.setBackgroundDrawable(null);
			break;
		}

		case 1: {
			mEditHourOrMinute = 1;
			mClearWhenClickInput = false;
			mHourTextView.setBackgroundResource(R.drawable.time_circle);
			mMinuteTextView.setBackgroundDrawable(null);
			break;
		}

		case 2: {
			mEditHourOrMinute = 2;
			mClearWhenClickInput = true;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundResource(R.drawable.time_circle_full);
			break;
		}

		case 3: {
			mEditHourOrMinute = 2;
			mClearWhenClickInput = false;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundResource(R.drawable.time_circle);
			break;
		}

		case 4: {
			mEditHourOrMinute = 0;
			mClearWhenClickInput = false;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundDrawable(null);
			changeToMediaMode();
			break;
		}
		}
		
		//ensure the format of hour and minute view is right every time the state is changed
		String hour = mHourTextView.getText().toString();
		String minute = mMinuteTextView.getText().toString();
		if (hour.length() == 0) {
			hour = "00";
		} else if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (minute.length() == 0) {
			minute = "00";
		} else if (minute.length() == 1) {
			minute = "0" + minute;
		}
		mHourTextView.setText(hour);
		mMinuteTextView.setText(minute);
	}

	/**
	 * 
	 * @param type   0.get text for name 1.get text for encourage words
	 */
	private void openGetTextActivity(int type) {
		mGetTextType = type;
		String data = null;
		if (type == 0) {
			data = mNameTextView.getText().toString();
			
		} else if (type == 1) {
			data = mEncourageWordsTextView.getText().toString();
		}
		
		Intent intent = new Intent(mActivity, GetTextActivity.class);
		intent.putExtra(GET_TEXT_EXTRA_SOURCE, data);
		mActivity.startActivityForResult(intent, REQUEST_CODE_GET_TEXT);
	}

	private void setViewListeners() {
		mRenameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openGetTextActivity(0);
			}
		});

		mHourTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsChanged = true;
				changeEditTimeState(0);
			}
		});

		mMinuteTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsChanged = true;
				changeEditTimeState(2);
			}
		});

		OnClickListener daysListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsChanged = true;
				boolean oneSelected = false;
				for (ToggleView t : mDaysViews) {
					if (t != v && t.isChecked()) {
						oneSelected = true;
						break;
					}
				}

				if (oneSelected) {
					((ToggleView) v).toggle();
				}

				updateRemainTime();
			}
		};
		for (View dayView : mDaysViews) {
			dayView.setOnClickListener(daysListener);
		}

		OnClickListener inputListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView timeView;
				int maxNumber;
				if (mEditHourOrMinute == 1) {// hour
					timeView = mHourTextView;
					maxNumber = 23;

				} else {// minute
					timeView = mMinuteTextView;
					maxNumber = 59;
				}

				String nowText = timeView.getText().toString();
				String tag = (String) v.getTag();
				if (mClearWhenClickInput) {
					if (mEditHourOrMinute == 1) {
						changeEditTimeState(1);
					} else {
						changeEditTimeState(3);
					}
					if (!mActivity.getResources().getString(R.string.tag_done)
							.equals(tag)) {
						nowText = "";// clear
					}
				}
				StringBuilder text = new StringBuilder(nowText);

				if (mActivity.getResources().getString(R.string.tag_done).equals(tag)) {// done
					timeView.setText(text);
					changeEditTimeState(4);// close input

				} else if (mActivity.getResources().getString(R.string.tag_back).equals(
						tag)) {// back
					if (text.length() > 0) {
						text.deleteCharAt(text.length() - 1);
					}
					timeView.setText(text);

				} else {// number
					if (text.length() < 2) {
						int number = Integer.parseInt(text.toString() + tag);
						if (number <= maxNumber) {
							text.append(tag);
							timeView.setText(text);
						}
						if (mEditHourOrMinute == 1 && text.length() == 2) {
							changeEditTimeState(2);
						}
					}
				}

				updateRemainTime();
			}
		};
		for (View inputView : mInputViews) {
			inputView.setOnClickListener(inputListener);
		}

		mSoundWayToggleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mShakeWayToggleView.isChecked()) {
					mIsChanged = true;
					mSoundWayToggleView.toggle();
					enableWakeMusic(mSoundWayToggleView.isSelected());
				}
			}
		});

		mShakeWayToggleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSoundWayToggleView.isChecked()) {
					mIsChanged = true;
					mShakeWayToggleView.toggle();
					shake();
				}
			}
		});

		mWakeMusicTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsChanged = true;
			}
		});

		mEncourageWordsTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openGetTextActivity(1);
			}
		});

		mAddMediaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsChanged = true;
			}
		});

	}

	private void alarmSettings() {
		if (mAlarmId == -1) {// setting suggest data
			settingFromSuggestion();

		} else {// setting from database
			settingFromDatabase();
		}
	}

	private void settingFromSuggestion() {
		// set name
		List<String> names = ModelUtil.getAlarmNames(mActivity);
		int noNameAlarmsNumber = 0;
		String initNameString = mActivity.getResources().getString(R.string.init_name);
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
		String suggestTime = ModelUtil.getSuggestTime(mActivity, time.hour);
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
		int nowDay = (time.weekDay + 6) % 7;// Monday is considered first
		if ((suggestHour > time.hour)
				|| (suggestHour == time.hour && suggestMinute >= time.minute)) {// today
			suggestDay = nowDay;
		} else {// tomorrow
			suggestDay = (nowDay + 1) % 7;
		}
		TimeEntry timeEntry = ViewUtil.getRemainTime(suggestDay, suggestHour,
				suggestMinute, nowDay, time.hour, time.minute);
		String remainTimeString;
		if (timeEntry.day == 0) {
			remainTimeString = mActivity.getResources()
					.getString(R.string.remain_time_hm)
					.replace("##", String.valueOf(timeEntry.hour))
					.replace("**", String.valueOf(timeEntry.minute));
		} else {
			remainTimeString = mActivity.getResources()
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
		List<String> datas = ModelUtil.getSuggestData(mActivity,
				ModelVariable.DATA_TYPE_MUSIC_URI);
		String musicName;
		if (datas.size() == 0) {// get default
			musicName = mActivity.getResources()
					.getString(R.string.init_wake_music_words);
		} else {
			musicName = ViewUtil.getMusicName(datas.get(0));
		}
		mWakeMusicTextView.setText(musicName);

		// set encourage words
		datas = ModelUtil
				.getSuggestData(mActivity, ModelVariable.DATA_TYPE_TEXT);
		String text;
		if (datas.size() == 0) {
			text = mActivity.getResources().getString(R.string.init_encourage_words);
		} else {
			text = datas.get(0);
		}
		mEncourageWordsTextView.setText(text);

		// extra data

	}

	private void settingFromDatabase() {
		ReadingModel model = new ReadingModel(mActivity, mAlarmId);

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
			if (((day == time.weekDay) && ((hour > time.hour) || (hour == time.hour && minute > time.minute)))
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
			remainTimeString = mActivity.getResources()
					.getString(R.string.remain_time_hm)
					.replace("##", String.valueOf(timeEntry.hour))
					.replace("**", String.valueOf(timeEntry.minute));
		} else {
			remainTimeString = mActivity.getResources()
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
		if (ModelVariable.ALARM_WAKE_WAY_SOUND.equals(wakeWay)) {// sound
			mSoundWayToggleView.setChecked(true);
			enableWakeMusic(true);

		} else if (ModelVariable.ALARM_WAKE_WAY_SHAKE.equals(wakeWay)) {// shake
			mShakeWayToggleView.setChecked(true);
			enableWakeMusic(false);

		} else {// both
			mSoundWayToggleView.setChecked(true);
			mShakeWayToggleView.setChecked(true);
			enableWakeMusic(true);
		}

		// set wake music
		String musicUri = model.getMusicUri();
		String musicName = ViewUtil.getMusicName(musicUri);
		mWakeMusicTextView.setText(musicName);

		// set text
		String text = model.getText();
		mEncourageWordsTextView.setText(text);

		// extra data

	}

	private void enableWakeMusic(boolean enable) {
		if (enable) {

		} else {

		}
	}

	private void changeToMediaMode() {
		mMediaLayout.setVisibility(View.VISIBLE);
		mInputLayout.setVisibility(View.GONE);
	}

	private void changeToInputMode() {
		mMediaLayout.setVisibility(View.GONE);
		mInputLayout.setVisibility(View.VISIBLE);
	}

	private void shake() {
		Vibrator vibrator = (Vibrator) mActivity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
	}

	private void enableRemainTime(boolean enable) {
		if (enable) {

		} else {

		}
	}

	/**
	 * update time according to hour,minute and day views
	 */
	private void updateRemainTime() {
		TimeEntry timeEntry = getRemainTime();
		if (timeEntry == null) {
			enableRemainTime(false);

		} else {
			enableRemainTime(true);
			String remainTimeString;
			if (timeEntry.day == 0) {
				remainTimeString = mActivity.getResources()
						.getString(R.string.remain_time_hm)
						.replace("##", String.valueOf(timeEntry.hour))
						.replace("**", String.valueOf(timeEntry.minute));
			} else {
				remainTimeString = mActivity.getResources()
						.getString(R.string.remain_time_dh)
						.replace("##", String.valueOf(timeEntry.day))
						.replace("**", String.valueOf(timeEntry.hour));
			}
			mRemainTextView.setText(remainTimeString);
		}
	}

}
