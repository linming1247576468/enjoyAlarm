package com.enjoyalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.enjoyalarm.view.MainViewManager;

public class MainActivity extends Activity {
	
	MainViewManager mManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mManager = new MainViewManager(this);
		setContentView(mManager.getMainView());
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		mManager.onActivityResult(requestCode, resultCode, data);
	}

}
