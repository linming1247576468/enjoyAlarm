package com.android.enjoyalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.enjoyalarm.view.MainViewManager;

public class MainActivity extends Activity {
	
	MainViewManager mManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mManager = new MainViewManager(this);
		if (getSharedPreferences(
				ActivityVariable.PREFERENCE_NAME_MAIN_ACTIVITY, 0).getInt(
				ActivityVariable.PREFERENCE_BOOLEAN_FIRST_USE, 0) == 0) {
			mManager.setInstr(true);
			getSharedPreferences(
					ActivityVariable.PREFERENCE_NAME_MAIN_ACTIVITY, 0).edit()
					.putInt(ActivityVariable.PREFERENCE_BOOLEAN_FIRST_USE, 1)
					.commit();
		}
		setContentView(mManager.getMainView());
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		mManager.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mManager.onStart();
	}
	

}
