package com.enjoyalarm.view;

import android.content.Context;

import com.enjoyalarm.alarmliststate.State;

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
}
