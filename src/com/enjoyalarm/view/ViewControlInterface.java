package com.enjoyalarm.view;

import java.util.List;

import android.content.Context;

import com.enjoyalarm.alarmliststate.State;
import com.enjoyalarm.model.ModelUtil.AlarmBasicInfo;

public interface ViewControlInterface {

	public void changeState(State state);

	public void refreshDraw();

	public void handleScrollToExitFinished();

	public void handleScrollToSettingFinished();
	
	public void handleScrollToListFinished();
	
	public void handleClickAlarmItem(int index);
	
	
	
	public float getDensity();

	public Context getContext();

	public float getViewWidth();

	public float getViewHeight();
	
	public List<AlarmBasicInfo> getAlarmsInfo();
	
	public int getCurrentAlarmIndex();
	
	public void setCurrentAlarmIndex(int index);
}
