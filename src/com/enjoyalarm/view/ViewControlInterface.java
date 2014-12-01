package com.enjoyalarm.view;

import java.util.List;

import android.content.Context;

import com.enjoyalarm.alarmliststate.State;
import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;

public interface ViewControlInterface {

	public void changeState(State state);

	public void refreshDraw();

	public void exitApp();

	public float getDensity();

	public void recoverToSettingView();

	public void clickAlarmItem(int alarmId);

	public Context getContext();

	public float getViewWidth();

	public float getViewHeight();
	
	public List<AlarmBasicInfo> getAlarmsInfo();
	
	public int getCurrentAlarmIndex();
}
