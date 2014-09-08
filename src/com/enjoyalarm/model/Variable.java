package com.enjoyalarm.model;

public class Variable {

	//sharedPreference
	static final String PREFERENCE_NAME = "enjoy_alarm_preference";
	static final String PREFERENCE_NEXT_ID = "next_id";
	
	//alarm table
	final static String ALARM_TABLE_NAME = "alarm_table";
	final static String ALARM_COLUMN1_ID = "_id";
	final static String ALARM_COLUMN2_NAME = "name";
	final static String ALARM_COLUMN3_TIME = "time";
	final static String ALARM_COLUMN4_DAYS = "days";
	final static String ALARM_COLUMN5_TEXT = "text";
	final static String ALARM_COLUMN6_MUSIC_URI = "music_uri";
	final static String ALARM_COLUMN7_PHOTO_URI = "photo_uri";
	final static String ALARM_COLUMN8_VIDEO_URI = "video_uri";
	final static String ALARM_COLUMN9_REPEATED = "repeated";
	final static String ALARM_REPEATED_YES = "yes";
	final static String ALARM_REPEATED_NO = "no";
	
	//time table
	final static String TIME_TABLE_NAME = "time_table";
	final static String TIME_COLUMN1_NOW_HOUR = "hour";
	final static String TIME_COLUMN2_SET_TIME = "time";
	
	//media table
	final static String MEDIA_TABLE_NAME = "media_table";
	final static String MEDIA_COLUMN1_TYPE = "type";
	final static String MEDIA_COLUMN2_DATA = "data";
	final static String MEDIA_COLUMN3_COUNT = "count";
	
	//media table column1's type
	final static String MEDIA_TYPE_TEXT = "1";
	final static String MEDIA_TYPE_MUSIC_URI = "2";
	final static String MEDIA_TYPE_PHOTO_URI = "3";
	final static String MEDIA_TYPE_VIDEO_URI = "4";
	
}
