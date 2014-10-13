package com.enjoyalarm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 
 * problems may occur: 
 * 		1.in sql "where ? = ?", if ? is varchar type, should I change the second ? into '?';
 *      2.in ContentValues.put(String,String), should I change the second String into '..';
 *
 */
public class ModelUtil {

	private static boolean debug = true;

	static class BasicInfoUnit {
		int id;
		String name;
		String time;
		String days;

		public BasicInfoUnit(int id, String name, String time, String days) {
			this.id = id;
			this.name = name;
			this.time = time;
			this.days = days;
		}
	}

	public static List<BasicInfoUnit> getAlarmsBasicInfo(Context context) {
		List<BasicInfoUnit> resultList = new ArrayList<BasicInfoUnit>();
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ?,?,? from ?", new String[] {
				ModelVariable.ALARM_COLUMN1_ID, ModelVariable.ALARM_COLUMN2_NAME,
				ModelVariable.ALARM_COLUMN3_TIME, ModelVariable.ALARM_COLUMN4_DAYS,
				ModelVariable.ALARM_TABLE_NAME });
		while (cursor.moveToNext()) {
			resultList.add(new BasicInfoUnit(cursor.getInt(0), cursor
					.getString(1), cursor.getString(2), cursor.getString(3)));
			if (debug) {
				Log.i("getAlarmBasicInfo", "id:" + cursor.getInt(0) + ", name:"
						+ cursor.getString(1));
			}
		}
		cursor.close();
		db.close();

		return resultList;
	}

	public static void deleteAlarm(Context context, int alarmId) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete(ModelVariable.ALARM_TABLE_NAME,
				ModelVariable.ALARM_COLUMN1_ID + " = ?",
				new String[] { String.valueOf(alarmId) });
		if (debug) {
			Log.i("deleteAlarm", "rows affected number:" + result);
		}
		db.close();
	}

	public static void recordTime(Context context, int hour, String time) {

		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db
				.rawQuery("select ? from ? where ? = ?", new String[] {
						ModelVariable.TIME_COLUMN1_NOW_HOUR,
						ModelVariable.TIME_TABLE_NAME,
						ModelVariable.TIME_COLUMN1_NOW_HOUR, String.valueOf(hour), });

		if (cursor.moveToFirst()) { // update
			ContentValues values = new ContentValues();
			values.put(ModelVariable.TIME_COLUMN1_NOW_HOUR, hour);
			values.put(ModelVariable.TIME_COLUMN2_SET_TIME, time);
			int result = db.update(ModelVariable.TIME_TABLE_NAME, values,
					ModelVariable.TIME_COLUMN1_NOW_HOUR + " = ?",
					new String[] { String.valueOf(hour) });
			if (debug) {
				Log.i("recordTime", "update result:" + result);
			}

		} else { // insert
			ContentValues values = new ContentValues();
			values.put(ModelVariable.TIME_COLUMN1_NOW_HOUR, hour);
			values.put(ModelVariable.TIME_COLUMN2_SET_TIME, time);
			long resultId = db.insert(ModelVariable.TIME_TABLE_NAME, null, values);
			if (debug) {
				Log.i("recordTime", "insert result id:" + resultId);
			}
		}

		cursor.close();
		db.close();
	}

	/**
	 * record text,musicUri, photoUri, videoUri and so on according to the type of
	 * Variable.DAIA_TYPE_**
	 */
	public static void recordData(Context context, String data, String type) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select ? from ? where ? = '?' and ? = '?'", new String[] {
						ModelVariable.DATA_COLUMN3_COUNT,
						ModelVariable.DATA_TABLE_NAME, ModelVariable.DATA_COLUMN1_TYPE,
						type, ModelVariable.DATA_COLUMN2_DATA, data });

		if (cursor.moveToFirst()) { // update
			int count = cursor.getInt(0);
			count++;
			ContentValues values = new ContentValues();
			values.put(ModelVariable.DATA_COLUMN3_COUNT, count);
			int result = db.update(ModelVariable.DATA_TABLE_NAME, values,
					"? = '?' and ? = '?'", new String[] {
							ModelVariable.DATA_COLUMN1_TYPE, type,
							ModelVariable.DATA_COLUMN2_DATA, data });
			if (debug) {
				Log.i("recordMediaData", "type:" + type + "  update result:"
						+ result);
			}

		} else { // insert
			ContentValues values = new ContentValues();
			values.put(ModelVariable.DATA_COLUMN1_TYPE, type);
			values.put(ModelVariable.DATA_COLUMN2_DATA, data);
			values.put(ModelVariable.DATA_COLUMN3_COUNT, 1);
			long resultId = db.insert(ModelVariable.DATA_TABLE_NAME, null, values);
			if (debug) {
				Log.i("recordMediaData", "type:" + type + "  insert result id:"
						+ resultId);
			}
		}

		cursor.close();
		db.close();
	}

	/**
	 * return the suggest time or null
	 */
	public static String getSuggestTime(Context context, int hour) {
		// find records equal to hour
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ? from ? where ? = ?",
				new String[] { ModelVariable.TIME_COLUMN2_SET_TIME,
						ModelVariable.TIME_TABLE_NAME,
						ModelVariable.TIME_COLUMN1_NOW_HOUR, String.valueOf(hour) });
		if (cursor.moveToFirst()) {
			String time = cursor.getString(0);
			cursor.close();
			db.close();
			return time;
		}

		// find records equal to hour-1
		int preHour = (hour - 1 + 24) % 24;
		cursor = db.rawQuery("select ? from ? where ? = ?", new String[] {
				ModelVariable.TIME_COLUMN2_SET_TIME, ModelVariable.TIME_TABLE_NAME,
				ModelVariable.TIME_COLUMN1_NOW_HOUR, String.valueOf(preHour) });
		if (cursor.moveToFirst()) {
			String time = cursor.getString(0);
			cursor.close();
			db.close();
			return time;
		}

		// find records equal to hour+1
		int nextHour = (hour + 1) % 24;
		cursor = db.rawQuery("select ? from ? where ? = ?", new String[] {
				ModelVariable.TIME_COLUMN2_SET_TIME, ModelVariable.TIME_TABLE_NAME,
				ModelVariable.TIME_COLUMN1_NOW_HOUR, String.valueOf(nextHour) });
		if (cursor.moveToFirst()) {
			String time = cursor.getString(0);
			cursor.close();
			db.close();
			return time;
		}

		return null;
	}

	/**
	 * return the top three data if possible
	 */
	public static List<String> getSuggestData(Context context, String type) {
		List<String> resultList = new ArrayList<String>();
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select ? from ? where ? = ? order by ? desc", new String[] {
						ModelVariable.DATA_COLUMN2_DATA, ModelVariable.DATA_TABLE_NAME,
						ModelVariable.DATA_COLUMN1_TYPE, type,
						ModelVariable.DATA_COLUMN3_COUNT });
		int number = 0;
		while (number++ < 3 && cursor.moveToNext()) {
			resultList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();

		return resultList;
	}

	public static List<String> getAlarmNames(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ? from ?", new String[]{ModelVariable.ALARM_COLUMN2_NAME, ModelVariable.ALARM_TABLE_NAME});
		List<String> result = new ArrayList<String>();
		while (cursor.moveToNext()) {
			result.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
	public static int getHourFromTime(String time) {
		return Integer.parseInt(time.substring(0, time.indexOf(':')));
	}
	
	public static int getMinuteFromTime(String time) {
		return Integer.parseInt(time.substring(time.indexOf(':') + 1));
	}
	
	public static String generateTime(int hour, int minute) {
		return hour + ":" + minute;
	}
	
	public static String getFriendNamesString(List<String> names) {
		StringBuilder result = new StringBuilder();
		for (String name : names) {
			result.append(name).append(',');
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	public static List<String> getFriendNamesList(String names) {
		return Arrays.asList(names.split(","));
	}

	public static String getFriendPhonesString(List<String> phones) {
		StringBuilder result = new StringBuilder();
		for (String phone : phones) {
			result.append(phone).append(',');
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	public static List<String> getFriendPhonesList(String phones) {
		return Arrays.asList(phones.split(","));
	}
}
