package com.enjoyalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.enjoyalarm.view.AlarmSettingViewManager;

public class MainActivity extends Activity {
	
	AlarmSettingViewManager alarmSettingViewManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alarmSettingViewManager = new AlarmSettingViewManager(this, -1);
		setContentView(alarmSettingViewManager.getMainView());
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		alarmSettingViewManager.onActivityResult(requestCode, resultCode, data);
	}

}
