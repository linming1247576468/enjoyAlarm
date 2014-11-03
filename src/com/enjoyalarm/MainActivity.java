package com.enjoyalarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

import com.enjoyalarm.view.AlarmSettingView;

public class MainActivity extends Activity {
	
	AlarmSettingView settingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingView = new AlarmSettingView(this, -1);
		setContentView(settingView);
		
		
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		settingView.hideGetTextView();
	}
	
}
