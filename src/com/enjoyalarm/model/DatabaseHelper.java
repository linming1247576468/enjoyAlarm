package com.enjoyalarm.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "alarm_database";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String sqlString = "create table " + Variable.ALARM_TABLE_NAME + "("
					+ Variable.ALARM_COLUMN1_ID + " integer primary key,"
					+ Variable.ALARM_COLUMN2_NAME + " varchar,"
					+ Variable.ALARM_COLUMN3_TIME + " varchar,"
					+ Variable.ALARM_COLUMN4_DAYS + " varchar,"
					+ Variable.ALARM_COLUMN5_IS_REPEAT + " varchar,"
					+ Variable.ALARM_COLUMN6_WAKE_WAY + " varvhar,"
					+ Variable.ALARM_COLUMN7_WAKE_MUSIC_URI + " varchar,"
					+ Variable.ALARM_COLUMN8_TEXT + " varchar,"
					+ Variable.ALARM_COLUMN9_MEDIA_WAY + " varchar,"
					+ Variable.ALARM_COLUMN10_MUSIC_URI + " varchar,"
					+ Variable.ALARM_COLUMN11_PHOTO_URI + " varchar,"
					+ Variable.ALARM_COLUMN12_VIDEO_URI + " varchar,"
					+ Variable.ALARM_COLUMN13_APP_PACKAGE_NAME + " varchar,"
					+ Variable.ALARM_COLUMN14_FRIEND_NAMES + " varchar,"
					+ Variable.ALARM_COLUMN15_FRIEND_PHONES + " varchar,"
					+ Variable.ALARM_COLUMN16_SEND_TEXT + " varchar)";
			db.execSQL(sqlString);

			sqlString = "create table " + Variable.TIME_TABLE_NAME + "("
					+ Variable.TIME_COLUMN1_NOW_HOUR + " varchar,"
					+ Variable.TIME_COLUMN2_SET_TIME + " varchar)";
			db.execSQL(sqlString);

			sqlString = "create table " + Variable.DATA_TABLE_NAME + "("
					+ Variable.DATA_COLUMN1_TYPE + " varchar,"
					+ Variable.DATA_COLUMN2_DATA + " varchar,"
					+ Variable.DATA_COLUMN3_COUNT + " integer)";
			db.execSQL(sqlString);
			
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL("drop table if exists " + Variable.ALARM_TABLE_NAME + ","
					+ Variable.TIME_TABLE_NAME + "," + Variable.DATA_TABLE_NAME);
			
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		onCreate(db);
	}
}
