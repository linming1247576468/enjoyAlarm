package com.enjoyalarm.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.enjoyalarm.model.ModelUtil;
import com.enjoyalarm.model.ModelVariable;
import com.enjoyalarm.model.ReadingModel;
import com.enjoyalarm.model.WritingModel;
import com.enjoyalarm.view.ViewUtil.TimeEntry;
import com.scut.enjoyalarm.R;

public class AlarmSettingView extends ScrollView {

	private boolean isChanged;
	private int mAlarmId;
	private Context mContext;
	private View mMediaLayout;
	private View mInputLayout;
	private EditText mNameEditText;
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
	private int editHourOrMinute; //0.none 1.hour 2.minute
	private boolean clearWhenClickInput;
	private Handler mHandler;
	private int whatMessage;

	/**
	 * alarmId = -1 for setting a new alarm instead of reading from database
	 */
	public AlarmSettingView(Context context, int alarmId) {
		super(context);
		mContext = context;
		mAlarmId = alarmId;

		init();
		startUpdatingRemainTime();
	}

	/**
	 * setting a new alarm
	 */
	public AlarmSettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mAlarmId = -1;

		init();
		startUpdatingRemainTime();
	}
	
	public TimeEntry getRemainTime() {
		// get data from views
		String hourString = mHourTextView.getText().toString();
		String minuteString = mMinuteTextView.getText().toString();
		if (hourString.length() == 0 || minuteString.length() == 0) {// hadn't completed
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
		return ViewUtil.getRemainTime(nextDay, hour, minute,
				time.weekDay, time.hour, time.minute);
	}
	
	/**
	 * @return if user had touch the settings
	 */
	public boolean isChanged() {
		return isChanged;
	}
	
	public void save() {
		WritingModel model = new WritingModel(mContext);
		//name
		model.setName(mNameEditText.getText().toString());
		//time
		model.setTime(Integer.parseInt(mHourTextView.getText().toString()),
				Integer.parseInt(mMinuteTextView.getText().toString()));
		//days
		List<Integer> days = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			if (mDaysViews[i].isChecked()) {
				days.add(i);
			}
		}
		model.setDays(days);
		//repeated
		model.setRepeated(mRepeatCheckBox.isChecked());
		//wake way
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
		//wake music
		model.setWakeMusicUri(mWakeMusicTextView.getText().toString());
		//encourage words
		model.setText(mEncourageWordsTextView.getText().toString());
		//media
		
		
		//save
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

	
	private void startUpdatingRemainTime() {
		whatMessage = 0;
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == whatMessage) {
					updateRemainTime();
				}
			}
		};
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					mHandler.obtainMessage(whatMessage).sendToTarget();
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
		ViewGroup mainLayout = (ViewGroup) inflate(mContext,
				R.layout.alarm_setting_layout, null);
		addView(mainLayout);

		//find view
		mMediaLayout = mainLayout.findViewById(R.id.media_layout);
		mInputLayout = mainLayout.findViewById(R.id.input_layout);
		mNameEditText = (EditText) mainLayout.findViewById(R.id.name_et);
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
		mInputViews = new Button[12];
		ids = new int[] { R.id.input0, R.id.input1, R.id.input2, R.id.input3, R.id.input4,
				R.id.input5, R.id.input6, R.id.input7, R.id.input8, R.id.input9,
				R.id.input_back, R.id.input_done
		};
		for (int i = 0; i < 12; i++) {
			mInputViews[i] = (Button) mainLayout.findViewById(ids[i]);
		}
		
		//set something
		setViewAttrs();
		setViewListeners();
	}
	
	private void setViewAttrs() {
		mInputLayout.setVisibility(View.GONE);
		mNameEditText.setFocusable(false);
		mHourTextView.setTextSize(ViewVariable.UNEDIT_TIME_TEXT_SIZE);
		mMinuteTextView.setTextSize(ViewVariable.UNEDIT_TIME_TEXT_SIZE);
	}

	/**
	 * 
	 * @param mode 0.hour first  1.hour  2.minute first  3.minute  4.not editting
	 */
	private void changeEditTimeMode(int mode) {
		if (editHourOrMinute == 0) {//the input is hidden
			changeToInputMode();
		}
		
		switch (mode) {
		case 0: {
			editHourOrMinute = 1;
			clearWhenClickInput = true;
			mHourTextView.setBackgroundResource(R.drawable.time_circle_full);
			mMinuteTextView.setBackgroundDrawable(null);
			break;
		}

		case 1: {
			editHourOrMinute = 1;
			clearWhenClickInput = false;
			mHourTextView.setBackgroundResource(R.drawable.time_circle);
			mMinuteTextView.setBackgroundDrawable(null);
			break;
		}

		case 2: {
			editHourOrMinute = 2;
			clearWhenClickInput = true;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundResource(R.drawable.time_circle_full);
			break;
		}

		case 3: {
			editHourOrMinute = 2;
			clearWhenClickInput = false;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundResource(R.drawable.time_circle);
			break;
		}
		
		case 4: {
			editHourOrMinute = 0;
			clearWhenClickInput = false;
			mHourTextView.setBackgroundDrawable(null);
			mMinuteTextView.setBackgroundDrawable(null);
			changeToMediaMode();
			break;
		}
		}
	}
	
	private void setViewListeners() {
		mRenameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
				if (!mNameEditText.isFocused()) {
					mNameEditText.requestFocusFromTouch();
				}
			}
		});
		
		mHourTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
				changeEditTimeMode(0);
			}
		});
		
		mMinuteTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
				changeEditTimeMode(2);
			}
		});
		
		OnClickListener daysListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
				boolean oneSelected = false;
				for (ToggleView t: mDaysViews) {
					if (t != v && t.isChecked()) {
						oneSelected = true;
						break;
					}
				}
				
				if (oneSelected) {
					((ToggleView)v).toggle();
				}
				
				updateRemainTime();
			}
		};
		for(View dayView: mDaysViews) {
			dayView.setOnClickListener(daysListener);
		}
		
		OnClickListener inputListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView timeView;
				int maxNumber;
				if (editHourOrMinute == 1) {//hour
					timeView = mHourTextView;
					maxNumber = 24;
					
				} else {//minute
					timeView = mMinuteTextView;
					maxNumber = 60;
				}
				
				String nowText = timeView.getText().toString();
				String tag = (String)v.getTag();
				if (clearWhenClickInput) {
					if (editHourOrMinute == 1) {
						changeEditTimeMode(1);
					} else {
						changeEditTimeMode(3);
					}
					if (!getResources().getString(R.string.tag_done).equals(tag)) {
						nowText = "";//clear
					}
				}
				StringBuilder text = new StringBuilder(nowText);
				
				if (getResources().getString(R.string.tag_done).equals(tag)) {//done
					if (text.length() == 0) {
						text.append("00");
						
					} else if (text.length() == 1) {
						text.insert(0, '0');
					}
					
					timeView.setText(text);
					changeEditTimeMode(4);//close input
					
				} else if (getResources().getString(R.string.tag_back).equals(tag)) {//back
					if (text.length() > 0) {
						text.deleteCharAt(text.length() - 1);
					}
					timeView.setText(text);
					
				} else {//number
					if (text.length() < 2) {
						int number = Integer.parseInt(text.toString() + tag);
						if (number <= maxNumber) {
							text.append(tag);
							timeView.setText(text);
						}
						if (editHourOrMinute == 1 && text.length() == 2) {
							changeEditTimeMode(2);
						}
					}
				}
				
				updateRemainTime();
			}
		};
		for (View inputView: mInputViews) {
			inputView.setOnClickListener(inputListener);
		}
		
		mSoundWayToggleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
				if (mShakeWayToggleView.isSelected()) {
					mSoundWayToggleView.toggle();
					enableWakeMusic(mSoundWayToggleView.isSelected());
				}
			}
		});
		
		mShakeWayToggleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
				if (mSoundWayToggleView.isSelected()) {
					mShakeWayToggleView.toggle();
					shake();
				}
			}
		});
		
		mWakeMusicTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
			}
		});
		
		mEncourageWordsTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
			}
		});
		
		mAddMediaButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChanged = true;
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
		List<String> names = ModelUtil.getAlarmNames(mContext);
		int noNameAlarmsNumber = 0;
		String initNameString = getResources().getString(R.string.init_name);
		String regString = initNameString.replace("##", "\\d+");
		for (String name : names) {
			if (name.matches(regString)) {
				noNameAlarmsNumber++;
			}
		}
		mNameEditText.setText(initNameString.replace("##",
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
				|| (suggestHour == time.hour && suggestMinute >= time.minute)) {// today
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
				ModelVariable.DATA_TYPE_MUSIC_URI);
		String musicName;
		if (datas.size() == 0) {// get default
			musicName = getResources()
					.getString(R.string.init_wake_music_words);
		} else {
			musicName = ViewUtil.getMusicName(datas.get(0));
		}
		mWakeMusicTextView.setText(musicName);

		// set encourage words
		datas = ModelUtil.getSuggestData(mContext, ModelVariable.DATA_TYPE_TEXT);
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
		mNameEditText.setText(model.getName());

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
		if (ModelVariable.ALARM_WAKE_WAY_SOUND.equals(wakeWay)) {//sound
			mSoundWayToggleView.setChecked(true);
			enableWakeMusic(true);
			
		} else if (ModelVariable.ALARM_WAKE_WAY_SHAKE.equals(wakeWay)) {//shake
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
		}
	}
}