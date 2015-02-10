package com.android.enjoyalarm.view;

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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.enjoyalarm.ActivityVariable;
import com.android.enjoyalarm.GetTextActivity;
import com.android.enjoyalarm.MusicSelectActivity;
import com.android.enjoyalarm.R;
import com.android.enjoyalarm.alarm.AlarmUtils;
import com.android.enjoyalarm.model.ModelUtil;
import com.android.enjoyalarm.model.ModelUtil.AlarmBasicInfo;
import com.android.enjoyalarm.model.ModelVariable;
import com.android.enjoyalarm.model.ReadingModel;
import com.android.enjoyalarm.model.WritingModel;
import com.android.enjoyalarm.view.ViewUtil.TimeEntry;

public class AlarmSettingViewManager {

	private int mGetTextType;
	private String mWakeMusicUri;
	private Activity mActivity;
	private ViewGroup mMainView;
	private int mAlarmId;
	private View mMediaLayout;
	private View mInputLayout;
	private TextView mNameTextView;
	private TextView mHourTextView;
	private TextView mMinuteTextView;
	private TextView mRemainTextView;
	private TextView mWakeMusicTextView;
	private TextView mEncourageWordsTextView;
	private View mRemainTimeLayerView;
	private View mWakeMusicLayerView;
	private View mRenameView;
	private Button mAddMediaButton;
	private CheckBox mRepeatCheckBox;
	private ToggleView mSoundWayToggleView;
	private ToggleView mShakeWayToggleView;
	private ToggleView[] mDaysViews;
	private Button[] mInputViews;
	private int mEditHourOrMinute; // 0.none 1.hour 2.minute
	private boolean mClearWhenClickInput;
	private Handler mHandler;
	private static final int MSG_WHAT_UPDATE_TIME = 0;
	private AlarmDataComparator mDataComparator;
	

	
	public void settingForInstruction() {
		mHourTextView.performClick();
	}
	
	public void settingFromInstruction() {
		mEditHourOrMinute = 0;
		mClearWhenClickInput = false;
		mHourTextView.setBackgroundDrawable(null);
		mMinuteTextView.setBackgroundDrawable(null);
		mMediaLayout.setVisibility(View.VISIBLE);
		mInputLayout.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * alarmId = -1 for setting a new alarm instead of reading from database
	 */
	public AlarmSettingViewManager(Activity activity, int alarmId) {
		mActivity = activity;
		mAlarmId = alarmId;
		mDataComparator = new AlarmDataComparator();

		init();
		startUpdatingRemainTime();
		mDataComparator.recordCurrentData();
	}

	public View getMainView() {
		return mMainView;
	}
	
	public TimeEntry getRemainTime() {
		// get data from views
		String hourString = mHourTextView.getText().toString();
		String minuteString = mMinuteTextView.getText().toString();
		if (hourString.length() != 2 || minuteString.length() != 2) {
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
		int nowDay = (time.weekDay + 6) % 7;//let Monday be first
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

	public boolean getIsChanged() {
		return mDataComparator.isChangedCompareCurrentWithDataKeeped();
	}

	/**
	 * if you just want to save the data in the temp table, you should call this
	 * instead of save() which will make a new id for this temp alarm
	 */
	public void saveTemp() {
		mDataComparator.recordCurrentData();
		WritingModel model = new WritingModel(mActivity);
		model.setName(mDataComparator.name);
		model.setTime(mDataComparator.hour, mDataComparator.minute);
		model.setDays(mDataComparator.days);
		model.setRepeated(mDataComparator.repeated);
		model.setWakeWay(mDataComparator.wakeWay);
		model.setWakeMusicUri(mDataComparator.wakeUri);
		model.setText(mDataComparator.encourageWords);
		model.update(-1);
	}
	
	public void save() {
		mDataComparator.recordCurrentData();
		WritingModel model = new WritingModel(mActivity);
		model.setName(mDataComparator.name);
		model.setTime(mDataComparator.hour, mDataComparator.minute);
		model.setDays(mDataComparator.days);
		model.setRepeated(mDataComparator.repeated);
		model.setWakeWay(mDataComparator.wakeWay);
		model.setWakeMusicUri(mDataComparator.wakeUri);
		model.setText(mDataComparator.encourageWords);
		
		// save
		if (mAlarmId == -1) {
			mAlarmId = model.createAndSave();
		} else {
			model.update(mAlarmId);
		}
	}

	public void startAlarm() {
		TimeEntry time = getRemainTime();
		long mills = System.currentTimeMillis() + time.day * 24 * 60 * 60
				* 1000 + time.hour * 60 * 60 * 1000 + time.minute * 60 * 1000;
		AlarmUtils.settingAlarm(mActivity, mAlarmId, mills);
	}

	public void saveAndStartAlarm() {
		save();
		startAlarm();
	}

	/**
	 * the activity using this manager should call this method in activity's onActivityResult to handle get text case
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityVariable.GET_TEXT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			String result = data.getStringExtra(ActivityVariable.GET_TEXT_EXTRA_RESULT);
			if (mGetTextType == 0) {// set name
				if (result.length() > 0 ) {// if input nothing, then remain the old name
					if (!mNameTextView.getText().toString().equals(result)) {
						mNameTextView.setText(result);
					}
				}
				
			} else if (mGetTextType == 1) {//set encourage words
				if (!mEncourageWordsTextView.getText().toString().equals(result)) {
					mEncourageWordsTextView.setText(result);
				}
			}
		} else if (requestCode == ActivityVariable.MUSIC_SELECT_REQUEST_CODE 
				&& resultCode == Activity.RESULT_OK) {
			mWakeMusicUri = data.getStringExtra(ActivityVariable.MUSIC_SELECT_EXTRA_SELECT_MUSIC_URI);
			mWakeMusicTextView.setText(data.getStringExtra(ActivityVariable.MUSIC_SELECT_EXTRA_SELECT_MUSIC_TITLE));
		}
	}
	
	/**
	 * replace all data for alarm from a new alarmId
	 * @param newId
	 */
	public void replaceAlarm(int newId) {
		if (newId != mAlarmId) {
			if (mEditHourOrMinute != 0) {
				changeEditTimeState(4);
			}
			mAlarmId = newId;
			mWakeMusicUri = null;
			settingFromDatabase();
			mDataComparator.recordCurrentData();

		}
	}
	
	public AlarmBasicInfo getCurrentAlarmInfo() {
		List<Integer> days = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			if (mDaysViews[i].isChecked()) {
				days.add(i);
			}
		}
		AlarmBasicInfo info = new AlarmBasicInfo(mAlarmId, mNameTextView
				.getText().toString(), Integer.parseInt(mHourTextView.getText()
				.toString()), Integer.parseInt(mMinuteTextView.getText()
				.toString()), ModelUtil.getDaysString(days), mRepeatCheckBox.isChecked());
		return info;
	}
	
	
	
	private void startUpdatingRemainTime() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MSG_WHAT_UPDATE_TIME) {
					updateRemainTime();
					
				}
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					mHandler.sendEmptyMessage(MSG_WHAT_UPDATE_TIME);
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
		mRemainTimeLayerView = mMainView.findViewById(R.id.remain_time_layer);
		mWakeMusicLayerView = mMainView.findViewById(R.id.wake_music_layer);
		mRenameView = mMainView.findViewById(R.id.rename_bt);
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
		mInputLayout.setVisibility(View.INVISIBLE);
		mMediaLayout.setVisibility(View.VISIBLE);
		mNameTextView.setTextSize(ViewVariable.NAME_TEXT_SIZE);
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
			mHourTextView.setBackgroundResource(R.drawable.time_click_full);
			mMinuteTextView.setBackgroundDrawable(null);
			break;
		}

		case 1: {
			mEditHourOrMinute = 1;
			mClearWhenClickInput = false;
			mHourTextView.setBackgroundResource(R.drawable.time_click);
			mMinuteTextView.setBackgroundDrawable(null);
			break;
		}

		case 2: {
			mEditHourOrMinute = 2;
			mClearWhenClickInput = true;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundResource(R.drawable.time_click_full);
			break;
		}

		case 3: {
			mEditHourOrMinute = 2;
			mClearWhenClickInput = false;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundResource(R.drawable.time_click);
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
		intent.putExtra(ActivityVariable.GET_TEXT_EXTRA_SOURCE, data);
		intent.putExtra(ActivityVariable.GET_TEXT_EXTRA_TYPE, type);
		mActivity.startActivityForResult(intent, ActivityVariable.GET_TEXT_REQUEST_CODE);
	}

	private void setViewListeners() {
		mRenameView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openGetTextActivity(0);
			}
		});

		mHourTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeEditTimeState(0);
			}
		});

		mMinuteTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeEditTimeState(2);
			}
		});

		OnClickListener daysListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
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
					mSoundWayToggleView.toggle();
					enableWakeMusic(mSoundWayToggleView.isChecked());
				}
			}
		});

		mShakeWayToggleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSoundWayToggleView.isChecked()) {
					if (!mShakeWayToggleView.isChecked()) {
						shake();
					}
					mShakeWayToggleView.toggle();
				}
			}
		});

		mWakeMusicTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,MusicSelectActivity.class);
				String musicText = mWakeMusicTextView.getText().toString();
				if (!musicText.equals(mActivity.getResources()
					.getString(R.string.init_wake_music_words))) {
					intent.putExtra(ActivityVariable.MUSIC_SELECT_EXTRA_ORIGINAL_MUSIC_TITLE, musicText);
				}
				mActivity.startActivityForResult(intent, ActivityVariable.MUSIC_SELECT_REQUEST_CODE);
			}
		});

		mWakeMusicLayerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//do nothing
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
		int maxNumber = 0;
		String initNameString = mActivity.getResources().getString(R.string.init_name);
		String regString = initNameString.replace("##", "\\d+");
		int numIndex = initNameString.indexOf('#');
		for (String name : names) {
			if (name.matches(regString)) {
				int nowNum = Integer.parseInt(name.substring(numIndex));
				if (nowNum > maxNumber) {
					maxNumber = nowNum;
				}
			}
		}
		mNameTextView.setText(initNameString.replace("##",
				String.valueOf(maxNumber + 1)));

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
		for (ToggleView view: mDaysViews) {
			view.setChecked(false);
		}
		mDaysViews[suggestDay].setChecked(true);

		// set repeat
		mRepeatCheckBox.setChecked(false);

		// set wake buttons
		mSoundWayToggleView.setChecked(true);
		mShakeWayToggleView.setChecked(false);
		enableWakeMusic(true);

		// set wake music
		List<String> datas = ModelUtil.getSuggestMusic(mActivity,
				ModelVariable.DATA_TYPE_WAKE_MUSIC_URI);
		String musicName = null;
		if (datas.size() > 0) {
			mWakeMusicUri = datas.get(0);
			musicName = ModelUtil.getMusicName(mActivity, mWakeMusicUri);
		}
		if (musicName == null) {
			musicName = mActivity.getResources()
					.getString(R.string.init_wake_music_words);
			mWakeMusicUri = null;
			
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
		int nowDay = (time.weekDay + 6) % 7;// Monday is considered first
		int nextDay = -1;
		for (int day : days) {
			if (((day == nowDay) && ((hour > time.hour) || (hour == time.hour && minute > time.minute)))
					|| (day > nowDay)) {
				nextDay = day;
				break;
			}
		}
		if (nextDay == -1) {// the closely next day is in next week
			nextDay = days.get(0);
		}
		TimeEntry timeEntry = ViewUtil.getRemainTime(nextDay, hour, minute,
				nowDay, time.hour, time.minute);
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
		for (ToggleView view: mDaysViews) {
			view.setChecked(false);
		}
		for (int day : days) {
			mDaysViews[day].setChecked(true);
		}

		// set wake buttons
		mSoundWayToggleView.setChecked(model.isWakeWayMusic());
		mShakeWayToggleView.setChecked(model.isWakeWayShake());
		enableWakeMusic(model.isWakeWayMusic());

		// set wake music
		String musicUri = model.getWakeMusicUri();
		String musicName = null;
		if (musicUri != null) {
			musicName = ModelUtil.getMusicName(mActivity, musicUri);
		}
		if (musicName == null) {
			musicName = mActivity.getResources().getString(
					R.string.init_wake_music_words);
		} else {
			mWakeMusicUri = musicUri;
		}
		mWakeMusicTextView.setText(musicName);

		// set text
		String text = model.getText();
		mEncourageWordsTextView.setText(text);

		// extra data

	}

	private void enableWakeMusic(boolean enable) {
		if (enable) {
			mWakeMusicLayerView.setVisibility(View.INVISIBLE);
		} else {
			mWakeMusicLayerView.setVisibility(View.VISIBLE);
		}
	}

	private void changeToMediaMode() {
		TranslateAnimation in_animation = (TranslateAnimation)AnimationUtils.loadAnimation(mActivity, R.anim.input_layout_in);
		TranslateAnimation out_animation = (TranslateAnimation)AnimationUtils.loadAnimation(mActivity, R.anim.input_layout_out);
		in_animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mMediaLayout.setVisibility(View.VISIBLE);
				mInputLayout.setVisibility(View.INVISIBLE);
			}
		});
		
		mInputLayout.startAnimation(out_animation);
		mMediaLayout.startAnimation(in_animation);
	}

	private void changeToInputMode() {
		TranslateAnimation in_animation = (TranslateAnimation)AnimationUtils.loadAnimation(mActivity, R.anim.input_layout_in);
		TranslateAnimation out_animation = (TranslateAnimation)AnimationUtils.loadAnimation(mActivity, R.anim.input_layout_out);
		in_animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mMediaLayout.setVisibility(View.INVISIBLE);
				mInputLayout.setVisibility(View.VISIBLE);
			}
		});
		
		mMediaLayout.startAnimation(out_animation);
		mInputLayout.startAnimation(in_animation);
	}

	private void shake() {
		Vibrator vibrator = (Vibrator) mActivity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	private void enableRemainTime(boolean enable) {
		if (enable) {
			mRemainTimeLayerView.setVisibility(View.INVISIBLE);
		} else {
			mRemainTimeLayerView.setVisibility(View.VISIBLE);
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

	
	
	private class AlarmDataRecorder {
		String name;
		int hour;
		int minute;
		boolean repeated;
		List<Integer> days = new ArrayList<Integer>();
		String wakeWay;
		String wakeUri;
		String encourageWords;
		
		void recordCurrentData() {
			name = mNameTextView.getText().toString();
			hour = Integer.parseInt(mHourTextView.getText().toString());
			minute = Integer.parseInt(mMinuteTextView.getText().toString());
			
			repeated = mRepeatCheckBox.isChecked();
			days.clear();
			for (int i = 0; i < 7; i++) {
				if (mDaysViews[i].isChecked()) {
					days.add(i);
				}
			}
			
			if (mSoundWayToggleView.isChecked()) {
				if (mShakeWayToggleView.isChecked()) {
					wakeWay = ModelVariable.ALARM_WAKE_WAY_SOUND_SHAKE;
				} else {
					wakeWay = ModelVariable.ALARM_WAKE_WAY_SOUND;
				}
			} else {
				wakeWay = ModelVariable.ALARM_WAKE_WAY_SHAKE;
			}
			
			wakeUri = mWakeMusicUri;
			encourageWords = mEncourageWordsTextView.getText().toString();
		}
	}
	
	private class AlarmDataComparator extends AlarmDataRecorder {
		
		/**
		 * @return true if there is change
		 */
		boolean isChangedCompareCurrentWithDataKeeped() {
			if (!mNameTextView.getText().toString().equals(name)) {
				return true;
			}
			if (Integer.parseInt(mHourTextView.getText().toString()) != hour) {
				return true;
			}
			if (Integer.parseInt(mMinuteTextView.getText().toString()) != minute) {
				return true;
			}
			if (mRepeatCheckBox.isChecked() != repeated) {
				return true;
			}
			
			List<Integer> days1 = new ArrayList<Integer>();
			for (int i = 0; i < 7; i++) {
				if (mDaysViews[i].isChecked()) {
					days1.add(i);
				}
			}
			if (!days1.equals(days)) {
				return true;
			}
			
			String wakeWay1;
			if (mSoundWayToggleView.isChecked()) {
				if (mShakeWayToggleView.isChecked()) {
					wakeWay1 = ModelVariable.ALARM_WAKE_WAY_SOUND_SHAKE;
				} else {
					wakeWay1 = ModelVariable.ALARM_WAKE_WAY_SOUND;
				}
			} else {
				wakeWay1 = ModelVariable.ALARM_WAKE_WAY_SHAKE;
			}
			if (!wakeWay1.equals(wakeWay)) {
				return true;
			}
			
			if ((wakeUri == null && mWakeMusicUri != null) 
					|| (wakeUri != null && !wakeUri.equals(mWakeMusicUri))) {
				return true;
			}
			if (!mEncourageWordsTextView.getText().toString().equals(encourageWords)) {
				return true;
			}
			
			return false;
		}
	}
}
