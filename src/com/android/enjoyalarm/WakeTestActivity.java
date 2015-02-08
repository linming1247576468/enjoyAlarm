package com.android.enjoyalarm;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.android.enjoyalarm.model.ReadingModel;
import com.android.enjoyalarm.view.WakeUpShowView;
import com.android.enjoyalarm.view.WakeUpShowView.OnDragFinishedListener;

public class WakeTestActivity extends Activity {

	WakeUpShowView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		DisplayMetrics display = getResources().getDisplayMetrics();
		view = new WakeUpShowView(this,display.widthPixels, display.heightPixels, new ReadingModel(this, -1));
		view.setOnDragFinishedListener(new OnDragFinishedListener() {
			
			@Override
			public void onDragFinished(View view) {
				finish();
			}
		});
		setContentView(view);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		view.stop();
	}
}
