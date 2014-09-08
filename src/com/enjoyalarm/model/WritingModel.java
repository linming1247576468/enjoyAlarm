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
	private String mText;
	private String mMusicUri;
	private String mPhotoUri;
	private String mVideoUri;
	private boolean mRepeated;

	
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
	 * required
	 */
	public void setDay(List<Integer> days) {
		StringBuilder dayString = new StringBuilder();
		for (Integer day : days) {
			dayString.append(day);
		}
		mDays = dayString.toString();
	}

	public void setText(String text) {
		mText = text;
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
	
	public void setRepeated(boolean repeated) {
		mRepeated = repeated;
	}

	public int save() {
		// get the next id
		SharedPreferences preference = mContext.getSharedPreferences(
				Variable.PREFERENCE_NAME, 0);
		mId = preference.getInt(Variable.PREFERENCE_NEXT_ID, 0);

		// write to database
		SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Variable.ALARM_COLUMN1_ID, mId);
		values.put(Variable.ALARM_COLUMN2_NAME, mName);
		values.put(Variable.ALARM_COLUMN3_TIME, mTime);
		values.put(Variable.ALARM_COLUMN4_DAYS, mDays);
		if (mText != null) {
			values.put(Variable.ALARM_COLUMN5_TEXT, mText);
		}
		if (mMusicUri != null) {
			values.put(Variable.ALARM_COLUMN6_MUSIC_URI, mMusicUri);
		}
		if (mPhotoUri != null) { 
			values.put(Variable.ALARM_COLUMN7_PHOTO_URI, mPhotoUri);
		}
		if (mVideoUri != null) {
			values.put(Variable.ALARM_COLUMN8_VIDEO_URI, mVideoUri);
		}
		if (mRepeated) {
			values.put(Variable.ALARM_COLUMN9_REPEATED, Variable.ALARM_REPEATED_YES);
		} else {
			values.put(Variable.ALARM_COLUMN9_REPEATED, Variable.ALARM_REPEATED_NO);
		}
		database.insert(Variable.ALARM_TABLE_NAME, null, values);
		database.close();
		if (debug) {
			Log.i(debugTag,"insert: " + values.toString());
		}
		
		//update the next id
		preference
			.edit()
				.putInt(Variable.PREFERENCE_NEXT_ID, mId + 1)
					.commit();
		
		return mId;
	}

}
