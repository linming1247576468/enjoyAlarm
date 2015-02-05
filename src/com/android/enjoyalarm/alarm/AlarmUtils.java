package com.android.enjoyalarm.alarm;

import java.util.Calendar;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.android.enjoyalarm.AlarmWakeActivity;

public class AlarmUtils {

	public final static String ALARM_ID_INTENT = "alarm_id_intent";

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void settingAlarm(Context context, int alarmId, int year,
			int month, int day, int hour, int minute) {
		// setting time
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute);
		
		//setting alarm
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmWakeActivity.class);// launch
																		// activity
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(AlarmUtils.ALARM_ID_INTENT, alarmId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				alarmId, intent, 0);// same request id will not set multi-alarm
									// for same intent
		/**
		 * in target 19, alarmManager.set() will set the alarm inexactly,use
		 * alarmManager.setExcact() instead
		 */
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pendingIntent);

		} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pendingIntent);
		}

	}
	
	public static void cancelAlarm(Context context, int alarmId) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmWakeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				alarmId, intent, 0);
		alarmManager.cancel(pendingIntent);
	}
}
