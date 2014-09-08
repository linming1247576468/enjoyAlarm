package com.enjoyalarm.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

public class AlarmUtils {

	public static void settingAlarm(Context context, String time, int alarmId) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Service.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();//setting time
		Intent intent = new Intent(context, AlarmUtils.class);
		intent.putExtra("", alarmId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				alarmId, intent, 0);// same request id will not set multi-alarm
									// for same intent
		/**
		 * in target 19, alarmManager.set() will set the alarm inexact,use
		 * alarmManager.setExcact() instead
		 */
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pendingIntent);
	}
}
