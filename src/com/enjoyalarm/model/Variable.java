package com.enjoyalarm.model;

public class Variable {

	//sharedPreference
	static final String PREFERENCE_NAME = "enjoy_alarm_preference";
	static final String PREFERENCE_NEXT_ID = "next_id";
	
	//alarm table
	final static String ALARM_TABLE_NAME = "alarm_table";
	final static String ALARM_COLUMN1_ID = "id";
	final static String ALARM_COLUMN2_NAME = "name";
	final static String ALARM_COLUMN3_TIME = "time";
	final static String ALARM_COLUMN4_DAYS = "days";
	final static String ALARM_COLUMN5_REPEAT = "repeat";
	final static String ALARM_COLUMN6_WAKE_WAY = "wake_way";
	final static String ALARM_COLUMN7_WAKE_MUSIC_URI = "wake_music_uri";
	final static String ALARM_COLUMN8_TEXT = "text";
	final static String ALARM_COLUMN9_MEDIA_WAY = "media_way";
	final static String ALARM_COLUMN10_MUSIC_URI = "music_uri";
	final static String ALARM_COLUMN11_PHOTO_URI = "photo_uri";
	final static String ALARM_COLUMN12_VIDEO_URI = "video_uri";
	final static String ALARM_COLUMN13_APP_PACKAGE_NAME = "app_package_name";
	final static String ALARM_COLUMN14_FRIEND_NAMES = "friend_names";
	final static String ALARM_COLUMN15_FRIEND_PHONES = "friend_phones";
	final static String ALARM_COLUMN16_SEND_TEXT = "send_text";
	
	final static String ALARM_REPEAT_YES = "1";
	final static String ALARM_REPEAT_NO = "2";
	final static String ALARM_WAKE_WAY_SOUND = "1";
	final static String ALARM_WAKE_WAY_SHAKE = "2";
	final static String ALARM_WAKE_WAY_SOUND_SHAKE = "3";
	final static String ALARM_MEDIA_WAY_PHOTO = "1";
	final static String ALARM_MEDIA_WAY_VIDEO = "2";
	final static String ALARM_MEDIA_WAY_APP = "3";
	final static String ALARM_MEDIA_WAY_SOCIAL = "4";

	
	//time table
	final static String TIME_TABLE_NAME = "time_table";
	final static String TIME_COLUMN1_NOW_HOUR = "hour";
	final static String TIME_COLUMN2_SET_TIME = "time";
	
	
	//data table, which is used for suggestions
	final static String DATA_TABLE_NAME = "data_table";
	final static String DATA_COLUMN1_TYPE = "type";
	final static String DATA_COLUMN2_DATA = "data";
	final static String DATA_COLUMN3_COUNT = "count";
	
	final static String DATA_TYPE_WAKE_MUSIC_URI = "1";
	final static String DATA_TYPE_TEXT = "2";
	final static String DATA_TYPE_MUSIC_URI = "3";
	final static String DATA_TYPE_PHOTO_URI = "4";
	final static String DATA_TYPE_VIDEO_URI = "5";
	final static String DATA_TYPE_APP_PACKAGE_NAME = "6";
	final static String DATA_TYPE_FRIEND_NAME = "7";
	final static String DATA_TYPE_SEND_TEXT = "8";
	
}
