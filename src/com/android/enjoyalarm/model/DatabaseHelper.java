package com.android.enjoyalarm.model;

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
			String baseString = "("
					+ ModelVariable.ALARM_COLUMN1_ID + " integer primary key,"
					+ ModelVariable.ALARM_COLUMN2_NAME + " varchar,"
					+ ModelVariable.ALARM_COLUMN3_TIME + " varchar,"
					+ ModelVariable.ALARM_COLUMN4_DAYS + " varchar,"
					+ ModelVariable.ALARM_COLUMN5_IS_REPEAT + " varchar,"
					+ ModelVariable.ALARM_COLUMN6_WAKE_WAY + " varvhar,"
					+ ModelVariable.ALARM_COLUMN7_WAKE_MUSIC_URI + " varchar,"
					+ ModelVariable.ALARM_COLUMN8_TEXT + " varchar,"
					+ ModelVariable.ALARM_COLUMN9_MEDIA_WAY + " varchar,"
					+ ModelVariable.ALARM_COLUMN10_MUSIC_URI + " varchar,"
					+ ModelVariable.ALARM_COLUMN11_PHOTO_URI + " varchar,"
					+ ModelVariable.ALARM_COLUMN12_VIDEO_URI + " varchar,"
					+ ModelVariable.ALARM_COLUMN13_APP_PACKAGE_NAME + " varchar,"
					+ ModelVariable.ALARM_COLUMN14_FRIEND_NAMES + " varchar,"
					+ ModelVariable.ALARM_COLUMN15_FRIEND_PHONES + " varchar,"
					+ ModelVariable.ALARM_COLUMN16_SEND_TEXT + " varchar)";
			
			String sqlString = "create table " + ModelVariable.ALARM_TABLE_NAME + baseString;
			db.execSQL(sqlString);
			
			sqlString = "create table " + ModelVariable.TEMP_ALARM_TABLE_NAME + baseString;
			db.execSQL(sqlString);
			db.execSQL("insert into " + ModelVariable.TEMP_ALARM_TABLE_NAME
					+ " (" + ModelVariable.ALARM_COLUMN1_ID + ")"
					+ "values(-1)");

			sqlString = "create table " + ModelVariable.TIME_TABLE_NAME + "("
					+ ModelVariable.TIME_COLUMN1_NOW_HOUR + " varchar,"
					+ ModelVariable.TIME_COLUMN2_SET_TIME + " varchar)";
			db.execSQL(sqlString);

			sqlString = "create table " + ModelVariable.DATA_TABLE_NAME + "("
					+ ModelVariable.DATA_COLUMN1_TYPE + " varchar,"
					+ ModelVariable.DATA_COLUMN2_DATA + " varchar,"
					+ ModelVariable.DATA_COLUMN3_COUNT + " integer)";
			db.execSQL(sqlString);
			
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL("drop table if exists " + ModelVariable.ALARM_TABLE_NAME + ","
					+ ModelVariable.TIME_TABLE_NAME + "," + ModelVariable.DATA_TABLE_NAME);
			
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		onCreate(db);
	}
}
