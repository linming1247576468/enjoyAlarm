package com.android.enjoyalarm.view;

import java.util.List;

import android.content.Context;

import com.android.enjoyalarm.alarmliststate.State;
import com.android.enjoyalarm.model.ModelUtil.AlarmBasicInfo;

public interface ViewControlInterface {

	public void changeState(State state);

	public void refreshDraw();

	public void handleScrollToExitFinished();

	public void handleScrollToSettingFinished();
	
	public void handleScrollToListStarted();
	
	public void handleClickAlarmItem(int index);
	
	public void handleDeleteAlarmItem(int index);
	
	
	public float getDensity();

	public Context getViewContext();

	public float getViewWidth();

	public float getViewHeight();
	
	public List<AlarmBasicInfo> getAlarmsInfo();
	
	public List<Integer> getAlarmsColor();
	
	public int getCurrentAlarmIndex();
	
	public void setCurrentAlarmIndex(int index);
}
