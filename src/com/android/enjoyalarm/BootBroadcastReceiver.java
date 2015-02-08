package com.android.enjoyalarm;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

import com.android.enjoyalarm.alarm.AlarmUtils;
import com.android.enjoyalarm.model.ModelUtil;
import com.android.enjoyalarm.model.ModelUtil.AlarmBasicInfo;
import com.android.enjoyalarm.view.ViewUtil;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		List<AlarmBasicInfo> alarms = ModelUtil.getAlarmsBasicInfo(context);
		loop: for (AlarmBasicInfo alarm : alarms) {
			List<Integer> days = getDays(alarm.days);
			Time time = new Time();
			time.setToNow();
			int nowDay = (time.weekDay + 6) % 7;// let Monday be first
			int nextDay = -1;
			for (int day : days) {
				if (((day == nowDay) && ((alarm.hour > time.hour) || (alarm.hour == time.hour && alarm.minute >= time.minute)))
						|| (day > nowDay)) {
					nextDay = day;
					break;
				}
			}
			
			if (!alarm.repeated) {//cancel alarms before
				if (nextDay == -1) {//delete
					ModelUtil.deleteAlarm(context, alarm.id);
					continue loop;
					
				} else {//update
					int index = days.indexOf(nextDay);
					for (int i=0; i<index; i++) {
						days.remove(i);
					}
					ModelUtil.updateAlarmWithDays(context, alarm.id, days);
				}
			}
			
			if (nextDay == -1) {// the closely next day is in next week for repeated alarm
				nextDay = days.get(0);
			}

			AlarmUtils.settingAlarm(
					context,
					alarm.id,
					System.currentTimeMillis()
							+ ViewUtil.getRemainTimeRTC(nextDay, alarm.hour,
									alarm.minute, nowDay, time.hour,
									time.minute));
			
		}
		
	}

	
	private List<Integer> getDays(String days) {
		List<Integer> result = new ArrayList<Integer>();
		char[] chars = days.toCharArray();
		for (char c : chars) {
			result.add(c - 48);
		}
		return result;
	}
}
