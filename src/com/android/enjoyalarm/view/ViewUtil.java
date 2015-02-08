package com.android.enjoyalarm.view;

import android.content.Context;

import com.android.enjoyalarm.R;

public class ViewUtil {

	/**
	 * get the double-bit String like '15','05'
	 */
	public static String getDoubleBitStringForTime(int hourOrMinute) {
		if (hourOrMinute < 10) {
			return "0" + String.valueOf(hourOrMinute);
		} else {
			return String.valueOf(hourOrMinute);
		}
	}

	public static long getRemainTimeRTC(int targetWeekDay, int targetHour,
			int targetMinute, int nowWeekDay, int nowHour, int nowMinute) {
		TimeEntry time = getRemainTime(targetWeekDay, targetHour, targetMinute, nowWeekDay, nowHour, nowMinute);
		return time.day * 24 * 60 * 60
				* 1000 + time.hour * 60 * 60 * 1000 + time.minute * 60 * 1000;
	}
	
	public static TimeEntry getRemainTime(int targetWeekDay, int targetHour,
			int targetMinute, int nowWeekDay, int nowHour, int nowMinute) {
		int totalMinute = ((((targetWeekDay + 7) * 24 + targetHour) * 60 + targetMinute) - ((nowWeekDay * 24 + nowHour) * 60 + nowMinute))
				% (7 * 24 * 60);

		return new TimeEntry(totalMinute / (24 * 60),
				(totalMinute % (24 * 60)) / 60, (totalMinute % (24 * 60)) % 60);
	}

	public static class TimeEntry {
		public int day;
		public int hour;
		public int minute;

		public TimeEntry(int day, int hour, int minute) {
			this.day = day;
			this.hour = hour;
			this.minute = minute;
		}
	}


	public static String getRemainTimeForToast(Context context, TimeEntry timeEntry) {
		String remainTimeString;
		if (timeEntry.day == 0) {
			remainTimeString = context.getResources()
					.getString(R.string.toast_show_remain_time_hm)
					.replace("##", String.valueOf(timeEntry.hour))
					.replace("**", String.valueOf(timeEntry.minute));
		} else {
			remainTimeString = context.getResources()
					.getString(R.string.toast_show_remain_time_dh)
					.replace("##", String.valueOf(timeEntry.day))
					.replace("**", String.valueOf(timeEntry.hour));
		}
		
		return remainTimeString;
	}
	
}
