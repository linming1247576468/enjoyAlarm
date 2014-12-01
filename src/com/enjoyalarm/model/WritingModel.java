package com.enjoyalarm.model;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WritingModel {

	private boolean debug = true;
	private String debugTag = "writing model";
	private Context mContext;
	private DatabaseHelper mDatabaseHelper;
	private int mId;
	private String mName;
	private String mTime;
	private String mDays;
	private String mWakeWay;
	private String mWakeMusicUri;
	private String mText;
	private String mMediaWay;
	private String mMusicUri;
	private String mPhotoUri;
	private String mVideoUri;
	private String mAppPackageName;
	private String mFriendNames;
	private String mFriendPhones;
	private String mSendText;
	private boolean mRepeat;

	public WritingModel(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(context);
	}

	/**
	 * required
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * required
	 */
	public void setTime(int hour, int minute) {
		mTime = hour + ":" + minute;
	}

	/**
	 * required days [0-6]
	 */
	public void setDays(List<Integer> days) {
		StringBuilder dayString = new StringBuilder();
		for (Integer day : days) {
			dayString.append(day);
		}
		mDays = dayString.toString();
	}

	/**
	 * required
	 * 
	 * @param way
	 *            see Variable.ALARM_WAKE_WAY_**
	 */
	public void setWakeWay(String way) {
		mWakeWay = way;
	}

	/**
	 * required
	 */
	public void setWakeMusicUri(String uri) {
		mWakeMusicUri = uri;
	}

	/**
	 * required
	 */
	public void setText(String text) {
		mText = text;
	}

	/**
	 * required
	 */
	public void setRepeated(boolean repeated) {
		mRepeat = repeated;
	}
	
	/**
	 * 
	 * @param way
	 *            see Varaible.ALARM_MEDIA_WAY_**
	 */
	public void setMediaWay(String way) {
		mMediaWay = way;
	}

	public void setMusicUri(String uri) {
		mMusicUri = uri;
	}

	public void setPhotoUri(String uri) {
		mPhotoUri = uri;
	}

	public void setVideoUri(String uri) {
		mVideoUri = uri;
	}

	public void setAppPackageName(String packageName) {
		mAppPackageName = packageName;
	}

	public void setFriendsData(List<String> names, List<String> phones) {
		mFriendNames = ModelUtil.getFriendNamesString(names);
		mFriendPhones = ModelUtil.getFriendPhonesString(phones);
	}

	public void setSendText(String text) {
		mSendText = text;
	}

	/**
	 * create a new alarm and save
	 * 
	 * @return the id of the new alarm
	 */
	public int createAndSave() {
		// get the next id
		SharedPreferences preference = mContext.getSharedPreferences(
				ModelVariable.PREFERENCE_NAME, 0);
		mId = preference.getInt(ModelVariable.PREFERENCE_NEXT_ID, 0);

		// write to database
		SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ModelVariable.ALARM_COLUMN1_ID, mId);
		values.put(ModelVariable.ALARM_COLUMN2_NAME, mName);
		values.put(ModelVariable.ALARM_COLUMN3_TIME, mTime);
		values.put(ModelVariable.ALARM_COLUMN4_DAYS, mDays);
		values.put(ModelVariable.ALARM_COLUMN6_WAKE_WAY, mWakeWay);
		if (mRepeat) {
			values.put(ModelVariable.ALARM_COLUMN5_IS_REPEAT, ModelVariable.ALARM_YES);
		} else {
			values.put(ModelVariable.ALARM_COLUMN5_IS_REPEAT, ModelVariable.ALARM_NO);
		}
		if (mWakeMusicUri != null) {
			values.put(ModelVariable.ALARM_COLUMN7_WAKE_MUSIC_URI, mWakeMusicUri);
		}
		if (mText != null) {
			values.put(ModelVariable.ALARM_COLUMN8_TEXT, mText);
		}
		if (mMediaWay != null) {
			values.put(ModelVariable.ALARM_COLUMN9_MEDIA_WAY, mMediaWay);
		}
		if (mMusicUri != null) {
			values.put(ModelVariable.ALARM_COLUMN10_MUSIC_URI, mMusicUri);
		}
		if (mPhotoUri != null) {
			values.put(ModelVariable.ALARM_COLUMN11_PHOTO_URI, mPhotoUri);
		}
		if (mVideoUri != null) {
			values.put(ModelVariable.ALARM_COLUMN12_VIDEO_URI, mVideoUri);
		}
		if (mAppPackageName != null) {
			values.put(ModelVariable.ALARM_COLUMN13_APP_PACKAGE_NAME,
					mAppPackageName);
		}
		if (mFriendNames != null) {
			values.put(ModelVariable.ALARM_COLUMN14_FRIEND_NAMES, mFriendNames);
		}
		if (mFriendPhones != null) {
			values.put(ModelVariable.ALARM_COLUMN15_FRIEND_PHONES, mFriendPhones);
		}
		if (mSendText != null) {
			values.put(ModelVariable.ALARM_COLUMN16_SEND_TEXT, mSendText);
		}

		database.insert(ModelVariable.ALARM_TABLE_NAME, null, values);
		database.close();
		if (debug) {
			Log.i(debugTag, "insert: " + values.toString());
		}

		// update the next id
		preference.edit().putInt(ModelVariable.PREFERENCE_NEXT_ID, mId + 1).commit();

		return mId;
	}

	/**
	 * update the existing alarm	notice:temp alarm must pass -1 here
	 */
	public void update(int alarmId) {
		mId = alarmId;
		String tableName;
		if (mId == -1) {
			tableName = ModelVariable.TEMP_ALARM_TABLE_NAME;
		} else {
			tableName = ModelVariable.ALARM_TABLE_NAME;
		}
		SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ModelVariable.ALARM_COLUMN2_NAME, mName);
		values.put(ModelVariable.ALARM_COLUMN3_TIME, mTime);
		values.put(ModelVariable.ALARM_COLUMN4_DAYS, mDays);
		values.put(ModelVariable.ALARM_COLUMN6_WAKE_WAY, mWakeWay);
		if (mRepeat) {
			values.put(ModelVariable.ALARM_COLUMN5_IS_REPEAT, ModelVariable.ALARM_YES);
		} else {
			values.put(ModelVariable.ALARM_COLUMN5_IS_REPEAT, ModelVariable.ALARM_NO);
		}
		if (mWakeMusicUri != null) {
			values.put(ModelVariable.ALARM_COLUMN7_WAKE_MUSIC_URI, mWakeMusicUri);
		}
		if (mText != null) {
			values.put(ModelVariable.ALARM_COLUMN8_TEXT, mText);
		}
		if (mMediaWay != null) {
			values.put(ModelVariable.ALARM_COLUMN9_MEDIA_WAY, mMediaWay);
		}
		if (mMusicUri != null) {
			values.put(ModelVariable.ALARM_COLUMN10_MUSIC_URI, mMusicUri);
		}
		if (mPhotoUri != null) {
			values.put(ModelVariable.ALARM_COLUMN11_PHOTO_URI, mPhotoUri);
		}
		if (mVideoUri != null) {
			values.put(ModelVariable.ALARM_COLUMN12_VIDEO_URI, mVideoUri);
		}
		if (mAppPackageName != null) {
			values.put(ModelVariable.ALARM_COLUMN13_APP_PACKAGE_NAME,
					mAppPackageName);
		}
		if (mFriendNames != null) {
			values.put(ModelVariable.ALARM_COLUMN14_FRIEND_NAMES, mFriendNames);
		}
		if (mFriendPhones != null) {
			values.put(ModelVariable.ALARM_COLUMN15_FRIEND_PHONES, mFriendPhones);
		}
		if (mSendText != null) {
			values.put(ModelVariable.ALARM_COLUMN16_SEND_TEXT, mSendText);
		}
		database.update(tableName, values, ModelVariable.ALARM_COLUMN1_ID
				+ "=?", new String[] { String.valueOf(alarmId) });
		database.close();
	}
}
