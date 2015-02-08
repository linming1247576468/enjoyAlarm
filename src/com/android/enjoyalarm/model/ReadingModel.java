package com.android.enjoyalarm.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ReadingModel {

	private boolean debug = true;
	private String debugTag = "reading model";
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

	private void readingData() {
		String tableName;
		if (mId == -1) {
			tableName = ModelVariable.TEMP_ALARM_TABLE_NAME;
		} else {
			tableName = ModelVariable.ALARM_TABLE_NAME;
		}
		SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = database.query(tableName,
				new String[] { "*" }, ModelVariable.ALARM_COLUMN1_ID + "=?",
				new String[] { String.valueOf(mId) }, null, null, null);
		if (cursor.moveToFirst()) {
			mName = cursor.getString(1);
			mTime = cursor.getString(2);
			mDays = cursor.getString(3);
			mWakeWay = cursor.getString(5);

			if (ModelVariable.ALARM_YES.equals(cursor.getString(4))) {
				mRepeat = true;
			}
			if (!cursor.isNull(6)) {
				mWakeMusicUri = cursor.getString(6);
			}
			if (!cursor.isNull(7)) {
				mText = cursor.getString(7);
			}
			if (!cursor.isNull(8)) {
				mMediaWay = cursor.getString(8);
			}
			if (!cursor.isNull(9)) {
				mMusicUri = cursor.getString(9);
			}
			if (!cursor.isNull(10)) {
				mPhotoUri = cursor.getString(10);
			}
			if (!cursor.isNull(11)) {
				mVideoUri = cursor.getString(11);
			}
			if (!cursor.isNull(12)) {
				mAppPackageName = cursor.getString(12);
			}
			if (!cursor.isNull(13)) {
				mFriendNames = cursor.getString(13);
			}
			if (!cursor.isNull(14)) {
				mFriendPhones = cursor.getString(14);
			}
			if (!cursor.isNull(15)) {
				mSendText = cursor.getString(15);
			}

		}
		cursor.close();
		database.close();

		if (debug) {
			Log.i(debugTag, "rawQuery: " + mName);
		}
	}

	public ReadingModel(Context context, int alarmId) {
		mDatabaseHelper = new DatabaseHelper(context);
		mId = alarmId;

		readingData();
	}

	public String getName() {
		return mName;
	}

	public String getTime() {
		return mTime;
	}

	/**
	 * 
	 * @return days like 0123456,146 and so on
	 */
	public List<Integer> getDays() {
		List<Integer> result = new ArrayList<Integer>();
		char[] chars = mDays.toCharArray();
		for (char c : chars) {
			result.add(c - 48);
		}
		return result;
	}

	public boolean getRepeat() {
		return mRepeat;
	}

	public String getWakeWay() {
		return mWakeWay;
	}
	
	public boolean isWakeWayMusic() {
		if (ModelVariable.ALARM_WAKE_WAY_SOUND.equals(mWakeWay)
				|| ModelVariable.ALARM_WAKE_WAY_SOUND_SHAKE.equals(mWakeWay)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isWakeWayShake() {
		if (ModelVariable.ALARM_WAKE_WAY_SHAKE.equals(mWakeWay)
				|| ModelVariable.ALARM_WAKE_WAY_SOUND_SHAKE.equals(mWakeWay)) {
			return true;
		} else {
			return false;
		}
	}

	public String getWakeMusicUri() {
		return mWakeMusicUri;
	}

	public String getText() {
		return mText;
	}

	public String getMediaWay() {
		return mMediaWay;
	}

	public String getMusicUri() {
		return mMusicUri;
	}

	public String getPhotoUri() {
		return mPhotoUri;
	}

	public String getVideoUri() {
		return mVideoUri;
	}

	public String getAppPackageName() {
		return mAppPackageName;
	}

	public List<String> getFriendNames() {
		return ModelUtil.getFriendNamesList(mFriendNames);
	}

	public List<String> getFriendPhones() {
		return ModelUtil.getFriendPhonesList(mFriendPhones);
	}

	public String getSendText() {
		return mSendText;
	}
}
