package com.enjoyalarm.view;

public class ViewUtil {

	public static String getStringForView(int hourOrMinute) {
		if (hourOrMinute < 10) {
			return "0" + String.valueOf(hourOrMinute);
		} else {
			return String.valueOf(hourOrMinute);
		}
	}

	public static String getRemainTimeStringForView(String baseString,
			int targetHour, int targetMinute, int nowHour, int nowMinute) {
		int totalMinute;
		if ((targetHour > nowHour)
				|| (targetHour == nowHour && targetMinute > nowMinute)) {// today
			totalMinute = (targetHour * 60 + targetMinute)
					- (nowHour * 60 + nowMinute);

		} else {// tomorrow
			totalMinute = (24 * 60 - (nowHour * 60 + nowMinute))
					+ (targetHour * 60 + targetMinute);
		}

		int remainHour = totalMinute / 60;
		int remainMinute = totalMinute % 60;
		baseString = baseString.replace("##", String.valueOf(remainHour))
				.replace("!!", String.valueOf(remainMinute));
		
		return baseString;
	}

}
