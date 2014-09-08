package com.enjoyalarm.model;

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
	private String mText;
	private String mMusicUri;
	private String mPhotoUri;
	private String mVideoUri;
	private boolean mRepeated;

	private void readingData() {
		SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from ? where ? = ?",
				new String[] { Variable.ALARM_TABLE_NAME,
						Variable.ALARM_COLUMN1_ID, String.valueOf(mId) });
		if (cursor.moveToFirst()) {
			mName = cursor.getString(1);
			mTime = cursor.getString(2);
			mDays = cursor.getString(3);
			
			if (cursor.isNull(4)) {
				mText = cursor.getString(4);
			}
			if (cursor.isNull(5)) {
				mMusicUri = cursor.getString(5);
			}
			if (cursor.isNull(6)) {
				mPhotoUri = cursor.getString(6);
			}
			if (cursor.isNull(7)) {
				mVideoUri = cursor.getString(7);
			}
			if (Variable.ALARM_REPEATED_YES.equals(cursor.getString(8))) {
				mRepeated = true;
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

	public String getDay() {
		return mDays;
	}

	public String getText() {
		return mText;
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
	
	public boolean getRepeated() {
		return mRepeated;
	}

}
